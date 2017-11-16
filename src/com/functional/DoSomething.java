package com.functional;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import com.asyncsupport.FutureAlbums;
import com.streams.Examples;
import com.streams.Examples.Album;

public class DoSomething<T> {
	private final Class<T> clazz;
	
	public DoSomething(Class<T> clazz) {
		this.clazz = clazz;
	}

	public AbstractBase<T> method1() {
		AbstractBase<T> base = new AbstractBase<T>() {
			@Override
			public List<T> supplyData() {
				List<T> data = new ArrayList<>();
				try {
					data.add(clazz.newInstance());
				} catch (InstantiationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return data;
			}

			@Override
			public void consumeData(T data) {
				System.out.println(data);
			}
		};
		return base;
	}
	
	public AbstractBase<T> method2(Supplier<List<T>> supplier, Consumer<T> consumer) {
		AbstractBase<T> base = new AbstractBase<T>() {
			@Override
			public List<T> supplyData() {
				return supplier.get();
			}

			@Override
			public void consumeData(T data) {
				consumer.accept(data);
			}
		};
		return base;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		test1();
		test2();
		test3();
	}
	
	public static void test1() {
		DoSomething<String> dos = new DoSomething<>(String.class);
		AbstractBase<String> base = dos.method1();
		Thread t1 = new Thread() {
			public void run() {
				base.run();
			}
		};
		t1.start();
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		base.setGo(false);
		System.out.println("**************   iterations: " + base.iterations + "*******************");
	}
	
	public static void test2() {
		// populate albums
		Examples albums = FutureAlbums.getAllRepos();
		
		// define supplier and consumer objects
		Supplier<List<Album>> allAlbums = () -> {
			return albums.getAlbums();
		};
		Consumer<Album> consumeAlbum = (a) -> {
			   System.out.println( a.getName());
			};
			
	    // now do something with supplier/consumer
		DoSomething<Album> dos = new DoSomething<>(Album.class);
		AbstractBase<Album> base = dos.method2(allAlbums, consumeAlbum);
		Thread t1 = new Thread() {
			public void run() {
				base.run();
			}
		};
		t1.start();
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		base.setGo(false);
		System.out.println("**************   iterations: " + base.iterations + "*******************");
	}
	
	public static void test3() {
		// populate albums
		Examples albums = FutureAlbums.getAllRepos();
		
	    // call service with anonymous supplier/consumer
		DoSomething<Album> dos = new DoSomething<>(Album.class);
		AbstractBase<Album> base = dos.method2(() -> albums.getAlbums(), (a) -> System.out.println(a));
		Thread t1 = new Thread() {
			public void run() {
				base.run();
			}
		};
		t1.start();
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		base.setGo(false);
		System.out.println("**************   iterations: " + base.iterations + "*******************");
	}



}
