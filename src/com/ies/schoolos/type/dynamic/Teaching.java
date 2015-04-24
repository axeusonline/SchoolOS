package com.ies.schoolos.type.dynamic;

import com.ies.schoolos.container.Container;
import com.ies.schoolos.container.DbConnection;
import com.ies.schoolos.schema.SessionSchema;
import com.ies.schoolos.schema.academic.LessonPlanSubjectSchema;
import com.ies.schoolos.schema.academic.TeachingSchema;
import com.ies.schoolos.schema.fundamental.SubjectSchema;
import com.ies.schoolos.schema.info.PersonnelSchema;
import com.ies.schoolos.utility.DateTimeUtil;
import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.data.util.sqlcontainer.query.FreeformQuery;

public class Teaching extends IndexedContainer{

	private static final long serialVersionUID = 1L;
	
	private SQLContainer tContainer;
	
	public Teaching() {
		addContainerProperty("name", String.class,null);
		initContainer();
	}
	
	public Teaching(Object classYear, Object semester){
		addContainerProperty("name", String.class,null);
		initContainer(classYear,semester);
	}
 
	@SuppressWarnings("unchecked")
	private void initContainer(){
		StringBuilder teachingBuilder = new StringBuilder();
		teachingBuilder.append(" SELECT * FROM "+ TeachingSchema.TABLE_NAME + " tc");
		teachingBuilder.append(" INNER JOIN "+ SubjectSchema.TABLE_NAME + " s ON s." + SubjectSchema.SUBJECT_ID + " = tc." + TeachingSchema.SUBJECT_ID);
		teachingBuilder.append(" WHERE tc."+ TeachingSchema.SCHOOL_ID + "=" + SessionSchema.getSchoolID());
		teachingBuilder.append(" AND tc." + TeachingSchema.ACADEMIC_YEAR + "=" + DateTimeUtil.getBuddishYear());
		
		tContainer = Container.getInstance().getFreeFormContainer(teachingBuilder.toString(), TeachingSchema.TEACHING_ID);
		for (int i = 0; i < tContainer.size(); i++) {
			Object teachingItemId = tContainer.getIdByIndex(i);
			
			Item teachingItem = tContainer.getItem(teachingItemId);
			String firstname = "";
			String lastname = "";
			/* ตรวจสอบว่า เป็นอาจารย์พิเศษไหม ถ้าใช่ แสดงว่า personnel_id = null จึงต้องดึงจากชื่อ Tmp มาแสดงแทน */
			if(teachingItem.getItemProperty(TeachingSchema.PERSONNEL_ID).getValue() == null){
				String[] nameTmp = teachingItem.getItemProperty(TeachingSchema.PERSONNEL_NAME_TMP).getValue().toString().split(" ");
				firstname = nameTmp[0];
				lastname = nameTmp[1];
			}else{
				try {
					StringBuilder builder = new StringBuilder();
					builder.append(" SELECT " + PersonnelSchema.PERSONNEL_ID + "," + PersonnelSchema.PERSONEL_CODE + "," + PersonnelSchema.FIRSTNAME + "," + PersonnelSchema.LASTNAME);
					builder.append(" FROM " + PersonnelSchema.TABLE_NAME);
					builder.append(" WHERE " + PersonnelSchema.PERSONNEL_ID + "=" + teachingItem.getItemProperty(TeachingSchema.PERSONNEL_ID).getValue());
					
					FreeformQuery tq = new FreeformQuery(builder.toString(), DbConnection.getConnection(),PersonnelSchema.PERSONNEL_ID);
					SQLContainer personnelContainer = new SQLContainer(tq);

					Item personnelItem = personnelContainer.getItem(personnelContainer.getIdByIndex(0));
					firstname = personnelItem.getItemProperty(PersonnelSchema.FIRSTNAME).getValue().toString();
					lastname = personnelItem.getItemProperty(PersonnelSchema.LASTNAME).getValue().toString();
				} catch (Exception e) {
					e.printStackTrace();
				}		
			}
			
			String subject = "";
			if(teachingItem.getItemProperty(SubjectSchema.CODE).getValue() != null)
				subject = teachingItem.getItemProperty(SubjectSchema.CODE).getValue().toString() + ":";
			subject += teachingItem.getItemProperty(SubjectSchema.NAME).getValue().toString();
			
			Item item = addItem(teachingItemId);
	        item.getItemProperty("name").setValue(subject + "(อ."+firstname + " " + lastname + ")");
		}
		
		/* ลบ WHERE ออกจาก Query เพื่อป้องกันการค้างของคำสั่่งจากการทำงานอื่นที่เรียกตัวแปรไปใช้ */
		tContainer.removeAllContainerFilters();
	}
	
