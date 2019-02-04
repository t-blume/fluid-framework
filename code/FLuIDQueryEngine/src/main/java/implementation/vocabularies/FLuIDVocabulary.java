package implementation.vocabularies;


import implementation.eval.EvalUnit;
import implementation.utils.VariablesGenerator;
import org.mapdb.Atomic;

import java.util.*;

public class FLuIDVocabulary implements VocabularyConstants{



    //TODO IN GENERAL: CHECK CONSTANTS (remove unused, make names more clear, all without"<" &">")
    //TODO             CHECK FUNTIONS (LINE_SEPERATOR,summarise redundant in private, make names more clear)
    //TODO SEPERATE THOSECLEARLY
    public static final EvalUnit.VocabularyTypes type = EvalUnit.VocabularyTypes.fluid;

    VariablesGenerator generator = VariablesGenerator.getInstance();

    //this lists needs to be initialised
    private static String[] filterPropertyList = {"schemex", "west", "fluid"};

    public static final String NS = "http://www.fluid.informatik.uni-kiel.de/ontologies/2018/2/";

    //schema
    public static final String CLASS_COMPLEX_SCHEMA_ELEMENT = NS + "ComplexSchemaElement";
    public static final String CLASS_PROPERTY_CLUSTER = NS + "PropertyCluster";
    public static final String CLASS_PROPERTY_OBJECT_CLUSTER = NS + "PropertyObjectCluster";
    public static final String CLASS_OBJECT_CLUSTER = NS + "ObjectCluster";


    //special objects
    public static final String UNRESOLVED_LITERAL_CLUSTER = NS + "ObjectCluster";
    public static final String EMPTY_OBJECTS = CLASS_OBJECT_CLUSTER + "NoObjects";
    public static final String EMPTY_PROPERTIES = CLASS_PROPERTY_CLUSTER + "NoProperties";

    //For what is this used
    public static final String EMPTY_PROPERTY_OBJECT = CLASS_PROPERTY_OBJECT_CLUSTER + "NoProperty-Objects";

    //blank node for storing Links
    public static final String getPropertyLink = NS + "getPropertyLink";
    public static final String getLinkObject = NS + "getLinkObject";

    //storing schema information
    public static final String hasAttribute = NS + "hasAttribute";

    //storing payload information
    public static final String hasPayload = NS + "hasPayload"; //the element
    public static final String payload = NS + "payload"; //the information

    //schema-level relationships
    public static final String isSubjectEquivalence = NS + "isSubjectEquivalenceOf";
    public static final String hasSubjectEquivalence = NS + "hasSubjectEquivalence";

    //Implementation detail for reading and writing
    private String LINE_SEPERATOR = " .\n";

    public String GET_PROPERTY_LINK() {
        return getPropertyLink;
    }

    @Override
    public EvalUnit.VocabularyTypes GET_VOCABULARY_TYPE() {
        return type;
    }

    @Override
    public String CONST_NO_PROPERTY_OBJECT_CLUSTER() {
        return EMPTY_PROPERTY_OBJECT ;
    }

    @Override
    public String CONST_COMPLEX_SCHEMA_ELEMENT() {
        return CLASS_COMPLEX_SCHEMA_ELEMENT ;
    }

    @Override
    public String CONST_OBJECT_CLUSTER() {
        return  CLASS_OBJECT_CLUSTER ;
    }

    @Override
    public String CONST_PROPERTY_OBJECT_CLUSTER() {
        return CLASS_PROPERTY_OBJECT_CLUSTER ;
    }

    @Override
    public String CONST_PROPERTY_CLUSTER() {
        return CLASS_PROPERTY_CLUSTER ;
    }



    @Override
    public String CONST_HAS_PAYLOAD_ELEMENT() {
        return hasPayload ;
    }



    @Override
    public String CONST_HAS_SUBJECT_EQUIVALENCE() {
        return hasSubjectEquivalence ;
    }





    @Override
    public String CONST_IS_SUBJECT_EQUIVALENCE_OF() {
        return isSubjectEquivalence ;
    }


    @Override
    public String CONST_PAYLOAD_INFORMATION() {
        return  payload ;
    }

    @Override
    public String CONST_HAS_ATTRIBUTE() {
        return  hasAttribute ;
    }


