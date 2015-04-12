package com.ies.schoolos.type;

import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;

public class PersonnelStatus extends IndexedContainer{

	private static final long serialVersionUID = 1L;
	
	private static String[] personalStatuses = {"ทำงาน","ลาออก","จำหน่ายออก"};
	
	public PersonnelStatus() {
		initContainer();
	}
 
	@SuppressWarnings("unchecked")
	private void initContainer(){
	   addContainerProperty("name", String.class,null);
	   for (int i = 0; i < personalStatuses.length; i++) {
	        Item item = addItem(i);
	        item.getItemProperty("name").setValue(personalStatuses[i]);
	   }
	}
	
	public static String getNameTh(int index){
		return personalStatuses[index];
	}
	
	/*public static String getNameEn(int index){
		return personalStatuses[index];
	}*/
}
