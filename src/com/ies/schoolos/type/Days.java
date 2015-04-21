package com.ies.schoolos.type;

import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;

public class Days extends IndexedContainer{

	private static final long serialVersionUID = 1L;
	
	private static String[] dayName = {"อาทิตย์","จันทร์","อังคาร","พุธ","พฤหัสบดี","ศุกร์","เสาร์"};

	public Days() {
		initContainer();
	}
 
	@SuppressWarnings("unchecked")
	private void initContainer(){
	   addContainerProperty("name", String.class,null);
	   for (int i = 0; i < dayName.length; i++) {
	        Item item = addItem(i);
	        item.getItemProperty("name").setValue(dayName[i]);
	   }
	}
	
	public static String getNameTh(int index){
		return dayName[index];
	}
	
	/*public static String getNameEn(int index){
		return days[index];
	}*/
}
