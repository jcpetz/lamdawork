package com.streams;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.function.BinaryOperator;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.Comparator;
import java.util.DoubleSummaryStatistics;
import java.util.HashMap;

import com.customcollector.StringCollector;
import com.customcollector.StringCombiner;
import com.newfeatures.CreateArtistFunction;
import com.simple.Function;
import com.simple.Function2;
import com.simple.TaskComparator;

public class Examples {

	private List<Album> albums = new ArrayList<>();

	private Optional<Set<Entry<ArtistProfile, List<Album>>>> albumsByArtist = Optional.empty();

	public Optional<Set<Entry<ArtistProfile, List<Album>>>> getAlbumsByArtist() {
		return albumsByArtist;
	}

	public List<Album> getAlbums() {
		return albums;
	}
	
	public void addAlbums(List<Album> moreAlbums) {
		albums.addAll(moreAlbums);
	}

	public void addAlbum(Album album) {
		albums.add(album);
	}

	public static class ArtistProfile {
		private String name;
		private String birthplace;
		private String groupName;
		private String nationality;
		private boolean solo = false;

		public ArtistProfile(String name, String birthplace, String groupName, String nationality) {
			super();
			this.name = name;
			this.birthplace = birthplace;
			this.groupName = groupName;
			this.nationality = nationality;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getBirthplace() {
			return birthplace;
		}

		public void setBirthplace(String birthplace) {
			this.birthplace = birthplace;
		}

		public String getGroupName() {
			return groupName;
		}

		public void setGroupName(String groupName) {
			this.groupName = groupName;
		}

		public boolean isFrom(String birthPlace) {
			return this.birthplace.equals(birthPlace);
		}

		public String getNationality() {
			return nationality;
		}

		public void setNationality(String nationality) {
			this.nationality = nationality;
		}

		public boolean isSolo() {
			return solo;
		}

		public void setSolo(boolean solo) {
			this.solo = solo;
		}

		// lambda function using abbrev syntax class:method
		static public ArtistProfile createNewArtist(String name, String birthplace, String groupName,
				String nationality) {
			CreateArtistFunction<String, String, String, String, ArtistProfile> newArtist = (a, b, c,
					d) -> new ArtistProfile(a, b, c, d);
			return newArtist.apply(name, birthplace, groupName, nationality);
		}

		static private Predicate<ArtistProfile> getArtistNamePredicate(String prefix) {
			final Predicate<ArtistProfile> sirNamePredicate = artist -> prefix
					.startsWith(artist.getName().substring(0, prefix.length() - 1));
			return sirNamePredicate;
		}
	}

	public static class Album {
		private final String name;
		private final List<ArtistProfile> artists;
		private final List<AlbumTrack> tracks;
		private final ArtistProfile mainArtist;

		public Album(String name, List<ArtistProfile> artists, List<AlbumTrack> tracks) {
			super();
			this.name = name;
			this.artists = artists;
			this.tracks = tracks;
			// assign main artist for band with 'Sir' in title. If none, then
			// assign first artist in list
			List<ArtistProfile> mainArtists = artists.stream().filter(ArtistProfile.getArtistNamePredicate("Sir"))
					.collect(Collectors.toList());
			mainArtist = mainArtists.isEmpty() ? artists.get(0) : mainArtists.get(0);
		}

		public String getName() {
			return name;
		}

		public List<ArtistProfile> getArtists() {
			return artists;
		}

		public List<AlbumTrack> getTracks() {
			return tracks;
		}

		public Stream<AlbumTrack> getTracksAsStream() {
			return tracks.stream();
		}

		public ArtistProfile getMainArtist() {
			return mainArtist;
		}
	}

	public static class AlbumTrack {
		private final String name;
		private final float length;

		public AlbumTrack(String name, float length) {
			super();
			this.name = name;
			this.length = length;
		}

		public String getName() {
			return name;
		}

		public float getLength() {
			return length;
		}
	}

	private Collection<ArtistProfile> artists = new HashSet<>();

