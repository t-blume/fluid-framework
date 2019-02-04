
import implementation.connectors.Connection;
import implementation.eval.EvalUnit;
import implementation.utils.datastructs.SetSet;
import implementation.vocabularies.FLuIDVocabulary;
import implementation.vocabularies.SchemEXVocabulary;
import implementation.vocabularies.VocabularyConstants;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import org.apache.commons.cli.*;
import org.apache.commons.lang3.ArrayUtils;


/**
 * Created by Blume Till on 19.08.2016.
 */
public class Main {
    public static void main(String[] args) throws IOException {
        System.out.println(ArrayUtils.toString(args));
        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatterC = new HelpFormatter();
        CommandLine cmd = null;
        Options computeOptions = new Options();


        String evalOpts = "";
        for (int i = 0; i < EvalUnit.EvalType.values().length - 1; i++)
            evalOpts += "<" + EvalUnit.EvalType.values()[i] + "> or ";
        evalOpts += "<" + EvalUnit.EvalType.values()[EvalUnit.EvalType.values().length - 1] + ">";
        Option evaluateOptions = new Option("evalOpt", "evaluate-options", true,
                "Evaluate only some queries: " + evalOpts);
        evaluateOptions.setArgs(Option.UNLIMITED_VALUES);

        computeOptions.addOption(evaluateOptions);


        String specialObjects = "";
        for (int i = 0; i < EvalUnit.SpecialObjects.values().length - 1; i++)
            specialObjects += "<" + EvalUnit.SpecialObjects.values()[i] + "> or ";
        specialObjects += "<" + EvalUnit.SpecialObjects.values()[EvalUnit.SpecialObjects.values().length - 1] + ">";
        Option specialObjectOptions = new Option("exSSO", "excludeSpecialSchemaObjects", true,
                "Exclude some special schema element: " + specialObjects);
        specialObjectOptions.setArgs(Option.UNLIMITED_VALUES);

        computeOptions.addOption(specialObjectOptions);


        Option evaluateOption = new Option("eval", "evaluate", true, "Evaluate: <goldRepo> <apprRepo>");
        evaluateOption.setArgs(2);
        evaluateOption.setRequired(true);
        computeOptions.addOption(evaluateOption);

        Option debugOption = new Option("db", "debug", false, "write comment to schema file");
        computeOptions.addOption(debugOption);

        Option skipElementsOption = new Option("sE", "skipElements", false, "Has to be active for exSSO to exclude elements; ");
        computeOptions.addOption(skipElementsOption);

        Option skipPropertiesOption = new Option("sP", "skipProperties", false, "Has to be active for exSSO to exclude elements from property object cluster");
        computeOptions.addOption(skipPropertiesOption);


        Option server = new Option("srv", "server", true, "rdf4j server URL");
        computeOptions.addOption(server);
        String vocabTypes = "";
        for (int i = 0; i < EvalUnit.VocabularyTypes.values().length - 1; i++)
            vocabTypes += "<" + EvalUnit.VocabularyTypes.values()[i] + "> or ";
        vocabTypes += "<" + EvalUnit.VocabularyTypes.values()[EvalUnit.VocabularyTypes.values().length - 1] + ">";
        Option vocabTypesOptions = new Option("vocTypes", "vocabularyTypes", true,
                "Exclude some special schema element: " + vocabTypes);
        vocabTypesOptions.setArgs(2);

        computeOptions.addOption(vocabTypesOptions);
        // this parses the command line for all other options
        try {
            // parse the command line arguments with the defined options
            cmd = parser.parse(computeOptions, args, true);


            String srv =  "http://localhost:8080/rdf4j-server";

            if (cmd.hasOption("srv"))
                srv = cmd.getOptionValue("srv");



            String goldRep="";
            VocabularyConstants goldVocab = new FLuIDVocabulary();
            String apprRep="";
            VocabularyConstants apprVocab = new FLuIDVocabulary();


            if (cmd.hasOption("eval")) {
                String[] repos = cmd.getOptionValues("eval");
                goldRep = repos[0];
                apprRep = repos[1];

            }


            // new params
            //evalOpt (OC, SUP_OC, ....) sagt welche der Eval methoden durchgeführt werden
            // debug --> wether debug is turned on or not
            //
            Set<EvalUnit.EvalType> evalTypes = new HashSet<>();
            if (cmd.hasOption("evalOpt")) {
                String[] evalOpt = cmd.getOptionValues("evalOpt");
                for (String eo : evalOpt) {
                    System.out.println(eo);
                    for (EvalUnit.EvalType et : EvalUnit.EvalType.values())
                        if (eo.matches(et.toString()))
                            evalTypes.add(et);
                }
            }
            Set<EvalUnit.SpecialObjects> specialSchemaElements = new HashSet<>();
            if (cmd.hasOption("exSSO")) {
                String[] exSSO = cmd.getOptionValues("exSSO");
                for (String sso : exSSO) {
                    System.out.println(sso);
                    for (EvalUnit.SpecialObjects et : EvalUnit.SpecialObjects.values())
                        if (sso.matches(et.toString()))
                            specialSchemaElements.add(et);
                }
            }


            List<EvalUnit.VocabularyTypes> vocabularyTypesList = new ArrayList<>();
            if (cmd.hasOption("vocTypes")) {
                String[] vocTypes = cmd.getOptionValues("vocTypes");
                for (String vT : vocTypes) {
                    System.out.println(vT);
                    for (EvalUnit.VocabularyTypes et : EvalUnit.VocabularyTypes.values())
                        if (vT.matches(et.toString())) {
                            vocabularyTypesList.add(et);
                        }
                }
                if (vocabularyTypesList.size() == 2) {

                        EvalUnit.VocabularyTypes typeGold = vocabularyTypesList.get(0);
                        EvalUnit.VocabularyTypes typeEval = vocabularyTypesList.get(1);

                        for (EvalUnit.VocabularyTypes t: EvalUnit.vocabularies.keySet()){
                            System.out.println("Vocab Keys:" +Arrays.toString(EvalUnit.vocabularies.keySet().toArray()));
                            if (t.equals(typeGold)) {
                                goldVocab = EvalUnit.vocabularies.get(t);
                                System.out.println("Goldvocab="+t.toString());

                            }
                            if (t.equals(typeEval)) {
                                apprVocab = EvalUnit.vocabularies.get(t);
                                System.out.println("apprVocab="+t.toString());


                            }

                        }






                }
                else  {
                    formatterC.printHelp(80, " ", "ERROR: PARAMETERS OF \"vocTypes\" ARE ERRONEOUS\n",
                            computeOptions, "\nError occurred! Please see the error message above", true);
                    System.exit(-1);
                }

            }
            boolean debug = false;
            if (cmd.hasOption("debug")) {
                debug = true;

                System.out.println("Debug:"+debug);
            }
            boolean skipElements = false;
            if (cmd.hasOption("sE")) {
                skipElements = true;

                System.out.println("skipElements:"+skipElements);
            }
            boolean skipProperties = false;
            if (cmd.hasOption("sP")) {
                skipProperties = true;

                System.out.println("skipProperties:"+skipProperties);
            }

            if (specialSchemaElements.size() > 0 && !(skipElements || skipProperties)) {
                formatterC.printHelp(80, " ", "ERROR: You need either or both skipElements or skipProperties for exSSO to work\n SSO:" +specialSchemaElements.size() + skipProperties +skipElements,
                        computeOptions, "\nError occurred! Please see the error message above", true);
                System.exit(-1);
            }

            EvalUnit evalUnit = new EvalUnit(Connection::getRDF4JConnector, srv,
                    goldRep, goldVocab, apprRep, apprVocab, debug, evalTypes, specialSchemaElements,skipProperties,skipElements);

            long start = System.currentTimeMillis();

            evalUnit.evaluate();


            Date date = new Date((System.currentTimeMillis() - start));
            DateFormat formatter = new SimpleDateFormat("mm:ss:SSS");

            System.out.println("Duration: " + formatter.format(date) + " ms");

            // everything's fine, run the program

        } catch (ParseException e2) {
            formatterC.printHelp(80, " ", "ERROR: " + e2.getMessage() + "\n",
                    computeOptions, "\nError occurred! Please see the error message above", true);
            System.exit(-1);
        }


    }



}
