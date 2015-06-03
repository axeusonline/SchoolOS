package com.ies.schoolos.component.academic;

import java.util.Collection;

import org.tepi.filtertable.FilterTable;
import org.tepi.filtertable.numberfilter.NumberInterval;

import com.ies.schoolos.component.ui.TwinSelectTable;
import com.ies.schoolos.container.Container;
import com.ies.schoolos.filter.TableFilterDecorator;
import com.ies.schoolos.filter.TableFilterGenerator;
import com.ies.schoolos.schema.CreateModifiedSchema;
import com.ies.schoolos.schema.SessionSchema;
import com.ies.schoolos.schema.academic.LessonPlanSubjectSchema;
import com.ies.schoolos.schema.fundamental.SubjectSchema;
import com.ies.schoolos.type.ClassYear;
import com.ies.schoolos.type.Semester;
import com.ies.schoolos.type.dynamic.LessonType;
import com.ies.schoolos.utility.Notification;
import com.ies.schoolos.utility.Utility;
import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.VerticalLayout;

public class AddLessonPlanSubject extends VerticalLayout {

	private static final long serialVersionUID = 1L;

	private Object lessonPlanId;
	private Object classRange;
	
	private Container container = new Container();
	private SQLContainer sContainer;
	private SQLContainer lessonPlanSubjectContainer = container.getLessonPlanSubjectContainer();
	
	private ComboBox classYear;
	private ComboBox semester;
	private TwinSelectTable twinSelect;
	
	public AddLessonPlanSubject(Object lessonPlanId, Object classRange) {
		this.lessonPlanId = lessonPlanId;
		this.classRange = classRange;

		buildMainLayout();
		setLeftData();
		setRightData();
		twinSelect.getRightTable().setFilterFieldValue(LessonPlanSubjectSchema.CLASS_YEAR, new NumberInterval(null, null, "-1"));
		twinSelect.getRightTable().setFilterFieldValue(LessonPlanSubjectSchema.SEMESTER, new NumberInterval(null, null, "-1"));
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
		classYear.setFilteringMode(FilteringMode.CONTAINS);
		classYear.addValueChangeListener(searchValueChange);
		toolStrip.addComponent(classYear);
		toolStrip.setComponentAlignment(classYear, Alignment.MIDDLE_LEFT);
		
		semester = new ComboBox("เทอมการศึกษา",new Semester());
		semester.setInputPrompt("กรุณาเลือก");
		semester.setItemCaptionPropertyId("name");
		semester.setImmediate(true);
		semester.setNullSelectionAllowed(false);
		semester.setRequired(true);
		semester.setWidth("-1px");
		semester.setHeight("-1px");
		semester.setFilteringMode(FilteringMode.CONTAINS);
		semester.addValueChangeListener(searchValueChange);
		toolStrip.addComponent(semester);
		toolStrip.setComponentAlignment(semester, Alignment.MIDDLE_LEFT);
		
		twinSelect = new TwinSelectTable();
		twinSelect.setSizeFull();
		twinSelect.setSpacing(true);
		twinSelect.setSelectable(true);
		twinSelect.setMultiSelect(true);
		twinSelect.showFooterCount(true);
		twinSelect.setFooterUnit("คน");
		
		twinSelect.addContainerProperty(SubjectSchema.CODE, String.class, null);
		twinSelect.addContainerProperty(SubjectSchema.NAME, String.class, null);
		twinSelect.addContainerProperty(SubjectSchema.LESSON_TYPE, String.class, null);
		twinSelect.getRightTable().addContainerProperty(LessonPlanSubjectSchema.CLASS_YEAR, Integer.class, null);
		twinSelect.getRightTable().addContainerProperty(LessonPlanSubjectSchema.SEMESTER, Integer.class, null);
		
		twinSelect.setFilterDecorator(new TableFilterDecorator());
		twinSelect.setFilterGenerator(new TableFilterGenerator());
		twinSelect.setFilterBarVisible(true);
        
		twinSelect.setColumnHeader(SubjectSchema.CODE, "รหัสวิชา");
		twinSelect.setColumnHeader(SubjectSchema.NAME,"ชื่อวิชา");
		twinSelect.setColumnHeader(SubjectSchema.LESSON_TYPE, "สาระการเรียนรู้");
		twinSelect.getRightTable().setColumnHeader(LessonPlanSubjectSchema.CLASS_YEAR, "ชั้นปี");
		twinSelect.getRightTable().setColumnHeader(LessonPlanSubjectSchema.SEMESTER, "เทอมการศึกษา");
		
		twinSelect.getRightTable().setColumnCollapsingAllowed(true);
		twinSelect.getRightTable().setColumnCollapsed(LessonPlanSubjectSchema.CLASS_YEAR, true);
		twinSelect.getRightTable().setColumnCollapsed(LessonPlanSubjectSchema.SEMESTER, true);
		// you can set individual columns non-collapsible
		/*twinSelect.getRightTable().setColumnCollapsible(LessonPlanSubjectSchema.CLASS_YEAR, false);
		twinSelect.getRightTable().setColumnCollapsible(LessonPlanSubjectSchema.SEMESTER, false);*/
		
		twinSelect.getLeftTable().setVisibleColumns(
				SubjectSchema.CODE, 
				SubjectSchema.NAME,
				SubjectSchema.LESSON_TYPE);
		
		twinSelect.getRightTable().setVisibleColumns(
				SubjectSchema.CODE, 
				SubjectSchema.NAME,
				SubjectSchema.LESSON_TYPE,
				LessonPlanSubjectSchema.CLASS_YEAR,
				LessonPlanSubjectSchema.SEMESTER);
		
		twinSelect.setAddClick(addListener);
		twinSelect.setAddAllClick(addAllListener);
		twinSelect.setRemoveClick(removeListener);
		twinSelect.setRemoveAllClick(removeAllListener);
		
		addComponent(twinSelect);
	}
	
