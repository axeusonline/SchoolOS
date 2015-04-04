package com.ies.schoolos.type;

import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;

public class Race extends IndexedContainer{

	private static final long serialVersionUID = 1L;
	
	private static String[] races = {"ไทย","มาเลเซีย","สิงคโปร์","ลาว","อินโดนีเซีย","กัมพูชา","บรูไน","เวียดนาม","พม่า","ฟิลิปปินส์","อื่น ๆ"};
	
	public Race() {
		initContainer();
	}
 
	@SuppressWarnings("unchecked")
	private void initContainer(){
	   addContainerProperty("name", String.class,null);
	   for (int i = 0; i < races.length; i++) {
	        Item item = addItem(i);
	        item.getItemProperty("name").setValue(races[i]);
	   }
	}

	public static String getNameTh(int index){
		return races[index];
	}
	
	/*public static String getNameEn(int index){
		return religions_en[index];
	}*/
}
