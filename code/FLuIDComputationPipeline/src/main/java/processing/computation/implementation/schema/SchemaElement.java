package processing.computation.implementation.schema;

import common.interfaces.IPayloadElement;
import common.interfaces.ISchemaElement;
import org.cyberborean.rdfbeans.annotations.RDF;
import org.cyberborean.rdfbeans.annotations.RDFSubject;
import utils.implementation.FLuIDVocabulary;

import java.util.Collection;
import java.util.HashSet;

public abstract class SchemaElement implements ISchemaElement {
    //which factory created this element
    private Integer abstractSchemaElementType;
    //relationships to other schema elements
    private Collection<IPayloadElement> payloadElements;
    private Collection<ComplexSchemaElement> isSubjectEquivalenceOf;
    private Collection<ComplexSchemaElement> isPredicateEquivalenceOf;
    private Collection<ComplexSchemaElement> isObjectEquivalenceOf;



    public SchemaElement(){
        payloadElements = new HashSet<>();
        isSubjectEquivalenceOf = new HashSet<>();
        isPredicateEquivalenceOf = new HashSet<>();
        isObjectEquivalenceOf = new HashSet<>();
    }

    @RDFSubject
    public String getURI(){
        return FLuIDVocabulary.createSubjectURIPrefix(this.getClass()) + getLocator().toString();
    }
    public void setURI(String uri){
        //Nothing todo here, gets always generated on the fly (request)
    }

    @RDF(FLuIDVocabulary.ABSTRACT_SCHEMA_ELEMENT_TYPE)
    public Integer abstractSchemaElementType() {
        return abstractSchemaElementType;
    }

    public void setAbstractSchemaElementType(Integer abstractSchemaElementType) {
        this.abstractSchemaElementType = abstractSchemaElementType;
    }

    @RDF(FLuIDVocabulary.HAS_PAYLOAD)
    public Collection<IPayloadElement> getPayload() {
        return payloadElements;
    }

    public void setPayload(Collection<IPayloadElement> payloadElements) {
        this.payloadElements = payloadElements;
    }

    public void addPayload(IPayloadElement newHasPayloadElement) {
        payloadElements.add(newHasPayloadElement);
    }

    public Collection<ComplexSchemaElement> getIsObjectEquivalenceOfInternal() {
        return isObjectEquivalenceOf;
    }

    @RDF(FLuIDVocabulary.IS_OBJECT_EQUIVALENCE)
    public Collection<String> getIsObjectEquivalenceOf() {
        Collection<String> col = new HashSet<>();
        isObjectEquivalenceOf.stream().forEach(X -> col.add(X.getURI()));

        return col;
    }


    public Collection<? extends ComplexSchemaElement> getIsPredicateEquivalenceOfInternal() { return isPredicateEquivalenceOf; }

    @RDF(FLuIDVocabulary.IS_PREDICATE_EQUIVALENCE)
    public Collection<String> getIsPredicateEquivalenceOf() {
        Collection<String> col = new HashSet<>();
        isPredicateEquivalenceOf.stream().forEach(X -> col.add(X.getURI()));

        return col;
    }



    public Collection<ComplexSchemaElement> getIsSubjectEquivalenceOfInternal() {
        return isSubjectEquivalenceOf; }


    //@RDF(FLuIDVocabulary.IS_SUBJECT_EQUIVALENCE)
    public Collection<String> getIsSubjectEquivalenceOf() {
        Collection<String> col = new HashSet<>();
        isSubjectEquivalenceOf.stream().forEach(X -> col.add(X.getURI()));

        return col;
    }


    public boolean hasIsObjectEquivalenceOf() {
        return !isObjectEquivalenceOf.isEmpty();
    }

    public void addIsObjectEquivalenceOf(ComplexSchemaElement newIsObjectEquivalenceOf) {
        isObjectEquivalenceOf.add(newIsObjectEquivalenceOf);
    }

    public void removeIsObjectEquivalenceOf(ComplexSchemaElement oldIsObjectEquivalenceOf) {
        isObjectEquivalenceOf.remove(oldIsObjectEquivalenceOf);
    }

    public boolean hasIsPredicateEquivalenceOf() {
        return !isPredicateEquivalenceOf.isEmpty();
    }

    public void addIsPredicateEquivalenceOf(ComplexSchemaElement newIsPredicateEquivalenceOf) {
        isPredicateEquivalenceOf.add(newIsPredicateEquivalenceOf);
    }

    public void removeIsPredicateEquivalenceOf(ComplexSchemaElement oldIsPredicateEquivalenceOf) {
        isPredicateEquivalenceOf.remove(oldIsPredicateEquivalenceOf);
    }

    public boolean hasIsSubjectEquivalenceOf() {
        return !isSubjectEquivalenceOf.isEmpty();
    }

    public void addIsSubjectEquivalenceOf(ComplexSchemaElement newIsSubjectEquivalenceOf) {
        isSubjectEquivalenceOf.add(newIsSubjectEquivalenceOf);
    }

    public void removeIsSubjectEquivalenceOf(ComplexSchemaElement oldIsSubjectEquivalenceOf) {
        isSubjectEquivalenceOf.remove(oldIsSubjectEquivalenceOf);
    }

    public void setIsSubjectEquivalenceOf(Collection<ComplexSchemaElement> isSubjectEquivalenceOf) {
        this.isSubjectEquivalenceOf = isSubjectEquivalenceOf;
    }

    public void setIsPredicateEquivalenceOf(Collection<ComplexSchemaElement> isPredicateEquivalenceOf) {
        this.isPredicateEquivalenceOf = isPredicateEquivalenceOf;
    }

    public void setIsObjectEquivalenceOf(Collection<ComplexSchemaElement> isObjectEquivalenceOf) {
        this.isObjectEquivalenceOf = isObjectEquivalenceOf;
    }

    @Override
    public String toString() {
        return "SchemaElement{" +
                "payloadElements=" + payloadElements +
                ", isSubjectEquivalenceOf=" + isSubjectEquivalenceOf +
                ", isPredicateEquivalenceOf=" + isPredicateEquivalenceOf +
                ", isObjectEquivalenceOf=" + isObjectEquivalenceOf +
                '}';
    }
}
