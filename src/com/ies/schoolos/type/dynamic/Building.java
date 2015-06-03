package com.ies.schoolos.type.dynamic;

import com.ies.schoolos.container.Container;
import com.ies.schoolos.schema.SessionSchema;
import com.ies.schoolos.schema.fundamental.BuildingSchema;
import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.data.util.filter.Compare.Equal;
import com.vaadin.data.util.sqlcontainer.SQLContainer;

public class Building extends IndexedContainer{

	private static final long serialVersionUID = 1L;

	private Container container = new Container();
	
	public Building() {
		addContainerProperty("name", String.class,null);
		initContainer();
	}
 
	@SuppressWarnings("unchecked")
	private void initContainer(){
		SQLContainer bcontainer = container.getBuildingContainer();
		bcontainer.addContainerFilter(new Equal(BuildingSchema.SCHOOL_ID, Integer.parseInt(SessionSchema.getSchoolID().toString())));
		
		for (int i = 0; i < bcontainer.size(); i++) {
			Object itemId = bcontainer.getIdByIndex(i);
			Item item = addItem(Integer.parseInt(itemId.toString()));
			Object value = bcontainer.getItem(itemId).getItemProperty(BuildingSchema.NAME).getValue() + 
					" (" + bcontainer.getItem(itemId).getItemProperty(BuildingSchema.ROOM_NUMBER).getValue() + ")";
			item.getItemProperty("name").setValue(value);
		}
		
		/* ลบ WHERE ออกจาก Query เพื่อป้องกันการค้างของคำสั่่งจากการทำงานอื่นที่เรียกตัวแปรไปใช้ */
		bcontainer.removeAllContainerFilters();
	}
}
