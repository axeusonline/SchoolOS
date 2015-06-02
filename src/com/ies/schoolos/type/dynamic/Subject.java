package com.ies.schoolos.type.dynamic;

import com.ies.schoolos.component.ui.SchoolOSLayout;
import com.ies.schoolos.schema.SessionSchema;
import com.ies.schoolos.schema.fundamental.SubjectSchema;
import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.data.util.filter.Compare.Equal;
import com.vaadin.data.util.sqlcontainer.SQLContainer;

public class Subject extends IndexedContainer{

	private static final long serialVersionUID = 1L;
	
	private SQLContainer container = SchoolOSLayout.container.getSubjectContainer();
	
	public Subject() {
		addContainerProperty("name", String.class,null);
		initContainer();
	}
 
	@SuppressWarnings("unchecked")
	private void initContainer(){
		container.addContainerFilter(new Equal(SubjectSchema.SCHOOL_ID, Integer.parseInt(SessionSchema.getSchoolID().toString())));
		
		for (int i = 0; i < container.size(); i++) {
			Object itemId = container.getIdByIndex(i);
			Item item = addItem(Integer.parseInt(itemId.toString()));
			String value = "";
			if(container.getItem(itemId).getItemProperty(SubjectSchema.CODE).getValue() != null)
				value = container.getItem(itemId).getItemProperty(SubjectSchema.CODE).getValue() + " : ";
			value += container.getItem(itemId).getItemProperty(SubjectSchema.NAME).getValue();
			item.getItemProperty("name").setValue(value);
		}
		
		/* ลบ WHERE ออกจาก Query เพื่อป้องกันการค้างของคำสั่่งจากการทำงานอื่นที่เรียกตัวแปรไปใช้ */
		container.removeAllContainerFilters();
	}
}
