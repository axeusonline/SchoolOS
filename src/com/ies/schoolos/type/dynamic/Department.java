package com.ies.schoolos.type.dynamic;

import com.ies.schoolos.container.Container;
import com.ies.schoolos.schema.fundamental.DepartmentSchema;
import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.data.util.sqlcontainer.SQLContainer;

public class Department extends IndexedContainer{

	private static final long serialVersionUID = 1L;

	
	public Department() {
		addContainerProperty("name", String.class,null);
		initContainer();
	}

	@SuppressWarnings("unchecked")
	private void initContainer() {
		SQLContainer dContainer = Container.getDepartmentContainer();
		//dContainer.addContainerFilter(new Equal(DepartmentSchema.SCHOOL_ID, UI.getCurrent().getSession().getAttribute(SessionSchema.SCHOOL_ID)));
		for (int i = 0; i < dContainer.size(); i++) {
			Object itemId = dContainer.getIdByIndex(i);
			Item item = addItem(Integer.parseInt(itemId.toString()));
			item.getItemProperty("name").setValue(dContainer.getItem(itemId).getItemProperty(DepartmentSchema.NAME).getValue());
		}
		
		//ลบ WHERE ออกจาก Query เพื่อป้องกันการค้างของคำสั่่งจากการทำงานอื่นที่เรียกตัวแปรไปใช้
		dContainer.removeAllContainerFilters();
		
	}
	
}
