package com.ies.schoolos.type;

import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;

public class RecruitType extends IndexedContainer{

	private static final long serialVersionUID = 1L;
	
	private static String[] recruitTypes = {"กำพร้า/อนาถา","บุตรอาจารย์/บุคลากร","สอบเข้า","อุปการะคุณโรงเรียน"};
	
	public RecruitType() {
		initContainer();
	}
 
	@SuppressWarnings("unchecked")
	private void initContainer(){
	   addContainerProperty("name", String.class,null);
	   for (int i = 0; i < recruitTypes.length; i++) {
	        Item item = addItem(i);
	        item.getItemProperty("name").setValue(recruitTypes[i]);
	   }
	}
	
	public static String getNameTh(int index){
		return recruitTypes[index];
	}
	
	/*public static String getNameEn(int index){
		return recruitTypes[index];
	}*/
}
