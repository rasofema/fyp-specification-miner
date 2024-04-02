package org.example.model;


import de.learnlib.oracle.MembershipOracle;
import de.learnlib.query.Query;
import de.learnlib.ralib.oracles.DataWordOracle;
import de.learnlib.ralib.words.PSymbolInstance;
import de.learnlib.ralib.words.ParameterizedSymbol;

import java.util.Collection;
import java.util.NoSuchElementException;


public class IterOracle implements DataWordOracle, MembershipOracle.DFAMembershipOracle<PSymbolInstance> {

    private Functions functions = new Functions();
    private boolean silentFailAdd = false;
    public void setSilentFailAddTrue() {
        this.silentFailAdd = true;
    }

    private final BoundedIterator<Object> iterator;

    public IterOracle() {
        this.iterator = new BoundedIterator<>(1);
    }

    private Boolean step(PSymbolInstance function) {
        if (function.getParameterValues().length > 0 && !function.getParameterValues()[0].getId().equals(0)) {
            return false;
        }

        switch (functions.getFunction(function.getBaseSymbol())) {
            case hasNextTrue -> {
                return this.iterator.hasNext();
            }
            case hasNextFalse -> {
                return !this.iterator.hasNext();
            }
            /*case init -> {
                return false;
            }*/
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
                /*if (!function.getParameterValues()[0].getId().equals(this.iterator.listSize())) {
                    return false;
                }*/
            }
            case add -> {
                /*if (!function.getParameterValues()[0].getId().equals(1) || this.iterator.tooFar()) {
                    return false;
                }*/
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
        iterator.reset();
        /*if (query.getInput().length() == 0) {
            query.answer(true);
            return;
        }*/

//        PSymbolInstance function = query.getInput().asList().get(0);
        /*if (!functions.getFunction(function.getBaseSymbol()).equals(init) || !function.getParameterValues()[0].getId().equals(0)) {
            query.answer(false);
            return;
        }*/
//        for (PSymbolInstance input : query.getInput().asList().subList(1, query.getInput().length())) {
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
