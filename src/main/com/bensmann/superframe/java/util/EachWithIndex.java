/*
 * EachWithIndex.java
 *
 * Created on July 29, 2005, 11:26 AM
 *
 */

package com.bensmann.superframe.java.util;

import java.util.Iterator;
import java.util.List;

/**
 * Iterator with an index
 *
 * <pre>
 * List<String> l = new LinkedList<String>();
 * l.add("Test1");
 * l.add("Test2");
 * EachWithIndex<String> ewi = new EachWithIndex<String>(l);
 * for (String s : ewi) {
 *      System.out.println(s + " " + ewi.getIndex());
 *                                   ^^^^^^^^^^^^^^
 * }
 * </pre>
 *
 * @author Ralf Bensmann
 * @version $Id$
 */
class IndexIterator<T> implements Iterator {

    /**
     *
     */
    private int index;

    /**
     *
     */
    private List<T> list;

    /**
     *
     */
    private Iterator<T> iterator;

    /**
     * Creates a new instance of EachWithIndex
     * @param list
     */
    public IndexIterator(List<T> list) {
        this.list = list;
        iterator = list.iterator();
    }

    /**
     *
     */
    public void remove() {
        iterator.remove();
    }

    /**
     *
     * @return
     */
    public T next() {
        index++;
        return iterator.next();
    }

    /**
     *
     * @return
     */
    public boolean hasNext() {
        return iterator.hasNext();
    }

    /**
     *
     * @return
     */
    public int getIndex() {
        return index;
    }
}

/**
 *
 * @param E
 * @author Ralf Bensmann
 * @version $Id$
 */
public class EachWithIndex<E> implements Iterable<E> {

    /**
     *
     */
    private List<E> list;

    /**
     *
     */
    private IndexIterator<E> indexIterator;

    /**
     *
     * @param list
     */
    public EachWithIndex(List<E> list) {
        this.list = list;
    }

    /**
     *
     * @return
     */
    public Iterator<E> iterator() {
        indexIterator = 
                new IndexIterator<E>(list);
        return (Iterator<E>) indexIterator;
    }

    /**
     *
     * @return
     */
    public int getIndex() {
        return indexIterator.getIndex();
    }
}