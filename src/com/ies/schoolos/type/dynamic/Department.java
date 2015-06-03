package com.ies.schoolos.type.dynamic;

import com.ies.schoolos.container.Container;
import com.ies.schoolos.schema.SessionSchema;
import com.ies.schoolos.schema.fundamental.DepartmentSchema;
import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.data.util.filter.Compare.Equal;
import com.vaadin.data.util.filter.IsNull;
import com.vaadin.data.util.filter.Or;
import com.vaadin.data.util.sqlcontainer.SQLContainer;

public class Department extends IndexedContainer{

	private static final long serialVersionUID = 1L;

	private Container container = new Container();
	
	public Department() {
		initContainer();
	}
 
	@SuppressWarnings("unchecked")
	private void initContainer(){
		SQLContainer dcontainer = container.getDepartmentContainer();
		dcontainer.addContainerFilter(new Or(new Equal(DepartmentSchema.SCHOOL_ID, SessionSchema.getSchoolID()),
				new IsNull(DepartmentSchema.SCHOOL_ID)));
		dcontainer.sort(new Object[]{DepartmentSchema.DEPARTMENT_ID}, new boolean[]{true});
		addContainerProperty("name", String.class,null);

		for (Object itemId:dcontainer.getItemIds()) {
			Item item = addItem(Integer.parseInt(itemId.toString()));
			item.getItemProperty("name").setValue(dcontainer.getItem(itemId).getItemProperty(DepartmentSchema.NAME).getValue());
		}
		
		//ลบ WHERE ออกจาก Query เพื่อป้องกันการค้างของคำสั่่งจากการทำงานอื่นที่เรียกตัวแปรไปใช้
		dcontainer.removeAllContainerFilters();
	}
	
	/*public static String getNameEn(int index){
		return departments[index];
	}*/
}
