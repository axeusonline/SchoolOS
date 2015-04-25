package com.ies.schoolos.component.recruit;

import java.util.Date;

import com.ies.schoolos.component.recruit.layout.RecruitStudentLayout;
import com.ies.schoolos.container.Container;
import com.ies.schoolos.report.RecruitStudentReport;
import com.ies.schoolos.schema.recruit.RecruitStudentSchema;
import com.ies.schoolos.utility.Notification;
import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.sqlcontainer.RowId;
import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.data.util.sqlcontainer.query.QueryDelegate.RowIdChangeEvent;
import com.vaadin.data.util.sqlcontainer.query.QueryDelegate.RowIdChangeListener;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Field;

public class EditRecruitStudentView extends RecruitStudentLayout {
	private static final long serialVersionUID = 1L;

	private String gParentsStr = "";
	private boolean printMode = false;

	private boolean isNewGuardian = false;
	
	private Object fatherId;
	private Object motherId;
	private Object guardianId;
	private Object newGuardianId;
	private Object studentId;
	
	private Item studentItem;
	private Item fatherItem;
	private Item motherItem;
	private Item guardianItem;
	
	public SQLContainer sSqlContainer = Container.getRecruitStudentContainer();
	public SQLContainer fSqlContainer = Container.getRecruitFamilyContainer();
	
	public EditRecruitStudentView(Object studentId) {
		this.studentId = studentId;
		initEdtiRecruitStudent();
	}
	
	public EditRecruitStudentView(Object studentId, boolean printMode) {
		this.studentId = studentId;
		this.printMode = printMode;
		initEdtiRecruitStudent();
	}
	
	private void initEdtiRecruitStudent(){
		setGParentsValueChange(gParensValueChange);
		setFinishhClick(finishClick);
		initEditData();
		initSqlContainerRowIdChange();
	}
	
	/* นำข้อมูลจาก studentId มาทำการกรอกในฟอร์มทั้งหมด */
	private void initEditData(){
		sSqlContainer.getItem(new RowId(studentId));

		studentItem = sSqlContainer.getItem(new RowId(studentId));
		
		fatherId = studentItem.getItemProperty(RecruitStudentSchema.FATHER_ID).getValue();
		motherId = studentItem.getItemProperty(RecruitStudentSchema.MOTHER_ID).getValue();
		guardianId = studentItem.getItemProperty(RecruitStudentSchema.GUARDIAN_ID).getValue();
		
		fatherItem = fSqlContainer.getItem(new RowId(fatherId));
		motherItem = fSqlContainer.getItem(new RowId(motherId));
		guardianItem = fSqlContainer.getItem(new RowId(guardianId));	
		
		fatherBinder.setItemDataSource(fatherItem);
		motherBinder.setItemDataSource(motherItem);
		guardianBinder.setItemDataSource(guardianItem);
		studentBinder.setItemDataSource(studentItem);
		
		/*ตรวจความสัมพันธ์ของผู้ปกครอง
		 *  กรณี เป็น "บิดา/มารดา (0)"
		 *  กรณี เป็น "อื่น ๆ " 
		 * */				
		int gRelation = Integer.parseInt(studentItem.getItemProperty(RecruitStudentSchema.GUARDIAN_RELATION).getValue().toString());
		if(gRelation == 0){
			/*ตรวจสอบ PK ของผู้ปกครองว่าเป็น บิดา หรือ มารดา
			 *  กรณี เป็น "บิดา (0)"
			 *  กรณี เป็น "มารดา (1)" 
			 * */	
			if(guardianId.equals(fatherId)){
				setGParentsValue(0);
			}else if(guardianId.equals(motherId)){
				setGParentsValue(1);
			}
		}else{
			setGParentsValue(2);
		}
	}
	
	/* Event บุคคล ที่ถูกเลือกเป็น ผู้ปกครอง */
	private ValueChangeListener gParensValueChange = new ValueChangeListener() {
		private static final long serialVersionUID = 1L;

		@Override
		public void valueChange(ValueChangeEvent event) {
			if(event.getProperty().getValue() != null){
				gParentsStr = event.getProperty().getValue().toString();

				/*กำหนดข้อมูลตามความสัมพันธ์ของผู้ปกครอง
				 * กรณี เลือกเป็น "บิดา (0)" ข้อมูลบิดาจะถูกนำมาตั้งค่าภายในฟอร์ม
				 * กรณี เลือกเป็น "มารดา (1)" ข้อมูลมารดาจะถูกนำมาตั้งค่าภายในฟอร์ม
				 * กรณี เลือกเป็น "อื่น ๆ (2)" ข้อมูลผู้ปกครองจะอนุญาติให้พิมพ์เอง
				 * */
				if(gParentsStr.equals("0")){
					newGuardianId = fatherId;
					guardianBinder.setItemDataSource(fatherItem);
					/* ตั้งค่าความสัมพันธืของผู้ปกครอง เป็น "บิดา/มารดา" */
					setGuardianRelationValue(0);
					disableGuardianBinder();
				}else if(gParentsStr.equals("1")){
					newGuardianId = motherId;
					guardianBinder.setItemDataSource(motherItem);
					/* ตั้งค่าความสัมพันธืของผู้ปกครอง เป็น "บิดา/มารดา" */
					setGuardianRelationValue(0);
					disableGuardianBinder();
				}else if(gParentsStr.equals("2")){
					newGuardianId = guardianId;
					enableGuardianBinder();
					/*
					 * ตรวจสอบ guardianId เดิมว่า ใครเป็นผู้ปกครอง
					 *  กรณีเดิมเป็นบิดา หรือ มาดา แสดงถึงการเพิ่มข้อมูลใหม่ ก็จะทำการตั้งสถานะเป็น เพิ่มผู้ปกครองใหม่
					 *  กรณีเดิ่มเป็นอื่น ๆ ก็จะทำการแทนค่าเดิมกลับมา
					 * */
					if(guardianId.equals(fatherId) || guardianId.equals(motherId)){
						isNewGuardian = true;
					}else{
						isNewGuardian = false;
						guardianBinder.setItemDataSource(guardianItem);
					}
					resetGuardian();
				}
				setNewGuardianIdToStudentForm();
			}
		}
	};
	
