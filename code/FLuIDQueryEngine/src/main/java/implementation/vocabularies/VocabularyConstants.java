package implementation.vocabularies;


import implementation.eval.EvalUnit;

import java.util.Map;
import java.util.Set;

public interface VocabularyConstants {




    //CONSTANTS
    EvalUnit.VocabularyTypes GET_VOCABULARY_TYPE();

    String CONST_NO_PROPERTY_OBJECT_CLUSTER();


    /**
     * A constant to give access to the Manchester OWL api representation of the class COMPLEXSCHEMAELEMENT.<p>
     */
    String CONST_COMPLEX_SCHEMA_ELEMENT();

    /**
     * A constant to give access to the Manchester OWL api representation of the class OBJECTCLUSTER.<p>
     */
    String CONST_OBJECT_CLUSTER();

    /**
     * A constant to give access to the Manchester OWL api representation of the class PROPERTYOBJECTCLUSTER.<p>
     */
    String CONST_PROPERTY_OBJECT_CLUSTER();

    String CONST_PROPERTY_CLUSTER();



    /**
     * A constant to give access to the Manchester OWL API representation of the object property HASPAYLOADELEMENT.<p>
     */
    String CONST_HAS_PAYLOAD_ELEMENT();



    /**
     * A constant to give access to the Manchester OWL API representation of the object property HASSUBJECTEQUIVALENCE.<p>
     */
    String CONST_HAS_SUBJECT_EQUIVALENCE();




    /**
     * A constant to give access to the Manchester OWL API representation of the object property ISSUBJECTEQUIVALENCEOF.<p>
     */
    String CONST_IS_SUBJECT_EQUIVALENCE_OF();

    /**
     * A constant to give access to the Manchester OWL API representation of the data property PAYLOAD.<p>
     */
    String CONST_PAYLOAD_INFORMATION();


    //TODO Comment all this functions
    String CONST_HAS_ATTRIBUTE();

    String CONST_UNRESOLVED_LITERAL_CLUSTER();

    String CONST_EMPTY_LITERAL_CLUSTER();

    String CONST_EMPTY_PROPERTIES_CLUSTER();

    String CONST_RDF_TYPE();

    String CONST_GET_LINK_OBJECT();



    // GET QUERIES VIA CONSTRAINT ---------------------------------------------------
    String GET_CONSTRAINTS_OC_TYPES_FROM_OC(String tc);
    /**
     * Creates a query which returns the attached properties of a Property Cluster.
     * @param pc URI of property Cluster.
     * @return SPARQL-Query
     */
    String GET_CONSTRAINTS_PROPERTIES_FROM_PC(String pc);


    /**
     *  Creates a query which will return all the Property - Object Cluster Pairs of property object cluster.
     * @param poc URI of Property Object Cluster
     * @return SPARQL-Query
     */
    String GET_CONSTRAINTS_PROPERTY_OBJECT_PAIRS_FROM_POC(String poc);


    String GET_CONSTRAINTS_PC_FROM_CSE(String cseNode);

    String GET_CONSTRAINTS_OC_FROM_CSE(String cseNode);

    String GET_CONSTRAINTS_POC_FROM_CSE(String cseNode);

    String GET_CONSTRAINTS_CSE_FROM_CSE(String cseNode);

    // DATASOURCE QUERY ---------------------------------------------------------------
    String GET_DS_FROM_TYPES(Set<String> types);
    /**
     * Creates a query which will return all the datasources associated with an object cluster.
     * @param tc Object Cluster
     * @return Query
     */
    String GET_DS_VIA_OC(String tc);


    /**
     * Returns a query which gets the datasources of a property cluster with the specified properties.
     * @param properties List of properties for the property cluster.
     * @return Query which returns data sources.
     */
    String GET_DS_FROM_PROPERTIES(Set<String> properties);

