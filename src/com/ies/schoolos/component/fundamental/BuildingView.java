package com.ies.schoolos.component.fundamental;

import org.tepi.filtertable.FilterTable;
import org.vaadin.dialogs.ConfirmDialog;

import com.ies.schoolos.component.ui.ContentPage;
import com.ies.schoolos.container.Container;
import com.ies.schoolos.filter.TableFilterDecorator;
import com.ies.schoolos.filter.TableFilterGenerator;
import com.ies.schoolos.schema.SchoolSchema;
import com.ies.schoolos.schema.SessionSchema;
import com.ies.schoolos.schema.fundamental.BuildingSchema;
import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.filter.Compare.Equal;
import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.CustomTable.Align;

public class BuildingView extends ContentPage {
	private static final long serialVersionUID = 1L;

	private boolean editMode = false;
	private SQLContainer bContainer = Container.getInstance().getBuildingContainer();
	
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
		super("อาคารเรียน");
		
		bContainer.refresh();
		
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
		buildingLayout.addComponent(table);

		table.addContainerProperty(BuildingSchema.NAME, String.class, null);
		table.addContainerProperty(BuildingSchema.ROOM_NUMBER, String.class, null);
		table.addContainerProperty(BuildingSchema.CAPACITY, Integer.class, null);
		table.addContainerProperty("note", Button.class, null);

		table.setFilterDecorator(new TableFilterDecorator());
		table.setFilterGenerator(new TableFilterGenerator());
        table.setFilterBarVisible(true);
        
		table.setColumnAlignment(BuildingSchema.NAME,Align.CENTER);
		table.setColumnAlignment(BuildingSchema.ROOM_NUMBER,Align.CENTER);
		table.setColumnAlignment(BuildingSchema.CAPACITY,Align.CENTER);
		table.setColumnAlignment("note",Align.CENTER);

		table.setColumnHeader(BuildingSchema.NAME, "อาคาร");
		table.setColumnHeader(BuildingSchema.ROOM_NUMBER,"ชื่อห้อง");
		table.setColumnHeader(BuildingSchema.CAPACITY, "จำนวนคนสูงสุด");
		table.setColumnHeader("note", "");
		table.setVisibleColumns(
				BuildingSchema.NAME, 
				BuildingSchema.ROOM_NUMBER,
				BuildingSchema.CAPACITY,
				"note");
		table.setFooterVisible(true);
		
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
				if(editMode){
					try {
						buildingBinder.commit();
						bContainer.commit();
						updateTable();
						editMode = false;
						Notification.show("บันทึกสำเร็จ", Type.HUMANIZED_MESSAGE);
					} catch (Exception e) {
						Notification.show("บันทึกไม่สำเร็จ", Type.WARNING_MESSAGE);
					}
				}else{
					if(addData()){
						try {
							bContainer.commit();
							updateTable();
							Notification.show("บันทึึกสำเร็จ", Type.HUMANIZED_MESSAGE);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}else{
						Notification.show("กรุณาพิมพ์ข้อมูลให้ครบถ้วน", Type.WARNING_MESSAGE);
					}
				}
			}
		});
		buildingForm.addComponent(save);
		
		setTableData();
		
	}
	
	/*ตั้งค่าข้อมูลในต่ารางในแต่ละ Column*/
	private void setTableData(){
		bContainer.addContainerFilter(new Equal(SchoolSchema.SCHOOL_ID,
				UI.getCurrent().getSession().getAttribute(SessionSchema.SCHOOL_ID)));
		
		for(final Object itemId:bContainer.getItemIds()){
			final Item studentItem = bContainer.getItem(itemId);
									
			Button removeButton = new Button(FontAwesome.TRASH_O);
			removeButton.setId(itemId.toString());
			removeButton.addClickListener(new ClickListener() {
				private static final long serialVersionUID = 1L;
				@Override
				public void buttonClick(ClickEvent event) {
					ConfirmDialog.show(UI.getCurrent(), "ลบนักเรียน","คุณต้องการลบนักเรียนนี้ใช่หรือไม่?","ตกลง","ยกเลิก",
				        new ConfirmDialog.Listener() {
							private static final long serialVersionUID = 1L;
							public void onClose(ConfirmDialog dialog) {
				                if (dialog.isConfirmed()) {
				                	if(bContainer.removeItem(itemId)){
				                		try {
				                			table.removeItem(itemId);
						                	table.commit();
											bContainer.commit();
											countTotalData();
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
			addDataItem(studentItem, itemId, removeButton);			
		}
		countTotalData();
		bContainer.removeAllContainerFilters();
	}
	
	/* เพิ่มข้อมูลในตารางในแต่ละ Row */
	private void addDataItem(Item item,Object itemId, Component component){
		table.addItem(new Object[] {
				item.getItemProperty(BuildingSchema.NAME).getValue(), 
				item.getItemProperty(BuildingSchema.ROOM_NUMBER).getValue(), 
				item.getItemProperty(BuildingSchema.CAPACITY).getValue(),
				component
		},itemId);
	}
	
	/* เพิ่มข้อมูลจากฟอร์ม */
	@SuppressWarnings("unchecked")
	private boolean addData(){
		boolean status = false;
		if(!buildingName.getValue().equals("") && 
				!roomName.getValue().equals("") && 
				!capacity.getValue().equals("")){
			
			bContainer.removeAllContainerFilters();
			
			//School item
			Object itemId = bContainer.addItem();
			
			item = bContainer.getItem(itemId);
			item.getItemProperty(SchoolSchema.SCHOOL_ID).setValue(UI.getCurrent().getSession().getAttribute(SessionSchema.SCHOOL_ID));
			item.getItemProperty(BuildingSchema.NAME).setValue(buildingName.getValue());
			item.getItemProperty(BuildingSchema.ROOM_NUMBER).setValue(roomName.getValue());
			item.getItemProperty(BuildingSchema.CAPACITY).setValue(Integer.parseInt(capacity.getValue().toString()));
			
			initFieldGroup();
			status = true;
		}
		return status;
	}
	
	/*อัพเดทค่าในตารางให้เป็นข้อมูลล่าสุด*/
	private void updateTable(){
		/*refresh ค่าใน ตาราง*/
		bContainer.refresh();
		table.removeAllItems();
		setTableData();
		
		/* รีเซ็ตค่าในฟอร์ม */
		item = null;
		initFieldGroup();
	}
	
	/* จัดกลุ่มของ ฟอร์มในการแก้ไข - เพิ่ม ข้อมูล */
	private void initFieldGroup(){		
		buildingBinder = new FieldGroup(item);
		buildingBinder.setBuffered(true);
		buildingBinder.bind(buildingName, BuildingSchema.NAME);
		buildingBinder.bind(roomName, BuildingSchema.ROOM_NUMBER);
		buildingBinder.bind(capacity, BuildingSchema.CAPACITY);
	}	
	
	/* นับจำนวนข้อมูลในตาราง */
	private void countTotalData(){
		table.setColumnFooter(BuildingSchema.NAME, "ทั้งหมด: "+table.size() + " ห้อง");	
	}
}
