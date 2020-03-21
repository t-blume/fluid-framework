package utils.implementation;

import common.IInstanceElement;
import common.interfaces.ISchemaElement;
import net.percederberg.grammatica.parser.Node;
import net.percederberg.grammatica.parser.ParserCreationException;
import net.percederberg.grammatica.parser.ParserLogException;
import net.percederberg.grammatica.parser.Token;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import output.interfaces.IElementStore;
import processing.computation.implementation.SchemaComputation;
import processing.computation.implementation.payload.DatasourceFactory;
import processing.computation.implementation.schema.*;
import utils.implementation.EBNFParser.FluidConstants;
import utils.implementation.EBNFParser.FluidParser;
import utils.interfaces.IElementCache;
import zbw.cau.gotham.schema.SchemaGraphInferencing;

import java.io.StringReader;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import static utils.implementation.Constants.rdfType;

public class FLuIDEbnfReader {
    private static final Logger logger = LogManager.getLogger(FLuIDEbnfReader.class.getName());

    public static SchemaComputation parseConfig(String configString, IElementCache<IInstanceElement> window,
                                                IElementStore<ISchemaElement> schemaCache, SchemaGraphInferencing schemaGraph) {
        try {
            logger.debug(configString);
            FluidParser fluidParser = new FluidParser(new StringReader(configString));

            Node rootNode = fluidParser.parse();


            Config config = parseParamterizations(rootNode);


            Node primeSchemaElement = rootNode.getChildAt(0);
            logger.debug(primeSchemaElement);
            SchemaElementFactory factory = null;
            if (primeSchemaElement.getId() == FluidConstants.COMPLEX_SCHEMA_ELEMENT) {
                factory = parseComplexSchemaElement(primeSchemaElement, window,
                        schemaCache, schemaGraph, config);

            } else if (primeSchemaElement.getId() == FluidConstants.SIMPLE_SCHEMA_ELEMENT) {
                factory = parseSimpleSchemaElement(primeSchemaElement, window,
                        schemaCache, schemaGraph, config);
            }
            //TODO: parameterize PAYLOAD
            return new SchemaComputation(factory, new DatasourceFactory());

        } catch (ParserCreationException e) {
            logger.error("Invalid configuration");
            logger.error(e.getLocalizedMessage());
            System.exit(-1);
        } catch (ParserLogException e) {
            logger.error("Invalid configuration");
            logger.error(e.getLocalizedMessage());
            System.exit(-1);
        }
        return null;
    }

    private static Config parseParamterizations(Node node) {
        return parseParamterizations(node, new Config());
    }

    private static Config parseParamterizations(Node node, Config defaultConfig) {
        Config config = defaultConfig.clone();
        for (int i = 0; i < node.getChildCount(); i++) {
            if (node.getChildAt(i).getId() == FluidConstants.INSTANCE_PARAM) {
                parseInstanceParam(node.getChildAt(i), config);
            } else if (node.getChildAt(i).getId() == FluidConstants.BISIM_PARAM)
                parseBisimParam(node.getChildAt(i), config);
            else if (node.getChildAt(i).getId() == FluidConstants.DIRECTION_OP) {
                parseDirectionParam(node.getChildAt(i), config);
            } else if (node.getChildAt(i).getId() == FluidConstants.LABEL_PARAM) {
                parseLabelConfig(node.getChildAt(i), config);
            }
        }

        return config;
    }

    private static void parseInstanceParam(Node node, Config config) {
        if (node.getChildCount() != 2)
            logger.error("Incorrect Config");
        if (node.getChildAt(0).getId() != FluidConstants.INSTANCE_OP)
            logger.error("Incorrect Config");
        if (node.getChildAt(1).getId() != FluidConstants.INSTANCE_SET)
            logger.error("Incorrect Config");

        Node parameterNode = node.getChildAt(1).getChildAt(0);
        if (parameterNode.getId() == FluidConstants.SAME_AS)
            config.useSameAsInstances = true;
        else if (parameterNode.getId() == FluidConstants.RELATED_PROPERTY)
            config.useRelatedProperties = true;

    }

    private static void parseBisimParam(Node node, Config config) {
        config.bisimulationDepth = Integer.parseInt(((Token) node.getChildAt(1)).getImage());
    }

