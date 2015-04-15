package com.ies.schoolos.report.excel;

import java.sql.Date;
import java.util.ArrayList;

import com.ies.schoolos.container.Container;
import com.ies.schoolos.schema.CitySchema;
import com.ies.schoolos.schema.DistrictSchema;
import com.ies.schoolos.schema.PostcodeSchema;
import com.ies.schoolos.schema.ProvinceSchema;
import com.ies.schoolos.schema.SessionSchema;
import com.ies.schoolos.schema.fundamental.BuildingSchema;
import com.ies.schoolos.schema.fundamental.ClassRoomSchema;
import com.ies.schoolos.schema.recruit.RecruitStudentFamilySchema;
import com.ies.schoolos.schema.recruit.RecruitStudentSchema;
import com.ies.schoolos.type.Blood;
import com.ies.schoolos.type.ClassRange;
import com.ies.schoolos.type.FamilyStatus;
import com.ies.schoolos.type.Gender;
import com.ies.schoolos.type.GuardianRelation;
import com.ies.schoolos.type.Nationality;
import com.ies.schoolos.type.Prename;
import com.ies.schoolos.type.Race;
import com.ies.schoolos.type.Religion;
import com.ies.schoolos.utility.DateTimeUtil;
import com.vaadin.data.Item;
import com.vaadin.data.util.filter.And;
import com.vaadin.data.util.filter.Compare.Equal;
import com.vaadin.data.util.filter.Compare.Greater;
import com.vaadin.data.util.filter.Compare.Less;
import com.vaadin.data.util.sqlcontainer.RowId;
import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.data.util.sqlcontainer.query.OrderBy;
import com.vaadin.ui.Table;
import com.vaadin.ui.UI;

public class RecruitStudentToExcel extends Table{
	private static final long serialVersionUID = 1L;
	
	public RecruitStudentToExcel() {
		setSizeFull();
		setData();
	}
	
