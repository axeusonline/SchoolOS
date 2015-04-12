package com.ies.schoolos.type;

import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;

public class JobPosition extends IndexedContainer{

	private static final long serialVersionUID = 1L;
	
	private static String[] jobPositions = {"กรรมการ","ครูสามัญ","ครูศาสนา","เจ้าหน้าที่",
		"เจ้าหน้าที่หอพักนักการภารโรง","พนักงานขับรถ",
		"พนักงานรักษาความปลอดภัย","ข้าราชการ","พนักงานช้าราชการ",
		"พนักงานช้าราชการชั่วคราว","ผู้บริหาร","ครูชำนาญการ",
		"ครูพิเศษ","อาจารย์ฝึกสอน","นักศึกษาฝึกงาน",
		"ผู้อำนวยการ","นายทะเบียน","เจ้าหน้าที่ไอที"};
	
	public JobPosition() {
		initContainer();
	}
 
	@SuppressWarnings("unchecked")
	private void initContainer(){
	   addContainerProperty("name", String.class,null);
	   for (int i = 0; i < jobPositions.length; i++) {
	        Item item = addItem(i);
	        item.getItemProperty("name").setValue(jobPositions[i]);
	   }
	}
	
	public static String getNameTh(int index){
		return jobPositions[index];
	}
	
	/*public static String getNameEn(int index){
		return jobPositions[index];
	}*/
}
