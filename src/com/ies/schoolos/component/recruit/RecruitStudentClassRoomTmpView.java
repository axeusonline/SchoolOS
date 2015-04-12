package com.ies.schoolos.component.recruit;

import java.util.Collection;

import org.tepi.filtertable.numberfilter.NumberInterval;

import com.ies.schoolos.component.ui.ContentPage;
import com.ies.schoolos.component.ui.TwinSelectTable;
import com.ies.schoolos.container.Container;
import com.ies.schoolos.filter.TableFilterDecorator;
import com.ies.schoolos.filter.TableFilterGenerator;
import com.ies.schoolos.schema.SchoolSchema;
import com.ies.schoolos.schema.SessionSchema;
import com.ies.schoolos.schema.fundamental.ClassRoomSchema;
import com.ies.schoolos.schema.recruit.RecruitStudentSchema;
import com.ies.schoolos.type.ClassRange;
import com.ies.schoolos.type.Prename;
import com.ies.schoolos.type.dynamic.ClassRoom;
import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.filter.And;
import com.vaadin.data.util.filter.Compare.Equal;
import com.vaadin.data.util.sqlcontainer.RowId;
import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.CustomTable;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CustomTable.ColumnGenerator;
import com.vaadin.ui.Notification.Type;

public class RecruitStudentClassRoomTmpView extends ContentPage {
	private static final long serialVersionUID = 1L;

	private int capacity = 0;
	
	private StringBuilder sqlBuilder = new StringBuilder();
	
	private SQLContainer leftContainer;
	private SQLContainer rightContainer = Container.getInstance().getRecruitStudentContainer();
	private SQLContainer classContainer = Container.getInstance().getClassRoomContainer();
	
	private ComboBox classRoom;
	private Label capacityLabel;
	
	private TwinSelectTable twinSelect; 
	
	public RecruitStudentClassRoomTmpView() {
		super("จัดห้องชั่วคราว");
		
		/* ดึงค่าตารางทางซ้าย */
		sqlBuilder.append(" SELECT * FROM " + RecruitStudentSchema.TABLE_NAME);
		sqlBuilder.append(" WHERE " + RecruitStudentSchema.SCHOOL_ID + "=" + UI.getCurrent().getSession().getAttribute(SessionSchema.SCHOOL_ID));
		sqlBuilder.append(" AND " + RecruitStudentSchema.CLASS_ROOM_ID + " IS NULL");
		leftContainer = new Container().getFreeFormContainer(sqlBuilder.toString(), RecruitStudentSchema.STUDENT_ID);
		
		/* ค้นหาตารางขวา */
		rightContainer.addContainerFilter(new Equal(SchoolSchema.SCHOOL_ID, UI.getCurrent().getSession().getAttribute(SessionSchema.SCHOOL_ID)));
		
		buildMainLayout();
	}
	
