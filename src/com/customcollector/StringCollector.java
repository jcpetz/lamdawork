package com.customcollector;

import java.util.HashSet;
import java.util.Set;
import java.util.StringJoiner;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

/**
 * move stream() reduce to custom class.   A Collector has 4 components:
 * - Supplier - factory for making a Container - here its StringCombiner [equiv to reduce() 1st arg]
 * - Accumulator - combines current element with preceding result [equiv to reduce() 2nd arg e.g. StringCombiner::add]
 * - Combiner - merge 2 containers [equiv to reduce() 3rd arg e.g. StringCombiner::merge]
 * - Finisher - map final container to desired output [e.g. String] along with any post operation 
 * @author petzj
 *
 */
public class StringCollector implements Collector<String, StringCombiner, String> {
	private final String prefix;
	private final String delim;
	private final String suffix;
	private static final Set<java.util.stream.Collector.Characteristics> characSet;
	
	static {
		characSet = new HashSet<>();
		characSet.add(Collector.Characteristics.CONCURRENT);
		//characSet.add(Collector.Characteristics.IDENTITY_FINISH); // if Finisher is identity function (simply returns its argument)
	}
	
	public StringCollector(final String delim, final String prefix, final String suffix) {
	   this.prefix = prefix;
	   this.delim = delim;
	   this.suffix = suffix;
	}

	@Override
	public Supplier<StringCombiner> supplier() {
		// create the container [similar to 1st arg in stream reduce()]
		return () -> new StringCombiner(delim, prefix, suffix);
	}

	@Override
	public BiConsumer<StringCombiner, String> accumulator() {
		// combine current element with preceding result
		return StringCombiner::add;
	}

	@Override
	public BinaryOperator<StringCombiner> combiner() {
		// merge 2 containers
		return StringCombiner::merge;
	}

	@Override
	public Function<StringCombiner, String> finisher() {
		return StringCombiner::toString;
	}

	@Override
	public Set<java.util.stream.Collector.Characteristics> characteristics() {
		return characSet;
	}

}
