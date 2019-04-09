package main;

import common.IInstanceElement;
import common.IQuint;
import common.interfaces.ISchemaElement;
import input.implementation.FileQuadSource;
import input.interfaces.IQuintSource;
import org.apache.commons.cli.*;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.rdf4j.repository.RepositoryException;
import output.implementation.FiFoSchemaCache;
import output.implementation.FileElementStore;
import output.implementation.RDF4JCachedSchemaElementStore;
import output.implementation.RDF4JSchemaElementStore;
import output.interfaces.IElementStore;
import processing.computation.implementation.SchemaComputation;
import processing.preprocessing.implementation.*;
import processing.preprocessing.interfaces.IQuintSourceListener;
import utils.implementation.FLuIDVocabulary;
import utils.implementation.MemoryTracker;
import utils.implementation.Window;
import utils.interfaces.IElementCache;
import zbw.cau.gotham.schema.SchemaGraphInferencing;

import java.io.*;
import java.util.*;

import static utils.implementation.Constants.RDFS_PROPERTIES;
import static utils.implementation.FLuIDEbnfReader.parseConfig;

public class Main {
    private static final Logger logger = LogManager.getLogger(Main.class.getName());

    private static String defaultContext = FLuIDVocabulary.NS;

    public enum WriterStyle {
        FILE, RDF4J, RDF4J_CACHED
    }

    public static RDF4JSchemaElementStore.WRITING_METHOD schema_writing_method = RDF4JSchemaElementStore.WRITING_METHOD.bulkupload;


    public static void main(String[] args) {
        runCommandLine(args);
    }

    public static void run(CommandLine cmd) throws RepositoryException {
        List<String> files = new LinkedList<>();
        boolean isDirectory = false;


        if (cmd.hasOption("swm")) {
            String input_writing_method = cmd.getOptionValue("swm");
            RDF4JSchemaElementStore.WRITING_METHOD[] array = RDF4JSchemaElementStore.WRITING_METHOD.values();
            Arrays.sort(array);
            if (Arrays.binarySearch(array, input_writing_method) != -1)
                schema_writing_method = RDF4JSchemaElementStore.WRITING_METHOD.valueOf(input_writing_method);
            else {
                logger.error("Wrong argument for the input writing method. Choose one of: "
                        + Arrays.toString(RDF4JSchemaElementStore.WRITING_METHOD.values()));
                System.exit(-1);
            }
        }

        if (cmd.hasOption("t"))
            RDF4JSchemaElementStore.DEFAULT_MAX_THREADS = Integer.parseInt(cmd.getOptionValue("t"));


        // option f - read and process a file
        if (cmd.hasOption("f")) {
            String input_filename = cmd.getOptionValue("f");
            files.add(input_filename);
            isDirectory = false;
        }
        // option d - read and process a directory
        if (cmd.hasOption("d")) {
            String input_directory = cmd.getOptionValue("d");
            files.add(input_directory);
            isDirectory = true;
        }
        boolean zip = cmd.hasOption("z");

        String w = cmd.getOptionValue("w");
        WriterStyle writerStyle = null;
        for (WriterStyle ws : WriterStyle.values())
            if (w.matches(ws.toString()))
                writerStyle = ws;

        String outputFolder = null;
        if (cmd.hasOption("o"))
            outputFolder = cmd.getOptionValue("o");

        String server = null;
        if (cmd.hasOption("s"))
            server = cmd.getOptionValue("s");

        boolean clearRepo = cmd.hasOption("cl");

        //configureAndStart pre-processing
        boolean fixLiterals = cmd.hasOption("l");
        boolean fixBlankNodes = cmd.hasOption("b");

        //configureAndStart processing
        int instanceWindowSize = Integer.MAX_VALUE;
        if (cmd.hasOption("c")) {
            try {
                int tmp;
                if ((tmp = Integer.parseInt(cmd.getOptionValue("c"))) > 0)
                    instanceWindowSize = tmp;
            } catch (Exception e) {
                logger.error("Instance Window size parameter is no integer value: "
                        + e.getMessage());
                System.exit(-1);
            }
        }
        int schemaCacheSize = Integer.MAX_VALUE;
        if (cmd.hasOption("sc")) {
            try {
                int tmp;
                if ((tmp = Integer.parseInt(cmd.getOptionValue("sc"))) > 0)
                    schemaCacheSize = tmp;
            } catch (Exception e) {
                logger.error("Schema Cache size parameter is no integer value: "
                        + e.getMessage());
                System.exit(-1);
            }
        }

        //Global Schema Parameters:
        boolean useIncomingProperties = cmd.hasOption("ip");
        boolean useOWLSameAs = cmd.hasOption("sa");
        boolean useRDFS = cmd.hasOption("rdfs");
        boolean filterRDFS = cmd.hasOption("fp");
        String externalSchemaGraph = null;
        if (cmd.hasOption("sg"))
            externalSchemaGraph = cmd.getOptionValue("sg");


        configureAndStart(files, isDirectory, zip,
                useIncomingProperties, useOWLSameAs, useRDFS, externalSchemaGraph,
                fixLiterals, fixBlankNodes, filterRDFS,
                outputFolder, server, writerStyle, clearRepo, instanceWindowSize,
                schemaCacheSize);

    }


