package com.ies.schoolos.type.dynamic;

import com.ies.schoolos.container.Container;
import com.ies.schoolos.schema.SessionSchema;
import com.ies.schoolos.schema.fundamental.BuildingSchema;
import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.data.util.filter.Compare.Equal;
import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.ui.UI;

public class Building extends IndexedContainer{

	private static final long serialVersionUID = 1L;
	
	private Container bContainer = Container.getInstance();
	
	public Building() {
		addContainerProperty("name", String.class,null);
		initContainer();
	}
 
	@SuppressWarnings("unchecked")
	private void initContainer(){
		SQLContainer container = bContainer.getBuildingContainer();
		container.addContainerFilter(new Equal(BuildingSchema.SCHOOL_ID, Integer.parseInt(UI.getCurrent().getSession().getAttribute(SessionSchema.SCHOOL_ID).toString())));
		
		for (int i = 0; i < container.size(); i++) {
			Object itemId = container.getIdByIndex(i);
			Item item = addItem(Integer.parseInt(itemId.toString()));
			Object value = container.getItem(itemId).getItemProperty(BuildingSchema.NAME).getValue() + 
					" (" + container.getItem(itemId).getItemProperty(BuildingSchema.ROOM_NUMBER).getValue() + ")";
			item.getItemProperty("name").setValue(value);
		}
		
		/* ลบ WHERE ออกจาก Query เพื่อป้องกันการค้างของคำสั่่งจากการทำงานอื่นที่เรียกตัวแปรไปใช้ */
		container.removeAllContainerFilters();
	}
}
