package com.ies.schoolos.schema;

import java.util.Date;

import com.vaadin.data.Item;

public class CreateModifiedSchema {

	public static final String CREATED_BY_ID = "created_by_id";
	public static final String CREATED_DATE = "created_date";
	public static final String MODIFIED_BY_ID = "modified_by_id";
	public static final String MODIFIED_DATE = "modified_date";
	
	@SuppressWarnings("unchecked")
	public static void setCreateAndModified(Item item){
		if(item.getItemProperty(CREATED_BY_ID).getValue() == null){
			item.getItemProperty(CREATED_BY_ID).setValue(SessionSchema.getUserID());
			item.getItemProperty(CREATED_DATE).setValue(new Date());
		}else{
			item.getItemProperty(MODIFIED_BY_ID).setValue(SessionSchema.getUserID());
			item.getItemProperty(MODIFIED_DATE).setValue(new Date());	
		}
	}
}