    public static void configureAndStart(List<String> files, boolean recursive, boolean zip,
                                         boolean useIncomingProps, boolean useOWLSameAs, boolean useRDFS, String useExternalSG,
                                         boolean fixLiterals, boolean fixBlankNodes, boolean filterRDFS,
                                         String outputFolder, String externalRepositoryURL,
                                         WriterStyle writerStyle, boolean clearRepo, int windowSize,
                                         int schemaCacheSize) {

        //mute System errors from NxParser for normal procedure
        if (logger.getLevel().isMoreSpecificThan(Level.INFO))
            System.setErr(new PrintStream(new OutputStream() {
                @Override
                public void write(int b) {
                }
            }));


        //1. create source
        IQuintSource source = new FileQuadSource(files, recursive, defaultContext);
        if (logger.getLevel().isLessSpecificThan(Level.INFO)) {
            source.registerQuintListener(new IQuintSourceListener() {
                long n = 0;

                @Override
                public void sourceStarted() {
                    logger.info("Sources started!");
                }

                @Override
                public void sourceClosed() {
                    logger.info("Sources closes! " + n + " quads processed.");
                }

                @Override
                public void pushedQuint(IQuint quint) {
                    n++;
                }
            });
        }

        //2. create instance window
        IElementCache<IInstanceElement> window = new Window(windowSize);

        //3. create pre-processing
        BasicQuintPipeline pipe = new BasicQuintPipeline();
        // Cleanup dirty data (i.e. broken URLs), mandatory!
        pipe.addProcessor(new NoisyDataFilter("http://fluid.de/broken/", fixLiterals, fixBlankNodes, outputFolder));
        //optional: filter rdfs properties OR add them to SG
        if (filterRDFS || useExternalSG != null) {
            //either only filter or use pre-computed schema graph, thus, filter only
            PropertyFilter filter = new PropertyFilter(RDFS_PROPERTIES);
            pipe.addProcessor(filter);
        }
        RDFSGraphFilter rdfsGraphFilter = null;
        if (useExternalSG != null) {
            logger.info("Using pre-computed Schema Graph: " + useExternalSG);
            /////////////<<<<LOADING>>>>>>////////////
            List<String> filepaths = new LinkedList<>();
            filepaths.add(useExternalSG);
            FileQuadSource fileQuadSource = new FileQuadSource(filepaths, false, defaultContext);
            BasicQuintPipeline schemaPipe = new BasicQuintPipeline();
            rdfsGraphFilter = new RDFSGraphFilter(RDFS_PROPERTIES);
            schemaPipe.addProcessor(rdfsGraphFilter);
            fileQuadSource.registerQuintListener(schemaPipe);
            // Start Schema Graph
            fileQuadSource.start();
            logger.info("Finished loading:  " + rdfsGraphFilter.getSchemaGraph().toString());
            //////////////////////////////////////////
        } else if (useRDFS) {
            //on-the-fly inferencing then
            // QuintFilter component that removes all RDFS properties, but adds them to a graph
            rdfsGraphFilter = new RDFSGraphFilter(RDFS_PROPERTIES);
            pipe.addProcessor(rdfsGraphFilter);
        }

        //aggregate triples to instances
        InstanceAggregator instanceAggregator;
        if (useOWLSameAs)
            instanceAggregator = new InstanceAggregatorOWLSameAs(useIncomingProps);
        else
            instanceAggregator = new InstanceAggregator(useIncomingProps);

        //4. configureAndStart schema
        Map<String, SchemaComputation> computationMap = loadSchemaPropertiesFiles(window, schemaCacheSize,
                rdfsGraphFilter == null ? null : rdfsGraphFilter.getSchemaGraph(), writerStyle,
                externalRepositoryURL, outputFolder, clearRepo, zip);

        if (logger.getLevel().isLessSpecificThan(Level.DEBUG)) {
            int i = 1;
            for (Map.Entry<String, SchemaComputation> entry : computationMap.entrySet()) {
                logger.debug(i + ". " + entry.getValue() + "\n\t\t -> " + entry.getKey());
                i++;
            }
        }

        ////////////////////////////////////////////////
        //pipe loaded triples to pre-processing pipeline
        source.registerQuintListener(pipe);
        //pipe output of pre-processing to instance aggregator
        pipe.registerQuintListener(instanceAggregator);
        //write/update aggregated instances to instance window
        instanceAggregator.setWindow(window);
        //OPTIONAL: track real-time memory consumption
        if (logger.getLevel().isLessSpecificThan(Level.INFO))
            window.registerCacheListener(new MemoryTracker(outputFolder));
        ////////////////////////////////////////////////
        //5. create coutput and pipe each schema computation to the output

        /*
            fully set
         */
        source.start();
    }


