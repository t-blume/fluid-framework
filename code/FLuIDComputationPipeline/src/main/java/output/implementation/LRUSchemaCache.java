package output.implementation;

import common.interfaces.IResource;
import common.interfaces.ISchemaElement;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import output.interfaces.IElementListener;
import output.interfaces.IElementStore;
import utils.implementation.LRUCache;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by bastian on 02.02.17.
 */
public class LRUSchemaCache implements IElementStore<ISchemaElement> {
    private static final Logger logger = LogManager.getLogger(LRUSchemaCache.class.getSimpleName());

    private LRUCache<IResource, ISchemaElement> elements;

    private List<IElementListener<ISchemaElement>> listeners;
    private int capacity;
    boolean closed;

    private int maxSize = 0;


    /**
     * Constructor. Creates a window with the given capacity
     *
     * @param capacity The capacity
     */
    public LRUSchemaCache(int capacity) {
        this.capacity = capacity;
        // this capacity initialises a hash map with capacity many buckets, which are probably implemented via arrays
        // so if you use a too big capacity it creates an out of memory exception, because too much is initialised
        // https://git.kd.informatik.uni-kiel.de/kd-group/fluid-framework/issues/4 for detailed explanation
        elements = new LRUCache<>(capacity, 0.75f);
        listeners = new ArrayList<>();
        closed = false;
    }

    @Override
    public boolean contains(IResource locator) {
        return elements.containsKey(locator);
    }

    @Override
    public ISchemaElement getSchemaElement(IResource schemaElementLocator, Class<? extends ISchemaElement> schemaElementType) {
        return elements.get(schemaElementLocator);
    }

    @Override
    public boolean removeElement(IResource schemaElementLocator, Class<? extends ISchemaElement> schemaElementType) {
        elements.remove(schemaElementLocator);
        return true;
    }

    @Override
    public Collection<ISchemaElement> removeLast(int n) {
        //if its all, then fast dump everything
        if(n == size()){
            Collection<ISchemaElement> eldest = new LinkedList<>();
            eldest.addAll(elements.values());
            elements.clear();
            return eldest;
        }

        //otherwise, iterate over values to retrieve them
        List<ISchemaElement> eldest = elements.values().stream().limit(n).collect(Collectors.toCollection(LinkedList::new));

        //then iterate again to delete
        eldest.forEach(SE -> {
            ISchemaElement schemaElement = elements.remove(SE.getLocator());
            if (schemaElement == null)
                throw new IllegalStateException("Could not remove the least recently used element from LRU Schema Cache");
            int currentSize = size();
            maxSize = Math.max(maxSize, currentSize);
            if (capacity > 100000 && maxSize % 1000 == 0)
                logger.info("\t\t\t\t\t\t\t\t\t\t\tSC: %08d / %08d\r", currentSize, maxSize);
        });
        return eldest;
}

    @Override
    public void add(ISchemaElement schemaElement) {
        IResource schemaElementLocator = schemaElement.getLocator();
        if (!contains(schemaElementLocator)) {
            int currentSize = size();
            maxSize = Math.max(maxSize, currentSize);
            if (capacity == Integer.MAX_VALUE && maxSize % 1000 == 0)
                logger.info("\t\t\t\t\t\t\t\t\t\t\tSC: %08d / %08d\r", currentSize, maxSize);

            if (currentSize == capacity)
                removeLast();

            elements.put(schemaElementLocator, schemaElement);
        }

//        if (schemaElement instanceof ComplexSchemaElement) {
//            //get sub elements
//            ComplexSchemaElement complexSchemaElement = (ComplexSchemaElement) schemaElement;
//            //<factoryHash, SchemaElement>
//            //Call function recursively for each subject element
//            Map<Integer, SchemaElement> subjectElements = complexSchemaElement.subjectElements();
//            subjectElements.values().forEach(X -> add(X));
//
//            //Call function recursively for each object element
//            Map<Integer, SchemaElement> objectElements = complexSchemaElement.objectElements();
//            objectElements.values().forEach(X -> add(X));
//
//            //Call function recursively for each predicate element
//            Map<Integer, SchemaElement> predicateElements = complexSchemaElement.predicateElements();
//            predicateElements.values().forEach(X -> add(X));
//        }
    }


    @Override
    public int size() {
        return elements.size();
    }

    private void removeLast() {
        Map.Entry<IResource, ISchemaElement> removeElement = elements.getEldestEntry();
        boolean success = elements.remove(removeElement.getKey(), removeElement.getValue());
        if (!success)
            throw new IllegalStateException("Could not remove the least recently used element from LRU Schema Cache");

        int currentSize = size();
        maxSize = Math.max(maxSize, currentSize);
        if (capacity > 100000 && maxSize % 1000 == 0)
            logger.info("\t\t\t\t\t\t\t\t\t\t\tSC: %08d / %08d\r", currentSize, maxSize);

        notifyListeners(removeElement.getValue());
    }


    private void notifyListeners(ISchemaElement schemaElement) {
        for (IElementListener l : listeners)
            l.elementEmitted(schemaElement);
    }


    @Override
    public void flush() {
        while (!elements.isEmpty()) {
            Map.Entry<IResource, ISchemaElement> eldestEntry = elements.entrySet().iterator().next();
            ISchemaElement element = eldestEntry.getValue();
            boolean success = elements.remove(eldestEntry.getKey(), element);
            if (!success)
                throw new IllegalStateException("Could not remove the eldest element from LRU Schema Cache");

            notifyListeners(element);
        }
    }

    @Override
    public void registerCacheListener(IElementListener listener) {
        listeners.add(listener);
    }


    @Override
    public void close() {
        //flush();
        closed = true;
        for (IElementListener l : listeners)
            l.finished(this);

        logger.info("Maximum window size: " + maxSize);
    }

    @Override
    public boolean isClosed() {
        return closed;
    }


    @Override
    public void elementEmitted(ISchemaElement el) {

    }

    @Override
    public void finished(IElementStore<ISchemaElement> emitter) {

    }
}
