package com.streams;

@FunctionalInterface
public interface ArtistComparator<T> {
   public int bandNameLength(T ap);
}
