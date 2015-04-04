package com.ies.schoolos.type;

import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;

public class AliveStatus extends IndexedContainer{

	private static final long serialVersionUID = 1L;
	
	private static String[] aliveStatuses = {"มีชีวิต","เสียชีวิต"};

	public AliveStatus() {
		initContainer();
	}
 
	@SuppressWarnings("unchecked")
	private void initContainer(){
	   addContainerProperty("name", String.class,null);
	   for (int i = 0; i < aliveStatuses.length; i++) {
	        Item item = addItem(i);
	        item.getItemProperty("name").setValue(aliveStatuses[i]);
	   }
	}
	
	public static String getNameTh(int index){
		return aliveStatuses[index];
	}
	
	/*public static String getNameEn(int index){
		return aliveStatuses[index];
	}*/
}
