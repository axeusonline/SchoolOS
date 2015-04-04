package com.ies.schoolos.type;

import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;

public class Semester extends IndexedContainer{

	private static final long serialVersionUID = 1L;
	
	private static String[] semesters = {"1","2","3"};
	
	public Semester() {
		initContainer();
	}
 
	@SuppressWarnings("unchecked")
	private void initContainer(){
	   addContainerProperty("name", String.class,null);
	   for (int i = 0; i < semesters.length; i++) {
	        Item item = addItem(i);
	        item.getItemProperty("name").setValue(semesters[i]);
	   }
	}
	
	public static String getNameTh(int index){
		return semesters[index];
	}
	
	/*public static String getNameEn(int index){
		return semesters[index];
	}*/
}