    private static void parseDirectionParam(Node node, Config config) {
        logger.debug(node.getChildAt(0));
        if (node.getChildAt(0).getId() == FluidConstants.UNDIRECTED) {
            config.useIncomingProperties = true;
            config.useOutgoingProperties = true;
        } else if (node.getChildAt(0).getId() == FluidConstants.INCOMING) {
            config.useIncomingProperties = true;
            config.useOutgoingProperties = false;
        } else if (node.getChildAt(0).getId() == FluidConstants.OUTGOING) {
            config.useIncomingProperties = false;
            config.useOutgoingProperties = true;
        }
    }

    private static void parseLabelConfig(Node node, Config config) {
        logger.debug(node.getChildAt(1).getChildAt(0));
        if (node.getChildAt(1).getChildAt(0).getId() == FluidConstants.TYPES)
            config.useTypeSets = true;
        else if (node.getChildAt(1).getChildAt(0).getId() == FluidConstants.RELATIONS)
            config.useRelationSets = true;
    }


    private static ComplexSchemaElementFactory parseComplexSchemaElement(Node cse,
                                                                         IElementCache<IInstanceElement> window,
                                                                         IElementStore<ISchemaElement> schemaCache,
                                                                         SchemaGraphInferencing schemaGraph,
                                                                         Config config) {

        config = parseParamterizations(cse, config);

        List<SchemaElementFactory> subjectEquivalences = new LinkedList<>();
        List<SchemaElementFactory> predicateEquivalences = new LinkedList<>();
        List<SchemaElementFactory> objectEquivalences = new LinkedList<>();

        //cse start with opening bracket, so skip it directly
        int elementIndex = 1;
        int elementType;
        int csePart = 0;
        while (elementIndex < cse.getChildCount()) {
            Node element = cse.getChildAt(elementIndex);
            elementType = element.getId();
            if (elementType == FluidConstants.SCHEMA_ELEMENT) {
                //follow hierarchy to low-level element
                element = element.getChildAt(0);
                elementType = element.getId();
            }
            if (elementType == FluidConstants.CSE_SEP)
                csePart++;
            else if (elementType == FluidConstants.COMBINE_OP) {

                //TODO: implement OR, default assumption
            } //nothing
            else if (elementType == FluidConstants.SIMPLE_SCHEMA_ELEMENT ||
                    elementType == FluidConstants.COMPLEX_SCHEMA_ELEMENT) {
                SchemaElementFactory factory = null;
                if (elementType == FluidConstants.SIMPLE_SCHEMA_ELEMENT)
                    factory = parseSimpleSchemaElement(element,
                            window, schemaCache, schemaGraph, config);
                if (elementType == FluidConstants.COMPLEX_SCHEMA_ELEMENT)
                    factory = parseComplexSchemaElement(element,
                            window, schemaCache, schemaGraph, config);

                if (csePart == 0)
                    subjectEquivalences.add(factory);
                else if (csePart == 1)
                    predicateEquivalences.add(factory);
                else if (csePart == 2)
                    objectEquivalences.add(factory);
            } else if (elementType == FluidConstants.CSE_CLOSE)
                break;//cse is closed, parameterizations have already been extracted before
            else
                logger.warn("Unexpected Schema Definition: " + element + "@" + elementIndex);

            elementIndex++;
        }
        boolean propertyPaths = false;
        Collection<String> allowedLabels = new HashSet<>();
        Collection<String> disAllowedLabels = new HashSet<>();

        for (SchemaElementFactory factory : predicateEquivalences) {
            if (factory instanceof IdentityEquivalenceFactory) {
                propertyPaths = true;
                allowedLabels = factory.getAllowedLabels();
                disAllowedLabels = factory.getDisallowedLabels();
            }
        }
        //TODO: undirected CSE currently not supported
        return new ComplexSchemaElementFactory(window, schemaCache, schemaGraph,
                subjectEquivalences, propertyPaths, objectEquivalences,
                false, disAllowedLabels, config.useSameAsInstances, allowedLabels, config.useOutgoingProperties,
                config.bisimulationDepth);

    }


