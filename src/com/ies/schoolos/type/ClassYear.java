package com.ies.schoolos.type;

import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;

public class ClassYear extends IndexedContainer{

	private static final long serialVersionUID = 1L;
	
	private static String[] classYears = {"อนุบาลศึกษาปีที่ 1","อนุบาลศึกษาปีที่ 2","อนุบาลศึกษาปีที่ 3",
	"ประถมศึกษาปีที่ 1","ประถมศึกษาปีที่ 2","ประถมศึกษาปีที่ 3","ประถมศึกษาปีที่ 4","ประถมศึกษาปีที่ 5","ประถมศึกษาปีที่ 6",
	"มัธยมศึกษาปีที่ 1","มัธยมศึกษาปีที่ 2","มัธยมศึกษาปีที่ 3","มัธยมศึกษาปีที่ 4","มัธยมศึกษาปีที่ 5","มัธยมศึกษาปีที่ 6",
	"ชั้นปีปีที่ 1","ชั้นปีปีที่ 2","ชั้นปีปีที่ 3","ชั้นปีปีที่ 4","ชั้นปีปีที่ 5","ชั้นปีปีที่ 6","ชั้นปีปีที่ 7","ชั้นปีปีที่ 8","ชั้นปีปีที่ 9","ชั้นปีปีที่ 10"};
	
	public ClassYear() {
		initContainer();
	}
	
	public ClassYear(int classRange) {
		/* ตรวจสอบช่วงชั้น เพื่อดึงชั้นปีที่อยู่ในช่วงที่กำหนด
		 *  กรณั ช่วงชั้นเป็น 0 แสดงถึงดึงเฉพาะ ชั้นปี อนุบาล
		 *  กรณี ช่วงชั้นเป็น 1 แสดงถึงดึงเฉพาะ ชั้นปี ประถม
		 *  กรณั ช่วงชั้นเป็น 2 แสดงถึงดึงเฉพาะ ชั้นปี ม.ต้น
		 *  กรณี ช่วงชั้นเป็น 3 แสดงถึงดึงเฉพาะ ชั้นปี ม.ปลาย */
		if(classRange == 0)
			initContainer(0, 2);
		else if(classRange == 1)
			initContainer(3, 8);
		else if(classRange == 2)
			initContainer(9, 11);
		else if(classRange == 3)
			initContainer(12, 14);
	}
 
	@SuppressWarnings("unchecked")
	private void initContainer(){
	   addContainerProperty("name", String.class,null);
	   for (int i = 0; i < classYears.length; i++) {
	        Item item = addItem(i);
	        item.getItemProperty("name").setValue(classYears[i]);
	   }
	}
	
	@SuppressWarnings("unchecked")
	private void initContainer(int firstIndex, int lastIndex){
	   addContainerProperty("name", String.class,null);
	   for (int i = firstIndex; i <= lastIndex; i++) {
	        Item item = addItem(i);
	        item.getItemProperty("name").setValue(classYears[i]);
	   }
	}
	
	public static String getNameTh(int index){
		return classYears[index];
	}
	
	/*public static String getNameEn(int index){
		return classYears[index];
	}*/
}
