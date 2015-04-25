package com.ies.schoolos.container;

import java.io.Serializable;
import java.sql.SQLException;

import com.ies.schoolos.schema.CitySchema;
import com.ies.schoolos.schema.DistrictSchema;
import com.ies.schoolos.schema.PostcodeSchema;
import com.ies.schoolos.schema.ProvinceSchema;
import com.ies.schoolos.schema.SchoolSchema;
import com.ies.schoolos.schema.academic.ClassRoomLessonPlanSchema;
import com.ies.schoolos.schema.academic.LessonPlanSchema;
import com.ies.schoolos.schema.academic.LessonPlanSubjectSchema;
import com.ies.schoolos.schema.academic.TeachingSchema;
import com.ies.schoolos.schema.academic.TimetableSchema;
import com.ies.schoolos.schema.fundamental.BuildingSchema;
import com.ies.schoolos.schema.fundamental.ClassRoomSchema;
import com.ies.schoolos.schema.fundamental.SubjectSchema;
import com.ies.schoolos.schema.info.FamilySchema;
import com.ies.schoolos.schema.info.PersonnelGraduatedHistorySchema;
import com.ies.schoolos.schema.info.PersonnelSchema;
import com.ies.schoolos.schema.info.StudentClassRoomSchema;
import com.ies.schoolos.schema.info.StudentSchema;
import com.ies.schoolos.schema.info.StudentStudySchema;
import com.ies.schoolos.schema.recruit.RecruitStudentFamilySchema;
import com.ies.schoolos.schema.recruit.RecruitStudentSchema;
import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.data.util.sqlcontainer.query.FreeformQuery;
import com.vaadin.data.util.sqlcontainer.query.TableQuery;

public class Container implements Serializable {
	private static final long serialVersionUID = 1L;

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

