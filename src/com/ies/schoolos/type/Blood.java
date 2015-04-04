package com.ies.schoolos.type;

import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;

public class Blood extends IndexedContainer{

	private static final long serialVersionUID = 1L;
	
	private static String[] bloods = {"เอ","บี","เอบี","โอ","ไม่ระบุ"};
	
	public Blood() {
		initContainer();
	}
 
	@SuppressWarnings("unchecked")
	private void initContainer(){
	   addContainerProperty("name", String.class,null);
	   for (int i = 0; i < bloods.length; i++) {
	        Item item = addItem(i);
	        item.getItemProperty("name").setValue(bloods[i]);
	   }
	}
	
	public static String getNameTh(int index){
		return bloods[index];
	}
	
	/*public static String getNameEn(int index){
		return bloods[index];
	}*/
}