    @Override
    public String CONST_UNRESOLVED_LITERAL_CLUSTER() {
        return UNRESOLVED_LITERAL_CLUSTER ;
    }

    @Override
    public String CONST_EMPTY_LITERAL_CLUSTER() {
        return  EMPTY_OBJECTS;
    }

    @Override
    public String CONST_EMPTY_PROPERTIES_CLUSTER() {
        return EMPTY_PROPERTIES;
    }

    @Override
    public String CONST_RDF_TYPE() {
        return "http://www.w3.org/1999/02/22-rdf-syntax-ns#type";
    }


    @Override
    public String CONST_GET_LINK_OBJECT() {
        return getLinkObject;
    }



    ///////////////////////////////////////////////////
    /////////////////////USED//////////////////////////
    ///////////////////////////////////////////////////


    //COMMON --------------------------------------------

    private String GET_DS_FROM (String object) {
        String query =  "?cse " + CONST_HAS_SUBJECT_EQUIVALENCE() + " <"+ object+ ">" + LINE_SEPERATOR+
                "?cse <" + CONST_HAS_PAYLOAD_ELEMENT() + "> ?pay " + LINE_SEPERATOR +
                "?pay <" + CONST_PAYLOAD_INFORMATION() + "> ?ds.";
        return query;
    }
    //CSE -------------------------------------------------------------


    @Override
    public String RETRIEVE_ALL_COMPLEX_SCHEMA_ELEMENTS() {
        return "?x" + " <" + CONST_RDF_TYPE() + "> <" + CONST_COMPLEX_SCHEMA_ELEMENT() +">";
    }
    @Override
    public String ADD_CSE_QUERY_CONSTRAINT_PC(String cseNode, Set<String> s) {
        String cseNodeReal = cseNode.contains("http") ? "<" + cseNode + "> " : "" + cseNode + " ";

        String query = "";
        query += "" + cseNodeReal + " " + CONST_HAS_SUBJECT_EQUIVALENCE() + " ?pc" + LINE_SEPERATOR;
        for (String p : s) {
            String node = " ?" + generator.getVariableName("node");

            query += "?pc " + CONST_HAS_ATTRIBUTE() + node;
            query += node + " " + GET_PROPERTY_LINK() + " <" + p + "> " + LINE_SEPERATOR;


        }
        return query;
    }
    // AAAA CSE_CONSTRAINT

    /**
     * New function replacing the other ones
     * @param cseNode
     * @param OC Valid OC without "<" and ">" (OC, UnresolvedLiteralCluster, EmptyLiteralCluster)
     * @return
     */
    public String ADD_CSE_QUERY_CONSTRAINT(String cseNode, String OC) {
        String cseNodeReal = cseNode.contains("http") ? "<" + cseNode + "> " : "" + cseNode + " ";

        String query = "" + cseNodeReal + " <" + CONST_HAS_SUBJECT_EQUIVALENCE() + "> <" + OC + ">" + LINE_SEPERATOR;
        return query;
    }

    @Override
    public String ADD_CSE_QUERY_CONSTRAINT_OC(String cseNode, String hashValue) {
        return ADD_CSE_QUERY_CONSTRAINT(cseNode,CREATE_OC_VIA_HASH(hashValue) );
    }

    @Override
    public String ADD_CSE_QUERY_CONSTRAINT_OC_UNRESOLVED_LITERAL_CLUSTER(String cseNode) {
        return ADD_CSE_QUERY_CONSTRAINT(cseNode,CONST_UNRESOLVED_LITERAL_CLUSTER() );
    }

    @Override
    public String ADD_CSE_QUERY_CONSTRAINT_OC_EMPTY_LITERAL_CLUSTER(String cseNode) {
        return ADD_CSE_QUERY_CONSTRAINT(cseNode,CONST_EMPTY_LITERAL_CLUSTER() );
    }
    // AAAA

