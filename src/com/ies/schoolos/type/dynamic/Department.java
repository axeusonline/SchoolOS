package com.ies.schoolos.type.dynamic;

import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;

public class Department extends IndexedContainer{

	private static final long serialVersionUID = 1L;
	
	private static String[] departments = {"ฝ่ายบริหาร","ฝ่ายงานวิชาการ","ฝ่ายงานทะเบียน",
		"ฝ่ายงานพัฒนาบุคลากร","ฝ่ายงานกิจการนักเรียน","ฝ่ายพัสดุ","ฝ่ายธุรการ",
		"ฝ่ายงานผู้ดูแลระบบ","ฝ่ายงานการเงิน","ฝ่ายงานพัฒนาผู้เรียน","ฝ่ายงานห้องสมุด",
		"ฝ่ายงานหอพักนักเรียน","ฝ่ายงานสหกรณ์", "ฝ่ายสมัครเรียน",
		"ฝ่ายอาคารและสถานที่","ฝ่ายอาจารย์ผู้สอน","ฝ่ายอาจารย์ประจำชั้น","ฝ่ายงานสาระการเรียนรู้ภาษาไทย",
		"ฝ่ายงานสาระการเรียนรู้ศิลปะ","ฝ่ายงานสาระการเรียนรู้วิทยาศาสตร์","ฝ่ายงานสาระการเรียนรู้สังคมศึกษา ศาสนา และ วัฒนธรรม",
		"ฝ่ายงานสาระการเรียนรู้คณิตศาสตร์","ฝ่ายงานสาระการเรียนรู้สุขศึกษาและพละศึกษา","ฝ่ายงานสาระการเรียนรู้การงานอาชีพและเทคโนโลยี",
		"ฝ่ายงานสาระการเรียนรู้ภาษาต่างประเทศ"
		};
	
	public Department() {
		initContainer();
	}
 
	@SuppressWarnings("unchecked")
	private void initContainer(){
	   addContainerProperty("name", String.class,null);
	   for (int i = 0; i < departments.length; i++) {
	        Item item = addItem(i);
	        item.getItemProperty("name").setValue(departments[i]);
	   }
	}
	
	public static String getNameTh(int index){
		return departments[index];
	}
	
	/*public static String getNameEn(int index){
		return departments[index];
	}*/
}
