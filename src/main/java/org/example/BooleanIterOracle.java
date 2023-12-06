package org.example;


import de.learnlib.oracle.SingleQueryOracle.SingleQueryOracleDFA;
import net.automatalib.word.Word;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class BooleanIterOracle implements SingleQueryOracleDFA<Function> {

    private static final int BOUND = 1;
    private int iterPosition;
    private BooleanBoundedList<Object> list;
    private Iterator<Object> iter;
    public BooleanIterOracle() {
        reset();
    }

    private void reset() {
        this.iterPosition = 0;
        this.list = new BooleanBoundedList<>(BOUND);
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
                boolean res = this.list.add(null);
                updateIterState();
                return res;
            }
        }
        return true;
    }

    @Override
    public Boolean answerQuery(Word<Function> prefix, Word<Function> suffix) {
        reset();
        System.out.println(prefix.concat(suffix));
        return prefix.concat(suffix).stream()
                .allMatch(this::step);
    }
}