    @Override
    public String ADD_CSE_QUERY_CONSTRAINT_POC(String cseNode, Set<Map<String, String>> s) {

        String query = "";
        String poc = "?" + generator.getVariableName("poc");

        String cseNodeReal = cseNode.contains("http") ? "<" + cseNode + "> " : "" + cseNode + " ";


        for (Map<String, String> map : s) {
            String property = map.keySet().stream().findFirst().get();
            String objectCluster = CREATE_OC_VIA_HASH(map.values().stream().findFirst().get());
            String node = "?" + generator.getVariableName("node");
            query += "" + cseNodeReal + " <" + CONST_HAS_SUBJECT_EQUIVALENCE() + "> " + poc + LINE_SEPERATOR;
            query += poc + " <" + CONST_HAS_ATTRIBUTE() + "> " + node + LINE_SEPERATOR;
            query += node + " <" + CONST_GET_LINK_OBJECT() + "> <" + objectCluster + ">" + LINE_SEPERATOR;
            query += node + " <" + GET_PROPERTY_LINK() + "> <" + property + ">" + LINE_SEPERATOR;
        }
        return query;
    }

    @Override
    public String ADD_CSE_QUERY_CONSTRAINT_NO_POC(String cseNode) {
        return ADD_CSE_QUERY_CONSTRAINT(cseNode,CONST_NO_PROPERTY_OBJECT_CLUSTER() );

    }




    @Override
    public String ADD_CSE_QUERY_CONSTRAINT_CSE(String cseNode, String s) {
        String query = "";
        if (s.contains("http"))          // CANNOT BE SIMPLIFIED
            query += "<" + cseNode + "> " + CONST_HAS_SUBJECT_EQUIVALENCE() + " <" + s + ">";
        else
            query += "<" + cseNode + "> " + CONST_HAS_SUBJECT_EQUIVALENCE() + " " + s + "";

        return query;
    }

    @Override
    public String ADD_CSE_QUERY_CONSTRAINT_DS(String cseNode) {
        String pay = "?" + generator.getVariableName("pay");
        String cseNodeReal = cseNode.contains("http") ? "<" + cseNode + "> " : "" + cseNode + " ";
        String query = cseNodeReal + " <" +CONST_HAS_PAYLOAD_ELEMENT() + "> " + pay + LINE_SEPERATOR +
                "?pay <" + CONST_PAYLOAD_INFORMATION() + "> ?ds.";

        return query;
    }


    @Override
    public String RETRIEVE_ALL_PRIME_COMPLEX_SCHEMA_ELEMENTS() {
        String query = "?x <" + CONST_RDF_TYPE() + "> <" + CONST_COMPLEX_SCHEMA_ELEMENT() + ">"+LINE_SEPERATOR;
        query += "FILTER (NOT EXISTS {?k " + "<"+CONST_HAS_SUBJECT_EQUIVALENCE()+">" + "?x})";
        return query;
    }


    private String GET_CONSTRAINTS_FROM_CSE (String cseNode, String type, String variableBase) {
        String variable = "?" + generator.getVariableName(variableBase);
        String query = "<" + cseNode + "> <" + CONST_HAS_SUBJECT_EQUIVALENCE() + "> " + variable + LINE_SEPERATOR;
        query += variable + " " + CONST_RDF_TYPE() + " <" + type+">";
        return query;
    }
    @Override
    public String GET_CONSTRAINTS_PC_FROM_CSE(String cseNode) {
        return GET_CONSTRAINTS_FROM_CSE(cseNode,CONST_PROPERTY_CLUSTER(),"pc");
    }

    @Override
    public String GET_CONSTRAINTS_OC_FROM_CSE(String cseNode) {
        return GET_CONSTRAINTS_FROM_CSE(cseNode,CONST_OBJECT_CLUSTER(),"oc");

    }

    @Override
    public String GET_CONSTRAINTS_POC_FROM_CSE(String cseNode) {
        return GET_CONSTRAINTS_FROM_CSE(cseNode,CONST_PROPERTY_OBJECT_CLUSTER(),"poc");

    }

    @Override
    public String GET_CONSTRAINTS_CSE_FROM_CSE(String cseNode) {
        return GET_CONSTRAINTS_FROM_CSE(cseNode,CONST_COMPLEX_SCHEMA_ELEMENT(),"cse");

    }


    @Override
    public String EXTRACT_HASH_VALUE_FROM_CSE(String oc) {
        String result = oc.replace(CLASS_COMPLEX_SCHEMA_ELEMENT, "");
        return result;
    }

    @Override
    public String CREATE_CSE_VIA_HASH(String s) {
        String result = CLASS_COMPLEX_SCHEMA_ELEMENT + s;
        return result;
    }

