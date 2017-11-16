package com.parallelism;

import java.util.Arrays;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.stream.IntStream;

public class MovingAverage {
   public static double[] simpleMovingAverage(double[] values, int n) {
	   double[] sums = Arrays.copyOf(values, values.length);
	   Arrays.parallelPrefix(sums, Double::sum);
	   int start = n - 1;
	   return IntStream.range(start, sums.length)
			   .mapToDouble(i -> {
				   double prefix = i == start ? 0 : sums[i-n];
				   return (sums[i] - prefix) / n;
			   })
			   .toArray();
   }
   
   public static void main(String[] args) {
	   double[] values = {10, 20, 30}; //, 40, 50, 60, 70, 80, 90, 100};
	   double ma[] = MovingAverage.simpleMovingAverage(values, 3);
	   System.out.println(Arrays.toString(ma));
	   final Logger logger = Logger.getLogger(MovingAverage.class.getSimpleName());
	   
	   
	   
   }
}
