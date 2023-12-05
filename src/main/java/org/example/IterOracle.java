package org.example;


import de.learnlib.oracle.SingleQueryOracle.SingleQueryOracleDFA;
import net.automatalib.word.Word;


import java.util.ArrayList;
import java.util.Iterator;

enum Function {
    hasNextTrue,
    hasNextFalse,
    next
};

public class IterOracle implements SingleQueryOracleDFA<Function> {
    private ArrayList<Integer> bl;
    private Iterator<Integer> iter;
    public IterOracle() {
        reset();
    }

    private void reset() {
        this.bl = new ArrayList<>();
        iter = bl.iterator();
    }

    private Boolean step(Function function) {
        switch (function) {
            case hasNextTrue -> {
                if (!this.iter.hasNext()) {
                    if (this.bl.isEmpty()) {
                        this.bl.add(0);
                    }
                    this.iter = this.bl.iterator();
                    if (!this.iter.hasNext()) throw new RuntimeException("Iterator should have next");
                }
            }
            case hasNextFalse -> {
                if (this.iter.hasNext()) {
                    while (this.iter.hasNext()) {
                        this.iter.next();
                    }
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
