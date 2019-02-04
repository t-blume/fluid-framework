package implementation.eval;


import implementation.utils.VariablesGenerator;
import implementation.utils.datastructs.EvalValueArray;
import implementation.utils.datastructs.SetSet;
import implementation.utils.datastructs.ValueArray;
import implementation.vocabularies.FLuIDVocabulary;
import implementation.vocabularies.SchemEXVocabulary;
import implementation.vocabularies.VocabularyConstants;
import interfaces.Connector;

import java.util.*;

import static implementation.connectors.Connection.*;

/**
 * Created by Blume Till on 22.08.2016.
 */
public class EvalUnit {

    private Set<String> propertyObjectClusterURIs;
    private Set<String> propertyClusterURIs;

    private Set<String> CSEURIs;



    //here the vocabularies are initialised


    public static Map<VocabularyTypes, VocabularyConstants> vocabularies = new HashMap<>();

    public enum VocabularyTypes {
        schemex, fluid
    }
    static {

        VocabularyConstants fluid = new FLuIDVocabulary();
        vocabularies.put(fluid.GET_VOCABULARY_TYPE(),fluid);

        VocabularyConstants schemex = new SchemEXVocabulary();
        vocabularies.put(schemex.GET_VOCABULARY_TYPE(),schemex);
    }
    public enum EvalType {
        OC, SUP_OC, PC, SUP_PC, POC, SUP_POC, CSE, SUP_CSE
    }

    // CONST_UNRESOLVED_LITERAL_CLUSTER
    // UNRESOLVED_OBJECT_CLUSTER
    // EMPTY_OBJECTS
    // UNRESOLVED_PROPERTY_CLUSTER
    // EMPTY_PROPERTIES
    // UNRESOLVED_PROPERTY_OBJECT_CLUSTER
    // EMPTY_PROPERTY_OBJECTS
    public enum SpecialObjects {
        UL, UO, EO, UP, EP, UPO, EPO, EL, //--> skipelement
    }






    private static final int interval = 1500;

    //db stuff
    private DBConnection dbConnection;
    private final String url;
    private final String graphGold;
    private final String graphEval;

    //TODO: not needed?
    private ValueArray precision_predicates = new ValueArray();
    private ValueArray recall_predicates = new ValueArray();

    //implementation.eval stuff
    private EvalValueArray evalType = new EvalValueArray();
    private EvalValueArray evalTCSupTC = new EvalValueArray();
    private EvalValueArray evalSUP_OC = new EvalValueArray();
    private EvalValueArray evalEQC = new EvalValueArray();
    private EvalValueArray evalEQCaTC = new EvalValueArray();
    private EvalValueArray evalSUP_PC = new EvalValueArray();
    private EvalValueArray evalOC = new EvalValueArray();
    private EvalValueArray evalSUP_POC = new EvalValueArray();
    private EvalValueArray evalSUP_CSE = new EvalValueArray();

    //optimizations
    private Set<String> typeClusterURIs;
    private Set<String> equiClassesURIs;


    private VocabularyConstants vocabularyConstantsGold;
    private VocabularyConstants vocabularyConstantsEval;
    private Set<String> typeClusterURIsEval;
    private Set<String> pocURIsEval;
    private Set<String> cseClassesURIs;
    private Connector conGold;
    private Connector conEval;

    private final boolean debug;
    private final boolean skipProperties;
    private final boolean skipElements;
    private final Set<EvalType> evalOpts;
    private final Set<SpecialObjects> specialSchemaElements;

    //implementation.eval stuff
    private EvalValueArray evalPC = new EvalValueArray();
    private EvalValueArray evalCSE = new EvalValueArray();
    private EvalValueArray evalPOCHash = new EvalValueArray();

    public EvalUnit(DBConnection dbConnection, String url, String graphGold, VocabularyConstants vocabularyConstantsGold, String graphEval,
                    VocabularyConstants vocabularyConstantsEval, boolean debug, Set<EvalType> evalOpts, Set<SpecialObjects> specialSchemaElements,
                    boolean skipProperties, boolean skipElements) {
        this.dbConnection = dbConnection;
        this.graphGold = graphGold;
        this.graphEval = graphEval;
        this.url = url;
        this.vocabularyConstantsGold = vocabularyConstantsGold;
        this.vocabularyConstantsEval = vocabularyConstantsEval;
        this.debug = debug;
        this.evalOpts = evalOpts;
        this.specialSchemaElements = specialSchemaElements;
        this.skipProperties = skipProperties;
        this.skipElements = skipElements;
    }


