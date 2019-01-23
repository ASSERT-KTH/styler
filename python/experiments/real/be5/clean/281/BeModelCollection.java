package com.developmentontheedge.be5.metadata.model.base;

import one.util.streamex.StreamEx;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public interface BeModelCollection<T extends BeModelElement> extends BeModelElement, Iterable<T>
{
    /**
     * Returns the number of elements in this data collection.
     *
     * @return Size of this data collection.
     */
    int getSize();

    /**
     * Returns the type of DataElements stored in the data collection.
     *
     * @return Type of DataElements stored in the data collection.
     */
    Class<? extends BeModelElement> getDataElementType();

    /**
     * Returns <b>true</b> if this data collection contains the element with the specified name, <b>false</b> otherwise
     *
     * @param name name of data element
     * @return <b>true</b> if this data collection contains the element with specified name,<br> <b>false</b> otherwise
     */
    boolean contains(String name);

    /**
     * Returns the <code>DataElement</code> with the specified name.
     * Returns <code>null</code> if the data collection
     * contains no data element for this name.
     */
    T get(String name);

    /**
     * Returns an iterator over the data elements in this collection.
     * There are no guarantees concerning the order in which the elements
     * are returned. If the data collection is modified while an iteration
     * over it is in progress, the results of the iteration are undefined.
     */
    @Override
    Iterator<T> iterator();

    /**
     * Returns an unmodifiable view of the data element name list.
     * Query operations on the returned list "read through" to the internal name list,
     * and attempts to modify the returned list, whether direct or via its iterator,
     * result in an <code>UnsupportedOperationException</code>.
     * The returned list is backed by the data collection,
     * so changes to the data collection are reflected in the returned list.
     * The name list can be sorted or unsorted depending on the DataCollection
     * implementing class.
     *
     * @return list of names
     */
    List<String> getNameList();

    /**
     * Adds the specified data element to the collection.
     * If the data collection previously contained the specified element,
     * the old value is replaced.
     *
     * @return previous version of the data element, or null if there was no one.
     */
    T put(T obj);

    /**
     * Removes the specified data element from the collection, if present.
     * Does nothing if null is supplied
     * Notifies all listeners if the data element was removed.
     *
     * @throws java.util.UnsupportedOperationException if the data collection is unmutable.
     */
    void remove(String name);

    /**
     * @return DataElementPath object representing the path to this collection
     */
    @Override
    DataElementPath getCompletePath();

    /**
     * Merge collection loaded from database to this collection
     *
     * @param other         collection to merge
     * @param ignoreMyItems whether to ignore absent EntityItems from other with the project origin
     * @param inherit       TODO
     */
    public void merge(BeModelCollection<T> other, boolean ignoreMyItems, boolean inherit);

    /**
     * Called by child to update last modification time
     */
    public void updateLastModification();

    public List<String> getAvailableNames();

    public Collection<T> getAvailableElements();

    /**
     * Notifies that source code of this element has been changed.
     */
    public void fireCodeChanged();

    /**
     * @return true if collection is empty
     */
    public boolean isEmpty();

    public StreamEx<T> stream();

    public StreamEx<String> names();
}
