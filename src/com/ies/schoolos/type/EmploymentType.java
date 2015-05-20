package com.ies.schoolos.type;

import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;

public class EmploymentType extends IndexedContainer{

	private static final long serialVersionUID = 1L;
	
	private static String[] employeeTypes = {"ประจำ","ชั่วคราว","รายชั่วโมง","ข้าราชการช่วย"};

	public EmploymentType() {
		initContainer();
	}
 
	@SuppressWarnings("unchecked")
	private void initContainer(){
	   addContainerProperty("name", String.class,null);
	   for (int i = 0; i < employeeTypes.length; i++) {
	        Item item = addItem(i);
	        item.getItemProperty("name").setValue(employeeTypes[i]);
	   }
	}
	
	public static String getNameTh(int index){
		return employeeTypes[index];
	}
	
	/*public static String getNameEn(int index){
		return employeeTypes[index];
	}*/
}
