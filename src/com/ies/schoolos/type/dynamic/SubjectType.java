package com.ies.schoolos.type.dynamic;

import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;

public class SubjectType extends IndexedContainer{

	private static final long serialVersionUID = 1L;
	
	private static String[] subjectTypes = {"วิชาพื้นฐานอนุบาล","วิชาพื้นฐาน",
		"วิชาเพิ่มเติม","วิชาเลือกเสรี",
		"กิจกรรมพัฒนาผู้เรียน","วิชาอิสลาม"};
	
	public SubjectType() {
		initContainer();
	}
 
	@SuppressWarnings("unchecked")
	private void initContainer(){
	   addContainerProperty("name", String.class,null);
	   for (int i = 0; i < subjectTypes.length; i++) {
	        Item item = addItem(i);
	        item.getItemProperty("name").setValue(subjectTypes[i]);
	   }
	}
	
	public static String getNameTh(int index){
		return subjectTypes[index];
	}
	
	/*public static String getNameEn(int index){
		return subjectTypes[index];
	}*/
}
