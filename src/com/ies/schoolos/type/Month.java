package com.ies.schoolos.type;

import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;

public class Month  extends IndexedContainer{

	private static final long serialVersionUID = 1L;
	
	private static String[] months = {"มกราคม","กุมภาพันธ์","มีนาคม","เมษายน","พฤษภาคม","มิถุนายน","กรกฏาคม","สิงหาคม","กันยายน","ตุลาคม","พฤษจิกายน","ธันวาคม"};

	public Month() {
		initContainer();
	}
 
	@SuppressWarnings("unchecked")
	private void initContainer(){
	   addContainerProperty("name", String.class,null);
	   for (int i = 0; i < months.length; i++) {
	        Item item = addItem(i);
	        item.getItemProperty("name").setValue(months[i]);
	   }
	}
	
	public static String getNameTh(int index){
		return months[index];
	}

}
