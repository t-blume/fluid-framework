package output.implementation;

import common.IResource;
import main.Main;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static junit.framework.TestCase.assertTrue;
import static junit.framework.TestCase.fail;


public class ExactCoordinatorTest {
    @Before
    public static void setUp() {
        listDeluxe = new ArrayList<>();
    }

    @After
    public static void clear() {
        listDeluxe = null;
    }
    public static List<Map<Map<String, IResource>,Map<IResource, Set<IResource>>>> listDeluxe = new ArrayList<>();

    @Test
    public static void  testIntegrity(){
        // Plan editiere die main Funktion mit Parameter test
        // füge Listener Hinzu irgendwie?
        //Für jeden von "updateSchemaElement" in der SchemaElementFactory wird aus dem ExactCoordinator ein paar aus
        // instance2element map (neue Map gefüllt mit putAll(..) and instance2payload weggespeichert
        /*
        Hier der Run.Main-Aufruf mit param listDeluxe und "test = true" oder so
         */

        // call main
        //////CONFIGURATION //////
        List<String> files = new LinkedList<>();
        files.add("placeholder"); //here you need to get path to timbl-500.nq
        boolean recursive = false;
        boolean zip = false;
        boolean useIncomingProps = true;
        boolean useOWLSameAs = true;
        boolean useRDFS = true;
        String useExternalSG = "placeholder"; //path  schemaGraph i guess? or schema.properties?
        boolean fixLiterals = true;
        boolean fixBlankNodes = true;
        boolean filterRDFS =false;

        String outputFolder = "placeholder"; // this propably doesnt matter really, since i wont look at the output
                                        //maybe disable all the output stuff, when testing is activated
        String externalRepositoryURL = "placeholder"; //brauch ich das?

        Main.WriterStyle writerStyle = Main.WriterStyle.FILE; //propably doesnt matter since i don't write in testing
        boolean clearRepo =true; // Ey kein plan
        int windowSize = 1000; //????

        int schemaCacheSize = 1000; // ????
        int databaseCacheSize = 1000;


        // dieser aufruf brauch noch 2 parameter
        //1. die Liste welche geupdatet wird
        //2. test = true oder so
        main.Main.configureAndStart(files, recursive, zip,
        useIncomingProps,  useOWLSameAs,  useRDFS,  useExternalSG,
         fixLiterals,  fixBlankNodes, filterRDFS,
         outputFolder,  externalRepositoryURL,
                writerStyle,  clearRepo,  windowSize,
         schemaCacheSize);
        //test the listDeluxe
        if (listDeluxe.size() > 0) {
            listDeluxe.forEach(LIST_EL -> {
                LIST_EL.forEach((PAIR_EL_1,PAIR_EL_2) -> {
                    if (PAIR_EL_1.keySet().size() ==0 || PAIR_EL_2.keySet().size() ==0)
                        fail("A MAP IS EMPTY!!!");

                    if (PAIR_EL_1.keySet().size() != PAIR_EL_1.values().size())
                        fail("keys and values do not match PAIR EL 1");

                    if (PAIR_EL_2.keySet().size() != PAIR_EL_2.values().size())
                        fail("keys and values do not match PAIR EL 2");



                    PAIR_EL_1.forEach((MAP_EL_1,MAP_EL_2) -> {

                        //check both maps if they have valid pairs
                        if (!MAP_EL_2.equals(null) )
                            assertTrue(true);

                        else
                            fail("key: " + MAP_EL_1.toString() + " has No Value attached");



                    });
                    PAIR_EL_2.forEach((MAP_EL_1,MAP_EL_2) -> {

                        //check both maps if they have valid pairs

                        if (!MAP_EL_2.equals(null)&& MAP_EL_2.size() != 0)
                            assertTrue(true);

                        else
                            fail("key: " + MAP_EL_1.toString() + " has No Value attached");


                    });
                });
            });
        }
        else {
            fail("THE LIST IS EMPTY");
        }


    }


    private class ListenerDeluxe {

        // Here store a list of maps for both instance2element map and instance2payload map clones from ExactCoordinatorTest


    }

}