	/* เรียงอันดับข้อมูลของตาราง */
	private void sortData(){
		Object[] prop = {SubjectSchema.CODE};
		boolean[] bool = {true};
		twinSelect.getLeftTable().sort(prop, bool);
		twinSelect.getRightTable().sort(prop, bool);
	}
	
	private void setLeftData(){		
		twinSelect.removeAllLeftItem();
		
		StringBuilder subject = new StringBuilder();
		subject.append(" SELECT * FROM " + SubjectSchema.TABLE_NAME);
		subject.append(" WHERE "+ SubjectSchema.SCHOOL_ID + "=" + SessionSchema.getSchoolID());
		subject.append(" AND " + SubjectSchema.SUBJECT_ID + " NOT IN (");
		subject.append(" SELECT "+ LessonPlanSubjectSchema.SUBJECT_ID);
		subject.append(" FROM "+ LessonPlanSubjectSchema.TABLE_NAME);
		subject.append(" WHERE "+ LessonPlanSubjectSchema.SCHOOL_ID + "=" + SessionSchema.getSchoolID());
		subject.append(" AND " + LessonPlanSubjectSchema.CLASS_YEAR + "=" + classYear.getValue());
		subject.append(" AND " + LessonPlanSubjectSchema.SEMESTER + "=" + semester.getValue());
		subject.append(" AND " + LessonPlanSubjectSchema.LESSON_PLAN_ID + "=" + lessonPlanId + ")");

		sContainer = container.getFreeFormContainer(subject.toString(), SubjectSchema.SUBJECT_ID);
		for(final Object itemId:sContainer.getItemIds()){
			Item item = sContainer.getItem(itemId);
			addItemData(twinSelect.getLeftTable(), itemId, item);
		}
		
		twinSelect.setLeftCountFooter(SubjectSchema.CODE);
	}
	
