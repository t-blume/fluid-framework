package processing.computation.implementation.schema;


import org.cyberborean.rdfbeans.annotations.RDF;
import org.cyberborean.rdfbeans.annotations.RDFBean;
import org.cyberborean.rdfbeans.annotations.RDFSubject;
import org.semanticweb.yars.nx.Resource;
import org.semanticweb.yars.nx.namespace.XSD;
import utils.implementation.FLuIDVocabulary;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;


@RDFBean(FLuIDVocabulary.CLASS_OBJECT_CLUSTER)
public class ObjectCluster extends SimpleSchemaElement {

    public ObjectCluster() {
        super();
    }


    @RDFSubject
    public String getURI() {
        if(super.getAttributes().size() == 1){
            Link tmp = super.getAttributes().iterator().next();
            if (tmp.getObject().toString().equals(FLuIDVocabulary.EMPTY_OBJECTS))
                return FLuIDVocabulary.EMPTY_OBJECTS;
            if (tmp.getObject().toString().equals(FLuIDVocabulary.UNRESOLVED_LITERAL_CLUSTER))
                return FLuIDVocabulary.UNRESOLVED_LITERAL_CLUSTER;
        }

        return FLuIDVocabulary.createSubjectURIPrefix(this.getClass()) + getLocator().toString();
    }

    @RDF(FLuIDVocabulary.HAS_ATTRIBUTE)
    public HashSet<Link> getAttributes() {
        HashSet<Link> attributes = (HashSet<Link>) super.getAttributes().clone();
        attributes.removeIf(A -> A.getObject().toString().equals(FLuIDVocabulary.EMPTY_OBJECTS) ||
                A.getObject().toString().equals(FLuIDVocabulary.UNRESOLVED_LITERAL_CLUSTER) ||
                A.getObject().toString().equals(FLuIDVocabulary.UNRESOLVED_OBJECT_CLUSTER)
        );
        return attributes;
    }


    /**
     * ObjectCluster for unresolved literals:
     */
    public static ObjectCluster OC_UNRESOLVED_LITERAL;

    public static ObjectCluster OC_UNRESOLVED;
    /**
     * Empty object cluster
     */
    public static ObjectCluster OC_EMPTY;

    // Numerical Types
    /**
     * Typecluster for integers
     */
    public static ObjectCluster OC_INTEGER;
    /**
     * Typecluster for decimals
     */
    public static ObjectCluster OC_DECIMAL;
    /**
     * Typecluster for floats
     */
    public static ObjectCluster OC_FLOAT;
    /**
     * Typecluster for double floating points
     */
    public static ObjectCluster OC_DOUBLE;

    /**
     * Typecluster for bytes
     */
    public static ObjectCluster OC_BYTE;

    /**
     * Typecluster for long integers
     */
    public static ObjectCluster OC_LONG;

    /**
     * Typecluster for short integers
     */
    public static ObjectCluster OC_SHORT;

    /**
     * Typecluster for booleans
     */
    public static ObjectCluster OC_BOOLEAN;

    /**
     * Typecluster for unsigned bytes
     */
    public static ObjectCluster OC_UNSIGNEDBYTE;
    /**
     * Typecluster for unsigned integers
     */
    public static ObjectCluster OC_UNSIGNEDINT;
    /**
     * Typecluster for unsigned short integers
     */
    public static ObjectCluster OC_UNSIGNEDSHORT;
    /**
     * Typecluster for unsigned long integers
     */
    public static ObjectCluster OC_UNSIGNEDLONG;

    /**
     * Typecluster for positive integers
     */
    public static ObjectCluster OC_POSITIVEINTEGER;
    /**
     * Typecluster for negative integers
     */
    public static ObjectCluster OC_NEGATIVEINTEGER;

    /**
     * Typecluster for non-positive integers
     */
    public static ObjectCluster OC_NONPOSITIVEINTEGER;

    /**
     * Typecluster for non-negative integers
     */
    public static ObjectCluster OC_NONNEGATIVEINTEGER;

    // Other

    /**
     * Typecluster for strings
     */
    public static ObjectCluster OC_STRING;
    /**
     * Typecluster for datetimes
     */
    public static ObjectCluster OC_DATETIME;

    /**
     * Typecluster for dates
     */
    public static ObjectCluster OC_DATE;

    /**
     * Typecluster for times
     */
    public static ObjectCluster OC_TIME;

    // Mapping

    /**
     * Mapping from {@link Resource}s describing supported XSD datatypes to
     * matching typeclusters
     */
    public static Map<Resource, ObjectCluster> DATATYPE_MAP;

