package com.newfeatures;

public interface CreateArtistFunction<A, B, C, D, R> {
	R apply(A a, B b, C c, D d);
}