	public Collection<ArtistProfile> getArtists() {
		return artists;
	}

	public void setArtists(Collection<ArtistProfile> artists) {
		this.artists = artists;
	}

	public void addArtist(ArtistProfile artistProfile) {
		this.artists.add(artistProfile);
	}

	public long getCollectionProcessTime(String birthPlace, boolean countOutput) {
		int count = 0;
		Collection<ArtistProfile> coll = null;
		if (!countOutput) {
			coll = new HashSet<>();
		}
		long startTime = System.nanoTime();
		for (ArtistProfile ap : getArtists()) {
			if ("London".equals(ap.getBirthplace())) {
				if (countOutput)
					count++;
				else
					coll.add(ap);
			}
		}
		return System.nanoTime() - startTime;
	}

	public long getLambdaProcessTime(String birthPlace, boolean useParallel, boolean countOutput) {
		final Predicate<ArtistProfile> birthplacePredicate = ap -> ap.isFrom(birthPlace);
		long startTime = System.nanoTime();
		Stream<ArtistProfile> stream = useParallel ? getArtists().parallelStream() : getArtists().stream();
		stream = stream.filter(birthplacePredicate);
		if (countOutput) {
			Long count = stream.count();
		} else {
			List<ArtistProfile> coll = stream.filter(birthplacePredicate).collect(Collectors.toList());
		}
		return System.nanoTime() - startTime;
	}

	public void populateArtists() {
		// Beatles
		addArtist(new ArtistProfile("Ringo Starr", "London", "Beatles", "British"));
		addArtist(new ArtistProfile("George Harrison", "London", "Beatles", "British"));
		addArtist(new ArtistProfile("Sir Paul McCartney", "Liverpool", "Beatles", "British"));
		addArtist(new ArtistProfile("John Lennon", "Kensington", "Beatles", "British"));

		// The Who
		addArtist(new ArtistProfile("Sir Roger Daltrey", "London", "The Who", "Welsh"));
		addArtist(new ArtistProfile("Pete Townsend", "Lancashire", "The Who", "British"));
		addArtist(new ArtistProfile("Sir Keith Moon", "London", "The Who", "British"));
		addArtist(new ArtistProfile("(Deceased) John Entwistle", "London", "The Who", "British"));

		// Deep Purple
		addArtist(new ArtistProfile("Jon Lord", "Leicester", "Deep Purple", "British"));
		addArtist(new ArtistProfile("Ian Gillan", "London", "Deep Purple", "British"));
		addArtist(new ArtistProfile("Ritchie Blackmore", "London", "Deep Purple", "British"));

		// ELP
		addArtist(new ArtistProfile("Sir Keith Emerson", "Lancashire", "ELP", "British"));
		addArtist(new ArtistProfile("Greg Lake", "London", "ELP", "British"));
		addArtist(new ArtistProfile("Carl Palmer", "unknown", "ELP", "British"));

		// Yes
		addArtist(new ArtistProfile("Sir Jon Anderson", "Somewhere in England", "Yes", "British"));
		addArtist(new ArtistProfile("Chris Squire", "Somewhere in England", "Yes", "British"));
		addArtist(new ArtistProfile("Rick Wakeman", "Somewhere in England", "Yes", "British"));
		addArtist(new ArtistProfile("Bill Burford", "Somewhere in England", "Yes", "British"));

		// Nice
		addArtist(new ArtistProfile("Brian Davidson", "Lancashire", "Nice", "British"));
		addArtist(new ArtistProfile("David O'List", "Lancashire", "Nice", "British"));
		addArtist(new ArtistProfile("Lee Jackson", "Lancashire", "Nice", "British"));
	}

