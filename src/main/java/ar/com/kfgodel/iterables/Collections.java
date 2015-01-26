package ar.com.kfgodel.iterables;

import java.util.*;

/**
 * This type represents a set of method added to collections
 * Created by ikari on 21/01/2015.
 */
public class Collections {

    public static<T> List<T> newList(T... elements){
        ArrayList<T> list = new ArrayList<T>();
        addAllIn(list, elements);
        return list;
    }

    public static<T> Set<T> newOrderedSet(T... elements){
        Set<T> set = new LinkedHashSet<>();
        addAllIn(set, elements);
        return set;
    }

    public static<T> void addAllIn(Collection<? super T> collection, T[] array){
        for (T element : array) {
            collection.add(element);
        }
    }

    public static<T> MergeResult<T> merge(Collection<T> older, Collection<T> newer) {
        return MergeResultImpl.create(older.iterator(), newer.iterator());
    }
}
