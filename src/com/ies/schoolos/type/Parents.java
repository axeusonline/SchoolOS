package com.ies.schoolos.type;

import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;

public class Parents extends IndexedContainer{

	private static final long serialVersionUID = 1L;
	
	private static String[] parents = {"บิดา","มารดา","อื่น ๆ"};
	private static String[] parents_en = {"Father.","Mother.","Other"};

	public Parents() {
		initContainer();
	}
 
	@SuppressWarnings("unchecked")
	private void initContainer(){
	   addContainerProperty("name", String.class,null);
	   for (int i = 0; i < parents.length; i++) {
	        Item item = addItem(i);
	        item.getItemProperty("name").setValue(parents[i]);
	   }
	}
	
	public static String getNameTh(int index){
		return parents[index];
	}
	
	public static String getNameEn(int index){
		return parents_en[index];
	}
}
