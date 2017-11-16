package com.simple;

public class TaskComparatorImpl {
   public static void main(String[] args) {
	  TaskComparator<Integer> comp = (Integer a1, Integer a2) -> { return a1 > a2; };
	  boolean result = comp.compareTasks(5, 3);
	  System.out.println(result);
	  
	  // Runnable as classic anonymous class/method
	  Runnable r1 = new Runnable() {
		  @Override
		  public void run() {
			  System.out.println(comp.compareTasks(1000, -1));
		  }
	  };
	  
	  // Runnable as functional interface
	  Runnable r2 = () -> System.out.println(comp.compareTasks(100, 345));
	  
	  new Thread(r1).start();
	  new Thread(r2).start();
   }
}
