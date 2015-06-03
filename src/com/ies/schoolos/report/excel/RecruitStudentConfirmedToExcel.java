package com.ies.schoolos.report.excel;

import java.util.Date;

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

public class RecruitStudentConfirmedToExcel extends Table{
	private static final long serialVersionUID = 1L;

	private Container container = new Container();
	
	public RecruitStudentConfirmedToExcel() {
		setSizeFull();
		setData();
	}
	
	public void setData(){
		SQLContainer sContainer = container.getRecruitStudentContainer();
		sContainer.addContainerFilter(new And(new Equal(RecruitStudentSchema.SCHOOL_ID,SessionSchema.getSchoolID()),
				new Greater(RecruitStudentSchema.REGISTER_DATE,DateTimeUtil.getFirstDateOfYear()),
				new Less(RecruitStudentSchema.REGISTER_DATE,DateTimeUtil.getLastDateOfYear()),
				new Equal(RecruitStudentSchema.IS_CONFIRM, true)));
		sContainer.addOrderBy(new OrderBy(RecruitStudentSchema.RECRUIT_CODE, true));		
		
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
		setColumnHeader(RecruitStudentSchema.EMAIL, "อีเมล์");
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
		
		for(Object itemId:sContainer.getItemIds()){
			Item item = sContainer.getItem(itemId);
			SQLContainer examBuildingContainer = container.getBuildingContainer();
			SQLContainer classRoomContainer = container.getClassRoomContainer();
			
			SQLContainer fContainer = container.getRecruitFamilyContainer();
			SQLContainer provinceContainer = container.getProvinceContainer();
			SQLContainer districtContainer = container.getDistrictContainer();
			SQLContainer cityContainer = container.getCityContainer();
			SQLContainer postcodeContainer = container.getPostcodeContainer();
			
			/* จังหวัดโรงเรียนที่จบ */
			String graduatedProvince = provinceContainer.
					getItem(new RowId(item.getItemProperty(RecruitStudentSchema.GRADUATED_SCHOOL_PROVINCE_ID).getValue())).
					getItemProperty(ProvinceSchema.NAME).getValue().toString();
			
			/* บิดา */
			Item fItem = fContainer.getItem(new RowId(item.getItemProperty(RecruitStudentSchema.FATHER_ID).getValue()));
			String fatherName = fItem.getItemProperty(RecruitStudentFamilySchema.FIRSTNAME).getValue().toString() + " " +
					fItem.getItemProperty(RecruitStudentFamilySchema.LASTNAME).getValue().toString();			
			/* มารดา */
			Item mItem = fContainer.getItem(new RowId(item.getItemProperty(RecruitStudentSchema.MOTHER_ID).getValue()));
			String motherName = mItem.getItemProperty(RecruitStudentFamilySchema.FIRSTNAME).getValue().toString() + " " +
					mItem.getItemProperty(RecruitStudentFamilySchema.LASTNAME).getValue().toString();			
			/* ผู้ปกครอง */
			Item gItem = fContainer.getItem(new RowId(item.getItemProperty(RecruitStudentSchema.GUARDIAN_ID).getValue()));
			String guardianName = gItem.getItemProperty(RecruitStudentFamilySchema.FIRSTNAME).getValue().toString() + " " +
					gItem.getItemProperty(RecruitStudentFamilySchema.LASTNAME).getValue().toString();
			
			/* จังหวัดที่อยู่ปัจจุบัน */
			String currentProvince = provinceContainer.
					getItem(new RowId(item.getItemProperty(RecruitStudentSchema.CURRENT_PROVINCE_ID).getValue())).
					getItemProperty(ProvinceSchema.NAME).getValue().toString();
			/* อำเภอที่อยู่ปัจจุบัน */
			String currentDistrict = districtContainer.
					getItem(new RowId(item.getItemProperty(RecruitStudentSchema.CURRENT_DISTRICT_ID).getValue())).
					getItemProperty(DistrictSchema.NAME).getValue().toString();
			/* ตำบลที่อยู่ปัจจุบัน */
			String currentCity = cityContainer.
					getItem(new RowId(item.getItemProperty(RecruitStudentSchema.CURRENT_CITY_ID).getValue())).
					getItemProperty(CitySchema.NAME).getValue().toString();
			/* ไปรษณีย์ที่อยู่ปัจจุบัน */
			String currentPostcode = postcodeContainer.
					getItem(new RowId(item.getItemProperty(RecruitStudentSchema.CURRENT_POSTCODE_ID).getValue())).
					getItemProperty(PostcodeSchema.CODE).getValue().toString();
			/* ห้องสอบ */
			String examBuilding = "ยังไม่ระบุ";
			if(item.getItemProperty(RecruitStudentSchema.CLASS_ROOM_ID).getValue() != null)
				examBuilding = examBuildingContainer.getItem(new RowId(item.getItemProperty(RecruitStudentSchema.EXAM_BUILDING_ID).getValue())).
					getItemProperty(BuildingSchema.NAME).getValue().toString();
			/* ห้องเรียน */
			String classRoom = "ยังไม่ระบุ";
			if(item.getItemProperty(RecruitStudentSchema.CLASS_ROOM_ID).getValue() != null)
				classRoom = classRoomContainer.getItem(new RowId(item.getItemProperty(RecruitStudentSchema.CLASS_ROOM_ID).getValue())).
					getItemProperty(ClassRoomSchema.NAME).getValue().toString();
			
			addItem(new Object[] {
					item.getItemProperty(RecruitStudentSchema.RECRUIT_CODE).getValue(), 
					ClassRange.getNameTh(Integer.parseInt(item.getItemProperty(RecruitStudentSchema.CLASS_RANGE).getValue().toString())),
					item.getItemProperty(RecruitStudentSchema.PEOPLE_ID).getValue(), 
					Prename.getNameTh(Integer.parseInt(item.getItemProperty(RecruitStudentSchema.PRENAME).getValue().toString())),
					item.getItemProperty(RecruitStudentSchema.FIRSTNAME).getValue(), 
					item.getItemProperty(RecruitStudentSchema.LASTNAME).getValue(),
					item.getItemProperty(RecruitStudentSchema.FIRSTNAME_ND).getValue(),
					item.getItemProperty(RecruitStudentSchema.LASTNAME_ND).getValue(),
					item.getItemProperty(RecruitStudentSchema.NICKNAME).getValue(),
					Gender.getNameTh(Integer.parseInt(item.getItemProperty(RecruitStudentSchema.GENDER).getValue().toString())),
					Religion.getNameTh(Integer.parseInt(item.getItemProperty(RecruitStudentSchema.RELIGION).getValue().toString())),
					Race.getNameTh(Integer.parseInt(item.getItemProperty(RecruitStudentSchema.RACE).getValue().toString())),
					Nationality.getNameTh(Integer.parseInt(item.getItemProperty(RecruitStudentSchema.NATIONALITY).getValue().toString())),
					item.getItemProperty(RecruitStudentSchema.BIRTH_DATE).getValue(),
					Blood.getNameTh(Integer.parseInt(item.getItemProperty(RecruitStudentSchema.BLOOD).getValue().toString())),
					item.getItemProperty(RecruitStudentSchema.HEIGHT).getValue(),
					item.getItemProperty(RecruitStudentSchema.WEIGHT).getValue(),
					item.getItemProperty(RecruitStudentSchema.CONGENITAL_DISEASE).getValue(),
					item.getItemProperty(RecruitStudentSchema.INTERESTED).getValue(),
					item.getItemProperty(RecruitStudentSchema.SIBLING_QTY).getValue(),
					item.getItemProperty(RecruitStudentSchema.SIBLING_SEQUENCE).getValue(),
					item.getItemProperty(RecruitStudentSchema.SIBLING_INSCHOOL_QTY).getValue(),
					item.getItemProperty(RecruitStudentSchema.GRADUATED_SCHOOL).getValue(),
					graduatedProvince,
					item.getItemProperty(RecruitStudentSchema.GRADUATED_GPA).getValue(),
					item.getItemProperty(RecruitStudentSchema.GRADUATED_YEAR).getValue(),
					item.getItemProperty(RecruitStudentSchema.TEL).getValue(),
					item.getItemProperty(RecruitStudentSchema.MOBILE).getValue(),
					item.getItemProperty(RecruitStudentSchema.EMAIL).getValue(),
					item.getItemProperty(RecruitStudentSchema.CURRENT_ADDRESS).getValue(),
					currentCity,
					currentDistrict,
					currentProvince,
					currentPostcode,
					fatherName,
					motherName,
					FamilyStatus.getNameTh(Integer.parseInt(item.getItemProperty(RecruitStudentSchema.FAMILY_STATUS).getValue().toString())),
					guardianName,
					GuardianRelation.getNameTh(Integer.parseInt(item.getItemProperty(RecruitStudentSchema.GUARDIAN_RELATION).getValue().toString())),
					item.getItemProperty(RecruitStudentSchema.REGISTER_DATE).getValue(),
					item.getItemProperty(RecruitStudentSchema.SCORE).getValue(),
					examBuilding,
					classRoom,
			},itemId);
		}
		/* ลบ WHERE ออกจาก Query เพื่อป้องกันการค้างของคำสั่่งจากการทำงานอื่นที่เรียกตัวแปรไปใช้ */
		sContainer.removeAllContainerFilters();
	}
}
