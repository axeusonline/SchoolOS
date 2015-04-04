package com.ies.schoolos.type;

import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;

public class PeopleIdType extends IndexedContainer{

	private static final long serialVersionUID = 1L;

	private static String[] peopleidTypes = {"บัตรประชาชน","พาสปอร์ต"};
	
	public PeopleIdType() {
		initContainer();
	}
 
	@SuppressWarnings("unchecked")
	private void initContainer(){
	   addContainerProperty("name", String.class,null);
	   for (int i = 0; i < peopleidTypes.length; i++) {
	        Item item = addItem(i);
	        item.getItemProperty("name").setValue(peopleidTypes[i]);
	   }
	}
	
	public static String getNameTh(int index){
		return peopleidTypes[index];
	}
	
	/*public static String getNameEn(int index){
		return religions_en[index];
	}*/

}
