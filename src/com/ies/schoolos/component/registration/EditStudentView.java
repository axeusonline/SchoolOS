package com.ies.schoolos.component.registration;

import java.util.Date;

import com.ies.schoolos.component.registration.layout.StudentLayout;
import com.ies.schoolos.container.Container;
import com.ies.schoolos.schema.info.StudentSchema;
import com.ies.schoolos.schema.info.StudentStudySchema;
import com.ies.schoolos.schema.recruit.RecruitStudentSchema;
import com.ies.schoolos.utility.Notification;
import com.ies.schoolos.utility.Utility;
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

public class EditStudentView extends StudentLayout {
	private static final long serialVersionUID = 1L;

	private int pkIndex = 0;
	
	private String gParentsStr = "";
	private boolean printMode = false;
	
	private boolean isNewGuardian = false;
	
	private Object fatherId;
	private Object motherId;
	private Object guardianId;
	private Object newGuardianId;
	private Object studyId;
	
	private Item studentItem;
	private Item studyItem;
	private Item fatherItem;
	private Item motherItem;
	private Item guardianItem;
	
	public SQLContainer ssSqlContainer = Container.getStudentStudyContainer();
	public SQLContainer sSqlContainer = Container.getStudentContainer();
	public SQLContainer fSqlContainer = Container.getFamilyContainer();
	
	public EditStudentView(Object studyId) {
		isEdit = true;
		this.studyId = studyId;
		initEdtiStudent();
	}
	
	public EditStudentView(Object studyId, boolean printMode) {
		isEdit = true;
		this.studyId = studyId;
		this.printMode = printMode;
		initEdtiStudent();
	}
	
	private void initEdtiStudent(){
		setGParentsValueChange(gParensValueChange);
		setFinishhClick(finishClick);
		initEditData();
		initSqlContainerRowIdChange();
	}
	
