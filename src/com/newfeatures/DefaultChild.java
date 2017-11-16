package com.newfeatures;

public interface DefaultChild extends DefaultParent {
   // override default welcome()
   @Override
   public default void welcome() {
	   this.assignMessage("Hello from DefaultChild");
   }
   
}