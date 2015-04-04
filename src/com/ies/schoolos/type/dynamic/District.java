package com.ies.schoolos.type.dynamic;

import com.ies.schoolos.container.Container;
import com.ies.schoolos.schema.DistrictSchema;
import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.data.util.filter.Compare.Equal;
import com.vaadin.data.util.sqlcontainer.SQLContainer;

public class District extends IndexedContainer{

	private static final long serialVersionUID = 1L;
	
	private Container dContainer = Container.getInstance();
		
	public District(int provinceId) {
		initContainer(provinceId);
	}
 
	@SuppressWarnings("unchecked")
	private void initContainer(int provinceId){			
		SQLContainer container = dContainer.getDistrictContainer();
		container.addContainerFilter(new Equal(DistrictSchema.PROVINCE_ID, provinceId));
		addContainerProperty("name", String.class,null);
		for (int i = 0; i < container.size(); i++) {
			Object itemId = container.getIdByIndex(i);
			Item item = addItem(Integer.parseInt(itemId.toString()));
			item.getItemProperty("name").setValue(container.getItem(itemId).getItemProperty(DistrictSchema.NAME).getValue());
		}
		

		//ลบ WHERE ออกจาก Query เพื่อป้องกันการค้างของคำสั่่งจากการทำงานอื่นที่เรียกตัวแปรไปใช้
		container.removeAllContainerFilters();
	}
}
