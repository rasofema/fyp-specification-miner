package org.example.model;


import de.learnlib.oracle.MembershipOracle;
import de.learnlib.query.Query;
import de.learnlib.ralib.oracles.DataWordOracle;
import de.learnlib.ralib.words.PSymbolInstance;

import java.util.Collection;
import java.util.NoSuchElementException;


public class IterOracle implements DataWordOracle, MembershipOracle.DFAMembershipOracle<PSymbolInstance> {

    private final Functions functions = new Functions();
    private boolean silentFailAdd = false;
    public void setSilentFailAddTrue() {
        this.silentFailAdd = true;
    }

    private final BoundedIterator<Object> iterator;

    public IterOracle() {
        this.iterator = new BoundedIterator<>(1);
    }

    private Boolean step(PSymbolInstance function) {
        switch (functions.getFunction(function.getBaseSymbol())) {
//            case hasNextTrue -> {
//                return this.iterator.hasNext();
//            }
//            case hasNextFalse -> {
//                return !this.iterator.hasNext();
//            }
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
    public void processQuery(Query<PSymbolInstance, Boolean> query) {
        this.iterator.reset();
        for (PSymbolInstance input : query.getInput().asList()) {
            if (!step(input)) {
                query.answer(false);
                return;
            }
        }
        query.answer(true);
    }

    @Override
    public void processQueries(Collection<? extends Query<PSymbolInstance, Boolean>> collection) {
        for (Query<PSymbolInstance, Boolean> query : collection) {
            processQuery(query);
        }
    }
}
