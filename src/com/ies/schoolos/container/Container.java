package com.ies.schoolos.container;

import java.io.Serializable;
import java.sql.SQLException;

import com.ies.schoolos.schema.CitySchema;
import com.ies.schoolos.schema.DistrictSchema;
import com.ies.schoolos.schema.PostcodeSchema;
import com.ies.schoolos.schema.ProvinceSchema;
import com.ies.schoolos.schema.SchoolSchema;
import com.ies.schoolos.schema.SessionSchema;
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
import com.vaadin.data.util.filter.IsNull;
import com.vaadin.data.util.filter.Or;
import com.vaadin.data.util.filter.Compare.Equal;
import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.data.util.sqlcontainer.query.FreeformQuery;
import com.vaadin.data.util.sqlcontainer.query.TableQuery;

public class Container implements Serializable {
	private static final long serialVersionUID = 1L;

	private SQLContainer provinceContainer = null;
	private SQLContainer districtContainer = null;
	private SQLContainer cityContainer = null;
	private SQLContainer postcodeContainer = null;
	private SQLContainer schoolContainer = null;
	private SQLContainer userContainer = null;
	private SQLContainer recruitStudentContainer = null;
	private SQLContainer recruitFamilyContainer = null;
	private SQLContainer buildingContainer = null;
	private SQLContainer classRoomContainer = null;
	private SQLContainer studentContainer = null;
	private SQLContainer studentStudyContainer = null;
	private SQLContainer familyContainer = null;
	private SQLContainer studentClassRoomContainer = null;
	private SQLContainer personnelContainer = null;
	private SQLContainer personnelGraduatedHistoryContainer = null;
	private SQLContainer subjectContainer = null;
	private SQLContainer lessonPlanContainer = null;
	private SQLContainer lessonPlanSubjectContainer = null;
	private SQLContainer classRoomLessonPlanContainer = null;
	private SQLContainer teachingContainer = null;
	private SQLContainer teacherHomeroomContainer = null;
	private SQLContainer timetableContainer = null;
	private SQLContainer departmentContainer = null;
	private SQLContainer jobPositionContainer = null;
	private SQLContainer behaviorContainer = null;
	private SQLContainer studentBehaviorContainer = null;
	
	public Container() {
		System.err.println("INITIAL ");
		initProvinceContainer();
		initDistrictContainer();
		initCityContainer();
		initPostcodeContainer();
		initSchoolContainer();
		initUserContainer();
		initRecruitStudentContainer();
		initRecruitFamilyContainer();
		initBuildingContainer();
		initClassRoomContainer();
		initStudentContainer();
		initStudentStudyContainer();
		initFamilyContainer(); 
		initStudentClassRoomContainer();
		initPersonnelContainer();
		initPersonnelGraduatedHistoryContainer();
		initSubjectContainer();
		initLessonPlanContainer();
		initLessonPlanSubjectContainer();
		initClassRoomLessonPlanContainer();
		initTeachingContainer();
		initTeacherHomeroomContainer();
		initTimetableContainer();
		initDepartmentContainer();
		initJobPositionContainer();
		initBehaviorContainer();
		initStudentBehaviorContainer();
	}
	
