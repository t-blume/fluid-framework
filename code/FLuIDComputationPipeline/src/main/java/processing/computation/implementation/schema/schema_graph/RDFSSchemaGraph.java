package processing.computation.implementation.schema.schema_graph;


import common.interfaces.IInstanceElement;
import common.interfaces.IQuint;
import common.interfaces.ISchemaGraph;
import utils.implementation.OptimizerCache;

import java.io.PrintStream;
import java.util.*;

import static utils.implementation.Constants.*;


/**
 * TODO: INCLUDE INCOMING PROPERTIES
 *
 * @author Blume Till
 */
public class RDFSSchemaGraph implements ISchemaGraph {

    private HashMap<String, PropertyNode> schemaPropertyHashMap;
    private HashMap<String, TypeNode> schemaTypeHashMap;

    private HashMap<String, Set<String>> recursiveCache;
    private OptimizerCache<String> cacheUses;


    boolean trackStatistics = true;
    //statistics
    private int cacheFlushes = 0;
    private List<Integer> superPropertiesAdded = new LinkedList<>();
    private List<Integer> superTypesAdded = new LinkedList<>();


    private int cacheHits = 0;
    private int cacheMisses = 0;

    public RDFSSchemaGraph() {
        schemaPropertyHashMap = new HashMap<>();
        schemaTypeHashMap = new HashMap<>();
        recursiveCache = new HashMap<>();
        cacheUses = new OptimizerCache<>(5000); //FIXME: proper estimate of size
    }


    public void printStatistics(PrintStream printStream) {
        printStream.println("==== Schema Graph Statistics ====");
        printStream.println("Property count: " + schemaPropertyHashMap.size());
        printStream.println("Property Inferences: " + superPropertiesAdded.size());

        int totalPropsInferred = 0;
        for (Integer x : superPropertiesAdded)
            totalPropsInferred += x;

        printStream.println("Total Properties Inferred: " + totalPropsInferred);
        printStream.println("Average Properties Inferred: " + (double) totalPropsInferred / (double) superPropertiesAdded.size());

        printStream.println("Type count: " + schemaTypeHashMap.size());
        printStream.println("Type Inferences: " + superTypesAdded.size());

        int totalTypesInferred = 0;
        for (Integer x : superTypesAdded)
            totalTypesInferred += x;

        printStream.println("Total Types Inferred: " + totalTypesInferred);
        printStream.println("Average Types Inferred: " + (double) totalTypesInferred / (double) superTypesAdded.size());

        printStream.println("== Cache-stats ==");
        printStream.println("Cache Hits: " + cacheHits);
        printStream.println("Cache flushes: " + cacheFlushes);
        printStream.println("--------------------------");

    }

    /**
     * Return a set of inferable TypeInformation for the given instance
     * To ensure maximal inference, infer all properties before calling
     * this method
     *
     * @param instance
     * @return
     */
    @Override
    public Set<String> inferSubjectTypes(IInstanceElement instance) {
        if (instance == null)
            return null;

        Set<String> tmpTypes = new HashSet<>();
        //outgoing quints
        for (IQuint quint : instance.getOutgoingQuints()) {
            //add types directly
            if (quint.getPredicate().toString().equals(RDF_TYPE))
                tmpTypes.add(quint.getObject().toString());
            else {
                PropertyNode tmpNode = schemaPropertyHashMap.get(quint.getPredicate().toString());
                if (tmpNode != null) {
                    //if we have domain information, add them as types
                    //TODO: Invalid use of domain/range check? e.g. inferred type is no type?
                    tmpTypes.addAll(tmpNode.getDomain());
                }
            }
        }
//        //incoming quints
//        for (IQuint quint : instance.getIncomingQuints()) {
//            if (quint.getPredicate().toString().equals(RDF_TYPE))
//                continue;
//            else {
//                PropertyNode tmpNode = schemaPropertyHashMap.get(quint.getPredicate().toString());
//                if (tmpNode != null) {
//                    //if we have domain information, add them as types
//                    //TODO: Invalid use of domain/range check? e.g. inferred type is no type?
//                    tmpTypes.addAll(tmpNode.getRange());
//                }
//            }
//        }
        return inferTypesFromTypeGraph(tmpTypes);
    }


