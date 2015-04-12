package com.ies.schoolos.component.recruit;

import java.util.ArrayList;
import java.util.Date;

import com.ies.schoolos.component.recruit.layout.RecruitStudentLayout;
import com.ies.schoolos.container.Container;
import com.ies.schoolos.report.RecruitStudentReport;
import com.ies.schoolos.schema.SessionSchema;
import com.ies.schoolos.schema.recruit.RecruitStudentSchema;
import com.ies.schoolos.schema.view.StatRecruitStudentCodeSchema;
import com.ies.schoolos.utility.Notification;
import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.data.util.PropertysetItem;
import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.data.util.sqlcontainer.query.QueryDelegate.RowIdChangeEvent;
import com.vaadin.data.util.sqlcontainer.query.QueryDelegate.RowIdChangeListener;
import com.vaadin.ui.Field;
import com.vaadin.ui.UI;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Notification.Type;

public class AddRecruitStudentView extends RecruitStudentLayout {
	private static final long serialVersionUID = 1L;

	private String gParentsStr = "";
	private boolean printMode = false;
	private boolean emailMode = false;
	
	/* ที่เก็บ Id Auto Increment เมื่อมีการ Commit SQLContainer 
	 * 0 แทนถึง id บิดา
	 * 1 แทนถึง id มารดา
	 * 2 แทนถึง id ผู้ปกครอง
	 * 3 แทนถึง id นักเรียน
	 * */
	private ArrayList<Object> idStore = new ArrayList<Object>();
	
	private SQLContainer sSqlContainer = Container.getInstance().getRecruitStudentContainer();
	private SQLContainer fSqlContainer = Container.getInstance().getRecruitFamilyContainer();
	
	public AddRecruitStudentView() {
		initAddRecruitStudent();
		//setDebugMode(true);
	}
	
	public AddRecruitStudentView(boolean printMode, boolean emailMode) {
		this.printMode = printMode;
		this.emailMode = emailMode;
		initAddRecruitStudent();
		//setDebugMode(true);
	}
	
	private void initAddRecruitStudent(){
		sSqlContainer.removeAllContainerFilters();
		fSqlContainer.removeAllContainerFilters();
		
		setGParentsValueChange(gParensValueChange);
		setFinishhClick(finishClick);
		initSqlContainerRowIdChange();
	}
	
