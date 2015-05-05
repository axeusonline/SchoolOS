package com.ies.schoolos.component.fundamental;

import org.tepi.filtertable.FilterTable;
import org.vaadin.dialogs.ConfirmDialog;

import com.ies.schoolos.component.ui.ContentPage;
import com.ies.schoolos.container.Container;
import com.ies.schoolos.filter.TableFilterDecorator;
import com.ies.schoolos.filter.TableFilterGenerator;
import com.ies.schoolos.schema.CreateModifiedSchema;
import com.ies.schoolos.schema.SessionSchema;
import com.ies.schoolos.schema.fundamental.BuildingSchema;
import com.ies.schoolos.schema.fundamental.SubjectSchema;
import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.filter.Compare.Equal;
import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.data.util.sqlcontainer.TemporaryRowId;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CustomTable;
import com.vaadin.ui.Field;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.CustomTable.ColumnGenerator;

public class BuildingView extends ContentPage{
	private static final long serialVersionUID = 1L;

	private boolean editMode = false;
	private SQLContainer bContainer = Container.getBuildingContainer();
	
	private Item item;

	private HorizontalLayout buildingLayout;
	private FilterTable table;
	
	private FieldGroup buildingBinder;
	private FormLayout buildingForm;
	private TextField buildingName;
	private TextField roomName;
	private TextField capacity;
	private Button save;	
	
	public BuildingView() {
		super("อาคารเรียน/สอบ");
		
		bContainer.refresh();
		bContainer.addContainerFilter(new Equal(SubjectSchema.SCHOOL_ID, SessionSchema.getSchoolID()));
		setSpacing(true);
		setMargin(true);
		buildMainLayout();
	}
	
