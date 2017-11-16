package com.asyncsupport;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.streams.Examples;
import com.streams.Examples.Album;
import com.streams.Examples.AlbumTrack;
import com.streams.Examples.ArtistProfile;

public class FutureAlbums {

	// simulate web services repo of Albums and artists
	private static final Examples albumrepos;
	static {
		albumrepos = new Examples();
		albumrepos.populateArtists();
		albumrepos.createAlbumsFromArtists();
		albumrepos.createArtistToAlbumAssociations();
	}
	
	public static Examples getAllRepos() {
		return albumrepos;
	}
	
	public static class Credentials {
		private final String userName;
		private final String password;
		private final String mode;
		private boolean isAuthenticated = false;

		private Credentials(String mode, String userName, String password) {
			super();
			this.mode = mode;
			this.userName = userName;
			this.password = password;
		}

		public String getUserName() {
			return userName;
		}

		public String getPassword() {
			return password;
		}

		public String getMode() {
			return mode;
		}

		public static int getMaxwaittimems() {
			return maxWaitTimems;
		}

		public boolean isAuthenticated() {
			return isAuthenticated;
		}

		public static Future<Credentials> login(String mode, String userName, String password, long waitTimems) {
			// tbd
			CompletableFuture<Credentials> completableFuture = new CompletableFuture<>();
			Executors.newCachedThreadPool().submit(() -> {
				if (waitTimems > maxWaitTimems)
					completableFuture.completeExceptionally(new Exception("can't wait that long"));
				Credentials creds = new Credentials(mode, userName, password);
				Thread.sleep(waitTimems);
				creds.isAuthenticated = true;
				completableFuture.complete(creds);
				return null;
			});
			return completableFuture;
		}

		public static CompletableFuture<Credentials> login2(String mode, String userName, String password,
				long waitTimems) {
			// tbd
			CompletableFuture<Credentials> completableFuture = new CompletableFuture<>();
			Executors.newCachedThreadPool().submit(() -> {
				if (waitTimems > maxWaitTimems)
					completableFuture.completeExceptionally(new Exception("can't wait that long"));
				Credentials creds = new Credentials(mode, userName, password);
				Thread.sleep(waitTimems);
				creds.isAuthenticated = true;
				completableFuture.complete(creds);
				return null;
			});
			return completableFuture;
		}
	}

	private static final int maxWaitTimems = 5000;

	private Future<String> simpleFuture(String message) {
		ExecutorService executor = Executors.newFixedThreadPool(1);

		Future<String> future = executor.submit(() -> {
			try {
				TimeUnit.SECONDS.sleep(2);
				return message;
			} catch (InterruptedException e) {
				throw new IllegalStateException("task interrupted", e);
			}
		});
		return future;
	}

	static private Predicate<AlbumTrack> getTrackNamePredicate(String name) {
		final Predicate<AlbumTrack> trackPredicate = track -> name.equals(track.getName());
		return trackPredicate;
	}

	static private Predicate<ArtistProfile> getArtistNamePredicate(String name) {
		final Predicate<ArtistProfile> artistPredicate = artist -> name.equals(artist.getName());
		return artistPredicate;
	}

	static private Predicate<Album> getAlbumArtistsPredicate(final List<ArtistProfile> artistList) {
		final Predicate<Album> albumPredicate = album -> {
			return album.getArtists().containsAll(artistList);
		};
		return albumPredicate;
	}

	static private Predicate<Album> getAlbumTracksPredicate(final List<AlbumTrack> trackList) {
		final Predicate<Album> albumPredicate = album -> {
			return album.getTracks().containsAll(trackList);
		};
		return albumPredicate;
	}

	public static Future<List<AlbumTrack>> lookupTracks(String trackName, Credentials creds) throws Exception {
		// first verify credentials were authenticated
		if (!creds.isAuthenticated) {
			throw new Exception(creds.getUserName() + "/" + creds.getPassword() + " failed to authenticate");
		}

		// submit future task to query for album tracks with specified name and
		// return immediately
		ExecutorService executor = Executors.newFixedThreadPool(1);
		Future<List<AlbumTrack>> trackList = executor.submit(() -> {
			try {
				TimeUnit.SECONDS.sleep(2);
				List<AlbumTrack> tracks = albumrepos.getAlbums().stream().flatMap(album -> album.getTracksAsStream())
						.filter(getTrackNamePredicate(trackName)).collect(Collectors.toList());
				return tracks.isEmpty() ? null : tracks;
			} catch (InterruptedException e) {
				throw new IllegalStateException("task interrupted", e);
			}
		});
		return trackList;

	}

