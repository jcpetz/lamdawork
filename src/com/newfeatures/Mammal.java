package com.newfeatures;

public interface Mammal {
	public void setDiet(String food);

	public String getDiet();

	public default String givesBirth() {
		return "via uterus";
	}
}