    /**
     * Returns a query which will get the attached property OC pairs.
     * @param objectPropertyPairs Map<X,Y> X: URI of property; Y: HASH Value of OC (can contain multiple types)
     * @return Query which returns the attached datasources
     */
    String GET_DS_FROM_PROPERTY_OBJECT_PAIRS(Set<Map<String, String>> objectPropertyPairs);


    /**
     * Creates a query which will get all the datasources associated with the Unresolved Literal Cluster
     * @return SPARQL-Query
     */
    String GET_DS_FROM_UNRESOLVED_LITERAL_CLUSTER();

    /**
     * Creates a query which will get all the datasources associated with the Empty Literal Cluster.
     * @return SPARQL-Query
     */
    String GET_DS_FROM_EMPTY_LITERAL_CLUSTER();

    /**
     * Creates a query which gets all the datasources associated with a no Property Object Cluster.
     * @return SPARQL-Query
     */
    String GET_DS_FROM_NO_PROPERTY_OBJECT_CLUSTER();


    String GET_DS_FROM_PROPERTY_OBJECT_CLUSTER(String poc);


    String GET_DS_VIA_PC(String pc);


    String GET_DS_FROM_EMPTY_PROPERTIES_CLUSTER();

    String GET_DS_VIA_CSE(String cse);




    //HASH VALUE TRANSLATION

    /**
     * Gets the hash value from the Object Cluster.
     * @param oc Object Cluster URI
     * @return Hash Value
     */
    String EXTRACT_HASH_VALUE_FROM_OC(String oc);

    /**
     * Creates an Object Cluster URI with the specified HASH Value.
     * @param s Hash value
     * @return Object Cluster URI.
     */
    String CREATE_OC_VIA_HASH(String s);


    String EXTRACT_HASH_VALUE_FROM_CSE(String cse);

    String CREATE_CSE_VIA_HASH(String o);


    String EXTRACT_HASH_VALUE_FROM_POC(String poc);

    String CREATE_POC_VIA_HASH(String s);


    String EXTRACT_HASH_VALUE_FROM_PC(String pc);

    String CREATE_PC_VIA_HASH(String s);


    //Miscellaneous

    /**
     * This checks wether a property should be filtered out, depending on the vocabularies specification.
     * @param x Property
     * @return true --> should be filtered; false --> should not be filtered.
     */
    boolean IS_FILTER_PROPERTY(String x);









    // SET OF OBJECTS OF A TYPE
    /**
     * Creates a query which will return all the Object Cluster that exist.
     * @return SPARQL-Query
     */
    String RETRIEVE_ALL_PROPERTY_OBJECT_CLUSTER();

    String RETRIEVE_ALL_GET_PROPERTY_CLUSTER();


    String RETRIEVE_ALL_OBJECT_CLUSTER();

    /**
     * This function returns a query body which will give the cse, which have no cse above them.
     * This is considered a prime cse.
     * @return SPARQL-Query
     */
    String RETRIEVE_ALL_PRIME_COMPLEX_SCHEMA_ELEMENTS();


    String RETRIEVE_ALL_COMPLEX_SCHEMA_ELEMENTS();



    //CSE_CONSTRAINTS

    String ADD_CSE_QUERY_CONSTRAINT_PC(String cseNode, Set<String> s);

    String ADD_CSE_QUERY_CONSTRAINT_OC(String cseNode, String s);

    String ADD_CSE_QUERY_CONSTRAINT_POC(String cseNode, Set<Map<String, String>> s);

    String ADD_CSE_QUERY_CONSTRAINT_DS(String cseNode);

    String ADD_CSE_QUERY_CONSTRAINT_CSE(String cseNode, String s);

    String ADD_CSE_QUERY_CONSTRAINT_OC_UNRESOLVED_LITERAL_CLUSTER(String cseNode);

    String ADD_CSE_QUERY_CONSTRAINT_OC_EMPTY_LITERAL_CLUSTER(String cseNode);

    String ADD_CSE_QUERY_CONSTRAINT_NO_POC(String cseNodeEval);









}
