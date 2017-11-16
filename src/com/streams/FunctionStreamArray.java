package com.streams;

@FunctionalInterface
public interface FunctionStreamArray<String, R> {
	R apply(String arg1, R arg2);
}