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
import com.ies.schoolos.utility.DateTimeUtil;
import com.ies.schoolos.utility.Utility;
import com.vaadin.data.Item;
import com.vaadin.data.util.filter.And;
import com.vaadin.data.util.filter.Compare.Equal;
import com.vaadin.data.util.filter.Compare.Greater;
import com.vaadin.data.util.filter.Compare.Less;
import com.vaadin.data.util.sqlcontainer.RowId;
import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

public class RecruitStudentConfirmView extends VerticalLayout{
	private static final long serialVersionUID = 1L;

	private SQLContainer sContainer = Container.getInstance().getRecruitStudentContainer();
	private SQLContainer classContainer = Container.getInstance().getClassRoomContainer();
	
	private TwinSelectTable twinSelect; 
	
	public RecruitStudentConfirmView() {
		setSizeFull();
		setSpacing(true);
		setMargin(true);
		buildMainLayout();
	}
	
	private void buildMainLayout(){
		/* Toolbar */
		/*toolbar = new HorizontalLayout();
		toolbar.setSpacing(true);
		addComponent(toolbar);
		
		ExcelExporter excelExporter = new ExcelExporter(new RecruitStudentToExcel());
		excelExporter.setIcon(FontAwesome.FILE_EXCEL_O);
		excelExporter.setCaption("ส่งออกไฟล์ Excel");
		toolbar.addComponent(excelExporter);*/
		
		/* ตารางรายการนักเรียน */
		twinSelect = new TwinSelectTable();
		twinSelect.setSizeFull();
		twinSelect.setSpacing(true);
		twinSelect.setSelectable(true);
		twinSelect.setMultiSelect(true);
		twinSelect.showFooterCount(true);
		twinSelect.setFooterUnit("คน");
		
		twinSelect.addContainerProperty(RecruitStudentSchema.CLASS_RANGE, String.class, null);
		twinSelect.addContainerProperty(RecruitStudentSchema.PRENAME, String.class, null);
		twinSelect.addContainerProperty(RecruitStudentSchema.FIRSTNAME, String.class, null);
		twinSelect.addContainerProperty(RecruitStudentSchema.LASTNAME, String.class, null);
		twinSelect.addContainerProperty(RecruitStudentSchema.CLASS_ROOM_ID, String.class, null);
		
		twinSelect.setFilterDecorator(new TableFilterDecorator());
		twinSelect.setFilterGenerator(new TableFilterGenerator());
		twinSelect.setFilterBarVisible(true);
        
		twinSelect.setColumnHeader(RecruitStudentSchema.CLASS_RANGE,"ช่วงชั้น");
		twinSelect.setColumnHeader(RecruitStudentSchema.PRENAME, "ชื่อต้น");
		twinSelect.setColumnHeader(RecruitStudentSchema.FIRSTNAME, "ชื่อ");
		twinSelect.setColumnHeader(RecruitStudentSchema.LASTNAME, "สกุล");
		twinSelect.setColumnHeader(RecruitStudentSchema.CLASS_ROOM_ID, "ห้องเรียนชั่วคราว");
		
		twinSelect.setVisibleColumns(
				RecruitStudentSchema.CLASS_RANGE,
				RecruitStudentSchema.PRENAME,
				RecruitStudentSchema.FIRSTNAME, 
				RecruitStudentSchema.LASTNAME,
				RecruitStudentSchema.CLASS_ROOM_ID);
		
		twinSelect.setAddClick(addListener);
		twinSelect.setAddAllClick(addAllListener);
		twinSelect.setRemoveClick(removeListener);
		twinSelect.setRemoveAllClick(removeAllListener);
		
		addComponent(twinSelect);
		setExpandRatio(twinSelect, 1);
		
		setLeftData();		
		setRightData();
	}
	
	/* จำนวนนักเรียนทีี่ค้นฟา */
	private void setLeftData(){
		twinSelect.removeAllLeftItem();
		
		/* ค้นหานักเรียนที่ยังไม่ถูกกำหนดชั้นเรียน */
		sContainer.addContainerFilter(new And(
				new Equal(RecruitStudentSchema.SCHOOL_ID, SessionSchema.getSchoolID()),
				new Greater(RecruitStudentSchema.REGISTER_DATE,DateTimeUtil.getFirstDateOfYear()),
				new Less(RecruitStudentSchema.REGISTER_DATE,DateTimeUtil.getLastDateOfYear()),
				new Equal(RecruitStudentSchema.IS_CONFIRM, false)));
		
		for(final Object itemId:sContainer.getItemIds()){
			Item item = sContainer.getItem(itemId);
			addItemData(twinSelect.getLeftTable(), itemId, item);
		}
		
		twinSelect.setLeftCountFooter(RecruitStudentSchema.CLASS_RANGE);
		/* ลบ WHERE ออกจาก Query เพื่อป้องกันการค้างของคำสั่่งจากการทำงานอื่นที่เรียกตัวแปรไปใช้ */
		sContainer.removeAllContainerFilters();
	}
	