    private String buildImpactParams () {
        String result = "";
        if (skipProperties) {
            result += "_"+"skipProperties";
            for (EvalUnit.SpecialObjects o : specialSchemaElements) {
                result += "_"+o;
            }
        }
         if (skipElements) {
            result += "_"+"skipElements";
            for (EvalUnit.SpecialObjects o : specialSchemaElements) {
                result += "_"+o;
            }
        }
        return result;
    }
    public void evaluate() {
        if (evalOpts.contains(EvalType.SUP_OC) || evalOpts.isEmpty()) {
            evalSUP_OC();
            Thread write = new Thread(() -> evalSUP_OC.toFile("out/" + graphEval+buildImpactParams() + "/SUP_OC.csv"));
            write.start();

        }

        if (evalOpts.contains(EvalType.OC) || evalOpts.isEmpty()) {
            evalOC(); //TODO: @Marius: OC -> exact matching of URIs, SUP_OC -> extract all objects + build query

            Thread write = new Thread(() -> evalOC.toFile("out/" + graphEval+buildImpactParams() + "/OC.csv"));
            write.start();
        }

        if (evalOpts.contains(EvalType.POC) || evalOpts.isEmpty()) {

            evalPOC();
            Thread write = new Thread(() -> evalPOCHash.toFile("out/" + graphEval+buildImpactParams() + "/POC.csv"));
            write.start();

        }

        if (evalOpts.contains(EvalType.SUP_POC) || evalOpts.isEmpty()) {
            evalSUP_POC();
            Thread write = new Thread(() -> evalSUP_POC.toFile("out/" + graphEval+buildImpactParams() + "/SUP_POC.csv"));
            write.start();

        }

        if (evalOpts.contains(EvalType.SUP_PC) || evalOpts.isEmpty()) {
            evalSUP_PC();
            Thread write = new Thread(() -> evalSUP_PC.toFile("out/" + graphEval+buildImpactParams() + "/SUP_PC.csv"));
            write.start();

        }

        if (evalOpts.contains(EvalType.PC) || evalOpts.isEmpty()) {
            evalPC();

            Thread write = new Thread(() -> evalPC.toFile("out/" + graphEval+buildImpactParams() + "/PC.csv"));
            write.start();
        }


        if (evalOpts.contains(EvalType.SUP_CSE) || evalOpts.isEmpty()) {
            evalSUP_CSE();

            Thread write = new Thread(() -> evalSUP_CSE.toFile("out/" + graphEval+buildImpactParams() + "/SUP_CSE.csv"));
            write.start();


        }

        if (evalOpts.contains(EvalType.CSE) || evalOpts.isEmpty()) {
            evalCSE();
            Thread write = new Thread(() -> evalCSE.toFile("out/" + graphEval+buildImpactParams() + "/CSE.csv"));
            write.start();


        }


    }
    //evalEQC einfach runterproggen, dass es funktioniert hash wert vergleiche !!! genau so für fluid

    /**
     * This function evaluates the type complex schema elements
     * // there needs to a function which can be recursively called, which gets the next row of elements:
     * // either cse, poc, oc, tc
     * // --> cse: get the next row again
     * // --> poc: return poc query
     * // --> oc : return oc query
     * // --> tc : return tc query
     * // !!<!>!! but here the problem is that we first get the cse from the gold graph and we need to connect them to
     * //         the eval graph. Till mentioned this can later be done via the hash value at the end of the URI.
     * //         If that doesn't work we need to do this with identifying attributes. Basically get a identifying
     * //         attribute tree from the gold graph, and with that create the eval cse query.
     * //
     * <p>
     * <p>
     * // This propably needs some kind of modular query builder for cse
     * //--- propably in a for loop which adds whenever the gold element finds an element, a constraint is added to the eval
     * // query as well
     */

    public void evalSUP_CSE() {
        System.out.println("_______________");
        System.out.println("evalSUP_CSE");

        conGold = dbConnection.getConnector(url, graphGold);
        conEval = dbConnection.getConnector(url, graphEval);

        // in this query only get the prime cse (those which have no other cse above them --> filter query catching this pattern)
        String cseQuery = vocabularyConstantsGold.RETRIEVE_ALL_PRIME_COMPLEX_SCHEMA_ELEMENTS();

        // for (String eqc : getEQC_URIs()) {
        for (String cse : getURIs_GOLD(cseQuery, cseClassesURIs)) {

            String evalPrimeCse = "?" + VariablesGenerator.getInstance().getVariableName("primecse");
            Map<String, String> resultMap = evalCSERecursion(cse, evalPrimeCse);
            if (resultMap == null)
                continue;
            String queryEval = resultMap.get("eval");
            queryEval += vocabularyConstantsEval.ADD_CSE_QUERY_CONSTRAINT_DS(evalPrimeCse);


            String queryGold = resultMap.get("gold"); // technically you could directly take the ds from the cse
            queryGold += vocabularyConstantsGold.ADD_CSE_QUERY_CONSTRAINT_DS(cse);

            eval(queryGold, queryEval, conGold, conEval, evalSUP_CSE, debug);


        }
        conGold.close();
        conEval.close();
        System.out.println(evalSUP_CSE.size());
        System.out.println("Precision;Recall;F1");
        System.out.println(evalSUP_CSE.average());


    }

