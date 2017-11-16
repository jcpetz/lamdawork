package com.newfeatures;

public interface NonMammal {
	public void setDiet(String food);

	public String getDiet();

	public default String givesBirth() {
		return "lays eggs";
	}
}
