package org.example.model;

import java.util.Iterator;

public class BoundedIterator<E> {
    private static final int BOUND = 2;
    private int position;
    private BoundedList<E> list;
    private Iterator<E> iter;
    public BoundedIterator() {
        reset();
    }

    public void reset() {
        this.position = 0;
        this.list = new BoundedList<>(BOUND);
        this.iter = this.list.iterator();
    }

    private void updateIterState() {
        this.iter = this.list.iterator();
        for (int i = 0; i < this.position; i++) {
            this.iter.next();
        }
    }

    public boolean hasNext() {
        return this.iter.hasNext();
    }

    public E next() {
        E val = this.iter.next();
        this.position++;
        return val;
    }

    public void remove() {
        this.iter.remove();
        this.position--;
    }

    public void add(E elem) {
        this.list.add(elem);
        updateIterState();
    }

}