	private void buildMainLayout(){
		setSizeFull();
		setSpacing(true);

		buildingLayout = new HorizontalLayout();
		buildingLayout.setSizeFull();
		buildingLayout.setSpacing(true);
		addComponent(buildingLayout);
		setExpandRatio(buildingLayout, 1);

		//Table
		table = new FilterTable();
		table.setSizeFull();
		table.setSelectable(true);
		table.setFooterVisible(true);        
		table.addValueChangeListener(new ValueChangeListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				if(event.getProperty().getValue() != null){
					editMode = true;
					save.setCaption("แก้ไข");
					item = bContainer.getItem(event.getProperty().getValue());
					initFieldGroup();
				}
			}
		});
		
		table.setFilterDecorator(new TableFilterDecorator());
		table.setFilterGenerator(new TableFilterGenerator());
        table.setFilterBarVisible(true);

		table.setContainerDataSource(bContainer);
	    setFooterData();
		initTableStyle();

		table.setColumnReorderingAllowed(true);
		table.setColumnCollapsingAllowed(true);
		buildingLayout.addComponent(table);
		
		//Form		
		buildingForm = new FormLayout();
		buildingForm.setSpacing(true);
		buildingForm.setStyleName("border-white");
		buildingLayout.addComponent(buildingForm);
		
		Label formLab = new Label("ข้อมูลอาคาร");
		buildingForm.addComponent(formLab);
		
		buildingName = new TextField();
		buildingName.setInputPrompt("ชื่ออาคาร");
		buildingName.setNullRepresentation("");
		buildingName.setImmediate(false);
		buildingName.setWidth("-1px");
		buildingName.setHeight("-1px");
		buildingForm.addComponent(buildingName);
		
		roomName = new TextField();
		roomName.setInputPrompt("ชื่อห้อง");
		roomName.setNullRepresentation("");
		roomName.setImmediate(false);
		roomName.setWidth("-1px");
		roomName.setHeight("-1px");
		buildingForm.addComponent(roomName);
		
		capacity = new TextField();
		capacity.setInputPrompt("จำนวนคนสูงสุด");
		capacity.setNullRepresentation("");
		capacity.setImmediate(false);
		capacity.setWidth("-1px");
		capacity.setHeight("-1px");
		buildingForm.addComponent(capacity);
		
		save = new Button("บันทึก", FontAwesome.SAVE);
		save.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event) {
				try {
					/* ตรวจสอบสถานะการจัดการข้อมูล
					 *  กรณีเป็น แก้ไข จะทำการ Update โดยใช้ข้อมูลในฟอร์มเดิม
					 *  กรณี เป็น เพิ่ม จะทำการ Inser โดยใช้ข้อมูลใหม่ที่กรอกในฟอร์ม */
					if(editMode){
						buildingBinder.commit();
						bContainer.commit();
						editMode = false;
						Notification.show("บันทึกสำเร็จ", Type.HUMANIZED_MESSAGE);
					}else{
						bContainer.removeAllContainerFilters();
						if(!buildingBinder.isValid()){
							Notification.show("กรุณากรอกข้อมูลให้ครบถ้วน", Type.WARNING_MESSAGE);
							return;
						}
							
						if(!saveFormData())
							return;
						
						bContainer.addContainerFilter(new Equal(SubjectSchema.SCHOOL_ID, SessionSchema.getSchoolID()));
					}
					item = null;
					save.setCaption("บันทึก");
					initFieldGroup();
					Notification.show("บันทึกสำเร็จ", Type.HUMANIZED_MESSAGE);
				} catch (Exception e) {
					e.printStackTrace();
					Notification.show("บันทึกไม่สำเร็จ", Type.HUMANIZED_MESSAGE);
				}
			}
		});
		buildingForm.addComponent(save);
		
		initFieldGroup();
		
	}
	
	/* ตั้งค่ารูปแบบแสดงของตาราง */
	private void initTableStyle(){		
		table.setColumnHeader(BuildingSchema.NAME, "อาคาร");
		table.setColumnHeader(BuildingSchema.ROOM_NUMBER,"ชื่อห้อง");
		table.setColumnHeader(BuildingSchema.CAPACITY, "จำนวนคนสูงสุด");
		
		table.setVisibleColumns(
				BuildingSchema.NAME, 
				BuildingSchema.ROOM_NUMBER,
				BuildingSchema.CAPACITY);
		
		setColumnGenerator("");
	}
	
	/* ตั้งค่ารูปแบบข้อมูลของค่า Fix */
	private void setColumnGenerator(Object... propertyIds){
		for(final Object propertyId:propertyIds){
			table.addGeneratedColumn(propertyId, new ColumnGenerator() {
				private static final long serialVersionUID = 1L;

				@Override
				public Object generateCell(CustomTable source, Object itemId, Object columnId) {
					Object value = null;
					Item item = source.getItem(itemId);
					if(item != null && itemId.getClass() != TemporaryRowId.class){
						if("".equals(propertyId))
							value = initButtonLayout(item, itemId);
					}
					return value;
				}
			});
		}
	}
	
	private HorizontalLayout initButtonLayout(final Item item, final Object itemId){
		final HorizontalLayout buttonLayout = new HorizontalLayout();
			
		Button removeButton = new Button(FontAwesome.TRASH_O);
		removeButton.setId(itemId.toString());
		removeButton.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 1L;
			@Override
			public void buttonClick(ClickEvent event) {
				ConfirmDialog.show(UI.getCurrent(), "ลบอาคารเรียน","คุณต้องการลบอาคารเรียนนี้ใช่หรือไม่?","ตกลง","ยกเลิก",
			        new ConfirmDialog.Listener() {
						private static final long serialVersionUID = 1L;
						public void onClose(ConfirmDialog dialog) {
			                if (dialog.isConfirmed()) {
			                	if(bContainer.removeItem(itemId)){
			                		try {
										bContainer.commit();
										setFooterData();
									} catch (Exception e) {
										Notification.show("ลบข้อมูลไม่สำเร็จ", Type.WARNING_MESSAGE);
										e.printStackTrace();
									}
			                	}
			                }
			            }
			        });
			}
		});
		
		buttonLayout.addComponent(removeButton);
		buttonLayout.setComponentAlignment(removeButton, Alignment.MIDDLE_CENTER);
		return buttonLayout;
	}
	
	/*นำจำนวนที่นับ มาใส่ค่าในส่วนท้ายตาราง*/
	private void setFooterData(){
		table.setColumnFooter(BuildingSchema.NAME, "ทั้งหมด: "+ table.size() + " อาคาร");
	}
	
	/* จัดกลุ่มของ ฟอร์มในการแก้ไข - เพิ่ม ข้อมูล */
	private void initFieldGroup(){		
		buildingBinder = new FieldGroup(item);
		buildingBinder.setBuffered(true);
		buildingBinder.bind(buildingName, BuildingSchema.NAME);
		buildingBinder.bind(roomName, BuildingSchema.ROOM_NUMBER);
		buildingBinder.bind(capacity, BuildingSchema.CAPACITY);
	}	
	
	/* กำหนดค่าภายใน FieldGroup ไปยัง Item */
	@SuppressWarnings({ "unchecked"})
	private boolean saveFormData(){
		try {				
			/* เพิ่มข้อมูล */
			Object tmpItem = bContainer.addItem();
			Item item = bContainer.getItem(tmpItem);
			for(Field<?> field: buildingBinder.getFields()){
				/* หาชนิดตัวแปร ของข้อมูลภายใน Database ของแต่ละ Field */
				Class<?> clazz = item.getItemProperty(buildingBinder.getPropertyId(field)).getType();		
				String className = clazz.getName();;
				Object value = null;
				if(buildingBinder.getField(buildingBinder.getPropertyId(field)).getValue() != null && 
						!buildingBinder.getField(buildingBinder.getPropertyId(field)).getValue().equals("")){
					/* ตรวจสอบ Class ที่ต้องแปลงที่ได้จากการตรวจสอบภายใน Database จาก item.getItemProperty(buildingBinder.getPropertyId(field)).getType()
					 *  กรณั เป็น Double ก็แปลง Object ด้วย parseDouble ซึ่งค่าที่แปลงต้องไม่เป็น Null
					 *  กรณั เป็น Integer ก็แปลง Object ด้วย parseInt ซึ่งค่าที่แปลงต้องไม่เป็น Null
					 *    */

					if(clazz == Double.class){
						value = Double.parseDouble(buildingBinder.getField(buildingBinder.getPropertyId(field)).getValue().toString());
					}else if(clazz == Integer.class){
						value = Integer.parseInt(buildingBinder.getField(buildingBinder.getPropertyId(field)).getValue().toString());
					}else{
						value = buildingBinder.getField(buildingBinder.getPropertyId(field)).getValue();
					}
				}
				
				Object data = Class.forName(className).cast(value);
				item.getItemProperty(buildingBinder.getPropertyId(field)).setValue(data);
			}
			item.getItemProperty(BuildingSchema.SCHOOL_ID).setValue(SessionSchema.getSchoolID());
			CreateModifiedSchema.setCreateAndModified(item);
			bContainer.commit();
			
			return true;
		} catch (Exception e) {
			Notification.show("บันทึกไม่สำเร็จ", Type.WARNING_MESSAGE);
			e.printStackTrace();
			return false;
		}
	}
}
