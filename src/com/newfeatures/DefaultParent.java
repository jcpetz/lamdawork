package com.newfeatures;

public interface DefaultParent {
	public void assignMessage(String message);
	
	public String getLastMessage();
	
	// define a "virtual" method with override rules ~ C++.  The method has default behavior so its not "pure virtual" as C++ defines
	public default void welcome() { 
		assignMessage("hello from DefaultParent");
	}
}
