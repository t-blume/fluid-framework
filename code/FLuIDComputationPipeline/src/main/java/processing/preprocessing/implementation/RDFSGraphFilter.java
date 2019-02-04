package processing.preprocessing.implementation;

import common.interfaces.IQuint;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import processing.computation.implementation.schema.schema_graph.RDFSSchemaGraph;
import processing.preprocessing.interfaces.IQuintProcessor;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by Blume Till on 16.11.2016.
 */
public class RDFSGraphFilter implements IQuintProcessor {
    private static final Logger logger = LogManager.getLogger(RDFSGraphFilter.class.getSimpleName());

    private RDFSSchemaGraph schemaGraph;
    private Set<String> filterProperties;

    private int count = 0;


    public RDFSSchemaGraph getSchemaGraph() {
        return schemaGraph;
    }

    public RDFSGraphFilter(Set<String> filterProperties) {
        this.schemaGraph = new RDFSSchemaGraph();
        this.filterProperties = filterProperties;
    }

    @Override
    public List<IQuint> processQuint(IQuint q) {
        List<IQuint> l = new ArrayList<>();
        //filter like PropertyFilter
        if (!filterProperties.contains(q.getPredicate().toString()))
            l.add(q);
        else {
            //but add filtered to the Schema Graph
            schemaGraph.add(q);
            count++;
        }
        return l;
    }

    @Override
    public void finished() {
        logger.info("Filtered " + count + " statements!");
    }

    @Override
    public String toString() {
        return "RDFSGraphFilter{" +
                "schemaGraph=" + schemaGraph +
                ", filterProperties=" + filterProperties +
                '}';
    }
}
