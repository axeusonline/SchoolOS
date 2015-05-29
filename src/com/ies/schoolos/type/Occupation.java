package com.ies.schoolos.type;

import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;

public class Occupation extends IndexedContainer{

	private static final long serialVersionUID = 1L;
	
	private static String[] aliveStatuses = {"ว่างงาน","เกษตรกรรม","รับจ้าง/ลูกจ้าง",
		"ค้าขาย","รับราชการ","รัฐวิสาหกิจ",
		"นักการเมือง","ทหาร/ตำรวจ","ประมง",
		"ครู","เลี้ยงสัตว์","นักบวช/สมณะ",
		"พ่อบ้าน/แม่บ้าน","นักเรียน","อื่น ๆ"};
			
	public Occupation() {
		initContainer();
	}
 
	@SuppressWarnings("unchecked")
	private void initContainer(){
	   addContainerProperty("name", String.class,null);
	   for (int i = 0; i < aliveStatuses.length; i++) {
	        Item item = addItem(i);
	        item.getItemProperty("name").setValue(aliveStatuses[i]);
	   }
	}
	
	public static String getNameTh(int index){
		return aliveStatuses[index];
	}
	
	/*public static String getNameEn(int index){
		return religions_en[index];
	}*/
}
