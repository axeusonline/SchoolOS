package com.ies.schoolos.type;

import com.ies.schoolos.schema.UserSchema;
import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;

public class Feature extends IndexedContainer{

	private static final long serialVersionUID = 1L;
	
	public static int RECRUIT_STUDENT = 0;
	public static int PERSONNEL = 1;
	public static int ACADEMIC = 2;
	public static int REGISTRATION = 3;
	public static int STUDENT_AFFAIRS = 4;
	public static int ADMIN = 5;
	
	private static String[] features = {"สมัครเรียน","ฝ่ายบุคคล","ฝ่ายวิชาการ","ฝ่ายทะเบียน","ฝ่ายกิจการนักเรียน", "ผู้ดูแลระบบ"};
	
	public Feature() {
		initContainer();
	}
 
	@SuppressWarnings("unchecked")
	private void initContainer(){
	   addContainerProperty("name", String.class,null);
	   for (int i = 0; i < features.length; i++) {
	        Item item = addItem(i);
	        item.getItemProperty("name").setValue(features[i]);
	   }
	}
	
	public static String getNameTh(int index){
		return features[index];
	}
	
	@SuppressWarnings("unchecked")
	public static void setPermission(Item item, boolean isRoot){
		String permission = "";
		String value = "";
		
		if(isRoot)
			permission = "1";
		else
			permission = "0";
		
		for(int i = 0; i < features.length; i++){
			value += permission+",";
		}
		item.getItemProperty(UserSchema.PERMISSION).setValue(value.substring(0, value.length()-1));
	}
	
	/*public static String getNameEn(int index){
		return features[index];
	}*/
}