    /**
     * Return a set of inferrable properties for the given instance
     *
     * @param instance
     * @return
     */
    @Override
    public HashMap<String, Set<String>> inferProperties(IInstanceElement instance) {
        if (instance == null)
            return null;

        Set<String> instanceProperties = new HashSet<>();
        for (IQuint quint : instance.getOutgoingQuints()) {
            //add non-type properties
            if (!quint.getPredicate().toString().equals(RDF_TYPE))
                instanceProperties.add(quint.getPredicate().toString());
        }

        HashMap<String, Set<String>> inferrableProperties = new HashMap<>();
        //use type graph to infer additional types based on all currently known types
        instanceProperties.forEach(PROP -> {
            Set<String> superProperties;
            Set<String> newProps = getCache(PROP);
            if (newProps == null) {
                PropertyNode tmp = schemaPropertyHashMap.get(PROP);
                //if present, add all connected SuperProperties
                if (tmp != null && !(superProperties = tmp.getSubPropertyOf()).isEmpty()) {
                    newProps = superProperties;
                    if (trackStatistics)
                        superPropertiesAdded.add(newProps.size());
                    addCache(PROP, newProps);
                }

            }
            if (newProps != null)
                inferrableProperties.put(PROP, newProps);
        });
        return inferrableProperties;
    }

    /**
     * Return a set of inferable TypeInformation about the object
     * for the given statement
     *
     * @param quint
     * @return
     */
    @Override
    public Set<String> inferObjectTypes(IQuint quint) {
        if (quint == null)
            return null;
        //get range information
        PropertyNode tmp = schemaPropertyHashMap.get(quint.getPredicate().toString());
        if (tmp != null) {
            Set<String> tmpTypes = tmp.getRange();
            return inferTypesFromTypeGraph(tmpTypes);
        } else
            return new HashSet<>();
    }


