package implementation.utils.datastructs;

import java.util.*;

/**
 *
 * Threadsafe
 *
 * Created by Blume Till on 23.08.2016.
 */
public class SetSet {
    List<Set<String>> setSet;

    public SetSet(){ setSet = new LinkedList<>();}
    public SetSet(Set<String> set){
        this();
        this.setSet.add(set);
    }

    public boolean add(Set<String> set){
        if(set != null)
            return setSet.add(set);
        else
            return false;


    }

    public Set<String> getAll(){
        Set<String> all = new TreeSet<>();
        setSet.forEach(X -> all.addAll(X));
        return all;
    }

    public List<Set<String>> get(){
        return setSet;
    }


    public Set<String> intersect(){
        if(setSet.isEmpty())
            return new TreeSet<>();

        Iterator<Set<String>> iterator = setSet.iterator();

        //first is starting set
        Set<String> intersection = iterator.next();

        iterator.forEachRemaining(SET -> {
            intersection.removeIf(X -> !SET.contains(X));
        });
        return intersection;
    }
    public String toString(){
        if(setSet.isEmpty())
            return "{}";
        StringBuilder string = new StringBuilder();
        string.append("{");
        setSet.forEach(SET -> {
            StringBuilder set = new StringBuilder();
            set.append("{");
            SET.forEach(ITEM -> set.append(ITEM + ","));
            set.append("},");
        });
        string.append("}");
        return string.toString();
    }
}



