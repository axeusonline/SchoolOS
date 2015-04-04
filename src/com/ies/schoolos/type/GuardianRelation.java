package com.ies.schoolos.type;

import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;

public class GuardianRelation extends IndexedContainer{

	private static final long serialVersionUID = 1L;
	
	private static String[] guardianRelations = {"บิดา/มารดา","ญาติ","ป้า/น้า","ลุง/อา","พี่สาว","พี่ชาย","ปู่/ตา","ย่า/ยาย","ลูกพี่ลูกน้อง","พ่อ/แม่ บุญธรรม"};

	public GuardianRelation() {
		initContainer();
	}
 
	@SuppressWarnings("unchecked")
	private void initContainer(){
	   addContainerProperty("name", String.class,null);
	   for (int i = 0; i < guardianRelations.length; i++) {
	        Item item = addItem(i);
	        item.getItemProperty("name").setValue(guardianRelations[i]);
	   }
	}
	
	public static String getNameTh(int index){
		return guardianRelations[index];
	}
	
	/*public static String getNameEn(int index){
		return guardianRelations[index];
	}*/
}
