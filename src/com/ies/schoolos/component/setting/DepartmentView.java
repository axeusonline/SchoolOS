package com.ies.schoolos.component.setting;

import org.vaadin.dialogs.ConfirmDialog;

import com.ies.schoolos.component.ui.ContentPage;
import com.ies.schoolos.container.Container;
import com.ies.schoolos.schema.CreateModifiedSchema;
import com.ies.schoolos.schema.SessionSchema;
import com.ies.schoolos.schema.fundamental.DepartmentSchema;
import com.ies.schoolos.type.dynamic.Department;
import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.sqlcontainer.RowId;
import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;
import com.vaadin.data.util.filter.Like;

public class DepartmentView extends ContentPage{
	private static final long serialVersionUID = 1L;
	// database container
	private SQLContainer departmentContainer = Container.getDepartmentContainer();
	
	// layout
	private VerticalLayout mainLayout;
	private HorizontalLayout actionLayout;
	private ListSelect listSelect;
	private TextField nameField;
	private Button createButton;
	private Button editButton;
	private Button deleteButton;
	private String name;
	private Object selectedId;
	private String selectedName;
	
	//listener
	private ClickListener addButtonListener;
	private ClickListener editButtonListener;
	private ClickListener deleteButtonListener;
	private ValueChangeListener listChangeListener;
	private ValueChangeListener filterChangeListener;
	
	// create/edit form
	private TextField formTextField;
	
	public DepartmentView() {
		super("ตั้งค่าแผนก");
		departmentContainer.refresh();
		
		setSpacing(true);
		setMargin(true);
		setHeight("-1px");
		
		buildInterfaces();
		addListeners();
	}
	
	private void buildInterfaces() {
		mainLayout = new VerticalLayout();
		mainLayout.setSizeFull();
		addComponent(mainLayout);

	    // department action
 	    actionLayout = new HorizontalLayout();
	    actionLayout.setSpacing(true);	  
	    actionLayout.setImmediate(true);
	    mainLayout.addComponent(actionLayout);
	    
	    nameField = new TextField();
	    nameField.setWidth(30.0f, Unit.REM);
	    actionLayout.addComponent(nameField);
	    
	    createButton = new Button("สร้าง");
	    createButton.setWidth(5.0f, Unit.REM);
	    actionLayout.addComponent(createButton);
	    
	    editButton = new Button("แก้ไข");
	    editButton.setWidth(5.0f,Unit.REM);
	    actionLayout.addComponent(editButton);
	    
	    deleteButton = new Button("ลบ");
	    deleteButton.setWidth(5.0f,Unit.REM);
	    actionLayout.addComponent(deleteButton);
	    
		listSelect = new ListSelect("รายชื่อแผนก");
		listSelect.setNullSelectionAllowed(false);
		listSelect.setContainerDataSource(new Department());
		listSelect.setItemCaptionPropertyId("name");
		listSelect.setRows(18);
	    listSelect.setImmediate(true);
	    listSelect.setWidth(100.0f, Unit.PERCENTAGE);
	    mainLayout.addComponent(listSelect);
	    
	    addButtonListener = new ClickListener(){
			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent e) {
				final Window window = new Window("สร้างแผนกใหม่");
				window.setWidth(300.0f, Unit.PIXELS);
				window.setHeight(150.0f, Unit.PIXELS);
				window.setModal(true);
				window.setClosable(false);
				window.setResizable(false);
				window.center();
				UI.getCurrent().addWindow(window);
				
				final FormLayout form = new FormLayout();
				form.setWidth(100.0f, Unit.PERCENTAGE);
				form.setSpacing(true);
				window.setContent(form);
				
				formTextField = new TextField();
				formTextField.setWidth(15.0f,Unit.REM);
				form.addComponent(formTextField);
				
				final HorizontalLayout buttonLayout = new HorizontalLayout();
				buttonLayout.setSpacing(true);
				final Button okButton = new Button("สร้าง");
				okButton.addClickListener(new ClickListener(){
					private static final long serialVersionUID = 1L;
					@SuppressWarnings("unchecked")
					@Override
					public void buttonClick(ClickEvent event) {
						if(isDuplicate()){
							Notification.show("ข้อมูลซ้ำ กรุณาพิมพ์ใหม่อีกครั้ง",Type.WARNING_MESSAGE);
							return;
						}
						if(formTextField.isEmpty()){
							Notification.show("กรุณาป้อนข้อมูล",Type.WARNING_MESSAGE);
							return;
						}
						try{
							departmentContainer.removeAllContainerFilters();
							Item item = departmentContainer.getItem(departmentContainer.addItem());
							item.getItemProperty(DepartmentSchema.NAME).setValue(formTextField.getValue());
							item.getItemProperty(DepartmentSchema.SCHOOL_ID).setValue(UI.getCurrent().getSession().getAttribute(SessionSchema.SCHOOL_ID));
							CreateModifiedSchema.setCreateAndModified(item);
							departmentContainer.commit();
							nameField.clear();
							viewRefresh();
							window.close();
							Notification.show("ข้อมูลถูกบันทึกแล้ว",Type.TRAY_NOTIFICATION);
							return;
						}catch(Exception ex){
							Notification.show("บันทึกข้อมูลไม่สำเร้จ", Type.TRAY_NOTIFICATION);
							ex.printStackTrace();
						}
					}
					
				});
				buttonLayout.addComponent(okButton);
				final Button cancelButton = new Button("ยกเลิก");
				cancelButton.addClickListener(new ClickListener(){
					private static final long serialVersionUID = 1L;
					@Override
					public void buttonClick(ClickEvent event) {
						window.close();
					}});
				buttonLayout.addComponent(cancelButton);
				form.addComponent(buttonLayout);
		}};
		