    private static SimpleSchemaElementFactory parseSimpleSchemaElement(Node sse,
                                                                       IElementCache<IInstanceElement> window,
                                                                       IElementStore<ISchemaElement> schemaCache,
                                                                       SchemaGraphInferencing schemaGraph,
                                                                       Config config) {

        config = parseParamterizations(sse, config);
        //get actual schema element
        int index = 0;
        Node element = null;
        while (index < sse.getChildCount()) {
            element = sse.getChildAt(index);
            if (element.getId() == FluidConstants.BASIC_ELEMENTS ||
                    element.getId() == FluidConstants.OBJECT_CLUSTER ||
                    element.getId() == FluidConstants.PROPERTY_CLUSTER ||
                    element.getId() == FluidConstants.PROPERTYOBJECT_CLUSTER)
                break;

            index++;
        }
        if (element == null)
            logger.error("Invalid Simple Schema Element!");

        //one more step for basic types
        if (element.getId() == FluidConstants.BASIC_ELEMENTS) {
            element = element.getChildAt(0);
        }


        if (element.getId() == FluidConstants.IDENTITY) {
            if (config.useTypeSets)
                return new IdentityEquivalenceFactory(window, schemaCache, schemaGraph, rdfType, new HashSet<>(),
                        config.useSameAsInstances, false, true, null);
            else if (config.useRelationSets)
                return new IdentityEquivalenceFactory(window, schemaCache, schemaGraph, new HashSet<>(), rdfType,
                        config.useSameAsInstances, false, true, null);
            else
                return new IdentityEquivalenceFactory(window, schemaCache, schemaGraph, new HashSet<>(), new HashSet<>(),
                        config.useSameAsInstances, false, true, null);
        } else if (element.getId() == FluidConstants.TAUTOLOGY) {
            return null;
        } else if (element.getId() == FluidConstants.OBJECT_CLUSTER) {
            if (config.useTypeSets)
                return new ObjectClusterFactory(window, schemaCache, schemaGraph,
                        rdfType, new HashSet<>(), config.useSameAsInstances, config.useIncomingProperties,
                        config.useOutgoingProperties, new TypeExtractor());
            else if (config.useRelationSets)
                return new ObjectClusterFactory(window, schemaCache, schemaGraph,
                        new HashSet<>(), rdfType, config.useSameAsInstances, config.useIncomingProperties,
                        config.useOutgoingProperties, new TypeExtractor());
            else
                return new ObjectClusterFactory(window, schemaCache, schemaGraph,
                        new HashSet<>(), new HashSet<>(), config.useSameAsInstances, config.useIncomingProperties,
                        config.useOutgoingProperties, new TypeExtractor());

        } else if (element.getId() == FluidConstants.PROPERTY_CLUSTER) {
            if (config.useTypeSets)
                return new PropertyClusterFactory(window, schemaCache, schemaGraph,
                        rdfType, new HashSet<>(), config.useSameAsInstances, config.useIncomingProperties,
                        config.useOutgoingProperties);
            else if (config.useRelationSets)
                return new PropertyClusterFactory(window, schemaCache, schemaGraph,
                        new HashSet<>(), rdfType, config.useSameAsInstances, config.useIncomingProperties,
                        config.useOutgoingProperties);
            else
                return new PropertyClusterFactory(window, schemaCache, schemaGraph,
                        new HashSet<>(), new HashSet<>(), config.useSameAsInstances, config.useIncomingProperties,
                        config.useOutgoingProperties);
        } else if (element.getId() == FluidConstants.PROPERTYOBJECT_CLUSTER) {
            if (config.useTypeSets)
                return new PropertyObjectClusterFactory(window, schemaCache, schemaGraph,
                        rdfType, new HashSet<>(), config.useSameAsInstances, config.useIncomingProperties,
                        config.useOutgoingProperties, null);
            else if (config.useRelationSets)
                return new PropertyObjectClusterFactory(window, schemaCache, schemaGraph,
                        new HashSet<>(), rdfType, config.useSameAsInstances, config.useIncomingProperties,
                        config.useOutgoingProperties, null);
            else
                return new PropertyObjectClusterFactory(window, schemaCache, schemaGraph,
                        new HashSet<>(), new HashSet<>(), config.useSameAsInstances, config.useIncomingProperties,
                        config.useOutgoingProperties, null);
        } else
            logger.error("Unsupported Simple Schema Element: " + element);


        return null;
    }
}
