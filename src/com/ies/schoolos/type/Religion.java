package com.ies.schoolos.type;

import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;

public class Religion extends IndexedContainer{

	private static final long serialVersionUID = 1L;
	
	private static String[] religions = {"พุทธ","อิสลาม","คริสเตียน","ฮินดู","ซิกซ์","ไม่มีศาสนา","ไม่ระบุ","อื่น ๆ "};

	public Religion() {
		initContainer();
	}
 
	@SuppressWarnings("unchecked")
	private void initContainer(){
	   addContainerProperty("name", String.class,null);
	   for (int i = 0; i < religions.length; i++) {
	        Item item = addItem(i);
	        item.getItemProperty("name").setValue(religions[i]);
	   }
	}
	
	public static String getNameTh(int index){
		return religions[index];
	}
	
	/*public static String getNameEn(int index){
		return religions_en[index];
	}*/
}
