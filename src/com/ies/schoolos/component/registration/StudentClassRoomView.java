package com.ies.schoolos.component.registration;

import java.util.Collection;

import com.ies.schoolos.component.ui.TwinSelectTable;
import com.ies.schoolos.container.Container;
import com.ies.schoolos.filter.TableFilterDecorator;
import com.ies.schoolos.filter.TableFilterGenerator;
import com.ies.schoolos.schema.CreateModifiedSchema;
import com.ies.schoolos.schema.SessionSchema;
import com.ies.schoolos.schema.fundamental.ClassRoomSchema;
import com.ies.schoolos.schema.info.StudentClassRoomSchema;
import com.ies.schoolos.schema.info.StudentSchema;
import com.ies.schoolos.schema.info.StudentStudySchema;
import com.ies.schoolos.type.Prename;
import com.ies.schoolos.type.dynamic.ClassRoom;
import com.ies.schoolos.utility.DateTimeUtil;
import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.sqlcontainer.RowId;
import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Notification.Type;

public class StudentClassRoomView extends VerticalLayout{
	private static final long serialVersionUID = 1L;

	private int capacity = 0;

	private Container container = new Container();
	private SQLContainer freeContainer;
	private SQLContainer studentClassRoomContainer = container.getStudentClassRoomContainer();
	private SQLContainer classtudentClassRoomContainer = container.getClassRoomContainer();
	
	private ComboBox classRoom;
	private TextField academicYear;
	private Label capacityLabel;
	
	private TwinSelectTable twinSelect; 
	
	public StudentClassRoomView() {
		setSizeFull();
		setSpacing(true);
		setMargin(true);
		buildMainLayout();
		fetchLeftData();
	}
	