	public void createAlbumsFromArtists() {
		// build an album of artists and music tracks
		// - first use lamdba to get artists who belong to the band "The Who"
		final String bandName = "The Who";
		final Predicate<ArtistProfile> bandNamePredicate = ap -> bandName.equals(ap.getGroupName());
		List<ArtistProfile> whoArtists = this.getArtists().stream().filter(bandNamePredicate)
				.collect(Collectors.toList());
		// create tracks
		List<AlbumTrack> whoTracks = new ArrayList<>();
		whoTracks.add(new AlbumTrack("Wont Get Fooled Again", 3.45f));
		whoTracks.add(new AlbumTrack("Baba O'Reilly", 5.42f));
		Album album = new Album("Whos Next", whoArtists, whoTracks);

		// add album to list
		addAlbum(album);

		// create a 2nd album - ELP
		final String bandName2 = "ELP";
		final Predicate<ArtistProfile> bandName2Predicate = ap -> bandName2.equals(ap.getGroupName());
		List<ArtistProfile> elpArtists = this.getArtists().stream().filter(bandName2Predicate)
				.collect(Collectors.toList());
		// create tracks
		List<AlbumTrack> elpTracks = new ArrayList<>();
		elpTracks.add(new AlbumTrack("Trilogy", 7.67f));
		elpTracks.add(new AlbumTrack("From The Beginning", 3.67f));
		elpTracks.add(new AlbumTrack("The Endless Enigma", 9.35f));
		Album album2 = new Album("Trilogy", elpArtists, elpTracks);

		// add album to list
		addAlbum(album2);

		// 3rd album - using artists from other groups [e.g. KE - ELP/Nice]
		final String bandName3 = "Nice";
		final Predicate<ArtistProfile> bandName3Predicate = ap -> bandName3.equals(ap.getGroupName());
		List<ArtistProfile> niceArtists = this.getArtists().stream().filter(bandName3Predicate)
				.collect(Collectors.toList());
		List<ArtistProfile> ke = getArtists().stream().filter(ap1 -> ap1.getName().equals("Sir Keith Emerson"))
				.collect(Collectors.toList());
		niceArtists.addAll(ke);
		// create tracks
		List<AlbumTrack> niceTracks = new ArrayList<>();
		elpTracks.add(new AlbumTrack("Daddy Where Did I Come From?", 6.13f));
		elpTracks.add(new AlbumTrack("Little Arabella ", 5.64f));
		elpTracks.add(new AlbumTrack("Happy Freuds", 5.35f));
		elpTracks.add(new AlbumTrack("Intermezzo from the Karelia Suite", 9.12f));
		Album album3 = new Album("ARS LONGA VITA BREVIS", niceArtists, niceTracks);
		addAlbum(album3);

		// 4th album - Yes Fragile
		List<ArtistProfile> yesArtists = this.getArtists().stream().filter(ap -> "Yes".equals(ap.getGroupName()))
				.collect(Collectors.toList());
		// -- create tracks
		List<AlbumTrack> fragileTracks = new ArrayList<>();
		fragileTracks.add(new AlbumTrack("Roundabout", 7.90f));
		fragileTracks.add(new AlbumTrack("Heart of the Sunrise", 8.56f));
		fragileTracks.add(new AlbumTrack("Brahms Hall Festival", 5.05f));

		// add album to list
		Album album4 = new Album("Fragile", yesArtists, fragileTracks);
		addAlbum(album4);
	}

	public void createArtistToAlbumAssociations() {
		// first get a Map of artist to album and then iterate the map,
		// assigning a new mapping of artist-name to artist/album-list
		Map<ArtistProfile, List<Album>> albumsMappedbyMainArtist = getAlbums().stream()
				.collect(Collectors.groupingBy(album -> album.getMainArtist()));
		albumsByArtist = Optional.of(albumsMappedbyMainArtist.entrySet());
		return;
	}