    /**
     * The idea is to recursively call this function, to build a query in a cumulative way.
     *
     * @return Map key:QueryConstraint: "eval":EvalConstraints, "gold":GoldConstraints
     */
    public Map<String, String> evalCSERecursion(String cseNodeGold, String cseNodeEval) {
        Map<String, String> constraintsGoldEval = new HashMap<>();
        String queryConstraintGold = "";
        String queryConstraintEval = "";

        Set<String> pcSet = queryDatasource(conGold, vocabularyConstantsGold.GET_CONSTRAINTS_PC_FROM_CSE(cseNodeGold), "c");
        for (String s : pcSet) {
            //add constraint to the current node
            // GET ALL THE PROPERTIES OF THE PROPERTY CLUSTER
            Set<String> properties = queryDatasource(conGold, vocabularyConstantsGold.GET_CONSTRAINTS_PROPERTIES_FROM_PC(s), "p");

            //SHOULD BE IMPLEMENTED FOR FLuID
            queryConstraintGold += vocabularyConstantsGold.ADD_CSE_QUERY_CONSTRAINT_PC(cseNodeGold, properties);
            queryConstraintEval += vocabularyConstantsEval.ADD_CSE_QUERY_CONSTRAINT_PC(cseNodeEval, properties);

        }
        //TODO !!! skip the whole element if it uses one of them as subject equivalence/hastypeCluster --> return value skip (boolean) --> return null
        // TODO !!! + skip the property of a EQC/POC if the link object matches one the above.


        Set<String> ocSet = queryDatasource(conGold, vocabularyConstantsGold.GET_CONSTRAINTS_OC_FROM_CSE(cseNodeGold), "c");

        for (String s : ocSet) {
            //GET ALL THE HASHVALUES this should work

            //add constraint to the current node
            if (s.equals(vocabularyConstantsGold.CONST_UNRESOLVED_LITERAL_CLUSTER().
                    replace("<", "").replace(">", ""))) {
                if (!skipElements &&!specialSchemaElements.contains(SpecialObjects.UL)) { //TODO OK SChau dir genau an was Till will im ISSUE
                    //einfach skippen?
                    queryConstraintGold += vocabularyConstantsGold.ADD_CSE_QUERY_CONSTRAINT_OC_UNRESOLVED_LITERAL_CLUSTER(cseNodeGold);
                    queryConstraintEval += vocabularyConstantsEval.ADD_CSE_QUERY_CONSTRAINT_OC_UNRESOLVED_LITERAL_CLUSTER(cseNodeEval);
                }
                else  {
                    return null;
                }


            } else if (s.equals(vocabularyConstantsGold.CONST_EMPTY_LITERAL_CLUSTER().
                    replace("<", "").replace(">", ""))) {
                if (!skipElements &&!specialSchemaElements.contains(SpecialObjects.EL)) { //TODO

                    queryConstraintGold += vocabularyConstantsGold.ADD_CSE_QUERY_CONSTRAINT_OC_EMPTY_LITERAL_CLUSTER(cseNodeGold);
                    queryConstraintEval += vocabularyConstantsEval.ADD_CSE_QUERY_CONSTRAINT_OC_EMPTY_LITERAL_CLUSTER(cseNodeEval);
                }
                else {
                    return null;
                }


            } else {
                queryConstraintGold += vocabularyConstantsGold.ADD_CSE_QUERY_CONSTRAINT_OC(cseNodeGold,
                        vocabularyConstantsGold.EXTRACT_HASH_VALUE_FROM_OC(s));
                queryConstraintEval += vocabularyConstantsEval.ADD_CSE_QUERY_CONSTRAINT_OC(cseNodeEval,
                        vocabularyConstantsGold.EXTRACT_HASH_VALUE_FROM_OC(s));
            }


        }

        Set<String> pocSet = queryDatasource(conGold, vocabularyConstantsGold.GET_CONSTRAINTS_POC_FROM_CSE(cseNodeGold), "c");

        for (String s : pocSet) {
            //GET ALL THE PROPERTY OBJECT PAIRS with ONLY HASHVALUE AS OBJECT @see evalSUP_POC
            Set<Map<String, String>> propertyObjectPairsOrig = new HashSet<>();
            //TODO make sure this is correct apparently all properties have an object, catch empty and unresolved and stuff oc
            if (s.equals(vocabularyConstantsGold.CONST_NO_PROPERTY_OBJECT_CLUSTER().
                    replace("<", "").replace(">", ""))) {
                if (!skipElements && !specialSchemaElements.contains(SpecialObjects.EPO)) {
                    queryConstraintGold += vocabularyConstantsGold.ADD_CSE_QUERY_CONSTRAINT_NO_POC(cseNodeGold);
                    queryConstraintEval += vocabularyConstantsEval.ADD_CSE_QUERY_CONSTRAINT_NO_POC(cseNodeEval);
                }
                else {
                    return null;
                }


            } else {
                propertyObjectPairsOrig.addAll(queryDatasourceDiffVars(conGold, vocabularyConstantsGold.
                        GET_CONSTRAINTS_PROPERTY_OBJECT_PAIRS_FROM_POC(s), "prop", "obj"));//i assume that there are only 1:1 connections

                //This data
                Set<Map<String, String>> propertyObjectPairsWithOnlyHashObj = new HashSet<>();


                createPOCPairs(propertyObjectPairsOrig, propertyObjectPairsWithOnlyHashObj);

                if (propertyObjectPairsWithOnlyHashObj.size() == 0) {
//                    System.out.println("No Property-Object Pairs");
//                    System.out.println(vocabularyConstantsGold.GET_CONSTRAINTS_PROPERTY_OBJECT_PAIRS_FROM_POC(s));
                    continue;
                }


                queryConstraintGold += vocabularyConstantsGold.ADD_CSE_QUERY_CONSTRAINT_POC(cseNodeGold, propertyObjectPairsWithOnlyHashObj);
                queryConstraintEval += vocabularyConstantsEval.ADD_CSE_QUERY_CONSTRAINT_POC(cseNodeEval, propertyObjectPairsWithOnlyHashObj);
            }

        }


        Set<String> cseSet = queryDatasource(conGold, vocabularyConstantsGold.GET_CONSTRAINTS_CSE_FROM_CSE(cseNodeGold), "c");
        // get all complexSchemaElements (maybe check wether the cse was already visited
        // Problem here is that all are already in the queue from evalSUP_CSE which iterates over all cse
        //
        for (String s : cseSet) {
            String varEval = VariablesGenerator.getInstance().getVariableName("cse");
            //Maybe just link we are a unique variable aka ?cse123 to the schema elements below it
            queryConstraintGold += vocabularyConstantsGold.ADD_CSE_QUERY_CONSTRAINT_CSE(cseNodeGold, s);
            queryConstraintEval += vocabularyConstantsEval.ADD_CSE_QUERY_CONSTRAINT_CSE(cseNodeEval, varEval);

            //

            //and check next node
            Map<String, String> result = evalCSERecursion(s, varEval);
            if (result==null)
                return null;


            queryConstraintGold += result.get("gold");
            queryConstraintEval += result.get("eval");
            //recursive function call

        }

        constraintsGoldEval.put("eval", queryConstraintEval);
        constraintsGoldEval.put("gold", queryConstraintGold);


        return constraintsGoldEval;

    }

