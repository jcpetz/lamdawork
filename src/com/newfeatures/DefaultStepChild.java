package com.newfeatures;

public class DefaultStepChild implements DefaultParent {
   private String message = "unassigned";
   
	@Override
	public void assignMessage(String message) {
		this.message = message;

	}

	@Override
	public String getLastMessage() {
		return message;
	}

}
