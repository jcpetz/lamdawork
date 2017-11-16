package com.simple;

@FunctionalInterface
public interface Function2<T, R> {
   R apply(T arg1, T arg2);
}