package com.ies.schoolos.component.recruit;

import java.util.Collection;

import org.tepi.filtertable.FilterTable;

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
import com.ies.schoolos.utility.DateTimeUtil;
import com.ies.schoolos.utility.Utility;
import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.filter.And;
import com.vaadin.data.util.filter.IsNull;
import com.vaadin.data.util.filter.Compare.Equal;
import com.vaadin.data.util.filter.Compare.Greater;
import com.vaadin.data.util.filter.Compare.Less;
import com.vaadin.data.util.sqlcontainer.RowId;
import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Notification.Type;

public class RecruitStudentClassRoomTmpView extends VerticalLayout{
	private static final long serialVersionUID = 1L;

	private int capacity = 0;
	
	private SQLContainer sContainer = Container.getInstance().getRecruitStudentContainer();
	private SQLContainer classContainer = Container.getInstance().getClassRoomContainer();
	
	private ComboBox classRoom;
	private Label capacityLabel;
	
	private TwinSelectTable twinSelect; 
	
	public RecruitStudentClassRoomTmpView() {
		setSizeFull();
		setSpacing(true);
		setMargin(true);
		buildMainLayout();
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
					Item item = classContainer.getItem(new RowId(event.getProperty().getValue()));
					capacity = Integer.parseInt(item.getItemProperty(ClassRoomSchema.CAPACITY).getValue().toString());
					
					capacityLabel.setValue("ความจุนักเรียน " + capacity + " คน");
					capacityLabel.setVisible(true);
					
					setRightData();
					sortData();
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
		
		twinSelect.addContainerProperty(RecruitStudentSchema.RECRUIT_CODE, String.class, null);
		twinSelect.addContainerProperty(RecruitStudentSchema.CLASS_RANGE, String.class, null);
		twinSelect.addContainerProperty(RecruitStudentSchema.PRENAME, String.class, null);
		twinSelect.addContainerProperty(RecruitStudentSchema.FIRSTNAME, String.class, null);
		twinSelect.addContainerProperty(RecruitStudentSchema.LASTNAME, String.class, null);
		twinSelect.addContainerProperty(RecruitStudentSchema.SCORE, Double.class, null);
		
		twinSelect.setFilterDecorator(new TableFilterDecorator());
		twinSelect.setFilterGenerator(new TableFilterGenerator());
		twinSelect.setFilterBarVisible(true);
        
		twinSelect.setColumnHeader(RecruitStudentSchema.RECRUIT_CODE, "หมายเลขสมัคร");
		twinSelect.setColumnHeader(RecruitStudentSchema.CLASS_RANGE,"ช่วงชั้น");
		twinSelect.setColumnHeader(RecruitStudentSchema.PRENAME, "ชื่อต้น");
		twinSelect.setColumnHeader(RecruitStudentSchema.FIRSTNAME, "ชื่อ");
		twinSelect.setColumnHeader(RecruitStudentSchema.LASTNAME, "สกุล");
		twinSelect.setColumnHeader(RecruitStudentSchema.SCORE, "คะแนนสอบ");
		
		twinSelect.setVisibleColumns(
				RecruitStudentSchema.RECRUIT_CODE, 
				RecruitStudentSchema.CLASS_RANGE,
				RecruitStudentSchema.PRENAME,
				RecruitStudentSchema.FIRSTNAME, 
				RecruitStudentSchema.LASTNAME,
				RecruitStudentSchema.SCORE);
		
		twinSelect.setAddClick(addListener);
		twinSelect.setAddAllClick(addAllListener);
		twinSelect.setRemoveClick(removeListener);
		twinSelect.setRemoveAllClick(removeAllListener);
		
		addComponent(twinSelect);
		setExpandRatio(twinSelect, 1);
		
		setLeftData();		
		sortData();
	}
	
	/* จำนวนนักเรียนทีี่ค้นฟา */
	private void setLeftData(){
		twinSelect.removeAllLeftItem();
		
		/* ค้นหานักเรียนที่ยังไม่ถูกกำหนดชั้นเรียน */
		sContainer.addContainerFilter(new And(
				new Equal(RecruitStudentSchema.SCHOOL_ID, SessionSchema.getSchoolID()),
				new Greater(RecruitStudentSchema.REGISTER_DATE,DateTimeUtil.getFirstDateOfYear()),
				new Less(RecruitStudentSchema.REGISTER_DATE,DateTimeUtil.getLastDateOfYear()),
				new IsNull(RecruitStudentSchema.CLASS_ROOM_ID)));
		
		for(final Object itemId:sContainer.getItemIds()){
			Item item = sContainer.getItem(itemId);
			addItemData(twinSelect.getLeftTable(), itemId, item);
		}
		
		twinSelect.setLeftCountFooter(RecruitStudentSchema.RECRUIT_CODE);
		/* ลบ WHERE ออกจาก Query เพื่อป้องกันการค้างของคำสั่่งจากการทำงานอื่นที่เรียกตัวแปรไปใช้ */
		sContainer.removeAllContainerFilters();
	}
	
	/* จำนวนนักเรียนที่ถูกเลือก */
	private void setRightData(){
		twinSelect.removeAllRightItem();
		
		/* ค้นหานักเรียนที่อยู่ชั้นเรียนที่กำหนด */
		sContainer.addContainerFilter(new And(new Equal(RecruitStudentSchema.CLASS_ROOM_ID, Integer.parseInt(classRoom.getValue().toString())),
				new Equal(SchoolSchema.SCHOOL_ID, SessionSchema.getSchoolID())));
		
		for(Object itemId: sContainer.getItemIds()){
			Item item = sContainer.getItem(itemId);
			addItemData(twinSelect.getRightTable(), itemId, item);
		}
		
		twinSelect.setRightCountFooter(RecruitStudentSchema.RECRUIT_CODE);
		/* ลบ WHERE ออกจาก Query เพื่อป้องกันการค้างของคำสั่่งจากการทำงานอื่นที่เรียกตัวแปรไปใช้ */
		sContainer.removeAllContainerFilters();
	}
	
