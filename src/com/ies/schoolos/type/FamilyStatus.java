package com.ies.schoolos.type;

import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;

public class FamilyStatus extends IndexedContainer{

	private static final long serialVersionUID = 1L;
	
	private static String[] familyStatuses = {"อยู่ด้วยกัน","แยกกันอยู่","หย่าร้าง","บิดาถึงแก่กรรม","มารดาถึงแก่กรรม","เสียชีวิตทั้งสอง","สามีถึงแก่กรรม","ภรรยาถึงแก่กรรม","พ่อม่าย","แม่ม่าย"};

	public FamilyStatus() {
		initContainer();
	}
 
	@SuppressWarnings("unchecked")
	private void initContainer(){
	   addContainerProperty("name", String.class,null);
	   for (int i = 0; i < familyStatuses.length; i++) {
	        Item item = addItem(i);
	        item.getItemProperty("name").setValue(familyStatuses[i]);
	   }
	}
	
	public static String getNameTh(int index){
		return familyStatuses[index];
	}
	
	/*public static String getNameEn(int index){
		return familyStatuses[index];
	}*/
}
