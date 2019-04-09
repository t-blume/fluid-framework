package processing.computation.implementation.schema;


import common.IResource;
import common.interfaces.ISchemaElement;
import org.cyberborean.rdfbeans.annotations.RDF;
import org.semanticweb.yars.nx.BNode;
import utils.implementation.FLuIDVocabulary;
import utils.implementation.Hash;

import java.util.HashSet;
import java.util.Objects;

public abstract class SimpleSchemaElement extends SchemaElement {

    //internal information
    private HashSet<Link> attributes;

    public SimpleSchemaElement(){
        super();
        attributes = new HashSet<>();
    }



    @RDF(FLuIDVocabulary.HAS_ATTRIBUTE)
    public HashSet<Link> getAttributes() {
        return attributes;
    }

    public void addAttribute(Link attribute) {
        attributes.add(attribute);
    }

    public void setAttributes(HashSet<Link> attributes) {
        this.attributes = attributes;
    }


    @Override
    public IResource getLocator() {
        return new IResource() {
            @Override
            public boolean equals(Object obj) {
                if (!(obj instanceof IResource))
                    return false;
                IResource r = (IResource) obj;
                return this.toString().equals(r.toString());
            }

            @Override
            public int hashCode() {
                return attributes.hashCode();
            }


            @Override
            public String toString() {
                return Hash.md5(Integer.toString(this.hashCode()));
            }

            @Override
            public String toN3() {
                return (new BNode(toString())).toString();
            }

        };
    }

    @Override
    public void merge(ISchemaElement schemaElement) {
        if(schemaElement instanceof SimpleSchemaElement){
            SimpleSchemaElement sse = (SimpleSchemaElement) schemaElement;
            attributes.addAll(sse.getAttributes());

            /*
                Subject and predicate equivalences only rely on this particular instance.
                Thus, all subject and predicate equivalences will be updated by the parent
                complex schema element. If there is no complex schema element, no such
                relations exist anyway.

                Object equivalences are tricky, since this means a complex schema element needs
                to be recomputed summarizing different instances.
             */
            getIsSubjectEquivalenceOfInternal().addAll(sse.getIsSubjectEquivalenceOfInternal());
            //TODO: cloning necessary?
            setPayload(schemaElement.getPayload());

        }



    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SimpleSchemaElement that = (SimpleSchemaElement) o;
        return Objects.equals(getLocator().toString(), that.getLocator().toString());
    }

    @Override
    public int hashCode() {
        return getLocator().hashCode();
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName().toString() + "{" +
                "URI=" + getURI() + " " +
                "attributes=" + attributes + " " +
                "PAYLOAD=" + getPayload() + " " +
                "}";
    }
}