	public void initialMiscLambdaExpressions() {
		// get count of all artists born in Liverpool using classic for loop
		long count = 0;
		for (ArtistProfile ap : getArtists()) {
			if ("London".equals(ap.getBirthplace())) {
				count++;
			}
		}

		// get same count using lambda expression with filter
		Long count2 = getArtists().stream().filter(ap -> ap.isFrom("London")).count();
		// Long count = example.getArtists().stream().filter(ap ->
		// ap.getBirthplace().equals("London")).count();
		assertEquals(count, count2.longValue());

		// now measure filter time between conventional loop vs lambda
		boolean countOutput = false;
		long conventionalTimems = getCollectionProcessTime("London", countOutput);
		long lamdaTimems = getLambdaProcessTime("London", false, countOutput);
		long lamdaTimems_parallel = getLambdaProcessTime("London", true, countOutput);

		System.out.println(conventionalTimems + ", " + lamdaTimems + " with lambda taking "
				+ (float) lamdaTimems / (float) conventionalTimems + " longer");
		System.out.println("lambda serial vs parallel ratio was " + (float) lamdaTimems / (float) lamdaTimems_parallel);

		// iterate over artist collection using lamdba parallel stream
		getArtists().parallelStream().forEach((ap) -> System.out.println(ap.getName()));
	}

	public void lambdaMap() {
		// final Function<String, String> toUpperCase = string ->
		// string.toUpperCase();
		// Stream<String> stream = lowerCase.stream();

		// simple map example (map replaces a value in the stream - e.g. lower
		// to upper case)
		List<String> upperCase = Stream.of("a", "b", "c").map(string -> string.toUpperCase())
				// .map(toUpperCase)
				.collect(Collectors.toList());

		// flatmap example (combines two lists (and their streams) into a single
		// stream and collects into a combined list
		List<Integer> together = Stream
				.of(Arrays.asList(1, 2, 3, 4), Arrays.asList(4, 5, 6, 7), Arrays.asList(40, 51, 61, 71))
				.flatMap(numbers -> numbers.stream()).collect(Collectors.toList());
		System.out.println(together);

		// min/max example using ArtistProfile
		// - in this example its necessary to be explicit on the lambda type
		// argument (ArtistProfile)
		// TaskComparator<ArtistProfile> compare = (ArtistProfile ap1,
		// ArtistProfile ap2) -> { return ap1.getGroupName().length() >
		// ap2.getGroupName().length(); };
		// ArtistComparator<ArtistProfile> comp = (ArtistProfile ap) -> { return
		// ap.getGroupName().length(); };
		ArtistProfile shortestBandName = this.getArtists().stream()
				.max(Comparator.comparing((ArtistProfile ap) -> ap.getGroupName().length())).get();
		System.out.println(shortestBandName.getGroupName());

		// similar min example based on string array
		String[] albumNames = { "Chopin I Love", "Trilogy", "We Take Requests" };
		List<String> albumNameList = Arrays.asList(albumNames);
		String shortestName = albumNameList.stream().min(Comparator.comparing((String e) -> e.length())).get();
		System.out.println(shortestName);

		// summation of a number list using stream reduce
		int count = Stream.of(1, 2, 3).reduce(0, (acc, element) -> acc + element);
		assertEquals(6, count);

		// try summing an enormous list of numbers and see of parallel stream
		// out performs conventional loop summation
		final long maxcount = 100;
		final long summaxcount = (maxcount * (maxcount + 1)) / 2;
		List<Long> numList = new ArrayList<>();
		for (long i = 1; i <= maxcount; i++) {
			numList.add(i);
		}

		// sum by loop <we know the answer should be (maxcount * maxcount+1)/2
		long sum = 0l;
		long elapsedTime = System.currentTimeMillis();
		for (Long l : numList) {
			sum += l;
		}
		elapsedTime = System.currentTimeMillis() - elapsedTime;
		assertEquals(sum, summaxcount);

		// sum using stream reduce
		long elapsedTime2 = System.currentTimeMillis();
		sum = numList.stream().reduce(0L, (acc, element) -> acc + element.longValue());
		elapsedTime2 = System.currentTimeMillis() - elapsedTime2;
		assertEquals(sum, summaxcount);

		// sum using parallel stream reduce
		long elapsedTime3 = System.currentTimeMillis();
		sum = numList.parallelStream().reduce(0L, (acc, element) -> acc + element.longValue());
		elapsedTime3 = System.currentTimeMillis() - elapsedTime3;
		assertEquals(sum, summaxcount);

		System.out.println("loop sum was faster than parallel stream/reduce by a factor of "
				+ (float) elapsedTime2 / (float) elapsedTime);

		System.out.println("parallel stream/reduce was faster than single stream by a factor of "
				+ (float) elapsedTime2 / (float) elapsedTime3);

		// now pre-define an accum Predicate and then use it
		BinaryOperator<Long> laccum = (acc, elem) -> acc + elem;
		long lcount = laccum.apply(laccum.apply(laccum.apply(0l, 1l), 2l), 3l);
		System.out.println(lcount);

	}

