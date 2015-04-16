package com.ies.schoolos.component.academic;

import java.util.Collection;

import org.tepi.filtertable.FilterTable;
import org.tepi.filtertable.numberfilter.NumberInterval;

import com.ies.schoolos.component.ui.TwinSelectTable;
import com.ies.schoolos.container.Container;
import com.ies.schoolos.container.DbConnection;
import com.ies.schoolos.filter.TableFilterDecorator;
import com.ies.schoolos.filter.TableFilterGenerator;
import com.ies.schoolos.schema.CreateModifiedSchema;
import com.ies.schoolos.schema.SessionSchema;
import com.ies.schoolos.schema.academic.TeachingSchema;
import com.ies.schoolos.schema.fundamental.SubjectSchema;
import com.ies.schoolos.schema.info.PersonnelSchema;
import com.ies.schoolos.type.dynamic.Subject;
import com.ies.schoolos.utility.DateTimeUtil;
import com.ies.schoolos.utility.Notification;
import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.data.util.sqlcontainer.query.FreeformQuery;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Notification.Type;

public class TeachingView extends VerticalLayout {

	private static final long serialVersionUID = 1L;
	
	private SQLContainer tContainer;
	private SQLContainer teachingContainer = Container.getInstance().getTeachingContainer();

	private ComboBox subject;
	private Button addition;
	private TwinSelectTable twinSelect;
	
	public TeachingView() {
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
		
		addition = new Button("เพิ่มอาจารย์พิเศษ", FontAwesome.USER);
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
									/* ก่อนแก้ไข เพิ่มต้องลบ Filter ก่อนหน้า */
									teachingContainer.removeAllContainerFilters();
									
									Object tempId = teachingContainer.addItem();
									
									Item teachingItem = teachingContainer.getItem(tempId);
									teachingItem.getItemProperty(TeachingSchema.SCHOOL_ID).setValue(UI.getCurrent().getSession().getAttribute(SessionSchema.SCHOOL_ID));
									teachingItem.getItemProperty(TeachingSchema.SUBJECT_ID).setValue(Integer.parseInt(subject.getValue().toString()));
									teachingItem.getItemProperty(TeachingSchema.PERSONNEL_NAME_TMP).setValue(name.getValue());
									teachingItem.getItemProperty(TeachingSchema.ACADEMIC_YEAR).setValue(DateTimeUtil.getBuddishYear());
									
									CreateModifiedSchema.setCreateAndModified(teachingItem);
									teachingContainer.commit();
									
									setRightData();
									sortData();
									twinSelect.setLeftCountFooter(PersonnelSchema.PERSONEL_CODE);
									twinSelect.setRightCountFooter(PersonnelSchema.PERSONEL_CODE);
									
									window.close();
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
		
		twinSelect = new TwinSelectTable();
		twinSelect.setSizeFull();
		twinSelect.setSpacing(true);
		twinSelect.setSelectable(true);
		twinSelect.setMultiSelect(true);
		twinSelect.showFooterCount(true);
		twinSelect.setFooterUnit("คน");
		
		twinSelect.addContainerProperty(PersonnelSchema.PERSONEL_CODE, String.class, null);
		twinSelect.addContainerProperty(PersonnelSchema.FIRSTNAME, String.class, null);
		twinSelect.addContainerProperty(PersonnelSchema.LASTNAME, String.class, null);
		twinSelect.getRightTable().addContainerProperty(TeachingSchema.ACADEMIC_YEAR, String.class, null);
		twinSelect.getRightTable().addContainerProperty(TeachingSchema.SUBJECT_ID, Integer.class, null);
		
		twinSelect.setFilterDecorator(new TableFilterDecorator());
		twinSelect.setFilterGenerator(new TableFilterGenerator());
		twinSelect.setFilterBarVisible(true);
        
		twinSelect.setColumnHeader(PersonnelSchema.PERSONEL_CODE, "รหัสประจำตัว");
		twinSelect.setColumnHeader(PersonnelSchema.FIRSTNAME,"ชื่อ");
		twinSelect.setColumnHeader(PersonnelSchema.LASTNAME, "สกุล");
		twinSelect.getRightTable().setColumnHeader(TeachingSchema.ACADEMIC_YEAR, "ปีการศึกษา");
		twinSelect.getRightTable().setColumnHeader(TeachingSchema.SUBJECT_ID, "รายวิชา");
		
		twinSelect.getLeftTable().setVisibleColumns(
				PersonnelSchema.PERSONEL_CODE, 
				PersonnelSchema.FIRSTNAME,
				PersonnelSchema.LASTNAME);
		
		twinSelect.getRightTable().setVisibleColumns(
				PersonnelSchema.PERSONEL_CODE, 
				PersonnelSchema.FIRSTNAME,
				PersonnelSchema.LASTNAME,
				TeachingSchema.ACADEMIC_YEAR,
				TeachingSchema.SUBJECT_ID);
		twinSelect.getRightTable().setFilterFieldVisible(TeachingSchema.SUBJECT_ID, false);
		
		twinSelect.setAddClick(addListener);
		twinSelect.setAddAllClick(addAllListener);
		twinSelect.setRemoveClick(removeListener);
		twinSelect.setRemoveAllClick(removeAllListener);
		
		addComponent(twinSelect);
		setExpandRatio(twinSelect, 1);
	}
	
