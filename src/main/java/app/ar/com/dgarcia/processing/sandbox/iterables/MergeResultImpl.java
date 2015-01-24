package app.ar.com.dgarcia.processing.sandbox.iterables;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Created by ikari on 21/01/2015.
 */
public class MergeResultImpl<T> implements MergeResult<T> {

    private Set<T> added;
    private Set<T> removed;
    private Set<T> kept;

    public static<T> MergeResultImpl<T> create(Iterator<T> older, Iterator<T> newer) {
        MergeResultImpl<T> result = new MergeResultImpl<>();
        result.added = new LinkedHashSet<>();
        result.removed = new LinkedHashSet<>();
        result.kept = new LinkedHashSet<>();
        result.merge(older, newer);
        return result;
    }

    @Override
    public Set<T> getAdded() {
        return added;
    }

    @Override
    public Set<T> getRemoved() {
        return removed;
    }

    @Override
    public Set<T> getKept() {
        return kept;
    }

    public void addRemoved(T removedElement) {
        this.removed.add(removedElement);
    }

    public void addAdded(T addedElement) {
        this.added.add(addedElement);
    }

    public void addKept(T keptElement) {
        this.kept.add(keptElement);
    }

    private void merge(Iterator<T> older, Iterator<T> newer) {
        //Elements that were taken from older, but didn't match a newer element
        Set<T> unmatchedOlderElements = new LinkedHashSet<>();

        // Compare elements in both collections while they have elements
        while(newer.hasNext()){
            T newerElement = newer.next();
            boolean foundMatchInOlder = unmatchedOlderElements.remove(newerElement);
            if(foundMatchInOlder){
                this.addKept(newerElement);
                continue;
            }
            boolean elementKept = false;
            while(older.hasNext()){
                T olderElement = older.next();
                if(newerElement == olderElement){
                    elementKept = true;
                    this.addKept(newerElement);
                    break;
                }
                unmatchedOlderElements.add(olderElement);
            }
            if(!older.hasNext()){
                if(!elementKept){
                    this.addAdded(newerElement);
                }
                break;
            }
        }
        // If there are more elements only in newer collection we still have to check them
        while(newer.hasNext()){
            T newerElement = newer.next();
            boolean foundMatchInOlder = unmatchedOlderElements.remove(newerElement);
            if(foundMatchInOlder){
                this.addKept(newerElement);
                continue;
            }
            this.addAdded(newerElement);
        }

        // All the unmatched elements are only in older collection (so they were removed)
        unmatchedOlderElements.forEach(this::addRemoved);
        // If there are more elements in older collection then they were removed
        while(older.hasNext()){
            T onlyInOlder = older.next();
            this.addRemoved(onlyInOlder);
        }
    }

}
