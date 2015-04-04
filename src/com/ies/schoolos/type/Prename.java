package com.ies.schoolos.type;

import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;

public class Prename extends IndexedContainer{

	private static final long serialVersionUID = 1L;
	
	private static String[] prenames = {"เด็กหญิง","เด็กชาย","นาย","นางสาว","นาง"};
	private static String[] prenames_en = {"Miss","Mstr","Mr","Miss","Mrs"};

	public Prename() {
		initContainer();
	}
 
	@SuppressWarnings("unchecked")
	private void initContainer(){
	   addContainerProperty("name", String.class,null);
	   for (int i = 0; i < prenames.length; i++) {
	        Item item = addItem(i);
	        item.getItemProperty("name").setValue(prenames[i]);
	   }
	}
	
	public static String getNameTh(int index){
		return prenames[index];
	}
	
	public static String getNameEn(int index){
		return prenames_en[index];
	}
}