	public static SQLContainer getFreeFormContainer(String sql, String primaryKey) {
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
	public SQLContainer initProvinceContainer() {
		TableQuery query;
		try {
			query = new TableQuery(ProvinceSchema.TABLE_NAME, DbConnection.getConnection());
			provinceContainer = new SQLContainer(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return provinceContainer;
	}

	/* TableQuery และ SQLContainer สำหรับอำเภอ */
	public SQLContainer initDistrictContainer() {
		TableQuery query;
		try {
			query = new TableQuery(DistrictSchema.TABLE_NAME, DbConnection.getConnection());
			districtContainer = new SQLContainer(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return districtContainer;
	}

	/* TableQuery และ SQLContainer สำหรับตำบล */
	public SQLContainer initCityContainer() {
		TableQuery query;
		try {
			query = new TableQuery(CitySchema.TABLE_NAME, DbConnection.getConnection());
			cityContainer = new SQLContainer(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return cityContainer;
	}

	/* TableQuery และ SQLContainer สำหรับ ปณ */
	public SQLContainer initPostcodeContainer() {
		TableQuery query;
		try {
			query = new TableQuery(PostcodeSchema.TABLE_NAME, DbConnection.getConnection());
			postcodeContainer = new SQLContainer(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return postcodeContainer;
	}
	
	/* TableQuery และ SQLContainer สำหรับโรงเรียน */
	public SQLContainer initSchoolContainer() {
		TableQuery query;
		try {
			query = new TableQuery(SchoolSchema.TABLE_NAME, DbConnection.getConnection());
			schoolContainer = new SQLContainer(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return schoolContainer;
	}
	
	/* TableQuery และ SQLContainer สำหรับผู้ใช้งาน */
	public SQLContainer initUserContainer() {
		TableQuery query;
		try {
			query = new TableQuery(UserSchema.TABLE_NAME, DbConnection.getConnection());
			userContainer = new SQLContainer(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return userContainer;
	}

	/* TableQuery และ SQLContainer สำหรับนักเรียนผู้สมัคร */
	public SQLContainer initRecruitStudentContainer() {
		TableQuery query;
		try {
			query = new TableQuery(RecruitStudentSchema.TABLE_NAME, DbConnection.getConnection());
			recruitStudentContainer = new SQLContainer(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return recruitStudentContainer;
	}

	/* TableQuery และ SQLContainer สำหรับครอบครัวนักเรียนผู้สมัคร */
	public SQLContainer initRecruitFamilyContainer() {
		TableQuery query;
		try {
			query = new TableQuery(RecruitStudentFamilySchema.TABLE_NAME, DbConnection.getConnection());
			recruitFamilyContainer = new SQLContainer(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return recruitFamilyContainer;
	}

	/* TableQuery และ SQLContainer สำหรับอาคาร */
	public SQLContainer initBuildingContainer() {
		TableQuery query;
		try {
			query = new TableQuery(BuildingSchema.TABLE_NAME, DbConnection.getConnection());
			buildingContainer = new SQLContainer(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return buildingContainer;
	}
	
	/* TableQuery และ SQLContainer สำหรับชั้นเรียน */
	public SQLContainer initClassRoomContainer() {
		TableQuery query;
		try {
			query = new TableQuery(ClassRoomSchema.TABLE_NAME, DbConnection.getConnection());
			classRoomContainer = new SQLContainer(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return classRoomContainer;
	}
	
	/* TableQuery และ SQLContainer สำหรับนักเรียน */
	public SQLContainer initStudentContainer() {
		TableQuery query;
		try {
			query = new TableQuery(StudentSchema.TABLE_NAME, DbConnection.getConnection());
			studentContainer = new SQLContainer(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return studentContainer;
	}

	/* TableQuery และ SQLContainer สำหรับข้อมูลการเรียน */
	public SQLContainer initStudentStudyContainer() {
		TableQuery query;
		try {
			query = new TableQuery(StudentStudySchema.TABLE_NAME, DbConnection.getConnection());
			studentStudyContainer = new SQLContainer(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return studentStudyContainer;
	}

	/* TableQuery และ SQLContainer สำหรับอครอบครัว */
	public SQLContainer initFamilyContainer() {
		TableQuery query;
		try {
			query = new TableQuery(FamilySchema.TABLE_NAME, DbConnection.getConnection());
			familyContainer = new SQLContainer(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return familyContainer;
	}
	
	/* TableQuery และ SQLContainer สำหรับชั้นเรียนนักเรียน */
	public SQLContainer initStudentClassRoomContainer() {
		TableQuery query;
		try {
			query = new TableQuery(StudentClassRoomSchema.TABLE_NAME, DbConnection.getConnection());
			studentClassRoomContainer = new SQLContainer(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return studentClassRoomContainer;
	}
	
	/* TableQuery และ SQLContainer สำหรับบุคลากร */
	public SQLContainer initPersonnelContainer() {
		TableQuery query;
		try {
			query = new TableQuery(PersonnelSchema.TABLE_NAME, DbConnection.getConnection());
			personnelContainer = new SQLContainer(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return personnelContainer;
	}
	
	/* TableQuery และ SQLContainer สำหรับประวัติการศึกษา */
	public SQLContainer initPersonnelGraduatedHistoryContainer() {
		TableQuery query;
		try {
			query = new TableQuery(PersonnelGraduatedHistorySchema.TABLE_NAME, DbConnection.getConnection());
			personnelGraduatedHistoryContainer = new SQLContainer(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return personnelGraduatedHistoryContainer;
	}
	
	/* TableQuery และ SQLContainer สำหรับอาจารย์ */
	public SQLContainer initSubjectContainer() {
		TableQuery query;
		try {
			query = new TableQuery(SubjectSchema.TABLE_NAME, DbConnection.getConnection());
			subjectContainer = new SQLContainer(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return subjectContainer;
	}
	
	/* TableQuery และ SQLContainer สำหรับแผนการเรียน */
	public SQLContainer initLessonPlanContainer() {
		TableQuery query;
		try {
			query = new TableQuery(LessonPlanSchema.TABLE_NAME, DbConnection.getConnection());
			lessonPlanContainer = new SQLContainer(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return lessonPlanContainer;
	}
	
	/* TableQuery และ SQLContainer สำหรับแผนการเรียนรายวิชา */
	public SQLContainer initLessonPlanSubjectContainer() {
		TableQuery query;
		try {
			query = new TableQuery(LessonPlanSubjectSchema.TABLE_NAME, DbConnection.getConnection());
			lessonPlanSubjectContainer = new SQLContainer(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return lessonPlanSubjectContainer;
	}
	
	/* TableQuery และ SQLContainer สำหรับแผนการเรียน-ชั้นเรียน */
	public SQLContainer initClassRoomLessonPlanContainer() {
		TableQuery query;
		try {
			query = new TableQuery(ClassRoomLessonPlanSchema.TABLE_NAME, DbConnection.getConnection());
			classRoomLessonPlanContainer = new SQLContainer(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return classRoomLessonPlanContainer;
	}
	
	/* TableQuery และ SQLContainer สำหรับอาจารย์ผู้สอน */
	public SQLContainer initTeachingContainer() {
		TableQuery query;
		try {
			query = new TableQuery(TeachingSchema.TABLE_NAME, DbConnection.getConnection());
			teachingContainer = new SQLContainer(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return teachingContainer;
	}
	
	/* TableQuery และ SQLContainer สำหรับอาจารย์ประจำชั้น */
	public SQLContainer initTeacherHomeroomContainer() {
		TableQuery query;
		try {
			query = new TableQuery(TeacherHomeroomSchema.TABLE_NAME, DbConnection.getConnection());
			teacherHomeroomContainer = new SQLContainer(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return teacherHomeroomContainer;
	}
	
	/* TableQuery และ SQLContainer สำหรับตารางสอน */
	public SQLContainer initTimetableContainer() {
    	TableQuery query;
		try {
			query = new TableQuery(TimetableSchema.TABLE_NAME, DbConnection.getConnection());
			timetableContainer = new SQLContainer(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return timetableContainer;
	}
	
	/* TableQuery และ SQLContainer สำหรับแผนก */
	public SQLContainer initDepartmentContainer() {
    	TableQuery query;
		try {
			query = new TableQuery(DepartmentSchema.TABLE_NAME, DbConnection.getConnection());
			departmentContainer = new SQLContainer(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return departmentContainer;
	}
	
	/* TableQuery และ SQLContainer สำหรับตำแหน่ง */
	public SQLContainer initJobPositionContainer() {
    	TableQuery query;
		try {
			query = new TableQuery(JobPositionSchema.TABLE_NAME, DbConnection.getConnection());
			jobPositionContainer = new SQLContainer(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return jobPositionContainer;
	}
	
	/* TableQuery และ SQLContainer สำหรับพฤติกรรม */
	public SQLContainer initBehaviorContainer() {
    	TableQuery query;
		try {
			query = new TableQuery(BehaviorSchema.TABLE_NAME, DbConnection.getConnection());
			behaviorContainer = new SQLContainer(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return behaviorContainer;
	}
	
	/* TableQuery และ SQLContainer สำหรับพฤติกรรมนักเรียน */
	public SQLContainer initStudentBehaviorContainer() {
    	TableQuery query;
		try {
			query = new TableQuery(StudentBehaviorSchema.TABLE_NAME, DbConnection.getConnection());
			studentBehaviorContainer = new SQLContainer(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return studentBehaviorContainer;
	}

	public SQLContainer getProvinceContainer() {
		provinceContainer.removeAllContainerFilters();
		return provinceContainer;
	}
	

	public SQLContainer getDistrictContainer() {
		districtContainer.removeAllContainerFilters();
		return districtContainer;
	}

	public SQLContainer getCityContainer() {
		cityContainer.removeAllContainerFilters();
		return cityContainer;
	}
	

	public SQLContainer getPostcodeContainer() {
		postcodeContainer.removeAllContainerFilters();
		return postcodeContainer;
	}
	

	public SQLContainer getSchoolContainer() {
		schoolContainer.removeAllContainerFilters();
		return schoolContainer;
	}
	

	public SQLContainer getUserContainer() {
		userContainer.removeAllContainerFilters();
		return userContainer;
	}
	

	public SQLContainer getRecruitStudentContainer() {
		recruitStudentContainer.removeAllContainerFilters();
		if(SessionSchema.getSchoolID() != null)
			recruitStudentContainer.addContainerFilter(new Equal(RecruitStudentSchema.SCHOOL_ID,SessionSchema.getSchoolID()));
		return recruitStudentContainer;
	}
	

	public SQLContainer getRecruitFamilyContainer() {
		recruitFamilyContainer.removeAllContainerFilters();
		return recruitFamilyContainer;
	}
	

	public SQLContainer getBuildingContainer() {
		buildingContainer.removeAllContainerFilters();
		if(SessionSchema.getSchoolID() != null)
			buildingContainer.addContainerFilter(new Equal(BuildingSchema.SCHOOL_ID,SessionSchema.getSchoolID()));
		return buildingContainer;
	}
	

	public SQLContainer getClassRoomContainer() {
		classRoomContainer.removeAllContainerFilters();
		if(SessionSchema.getSchoolID() != null)
			classRoomContainer.addContainerFilter(new Equal(ClassRoomSchema.SCHOOL_ID,SessionSchema.getSchoolID()));
		return classRoomContainer;
	}
	

	public SQLContainer getStudentContainer() {
		studentContainer.removeAllContainerFilters();
		if(SessionSchema.getSchoolID() != null)
			studentContainer.addContainerFilter(new Equal(StudentSchema.SCHOOL_ID,SessionSchema.getSchoolID()));
		return studentContainer;
	}
	

	public SQLContainer getStudentStudyContainer() {
		studentStudyContainer.removeAllContainerFilters();
		if(SessionSchema.getSchoolID() != null)
			studentStudyContainer.addContainerFilter(new Equal(StudentStudySchema.SCHOOL_ID,SessionSchema.getSchoolID()));
		return studentStudyContainer;
	}
	

	public SQLContainer getFamilyContainer() {
		familyContainer.removeAllContainerFilters();
		return familyContainer;
	}
	

	public SQLContainer getStudentClassRoomContainer() {
		studentClassRoomContainer.removeAllContainerFilters();
		if(SessionSchema.getSchoolID() != null)
			studentClassRoomContainer.addContainerFilter(new Equal(StudentClassRoomSchema.SCHOOL_ID,SessionSchema.getSchoolID()));
		return studentClassRoomContainer;
	}
	

	public SQLContainer getPersonnelContainer() {
		personnelContainer.removeAllContainerFilters();
		if(SessionSchema.getSchoolID() != null)
			personnelContainer.addContainerFilter(new Equal(PersonnelSchema.SCHOOL_ID,SessionSchema.getSchoolID()));
		return personnelContainer;
	}
	

	public SQLContainer getPersonnelGraduatedHistoryContainer() {
		personnelGraduatedHistoryContainer.removeAllContainerFilters();
		if(SessionSchema.getSchoolID() != null)
			personnelGraduatedHistoryContainer.addContainerFilter(new Equal(PersonnelGraduatedHistorySchema.SCHOOL_ID,SessionSchema.getSchoolID()));
		return personnelGraduatedHistoryContainer;
	}
	

	public SQLContainer getSubjectContainer() {
		subjectContainer.removeAllContainerFilters();
		if(SessionSchema.getSchoolID() != null)
			subjectContainer.addContainerFilter(new Equal(SubjectSchema.SCHOOL_ID,SessionSchema.getSchoolID()));
		return subjectContainer;
	}
	

	public SQLContainer getLessonPlanContainer() {
		lessonPlanContainer.removeAllContainerFilters();
		if(SessionSchema.getSchoolID() != null)
			lessonPlanContainer.addContainerFilter(new Equal(LessonPlanSchema.SCHOOL_ID,SessionSchema.getSchoolID()));
		return lessonPlanContainer;
	}
	

	public SQLContainer getLessonPlanSubjectContainer() {
		lessonPlanSubjectContainer.removeAllContainerFilters();
		if(SessionSchema.getSchoolID() != null)
			lessonPlanSubjectContainer.addContainerFilter(new Equal(LessonPlanSubjectSchema.SCHOOL_ID,SessionSchema.getSchoolID()));
		return lessonPlanSubjectContainer;
	}
	

	public SQLContainer getClassRoomLessonPlanContainer() {
		classRoomLessonPlanContainer.removeAllContainerFilters();
		if(SessionSchema.getSchoolID() != null)
			classRoomLessonPlanContainer.addContainerFilter(new Equal(ClassRoomLessonPlanSchema.SCHOOL_ID,SessionSchema.getSchoolID()));
		return classRoomLessonPlanContainer;
	}
	

	public SQLContainer getTeachingContainer() {
		teachingContainer.removeAllContainerFilters();
		if(SessionSchema.getSchoolID() != null)
			teachingContainer.addContainerFilter(new Equal(TeachingSchema.SCHOOL_ID,SessionSchema.getSchoolID()));
		return teachingContainer;
	}
	

	public SQLContainer getTeacherHomeroomContainer() {
		teacherHomeroomContainer.removeAllContainerFilters();
		if(SessionSchema.getSchoolID() != null)
			teacherHomeroomContainer.addContainerFilter(new Equal(TeacherHomeroomSchema.SCHOOL_ID,SessionSchema.getSchoolID()));
		return teacherHomeroomContainer;
	}
	

	public SQLContainer getTimetableContainer() {
		timetableContainer.removeAllContainerFilters();
		if(SessionSchema.getSchoolID() != null)
			timetableContainer.addContainerFilter(new Equal(TimetableSchema.SCHOOL_ID,SessionSchema.getSchoolID()));
		return timetableContainer;
	}
	

	public SQLContainer getDepartmentContainer() {
		departmentContainer.removeAllContainerFilters();
		if(SessionSchema.getSchoolID() != null)
			departmentContainer.addContainerFilter(new Or(new Equal(DepartmentSchema.SCHOOL_ID, SessionSchema.getSchoolID()),
					new IsNull(DepartmentSchema.SCHOOL_ID)));
		return departmentContainer;
	}
	

	public SQLContainer getJobPositionContainer() {
		jobPositionContainer.removeAllContainerFilters();
		if(SessionSchema.getSchoolID() != null)
			jobPositionContainer.addContainerFilter(new Or(new Equal(JobPositionSchema.SCHOOL_ID, SessionSchema.getSchoolID()),
					new IsNull(JobPositionSchema.SCHOOL_ID)));
		return jobPositionContainer;
	}
	

	public SQLContainer getBehaviorContainer() {
		behaviorContainer.removeAllContainerFilters();
		if(SessionSchema.getSchoolID() != null)
			behaviorContainer.addContainerFilter(new Equal(BehaviorSchema.SCHOOL_ID,SessionSchema.getSchoolID()));
		return behaviorContainer;
	}
	

	public SQLContainer getStudentBehaviorContainer() {
		studentBehaviorContainer.removeAllContainerFilters();
		if(SessionSchema.getSchoolID() != null)
			studentBehaviorContainer.addContainerFilter(new Equal(StudentBehaviorSchema.SCHOOL_ID,SessionSchema.getSchoolID()));
		return studentBehaviorContainer;
	}
}
