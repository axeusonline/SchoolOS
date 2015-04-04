package com.ies.schoolos.type;

import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;

public class MaritalStatus extends IndexedContainer{

	private static final long serialVersionUID = 1L;
	
	private static String[] statuses = {"โสด","สมรส"};

	public MaritalStatus() {
		initContainer();
	}
 
	@SuppressWarnings("unchecked")
	private void initContainer(){
	   addContainerProperty("name", String.class,null);
	   for (int i = 0; i < statuses.length; i++) {
	        Item item = addItem(i);
	        item.getItemProperty("name").setValue(statuses[i]);
	   }
	}
	
	public static String getNameTh(int index){
		return statuses[index];
	}
	
	/*public static String getNameEn(int index){
		return statuses[index];
	}*/
}
