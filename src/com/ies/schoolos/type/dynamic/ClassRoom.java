package com.ies.schoolos.type.dynamic;

import com.ies.schoolos.container.Container;
import com.ies.schoolos.schema.ClassRoomSchema;
import com.ies.schoolos.schema.SessionSchema;
import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.data.util.filter.Compare.Equal;
import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.ui.UI;

public class ClassRoom extends IndexedContainer{

	private static final long serialVersionUID = 1L;
	
	private Container crContainer = Container.getInstance();
	
	public ClassRoom() {
		addContainerProperty("name", String.class,null);
		initContainer();
	}
 
	@SuppressWarnings("unchecked")
	private void initContainer(){
		SQLContainer container = crContainer.getClassRoomContainer();
		container.addContainerFilter(new Equal(ClassRoomSchema.SCHOOL_ID, UI.getCurrent().getSession().getAttribute(SessionSchema.SCHOOL_ID)));
		addContainerProperty("name", String.class,null);
		
		for (int i = 0; i < container.size(); i++) {
			Object itemId = container.getIdByIndex(i);
			Item item = addItem(Integer.parseInt(itemId.toString()));
			item.getItemProperty("name").setValue(container.getItem(itemId).getItemProperty(ClassRoomSchema.NAME).getValue());
		}
		
		//ลบ WHERE ออกจาก Query เพื่อป้องกันการค้างของคำสั่่งจากการทำงานอื่นที่เรียกตัวแปรไปใช้
		container.removeAllContainerFilters();
	}
}
