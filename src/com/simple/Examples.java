package com.simple;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class Examples {
	
	private String logo;
	
	public Examples(String logo) {
		this.logo = logo;
	}
	
	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

	public static void delayms(long ms) {
		try {
			Thread.sleep(ms);
		} catch (InterruptedException e) {
		}

	}
	
	private static String dateFormat = "yyyy MM:dd HH:mm:ss";
	static final void assignDateFormat(String format) {
		dateFormat = format;
	}

	public static final ThreadLocal<SimpleDateFormat> formatter = ThreadLocal.<SimpleDateFormat>withInitial(() -> {
		return new SimpleDateFormat(dateFormat);
	});

	public static final SimpleDateFormat nonSafeDateFormat = new SimpleDateFormat("yyyy MM:dd HH:mm:ss");

	public static final ThreadLocal<Date> date = ThreadLocal.<Date>withInitial(() -> {
		return Calendar.getInstance().getTime();
	});

	public static final int iterations = 100;

	public static void main(String[] args) {

		Examples myExample = new Examples("LendingTree.com");
		Supplier<String> logo = () -> {
			return myExample.getLogo();
		};
		
		Consumer<Examples> consumeMe = (v) -> {
		   String loginUri = v.getLogo() + "/login";
		   v.setLogo(loginUri);
		   System.out.println(loginUri);
		};
		
		consumeMe.accept(myExample);
		
		Predicate<Integer> greaterThan5 = x -> x > 5;
		int[] data = { 1, 500, -24, 500, 300, 12, -1, 0 };
		for (int t : data) {
			boolean result = greaterThan5.test(t);
			System.out.println(result);
		}

		BinaryOperator<Long> addLongs = (x, y) -> x + y;
		Long result = addLongs.apply(new Long(100), new Long(345));
		System.out.println(result);

		// Function<type1, type2>: 1st generic type is argument type of function, 2nd type is return type
		Function<Long, Long> newAdd = (x) -> x + 10;
		result = newAdd.apply(new Long(100));
		System.out.println(result);

		Function<Long, Long> new10xScale = (x) -> x * 10;
		
		result = new10xScale.apply(new Long(34));
		System.out.println(result);
		
		Function<Boolean, Long> booleanCheck = (x) -> x ? 20l : 0l;
		
		Long condLong = booleanCheck.apply(true);
		System.out.println(condLong);

		Function2<Long, Long> newMulti = (x, y) -> x * y;
		result = newMulti.apply(new Long(100), new Long(345));
		System.out.println(result);
		
		Function3<Long, Long> getMax = (x, y, z) -> {
			Long max = x > y ? x : y;
			max = max > z ? max : z;
			return max;
		};
		
		result = getMax.apply(30l, 10l, -5l);
		System.out.println(result);
		

		Thread t = new Thread() {
			public void run() {
				
				Examples.assignDateFormat("HH:mm:ss yyyy MM:dd");
				for (int i = 0; i < Examples.iterations; i++) {
					Date date = new Date();
					String sDate = Examples.formatter.get().format(date);
					System.out.println("Thread " + Thread.currentThread().getId() + " reports time as " + sDate);

					Examples.delayms(10);
				}
			}
		};

		t.start();

		for (int i = 0; i < Examples.iterations; i++) {
			Date date = new Date();
			String sDate = Examples.formatter.get().format(date);
			System.out.println("Thread " + Thread.currentThread().getId() + " reports time as " + sDate);

			Examples.delayms(10);
		}
	}

	private static Long new20xIntScale(Long long1) {
		// TODO Auto-generated method stub
		return null;
	}

}