    @Override
    public String GET_DS_VIA_CSE(String cse) {
        String query = cse+" <" + CONST_HAS_PAYLOAD_ELEMENT() + "> ?pay" + LINE_SEPERATOR +
                "?pay <" + CONST_PAYLOAD_INFORMATION() + "> ?ds.";
        return query;
    }


    //PC --------------------------------------------------------------


    @Override
    public String RETRIEVE_ALL_GET_PROPERTY_CLUSTER() {
        return "?x" + " <" + CONST_RDF_TYPE() + ">  <" + CONST_PROPERTY_CLUSTER() +">";
    }
    @Override
    public String GET_CONSTRAINTS_PROPERTIES_FROM_PC(String pc) {
        String query = "<" + pc + "> ?p " + " []";
        return query;
    }


    @Override
    public String GET_DS_VIA_PC(String pc) {
        return GET_DS_FROM(pc);
    }

    @Override
    public String EXTRACT_HASH_VALUE_FROM_PC(String pc) {
        String result = pc.replace(CONST_PROPERTY_CLUSTER(), "");
        return result;
    }

    @Override
    public String CREATE_PC_VIA_HASH(String s) {
        String result = CLASS_PROPERTY_CLUSTER + s;
        return result;
    }

    @Override
    public String GET_DS_FROM_EMPTY_PROPERTIES_CLUSTER() {
        return GET_DS_FROM(CONST_EMPTY_PROPERTIES_CLUSTER());
    }



    @Override
    public String GET_DS_FROM_PROPERTIES(Set<String> properties) {


        String resultQuery = "";
        for (String s : properties) {
            String var  = "?"+VariablesGenerator.getInstance().getVariableName("node");
            resultQuery += var + " <" + GET_PROPERTY_LINK() + "> <" + s + ">" +LINE_SEPERATOR;
            resultQuery += "?poc <" + CONST_HAS_ATTRIBUTE() + "> "+var + "" +LINE_SEPERATOR;

        }
        resultQuery +=
                "?cse <" + CONST_HAS_SUBJECT_EQUIVALENCE() + "> ?poc" + LINE_SEPERATOR+
                        "?cse <" + CONST_HAS_PAYLOAD_ELEMENT() + "> ?pay" + LINE_SEPERATOR+
                        "?pay <" + CONST_PAYLOAD_INFORMATION() + "> ?ds.";


        return resultQuery;
    }

    @Override
    public boolean IS_FILTER_PROPERTY(String x) {
        if (Arrays.stream(filterPropertyList).parallel().anyMatch(x::contains))
            return true;

        return false;
    }


    //POC --------------------------------------------------------------
    @Override
    public String GET_DS_FROM_NO_PROPERTY_OBJECT_CLUSTER() {
        return GET_DS_FROM(CONST_NO_PROPERTY_OBJECT_CLUSTER());
    }

    @Override
    public String GET_DS_FROM_PROPERTY_OBJECT_CLUSTER(String poc) {
        return GET_DS_FROM(poc);
    }

    @Override
    public String EXTRACT_HASH_VALUE_FROM_POC(String poc) {
        String result  = poc.replace(CONST_PROPERTY_OBJECT_CLUSTER(), "");
        return result;
    }

    @Override
    public String CREATE_POC_VIA_HASH(String s) {
        String result = CLASS_PROPERTY_OBJECT_CLUSTER+ s;
        return result;
    }


    @Override
    public String RETRIEVE_ALL_PROPERTY_OBJECT_CLUSTER() {

        String query = "?x" + " <" + CONST_RDF_TYPE() + "> <" + CONST_PROPERTY_OBJECT_CLUSTER()+">" + LINE_SEPERATOR;

        //add Filter param:
        return query;
    }






    @Override
    public String GET_CONSTRAINTS_PROPERTY_OBJECT_PAIRS_FROM_POC(String poc) {
        String query = "<" + poc + "><" + CONST_HAS_ATTRIBUTE() + ">?node." + LINE_SEPERATOR+
                "?node <" + GET_PROPERTY_LINK() + "> ?prop." + LINE_SEPERATOR +
                "?node <" + CONST_GET_LINK_OBJECT() + "> ?obj.";
        return query;
    }

