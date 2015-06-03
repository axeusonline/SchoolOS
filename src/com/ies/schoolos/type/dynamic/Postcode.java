package com.ies.schoolos.type.dynamic;

import com.ies.schoolos.container.Container;
import com.ies.schoolos.schema.PostcodeSchema;
import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.data.util.filter.Compare.Equal;
import com.vaadin.data.util.sqlcontainer.SQLContainer;

public class Postcode extends IndexedContainer{

	private static final long serialVersionUID = 1L;

	private Container container = new Container();
	
	public Postcode(int districtId) {
		initContainer(districtId);
	}
 
	@SuppressWarnings("unchecked")
	private void initContainer(int districtId){		
		SQLContainer dcontainer = container.getPostcodeContainer();
		dcontainer.addContainerFilter(new Equal(PostcodeSchema.DISTRICT_ID, districtId));
		addContainerProperty("name", String.class,null);
		for (int i = 0; i < dcontainer.size(); i++) {
			Object itemId = dcontainer.getIdByIndex(i);
			Item item = addItem(Integer.parseInt(itemId.toString()));
			item.getItemProperty("name").setValue(dcontainer.getItem(itemId).getItemProperty(PostcodeSchema.CODE).getValue());
		}
		
		//ลบ WHERE ออกจาก Query เพื่อป้องกันการค้างของคำสั่่งจากการทำงานอื่นที่เรียกตัวแปรไปใช้
		dcontainer.removeAllContainerFilters();
	}
}
