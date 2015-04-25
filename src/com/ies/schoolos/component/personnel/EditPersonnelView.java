package com.ies.schoolos.component.personnel;

import java.util.Date;

import org.vaadin.dialogs.ConfirmDialog;

import com.ies.schoolos.component.personnel.layout.PersonnelLayout;
import com.ies.schoolos.container.Container;
import com.ies.schoolos.schema.info.PersonnelSchema;
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
import com.vaadin.ui.UI;

public class EditPersonnelView extends PersonnelLayout {
	private static final long serialVersionUID = 1L;

	private Object familyStore[] = new Object[4];
	private int fIndex = 0;
	
	private String maritalStr = "";
	private boolean printMode = false;
	
	private Object fatherId;
	private Object motherId;
	private Object spouseId;
	private Object personnelId;
	
	private Item personnelItem;
	private Item fatherItem;
	private Item motherItem;
	private Item spouseItem;
	
	public SQLContainer pSqlContainer = Container.getPersonnelContainer();
	public SQLContainer fSqlContainer = Container.getFamilyContainer();
	
	public EditPersonnelView(Object personnelId) {
		this.personnelId = personnelId;
		initEdtiPersonnel();
	}
	
	public EditPersonnelView(Object personnelId, boolean printMode) {
		this.personnelId = personnelId;
		this.printMode = printMode;
		initEdtiPersonnel();
	}
	
	private void initEdtiPersonnel(){
		setMaritalValueChange(maritalValueChange);
		setFinishhClick(finishClick);
		initEditData();
		initSqlContainerRowIdChange();
	}
	
