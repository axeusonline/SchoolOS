package com.ies.schoolos.type.dynamic;

import com.ies.schoolos.component.ui.SchoolOSLayout;
import com.ies.schoolos.schema.CitySchema;
import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.data.util.filter.Compare.Equal;
import com.vaadin.data.util.sqlcontainer.SQLContainer;

public class City extends IndexedContainer{

	private static final long serialVersionUID = 1L;
	
	public City(int districtId) {
		initContainer(districtId);
	}
 
	@SuppressWarnings("unchecked")
	private void initContainer(int districtId){		
		SQLContainer container = SchoolOSLayout.container.getCityContainer();
		container.addContainerFilter(new Equal(CitySchema.DISTRICT_ID, districtId));
		addContainerProperty("name", String.class,null);
		for (int i = 0; i < container.size(); i++) {
			Object itemId = container.getIdByIndex(i);
			Item item = addItem(Integer.parseInt(itemId.toString()));
			item.getItemProperty("name").setValue(container.getItem(itemId).getItemProperty(CitySchema.NAME).getValue());
		}

		//ลบ WHERE ออกจาก Query เพื่อป้องกันการค้างของคำสั่่งจากการทำงานอื่นที่เรียกตัวแปรไปใช้
		container.removeAllContainerFilters();
	}
	
	
}
