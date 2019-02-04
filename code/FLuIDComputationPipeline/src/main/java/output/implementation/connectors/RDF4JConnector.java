package output.implementation.connectors;


import common.implemenation.NodeResource;
import common.implemenation.Quad;
import common.interfaces.IQuint;
import common.interfaces.IResource;
import common.interfaces.ISchemaElement;
import org.apache.jena.query.Query;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cyberborean.rdfbeans.RDFBeanManager;
import org.cyberborean.rdfbeans.exceptions.RDFBeanException;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.query.*;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.repository.RepositoryResult;
import org.eclipse.rdf4j.repository.http.HTTPRepository;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.rio.*;
import org.eclipse.rdf4j.rio.helpers.NTriplesParserSettings;
import org.eclipse.rdf4j.rio.ntriples.NTriplesParserFactory;
import org.eclipse.rdf4j.rio.ntriples.NTriplesWriterFactory;
import org.eclipse.rdf4j.rio.turtle.TurtleWriterFactory;
import org.eclipse.rdf4j.sail.nativerdf.NativeStore;
import org.semanticweb.yars.nx.Resource;
import output.interfaces.IConnector;
import utils.implementation.FLuIDVocabulary;
import utils.implementation.Helper;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Blume Till on 25.08.2016.
 */
public class RDF4JConnector extends Thread implements IConnector {
    private static final Logger logger = LogManager.getLogger(RDF4JConnector.class.getSimpleName());
    private static final int loggingInterval = 5000;

    public static final String defaultDataDir = ".";
    private final String server_uri;
    private final String repository_id;
    private Repository repository;
    private final ValueFactory valueFactory;

    public Repository getRepository() {
        return repository;
    }

    private final RepositoryConnection connection;
    //POJO
    private RDFBeanManager beanManager;

    private long written = 0;
    //stream writing
    private final int BUFF_SIZE = 10000;
    private final int MAX_STRING_LENGTH = 1000;
    private final StringBuffer sbf = new StringBuffer(BUFF_SIZE * MAX_STRING_LENGTH);

    private long addCount = 0;
    private long min = BUFF_SIZE * MAX_STRING_LENGTH;
    ///////////////

    ////// Thread stuff
    private Collection<ISchemaElement> schemaElementList = new LinkedList<>();

    public void setSchemaElementList(Collection<ISchemaElement> schemaElementList) {
        this.schemaElementList = schemaElementList;
    }
    ///////////////

    public RDF4JConnector(Repository repository) {
        this.repository = repository;
        this.connection = repository.getConnection();
        this.valueFactory = repository.getValueFactory();
        server_uri = null;
        repository_id = null;
        RDFParser parser = Rio.createParser(RDFFormat.NTRIPLES);
        ParserConfig config = parser.getParserConfig();
        config.addNonFatalError(NTriplesParserSettings.FAIL_ON_NTRIPLES_INVALID_LINES);
        connection.setParserConfig(config);

        beanManager = new RDFBeanManager(connection);
    }



    public RDF4JConnector(String repositoryId) throws RepositoryException {
        this(null, repositoryId);
    }

    public RDF4JConnector(String serverUri, String repositoryId) throws RepositoryException {
        if (serverUri == null) {
            logger.debug("Creating/Accessing local repository.");
            server_uri = "local";
            repository_id = repositoryId;

            File dataDir;
            if (!repositoryId.startsWith("/|\\|[A-Z]:")) //no absolute path fo unix + windows?
                dataDir = Helper.createFile(defaultDataDir + File.separator + repositoryId);
            else
                dataDir = Helper.createFile(repositoryId);

            repository = new SailRepository(new NativeStore(dataDir));
            repository.initialize();
            logger.debug("Writable: " + repository.isWritable());
        } else {
            logger.debug("Accessing remote HTTP repository.");
            server_uri = serverUri;
            repository_id = repositoryId;
            repository = new HTTPRepository(server_uri, repository_id);
            repository.initialize();
        }
        valueFactory = repository.getValueFactory();
        connection = repository.getConnection();
        //somehow this crashes on the server
        RDFParser parser ;// Rio.createParser(RDFFormat.NTRIPLES);


        ///HOTFIX
        RDFParserRegistry parserRegistry = RDFParserRegistry.getInstance();

        parserRegistry.add(new NTriplesParserFactory());

        parserRegistry.get(RDFFormat.NTRIPLES).get().getParser();




        System.out.println(RDFParserRegistry.getInstance().getKeys());
        RDFParserRegistry.getInstance().getAll().stream().forEach(X -> System.out.println(X.getRDFFormat()));
       // parser = RDFParserRegistry.getInstance().get(RDFFormat.TRIX).get().getParser();
        parser =    parserRegistry.get(RDFFormat.NTRIPLES).get().getParser();


        ParserConfig config = parser.getParserConfig();
        config.addNonFatalError(NTriplesParserSettings.FAIL_ON_NTRIPLES_INVALID_LINES);
        connection.setParserConfig(config);
        beanManager = new RDFBeanManager(connection);
    }


