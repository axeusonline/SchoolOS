package com.ies.schoolos.type;

import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;

public class Nationality extends IndexedContainer{

	private static final long serialVersionUID = 1L;
	
	private static String[] nationalities = {"ไทย","มาเลเซีย","สิงคโปร์","ลาว","อินโดนีเซีย","กัมพูชา","บรูไน","เวียดนาม","พม่า","ฟิลิปปินส์","อื่น ๆ"};
	
	public Nationality() {
		initContainer();
	}
 
	@SuppressWarnings("unchecked")
	private void initContainer(){
	   addContainerProperty("name", String.class,null);
	   for (int i = 0; i < nationalities.length; i++) {
	        Item item = addItem(i);
	        item.getItemProperty("name").setValue(nationalities[i]);
	   }
	}
	
	public static String getNameTh(int index){
		return nationalities[index];
	}
	
	/*public static String getNameEn(int index){
		return nationalities[index];
	}*/
}