    @Override
    public String GET_DS_FROM_PROPERTY_OBJECT_PAIRS(Set<Map<String, String>> propertyObjectPairs) {
        VariablesGenerator gen = VariablesGenerator.getInstance();
        String poc = gen.getVariableName("poc");
        String cse = gen.getVariableName("cse");
        String pay = gen.getVariableName("pay");
        String ds = gen.getVariableName("ds");
        String query = "";


        for (Map<String, String> m : propertyObjectPairs) {
            String var = gen.getVariableName("node");
            query += poc + " <" + CONST_HAS_ATTRIBUTE() + "> " + var + "" + LINE_SEPERATOR;
            Collection<String> object = m.values();
            Collection<String> property = m.keySet();
            //ADD OC-URI
            if (object.size() > 0) {
                String uri = CREATE_OC_VIA_HASH(object.stream().findFirst().get());
                query += var + " <" + CONST_GET_LINK_OBJECT() + "> " + "<" + uri + ">" + LINE_SEPERATOR;
            }  //currently these pairs should only have a 1:1 connection
            //ADD property
            if (property.size() > 0)
                query += var + " <" + GET_PROPERTY_LINK() + "> " + "<" + property.stream().findFirst().get() + ">" + LINE_SEPERATOR; //currently these pairs should only have a 1:1 connection

        }
        query += cse + " <" + CONST_HAS_SUBJECT_EQUIVALENCE() + "> " + poc + LINE_SEPERATOR;
        query += cse + " <" + CONST_HAS_PAYLOAD_ELEMENT() + "> " + pay + LINE_SEPERATOR;
        query += pay + " <" + CONST_PAYLOAD_INFORMATION() + "> " + ds;
        return query;
    }

    //OC --------------------------------------------------------------


    @Override
    public String RETRIEVE_ALL_OBJECT_CLUSTER() {
        return "?x" + " <" + CONST_RDF_TYPE() + "> <" + CONST_OBJECT_CLUSTER() +">";
    }
    @Override
    public String EXTRACT_HASH_VALUE_FROM_OC(String oc) {
        String result  = oc.replace(CLASS_OBJECT_CLUSTER, "");
        return result;
    }

    @Override
    public String CREATE_OC_VIA_HASH(String s) {
        String result = CLASS_OBJECT_CLUSTER + s;
        return result;
    }


    @Override
    public String GET_DS_FROM_UNRESOLVED_LITERAL_CLUSTER() {
        return GET_DS_FROM(CONST_UNRESOLVED_LITERAL_CLUSTER());
    }


    @Override
    public String GET_DS_FROM_EMPTY_LITERAL_CLUSTER() {
        return GET_DS_FROM(CONST_EMPTY_LITERAL_CLUSTER());
    }

    @Override
    public String GET_CONSTRAINTS_OC_TYPES_FROM_OC(String tc) {
        String query = "<" + tc + "> <" + CONST_HAS_ATTRIBUTE() + ">?node" + LINE_SEPERATOR;
        query += "?node <" + CONST_GET_LINK_OBJECT() + "> " + "?type";
        return query;
    }

    @Override
    public String GET_DS_FROM_TYPES(Set<String> types) {
        VariablesGenerator gen = VariablesGenerator.getInstance();

        String typeQueryEval = "{\n";

        for (String type : types) {
            String var = gen.getVariableName("type");
            typeQueryEval += "?tc <" + CONST_HAS_ATTRIBUTE() + "> ?" + var + " "+LINE_SEPERATOR;
            typeQueryEval += "?" + var + " <" + CONST_GET_LINK_OBJECT() + "> <" + type + ">" +LINE_SEPERATOR;

        }
        typeQueryEval += "?eqc <" + CONST_HAS_SUBJECT_EQUIVALENCE() + "> ?tc "+LINE_SEPERATOR;

        //get connected payload element
        typeQueryEval += "?eqc <" + CONST_HAS_PAYLOAD_ELEMENT() + "> ?pe "+LINE_SEPERATOR;

        //get connected data sources
        typeQueryEval += "?pe <" + CONST_PAYLOAD_INFORMATION() + "> ?ds "+LINE_SEPERATOR;

        typeQueryEval += "}\n";

        return typeQueryEval;

    }

    @Override
    public String GET_DS_VIA_OC(String tc) {
        return GET_DS_FROM(tc);
    }



}
