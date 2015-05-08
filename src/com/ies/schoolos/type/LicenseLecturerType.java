package com.ies.schoolos.type;

import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;

public class LicenseLecturerType extends IndexedContainer{

	private static final long serialVersionUID = 1L;
	
	private static String[] licenseLecturerTypes = {"ใบประกอบวิชาชีพ","ใบอนุญาตปฎิบัติการสอน","บุคคลที่รับการยกเว้น"};

	public LicenseLecturerType() {
		initContainer();
	}
 
	@SuppressWarnings("unchecked")
	private void initContainer(){
	   addContainerProperty("name", String.class,null);
	   for (int i = 0; i < licenseLecturerTypes.length; i++) {
	        Item item = addItem(i);
	        item.getItemProperty("name").setValue(licenseLecturerTypes[i]);
	   }
	}
	
	public static String getNameTh(int index){
		return licenseLecturerTypes[index];
	}
	
	/*public static String getNameEn(int index){
		return licenseLecturerTypes_en[index];
	}*/
}
