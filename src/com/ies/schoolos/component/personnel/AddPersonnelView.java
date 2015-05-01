package com.ies.schoolos.component.personnel;

import java.util.Date;

import com.ies.schoolos.component.personnel.layout.PersonnelLayout;
import com.ies.schoolos.container.Container;
import com.ies.schoolos.schema.CreateModifiedSchema;
import com.ies.schoolos.schema.SchoolSchema;
import com.ies.schoolos.schema.SessionSchema;
import com.ies.schoolos.schema.UserSchema;
import com.ies.schoolos.schema.info.PersonnelSchema;
import com.ies.schoolos.utility.BCrypt;
import com.ies.schoolos.utility.Notification;
import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.fieldgroup.FieldGroup;
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

public class AddPersonnelView extends PersonnelLayout {
	private static final long serialVersionUID = 1L;

	private Object pkStore[] = new Object[4];
	private int pkIndex = 0;
	private SQLContainer userContainer = Container.getUserContainer();
	
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
						pkIndex = 0;
						if(!saveFormData(fSqlContainer, fatherBinder))
							return;	
						
						/* เพิ่มมารดา  หากบันทึกไม่ผ่านจะหยุดการทำงานทันที */
						pkIndex = 1;
						if(!saveFormData(fSqlContainer, motherBinder))
							return;
						
						/* ตรวจสอบ คู่สมรส 
						 *  กรณีเป็น "สมรส (1)"จะบันทึกข้อมูลคู่สมรส
						 * */
						pkIndex = 2;
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
				pkIndex = 3;
				if(!saveFormData(pSqlContainer, personnelBinder))
					return;
				else
					Notification.show("บันทึกสำเร็จ", Type.HUMANIZED_MESSAGE);
				
				
				generateUser();
				
				/* ตรวจสอบสถานะการพิมพ์*/
				if(printMode){
					visiblePrintButton();
					/*WorkThread thread = new WorkThread();
			        thread.start();
			        UI.getCurrent().setPollInterval(500);*/
					//new PersonnelReport(Integer.parseInt(pkStore[3).toString()),emailMode);
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
				pkStore[pkIndex] = arg0.getNewRowId();
			}
		});
		
		/* บิดา แม่ คู่สมรส */
		fSqlContainer.addRowIdChangeListener(new RowIdChangeListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void rowIdChange(RowIdChangeEvent arg0) {
				pkStore[pkIndex] = arg0.getNewRowId();
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

				item.getItemProperty(fieldGroup.getPropertyId(field)).setValue(data);
			}
			
			/* ถ้าเป็นนักเรียนจะมีการเพิ่มข้อมูลเพิ่มเติมภายในจาก ข้อมูลก่อนหน้า */
			if(sqlContainer == pSqlContainer){
				
				if(isInsertParents){
					item.getItemProperty(PersonnelSchema.FATHER_ID).setValue(Integer.parseInt(pkStore[0].toString()));
					item.getItemProperty(PersonnelSchema.MOTHER_ID).setValue(Integer.parseInt(pkStore[1].toString()));
					/* กรณีบันทึกคู่สมรส */
					if(maritalStr.equals("1"))
						item.getItemProperty(PersonnelSchema.SPOUSE_ID).setValue(Integer.parseInt(pkStore[2].toString()));
				}
				
				item.getItemProperty(PersonnelSchema.SCHOOL_ID).setValue(SessionSchema.getSchoolID());
				if(SessionSchema.getUserID() != null)
					item.getItemProperty(PersonnelSchema.RECRUIT_BY_ID).setValue(SessionSchema.getUserID());
				else
					item.getItemProperty(PersonnelSchema.RECRUIT_BY_ID).setValue(SessionSchema.getSchoolID());
				
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

	/* สร้าง User */
	@SuppressWarnings("unchecked")
	private void generateUser(){
		try{
			Object userTmpId = userContainer.addItem();
			Item userItem = userContainer.getItem(userTmpId);
			userItem.getItemProperty(UserSchema.SCHOOL_ID).setValue(Integer.parseInt(SessionSchema.getSchoolID().toString()));
			userItem.getItemProperty(UserSchema.FIRSTNAME).setValue(personnelBinder.getField(PersonnelSchema.FIRSTNAME).getValue());
			userItem.getItemProperty(UserSchema.LASTNAME).setValue(personnelBinder.getField(PersonnelSchema.LASTNAME).getValue());
			userItem.getItemProperty(UserSchema.EMAIL).setValue(personnelBinder.getField(PersonnelSchema.EMAIL).getValue());
			userItem.getItemProperty(UserSchema.PASSWORD).setValue(BCrypt.hashpw(personnelBinder.getField(PersonnelSchema.PEOPLE_ID).getValue().toString(), BCrypt.gensalt()));
			userItem.getItemProperty(UserSchema.STATUS).setValue(0);
			userItem.getItemProperty(UserSchema.REF_USER_ID).setValue(Integer.parseInt(pkStore[3].toString()));
			userItem.getItemProperty(UserSchema.REF_USER_TYPE).setValue(1);
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
			builder.append("บัญชีผู้ใช้ :" + personnelBinder.getField(PersonnelSchema.EMAIL).getValue() + "<br/>");
			builder.append("รหัสผ่าน:" + personnelBinder.getField(PersonnelSchema.PEOPLE_ID).getValue());
			
			Label username = new Label(builder.toString());
			username.setContentMode(ContentMode.HTML);
			labelLayout.addComponent(username);
			
			//new EmailSender(personnelBinder.getField(PersonnelSchema.EMAIL).getValue().toString(), "บัญชีผู้ใช้งาน", builder.toString(), null, null);
			
		}catch(Exception e){
			Notification.show("บันทึกสำเร็จ", Type.HUMANIZED_MESSAGE);
			e.printStackTrace();
		}
		
	}
}
