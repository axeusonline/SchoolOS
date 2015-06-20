package com.ies.schoolos.type;

import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;

public class ClassRange extends IndexedContainer{

	private static final long serialVersionUID = 1L;
	
	private static String[] classRanges = {"อนุบาล","ประถมศึกษา","มัธยมต้น","มัธยมปลาย","ศาสนาชั้นต้น","ศาสนาชั้นกลาง","ศาสนาชั้นปลาย"};
	
	public ClassRange() {
		initContainer();
	}
 
	@SuppressWarnings("unchecked")
	private void initContainer(){
	   addContainerProperty("name", String.class,null);
	   for (int i = 0; i < classRanges.length; i++) {
	        Item item = addItem(i);
	        item.getItemProperty("name").setValue(classRanges[i]);
	   }
	}
	
	public static String getNameTh(int index){
		return classRanges[index];
	}
	
	/*public static String getNameEn(int index){
		return classRanges[index];
	}*/
}