    /**
     * Add a RDFS statement to SchemaGraph.
     *
     * @param schemaStatement
     * @return
     */
    @Override
    public boolean add(IQuint schemaStatement) {
        if (schemaStatement == null)
            return false;

        clearCache(); //window possible not up to date //FIXME make efficient
        switch (schemaStatement.getPredicate().toString()) {
            case RDFS_DOMAIN: {
                //Inferable Knowledge: A Instance having the subject of this
                //statement as property, has also the object of this statement as type
                //create entry

                //if object-property does not exist, create it
                if (!schemaPropertyHashMap.containsKey(schemaStatement.getObject().toString()))
                    schemaPropertyHashMap.put(schemaStatement.getObject().toString(),
                            new PropertyNode(schemaStatement.getObject().toString()));

                //if subject-property does not exist, create it
                if (!schemaPropertyHashMap.containsKey(schemaStatement.getSubject().toString()))
                    schemaPropertyHashMap.put(schemaStatement.getSubject().toString(),
                            new PropertyNode(schemaStatement.getSubject().toString()));

                //create link between them both
                schemaPropertyHashMap.get(schemaStatement.getSubject().toString())
                        .addDomain(schemaPropertyHashMap.get(schemaStatement.getObject().toString()));
                return true;
            }
            case RDFS_RANGE: {
                //Inferable Knowledge: A Instance having the subject of this
                //statement as property, the corresponding object TypeCluster also
                //has the object of this statement as type

                //if object-property does not exist, create it
                if (!schemaPropertyHashMap.containsKey(schemaStatement.getObject().toString()))
                    schemaPropertyHashMap.put(schemaStatement.getObject().toString(),
                            new PropertyNode(schemaStatement.getObject().toString()));

                //if subject-property does not exist, create it
                if (!schemaPropertyHashMap.containsKey(schemaStatement.getSubject().toString()))
                    schemaPropertyHashMap.put(schemaStatement.getSubject().toString(),
                            new PropertyNode(schemaStatement.getSubject().toString()));

                //create link between them both
                schemaPropertyHashMap.get(schemaStatement.getSubject().toString())
                        .addRange(schemaPropertyHashMap.get(schemaStatement.getObject().toString()));
                return true;
            }
            case RDFS_SUBCLASSOF: {
                //Inferable Knowledge: A Instance having the subject of this
                //statement as type, has also the object of this statement as type

                //if object-property does not exist, create it
                if (!schemaTypeHashMap.containsKey(schemaStatement.getObject().toString()))
                    schemaTypeHashMap.put(schemaStatement.getObject().toString(),
                            new TypeNode(schemaStatement.getObject().toString()));

                //if subject-property does not exist, create it
                if (!schemaTypeHashMap.containsKey(schemaStatement.getSubject().toString()))
                    schemaTypeHashMap.put(schemaStatement.getSubject().toString(),
                            new TypeNode(schemaStatement.getSubject().toString()));

                //create link between them both
                schemaTypeHashMap.get(schemaStatement.getSubject().toString())
                        .addSubClassOf(schemaTypeHashMap.get(schemaStatement.getObject().toString()));

                return true;
            }
            case RDFS_SUBPROPERTYOF: {
                //Inferable Knowledge: A Instance having the subject of this
                //statement as property, has also the object of this statement as property
                //if object-property does not exist, create it
                if (!schemaPropertyHashMap.containsKey(schemaStatement.getObject().toString()))
                    schemaPropertyHashMap.put(schemaStatement.getObject().toString(),
                            new PropertyNode(schemaStatement.getObject().toString()));

                //if subject-property does not exist, create it
                if (!schemaPropertyHashMap.containsKey(schemaStatement.getSubject().toString()))
                    schemaPropertyHashMap.put(schemaStatement.getSubject().toString(),
                            new PropertyNode(schemaStatement.getSubject().toString()));

                //create link between them both
                schemaPropertyHashMap.get(schemaStatement.getSubject().toString())
                        .addSubPropertyOf(schemaPropertyHashMap.get(schemaStatement.getObject().toString()));
                return true;
            }
            default: {
                //("Ups: " + schemaStatement.getPredicate().toString());
                return false;
            }
        }
    }


    private Set<String> getCache(String term) {
        Set<String> newTerms = recursiveCache.get(term);

        if (trackStatistics && newTerms != null)
            cacheHits++;

        if (newTerms != null)
            cacheUses.incrementalMerge(term);
        return newTerms;
    }

    private void addCache(String term, Set<String> terms) {
        recursiveCache.put(term, terms);
        String removedKey = cacheUses.add(term, 1);
        if (removedKey != null)
            recursiveCache.remove(removedKey);
    }

    private void clearCache() {
        recursiveCache = new HashMap<>();
        cacheUses = new OptimizerCache<>(cacheUses.getCapacity());
        cacheFlushes++;
    }

    private Set<String> inferTypesFromTypeGraph(Set<String> types) {
        Set<String> allTypes = new HashSet<>();
        //use type graph to infer additional types based on all currently known types
        types.forEach(TYPE -> {
            Set<String> newTypes = getCache(TYPE);
            if (newTypes == null) {
                TypeNode tmp = schemaTypeHashMap.get(TYPE);
                //if present, add all connected SuperClasses
                if (tmp != null) {
                    newTypes = tmp.getSubClassOf();
                    if (trackStatistics)
                        superTypesAdded.add(newTypes.size());
                    addCache(TYPE, newTypes);
                }
            }
            if (newTypes != null)
                allTypes.addAll(newTypes);
            //add type itself anyway
            allTypes.add(TYPE);
        });
        return allTypes;
    }

    @Override
    public String toString() {
        return "RDFSSchemaGraph{" +
                "schemaPropertyHashMap:" + schemaPropertyHashMap.size() +
                ", schemaTypeHashMap:" + schemaTypeHashMap.size() +
                '}';
    }
}
