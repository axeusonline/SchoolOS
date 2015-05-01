package com.ies.schoolos.type;

import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;

public class StudentComeWith extends IndexedContainer{

	private static final long serialVersionUID = 1L;
	
	private static String[] strudentComeWith = {"รถโรงเรียน","รถรับจ้าง","รถมอเตอร์ไซต์"};

	public StudentComeWith() {
		initContainer();
	}
 
	@SuppressWarnings("unchecked")
	private void initContainer(){
	   addContainerProperty("name", String.class,null);
	   for (int i = 0; i < strudentComeWith.length; i++) {
	        Item item = addItem(i);
	        item.getItemProperty("name").setValue(strudentComeWith[i]);
	   }
	}
	
	public static String getNameTh(int index){
		return strudentComeWith[index];
	}
	
	/*public static String getNameEn(int index){
		return strudentComeWith[index];
	}*/
}
