package com.ies.schoolos.type;

import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;

public class GraduatedLevel extends IndexedContainer{

	private static final long serialVersionUID = 1L;
	
	private static String[] graduatedLevels = {"มัธยมต้น","ปวช","ปวส","ปริญญาตรี","ปริญญาโท","อื่น ๆ"};
	
	public GraduatedLevel() {
		initContainer();
	}
 
	@SuppressWarnings("unchecked")
	private void initContainer(){
	   addContainerProperty("name", String.class,null);
	   for (int i = 0; i < graduatedLevels.length; i++) {
	        Item item = addItem(i);
	        item.getItemProperty("name").setValue(graduatedLevels[i]);
	   }
	}
	
	public static String getNameTh(int index){
		return graduatedLevels[index];
	}
	
	/*public static String getNameEn(int index){
		return graduatedLevels[index];
	}*/
}
