package com.ies.schoolos.type;

import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;

public class StudentStatus extends IndexedContainer{

	private static final long serialVersionUID = 1L;
	
	private static String[] studentStatuses = {"กำลังศึกษา","พักการเรียน","นักเรียนโครงการ","ลาออก","จำหน่ายออก","จบการศึกษา"};
	
	public StudentStatus() {
		initContainer();
	}
 
	@SuppressWarnings("unchecked")
	private void initContainer(){
	   addContainerProperty("name", String.class,null);
	   for (int i = 0; i < studentStatuses.length; i++) {
	        Item item = addItem(i);
	        item.getItemProperty("name").setValue(studentStatuses[i]);
	   }
	}
	
	public static String getNameTh(int index){
		return studentStatuses[index];
	}
	
	/*public static String getNameEn(int index){
		return studentStatuses[index];
	}*/
}
