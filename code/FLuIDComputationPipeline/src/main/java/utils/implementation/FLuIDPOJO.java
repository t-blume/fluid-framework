package utils.implementation;


import common.implemenation.NodeResource;
import common.implemenation.Quad;
import common.interfaces.IPayloadElement;
import common.interfaces.IQuint;
import common.interfaces.IResource;
import common.interfaces.ISchemaElement;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.sparql.syntax.ElementGroup;
import org.apache.jena.sparql.syntax.ElementOptional;
import org.apache.jena.sparql.syntax.ElementPathBlock;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.semanticweb.yars.nx.Resource;
import output.implementation.connectors.RDF4JConnector;
import processing.computation.implementation.payload.CountElement;
import processing.computation.implementation.payload.DatasourceElement;
import processing.computation.implementation.payload.SnippetElement;
import processing.computation.implementation.schema.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

import static utils.implementation.Constants.RDF_TYPE;
import static utils.implementation.FLuIDVocabulary.*;
/**
 * @version generated on Fri Jul 14 09:00:06 CEST 2017 by Blume Till
 */

public class FLuIDPOJO {
    private static final Logger logger = LogManager.getLogger(FLuIDPOJO.class.getName());



    public static void main(String[] args) throws URISyntaxException {


        Model model;

        Map<String, ISchemaElement> schemaElementMap = new HashMap<>();

        Set<IResource> contexts = new HashSet<>();
        contexts.add(new NodeResource(new Resource("http://example.com")));
        String locator = "hans";
        ObjectCluster schemaElement1 = new ObjectCluster();
        schemaElement1.addAttribute(new Link(null, new URI("http://test-object1.com")));
        schemaElement1.addAttribute(new Link(null, new URI("http://test-object2.com")));
        schemaElement1.addPayload(new DatasourceElement(contexts));

        schemaElementMap.put(locator, schemaElement1);

        System.out.println(schemaElement1);


        ((ObjectCluster) schemaElementMap.get(locator)).addAttribute(new Link(null, new URI("http://test-object3.com")));

        System.out.println(schemaElement1);

        List<IQuint> exported = exportSchemaElement(schemaElement1);
        exported.forEach(X -> System.out.println(X));

        System.out.println(importSchemaElement(exported));

        System.out.println(queryForSimpleSchemaElement(schemaElement1));


        RDF4JConnector connector = new RDF4JConnector("http://localhost:8080/rdf4j-server/", "testing");
        connector.clear();

        connector.addQuints(exported);

        List<IQuint> databaseQuints = connector.executeQuery(queryForSchemaElement(schemaElement1));
        List<IQuint> parsedQuints = new LinkedList<>();
        for (IQuint quint : databaseQuints) {
            parsedQuints.add(new Quad(schemaElement1.getLocator(), quint.getPredicate(), quint.getObject()));
        }


        System.out.println(importSchemaElement(parsedQuints));


    }

    public static Query queryForSchemaElement(ISchemaElement schemaElement) {
        if (!(schemaElement instanceof ComplexSchemaElement))
            return queryForSimpleSchemaElement((SimpleSchemaElement) schemaElement);
        else
            return queryForComplexSchemaElement((ComplexSchemaElement) schemaElement);
    }

    private static Query queryForComplexSchemaElement(ComplexSchemaElement schemaElement) {
        Query query = QueryFactory.create();
        query.setQuerySelectType();

        ElementGroup outputQueryPatternGroup = new ElementGroup();
        ElementPathBlock outputTriplePatternBlock = new ElementPathBlock();
        Node p = NodeFactory.createVariable("p"); //TODO make constants
        Node o = NodeFactory.createVariable("o");
        query.addResultVar(p);
        query.addResultVar(o);
        IResource schemaNode = createCSENode(schemaElement);
        IResource typeNode = CLASS_COMPLEX_SCHEMA_ELEMENT_RESOURCE;

        outputTriplePatternBlock.addTriple(new Triple(NodeFactory.createURI(schemaNode.toString()),
                NodeFactory.createURI(RDF_TYPE), NodeFactory.createURI(typeNode.toString())));
        outputTriplePatternBlock.addTriple(new Triple(NodeFactory.createURI(schemaNode.toString()), p, o));
        //TODO:
        outputQueryPatternGroup.addElement(outputTriplePatternBlock);
        query.setQueryPattern(outputQueryPatternGroup);
        return query;
    }

