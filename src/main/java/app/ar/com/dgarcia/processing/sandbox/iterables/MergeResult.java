package app.ar.com.dgarcia.processing.sandbox.iterables;

import java.util.Set;

/**
 * This type represents a diff comparison between two iterables as they were the same entity in different moments.
 * So one is older, and the other is newer.<br>
 * This type contains "added" elements that are only present in the newer iterable. "removed" elements that are only
 * present in the older iterable, and "kept" elements that are present in both iterables
 *
 * Created by ikari on 21/01/2015.
 */
public interface MergeResult<T> {

    Set<T> getAdded();
    Set<T> getRemoved();
    Set<T> getKept();
}
