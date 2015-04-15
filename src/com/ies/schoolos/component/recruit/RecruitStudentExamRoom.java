package com.ies.schoolos.component.recruit;

import java.util.Collection;

import org.tepi.filtertable.FilterTable;

import com.ies.schoolos.component.ui.TwinSelectTable;
import com.ies.schoolos.container.Container;
import com.ies.schoolos.filter.TableFilterDecorator;
import com.ies.schoolos.filter.TableFilterGenerator;
import com.ies.schoolos.schema.SchoolSchema;
import com.ies.schoolos.schema.SessionSchema;
import com.ies.schoolos.schema.fundamental.BuildingSchema;
import com.ies.schoolos.schema.recruit.RecruitStudentSchema;
import com.ies.schoolos.type.ClassRange;
import com.ies.schoolos.type.Prename;
import com.ies.schoolos.type.dynamic.Building;
import com.ies.schoolos.utility.Notification;
import com.ies.schoolos.utility.Utility;
import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.filter.And;
import com.vaadin.data.util.filter.Compare.Equal;
import com.vaadin.data.util.filter.IsNull;
import com.vaadin.data.util.sqlcontainer.RowId;
import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;


public class RecruitStudentExamRoom extends VerticalLayout{
	private static final long serialVersionUID = 1L;

	private int capacity = 0;
	
	private ComboBox building;
	private ComboBox classRange;
	private Label capacityLabel;
	
	private TwinSelectTable twinSelect;
	
	private SQLContainer sContainer = Container.getInstance().getRecruitStudentContainer();
	private SQLContainer bContainer = Container.getInstance().getBuildingContainer();

	public RecruitStudentExamRoom() {		
		sContainer.refresh();
		bContainer.refresh();

		setSizeFull();
		setSpacing(true);
		setMargin(true);
		buildMainLayout();
	}

	private void buildMainLayout(){
		
		/* Toolbar */
		HorizontalLayout toolStrip = new HorizontalLayout();
		toolStrip.setWidth("100%");
		toolStrip.setHeight("90px");
		toolStrip.setStyleName("border-white");
		addComponent(toolStrip);
		
		building = new ComboBox("อาคาร",new Building());
		building.setInputPrompt("กรุณาเลือก");
		building.setItemCaptionPropertyId("name");
		building.setImmediate(true);
        building.setNullSelectionAllowed(false);
        building.setRequired(true);
		building.setWidth("-1px");
		building.setHeight("-1px");
		building.setFilteringMode(FilteringMode.CONTAINS);
		building.addValueChangeListener(searchValueChange);
		toolStrip.addComponent(building);
		toolStrip.setComponentAlignment(building, Alignment.MIDDLE_LEFT);
		
		classRange = new ComboBox("ระดับชั้นที่สมัคร",new ClassRange());
		classRange.setInputPrompt("กรุณาเลือก");
		classRange.setItemCaptionPropertyId("name");
		classRange.setImmediate(true);
		classRange.setNullSelectionAllowed(false);
		classRange.setRequired(true);
		classRange.setWidth("-1px");
		classRange.setHeight("-1px");
		classRange.setFilteringMode(FilteringMode.CONTAINS);
		classRange.addValueChangeListener(searchValueChange);
		toolStrip.addComponent(classRange);
		toolStrip.setComponentAlignment(classRange, Alignment.MIDDLE_LEFT);
		
		capacityLabel = new Label();
		capacityLabel.setStyleName("label-green");
		toolStrip.addComponent(capacityLabel);
		toolStrip.setComponentAlignment(capacityLabel, Alignment.MIDDLE_LEFT);
		
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
		
		twinSelect.setFilterDecorator(new TableFilterDecorator());
		twinSelect.setFilterGenerator(new TableFilterGenerator());
		twinSelect.setFilterBarVisible(true);
        
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
		
		twinSelect.setAddClick(addListener);
		twinSelect.setAddAllClick(addAllListener);
		twinSelect.setRemoveClick(removeListener);
		twinSelect.setRemoveAllClick(removeAllListener);
		
		addComponent(twinSelect);
		setExpandRatio(twinSelect, 1);
		sortData();
	}
	
