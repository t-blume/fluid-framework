package utils.implementation;

import java.util.*;

/**
 * TODO: maybe generic implementation including interface definition
 *
 * Created by Blume Till on 08.08.2016.
 */
public class ValueCache<V extends Comparable, K> {


    private Map<V, LinkedHashSet<K>> valueBuckets;


    public ValueCache(){
        valueBuckets = new HashMap<>();
    }
    public void add(V value, K key){
        if(valueBuckets.containsKey(value))
            valueBuckets.get(value).add(key);
        else{
            valueBuckets.put(value, new LinkedHashSet<>());
            valueBuckets.get(value).add(key);
        }
    }
    public boolean remove(V value, K key){
        if(valueBuckets.containsKey(value)){
            if(safeRemoveFromBucket(valueBuckets.get(value), key))
                valueBuckets.remove(value);

            return true;
        }
        else
            return false;
    }
    public K removeLowestEldest(){
        Map.Entry<V, LinkedHashSet<K>> lowest = null;
        for(Map.Entry<V, LinkedHashSet<K>> entry : valueBuckets.entrySet()){
            if (lowest == null || entry.getKey().compareTo(lowest.getKey()) < 0)
                lowest = entry;
        }
        if(lowest == null)
            return null;

        K key = lowest.getValue().iterator().next();
        // if bucket is empty, delete it (garbage collection)
        if(safeRemoveFromBucket(lowest.getValue(), key))
            valueBuckets.remove(lowest.getKey());
        //lowest.getValue().remove(key);
        return key;
    }

    public K getHighest(){
        Map.Entry<V, LinkedHashSet<K>> highest = null;
        for(Map.Entry<V, LinkedHashSet<K>> entry : valueBuckets.entrySet()){
            if (highest == null || entry.getKey().compareTo(highest.getKey()) > 0)
                highest = entry;
        }
        if(highest == null)
            return null;

        K key = highest.getValue().iterator().next();
        return key;
    }

    /**
     * bucket === empty
     *
     * @param bucket
     * @param key
     * @return
     */
    private boolean safeRemoveFromBucket(LinkedHashSet<K> bucket, K key){
        if(bucket == null)
            return false;
        bucket.remove(key);
        if (bucket.isEmpty())
            return true;
        else
            return false;
    }

    public Set<Map.Entry<K, V>> entrySet(){
        Set<Map.Entry<K, V>> reultSet = new HashSet<>();
        for(Map.Entry<V, LinkedHashSet<K>> entry : valueBuckets.entrySet()){
            entry.getValue().forEach(X -> reultSet.add(new Map.Entry<K, V>() {

                @Override
                public K getKey() {
                    return X;
                }

                @Override
                public V getValue() {
                    return entry.getKey();
                }

                @Override
                public V setValue(V value) {
                    return null;
                }
            }));

        }
        return reultSet;
    }
}
