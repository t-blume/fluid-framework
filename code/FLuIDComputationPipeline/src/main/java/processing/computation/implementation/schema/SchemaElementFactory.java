package processing.computation.implementation.schema;

import common.IInstanceElement;
import common.IQuint;
import common.IResource;
import common.interfaces.ISchemaElement;
import output.interfaces.IElementStore;
import output.interfaces.IUpdateCoordinator;
import utils.interfaces.IElementCache;
import utils.interfaces.IElementCacheListener;
import zbw.cau.gotham.schema.ISchemaGraph;
import zbw.cau.gotham.schema.SchemaGraphInferencing;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public abstract class SchemaElementFactory implements IElementCacheListener<IInstanceElement> {

    //this is unique in the sense that the same configuration and and same instance lead to the same schema element!
    protected final Collection<String> allowedLabels;
    protected final Collection<String> disallowedLabels;
    protected final boolean useSameAsInstances;
    protected final boolean useIncomingProperties;
    protected final boolean useOutgoingProperties;
    //current window operating on number of instances
    protected IElementCache<IInstanceElement> window;   //IN

    public IElementStore<ISchemaElement> getSchemaElementsStore() {
        return schemaElementsStore;
    }

    public Collection<String> getAllowedLabels() {
        return allowedLabels;
    }

    public Collection<String> getDisallowedLabels() {
        return disallowedLabels;
    }

    public boolean isUseSameAsInstances() {
        return useSameAsInstances;
    }

    public boolean isUseIncomingProperties() {
        return useIncomingProperties;
    }

    public boolean isUseOutgoingProperties() {
        return useOutgoingProperties;
    }

    //where to store/lookup the computed schema elements
    protected IElementStore<ISchemaElement> schemaElementsStore;   //OUT

    //where to store/lookup the ontology-level statements for inference
    protected SchemaGraphInferencing schemaGraph;     //ADD-INFO


    public IUpdateCoordinator getUpdateCoordinator() {
        return updateCoordinator;
    }

    public void setUpdateCoordinator(IUpdateCoordinator updateCoordinator) {
        this.updateCoordinator = updateCoordinator;
    }

    //update coordinator
    protected IUpdateCoordinator updateCoordinator;

    public SchemaElementFactory(IElementCache<IInstanceElement> window, IElementStore schemaElementsStore,
                                SchemaGraphInferencing schemaGraph, boolean useIncomingProperties,
                                Collection<String> disallowedLabels, boolean useSameAsInstances,
                                Collection<String> allowedLabels, boolean useOutgoingProperties) {
        this.window = window;
        this.schemaElementsStore = schemaElementsStore;
        this.schemaGraph = schemaGraph;
        this.useIncomingProperties = useIncomingProperties;
        this.disallowedLabels = disallowedLabels;
        this.useSameAsInstances = useSameAsInstances;
        this.allowedLabels = allowedLabels;
        this.useOutgoingProperties = useOutgoingProperties;
    }

    public abstract SchemaElement createSchemaElement(IInstanceElement instanceElement);

    public abstract SchemaElement createSchemaElement(IInstanceElement instanceElement, IResource parentInstanceLocator);

    /**
     * @TODO Immer nachdem updateSchemaElement aufgerufen wurde, Integretät der Datenstruktur testen.
     * @param instanceElement
     * @param schemaElement
     * @param parentInstanceLocator, null if non exist
     */
    public void updateSchemaElement(IInstanceElement instanceElement, ISchemaElement schemaElement, IResource parentInstanceLocator) {
        schemaElementsStore.add(schemaElement);
        if (updateCoordinator != null) {
            Set<IResource> contexts = new HashSet<>();
            for (IQuint q : instanceElement.getOutgoingQuints())
                contexts.add(q.getContext());

            if (parentInstanceLocator != null) {
                HashSet<IResource> parentInstances = new HashSet<>();
                parentInstances.add(parentInstanceLocator);
                updateCoordinator.addSchemaElement(hashCode(), instanceElement.getLocator(), schemaElement.getLocator(),contexts, parentInstances);
            } else
                updateCoordinator.addSchemaElement(hashCode(), instanceElement.getLocator(), schemaElement.getLocator(), contexts);

        }
    }

    @Override
    public void elementFlushed(IInstanceElement instance) {
        createSchemaElement(instance);
    }

    @Override
    public void finished() {
        schemaElementsStore.close();
        if (updateCoordinator != null)
            updateCoordinator.printInfo();
    }
}
