package org.example;


import de.learnlib.oracle.SingleQueryOracle.SingleQueryOracleDFA;
import net.automatalib.word.Word;


import java.util.*;
import java.util.stream.Stream;

enum Function {
    hasNextTrue,
    hasNextFalse,
    next,
    remove
};

public class IterOracle implements SingleQueryOracleDFA<Function> {
    private Iterator<Object> iter;
    public IterOracle() {
        reset();
    }

    private void reset() {
        this.iter = Collections.emptyIterator();
    }

    private Boolean step(Function function) {
        switch (function) {
            case hasNextTrue -> {
                if (!this.iter.hasNext()) {
                    ArrayList<Object> list = new ArrayList<>();
                    list.add(null);
                    this.iter = list.iterator();
                    if (!this.iter.hasNext()) throw new RuntimeException("Iterator should have next");
                }
            }
            case hasNextFalse -> {
                this.iter = new ArrayList<>().iterator();
                if (this.iter.hasNext()) throw new RuntimeException("Iterator should not have next");
            }
            case next -> {
                try {
                    this.iter.next();
                } catch (Exception e) {
                    return false;
                }
            }
            case remove -> {
                try {
                    this.iter.remove();
                } catch (IllegalStateException e) {
                    return false;
                } catch (UnsupportedOperationException e) {
                    throw new RuntimeException("Iterator should support operation");
                }
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
