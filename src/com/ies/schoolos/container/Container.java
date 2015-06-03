package com.ies.schoolos.container;

import java.io.Serializable;
import java.sql.SQLException;

import com.ies.schoolos.schema.CitySchema;
import com.ies.schoolos.schema.DistrictSchema;
import com.ies.schoolos.schema.PostcodeSchema;
import com.ies.schoolos.schema.ProvinceSchema;
import com.ies.schoolos.schema.SchoolSchema;
import com.ies.schoolos.schema.UserSchema;
import com.ies.schoolos.schema.academic.ClassRoomLessonPlanSchema;
import com.ies.schoolos.schema.academic.LessonPlanSchema;
import com.ies.schoolos.schema.academic.LessonPlanSubjectSchema;
import com.ies.schoolos.schema.academic.TeacherHomeroomSchema;
import com.ies.schoolos.schema.academic.TeachingSchema;
import com.ies.schoolos.schema.academic.TimetableSchema;
import com.ies.schoolos.schema.fundamental.BehaviorSchema;
import com.ies.schoolos.schema.fundamental.BuildingSchema;
import com.ies.schoolos.schema.fundamental.ClassRoomSchema;
import com.ies.schoolos.schema.fundamental.DepartmentSchema;
import com.ies.schoolos.schema.fundamental.JobPositionSchema;
import com.ies.schoolos.schema.fundamental.SubjectSchema;
import com.ies.schoolos.schema.info.FamilySchema;
import com.ies.schoolos.schema.info.PersonnelGraduatedHistorySchema;
import com.ies.schoolos.schema.info.PersonnelSchema;
import com.ies.schoolos.schema.info.StudentClassRoomSchema;
import com.ies.schoolos.schema.info.StudentSchema;
import com.ies.schoolos.schema.info.StudentStudySchema;
import com.ies.schoolos.schema.recruit.RecruitStudentFamilySchema;
import com.ies.schoolos.schema.recruit.RecruitStudentSchema;
import com.ies.schoolos.schema.studentaffairs.StudentBehaviorSchema;
import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.data.util.sqlcontainer.query.FreeformQuery;
import com.vaadin.data.util.sqlcontainer.query.TableQuery;

public class Container implements Serializable {
	private static final long serialVersionUID = 1L;

