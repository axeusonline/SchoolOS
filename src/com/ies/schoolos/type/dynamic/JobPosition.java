package com.ies.schoolos.type.dynamic;

import com.ies.schoolos.container.Container;
import com.ies.schoolos.schema.SessionSchema;
import com.ies.schoolos.schema.fundamental.JobPositionSchema;
import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.data.util.filter.Compare.Equal;
import com.vaadin.data.util.filter.IsNull;
import com.vaadin.data.util.filter.Or;
import com.vaadin.data.util.sqlcontainer.SQLContainer;

public class JobPosition extends IndexedContainer{

	private static final long serialVersionUID = 1L;

	private Container container = new Container();
	
	public JobPosition() {
		initContainer();
	}
 
	@SuppressWarnings("unchecked")
	private void initContainer(){
		SQLContainer jcontainer = container.getJobPositionContainer();
		jcontainer.addContainerFilter(new Or(new Equal(JobPositionSchema.SCHOOL_ID, SessionSchema.getSchoolID()),
				new IsNull(JobPositionSchema.SCHOOL_ID)));
		jcontainer.sort(new Object[]{JobPositionSchema.JOB_POSITION_ID}, new boolean[]{true});
		addContainerProperty("name", String.class,null);

		for (Object itemId:jcontainer.getItemIds()) {
			Item item = addItem(Integer.parseInt(itemId.toString()));
			item.getItemProperty("name").setValue(jcontainer.getItem(itemId).getItemProperty(JobPositionSchema.NAME).getValue());
		}
		
		//ลบ WHERE ออกจาก Query เพื่อป้องกันการค้างของคำสั่่งจากการทำงานอื่นที่เรียกตัวแปรไปใช้
		jcontainer.removeAllContainerFilters();
	}
	
	/*public static String getNameEn(int index){
		return jobPositions[index];
	}*/
}
