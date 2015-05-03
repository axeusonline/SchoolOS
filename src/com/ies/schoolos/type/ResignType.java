package com.ies.schoolos.type;

import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;

public class ResignType extends IndexedContainer{

	private static final long serialVersionUID = 1L;
	
	private static String[] resignTypes = {"ลาออก","จำหน่ายออก"};
	
	public ResignType() {
		initContainer();
	}
	
	public ResignType(int classRange) {
		/* ตรวจสอบช่วงชั้น เพื่อดึงชั้นปีที่อยู่ในช่วงที่กำหนด
		 *  กรณั ช่วงชั้นเป็น 0 แสดงถึงดึงเฉพาะ ชั้นปี อนุบาล
		 *  กรณี ช่วงชั้นเป็น 1 แสดงถึงดึงเฉพาะ ชั้นปี ประถม
		 *  กรณั ช่วงชั้นเป็น 2 แสดงถึงดึงเฉพาะ ชั้นปี ม.ต้น
		 *  กรณี ช่วงชั้นเป็น 3 แสดงถึงดึงเฉพาะ ชั้นปี ม.ปลาย 
		 *  กรณี ช่วงชั้นเป็น 4 แสดงถึงดึงเฉพาะ ชั้นปี ศาสนา */
		if(classRange == 0)
			initContainer(0, 2);
		else if(classRange == 1)
			initContainer(3, 8);
		else if(classRange == 2)
			initContainer(9, 11);
		else if(classRange == 3)
			initContainer(12, 14);
		else if(classRange == 4)
			initContainer(15, 24);
	}
 
	@SuppressWarnings("unchecked")
	private void initContainer(){
	   addContainerProperty("name", String.class,null);
	   for (int i = 0; i < resignTypes.length; i++) {
	        Item item = addItem(i);
	        item.getItemProperty("name").setValue(resignTypes[i]);
	   }
	}
	
	@SuppressWarnings("unchecked")
	private void initContainer(int firstIndex, int lastIndex){
	   addContainerProperty("name", String.class,null);
	   for (int i = firstIndex; i <= lastIndex; i++) {
	        Item item = addItem(i);
	        item.getItemProperty("name").setValue(resignTypes[i]);
	   }
	}
	
	public static String getNameTh(int index){
		return resignTypes[index];
	}
	
	/*public static String getNameEn(int index){
		return resignTypes[index];
	}*/
}