	/* เรียงอันดับข้อมูลของตาราง */
	private void sortData(){
		Object[] prop = {PersonnelSchema.PERSONEL_CODE};
		boolean[] bool = {true};
		twinSelect.getLeftTable().sort(prop, bool);
		twinSelect.getRightTable().sort(prop, bool);
	}
	
	private void setLeftData(){		
		twinSelect.removeAllLeftItem();
		
		StringBuilder subject = new StringBuilder();
		subject.append(" SELECT * FROM " + PersonnelSchema.TABLE_NAME);
		subject.append(" WHERE "+ PersonnelSchema.SCHOOL_ID + "=" + UI.getCurrent().getSession().getAttribute(SessionSchema.SCHOOL_ID));
		subject.append(" AND " + PersonnelSchema.PERSONNEL_ID + " NOT IN (");
		subject.append(" SELECT "+ TeachingSchema.PERSONNEL_ID);
		subject.append(" FROM "+ TeachingSchema.TABLE_NAME);
		subject.append(" WHERE "+ TeachingSchema.SCHOOL_ID + "=" + UI.getCurrent().getSession().getAttribute(SessionSchema.SCHOOL_ID));
		subject.append(" AND " + TeachingSchema.ACADEMIC_YEAR + "=" + DateTimeUtil.getBuddishYear() + ")");

		tContainer = Container.getInstance().getFreeFormContainer(subject.toString(), PersonnelSchema.PERSONNEL_ID);
		for(final Object itemId:tContainer.getItemIds()){
			Item item = tContainer.getItem(itemId);
			addItemData(twinSelect.getLeftTable(), itemId, item);
		}
		
		twinSelect.setLeftCountFooter(PersonnelSchema.PERSONEL_CODE);
	}
	
