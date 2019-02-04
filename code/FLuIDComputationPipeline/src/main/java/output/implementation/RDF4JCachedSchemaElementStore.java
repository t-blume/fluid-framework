package output.implementation;

import common.interfaces.IResource;
import common.interfaces.ISchemaElement;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import output.interfaces.IElementListener;
import output.interfaces.IElementStore;

import java.util.List;

public class RDF4JCachedSchemaElementStore implements IElementStore<ISchemaElement> {

    private static final Logger logger = LogManager.getLogger(RDF4JCachedSchemaElementStore.class.getSimpleName());

    private final RDF4JSchemaElementStore tripleStore;
    private final LRUSchemaCache cache;
    private boolean closed = false;

    /**
     * cache size not maximal, heap space errors
     * @param url
     * @param repository
     * @param cacheSize
     */
    public RDF4JCachedSchemaElementStore(String url, String repository, int cacheSize) {
        tripleStore = new RDF4JSchemaElementStore(url, repository);
        cache = new LRUSchemaCache(cacheSize);
        cache.registerCacheListener(tripleStore);
    }

    @Override
    public boolean contains(IResource locator) {
        if (cache.contains(locator))
            return true;
        else if (tripleStore.contains(locator))
            return true;

        return false;
    }

    @Override
    public ISchemaElement getSchemaElement(IResource elementLocator, Class<? extends ISchemaElement> schemaElementType) {
        if (cache.contains(elementLocator))
            return cache.getSchemaElement(elementLocator, schemaElementType);
        else {
            ISchemaElement element = tripleStore.getSchemaElement(elementLocator, schemaElementType);
            if (element != null)
                cache.add(element);
            return element;
        }
    }

    @Override
    public boolean removeElement(IResource elementLocator, Class<? extends ISchemaElement> elementType) {
        if (cache.contains(elementLocator))
            cache.removeElement(elementLocator, elementType);

        if (tripleStore.contains(elementLocator))
            tripleStore.removeElement(elementLocator, elementType);

        return true;
    }

    @Override
    public List<ISchemaElement> removeLast(int n) {
        logger.warn("removeLast(int n) unsupported!");
        return null;
    }

    @Override
    public void add(ISchemaElement element) {
        cache.add(element);
    }

    @Override
    public int size() {
        return cache.size();
    }

    @Override
    public void flush() {
        cache.flush();
        tripleStore.flush();
    }

    @Override
    public void close() {
        cache.close();
        tripleStore.close();
        closed = true;
    }

    @Override
    public boolean isClosed() {
        return closed;
    }

    @Override
    public void registerCacheListener(IElementListener<ISchemaElement> listener) {

    }


    @Override
    public void elementEmitted(ISchemaElement el) {
        add(el);
    }

    @Override
    public void finished(IElementStore<ISchemaElement> emitter) {
        logger.debug("Flushing window into triple store, " + cache.size() + " elements to go.");
        cache.flush();
        logger.debug("Flushing finished!");
    }
}
