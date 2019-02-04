package utils.implementation;


import common.implemenation.NodeResource;
import common.interfaces.IResource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.semanticweb.yars.nx.Resource;
import processing.computation.implementation.payload.CountElement;
import processing.computation.implementation.payload.DatasourceElement;
import processing.computation.implementation.payload.SnippetElement;
import processing.computation.implementation.schema.ComplexSchemaElement;
import processing.computation.implementation.schema.ObjectCluster;
import processing.computation.implementation.schema.PropertyCluster;
import processing.computation.implementation.schema.PropertyObjectCluster;

import java.util.HashMap;
import java.util.Map;

/**
 * Types and properties used!
 */
public class FLuIDVocabulary {
    private static final Logger logger = LogManager.getLogger(FLuIDVocabulary.class.getName());

    public static final String NS = "http://www.fluid.informatik.uni-kiel.de/ontologies/2018/2/";
    public static final String NS_IMPL = "http://www.fluid-framework.informatik.uni-kiel.de/";

    //schema
    public static final String CLASS_COMPLEX_SCHEMA_ELEMENT = NS + "ComplexSchemaElement";
    public static final IResource CLASS_COMPLEX_SCHEMA_ELEMENT_RESOURCE = new NodeResource(new Resource(CLASS_COMPLEX_SCHEMA_ELEMENT));
    public static final String CLASS_PROPERTY_CLUSTER = NS + "PropertyCluster";
    public static final IResource CLASS_PROPERTY_CLUSTER_RESOURCE = new NodeResource(new Resource(CLASS_PROPERTY_CLUSTER));
    public static final String CLASS_PROPERTY_OBJECT_CLUSTER = NS + "PropertyObjectCluster";
    public static final IResource CLASS_PROPERTY_OBJECT_CLUSTER_RESOURCE = new NodeResource(new Resource(CLASS_PROPERTY_OBJECT_CLUSTER));
    public static final String CLASS_OBJECT_CLUSTER = NS + "ObjectCluster";
    public static final IResource CLASS_OBJECT_CLUSTER_RESOURCE = new NodeResource(new Resource(CLASS_OBJECT_CLUSTER));

    //paylaod
    public static final String CLASS_DATASOURCE_ELEMENT = NS + "DatasourceElement";
    public static final IResource CLASS_DATASOURCE_ELEMENT_RESOURCE = new NodeResource(new Resource(CLASS_DATASOURCE_ELEMENT));
    public static final String CLASS_COUNT_ELEMENT = NS + "CountElement";
    public static final IResource CLASS_COUNT_ELEMENT_RESOURCE = new NodeResource(new Resource(CLASS_COUNT_ELEMENT));
    public static final String CLASS_SNIPPET_ELEMENT = NS + "SnippetElement";
    public static final IResource CLASS_SNIPPET_ELEMENT_RESOURCE = new NodeResource(new Resource(CLASS_SNIPPET_ELEMENT));

    public static final Map<IResource, Class> RESOURCE_CLASS_MAP = new HashMap<>();

    static {
        RESOURCE_CLASS_MAP.put(CLASS_COMPLEX_SCHEMA_ELEMENT_RESOURCE, ComplexSchemaElement.class);
        RESOURCE_CLASS_MAP.put(CLASS_PROPERTY_CLUSTER_RESOURCE, PropertyCluster.class);
        RESOURCE_CLASS_MAP.put(CLASS_PROPERTY_OBJECT_CLUSTER_RESOURCE, PropertyObjectCluster.class);
        RESOURCE_CLASS_MAP.put(CLASS_OBJECT_CLUSTER_RESOURCE, ObjectCluster.class);
        RESOURCE_CLASS_MAP.put(CLASS_DATASOURCE_ELEMENT_RESOURCE, DatasourceElement.class);
        RESOURCE_CLASS_MAP.put(CLASS_COUNT_ELEMENT_RESOURCE, CountElement.class);
        RESOURCE_CLASS_MAP.put(CLASS_SNIPPET_ELEMENT_RESOURCE, SnippetElement.class);
    }

    //special objects
    public static final String UNRESOLVED_LITERAL_CLUSTER = CLASS_OBJECT_CLUSTER + "UnresolvedLiteral";
    public static final IResource UNRESOLVED_LITERAL_CLUSTER_RESOURCE = new NodeResource(new Resource(UNRESOLVED_LITERAL_CLUSTER));
    public static final String UNRESOLVED_OBJECT_CLUSTER = CLASS_OBJECT_CLUSTER + "UnresolvedObject";
    public static final IResource UNRESOLVED_OBJECT_CLUSTER_RESOURCE = new NodeResource(new Resource(UNRESOLVED_OBJECT_CLUSTER));
    public static final String EMPTY_OBJECTS = CLASS_OBJECT_CLUSTER + "NoObjects";
    public static final IResource EMPTY_OBJECTS_RESOURCE = new NodeResource(new Resource(EMPTY_OBJECTS));
    public static final String UNRESOLVED_PROPERTY_CLUSTER = CLASS_PROPERTY_CLUSTER + "UnresolvedProperties";
    public static final IResource UNRESOLVED_PROPERTY_CLUSTER_RESOURCE = new NodeResource(new Resource(UNRESOLVED_PROPERTY_CLUSTER));
    public static final String EMPTY_PROPERTIES = CLASS_PROPERTY_CLUSTER + "NoProperties";
    public static final IResource EMPTY_PROPERTIES_RESOURCE = new NodeResource(new Resource(EMPTY_PROPERTIES));
    public static final String UNRESOLVED_PROPERTY_OBJECT_CLUSTER = CLASS_PROPERTY_OBJECT_CLUSTER + "UnresolvedProperty-Objects";
    public static final IResource UNRESOLVED_PROPERTY_OBJECT_CLUSTER_RESOURCE = new NodeResource(new Resource(UNRESOLVED_PROPERTY_OBJECT_CLUSTER));
    public static final String EMPTY_PROPERTY_OBJECTS = CLASS_PROPERTY_OBJECT_CLUSTER + "NoProperty-Objects";
    public static final IResource EMPTY_PROPERTY_OBJECTS_RESOURCE = new NodeResource(new Resource(EMPTY_PROPERTY_OBJECTS));


