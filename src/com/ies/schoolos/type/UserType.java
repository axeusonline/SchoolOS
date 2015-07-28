package com.ies.schoolos.type;

import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;

public class UserType extends IndexedContainer{

	private static final long serialVersionUID = 1L;
	
	public static final int ADMIN = 0;
	public static final int EMPLOYEE = 1;
	public static final int STUDENT = 2;
	
	private static String[] userTypes = {"แอดมิน","เจ้าหน้าที่","นักเรียน"};

	public UserType() {
		initContainer();
	}
 
	@SuppressWarnings("unchecked")
	private void initContainer(){
	   addContainerProperty("name", String.class,null);
	   for (int i = 0; i < userTypes.length; i++) {
	        Item item = addItem(i);
	        item.getItemProperty("name").setValue(userTypes[i]);
	   }
	}
	
	public static String getNameTh(int index){
		return userTypes[index];
	}
	
	/*public static String getNameEn(int index){
		return userTypes[index];
	}*/
}