	public SQLContainer getFreeFormContainer(String sql, String primaryKey) {
		FreeformQuery tq;
		SQLContainer freeFormContainer = null;
		try {
			tq = new FreeformQuery(sql, DbConnection.getConnection(),primaryKey);		
			freeFormContainer = new SQLContainer(tq);
			freeFormContainer.setAutoCommit(true);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return freeFormContainer;
	}

	/* TableQuery และ SQLContainer สำหรับจังหวัด */
	public SQLContainer getProvinceContainer() {
		TableQuery query;
    	SQLContainer provinceContainer = null;;
		try {
			query = new TableQuery(ProvinceSchema.TABLE_NAME, DbConnection.getConnection());
			provinceContainer = new SQLContainer(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return provinceContainer;
	}

	/* TableQuery และ SQLContainer สำหรับอำเภอ */
	public SQLContainer getDistrictContainer() {
		TableQuery query;
    	SQLContainer districtContainer = null;;
		try {
			query = new TableQuery(DistrictSchema.TABLE_NAME, DbConnection.getConnection());
			districtContainer = new SQLContainer(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return districtContainer;
	}

	/* TableQuery และ SQLContainer สำหรับตำบล */
	public SQLContainer getCityContainer() {
		TableQuery query;
    	SQLContainer cityContainer = null;;
		try {
			query = new TableQuery(CitySchema.TABLE_NAME, DbConnection.getConnection());
			cityContainer = new SQLContainer(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return cityContainer;
	}

	/* TableQuery และ SQLContainer สำหรับ ปณ */
	public SQLContainer getPostcodeContainer() {
		TableQuery query;
    	SQLContainer postcodeContainer = null;;
		try {
			query = new TableQuery(PostcodeSchema.TABLE_NAME, DbConnection.getConnection());
			postcodeContainer = new SQLContainer(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return postcodeContainer;
	}
	
	/* TableQuery และ SQLContainer สำหรับโรงเรียน */
	public SQLContainer getSchoolContainer() {
		TableQuery query;
    	SQLContainer schoolContainer = null;;
		try {
			query = new TableQuery(SchoolSchema.TABLE_NAME, DbConnection.getConnection());
			schoolContainer = new SQLContainer(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return schoolContainer;
	}
	
	/* TableQuery และ SQLContainer สำหรับผู้ใช้งาน */
	public SQLContainer getUserContainer() {
		TableQuery query;
    	SQLContainer userContainer = null;;
		try {
			query = new TableQuery(UserSchema.TABLE_NAME, DbConnection.getConnection());
			userContainer = new SQLContainer(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return userContainer;
	}

	/* TableQuery และ SQLContainer สำหรับนักเรียนผู้สมัคร */
	public SQLContainer getRecruitStudentContainer() {
		TableQuery query;
    	SQLContainer recruitStudentContainer = null;;
		try {
			query = new TableQuery(RecruitStudentSchema.TABLE_NAME, DbConnection.getConnection());
			recruitStudentContainer = new SQLContainer(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return recruitStudentContainer;
	}

	/* TableQuery และ SQLContainer สำหรับครอบครัวนักเรียนผู้สมัคร */
	public SQLContainer getRecruitFamilyContainer() {
		TableQuery query;
    	SQLContainer recruitFamilyContainer = null;;
		try {
			query = new TableQuery(RecruitStudentFamilySchema.TABLE_NAME, DbConnection.getConnection());
			recruitFamilyContainer = new SQLContainer(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return recruitFamilyContainer;
	}

	/* TableQuery และ SQLContainer สำหรับอาคาร */
	public SQLContainer getBuildingContainer() {
		TableQuery query;
    	SQLContainer buildingContainer = null;;
		try {
			query = new TableQuery(BuildingSchema.TABLE_NAME, DbConnection.getConnection());
			buildingContainer = new SQLContainer(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return buildingContainer;
	}
	
	/* TableQuery และ SQLContainer สำหรับชั้นเรียน */
	public SQLContainer getClassRoomContainer() {
		TableQuery query;
    	SQLContainer classRoomContainer = null;;
		try {
			query = new TableQuery(ClassRoomSchema.TABLE_NAME, DbConnection.getConnection());
			classRoomContainer = new SQLContainer(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return classRoomContainer;
	}
	
	/* TableQuery และ SQLContainer สำหรับนักเรียน */
	public SQLContainer getStudentContainer() {
		TableQuery query;
    	SQLContainer studentContainer = null;;
		try {
			query = new TableQuery(StudentSchema.TABLE_NAME, DbConnection.getConnection());
			studentContainer = new SQLContainer(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return studentContainer;
	}

	/* TableQuery และ SQLContainer สำหรับข้อมูลการเรียน */
	public SQLContainer getStudentStudyContainer() {
		TableQuery query;
    	SQLContainer studentStudyContainer = null;;
		try {
			query = new TableQuery(StudentStudySchema.TABLE_NAME, DbConnection.getConnection());
			studentStudyContainer = new SQLContainer(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return studentStudyContainer;
	}

	/* TableQuery และ SQLContainer สำหรับอครอบครัว */
	public SQLContainer getFamilyContainer() {
		TableQuery query;
    	SQLContainer familyContainer = null;;
		try {
			query = new TableQuery(FamilySchema.TABLE_NAME, DbConnection.getConnection());
			familyContainer = new SQLContainer(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return familyContainer;
	}
	
	/* TableQuery และ SQLContainer สำหรับชั้นเรียนนักเรียน */
	public SQLContainer getStudentClassRoomContainer() {
		TableQuery query;
    	SQLContainer studentClassRoomContainer = null;;
		try {
			query = new TableQuery(StudentClassRoomSchema.TABLE_NAME, DbConnection.getConnection());
			studentClassRoomContainer = new SQLContainer(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return studentClassRoomContainer;
	}
	
	/* TableQuery และ SQLContainer สำหรับบุคลากร */
	public SQLContainer getPersonnelContainer() {
		TableQuery query;
    	SQLContainer personnelContainer = null;;
		try {
			query = new TableQuery(PersonnelSchema.TABLE_NAME, DbConnection.getConnection());
			personnelContainer = new SQLContainer(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return personnelContainer;
	}
	
	/* TableQuery และ SQLContainer สำหรับประวัติการศึกษา */
	public SQLContainer getPersonnelGraduatedHistoryContainer() {
		TableQuery query;
    	SQLContainer personnelGraduatedHistoryContainer = null;;
		try {
			query = new TableQuery(PersonnelGraduatedHistorySchema.TABLE_NAME, DbConnection.getConnection());
			personnelGraduatedHistoryContainer = new SQLContainer(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return personnelGraduatedHistoryContainer;
	}
	
	/* TableQuery และ SQLContainer สำหรับอาจารย์ */
	public SQLContainer getSubjectContainer() {
		TableQuery query;
    	SQLContainer subjectContainer = null;;
		try {
			query = new TableQuery(SubjectSchema.TABLE_NAME, DbConnection.getConnection());
			subjectContainer = new SQLContainer(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return subjectContainer;
	}
	
	/* TableQuery และ SQLContainer สำหรับแผนการเรียน */
	public SQLContainer getLessonPlanContainer() {
		TableQuery query;
    	SQLContainer lessonPlanContainer = null;;
		try {
			query = new TableQuery(LessonPlanSchema.TABLE_NAME, DbConnection.getConnection());
			lessonPlanContainer = new SQLContainer(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return lessonPlanContainer;
	}
	
	/* TableQuery และ SQLContainer สำหรับแผนการเรียนรายวิชา */
	public SQLContainer getLessonPlanSubjectContainer() {
		TableQuery query;
    	SQLContainer lessonPlanSubjectContainer = null;;
		try {
			query = new TableQuery(LessonPlanSubjectSchema.TABLE_NAME, DbConnection.getConnection());
			lessonPlanSubjectContainer = new SQLContainer(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return lessonPlanSubjectContainer;
	}
	
	/* TableQuery และ SQLContainer สำหรับแผนการเรียน-ชั้นเรียน */
	public SQLContainer getClassRoomLessonPlanContainer() {
		TableQuery query;
    	SQLContainer classRoomLessonPlanContainer = null;;
		try {
			query = new TableQuery(ClassRoomLessonPlanSchema.TABLE_NAME, DbConnection.getConnection());
			classRoomLessonPlanContainer = new SQLContainer(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return classRoomLessonPlanContainer;
	}
	
	/* TableQuery และ SQLContainer สำหรับอาจารย์ผู้สอน */
	public SQLContainer getTeachingContainer() {
		TableQuery query;
    	SQLContainer teachingContainer = null;;
		try {
			query = new TableQuery(TeachingSchema.TABLE_NAME, DbConnection.getConnection());
			teachingContainer = new SQLContainer(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return teachingContainer;
	}
	
	/* TableQuery และ SQLContainer สำหรับอาจารย์ประจำชั้น */
	public SQLContainer getTeacherHomeroomContainer() {
		TableQuery query;
    	SQLContainer teacherHomeroomContainer = null;;
		try {
			query = new TableQuery(TeacherHomeroomSchema.TABLE_NAME, DbConnection.getConnection());
			teacherHomeroomContainer = new SQLContainer(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return teacherHomeroomContainer;
	}
	
	/* TableQuery และ SQLContainer สำหรับตารางสอน */
	public SQLContainer getTimetableContainer() {
    	TableQuery query;
    	SQLContainer timetableContainer = null;;
		try {
			query = new TableQuery(TimetableSchema.TABLE_NAME, DbConnection.getConnection());
			timetableContainer = new SQLContainer(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return timetableContainer;
	}
	
	/* TableQuery และ SQLContainer สำหรับแผนก */
	public SQLContainer getDepartmentContainer() {
    	TableQuery query;
    	SQLContainer departmentContainer = null;;
		try {
			query = new TableQuery(DepartmentSchema.TABLE_NAME, DbConnection.getConnection());
			departmentContainer = new SQLContainer(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return departmentContainer;
	}
	
	/* TableQuery และ SQLContainer สำหรับตำแหน่ง */
	public SQLContainer getJobPositionContainer() {
    	TableQuery query;
    	SQLContainer jobPositionContainer = null;;
		try {
			query = new TableQuery(JobPositionSchema.TABLE_NAME, DbConnection.getConnection());
			jobPositionContainer = new SQLContainer(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return jobPositionContainer;
	}
	
	/* TableQuery และ SQLContainer สำหรับพฤติกรรม */
	public SQLContainer getBehaviorContainer() {
    	TableQuery query;
    	SQLContainer behaviorContainer = null;;
		try {
			query = new TableQuery(BehaviorSchema.TABLE_NAME, DbConnection.getConnection());
			behaviorContainer = new SQLContainer(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return behaviorContainer;
	}
	
	/* TableQuery และ SQLContainer สำหรับพฤติกรรมนักเรียน */
	public SQLContainer getStudentBehaviorContainer() {
    	TableQuery query;
    	SQLContainer studentBehaviorContainer = null;;
		try {
			query = new TableQuery(StudentBehaviorSchema.TABLE_NAME, DbConnection.getConnection());
			studentBehaviorContainer = new SQLContainer(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return studentBehaviorContainer;
	}
}