	public static CompletableFuture<List<AlbumTrack>> lookupTracks2(String trackName, Credentials creds) {
		// first verify credentials were authenticated
		if (!creds.isAuthenticated) {
			return null;
		}

		// submit future task to query for album tracks with specified name and
		// return immediately
		CompletableFuture<List<AlbumTrack>> completableFuture = new CompletableFuture<>();
		Executors.newCachedThreadPool().submit(() -> {
			try {
				TimeUnit.SECONDS.sleep(2);
				List<AlbumTrack> tracks = albumrepos.getAlbums().stream().flatMap(album -> album.getTracksAsStream())
						.filter(getTrackNamePredicate(trackName)).collect(Collectors.toList());
				completableFuture.complete(tracks);
				return null;
			} catch (InterruptedException e) {
				throw new IllegalStateException("task interrupted", e);
			}
		});
		return completableFuture;

	}

	public static Future<List<ArtistProfile>> lookupArtists(String trackName, Credentials creds) throws Exception {
		// first verify credentials were authenticated
		if (!creds.isAuthenticated) {
			throw new Exception(creds.getUserName() + "/" + creds.getPassword() + " failed to authenticate");
		}

		// submit future task to query for artists with specified name and
		// return immediately
		ExecutorService executor = Executors.newFixedThreadPool(1);
		Future<List<ArtistProfile>> artistList = executor.submit(() -> {
			try {
				TimeUnit.SECONDS.sleep(2);
				List<ArtistProfile> artists = albumrepos.getAlbums().stream()
						.flatMap(album -> album.getArtists().stream()).filter(getArtistNamePredicate(trackName))
						.collect(Collectors.toList());
				return artists.isEmpty() ? null : artists;
			} catch (InterruptedException e) {
				throw new IllegalStateException("task interrupted", e);
			}
		});
		return artistList;

	}

	public static CompletableFuture<List<ArtistProfile>> lookupArtists2(String trackName, Credentials creds) {
		// first verify credentials were authenticated
		if (!creds.isAuthenticated) {
			return null;
		}

		// submit future task to query for artists with specified name and
		// return immediately
		CompletableFuture<List<ArtistProfile>> completableFuture = new CompletableFuture<>();
		Executors.newCachedThreadPool().submit(() -> {
			try {
				TimeUnit.SECONDS.sleep(2);
				List<ArtistProfile> artists = albumrepos.getAlbums().stream()
						.flatMap(album -> album.getArtists().stream()).filter(getArtistNamePredicate(trackName))
						.collect(Collectors.toList());
				completableFuture.complete(artists);
				return null;
			} catch (InterruptedException e) {
				throw new IllegalStateException("task interrupted", e);
			}
		});
		return completableFuture;

	}

	public Future<String> nextAlbum(String albumName, int waitTimems) throws InterruptedException {
		CompletableFuture<String> completableFuture = new CompletableFuture<>();
		Executors.newCachedThreadPool().submit(() -> {
			if (waitTimems > maxWaitTimems)
				completableFuture.completeExceptionally(new Exception("can't wait that long"));
			Thread.sleep(waitTimems);
			completableFuture.complete(albumName);
			return null;
		});
		return completableFuture;
	}

