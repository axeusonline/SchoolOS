package com.ies.schoolos.type.dynamic;

import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;

public class LessonType extends IndexedContainer{

	private static final long serialVersionUID = 1L;
	
	private static String[] lessonTypes = {"ภาษาไทย","คณิตศาสตร์","วิทยาศาสตร์",
		"สังคมศึกษา ศาสนา และ วัฒนธรรม",	"สุขศึกษาและพละศึกษา","ศิลปะ",
		"การงานอาชีพและเทคโนโลยี","ภาษาต่างประเทศ","กิจกรรมพัฒนาผู้เรียน",
		"ชุมนุม/ชมรม","คุณลักษณะอันพึงประสงค์","การอ่าน วิเคราะห์ และ เขียนสื่อความหมาย",
		"ภาษาอาหรับ","อากีดะห์","ประวัติศาสตร์อิสลาม",
		"ชารีอะห์","กุรอาน และ ฮาดีส","มลายู"};
	
	public LessonType() {
		initContainer();
	}
 
	@SuppressWarnings("unchecked")
	private void initContainer(){
	   addContainerProperty("name", String.class,null);
	   for (int i = 0; i < lessonTypes.length; i++) {
	        Item item = addItem(i);
	        item.getItemProperty("name").setValue(lessonTypes[i]);
	   }
	}
	
	public static String getNameTh(int index){
		return lessonTypes[index];
	}
	
	/*public static String getNameEn(int index){
		return lessonTypes[index];
	}*/
}
