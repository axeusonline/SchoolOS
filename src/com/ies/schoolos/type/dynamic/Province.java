package com.ies.schoolos.type.dynamic;

import com.ies.schoolos.container.Container;
import com.ies.schoolos.schema.ProvinceSchema;
import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.data.util.sqlcontainer.SQLContainer;

public class Province extends IndexedContainer{

	private static final long serialVersionUID = 1L;
	
	public Province() {
		addContainerProperty("name", String.class,null);
		initContainer();
	}
 
	@SuppressWarnings("unchecked")
	private void initContainer(){
		SQLContainer container = Container.getProvinceContainer();
		for (int i = 0; i < container.size(); i++) {
			Object itemId = container.getIdByIndex(i);
			Item item = addItem(Integer.parseInt(itemId.toString()));
			item.getItemProperty("name").setValue(container.getItem(itemId).getItemProperty(ProvinceSchema.NAME).getValue());
		}
	}
}
