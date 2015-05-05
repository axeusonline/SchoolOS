package com.ies.schoolos.type;

import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;

public class SeverityType extends IndexedContainer{

	private static final long serialVersionUID = 1L;
	
	private static String[] severities = {"เบา","ปานกลาง","หนัก"};

	public SeverityType() {
		initContainer();
	}
 
	@SuppressWarnings("unchecked")
	private void initContainer(){
	   addContainerProperty("name", String.class,null);
	   for (int i = 0; i < severities.length; i++) {
	        Item item = addItem(i);
	        item.getItemProperty("name").setValue(severities[i]);
	   }
	}
	
	public static String getNameTh(int index){
		return severities[index];
	}
	
	/*public static String getNameEn(int index){
		return days[index];
	}*/
}
