package processing.computation.implementation.schema;

import common.IInstanceElement;
import common.IResource;
import output.interfaces.IElementStore;
import utils.interfaces.IElementCache;
import utils.interfaces.IValueHandler;
import zbw.cau.gotham.schema.SchemaGraphInferencing;

import java.util.Collection;

public class IdentityEquivalenceFactory extends  SimpleSchemaElementFactory {

    /**
     * @param window
     * @param schemaElementsStore
     * @param schemaGraph
     * @param allowedLabels
     * @param disallowedLabels
     * @param useSameAsInstances
     * @param useIncomingProperties
     * @param useOutgoingProperties
     * @param valueHandler
     */
    public IdentityEquivalenceFactory(IElementCache<IInstanceElement> window, IElementStore schemaElementsStore, SchemaGraphInferencing schemaGraph, Collection<String> allowedLabels, Collection<String> disallowedLabels, boolean useSameAsInstances, boolean useIncomingProperties, boolean useOutgoingProperties, IValueHandler valueHandler) {
        super(window, schemaElementsStore, schemaGraph, allowedLabels, disallowedLabels, useSameAsInstances, useIncomingProperties, useOutgoingProperties, valueHandler);
    }

    @Override
    public SchemaElement createSchemaElement(IInstanceElement instanceElement) {
        return null;
    }

    @Override
    public SchemaElement createSchemaElement(IInstanceElement instanceElement, IResource parentInstanceLocator) {
        return null;
    }

    @Override
    public String toString() {
        return "IdentityEquivalenceFactory{" + super.toString() + "}";
    }
}
