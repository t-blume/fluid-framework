package output.implementation;

import common.interfaces.IQuint;
import common.interfaces.IResource;
import common.interfaces.ISchemaElement;
import main.Main;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.rdf4j.repository.Repository;
import output.implementation.connectors.RDF4JConnector;
import output.implementation.sinks.FileTripleSink;
import output.interfaces.IElementListener;
import output.interfaces.IElementStore;
import utils.implementation.FLuIDPOJO;
import utils.implementation.FLuIDVocabulary;
import utils.implementation.Helper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


public class RDF4JSchemaElementStore implements IElementStore<ISchemaElement> {


    private static final Logger logger = LogManager.getLogger(RDF4JSchemaElementStore.class.getSimpleName());

    private final RDF4JConnector connector;
    private boolean isClosed = false;

    public static  int DEFAULT_MAX_THREADS = 2;
    private final int maximumThreads;

    public RDF4JSchemaElementStore(Repository repository) {
        this.connector = new RDF4JConnector(repository);
        maximumThreads = DEFAULT_MAX_THREADS;
    }

    public RDF4JSchemaElementStore(String repository) {
        connector = new RDF4JConnector(repository);
        maximumThreads = DEFAULT_MAX_THREADS;
    }

    public RDF4JSchemaElementStore(String repository, int maximumThreads) {
        connector = new RDF4JConnector(repository);
        this.maximumThreads = maximumThreads;
    }

    public RDF4JSchemaElementStore(String url, String repository) {
        //TODO: if url == null, create native local repo
        connector = new RDF4JConnector(url, repository);
        maximumThreads = DEFAULT_MAX_THREADS;
    }

    public RDF4JSchemaElementStore(String url, String repository, int maximumThreads) {
        connector = new RDF4JConnector(url, repository);
        this.maximumThreads = maximumThreads;

    }


    @Override
    public boolean contains(IResource locator) {
        return connector.getQuints(locator) != null;
    }

    @Override
    public ISchemaElement getSchemaElement(IResource schemaElementLocator, Class<? extends ISchemaElement> schemaElementType) {
        String schemaElementURI = FLuIDVocabulary.createSubjectURIPrefix(schemaElementType) + schemaElementLocator.toString();
        return connector.getSchemaElement(schemaElementURI, schemaElementType);
    }

    @Override
    public boolean removeElement(IResource schemaElementLocator, Class<? extends ISchemaElement> schemaElementType) {
        String schemaElementURI = FLuIDVocabulary.createSubjectURIPrefix(schemaElementType) + schemaElementLocator.toString();
        return connector.removeSchemaElement(schemaElementURI, schemaElementType);
    }

    @Override
    public List<ISchemaElement> removeLast(int n) {
        logger.warn("removeLast(int n) unsupported!");
        return null;
    }

    @Override
    public void add(ISchemaElement schemaElement) {
        connector.addSchemaElement(schemaElement);
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public void flush() {
        connector.clear();
        logger.info("Repository cleared!");
    }

    @Override
    public void close() {
        connector.close();
        isClosed = true;
    }

    @Override
    public boolean isClosed() {
        return isClosed;
    }


    @Override
    public void registerCacheListener(IElementListener listener) {
        logger.debug("registerCacheListener(IElementListener listener) Unsupported!");
    }

    @Override
    public void elementEmitted(ISchemaElement el) {
        add(el);
    }

    @Override
    public void finished(IElementStore<ISchemaElement> emitter) {


        if (Main.schema_writing_method.equals(WRITING_METHOD.bulkupload)){
            bulkUploading(emitter);
        }
        else if (Main.schema_writing_method.equals(WRITING_METHOD.dump)) {
            dump(emitter);
        }
        //default
        else {
            bulkUploading(emitter);
        }
    }
    public enum WRITING_METHOD {
        dump, bulkupload
    }
    private void dump(IElementStore<ISchemaElement> emitter) {
        long start = System.nanoTime();
        long end ;

        //intermediate solution, disk writing!
        File file = Helper.createFile(connector.getRepository().getDataDir() + File.separator + "dump.nt");
        int total = emitter.size();
        logger.info("Bulk dumping remaining " + total + " SEs to disk (" + file.getAbsolutePath() + ")\n (Potential data loss, only for intermediate results)");
        try {
            PrintStream schemaWriter = new PrintStream(file.getAbsolutePath());
            FileTripleSink fileSink = new FileTripleSink(schemaWriter, false);


            Collection<ISchemaElement> schemaElements = emitter.removeLast(emitter.size());

            int i = 0;

            for (ISchemaElement schemaElement : schemaElements) {
                List<IQuint> quints = FLuIDPOJO.exportSchemaElement(schemaElement);
                for (IQuint quint : quints)
                    fileSink.print(quint.getSubject().toN3(), quint.getPredicate().toN3(), quint.getObject().toN3());

                i++;
                if (i % 10000 == 0) {
                    logger.info("Progress: " + i + "/" + total);
                    logger.info("Progress: " + ((int) (((double) i / (double) total) * 100.0)) + "%");
                }
            }




        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        end = System.nanoTime();
        logger.info("Time for bulk Uploading: "+(end -start)/1000000000.0);
    }

    private void paralellUploading (IElementStore<ISchemaElement> emitter) {





    }

    private void bulkUploading(IElementStore<ISchemaElement> emitter) {

        long start = System.nanoTime();
        long end ;
        int numberOfElements = emitter.size();
        int numberOfThreads = Math.min(numberOfElements, maximumThreads);
        int threadChunkSize = numberOfElements / numberOfThreads;
        int extraChunk = numberOfElements % numberOfThreads;
        logger.info("Bulk uploading remaining data of " + numberOfElements + " elements with " + numberOfThreads + " threads");


        List<Thread> threadList = new CopyOnWriteArrayList<>();
        RDF4JConnector tmpConnector;
        tmpConnector = new RDF4JConnector(connector.getRepository());


        logger.debug("Thread0: " + (threadChunkSize + extraChunk));
        Collection<ISchemaElement> schemaElements = emitter.removeLast(threadChunkSize + extraChunk);
        tmpConnector.setSchemaElementList(schemaElements);
        threadList.add(tmpConnector);
        threadList.get(threadList.size() - 1).start();
        for (int i = 1; i < numberOfThreads; i++) {
            tmpConnector = new RDF4JConnector(connector.getRepository());
            tmpConnector.setSchemaElementList(emitter.removeLast(threadChunkSize));
            threadList.add(tmpConnector);
            logger.debug("Thread" + i + ": " + threadChunkSize);

            threadList.get(threadList.size() - 1).start();

        }
        logger.info("Bulk upload started with all threads running!");
        for (Thread thread : threadList) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
                logger.debug("WTF");
                logger.debug(e.getLocalizedMessage());
            }
        }
        logger.info("Bulk upload finished!");
        end = System.nanoTime();
        logger.info("Time for bulk Uploading: "+(end -start)/1000000000.0);
    }
}