	/* นำข้อมูลจาก personnelId มาทำการกรอกในฟอร์มทั้งหมด */
	private void initEditData(){
		pSqlContainer.getItem(new RowId(personnelId));
		personnelItem = pSqlContainer.getItem(new RowId(personnelId));
		
		fatherId = personnelItem.getItemProperty(PersonnelSchema.FATHER_ID).getValue();
		motherId = personnelItem.getItemProperty(PersonnelSchema.MOTHER_ID).getValue();
		spouseId = personnelItem.getItemProperty(PersonnelSchema.SPOUSE_ID).getValue();
		
		if(fatherId != null){
			fatherItem = fSqlContainer.getItem(new RowId(fatherId));
			familyStore[0] = fatherId;
		}
		if(motherId != null){
			motherItem = fSqlContainer.getItem(new RowId(motherId));
			familyStore[1] = motherId;
		}
		if(spouseId != null){
			spouseItem = fSqlContainer.getItem(new RowId(spouseId));
			familyStore[2] = spouseId;
		}

		fatherBinder.setItemDataSource(fatherItem);
		motherBinder.setItemDataSource(motherItem);
		spouseBinder.setItemDataSource(spouseItem);
		personnelBinder.setItemDataSource(personnelItem);

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
				}else if(maritalStr.equals("1") && spouseId == null){
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
						
			try {
				/* ตรวจสอบว่า อยู่สถานะการเพิ่มข้อมูลใหม่ บิดา มารดา หรือไม่? 
				 *  ถ้าใช่ ก็บันทึกข้อมูลใหม่
				 *  ถ้าไม่ ก็อัพเดทข้อมูลเดิม */
				if(isInsertParents){
					/* ตรวจสอบว่า เป็นการแก้ไข หรือการเพิ่มข้อมูลบิดา
					 *  ถ้า id = null แปลว่าเพิ่ม
					 *  ถ้า id != null แปลว่าแก้ไข  */
					if(personnelBinder.getItemDataSource().getItemProperty(PersonnelSchema.FATHER_ID).getValue() == null){
						fIndex = 0;
						/* เพิ่มบิดา โดยตรวจสอบว่าบิดาดังกล่าวไม่ซ้ำ และถูกบันทึกข้อมูลใหม่  หากบันทึกไม่ผ่านจะหยุดการทำงานทันที */
						if(!isDuplicateFather &&  !saveFormData(fSqlContainer, fatherBinder))
							return;	
						setFatherIdToPersonnelForm();
					}else{
						fatherBinder.commit();
						fSqlContainer.commit();
					}

					/* ตรวจสอบว่า เป็นการแก้ไข หรือการเพิ่มข้อมูลมารดา
					 *  ถ้า id = null แปลว่าเพิ่ม
					 *  ถ้า id != null แปลว่าแก้ไข  */
					if(personnelBinder.getItemDataSource().getItemProperty(PersonnelSchema.MOTHER_ID).getValue() == null){
						fIndex = 1;
						/* เพิ่มบิดา โดยตรวจสอบว่ามารดาดังกล่าวไม่ซ้ำ และถูกบันทึกข้อมูลใหม่  หากบันทึกไม่ผ่านจะหยุดการทำงานทันที */
						if(!isDuplicateMother && !saveFormData(fSqlContainer, motherBinder))
								return;
						setMotherIdToPersonnelForm();
					}else{
						motherBinder.commit();
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
				}
				
				/* ตรวจสอบข้อมูลคู่สมรสว่าครบถ้วนไหม?
				 *  หากครบถ้วน แสดงว่าต้องการเพิ่มข้อมูล 
				 *  หากไม่ครบ แสถงว่า ต้องการเพิ่มแต่ข้อมูลไม่ครบ หรือ ไม่ต้องการเพิ่ม 
				 *  */
				if(spouseBinder.isValid()){
					/* ตรวจสอบว่าเป็นการเพิ่มคู่สมรสใหม่หรือแก้ไขจากข้อมูลเดิม */
					if(maritalStr.equals("1") && spouseId == null){
						/* เพิ่มบิดา โดยตรวจสอบว่าคู่สมรสดังกล่าวไม่ซ้ำ และถูกบันทึกข้อมูลใหม่  หากบันทึกไม่ผ่านจะหยุดการทำงานทันที */
						if(personnelBinder.getItemDataSource().getItemProperty(PersonnelSchema.SPOUSE_ID).getValue() == null){
							fIndex = 2;
							if(!isDuplicateSpouse &&  !saveFormData(fSqlContainer, spouseBinder))
								return;
							setSpouseIdToPersonnelForm();
						}else{
							spouseBinder.commit();
							fSqlContainer.commit();
						}
					}else{
						/* ตรวจสอบว่าเดิมมีข้อมูลหรือเปล่า ถ้ามีก็ให้แก้ไขแทน */
						if(spouseBinder.getItemDataSource() != null){
							spouseBinder.commit();
							fSqlContainer.commit();
						}
					}
					/* เพิ่มนักเรียน หากบันทึกไม่ผ่านจะหยุดการทำงานทันที*/					
					personnelBinder.commit();
					pSqlContainer.commit();
					Notification.show("บันทึกสำเร็จ", Type.HUMANIZED_MESSAGE);
				}else{
					/* กรณีเลือกสถานะเป็นสมรส ต้องตรวจสอบว่าจะเพิ่มข้อมูลหรือไม้่?
					 *  หากตอบใช่ แปลว่าเข้าช่้อมูลไม่ครบ 
					 *  หากไม่ใช่ ถือว่าบันทึกข้อมูลบุคลากรเลย 
					 *  */
					if(maritalStr.equals("1") && spouseId == null){
						ConfirmDialog.show(UI.getCurrent(), "เพิ่มคู่สมรส","บุคลากรสถานะสมรส ต้องการเพิ่มข้อมูลคู่สมรส?","ตกลง","ยกเลิก",
					        new ConfirmDialog.Listener() {
								private static final long serialVersionUID = 1L;
								public void onClose(ConfirmDialog dialog) {
					                if (dialog.isConfirmed()) {
					                	selectSpouseFormTab();
					                }else{
					                	try{
					                		/* เพิ่มนักเรียน หากบันทึกไม่ผ่านจะหยุดการทำงานทันที*/					
						    				personnelBinder.commit();
						    				pSqlContainer.commit();
						    				Notification.show("บันทึกสำเร็จ", Type.HUMANIZED_MESSAGE);
						    				
					                	}catch (Exception e) {
					        				Notification.show("บันทึกไม่สำเร็จ", Type.WARNING_MESSAGE);
					        				e.printStackTrace();
					        			}
					                }
					            }
				        });
					}
				}
				
				/* ตรวจสอบสถานะการพิมพ์*/
				if(printMode){
					//visiblePrintButton();
					//new PersonnelReport(Integer.parseInt(personnelId.toString()));
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
				familyStore[fIndex] = arg0.getNewRowId();
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
				System.err.println(fieldGroup.getPropertyId(field) + "," + data);
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
	private void setFatherIdToPersonnelForm(){
		personnelItem.getItemProperty(PersonnelSchema.FATHER_ID).setValue(Integer.parseInt(familyStore[0].toString()));
	}
	
	@SuppressWarnings("unchecked")
	private void setMotherIdToPersonnelForm(){
		personnelItem.getItemProperty(PersonnelSchema.MOTHER_ID).setValue(Integer.parseInt(familyStore[1].toString()));
	}
	
	@SuppressWarnings("unchecked")
	private void setSpouseIdToPersonnelForm(){
		personnelItem.getItemProperty(PersonnelSchema.SPOUSE_ID).setValue(Integer.parseInt(familyStore[2].toString()));
	}
}
