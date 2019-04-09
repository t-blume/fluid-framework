package processing.computation.implementation.schema;


import common.IInstanceElement;
import common.IResource;
import common.implemenation.RDFInstance;
import output.interfaces.IElementStore;
import utils.interfaces.IElementCache;
import zbw.cau.gotham.schema.SchemaGraphInferencing;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;

import static utils.implementation.Helper.isLiteral;

public class PropertyObjectClusterFactory extends SimpleSchemaElementFactory {

    /**
     * object Equivalence == null is identity equivalence
     *
     * @param window
     * @param schemaElementsStore
     * @param schemaGraph
     * @param allowedLabels
     * @param disallowedLabels
     * @param useSameAsInstances
     * @param useIncomingProperties
     * @param useOutgoingProperties
     * @param objectEquivalence
     */
    public PropertyObjectClusterFactory(IElementCache<IInstanceElement> window, IElementStore schemaElementsStore, SchemaGraphInferencing schemaGraph,
                                        Collection<String> allowedLabels, Collection<String> disallowedLabels, boolean useSameAsInstances,
                                        boolean useIncomingProperties, boolean useOutgoingProperties, SchemaElementFactory objectEquivalence) {
        super(window, schemaElementsStore, schemaGraph, allowedLabels, disallowedLabels, useSameAsInstances, useIncomingProperties,
                useOutgoingProperties, (quint) -> {
                    try {
                        String object;
                        if(!isLiteral(quint.getObject())) {
                            if (objectEquivalence != null) {
                                IInstanceElement element = window.get(RDFInstance.createLocator(quint.getObject()));
                                object = objectEquivalence.createSchemaElement(element, quint.getSubject()).getURI();
                            } else
                                object = quint.getObject().toString();

                            return new Link(new URI(quint.getPredicate().toString()), new URI(object));
                        }else {
                            return new Link(new URI(quint.getPredicate().toString()), new URI(ObjectCluster.OC_UNRESOLVED_LITERAL.getURI()));
                        }

                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                        return null;
                    }
                });
    }


    @Override
    public SchemaElement createSchemaElement(IInstanceElement instanceElement) {
        PropertyObjectCluster propertyObjectCluster = new PropertyObjectCluster();
        propertyObjectCluster = (PropertyObjectCluster) createSchemaElement(instanceElement, propertyObjectCluster);
        return propertyObjectCluster;
    }

    @Override
    public SchemaElement createSchemaElement(IInstanceElement instanceElement, IResource parentInstanceLocator) {
        PropertyObjectCluster propertyObjectCluster = new PropertyObjectCluster();
        propertyObjectCluster = (PropertyObjectCluster) createSchemaElement(instanceElement, propertyObjectCluster, parentInstanceLocator);
        return propertyObjectCluster;
    }

    @Override
    public String toString() {
        return "POC{" + super.toString() + "}";
    }
}