    private void createPOCPairs(Set<Map<String, String>> propertyObjectPairsOrig, Set<Map<String, String>> propertyObjectPairsWithOnlyHashObj) {
        for (Map<String, String> pair : propertyObjectPairsOrig) {
            HashMap<String, String> hashMap = new HashMap<>();
            String prop = pair.get("prop");
            String obj = vocabularyConstantsGold.EXTRACT_HASH_VALUE_FROM_OC(pair.get("obj"));

            if (("<" + prop + ">").equals(vocabularyConstantsGold.CONST_RDF_TYPE()) || vocabularyConstantsGold.IS_FILTER_PROPERTY(prop)) {
                continue;
            }
            //check to which oc the property is connected to
            if (obj.equals(vocabularyConstantsGold.CONST_EMPTY_LITERAL_CLUSTER())) {

                if (!skipProperties &&!specialSchemaElements.contains(SpecialObjects.EL)) {

                obj = vocabularyConstantsEval.CONST_EMPTY_LITERAL_CLUSTER();
                hashMap.put(pair.get("prop"), obj);}

            } else if (obj.equals(vocabularyConstantsGold.CONST_UNRESOLVED_LITERAL_CLUSTER())) {
                if (!skipProperties &&!specialSchemaElements.contains(SpecialObjects.UL)) {

                    obj = vocabularyConstantsEval.CONST_UNRESOLVED_LITERAL_CLUSTER();
                hashMap.put(pair.get("prop"), obj);}
            } else {
                hashMap.put(pair.get("prop"), vocabularyConstantsGold.EXTRACT_HASH_VALUE_FROM_OC(obj));

            }

            propertyObjectPairsWithOnlyHashObj.add(hashMap);
        }
    }