    private static void runCommandLine(String[] args) {
        Options options = new Options();
        ///////////////// INPUT ///////////////////////////////////
        // input options (mutually exclusive)
        OptionGroup input = new OptionGroup();
        // read an input file
        Option file = new Option("f", "file", true, "location of input file");
        file.setArgName("file");
        input.addOption(file);
        // read a complete directory
        Option dir = new Option("d", "directory", true, "location of input directory");
        dir.setArgName("dir");
        input.addOption(dir);
        //TODO: crawler source
        input.setRequired(true);
        options.addOptionGroup(input);
        ////////////////// OUTPUT /////////////////////////////////
        Option debugFolder = new Option("o", "output", true, "output folder for additional information");
        debugFolder.setArgName("folder");
        debugFolder.setRequired(true);
        options.addOption(debugFolder);

        String writerStyles = "";
        for (int i = 0; i < WriterStyle.values().length - 1; i++)
            writerStyles += "<" + WriterStyle.values()[i] + "> or ";
        writerStyles += "<" + WriterStyle.values()[WriterStyle.values().length - 1] + ">";

        Option writer = new Option("w", "writer", true, writerStyles);
        writer.setArgName("arg");
        writer.setRequired(true);
        options.addOption(writer);

        Option server = new Option("s", "server", true, "server URL to external repository (omit if local)");
        server.setArgName("server URL");
        server.setRequired(false);
        options.addOption(server);

        Option zip = new Option("z", "zip", false, "zip output file");
        options.addOption(zip);
        ///////////////// PRE-PROCESSING //////////////////////////
        Option fixLiteral = new Option("l", "literal", false, "try to fix literals");
        options.addOption(fixLiteral);

        Option fixBlankNodes = new Option("b", "blanks", false, "try to de-anonymize blank nodes");
        options.addOption(fixBlankNodes);

        Option filterRDFSProperties = new Option("fp", "filter-properties", false, "filter RDFS properties");
        options.addOption(filterRDFSProperties);

        // empty repository/folder before writing
        Option clearRepo = new Option("cl", "clear", false, "clear repository or file before writing");
        options.addOption(clearRepo);
        ////////////////// PROCESSING /////////////////////////////////
        Option cacheSize = new Option("c", "windowsize", true,
                "instance window size (max cached number of RDF instances)");
        cacheSize.setArgName("int");
        cacheSize.setRequired(true);
        options.addOption(cacheSize);

        Option schema_cacheSize = new Option("sc", "schema-cachesize", true, "schema cache size");
        schema_cacheSize.setArgName("int");
        schema_cacheSize.setRequired(true);
        options.addOption(schema_cacheSize);

        Option schema_writing_method = new Option("swm", "schema_writing_method", true,
                "chooses the writing method to use: " + Arrays.toString(RDF4JSchemaElementStore.WRITING_METHOD.values()));
        schema_writing_method.setArgs(1);
        options.addOption(schema_writing_method);


        Option threadsNumber = new Option("t", "threadNumber", true, "max threads that will be created in " +
                "the RDF4JSchemaElementStore bulkUpload method ");
        threadsNumber.setArgs(1);
        options.addOption(threadsNumber);
        ///////////////// SCHEMA COMPUTATION //////////////////////
        //Configuration
        Option useRDFs = new Option("rdfs", "RDFs", false, "Infer RDF Schema information");
        options.addOption(useRDFs);

        Option useSameAs = new Option("sa", "sameAs", false, "Infer owl:sameAs schema information");
        options.addOption(useSameAs);

        Option useExternalSchemaGraph = new Option("sg", "schemagraph", true, "Use existing schema graph");
        options.addOption(useExternalSchemaGraph);

//        Option snippets = new Option("sn", "snippets", true, "Number of snippets attached as PAYLOAD");
//        snippets.setArgName("int");
//        options.addOption(snippets);
        ///////////////////////////////////////////////////////////
        Options optionsHelp = new Options();
        Option help = new Option("h", "help", false, "print help");
        optionsHelp.addOption(help);

        CommandLineParser parserHelp = new DefaultParser();
        HelpFormatter formatterHelp = new HelpFormatter();
        CommandLine cmdHelp;

        // this parses the command line just for help and doesn't throw an exception on unknown options
        try {
            // parse the command line arguments for the help option
            cmdHelp = parserHelp.parse(optionsHelp, args, true);

            // print help
            if (cmdHelp.hasOption("h") || cmdHelp.hasOption("help")) {
                formatterHelp.printHelp(80, " ", "FLuID Framework\n",
                        options, "\n", true);
                System.exit(0);
            }

        } catch (ParseException e1) {
            formatterHelp.printHelp(80, " ", "ERROR: " + e1.getMessage() + "\n",
                    optionsHelp, "\nError occurred! Please see the error message above", true);
            System.exit(-1);
        }

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd;
        // this parses the command line for all other options
        try {
            // parse the command line arguments with the defined options
            cmd = parser.parse(options, args, true);

            // everything's fine, run the program
            run(cmd);
        } catch (ParseException e) {
            formatter.printHelp(80, " ", "ERROR: " + e.getMessage() + "\n",
                    options, "\nError occurred! Please see the error message above", true);
            System.exit(-1);
        }
    }


