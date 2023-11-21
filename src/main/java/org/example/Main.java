package org.example;

import org.example.utils.FunctionCall;
import org.openjdk.jmc.common.collection.BoundedList;

import java.util.Iterator;

public class Main {
    public static void main(String[] args) {
        BoundedList<Integer> list = new BoundedList<Integer>(10);
        Iterator<Integer> iterator = list.iterator();
        System.out.println(list.getSize());
        list.add(1);
        System.out.println(list.getSize());
        System.out.println(iterator.hasNext());
        System.out.println(list.iterator().hasNext());


        //FunctionCall fc = new FunctionCall(BoundedList.class);
    }
}