
package org.example.model;


import de.learnlib.api.oracle.SingleQueryOracle.SingleQueryOracleDFA;
import de.learnlib.api.query.Query;
import de.learnlib.ralib.oracles.DataWordOracle;
import de.learnlib.ralib.oracles.QueryCounter;
import de.learnlib.ralib.words.PSymbolInstance;
import net.automatalib.words.Word;

import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

/*public class IterRAOracle extends QueryCounter implements DataWordOracle {

    private static final int BOUND = 2;
    private int iterPosition;
    private BoundedList<Object> list;
    private Iterator<Object> iter;
    private boolean silentFailAdd = false;
    public void setSilentFailAddTrue() {
        this.silentFailAdd = true;
    }

    public IterRAOracle() {
        reset();
    }

    private void reset() {
        this.iterPosition = 0;
        this.list = new BoundedList<>(BOUND);
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
                    return this.silentFailAdd;  // true if silent fail, false otherwise
                }
                updateIterState();
            }
        }
        return true;
    }

    private Boolean step(Word<PSymbolInstance> query) {
        function
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
                    return this.silentFailAdd;  // true if silent fail, false otherwise
                }
                updateIterState();
            }
        }
        return true;
    }

    @Override
    public void processQueries(Collection<? extends Query<PSymbolInstance, Boolean>> queries) {
        reset();
        for (Query<PSymbolInstance, Boolean> query : queries) {
            boolean answer = step(query.getInput());
            query.answer(answer);
        }
    }
}*/