	/* นำข้อมูลจาก studyId มาทำการกรอกในฟอร์มทั้งหมด */
	private void initEditData(){
		studyItem = ssSqlContainer.getItem(studyId);
		studentItem = sSqlContainer.getItem(new RowId(studyItem.getItemProperty(StudentStudySchema.STUDENT_ID).getValue()));

		fatherId = studentItem.getItemProperty(StudentSchema.FATHER_ID).getValue();
		motherId = studentItem.getItemProperty(StudentSchema.MOTHER_ID).getValue();
		guardianId = studyItem.getItemProperty(StudentStudySchema.GUARDIAN_ID).getValue();
		
		if(fatherId != null){
			fatherItem = fSqlContainer.getItem(new RowId(fatherId));
			pkStore[0] = fatherId;
		}
		if(motherId != null){
			motherItem = fSqlContainer.getItem(new RowId(motherId));
			pkStore[1] = motherId;
		}
		if(guardianId != null){
			guardianItem = fSqlContainer.getItem(new RowId(guardianId));
			pkStore[2] = guardianId;
		}

		fatherBinder.setItemDataSource(fatherItem);
		motherBinder.setItemDataSource(motherItem);
		guardianBinder.setItemDataSource(guardianItem);
		studentBinder.setItemDataSource(studentItem);
		studentStudyBinder.setItemDataSource(studyItem);
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
			/* ตรวจสอบความครบถ้วนของข้อมูล*/
			if(!validateForms()){
				Notification.show("กรุณากรอกข้อมูลให้ครบถ้วน", Type.WARNING_MESSAGE);
				return;
			}
						
			try {
				/* ตรวจสอบว่า อยู่สถานะการเพิ่มข้อมูลใหม่ บิดา มารดา หรือไม่? 
				 *  ถ้าใช่ ก็บันทึกข้อมูลใหม่
				 *  ถ้าไม่ ก็อัพเดทข้อมูลเดิม */
				if(isInsertParents){
					/* ตรวจสอบว่า เป็นการแก้ไข หรือการเพิ่มข้อมูลบิดา
					 *  ถ้า id = null แปลว่าเพิ่ม
					 *  ถ้า id != null แปลว่าแก้ไข  */
					if(studentBinder.getItemDataSource().getItemProperty(StudentSchema.FATHER_ID).getValue() == null){
						pkIndex = 0;
						/* เพิ่มบิดา โดยตรวจสอบว่าบิดาดังกล่าวไม่ซ้ำ และถูกบันทึกข้อมูลใหม่  หากบันทึกไม่ผ่านจะหยุดการทำงานทันที */
						if(!isDuplicateFather &&  !saveFormData(fSqlContainer, fatherBinder))
							return;	
						setFatherIdToStudentForm();
					}else{
						fatherBinder.commit();
						fSqlContainer.commit();
					}

					/* ตรวจสอบว่า เป็นการแก้ไข หรือการเพิ่มข้อมูลมารดา
					 *  ถ้า id = null แปลว่าเพิ่ม
					 *  ถ้า id != null แปลว่าแก้ไข  */
					if(studentBinder.getItemDataSource().getItemProperty(StudentSchema.MOTHER_ID).getValue() == null){
						pkIndex = 1;
						/* เพิ่มบิดา โดยตรวจสอบว่ามารดาดังกล่าวไม่ซ้ำ และถูกบันทึกข้อมูลใหม่  หากบันทึกไม่ผ่านจะหยุดการทำงานทันที */
						if(!isDuplicateMother && !saveFormData(fSqlContainer, motherBinder))
								return;
						setMotherIdToStudentForm();
					}else{
						motherBinder.commit();
						fSqlContainer.commit();
					}
					
					/* ตรวจสอบว่า เป็นการแก้ไข หรือการเพิ่มข้อมูลผู้ปกครอง
					 *  ถ้า id = null แปลว่าเพิ่ม
					 *  ถ้า id != null แปลว่าแก้ไข  */
					if(studentStudyBinder.getItemDataSource().getItemProperty(StudentStudySchema.GUARDIAN_ID).getValue() == null){
						pkIndex = 2;
						/* เพิ่มบิดา โดยตรวจสอบว่ามารดาดังกล่าวไม่ซ้ำ และถูกบันทึกข้อมูลใหม่  หากบันทึกไม่ผ่านจะหยุดการทำงานทันที */
						if((!isDuplicateGuardian || isNewGuardian) && !saveFormData(fSqlContainer, guardianBinder))
								return;
						setGuardianIdToStudentForm();
					}else{
						guardianBinder.commit();
						fSqlContainer.commit();
					}
					
				}else{
					/* ตรวจสอบว่าเดิมมีข้อมูลหรือเปล่า ถ้ามีก็ให้แก้ไขแทน */
					if(fatherBinder.getItemDataSource() != null){
						fatherBinder.commit();
						fSqlContainer.commit();
					}

					/* ตรวจสอบว่าเดิมมีข้อมูลหรือเปล่า ถ้ามีก็ให้แก้ไขแทน */
					if(motherBinder.getItemDataSource() != null){
						motherBinder.commit();
						fSqlContainer.commit();
					}
					
					/* ตรวจสอบว่าเดิมมีข้อมูลหรือเปล่า ถ้ามีก็ให้แก้ไขแทน */
					if(guardianBinder.getItemDataSource() != null){
						guardianBinder.commit();
						fSqlContainer.commit();
					}
				}
				
				Notification.show("บันทึกสำเร็จ", Type.HUMANIZED_MESSAGE);
				/* ตรวจสอบสถานะการพิมพ์*/
				if(printMode){
					//visiblePrintButton();
					//new StudentReport(Integer.parseInt(studyId.toString()));
				}
			}catch (Exception e) {
				Notification.show("บันทึกไม่สำเร็จ", Type.WARNING_MESSAGE);
				e.printStackTrace();
			}
		}
	};
	
	/* กำหนดค่า PK Auto Increment หลังการบันทึก */
	private void initSqlContainerRowIdChange(){		
		/* บิดา แม่ คู่สมรส */
		fSqlContainer.addRowIdChangeListener(new RowIdChangeListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void rowIdChange(RowIdChangeEvent arg0) {
				pkStore[pkIndex] = arg0.getNewRowId();
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
					if(Utility.isInteger(fieldGroup.getField(fieldGroup.getPropertyId(field)).getValue()))
						value = Double.parseDouble(fieldGroup.getField(fieldGroup.getPropertyId(field)).getValue().toString());
					else
						value = 0.0;
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
	private void setFatherIdToStudentForm(){
		studentItem.getItemProperty(StudentSchema.FATHER_ID).setValue(Integer.parseInt(pkStore[0].toString()));
	}
	
	@SuppressWarnings("unchecked")
	private void setMotherIdToStudentForm(){
		studentItem.getItemProperty(StudentSchema.MOTHER_ID).setValue(Integer.parseInt(pkStore[1].toString()));
	}
	
	@SuppressWarnings("unchecked")
	private void setGuardianIdToStudentForm(){
		studyItem.getItemProperty(StudentStudySchema.GUARDIAN_ID).setValue(Integer.parseInt(pkStore[2].toString()));
	}
	
	@SuppressWarnings("unchecked")
	private void setNewGuardianIdToStudentForm(){
		studyItem.getItemProperty(RecruitStudentSchema.GUARDIAN_ID).setValue(Integer.parseInt(newGuardianId.toString()));
	}
}