    /**
     * This function is used to store a result of one comparison into a EvalValueArray and display some statistics if
     * the result isn't perfect.
     *
     * @param goldQueryDS Query which queries for datasources in the gold graph.
     * @param evalQueryDS Query which queries for datasources in the eval graph.
     * @param conGold     Connection to the gold graph.
     * @param conEval     Connection to the eval graph.
     * @param evalArray   EvalValueArray into which the result is to be stored.
     */
    public void eval(String goldQueryDS, String evalQueryDS, Connector conGold, Connector conEval, EvalValueArray evalArray, Boolean debug) {
        Set<String> setGold = queryDatasource(conGold, goldQueryDS, "ds");
        Set<String> setEval = queryDatasource(conEval, evalQueryDS, "ds");
        Comparator comparator = new Comparator(setGold, setEval);

        evalArray.addResult(comparator);

        if (debug && (comparator.getMssing().size() > 0 || comparator.getWrong().size() > 0)) {
            System.out.println("GOLD QUERY:\n" + goldQueryDS);
            System.out.println("EVAL QUERY:\n" + evalQueryDS + "\n");
            System.out.println("GOLD DS: " + setGold);
            System.out.println("EVAL DS: " + setEval);
            if (comparator.getMssing().size() > 0)
                System.out.println("Missing: " + comparator.getMssing());

            if (comparator.getWrong().size() > 0)
                System.out.println("Wrong: " + comparator.getWrong());
            System.out.println("------------------------------------------------------\n");
        }
    }

    /**
     * This evaluation function works in the following way:
     * 1. Get all the property cluster from the Gold Graph
     * 2. For each property cluster
     * 1. get the attached properties
     * 2. filter unwanted properties
     * 3. create a gold query based on the remaining properties to find the related datasources
     * 4. create a eval query based on the remaining properties to find the related datasources
     * 5. query the datasources of eval and gold
     * 6. compare them (missing;wrong;precision;recall;f1-measure)
     */
    public void evalSUP_PC() {
        System.out.println("_______________");
        System.out.println("evalSUP_PC");

        Connector conGold = dbConnection.getConnector(url, graphGold);
        Connector conEval = dbConnection.getConnector(url, graphEval);


        String pcQuery = vocabularyConstantsGold.RETRIEVE_ALL_GET_PROPERTY_CLUSTER();
        // for (String eqc : getEQC_URIs()) {
        for (String pc : getURIs_GOLD(pcQuery, equiClassesURIs)) {
            //debug

            String queryEval = "";
            String queryGold = "";
            if (pc.equals(vocabularyConstantsGold.CONST_EMPTY_PROPERTIES_CLUSTER().replace("<", "").replace(">", ""))) {
                if (!skipElements &&!specialSchemaElements.contains(SpecialObjects.EP)){
                    continue;
                }
                else
                    continue; //TODO TIll

            } else {


                // make this into the vocabulary
                Set<String> propertySet = queryDatasource(conGold, vocabularyConstantsGold.GET_CONSTRAINTS_PROPERTIES_FROM_PC(pc), "p");
                Set<String> removeSet = new HashSet<>();
                //this part filters rdf_type and whatever is defined in the gold vocabulary
                propertySet.forEach(X -> {
                    if (("<" + X + ">").equals(vocabularyConstantsGold.CONST_RDF_TYPE()) || vocabularyConstantsGold.IS_FILTER_PROPERTY(X))
                        removeSet.add(X);
                });
                propertySet.removeAll(removeSet);


                queryEval = vocabularyConstantsEval.GET_DS_FROM_PROPERTIES(propertySet);

                queryGold = vocabularyConstantsGold.GET_DS_FROM_PROPERTIES(propertySet);


            }
            eval(queryGold, queryEval, conGold, conEval, evalSUP_PC, debug);
        }
        conGold.close();
        conEval.close();
        System.out.println(evalSUP_PC.size());

        System.out.println("Precision;Recall;F1");
        System.out.println(evalSUP_PC.average());

    }

