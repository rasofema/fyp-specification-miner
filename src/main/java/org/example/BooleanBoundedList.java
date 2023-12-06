package org.example;

import java.util.ArrayList;
import java.util.Iterator;

public class BooleanBoundedList<T> implements Iterable<T> {
    private final ArrayList<T> list;
    private final int bound;

    public BooleanBoundedList(int bound) {
        if (bound <= 0) {
            throw new IllegalArgumentException("Illegal bound: " + bound);
        }
        list = new ArrayList<>(bound);
        this.bound = bound;
    }

    public boolean add(T elem) {
        if (list.size() >= bound) {
            return false;
        }
        list.add(elem);
        return true;
    }

    @Override
    public Iterator<T> iterator() {
        return list.iterator();
    }
}
