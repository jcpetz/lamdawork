package com.simple;

@FunctionalInterface
public interface Function<T, R> {
   R apply(T t);
}