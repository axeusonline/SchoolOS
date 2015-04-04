package com.ies.schoolos.component.recruit;

import java.util.ArrayList;
import java.util.Date;

import org.tepi.filtertable.FilterTable;
import org.vaadin.dialogs.ConfirmDialog;
import org.vaadin.haijian.ExcelExporter;

import com.ies.schoolos.component.ui.ContentPage;
import com.ies.schoolos.container.Container;
import com.ies.schoolos.filter.TableFilterDecorator;
import com.ies.schoolos.filter.TableFilterGenerator;
import com.ies.schoolos.report.excel.StudentToExcel;
import com.ies.schoolos.schema.CreateModifiedSchema;
import com.ies.schoolos.schema.FamilySchema;
import com.ies.schoolos.schema.RecruitStudentFamilySchema;
import com.ies.schoolos.schema.RecruitStudentSchema;
import com.ies.schoolos.schema.SchoolSchema;
import com.ies.schoolos.schema.SessionSchema;
import com.ies.schoolos.schema.StudentClassRoomSchema;
import com.ies.schoolos.schema.StudentSchema;
import com.ies.schoolos.schema.StudentStudySchema;
import com.ies.schoolos.schema.view.StatStudentCodeSchema;
import com.ies.schoolos.type.ClassRange;
import com.ies.schoolos.type.Prename;
import com.ies.schoolos.utility.Utility;
import com.vaadin.data.Item;
import com.vaadin.data.util.filter.And;
import com.vaadin.data.util.filter.Compare.Equal;
import com.vaadin.data.util.sqlcontainer.RowId;
import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.data.util.sqlcontainer.query.OrderBy;
import com.vaadin.data.util.sqlcontainer.query.QueryDelegate.RowIdChangeEvent;
import com.vaadin.data.util.sqlcontainer.query.QueryDelegate.RowIdChangeListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CustomTable.Align;
import com.vaadin.ui.Notification.Type;

public class RecruitToStudentView extends ContentPage {
	private static final long serialVersionUID = 1L;

	private int confirmQty = 0;
	private int unconfirmQty = 0;
	private int generateCodeType = 0;
	
	/* ที่เก็บ Id Auto Increment เมื่อมีการ Commit SQLContainer 
	 * 0 แทนถึง id บิดา
	 * 1 แทนถึง id มารดา
	 * 2 แทนถึง id ผู้ปกครอง
	 * 3 แทนถึง id นักเรียน
	 * 4 แทนถึง id ข้อมูลการเรียนนักเรียน
	 * */
	private ArrayList<Object> idStore = new ArrayList<Object>();;
	
	private SQLContainer rsContainer = Container.getInstance().getRecruitStudentContainer();
	private SQLContainer rsFamilyContainer = Container.getInstance().getRecruitFamilyContainer();
	private SQLContainer schoolContainer = Container.getInstance().getSchoolContainer();
	private SQLContainer studentContainer = Container.getInstance().getStudentContainer();
	private SQLContainer studentStudyContainer = Container.getInstance().getStudentStudyContainer();
	private SQLContainer familyContainer = Container.getInstance().getFamilyContainer();
	private SQLContainer studentClassRoomContainer = Container.getInstance().getStudentClassRoomContainer();
	
	private HorizontalLayout toolbar;
	private Button confirm;	
	private FilterTable  table;
	
	public RecruitToStudentView() {
		super("กำหนดรหัสนักเรียน");
		buildMainLayout();
		initSqlContainerRowIdChange();
	}
	
