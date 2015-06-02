package com.ies.schoolos.component.academic;

import java.util.Collection;

import org.tepi.filtertable.FilterTable;

import com.ies.schoolos.component.ui.SchoolOSLayout;
import com.ies.schoolos.component.ui.TwinSelectTable;
import com.ies.schoolos.container.Container;
import com.ies.schoolos.filter.TableFilterDecorator;
import com.ies.schoolos.filter.TableFilterGenerator;
import com.ies.schoolos.schema.CreateModifiedSchema;
import com.ies.schoolos.schema.SessionSchema;
import com.ies.schoolos.schema.academic.ClassRoomLessonPlanSchema;
import com.ies.schoolos.schema.fundamental.ClassRoomSchema;
import com.ies.schoolos.type.ClassYear;
import com.ies.schoolos.utility.DateTimeUtil;
import com.ies.schoolos.utility.Notification;
import com.ies.schoolos.utility.Utility;
import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Notification.Type;

public class AddClassRoomLessonPlan extends SchoolOSLayout {

	private static final long serialVersionUID = 1L;

	private Object lessonPlanId;
	private Object classRange;
	
	private SQLContainer crlContainer;
	private SQLContainer classRoomLessonPlanContainer = container.getClassRoomLessonPlanContainer();
	
	private ComboBox classYear;
	private TextField academicYear;
	private TwinSelectTable twinSelect;
	
	public AddClassRoomLessonPlan(Object lessonPlanId, Object classRange) {
		this.lessonPlanId = lessonPlanId;
		this.classRange = classRange;

		buildMainLayout();
		setLeftData();
		setRightData();
	}
	