    /**
     * This evaluation function works in the following way:
     * 1. Get all the property object cluster from the Gold Graph (for schemex all eqc)
     * 2. For each property object cluster
     * 1. get the attached properties object pairs (for schemex this means getting the p/o pairs with a valid o)???
     * 2. create a gold query based on the remaining properties, objects to find the related datasources
     * 3. query the datasources of eval and gold
     * 4. compare them (missing;wrong;precision;recall; f1-measure)
     * !!! OK ROLL BACK SCHEMEX POC IS a LINK PROP TO AN OC cse (?p ?o) --> POC where ?o is valid right?
     */
    public void evalSUP_POC() {
        System.out.println("_______________");
        System.out.println("evalSUP_POC");

        Connector conGold = dbConnection.getConnector(url, graphGold);
        Connector conEval = dbConnection.getConnector(url, graphEval);

        String query = vocabularyConstantsGold.RETRIEVE_ALL_PROPERTY_OBJECT_CLUSTER();

        for (String poc : getURIs_GOLD(query, pocURIsEval)) {
            String queryEval = "";
            String queryGold = "";
            if (poc.equals(vocabularyConstantsGold.CONST_NO_PROPERTY_OBJECT_CLUSTER().
                    replace("<", "").replace(">", ""))) {

                if (!skipElements &&!specialSchemaElements.contains(SpecialObjects.EPO)) {
                    queryEval = vocabularyConstantsEval.GET_DS_FROM_NO_PROPERTY_OBJECT_CLUSTER();
                    queryGold = vocabularyConstantsEval.GET_DS_FROM_NO_PROPERTY_OBJECT_CLUSTER();
                }
                else
                    continue;

            } else {
                Set<Map<String, String>> propertyObjectPairsOrig = new HashSet<>();
                //TODO make sure this is correct apparently all properties have an object, catch empty and unresolved and stuff oc
                // add the moment the query only gets the pairs which have types attached
                propertyObjectPairsOrig.addAll(queryDatasourceDiffVars(conGold, vocabularyConstantsGold.GET_CONSTRAINTS_PROPERTY_OBJECT_PAIRS_FROM_POC(poc), "prop", "obj")); // i assume that there are only 1:1 connections

                Set<Map<String, String>> propertyObjectPairsWithOnlyHashObj = new HashSet<>();


                createPOCPairs(propertyObjectPairsOrig, propertyObjectPairsWithOnlyHashObj);

                if (propertyObjectPairsWithOnlyHashObj.size() == 0) {
//                    System.out.println("No Property-Object Pairs");
//                    System.out.println(vocabularyConstantsGold.GET_CONSTRAINTS_PROPERTY_OBJECT_PAIRS_FROM_POC(poc));
                    continue;
                }

                queryEval = vocabularyConstantsEval.GET_DS_FROM_PROPERTY_OBJECT_PAIRS(propertyObjectPairsWithOnlyHashObj);

                queryGold = vocabularyConstantsGold.GET_DS_FROM_PROPERTY_OBJECT_PAIRS(propertyObjectPairsWithOnlyHashObj);
            }

//           > System.out.println("Eval\n"+ queryEval);
//            System.out.println("Gold\n"+ queryGold);

            eval(queryGold, queryEval, conGold, conEval, evalSUP_POC, debug);


        }


        conEval.close();
        conGold.close();
        System.out.println(evalSUP_POC.size());
        System.out.println("Precision;Recall;F1");
        System.out.println(evalSUP_POC.average());


    }


    /**
     * This evaluation function works in the following way:
     * 1. Get all the object cluster from the Gold Graph
     * 2. For each  object cluster
     * 1. get the attached types
     * 2. create a gold query based on the remaining types to find the related datasources
     * 3. query the datasources of eval and gold
     * 4. compare them (missing;wrong;precision;recall; f1-measure)
     */
    public void evalSUP_OC() {
        System.out.println("_______________");
        System.out.println("evalSUP_OC");

        Connector conGold = dbConnection.getConnector(url, graphGold);
        Connector conEval = dbConnection.getConnector(url, graphEval);

        //for (String oc : getOC_URIsGold()) {
        String query = vocabularyConstantsGold.RETRIEVE_ALL_OBJECT_CLUSTER();
        for (String oc : getURIs_GOLD(query, typeClusterURIs)) {
            String queryGold;
            String queryEval;
            if (oc.equals(vocabularyConstantsGold.CONST_UNRESOLVED_LITERAL_CLUSTER().
                    replace("<", "").replace(">", ""))) {
                if (!skipElements &&!specialSchemaElements.contains(SpecialObjects.UL)){
                    queryGold = vocabularyConstantsGold.GET_DS_FROM_UNRESOLVED_LITERAL_CLUSTER();
                    queryEval = vocabularyConstantsEval.GET_DS_FROM_UNRESOLVED_LITERAL_CLUSTER();
                }
                else
                    continue;



            } else if (oc.equals(vocabularyConstantsGold.CONST_EMPTY_LITERAL_CLUSTER().
                    replace("<", "").replace(">", ""))) {
                if (!skipElements &&!specialSchemaElements.contains(SpecialObjects.EL)) {


                    queryGold = vocabularyConstantsGold.GET_DS_FROM_EMPTY_LITERAL_CLUSTER();
                    queryEval = vocabularyConstantsEval.GET_DS_FROM_EMPTY_LITERAL_CLUSTER();
                }
                else
                    continue;

            } else {
                Set<String> types = queryDatasource(conGold, vocabularyConstantsGold.GET_CONSTRAINTS_OC_TYPES_FROM_OC(oc), "type");
                if (types.size() == 0)
                    continue;
                queryGold = vocabularyConstantsGold.GET_DS_FROM_TYPES(types);
                queryEval = vocabularyConstantsEval.GET_DS_FROM_TYPES(types);
            }
            eval(queryGold, queryEval, conGold, conEval, evalSUP_OC, debug);
        }

        conEval.close();
        conGold.close();

        System.out.println(evalSUP_OC.size());
        System.out.println("Precision;Recall;F1");
        System.out.println(evalSUP_OC.average());

    }