		editButtonListener = new ClickListener(){
			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent e) {
				if(selectedId != null){
					if(canModify(selectedId)){
						final Window window = new Window("แก้ไขแผนก");
						window.setWidth(300.0f, Unit.PIXELS);
						window.setHeight(150.0f, Unit.PIXELS);
						window.setModal(true);
						window.setClosable(false);
						window.setResizable(false);
						window.center();
						UI.getCurrent().addWindow(window);
						
						final FormLayout form = new FormLayout();
						form.setWidth(100.0f, Unit.PERCENTAGE);
						form.setSpacing(true);
						window.setContent(form);
						
						formTextField = new TextField();
						formTextField.setWidth(15.0f,Unit.REM);
						Item editItem = departmentContainer.getItem(new RowId(selectedId));
						formTextField.setValue(editItem.getItemProperty(DepartmentSchema.NAME).getValue().toString());
						form.addComponent(formTextField);
						
						final HorizontalLayout buttonLayout = new HorizontalLayout();
						buttonLayout.setSpacing(true);
						final Button okButton = new Button("แก้ไข");
						okButton.addClickListener(new ClickListener(){
							private static final long serialVersionUID = 1L;
							@SuppressWarnings("unchecked")
							@Override
							public void buttonClick(ClickEvent event) {
								if(isDuplicate()){
									Notification.show("ข้อมูลซ้ำ กรุณาพิมพ์ใหม่อีกครั้ง",Type.WARNING_MESSAGE);
									return;
								}
								if(formTextField.isEmpty()){
									Notification.show("กรุณาป้อนข้อมูล",Type.WARNING_MESSAGE);
									return;
								}	
								try{
									departmentContainer.removeAllContainerFilters();
									Item item = departmentContainer.getItem(new RowId(selectedId));
									item.getItemProperty(DepartmentSchema.NAME).setValue(formTextField.getValue());
									CreateModifiedSchema.setCreateAndModified(item);
									departmentContainer.commit();
									nameField.clear();
									viewRefresh();
									window.close();
									Notification.show("ข้อมูลถูกบันทึกแล้ว",Type.TRAY_NOTIFICATION);
									return;
								}catch(Exception ex){
									Notification.show("บันทึกข้อมูลไม่สำเร้จ", Type.TRAY_NOTIFICATION);
									ex.printStackTrace();
								}	
							}
						});
						buttonLayout.addComponent(okButton);
						final Button cancelButton = new Button("ยกเลิก");
						cancelButton.addClickListener(new ClickListener(){
							private static final long serialVersionUID = 1L;
							@Override
							public void buttonClick(ClickEvent event) {
								window.close();
							}});	
						buttonLayout.addComponent(cancelButton);
						form.addComponent(buttonLayout);	
					}
				else
					Notification.show("ข้อมูล \""+name+"\" ไม่สามารถลบหรือแก้ไขข้อมูลได้ กรุณาเลือกข้อมูลอื่น" , Type.TRAY_NOTIFICATION);
				}
			else
				Notification.show("กรุณาเลือกข้อมูลที่ต้องการแก้ไข" , Type.TRAY_NOTIFICATION);
			}
		};
		
		deleteButtonListener = new ClickListener(){
			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent e) {
				if(selectedId != null){
					if(canModify(selectedId)){
						ConfirmDialog.show(UI.getCurrent(), "ลบแผนก","คุณต้องการลบแผนก\""+name+"\"ใช่หรือไม่?","ตกลง","ยกเลิก",
						        new ConfirmDialog.Listener() {
									private static final long serialVersionUID = 1L;
									public void onClose(ConfirmDialog dialog) {
						                if (dialog.isConfirmed()) {
				                			try {
				                				departmentContainer.removeItem(new RowId(selectedId));
					                			departmentContainer.commit();
												Notification.show("ลบข้อมูล \""+name+"\" สำเร้จ", Type.TRAY_NOTIFICATION);
												nameField.clear();
												viewRefresh();
											}catch (Exception e1) {
												Notification.show("ลบข้อมูล \""+name+"\" ไม่สำเร็จ, กรุณาลองอีกครั้ง" , Type.TRAY_NOTIFICATION);
												e1.printStackTrace();
											}
						                }
						            }
					        	});
					}
					else
						Notification.show("ข้อมูล \""+name+"\" ไม่สามารถลบหรือแก้ไขข้อมูลได้ กรุณาเลือกข้อมูลอื่น" , Type.TRAY_NOTIFICATION);
				}
				else{
					Notification.show("กรุณาเลือกข้อมูลที่ต้องการลบ" , Type.TRAY_NOTIFICATION);
				}
		}};
	
		listChangeListener = new ValueChangeListener(){
			private static final long serialVersionUID = 1L;

			@Override
			public void valueChange(ValueChangeEvent e) {
				selectedId = e.getProperty().getValue();
				selectedName = String.valueOf(
						departmentContainer
						.getItem(new RowId(selectedId))
						.getItemProperty("name").getValue()
						);
				nameField.setValue(selectedName);
				Notification.show("Selected Value:",selectedName,Type.TRAY_NOTIFICATION);
			}
		};
		
		filterChangeListener = new ValueChangeListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void valueChange(ValueChangeEvent e) {
				name = String.valueOf(e.getProperty().getValue());
				if(name != selectedName){
					departmentContainer.removeAllContainerFilters();
					if(name != null){
						departmentContainer.addContainerFilter(new Like(DepartmentSchema.NAME,"%"+name+"%"));
					}
					viewRefresh();
					Notification.show("Filter is "+name);
				}
			}
		};
	}

	private void addListeners(){
	    listSelect.addValueChangeListener(listChangeListener);
	    nameField.addValueChangeListener(filterChangeListener);
	    createButton.addClickListener(addButtonListener);
	    editButton.addClickListener(editButtonListener);
	    deleteButton.addClickListener(deleteButtonListener);
	}

	private void viewRefresh(){
		selectedName = null;
		selectedName = null;
		mainLayout.removeComponent(listSelect);
		listSelect = new ListSelect("รายชื่อแผนก");
		listSelect.setNullSelectionAllowed(false);
		departmentContainer.refresh();
		listSelect.setContainerDataSource(new Department());
		listSelect.setItemCaptionPropertyId("name");
		listSelect.setRows(18);
	    listSelect.setImmediate(true);
	    listSelect.setWidth(100.0f, Unit.PERCENTAGE);
	    listSelect.addValueChangeListener(listChangeListener);
	    
	    mainLayout.addComponent(listSelect);
	    createButton.removeClickListener(addButtonListener);
	    createButton.addClickListener(addButtonListener);
	    editButton.removeClickListener(editButtonListener);
	    editButton.addClickListener(editButtonListener);
	    deleteButton.removeClickListener(deleteButtonListener);
	    deleteButton.addClickListener(deleteButtonListener);   
	}
	
	private boolean isDuplicate(){
		StringBuilder builder = new StringBuilder();
		builder.append(" SELECT * FROM " + DepartmentSchema.TABLE_NAME);
		builder.append(" WHERE "+  DepartmentSchema.NAME + "='" + formTextField.getValue() + "'");
		builder.append(" AND (" + DepartmentSchema.SCHOOL_ID + "=" + UI.getCurrent().getSession().getAttribute(SessionSchema.SCHOOL_ID));
		builder.append(" OR " + DepartmentSchema.SCHOOL_ID + " IS NULL)");
		SQLContainer freeform = Container.getFreeFormContainer(builder.toString(), DepartmentSchema.DEPARTMENT_ID);
		
		if(freeform.size() > 0)
			return true;
		else
			return false;
	}
	
	private boolean canModify(Object id){	
		if(departmentContainer.getItem(new RowId(id)).getItemProperty(DepartmentSchema.SCHOOL_ID).getValue() != null)			
			return true;
		else
			return false;
	}

}
