package com.ies.schoolos.type.dynamic;

import com.ies.schoolos.container.Container;
import com.ies.schoolos.schema.SessionSchema;
import com.ies.schoolos.schema.fundamental.SubjectSchema;
import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.data.util.filter.Compare.Equal;
import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.ui.UI;

public class Subject extends IndexedContainer{

	private static final long serialVersionUID = 1L;
	
	private Container sContainer = Container.getInstance();
	
	public Subject() {
		addContainerProperty("name", String.class,null);
		initContainer();
	}
 
	@SuppressWarnings("unchecked")
	private void initContainer(){
		SQLContainer container = sContainer.getSubjectContainer();
		container.addContainerFilter(new Equal(SubjectSchema.SCHOOL_ID, Integer.parseInt(UI.getCurrent().getSession().getAttribute(SessionSchema.SCHOOL_ID).toString())));
		
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