	public void setData(){
		addContainerProperty(RecruitStudentSchema.RECRUIT_CODE, String.class, null);
		addContainerProperty(RecruitStudentSchema.CLASS_RANGE, String.class, null);
		addContainerProperty(RecruitStudentSchema.PEOPLE_ID, String.class, null);
		addContainerProperty(RecruitStudentSchema.PRENAME, String.class, null);
		addContainerProperty(RecruitStudentSchema.FIRSTNAME, String.class, null);
		addContainerProperty(RecruitStudentSchema.LASTNAME, String.class, null);
		addContainerProperty(RecruitStudentSchema.FIRSTNAME_ND, String.class, null);
		addContainerProperty(RecruitStudentSchema.LASTNAME_ND, String.class, null);
		addContainerProperty(RecruitStudentSchema.NICKNAME, String.class, null);
		addContainerProperty(RecruitStudentSchema.GENDER, String.class, null);
		addContainerProperty(RecruitStudentSchema.RELIGION, String.class, null);
		addContainerProperty(RecruitStudentSchema.RACE, String.class, null);
		addContainerProperty(RecruitStudentSchema.NATIONALITY, String.class, null);
		addContainerProperty(RecruitStudentSchema.BIRTH_DATE, Date.class, null);
		addContainerProperty(RecruitStudentSchema.BLOOD, String.class, null);
		addContainerProperty(RecruitStudentSchema.HEIGHT, Double.class, null);
		addContainerProperty(RecruitStudentSchema.WEIGHT, Double.class, null);
		addContainerProperty(RecruitStudentSchema.CONGENITAL_DISEASE, String.class, null);
		addContainerProperty(RecruitStudentSchema.INTERESTED, String.class, null);
		addContainerProperty(RecruitStudentSchema.SIBLING_QTY, Integer.class, null);
		addContainerProperty(RecruitStudentSchema.SIBLING_SEQUENCE, Integer.class, null);
		addContainerProperty(RecruitStudentSchema.SIBLING_INSCHOOL_QTY, Integer.class, null);
		addContainerProperty(RecruitStudentSchema.GRADUATED_SCHOOL, String.class, null);
		addContainerProperty(RecruitStudentSchema.GRADUATED_SCHOOL_PROVINCE_ID, String.class, null);
		addContainerProperty(RecruitStudentSchema.GRADUATED_GPA, Double.class, null);
		addContainerProperty(RecruitStudentSchema.GRADUATED_YEAR, String.class, null);
		addContainerProperty(RecruitStudentSchema.TEL, String.class, null);
		addContainerProperty(RecruitStudentSchema.MOBILE, String.class, null);
		addContainerProperty(RecruitStudentSchema.EMAIL, String.class, null);
		addContainerProperty(RecruitStudentSchema.CURRENT_ADDRESS, String.class, null);
		addContainerProperty(RecruitStudentSchema.CURRENT_CITY_ID, String.class, null);
		addContainerProperty(RecruitStudentSchema.CURRENT_DISTRICT_ID, String.class, null);
		addContainerProperty(RecruitStudentSchema.CURRENT_PROVINCE_ID, String.class, null);
		addContainerProperty(RecruitStudentSchema.CURRENT_POSTCODE_ID, String.class, null);
		addContainerProperty(RecruitStudentSchema.FATHER_ID, String.class, null);
		addContainerProperty(RecruitStudentSchema.MOTHER_ID, String.class, null);
		addContainerProperty(RecruitStudentSchema.FAMILY_STATUS, String.class, null);
		addContainerProperty(RecruitStudentSchema.GUARDIAN_ID, String.class, null);
		addContainerProperty(RecruitStudentSchema.GUARDIAN_RELATION, String.class, null);
		addContainerProperty(RecruitStudentSchema.REGISTER_DATE, Date.class, null);
		addContainerProperty(RecruitStudentSchema.SCORE, Double.class, null);
		addContainerProperty(RecruitStudentSchema.EXAM_BUILDING_ID, String.class, null);
		addContainerProperty(RecruitStudentSchema.CLASS_ROOM_ID, String.class, null);
		
		setColumnHeader(RecruitStudentSchema.RECRUIT_CODE, "หมายเลขสมัคร");
		setColumnHeader(RecruitStudentSchema.CLASS_RANGE, "ช่วงชั้น");
		setColumnHeader(RecruitStudentSchema.PEOPLE_ID, "หมายเลขประจำตัวประชาชน");
		setColumnHeader(RecruitStudentSchema.PRENAME, "ชื่อต้น");
		setColumnHeader(RecruitStudentSchema.FIRSTNAME, "ชื่อ");
		setColumnHeader(RecruitStudentSchema.LASTNAME, "สกุล");
		setColumnHeader(RecruitStudentSchema.FIRSTNAME_ND, "ชื่อ (ภาษาที่สอง)");
		setColumnHeader(RecruitStudentSchema.LASTNAME_ND, "สกุล (ภาษาที่สอง)");
		setColumnHeader(RecruitStudentSchema.NICKNAME, "ชื่อเล่น");
		setColumnHeader(RecruitStudentSchema.GENDER, "เพศ");
		setColumnHeader(RecruitStudentSchema.RELIGION, "ศาสนา");
		setColumnHeader(RecruitStudentSchema.RACE, "เชื้อชาติ");
		setColumnHeader(RecruitStudentSchema.NATIONALITY, "สัญชาติ");
		setColumnHeader(RecruitStudentSchema.BIRTH_DATE, "วัน/เดือน/ปี เกิด");
		setColumnHeader(RecruitStudentSchema.BLOOD, "หมู่เลือด");
		setColumnHeader(RecruitStudentSchema.HEIGHT, "ส่วนสูง");
		setColumnHeader(RecruitStudentSchema.WEIGHT, "น้ำหนัก");
		setColumnHeader(RecruitStudentSchema.CONGENITAL_DISEASE, "โรคประจำตัว");
		setColumnHeader(RecruitStudentSchema.INTERESTED, "สิ่งที่สนใจ");
		setColumnHeader(RecruitStudentSchema.SIBLING_QTY, "จำนวนพี่น้อง");
		setColumnHeader(RecruitStudentSchema.SIBLING_SEQUENCE, "ลำดับที่");
		setColumnHeader(RecruitStudentSchema.SIBLING_INSCHOOL_QTY, "จำนวนพี่น้องที่กำลังศึกษา");
		setColumnHeader(RecruitStudentSchema.GRADUATED_SCHOOL, "โรงเรียนที่จบ");
		setColumnHeader(RecruitStudentSchema.GRADUATED_SCHOOL_PROVINCE_ID, "จังหวัด");
		setColumnHeader(RecruitStudentSchema.GRADUATED_GPA, "เกรดเฉลี่ย");
		setColumnHeader(RecruitStudentSchema.GRADUATED_YEAR, "ปีการศึกษา");
		setColumnHeader(RecruitStudentSchema.TEL, "โทร");
		setColumnHeader(RecruitStudentSchema.MOBILE, "มือถือ");
		setColumnHeader(RecruitStudentSchema.EMAIL, "อีเมลล์");
		setColumnHeader(RecruitStudentSchema.CURRENT_ADDRESS, "ที่อยู่ปัจจุบัน");
		setColumnHeader(RecruitStudentSchema.CURRENT_CITY_ID, "ตำบล");
		setColumnHeader(RecruitStudentSchema.CURRENT_DISTRICT_ID, "อำเภอ");
		setColumnHeader(RecruitStudentSchema.CURRENT_PROVINCE_ID, "จังหวัด");
		setColumnHeader(RecruitStudentSchema.CURRENT_POSTCODE_ID, "ไปรษณีย์");
		setColumnHeader(RecruitStudentSchema.FATHER_ID, "บิดา");
		setColumnHeader(RecruitStudentSchema.MOTHER_ID, "มารดา");
		setColumnHeader(RecruitStudentSchema.FAMILY_STATUS, "สถานะครอบครัว");
		setColumnHeader(RecruitStudentSchema.GUARDIAN_ID, "ผู้ปกครอง");
		setColumnHeader(RecruitStudentSchema.GUARDIAN_RELATION, "ความสัมพันธ์");
		setColumnHeader(RecruitStudentSchema.REGISTER_DATE, "วันที่สมัคร");
		setColumnHeader(RecruitStudentSchema.SCORE, "คะแนนสอบ");
		setColumnHeader(RecruitStudentSchema.EXAM_BUILDING_ID, "ห้องสอบ");
		setColumnHeader(RecruitStudentSchema.CLASS_ROOM_ID, "ชั้นเรียนชั่วคราว");
		
		setVisibleColumns(
			RecruitStudentSchema.RECRUIT_CODE,
			RecruitStudentSchema.CLASS_RANGE,
			RecruitStudentSchema.PEOPLE_ID,
			RecruitStudentSchema.PRENAME,
			RecruitStudentSchema.FIRSTNAME,
			RecruitStudentSchema.LASTNAME,
			RecruitStudentSchema.FIRSTNAME_ND,
			RecruitStudentSchema.LASTNAME_ND,
			RecruitStudentSchema.NICKNAME,
			RecruitStudentSchema.GENDER,
			RecruitStudentSchema.RELIGION,
			RecruitStudentSchema.RACE,
			RecruitStudentSchema.NATIONALITY,
			RecruitStudentSchema.BIRTH_DATE,
			RecruitStudentSchema.BLOOD,
			RecruitStudentSchema.HEIGHT,
			RecruitStudentSchema.WEIGHT,
			RecruitStudentSchema.CONGENITAL_DISEASE,
			RecruitStudentSchema.INTERESTED,
			RecruitStudentSchema.SIBLING_QTY,
			RecruitStudentSchema.SIBLING_SEQUENCE,
			RecruitStudentSchema.SIBLING_INSCHOOL_QTY,
			RecruitStudentSchema.GRADUATED_SCHOOL,
			RecruitStudentSchema.GRADUATED_SCHOOL_PROVINCE_ID,
			RecruitStudentSchema.GRADUATED_GPA,
			RecruitStudentSchema.GRADUATED_YEAR,
			RecruitStudentSchema.TEL,
			RecruitStudentSchema.MOBILE,
			RecruitStudentSchema.EMAIL,
			RecruitStudentSchema.CURRENT_ADDRESS,
			RecruitStudentSchema.CURRENT_CITY_ID,
			RecruitStudentSchema.CURRENT_DISTRICT_ID,
			RecruitStudentSchema.CURRENT_PROVINCE_ID,
			RecruitStudentSchema.CURRENT_POSTCODE_ID,
			RecruitStudentSchema.FATHER_ID,
			RecruitStudentSchema.MOTHER_ID,
			RecruitStudentSchema.FAMILY_STATUS,
			RecruitStudentSchema.GUARDIAN_ID,
			RecruitStudentSchema.GUARDIAN_RELATION,
			RecruitStudentSchema.REGISTER_DATE,
			RecruitStudentSchema.SCORE,
			RecruitStudentSchema.EXAM_BUILDING_ID,
			RecruitStudentSchema.CLASS_ROOM_ID);
		
		SQLContainer sContainer = Container.getInstance().getRecruitStudentContainer();
		sContainer.addContainerFilter(new And(new Equal(RecruitStudentSchema.SCHOOL_ID,UI.getCurrent().getSession().getAttribute(SessionSchema.SCHOOL_ID)),
				new Greater(RecruitStudentSchema.REGISTER_DATE,DateTimeUtil.getFirstDateOfYear()),
				new Less(RecruitStudentSchema.REGISTER_DATE,DateTimeUtil.getLastDateOfYear())));
		sContainer.addOrderBy(new OrderBy(RecruitStudentSchema.RECRUIT_CODE, true));	
		
		SQLContainer examBuildingContainer = Container.getInstance().getBuildingContainer();
		SQLContainer classRoomContainer = Container.getInstance().getClassRoomContainer();
		SQLContainer provinceContainer = Container.getInstance().getProvinceContainer();
		SQLContainer districtContainer = Container.getInstance().getDistrictContainer();
		SQLContainer cityContainer = Container.getInstance().getCityContainer();
		SQLContainer postcodeContainer = Container.getInstance().getPostcodeContainer();
		
		for(Object itemId:sContainer.getItemIds()){
			Item item = sContainer.getItem(itemId);
			ArrayList<Object> objects = new ArrayList<Object>();
			for(Object propertyId:getVisibleColumns()){
				Object value = item.getItemProperty(propertyId).getValue();
				
				if(propertyId.equals(RecruitStudentSchema.CLASS_RANGE))
					value = ClassRange.getNameTh((int)value);
				else if(propertyId.equals(RecruitStudentSchema.PRENAME))
					value = Prename.getNameTh((int)value);
				else if(propertyId.equals(RecruitStudentSchema.GENDER))
					value = Gender.getNameTh((int)value);
				else if(propertyId.equals(RecruitStudentSchema.RELIGION))
					value = Religion.getNameTh((int)value);
				else if(propertyId.equals(RecruitStudentSchema.RACE))
					value = Race.getNameTh((int)value);
				else if(propertyId.equals(RecruitStudentSchema.NATIONALITY))
					value = Nationality.getNameTh((int)value);
				else if(propertyId.equals(RecruitStudentSchema.BLOOD))
					value = Blood.getNameTh((int)value);
				else if(propertyId.equals(RecruitStudentSchema.FAMILY_STATUS))
					value = FamilyStatus.getNameTh((int)value);
				else if(propertyId.equals(RecruitStudentSchema.GUARDIAN_RELATION))
					value = GuardianRelation.getNameTh((int)value);
				else if(propertyId.equals(RecruitStudentSchema.FATHER_ID) ||
						propertyId.equals(RecruitStudentSchema.MOTHER_ID) ||
						propertyId.equals(RecruitStudentSchema.GUARDIAN_ID)){
					/* บิดา มารดา ผู้ปกครอง*/ 
					if(value != null){
						StringBuilder familySQL = new StringBuilder();
						familySQL.append(" SELECT " + RecruitStudentFamilySchema.REG_FAMILY_ID + "," + RecruitStudentFamilySchema.FIRSTNAME + "," + RecruitStudentFamilySchema.LASTNAME);
						familySQL.append(" FROM " + RecruitStudentFamilySchema.TABLE_NAME);
						familySQL.append(" WHERE " + RecruitStudentFamilySchema.REG_FAMILY_ID + "=");
						
						SQLContainer familyContainer = Container.getInstance().getFreeFormContainer(familySQL.toString() + value, RecruitStudentFamilySchema.REG_FAMILY_ID);						
						
						Item familyItem = familyContainer.getItem(new RowId(value));
						value = familyItem.getItemProperty(RecruitStudentFamilySchema.FIRSTNAME).getValue().toString() + " " +
								familyItem.getItemProperty(RecruitStudentFamilySchema.LASTNAME).getValue().toString();
					}else{
						value = "ไม่พบข้อมูล";
					}
				}else if(propertyId.equals(RecruitStudentSchema.EXAM_BUILDING_ID)){
					/* อาคารสอบ*/ 
					if(value != null){
						Item buildingItem = examBuildingContainer.getItem(new RowId(value));
						value = buildingItem.getItemProperty(BuildingSchema.NAME).getValue().toString() + " "
								+ "(" + buildingItem.getItemProperty(BuildingSchema.ROOM_NUMBER).getValue().toString() + ")";
					}else{
						value = "ไม่พบข้อมูล";
					}
					
				}else if(propertyId.equals(RecruitStudentSchema.CLASS_ROOM_ID)){
					/* ห้องเรียน */ 
					if(value != null){
						Item classRoomItem = classRoomContainer.getItem(new RowId(value));
						value = classRoomItem.getItemProperty(ClassRoomSchema.NAME).getValue().toString();
					}else{
						value = "ยังไม่ระบุห้องเรียน";
					}
				}else if(propertyId.equals(RecruitStudentSchema.GRADUATED_SCHOOL_PROVINCE_ID) ||
						propertyId.equals(RecruitStudentSchema.CURRENT_PROVINCE_ID)){
					/* จังหวัดโรงเรียนที่จบ ที่อยู่ปัจจุบัน*/ 
					Item provinceItem = provinceContainer.getItem(new RowId(value));
					value = provinceItem.getItemProperty(ProvinceSchema.NAME).getValue().toString();
				}else if(propertyId.equals(RecruitStudentSchema.CURRENT_DISTRICT_ID)){
					 /*อำเภอที่อยู่ปัจจุบัน*/ 
					Item districtItem = districtContainer.getItem(new RowId(value));
					value = districtItem.getItemProperty(DistrictSchema.NAME).getValue().toString();
				}else if(propertyId.equals(RecruitStudentSchema.CURRENT_CITY_ID)){
					/* ตำบลที่อยู่ปัจจุบัน*/ 
					Item cityItem = cityContainer.getItem(new RowId(value));
					value = cityItem.getItemProperty(CitySchema.NAME).getValue().toString();
				}else if(propertyId.equals(RecruitStudentSchema.CURRENT_POSTCODE_ID)){
					/* ตำบลที่อยู่ปัจจุบัน*/ 
					Item postcodeItem = postcodeContainer.getItem(new RowId(value));
					value = postcodeItem.getItemProperty(PostcodeSchema.CODE).getValue().toString();
				}
				
				objects.add(value);
				
			}

			addItem(objects.toArray(), itemId);
		}
	}
}