    public boolean addSchemaElement(ISchemaElement schemaElement) {
        try {

            long before = System.currentTimeMillis();
            //todo is buggy, füge ein schema element hinzu und lese es wieder aus (evtl. anderes framework)
            // vllt blank nodes machen probleme
            beanManager.add(schemaElement);
            long diff;
            if ((diff = System.currentTimeMillis() - before) >= 5000) {
                logger.debug("Time for adding: " + diff / 1000 + "s");
                logger.debug("SchemaElement: \n" + schemaElement);
                logger.debug("---------------------------------------");
            }
            written++;
            if (written % loggingInterval == 0)
                logger.debug("Written " + written + " elements.");

            return true;
        } catch (RDFBeanException e) {
            logger.debug(schemaElement);
            logger.error(e.getMessage());
            return false;
        }
    }

    public boolean updateSchemaElement(ISchemaElement schemaElement) {
        try {
            beanManager.update(schemaElement);
            return true;
        } catch (RDFBeanException e) {
            logger.debug(schemaElement);
            logger.error(e.getMessage());
            return false;
        }
    }


    public ISchemaElement getSchemaElement(String schemaElementURI, Class<? extends ISchemaElement> schemaElementType) {
        try {
            return beanManager.get(schemaElementURI, schemaElementType);
        } catch (RDFBeanException e) {
            logger.error(e.getMessage());
            return null;
        }
    }

    public boolean removeSchemaElement(String schemaElementURI, Class<? extends ISchemaElement> schemaElementType) {
        try {
            beanManager.delete(schemaElementURI, schemaElementType);
            return true;
        } catch (RDFBeanException e) {
            logger.error(e.getMessage());
            return false;
        }
    }


    ///////////////////////////////////////////////////////////////////////////////
    ////////////////    Public Interface Methods    ///////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////

    @Override
    public void clear() {
        connection.clear();
    }

    @Override
    public void close() {
        connection.close();
    }

    @Override
    public String getContext() {
        return null;
    }

    @Override
    public boolean addQuint(IQuint quint) {
        return addStream(quint.getSubject().toN3(), quint.getPredicate().toN3(), quint.getObject().toN3());
    }

    @Override
    public boolean addQuints(List<IQuint> quints) {
        return addStream(quints);
    }

    @Override
    public boolean hasQuint(IQuint quint) {
        IRI subject = (quint.getSubject() != null ? repository.getValueFactory().createIRI(quint.getSubject().toString()) : null);
        IRI predicate = (quint.getPredicate() != null ? repository.getValueFactory().createIRI(quint.getPredicate().toString()) : null);
        IRI object = (quint.getObject() != null ? repository.getValueFactory().createIRI(quint.getObject().toString()) : null);

        return connection.hasStatement(subject, predicate, object, false);
    }

    @Override
    public List<IQuint> getQuints(IResource subjectURI) {
        IRI subject = repository.getValueFactory().createIRI(subjectURI.toString());

        List<IQuint> quints = null;
        RepositoryResult<Statement> statements = connection.getStatements(subject, null, null);
        if (statements != null) {
            quints = new LinkedList<>();
            while (statements.hasNext()) {
                Statement statement = statements.next();
                quints.add(new Quad(new NodeResource(new Resource(statement.getSubject().stringValue())),
                        new NodeResource(new Resource(statement.getPredicate().stringValue())),
                        new NodeResource(new Resource(statement.getObject().stringValue()))));
            }
        }
        return quints;
    }

    @Override
    public void removeQuint(IQuint quint) {
        if (quint == null)
            return;
        IRI subjectIRI = quint.getSubject() != null ? valueFactory.createIRI(quint.getSubject().toString()) : null;
        IRI predicateIRI = quint.getPredicate() != null ? valueFactory.createIRI(quint.getPredicate().toString()) : null;
        IRI objectIRI = quint.getObject() != null ? valueFactory.createIRI(quint.getObject().toString()) : null;
        connection.remove(subjectIRI, predicateIRI, objectIRI);
    }

    @Override
    public void removeQuints(List<IQuint> quints) {
        quints.forEach(this::removeQuint);
    }

