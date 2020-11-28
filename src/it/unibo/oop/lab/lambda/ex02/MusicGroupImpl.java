package it.unibo.oop.lab.lambda.ex02;

import static org.junit.jupiter.api.DynamicTest.stream;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Collectors.*;
import java.util.stream.Stream;

/**
 *
 */
public final class MusicGroupImpl implements MusicGroup {

    private final Map<String, Integer> albums = new HashMap<>();
    private final Set<Song> songs = new HashSet<>();

    @Override
    public void addAlbum(final String albumName, final int year) {
        this.albums.put(albumName, year);
    }

    @Override
    public void addSong(final String songName, final Optional<String> albumName, final double duration) {
        if (albumName.isPresent() && !this.albums.containsKey(albumName.get())) {
            throw new IllegalArgumentException("invalid album name");
        }
        this.songs.add(new MusicGroupImpl.Song(songName, albumName, duration));
    }

    @Override
    public Stream<String> orderedSongNames() {
    	return this.songs
    			.stream()
    			.map(a -> a.songName)
    			.sorted();
    }

    @Override
    public Stream<String> albumNames() {
       return this.albums.keySet().stream();
    } 

    @Override
    public Stream<String> albumInYear(final int year) {
    	return albumNames()
    			.filter(alb -> this.albums.get(alb).equals(year))
    			.distinct();
    }

    @Override
    public int countSongs(final String albumName) {
        /*return (int) albumNames()
        			.filter(alb ->  this.songs.equals(alb.equals(albumName)))
        			.filter(album -> this.songs.equals(album))
        			.count(); */
    	return (int)this.songs.stream()
    			.filter(song -> song.albumName.isPresent())
    			.filter(song -> song.albumName.get().equals(albumName))
    			.count();
    }

    @Override
    public int countSongsInNoAlbum() {
    	return (int) this.songs
    					.stream()
    					.filter(song ->  song.albumName.isEmpty())
    					.count();
    }

    @Override
    public OptionalDouble averageDurationOfSongs(final String albumName) {	
    	/* return OptionalDouble.of(this.songs.stream()
    					.filter(song -> song.albumName.isPresent())
    					.map(song -> song.duration)
    					.reduce(0.0, Double::sum) / countSongs(albumName)); */
    	return OptionalDouble.of(this.songs.stream()
				.filter(song -> song.albumName.equals(Optional.of(albumName)))
				.map(song -> song.duration)
				.reduce(0.0, Double::sum) / countSongs(albumName));
    }

    @Override
    public Optional<String> longestSong() {
        return Optional.of(this.songs.stream()
        		.max((a, b) -> Double.compare(a.duration, b.duration))
        		.get().getSongName()); 
    }

    @Override
    public Optional<String> longestAlbum() {
        return Optional.of(this.songs.stream()
        		.filter(song -> song.albumName.isPresent())
        		.collect(Collectors.groupingBy(Song::getAlbumName,Collectors.summingDouble(Song::getDuration)))
        // {Optional[II]=10.1, Optional[untitled]=12.0, Optional[III]=7.800000000000001, Optional[I]=11.1} 
        		.entrySet()
        // Optional[II]=10.1, Optional[untitled]=12.0, Optional[III]=7.800000000000001, Optional[I]=11.1
        		.stream() //ora ho delle mappe 
        	/*	.map(alb -> alb.getValue())
        		.collect(Collectors.maxBy((d1, d2) -> Double.compare(d1, d2)))); */ // Funziona ma non ottengo il nome ma il double
        		.max((a, b) -> Double.compare(a.getValue(), b.getValue()))
        		.get().getKey().get());
    }

    private static final class Song {

        private final String songName;
        private final Optional<String> albumName;
        private final double duration;
        private int hash;

        Song(final String name, final Optional<String> album, final double len) {
            super();
            this.songName = name;
            this.albumName = album;
            this.duration = len;
        }

        public String getSongName() {
            return songName;
        }

        public Optional<String> getAlbumName() {
            return albumName;
        }

        public double getDuration() {
            return duration;
        }

        @Override
        public int hashCode() {
            if (hash == 0) {
                hash = songName.hashCode() ^ albumName.hashCode() ^ Double.hashCode(duration);
            }
            return hash;
        }

        @Override
        public boolean equals(final Object obj) {
            if (obj instanceof Song) {
                final Song other = (Song) obj;
                return albumName.equals(other.albumName) && songName.equals(other.songName)
                        && duration == other.duration;
            }
            return false;
        }

        @Override
        public String toString() {
            return "Song [songName=" + songName + ", albumName=" + albumName + ", duration=" + duration + "]";
        }

    }

}