    public static Map<String, SchemaComputation> loadSchemaPropertiesFiles(IElementCache<IInstanceElement> window,
                                                                           int schemaCacheSize, SchemaGraphInferencing schemaGraph,
                                                                           WriterStyle writerStyle, String externalRepositoryURL,
                                                                           String outputFolder, boolean clearRepo, boolean zip) {
        Properties prop = new Properties();
        InputStream input = null;

        Map<String, SchemaComputation> computations = new HashMap<>();
        try {
            input = new FileInputStream("src/main/resources/schema.properties");

            // load a properties file
            prop.load(input);
            String[] schemas = prop.getProperty("schema").split("&&");
            String[] outputs = prop.getProperty("output").split("&&");

            //debugging
            for (int i = 0; i < schemas.length; i++)
                System.out.println((i + 1) + ": " + schemas[i] + " -> " + outputs[Math.min(i, outputs.length - 1)]);

            for (int i = 0; i < schemas.length; i++) {
                computations.put(outputs[Math.min(i, outputs.length - 1)],
                        parseConfig(schemas[i], window, new FiFoSchemaCache(schemaCacheSize), schemaGraph));
            }

            for (int i = 0; i < schemas.length; i++) {
                SchemaComputation computation = null;
                IElementStore<ISchemaElement> elementStore = null;
                String computationName = outputs[Math.min(i, outputs.length - 1)];

                if (writerStyle == WriterStyle.FILE) {
                    computation = parseConfig(schemas[i], window, new FiFoSchemaCache(schemaCacheSize), schemaGraph);
                    try {
                        elementStore = new FileElementStore(outputFolder, computationName, zip);
                    } catch (IOException e) {
                        e.printStackTrace();
                        System.exit(-1);
                    }
                    //write computed schema and PAYLOAD elements to an external element store
                    computation.getSchemaCache().registerCacheListener(elementStore);

                } else if (writerStyle == WriterStyle.RDF4J) {
                    computation = parseConfig(schemas[i], window, new FiFoSchemaCache(schemaCacheSize), schemaGraph);

                    if (externalRepositoryURL == null)
                        elementStore = new RDF4JSchemaElementStore(null, outputFolder + File.separator + computationName);
                    else
                        elementStore = new RDF4JSchemaElementStore(externalRepositoryURL, computationName);

                    //write computed schema and PAYLOAD elements to an external element store
                    computation.getSchemaCache().registerCacheListener(elementStore);

                } else if (writerStyle == WriterStyle.RDF4J_CACHED) {
                    //this writer style does not use FiFO Schema Cache
                    if (externalRepositoryURL == null)
                        elementStore = new RDF4JCachedSchemaElementStore(null,
                                outputFolder + File.separator + computationName, schemaCacheSize);
                    else
                        elementStore = new RDF4JCachedSchemaElementStore(externalRepositoryURL, computationName, schemaCacheSize);

                    computation = parseConfig(schemas[i], window, elementStore, schemaGraph);

                }

                computations.put(computationName, computation);
                //pipe instances from instance window to schema computation (schema computation uses schema cache)
                window.registerCacheListener(computation);

                //clear repository before computation starts?
                if (clearRepo)
                    elementStore.flush();
            }


//        for(int i=0;i<schemas.length;i++){
//        computations.put(outputs[Math.min(i,outputs.length-1)],
//        parseConfig(schemas[i],window,new FiFoSchemaCache(schemaCacheSize),schemaGraph));
//        }

            return computations;
        } catch (
                IOException ex) {
            ex.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
}