	private void setRightData(){
		twinSelect.removeAllRightItem();
		
		StringBuilder subjectBuilder = new StringBuilder();
		subjectBuilder.append(" SELECT * FROM "+ TeachingSchema.TABLE_NAME + " tc");
		subjectBuilder.append(" INNER JOIN "+ SubjectSchema.TABLE_NAME + " s ON s." + SubjectSchema.SUBJECT_ID + " = tc." + TeachingSchema.SUBJECT_ID);
		subjectBuilder.append(" WHERE tc."+ TeachingSchema.SCHOOL_ID + "=" + UI.getCurrent().getSession().getAttribute(SessionSchema.SCHOOL_ID));
		subjectBuilder.append(" AND tc." + TeachingSchema.ACADEMIC_YEAR + "=" + DateTimeUtil.getBuddishYear());

		tContainer = Container.getInstance().getFreeFormContainer(subjectBuilder.toString(), TeachingSchema.TEACHING_ID);
		for(Object itemId: tContainer.getItemIds()){
			Item item = tContainer.getItem(itemId);
			addItemData(twinSelect.getRightTable(), itemId, item);
		}
		
		twinSelect.setRightCountFooter(PersonnelSchema.PERSONEL_CODE);
		
		if(subject.getValue() == null){
			twinSelect.getRightTable().setFilterFieldValue(TeachingSchema.SUBJECT_ID, new NumberInterval(null, null, "0"));
			
			twinSelect.setLeftCountFooter(PersonnelSchema.PERSONEL_CODE);
			twinSelect.setRightCountFooter(PersonnelSchema.PERSONEL_CODE);
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
				teachingItem.getItemProperty(TeachingSchema.SCHOOL_ID).setValue(UI.getCurrent().getSession().getAttribute(SessionSchema.SCHOOL_ID));
				teachingItem.getItemProperty(TeachingSchema.SUBJECT_ID).setValue(Integer.parseInt(subject.getValue().toString()));
				teachingItem.getItemProperty(TeachingSchema.PERSONNEL_ID).setValue(Integer.parseInt(itemId.toString()));
				teachingItem.getItemProperty(TeachingSchema.ACADEMIC_YEAR).setValue(DateTimeUtil.getBuddishYear());
				
				CreateModifiedSchema.setCreateAndModified(teachingItem);
				teachingContainer.commit();
			}
			setLeftData();
			setRightData();
			sortData();
			twinSelect.setLeftCountFooter(PersonnelSchema.PERSONEL_CODE);
			twinSelect.setRightCountFooter(PersonnelSchema.PERSONEL_CODE);
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
				teachingItem.getItemProperty(TeachingSchema.SCHOOL_ID).setValue(UI.getCurrent().getSession().getAttribute(SessionSchema.SCHOOL_ID));
				teachingItem.getItemProperty(TeachingSchema.SUBJECT_ID).setValue(Integer.parseInt(subject.getValue().toString()));
				teachingItem.getItemProperty(TeachingSchema.PERSONNEL_ID).setValue(Integer.parseInt(itemId.toString()));
				teachingItem.getItemProperty(TeachingSchema.ACADEMIC_YEAR).setValue(DateTimeUtil.getBuddishYear());
				
				CreateModifiedSchema.setCreateAndModified(teachingItem);
				teachingContainer.commit();
			}
			setLeftData();
			setRightData();
			sortData();
			twinSelect.setLeftCountFooter(PersonnelSchema.PERSONEL_CODE);
			twinSelect.setRightCountFooter(PersonnelSchema.PERSONEL_CODE);
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
			twinSelect.setLeftCountFooter(PersonnelSchema.PERSONEL_CODE);
			twinSelect.setRightCountFooter(PersonnelSchema.PERSONEL_CODE);
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
			
			twinSelect.setLeftCountFooter(PersonnelSchema.PERSONEL_CODE);
			twinSelect.setRightCountFooter(PersonnelSchema.PERSONEL_CODE);
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/* ใส่ข้อมูลในตาราง */
	private void addItemData(FilterTable table, Object itemId, Item item){		
		if(table == twinSelect.getLeftTable()){
			table.addItem(new Object[] {
					item.getItemProperty(PersonnelSchema.PERSONEL_CODE).getValue(), 
					item.getItemProperty(PersonnelSchema.FIRSTNAME).getValue(), 
					item.getItemProperty(PersonnelSchema.LASTNAME).getValue()
			},itemId);
		}else{
			Object personalCode = null;
			Object firstname = null;
			Object lastname = null;
			
			/* ตรวจสอบว่า เป็นอาจารย์พิเศษไหม ถ้าใช่ แสดงว่า personnel_id = null จึงต้องดึงจากชื่อ Tmp มาแสดงแทน */
			if(item.getItemProperty(TeachingSchema.PERSONNEL_ID).getValue() == null){
				String[] nameTmp = item.getItemProperty(TeachingSchema.PERSONNEL_NAME_TMP).getValue().toString().split(" ");
				firstname = nameTmp[0];
				lastname = nameTmp[1];
			}else{
				try {
					StringBuilder builder = new StringBuilder();
					builder.append(" SELECT " + PersonnelSchema.PERSONNEL_ID + "," + PersonnelSchema.PERSONEL_CODE + "," + PersonnelSchema.FIRSTNAME + "," + PersonnelSchema.LASTNAME);
					builder.append(" FROM " + PersonnelSchema.TABLE_NAME);
					builder.append(" WHERE " + PersonnelSchema.PERSONNEL_ID + "=" + item.getItemProperty(TeachingSchema.PERSONNEL_ID).getValue());

					FreeformQuery tq = new FreeformQuery(builder.toString(), DbConnection.getConnection(),PersonnelSchema.PERSONNEL_ID);
					SQLContainer personnelContainer = new SQLContainer(tq);

					Item personnelItem = personnelContainer.getItem(personnelContainer.getIdByIndex(0));
					personalCode = personnelItem.getItemProperty(PersonnelSchema.PERSONEL_CODE).getValue();
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
				
				twinSelect.setLeftCountFooter(PersonnelSchema.PERSONEL_CODE);
				twinSelect.setRightCountFooter(PersonnelSchema.PERSONEL_CODE);
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