	private void buildMainLayout(){
		/* Toolbar */
		HorizontalLayout toolStrip = new HorizontalLayout();
		toolStrip.setWidth("100%");
		toolStrip.setHeight("90px");
		toolStrip.setStyleName("border-white");
		addComponent(toolStrip);
		
		classYear = new ComboBox("ชั้นปี",new ClassYear(Integer.parseInt(classRange.toString())));
		classYear.setInputPrompt("กรุณาเลือก");
		classYear.setItemCaptionPropertyId("name");
		classYear.setImmediate(true);
        classYear.setNullSelectionAllowed(false);
        classYear.setRequired(true);
		classYear.setWidth("-1px");
		classYear.setHeight("-1px");
		classYear.setValue(new ClassYear(Integer.parseInt(classRange.toString())).getIdByIndex(0));
		classYear.addValueChangeListener(new ValueChangeListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				setLeftData();
				setRightData();
			}
		});
		
		toolStrip.addComponent(classYear);
		toolStrip.setComponentAlignment(classYear, Alignment.MIDDLE_LEFT);
		
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
		
		twinSelect = new TwinSelectTable();
		twinSelect.setSizeFull();
		twinSelect.setSpacing(true);
		twinSelect.setSelectable(true);
		twinSelect.setMultiSelect(true);
		twinSelect.showFooterCount(true);
		twinSelect.setFooterUnit("คน");
		
		twinSelect.addContainerProperty(ClassRoomSchema.CLASS_YEAR, String.class, null);
		twinSelect.addContainerProperty(ClassRoomSchema.NUMBER, Integer.class, null);
		twinSelect.addContainerProperty(ClassRoomSchema.NAME, String.class, null);
		twinSelect.getRightTable().addContainerProperty(ClassRoomLessonPlanSchema.ACADEMIC_YEAR, String.class, null);
		
		twinSelect.setFilterDecorator(new TableFilterDecorator());
		twinSelect.setFilterGenerator(new TableFilterGenerator());
		twinSelect.setFilterBarVisible(true);
        
		twinSelect.setColumnHeader(ClassRoomSchema.CLASS_YEAR, "ชั้นปี");
		twinSelect.setColumnHeader(ClassRoomSchema.NUMBER,"หมายเลขชั้นเรียน");
		twinSelect.setColumnHeader(ClassRoomSchema.NAME, "ชื่อชั้นเรียน");
		twinSelect.getRightTable().setColumnHeader(ClassRoomLessonPlanSchema.ACADEMIC_YEAR, "ปีการศึกษา");
		
		twinSelect.getLeftTable().setVisibleColumns(
				ClassRoomSchema.CLASS_YEAR, 
				ClassRoomSchema.NUMBER,
				ClassRoomSchema.NAME);
		
		twinSelect.getRightTable().setVisibleColumns(
				ClassRoomSchema.CLASS_YEAR, 
				ClassRoomSchema.NUMBER,
				ClassRoomSchema.NAME,
				ClassRoomLessonPlanSchema.ACADEMIC_YEAR);
		
		twinSelect.setAddClick(addListener);
		twinSelect.setAddAllClick(addAllListener);
		twinSelect.setRemoveClick(removeListener);
		twinSelect.setRemoveAllClick(removeAllListener);
		
		addComponent(twinSelect);
	}
	
	/* เรียงอันดับข้อมูลของตาราง */
	private void sortData(){
		Object[] prop = {ClassRoomSchema.NUMBER};
		boolean[] bool = {true};
		twinSelect.getLeftTable().sort(prop, bool);
		twinSelect.getRightTable().sort(prop, bool);
	}
	
	private void setLeftData(){		
		twinSelect.removeAllLeftItem();
		
		StringBuilder subject = new StringBuilder();
		subject.append(" SELECT * FROM " + ClassRoomSchema.TABLE_NAME);
		subject.append(" WHERE "+ ClassRoomSchema.SCHOOL_ID + "=" + SessionSchema.getSchoolID());
		subject.append(" AND " + ClassRoomSchema.CLASS_YEAR + "=" + classYear.getValue());
		subject.append(" AND " + ClassRoomSchema.CLASS_ROOM_ID + " NOT IN (");
		subject.append(" SELECT "+ ClassRoomLessonPlanSchema.CLASS_ROOM_ID);
		subject.append(" FROM "+ ClassRoomLessonPlanSchema.TABLE_NAME);
		subject.append(" WHERE "+ ClassRoomLessonPlanSchema.SCHOOL_ID + "=" + SessionSchema.getSchoolID());
		subject.append(" AND " + ClassRoomLessonPlanSchema.LESSON_PLAN_ID + "=" + lessonPlanId);
		subject.append(" AND " + ClassRoomLessonPlanSchema.ACADEMIC_YEAR + "='" + DateTimeUtil.getBuddishYear() + "')");

		crlContainer = Container.getFreeFormContainer(subject.toString(), ClassRoomSchema.CLASS_ROOM_ID);
		for(final Object itemId:crlContainer.getItemIds()){
			Item item = crlContainer.getItem(itemId);
			addItemData(twinSelect.getLeftTable(), itemId, item);
		}
		
		twinSelect.setLeftCountFooter(ClassRoomSchema.CLASS_YEAR);
	}
	
	private void setRightData(){
		twinSelect.removeAllRightItem();
		
		StringBuilder subject = new StringBuilder();
		subject.append(" SELECT * FROM "+ ClassRoomLessonPlanSchema.TABLE_NAME + " crl");
		subject.append(" INNER JOIN "+ ClassRoomSchema.TABLE_NAME + " cr ON cr." + ClassRoomSchema.CLASS_ROOM_ID + " = crl." + ClassRoomLessonPlanSchema.CLASS_ROOM_ID);
		subject.append(" WHERE crl."+ ClassRoomLessonPlanSchema.SCHOOL_ID + "=" + SessionSchema.getSchoolID());
		subject.append(" AND crl." + ClassRoomLessonPlanSchema.LESSON_PLAN_ID + "=" + lessonPlanId);
		subject.append(" AND crl." + ClassRoomLessonPlanSchema.ACADEMIC_YEAR + "='" + DateTimeUtil.getBuddishYear()+"'");
		
		crlContainer = Container.getFreeFormContainer(subject.toString(), ClassRoomLessonPlanSchema.CLASS_ROOM_LESSON_PLAN_ID);
		for(Object itemId: crlContainer.getItemIds()){
			Item item = crlContainer.getItem(itemId);
			addItemData(twinSelect.getRightTable(), itemId, item);
		}
		
		twinSelect.setRightCountFooter(ClassRoomSchema.CLASS_YEAR);
	}
	
	/* ย้ายข้างจากซ้ายไปขวาจากที่ถูกเลือก */
	@SuppressWarnings("unchecked")
	private void selectData(Object... itemIds){
		try {
			for(Object itemId: itemIds){
				/* ก่อนแก้ไข เพิ่มต้องลบ Filter ก่อนหน้า */
				classRoomLessonPlanContainer.removeAllContainerFilters();
				
				Object tempId = classRoomLessonPlanContainer.addItem();
				
				Item classRoomLessonPlanItem = classRoomLessonPlanContainer.getItem(tempId);
				classRoomLessonPlanItem.getItemProperty(ClassRoomLessonPlanSchema.SCHOOL_ID).setValue(SessionSchema.getSchoolID());
				classRoomLessonPlanItem.getItemProperty(ClassRoomLessonPlanSchema.LESSON_PLAN_ID).setValue(Integer.parseInt(lessonPlanId.toString()));
				classRoomLessonPlanItem.getItemProperty(ClassRoomLessonPlanSchema.CLASS_ROOM_ID).setValue(Integer.parseInt(itemId.toString()));
				classRoomLessonPlanItem.getItemProperty(ClassRoomLessonPlanSchema.ACADEMIC_YEAR).setValue(academicYear.getValue());
				CreateModifiedSchema.setCreateAndModified(classRoomLessonPlanItem);
				classRoomLessonPlanContainer.commit();
			}
			setLeftData();
			setRightData();
			sortData();
			twinSelect.setLeftCountFooter(ClassRoomSchema.CLASS_YEAR);
			twinSelect.setRightCountFooter(ClassRoomSchema.CLASS_YEAR);
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
				classRoomLessonPlanContainer.removeAllContainerFilters();
				
				Object tempId = classRoomLessonPlanContainer.addItem();
				
				Item classRoomLessonPlanItem = classRoomLessonPlanContainer.getItem(tempId);
				classRoomLessonPlanItem.getItemProperty(ClassRoomLessonPlanSchema.SCHOOL_ID).setValue(SessionSchema.getSchoolID());
				classRoomLessonPlanItem.getItemProperty(ClassRoomLessonPlanSchema.LESSON_PLAN_ID).setValue(Integer.parseInt(lessonPlanId.toString()));
				classRoomLessonPlanItem.getItemProperty(ClassRoomLessonPlanSchema.CLASS_ROOM_ID).setValue(Integer.parseInt(itemId.toString()));
				classRoomLessonPlanItem.getItemProperty(ClassRoomLessonPlanSchema.ACADEMIC_YEAR).setValue(DateTimeUtil.getBuddishYear());
				CreateModifiedSchema.setCreateAndModified(classRoomLessonPlanItem);
				classRoomLessonPlanContainer.commit();
			}
			setLeftData();
			setRightData();
			sortData();
			twinSelect.setLeftCountFooter(ClassRoomSchema.CLASS_YEAR);
			twinSelect.setRightCountFooter(ClassRoomSchema.CLASS_YEAR);
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	/* ย้ายข้างจากขวาไปซ้ายจากที่เลือก */
	private void removeData(Object... itemIds){
		try {
			for(Object itemId: itemIds){
				classRoomLessonPlanContainer.removeItem(itemId);
				classRoomLessonPlanContainer.commit();
			}
			setLeftData();
			setRightData();
			sortData();
			twinSelect.setLeftCountFooter(ClassRoomSchema.CLASS_YEAR);
			twinSelect.setRightCountFooter(ClassRoomSchema.CLASS_YEAR);
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/* ย้ายข้างจากขวาไปซ้ายจากจำนวนทั้งหมด */
	private void removeAllData(){
		try {
			for(Object itemId: twinSelect.getRightTable().getItemIds()){
				classRoomLessonPlanContainer.removeItem(itemId);
				classRoomLessonPlanContainer.commit();
			}
			setLeftData();
			setRightData();
			sortData();
			
			twinSelect.setLeftCountFooter(ClassRoomSchema.CLASS_YEAR);
			twinSelect.setRightCountFooter(ClassRoomSchema.CLASS_YEAR);
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/* ใส่ข้อมูลในตาราง */
	private void addItemData(FilterTable table, Object itemId, Item item){
		/* ตรวจสอบข้อมูล หากมาจาก setLeftData , setRightData ค่าจะเป็น int
		 * หากมาจากการย้ายข้าง ข้อมูลจะเป็น String อยู่แล้วไม่จำเป็นต้องมาดึงค่าของตัวแปร Fix 
		 * */
		String classYear = item.getItemProperty(ClassRoomSchema.CLASS_YEAR).getValue().toString();
		
		if(Utility.isInteger(classYear))
			classYear = ClassYear.getNameTh(Integer.parseInt(item.getItemProperty(ClassRoomSchema.CLASS_YEAR).getValue().toString()));
		
		if(table == twinSelect.getLeftTable())
			table.addItem(new Object[] {
					classYear,
					item.getItemProperty(ClassRoomSchema.NUMBER).getValue(), 
					item.getItemProperty(ClassRoomSchema.NAME).getValue()
			},itemId);
		else
			table.addItem(new Object[] {
					classYear,
					item.getItemProperty(ClassRoomSchema.NUMBER).getValue(), 
					item.getItemProperty(ClassRoomSchema.NAME).getValue(),
					item.getItemProperty(ClassRoomLessonPlanSchema.ACADEMIC_YEAR).getValue()
			},itemId);
	}
	
	/* ปุ่มเลือกนักเรียนจากซ้ายไปขวา จากนักเรียนที่เลือก */
	private ClickListener addListener = new ClickListener() {
		private static final long serialVersionUID = 1L;
		@Override
		public void buttonClick(ClickEvent event) {
			if(classYear.getValue() == null){
				Notification.show("กรุณาเลือกชั้นปี", Type.WARNING_MESSAGE);
				return;
			}
			
			if(academicYear.getValue() == null){
				Notification.show("กรุณาเลือกเทอมการศึกษา", Type.WARNING_MESSAGE);
				return;
			}
			
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
			if(classYear.getValue() == null){
				Notification.show("กรุณาเลือกชั้นปี", Type.WARNING_MESSAGE);
				return;
			}
			
			if(academicYear.getValue() == null){
				Notification.show("กรุณาเลือกเทอมการศึกษา", Type.WARNING_MESSAGE);
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