	public Optional<Album> lookupAlbumByTrackandArtist(String trackName, String artistName) {
		// use conventional Future<> to operate 4 web service calls concurrently
		// <2 login, 2 queries>
		String userName = "jpetz";
		String password = "joker";
		Future<Credentials> trackLogin = FutureAlbums.Credentials.login("track", userName, password, 3000);
		Future<Credentials> artistLogin = FutureAlbums.Credentials.login("artist", userName, password, 3000);
		try {
			Future<List<AlbumTrack>> waitOnTracks = lookupTracks(trackName, trackLogin.get());
			Future<List<ArtistProfile>> waitOnArtists = lookupArtists(artistName, artistLogin.get());
			List<AlbumTrack> trackList = waitOnTracks.get();
			if (trackList == null || trackList.isEmpty()) {
				System.out.printf("no tracks found with name '%s'\n", trackName);
				return Optional.empty();
			}
			trackList.forEach(track -> System.out.printf("found track: '%s'\n", track.getName()));
			List<ArtistProfile> artistList = waitOnArtists.get();
			if (artistList == null || artistList.isEmpty()) {
				System.out.printf("no artists found with name '%s'\n", artistName);
				return Optional.empty();
			}
			artistList.forEach(artist -> System.out.printf("found artist: '%s'\n", artist.getName()));

			// now correlate tracks with artists to see if any album(s) exist
			// which satisfies both
			List<Album> albums = albumrepos.getAlbums().stream().filter(getAlbumArtistsPredicate(artistList))
					.filter(getAlbumTracksPredicate(trackList)).collect(Collectors.toList());

			// return result
			return albums.isEmpty() ? Optional.empty() : Optional.of(albums.get(0));

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return Optional.empty();

	}

	public Optional<Album> createAlbumByTrackandArtist(String trackName, String artistName, String expectedAlbumName) {
		// use jdk1.8 CompletableFuture<> to operate 4 web service calls
		// concurrently <2 login, 2 queries>
		String userName = "jpetz";
		String password = "joker";
		try {
			// get track login followed by track list lookup
			CompletableFuture<List<AlbumTrack>> trackLookup = FutureAlbums.Credentials
					.login2("track", userName, password, 3000)
					.thenCompose(trackLogin -> lookupTracks2(trackName, trackLogin));

			// get artist login, followed by artist lookup and finally combine to create a single album <note:  we don't know the album name so use the specified expected name>.
			Album album = FutureAlbums.Credentials.login2("track", userName, password, 3000)
					.thenCompose(artistLogin -> lookupArtists2(artistName, artistLogin))
					.thenCombine(trackLookup, (artists, tracks) -> new Album(expectedAlbumName, artists, tracks)).join();
			return Optional.of(album);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return Optional.empty();

	}

	public static void main(String[] args) {
		FutureAlbums fa = new FutureAlbums();
		try {
			String albumName = "'Future Days of Future Passed'";
			fa.testSimpleFuture(albumName);

			// test java8 CompletableFuture
			System.out.println("lookup an album based on track and artist using conventional Future<>...");
			final long startTime = System.currentTimeMillis();
			Optional<Album> album = fa.lookupAlbumByTrackandArtist("From The Beginning", "Greg Lake");
			album.ifPresent(al -> System.out.printf("lookup produced album name '%s'...lookup time was %d msec\n",
					al.getName(), System.currentTimeMillis() - startTime));

			System.out.println("now create an album based on track and artist using jdk1.8 CompletableFuture<>...");
			final long startTime2 = System.currentTimeMillis();
			album = fa.createAlbumByTrackandArtist("From The Beginning", "Greg Lake", "Trilogy-Cut");
			album.ifPresent(al -> System.out.printf("created album '%s' with %d tracks and %d artists...lookup time was %d msec\n",
					al.getName(), al.getTracks().size(), al.getArtists().size(), System.currentTimeMillis() - startTime2));

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void testSimpleFuture(String albumName) {
		long startTime = System.currentTimeMillis();
		Future<String> futureAlbumName = simpleFuture(albumName);
		try {
			while (!futureAlbumName.isDone()) {
				System.out.println(
						"still waiting for new album via classic Future<> ...nothing else to do until its completed!!");
				delay(100);
			}
			System.out.printf("new album request '%s' was created in %d (msec)\n", futureAlbumName.get(),
					System.currentTimeMillis() - startTime);

			System.out.printf("requesting future album with name %s\n", albumName);
			startTime = System.currentTimeMillis();
			Future<String> nextAlbum = nextAlbum(albumName, 1000);
			while (!nextAlbum.isDone()) {
				System.out.println("still waiting for that new album...nothing else to do until its completed!!");
				Thread.sleep(100);
			}
			System.out.printf("new album request '%s' was created in %d (msec)\n", nextAlbum.get(),
					System.currentTimeMillis() - startTime);

		} catch (ExecutionException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void delay(long msec) {
		try {
			Thread.sleep(msec);
		} catch (InterruptedException e) {
		}
	}
}
