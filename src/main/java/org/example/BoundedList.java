package org.example;

import java.util.ArrayList;
import java.util.Iterator;

public class BoundedList<T> implements Iterable<T> {
    private final ArrayList<T> list;
    private final int bound;

    public BoundedList(int bound) {
        if (bound <= 0) {
            throw new IllegalArgumentException("Illegal bound: " + bound);
        }
        list = new ArrayList<>(bound);
        this.bound = bound;
    }

    public void add(T elem) {
        if (list.size() >= bound) {
            throw new OutOfMemoryError("No more space in the array");
        }
        list.add(elem);
    }

    @Override
    public Iterator<T> iterator() {
        return list.iterator();
    }
}
