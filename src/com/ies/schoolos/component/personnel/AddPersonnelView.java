package com.ies.schoolos.component.personnel;

import java.util.Date;

import com.ies.schoolos.component.personnel.layout.PersonnelLayout;
import com.ies.schoolos.schema.CreateModifiedSchema;
import com.ies.schoolos.schema.SessionSchema;
import com.ies.schoolos.schema.info.PersonnelSchema;
import com.ies.schoolos.utility.Notification;
import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.data.util.sqlcontainer.query.QueryDelegate.RowIdChangeEvent;
import com.vaadin.data.util.sqlcontainer.query.QueryDelegate.RowIdChangeListener;
import com.vaadin.ui.Field;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Notification.Type;

public class AddPersonnelView extends PersonnelLayout {
	private static final long serialVersionUID = 1L;

	private String maritalStr = "";
	private boolean printMode = false;
	
	public AddPersonnelView() {
		initAddPersonnel();
		//setDebugMode(true);
	}
	
	public AddPersonnelView(boolean printMode) {
		this.printMode = printMode;
		initAddPersonnel();
		//setDebugMode(true);
	}
	
	private void initAddPersonnel(){
		pSqlContainer.removeAllContainerFilters();
		fSqlContainer.removeAllContainerFilters();
		
		setMaritalValueChange(maritalValueChange);
		setFinishhClick(finishClick);
		initSqlContainerRowIdChange();
	}
	
	/* Event บุคคล ที่ถูกเลือกเป็น คู่สมรส */
	private ValueChangeListener maritalValueChange = new ValueChangeListener() {
		private static final long serialVersionUID = 1L;

		@Override
		public void valueChange(ValueChangeEvent event) {
			if(event.getProperty().getValue() != null){
				enableSpouseBinder();
				maritalStr = event.getProperty().getValue().toString();

				/*กำหนดข้อมูลตามความสัมพันธ์ของคู่สมรส
				 * กรณี เลือกเป็น "โสด (0)" ข้อมูลคู่สมรสจะถูกปิดไม่ให้เพิ่ม
				 * กรณี เลือกเป็น "สมรส (1)" ข้อมูลสมรสจะถูกเปิดเพื่อทำการกรอกในฟอร์ม
				 * */
				if(maritalStr.equals("0")){
					resetSpouse();
					disableSpouseBinder();
				}else if(maritalStr.equals("1")){
					resetSpouse();
				}
			}
		}
	};
	
	/* Event ปุ่มบันทึก การสมัคร */
	private ClickListener finishClick = new ClickListener() {
		private static final long serialVersionUID = 1L;

		@Override
		public void buttonClick(ClickEvent event) {
			/* ตรวจสอบความครบถ้วนของข้อมูล*/
			if(!validateForms()){
				Notification.show("กรุณากรอกข้อมูลให้ครบถ้วน", Type.WARNING_MESSAGE);
				return;
			}
			
			/* ป้องกันการกดปุ่มบันทึกซ้ำ */
			if(idStore.size() == 0){
				if(isInsertParents){
					try {				
						/* เพิ่มบิดา  หากบันทึกไม่ผ่านจะหยุดการทำงานทันที */
						if(!saveFormData(fSqlContainer, fatherBinder))
							return;	
						
						/* เพิ่มมารดา  หากบันทึกไม่ผ่านจะหยุดการทำงานทันที */
						if(!saveFormData(fSqlContainer, motherBinder))
							return;
						
						/* ตรวจสอบ คู่สมรส 
						 *  กรณีเป็น "สมรส (1)"จะบันทึกข้อมูลคู่สมรส
						 * */
						if(maritalStr.equals("1")){
							/* เพิ่มคู่สมรส  หากบันทึกไม่ผ่านจะหยุดการทำงานทันที */
							if(!saveFormData(fSqlContainer, spouseBinder))
								return;
						}
					} catch (Exception e) {
						Notification.show("บันทึกไม่สำเร็จ", Type.WARNING_MESSAGE);
						e.printStackTrace();
					}
				}
				
				/* เพิ่มบุคลากร หากบันทึกไม่ผ่านจะหยุดการทำงานทันที*/
				if(!saveFormData(pSqlContainer, personnelBinder))
					return;
				else
					Notification.show("บันทึกสำเร็จ", Type.HUMANIZED_MESSAGE);
				
				/* ตรวจสอบสถานะการพิมพ์*/
				if(printMode){
					visiblePrintButton();
					/*WorkThread thread = new WorkThread();
			        thread.start();
			        UI.getCurrent().setPollInterval(500);*/
					//new PersonnelReport(Integer.parseInt(idStore.get(3).toString()),emailMode);
				}
			}else{
				Notification.show("ข้อมูลถูกบันทึกแล้วไม่สามารถแก้ไขได้", Type.WARNING_MESSAGE);
			}
		}
	};

