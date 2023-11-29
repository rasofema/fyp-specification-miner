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
    private final ArrayList<Integer> bl;
    private Iterator<Integer> iter;
    public IterOracle() {
        this.bl = new ArrayList<>();
        bl.add(0);
        iter = bl.iterator();
    }

    private Boolean step(Function function) {
        switch (function) {
            case hasNextTrue -> {
                if (!this.iter.hasNext()) {
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
        boolean result = true;
        prefix.forEach(this::step);

        for (Function f : suffix)
            result = step(f);
        return result;
    }
}
