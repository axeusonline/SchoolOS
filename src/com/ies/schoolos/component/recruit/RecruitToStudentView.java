package com.ies.schoolos.component.recruit;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.tepi.filtertable.FilterTable;
import org.vaadin.dialogs.ConfirmDialog;

import com.ies.schoolos.container.Container;
import com.ies.schoolos.filter.TableFilterDecorator;
import com.ies.schoolos.filter.TableFilterGenerator;
import com.ies.schoolos.report.excel.StudentToExcel;
import com.ies.schoolos.schema.CreateModifiedSchema;
import com.ies.schoolos.schema.SchoolSchema;
import com.ies.schoolos.schema.SessionSchema;
import com.ies.schoolos.schema.info.FamilySchema;
import com.ies.schoolos.schema.info.StudentClassRoomSchema;
import com.ies.schoolos.schema.info.StudentSchema;
import com.ies.schoolos.schema.info.StudentStudySchema;
import com.ies.schoolos.schema.recruit.RecruitStudentFamilySchema;
import com.ies.schoolos.schema.recruit.RecruitStudentSchema;
import com.ies.schoolos.schema.view.StatStudentCodeSchema;
import com.ies.schoolos.type.ClassRange;
import com.ies.schoolos.type.Gender;
import com.ies.schoolos.type.Prename;
import com.ies.schoolos.utility.DateTimeUtil;
import com.ies.schoolos.utility.Utility;
import com.vaadin.addon.tableexport.ExcelExport;
import com.vaadin.data.Item;
import com.vaadin.data.util.filter.And;
import com.vaadin.data.util.filter.Compare.Equal;
import com.vaadin.data.util.filter.Compare.Greater;
import com.vaadin.data.util.filter.Compare.Less;
import com.vaadin.data.util.sqlcontainer.RowId;
import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.data.util.sqlcontainer.query.OrderBy;
import com.vaadin.data.util.sqlcontainer.query.QueryDelegate.RowIdChangeEvent;
import com.vaadin.data.util.sqlcontainer.query.QueryDelegate.RowIdChangeListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Table;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CustomTable.Align;
import com.vaadin.ui.Notification.Type;

public class RecruitToStudentView extends VerticalLayout{
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
	private ArrayList<Object> idStore = new ArrayList<Object>();
	private HashMap<Object, HashMap<Object, Object>> summarizes = new HashMap<Object, HashMap<Object, Object>>();
	
	private SQLContainer rsContainer = Container.getRecruitStudentContainer();
	private SQLContainer rsFamilyContainer = Container.getRecruitFamilyContainer();
	private SQLContainer schoolContainer = Container.getSchoolContainer();
	private SQLContainer studentContainer = Container.getStudentContainer();
	private SQLContainer studentStudyContainer = Container.getStudentStudyContainer();
	private SQLContainer familyContainer = Container.getFamilyContainer();
	private SQLContainer studentClassRoomContainer = Container.getStudentClassRoomContainer();
	
	private HorizontalLayout toolbar;
	private Button confirm;	
	private FilterTable  table;
	private Label summarize;
	