	private void buildMainLayout(){
		/* Toolbar */
		toolbar = new HorizontalLayout();
		toolbar.setSpacing(true);
		addComponent(toolbar);
		
		confirm = new Button("กำหนดรหัสอัตโนมัติ         ", FontAwesome.CHECK);
		confirm.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event) {
				Item schoolItem = schoolContainer.getItem(new RowId(UI.getCurrent().getSession().getAttribute(SessionSchema.SCHOOL_ID)));
				/* ตรวจสอบประเภทการสร้างรหัสนักเรียน
				 *  กรณีมีการตั้งค่าประเภทการกำหนดรหัสนักเรียน (อัตโนมัติ = 0 , กำหนดเอง = 1) 
				 *  กรณีไม่มีการตั้งค่าประเภทการกำหนดรหัสนักเรียน จะขึ้นเตือนว่าให้มีการตั้งค่าก่อน*/
				if(schoolItem.getItemProperty(SchoolSchema.STUDENT_CODE_GENERATE_TYPE).getValue() 	!= null){
					generateCodeType = Integer.parseInt(schoolItem.getItemProperty(SchoolSchema.STUDENT_CODE_GENERATE_TYPE).getValue().toString());

				}else{
					Notification.show("คุณยังไม่ได้กำหนดรูปแบบรหัสนักเรียน กรุณากำหนดในเมนูตั้งค่า > ข้อมูลโรงเรียน", Type.WARNING_MESSAGE);
					return;
				}

				ConfirmDialog.show(UI.getCurrent(), "ยืนยันรหัสนักเรียน", "คุณต้องการกำหนดรหัสนักเรียนตามที่ตั้งค่าไว้ ในเมนูตั้งค่า ใช่หรือไม่?", "ตกลง", "ยกเลิก", 
						 new ConfirmDialog.Listener() {
					private static final long serialVersionUID = 1L;
					
					public void onClose(ConfirmDialog dialog) {
						if (dialog.isConfirmed()) {
							transferRecruitToStudent();
						}
			               	
		           }
		       });
				
			}
		});
		toolbar.addComponent(confirm);
		
		ExcelExporter excelExporter = new ExcelExporter(new StudentToExcel());
		excelExporter.setIcon(FontAwesome.FILE_EXCEL_O);
		excelExporter.setCaption("ส่งออกไฟล์ Excel");
		toolbar.addComponent(excelExporter);
		
		/* Content */
		table = new FilterTable();
		table.setSizeFull();
		table.setSelectable(true);
		table.setFooterVisible(true);        
        
        table.addContainerProperty(RecruitStudentSchema.RECRUIT_CODE, String.class, null);
		table.addContainerProperty(RecruitStudentSchema.CLASS_RANGE, String.class, null);
		table.addContainerProperty(RecruitStudentSchema.PRENAME, String.class, null);
		table.addContainerProperty(RecruitStudentSchema.FIRSTNAME, String.class, null);
		table.addContainerProperty(RecruitStudentSchema.LASTNAME, String.class, null);

		table.setFilterDecorator(new TableFilterDecorator());
		table.setFilterGenerator(new TableFilterGenerator());
        table.setFilterBarVisible(true);
        
		table.setColumnAlignment((Object)RecruitStudentSchema.RECRUIT_CODE,Align.CENTER);
		table.setColumnAlignment(RecruitStudentSchema.CLASS_RANGE,Align.CENTER);
		table.setColumnAlignment(RecruitStudentSchema.PRENAME,Align.CENTER);
		table.setColumnAlignment(RecruitStudentSchema.FIRSTNAME,Align.CENTER);
		table.setColumnAlignment(RecruitStudentSchema.LASTNAME,Align.CENTER);

		table.setColumnHeader(RecruitStudentSchema.RECRUIT_CODE, "หมายเลขสมัคร");
		table.setColumnHeader(RecruitStudentSchema.CLASS_RANGE,"ช่วงชั้น");
		table.setColumnHeader(RecruitStudentSchema.PRENAME, "ชื่อต้น");
		table.setColumnHeader(RecruitStudentSchema.FIRSTNAME, "ชื่อ");
		table.setColumnHeader(RecruitStudentSchema.LASTNAME, "สกุล");
		table.setVisibleColumns(
				RecruitStudentSchema.RECRUIT_CODE, 
				RecruitStudentSchema.CLASS_RANGE,
				RecruitStudentSchema.PRENAME,
				RecruitStudentSchema.FIRSTNAME, 
				RecruitStudentSchema.LASTNAME);
		
		addComponent(table);
        setExpandRatio(table, 1);

		setTableData();
		setFooterData();

	}
	
	/* กำหนดค่า PK Auto Increment หลังการบันทึก */
	private void initSqlContainerRowIdChange(){
		/* นักเรียน */
		studentContainer.addRowIdChangeListener(new RowIdChangeListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void rowIdChange(RowIdChangeEvent arg0) {
				idStore.add(arg0.getNewRowId());
				System.err.println("UPDATE STUDENT COMPLET:" + idStore.size() + ",id:" + arg0.getNewRowId().toString());
			}
		});
		
		/* ข้อมูลการเรียนนักเรียน */
		studentStudyContainer.addRowIdChangeListener(new RowIdChangeListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void rowIdChange(RowIdChangeEvent arg0) {
				idStore.add(arg0.getNewRowId());
				System.err.println("UPDATE STUDENT STUDY COMPLET:" + idStore.size() + ",id:" + arg0.getNewRowId().toString());
			}
		});
		
		/* บิดา แม่ ผู้ปกครอง */
		familyContainer.addRowIdChangeListener(new RowIdChangeListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void rowIdChange(RowIdChangeEvent arg0) {
				idStore.add(arg0.getNewRowId());
				System.err.println("UPDATE FAMILY COMPLET:" + idStore.size() + ",id:" + arg0.getNewRowId().toString()+","+idStore.toString());
			}
		});
		
		studentClassRoomContainer.addRowIdChangeListener(new RowIdChangeListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void rowIdChange(RowIdChangeEvent arg0) {
				idStore.add(arg0.getNewRowId());
				System.err.println("UPDATE STUDENT CLASS ROOM COMPLET:" + idStore.size() + ",id:" + arg0.getNewRowId().toString());
			}
		});
	}
	
	/*สร้าง Layout ของข้อมูลเพื่อนำไปใส่ในตาราง*/
	private void setTableData(){
		/* ดึงจำนวนนักเรียนที่ไม่ยืนยันตัว เพื่อหาจำนวนผู้สมัครทั้งหมด */
		rsContainer.addContainerFilter(new And(
				new Equal(RecruitStudentSchema.SCHOOL_ID, UI.getCurrent().getSession().getAttribute(SessionSchema.SCHOOL_ID)),
				new Equal(RecruitStudentSchema.IS_CONFIRM, false)));
		unconfirmQty = rsContainer.size();
		//ลบ WHERE ออกจาก Query เพื่อป้องกันการค้างของคำสั่่งจากการทำงานอื่นที่เรียกตัวแปรไปใช้
		rsContainer.removeAllContainerFilters();
		
		/* ดึงจำนวนนักเรียนที่ยืนยันตัว */ 
		rsContainer.addContainerFilter(new And(
				new Equal(SchoolSchema.SCHOOL_ID,UI.getCurrent().getSession().getAttribute(SessionSchema.SCHOOL_ID)),
				new Equal(RecruitStudentSchema.IS_CONFIRM, true)));
		confirmQty = rsContainer.size();
		
		for(final Object itemId:rsContainer.getItemIds()){			
			final Item studentItem = rsContainer.getItem(itemId);
			addDataItem(studentItem, itemId);
		}
		/* ลบ WHERE ออกจาก Query เพื่อป้องกันการค้างของคำสั่่งจากการทำงานอื่นที่เรียกตัวแปรไปใช้ */
		rsContainer.removeAllContainerFilters();
	}
	
	/*นำ Layout มาใส่ในแต่ละแถวของตาราง*/
	private void addDataItem(Item item,Object itemId){
		table.addItem(new Object[] {
				item.getItemProperty(RecruitStudentSchema.RECRUIT_CODE).getValue(), 
				ClassRange.getNameTh(Integer.parseInt(item.getItemProperty(RecruitStudentSchema.CLASS_RANGE).getValue().toString())),
				Prename.getNameTh(Integer.parseInt(item.getItemProperty(RecruitStudentSchema.PRENAME).getValue().toString())), 
				item.getItemProperty(RecruitStudentSchema.FIRSTNAME).getValue(), 
				item.getItemProperty(RecruitStudentSchema.LASTNAME).getValue()
		},itemId);
	}
	/*นำจำนวนที่นับ มาใส่ค่าในส่วนท้ายตาราง*/
	private void setFooterData(){
		table.setColumnFooter(RecruitStudentSchema.RECRUIT_CODE, "ทั้งหมด: "+ (confirmQty+unconfirmQty) + " คน</br>"
				+ "จำนวนยืนยันตัว: " + confirmQty + " คน</br>"
				+ "จำนวนไม่่ยืนยันตัว: " + unconfirmQty +" คน");
	}
	
	/* ย้ายข้อมูลการสมัคร ไปยัง ข้อมูลจริง */
	private void transferRecruitToStudent(){
		/* ดึงนักเรียนทั้งหมด ที่มีการยืนยันตัว โดยเรียงตาม ช่วงชั้นและชื่อ */	
		rsContainer.addContainerFilter(new And(
				new Equal(RecruitStudentSchema.SCHOOL_ID, UI.getCurrent().getSession().getAttribute(SessionSchema.SCHOOL_ID)),
				new Equal(RecruitStudentSchema.IS_CONFIRM, true),
				new Equal(RecruitStudentSchema.IS_GENERATE_STUDENT_CODE, false)));
		rsContainer.addOrderBy(new OrderBy(RecruitStudentSchema.CLASS_RANGE, true));
		rsContainer.addOrderBy(new OrderBy(RecruitStudentSchema.FIRSTNAME, true));
		  
		if(rsContainer.size() > 0){
			for (Object itemId:rsContainer.getItemIds()) {
			    Item recruitStudentItem = rsContainer.getItem(itemId);
			    System.err.println(recruitStudentItem.getItemProperty(RecruitStudentSchema.CLASS_RANGE).getValue().toString() +
					   recruitStudentItem.getItemProperty(RecruitStudentSchema.FIRSTNAME).getValue().toString() +
					   recruitStudentItem.getItemProperty(RecruitStudentSchema.LASTNAME).getValue().toString());
			    
			    /*  ดึงข้อมูลบิด า*/ 
			    Item recruitFatherItem = rsFamilyContainer.getItem(new RowId(recruitStudentItem.getItemProperty(RecruitStudentSchema.FATHER_ID).getValue()));
			    manageFamilyData(recruitFatherItem);
			    
			    /* ดึงข้อมูลมารดา*/ 
			    Item recruitMotherItem = rsFamilyContainer.getItem(new RowId(recruitStudentItem.getItemProperty(RecruitStudentSchema.MOTHER_ID).getValue()));
			    manageFamilyData(recruitMotherItem);
			    
			    /* ดึงข้อมูลผู้ปกครอง*/ 
			    Item recruitGuradianItem = rsFamilyContainer.getItem(new RowId(recruitStudentItem.getItemProperty(RecruitStudentSchema.GUARDIAN_ID).getValue()));
			    manageFamilyData(recruitGuradianItem);

			    /* ดึงข้อมูลนักเรียน */
			    manageStudentData(recruitStudentItem);		   

		    	/* เพิ่มข้อมูลการเรียนนักเรียน */
		    	newStudentStudy(recruitStudentItem);
		    	
		    	/* เพิ่มข้อมูลห้องเรียนนักเรียน */
		    	newStudentClassRoom(recruitStudentItem);
		    	
		    	/* เสร็จสิ้นกระบวนการ */
		    	setGenerateStudentCodeComplete(recruitStudentItem);
		    }		

			/* ลบ WHERE ออกจาก Query เพื่อป้องกันการค้างของคำสั่่งจากการทำงานอื่นที่เรียกตัวแปรไปใช้ */
			rsContainer.removeAllContainerFilters();	
		}else{
			Notification.show("รหัสนักเรียนทั้งหมดถูกกำหนดแล้ว", Type.WARNING_MESSAGE);
		}
	}

	/* จัดการข้อมูลบิดา มารดา ผู้ปกครอง ว่าต้องมีการเพิ่มใหม่ หรือ นำข้อมูลเดิมมาใช้ */
	private void manageFamilyData(Item oldFamilyItem){
		/* ดึงข้อมูลบิดา มารดา ผู้ปกครอง  จากฐานข้อมูล */
		String peopleId = oldFamilyItem.getItemProperty(RecruitStudentFamilySchema.PEOPLE_ID).getValue().toString();
		Item familyItem = getFamilyFromPeopleId(peopleId);
		
		/* ตรวจสอบข้อมูลบิดา มารดา ผู้ปกครอง ว่ามีอยู่ในระบบหรือยัง
	     *  กรณีไม่มีก็ให้ทำการเพิมข้อมูลใหม่
	     *  กรณีมี ก็เก็บ id ลงใน ArrayList */
	    if(familyItem == null){
	    	Object familyIdTemp = familyContainer.addItem();
	    	familyItem = familyContainer.getItem(familyIdTemp);
	    	
	    	newFamily(oldFamilyItem,familyItem);
	    }else{
	    	System.err.println("HAS FAMILY PEOPLE ID:"+familyItem.getItemProperty(FamilySchema.FAMILY_ID).getValue() );
	    	idStore.add(familyItem.getItemProperty(FamilySchema.FAMILY_ID).getValue());
	    }
	}
	
	/* จัดการข้อมูลนักเรียน ว่าต้องมีการเพิ่มใหม่ หรือ นำข้อมูลเดิมมาใช้ */
	private void manageStudentData(Item oldStudentItem){
		/* ดึงข้อมูลนักเรียน จากฐานข้อมูล */
		String peopleId = oldStudentItem.getItemProperty(RecruitStudentSchema.PEOPLE_ID).getValue().toString();
		Item studentItem = getStudentFromPeopleId(peopleId);
		
		 /* ตรวจสอบข้อมูลนักเรียนว่ามีอยู่ในระบบหรือยัง
	     *  กรณีไม่มีก็ให้ทำการเพิมข้อมูลใหม่
	     *  กรณีมี ก็เก็บ id ลงใน ArrayList */
	    if(studentItem == null){
	    	Object studentIdTemp = studentContainer.addItem();
	    	studentItem = studentContainer.getItem(studentIdTemp);

	    	newStudent(oldStudentItem, studentItem);
	    }else{
	    	System.err.println("HAS STUDENT PEOPLE ID:"+studentItem.getItemProperty(StudentSchema.STUDENT_ID).getValue() );
	    	idStore.add(studentItem.getItemProperty(StudentSchema.STUDENT_ID).getValue());
	    }	    
	}
		
	/* ดึงข้อมูล บิดา มารดา ผู้ปกครอง เดิม ใช้ในกรณีที่ มีนักเรียนที่ใส่่ข้อมูลภายในระบบแล้ว มากำหนดให้เป็นของนักเรียนใหม่ */
	private Item getFamilyFromPeopleId(String peopleId){
		Item familyItem = null;
		
		familyContainer.addContainerFilter(new Equal(FamilySchema.PEOPLE_ID, peopleId));
		if(familyContainer.size() > 0){
			familyItem = familyContainer.getItem(familyContainer.getIdByIndex(0));
		}
		/* ลบ WHERE ออกจาก Query เพื่อป้องกันการค้างของคำสั่่งจากการทำงานอื่นที่เรียกตัวแปรไปใช้ */
		familyContainer.removeAllContainerFilters();
		return familyItem;
	}
	
	/* ดึงข้อมูลนักเรียนเดิม ใช้ในกรณีที่ นักเรียน ม.1 ขึ้น ม.4 โรงเรียนเดิมก็แค่ update ข้อมูลล่าสุด */
	private Item getStudentFromPeopleId(String peopleId){
		Item studentItem = null;
		
		studentContainer.addContainerFilter(new And(
				new Equal(StudentSchema.SCHOOL_ID, UI.getCurrent().getSession().getAttribute(SessionSchema.SCHOOL_ID)),
				new Equal(StudentSchema.PEOPLE_ID, peopleId)));
		if(studentContainer.size() > 0){
			studentItem = studentContainer.getItem(studentContainer.getIdByIndex(0));
		}
		/* ลบ WHERE ออกจาก Query เพื่อป้องกันการค้างของคำสั่่งจากการทำงานอื่นที่เรียกตัวแปรไปใช้ */
		studentContainer.removeAllContainerFilters();
		return studentItem;
	}
	
	/* บันทึกข้อมูลใหม่ ของผู้ปกครอง */
	@SuppressWarnings("unchecked")
	private void newFamily(Item oldItem,Item newItem){
		try {
			for(Object propertyId:oldItem.getItemPropertyIds()){
				Object value = oldItem.getItemProperty(propertyId).getValue();
				/* ตรวจสอบ ชื่อ Column ที่ชื่อตรงกันของ ตาราง RecruitStudentFamily และ Family */
				if(newItem.getItemPropertyIds().contains(propertyId)){
					if(!propertyId.toString().equals(RecruitStudentFamilySchema.REG_FAMILY_ID)){
						newItem.getItemProperty(propertyId).setValue(value);
					}
				}
			}
			familyContainer.commit();
		}catch (Exception e) {
			Notification.show("บันทึกข้อมูลผู้ปกครองไม่สำเร็จ", Type.WARNING_MESSAGE);
			e.printStackTrace();
		}
	}
	
	/* บันทึกข้อมูลใหม่ ของนักเรียน */
	@SuppressWarnings("unchecked")
	private void newStudent(Item oldItem, Item newItem){
		try {
			/* กำหนดค่าให้ข้อมูลนักเรียน โดยตรวจสอบชื่อ Column ที่เหมือนกัน ระหว่าง ข้อมูลการสมัคร(RecruitStudent) และ ข้อมูลนักเรียน (Student)*/
		    for(Object propertyId:oldItem.getItemPropertyIds()){
				Object value = oldItem.getItemProperty(propertyId).getValue();
				if(newItem.getItemPropertyIds().contains(propertyId)){
					if(!propertyId.toString().equals(RecruitStudentSchema.STUDENT_ID)){
						/* หากข้อมูลบิดา มารดา มีอยู่ในระบบแล้ว จะนำข้อมูลเดิมมาใส่ */
		    			if(propertyId.equals(StudentSchema.FATHER_ID)){
		    				value = Integer.parseInt(idStore.get(0).toString());
		    			}else if(propertyId.equals(StudentSchema.MOTHER_ID)){
		    				value = Integer.parseInt(idStore.get(1).toString());
		    			}
		    			System.err.println("NEW STUDENT:" + propertyId + "," + value + ",");
		    			//System.err.println(value.getClass());
						newItem.getItemProperty(propertyId).setValue(value);
					}
				}
				
			}
		    System.err.println("STUDENT");
		    /* กำหนด ผู้ใส่ หรือ แก้ไขข้อมูล*/
		    CreateModifiedSchema.setCreateAndModified(newItem);
		    
		    studentContainer.commit();
		}catch (Exception e) {
			Notification.show("บันทึกข้อมูลนักเรียนไม่สำเร็จ", Type.WARNING_MESSAGE);
			e.printStackTrace();
		}
	}
	
	/* บันทึกข้อมูลใหม่ ของข้อมูลการเรียนนักเรียน */
	@SuppressWarnings({ "unchecked", "deprecation" })
	private void newStudentStudy(Item oldItem){
		
    	try {
    		Object studentStudyIdTemp = studentStudyContainer.addItem();
        	Item newItem = studentStudyContainer.getItem(studentStudyIdTemp);
        	
			/* กำหนดค่าให้ข้อมูลนักเรียน โดยตรวจสอบชื่อ Column ที่เหมือนกัน ระหว่าง ข้อมูลการสมัคร(RecruitStudent) และ ข้อมูลนักเรียน (Student)*/
		    for(Object propertyId:oldItem.getItemPropertyIds()){
				Object value = oldItem.getItemProperty(propertyId).getValue();
				if(newItem.getItemPropertyIds().contains(propertyId)){
					if(!propertyId.toString().equals(StudentStudySchema.STUDENT_STUDY_ID)){
						/* หากข้อมูลบิดา มารดา มีอยู่ในระบบแล้ว จะนำข้อมูลเดิมมาใส่ */
		    			if(propertyId.equals(StudentStudySchema.GUARDIAN_ID)){
		    				value = Integer.parseInt(idStore.get(2).toString());
		    			}

		    			System.err.println("NEW STUDY STUDENT:" + propertyId + "," + value + ",");

						newItem.getItemProperty(propertyId).setValue(value);
					}
				}
			}
		    
		    int classRange = Integer.parseInt(oldItem.getItemProperty(RecruitStudentSchema.CLASS_RANGE).getValue().toString());

		    /* ตรวจสอบ ช่วงชั้นที่สมัคร
			*   กรณี เป็น 0 แทนด้วย "อนุบาล"
			*   กรณี เป็น 1 แทนด้วย "ประภม"
			*   กรณี เป็น 2 แทนด้วย ม.ต้น
			*   กรณี เป็น 3 แทนด้วย ม ปลาย
			*  */
		   int classYear = 0;
		   if(classRange == 0){
			   classYear = 0;
		   }else if(classRange == 1){
			   classYear = 3;
		   }else if(classRange == 2){
			   classYear = 9;
		   }else if(classRange == 3){
			   classYear = 12;
		   }
		   
		    String studentCode = getStudentCode(classYear);
		    
		    newItem.getItemProperty(StudentStudySchema.STUDENT_ID).setValue(Integer.parseInt(idStore.get(3).toString()));
		    newItem.getItemProperty(StudentStudySchema.STUDENT_STATUS).setValue(0);
		    newItem.getItemProperty(StudentStudySchema.STUDENT_CODE).setValue(studentCode);
		    newItem.getItemProperty(StudentStudySchema.RECRUIT_BY_ID).setValue(UI.getCurrent().getSession().getAttribute(SessionSchema.USER_ID));
		    newItem.getItemProperty(StudentStudySchema.RECRUIT_DATE).setValue(new Date());
		    newItem.getItemProperty(StudentStudySchema.RECRUIT_TYPE).setValue(2);
		    newItem.getItemProperty(StudentStudySchema.RECRUIT_CLASS_YEAR).setValue(classYear);
		    newItem.getItemProperty(StudentStudySchema.RECRUIT_YEAR).setValue(1900 + new Date().getYear());
		    newItem.getItemProperty(StudentStudySchema.RECRUIT_SEMESTER).setValue(0);
			newItem.getItemProperty(StudentStudySchema.GRADUATED_CLASS_RANGE).setValue(classRange-1);
		    
		    /* กำหนด ผู้ใส่ หรือ แก้ไขข้อมูล*/
		    CreateModifiedSchema.setCreateAndModified(newItem);

		    studentStudyContainer.commit();
		    
		    Notification.show("บันทึกสำเร็จ", Type.HUMANIZED_MESSAGE);
		}catch (Exception e) {
			Notification.show("บันทึกข้อมูลการเรียนไม่สำเร็จ", Type.WARNING_MESSAGE);
			e.printStackTrace();
		}
	}
	
	/* บันทึกข้อมูลใหม่ ของข้อมูลห้องเรียนนักเรียน */
	@SuppressWarnings({ "unchecked", "deprecation" })
	private void newStudentClassRoom(Item oldItem){
		try {
    		Object studentStudyIdTemp = studentClassRoomContainer.addItem();
        	Item newItem = studentClassRoomContainer.getItem(studentStudyIdTemp);
        	
        	/* กำหนดค่าให้ข้อมูลนักเรียน โดยตรวจสอบชื่อ Column ที่เหมือนกัน ระหว่าง ข้อมูลการสมัคร(RecruitStudent) และ ข้อมูลนักเรียน (Student)*/
		    for(Object propertyId:oldItem.getItemPropertyIds()){
				Object value = oldItem.getItemProperty(propertyId).getValue();
				if(newItem.getItemPropertyIds().contains(propertyId)){
					if(!propertyId.toString().equals(StudentClassRoomSchema.STUDENT_CLASS_ROOM_ID)){
						
		    			System.err.println("NEW STUDENT CLASS ROOM:" + propertyId + "," + value + ",");

						newItem.getItemProperty(propertyId).setValue(value);
					}
				}
			}
		    
		    newItem.getItemProperty(StudentClassRoomSchema.STUDENT_STUDY_ID).setValue(Integer.parseInt(idStore.get(4).toString()));
		    newItem.getItemProperty(StudentClassRoomSchema.ACADEMIC_YEAR).setValue(Integer.toString(1900 + new Date().getYear()));
		    
		    /* กำหนด ผู้ใส่ หรือ แก้ไขข้อมูล*/
		    CreateModifiedSchema.setCreateAndModified(newItem);
		    
		    studentClassRoomContainer.commit();
		    idStore.clear();
		    System.err.println("CLEAR:" + idStore.size());
		}catch (Exception e) {
			Notification.show("บันทึกห้องเรียนไม่สำเร็จ", Type.WARNING_MESSAGE);
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	private void setGenerateStudentCodeComplete(Item oldItem){
		try{
			oldItem.getItemProperty(RecruitStudentSchema.IS_GENERATE_STUDENT_CODE).setValue(true);
			rsContainer.commit();
		}catch (Exception e) {
			Notification.show("บันทึกข้อมูลการเรียนไม่สำเร็จ", Type.WARNING_MESSAGE);
			e.printStackTrace();
		}
	}
	
	/* สร้างรหัส จากข้อมูลนักเรียนที่ยืนยัน มอบตัว */
	@SuppressWarnings("deprecation")
	private String getStudentCode(int classYear){
		String studentCode = "";

	   SQLContainer freeFormContainer = null;
	   int maxCode = 0;
	   
	   /* ตรวจสอบประเภทการกำหนดรหัสนักเรียน
	    *   กรณีการตั้งค่าเป็นแบบอัตโนมัติ
	    *   กรณีตั้งค่าแบบกำหนดเอง*/
	   if(generateCodeType == 0){
		   /* ค้นรหัสนักเรียนที่มากสุดของแต่ละชั้นปี เพื่อทำการบวกรหัสนักเรียน */
		   freeFormContainer = Container.getInstance().getFreeFormContainer(StatStudentCodeSchema.getQuery(classYear), StatStudentCodeSchema.MAX_STUDENT_CODE);
		   
		   maxCode = new Date().getYear()+2443;
		   studentCode = Integer.toString(maxCode).substring(2)+maxCode+"001";
		   System.err.println("AUTO:" + studentCode);
	   }else if(generateCodeType == 1){
		   /* ค้นรหัสนักเรียนที่มากสุด เพื่อบวกค่าเรื่อย ๆ เพื่อทำการบวกรหัสนักเรียน */
		   freeFormContainer = Container.getInstance().getFreeFormContainer(StatStudentCodeSchema.getQuery(), StatStudentCodeSchema.MAX_STUDENT_CODE);
		   
		   Item schoolItem = schoolContainer.getItem(new RowId(UI.getCurrent().getSession().getAttribute(SessionSchema.SCHOOL_ID)));
		   /* ตรวจสอบ รหัสนักเรียนที่มากสุดของแต่ละชั้นปีว่ามีการกำหนดหรือยัง
		    *  กรณี มีการกำหนดแล้วก็จะบวกรหัสที่มากสุดณปัจจุุบัน
		    *  กรณี ไม่มีการกำหนดจะทำการกำหนดค่าเอง  */
		   if(freeFormContainer.size() > 0){				
				for(Object object:freeFormContainer.getItemIds()){
					if(Utility.isInteger(object)){
						maxCode = Integer.parseInt(object.toString())+1;
						studentCode = Integer.toString(maxCode);
					}else{
						studentCode = schoolItem.getItemProperty(SchoolSchema.STUDENT_CODE_FIRST).getValue().toString();
					}
				}
				System.err.println("HAS:" + studentCode);
			}else{
				studentCode = schoolItem.getItemProperty(SchoolSchema.STUDENT_CODE_FIRST).getValue().toString();
			}
		   
		   
		   System.err.println("MANAUL:" + studentCode);
	   }
	   
	   
	   //ลบ WHERE ออกจาก Query เพื่อป้องกันการค้างของคำสั่่งจากการทำงานอื่นที่เรียกตัวแปรไปใช้
	   freeFormContainer.removeAllContainerFilters();
	   
	   return studentCode;
	}
}