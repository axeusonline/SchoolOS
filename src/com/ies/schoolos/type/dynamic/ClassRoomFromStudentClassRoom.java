package com.ies.schoolos.type.dynamic;

import com.ies.schoolos.container.Container;
import com.ies.schoolos.schema.SessionSchema;
import com.ies.schoolos.schema.fundamental.ClassRoomSchema;
import com.ies.schoolos.schema.info.StudentClassRoomSchema;
import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.data.util.sqlcontainer.SQLContainer;

public class ClassRoomFromStudentClassRoom extends IndexedContainer{

	private Object studyId;
	private static final long serialVersionUID = 1L;

	public ClassRoomFromStudentClassRoom(Object studyId) {
		this.studyId = studyId;
		addContainerProperty("name", String.class,null);
		initContainer();
	}
 
	@SuppressWarnings("unchecked")
	private void initContainer(){
		StringBuilder builder = new StringBuilder();
		builder.append(" SELECT * FROM " + StudentClassRoomSchema.TABLE_NAME + " scr");
		builder.append(" INNER JOIN " + ClassRoomSchema.TABLE_NAME + " cr ON cr." + ClassRoomSchema.CLASS_ROOM_ID + "= scr." + StudentClassRoomSchema.CLASS_ROOM_ID);
		builder.append(" WHERE scr." + StudentClassRoomSchema.STUDENT_STUDY_ID + "=" + studyId.toString());
		builder.append(" AND scr." + StudentClassRoomSchema.SCHOOL_ID + "=" + SessionSchema.getSchoolID());	
		builder.append(" ORDER BY scr." + StudentClassRoomSchema.ACADEMIC_YEAR + " DESC");
		
		SQLContainer freeContainer = Container.getFreeFormContainer(builder.toString(), ClassRoomSchema.CLASS_ROOM_ID);
		addContainerProperty("name", String.class,null);
		
		for (int i = 0; i < freeContainer.size(); i++) {
			Object itemId = freeContainer.getIdByIndex(i);
			Item classRoomItem = freeContainer.getItem(itemId);
			
			Item tmpItem = addItem(Integer.parseInt(classRoomItem.getItemProperty(ClassRoomSchema.CLASS_ROOM_ID).getValue().toString()));
			tmpItem.getItemProperty("name").setValue(classRoomItem.getItemProperty(ClassRoomSchema.NAME).getValue());
		}
		
		//ลบ WHERE ออกจาก Query เพื่อป้องกันการค้างของคำสั่่งจากการทำงานอื่นที่เรียกตัวแปรไปใช้
		freeContainer.removeAllContainerFilters();
	}
}
