package com.ies.schoolos.type;

import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;

public class StudentStayWith extends IndexedContainer{

	private static final long serialVersionUID = 1L;
	
	private static String[] studentStayWith = {"หอพักใน","หอพักนอก","บ้าน"};

	public StudentStayWith() {
		initContainer();
	}
 
	@SuppressWarnings("unchecked")
	private void initContainer(){
	   addContainerProperty("name", String.class,null);
	   for (int i = 0; i < studentStayWith.length; i++) {
	        Item item = addItem(i);
	        item.getItemProperty("name").setValue(studentStayWith[i]);
	   }
	}
	
	public static String getNameTh(int index){
		return studentStayWith[index];
	}
	
	/*public static String getNameEn(int index){
		return studentStayWith[index];
	}*/
}