    /**
     * The Types in this case are the identifying variables maybe those should be returned.
     */
    public void evalOC() {
        System.out.println("_______________");
        System.out.println("evalSUP_OC via hash value");

        Connector conGold = dbConnection.getConnector(url, graphGold);
        Connector conEval = dbConnection.getConnector(url, graphEval);

        //for (String oc : getOC_URIsGold()) {
        String query = vocabularyConstantsGold.RETRIEVE_ALL_OBJECT_CLUSTER();
        for (String oc : getURIs_GOLD(query, typeClusterURIs)) {

            String queryGold = "";

            String queryEval = "";

            if (oc.equals(vocabularyConstantsGold.CONST_UNRESOLVED_LITERAL_CLUSTER().
                    replace("<", "").replace(">", ""))) {
                if (!skipElements &&!specialSchemaElements.contains(SpecialObjects.UL)) {

                    queryGold = vocabularyConstantsGold.GET_DS_FROM_UNRESOLVED_LITERAL_CLUSTER();
                    queryEval = vocabularyConstantsEval.GET_DS_FROM_UNRESOLVED_LITERAL_CLUSTER();
                }
                else
                    continue;


            } else if (oc.equals(vocabularyConstantsGold.CONST_EMPTY_LITERAL_CLUSTER().
                    replace("<", "").replace(">", ""))) {
                if (!skipElements &&!specialSchemaElements.contains(SpecialObjects.EL)) {

                    queryGold = vocabularyConstantsGold.GET_DS_FROM_EMPTY_LITERAL_CLUSTER();
                    queryEval = vocabularyConstantsEval.GET_DS_FROM_EMPTY_LITERAL_CLUSTER();
                }
                else
                    continue;


            } else {

                queryGold = vocabularyConstantsGold.GET_DS_VIA_OC(oc);

                queryEval = vocabularyConstantsEval.GET_DS_VIA_OC(vocabularyConstantsEval.
                        CREATE_OC_VIA_HASH(vocabularyConstantsGold.EXTRACT_HASH_VALUE_FROM_OC(oc)));


            }


            eval(queryGold, queryEval, conGold, conEval, evalOC, debug);

        }

        conEval.close();
        conGold.close();

        System.out.println(evalOC.size());
        System.out.println("Precision;Recall;F1");
        System.out.println(evalOC.average());

    }

    /**
     * The Types in this case are the identifying variables maybe those should be returned.
     */
    public void evalPOC() {
        System.out.println("_______________");
        System.out.println("evalSUP_POC via hash value");

        Connector conGold = dbConnection.getConnector(url, graphGold);
        Connector conEval = dbConnection.getConnector(url, graphEval);

        //for (String oc : getOC_URIsGold()) {
        String query = vocabularyConstantsGold.RETRIEVE_ALL_PROPERTY_OBJECT_CLUSTER();
        for (String poc : getURIs_GOLD(query, propertyObjectClusterURIs)) {

            String queryGold = "";

            String queryEval= "";

            if (poc.equals(vocabularyConstantsGold.CONST_NO_PROPERTY_OBJECT_CLUSTER())) {
                if (!skipElements &&!specialSchemaElements.contains(SpecialObjects.EPO)) {
                    queryGold = vocabularyConstantsGold.GET_DS_FROM_NO_PROPERTY_OBJECT_CLUSTER();

                    queryEval = vocabularyConstantsEval.GET_DS_FROM_NO_PROPERTY_OBJECT_CLUSTER();
                }
                else
                    continue;

            } else {

                queryGold = vocabularyConstantsGold.GET_DS_FROM_PROPERTY_OBJECT_CLUSTER(poc);

                queryEval = vocabularyConstantsEval.GET_DS_FROM_PROPERTY_OBJECT_CLUSTER((vocabularyConstantsEval.
                        CREATE_POC_VIA_HASH(vocabularyConstantsGold.EXTRACT_HASH_VALUE_FROM_POC(poc))));


            }


            eval(queryGold, queryEval, conGold, conEval, evalPOCHash, debug);

        }

        conEval.close();
        conGold.close();

        System.out.println(evalPOCHash.size());
        System.out.println("Precision;Recall;F1");
        System.out.println(evalPOCHash.average());

    }

