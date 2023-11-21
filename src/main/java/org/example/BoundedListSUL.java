package org.example;

import de.learnlib.api.SUL;

public class BoundedListSUL implements SUL<Integer,Integer> {
    public BoundedListSUL() {
    }

    @Override
    public void pre() {

    }

    @Override
    public void post() {

    }

    @Override
    public Integer step(Integer integer) {
        return null;
    }

    @Override
    public boolean canFork() {
        return true;
    }

    @Override
    public SUL fork() {
        return SUL.super.fork();
    }
}
