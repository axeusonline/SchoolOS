package com.ies.schoolos.type;

import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;

public class UserStatus extends IndexedContainer{

	private static final long serialVersionUID = 1L;
	
	private static String[] userStatuses = {"เปิดการใช้งาน","ปิดการใช้"};

	public UserStatus() {
		initContainer();
	}
 
	@SuppressWarnings("unchecked")
	private void initContainer(){
	   addContainerProperty("name", String.class,null);
	   for (int i = 0; i < userStatuses.length; i++) {
	        Item item = addItem(i);
	        item.getItemProperty("name").setValue(userStatuses[i]);
	   }
	}
	
	public static String getNameTh(int index){
		return userStatuses[index];
	}
	
	/*public static String getNameEn(int index){
		return userStatuses[index];
	}*/
}
