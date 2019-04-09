package processing.computation.implementation.schema;


import common.IInstanceElement;
import common.IResource;
import output.interfaces.IElementStore;
import utils.interfaces.IElementCache;
import utils.interfaces.IValueHandler;
import zbw.cau.gotham.schema.SchemaGraphInferencing;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;

public class ObjectClusterFactory extends SimpleSchemaElementFactory{


    public ObjectClusterFactory(IElementCache<IInstanceElement> window, IElementStore schemaElementsStore,
                                SchemaGraphInferencing schemaGraph, Collection<String> allowedLabels,
                                Collection<String> disallowedLabels, boolean useSameAsInstances,
                                boolean useIncomingProperties, boolean useOutgoingProperties, IValueHandler valueHandler) {
        super(window, schemaElementsStore, schemaGraph, allowedLabels, disallowedLabels, useSameAsInstances, useIncomingProperties, useOutgoingProperties, valueHandler);
    }

    public ObjectClusterFactory(IElementCache<IInstanceElement> window, IElementStore schemaElementsStore, SchemaGraphInferencing schemaGraph, Collection<String> allowedLabels, Collection<String> disallowedLabels, boolean useSameAsInstances, boolean useIncomingProperties, boolean useOutgoingProperties) {
        super(window, schemaElementsStore, schemaGraph, allowedLabels, disallowedLabels, useSameAsInstances, useIncomingProperties,
                useOutgoingProperties, (quint) ->
                {
                    try {
                        return new Link(null, new URI(quint.getObject().toString()));
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                        return null;
                    }
                });
    }

    @Override
    public SchemaElement createSchemaElement(IInstanceElement instanceElement) {
        if(instanceElement == null)
            return ObjectCluster.OC_UNRESOLVED_LITERAL; //FIXME: Different unresolved types != compatibility

        ObjectCluster objectCluster = new ObjectCluster();
        objectCluster = (ObjectCluster) createSchemaElement(instanceElement,  objectCluster);

        return objectCluster;
    }

    @Override
    public SchemaElement createSchemaElement(IInstanceElement instanceElement, IResource parentInstanceLocator) {
        if(instanceElement == null)
            return ObjectCluster.OC_UNRESOLVED_LITERAL; //FIXME: Different unresolved types != compatibility

        ObjectCluster objectCluster = new ObjectCluster();
        objectCluster = (ObjectCluster) createSchemaElement(instanceElement,  objectCluster, parentInstanceLocator);
        return objectCluster;
    }

    @Override
    public String toString() {
        return "OC{" + super.toString() + "}";
    }
}
