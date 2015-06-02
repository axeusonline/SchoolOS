package com.ies.schoolos.component.academic;

import java.util.Collection;

import org.tepi.filtertable.FilterTable;
import org.tepi.filtertable.numberfilter.NumberInterval;

import com.ies.schoolos.component.ui.SchoolOSLayout;
import com.ies.schoolos.component.ui.TwinSelectTable;
import com.ies.schoolos.container.Container;
import com.ies.schoolos.filter.TableFilterDecorator;
import com.ies.schoolos.filter.TableFilterGenerator;
import com.ies.schoolos.schema.CreateModifiedSchema;
import com.ies.schoolos.schema.SessionSchema;
import com.ies.schoolos.schema.academic.TeachingSchema;
import com.ies.schoolos.schema.fundamental.SubjectSchema;
import com.ies.schoolos.schema.info.PersonnelSchema;
import com.ies.schoolos.type.Days;
import com.ies.schoolos.type.dynamic.Subject;
import com.ies.schoolos.type.dynamic.Teaching;
import com.ies.schoolos.utility.DateTimeUtil;
import com.ies.schoolos.utility.Notification;
import com.ies.schoolos.utility.Utility;
import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.filter.And;
import com.vaadin.data.util.filter.Compare.Equal;
import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Notification.Type;

public class TeachingView extends SchoolOSLayout {

	private static final long serialVersionUID = 1L;
	
	private SQLContainer freeContainer;
	private SQLContainer teachingContainer;

	private ComboBox subject;
	private Button addition;
	private Button weekend;
	private TwinSelectTable twinSelect;
	
	public TeachingView() {
		teachingContainer = container.getTeachingContainer();
		teachingContainer.refresh();
		
		setSizeFull();
		setSpacing(true);
		setMargin(true);
		buildMainLayout();
		setLeftData();
		setRightData();
	}
	
