package com.ies.schoolos.component.info;

import com.ies.schoolos.component.registration.layout.StudentLayout;
import com.ies.schoolos.schema.info.StudentSchema;
import com.ies.schoolos.schema.info.StudentStudySchema;
import com.vaadin.data.Item;
import com.vaadin.data.util.sqlcontainer.RowId;

public class StudentView extends StudentLayout {
	private static final long serialVersionUID = 1L;

	private Object studyId;
	
	public StudentView(Object studyId) {
		this.studyId = studyId;
		initEditData();
	}
	
	/* นำข้อมูลจาก studyId มาทำการกรอกในฟอร์มทั้งหมด */
	private void initEditData(){
		Item studyItem = ssSqlContainer.getItem(studyId);
		Item studentItem = sSqlContainer.getItem(new RowId(studyItem.getItemProperty(StudentStudySchema.STUDENT_ID).getValue()));

		Object fatherId = studentItem.getItemProperty(StudentSchema.FATHER_ID).getValue();
		Object motherId = studentItem.getItemProperty(StudentSchema.MOTHER_ID).getValue();
		Object guardianId = studyItem.getItemProperty(StudentStudySchema.GUARDIAN_ID).getValue();
		
		Item fatherItem = null;
		Item motherItem = null;
		Item guardianItem = null;
		if(fatherId != null){
			fatherItem = fSqlContainer.getItem(new RowId(fatherId));
			pkStore[0] = fatherId;
		}
		if(motherId != null){
			motherItem = fSqlContainer.getItem(new RowId(motherId));
			pkStore[1] = motherId;
		}
		if(guardianId != null){
			guardianItem = fSqlContainer.getItem(new RowId(guardianId));
			pkStore[2] = guardianId;
		}

		fatherBinder.setItemDataSource(fatherItem);
		motherBinder.setItemDataSource(motherItem);
		guardianBinder.setItemDataSource(guardianItem);
		studentBinder.setItemDataSource(studentItem);
		studentStudyBinder.setItemDataSource(studyItem);
		
		fatherBinder.setReadOnly(true);
		motherBinder.setReadOnly(true);
		guardianBinder.setReadOnly(true);
		studentBinder.setReadOnly(true);
		studentStudyBinder.setReadOnly(true);
	}
}
