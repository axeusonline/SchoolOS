package com.ies.schoolos.component.registration;

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
import com.ies.schoolos.schema.academic.TeacherHomeroomSchema;
import com.ies.schoolos.schema.fundamental.ClassRoomSchema;
import com.ies.schoolos.schema.info.PersonnelSchema;
import com.ies.schoolos.type.dynamic.ClassRoom;
import com.ies.schoolos.utility.DateTimeUtil;
import com.ies.schoolos.utility.Notification;
import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.filter.And;
import com.vaadin.data.util.filter.Compare.Equal;
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

public class TeacherHomeroomView extends VerticalLayout {

	private static final long serialVersionUID = 1L;
	
	private SQLContainer tContainer;
	private SQLContainer teacherHomeroomContainer = Container.getTeacherHomeroomContainer();

	private ComboBox classRoom;
	private Button addition;
	private TwinSelectTable twinSelect;
	
	public TeacherHomeroomView() {
		teacherHomeroomContainer.refresh();
		
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
		
		classRoom = new ComboBox("ชั้นเรียน",new ClassRoom());
		classRoom.setInputPrompt("กรุณาเลือก");
		classRoom.setItemCaptionPropertyId("name");
		classRoom.setImmediate(true);
        classRoom.setNullSelectionAllowed(false);
        classRoom.setRequired(true);
		classRoom.setWidth("-1px");
		classRoom.setHeight("-1px");
		classRoom.setFilteringMode(FilteringMode.CONTAINS);
		classRoom.addValueChangeListener(searchValueChange);
		toolStrip.addComponent(classRoom);
		toolStrip.setComponentAlignment(classRoom, Alignment.MIDDLE_LEFT);
		
		addition = new Button("เพิ่มอาจารย์ชั่วคราว", FontAwesome.USER);
		addition.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event) {
				if(classRoom.getValue() != null){
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
										teacherHomeroomContainer.addContainerFilter(new And(
											new Equal(TeacherHomeroomSchema.SCHOOL_ID,SessionSchema.getSchoolID())	,
											new Equal(TeacherHomeroomSchema.CLASS_ROOM_ID,Integer.parseInt(classRoom.getValue().toString())),
											new Equal(TeacherHomeroomSchema.PERSONNEL_NAME_TMP,name.getValue())));
										
										if(teacherHomeroomContainer.size() > 0){
											Notification.show("อาจารย์ซ้ำ กรุณาตรวจสอบชื่ออีกครั้ง", Type.WARNING_MESSAGE);
											return;
										}
										
										/* ก่อนแก้ไข เพิ่มต้องลบ Filter ก่อนหน้า */
										teacherHomeroomContainer.removeAllContainerFilters();
										
										Object tempId = teacherHomeroomContainer.addItem();
										
										Item teacherHomeroomItem = teacherHomeroomContainer.getItem(tempId);
										teacherHomeroomItem.getItemProperty(TeacherHomeroomSchema.SCHOOL_ID).setValue(SessionSchema.getSchoolID());
										teacherHomeroomItem.getItemProperty(TeacherHomeroomSchema.CLASS_ROOM_ID).setValue(Integer.parseInt(classRoom.getValue().toString()));
										teacherHomeroomItem.getItemProperty(TeacherHomeroomSchema.PERSONNEL_NAME_TMP).setValue(name.getValue());
										teacherHomeroomItem.getItemProperty(TeacherHomeroomSchema.ACADEMIC_YEAR).setValue(DateTimeUtil.getBuddishYear());
										
										CreateModifiedSchema.setCreateAndModified(teacherHomeroomItem);
										teacherHomeroomContainer.commit();
										
										setRightData();
										sortData();
										twinSelect.setLeftCountFooter(PersonnelSchema.PERSONEL_CODE);
										twinSelect.setRightCountFooter(PersonnelSchema.PERSONEL_CODE);
										
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
					Notification.show("กรุณาเลือกชั้นเรียน", Type.WARNING_MESSAGE);
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
		twinSelect.getRightTable().addContainerProperty(TeacherHomeroomSchema.ACADEMIC_YEAR, String.class, null);
		twinSelect.getRightTable().addContainerProperty(TeacherHomeroomSchema.CLASS_ROOM_ID, Integer.class, null);
		
		twinSelect.setFilterDecorator(new TableFilterDecorator());
		twinSelect.setFilterGenerator(new TableFilterGenerator());
		twinSelect.setFilterBarVisible(true);
        
		twinSelect.setColumnHeader(PersonnelSchema.PERSONEL_CODE, "รหัสประจำตัว");
		twinSelect.setColumnHeader(PersonnelSchema.FIRSTNAME,"ชื่อ");
		twinSelect.setColumnHeader(PersonnelSchema.LASTNAME, "สกุล");
		twinSelect.getRightTable().setColumnHeader(TeacherHomeroomSchema.ACADEMIC_YEAR, "ปีการศึกษา");
		twinSelect.getRightTable().setColumnHeader(TeacherHomeroomSchema.CLASS_ROOM_ID, "ชั้นเรียน");
		
		twinSelect.getLeftTable().setVisibleColumns(
				PersonnelSchema.PERSONEL_CODE, 
				PersonnelSchema.FIRSTNAME,
				PersonnelSchema.LASTNAME);
		
		twinSelect.getRightTable().setVisibleColumns(
				PersonnelSchema.PERSONEL_CODE, 
				PersonnelSchema.FIRSTNAME,
				PersonnelSchema.LASTNAME,
				TeacherHomeroomSchema.ACADEMIC_YEAR,
				TeacherHomeroomSchema.CLASS_ROOM_ID);
		
		twinSelect.getRightTable().setColumnCollapsingAllowed(true);
		twinSelect.getRightTable().setColumnCollapsed(TeacherHomeroomSchema.CLASS_ROOM_ID, true);
		
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
		
		StringBuilder classRoomBuilder = new StringBuilder();
		classRoomBuilder.append(" SELECT * FROM " + PersonnelSchema.TABLE_NAME);
		classRoomBuilder.append(" WHERE "+ PersonnelSchema.SCHOOL_ID + "=" + SessionSchema.getSchoolID());
		classRoomBuilder.append(" AND "+ PersonnelSchema.PERSONNEL_ID + " NOT IN (");
		classRoomBuilder.append(" SELECT "+ TeacherHomeroomSchema.PERSONNEL_ID + " FROM " + TeacherHomeroomSchema.TABLE_NAME);
		classRoomBuilder.append(" WHERE "+ TeacherHomeroomSchema.SCHOOL_ID + "=" + SessionSchema.getSchoolID());
		classRoomBuilder.append(" AND "+ TeacherHomeroomSchema.CLASS_ROOM_ID + "=" + classRoom.getValue());
		classRoomBuilder.append(" AND "+ TeacherHomeroomSchema.PERSONNEL_ID + " IS NOT NULL )");
		
		tContainer = Container.getFreeFormContainer(classRoomBuilder.toString(), PersonnelSchema.PERSONNEL_ID);
		for(final Object itemId:tContainer.getItemIds()){
			Item item = tContainer.getItem(itemId);
			addItemData(twinSelect.getLeftTable(), itemId, item);
		}
		
		twinSelect.setLeftCountFooter(PersonnelSchema.PERSONEL_CODE);
	}
	
	private void setRightData(){
		twinSelect.removeAllRightItem();
		
		StringBuilder teacherHomeroomBuilder = new StringBuilder();
		teacherHomeroomBuilder.append(" SELECT * FROM "+ TeacherHomeroomSchema.TABLE_NAME + " tc");
		teacherHomeroomBuilder.append(" INNER JOIN "+ ClassRoomSchema.TABLE_NAME + " s ON s." + ClassRoomSchema.CLASS_ROOM_ID + " = tc." + TeacherHomeroomSchema.CLASS_ROOM_ID);
		teacherHomeroomBuilder.append(" WHERE tc."+ TeacherHomeroomSchema.SCHOOL_ID + "=" + SessionSchema.getSchoolID());
		teacherHomeroomBuilder.append(" AND tc." + TeacherHomeroomSchema.ACADEMIC_YEAR + "='" + DateTimeUtil.getBuddishYear()+"'");

		tContainer = Container.getFreeFormContainer(teacherHomeroomBuilder.toString(), TeacherHomeroomSchema.TEACHER_HOMEROOM_ID);
		for(Object itemId: tContainer.getItemIds()){
			Item item = tContainer.getItem(itemId);
			addItemData(twinSelect.getRightTable(), itemId, item);
		}
		
		twinSelect.setRightCountFooter(PersonnelSchema.PERSONEL_CODE);
		
		if(classRoom.getValue() == null){
			twinSelect.getRightTable().setFilterFieldValue(TeacherHomeroomSchema.CLASS_ROOM_ID, new NumberInterval(null, null, "0"));
			
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
				teacherHomeroomContainer.removeAllContainerFilters();
				
				Object tempId = teacherHomeroomContainer.addItem();
				
				Item teacherHomeroomItem = teacherHomeroomContainer.getItem(tempId);
				teacherHomeroomItem.getItemProperty(TeacherHomeroomSchema.SCHOOL_ID).setValue(SessionSchema.getSchoolID());
				teacherHomeroomItem.getItemProperty(TeacherHomeroomSchema.CLASS_ROOM_ID).setValue(Integer.parseInt(classRoom.getValue().toString()));
				teacherHomeroomItem.getItemProperty(TeacherHomeroomSchema.PERSONNEL_ID).setValue(Integer.parseInt(itemId.toString()));
				teacherHomeroomItem.getItemProperty(TeacherHomeroomSchema.ACADEMIC_YEAR).setValue(DateTimeUtil.getBuddishYear());
				
				CreateModifiedSchema.setCreateAndModified(teacherHomeroomItem);
				teacherHomeroomContainer.commit();
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
				teacherHomeroomContainer.removeAllContainerFilters();
				
				Object tempId = teacherHomeroomContainer.addItem();
				
				Item teacherHomeroomItem = teacherHomeroomContainer.getItem(tempId);
				teacherHomeroomItem.getItemProperty(TeacherHomeroomSchema.SCHOOL_ID).setValue(SessionSchema.getSchoolID());
				teacherHomeroomItem.getItemProperty(TeacherHomeroomSchema.CLASS_ROOM_ID).setValue(Integer.parseInt(classRoom.getValue().toString()));
				teacherHomeroomItem.getItemProperty(TeacherHomeroomSchema.PERSONNEL_ID).setValue(Integer.parseInt(itemId.toString()));
				teacherHomeroomItem.getItemProperty(TeacherHomeroomSchema.ACADEMIC_YEAR).setValue(DateTimeUtil.getBuddishYear());
				
				CreateModifiedSchema.setCreateAndModified(teacherHomeroomItem);
				teacherHomeroomContainer.commit();
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
				teacherHomeroomContainer.removeItem(itemId);
				teacherHomeroomContainer.commit();
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
				teacherHomeroomContainer.removeItem(itemId);
				teacherHomeroomContainer.commit();
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
			
			/* ตรวจสอบว่า เป็นอาจารย์ชั่วคราวไหม ถ้าใช่ แสดงว่า personnel_id = null จึงต้องดึงจากชื่อ Tmp มาแสดงแทน */
			if(item.getItemProperty(TeacherHomeroomSchema.PERSONNEL_ID).getValue() == null){
				String[] nameTmp = item.getItemProperty(TeacherHomeroomSchema.PERSONNEL_NAME_TMP).getValue().toString().split(" ");
				firstname = nameTmp[0];
				lastname = nameTmp[1];
			}else{
				try {
					StringBuilder builder = new StringBuilder();
					builder.append(" SELECT " + PersonnelSchema.PERSONNEL_ID + "," + PersonnelSchema.PERSONEL_CODE + "," + PersonnelSchema.FIRSTNAME + "," + PersonnelSchema.LASTNAME);
					builder.append(" FROM " + PersonnelSchema.TABLE_NAME);
					builder.append(" WHERE " + PersonnelSchema.PERSONNEL_ID + "=" + item.getItemProperty(TeacherHomeroomSchema.PERSONNEL_ID).getValue());

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
					item.getItemProperty(TeacherHomeroomSchema.ACADEMIC_YEAR).getValue(),
					item.getItemProperty(TeacherHomeroomSchema.CLASS_ROOM_ID).getValue()
			},itemId);
		}
	}
	
	/* ค้นหานักเรียนตามเงื่อนไขที่เลือก */
	private ValueChangeListener searchValueChange = new ValueChangeListener() {
		private static final long serialVersionUID = 1L;

		@Override
		public void valueChange(ValueChangeEvent event) {
			if(classRoom.getValue() != null){
				twinSelect.getRightTable().setFilterFieldValue(TeacherHomeroomSchema.CLASS_ROOM_ID, new NumberInterval(null, null, classRoom.getValue().toString()));
				setLeftData();
				
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
			if(classRoom.getValue() == null){
				Notification.show("กรุณาเลือกชั้นเรียน", Type.WARNING_MESSAGE);
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
			if(classRoom.getValue() == null){
				Notification.show("กรุณาเลือกชั้นเรียน", Type.WARNING_MESSAGE);
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
