package org.example.model;


import de.learnlib.api.oracle.SingleQueryOracle.SingleQueryOracleDFA;
import net.automatalib.words.Word;

import java.util.NoSuchElementException;

public class IterOracle implements SingleQueryOracleDFA<Function> {

    private boolean silentFailAdd = false;
    public void setSilentFailAddTrue() {
        this.silentFailAdd = true;
    }

    private final BoundedIterator<Object> iterator;

    public IterOracle() {
        this.iterator = new BoundedIterator<>();
    }

    private Boolean step(Function function) {
        switch (function) {
            case hasNextTrue -> {
                return this.iterator.hasNext();
            }
            case hasNextFalse -> {
                return !this.iterator.hasNext();
            }
            case next -> {
                try {
                    this.iterator.next();
                } catch (NoSuchElementException e) {
                    return false;
                }
            }
            case remove -> {
                try {
                    this.iterator.remove();
                } catch (IllegalStateException e) {
                    return false;
                } catch (UnsupportedOperationException e) {
                    throw new RuntimeException("Iterator should support operation");
                }
            }
            case add -> {
                try {
                    this.iterator.add(null);
                } catch (OutOfMemoryError e) {
                    return this.silentFailAdd;  // true if silent fail, false otherwise
                }
            }
        }
        return true;
    }

    @Override
    public Boolean answerQuery(Word<Function> prefix, Word<Function> suffix) {
        iterator.reset();
        return prefix.concat(suffix).stream()
                .allMatch(this::step);
    }
}