	/* เรียงอันดับข้อมูลของตาราง */
	private void sortData(){
		Object[] propLeft = {RecruitStudentSchema.SCORE};
		boolean[] boolLeft = {false};
		twinSelect.getLeftTable().sort(propLeft, boolLeft);
		
		Object[] propRight = {RecruitStudentSchema.FIRSTNAME};
		boolean[] boolRight = {true};
		twinSelect.getRightTable().sort(propRight, boolRight);
	}
	
	/* ย้ายข้างจากซ้ายไปขวาจากที่ถูกเลือก */
	@SuppressWarnings("unchecked")
	private void selectData(Object... itemIds){
		try {
			for(Object itemId: itemIds){
				Item leftData = twinSelect.getLeftTable().getItem(itemId);
				addItemData(twinSelect.getRightTable(), itemId, leftData);
				twinSelect.getLeftTable().removeItem(itemId);
				
				Item studentItem = sContainer.getItem(itemId);
				studentItem.getItemProperty(RecruitStudentSchema.CLASS_ROOM_ID).setValue(Integer.parseInt(classRoom.getValue().toString()));
				sContainer.commit();
			}
			sortData();
			twinSelect.setLeftCountFooter(RecruitStudentSchema.RECRUIT_CODE);
			twinSelect.setRightCountFooter(RecruitStudentSchema.RECRUIT_CODE);
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
				Item item = twinSelect.getLeftTable().getItem(itemId);
				addItemData(twinSelect.getRightTable(), itemId, item);
				
				Item studentItem = sContainer.getItem(itemId);
				studentItem.getItemProperty(RecruitStudentSchema.CLASS_ROOM_ID).setValue(Integer.parseInt(classRoom.getValue().toString()));
				sContainer.commit();
			}
			twinSelect.getLeftTable().removeAllItems();
			sortData();
			twinSelect.setLeftCountFooter(RecruitStudentSchema.RECRUIT_CODE);
			twinSelect.setRightCountFooter(RecruitStudentSchema.RECRUIT_CODE);
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	/* ย้ายข้างจากขวาไปซ้ายจากที่เลือก */
	@SuppressWarnings("unchecked")
	private void removeData(Object... itemIds){
		try {
			for(Object itemId: itemIds){
					Item item = twinSelect.getRightTable().getItem(itemId);
					addItemData(twinSelect.getLeftTable(), itemId, item);
					twinSelect.getRightTable().removeItem(itemId);	
					
					Item studentItem = sContainer.getItem(itemId);
					studentItem.getItemProperty(RecruitStudentSchema.CLASS_ROOM_ID).setValue(null);
					sContainer.commit();
			}
			sortData();
			twinSelect.setLeftCountFooter(RecruitStudentSchema.RECRUIT_CODE);
			twinSelect.setRightCountFooter(RecruitStudentSchema.RECRUIT_CODE);
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/* ย้ายข้างจากขวาไปซ้ายจากจำนวนทั้งหมด */
	@SuppressWarnings("unchecked")
	private void removeAllData(){
		try {
			for(Object itemId: twinSelect.getRightTable().getItemIds()){
				Item item = twinSelect.getRightTable().getItem(itemId);
				addItemData(twinSelect.getLeftTable(), itemId, item);
				
				Item studentItem = sContainer.getItem(itemId);
				studentItem.getItemProperty(RecruitStudentSchema.CLASS_ROOM_ID).setValue(null);
				sContainer.commit();
			}
				
			twinSelect.getRightTable().removeAllItems();
			sortData();
			twinSelect.setLeftCountFooter(RecruitStudentSchema.RECRUIT_CODE);
			twinSelect.setRightCountFooter(RecruitStudentSchema.RECRUIT_CODE);
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
	
	/* ใส่ข้อมูลในตาราง */
	private void addItemData(FilterTable table, Object itemId, Item item){
		/* ตรวจสอบข้อมูล หากมาจาก setLeftData , setRightData ค่าจะเป็น int
		 * หากมาจากการย้ายข้าง ข้อมูลจะเป็น String อยู่แล้วไม่จำเป็นต้องมาดึงค่าของตัวแปร Fix 
		 * */
		String classRange = item.getItemProperty(RecruitStudentSchema.CLASS_RANGE).getValue().toString();
		String prename = item.getItemProperty(RecruitStudentSchema.PRENAME).getValue().toString();
		
		if(Utility.isInteger(classRange))
			classRange = ClassRange.getNameTh(Integer.parseInt(item.getItemProperty(RecruitStudentSchema.CLASS_RANGE).getValue().toString()));
		if(Utility.isInteger(prename))
			prename = Prename.getNameTh(Integer.parseInt(item.getItemProperty(RecruitStudentSchema.PRENAME).getValue().toString()));
		
		table.addItem(new Object[] {
				item.getItemProperty(RecruitStudentSchema.RECRUIT_CODE).getValue(), 
				classRange,
				prename, 
				item.getItemProperty(RecruitStudentSchema.FIRSTNAME).getValue(), 
				item.getItemProperty(RecruitStudentSchema.LASTNAME).getValue(),
				item.getItemProperty(RecruitStudentSchema.SCORE).getValue()
		},itemId);
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
