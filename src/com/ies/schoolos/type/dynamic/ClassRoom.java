package com.ies.schoolos.type.dynamic;

import com.ies.schoolos.container.Container;
import com.ies.schoolos.schema.SessionSchema;
import com.ies.schoolos.schema.fundamental.ClassRoomSchema;
import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.data.util.filter.Compare.Equal;
import com.vaadin.data.util.sqlcontainer.SQLContainer;

public class ClassRoom extends IndexedContainer{

	private static final long serialVersionUID = 1L;

	private Container container = new Container();
	
	public ClassRoom() {
		addContainerProperty("name", String.class,null);
		initContainer();
	}
 
	@SuppressWarnings("unchecked")
	private void initContainer(){
		SQLContainer ccontainer = container.getClassRoomContainer();
		ccontainer.addContainerFilter(new Equal(ClassRoomSchema.SCHOOL_ID, SessionSchema.getSchoolID()));
		addContainerProperty("name", String.class,null);
		
		for (int i = 0; i < ccontainer.size(); i++) {
			Object itemId = ccontainer.getIdByIndex(i);
			Item item = addItem(Integer.parseInt(itemId.toString()));
			item.getItemProperty("name").setValue(ccontainer.getItem(itemId).getItemProperty(ClassRoomSchema.NAME).getValue());
		}
		
		//ลบ WHERE ออกจาก Query เพื่อป้องกันการค้างของคำสั่่งจากการทำงานอื่นที่เรียกตัวแปรไปใช้
		ccontainer.removeAllContainerFilters();
	}
}