    @Override
    public List<IQuint> executeQuery(Query query) {
        List<IQuint> result = new LinkedList<>();
        TupleQueryResult queryResult = null;
        TupleQuery tupleQuery = connection.prepareTupleQuery(QueryLanguage.SPARQL, query.toString());
        try {
            queryResult = tupleQuery.evaluate();
            while (queryResult.hasNext()) {
                BindingSet bindingSet = queryResult.next();
                Iterator<Binding> bindingIterator = bindingSet.iterator();
                String subject = null;
                String predicate = null;
                String object = null;
                String payload = null;
                while (bindingIterator.hasNext()) {
                    Binding binding = bindingIterator.next();
                    if (binding.getName().equals("s"))
                        subject = binding.getValue().stringValue();
                    else if (binding.getName().equals("p"))
                        predicate = binding.getValue().stringValue();
                    else if (binding.getName().equals("o"))
                        object = binding.getValue().stringValue();
                    else if (binding.getName().equals("PAY_INFO"))
                        payload = binding.getValue().stringValue();
                    else
                        logger.warn("Invalid binding name in query:\n" + binding.getName() + ": " + binding.getValue().stringValue());

                }

                if (subject != null && predicate != null && object != null)
                    result.add(new Quad(new NodeResource(new Resource(subject)),
                            new NodeResource(new Resource(predicate)), new NodeResource(new Resource(object))));
                    //if subject is null, this can be alright
                else if (predicate != null && object != null)
                    result.add(new Quad(new NodeResource(new Resource("DUMMY")),
                            new NodeResource(new Resource(predicate)), new NodeResource(new Resource(object))));
                else
                    logger.warn("Incomplete triple received!");


                if (payload != null)
                    result.add(new Quad(new NodeResource(new Resource("PAY_DUMMY")),
                            FLuIDVocabulary.PAYLOAD_RESOURCE, new NodeResource(new Resource(payload)))); //FIXME: literals in PAYLOAD
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        } finally {
            if (queryResult != null)
                queryResult.close();

            connection.close();
        }
        return result;
    }


    ///////////////////////////////////////////////////////////////////////////////
    /////////////////////    Dirty Little Helper    ///////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////
    private boolean add(String subject, String predicate, String object) {
        try {
            // create some resources and literals to make statements out of
            IRI subjectIRI = valueFactory.createIRI(subject);
            IRI predicateIRI = valueFactory.createIRI(predicate);
            IRI objectIRI = valueFactory.createIRI(object);

            connection.add(subjectIRI, predicateIRI, objectIRI);
            return true;
        } catch (RepositoryException e) {
            // handle exception
            logger.error(e);
            return false;
        }
    }

    private boolean addStream(List<IQuint> quints) {
        StringBuilder sb = new StringBuilder();
        for (IQuint quint : quints)
            sb.append(quint.getSubject().toN3() + " " + " " + quint.getPredicate().toN3() +
                    " " + quint.getObject().toN3() + " .\n");
        sbf.append(sb);
        int len = BUFF_SIZE * MAX_STRING_LENGTH - sbf.length();
        min = Math.min(min, len);
        if (addCount % BUFF_SIZE == 0) {
            InputStream in = new ByteArrayInputStream(sbf.toString().getBytes());
            try {
                connection.begin();
                connection.add(in, null, RDFFormat.NTRIPLES);
                connection.commit();
            } catch (RepositoryException | IOException e) {
                logger.error("printTriple() Exception thrown  :" + e);
                logger.error(sbf.toString());
                System.exit(-1);
            }
            // reset stream
            sbf.setLength(0);
        }
        return true;
    }

    private boolean addStream(String subject, String predicate, String object) {
        StringBuilder sb = new StringBuilder();
        sb.append(subject + " " + " " + predicate + " " + object + " .\n");
        sbf.append(sb);
        int len = BUFF_SIZE * MAX_STRING_LENGTH - sbf.length();
        min = Math.min(min, len);
        if (addCount % BUFF_SIZE == 0) {
            InputStream in = new ByteArrayInputStream(sbf.toString().getBytes());
            try {
                connection.begin();
                connection.add(in, null, RDFFormat.NTRIPLES);
                connection.commit();
            } catch (RepositoryException | IOException e) {
                logger.error("printTriple() Exception thrown  :" + e);
                logger.error(sbf.toString());
                System.exit(-1);
            }
            // reset stream
            sbf.setLength(0);
        }
        return true;
    }


    ///////////////THREAD STUFF

    public void run() {
        logger.debug("Adding " + schemaElementList.size() + " elements...");
        schemaElementList.forEach(SE -> addSchemaElement(SE));
        logger.debug(".. finished adding!");
        connection.close();
    }
}