    /**
     * The Types in this case are the identifying variables maybe those should be returned.
     */
    public void evalPC() {
        System.out.println("_______________");
        System.out.println("evalSUP_PC via hash value");

        Connector conGold = dbConnection.getConnector(url, graphGold);
        Connector conEval = dbConnection.getConnector(url, graphEval);

        //for (String oc : getOC_URIsGold()) {
        String query = vocabularyConstantsGold.RETRIEVE_ALL_GET_PROPERTY_CLUSTER();
        for (String pc : getURIs_GOLD(query, propertyClusterURIs)) {

            String queryGold = "";

            String queryEval = "";

            if (pc.equals(vocabularyConstantsGold.CONST_EMPTY_PROPERTIES_CLUSTER().
                    replace("<", "").replace(">", ""))) {
                if (!skipElements &&!specialSchemaElements.contains(SpecialObjects.EP)) {
                    queryGold = vocabularyConstantsGold.GET_DS_FROM_EMPTY_PROPERTIES_CLUSTER();
                    queryEval = vocabularyConstantsEval.GET_DS_FROM_EMPTY_PROPERTIES_CLUSTER();
                }
                else
                    continue;


            } else {

                queryGold = vocabularyConstantsGold.GET_DS_VIA_PC(pc);

                queryEval = vocabularyConstantsEval.GET_DS_VIA_PC(vocabularyConstantsEval.
                        CREATE_PC_VIA_HASH(vocabularyConstantsGold.EXTRACT_HASH_VALUE_FROM_PC(pc)));


            }


            eval(queryGold, queryEval, conGold, conEval, evalPC, debug);

        }

        conEval.close();
        conGold.close();

        System.out.println(evalPC.size());
        System.out.println("Precision;Recall;F1");
        System.out.println(evalPC.average());

    }

    /**
     * The Types in this case are the identifying variables maybe those should be returned.
     */
    public void evalCSE() {
        System.out.println("_______________");
        System.out.println("evalSUP_CSE via hash value");

        Connector conGold = dbConnection.getConnector(url, graphGold);
        Connector conEval = dbConnection.getConnector(url, graphEval);

        //for (String oc : getOC_URIsGold()) {
        String query = vocabularyConstantsGold.RETRIEVE_ALL_COMPLEX_SCHEMA_ELEMENTS();
        for (String cse : getURIs_GOLD(query, CSEURIs)) {

            String queryGold;

            String queryEval;


            queryGold = vocabularyConstantsGold.GET_DS_VIA_CSE(cse);

            queryEval = vocabularyConstantsEval.GET_DS_VIA_CSE(vocabularyConstantsEval.
                    CREATE_CSE_VIA_HASH(vocabularyConstantsGold.EXTRACT_HASH_VALUE_FROM_CSE(cse)));


            eval(queryGold, queryEval, conGold, conEval, evalCSE, debug);

        }

        conEval.close();
        conGold.close();

        System.out.println(evalCSE.size());
        System.out.println("Precision;Recall;F1");
        System.out.println(evalCSE.average());

    }

    /**
     * Starts a query which returns a specfified ?x in the query (this maybe can be parametrized).
     *
     * @param query  Input SPARQL-query containing a ?x
     * @param uriSet The URI-set the results URIs are saved into.
     * @return result set
     */
    private Set<String> getURIs_GOLD(String query, Set<String> uriSet) {
        if (uriSet == null || uriSet.size() == 0) {
            Connector connector = dbConnection.getConnector(url, graphGold);
            uriSet = queryDatasource(connector, query, "x");
        }
        if (true) {
            System.out.println("GetUris_Gold: Query: " + query);
            System.out.println("Retrieved URIs: "+uriSet.size());
        }

        return uriSet;
    }


    public SetSet getTC_TS() {
        Connector connector = dbConnection.getConnector(url, graphGold);
        SetSet setSet = new SetSet();
        for (String tc : getOC_URIsGold())
            setSet.add(queryDatasource(connector, "<" + tc + "> " + vocabularyConstantsGold.CONST_HAS_ATTRIBUTE() + " ?c"));
        connector.close();
        return setSet;
    }

    public Set<String> getOC_URIsGold() {
        if (typeClusterURIs == null) {
            Connector conGold = dbConnection.getConnector(url, graphGold);
            typeClusterURIs = queryDatasource(conGold, "?tc" + " " + vocabularyConstantsGold.CONST_RDF_TYPE() + " " + vocabularyConstantsGold.CONST_OBJECT_CLUSTER());
           // System.out.println("typeClusterURIs.size" + typeClusterURIs.size());
            //filter(typeClusterURIs, TC_NOTYPE + "|" + TC_UNRESOLVED);
           // System.out.println("typeClusterURIs.size" + typeClusterURIs.size());
        }
        return typeClusterURIs;
    }


    //////////////////////////////////////////////
    //////////////      HELPER      //////////////
    //////////////////////////////////////////////

    /**
     * @param connection
     * @param qBody
     * @param vars
     * @return
     */
    public static Set<String> queryDatasource(Connector connection, String qBody, String... vars) {
        return connection.executeQuery(SELECT, DISTINCT, vars, qBody);
    }

    public static Set<Map<String, String>> queryDatasourceDiffVars(Connector connection, String qBody, String... vars) {
        return connection.executeQueryDiffVars(SELECT, DISTINCT, vars, qBody);
    }

    public static long printProgress(int i, int max, int interval, long start) {
        if (i % interval == 0) {
            long stop = System.currentTimeMillis();
            System.out.format("Progress: %08d / %08d RQ/s: %.2f\n", i, max,
                    ((double) interval / ((double) ((stop - start) / 1000))));
            return stop;
        } else
            return -1;
    }
}
