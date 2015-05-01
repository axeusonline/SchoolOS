package com.ies.schoolos.component.registration;

import java.util.Date;

import com.ies.schoolos.component.registration.layout.StudentLayout;
import com.ies.schoolos.container.Container;
import com.ies.schoolos.schema.CreateModifiedSchema;
import com.ies.schoolos.schema.SchoolSchema;
import com.ies.schoolos.schema.SessionSchema;
import com.ies.schoolos.schema.UserSchema;
import com.ies.schoolos.schema.info.FamilySchema;
import com.ies.schoolos.schema.info.PersonnelSchema;
import com.ies.schoolos.schema.info.StudentSchema;
import com.ies.schoolos.schema.info.StudentStudySchema;
import com.ies.schoolos.utility.BCrypt;
import com.ies.schoolos.utility.Notification;
import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.data.util.PropertysetItem;
import com.vaadin.data.util.sqlcontainer.RowId;
import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.data.util.sqlcontainer.query.QueryDelegate.RowIdChangeEvent;
import com.vaadin.data.util.sqlcontainer.query.QueryDelegate.RowIdChangeListener;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Field;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class AddStudentView extends StudentLayout {
	private static final long serialVersionUID = 1L;

	private int pkIndex = 0;
	
	private SQLContainer userContainer = Container.getUserContainer();
	
	private String gParentsStr = "";
	private boolean printMode = false;
	private boolean isSaved = false;
	
	public AddStudentView() {
		initAddStudent();
		//setDebugMode(true);
	}
	
	public AddStudentView(boolean printMode) {
		this.printMode = printMode;
		initAddStudent();
		//setDebugMode(true);
	}
	
	private void initAddStudent(){
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
			if(!isSaved){
				if(isInsertParents){
					try {				
						/* เพิ่มบิดา  หากบันทึกไม่ผ่านจะหยุดการทำงานทันที */
						if(pkStore[0] != null){
							fatherBinder.commit();
							fSqlContainer.commit();
						}else{
							/* เพิ่มมารดา  หากบันทึกไม่ผ่านจะหยุดการทำงานทันที */
							pkIndex = 0;
							if(!saveFormData(fSqlContainer, fatherBinder))
								return;	
						}
							
						if(pkStore[1] != null){
							fatherBinder.commit();
							motherBinder.commit();
							System.err.println("แม่มี");
						}else{
							/* เพิ่มมารดา  หากบันทึกไม่ผ่านจะหยุดการทำงานทันที */
							pkIndex = 1;
							if(!saveFormData(fSqlContainer, motherBinder))
								return;	
							System.err.println("แม่ไม่มี");
						}
																		
						/* ตรวจสอบ ผู้ปกครอง 
						 *  กรณีเป็น "บิดา (0)"จะนำ id ที่ได้มาใส่เป็นผู้ครอง
						 *  กรณีเป็น "มารดา (1)"จะนำ id ที่ได้มาใส่เป็นผู้ครอง
						 *  กรณีเป็น "อื่น ๆ (2)" จะอนุญาติให้เพิ่มผู้ปกครองคนใหม่
						 * */
						pkIndex = 2;
						if(gParentsStr.equals("0")){
							pkStore[pkIndex] = pkStore[0];
						}else if(gParentsStr.equals("1")){
							pkStore[pkIndex] = pkStore[1];
						}else if(gParentsStr.equals("2")){
							if(pkStore[2] != null){
								guardianBinder.commit();
								fSqlContainer.commit();
							}else{
								/* เพิ่มผู้ปกครอง  หากบันทึกไม่ผ่านจะหยุดการทำงานทันที */
								if(!saveFormData(fSqlContainer, guardianBinder))
									return;
							}
						}
					} catch (Exception e) {
						Notification.show("บันทึกไม่สำเร็จ", Type.WARNING_MESSAGE);
						e.printStackTrace();
					}
				}
				
				/* เพิ่มนักเรียน หากบันทึกไม่ผ่านจะหยุดการทำงานทันที*/
				pkIndex = 3;
				if(!saveFormData(sSqlContainer, studentBinder))
					return;
				else
					Notification.show("บันทึกสำเร็จ", Type.HUMANIZED_MESSAGE);
				
				/* เพิ่มนักเรียน หากบันทึกไม่ผ่านจะหยุดการทำงานทันที*/
				pkIndex = 4;
				if(!saveFormData(ssSqlContainer, studentStudyBinder))
					return;
				else
					Notification.show("บันทึกสำเร็จ", Type.HUMANIZED_MESSAGE);
				
				isSaved = true;
				
				generateUser();
				
				/* ตรวจสอบสถานะการพิมพ์*/
				if(printMode){
					visiblePrintButton();
					/*WorkThread thread = new WorkThread();
			        thread.start();
			        UI.getCurrent().setPollInterval(500);*/
					//new StudentReport(Integer.parseInt(pkStore[3).toString()),emailMode);
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
				pkStore[pkIndex] = arg0.getNewRowId();
			}
		});
		
		/* นักเรียน */
		ssSqlContainer.addRowIdChangeListener(new RowIdChangeListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void rowIdChange(RowIdChangeEvent arg0) {
				pkStore[pkIndex] = arg0.getNewRowId();
			}
		});
		
		/* บิดา แม่ ผู้ปกครอง */
		fSqlContainer.addRowIdChangeListener(new RowIdChangeListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void rowIdChange(RowIdChangeEvent arg0) {
				pkStore[pkIndex] = arg0.getNewRowId();
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
	@SuppressWarnings({ "unchecked"})
	private boolean saveFormData(SQLContainer sqlContainer, FieldGroup fieldGroup){
		try {				
			/* เพิ่มข้อมูล */
			Object tmpItem = sqlContainer.addItem();
			Item item = sqlContainer.getItem(tmpItem);
			for(Field<?> field: fieldGroup.getFields()){
				/* หาชนิดตัวแปร ของข้อมูลภายใน Database ของแต่ละ Field */
				System.err.println(fieldGroup.getPropertyId(field));
				if(item == null)
					System.err.println("NULL");
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
					
					if(sqlContainer == fSqlContainer){
						if(fieldGroup.getPropertyId(field).equals(FamilySchema.PEOPLE_ID)){
							value = value.toString().replace(" ", "");
							value = value.toString().replace("-", "");
						}
					}else if(sqlContainer == sSqlContainer){
						if(fieldGroup.getPropertyId(field).equals(PersonnelSchema.PEOPLE_ID)){
							value = value.toString().replace(" ", "");
							value = value.toString().replace("-", "");
						}
					}
				}
				
				Object data = Class.forName(className).cast(value);

				item.getItemProperty(fieldGroup.getPropertyId(field)).setValue(data);
			}
			
			/* ถ้าเป็นนักเรียนจะมีการเพิ่มข้อมูลเพิ่มเติมภายในจาก ข้อมูลก่อนหน้า */
			if(sqlContainer == sSqlContainer){
				
				if(isInsertParents){
					item.getItemProperty(StudentSchema.FATHER_ID).setValue(Integer.parseInt(pkStore[0].toString()));
					item.getItemProperty(StudentSchema.MOTHER_ID).setValue(Integer.parseInt(pkStore[1].toString()));
				}
				item.getItemProperty(StudentSchema.SCHOOL_ID).setValue(SessionSchema.getSchoolID());
				CreateModifiedSchema.setCreateAndModified(item);
			}else if(sqlContainer == ssSqlContainer){

				if(isInsertParents){
					item.getItemProperty(StudentStudySchema.GUARDIAN_ID).setValue(Integer.parseInt(pkStore[2].toString()));
				}
				item.getItemProperty(StudentStudySchema.SCHOOL_ID).setValue(SessionSchema.getSchoolID());
				item.getItemProperty(StudentStudySchema.STUDENT_ID).setValue(Integer.parseInt(pkStore[3].toString()));
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
	
	/* สร้าง User */
	@SuppressWarnings("unchecked")
	private void generateUser(){
		try{
			Object userTmpId = userContainer.addItem();
			Item userItem = userContainer.getItem(userTmpId);
			userItem.getItemProperty(UserSchema.SCHOOL_ID).setValue(Integer.parseInt(SessionSchema.getSchoolID().toString()));
			userItem.getItemProperty(UserSchema.FIRSTNAME).setValue(studentBinder.getField(StudentSchema.FIRSTNAME).getValue());
			userItem.getItemProperty(UserSchema.LASTNAME).setValue(studentBinder.getField(StudentSchema.LASTNAME).getValue());
			userItem.getItemProperty(UserSchema.EMAIL).setValue(studentStudyBinder.getField(StudentStudySchema.EMAIL).getValue());
			userItem.getItemProperty(UserSchema.PASSWORD).setValue(BCrypt.hashpw(studentBinder.getField(StudentSchema.PEOPLE_ID).getValue().toString(), BCrypt.gensalt()));
			userItem.getItemProperty(UserSchema.STATUS).setValue(0);
			userItem.getItemProperty(UserSchema.REF_USER_ID).setValue(Integer.parseInt(pkStore[3].toString()));
			userItem.getItemProperty(UserSchema.REF_USER_TYPE).setValue(2);
			userItem.getItemProperty(CreateModifiedSchema.CREATED_BY_ID).setValue(Integer.parseInt(pkStore[3].toString()));
			userItem.getItemProperty(CreateModifiedSchema.CREATED_DATE).setValue(new Date());
			userContainer.commit();
			
			/* ปิดหน้าต่างทั้งหมด */
			for(Window window:UI.getCurrent().getWindows()){
				window.close();
			}
			
			final Window window = new Window("รหัสเข้าใช้ระบบ กรุณาจดบันทึก");
			window.setWidth("400px");
			window.setHeight("150px");
			window.center();
			UI.getCurrent().addWindow(window);
			
			VerticalLayout labelLayout = new VerticalLayout();
			labelLayout.setWidth("100%");
			labelLayout.setMargin(true);
			window.setContent(labelLayout);
			
			StringBuilder builder = new StringBuilder();
			String schoolName = "";
			if(SessionSchema.getSchoolName() == null){
				Item item = Container.getSchoolContainer().getItem(new RowId(SessionSchema.getSchoolID()));
				schoolName += item.getItemProperty(SchoolSchema.NAME).getValue().toString();
			}else{
				schoolName = SessionSchema.getSchoolName().toString();
			}
			builder.append(schoolName + "<br/>");
			builder.append("บัญชีผู้ใช้ :" + studentStudyBinder.getField(StudentStudySchema.EMAIL).getValue() + "<br/>");
			builder.append("รหัสผ่าน:" + studentBinder.getField(StudentSchema.PEOPLE_ID).getValue());
			
			Label username = new Label(builder.toString());
			username.setContentMode(ContentMode.HTML);
			labelLayout.addComponent(username);

			//new EmailSender(studentBinder.getField(StudentStudySchema.EMAIL).getValue().toString(), "บัญชีผู้ใช้งาน", builder.toString(), null, null);
			
		}catch(Exception e){
			Notification.show("บันทึกไม่สำเร็จ", Type.WARNING_MESSAGE);
			e.printStackTrace();
		}
	}
}
