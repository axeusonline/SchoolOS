package com.ies.schoolos.report.excel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import com.ies.schoolos.container.Container;
import com.ies.schoolos.schema.CitySchema;
import com.ies.schoolos.schema.DistrictSchema;
import com.ies.schoolos.schema.PostcodeSchema;
import com.ies.schoolos.schema.ProvinceSchema;
import com.ies.schoolos.schema.SessionSchema;
import com.ies.schoolos.schema.fundamental.ClassRoomSchema;
import com.ies.schoolos.schema.info.FamilySchema;
import com.ies.schoolos.schema.info.StudentClassRoomSchema;
import com.ies.schoolos.schema.info.StudentSchema;
import com.ies.schoolos.schema.info.StudentStudySchema;
import com.ies.schoolos.type.Blood;
import com.ies.schoolos.type.FamilyStatus;
import com.ies.schoolos.type.Gender;
import com.ies.schoolos.type.GuardianRelation;
import com.ies.schoolos.type.Nationality;
import com.ies.schoolos.type.Prename;
import com.ies.schoolos.type.Race;
import com.ies.schoolos.type.Religion;
import com.ies.schoolos.utility.DateTimeUtil;
import com.vaadin.data.Item;
import com.vaadin.data.util.sqlcontainer.RowId;
import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.ui.Table;

public class StudentToExcel extends Table{
	private static final long serialVersionUID = 1L;
	
	public StudentToExcel() {
		setSizeFull();
		setData();
	}
	
