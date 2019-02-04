package processing.preprocessing.implementation;

import common.interfaces.IQuint;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import processing.preprocessing.interfaces.IQuintProcessor;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * A filter for quads, which won't forward any quints which contain a predicate
 * matching a filter string
 *
 * @author Bastian
 * @author Blume Till
 */
public class PropertyFilter implements IQuintProcessor {
    private static final Logger logger = LogManager.getLogger(PropertyFilter.class.getSimpleName());

    private Set<String> filters;
    private int count = 0;

    /**
     * Contructor
     *
     * @param filters The strings by which the quints' predicates will be filtered
     */
    public PropertyFilter(Set<String> filters) {
        this.filters = filters;
    }

    @Override
    public List<IQuint> processQuint(IQuint q) {
        List<IQuint> l = new ArrayList<>();
        //if it is not in there, return it
        if (!filters.contains(q.getPredicate().toString()))
            l.add(q);
        else
            count++;

        return l;

    }

    @Override
    public void finished() {
        logger.info("Filtered " + count + " statements!");
    }


    @Override
    public String toString() {
        return "PropertyFilter{" +
                "filters=" + filters +
                '}';
    }
}
