/**
 * 
 */
package com.newfeatures;

/**
 * @author petzj
 *
 */
public class Platypus implements Mammal, NonMammal {
	private String diet = "unassigned";

	/* (non-Javadoc)
	 * @see com.newfeatures.Mammal#setDiet(java.lang.String)
	 */
	@Override
	public void setDiet(String food) {
		this.diet = food;

	}

	/* (non-Javadoc)
	 * @see com.newfeatures.Mammal#getDiet()
	 */
	@Override
	public String getDiet() {
		// TODO Auto-generated method stub
		return diet;
	}
	
	// override givesBirth() method to resolve same default conflict across 2 interfaces inherited - use NonMammal behavior
	@Override
	public String givesBirth() {
		return NonMammal.super.givesBirth();
	}
	
	public static void main(String[] args) {
		Platypus platypus = new Platypus();
		Mammal mammal = platypus;
		System.out.println(mammal.givesBirth());
		NonMammal nonMammal = platypus;
		System.out.println(nonMammal.givesBirth());
		
	}

}