	public void setData(){
		addContainerProperty(ClassRoomSchema.CLASS_ROOM_ID, String.class, null);
		addContainerProperty(StudentStudySchema.STUDENT_CODE, String.class, null);
		addContainerProperty(StudentSchema.PEOPLE_ID, String.class, null);
		addContainerProperty(StudentSchema.PRENAME, String.class, null);
		addContainerProperty(StudentSchema.FIRSTNAME, String.class, null);
		addContainerProperty(StudentSchema.LASTNAME, String.class, null);
		addContainerProperty(StudentSchema.FIRSTNAME_ND, String.class, null);
		addContainerProperty(StudentSchema.LASTNAME_ND, String.class, null);
		addContainerProperty(StudentSchema.NICKNAME, String.class, null);
		addContainerProperty(StudentSchema.GENDER, String.class, null);
		addContainerProperty(StudentSchema.RELIGION, String.class, null);
		addContainerProperty(StudentSchema.RACE, String.class, null);
		addContainerProperty(StudentSchema.NATIONALITY, String.class, null);
		addContainerProperty(StudentSchema.BIRTH_DATE, Date.class, null);
		addContainerProperty(StudentSchema.BLOOD, String.class, null);
		addContainerProperty(StudentSchema.HEIGHT, Double.class, null);
		addContainerProperty(StudentSchema.WEIGHT, Double.class, null);
		addContainerProperty(StudentSchema.CONGENITAL_DISEASE, String.class, null);
		addContainerProperty(StudentSchema.INTERESTED, String.class, null);
		addContainerProperty(StudentSchema.SIBLING_QTY, Integer.class, null);
		addContainerProperty(StudentSchema.SIBLING_SEQUENCE, Integer.class, null);
		addContainerProperty(StudentSchema.SIBLING_INSCHOOL_QTY, Integer.class, null);
		addContainerProperty(StudentStudySchema.GRADUATED_SCHOOL, String.class, null);
		addContainerProperty(StudentStudySchema.GRADUATED_SCHOOL_PROVINCE_ID, String.class, null);
		addContainerProperty(StudentStudySchema.GRADUATED_GPA, Double.class, null);
		addContainerProperty(StudentStudySchema.GRADUATED_YEAR, String.class, null);
		addContainerProperty(StudentStudySchema.TEL, String.class, null);
		addContainerProperty(StudentStudySchema.MOBILE, String.class, null);
		addContainerProperty(StudentStudySchema.EMAIL, String.class, null);
		addContainerProperty(StudentStudySchema.CURRENT_ADDRESS, String.class, null);
		addContainerProperty(StudentStudySchema.CURRENT_CITY_ID, String.class, null);
		addContainerProperty(StudentStudySchema.CURRENT_DISTRICT_ID, String.class, null);
		addContainerProperty(StudentStudySchema.CURRENT_PROVINCE_ID, String.class, null);
		addContainerProperty(StudentStudySchema.CURRENT_POSTCODE_ID, String.class, null);
		addContainerProperty(StudentSchema.FATHER_ID, String.class, null);
		addContainerProperty(StudentSchema.MOTHER_ID, String.class, null);
		addContainerProperty(StudentSchema.FAMILY_STATUS, String.class, null);
		addContainerProperty(StudentStudySchema.GUARDIAN_ID, String.class, null);
		addContainerProperty(StudentStudySchema.GUARDIAN_RELATION, String.class, null);
		addContainerProperty(StudentStudySchema.RECRUIT_DATE, Date.class, null);
		
		setColumnHeader(ClassRoomSchema.CLASS_ROOM_ID, "ห้องเรียน");
		setColumnHeader(StudentStudySchema.STUDENT_CODE, "รหัสนักเรียน");
		setColumnHeader(StudentSchema.PEOPLE_ID, "หมายเลขประจำตัวประชาชน");
		setColumnHeader(StudentSchema.PRENAME, "ชื่อต้น");
		setColumnHeader(StudentSchema.FIRSTNAME, "ชื่อ");
		setColumnHeader(StudentSchema.LASTNAME, "สกุล");
		setColumnHeader(StudentSchema.FIRSTNAME_ND, "ชื่อ (ภาษาที่สอง)");
		setColumnHeader(StudentSchema.LASTNAME_ND, "สกุล (ภาษาที่สอง)");
		setColumnHeader(StudentSchema.NICKNAME, "ชื่อเล่น");
		setColumnHeader(StudentSchema.GENDER, "เพศ");
		setColumnHeader(StudentSchema.RELIGION, "ศาสนา");
		setColumnHeader(StudentSchema.RACE, "เชื้อชาติ");
		setColumnHeader(StudentSchema.NATIONALITY, "สัญชาติ");
		setColumnHeader(StudentSchema.BIRTH_DATE, "วัน/เดือน/ปี เกิด");
		setColumnHeader(StudentSchema.BLOOD, "หมู่เลือด");
		setColumnHeader(StudentSchema.HEIGHT, "ส่วนสูง");
		setColumnHeader(StudentSchema.WEIGHT, "น้ำหนัก");
		setColumnHeader(StudentSchema.CONGENITAL_DISEASE, "โรคประจำตัว");
		setColumnHeader(StudentSchema.INTERESTED, "สิ่งที่สนใจ");
		setColumnHeader(StudentSchema.SIBLING_QTY, "จำนวนพี่น้อง");
		setColumnHeader(StudentSchema.SIBLING_SEQUENCE, "ลำดับที่");
		setColumnHeader(StudentSchema.SIBLING_INSCHOOL_QTY, "จำนวนพี่น้องที่กำลังศึกษา");
		setColumnHeader(StudentStudySchema.GRADUATED_SCHOOL, "โรงเรียนที่จบ");
		setColumnHeader(StudentStudySchema.GRADUATED_SCHOOL_PROVINCE_ID, "จังหวัด");
		setColumnHeader(StudentStudySchema.GRADUATED_GPA, "เกรดเฉลี่ย");
		setColumnHeader(StudentStudySchema.GRADUATED_YEAR, "ปีการศึกษา");
		setColumnHeader(StudentStudySchema.TEL, "โทร");
		setColumnHeader(StudentStudySchema.MOBILE, "มือถือ");
		setColumnHeader(StudentStudySchema.EMAIL, "อีเมล์");
		setColumnHeader(StudentStudySchema.CURRENT_ADDRESS, "ที่อยู่ปัจจุบัน");
		setColumnHeader(StudentStudySchema.CURRENT_CITY_ID, "ตำบล");
		setColumnHeader(StudentStudySchema.CURRENT_DISTRICT_ID, "อำเภอ");
		setColumnHeader(StudentStudySchema.CURRENT_PROVINCE_ID, "จังหวัด");
		setColumnHeader(StudentStudySchema.CURRENT_POSTCODE_ID, "ไปรษณีย์");
		setColumnHeader(StudentSchema.FATHER_ID, "บิดา");
		setColumnHeader(StudentSchema.MOTHER_ID, "มารดา");
		setColumnHeader(StudentSchema.FAMILY_STATUS, "สถานะครอบครัว");
		setColumnHeader(StudentStudySchema.GUARDIAN_ID, "ผู้ปกครอง");
		setColumnHeader(StudentStudySchema.GUARDIAN_RELATION, "ความสัมพันธ์");
		setColumnHeader(StudentStudySchema.RECRUIT_DATE, "วันที่เข้าเรียน");
		
		setVisibleColumns(
				ClassRoomSchema.CLASS_ROOM_ID,
				StudentStudySchema.STUDENT_CODE,
				StudentSchema.PEOPLE_ID,
				StudentSchema.PRENAME,
				StudentSchema.FIRSTNAME,
				StudentSchema.LASTNAME,
				StudentSchema.FIRSTNAME_ND,
				StudentSchema.LASTNAME_ND,
				StudentSchema.NICKNAME,
				StudentSchema.GENDER,
				StudentSchema.RELIGION,
				StudentSchema.RACE,
				StudentSchema.NATIONALITY,
				StudentSchema.BIRTH_DATE,
				StudentSchema.BLOOD,
				StudentSchema.HEIGHT,
				StudentSchema.WEIGHT,
				StudentSchema.CONGENITAL_DISEASE,
				StudentSchema.INTERESTED,
				StudentSchema.SIBLING_QTY,
				StudentSchema.SIBLING_SEQUENCE,
				StudentSchema.SIBLING_INSCHOOL_QTY,
				StudentStudySchema.GRADUATED_SCHOOL,
				StudentStudySchema.GRADUATED_SCHOOL_PROVINCE_ID,
				StudentStudySchema.GRADUATED_GPA,
				StudentStudySchema.GRADUATED_YEAR,
				StudentStudySchema.TEL,
				StudentStudySchema.MOBILE,
				StudentStudySchema.EMAIL,
				StudentStudySchema.CURRENT_ADDRESS,
				StudentStudySchema.CURRENT_CITY_ID,
				StudentStudySchema.CURRENT_DISTRICT_ID,
				StudentStudySchema.CURRENT_PROVINCE_ID,
				StudentStudySchema.CURRENT_POSTCODE_ID,
				StudentSchema.FATHER_ID,
				StudentSchema.MOTHER_ID,
				StudentSchema.FAMILY_STATUS,
				StudentStudySchema.GUARDIAN_ID,
				StudentStudySchema.GUARDIAN_RELATION,
				StudentStudySchema.RECRUIT_DATE
				);
		
		SQLContainer freeFormContainer = Container.getFreeFormContainer(""
				+ "SELECT * FROM student_class_room scr "
				+ "INNER JOIN class_room cr ON scr.class_room_id = cr.class_room_id "
				+ "INNER JOIN student_study ss ON scr.student_study_id = ss.student_study_id "
				+ "INNER JOIN student s ON s.student_id = ss.student_id "
				+ "INNER JOIN family f1 ON s.father_id = f1.family_id "
				+ "INNER JOIN family f2 ON s.mother_id = f2.family_id "
				+ "INNER JOIN family f3 ON ss.guardian_id = f3.family_id "
				+ "WHERE scr.school_id = " + SessionSchema.getSchoolID()+ " "
				+ "AND scr.academic_year =" + DateTimeUtil.getChristianYear() + " "
				+ "ORDER BY cr.class_year,cr.number,ss.student_code", StudentClassRoomSchema.STUDENT_CLASS_ROOM_ID);
		
		
		ArrayList<Object> visibleColumns = new ArrayList<Object>();
		visibleColumns.addAll(Arrays.asList(getVisibleColumns()));
		
		for(Object itemId:freeFormContainer.getItemIds()){
			ArrayList<Object> objects = new ArrayList<Object>();
			
			Item item = freeFormContainer.getItem(itemId);
			for(Object propertyId:getVisibleColumns()){

				Object value = item.getItemProperty(propertyId).getValue();
				if(propertyId.equals(StudentSchema.PRENAME))
					value = Prename.getNameTh((int)value);
				else if(propertyId.equals(StudentSchema.GENDER))
					value = Gender.getNameTh((int)value);
				else if(propertyId.equals(StudentSchema.RELIGION))
					value = Religion.getNameTh((int)value);
				else if(propertyId.equals(StudentSchema.RACE))
					value = Race.getNameTh((int)value);
				else if(propertyId.equals(StudentSchema.NATIONALITY))
					value = Nationality.getNameTh((int)value);
				else if(propertyId.equals(StudentSchema.BLOOD))
					value = Blood.getNameTh((int)value);
				else if(propertyId.equals(StudentSchema.FAMILY_STATUS))
					value = FamilyStatus.getNameTh((int)value);
				else if(propertyId.equals(StudentStudySchema.GUARDIAN_RELATION))
					value = GuardianRelation.getNameTh((int)value);
				else if(propertyId.equals(ClassRoomSchema.CLASS_ROOM_ID)){
					value = item.getItemProperty(ClassRoomSchema.NAME).getValue();
				}else if(propertyId.equals(StudentStudySchema.GRADUATED_SCHOOL_PROVINCE_ID)){
					value = item.getItemProperty(ClassRoomSchema.NAME).getValue();
				}else if(propertyId.equals(StudentStudySchema.CURRENT_CITY_ID)){
					SQLContainer cityContainer = Container.getCityContainer();
					Item cityItem = cityContainer.getItem(new RowId(value));
					value = cityItem.getItemProperty(CitySchema.NAME).getValue();
				}else if(propertyId.equals(StudentStudySchema.CURRENT_DISTRICT_ID)){
					SQLContainer districtContainer = Container.getDistrictContainer();
					Item districtItem = districtContainer.getItem(new RowId(value));
					value = districtItem.getItemProperty(DistrictSchema.NAME).getValue();
				}else if(propertyId.equals(StudentStudySchema.CURRENT_PROVINCE_ID)){
					SQLContainer provinceContainer = Container.getProvinceContainer();
					Item provinceItem = provinceContainer.getItem(new RowId(value));
					value = provinceItem.getItemProperty(ProvinceSchema.NAME).getValue();
				}else if(propertyId.equals(StudentStudySchema.CURRENT_POSTCODE_ID)){
					SQLContainer postcodeContainer = Container.getPostcodeContainer();
					Item postcodeItem = postcodeContainer.getItem(new RowId(value));
					value = postcodeItem.getItemProperty(PostcodeSchema.CODE).getValue();
				}else if(propertyId.equals(StudentSchema.FATHER_ID) ||
						propertyId.equals(StudentSchema.MOTHER_ID) ||
						propertyId.equals(StudentStudySchema.GUARDIAN_ID)){
					SQLContainer fContainer = Container.getFamilyContainer();
					Item familyItem = fContainer.getItem(new RowId(value));
					value = familyItem.getItemProperty(FamilySchema.FIRSTNAME).getValue() + " " + familyItem.getItemProperty(FamilySchema.LASTNAME).getValue();
				}

				objects.add(value);
			}
			addItem(objects.toArray(), itemId);
		}
		/* ลบ WHERE ออกจาก Query เพื่อป้องกันการค้างของคำสั่่งจากการทำงานอื่นที่เรียกตัวแปรไปใช้ */
		freeFormContainer.removeAllContainerFilters();
	}
}
