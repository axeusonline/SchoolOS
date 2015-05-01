package com.ies.schoolos.type;

import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;

public class StudentPayerCourse extends IndexedContainer{

	private static final long serialVersionUID = 1L;
	
	private static String[] strudentComeWith = {"บิดา","มารดา","ผู้ปกครอง"};

	public StudentPayerCourse() {
		initContainer();
	}
 
	@SuppressWarnings("unchecked")
	private void initContainer(){
	   addContainerProperty("name", String.class,null);
	   for (int i = 0; i < strudentComeWith.length; i++) {
	        Item item = addItem(i);
	        item.getItemProperty("name").setValue(strudentComeWith[i]);
	   }
	}
	
	public static String getNameTh(int index){
		return strudentComeWith[index];
	}
	
	/*public static String getNameEn(int index){
		return strudentComeWith[index];
	}*/
}
