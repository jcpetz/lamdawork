package com.newfeatures;

public class DefaultParentImpl implements DefaultParent {

	private String message = "not assigned";

	@Override
	public void assignMessage(String message) {
		this.message = message;	
	}
	
	@Override
	public String getLastMessage() {
		return message;
	}
	
	// now override default welcome()
	@Override
	public void welcome() {
		message = "hello from DefaultParentImpl";
	}
	
	public static void main(String[] args) {
		// default parent
		DefaultParent df = new DefaultParentImpl();
		df.welcome();
		System.out.println(df.getLastMessage());
		
		// default parent defined at child subclass level
		DefaultParent dfsub = new DefaultChildImpl();
		dfsub.welcome();
		System.out.println(df.getLastMessage());
		
		// default child interface
		DefaultChild dfchild = new DefaultChildImpl();
		dfchild.welcome();
		System.out.println(dfchild.getLastMessage());
		
		// default step-child interface [uses default welcome]
		DefaultStepChild dfstepchild = new DefaultStepChild();
		dfstepchild.welcome();
		System.out.println(dfstepchild.getLastMessage());
	}

}