	/* จำนวนนักเรียนที่ถูกเลือก */
	private void setRightData(){
		twinSelect.removeAllRightItem();
		
		/* ค้นหานักเรียนที่อยู่ชั้นเรียนที่กำหนด */
		sContainer.addContainerFilter(new And(new Equal(RecruitStudentSchema.IS_CONFIRM, true),
				new Equal(SchoolSchema.SCHOOL_ID, SessionSchema.getSchoolID())));
		
		for(Object itemId: sContainer.getItemIds()){
			Item item = sContainer.getItem(itemId);
			addItemData(twinSelect.getRightTable(), itemId, item);
		}
		
		twinSelect.setRightCountFooter(RecruitStudentSchema.CLASS_RANGE);
		/* ลบ WHERE ออกจาก Query เพื่อป้องกันการค้างของคำสั่่งจากการทำงานอื่นที่เรียกตัวแปรไปใช้ */
		sContainer.removeAllContainerFilters();
	}
	
	/* ย้ายข้างจากซ้ายไปขวาจากที่ถูกเลือก */
	@SuppressWarnings("unchecked")
	private void selectData(Object... itemIds){
		for(Object itemId: itemIds){
			try {
				Item leftData = twinSelect.getLeftTable().getItem(itemId);
				addItemData(twinSelect.getRightTable(), itemId, leftData);
				twinSelect.getLeftTable().removeItem(itemId);
				
				Item studentItem = sContainer.getItem(itemId);
				studentItem.getItemProperty(RecruitStudentSchema.IS_CONFIRM).setValue(true);
				sContainer.commit();
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
				Item item = twinSelect.getLeftTable().getItem(itemId);
				addItemData(twinSelect.getRightTable(), itemId, item);
				
				Item studentItem = sContainer.getItem(itemId);
				studentItem.getItemProperty(RecruitStudentSchema.IS_CONFIRM).setValue(true);
				sContainer.commit();
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
				Item item = twinSelect.getRightTable().getItem(itemId);
				addItemData(twinSelect.getLeftTable(), itemId, item);
				twinSelect.getRightTable().removeItem(itemId);	
				
				Item studentItem = sContainer.getItem(itemId);
				studentItem.getItemProperty(RecruitStudentSchema.IS_CONFIRM).setValue(false);
				sContainer.commit();
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
				Item item = twinSelect.getRightTable().getItem(itemId);
				addItemData(twinSelect.getLeftTable(), itemId, item);
				
				Item studentItem = sContainer.getItem(itemId);
				studentItem.getItemProperty(RecruitStudentSchema.IS_CONFIRM).setValue(false);
				sContainer.commit();
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
		twinSelect.getRightTable().removeAllItems();
		twinSelect.setLeftCountFooter(RecruitStudentSchema.RECRUIT_CODE);
		twinSelect.setRightCountFooter(RecruitStudentSchema.RECRUIT_CODE);
	}

	/* ใส่ข้อมูลในตาราง */
	private void addItemData(FilterTable table, Object itemId, Item item){
		
		
		/* ตรวจสอบข้อมูล หากมาจาก setLeftData , setRightData ค่าจะเป็น int
		 * หากมาจากการย้ายข้าง ข้อมูลจะเป็น String อยู่แล้วไม่จำเป็นต้องมาดึงค่าของตัวแปร Fix 
		 * */
		String classRange = item.getItemProperty(RecruitStudentSchema.CLASS_RANGE).getValue().toString();
		String prename = item.getItemProperty(RecruitStudentSchema.PRENAME).getValue().toString();

		Object className = item.getItemProperty(RecruitStudentSchema.CLASS_ROOM_ID).getValue();
		if(className == null)
			className = "ยังไม่ระบุชั้นเรียน";
		
		if(Utility.isInteger(classRange))
			classRange = ClassRange.getNameTh(Integer.parseInt(item.getItemProperty(RecruitStudentSchema.CLASS_RANGE).getValue().toString()));
		if(Utility.isInteger(prename))
			prename = Prename.getNameTh(Integer.parseInt(item.getItemProperty(RecruitStudentSchema.PRENAME).getValue().toString()));
		if(Utility.isInteger(className))
			className = classContainer.getItem(new RowId(item.getItemProperty(RecruitStudentSchema.CLASS_ROOM_ID).getValue())).getItemProperty(ClassRoomSchema.NAME).getValue().toString();
		
		table.addItem(new Object[] {
				classRange,
				prename, 
				item.getItemProperty(RecruitStudentSchema.FIRSTNAME).getValue(), 
				item.getItemProperty(RecruitStudentSchema.LASTNAME).getValue(),
				className
		},itemId);
	}
	
	/* ปุ่มเลือกนักเรียนจากซ้ายไปขวา จากนักเรียนที่เลือก */
	private ClickListener addListener = new ClickListener() {
		private static final long serialVersionUID = 1L;
		@Override
		public void buttonClick(ClickEvent event) {
			Collection<?> itemIds = (Collection<?>)twinSelect.getLeftTable().getValue();
						
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
