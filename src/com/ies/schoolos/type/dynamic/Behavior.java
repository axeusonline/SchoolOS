package com.ies.schoolos.type.dynamic;

import com.ies.schoolos.container.Container;
import com.ies.schoolos.schema.SessionSchema;
import com.ies.schoolos.schema.fundamental.BehaviorSchema;
import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.data.util.filter.Compare.Equal;
import com.vaadin.data.util.sqlcontainer.SQLContainer;

public class Behavior extends IndexedContainer{

	private static final long serialVersionUID = 1L;

	public Behavior() {
		addContainerProperty("name", String.class,null);
		initContainer();
	}
 
	@SuppressWarnings("unchecked")
	private void initContainer(){
		SQLContainer container = Container.getBehaviorContainer();
		container.addContainerFilter(new Equal(BehaviorSchema.SCHOOL_ID, Integer.parseInt(SessionSchema.getSchoolID().toString())));
		
		for (int i = 0; i < container.size(); i++) {
			Object itemId = container.getIdByIndex(i);
			Item item = addItem(Integer.parseInt(itemId.toString()));
			Object value = container.getItem(itemId).getItemProperty(BehaviorSchema.NAME).getValue() + 
					" (" + container.getItem(itemId).getItemProperty(BehaviorSchema.MIN_SCORE).getValue() + 
					"-" +
					container.getItem(itemId).getItemProperty(BehaviorSchema.MAX_SCORE).getValue() + ")";
			item.getItemProperty("name").setValue(value);
		}
		
		/* ลบ WHERE ออกจาก Query เพื่อป้องกันการค้างของคำสั่่งจากการทำงานอื่นที่เรียกตัวแปรไปใช้ */
		container.removeAllContainerFilters();
	}
}
