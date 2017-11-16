package com.simple;

@FunctionalInterface
public interface TaskComparator<T> {
   public boolean compareTasks(T a1, T a2);
}
