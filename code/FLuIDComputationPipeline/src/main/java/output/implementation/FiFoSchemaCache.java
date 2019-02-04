package output.implementation;

import common.interfaces.IResource;
import common.interfaces.ISchemaElement;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import output.interfaces.IElementListener;
import output.interfaces.IElementStore;

import java.util.*;

/**
 * A first-in-first-out (FIFO) window implementation for schema elements. Objects
 * are stored as long as the internal capacity is not exhausted. At that point,
 * the earliest added entry will be removed to clear some space for the new
 * entry. If the window is closed, all not yet removed entries will be removed
 * and listeners will be informed of the closing. Each removed entry will be
 * given to the registered listeners. Entries contain both the elements and
 * relations they have with other elements
 *
 * @author Blume Till
 */
public class FiFoSchemaCache implements IElementStore<ISchemaElement> {
    private static final Logger logger = LogManager.getLogger(FiFoSchemaCache.class.getSimpleName());
    private int loggingInterval = 1000;

    private Map<IResource, ISchemaElement> elements;
    private Queue<IResource> queue;

    private List<IElementListener> listeners;
    //maximum allowed number of elements
    private int capacity;
    boolean closed;

    //what was the maximums size during computation?
    private int maxSize = 0;

    /**
     * Constructor. Creates a window with the highest integer as capacity
     */
    public FiFoSchemaCache() {
        this(Integer.MAX_VALUE);
    }

    /**
     * Constructor. Creates a window with the given capacity
     *
     * @param capacity The capacity
     */
    public FiFoSchemaCache(int capacity) {
        this.capacity = capacity;
        elements = new HashMap<>();
        queue = new ArrayDeque<>();
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
        queue.remove(schemaElementLocator);
        elements.remove(schemaElementLocator);
        return true;
    }

    //       queue.parallelStream().limit(n).collect(Collectors.toList());

    @Override
    public List<ISchemaElement> removeLast(int n) {
        List<ISchemaElement> resultList = new LinkedList<>();
       while (n > 0 ) {
           resultList.add(elements.remove(queue.poll()));
            n--;
       }

        return resultList;
    }

    @Override
    public void add(ISchemaElement schemaElement) {
        IResource schemaElementLocator = schemaElement.getLocator();

        if (!contains(schemaElementLocator)) {
            int currentSize = size();
            maxSize = Math.max(maxSize, currentSize);
            if (capacity == Integer.MAX_VALUE && maxSize % loggingInterval == 0)
                logger.info("Adding to SC: " + currentSize + " / " + capacity);

            if (currentSize == capacity) {
                if (capacity == Integer.MAX_VALUE) {
                    logger.error("\nSchema Cache limit reached - Gold standard failed !");
                    System.exit(-1);
                }
                removeLast();
            }
            elements.put(schemaElementLocator, schemaElement);
            queue.add(schemaElementLocator);
        }
    }

    @Override
    public int size() {
        return queue.size();
    }

    @Override
    public void flush() {
        while (size() != 0)
            removeLast();
    }

    @Override
    public void close() {

        logger.info("CLOSE");
        //flush(); //TODO @Marius: when closing, do not flush the window, but implement a proper finished
        //method for the RDF4J Schema Element Store
        //Idea: create public method removeLast(int chunkSize) to retrieve a chunk of elements
        closed = true;
        for (IElementListener l : listeners)
            l.finished(this);
    }

    @Override
    public boolean isClosed() {
        return closed;
    }

    @Override
    public void registerCacheListener(IElementListener listener) {
        listeners.add(listener);
    }


    /////////////////////////////////////
    //////    PRIVATE FUNCTIONS   ///////
    /////////////////////////////////////

    private void notifyListeners(ISchemaElement schemaElement) {
        for (IElementListener l : listeners)
            l.elementEmitted(schemaElement); //TODO: double check if this works

    }

    private void removeLast() {
        IResource remove = queue.poll();
        ISchemaElement removeElement = elements.remove(remove);

        int currentSize = size();
        maxSize = Math.max(maxSize, currentSize);
        if (capacity == Integer.MAX_VALUE && maxSize % loggingInterval == 0)
            logger.info("Removing from SC: " + currentSize + " / " + capacity);

        notifyListeners(removeElement);
    }

    @Override
    public void elementEmitted(ISchemaElement el) {

    }

    @Override
    public void finished(IElementStore<ISchemaElement> emitter) {

    }
}
