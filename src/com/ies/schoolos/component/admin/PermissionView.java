package com.ies.schoolos.component.admin;

import java.util.Collection;

import org.tepi.filtertable.FilterTable;

import com.ies.schoolos.component.ui.TwinSelectTable;
import com.ies.schoolos.container.Container;
import com.ies.schoolos.filter.TableFilterDecorator;
import com.ies.schoolos.filter.TableFilterGenerator;
import com.ies.schoolos.schema.CreateModifiedSchema;
import com.ies.schoolos.schema.SessionSchema;
import com.ies.schoolos.schema.UserSchema;
import com.ies.schoolos.schema.info.PersonnelSchema;
import com.ies.schoolos.type.Feature;
import com.ies.schoolos.type.UserType;
import com.ies.schoolos.type.dynamic.Department;
import com.ies.schoolos.utility.Notification;
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

public class PermissionView extends VerticalLayout {

	private static final long serialVersionUID = 1L;
	
	private Container container = new Container();
	private SQLContainer freeContainer;
	private SQLContainer userContainer = container.getUserContainer();

	private int currentFeature = 0;
	
	private ComboBox features;
	private TwinSelectTable twinSelect;
	
	public PermissionView() {
		userContainer.refresh();
		
		setSizeFull();
		setSpacing(true);
		setMargin(true);
		buildMainLayout();
	}
	
	private void buildMainLayout(){
		/* Toolbar */
		HorizontalLayout toolStrip = new HorizontalLayout();
		toolStrip.setHeight("90px");
		toolStrip.setStyleName("border-white");
		toolStrip.setSpacing(true);
		addComponent(toolStrip);
		
		features = new ComboBox("การทำงาน",new Feature());
		features.setInputPrompt("กรุณาเลือก");
		features.setItemCaptionPropertyId("name");
		features.setImmediate(true);
        features.setNullSelectionAllowed(false);
        features.setRequired(true);
		features.setWidth("-1px");
		features.setHeight("-1px");
		features.setFilteringMode(FilteringMode.CONTAINS);
		features.addValueChangeListener(searchValueChange);
		toolStrip.addComponent(features);
		toolStrip.setComponentAlignment(features, Alignment.MIDDLE_LEFT);
		
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
		twinSelect.addContainerProperty(PersonnelSchema.DEPARTMENT_ID, String.class, null);
		
		twinSelect.setFilterDecorator(new TableFilterDecorator());
		twinSelect.setFilterGenerator(new TableFilterGenerator());
		twinSelect.setFilterBarVisible(true);
        
		twinSelect.setColumnHeader(PersonnelSchema.PERSONNEL_CODE, "รหัสประจำตัว");
		twinSelect.setColumnHeader(PersonnelSchema.FIRSTNAME,"ชื่อ");
		twinSelect.setColumnHeader(PersonnelSchema.LASTNAME, "สกุล");
		twinSelect.setColumnHeader(PersonnelSchema.DEPARTMENT_ID, "แผนก");

		twinSelect.getLeftTable().setVisibleColumns(
				PersonnelSchema.PERSONNEL_CODE, 
				PersonnelSchema.FIRSTNAME,
				PersonnelSchema.LASTNAME,
				PersonnelSchema.DEPARTMENT_ID);
		
		twinSelect.getRightTable().setVisibleColumns(
				PersonnelSchema.PERSONNEL_CODE, 
				PersonnelSchema.FIRSTNAME,
				PersonnelSchema.LASTNAME,
				PersonnelSchema.DEPARTMENT_ID);
		
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
		
		StringBuilder userBuilder = new StringBuilder();
		userBuilder.append(" SELECT * FROM " + UserSchema.TABLE_NAME + " u");
		userBuilder.append(" INNER JOIN " + PersonnelSchema.TABLE_NAME + " p ON p." + PersonnelSchema.PERSONNEL_ID + "= u." +UserSchema.REF_USER_ID);
		userBuilder.append(" WHERE u."+ PersonnelSchema.SCHOOL_ID + "=" + SessionSchema.getSchoolID());
		userBuilder.append(" AND u."+ UserSchema.REF_USER_TYPE + "<> " + UserType.ADMIN);
		userBuilder.append(" AND SUBSTR("+ UserSchema.PERMISSION + "," + currentFeature +",1) = 0");
		
		freeContainer = container.getFreeFormContainer(userBuilder.toString(), UserSchema.USER_ID);
		for(final Object itemId:freeContainer.getItemIds()){
			Item item = freeContainer.getItem(itemId);
			addItemData(twinSelect.getLeftTable(), itemId, item);
		}
		
		twinSelect.setLeftCountFooter(PersonnelSchema.PERSONNEL_CODE);
	}
	
	private void setRightData(){
		twinSelect.removeAllRightItem();
		
		StringBuilder userBuilder = new StringBuilder();
		userBuilder.append(" SELECT * FROM " + UserSchema.TABLE_NAME + " u");
		userBuilder.append(" INNER JOIN " + PersonnelSchema.TABLE_NAME + " p ON p." + PersonnelSchema.PERSONNEL_ID + "= u." +UserSchema.REF_USER_ID);
		userBuilder.append(" WHERE u."+ PersonnelSchema.SCHOOL_ID + "=" + SessionSchema.getSchoolID());
		userBuilder.append(" AND u."+ UserSchema.REF_USER_TYPE + "<> " + UserType.ADMIN);
		userBuilder.append(" AND SUBSTR("+ UserSchema.PERMISSION + "," + currentFeature+",1) = 1");

		freeContainer = container.getFreeFormContainer(userBuilder.toString(), UserSchema.USER_ID);
		for(Object itemId: freeContainer.getItemIds()){
			Item item = freeContainer.getItem(itemId);
			addItemData(twinSelect.getRightTable(), itemId, item);
		}
		
		twinSelect.setRightCountFooter(PersonnelSchema.PERSONNEL_CODE);		
	}
	
