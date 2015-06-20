package com.ies.schoolos.type.dynamic;

import com.ies.schoolos.container.Container;
import com.ies.schoolos.schema.SessionSchema;
import com.ies.schoolos.schema.fundamental.SubjectSchema;
import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.data.util.filter.Compare.Equal;
import com.vaadin.data.util.sqlcontainer.SQLContainer;

public class Subject extends IndexedContainer{

	private static final long serialVersionUID = 1L;

	private Container container = new Container();
	
	public Subject() {
		addContainerProperty("name", String.class,null);
		initContainer();
	}
 
	@SuppressWarnings("unchecked")
	private void initContainer(){
		SQLContainer scontainer = container.getSubjectContainer();
		scontainer.addContainerFilter(new Equal(SubjectSchema.SCHOOL_ID, Integer.parseInt(SessionSchema.getSchoolID().toString())));
		
		for (int i = 0; i < scontainer.size(); i++) {
			Object itemId = scontainer.getIdByIndex(i);
			Item item = addItem(Integer.parseInt(itemId.toString()));
			Item subjectItem = scontainer.getItem(itemId);
			String value = "";
			if(scontainer.getItem(itemId).getItemProperty(SubjectSchema.CODE).getValue() != null)
				value = subjectItem.getItemProperty(SubjectSchema.CODE).getValue() + " : ";
			value += subjectItem.getItemProperty(SubjectSchema.NAME).getValue()+" ("+subjectItem.getItemProperty(SubjectSchema.WEIGHT).getValue()+")";
			item.getItemProperty("name").setValue(value);
		}
		
		/* ลบ WHERE ออกจาก Query เพื่อป้องกันการค้างของคำสั่่งจากการทำงานอื่นที่เรียกตัวแปรไปใช้ */
		scontainer.removeAllContainerFilters();
	}
}
