package com.ies.schoolos.container;

import java.io.Serializable;
import java.sql.SQLException;

import com.ies.schoolos.schema.BuildingSchema;
import com.ies.schoolos.schema.CitySchema;
import com.ies.schoolos.schema.ClassRoomSchema;
import com.ies.schoolos.schema.DistrictSchema;
import com.ies.schoolos.schema.FamilySchema;
import com.ies.schoolos.schema.PostcodeSchema;
import com.ies.schoolos.schema.ProvinceSchema;
import com.ies.schoolos.schema.RecruitStudentFamilySchema;
import com.ies.schoolos.schema.RecruitStudentSchema;
import com.ies.schoolos.schema.SchoolSchema;
import com.ies.schoolos.schema.StudentClassRoomSchema;
import com.ies.schoolos.schema.StudentSchema;
import com.ies.schoolos.schema.StudentStudySchema;
import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.data.util.sqlcontainer.query.FreeformQuery;
import com.vaadin.data.util.sqlcontainer.query.TableQuery;

public class Container implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private static Container container;
	 
	//ใช้สำหรับ Manaul Query
	private SQLContainer freeFormContainer;
	
	//ใช้สำหรับ Query จังหวัด
	private SQLContainer provinceContainer;
	//ใช้สำหรับ Query อำเภอ
	private SQLContainer districtContainer;
	//ใช้สำหรับ Query ตำบล
	private SQLContainer cityContainer;
	//ใช้สำหรับ Query ไปรษณีย์
	private SQLContainer postcodeContainer;
	
	//ใช้สำหรับ Query โรงเรียน
	private SQLContainer schoolContainer;
	//ใช้สำหรับ Query ผู้สมัครเรียน
	private SQLContainer recruitStudentContainer;
	//ใช้สำหรับ Query ผู้ปกครองผู้สมัครเรียน
	private SQLContainer recruitFamilyContainer;
	//ใช้สำหรับ Query อาคารเรียน
	private SQLContainer buildingContainer;
	//ใช้สำหรับ Query ห้องเรียน
	private SQLContainer classRoomContainer;
	//ใช้สำหรับ Query นักเรียน
	private SQLContainer studentContainer;
	//ใช้สำหรับ Query ข้อมูลการเรียน
	private SQLContainer studentStudyContainer;
	//ใช้สำหรับ Query ผุ้ปกครอง
	private SQLContainer familyContainer;
	//ใช้สำหรับ Query ห้องเรียนนักเรียน
	private SQLContainer studentClassRoomContainer;

	public static Container getInstance(){ 
		if(container == null)
			container = new Container();
        return container;  
    }

	public Container() {
        initContainers();
	}
	
	private void initContainers() {
        try {
        	 /* TableQuery และ SQLContainer สำหรับตาราง จังหวัด */
            TableQuery qProvince = new TableQuery(ProvinceSchema.TABLE_NAME, DbConnection.getConnection());
            provinceContainer = new SQLContainer(qProvince);
            
            /* TableQuery และ SQLContainer สำหรับตาราง อำเภอ */
            TableQuery qDistrict = new TableQuery(DistrictSchema.TABLE_NAME, DbConnection.getConnection());
            districtContainer = new SQLContainer(qDistrict);
            
            /* TableQuery และ SQLContainer สำหรับตาราง  ตำบล */
            TableQuery qCity = new TableQuery(CitySchema.TABLE_NAME, DbConnection.getConnection());
            cityContainer = new SQLContainer(qCity);
            
            /* TableQuery และ SQLContainer สำหรับตาราง ไปรษณีย์ */
            TableQuery qPostcode = new TableQuery(PostcodeSchema.TABLE_NAME, DbConnection.getConnection());
            postcodeContainer = new SQLContainer(qPostcode);
            
            /* TableQuery และ SQLContainer สำหรับตาราง School */
            TableQuery qSchool = new TableQuery(SchoolSchema.TABLE_NAME, DbConnection.getConnection());
            schoolContainer = new SQLContainer(qSchool);

            /* TableQuery และ SQLContainer สำหรับตาราง สมัครนักเรียน */
            TableQuery qRecruitStudent = new TableQuery(RecruitStudentSchema.TABLE_NAME, DbConnection.getConnection());
            recruitStudentContainer = new SQLContainer(qRecruitStudent);
            
            /* TableQuery และ SQLContainer สำหรับตาราง บิดา มารดา ผ้ปกครอง ของผู้สมัครเรียน */
            TableQuery qRecruitFamily = new TableQuery(RecruitStudentFamilySchema.TABLE_NAME, DbConnection.getConnection());
            recruitFamilyContainer = new SQLContainer(qRecruitFamily);
            
            /* TableQuery และ SQLContainer สำหรับตาราง อาคารสอบ */
            TableQuery qBuilding = new TableQuery(BuildingSchema.TABLE_NAME, DbConnection.getConnection());
            buildingContainer = new SQLContainer(qBuilding);
            
            /* TableQuery และ SQLContainer สำหรับตาราง ชั้นเรียน */
            TableQuery qClassRoom = new TableQuery(ClassRoomSchema.TABLE_NAME, DbConnection.getConnection());
            classRoomContainer = new SQLContainer(qClassRoom); 
            
            /* TableQuery และ SQLContainer สำหรับตาราง นักเรียน */
            TableQuery qStudent = new TableQuery(StudentSchema.TABLE_NAME, DbConnection.getConnection());
            studentContainer = new SQLContainer(qStudent); 
            
            /* TableQuery และ SQLContainer สำหรับตาราง ข้อมูลการเรียน */
            TableQuery qStudentStudy = new TableQuery(StudentStudySchema.TABLE_NAME, DbConnection.getConnection());
            studentStudyContainer = new SQLContainer(qStudentStudy); 

        	/* TableQuery และ SQLContainer สำหรับตาราง ผุ้ปกครอง */
            TableQuery qFamily = new TableQuery(FamilySchema.TABLE_NAME, DbConnection.getConnection());
            familyContainer = new SQLContainer(qFamily); 

        	//ใช้สำหรับ Query ห้องเรียนนักเรียน
        	 /* TableQuery และ SQLContainer สำหรับตาราง ชั้นเรียน */
            TableQuery qStudentClassRoom = new TableQuery(StudentClassRoomSchema.TABLE_NAME, DbConnection.getConnection());
            studentClassRoomContainer = new SQLContainer(qStudentClassRoom); 


        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

	public SQLContainer getFreeFormContainer(String sql, String primaryKey) {
		try {
			FreeformQuery tq = new FreeformQuery(sql, DbConnection.getConnection(),primaryKey);		
			freeFormContainer = new SQLContainer(tq);
			freeFormContainer.setAutoCommit(true);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return freeFormContainer;
	}

	public SQLContainer getProvinceContainer() {
		return provinceContainer;
	}

	public SQLContainer getDistrictContainer() {
		return districtContainer;
	}

	public SQLContainer getCityContainer() {
		return cityContainer;
	}

	public SQLContainer getPostcodeContainer() {
		return postcodeContainer;
	}
	
	public SQLContainer getSchoolContainer() {
		return schoolContainer;
	}

	public SQLContainer getRecruitStudentContainer() {
		return recruitStudentContainer;
	}

	public SQLContainer getRecruitFamilyContainer() {
		return recruitFamilyContainer;
	}

	public SQLContainer getBuildingContainer() {
		return buildingContainer;
	}
	
	public SQLContainer getClassRoomContainer() {
		return classRoomContainer;
	}

	public static Container getContainer() {
		return container;
	}

	public SQLContainer getFreeFormContainer() {
		return freeFormContainer;
	}

	public SQLContainer getStudentContainer() {
		return studentContainer;
	}

	public SQLContainer getStudentStudyContainer() {
		return studentStudyContainer;
	}

	public SQLContainer getFamilyContainer() {
		return familyContainer;
	}
	
	public SQLContainer getStudentClassRoomContainer() {
		return studentClassRoomContainer;
	}
}
