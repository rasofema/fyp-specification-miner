package org.example.utils;

import org.openjdk.jmc.common.collection.BoundedList;

import java.lang.reflect.Method;

public class FunctionCall {
    public FunctionCall(Class<?> thisClass) {
        try {
            Method[] methods = thisClass.getDeclaredMethods();

            for (Method method : methods) {
                System.out.println(method.toString());
            }
        } catch (Throwable e) {
            System.err.println(e);
        }
    }
}