	/* Event บุคคล ที่ถูกเลือกเป็น ผู้ปกครอง */
	private ValueChangeListener gParensValueChange = new ValueChangeListener() {
		private static final long serialVersionUID = 1L;

		@Override
		public void valueChange(ValueChangeEvent event) {
			if(event.getProperty().getValue() != null){
				enableGuardianBinder();
				PropertysetItem item = new PropertysetItem();
				gParentsStr = event.getProperty().getValue().toString();

				/*กำหนดข้อมูลตามความสัมพันธ์ของผู้ปกครอง
				 * กรณี เลือกเป็น "บิดา (0)" ข้อมูลบิดาจะถูกนำมาตั้งค่าภายในฟอร์ม
				 * กรณี เลือกเป็น "มารดา (1)" ข้อมูลมารดาจะถูกนำมาตั้งค่าภายในฟอร์ม
				 * กรณี เลือกเป็น "อื่น ๆ (2)" ข้อมูลผู้ปกครองจะอนุญาติให้พิมพ์เอง
				 * */
				if(gParentsStr.equals("0")){
					item = getFatherItem();
					guardianBinder.setItemDataSource(item);
					disableGuardianBinder();
				}else if(gParentsStr.equals("1")){
					item = getMotherItem();
					guardianBinder.setItemDataSource(item);
					disableGuardianBinder();
				}else if(gParentsStr.equals("2")){
					resetGuardian();
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
				try {				
					/* เพิ่มบิดา  หากบันทึกไม่ผ่านจะหยุดการทำงานทันที */
					if(!saveFormData(fSqlContainer, fatherBinder))
						return;					
						
					/* เพิ่มมารดา  หากบันทึกไม่ผ่านจะหยุดการทำงานทันที */
					if(!saveFormData(fSqlContainer, motherBinder))
						return;
					
					/* ตรวจสอบ ผู้ปกครอง 
					 *  กรณีเป็น "บิดา (0)"จะนำ id ที่ได้มาใส่เป็นผู้ครอง
					 *  กรณีเป็น "มารดา (1)"จะนำ id ที่ได้มาใส่เป็นผู้ครอง
					 *  กรณีเป็น "อื่น ๆ (2)" จะอนุญาติให้เพิ่มผู้ปกครองคนใหม่
					 * */
					if(gParentsStr.equals("0")){
						idStore.add(idStore.get(0));
					}else if(gParentsStr.equals("1")){
						idStore.add(idStore.get(1));
					}else if(gParentsStr.equals("2")){
						/* เพิ่มผู้ปกครอง  หากบันทึกไม่ผ่านจะหยุดการทำงานทันที */
						if(!saveFormData(fSqlContainer, guardianBinder))
							return;
					}
										
					/* เพิ่มนักเรียน หากบันทึกไม่ผ่านจะหยุดการทำงานทันที*/
					if(!saveFormData(sSqlContainer, studentBinder))
						return;
					else
						Notification.show("บันทึกสำเร็จ", Type.HUMANIZED_MESSAGE);
					
					/* ตรวจสอบสถานะการพิมพ์*/
					if(printMode){
						visiblePrintButton();
						/*WorkThread thread = new WorkThread();
				        thread.start();
				        UI.getCurrent().setPollInterval(500);*/
						new RecruitStudentReport(Integer.parseInt(idStore.get(3).toString()),emailMode);
					}
				} catch (Exception e) {
					Notification.show("บันทึกไม่สำเร็จ", Type.WARNING_MESSAGE);
					e.printStackTrace();
				}
			}else{
				Notification.show("ข้อมูลถูกบันทึกแล้วไม่สามารถแก้ไขได้", Type.WARNING_MESSAGE);
			}
		}
	};

	/* กำหนดค่า PK Auto Increment หลังการบันทึก */
	private void initSqlContainerRowIdChange(){
		/* นักเรียน */
		sSqlContainer.addRowIdChangeListener(new RowIdChangeListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void rowIdChange(RowIdChangeEvent arg0) {
				idStore.add(arg0.getNewRowId());
			}
		});
		
		/* บิดา แม่ ผู้ปกครอง */
		fSqlContainer.addRowIdChangeListener(new RowIdChangeListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void rowIdChange(RowIdChangeEvent arg0) {
				idStore.add(arg0.getNewRowId());
			}
		});
	}

	/* ดึงค่า Item จากฟอรฺ์มบิดา */
	private PropertysetItem getFatherItem(){
		PropertysetItem item = new PropertysetItem();
		for(Field<?> field: fatherBinder.getFields()){
			if(fatherBinder.getField(fatherBinder.getPropertyId(field)).getValue() != null)
				item.addItemProperty(fatherBinder.getPropertyId(field), new ObjectProperty<Object>(fatherBinder.getField(fatherBinder.getPropertyId(field)).getValue()));
			else
				item.addItemProperty(fatherBinder.getPropertyId(field), new ObjectProperty<Object>(""));
		}
		return item;
	}
	
	/* ดึงค่า Item จากฟอรฺ์มมารดา */
	private PropertysetItem getMotherItem(){
		PropertysetItem item = new PropertysetItem();
		for(Field<?> field: motherBinder.getFields()){
			if(motherBinder.getField(motherBinder.getPropertyId(field)).getValue() != null)
				item.addItemProperty(motherBinder.getPropertyId(field), new ObjectProperty<Object>(motherBinder.getField(motherBinder.getPropertyId(field)).getValue()));
			else
				item.addItemProperty(motherBinder.getPropertyId(field), new ObjectProperty<Object>(""));
		}
		return item;
	}
	
	/* กำหนดค่าภายใน FieldGroup ไปยัง Item */
	@SuppressWarnings({ "unchecked", "deprecation" })
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
				item.getItemProperty(fieldGroup.getPropertyId(field)).setValue(data);
			}
			
			/* ถ้าเป็นนักเรียนจะมีการเพิ่มข้อมูลเพิ่มเติมภายในจาก ข้อมูลก่อนหน้า */
			if(sqlContainer == sSqlContainer){
				
				/* ค้นหารหัสล่าสุด 
				 * กรณีไม่กำหนดรูปแบบ จะสร้างอัตโนมิตในรูปแบบของ 581001 
				 *   - 58 แสดงถึงปี พศ
				 *   - 1 แสดงถึงรหัสช่วงชั้น
				 *   - 001 แสดงถึงลำดับ
				 * กรณีมีอยู่แล้ว อาจจะการนำเข้าด้วย Excel ก็จะบวกรหัสไปเรื่อย ๆ
				 * */	
				SQLContainer freeFormContainer = Container.getInstance().getFreeFormContainer(StatRecruitStudentCodeSchema.getQuery(), StatRecruitStudentCodeSchema.MAX_CODE);
				
				int maxCode = 0;
				for(Object object:freeFormContainer.getItemIds())
					maxCode = Integer.parseInt(object.toString())+1;
				/* ลบ WHERE ออกจาก Query เพื่อป้องกันการค้างของคำสั่่งจากการทำงานอื่นที่เรียกตัวแปรไปใช้ */
				freeFormContainer.removeAllContainerFilters();
				
				String recruitCode = "";
				if(maxCode == 0){
					maxCode = new Date().getYear()+2443;
					recruitCode = Integer.toString(maxCode).substring(2)+studentBinder.getField(RecruitStudentSchema.CLASS_RANGE).getValue().toString()+"001";
				}else{
					recruitCode = Integer.toString(maxCode);
				}
				item.getItemProperty(RecruitStudentSchema.FATHER_ID).setValue(Integer.parseInt(idStore.get(0).toString()));
				item.getItemProperty(RecruitStudentSchema.MOTHER_ID).setValue(Integer.parseInt(idStore.get(1).toString()));
				item.getItemProperty(RecruitStudentSchema.GUARDIAN_ID).setValue(Integer.parseInt(idStore.get(2).toString()));
				item.getItemProperty(RecruitStudentSchema.REGISTER_DATE).setValue(new Date());
				item.getItemProperty(RecruitStudentSchema.RECRUIT_CODE).setValue(recruitCode);
				item.getItemProperty(RecruitStudentSchema.SCHOOL_ID).setValue(UI.getCurrent().getSession().getAttribute(SessionSchema.SCHOOL_ID));
				item.getItemProperty(RecruitStudentSchema.IS_CONFIRM).setValue(false);
				item.getItemProperty(RecruitStudentSchema.IS_GENERATE_STUDENT_CODE).setValue(false);
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
