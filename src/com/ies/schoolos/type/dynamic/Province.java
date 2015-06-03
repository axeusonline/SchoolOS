package com.ies.schoolos.type.dynamic;

import com.ies.schoolos.container.Container;
import com.ies.schoolos.schema.ProvinceSchema;
import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.data.util.sqlcontainer.SQLContainer;

public class Province extends IndexedContainer{

	private static final long serialVersionUID = 1L;

	private Container container = new Container();
	
	public Province() {
		addContainerProperty("name", String.class,null);
		initContainer();
	}
 
	@SuppressWarnings("unchecked")
	private void initContainer(){
		SQLContainer pcontainer = container.getProvinceContainer();
		for (int i = 0; i < pcontainer.size(); i++) {
			Object itemId = pcontainer.getIdByIndex(i);
			Item item = addItem(Integer.parseInt(itemId.toString()));
			item.getItemProperty("name").setValue(pcontainer.getItem(itemId).getItemProperty(ProvinceSchema.NAME).getValue());
		}
	}
}