	@SuppressWarnings("unchecked")
	private void initContainer(Object classYear, Object semester){
		/*SELECT * FROM teaching tc 
		INNER JOIN subject s ON s.subject_id = tc.subject_id 
		WHERE tc.school_id=9 
		AND tc.academic_year=2558 
		AND s.subject_id IN 
		(SELECT subject_id FROM lesson_plan_subject 
		WHERE class_year=9 
		AND semester=0)*/

		StringBuilder teachingBuilder = new StringBuilder();
		teachingBuilder.append(" SELECT * FROM "+ TeachingSchema.TABLE_NAME + " tc");
		teachingBuilder.append(" INNER JOIN "+ SubjectSchema.TABLE_NAME + " s ON s." + SubjectSchema.SUBJECT_ID + " = tc." + TeachingSchema.SUBJECT_ID);
		teachingBuilder.append(" WHERE tc."+ TeachingSchema.SCHOOL_ID + "=" + SessionSchema.getSchoolID());
		teachingBuilder.append(" AND tc." + TeachingSchema.ACADEMIC_YEAR + "=" + DateTimeUtil.getBuddishYear());
		teachingBuilder.append(" AND s." + SubjectSchema.SUBJECT_ID + " IN");
		teachingBuilder.append(" (SELECT " + LessonPlanSubjectSchema.SUBJECT_ID + " FROM " + LessonPlanSubjectSchema.TABLE_NAME);
		teachingBuilder.append(" WHERE " + LessonPlanSubjectSchema.CLASS_YEAR + "=" + classYear);
		teachingBuilder.append(" AND " + LessonPlanSubjectSchema.SEMESTER + "=" + semester +")");
		
		tContainer = Container.getInstance().getFreeFormContainer(teachingBuilder.toString(), TeachingSchema.TEACHING_ID);
		for (int i = 0; i < tContainer.size(); i++) {
			Object teachingItemId = tContainer.getIdByIndex(i);
			
			Item teachingItem = tContainer.getItem(teachingItemId);
			String firstname = "";
			String lastname = "";
			/* ตรวจสอบว่า เป็นอาจารย์พิเศษไหม ถ้าใช่ แสดงว่า personnel_id = null จึงต้องดึงจากชื่อ Tmp มาแสดงแทน */
			if(teachingItem.getItemProperty(TeachingSchema.PERSONNEL_ID).getValue() == null){
				String[] nameTmp = teachingItem.getItemProperty(TeachingSchema.PERSONNEL_NAME_TMP).getValue().toString().split(" ");
				firstname = nameTmp[0];
				lastname = nameTmp[1];
			}else{
				try {
					StringBuilder builder = new StringBuilder();
					builder.append(" SELECT " + PersonnelSchema.PERSONNEL_ID + "," + PersonnelSchema.PERSONEL_CODE + "," + PersonnelSchema.FIRSTNAME + "," + PersonnelSchema.LASTNAME);
					builder.append(" FROM " + PersonnelSchema.TABLE_NAME);
					builder.append(" WHERE " + PersonnelSchema.PERSONNEL_ID + "=" + teachingItem.getItemProperty(TeachingSchema.PERSONNEL_ID).getValue());

					FreeformQuery tq = new FreeformQuery(builder.toString(), DbConnection.getConnection(),PersonnelSchema.PERSONNEL_ID);
					SQLContainer personnelContainer = new SQLContainer(tq);

					Item personnelItem = personnelContainer.getItem(personnelContainer.getIdByIndex(0));
					firstname = personnelItem.getItemProperty(PersonnelSchema.FIRSTNAME).getValue().toString();
					lastname = personnelItem.getItemProperty(PersonnelSchema.LASTNAME).getValue().toString();
				} catch (Exception e) {
					e.printStackTrace();
				}		
			}
			
			String subject = "";
			if(teachingItem.getItemProperty(SubjectSchema.CODE).getValue() != null)
				subject = teachingItem.getItemProperty(SubjectSchema.CODE).getValue().toString() + ":";
			subject += teachingItem.getItemProperty(SubjectSchema.NAME).getValue().toString();
			
			Item item = addItem(teachingItemId);
	        item.getItemProperty("name").setValue(subject + "(อ."+firstname + " " + lastname + ")");
		}
		
		/* ลบ WHERE ออกจาก Query เพื่อป้องกันการค้างของคำสั่่งจากการทำงานอื่นที่เรียกตัวแปรไปใช้ */
		tContainer.removeAllContainerFilters();
	}
}
