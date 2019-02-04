package processing.computation.implementation.schema;

import common.interfaces.IResource;
import common.interfaces.ISchemaElement;
import org.cyberborean.rdfbeans.annotations.RDFBean;
import org.semanticweb.yars.nx.BNode;
import utils.implementation.FLuIDVocabulary;
import utils.implementation.Hash;

import java.net.URI;
import java.util.*;

@RDFBean(FLuIDVocabulary.CLASS_COMPLEX_SCHEMA_ELEMENT)
public class ComplexSchemaElement extends SchemaElement {

    //Factory Hash -> produced element
    private Map<Integer, SchemaElement> subjectElements;
    private Map<Integer, SchemaElement> predicateElements;
    private Map<Integer, SchemaElement> objectElements;


    private int chainingLength;

    public ComplexSchemaElement(){
        this(1);
    }

    public ComplexSchemaElement(int chainingLength) {
        super();
        subjectElements = new HashMap<>();
        predicateElements = new HashMap<>();
        objectElements = new HashMap<>();

        this.chainingLength = chainingLength;
    }
    //
    public Collection<SchemaElement> getHasSubjectEquivalencesInternal(){
        return subjectElements.values();
    }

//    @RDF(FLuIDVocabulary.HAS_SUBJECT_EQUIVALENCE)
    public Collection<URI> getHasSubjectEquivalences(){
        Collection<URI> col = new HashSet<>();
        subjectElements.values().stream().forEach(X -> col.add(URI.create(X.getURI())));

        return col;
    }
    public Collection<SchemaElement> getHasPredicateEquivalencesInternal(){
        return predicateElements.values();
    }

//    @RDF(FLuIDVocabulary.HAS_PREDICATE_EQUIVALENCE)
    public Collection<URI> getHasPredicateEquivalences(){
        Collection<URI> col = new HashSet<>();
        predicateElements.values().stream().forEach(X -> col.add(URI.create(X.getURI())));

        return col;
    }
    public Collection<SchemaElement> getHasObjectEquivalencesInternal(){
        return objectElements.values();
    }
//    @RDF(FLuIDVocabulary.HAS_OBJECT_EQUIVALENCE)
    public Collection<URI> getHasObjectEquivalences(){
        Collection<URI> col = new HashSet<>();
        objectElements.values().stream().forEach(X -> col.add(URI.create(X.getURI())));

        return col;
    }

    //

    public void setHasSubjectEquivalences(Collection<SchemaElement> subjectEquivalences){
        if(subjectEquivalences != null)
            subjectEquivalences.forEach(EQ -> subjectElements.put(EQ.abstractSchemaElementType(), EQ));
    }
    public void setHasPredicateEquivalences(Collection<SchemaElement> predicateEquivalences){
        if(predicateEquivalences != null)
            predicateEquivalences.forEach(EQ -> predicateElements.put(EQ.abstractSchemaElementType(), EQ));
    }
    public void setHasObjectEquivalences(Collection<SchemaElement> objectEquivalences){
        if(objectEquivalences != null)
            objectEquivalences.forEach(EQ -> objectElements.put(EQ.abstractSchemaElementType(), EQ));
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
                int hash1 = subjectElements.isEmpty() ? 0 : subjectElements.values().hashCode();
                int hash2 = predicateElements.isEmpty() ? 0 : predicateElements.values().hashCode();
                int hash3 = objectElements.isEmpty() ? 0 : objectElements.values().hashCode();
                return hash1 + hash2 - hash3;
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
    public ISchemaElement clone() {
        return null;
    }




    @Override
    public String toString() {
        return "ComplexSchemaElement{" +
                ",PAYLOAD="+getPayload()+
                ",HAS_SUBJECT_EQUIVALENCE=" + subjectElements +
                ", HAS_PREDICATE_EQUIVALENCE=" + predicateElements +
                ", HAS_OBJECT_EQUIVALENCE=" + objectElements +
                ", chainingLength=" + chainingLength +
                '}';
    }


    @Override
    public void merge(ISchemaElement otherSchemaElement) {

        ComplexSchemaElement otherComplexSchemaElement;
        if(otherSchemaElement instanceof ComplexSchemaElement)
            otherComplexSchemaElement = (ComplexSchemaElement) otherSchemaElement;
        else
            return;

        /**
         * Merging algorithm:
         * 1. All the subject equivalences need to be merged!
         * 2. ...?
         */
        if(subjectElements == null)
            subjectElements = new HashMap<>();
        if(otherComplexSchemaElement.subjectElements() != null){
            otherComplexSchemaElement.subjectElements().forEach((K, V) -> {
                SchemaElement tmp = subjectElements.get(K);
                if(tmp != null) {
                    //other schema element has same kind of child element, so merge
                    tmp.merge(V);
                    subjectElements.put(K, tmp);//probably unnecessary due to object reference
                }
            });

        }
        //TODO: repeat for other

    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ComplexSchemaElement that = (ComplexSchemaElement) o;
        return Objects.equals(subjectElements, that.subjectElements) &&
                Objects.equals(predicateElements, that.predicateElements) &&
                Objects.equals(objectElements, that.objectElements);
    }

    @Override
    public int hashCode() {
        return getLocator().hashCode();
    }

    public Map<Integer, SchemaElement> subjectElements() {
        return subjectElements;
    }

    public void putSubjectElement(Integer schemaFactoryHash, SchemaElement subjectElement) {
        this.subjectElements.put(schemaFactoryHash, subjectElement);
    }

    public void setSubjectElements(Map<Integer, SchemaElement> subjectElements) {
        this.subjectElements = subjectElements;
    }

    public Map<Integer, SchemaElement> predicateElements() {
        return predicateElements;
    }

    public void putPredicateElement(Integer schemaFactoryHash, SchemaElement subjectElement) {
        this.predicateElements.put(schemaFactoryHash, subjectElement);
    }

    public void setPredicateElements(Map<Integer, SchemaElement> predicateElements) {
        this.predicateElements = predicateElements;
    }

    public Map<Integer, SchemaElement> objectElements() {
        return objectElements;
    }

    public void putObjectElement(Integer schemaFactoryHash, SchemaElement subjectElement) {
        this.objectElements.put(schemaFactoryHash, subjectElement);
    }

    public void setObjectElements(Map<Integer, SchemaElement> objectElements) {
        this.objectElements = objectElements;
    }

    public int getChainingLength() {
        return chainingLength;
    }
}
