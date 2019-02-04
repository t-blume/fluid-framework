package processing.computation.implementation.schema;


import common.interfaces.IInstanceElement;
import common.interfaces.IResource;
import common.interfaces.ISchemaGraph;
import output.interfaces.IElementStore;
import utils.interfaces.IElementCache;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;

public class PropertyClusterFactory extends SimpleSchemaElementFactory {


    public PropertyClusterFactory(IElementCache<IInstanceElement> window, IElementStore schemaElementsStore, ISchemaGraph schemaGraph,
                                  Collection<String> allowedLabels, Collection<String> disallowedLabels, boolean useSameAsInstances,
                                  boolean useIncomingProperties, boolean useOutgoingProperties) {
        super(window, schemaElementsStore, schemaGraph, allowedLabels, disallowedLabels, useSameAsInstances,
                useIncomingProperties, useOutgoingProperties, (quint) ->{
                    try {
                        return new Link(new URI(quint.getPredicate().toString()), null);
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                        return null;
                    }
                });
    }

    @Override
    public SchemaElement createSchemaElement(IInstanceElement instanceElement) {
        PropertyCluster propertyCluster = new PropertyCluster();
        propertyCluster = (PropertyCluster) createSchemaElement(instanceElement, propertyCluster);
        return propertyCluster;
    }

    @Override
    public SchemaElement createSchemaElement(IInstanceElement instanceElement, IResource parentInstanceLocator) {
        PropertyCluster propertyCluster = new PropertyCluster();
        propertyCluster = (PropertyCluster) createSchemaElement(instanceElement, propertyCluster, parentInstanceLocator);
        return propertyCluster;
    }

    @Override
    public String toString() {
        return "PC{" + super.toString() + "}";
    }
}
