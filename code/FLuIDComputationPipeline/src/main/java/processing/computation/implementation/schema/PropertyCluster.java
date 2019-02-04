package processing.computation.implementation.schema;


import org.cyberborean.rdfbeans.annotations.RDF;
import org.cyberborean.rdfbeans.annotations.RDFBean;
import org.cyberborean.rdfbeans.annotations.RDFSubject;
import utils.implementation.FLuIDVocabulary;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;


@RDFBean(FLuIDVocabulary.CLASS_PROPERTY_CLUSTER)
public class PropertyCluster extends SimpleSchemaElement {

    public static PropertyCluster PC_EMPTY;
    public static PropertyCluster PC_UNRESOLVED;
    static {
        try {
            PC_EMPTY = new PropertyCluster();
            PC_EMPTY.addAttribute(new Link(new URI(FLuIDVocabulary.EMPTY_PROPERTIES), null));

            PC_UNRESOLVED = new PropertyCluster();
            PC_UNRESOLVED.addAttribute(new Link(new URI(FLuIDVocabulary.UNRESOLVED_PROPERTY_CLUSTER), null));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }


    public PropertyCluster() {
        super();
     }

    @RDFSubject
    public String getURI() {
        if(super.getAttributes().size() == 1){
            Link tmp = super.getAttributes().iterator().next();
            if (tmp.getProperty().toString().equals(FLuIDVocabulary.EMPTY_PROPERTIES))
                return FLuIDVocabulary.EMPTY_PROPERTIES;
        }

        return FLuIDVocabulary.createSubjectURIPrefix(this.getClass()) + getLocator().toString();
    }

    @RDF(FLuIDVocabulary.HAS_ATTRIBUTE)
    public HashSet<Link> getAttributes() {
        HashSet<Link> attributes = (HashSet<Link>) super.getAttributes().clone();
        attributes.removeIf(A -> A.getProperty().toString().equals(FLuIDVocabulary.EMPTY_PROPERTIES) ||
                A.getProperty().toString().equals(FLuIDVocabulary.UNRESOLVED_PROPERTY_CLUSTER));
        return attributes;
    }
}