	public void lamdaComboOps() {
		// get nationality of artists from 1st album with 'Sir' title in name
		Album album = getAlbums().get(0);
		String prefix = "Sir"; // although final not necessary any future
								// assignment will break closure and so it is
								// "effectively" final. So move predicate def to
								// return method with prefix as argument
		Set<String> origins = album.getArtists().stream().filter(ArtistProfile.getArtistNamePredicate(prefix))
				.map(artist -> artist.getNationality()).collect(Collectors.toSet());
		System.out.println(origins);

		// different prefix
		final String prefix2 = "(Deceased)";
		origins = album.getArtists().stream().filter(ArtistProfile.getArtistNamePredicate(prefix2))
				.map(artist -> artist.getNationality()).collect(Collectors.toSet());
		System.out.println(origins);

		// now search across all albums for names of tracks greater than x.y min
		// in length and return as a set
		Set<String> trackNames = getTrackNamesByPlayLength(getAlbums(), 6.0f);
		System.out.println(trackNames);

		// now get stats on Track play lengths using lambda libs in 1.8 for 1st
		// album
		DoubleSummaryStatistics stats = album.getTracksAsStream().mapToDouble(track -> track.getLength())
				.summaryStatistics();
		System.out.printf("Whos Next album stats -> Max: %2.2f, Min %2.2f, Avg: %2.2f, Sum: %2.2f\n", stats.getMax(),
				stats.getMin(), stats.getAverage(), stats.getSum());

		// and then stats across all albums
		stats = getTrackStats(getAlbums());
		System.out.printf("All album stats -> Max: %2.2f, Min %2.2f, Avg: %2.2f, Sum: %2.2f\n", stats.getMax(),
				stats.getMin(), stats.getAverage(), stats.getSum());

	}

	private Set<String> getTrackNamesByPlayLength(final List<Album> albums, final float minLength) {
		Set<String> trackNames = albums.stream().flatMap(album -> album.getTracksAsStream())
				.filter(track -> track.getLength() > minLength).map(track -> track.getName())
				.collect(Collectors.toSet());
		return trackNames;
	}

	private DoubleSummaryStatistics getTrackStats(final List<Album> albums) {
		DoubleSummaryStatistics stats = albums.stream().flatMap(album -> album.getTracksAsStream())
				.mapToDouble(track -> track.getLength()).summaryStatistics();
		return stats;
	}