	private void setRightData(){
		twinSelect.removeAllRightItem();
		
		StringBuilder subject = new StringBuilder();
		subject.append(" SELECT * FROM "+ LessonPlanSubjectSchema.TABLE_NAME + " lps");
		subject.append(" INNER JOIN "+ SubjectSchema.TABLE_NAME + " s ON s." + SubjectSchema.SUBJECT_ID + " = lps." + LessonPlanSubjectSchema.SUBJECT_ID);
		subject.append(" WHERE lps."+ LessonPlanSubjectSchema.SCHOOL_ID + "=" + SessionSchema.getSchoolID());
		subject.append(" AND lps." + LessonPlanSubjectSchema.LESSON_PLAN_ID + "=" + lessonPlanId);
		
		sContainer = container.getFreeFormContainer(subject.toString(), LessonPlanSubjectSchema.LESSON_PLAN_SUBJECT_ID);
		for(Object itemId: sContainer.getItemIds()){
			Item item = sContainer.getItem(itemId);
			addItemData(twinSelect.getRightTable(), itemId, item);
		}
		
		twinSelect.setRightCountFooter(SubjectSchema.CODE);
	}
	
	/* ย้ายข้างจากซ้ายไปขวาจากที่ถูกเลือก */
	@SuppressWarnings("unchecked")
	private void selectData(Object... itemIds){
		try {
			for(Object itemId: itemIds){
				/* ก่อนแก้ไข เพิ่มต้องลบ Filter ก่อนหน้า */
				lessonPlanSubjectContainer.removeAllContainerFilters();
				
				Object tempId = lessonPlanSubjectContainer.addItem();
				
				Item lessonPlanSubjectItem = lessonPlanSubjectContainer.getItem(tempId);
				lessonPlanSubjectItem.getItemProperty(LessonPlanSubjectSchema.SCHOOL_ID).setValue(SessionSchema.getSchoolID());
				lessonPlanSubjectItem.getItemProperty(LessonPlanSubjectSchema.LESSON_PLAN_ID).setValue(Integer.parseInt(lessonPlanId.toString()));
				lessonPlanSubjectItem.getItemProperty(LessonPlanSubjectSchema.SUBJECT_ID).setValue(Integer.parseInt(itemId.toString()));
				lessonPlanSubjectItem.getItemProperty(LessonPlanSubjectSchema.CLASS_YEAR).setValue(Integer.parseInt(classYear.getValue().toString()));
				lessonPlanSubjectItem.getItemProperty(LessonPlanSubjectSchema.SEMESTER).setValue(Integer.parseInt(semester.getValue().toString()));
				
				CreateModifiedSchema.setCreateAndModified(lessonPlanSubjectItem);
				lessonPlanSubjectContainer.commit();
			}
			setLeftData();
			setRightData();
			sortData();
			twinSelect.setLeftCountFooter(SubjectSchema.CODE);
			twinSelect.setRightCountFooter(SubjectSchema.CODE);
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
				lessonPlanSubjectContainer.removeAllContainerFilters();
				
				Object tempId = lessonPlanSubjectContainer.addItem();
				
				Item lessonPlanSubjectItem = lessonPlanSubjectContainer.getItem(tempId);
				lessonPlanSubjectItem.getItemProperty(LessonPlanSubjectSchema.SCHOOL_ID).setValue(SessionSchema.getSchoolID());
				lessonPlanSubjectItem.getItemProperty(LessonPlanSubjectSchema.LESSON_PLAN_ID).setValue(Integer.parseInt(lessonPlanId.toString()));
				lessonPlanSubjectItem.getItemProperty(LessonPlanSubjectSchema.SUBJECT_ID).setValue(Integer.parseInt(itemId.toString()));
				lessonPlanSubjectItem.getItemProperty(LessonPlanSubjectSchema.CLASS_YEAR).setValue(Integer.parseInt(classYear.getValue().toString()));
				lessonPlanSubjectItem.getItemProperty(LessonPlanSubjectSchema.SEMESTER).setValue(Integer.parseInt(semester.getValue().toString()));
				
				CreateModifiedSchema.setCreateAndModified(lessonPlanSubjectItem);
				lessonPlanSubjectContainer.commit();
			}
			setLeftData();
			setRightData();
			sortData();
			twinSelect.setLeftCountFooter(SubjectSchema.CODE);
			twinSelect.setRightCountFooter(SubjectSchema.CODE);
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	/* ย้ายข้างจากขวาไปซ้ายจากที่เลือก */
	private void removeData(Object... itemIds){
		try {
			for(Object itemId: itemIds){
				lessonPlanSubjectContainer.removeItem(itemId);
				lessonPlanSubjectContainer.commit();
			}
			setLeftData();
			setRightData();
			sortData();
			twinSelect.setLeftCountFooter(SubjectSchema.CODE);
			twinSelect.setRightCountFooter(SubjectSchema.CODE);
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/* ย้ายข้างจากขวาไปซ้ายจากจำนวนทั้งหมด */
	private void removeAllData(){
		try {
			for(Object itemId: twinSelect.getRightTable().getItemIds()){
				lessonPlanSubjectContainer.removeItem(itemId);
				lessonPlanSubjectContainer.commit();
			}
			setLeftData();
			setRightData();
			sortData();
			
			twinSelect.setLeftCountFooter(SubjectSchema.CODE);
			twinSelect.setRightCountFooter(SubjectSchema.CODE);
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/* ใส่ข้อมูลในตาราง */
	private void addItemData(FilterTable table, Object itemId, Item item){
		/* ตรวจสอบข้อมูล หากมาจาก setLeftData , setRightData ค่าจะเป็น int
		 * หากมาจากการย้ายข้าง ข้อมูลจะเป็น String อยู่แล้วไม่จำเป็นต้องมาดึงค่าของตัวแปร Fix 
		 * */
		String lessonType = item.getItemProperty(SubjectSchema.LESSON_TYPE).getValue().toString();
		
		if(Utility.isInteger(lessonType))
			lessonType = LessonType.getNameTh(Integer.parseInt(item.getItemProperty(SubjectSchema.LESSON_TYPE).getValue().toString()));
		
		if(table == twinSelect.getLeftTable())
			table.addItem(new Object[] {
					item.getItemProperty(SubjectSchema.CODE).getValue(), 
					item.getItemProperty(SubjectSchema.NAME).getValue(), 
					lessonType
			},itemId);
		else
			table.addItem(new Object[] {
					item.getItemProperty(SubjectSchema.CODE).getValue(), 
					item.getItemProperty(SubjectSchema.NAME).getValue(), 
					lessonType,
					item.getItemProperty(LessonPlanSubjectSchema.CLASS_YEAR).getValue(), 
					item.getItemProperty(LessonPlanSubjectSchema.SEMESTER).getValue(),
			},itemId);
	}
	
	/* ค้นหานักเรียนตามเงื่อนไขที่เลือก */
	private ValueChangeListener searchValueChange = new ValueChangeListener() {
		private static final long serialVersionUID = 1L;

		@Override
		public void valueChange(ValueChangeEvent event) {
			if(classYear.getValue() != null &&
					semester.getValue() != null){
				setLeftData();
				
				twinSelect.getRightTable().setFilterFieldValue(LessonPlanSubjectSchema.CLASS_YEAR, new NumberInterval(null, null, classYear.getValue().toString()));
				twinSelect.getRightTable().setFilterFieldValue(LessonPlanSubjectSchema.SEMESTER, new NumberInterval(null, null, semester.getValue().toString()));

				twinSelect.setLeftCountFooter(SubjectSchema.CODE);
				twinSelect.setRightCountFooter(SubjectSchema.CODE);
			}
		}
	};
	
	/* ปุ่มเลือกนักเรียนจากซ้ายไปขวา จากนักเรียนที่เลือก */
	private ClickListener addListener = new ClickListener() {
		private static final long serialVersionUID = 1L;
		@Override
		public void buttonClick(ClickEvent event) {
			if(classYear.getValue() == null){
				Notification.show("กรุณาเลือกชั้นปี", Type.WARNING_MESSAGE);
				return;
			}
			
			if(semester.getValue() == null){
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
			
			if(semester.getValue() == null){
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