    //blank node for storing Links
    public static final String CLASS_LINK_ELEMENT = NS_IMPL + "Link";
    public static final IResource CLASS_LINK_ELEMENT_RESOURCE = new NodeResource(new Resource(CLASS_LINK_ELEMENT));
    public static final String GET_PROPERTY_LINK = NS + "getPropertyLink";
    public static final IResource GET_PROPERTY_LINK_RESOURCE = new NodeResource(new Resource(GET_PROPERTY_LINK));
    public static final String GET_LINK_OBJECT = NS + "getLinkObject";
    public static final IResource GET_LINK_OBJECT_RESOURCE = new NodeResource(new Resource(GET_LINK_OBJECT));

    //storing schema information
    public static final String HAS_ATTRIBUTE = NS + "hasAttribute";
    public static final IResource HAS_ATTRIBUTE_RESOURCE = new NodeResource(new Resource(HAS_ATTRIBUTE));

    //storing PAYLOAD information
    public static final String HAS_PAYLOAD = NS + "hasPayload";
    public static final IResource HAS_PAYLOAD_RESOURCE = new NodeResource(new Resource(HAS_PAYLOAD));
    public static final String PAYLOAD = NS + "payload";
    public static final IResource PAYLOAD_RESOURCE = new NodeResource(new Resource(PAYLOAD));

    //schema-level relationships
    public static final String IS_SUBJECT_EQUIVALENCE = NS + "isSubjectEquivalenceOf";
    public static final IResource IS_SUBJECT_EQUIVALENCE_RESOURCE = new NodeResource(new Resource(IS_SUBJECT_EQUIVALENCE));
    public static final String IS_PREDICATE_EQUIVALENCE = NS + "isPredicateEquivalenceOf";
    public static final IResource IS_PREDICATE_EQUIVALENCE_RESOURCE = new NodeResource(new Resource(IS_PREDICATE_EQUIVALENCE));
    public static final String IS_OBJECT_EQUIVALENCE = NS + "isObjectEquivalenceOf";
    public static final IResource IS_OBJECT_EQUIVALENCE_RESOURCE = new NodeResource(new Resource(IS_OBJECT_EQUIVALENCE));
//    public static final String HAS_SUBJECT_EQUIVALENCE = NS + "hasSubjectEquivalence";
//    public static final IResource HAS_SUBJECT_EQUIVALENCE_RESOURCE = new NodeResource(new Resource(HAS_SUBJECT_EQUIVALENCE));
//    public static final String HAS_PREDICATE_EQUIVALENCE = NS + "hasPredicateEquivalence";
//    public static final IResource HAS_PREDICATE_EQUIVALENCE_RESOURCE = new NodeResource(new Resource(HAS_PREDICATE_EQUIVALENCE));
//    public static final String HAS_OBJECT_EQUIVALENCE = NS + "hasObjectEquivalence";
//    public static final IResource HAS_OBJECT_EQUIVALENCE_RESOURCE = new NodeResource(new Resource(HAS_OBJECT_EQUIVALENCE));

    //Implementation detail for reading and writing
    public static final String ABSTRACT_SCHEMA_ELEMENT_TYPE = NS_IMPL + "abstractSchemaElementType";
    public static final IResource ABSTRACT_SCHEMA_ELEMENT_TYPE_RESOURCE = new NodeResource(new Resource(ABSTRACT_SCHEMA_ELEMENT_TYPE));

    public static String createSubjectURIPayloadPrefix(Class payloadElementClass) {
        if (payloadElementClass.equals(DatasourceElement.class))
            return CLASS_DATASOURCE_ELEMENT;
        else {
            logger.warn("Invalid Payload Element: " + payloadElementClass);
            return null;
        }
    }

    public static String createSubjectURIPrefix(Class schemaElementClass) {
        if (schemaElementClass.equals(ComplexSchemaElement.class))
            return CLASS_COMPLEX_SCHEMA_ELEMENT;
        else if (schemaElementClass.equals(PropertyCluster.class))
            return CLASS_PROPERTY_CLUSTER;
        else if (schemaElementClass.equals(ObjectCluster.class))
            return CLASS_OBJECT_CLUSTER;
        else if (schemaElementClass.equals(PropertyObjectCluster.class))
            return CLASS_PROPERTY_OBJECT_CLUSTER;
        else {
            logger.warn("Invalid Schema Element: " + schemaElementClass);
            return null;
        }
    }

}