	public void demoAdvancedLambdaCollections() {
		// demonstrate stream partitioning into 2 collections.
		// - first set all artists with prefix 'Sir' to soloist
		final String prefix = "Sir";
		this.getArtists().stream().filter(ArtistProfile.getArtistNamePredicate(prefix)).forEach(ar -> ar.setSolo(true));

		// now get a partitioned list of artists who are also soloists
		// <true/false> - use method reference syntax classname::method
		Map<Boolean, List<ArtistProfile>> soloistMap = this.getArtists().stream()
				.collect(Collectors.partitioningBy(ArtistProfile::isSolo));

		// print the map partition using lamdba forEach
		soloistMap.forEach((k, v) -> {
			if (k) {
				v.forEach(ar -> System.out.println("soloist Name : " + ar.getName()));
			} else {
				v.forEach(ar -> System.out.println("non soloist Name : " + ar.getName()));
			}
		});

		// group albums by main artist and print using lambda forEach
		Map<ArtistProfile, List<Album>> albumsMappedbyMainArtist = getAlbums().stream()
				.collect(Collectors.groupingBy(album -> album.getMainArtist()));
		albumsMappedbyMainArtist.forEach((k, v) -> {
			System.out.println(k.getName() + " is the main artist for the following albums:");
			v.forEach(album -> System.out.println(album.getName()));
		});

		// print out the artists for an album using conventional loop
		StringBuilder sb = new StringBuilder("[");
		Album album = getAlbums().get(0);
		for (ArtistProfile ap : album.getArtists()) {
			if (sb.length() > 1) {
				sb.append(", ");
			}
			sb.append(ap.getName());
		}
		sb.append("]");
		System.out.println(sb.toString());

		// now do the same using lambda expression
		String result = album.getArtists().stream().map(ArtistProfile::getName)
				.collect(Collectors.joining(", ", "[", "]"));
		System.out.println(result);

		// count the number of albums per main artist and print using lambda
		// forEach
		Map<ArtistProfile, Long> albumsPerArtist = getAlbums().stream()
				.collect(Collectors.groupingBy(album_ -> album_.getMainArtist(), Collectors.counting()));
		albumsPerArtist.forEach((k, v) -> {
			System.out.println(k.getName() + " as a main artist has " + v.longValue() + " albums");
		});

		// revisit the main artist to album use case, except now we want just
		// the album name vs the album object (think mapping within the grouping
		// function). A 2nd "downstream" Collector maps the grouping of albums
		// to album names
		// - group album names by main artist and print using lambda forEach
		Map<ArtistProfile, List<String>> albumNamesMappedbyMainArtist = getAlbums().stream().collect(Collectors
				.groupingBy(album_ -> album_.getMainArtist(), Collectors.mapping(Album::getName, Collectors.toList())));
		albumNamesMappedbyMainArtist.forEach((k, v) -> {
			System.out.println(k.getName() + " is the main artist for the following album names:");
			v.forEach(albumName -> System.out.println(albumName));
		});

	}

	public void customCollectors() {
		final String[] delims = { "[", ", ", "]" };
		// lets start with printing names of artists again, this time using
		// forEach and lambda reduce which outputs to a StringBuilder
		StringBuilder reduced = getArtists().stream().map(ArtistProfile::getName).reduce(new StringBuilder(),
				(builder, name) -> {
					if (builder.length() > 0)
						builder.append(delims[1]);
					builder.append(name);
					return builder;
				}, (left, right) -> left.append(right));

		// wrap result in delimiters [] and print
		reduced.insert(0, delims[0]);
		reduced.append(delims[2]);
		System.out.println(reduced.toString());

		// now use a custom class to simplify the solution [i.e. the builder
		// append and left/right merge ops]
		String combined = getArtists().stream().map(ArtistProfile::getName)
				.reduce(new StringCombiner(delims[1], delims[0], delims[2]), StringCombiner::add, StringCombiner::merge)
				.toString();
		System.out.println(combined);

		// simplify even further with custom Collector class
		String result = getArtists().stream().map(ArtistProfile::getName)
				.collect(new StringCollector(delims[1], delims[0], delims[2]));
		System.out.println(result);

		// and one last variant - using a reducer as a custom collector [bad
		// idea since a new StringCombiner is created for every add plus the add
		// logic is wrong]
		result = getArtists().stream().map(ArtistProfile::getName)
				.collect(Collectors.reducing(new StringCombiner(delims[1], delims[0], delims[2]),
						name -> new StringCombiner(delims[1], delims[0], delims[2]).add(name), StringCombiner::merge))
				.toString();
		System.out.println(result);

	}