    static {
        try {
            OC_UNRESOLVED_LITERAL = new ObjectCluster();
            OC_UNRESOLVED_LITERAL.addAttribute(new Link(null, new URI(FLuIDVocabulary.UNRESOLVED_LITERAL_CLUSTER)));

            //TODO: new vocabulary constant
            OC_UNRESOLVED = new ObjectCluster();
            OC_UNRESOLVED.addAttribute(new Link(null, new URI(FLuIDVocabulary.UNRESOLVED_LITERAL_CLUSTER)));

            OC_EMPTY = new ObjectCluster();
            OC_EMPTY.addAttribute(new Link(null, new URI(FLuIDVocabulary.EMPTY_OBJECTS)));

            // Numerical Types
            OC_INTEGER = new ObjectCluster();
            OC_INTEGER.addAttribute(new Link(null, new URI(XSD.INTEGER.getLabel())));

            OC_DECIMAL = new ObjectCluster();
            OC_DECIMAL.addAttribute(new Link(null, new URI(XSD.DECIMAL.getLabel())));

            OC_FLOAT = new ObjectCluster();
            OC_FLOAT.addAttribute(new Link(null, new URI(XSD.FLOAT.getLabel())));

            OC_DOUBLE = new ObjectCluster();
            OC_DOUBLE.addAttribute(new Link(null, new URI(XSD.DOUBLE.getLabel())));

            OC_BYTE = new ObjectCluster();
            OC_BYTE.addAttribute(new Link(null, new URI(XSD.BYTE.getLabel())));

            OC_LONG = new ObjectCluster();
            OC_LONG.addAttribute(new Link(null, new URI(XSD.LONG.getLabel())));

            OC_SHORT = new ObjectCluster();
            OC_SHORT.addAttribute(new Link(null, new URI(XSD.SHORT.getLabel())));

            OC_BOOLEAN = new ObjectCluster();
            OC_BOOLEAN.addAttribute(new Link(null, new URI(XSD.BOOLEAN.getLabel())));

            OC_UNSIGNEDBYTE = new ObjectCluster();
            OC_UNSIGNEDBYTE.addAttribute(new Link(null, new URI(XSD.UNSIGNEDBYTE.getLabel())));

            OC_UNSIGNEDINT = new ObjectCluster();
            OC_UNSIGNEDINT.addAttribute(new Link(null, new URI(XSD.UNSIGNEDINT.getLabel())));

            OC_UNSIGNEDSHORT = new ObjectCluster();
            OC_UNSIGNEDSHORT.addAttribute(new Link(null, new URI(XSD.UNSIGNEDSHORT.getLabel())));

            OC_UNSIGNEDLONG = new ObjectCluster();
            OC_UNSIGNEDLONG.addAttribute(new Link(null, new URI(XSD.UNSIGNEDLONG.getLabel())));

            OC_POSITIVEINTEGER = new ObjectCluster();
            OC_POSITIVEINTEGER.addAttribute(new Link(null, new URI(XSD.POSITIVEINTEGER.getLabel())));

            OC_NEGATIVEINTEGER = new ObjectCluster();
            OC_NEGATIVEINTEGER.addAttribute(new Link(null, new URI(XSD.NEGATIVEINTEGER.getLabel())));

            OC_NONPOSITIVEINTEGER = new ObjectCluster();
            OC_NONPOSITIVEINTEGER.addAttribute(new Link(null, new URI(XSD.NONPOSITIVEINTEGER.getLabel())));

            OC_NONNEGATIVEINTEGER = new ObjectCluster();
            OC_NONNEGATIVEINTEGER.addAttribute(new Link(null, new URI(XSD.NONNEGATIVEINTEGER.getLabel())));

            OC_STRING = new ObjectCluster();
            OC_STRING.addAttribute(new Link(null, new URI(XSD.STRING.getLabel())));

            OC_DATETIME = new ObjectCluster();
            OC_DATETIME.addAttribute(new Link(null, new URI(XSD.DATETIME.getLabel())));

            OC_DATE = new ObjectCluster();
            OC_DATE.addAttribute(new Link(null, new URI(XSD.DATE.getLabel())));

            OC_TIME = new ObjectCluster();
            OC_TIME.addAttribute(new Link(null, new URI(XSD.TIME.getLabel())));

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        // Set up map
        DATATYPE_MAP = new HashMap<>();
        DATATYPE_MAP.put(XSD.INTEGER, OC_INTEGER);
        DATATYPE_MAP.put(XSD.DECIMAL, OC_DECIMAL);
        DATATYPE_MAP.put(XSD.FLOAT, OC_FLOAT);
        DATATYPE_MAP.put(XSD.DOUBLE, OC_DOUBLE);
        DATATYPE_MAP.put(XSD.BYTE, OC_BYTE);
        DATATYPE_MAP.put(XSD.LONG, OC_LONG);
        DATATYPE_MAP.put(XSD.SHORT, OC_SHORT);
        DATATYPE_MAP.put(XSD.BOOLEAN, OC_BOOLEAN);
        DATATYPE_MAP.put(XSD.UNSIGNEDBYTE, OC_UNSIGNEDBYTE);
        DATATYPE_MAP.put(XSD.UNSIGNEDINT, OC_UNSIGNEDINT);
        DATATYPE_MAP.put(XSD.UNSIGNEDSHORT, OC_UNSIGNEDSHORT);
        DATATYPE_MAP.put(XSD.UNSIGNEDLONG, OC_UNSIGNEDLONG);
        DATATYPE_MAP.put(XSD.POSITIVEINTEGER, OC_POSITIVEINTEGER);
        DATATYPE_MAP.put(XSD.NEGATIVEINTEGER, OC_NEGATIVEINTEGER);
        DATATYPE_MAP.put(XSD.NONPOSITIVEINTEGER, OC_NONPOSITIVEINTEGER);
        DATATYPE_MAP.put(XSD.NONNEGATIVEINTEGER, OC_NONNEGATIVEINTEGER);

        DATATYPE_MAP.put(XSD.STRING, OC_STRING);
        DATATYPE_MAP.put(XSD.DATETIME, OC_DATETIME);
        DATATYPE_MAP.put(XSD.DATE, OC_DATE);
        DATATYPE_MAP.put(XSD.TIME, OC_TIME);
    }
}
