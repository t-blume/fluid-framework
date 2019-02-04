package processing.computation.implementation.schema;

import org.cyberborean.rdfbeans.annotations.RDF;
import org.cyberborean.rdfbeans.annotations.RDFBean;
import utils.implementation.FLuIDVocabulary;

import java.net.URI;


/**
 * Anonymous blank node for storing schema information:
 *
 * ObjectCluster: Only object URI.
 * PropertyCLuster: Only property URI.
 * PropertyObjectCluster: Property and object URI.
 */
@RDFBean(FLuIDVocabulary.CLASS_LINK_ELEMENT)
public class Link {
    private URI property;
    private URI object;

    public Link() {
        property = null;
        object = null;
    }

    public Link(URI property, URI object) {
        this.property = property;
        this.object = object;
    }

    @RDF(FLuIDVocabulary.GET_PROPERTY_LINK)
    public URI getProperty() {
        return property;
    }

    public void setProperty(URI property) {
        this.property = property;
    }

    @RDF(FLuIDVocabulary.GET_LINK_OBJECT)
    public URI getObject() {
        return object;
    }

    public void setObject(URI object) {
        this.object = object;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Link link = (Link) o;
        return this.hashCode() == link.hashCode();
    }

    @Override
    public int hashCode() {
        String concat = "";
        if(property != null)
            concat += property.toString();
        if(object != null)
            concat += object.toString();

        return concat.hashCode();
    }

    @Override
    public String toString() {
        return "Link{" +
                "property=" + property +
                ", object=" + object +
                '}';
    }
}