	private void buildMainLayout(){
		setSpacing(true);
		
		/* ==== Layout ส่วนของห้องเรียน ==== */
		HorizontalLayout toolStrip = new HorizontalLayout();
		toolStrip.setWidth("100%");
		toolStrip.setHeight("90px");
		toolStrip.setStyleName("border-white");
		addComponent(toolStrip);
		
		classRoom = new ComboBox("ชั้นเรียน",new ClassRoom());
		classRoom.setInputPrompt("กรุณาเลือก");
		classRoom.setItemCaptionPropertyId("name");
		classRoom.setImmediate(true);
        classRoom.setNullSelectionAllowed(false);
        classRoom.setRequired(true);
		classRoom.setWidth("-1px");
		classRoom.setHeight("-1px");
		classRoom.setFilteringMode(FilteringMode.CONTAINS);
		classRoom.addValueChangeListener(new ValueChangeListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				if(event.getProperty().getValue() != null){
				
					/* ======== ดึงจำนวนคนที่ห้องรองรับได้ ========== */
					Item item = classtudentClassRoomContainer.getItem(new RowId(event.getProperty().getValue()));
					capacity = Integer.parseInt(item.getItemProperty(ClassRoomSchema.CAPACITY).getValue().toString());
					
					capacityLabel.setValue("ความจุนักเรียน " + capacity + " คน");
					capacityLabel.setVisible(true);
					
					fetchRightData();
				}
			}
		});
		toolStrip.addComponent(classRoom);
		toolStrip.setComponentAlignment(classRoom, Alignment.MIDDLE_LEFT);
		
		academicYear = new TextField("ปีการศึกษา");
		academicYear.setInputPrompt("ปีการศึกษา");
		academicYear.setImmediate(true);
		academicYear.setRequired(true);
		academicYear.setWidth("-1px");
		academicYear.setHeight("-1px");
		academicYear.setValue(DateTimeUtil.getBuddishYear());
		academicYear.setReadOnly(true);
		toolStrip.addComponent(academicYear);
		toolStrip.setComponentAlignment(academicYear, Alignment.MIDDLE_LEFT);
		
		capacityLabel = new Label();
		capacityLabel.setWidth("200px");
		capacityLabel.setHeight("50px");
		capacityLabel.setStyleName("label-green");
		capacityLabel.setVisible(false);
		toolStrip.addComponent(capacityLabel);
		toolStrip.setComponentAlignment(capacityLabel, Alignment.MIDDLE_LEFT);
		
		/* ตารางรายการนักเรียน */
		twinSelect = new TwinSelectTable();
		twinSelect.setSizeFull();
		twinSelect.setSpacing(true);
		twinSelect.setSelectable(true);
		twinSelect.setMultiSelect(true);
		twinSelect.showFooterCount(true);
		twinSelect.setFooterUnit("คน");
	
		twinSelect.addContainerProperty(StudentStudySchema.STUDENT_CODE, String.class, null);
		twinSelect.addContainerProperty(StudentSchema.PRENAME, String.class, null);
		twinSelect.addContainerProperty(StudentSchema.FIRSTNAME, String.class, null);
		twinSelect.addContainerProperty(StudentSchema.LASTNAME, String.class, null);
		
		twinSelect.setFilterDecorator(new TableFilterDecorator());
		twinSelect.setFilterGenerator(new TableFilterGenerator());
		twinSelect.setFilterBarVisible(true);
        
		initTableStyle();
		
		twinSelect.setAddClick(addListener);
		twinSelect.setAddAllClick(addAllListener);
		twinSelect.setRemoveClick(removeListener);
		twinSelect.setRemoveAllClick(removeAllListener);
		
		addComponent(twinSelect);
		setExpandRatio(twinSelect, 1);
	}
	
	/* ตั้งค่ารูปแบบแสดงของตาราง */
	private void initTableStyle(){				
		twinSelect.setColumnHeader(StudentStudySchema.STUDENT_CODE, "หมายเลขประจำตัว");
		twinSelect.setColumnHeader(StudentSchema.PRENAME, "ชื่อต้น");
		twinSelect.setColumnHeader(StudentSchema.FIRSTNAME, "ชื่อ");
		twinSelect.setColumnHeader(StudentSchema.LASTNAME, "สกุล");
		
		twinSelect.setVisibleColumns(
				StudentStudySchema.STUDENT_CODE, 
				StudentSchema.PRENAME,
				StudentSchema.FIRSTNAME, 
				StudentSchema.LASTNAME);
	}
	
	private void fetchLeftData(){	
		twinSelect.removeAllLeftItem();
		
		StringBuilder builder = new StringBuilder();
		builder.append(" SELECT * FROM " + StudentStudySchema.TABLE_NAME + " ss");
		builder.append(" INNER JOIN " + StudentSchema.TABLE_NAME + " s ON s." + StudentSchema.STUDENT_ID + "= ss." + StudentStudySchema.STUDENT_ID);
		builder.append(" WHERE ss." + StudentSchema.SCHOOL_ID + "=" + SessionSchema.getSchoolID());
		builder.append(" AND ss." + StudentStudySchema.STUDENT_STUDY_ID + " NOT IN (");
		builder.append(" SELECT " + StudentClassRoomSchema.STUDENT_STUDY_ID + " FROM " + StudentClassRoomSchema.TABLE_NAME);
		builder.append(" WHERE " + StudentClassRoomSchema.ACADEMIC_YEAR + "='" + academicYear.getValue() + "'");
		builder.append(" AND " + StudentClassRoomSchema.SCHOOL_ID + "=" + SessionSchema.getSchoolID() + ")");

		freeContainer = container.getFreeFormContainer(builder.toString(), StudentStudySchema.STUDENT_STUDY_ID);
		for(final Object itemId:freeContainer.getItemIds()){
			Item item = freeContainer.getItem(itemId);
			twinSelect.getLeftTable().addItem(new Object[]{
				item.getItemProperty(StudentStudySchema.STUDENT_CODE).getValue(),
				Prename.getNameTh((int)item.getItemProperty(StudentSchema.PRENAME).getValue()),
				item.getItemProperty(StudentSchema.FIRSTNAME).getValue(),
				item.getItemProperty(StudentSchema.LASTNAME).getValue()
			}, itemId);
		}
		
		Object[] propLeft = {StudentStudySchema.STUDENT_CODE};
		boolean[] boolLeft = {false};
		twinSelect.getLeftTable().sort(propLeft, boolLeft);
		twinSelect.setLeftCountFooter(StudentStudySchema.STUDENT_CODE);
	}
	
	/* จำนวนนักเรียนที่ถูกเลือก */
	private void fetchRightData(){
		twinSelect.removeAllRightItem();
		
		StringBuilder builder = new StringBuilder();
		builder.append(" SELECT * FROM " + StudentClassRoomSchema.TABLE_NAME + " scr");
		builder.append(" INNER JOIN " + StudentStudySchema.TABLE_NAME + " ss ON ss." + StudentStudySchema.STUDENT_STUDY_ID + "= scr." + StudentClassRoomSchema.STUDENT_STUDY_ID);
		builder.append(" INNER JOIN " + StudentSchema.TABLE_NAME + " s ON s." + StudentSchema.STUDENT_ID + "= ss." + StudentStudySchema.STUDENT_ID);
		builder.append(" INNER JOIN " + ClassRoomSchema.TABLE_NAME + " cr ON cr." + ClassRoomSchema.CLASS_ROOM_ID + "= scr." + StudentClassRoomSchema.CLASS_ROOM_ID);
		builder.append(" WHERE scr." + StudentSchema.SCHOOL_ID + "=" + SessionSchema.getSchoolID());
		builder.append(" AND scr." + StudentClassRoomSchema.CLASS_ROOM_ID + "=" + classRoom.getValue());
		builder.append(" AND scr." + StudentClassRoomSchema.ACADEMIC_YEAR + "='" + academicYear.getValue() + "'");

		freeContainer = container.getFreeFormContainer(builder.toString(), StudentClassRoomSchema.STUDENT_CLASS_ROOM_ID);
		for(final Object itemId:freeContainer.getItemIds()){
			Item item = freeContainer.getItem(itemId);
			twinSelect.getRightTable().addItem(new Object[]{
				item.getItemProperty(StudentStudySchema.STUDENT_CODE).getValue(),
				Prename.getNameTh((int)item.getItemProperty(StudentSchema.PRENAME).getValue()),
				item.getItemProperty(StudentSchema.FIRSTNAME).getValue(),
				item.getItemProperty(StudentSchema.LASTNAME).getValue()
			}, itemId);
		}
		
		Object[] propRight = {StudentStudySchema.STUDENT_CODE};
		boolean[] boolRight = {true};
		twinSelect.getRightTable().sort(propRight, boolRight);
		twinSelect.setRightCountFooter(StudentStudySchema.STUDENT_CODE);

	}

	/* ย้ายข้างจากซ้ายไปขวาจากที่ถูกเลือก */
	@SuppressWarnings("unchecked")
	private void selectData(Object... itemIds){
		try {
			for(Object itemId: itemIds){
				/* ก่อนแก้ไข เพิ่มต้องลบ Filter ก่อนหน้า */
				studentClassRoomContainer.removeAllContainerFilters();
				
				Object tempId = studentClassRoomContainer.addItem();
				
				Item studentClassRoomItem = studentClassRoomContainer.getItem(tempId);
				studentClassRoomItem.getItemProperty(StudentClassRoomSchema.SCHOOL_ID).setValue(SessionSchema.getSchoolID());
				studentClassRoomItem.getItemProperty(StudentClassRoomSchema.CLASS_ROOM_ID).setValue(Integer.parseInt(classRoom.getValue().toString()));
				studentClassRoomItem.getItemProperty(StudentClassRoomSchema.STUDENT_STUDY_ID).setValue(Integer.parseInt(itemId.toString()));
				studentClassRoomItem.getItemProperty(StudentClassRoomSchema.ACADEMIC_YEAR).setValue(academicYear.getValue());
				CreateModifiedSchema.setCreateAndModified(studentClassRoomItem);
				studentClassRoomContainer.commit();
			}
			fetchLeftData();
			fetchRightData();
		}catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	/* ย้ายข้างจากซ้ายไปขวาจากทั้งหมด*/
	@SuppressWarnings("unchecked")
	private void selectAllData(){
		try {
			Collection<?> itemIds = twinSelect.getLeftTable().getItemIds();
			for(Object itemId: itemIds){
				/* ก่อนแก้ไข เพิ่มต้องลบ Filter ก่อนหน้า */
				studentClassRoomContainer.removeAllContainerFilters();
				
				Object tempId = studentClassRoomContainer.addItem();
				
				Item studentClassRoomItem = studentClassRoomContainer.getItem(tempId);
				studentClassRoomItem.getItemProperty(StudentClassRoomSchema.SCHOOL_ID).setValue(SessionSchema.getSchoolID());
				studentClassRoomItem.getItemProperty(StudentClassRoomSchema.CLASS_ROOM_ID).setValue(Integer.parseInt(classRoom.getValue().toString()));
				studentClassRoomItem.getItemProperty(StudentClassRoomSchema.STUDENT_STUDY_ID).setValue(Integer.parseInt(itemId.toString()));
				studentClassRoomItem.getItemProperty(StudentClassRoomSchema.ACADEMIC_YEAR).setValue(academicYear.getValue());
				CreateModifiedSchema.setCreateAndModified(studentClassRoomItem);
				studentClassRoomContainer.commit();
			}
			fetchLeftData();
			fetchRightData();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	/* ย้ายข้างจากขวาไปซ้ายจากที่เลือก */
	private void removeData(Object... itemIds){
		try {
			for(Object itemId: itemIds){
				studentClassRoomContainer.removeItem(itemId);
				studentClassRoomContainer.commit();
			}
			fetchLeftData();
			fetchRightData();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/* ย้ายข้างจากขวาไปซ้ายจากจำนวนทั้งหมด */
	private void removeAllData(){
		try {
			for(Object itemId: twinSelect.getRightTable().getItemIds()){
				studentClassRoomContainer.removeItem(itemId);
				studentClassRoomContainer.commit();
			}
			fetchLeftData();
			fetchRightData();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/* ตรวจสอบขนาดชั้นเรียน กับ จำนวนนักเรียนที่เลือก */
	private boolean isFullCapacity(int selectedSize){
		boolean isFull = true;
	
		/* จำนวนนักเรียนที่เลือกอยู่แล้วรวมกับที่เลือกใหม่ */
		int totalSelected = twinSelect.getRightTable().size() + selectedSize;
		if(totalSelected <= capacity)
			isFull = false;
		return isFull;
	}
	
	/* ปุ่มเลือกนักเรียนจากซ้ายไปขวา จากนักเรียนที่เลือก */
	private ClickListener addListener = new ClickListener() {
		private static final long serialVersionUID = 1L;
		@Override
		public void buttonClick(ClickEvent event) {
			/* ตรวจสอบว่ามีการเลือกห้องหรือยัง */
			if(classRoom.getValue() == null){
				Notification.show("กรุณาเลือกห้องเรียน", Type.WARNING_MESSAGE);
				return;
			}
			
			Collection<?> itemIds = (Collection<?>)twinSelect.getLeftTable().getValue();
			
			/* ตรวจสอบว่าชั้นเรียนเต็มหรือไม่ */
			if(isFullCapacity(itemIds.size())){
				Notification.show("ชั้นเรียนเต็ม กรุณาเลือกชั้นเรียนใหม่่", Type.WARNING_MESSAGE);
				return;
			}
			
			for(Object itemId:itemIds){
				selectData(itemId);
			}
		}
	};
	
	/* ปุ่มเลือกนักเรียนจากซ้ายไปขวา จากนักเรียนทั้งหมด */
	private ClickListener addAllListener = new ClickListener() {
		private static final long serialVersionUID = 1L;
		@Override
		public void buttonClick(ClickEvent event) {
			/* ตรวจสอบว่ามีการเลือกห้องหรือยัง */
			if(classRoom.getValue() == null){
				Notification.show("กรุณาเลือกห้องเรียน", Type.WARNING_MESSAGE);
				return;
			}
			
			Collection<?> itemIds = twinSelect.getLeftTable().getItemIds();
			
			/* ตรวจสอบว่าชั้นเรียนเต็มหรือไม่ */
			if(isFullCapacity(itemIds.size())){
				Notification.show("ชั้นเรียนเต็ม กรุณาเลือกชั้นเรียนใหม่่", Type.WARNING_MESSAGE);
				return;
			}
			selectAllData();
		}
	};
	
	/* ปุ่มเลือกนักเรียนจากขวาไปซ้าย จากนักเรียนที่เลือก */
	private ClickListener removeListener = new ClickListener() {
		private static final long serialVersionUID = 1L;
		@Override
		public void buttonClick(ClickEvent event) {
			for(Object itemId:(Collection<?>)twinSelect.getRightTable().getValue()){
				removeData(itemId);
			}
		}
	};
	
	/* ปุ่มเลือกนักเรียนจากขวาไปซ้าย จากนักเรียนทั้งหมด */
	private ClickListener removeAllListener = new ClickListener() {
		private static final long serialVersionUID = 1L;
		@Override
		public void buttonClick(ClickEvent event) {
			removeAllData();
		}
	};
}
