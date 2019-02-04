package utils.implementation;



import utils.interfaces.IElementCacheListener;

import java.util.*;

/**
 * Collect Term frequencies. Caching strategy: Mixed LRU + FIFO
 * Keep most used terms in window, when there are multiple candidates for flushing,
 * use oldest.
 *
 * TODO: Generic Value
 * Created by Blume Till on 05.08.2016.
 */
public class OptimizerCache<K>{
    public static void main(String[] args){
        OptimizerCache<String> cache = new OptimizerCache<>();
        cache.incrementalMerge("key1");
        cache.incrementalMerge("key2");
        cache.incrementalMerge("key1");
        cache.incrementalMerge("key4");
        cache.incrementalMerge("key3");
        cache.incrementalMerge("key3");
        cache.incrementalMerge("key5");
        cache.incrementalMerge("key1");
        System.out.println("----");
        for (Map.Entry<String, Integer> entry : cache.entrySet()) {
            System.out.println(entry.getKey() + ";" + entry.getValue());
        }
        System.out.println("----");
    }

    //"normal" caching structure
    private Map<K, Integer> elements;

    //Value based caching buckets which keep track of insertion order
    private ValueCache<Integer,K> valueCache;

    private int capacity;

    private List<IElementCacheListener> listeners;


    /**
     * Constructor. Creates a window with the highest integer as capacity
     */
    public OptimizerCache() {
        this(Integer.MAX_VALUE);
    }

    /**
     * Constructor. Creates a window with the given capacity
     *
     * @param capacity
     *            The capacity
     */
    public OptimizerCache(int capacity) {
        elements = new HashMap<>();
        valueCache = new ValueCache();
        this.capacity = capacity;
        listeners = new ArrayList<>();
    }

    /**
     * Calling this function shall not affect the LRU mechanism and therefore is not
     * performed on the valueBucket variable.
     *
     * @param key
     *            The instance to be checked for
     * @return
     */
    public boolean contains(K key) {
        return elements.containsKey(key);
    }


    /**
     * Calling this function shall not affect the LRU mechanism and therefore is not
     * performed on the valueBucket variable.
     *
     * @param key
     *            The instance's locator
     * @return
     */
    public Integer get(K key) {
        return elements.get(key);
    }

    public int size() {
        return elements.size();
    }

    public K add(K key, Integer value) {
        K removedKey = null;
        if (!contains(key)) {
            int currentSize = size();
            // If the capacity is full -> remove first entry added from the lowest bucket
            if (currentSize == capacity)
                removedKey = removeLowestValueFirstAdded();

        }
        elements.put(key, value);
        valueCache.add(value, key);
        return removedKey;
    }


    public K getHighest(){
        return valueCache.getHighest();
    }

    /**
     * current key-value pair
     *
     * @param key
     */
    public K incrementalMerge(K key) {
        //if it is new pair, add it plainly
        K removedKey = null;
        if (!contains(key)) {
            int currentSize = size();
            // If the capacity is full -> remove first entry added from the lowest bucket
            if (currentSize == capacity)
                removedKey = removeLowestValueFirstAdded();
            elements.put(key, 1);
            valueCache.add(1, key);
        }else{
            //else lookup old value
            Integer value = elements.get(key);
            //increment value
            elements.put(key, value + 1);
            //move key one bucket further
            valueCache.remove(value, key);
            valueCache.add(value + 1, key);
        }
        return removedKey;
    }

    private K removeLowestValueFirstAdded(){
        K key = valueCache.removeLowestEldest();
        elements.remove(key);
        return key;
    }


    private void notifyListeners(StatEntry el) {

        for (IElementCacheListener<StatEntry> l : listeners) {
            l.elementFlushed(el);
        }
    }

    public void flush() {
        while (size() != 0) {
            removeLowestValueFirstAdded();
        }
    }

    public void registerCacheListener(IElementCacheListener<StatEntry> listener) {
        listeners.add(listener);

    }

    public void close() {
        flush();
        for (IElementCacheListener<StatEntry> l : listeners) {
            l.finished();
        }
    }
    public int getCapacity(){
        return capacity;
    }
    public Set<Map.Entry<K, Integer>> entrySet(){
        return valueCache.entrySet();
    }
}
