package processing.computation.implementation.schema;


import common.interfaces.IInstanceElement;
import common.interfaces.IResource;
import common.interfaces.ISchemaGraph;
import output.interfaces.IElementStore;
import utils.interfaces.IElementCache;

import java.util.Collection;
import java.util.List;

public class ComplexSchemaElementFactory extends SchemaElementFactory {

    private int chainingLength;

    private List<SchemaElementFactory> subjectEquivalenceFactories;
    private SchemaElementFactory predicateEquivalenceFactory = null;
    private SchemaElementFactory objectEquivalenceFactory = null;


    public ComplexSchemaElementFactory(IElementCache<IInstanceElement> window, IElementStore schemaElementsStore, ISchemaGraph schemaGraph,
                                       List<SchemaElementFactory> subjectEquivalenceFactories,
                                       boolean predicateIdentityEquivalence,
                                       List<SchemaElementFactory> objectEquivalenceFactories,
                                       boolean useIncomingProperties,
                                       Collection<String> disallowedLabels, boolean useSameAsInstances,
                                       Collection<String> allowedLabels, boolean useOutgoingProperties,
                                       int chainingLength) {

        super(window, schemaElementsStore, schemaGraph, useIncomingProperties, disallowedLabels, useSameAsInstances, allowedLabels, useOutgoingProperties);
        this.subjectEquivalenceFactories = subjectEquivalenceFactories;
        //FIXME: Currently support only 1 object equivalence
        this.objectEquivalenceFactory = objectEquivalenceFactories != null && !objectEquivalenceFactories.isEmpty() ?
                objectEquivalenceFactories.get(0) : null;
        //Note: Implementation hack
        if (predicateIdentityEquivalence) {
            if (objectEquivalenceFactory != null)
                predicateEquivalenceFactory = new PropertyObjectClusterFactory(window, schemaElementsStore,
                        schemaGraph, allowedLabels, disallowedLabels, useSameAsInstances, useIncomingProperties, useOutgoingProperties, objectEquivalenceFactory);

                //if there is no object equivalence, it is a tautology, use different hack
            else if (objectEquivalenceFactories.isEmpty())
                predicateEquivalenceFactory = new PropertyClusterFactory(window, schemaElementsStore,
                        schemaGraph, allowedLabels, disallowedLabels, useSameAsInstances, useIncomingProperties, useOutgoingProperties);
        }
        this.chainingLength = chainingLength;
    }

    @Override
    public SchemaElement createSchemaElement(IInstanceElement instanceElement) {
        return createSchemaElement(instanceElement, null);
    }

    @Override
    public SchemaElement createSchemaElement(IInstanceElement instanceElement, IResource parentInstanceLocator) {
        ComplexSchemaElement complexSchemaElement = new ComplexSchemaElement(chainingLength);
        if (subjectEquivalenceFactories != null) {
            for (SchemaElementFactory factory : subjectEquivalenceFactories) {
                SchemaElement subjectElement = factory.createSchemaElement(instanceElement);
                complexSchemaElement.putSubjectElement(factory.hashCode(), subjectElement); //TODO merge?
                subjectElement.addIsSubjectEquivalenceOf(complexSchemaElement);
            }
        }


        //TODO: verify if correct
        if (predicateEquivalenceFactory != null) {
            SchemaElement predicateElement = predicateEquivalenceFactory.createSchemaElement(instanceElement);
            complexSchemaElement.putPredicateElement(predicateEquivalenceFactory.hashCode(), predicateElement); //TODO merge?
            predicateElement.addIsPredicateEquivalenceOf(complexSchemaElement);
        }


        //if incrementally configured, update the element. Otherwise simply add it
        updateSchemaElement(instanceElement, complexSchemaElement, parentInstanceLocator);

        return complexSchemaElement;
    }

    @Override
    public String toString() {
        return "CSE{" +
                "chaining=" + chainingLength +
                ", subjEquiv=" + subjectEquivalenceFactories +
                ", predEquiv=" + predicateEquivalenceFactory +
                ", objeEquiv=" + objectEquivalenceFactory +
                "} ";
    }
}