	/* Event ปุ่มบันทึก การสมัคร */
	private ClickListener finishClick = new ClickListener() {
		private static final long serialVersionUID = 1L;

		@Override
		public void buttonClick(ClickEvent event) {
			if(!validateForms()){
				Notification.show("กรุณากรอกข้อมูลให้ครบถ้วน", Type.WARNING_MESSAGE);
				return;
			}
		
			try {
				/* เพิ่มบิดา  หากบันทึกไม่ผ่านจะหยุดการทำงานทันที */
				fatherBinder.commit();
				fSqlContainer.commit();
					
				/* เพิ่มมารดา  หากบันทึกไม่ผ่านจะหยุดการทำงานทันที */
				motherBinder.commit();
				fSqlContainer.commit();
				
				/* ตรวจสอบว่าเป็นการเพิ่มผู้ปกครองใหม่หรือแก้ไขจากข้อมูลเดิม */
				if(isNewGuardian){
					if(!saveFormData(fSqlContainer, guardianBinder))
						return;
				}else{
					guardianBinder.commit();
					fSqlContainer.commit();
				}
									
				/* เพิ่มนักเรียน หากบันทึกไม่ผ่านจะหยุดการทำงานทันที*/					
				studentBinder.commit();
				sSqlContainer.commit();
				
				/* ตรวจสอบสถานะการพิมพ์*/
				if(printMode){
					visiblePrintButton();
					new RecruitStudentReport(Integer.parseInt(studentId.toString()));
				}
				
				Notification.show("บันทึกสำเร็จ", Type.HUMANIZED_MESSAGE);
				
			} catch (Exception e) {
				Notification.show("บันทึกไม่สำเร็จ", Type.WARNING_MESSAGE);
				e.printStackTrace();
			}
		}
	};
	
	/* กำหนดค่า PK Auto Increment หลังการบันทึก */
	private void initSqlContainerRowIdChange(){		
		/* บิดา แม่ ผู้ปกครอง */
		fSqlContainer.addRowIdChangeListener(new RowIdChangeListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void rowIdChange(RowIdChangeEvent arg0) {
				newGuardianId = arg0.getNewRowId();
				setNewGuardianIdToStudentForm();
			}
		});
	}
	
	/* กำหนดค่าภายใน FieldGroup ไปยัง Item 
	 *  ใช้กรณี เปลี่ยนจาก บิดา มารดา เป็น อื่น ๆ 
	 * */
	@SuppressWarnings("unchecked")
	private boolean saveFormData(SQLContainer sqlContainer, FieldGroup fieldGroup){
		try {
			/* เพิ่มข้อมูล */
			Object tmpItem = sqlContainer.addItem();
			Item item = sqlContainer.getItem(tmpItem);
			for(Field<?> field: fieldGroup.getFields()){
				/* หาชนิดตัวแปร ของข้อมูลภายใน Database ของแต่ละ Field */
				Class<?> clazz = item.getItemProperty(fieldGroup.getPropertyId(field)).getType();
				
				/* ตรวจสอบ Class ที่ต้องแปลงที่ได้จากการตรวจสอบภายใน Database จาก item.getItemProperty(fieldGroup.getPropertyId(field)).getType()
				 *  กรณี เป็นjava.sql.Dateต้องทำการเปลี่ยนเป็น java.util.date 
				 *  กรณั เป็น Double ก็แปลง Object ด้วย parseDouble ซึ่งค่าที่แปลงต้องไม่เป็น Null
				 *  กรณั เป็น Integer ก็แปลง Object ด้วย parseInt ซึ่งค่าที่แปลงต้องไม่เป็น Null
				 *    */
				String className = clazz.getName();;
				Object value = null;
				
				if(clazz == java.sql.Date.class){
					className = Date.class.getName();
					value = fieldGroup.getField(fieldGroup.getPropertyId(field)).getValue();
				}else if(clazz == Double.class && fieldGroup.getField(fieldGroup.getPropertyId(field)).getValue() != null){
					value = Double.parseDouble(fieldGroup.getField(fieldGroup.getPropertyId(field)).getValue().toString());
				}else if(clazz == Integer.class && fieldGroup.getField(fieldGroup.getPropertyId(field)).getValue() != null){
					value = Integer.parseInt(fieldGroup.getField(fieldGroup.getPropertyId(field)).getValue().toString());
				}else{
					value = fieldGroup.getField(fieldGroup.getPropertyId(field)).getValue();
				}
				Object data = Class.forName(className).cast(value);
				item.getItemProperty(fieldGroup.getPropertyId(field)).setValue(data);
			}
			sqlContainer.commit();
			
			return true;
		} catch (Exception e) {
			Notification.show("บันทึกไม่สำเร็จ", Type.WARNING_MESSAGE);
			e.printStackTrace();
			return false;
		}
	}
	
	@SuppressWarnings("unchecked")
	private void setNewGuardianIdToStudentForm(){
		studentItem.getItemProperty(RecruitStudentSchema.GUARDIAN_ID).setValue(Integer.parseInt(newGuardianId.toString()));
	}
}
