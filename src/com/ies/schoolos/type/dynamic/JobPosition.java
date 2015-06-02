package com.ies.schoolos.type.dynamic;

import com.ies.schoolos.component.ui.SchoolOSLayout;
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
	
	public JobPosition() {
		initContainer();
	}
 
	@SuppressWarnings("unchecked")
	private void initContainer(){
		SQLContainer container = SchoolOSLayout.container.getJobPositionContainer();
		container.addContainerFilter(new Or(new Equal(JobPositionSchema.SCHOOL_ID, SessionSchema.getSchoolID()),
				new IsNull(JobPositionSchema.SCHOOL_ID)));
		container.sort(new Object[]{JobPositionSchema.JOB_POSITION_ID}, new boolean[]{true});
		addContainerProperty("name", String.class,null);

		for (Object itemId:container.getItemIds()) {
			Item item = addItem(Integer.parseInt(itemId.toString()));
			item.getItemProperty("name").setValue(container.getItem(itemId).getItemProperty(JobPositionSchema.NAME).getValue());
		}
		
		//ลบ WHERE ออกจาก Query เพื่อป้องกันการค้างของคำสั่่งจากการทำงานอื่นที่เรียกตัวแปรไปใช้
		container.removeAllContainerFilters();
	}
	
	/*public static String getNameEn(int index){
		return jobPositions[index];
	}*/
}
