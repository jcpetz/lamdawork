package com.parallelism;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.util.List;
import java.util.function.ToDoubleFunction;
import java.util.function.ToLongFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import com.streams.Examples;
import com.streams.Examples.Album;


public class MiscExamples {

	private final List<Album> albums;

	public MiscExamples(List<Album> albums) {
		this.albums = albums;
	}

	public static void main(String[] args) {
       //MiscExamples.moreAlbumExamples();
		final long max = 1;
		long startTime = System.currentTimeMillis();
		int count = MiscExamples.countPrimeNumbers(max);
		System.out.printf("prime numbers between 1 and %d: %d...compute time (ms) = %d\n", max, count, System.currentTimeMillis()-startTime);
		
		startTime = System.currentTimeMillis();
		count = MiscExamples.countPrimeNumbersViaParallelLamda(max);
		System.out.printf("parallel lambda: prime numbers between 1 and %d: %d...compute time (ms) = %d\n", max, count, System.currentTimeMillis()-startTime);
		
		// test file parsing with lambda
		FileReader fr;
		try {
			final String fileName = args.length > 0 ? args[0] : "testdata.txt";
			final int lineCount = args.length > 1 ? Integer.parseInt(args[1]) : 1000;
			System.out.printf("creating test file %s with %d records\n", fileName, lineCount);
			MiscExamples.createTestFile(fileName, lineCount);
			fr = new FileReader(fileName);
			System.out.printf("parsing test file in parallel...");
			startTime = System.currentTimeMillis();
			List<String> lines = MiscExamples.findLinesEndingWith(fr, ".", true);
			if (lines.size() == lineCount) {
				System.out.printf("elapsed time was %d msec\n", System.currentTimeMillis()-startTime);
			} else {
				System.err.println("assert error - line counts don't match");
			}
			System.out.printf("parsing test file with one thread...");
			fr = new FileReader(fileName);
			startTime = System.currentTimeMillis();
			lines = MiscExamples.findLinesEndingWith(fr, ".", false);
			System.out.printf("elapsed time was %d msec\n", System.currentTimeMillis()-startTime);
			//lines.forEach((e) -> System.out.println(e));
		} catch (IOException e) {	
			e.printStackTrace();
		}
		
		
	}

	public double countFeatureAsDouble(ToDoubleFunction<Album> function) {
		return albums.stream().mapToDouble(function).sum();
	}

	public long countFeature(ToLongFunction<Album> function) {
		return albums.stream().mapToLong(function).sum();
	}

	public static void moreAlbumExamples() {
		// build album repos
		Examples example = new Examples();
		example.populateArtists();
		example.createAlbumsFromArtists();
		example.createArtistToAlbumAssociations();

		// ThreadLocal example using anonymous class for assignment -
		// initialValue() not invoked until var reference
		ThreadLocal<Examples.Album> thisAlbum = new ThreadLocal<Examples.Album>() {
			@Override
			protected Examples.Album initialValue() {
				return example.getAlbums().get(0);
			}
		};
		System.out.println(thisAlbum.get().getName());

		// now the lambda init - get different album
		thisAlbum = ThreadLocal.withInitial(() -> example.getAlbums().get(1));
		System.out.println(thisAlbum.get().getName());

		// count album info using conventional means
		float runningTime = 0.0f;
		int artistCount = 0;
		int trackCount = 0;
		for (Examples.Album album : example.getAlbums()) {
			artistCount += album.getArtists().size();
			trackCount += album.getTracks().size();
			for (Examples.AlbumTrack track : album.getTracks()) {
				runningTime += track.getLength();
			}
		}
		System.out.printf("album totals: runningTime: %4.2f, artists: %d, tracks: %d\n", runningTime, artistCount,
				trackCount);

		// now using lambda streams for the same info <less code but stream
		// boilerplate code repeated>
		float runningTime2 = (float) example.getAlbums().stream()
				.mapToDouble(album -> album.getTracksAsStream().mapToDouble(track -> track.getLength()).sum()).sum();
		long artistCount2 = example.getAlbums().stream().mapToLong(album -> album.getArtists().size()).sum();
		long trackCount2 = example.getAlbums().stream().mapToLong(album -> album.getTracks().size()).sum();
		System.out.printf("lamdba totals: runningTime: %4.2f, artists: %d, tracks: %d\n", runningTime2, artistCount2,
				trackCount2);

		// to reuse stream with different predicates (one returning Double, the
		// other returning long) lets define methods
		MiscExamples me = new MiscExamples(example.getAlbums());
		float runningTime3 = (float) me
				.countFeatureAsDouble(album -> album.getTracksAsStream().mapToDouble(track -> track.getLength()).sum());
		long artistCount3 = me.countFeature(album -> album.getArtists().size());
		long trackCount3 = me.countFeature(album -> album.getTracks().size());
		System.out.printf("lamdba totals: runningTime: %4.2f, artists: %d, tracks: %d\n", runningTime3, artistCount3,
				trackCount3);

	}

	public static int countPrimeNumbers(long max) {
		// first conventional
		int count = 0;
		for (long i = 1; i <= max; i++) {
			boolean isPrime = true;
			for (long j = 2; j < i; j++) {
				if (i % j == 0) {
					isPrime = false;
					break;
				}
			}
			if (isPrime)
				count++;
		}
		return count;
	}
	
	public static int countPrimeNumbersViaParallelLamda(long max) {
		return (int)LongStream.range(1, max).parallel().filter(MiscExamples::isPrime).count();
		
	}
	
	private static boolean isPrime(long number) {
		return LongStream.range(2, number).allMatch(x -> (number % x) != 0);
	}
	
	public static List<String> findLinesEndingWith(Reader input, final String suffix, boolean useParallel) {
		try (BufferedReader reader = new BufferedReader(input)) {
			Stream<String> stream = useParallel ? reader.lines().parallel() : reader.lines();
			return stream.filter(line -> line.endsWith(suffix)).map(line -> line.substring(0,  line.length())).collect(Collectors.toList());
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}
	
	private static void createTestFile(String fileName, int count) {
		final String line = "Line Number %d - the time is now %d and so all good men must come to the aid of their country.\n";
		try (BufferedWriter os = new BufferedWriter(new FileWriter(fileName))) {
			for (int i=1; i <= count; i++) {
				os.write(String.format(line, i, System.currentTimeMillis()));
			}
		} catch (IOException ex) {
			System.err.println(ex.getMessage());
		}
	}
}