    private static Query queryForSimpleSchemaElement(SimpleSchemaElement schemaElement) {
        Query query = QueryFactory.create();
        query.setQuerySelectType();

        ElementGroup outputQueryPatternGroup = new ElementGroup();
        ElementPathBlock outputTriplePatternBlock = new ElementPathBlock();
        Node p = NodeFactory.createVariable("p"); //TODO make constants
        Node o = NodeFactory.createVariable("o");
        Node payload = NodeFactory.createVariable("PAY");
        Node payloadInfo = NodeFactory.createVariable("PAY_INFO");


        query.addResultVar(p);
        query.addResultVar(o);
        query.addResultVar(payloadInfo);
        IResource schemaNode = createSSENode(schemaElement);

        IResource typeNode = null;
        if (schemaElement instanceof ObjectCluster) {
            typeNode = CLASS_OBJECT_CLUSTER_RESOURCE;
        } else if (schemaElement instanceof PropertyCluster) {
            typeNode = CLASS_PROPERTY_CLUSTER_RESOURCE;
        } else if (schemaElement instanceof PropertyObjectCluster) {
            typeNode = CLASS_PROPERTY_OBJECT_CLUSTER_RESOURCE;
        } else
            logger.error("INVALID TYPE: " + schemaElement);

        //get all the schema information
        outputTriplePatternBlock.addTriple(new Triple(NodeFactory.createURI(schemaNode.toString()),
                NodeFactory.createURI(RDF_TYPE), NodeFactory.createURI(typeNode.toString())));
        outputTriplePatternBlock.addTriple(new Triple(NodeFactory.createURI(schemaNode.toString()), p, o));

        //get PAYLOAD information if attached
        ElementPathBlock optionalTriplePatternBlock = new ElementPathBlock();
        optionalTriplePatternBlock.addTriple(
                new Triple(NodeFactory.createURI(schemaNode.toString()),
                        NodeFactory.createURI(HAS_PAYLOAD), payload));

        optionalTriplePatternBlock.addTriple(
                new Triple(payload, NodeFactory.createURI(PAYLOAD), payloadInfo));

        //additional query for the actual PAYLOAD information?
        outputQueryPatternGroup.addElement(new ElementOptional(optionalTriplePatternBlock));

        outputQueryPatternGroup.addElement(outputTriplePatternBlock);
        query.setQueryPattern(outputQueryPatternGroup);
        return query;
    }

    /**
     * @param schemaElement
     * @return
     */
    public static List<IQuint> exportSchemaElement(ISchemaElement schemaElement) {
        if (!(schemaElement instanceof ComplexSchemaElement))
            return exportSimpleSchemaElement((SimpleSchemaElement) schemaElement);
        else
            return exportComplexSchemaElement((ComplexSchemaElement) schemaElement);
    }

    public static SchemaElement importSchemaElement(List<IQuint> quints) {
        if (quints == null || quints.isEmpty())
            return null;

        for (IQuint quint : quints) {
            if (quint.getPredicate().toString().equals(RDF_TYPE)) {
                Class elementClass = RESOURCE_CLASS_MAP.get(quint.getObject());
                if (elementClass.equals(ObjectCluster.class)) {
                    ObjectCluster objectCluster = new ObjectCluster();
                    importSimpleSchemaElement(objectCluster, quints);
                    return objectCluster;
                } else if (elementClass.equals(PropertyCluster.class)) {
                    PropertyCluster propertyCluster = new PropertyCluster();
                    importSimpleSchemaElement(propertyCluster, quints);
                    return propertyCluster;
                } else if (elementClass.equals(PropertyObjectCluster.class)) {
                    PropertyObjectCluster propertyObjectCluster = new PropertyObjectCluster();
                    importSimpleSchemaElement(propertyObjectCluster, quints);
                    return propertyObjectCluster;
                } else if (elementClass.equals(ComplexSchemaElement.class)) {
                    //TODO: implement
                    logger.error("Importing complex schema element");
                    return new PropertyObjectCluster();
                } else
                    logger.error("Is not a schema element!");
                return null;
            }
        }

        return null;//FIXME
    }


    public static List<IQuint> exportPayloadElement(IPayloadElement payloadElement) {
        List<IQuint> quints = new LinkedList<>();
        IResource payloadNode = createPayloadNode(payloadElement);

        if (payloadElement instanceof DatasourceElement) {
            quints.add(new Quad(payloadNode, new NodeResource(new Resource(RDF_TYPE)), CLASS_DATASOURCE_ELEMENT_RESOURCE));
            Set<IResource> contexts = ((DatasourceElement) payloadElement).contexts();
            for (IResource context : contexts) {
                quints.add(new Quad(payloadNode, PAYLOAD_RESOURCE,
                        new NodeResource(new Resource(context.toString()))));

            }
        } else {
            logger.error("No additional PAYLOAD elements supported in this version!");
        }

        return quints;
    }

