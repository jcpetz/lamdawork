package com.parallelism;

import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.IntFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import com.simple.Function2;

public class MonteCarloSim {

	private static final int N = 1000000000;
	private static ThreadLocalRandom random = ThreadLocalRandom.current();

	public Map<Integer, Double> runStreamVersion() {
		double fraction = 1.0 / N;
		return IntStream.range(0, N).parallel().mapToObj(twoDiceThrows())
				.collect(Collectors.groupingBy(side -> side, Collectors.summingDouble(n -> fraction)));
	}

	private static IntFunction<Integer> twoDiceThrows() {

        return i -> {

            ThreadLocalRandom random = ThreadLocalRandom.current();

            int firstThrow = random.nextInt(1, 7);

            int secondThrow = random.nextInt(1, 7);

            return firstThrow + secondThrow;

        };

    }
	
	private static IntFunction<String> mapper() {
		IntFunction<String> intAdd = n -> Integer.toBinaryString(n);
		return intAdd;
	}
	
	private static IntFunction<Integer> imapper() {
		IntFunction<Integer> intAdd = n -> n;
		return intAdd;
	}

	public static void main(String[] args) {
		IntStream i = IntStream.of(6, 5, 7, 1, 2, 3, 3);
		Stream<String> d = i.mapToObj(mapper());
		d.forEach(System.out::println);
		double fraction = 1.0 / N;
		Map<Integer, Double> result = new MonteCarloSim().runStreamVersion();
		System.out.println(result);
		
	}
}
