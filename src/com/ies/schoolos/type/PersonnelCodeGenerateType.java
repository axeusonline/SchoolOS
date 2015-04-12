package com.ies.schoolos.type;

import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;

public class PersonnelCodeGenerateType  extends IndexedContainer{

	private static final long serialVersionUID = 1L;
	
	private static String[] personnelCodeGenerateTypes = {"อัตโนมัติ","กำหนดเอง"};

	public PersonnelCodeGenerateType() {
		initContainer();
	}
 
	@SuppressWarnings("unchecked")
	private void initContainer(){
	   addContainerProperty("name", String.class,null);
	   for (int i = 0; i < personnelCodeGenerateTypes.length; i++) {
	        Item item = addItem(i);
	        item.getItemProperty("name").setValue(personnelCodeGenerateTypes[i]);
	   }
	}
	
	public static String getNameTh(int index){
		return personnelCodeGenerateTypes[index];
	}

}
