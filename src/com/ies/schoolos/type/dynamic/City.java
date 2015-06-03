package com.ies.schoolos.type.dynamic;

import com.ies.schoolos.container.Container;
import com.ies.schoolos.schema.CitySchema;
import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.data.util.filter.Compare.Equal;
import com.vaadin.data.util.sqlcontainer.SQLContainer;

public class City extends IndexedContainer{

	private static final long serialVersionUID = 1L;

	private Container container = new Container();
	
	public City(int districtId) {
		initContainer(districtId);
	}
 
	@SuppressWarnings("unchecked")
	private void initContainer(int districtId){		
		SQLContainer ccontainer = container.getCityContainer();
		ccontainer.addContainerFilter(new Equal(CitySchema.DISTRICT_ID, districtId));
		addContainerProperty("name", String.class,null);
		for (int i = 0; i < ccontainer.size(); i++) {
			Object itemId = ccontainer.getIdByIndex(i);
			Item item = addItem(Integer.parseInt(itemId.toString()));
			item.getItemProperty("name").setValue(ccontainer.getItem(itemId).getItemProperty(CitySchema.NAME).getValue());
		}

		//ลบ WHERE ออกจาก Query เพื่อป้องกันการค้างของคำสั่่งจากการทำงานอื่นที่เรียกตัวแปรไปใช้
		ccontainer.removeAllContainerFilters();
	}
	
	
}