	/* กำหนดค่า PK Auto Increment หลังการบันทึก */
	private void initSqlContainerRowIdChange(){
		/* บุคลากร */
		pSqlContainer.addRowIdChangeListener(new RowIdChangeListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void rowIdChange(RowIdChangeEvent arg0) {
				idStore.add(arg0.getNewRowId());
			}
		});
		
		/* บิดา แม่ คู่สมรส */
		fSqlContainer.addRowIdChangeListener(new RowIdChangeListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void rowIdChange(RowIdChangeEvent arg0) {
				idStore.add(arg0.getNewRowId());
			}
		});
	}

	/* กำหนดค่าภายใน FieldGroup ไปยัง Item */
	@SuppressWarnings({ "unchecked"})
	private boolean saveFormData(SQLContainer sqlContainer, FieldGroup fieldGroup){
		try {				
			/* เพิ่มข้อมูล */
			Object tmpItem = sqlContainer.addItem();
			Item item = sqlContainer.getItem(tmpItem);
			for(Field<?> field: fieldGroup.getFields()){
				/* หาชนิดตัวแปร ของข้อมูลภายใน Database ของแต่ละ Field */
				Class<?> clazz = item.getItemProperty(fieldGroup.getPropertyId(field)).getType();			

				String className = clazz.getName();;
				Object value = null;
				if(fieldGroup.getField(fieldGroup.getPropertyId(field)).getValue() != null && 
						!fieldGroup.getField(fieldGroup.getPropertyId(field)).getValue().equals("")){
					/* ตรวจสอบ Class ที่ต้องแปลงที่ได้จากการตรวจสอบภายใน Database จาก item.getItemProperty(fieldGroup.getPropertyId(field)).getType()
					 *  กรณี เป็นjava.sql.Dateต้องทำการเปลี่ยนเป็น java.util.date 
					 *  กรณั เป็น Double ก็แปลง Object ด้วย parseDouble ซึ่งค่าที่แปลงต้องไม่เป็น Null
					 *  กรณั เป็น Integer ก็แปลง Object ด้วย parseInt ซึ่งค่าที่แปลงต้องไม่เป็น Null
					 *    */
					if(clazz == java.sql.Date.class){
						className = Date.class.getName();
						value = fieldGroup.getField(fieldGroup.getPropertyId(field)).getValue();
					}else if(clazz == Double.class){
						value = Double.parseDouble(fieldGroup.getField(fieldGroup.getPropertyId(field)).getValue().toString());
					}else if(clazz == Integer.class){
						value = Integer.parseInt(fieldGroup.getField(fieldGroup.getPropertyId(field)).getValue().toString());
					}else{
						value = fieldGroup.getField(fieldGroup.getPropertyId(field)).getValue();
					}
				}
				
				Object data = Class.forName(className).cast(value);
				System.err.println(fieldGroup.getPropertyId(field) + "," + data);
				item.getItemProperty(fieldGroup.getPropertyId(field)).setValue(data);
			}
			
			/* ถ้าเป็นนักเรียนจะมีการเพิ่มข้อมูลเพิ่มเติมภายในจาก ข้อมูลก่อนหน้า */
			if(sqlContainer == pSqlContainer){
				
				if(isInsertParents){
					item.getItemProperty(PersonnelSchema.FATHER_ID).setValue(Integer.parseInt(idStore.get(0).toString()));
					item.getItemProperty(PersonnelSchema.MOTHER_ID).setValue(Integer.parseInt(idStore.get(1).toString()));
					/* กรณีบันทึกคู่สมรส */
					if(maritalStr.equals("1"))
						item.getItemProperty(PersonnelSchema.SPOUSE_ID).setValue(Integer.parseInt(idStore.get(2).toString()));
				}
				
				item.getItemProperty(PersonnelSchema.SCHOOL_ID).setValue(SessionSchema.getSchoolID());
				item.getItemProperty(PersonnelSchema.RECRUIT_BY_ID).setValue(SessionSchema.getUserID());
				item.getItemProperty(PersonnelSchema.RECRUIT_DATE).setValue(new Date());
				item.getItemProperty(PersonnelSchema.START_WORK_DATE).setValue(new Date());
				
				CreateModifiedSchema.setCreateAndModified(item);
			}
				
			sqlContainer.commit();
			
			return true;
		} catch (Exception e) {
			Notification.show("บันทึกไม่สำเร็จ", Type.WARNING_MESSAGE);
			e.printStackTrace();
			return false;
		}
	}
}