	public void moreCollectionNiceties() {
		// cache checking for value - use new computeIfAbsent() method and avoid
		// initial null check
		// - first convert artist list to a map with artist name as key, its
		// object as value
		Map<String, ArtistProfile> artistCache = getArtists().stream()
				.collect(Collectors.toMap(ArtistProfile::getName, artistprofile -> artistprofile));

		// now lookup artist John Petz and if he doesn't exist create him in
		// cache using new Map method computeIfAbsent()
		String key = "John Petz";
		ArtistProfile ap = artistCache.computeIfAbsent(key,
				value -> new ArtistProfile(key, "Toledo, OH", "freelance", "American"));
		System.out.printf("New Artist <key %s value %s>\n", key, ap.getName());
		ap = artistCache.get(key);
		System.out.printf("get cache confirm:  Artist <key %s value %s>\n", key, ap.getName());

		// iterate over a Map to count main artist's albums
		// -old way
		Map<ArtistProfile, Integer> countOfAlbums = new HashMap<>();
		if (this.albumsByArtist.isPresent()) {
			for (Map.Entry<ArtistProfile, List<Album>> e : this.albumsByArtist.get()) {
				ArtistProfile apKey = e.getKey();
				List<Album> albums = e.getValue();
				countOfAlbums.put(apKey, albums.size());
			}
		}
		countOfAlbums.forEach((ArtistProfile k, Integer v) -> {
			System.out.printf("%s is on %d albums\n", k.getName(), v.intValue());
		});

		// now the new way
		countOfAlbums.clear();
		if (this.albumsByArtist.isPresent()) {
			albumsByArtist.get().forEach((e) -> {
				countOfAlbums.put(e.getKey(), e.getValue().size());
			});
		}
		countOfAlbums.forEach((ArtistProfile k, Integer v) -> {
			System.out.printf("recount:  %s is on %d albums\n", k.getName(), v.intValue());
		});

	}

	public void moreStreamParalleism() {
		// serial track lengths across all albums [flatten albums down to Tracks
		// and then get their play lengths]
		long startTime = System.currentTimeMillis();
		double sumTrackLengths = getAlbums().stream().flatMap(Album::getTracksAsStream)
				.mapToDouble(AlbumTrack::getLength).sum();
		System.out.printf("serial sum: total track length across all albums: %4.2f in %d ms\n", sumTrackLengths,
				System.currentTimeMillis() - startTime);

		// parallel version
		startTime = System.currentTimeMillis();
		sumTrackLengths = getAlbums().parallelStream().flatMap(Album::getTracksAsStream)
				.mapToDouble(AlbumTrack::getLength).sum();
		System.out.printf("parallel sum: total track length across all albums: %4.2f in %d ms\n", sumTrackLengths,
				System.currentTimeMillis() - startTime);

		// array init using stream
		double[] values = new double[1000];
		final double scaleFactor = 0.5;
		Arrays.parallelSetAll(values, i -> i * scaleFactor);
		for (int i = 0; i < values.length; i++) {
			final double expectedValue = (double) i * scaleFactor;
			assertEquals(expectedValue, values[i]);
		}

		// Monte Carlo sim - showcase parallelism adv

	}

	public static void main(String[] args) {

		// add artists to collection
		Examples example = new Examples();
		example.populateArtists();
		example.createAlbumsFromArtists();
		example.createArtistToAlbumAssociations();

		// test lambda expressions
		example.initialMiscLambdaExpressions();
		example.lamdaComboOps();
		example.lambdaMap();
		example.demoAdvancedLambdaCollections();
		example.customCollectors();
		example.moreCollectionNiceties();
		example.moreStreamParalleism();

	}

	private static void assertEquals(Integer i1, Integer i2) {
		if (i1.intValue() != i2.intValue())
			throw new AssertionError("values are not equal");
	}

	private static void assertEquals(Long i1, Long i2) {
		if (i1.longValue() != i2.longValue())
			throw new AssertionError("values are not equal");
	}

	private static void assertEquals(double d1, double d2) {
		final double epsilon = 0.00000001d;
		boolean eq = Math.abs(d1 - d2) < epsilon;
		if (!eq)
			throw new AssertionError("values are not equal");
	}
}
