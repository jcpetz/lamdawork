package com.newfeatures;

public class DefaultChildImpl implements DefaultChild {
	private String message = "unassigned";

	@Override
	public void assignMessage(String message) {
		this.message = message;

	}

	@Override
	public String getLastMessage() {
		return message;
	}
	
	// override default welcome
	@Override
	public void welcome() {
		message = "Hello from DefaultChildImpl";
	}

}