	/* เรียงอันดับข้อมูลของตาราง */
	private void sortData(){
		Object[] prop = {RecruitStudentSchema.FIRSTNAME};
		boolean[] bool = {true};
		twinSelect.getLeftTable().sort(prop, bool);
		twinSelect.getRightTable().sort(prop, bool);
	}
	
	/* จำนวนนักเรียนทีี่ค้นฟา */
	private void setLeftData(){
		twinSelect.removeAllLeftItem();
		
		/* ค้นหานักเรียนที่ยังไม่ถูกกำหนดห้องสอบ */
		sContainer.addContainerFilter(new And(new Equal(RecruitStudentSchema.CLASS_RANGE, classRange.getValue()),
				new Equal(RecruitStudentSchema.SCHOOL_ID, UI.getCurrent().getSession().getAttribute(SessionSchema.SCHOOL_ID)),
				new IsNull(RecruitStudentSchema.EXAM_BUILDING_ID)));
		
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
		
		/* ค้นหานักเรียนที่อยู่ห้องสอบที่กำหนด */
		sContainer.addContainerFilter(new And(new Equal(RecruitStudentSchema.EXAM_BUILDING_ID, building.getValue()),
				new Equal(SchoolSchema.SCHOOL_ID, UI.getCurrent().getSession().getAttribute(SessionSchema.SCHOOL_ID))));
		
		for(Object itemId: sContainer.getItemIds()){
			Item item = sContainer.getItem(itemId);
			addItemData(twinSelect.getRightTable(), itemId, item);
		}
		
		twinSelect.setRightCountFooter(RecruitStudentSchema.RECRUIT_CODE);
		/* ลบ WHERE ออกจาก Query เพื่อป้องกันการค้างของคำสั่่งจากการทำงานอื่นที่เรียกตัวแปรไปใช้ */
		sContainer.removeAllContainerFilters();
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
				studentItem.getItemProperty(RecruitStudentSchema.EXAM_BUILDING_ID).setValue(Integer.parseInt(building.getValue().toString()));
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
				studentItem.getItemProperty(RecruitStudentSchema.EXAM_BUILDING_ID).setValue(Integer.parseInt(building.getValue().toString()));
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
					studentItem.getItemProperty(RecruitStudentSchema.EXAM_BUILDING_ID).setValue(null);
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
				studentItem.getItemProperty(RecruitStudentSchema.EXAM_BUILDING_ID).setValue(null);
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
	
	/* ตรวจสอบขนาดห้องสอบ กับ จำนวนนักเรียนที่เลือก */
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
				item.getItemProperty(RecruitStudentSchema.LASTNAME).getValue()
		},itemId);
	}
	
	/* ค้นหานักเรียนตามเงื่อนไขที่เลือก */
	private ValueChangeListener searchValueChange = new ValueChangeListener() {
		private static final long serialVersionUID = 1L;

		@Override
		public void valueChange(ValueChangeEvent event) {
			if(building.getValue() != null &&
					/*gender.getValue() != null &&*/
					classRange.getValue() != null){
				/* ======== ดึงข้อมูลนักเรียนที่ค้นหา ========== */
				setLeftData();
				setRightData();
				
				/* ======== ดึงจำนวนคนที่ห้องรองรับได้ ========== */
				Item item = bContainer.getItem(new RowId(building.getValue()));
				capacity = Integer.parseInt(item.getItemProperty(BuildingSchema.CAPACITY).getValue().toString());
				
				capacityLabel.setValue("ความจุนักเรียน " + capacity + " คน");
				sortData();
			}
				
		}
	};
	
	/* ปุ่มเลือกนักเรียนจากซ้ายไปขวา จากนักเรียนที่เลือก */
	private ClickListener addListener = new ClickListener() {
		private static final long serialVersionUID = 1L;
		@Override
		public void buttonClick(ClickEvent event) {
			Collection<?> itemIds = (Collection<?>)twinSelect.getLeftTable().getValue();
			
			if(isFullCapacity(itemIds.size())){
				Notification.show("ห้องสอบเต็ม กรุณาเลือกห้องสอบใหม่่", Type.WARNING_MESSAGE);
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
			Collection<?> itemIds = twinSelect.getLeftTable().getItemIds();
			
			if(isFullCapacity(itemIds.size())){
				Notification.show("ห้องสอบเต็ม กรุณาเลือกห้องสอบใหม่่", Type.WARNING_MESSAGE);
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
