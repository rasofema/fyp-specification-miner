package org.example;


import de.learnlib.oracle.SingleQueryOracle.SingleQueryOracleDFA;
import net.automatalib.word.Word;


import java.util.*;

enum Function {
    hasNextTrue,
    hasNextFalse,
    next
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
                    this.iter = Collections.singletonList(null).iterator();
                    if (!this.iter.hasNext()) throw new RuntimeException("Iterator should have next");
                }
            }
            case hasNextFalse -> {
                while (this.iter.hasNext()) {
                    this.iter.next();
                }
            }
            case next -> {
                try {
                    this.iter.next();
                } catch (Exception e) {
                    return false;
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