	private void buildMainLayout(){
		setSpacing(true);
		
		/* ==== Layout ส่วนของชั้นเรียน ==== */
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
					Item item = classContainer.getItem(new RowId(event.getProperty().getValue()));
					capacity = Integer.parseInt(item.getItemProperty(ClassRoomSchema.CAPACITY).getValue().toString());
					
					capacityLabel.setValue("ความจุนักเรียน " + capacity + " คน");
					capacityLabel.setVisible(true);
					
					setRightData();
				}
			}
		});
		toolStrip.addComponent(classRoom);
		toolStrip.setComponentAlignment(classRoom, Alignment.MIDDLE_LEFT);
		
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

		twinSelect.setFilterDecorator(new TableFilterDecorator());
		twinSelect.setFilterGenerator(new TableFilterGenerator());
		twinSelect.setFilterBarVisible(true);
		
		twinSelect.getLeftTable().setContainerDataSource(leftContainer);
		twinSelect.getRightTable().setContainerDataSource(rightContainer);
		setRightData();
		initTableStyle();
		
		twinSelect.setAddClick(addListener);
		twinSelect.setAddAllClick(addAllListener);
		twinSelect.setRemoveClick(removeListener);
		twinSelect.setRemoveAllClick(removeAllListener);
		
		addComponent(twinSelect);
		setExpandRatio(twinSelect, 1);
		
		setLeftData();		
	}
	
	/* ตั้งค่ารูปแบบแสดงของตาราง */
	private void initTableStyle(){
		twinSelect.setColumnHeader(RecruitStudentSchema.RECRUIT_CODE, "หมายเลขสมัคร");
		twinSelect.setColumnHeader(RecruitStudentSchema.CLASS_RANGE,"ช่วงชั้น");
		twinSelect.setColumnHeader(RecruitStudentSchema.PRENAME, "ชื่อต้น");
		twinSelect.setColumnHeader(RecruitStudentSchema.FIRSTNAME, "ชื่อ");
		twinSelect.setColumnHeader(RecruitStudentSchema.LASTNAME, "สกุล");
		
		twinSelect.setVisibleColumns(
				RecruitStudentSchema.RECRUIT_CODE, 
				RecruitStudentSchema.CLASS_RANGE,
				RecruitStudentSchema.PRENAME,
				RecruitStudentSchema.FIRSTNAME, 
				RecruitStudentSchema.LASTNAME);
		
		setColumnGenerator(RecruitStudentSchema.CLASS_RANGE, RecruitStudentSchema.PRENAME, "");
	}
	
	/* ตั้งค่ารูปแบบข้อมูลของค่า Fix */
	private void setColumnGenerator(Object... propertyIds){
		for(final Object propertyId:propertyIds){
			twinSelect.addGeneratedColumn(propertyId, new ColumnGenerator() {
				private static final long serialVersionUID = 1L;

				@Override
				public Object generateCell(CustomTable source, Object itemId, Object columnId) {
					Item item = source.getItem(itemId);
					Object value = new Object();
					
					if(RecruitStudentSchema.CLASS_RANGE.equals(propertyId))
						value = ClassRange.getNameTh(Integer.parseInt(item.getItemProperty(RecruitStudentSchema.CLASS_RANGE).getValue().toString()));
					else if(RecruitStudentSchema.PRENAME.equals(propertyId))
						value = Prename.getNameTh(Integer.parseInt(item.getItemProperty(RecruitStudentSchema.PRENAME).getValue().toString()));
					return value;
				}
			});
		}
	}
	
	/* จำนวนนักเรียนทีี่ค้นฟา */
	private void setLeftData(){
		twinSelect.setLeftCountFooter(RecruitStudentSchema.RECRUIT_CODE);
	}
	
	/* จำนวนนักเรียนที่ถูกเลือก */
	private void setRightData(){
		Object classRoomSelect = classRoom.getValue();
		if(classRoomSelect != null)
			twinSelect.getRightTable().setFilterFieldValue(RecruitStudentSchema.CLASS_ROOM_ID, new NumberInterval(null, null, classRoomSelect.toString()));
		else
			twinSelect.getRightTable().setFilterFieldValue(RecruitStudentSchema.CLASS_ROOM_ID, new NumberInterval(null, null, "0"));
		
		twinSelect.setRightCountFooter(RecruitStudentSchema.RECRUIT_CODE);
	}
	
	/* ย้ายข้างจากซ้ายไปขวาจากที่ถูกเลือก */
	@SuppressWarnings("unchecked")
	private void selectData(Object... itemIds){
		for(Object itemId: itemIds){
			try {
				/* ก่อนแก้ไข เพิ่มต้องลบ Filter ก่อนหน้า */
				rightContainer.removeAllContainerFilters();
				
				Item studentItem = rightContainer.getItem(itemId);
				studentItem.getItemProperty(RecruitStudentSchema.CLASS_ROOM_ID).setValue(Integer.parseInt(classRoom.getValue().toString()));
				rightContainer.commit();
				
				/* Refresh ข้อมูลาราง */
				rightContainer.addContainerFilter(new And(new Equal(RecruitStudentSchema.CLASS_ROOM_ID, classRoom.getValue()),
						new Equal(SchoolSchema.SCHOOL_ID, UI.getCurrent().getSession().getAttribute(SessionSchema.SCHOOL_ID))));
				leftContainer.refresh();
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
		twinSelect.setLeftCountFooter(RecruitStudentSchema.RECRUIT_CODE);
		twinSelect.setRightCountFooter(RecruitStudentSchema.RECRUIT_CODE);
	}
	
	/* ย้ายข้างจากซ้ายไปขวาจากทั้งหมด*/
	@SuppressWarnings("unchecked")
	private void selectAllData(){
		Collection<?> itemIds = twinSelect.getLeftTable().getItemIds();
		for(Object itemId: itemIds){
			try {
				/* ก่อนแก้ไข เพิ่มต้องลบ Filter ก่อนหน้า */
				rightContainer.removeAllContainerFilters();
				
				Item studentItem = rightContainer.getItem(itemId);
				studentItem.getItemProperty(RecruitStudentSchema.CLASS_ROOM_ID).setValue(Integer.parseInt(classRoom.getValue().toString()));
				rightContainer.commit();
				
				/* Refresh ข้อมูลาราง */
				rightContainer.addContainerFilter(new And(new Equal(RecruitStudentSchema.CLASS_ROOM_ID, classRoom.getValue()),
						new Equal(SchoolSchema.SCHOOL_ID, UI.getCurrent().getSession().getAttribute(SessionSchema.SCHOOL_ID))));
				leftContainer.refresh();
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
		twinSelect.getLeftTable().removeAllItems();
		twinSelect.setLeftCountFooter(RecruitStudentSchema.RECRUIT_CODE);
		twinSelect.setRightCountFooter(RecruitStudentSchema.RECRUIT_CODE);
	}

	/* ย้ายข้างจากขวาไปซ้ายจากที่เลือก */
	@SuppressWarnings("unchecked")
	private void removeData(Object... itemIds){
		for(Object itemId: itemIds){
			try {
				/* ก่อนแก้ไข เพิ่มต้องลบ Filter ก่อนหน้า */
				rightContainer.removeAllContainerFilters();
				
				Item studentItem = rightContainer.getItem(itemId);
				studentItem.getItemProperty(RecruitStudentSchema.CLASS_ROOM_ID).setValue(null);
				rightContainer.commit();
				
				/* Refresh ข้อมูลาราง */
				rightContainer.addContainerFilter(new And(new Equal(RecruitStudentSchema.CLASS_ROOM_ID, classRoom.getValue()),
						new Equal(SchoolSchema.SCHOOL_ID, UI.getCurrent().getSession().getAttribute(SessionSchema.SCHOOL_ID))));
				leftContainer.refresh();
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
		twinSelect.setLeftCountFooter(RecruitStudentSchema.RECRUIT_CODE);
		twinSelect.setRightCountFooter(RecruitStudentSchema.RECRUIT_CODE);
	}
	
	/* ย้ายข้างจากขวาไปซ้ายจากจำนวนทั้งหมด */
	@SuppressWarnings("unchecked")
	private void removeAllData(){
		for(Object itemId: twinSelect.getRightTable().getItemIds()){
			try {
				/* ก่อนแก้ไข เพิ่มต้องลบ Filter ก่อนหน้า */
				rightContainer.removeAllContainerFilters();
				
				Item studentItem = rightContainer.getItem(itemId);
				studentItem.getItemProperty(RecruitStudentSchema.CLASS_ROOM_ID).setValue(null);
				rightContainer.commit();
				
				/* Refresh ข้อมูลาราง */
				rightContainer.addContainerFilter(new And(new Equal(RecruitStudentSchema.CLASS_ROOM_ID, classRoom.getValue()),
						new Equal(SchoolSchema.SCHOOL_ID, UI.getCurrent().getSession().getAttribute(SessionSchema.SCHOOL_ID))));
				leftContainer.refresh();
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
		twinSelect.getRightTable().removeAllItems();
		twinSelect.setLeftCountFooter(RecruitStudentSchema.RECRUIT_CODE);
		twinSelect.setRightCountFooter(RecruitStudentSchema.RECRUIT_CODE);
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
				Notification.show("กรุณาเลือกชั้นเรียน", Type.WARNING_MESSAGE);
				return;
			}
			
			Collection<?> itemIds = (Collection<?>)twinSelect.getLeftTable().getValue();
			
			/* ตรวจสอบว่าชั้นเรียนไม่เพียงพอหรือไม่ */
			if(isFullCapacity(itemIds.size())){
				Notification.show("ชั้นเรียนไม่เพียงพอ", Type.WARNING_MESSAGE);
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
				Notification.show("กรุณาเลือกชั้นเรียน", Type.WARNING_MESSAGE);
				return;
			}
			
			Collection<?> itemIds = twinSelect.getLeftTable().getItemIds();
			
			/* ตรวจสอบว่าชั้นเรียนไม่เพียงพอหรือไม่ */
			if(isFullCapacity(itemIds.size())){
				Notification.show("ชั้นเรียนไม่เพียงพอ", Type.WARNING_MESSAGE);
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
			/* ตรวจสอบว่ามีการเลือกห้องหรือยัง */
			if(classRoom.getValue() == null){
				Notification.show("กรุณาเลือกชั้นเรียน", Type.WARNING_MESSAGE);
				return;
			}
			
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
			/* ตรวจสอบว่ามีการเลือกห้องหรือยัง */
			if(classRoom.getValue() == null){
				Notification.show("กรุณาเลือกชั้นเรียน", Type.WARNING_MESSAGE);
				return;
			}
			
			removeAllData();
		}
	};
}