    /**
     * @param cse
     * @return
     */
    public static List<IQuint> exportComplexSchemaElement(ComplexSchemaElement cse) {
        List<IQuint> quints = new LinkedList<>();
        IResource cseNode = createCSENode(cse);

        quints.add(new Quad(cseNode, new NodeResource(new Resource(RDF_TYPE)), CLASS_COMPLEX_SCHEMA_ELEMENT_RESOURCE));

//OPTIONAL
//        cse.subjectElements().values().forEach((SE) ->
//                quints.add(new Quad(cseNode, HAS_SUBJECT_EQUIVALENCE_RESOURCE,
//                        (SE instanceof SimpleSchemaElement ? createSSENode((SimpleSchemaElement) SE) :
//                                createCSENode((ComplexSchemaElement) SE)))));
//
//
//        cse.predicateElements().values().forEach((SE) ->
//                quints.add(new Quad(cseNode, HAS_PREDICATE_EQUIVALENCE_RESOURCE,
//                        (SE instanceof SimpleSchemaElement ? createSSENode((SimpleSchemaElement) SE) :
//                                createCSENode((ComplexSchemaElement) SE)))));
//
//        cse.objectElements().values().forEach((SE) ->
//                quints.add(new Quad(cseNode, HAS_OBJECT_EQUIVALENCE_RESOURCE,
//                        (SE instanceof SimpleSchemaElement ? createSSENode((SimpleSchemaElement) SE) :
//                                createCSENode((ComplexSchemaElement) SE)))));


        cse.getIsSubjectEquivalenceOfInternal().forEach((SE) ->
                quints.add(new Quad(cseNode, IS_SUBJECT_EQUIVALENCE_RESOURCE, createCSENode(SE))));

        cse.getIsPredicateEquivalenceOfInternal().forEach((SE) ->
                quints.add(new Quad(cseNode, IS_PREDICATE_EQUIVALENCE_RESOURCE, createCSENode(SE))));


        cse.getIsObjectEquivalenceOfInternal().forEach((SE) ->
                quints.add(new Quad(cseNode, IS_OBJECT_EQUIVALENCE_RESOURCE, createCSENode(SE))));

        if (cse.getPayload() != null) {
            for (IPayloadElement payloadElement : cse.getPayload()) {
                quints.add(new Quad(cseNode, HAS_PAYLOAD_RESOURCE,
                        createPayloadNode(payloadElement)));
                quints.addAll(exportPayloadElement(payloadElement));
            }
        }
        return quints;
    }


    /**
     * @param sse
     * @param quints
     */
    private static void importSimpleSchemaElement(SimpleSchemaElement sse, List<IQuint> quints) {
        if (quints == null || quints.isEmpty())
            return;

        IResource attributeNode = null;
        if (sse instanceof ObjectCluster) {
            attributeNode = GET_LINK_OBJECT_RESOURCE;
        } else if (sse instanceof PropertyCluster) {
            attributeNode = GET_PROPERTY_LINK_RESOURCE;
        } else if (sse instanceof PropertyObjectCluster) {
            attributeNode = null;//FIXME
        } else
            logger.error("INVALID TYPE: " + sse);

//        for (IQuint quint : quints) {
////            if (quint.getPredicate().equals(RDF_TYPE_NODE))
////                continue;//we already know that
////
////                //read in the attribiutes
////            else if (quint.getPredicate().equals(attributeNode)) {
////                if (!(sse instanceof PropertyObjectCluster)) {
////                    sse.addAttribute(quint.getObject().toString());
////
////                } else {
////                    sse.addAttribute(quint.getPredicate().toString() + PropertyObjectClusterFactory.DELIMITER +
////                            quint.getObject().toString().replace(CLASS_OBJECTCLUSTER.getIRI() + "/", ""));
//                    //TODO FIXME PROPER reuse of functions
//                }
//            }else if(quint.getPredicate().equals(DATA_PROPERTY_PAYLOAD_RESOURCE)){
//                //add PAYLOAD
//                System.out.println(quint.getObject());
//            }
//        }
    }

