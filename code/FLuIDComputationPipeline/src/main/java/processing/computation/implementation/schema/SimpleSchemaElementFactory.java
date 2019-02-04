package processing.computation.implementation.schema;

import common.implemenation.NodeResource;
import common.implemenation.OWLSameAsInstance;
import common.implemenation.Quad;
import common.interfaces.IInstanceElement;
import common.interfaces.IQuint;
import common.interfaces.IResource;
import common.interfaces.ISchemaGraph;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.semanticweb.yars.nx.Resource;
import output.interfaces.IElementStore;
import utils.interfaces.IElementCache;
import utils.interfaces.IValueHandler;

import java.util.*;

import static utils.implementation.Constants.RDF_TYPE;


public abstract class SimpleSchemaElementFactory extends SchemaElementFactory {
    private static final Logger logger = LogManager.getLogger(SimpleSchemaElementFactory.class.getSimpleName());

    private final IValueHandler valueHandler;
    ///<<--------------

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SimpleSchemaElementFactory that = (SimpleSchemaElementFactory) o;
        return useSameAsInstances == that.useSameAsInstances &&
                useIncomingProperties == that.useIncomingProperties &&
                useOutgoingProperties == that.useOutgoingProperties &&
                Objects.equals(allowedLabels, that.allowedLabels) &&
                Objects.equals(disallowedLabels, that.disallowedLabels) &&
                Objects.equals(valueHandler, that.valueHandler);
    }

    @Override
    public int hashCode() {
        return Objects.hash(allowedLabels, disallowedLabels, useSameAsInstances, useIncomingProperties, useOutgoingProperties, valueHandler);
    }

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
    public SimpleSchemaElementFactory(IElementCache<IInstanceElement> window, IElementStore schemaElementsStore, ISchemaGraph schemaGraph,
                                      Collection<String> allowedLabels, Collection<String> disallowedLabels, boolean useSameAsInstances,
                                      boolean useIncomingProperties, boolean useOutgoingProperties, IValueHandler valueHandler) {
        super(window, schemaElementsStore, schemaGraph, useIncomingProperties, disallowedLabels, useSameAsInstances, allowedLabels, useOutgoingProperties);

        this.valueHandler = valueHandler;
    }

    public SchemaElement createSchemaElement(IInstanceElement instanceElement, SimpleSchemaElement schemaElement) {
        return createSchemaElement(instanceElement, schemaElement, null);
    }

    public SchemaElement createSchemaElement(IInstanceElement instanceElement, SimpleSchemaElement schemaElement, IResource parentInstanceLocator) {
        if (instanceElement == null) {
            if (schemaElement instanceof ObjectCluster)
                schemaElement = ObjectCluster.OC_UNRESOLVED;
            else if (schemaElement instanceof PropertyCluster)
                schemaElement = PropertyCluster.PC_UNRESOLVED;
            else if (schemaElement instanceof PropertyObjectCluster)
                schemaElement = PropertyObjectCluster.POC_UNRESOLVED;
            else
                logger.error("Creating invalid simple schema element!");

        }

        //Schema Graph
        inferProperties(instanceElement, schemaGraph);
        inferTypes(instanceElement, schemaGraph);

        if (useSameAsInstances) {
            OWLSameAsInstance owlSameAsInstance = null;
            if (instanceElement instanceof OWLSameAsInstance)
                owlSameAsInstance = (OWLSameAsInstance) instanceElement;
            else {
                logger.error("Misconfiguration of owl:sameAs Instances");
                System.exit(-1);
            }
            //iterate over all connected instances and add all their statements
            IInstanceElement tmpInstance;
            for (IResource locator : owlSameAsInstance.getOWLSameAsInstances()) {
                if ((tmpInstance = window.get(locator)) != null) {
                    tmpInstance.getOutgoingQuints().forEach(Q -> instanceElement.addOutgoingQuint(Q));
                    tmpInstance.getIncomingQuints().forEach(Q -> instanceElement.addIncomingQuint(Q));
                }
            }
        }

        if (useOutgoingProperties) {
            for (IQuint quint : instanceElement.getOutgoingQuints())
                if (!allowedLabels.isEmpty() && allowedLabels.contains(quint.getPredicate().toString()) || (!disallowedLabels.isEmpty() &&
                        !disallowedLabels.contains(quint.getPredicate().toString())) || allowedLabels.isEmpty() && disallowedLabels.isEmpty())
                    schemaElement.addAttribute(valueHandler.extract(quint));

        }

        if (useIncomingProperties) {
            for (IQuint quint : instanceElement.getIncomingQuints())
                if (!allowedLabels.isEmpty() && allowedLabels.contains(quint.getPredicate().toString()) || (!disallowedLabels.isEmpty() &&
                        !disallowedLabels.contains(quint.getPredicate().toString())) || allowedLabels.isEmpty() && disallowedLabels.isEmpty())
                    schemaElement.addAttribute(valueHandler.extract(quint));
        }
        if (schemaElement.getAttributes().isEmpty()) {
            if (schemaElement instanceof ObjectCluster)
                schemaElement = ObjectCluster.OC_EMPTY;
            else if (schemaElement instanceof PropertyCluster)
                schemaElement = PropertyCluster.PC_EMPTY;
            else if (schemaElement instanceof PropertyObjectCluster)
                schemaElement = PropertyObjectCluster.POC_EMPTY;
            else
                logger.error("Creating invalid simple schema element!");

        }

        //if incrementally configured, update the element. Otherwise simply add it
        updateSchemaElement(instanceElement, schemaElement, parentInstanceLocator);

        return schemaElement;
    }


    /**
     * Cloning instance is not good since it creates a new resource, check if this still works
     *
     * @param instance
     * @return
     */
    private void inferProperties(IInstanceElement instance, ISchemaGraph schemaGraph) {
        IInstanceElement newInstance = instance.clone();
        if (schemaGraph != null) {
            HashMap<String, Set<String>> inferredProperties = schemaGraph.inferProperties(newInstance);
            if (inferredProperties != null) {
                for (Map.Entry<String, Set<String>> infProps : inferredProperties.entrySet()) {
                    IResource object = null;
                    IResource context = null;
                    for (IQuint instanceQuint : newInstance.getOutgoingQuints()) {
                        if (instanceQuint.getPredicate().toString().equals(infProps.getKey())) {
                            object = instanceQuint.getObject();
                            context = instanceQuint.getContext();
                            break;
                        }
                    }

                    if (object == null)
                        logger.error("Could not find original statement in schema graph!!");
                    for (String infProp : infProps.getValue())
                        instance.addOutgoingQuint(new Quad(instance.getLocator(), new NodeResource(new Resource(infProp)), object, context));

                }
            }
        }
    }


    private void inferTypes(IInstanceElement instance, ISchemaGraph schemaGraph) {
        if (schemaGraph != null) {
            Set<String> types = schemaGraph.inferSubjectTypes(instance);
            if (types != null) {
                for (String type : types) {//FIXME: Add proper context as in in inferProperties
                    instance.addOutgoingQuint(new Quad(instance.getLocator(), new NodeResource(new Resource(RDF_TYPE)),
                            new NodeResource(new Resource(type)), new NodeResource(new Resource("SchemaGRAPH"))));
                }
            }
        }
    }


    @Override
    public String toString() {
        return "allowedLabels=" + allowedLabels +
                ", disallowedLabels=" + disallowedLabels +
                ", useSameAsInstances=" + useSameAsInstances +
                ", useIncomingProps=" + useIncomingProperties +
                ", useOutgoingProps=" + useOutgoingProperties +
                ", valueHandler=" + valueHandler +
                ", schemaGraph=" + schemaGraph;
    }
}