	private void buildMainLayout(){
		/* Toolbar */
		HorizontalLayout toolStrip = new HorizontalLayout();
		//toolStrip.setWidth("100%");
		toolStrip.setHeight("90px");
		toolStrip.setStyleName("border-white");
		toolStrip.setSpacing(true);
		addComponent(toolStrip);
		
		subject = new ComboBox("รายวิชา",new Subject());
		subject.setInputPrompt("กรุณาเลือก");
		subject.setItemCaptionPropertyId("name");
		subject.setImmediate(true);
        subject.setNullSelectionAllowed(false);
        subject.setRequired(true);
		subject.setWidth("-1px");
		subject.setHeight("-1px");
		subject.setFilteringMode(FilteringMode.CONTAINS);
		subject.addValueChangeListener(searchValueChange);
		toolStrip.addComponent(subject);
		toolStrip.setComponentAlignment(subject, Alignment.MIDDLE_LEFT);
		
		addition = new Button("เพิ่มอาจารย์ชั่วคราว", FontAwesome.USER);
		addition.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event) {
				if(subject.getValue() != null){
					final Window window = new Window();
					window.setSizeUndefined();
					window.center();
					UI.getCurrent().addWindow(window);
					
					FormLayout form = new FormLayout();
					form.setSizeUndefined();
					form.setMargin(true);
					window.setContent(form);
					
					final TextField name = new TextField("ชื่ออาจารย์ผู้สอน");
					name.setInputPrompt("ชื่อ-สกุล");
					name.setImmediate(false);
					name.setRequired(true);
					name.setWidth("-1px");
					name.setHeight("-1px");
					name.setNullRepresentation("");
					form.addComponent(name);
					
					Button save = new Button("บันทึก", FontAwesome.SAVE);
					save.addClickListener(new ClickListener() {
						private static final long serialVersionUID = 1L;

						@SuppressWarnings("unchecked")
						@Override
						public void buttonClick(ClickEvent event) {
							if(name.isValid()){
								try {
									if(name.getValue().split(" ").length > 1){
										teachingContainer.addContainerFilter(new And(
											new Equal(TeachingSchema.SCHOOL_ID,SessionSchema.getSchoolID())	,
											new Equal(TeachingSchema.SUBJECT_ID,Integer.parseInt(subject.getValue().toString())),
											new Equal(TeachingSchema.PERSONNEL_NAME_TMP,name.getValue())));
										
										if(teachingContainer.size() > 0){
											Notification.show("อาจารย์ซ้ำ กรุณาตรวจสอบชื่ออีกครั้ง", Type.WARNING_MESSAGE);
											return;
										}
										
										/* ก่อนแก้ไข เพิ่มต้องลบ Filter ก่อนหน้า */
										teachingContainer.removeAllContainerFilters();
										
										Object tempId = teachingContainer.addItem();
										
										Item teachingItem = teachingContainer.getItem(tempId);
										teachingItem.getItemProperty(TeachingSchema.SCHOOL_ID).setValue(SessionSchema.getSchoolID());
										teachingItem.getItemProperty(TeachingSchema.SUBJECT_ID).setValue(Integer.parseInt(subject.getValue().toString()));
										teachingItem.getItemProperty(TeachingSchema.PERSONNEL_NAME_TMP).setValue(name.getValue());
										teachingItem.getItemProperty(TeachingSchema.ACADEMIC_YEAR).setValue(DateTimeUtil.getBuddishYear());
										
										CreateModifiedSchema.setCreateAndModified(teachingItem);
										teachingContainer.commit();
										
										setRightData();
										sortData();
										twinSelect.setLeftCountFooter(PersonnelSchema.PERSONNEL_CODE);
										twinSelect.setRightCountFooter(PersonnelSchema.PERSONNEL_CODE);
										
										window.close();
									}else{
										Notification.show("กรุณาระบุ ชื่อ นามสกุล ให้ถูกต้อง", Type.WARNING_MESSAGE);
									}
									
								}catch (Exception e) {
									Notification.show("บันทึกไม่สำเร็จ", Type.WARNING_MESSAGE);
									e.printStackTrace();
								}
							}else{
								Notification.show("กรุณากรอกข้อมูลให้ครบถ้วน", Type.WARNING_MESSAGE);
							}
						}
					});
					form.addComponent(save);
				}else{
					Notification.show("กรุณาเลือกรายวิชา", Type.WARNING_MESSAGE);
				}
				
			}
		});
		toolStrip.addComponent(addition);
		toolStrip.setComponentAlignment(addition, Alignment.MIDDLE_LEFT);
		
		weekend  = new Button("วันหยุดรายบุคคล", FontAwesome.CALENDAR);
		weekend.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 1L;
			
			private String FIELD_NAME = "name";
			
			private Teaching remainTeaching = new Teaching(null);
			private OptionGroup days;
			private TwinSelectTable weekendTwinSelect;
			
			@Override
			public void buttonClick(ClickEvent event) {

				final Window window = new Window("กำหนดวันหยุด");
				window.setSizeFull();
				window.center();
				UI.getCurrent().addWindow(window);
				
				VerticalLayout weekendLayout = new VerticalLayout();
				weekendLayout.setSizeFull();
				weekendLayout.setSpacing(true);
				weekendLayout.setMargin(true);
				window.setContent(weekendLayout);

				days = new OptionGroup("เลือกวันหยุด", new Days());
				days.setMultiSelect(true);
				days.setRequired(true);
				days.setItemCaptionPropertyId("name");
		        days.setNullSelectionAllowed(false);
		        days.setHtmlContentAllowed(true);
		        days.setImmediate(true);
		        days.addValueChangeListener(new ValueChangeListener() {
					private static final long serialVersionUID = 1L;

					@Override
					public void valueChange(ValueChangeEvent event) {
						setRightData();
					}
				});
		        weekendLayout.addComponent(days);
		        
		        weekendTwinSelect = new TwinSelectTable();
		        weekendTwinSelect.setSizeFull();
		        weekendTwinSelect.setSpacing(true);
		        weekendTwinSelect.setSelectable(true);
		        weekendTwinSelect.setMultiSelect(true);
		        weekendTwinSelect.showFooterCount(true);
		        weekendTwinSelect.setFooterUnit("คน");
				
		        weekendTwinSelect.addContainerProperty(FIELD_NAME, String.class, null);
		        
		        weekendTwinSelect.setFilterDecorator(new TableFilterDecorator());
		        weekendTwinSelect.setFilterGenerator(new TableFilterGenerator());
		        weekendTwinSelect.setFilterBarVisible(true);
		        
		        weekendTwinSelect.setColumnHeader(FIELD_NAME, "อาจารย์ผู้สอน");
		        
		        weekendTwinSelect.setVisibleColumns(FIELD_NAME);

		        weekendTwinSelect.setAddClick(addWeekendListener);
		        weekendTwinSelect.setAddAllClick(addAllWeekendListener);
		        weekendTwinSelect.setRemoveClick(removeWeekendListener);
		        weekendTwinSelect.setRemoveAllClick(removeAllWeekendListener);
				
		        weekendLayout.addComponent(weekendTwinSelect);
		        weekendLayout.setExpandRatio(weekendTwinSelect, 1);
		        
		        setLeftData();	
			}
		
			/* จำนวนนักเรียนทีี่ค้นฟา */
			private void setLeftData(){
				weekendTwinSelect.removeAllLeftItem();
				
				for(final Object itemId:remainTeaching.getItemIds()){
					Item item = remainTeaching.getItem(itemId);
					addItemData(weekendTwinSelect.getLeftTable(), itemId, item);
				}
				
				weekendTwinSelect.setLeftCountFooter(FIELD_NAME);
			}
			
			/* จำนวนนักเรียนที่ถูกเลือก */
			private void setRightData(){
				weekendTwinSelect.removeAllRightItem();

				for(final Object itemId:new Teaching(Utility.sortOptionGroup(days.getValue())).getItemIds()){
					Item item = new Teaching().getItem(itemId);
					addItemData(weekendTwinSelect.getRightTable(), itemId, item);
				}
				
				weekendTwinSelect.setRightCountFooter(FIELD_NAME);
			}
			
			/* ย้ายข้างจากซ้ายไปขวาจากที่ถูกเลือก */
			@SuppressWarnings("unchecked")
			private void selectData(Object... itemIds){
				try {
					for(Object itemId: itemIds){
						Item leftData = weekendTwinSelect.getLeftTable().getItem(itemId);
						addItemData(weekendTwinSelect.getRightTable(), itemId, leftData);
						weekendTwinSelect.getLeftTable().removeItem(itemId);
						
						Item studentItem = teachingContainer.getItem(itemId);
						studentItem.getItemProperty(TeachingSchema.WEEKEND).setValue(Utility.sortOptionGroup(days.getValue()));

						teachingContainer.commit();
					}
					sortWeekendData();
					weekendTwinSelect.setLeftCountFooter(FIELD_NAME);
					weekendTwinSelect.setRightCountFooter(FIELD_NAME);
				}catch (Exception e) {
					e.printStackTrace();
				}
				
			}
			
			/* ย้ายข้างจากซ้ายไปขวาจากทั้งหมด*/
			@SuppressWarnings("unchecked")
			private void selectAllData(){
				try {
					Collection<?> itemIds = weekendTwinSelect.getLeftTable().getItemIds();
					for(Object itemId: itemIds){
						Item item = weekendTwinSelect.getLeftTable().getItem(itemId);
						addItemData(weekendTwinSelect.getRightTable(), itemId, item);
						
						Item studentItem = teachingContainer.getItem(itemId);
						studentItem.getItemProperty(TeachingSchema.WEEKEND).setValue(Utility.sortOptionGroup(days.getValue()));
						teachingContainer.commit();
					}
					weekendTwinSelect.getLeftTable().removeAllItems();
					sortWeekendData();
					weekendTwinSelect.setLeftCountFooter(FIELD_NAME);
					weekendTwinSelect.setRightCountFooter(FIELD_NAME);
				}catch (Exception e) {
					e.printStackTrace();
				}
			}

			/* ย้ายข้างจากขวาไปซ้ายจากที่เลือก */
			@SuppressWarnings("unchecked")
			private void removeData(Object... itemIds){
				try {
					for(Object itemId: itemIds){
							Item item = weekendTwinSelect.getRightTable().getItem(itemId);
							addItemData(weekendTwinSelect.getLeftTable(), itemId, item);
							weekendTwinSelect.getRightTable().removeItem(itemId);	
							
							Item studentItem = teachingContainer.getItem(itemId);
							studentItem.getItemProperty(TeachingSchema.WEEKEND).setValue(null);
							teachingContainer.commit();
					}
					sortWeekendData();
					weekendTwinSelect.setLeftCountFooter(FIELD_NAME);
					weekendTwinSelect.setRightCountFooter(FIELD_NAME);
				}catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			/* ย้ายข้างจากขวาไปซ้ายจากจำนวนทั้งหมด */
			@SuppressWarnings("unchecked")
			private void removeAllData(){
				try {
					for(Object itemId: weekendTwinSelect.getRightTable().getItemIds()){
						Item item = weekendTwinSelect.getRightTable().getItem(itemId);
						addItemData(weekendTwinSelect.getLeftTable(), itemId, item);
						
						Item studentItem = teachingContainer.getItem(itemId);
						studentItem.getItemProperty(TeachingSchema.WEEKEND).setValue(null);
						teachingContainer.commit();
					}
						
					weekendTwinSelect.getRightTable().removeAllItems();
					sortWeekendData();
					weekendTwinSelect.setLeftCountFooter(FIELD_NAME);
					weekendTwinSelect.setRightCountFooter(FIELD_NAME);
				}catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			/* ใส่ข้อมูลในตาราง */
			private void addItemData(FilterTable table, Object itemId, Item item){
				table.addItem(new Object[] {
					item.getItemProperty(FIELD_NAME).getValue()
				},itemId);
			}
			
			/* เรียงอันดับข้อมูลของตาราง */
			private void sortWeekendData(){
				Object[] propLeft = {FIELD_NAME};
				boolean[] boolLeft = {false};
				twinSelect.getLeftTable().sort(propLeft, boolLeft);
				
				Object[] propRight = {FIELD_NAME};
				boolean[] boolRight = {true};
				twinSelect.getRightTable().sort(propRight, boolRight);
			}
			
			/* ปุ่มเลือกนักเรียนจากซ้ายไปขวา จากนักเรียนที่เลือก */
			private ClickListener addWeekendListener = new ClickListener() {
				private static final long serialVersionUID = 1L;
				@Override
				public void buttonClick(ClickEvent event) {
					if(days.getValue() == null){
						Notification.show("กรุณาระบุวันหยุด", Type.WARNING_MESSAGE);
						return;
					}
					Collection<?> itemIds = (Collection<?>)weekendTwinSelect.getLeftTable().getValue();
					for(Object itemId:itemIds){
						selectData(itemId);
					}
				}
			};
			
			/* ปุ่มเลือกนักเรียนจากซ้ายไปขวา จากนักเรียนทั้งหมด */
			private ClickListener addAllWeekendListener = new ClickListener() {
				private static final long serialVersionUID = 1L;
				@Override
				public void buttonClick(ClickEvent event) {
					if(days.getValue() == null){
						Notification.show("กรุณาระบุวันหยุด", Type.WARNING_MESSAGE);
						return;
					}
					selectAllData();
				}
			};
			
			/* ปุ่มเลือกนักเรียนจากขวาไปซ้าย จากนักเรียนที่เลือก */
			private ClickListener removeWeekendListener = new ClickListener() {
				private static final long serialVersionUID = 1L;
				@Override
				public void buttonClick(ClickEvent event) {
					for(Object itemId:(Collection<?>)
						weekendTwinSelect.getRightTable().getValue()){
						removeData(itemId);
					}
				}
			};
			
			/* ปุ่มเลือกนักเรียนจากขวาไปซ้าย จากนักเรียนทั้งหมด */
			private ClickListener removeAllWeekendListener = new ClickListener() {
				private static final long serialVersionUID = 1L;
				@Override
				public void buttonClick(ClickEvent event) {
					removeAllData();
				}
			};
		});
		toolStrip.addComponent(weekend);
		toolStrip.setComponentAlignment(weekend, Alignment.MIDDLE_LEFT);
		
		twinSelect = new TwinSelectTable();
		twinSelect.setSizeFull();
		twinSelect.setSpacing(true);
		twinSelect.setSelectable(true);
		twinSelect.setMultiSelect(true);
		twinSelect.showFooterCount(true);
		twinSelect.setFooterUnit("คน");
		
		twinSelect.addContainerProperty(PersonnelSchema.PERSONNEL_CODE, String.class, null);
		twinSelect.addContainerProperty(PersonnelSchema.FIRSTNAME, String.class, null);
		twinSelect.addContainerProperty(PersonnelSchema.LASTNAME, String.class, null);
		twinSelect.getRightTable().addContainerProperty(TeachingSchema.ACADEMIC_YEAR, String.class, null);
		twinSelect.getRightTable().addContainerProperty(TeachingSchema.SUBJECT_ID, Integer.class, null);
		
		twinSelect.setFilterDecorator(new TableFilterDecorator());
		twinSelect.setFilterGenerator(new TableFilterGenerator());
		twinSelect.setFilterBarVisible(true);
        
		twinSelect.setColumnHeader(PersonnelSchema.PERSONNEL_CODE, "รหัสประจำตัว");
		twinSelect.setColumnHeader(PersonnelSchema.FIRSTNAME,"ชื่อ");
		twinSelect.setColumnHeader(PersonnelSchema.LASTNAME, "สกุล");
		twinSelect.getRightTable().setColumnHeader(TeachingSchema.ACADEMIC_YEAR, "ปีการศึกษา");
		twinSelect.getRightTable().setColumnHeader(TeachingSchema.SUBJECT_ID, "รายวิชา");
		
		twinSelect.getLeftTable().setVisibleColumns(
				PersonnelSchema.PERSONNEL_CODE, 
				PersonnelSchema.FIRSTNAME,
				PersonnelSchema.LASTNAME);
		
		twinSelect.getRightTable().setVisibleColumns(
				PersonnelSchema.PERSONNEL_CODE, 
				PersonnelSchema.FIRSTNAME,
				PersonnelSchema.LASTNAME,
				TeachingSchema.ACADEMIC_YEAR,
				TeachingSchema.SUBJECT_ID);
		
		twinSelect.getRightTable().setColumnCollapsingAllowed(true);
		twinSelect.getRightTable().setColumnCollapsed(TeachingSchema.SUBJECT_ID, true);
		
		twinSelect.setAddClick(addListener);
		twinSelect.setAddAllClick(addAllListener);
		twinSelect.setRemoveClick(removeListener);
		twinSelect.setRemoveAllClick(removeAllListener);
		
		addComponent(twinSelect);
		setExpandRatio(twinSelect, 1);
	}
	
	/* เรียงอันดับข้อมูลของตาราง */
	private void sortData(){
		Object[] prop = {PersonnelSchema.PERSONNEL_CODE};
		boolean[] bool = {true};
		twinSelect.getLeftTable().sort(prop, bool);
		twinSelect.getRightTable().sort(prop, bool);
	}
	
	private void setLeftData(){		
		twinSelect.removeAllLeftItem();
		
		StringBuilder subjectBuilder = new StringBuilder();
		subjectBuilder.append(" SELECT * FROM " + PersonnelSchema.TABLE_NAME);
		subjectBuilder.append(" WHERE "+ PersonnelSchema.SCHOOL_ID + "=" + SessionSchema.getSchoolID());
		subjectBuilder.append(" AND "+ PersonnelSchema.PERSONNEL_ID + " NOT IN (");
		subjectBuilder.append(" SELECT "+ TeachingSchema.PERSONNEL_ID + " FROM " + TeachingSchema.TABLE_NAME);
		subjectBuilder.append(" WHERE "+ TeachingSchema.SCHOOL_ID + "=" + SessionSchema.getSchoolID());
		subjectBuilder.append(" AND "+ TeachingSchema.SUBJECT_ID + "=" + subject.getValue());
		subjectBuilder.append(" AND "+ TeachingSchema.ACADEMIC_YEAR + "='" + DateTimeUtil.getBuddishYear() + "'");
		subjectBuilder.append(" AND "+ TeachingSchema.PERSONNEL_ID + " IS NOT NULL )");
		
		freeContainer = Container.getFreeFormContainer(subjectBuilder.toString(), PersonnelSchema.PERSONNEL_ID);
		for(final Object itemId:freeContainer.getItemIds()){
			Item item = freeContainer.getItem(itemId);
			addItemData(twinSelect.getLeftTable(), itemId, item);
		}
		
		twinSelect.setLeftCountFooter(PersonnelSchema.PERSONNEL_CODE);
	}
	
	private void setRightData(){
		twinSelect.removeAllRightItem();
		
		StringBuilder teachingBuilder = new StringBuilder();
		teachingBuilder.append(" SELECT * FROM "+ TeachingSchema.TABLE_NAME + " tc");
		teachingBuilder.append(" INNER JOIN "+ SubjectSchema.TABLE_NAME + " s ON s." + SubjectSchema.SUBJECT_ID + " = tc." + TeachingSchema.SUBJECT_ID);
		teachingBuilder.append(" WHERE tc."+ TeachingSchema.SCHOOL_ID + "=" + SessionSchema.getSchoolID());
		teachingBuilder.append(" AND tc." + TeachingSchema.ACADEMIC_YEAR + "='" + DateTimeUtil.getBuddishYear()+"'");

		freeContainer = Container.getFreeFormContainer(teachingBuilder.toString(), TeachingSchema.TEACHING_ID);
		for(Object itemId: freeContainer.getItemIds()){
			Item item = freeContainer.getItem(itemId);
			addItemData(twinSelect.getRightTable(), itemId, item);
		}
		
		twinSelect.setRightCountFooter(PersonnelSchema.PERSONNEL_CODE);
		
		if(subject.getValue() == null){
			twinSelect.getRightTable().setFilterFieldValue(TeachingSchema.SUBJECT_ID, new NumberInterval(null, null, "0"));
			
			twinSelect.setLeftCountFooter(PersonnelSchema.PERSONNEL_CODE);
			twinSelect.setRightCountFooter(PersonnelSchema.PERSONNEL_CODE);
		}
			
	}
	
	/* ย้ายข้างจากซ้ายไปขวาจากที่ถูกเลือก */
	@SuppressWarnings("unchecked")
	private void selectData(Object... itemIds){
		try {
			for(Object itemId: itemIds){
				/* ก่อนแก้ไข เพิ่มต้องลบ Filter ก่อนหน้า */
				teachingContainer.removeAllContainerFilters();
				
				Object tempId = teachingContainer.addItem();
				
				Item teachingItem = teachingContainer.getItem(tempId);
				teachingItem.getItemProperty(TeachingSchema.SCHOOL_ID).setValue(SessionSchema.getSchoolID());
				teachingItem.getItemProperty(TeachingSchema.SUBJECT_ID).setValue(Integer.parseInt(subject.getValue().toString()));
				teachingItem.getItemProperty(TeachingSchema.PERSONNEL_ID).setValue(Integer.parseInt(itemId.toString()));
				teachingItem.getItemProperty(TeachingSchema.ACADEMIC_YEAR).setValue(DateTimeUtil.getBuddishYear());
				
				CreateModifiedSchema.setCreateAndModified(teachingItem);
				teachingContainer.commit();
			}
			setLeftData();
			setRightData();
			sortData();
			twinSelect.setLeftCountFooter(PersonnelSchema.PERSONNEL_CODE);
			twinSelect.setRightCountFooter(PersonnelSchema.PERSONNEL_CODE);
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
				teachingContainer.removeAllContainerFilters();
				
				Object tempId = teachingContainer.addItem();
				
				Item teachingItem = teachingContainer.getItem(tempId);
				teachingItem.getItemProperty(TeachingSchema.SCHOOL_ID).setValue(SessionSchema.getSchoolID());
				teachingItem.getItemProperty(TeachingSchema.SUBJECT_ID).setValue(Integer.parseInt(subject.getValue().toString()));
				teachingItem.getItemProperty(TeachingSchema.PERSONNEL_ID).setValue(Integer.parseInt(itemId.toString()));
				teachingItem.getItemProperty(TeachingSchema.ACADEMIC_YEAR).setValue(DateTimeUtil.getBuddishYear());
				
				CreateModifiedSchema.setCreateAndModified(teachingItem);
				teachingContainer.commit();
			}
			setLeftData();
			setRightData();
			sortData();
			twinSelect.setLeftCountFooter(PersonnelSchema.PERSONNEL_CODE);
			twinSelect.setRightCountFooter(PersonnelSchema.PERSONNEL_CODE);
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	/* ย้ายข้างจากขวาไปซ้ายจากที่เลือก */
	private void removeData(Object... itemIds){
		try {
			for(Object itemId: itemIds){
				teachingContainer.removeItem(itemId);
				teachingContainer.commit();
			}
			setLeftData();
			setRightData();
			sortData();
			twinSelect.setLeftCountFooter(PersonnelSchema.PERSONNEL_CODE);
			twinSelect.setRightCountFooter(PersonnelSchema.PERSONNEL_CODE);
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/* ย้ายข้างจากขวาไปซ้ายจากจำนวนทั้งหมด */
	private void removeAllData(){
		try {
			for(Object itemId: twinSelect.getRightTable().getItemIds()){
				teachingContainer.removeItem(itemId);
				teachingContainer.commit();
			}
			setLeftData();
			setRightData();
			sortData();
			
			twinSelect.setLeftCountFooter(PersonnelSchema.PERSONNEL_CODE);
			twinSelect.setRightCountFooter(PersonnelSchema.PERSONNEL_CODE);
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/* ใส่ข้อมูลในตาราง */
	private void addItemData(FilterTable table, Object itemId, Item item){		
		if(table == twinSelect.getLeftTable()){
			table.addItem(new Object[] {
					item.getItemProperty(PersonnelSchema.PERSONNEL_CODE).getValue(), 
					item.getItemProperty(PersonnelSchema.FIRSTNAME).getValue(), 
					item.getItemProperty(PersonnelSchema.LASTNAME).getValue()
			},itemId);
		}else{
			Object personalCode = null;
			Object firstname = null;
			Object lastname = null;
			
			/* ตรวจสอบว่า เป็นอาจารย์ชั่วคราวไหม ถ้าใช่ แสดงว่า personnel_id = null จึงต้องดึงจากชื่อ Tmp มาแสดงแทน */
			if(item.getItemProperty(TeachingSchema.PERSONNEL_ID).getValue() == null){
				String[] nameTmp = item.getItemProperty(TeachingSchema.PERSONNEL_NAME_TMP).getValue().toString().split(" ");
				firstname = nameTmp[0];
				lastname = nameTmp[1];
			}else{
				try {
					StringBuilder builder = new StringBuilder();
					builder.append(" SELECT " + PersonnelSchema.PERSONNEL_ID + "," + PersonnelSchema.PERSONNEL_CODE + "," + PersonnelSchema.FIRSTNAME + "," + PersonnelSchema.LASTNAME);
					builder.append(" FROM " + PersonnelSchema.TABLE_NAME);
					builder.append(" WHERE " + PersonnelSchema.PERSONNEL_ID + "=" + item.getItemProperty(TeachingSchema.PERSONNEL_ID).getValue());

					SQLContainer freeContainer = Container.getFreeFormContainer(builder.toString(), PersonnelSchema.PERSONNEL_ID);

					Item personnelItem = freeContainer.getItem(freeContainer.getIdByIndex(0));
					personalCode = personnelItem.getItemProperty(PersonnelSchema.PERSONNEL_CODE).getValue();
					firstname = personnelItem.getItemProperty(PersonnelSchema.FIRSTNAME).getValue();
					lastname = personnelItem.getItemProperty(PersonnelSchema.LASTNAME).getValue();
				} catch (Exception e) {
					e.printStackTrace();
				}		
			}
			table.addItem(new Object[] {
					personalCode, 
					firstname, 
					lastname,
					item.getItemProperty(TeachingSchema.ACADEMIC_YEAR).getValue(),
					item.getItemProperty(TeachingSchema.SUBJECT_ID).getValue()
			},itemId);
		}
	}
	
	/* ค้นหานักเรียนตามเงื่อนไขที่เลือก */
	private ValueChangeListener searchValueChange = new ValueChangeListener() {
		private static final long serialVersionUID = 1L;

		@Override
		public void valueChange(ValueChangeEvent event) {
			if(subject.getValue() != null){
				twinSelect.getRightTable().setFilterFieldValue(TeachingSchema.SUBJECT_ID, new NumberInterval(null, null, subject.getValue().toString()));
				setLeftData();
				
				twinSelect.setLeftCountFooter(PersonnelSchema.PERSONNEL_CODE);
				twinSelect.setRightCountFooter(PersonnelSchema.PERSONNEL_CODE);
			}
		}
	};
	
	/* ปุ่มเลือกนักเรียนจากซ้ายไปขวา จากนักเรียนที่เลือก */
	private ClickListener addListener = new ClickListener() {
		private static final long serialVersionUID = 1L;
		@Override
		public void buttonClick(ClickEvent event) {
			if(subject.getValue() == null){
				Notification.show("กรุณาเลือกรายวิชา", Type.WARNING_MESSAGE);
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
			if(subject.getValue() == null){
				Notification.show("กรุณาเลือกรายวิชา", Type.WARNING_MESSAGE);
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