	/* ย้ายข้างจากซ้ายไปขวาจากที่ถูกเลือก */
	@SuppressWarnings("unchecked")
	private void selectData(Object... itemIds){
		try {
			for(Object itemId: itemIds){
				/* ก่อนแก้ไข เพิ่มต้องลบ Filter ก่อนหน้า */
				userContainer.removeAllContainerFilters();
				Item userItem = userContainer.getItem(itemId);
				
				String permission = "";
				String[] permissionArray = userItem.getItemProperty(UserSchema.PERMISSION).getValue().toString().split(",");
				for(int i =0; i < permissionArray.length; i++){
					if(Integer.parseInt(features.getValue().toString()) == i)
						permissionArray[i] = "1";
					permission += permissionArray[i]+",";
				}
				userItem.getItemProperty(UserSchema.PERMISSION).setValue(permission.substring(0, permission.length()-1));
				CreateModifiedSchema.setCreateAndModified(userItem);
				userContainer.commit();
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
				userContainer.removeAllContainerFilters();
				Item userItem = userContainer.getItem(itemId);
				
				String permission = "";
				String[] permissionArray = userItem.getItemProperty(UserSchema.PERMISSION).getValue().toString().split(",");
				for(int i =0; i < permissionArray.length; i++){
					if(Integer.parseInt(features.getValue().toString()) == i)
						permissionArray[i] = "1";
					permission += permissionArray[i]+",";
				}
				userItem.getItemProperty(UserSchema.PERMISSION).setValue(permission.substring(0, permission.length()-1));
				CreateModifiedSchema.setCreateAndModified(userItem);
				userContainer.commit();
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
	@SuppressWarnings("unchecked")
	private void removeData(Object... itemIds){
		try {
			for(Object itemId: itemIds){
				/* ก่อนแก้ไข เพิ่มต้องลบ Filter ก่อนหน้า */
				userContainer.removeAllContainerFilters();
				Item userItem = userContainer.getItem(itemId);
				
				String permission = "";
				String[] permissionArray = userItem.getItemProperty(UserSchema.PERMISSION).getValue().toString().split(",");
				for(int i =0; i < permissionArray.length; i++){
					if(Integer.parseInt(features.getValue().toString()) == i)
						permissionArray[i] = "0";
					permission += permissionArray[i]+",";
				}
				userItem.getItemProperty(UserSchema.PERMISSION).setValue(permission.substring(0, permission.length()-1));
				CreateModifiedSchema.setCreateAndModified(userItem);
				userContainer.commit();
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
	@SuppressWarnings("unchecked")
	private void removeAllData(){
		try {
			for(Object itemId: twinSelect.getRightTable().getItemIds()){
				/* ก่อนแก้ไข เพิ่มต้องลบ Filter ก่อนหน้า */
				userContainer.removeAllContainerFilters();
				Item userItem = userContainer.getItem(itemId);
				
				String permission = "";
				String[] permissionArray = userItem.getItemProperty(UserSchema.PERMISSION).getValue().toString().split(",");
				for(int i =0; i < permissionArray.length; i++){
					if(Integer.parseInt(features.getValue().toString()) == i)
						permissionArray[i] = "0";
					permission += permissionArray[i]+",";
				}
				userItem.getItemProperty(UserSchema.PERMISSION).setValue(permission.substring(0, permission.length()-1));
				CreateModifiedSchema.setCreateAndModified(userItem);
				userContainer.commit();
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
		String department = "ไม่ระบุ";
		if(item.getItemProperty(PersonnelSchema.DEPARTMENT_ID).getValue() != null)
			department = new Department().getItem(item.getItemProperty(PersonnelSchema.DEPARTMENT_ID).getValue()).getItemProperty("name").getValue().toString();
		table.addItem(new Object[] {
				item.getItemProperty(PersonnelSchema.PERSONNEL_CODE).getValue(), 
				item.getItemProperty(PersonnelSchema.FIRSTNAME).getValue(), 
				item.getItemProperty(PersonnelSchema.LASTNAME).getValue(),
				department
		},itemId);
	}
	
	/* ค้นหานักเรียนตามเงื่อนไขที่เลือก */
	private ValueChangeListener searchValueChange = new ValueChangeListener() {
		private static final long serialVersionUID = 1L;

		@Override
		public void valueChange(ValueChangeEvent event) {
			if(features.getValue() != null){
				currentFeature = Integer.parseInt(event.getProperty().getValue().toString())+1;
				currentFeature = currentFeature + (currentFeature-1);
				
				setLeftData();
				setRightData();
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
			if(features.getValue() == null){
				Notification.show("กรุณาเลือกการทำงาน", Type.WARNING_MESSAGE);
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
			if(features.getValue() == null){
				Notification.show("กรุณาเลือกการทำงาน", Type.WARNING_MESSAGE);
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
