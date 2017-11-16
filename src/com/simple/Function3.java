package com.simple;

@FunctionalInterface
public interface Function3<T, R> {
   R apply(T arg1, T arg2, T arg3);
}