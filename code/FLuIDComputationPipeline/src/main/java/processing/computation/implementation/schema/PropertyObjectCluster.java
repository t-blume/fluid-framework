package processing.computation.implementation.schema;


import org.cyberborean.rdfbeans.annotations.RDF;
import org.cyberborean.rdfbeans.annotations.RDFBean;
import org.cyberborean.rdfbeans.annotations.RDFSubject;
import utils.implementation.FLuIDVocabulary;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;

@RDFBean(FLuIDVocabulary.CLASS_PROPERTY_OBJECT_CLUSTER)
public class PropertyObjectCluster extends SimpleSchemaElement {

    public static PropertyObjectCluster POC_EMPTY;
    public static PropertyObjectCluster POC_UNRESOLVED;

    static {
        try {
            POC_EMPTY = new PropertyObjectCluster();
            POC_EMPTY.addAttribute(new Link(new URI(FLuIDVocabulary.EMPTY_PROPERTIES), new URI(FLuIDVocabulary.EMPTY_OBJECTS)));

            POC_UNRESOLVED = new PropertyObjectCluster();
            POC_UNRESOLVED.addAttribute(new Link(new URI(FLuIDVocabulary.UNRESOLVED_PROPERTY_CLUSTER), new URI(FLuIDVocabulary.UNRESOLVED_OBJECT_CLUSTER)));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }


    /**
     *
     */
    public PropertyObjectCluster() {
        super();
    }

    @RDFSubject
    public String getURI() {
        if(super.getAttributes().size() == 1){
            Link tmp = super.getAttributes().iterator().next();
            if (tmp.getProperty().toString().equals(FLuIDVocabulary.EMPTY_PROPERTIES) &&
                    tmp.getObject().toString().equals(FLuIDVocabulary.EMPTY_OBJECTS))
                return FLuIDVocabulary.EMPTY_PROPERTY_OBJECTS;
        }

        return FLuIDVocabulary.createSubjectURIPrefix(this.getClass()) + getLocator().toString();
    }

    @RDF(FLuIDVocabulary.HAS_ATTRIBUTE)
    public HashSet<Link> getAttributes() {
        HashSet<Link> attributes = (HashSet<Link>) super.getAttributes().clone();
        attributes.removeIf(A -> (A.getProperty().toString().equals(FLuIDVocabulary.EMPTY_PROPERTIES) &&
                A.getObject().toString().equals(FLuIDVocabulary.EMPTY_OBJECTS)) ||
                (A.getProperty().toString().equals(FLuIDVocabulary.UNRESOLVED_PROPERTY_CLUSTER) &&
                        A.getObject().toString().equals(FLuIDVocabulary.UNRESOLVED_OBJECT_CLUSTER))
        );
        return attributes;
    }
}
