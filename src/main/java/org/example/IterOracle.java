package org.example;


import de.learnlib.oracle.SingleQueryOracle.SingleQueryOracleDFA;
import net.automatalib.word.Word;


import java.util.*;

enum Function {
    hasNextTrue,
    hasNextFalse,
    next,
    remove,
    add
};

public class IterOracle implements SingleQueryOracleDFA<Function> {

    private static final int BOUND = 2;
    private int iterPosition;
    private ExceptionBoundedList<Object> list;
    private Iterator<Object> iter;
    public IterOracle() {
        reset();
    }

    private void reset() {
        this.iterPosition = 0;
        this.list = new ExceptionBoundedList<>(BOUND);
        this.iter = this.list.iterator();
    }

    private void updateIterState() {
        this.iter = this.list.iterator();
        for (int i = 0; i < this.iterPosition; i++) {
            this.iter.next();
        }
    }

    private Boolean step(Function function) {
        switch (function) {
            case hasNextTrue -> {
                return this.iter.hasNext();
            }
            case hasNextFalse -> {
                return !this.iter.hasNext();
            }
            case next -> {
                try {
                    this.iter.next();
                } catch (NoSuchElementException e) {
                    return false;
                }
                this.iterPosition++;
            }
            case remove -> {
                try {
                    this.iter.remove();
                } catch (IllegalStateException e) {
                    return false;
                } catch (UnsupportedOperationException e) {
                    throw new RuntimeException("Iterator should support operation");
                }
                this.iterPosition--;
            }
            case add -> {
                try {
                    this.list.add(null);
                } catch (OutOfMemoryError e) {
                    return false;
                }
                updateIterState();
            }
        }
        return true;
    }

    @Override
    public Boolean answerQuery(Word<Function> prefix, Word<Function> suffix) {
        reset();
        return prefix.concat(suffix).stream()
                .allMatch(this::step);
    }
}