	public RecruitToStudentView() {
		setSizeFull();
		setSpacing(true);
		setMargin(true);
		buildMainLayout();
		initSqlContainerRowIdChange();
		setSummarize();
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
				Item schoolItem = schoolContainer.getItem(new RowId(SessionSchema.getSchoolID()));
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
		
		Button excelExport = new Button("ส่งออกไฟล์ Excel", FontAwesome.FILE_EXCEL_O);
		excelExport.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event) {
				final Table tableEx = new StudentToExcel();
				tableEx.setVisible(false);
				addComponent(tableEx);
				
				ExcelExport excelExport = new ExcelExport(tableEx,"student");
                excelExport.excludeCollapsedColumns();
                excelExport.setReportTitle("Student");
				excelExport.setExportFileName("Student.xls");
                excelExport.export();
                
                removeComponent(tableEx);
			}
		});
		toolbar.addComponent(excelExport);
		
		
		/* Content */
		HorizontalLayout studentsLayout = new HorizontalLayout();
		studentsLayout.setSizeFull();
		addComponent(studentsLayout);
        setExpandRatio(studentsLayout, 1);
        
		table = new FilterTable();
		table.setWidth("60%");
		table.setHeight("100%");
		table.setSelectable(true);
		table.setFooterVisible(true);        
		studentsLayout.addComponent(table);
		studentsLayout.setExpandRatio(table, 3);
		
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

		setTableData();
		
		summarize = new Label();
        summarize.setWidth("100%");
        summarize.setContentMode(ContentMode.HTML);
        studentsLayout.addComponent(summarize);
		studentsLayout.setExpandRatio(summarize, 1);

	}
	
	/* กำหนดค่า PK Auto Increment หลังการบันทึก */
	private void initSqlContainerRowIdChange(){
		/* นักเรียน */
		studentContainer.addRowIdChangeListener(new RowIdChangeListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void rowIdChange(RowIdChangeEvent arg0) {
				idStore.add(arg0.getNewRowId());
			}
		});
		
		/* ข้อมูลการเรียนนักเรียน */
		studentStudyContainer.addRowIdChangeListener(new RowIdChangeListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void rowIdChange(RowIdChangeEvent arg0) {
				idStore.add(arg0.getNewRowId());
			}
		});
		
		/* บิดา แม่ ผู้ปกครอง */
		familyContainer.addRowIdChangeListener(new RowIdChangeListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void rowIdChange(RowIdChangeEvent arg0) {
				idStore.add(arg0.getNewRowId());
			}
		});
		
		studentClassRoomContainer.addRowIdChangeListener(new RowIdChangeListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void rowIdChange(RowIdChangeEvent arg0) {
				idStore.add(arg0.getNewRowId());
			}
		});
	}
	
	/*สร้าง Layout ของข้อมูลเพื่อนำไปใส่ในตาราง*/
	private void setTableData(){
		/* ดึงจำนวนนักเรียนที่ไม่ยืนยันตัว เพื่อหาจำนวนผู้สมัครทั้งหมด */
		rsContainer.addContainerFilter(new And(
				new Equal(RecruitStudentSchema.SCHOOL_ID, SessionSchema.getSchoolID()),
				new Greater(RecruitStudentSchema.REGISTER_DATE,DateTimeUtil.getFirstDateOfYear()),
				new Less(RecruitStudentSchema.REGISTER_DATE,DateTimeUtil.getLastDateOfYear()),
				new Equal(RecruitStudentSchema.IS_CONFIRM, false)));
		unconfirmQty = rsContainer.size();
		//ลบ WHERE ออกจาก Query เพื่อป้องกันการค้างของคำสั่่งจากการทำงานอื่นที่เรียกตัวแปรไปใช้
		rsContainer.removeAllContainerFilters();
		
		/* ดึงจำนวนนักเรียนที่ยืนยันตัว */ 
		rsContainer.addContainerFilter(new And(
				new Equal(SchoolSchema.SCHOOL_ID,SessionSchema.getSchoolID()),
				new Greater(RecruitStudentSchema.REGISTER_DATE,DateTimeUtil.getFirstDateOfYear()),
				new Less(RecruitStudentSchema.REGISTER_DATE,DateTimeUtil.getLastDateOfYear()),
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
	
	/* ย้ายข้อมูลการสมัคร ไปยัง ข้อมูลจริง */
	private void transferRecruitToStudent(){
		/* ดึงนักเรียนทั้งหมด ที่มีการยืนยันตัว โดยเรียงตาม ช่วงชั้นและชื่อ */	
		rsContainer.addContainerFilter(new And(
				new Equal(RecruitStudentSchema.SCHOOL_ID, SessionSchema.getSchoolID()),
				new Equal(RecruitStudentSchema.IS_CONFIRM, true),
				new Equal(RecruitStudentSchema.IS_GENERATE_STUDENT_CODE, false)));
		rsContainer.addOrderBy(new OrderBy(RecruitStudentSchema.CLASS_RANGE, true));
		rsContainer.addOrderBy(new OrderBy(RecruitStudentSchema.FIRSTNAME, true));
		  
		if(rsContainer.size() > 0){
			for (Object itemId:rsContainer.getItemIds()) {
			    Item recruitStudentItem = rsContainer.getItem(itemId);
			   
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
				new Equal(StudentSchema.SCHOOL_ID, SessionSchema.getSchoolID()),
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
						newItem.getItemProperty(propertyId).setValue(value);
					}
				}
				
			}
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
		    newItem.getItemProperty(StudentStudySchema.RECRUIT_BY_ID).setValue(SessionSchema.getUserID());
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
	@SuppressWarnings({ "unchecked" })
	private void newStudentClassRoom(Item oldItem){
		try {
    		Object studentStudyIdTemp = studentClassRoomContainer.addItem();
        	Item newItem = studentClassRoomContainer.getItem(studentStudyIdTemp);
        	
        	/* กำหนดค่าให้ข้อมูลนักเรียน โดยตรวจสอบชื่อ Column ที่เหมือนกัน ระหว่าง ข้อมูลการสมัคร(RecruitStudent) และ ข้อมูลนักเรียน (Student)*/
		    for(Object propertyId:oldItem.getItemPropertyIds()){
				Object value = oldItem.getItemProperty(propertyId).getValue();
				if(newItem.getItemPropertyIds().contains(propertyId)){
					if(!propertyId.toString().equals(StudentClassRoomSchema.STUDENT_CLASS_ROOM_ID)){
						newItem.getItemProperty(propertyId).setValue(value);
					}
				}
			}
		    
		    newItem.getItemProperty(StudentClassRoomSchema.STUDENT_STUDY_ID).setValue(Integer.parseInt(idStore.get(4).toString()));
		    newItem.getItemProperty(StudentClassRoomSchema.ACADEMIC_YEAR).setValue(Integer.parseInt(DateTimeUtil.getBuddishYear()));
		    
		    /* กำหนด ผู้ใส่ หรือ แก้ไขข้อมูล*/
		    CreateModifiedSchema.setCreateAndModified(newItem);
		    
		    studentClassRoomContainer.commit();
		    idStore.clear();
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
		   freeFormContainer = Container.getFreeFormContainer(StatStudentCodeSchema.getQuery(classYear), StatStudentCodeSchema.MAX_STUDENT_CODE);
		   
		   maxCode = new Date().getYear()+2443;
		   studentCode = Integer.toString(maxCode).substring(2)+classYear+"001";
	   }else if(generateCodeType == 1){
		   /* ค้นรหัสนักเรียนที่มากสุด เพื่อบวกค่าเรื่อย ๆ เพื่อทำการบวกรหัสนักเรียน */
		   freeFormContainer = Container.getFreeFormContainer(StatStudentCodeSchema.getQuery(), StatStudentCodeSchema.MAX_STUDENT_CODE);
		   
		   Item schoolItem = schoolContainer.getItem(new RowId(SessionSchema.getSchoolID()));
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
			}else{
				studentCode = schoolItem.getItemProperty(SchoolSchema.STUDENT_CODE_FIRST).getValue().toString();
			}
	   }
	   
	   
	   //ลบ WHERE ออกจาก Query เพื่อป้องกันการค้างของคำสั่่งจากการทำงานอื่นที่เรียกตัวแปรไปใช้
	   freeFormContainer.removeAllContainerFilters();
	   
	   return studentCode;
	}
	
	private void setSummarize(){
		
		summarizes.clear();
		
		/*SELECT gender , COUNT(class_range) AS class_range 
		FROM recruit_student 
		WHERE school_id = 9 GROUP BY class_range,gender ORDER BY class_range ASC;*/
		StringBuilder builder = new StringBuilder();
		builder.append(" SELECT " + RecruitStudentSchema.STUDENT_ID + "," + RecruitStudentSchema.GENDER + "," + RecruitStudentSchema.CLASS_RANGE + ", COUNT("+RecruitStudentSchema.CLASS_RANGE+") AS sum");
		builder.append(" FROM " + RecruitStudentSchema.TABLE_NAME);
		builder.append(" WHERE " + RecruitStudentSchema.SCHOOL_ID + "=" + SessionSchema.getSchoolID());
		builder.append(" AND " + RecruitStudentSchema.IS_CONFIRM + "=" + true);
		builder.append(" GROUP BY " +  RecruitStudentSchema.CLASS_RANGE + "," + RecruitStudentSchema.GENDER);
		builder.append(" ORDER BY " +  RecruitStudentSchema.CLASS_RANGE + " ASC");

		SQLContainer freeCon = Container.getFreeFormContainer(builder.toString(), RecruitStudentSchema.STUDENT_ID);
		
		HashMap<Object, Object> genderMap = null;
		StringBuilder sumStr = new StringBuilder();
		int currentClassRange = -1;
		for (Object itemId:freeCon.getItemIds()) {
			Item item = freeCon.getItem(itemId);
			
			int classRange = Integer.parseInt(item.getItemProperty(RecruitStudentSchema.CLASS_RANGE).getValue().toString());
			int gender = Integer.parseInt(item.getItemProperty(RecruitStudentSchema.GENDER).getValue().toString());
			int sum = Integer.parseInt(item.getItemProperty("sum").getValue().toString());
			
			if(currentClassRange < classRange){
				genderMap = new HashMap<Object, Object>();
				currentClassRange = classRange;
				summarizes.put(classRange, genderMap);
			}

			genderMap.put(gender, sum);
			
		}
		
		for ( Map.Entry<Object, HashMap<Object, Object>> entry : summarizes.entrySet()) {
			String genderStr = "";
			int total = 0;
			int classRange =(int) entry.getKey();
		    HashMap<Object, Object> genders = entry.getValue();
		    
		    for(Map.Entry<Object, Object> genderEntry : genders.entrySet()){
		    	int genderKey =(int) genderEntry.getKey();
		    	int genderSum = (int) genderEntry.getValue();
		    	genderStr += ("<b>"+Gender.getNameTh(genderKey)+"</b> " + genderSum)+" คน<br/>";
		    	total += genderSum;
		    }
		    sumStr.append("<b>" + ClassRange.getNameTh(classRange) +"</b> " + total + " คน </br>"+ genderStr + "</br>");
		}
		sumStr.append("<b>จำนวนยืนยันตัว </b> " + confirmQty + " คน </br>");
		sumStr.append("<b>จำนวนไม่่ยืนยันตัว </b> " + unconfirmQty + " คน </br>");
		
		summarize.setValue(sumStr.toString());
	}
}