	public static SQLContainer getProvinceContainer() {
		TableQuery tQuery;
    	SQLContainer provinceContainer = null;;
		try {
			tQuery = new TableQuery(ProvinceSchema.TABLE_NAME, DbConnection.getConnection());
			provinceContainer = new SQLContainer(tQuery);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return provinceContainer;
	}

	public static SQLContainer getDistrictContainer() {
		TableQuery tQuery;
    	SQLContainer districtContainer = null;;
		try {
			tQuery = new TableQuery(DistrictSchema.TABLE_NAME, DbConnection.getConnection());
			districtContainer = new SQLContainer(tQuery);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return districtContainer;
	}

	public static SQLContainer getCityContainer() {
		TableQuery tQuery;
    	SQLContainer cityContainer = null;;
		try {
			tQuery = new TableQuery(CitySchema.TABLE_NAME, DbConnection.getConnection());
			cityContainer = new SQLContainer(tQuery);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return cityContainer;
	}

	public static SQLContainer getPostcodeContainer() {
		TableQuery tQuery;
    	SQLContainer postcodeContainer = null;;
		try {
			tQuery = new TableQuery(PostcodeSchema.TABLE_NAME, DbConnection.getConnection());
			postcodeContainer = new SQLContainer(tQuery);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return postcodeContainer;
	}
	
	public static SQLContainer getSchoolContainer() {
		TableQuery tQuery;
    	SQLContainer schoolContainer = null;;
		try {
			tQuery = new TableQuery(SchoolSchema.TABLE_NAME, DbConnection.getConnection());
			schoolContainer = new SQLContainer(tQuery);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return schoolContainer;
	}

	public static SQLContainer getRecruitStudentContainer() {
		TableQuery tQuery;
    	SQLContainer recruitStudentContainer = null;;
		try {
			tQuery = new TableQuery(RecruitStudentSchema.TABLE_NAME, DbConnection.getConnection());
			recruitStudentContainer = new SQLContainer(tQuery);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return recruitStudentContainer;
	}

	public static SQLContainer getRecruitFamilyContainer() {
		TableQuery tQuery;
    	SQLContainer recruitFamilyContainer = null;;
		try {
			tQuery = new TableQuery(RecruitStudentFamilySchema.TABLE_NAME, DbConnection.getConnection());
			recruitFamilyContainer = new SQLContainer(tQuery);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return recruitFamilyContainer;
	}

	public static SQLContainer getBuildingContainer() {
		TableQuery tQuery;
    	SQLContainer buildingContainer = null;;
		try {
			tQuery = new TableQuery(BuildingSchema.TABLE_NAME, DbConnection.getConnection());
			buildingContainer = new SQLContainer(tQuery);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return buildingContainer;
	}
	
	public static SQLContainer getClassRoomContainer() {
		TableQuery tQuery;
    	SQLContainer classRoomContainer = null;;
		try {
			tQuery = new TableQuery(ClassRoomSchema.TABLE_NAME, DbConnection.getConnection());
			classRoomContainer = new SQLContainer(tQuery);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return classRoomContainer;
	}
	
	public static SQLContainer getStudentContainer() {
		TableQuery tQuery;
    	SQLContainer studentContainer = null;;
		try {
			tQuery = new TableQuery(StudentSchema.TABLE_NAME, DbConnection.getConnection());
			studentContainer = new SQLContainer(tQuery);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return studentContainer;
	}

	public static SQLContainer getStudentStudyContainer() {
		TableQuery tQuery;
    	SQLContainer studentStudyContainer = null;;
		try {
			tQuery = new TableQuery(StudentStudySchema.TABLE_NAME, DbConnection.getConnection());
			studentStudyContainer = new SQLContainer(tQuery);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return studentStudyContainer;
	}

	public static SQLContainer getFamilyContainer() {
		TableQuery tQuery;
    	SQLContainer familyContainer = null;;
		try {
			tQuery = new TableQuery(FamilySchema.TABLE_NAME, DbConnection.getConnection());
			familyContainer = new SQLContainer(tQuery);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return familyContainer;
	}
	
	public static SQLContainer getStudentClassRoomContainer() {
		TableQuery tQuery;
    	SQLContainer studentClassRoomContainer = null;;
		try {
			tQuery = new TableQuery(StudentClassRoomSchema.TABLE_NAME, DbConnection.getConnection());
			studentClassRoomContainer = new SQLContainer(tQuery);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return studentClassRoomContainer;
	}
	
	public static SQLContainer getPersonnelContainer() {
		TableQuery tQuery;
    	SQLContainer personnelContainer = null;;
		try {
			tQuery = new TableQuery(PersonnelSchema.TABLE_NAME, DbConnection.getConnection());
			personnelContainer = new SQLContainer(tQuery);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return personnelContainer;
	}
	
	public static SQLContainer getPersonnelGraduatedHistoryContainer() {
		TableQuery tQuery;
    	SQLContainer personnelGraduatedHistoryContainer = null;;
		try {
			tQuery = new TableQuery(PersonnelGraduatedHistorySchema.TABLE_NAME, DbConnection.getConnection());
			personnelGraduatedHistoryContainer = new SQLContainer(tQuery);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return personnelGraduatedHistoryContainer;
	}
	
	public static SQLContainer getSubjectContainer() {
		TableQuery tQuery;
    	SQLContainer subjectContainer = null;;
		try {
			tQuery = new TableQuery(SubjectSchema.TABLE_NAME, DbConnection.getConnection());
			subjectContainer = new SQLContainer(tQuery);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return subjectContainer;
	}
	
	public static SQLContainer getLessonPlanContainer() {
		TableQuery tQuery;
    	SQLContainer lessonPlanContainer = null;;
		try {
			tQuery = new TableQuery(LessonPlanSchema.TABLE_NAME, DbConnection.getConnection());
			lessonPlanContainer = new SQLContainer(tQuery);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return lessonPlanContainer;
	}
	
	public static SQLContainer getLessonPlanSubjectContainer() {
		TableQuery tQuery;
    	SQLContainer lessonPlanSubjectContainer = null;;
		try {
			tQuery = new TableQuery(LessonPlanSubjectSchema.TABLE_NAME, DbConnection.getConnection());
			lessonPlanSubjectContainer = new SQLContainer(tQuery);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return lessonPlanSubjectContainer;
	}
	
	public static SQLContainer getClassRoomLessonPlanContainer() {
		TableQuery tQuery;
    	SQLContainer classRoomLessonPlanContainer = null;;
		try {
			tQuery = new TableQuery(ClassRoomLessonPlanSchema.TABLE_NAME, DbConnection.getConnection());
			classRoomLessonPlanContainer = new SQLContainer(tQuery);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return classRoomLessonPlanContainer;
	}
	
	public static SQLContainer getTeachingContainer() {
		TableQuery tQuery;
    	SQLContainer teachingContainer = null;;
		try {
			tQuery = new TableQuery(TeachingSchema.TABLE_NAME, DbConnection.getConnection());
			teachingContainer = new SQLContainer(tQuery);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return teachingContainer;
	}
	
	/* TableQuery และ SQLContainer สำหรับตาราง อาจารย์ผ้สอน */
	public static SQLContainer getTimetableContainer() {
    	TableQuery qTimetable;
    	SQLContainer timetableContainer = null;;
		try {
			qTimetable = new TableQuery(TimetableSchema.TABLE_NAME, DbConnection.getConnection());
			timetableContainer = new SQLContainer(qTimetable);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return timetableContainer;
	}
	
	
}