    /**
     * @param sse
     * @return
     */
    public static List<IQuint> exportSimpleSchemaElement(SimpleSchemaElement sse) {
        IResource sseNode = createSSENode(sse);
        List<IQuint> quints = new LinkedList<>();

        IResource typeNode = null;
        IResource attributeNode = null;
        if (sse instanceof ObjectCluster) {
            typeNode = CLASS_OBJECT_CLUSTER_RESOURCE;
            attributeNode = GET_LINK_OBJECT_RESOURCE;
        } else if (sse instanceof PropertyCluster) {
            typeNode = CLASS_PROPERTY_CLUSTER_RESOURCE;
            attributeNode = GET_PROPERTY_LINK_RESOURCE;
        } else if (sse instanceof PropertyObjectCluster) {
            typeNode = CLASS_PROPERTY_OBJECT_CLUSTER_RESOURCE;
            attributeNode = null; //FIXME
        } else
            logger.error("INVALID TYPE: " + sse);


        //locator -> RDF_TYPE -> SchemaElement Type
        quints.add(new Quad(sseNode, new NodeResource(new Resource(RDF_TYPE)), typeNode));

        if (!(sse instanceof PropertyObjectCluster)) {
            //these special elements have only dummy attributes
            if (!sse.equals(ObjectCluster.OC_EMPTY) && !sse.equals(PropertyCluster.PC_EMPTY)) {
                // Write out the attributes
//                for (URI s : sse.getAttributes())
//                    quints.add(new Quad(sseNode, attributeNode, new NodeResource(new Resource(s))));
            }

        } else {
//            for (URI s : sse.getAttributes()) {
//                String[] split = s.split(PropertyObjectClusterFactory.DELIMITER);
//                //TODO FIXME PROPER reuse of functions
//                if (split.length > 1)
//                    quints.add(new Quad(sseNode, new NodeResource(new Resource(split[0])),
//                            new NodeResource(new Resource(CLASS_OBJECTCLUSTER.getIRI().toString() + "/" + split[1]))));
//            }
        }

        Collection<ComplexSchemaElement> schemaElements = sse.getIsSubjectEquivalenceOfInternal();
        Iterator<ComplexSchemaElement> schemaIterator = schemaElements.iterator();
        while (schemaIterator.hasNext()) {
            ComplexSchemaElement parentSchemaElement = schemaIterator.next();
            quints.add(new Quad(sseNode, IS_SUBJECT_EQUIVALENCE_RESOURCE,
                    createCSENode(parentSchemaElement)));

        }

        if (sse.getPayload() != null) {
//            logger.error("PAYLOADNULL!!: ");

            for (IPayloadElement payloadElement : sse.getPayload()) {
                quints.add(new Quad(sseNode, HAS_PAYLOAD_RESOURCE,
                        createPayloadNode(payloadElement)));
                quints.addAll(exportPayloadElement(payloadElement));
            }
        }

        return quints;
    }


    /**
     * @param cse
     * @return
     */
    private static IResource createCSENode(ComplexSchemaElement cse) {
        StringBuilder sb = new StringBuilder();
        sb.append(CLASS_COMPLEX_SCHEMA_ELEMENT);
        sb.append(cse.getLocator().toString());
        return new NodeResource(new Resource(sb.toString()));
    }

    /**
     * @param sse
     * @return
     */
    private static IResource createSSENode(SimpleSchemaElement sse) {
        StringBuilder sb = new StringBuilder();
        if (sse instanceof PropertyCluster) {
            if (sse.equals(PropertyCluster.PC_EMPTY))
                return EMPTY_PROPERTIES_RESOURCE;

            sb.append(CLASS_PROPERTY_CLUSTER);
        } else if (sse instanceof PropertyObjectCluster) {
            if (sse.equals(PropertyObjectCluster.POC_EMPTY))
                return EMPTY_PROPERTY_OBJECTS_RESOURCE;

            sb.append(CLASS_PROPERTY_OBJECT_CLUSTER);
        } else if (sse instanceof ObjectCluster) {
            if (sse.equals(ObjectCluster.OC_EMPTY))
                return EMPTY_OBJECTS_RESOURCE;

            sb.append(CLASS_OBJECT_CLUSTER);
        }

        sb.append("/");
        sb.append(sse.getLocator().toString());
        return new NodeResource(new Resource(sb.toString()));
    }

    /**
     * @param payloadElement
     * @return
     */
    private static IResource createPayloadNode(IPayloadElement payloadElement) {
        StringBuilder sb = new StringBuilder();
        if (payloadElement instanceof DatasourceElement)
            sb.append(CLASS_DATASOURCE_ELEMENT);
        else if (payloadElement instanceof CountElement)
            sb.append(CLASS_COUNT_ELEMENT);
        else if (payloadElement instanceof SnippetElement)
            sb.append(CLASS_SNIPPET_ELEMENT);

        sb.append("/");
        sb.append(payloadElement.getLocator().toString());
        return new NodeResource(new Resource(sb.toString()));
    }

}
