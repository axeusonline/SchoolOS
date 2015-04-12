package com.ies.schoolos.component.fundamental;

import org.tepi.filtertable.FilterTable;
import org.vaadin.dialogs.ConfirmDialog;

import com.ies.schoolos.component.ui.ContentPage;
import com.ies.schoolos.component.ui.NumberField;
import com.ies.schoolos.container.Container;
import com.ies.schoolos.filter.TableFilterDecorator;
import com.ies.schoolos.filter.TableFilterGenerator;
import com.ies.schoolos.schema.SchoolSchema;
import com.ies.schoolos.schema.SessionSchema;
import com.ies.schoolos.schema.fundamental.ClassRoomSchema;
import com.ies.schoolos.schema.view.StatClassRoomSchema;
import com.ies.schoolos.type.ClassRange;
import com.ies.schoolos.type.ClassYear;
import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.filter.Compare.Equal;
import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
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

public class ClassRoomView  extends ContentPage {
	private static final long serialVersionUID = 1L;

	private boolean editMode = false;
	private SQLContainer classContainer = Container.getInstance().getClassRoomContainer();
	
	private Item item;

	private HorizontalLayout classRoomLayout;
	private FilterTable table;
	
	private FieldGroup classRoomBinder;
	private FormLayout classRoomForm;
	private ComboBox classYear;
	private ComboBox classRange;
	private NumberField number;
	private TextField name;
	private NumberField capacity;
	private Button save;	
	
	public ClassRoomView() {
		super("ชั้นเรียน");
		
		classContainer.refresh();
		
		buildMainLayout();
	}
	
	private void buildMainLayout(){
		setSizeFull();
		setSpacing(true);

		classRoomLayout = new HorizontalLayout();
		classRoomLayout.setSizeFull();
		classRoomLayout.setSpacing(true);
		addComponent(classRoomLayout);
		setExpandRatio(classRoomLayout, 1);

		//Table
		table = new FilterTable();
		table.setSelectable(true);
		table.setFooterVisible(true);   
		table.setSizeFull();
		table.addValueChangeListener(new ValueChangeListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				if(event.getProperty().getValue() != null){
					editMode = true;
					save.setCaption("แก้ไข");
					item = classContainer.getItem(event.getProperty().getValue());
					initFieldGroup();
				}
			}
		});
		classRoomLayout.addComponent(table);

		table.addContainerProperty(ClassRoomSchema.CLASS_YEAR, String.class, null);
		table.addContainerProperty(ClassRoomSchema.CLASS_RANGE, String.class, null);
		table.addContainerProperty(ClassRoomSchema.NUMBER, Integer.class, null);
		table.addContainerProperty(ClassRoomSchema.NAME, String.class, null);
		table.addContainerProperty(ClassRoomSchema.CAPACITY, Integer.class, null);
		table.addContainerProperty("note", Button.class, null);

		table.setFilterDecorator(new TableFilterDecorator());
		table.setFilterGenerator(new TableFilterGenerator());
        table.setFilterBarVisible(true);
        
		table.setColumnAlignment(ClassRoomSchema.CLASS_YEAR,Align.CENTER);
		table.setColumnAlignment(ClassRoomSchema.CLASS_RANGE,Align.CENTER);
		table.setColumnAlignment(ClassRoomSchema.NUMBER,Align.CENTER);
		table.setColumnAlignment(ClassRoomSchema.NAME,Align.CENTER);
		table.setColumnAlignment(ClassRoomSchema.CAPACITY,Align.CENTER);
		table.setColumnAlignment("note",Align.CENTER);

		table.setColumnHeader(ClassRoomSchema.CLASS_YEAR, "ชั้นปี");
		table.setColumnHeader(ClassRoomSchema.CLASS_RANGE,"ช่วงชั้น");
		table.setColumnHeader(ClassRoomSchema.NUMBER, "หมายเลขห้อง");
		table.setColumnHeader(ClassRoomSchema.NAME,"ชื่อห้อง");
		table.setColumnHeader(ClassRoomSchema.CAPACITY, "จำนวนคนสูงสุด");
		table.setColumnHeader("note", "");
		table.setVisibleColumns(
				ClassRoomSchema.CLASS_YEAR, 
				ClassRoomSchema.CLASS_RANGE,
				ClassRoomSchema.NUMBER,
				ClassRoomSchema.NAME,
				ClassRoomSchema.CAPACITY,
				"note");
		table.setFooterVisible(true);
		
		//Form		
		classRoomForm = new FormLayout();
		classRoomForm.setSpacing(true);
		classRoomForm.setStyleName("border-white");
		classRoomLayout.addComponent(classRoomForm);
		
		Label formLab = new Label("ข้อมูลชั้นเรียน");
		classRoomForm.addComponent(formLab);
		
		classYear = new ComboBox("ชั้นปี",new ClassYear());
		classYear.setInputPrompt("กรุณาเลือก");
		classYear.setItemCaptionPropertyId("name");
		classYear.setImmediate(true);
		classYear.setNullSelectionAllowed(false);
		classYear.setRequired(true);
		classYear.setWidth("-1px");
		classYear.setHeight("-1px");
		classYear.setFilteringMode(FilteringMode.CONTAINS);
		classYear.addValueChangeListener(new ValueChangeListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				if(event.getProperty().getValue() != null){
					int classYear = Integer.parseInt(event.getProperty().getValue().toString());
					/* ตรวจสอบชั้นปี
					 *  กรณีค่าอยู่ระหว่าง 0 - 2 แสดงถึง ช่วงชั้นอนุบาล (0)
					 *  กรณีค่าอยู่ระหว่าง 3 - 8 แสดงถึง ช่วงชั้นประถม (1)
					 *  กรณีค่าอยู่ระหว่าง 9 - 11 แสดงถึง ช่วงชั้นมัธยมต้น (2)
					 *  กรณีค่าอยู่ระหว่าง 12 - 14 แสดงถึง ช่วงชั้นมัธยมปลาย (3)
					 *  กรณีค่าอยู่ระหว่าง 15 - 24 แสดงถึง ช่วงชั้นศาสนา (4)
					 *  */
					if(classYear <= 2){
						classRange.setValue(0);
					}else if(classYear >= 3 && classYear <= 8){
						classRange.setValue(1);
					}else if(classYear >= 9 && classYear <= 11){
						classRange.setValue(2);
					}else if(classYear >= 12 && classYear <= 14){
						classRange.setValue(3);
					}else if(classYear >= 15 && classYear <= 24){
						classRange.setValue(4);
					}
					
					
					SQLContainer freeFormContainer = Container.getInstance().getFreeFormContainer(StatClassRoomSchema.getQuery(classYear), StatClassRoomSchema.MAX_CODE);
					
					int maxNumber = 1;
					for(Object object:freeFormContainer.getItemIds())
						maxNumber = Integer.parseInt(object.toString())+1;
					/* ลบ WHERE ออกจาก Query เพื่อป้องกันการค้างของคำสั่่งจากการทำงานอื่นที่เรียกตัวแปรไปใช้ */
					freeFormContainer.removeAllContainerFilters();
					number.setValue(Integer.toString(maxNumber));
					
					classRange.setReadOnly(true);
					number.setReadOnly(true);
				}
			}
		});
		classRoomForm.addComponent(classYear);

		classRange = new ComboBox("ช่วงชั้น",new ClassRange());
		classRange.setInputPrompt("กรุณาเลือก");
		classRange.setItemCaptionPropertyId("name");
		classRange.setImmediate(true);
		classRange.setNullSelectionAllowed(false);
		classRange.setRequired(true);
		classRange.setWidth("-1px");
		classRange.setHeight("-1px");
		classRange.setFilteringMode(FilteringMode.CONTAINS);
		classRoomForm.addComponent(classRange);

		number = new NumberField("หมายเลขห้อง");
		number.setInputPrompt("หมายเลขห้อง");
		number.setNullRepresentation("");
		number.setImmediate(false);
		number.setRequired(true);
		number.setWidth("-1px");
		number.setHeight("-1px");
		classRoomForm.addComponent(number);
		
		name = new TextField("ชื่อห้อง");
		name.setInputPrompt("ชื่อห้อง");
		name.setNullRepresentation("");
		name.setImmediate(false);
		name.setRequired(true);
		name.setWidth("-1px");
		name.setHeight("-1px");
		classRoomForm.addComponent(name);

		capacity = new NumberField("จำนวนรองรับ");
		capacity.setInputPrompt("จำนวนคนสูงสุด");
		capacity.setNullRepresentation("");
		capacity.setImmediate(false);
		capacity.setRequired(true);
		capacity.setWidth("-1px");
		capacity.setHeight("-1px");
		classRoomForm.addComponent(capacity);
		
		save = new Button("บันทึก", FontAwesome.SAVE);
		save.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event) {
				if(editMode){
					try {
						classRoomBinder.commit();
						classContainer.commit();
						updateTable();
						editMode = false;
						Notification.show("บันทึกสำเร็จ", Type.HUMANIZED_MESSAGE);
					} catch (Exception e) {
						Notification.show("บันทึกไม่สำเร็จ"+e.toString(), Type.WARNING_MESSAGE);
						System.err.println(e.toString());
					}
				}else{
					if(addData()){
						try {
							classContainer.commit();
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
		classRoomForm.addComponent(save);
		
		setTableData();
		
	}
	
	/*ตั้งค่าข้อมูลในต่ารางในแต่ละ Column*/
	private void setTableData(){
		classContainer.addContainerFilter(new Equal(SchoolSchema.SCHOOL_ID,
				UI.getCurrent().getSession().getAttribute(SessionSchema.SCHOOL_ID)));
		
		for(final Object itemId:classContainer.getItemIds()){
			final Item studentItem = classContainer.getItem(itemId);
									
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
				                	if(classContainer.removeItem(itemId)){
				                		try {
				                			table.removeItem(itemId);
						                	table.commit();
											classContainer.commit();
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
		classContainer.removeAllContainerFilters();
	}
	
	/* เพิ่มข้อมูลในตารางในแต่ละ Row */
	private void addDataItem(Item item,Object itemId, Component component){
		table.addItem(new Object[] {
				ClassYear.getNameTh(Integer.parseInt(item.getItemProperty(ClassRoomSchema.CLASS_YEAR).getValue().toString())),
				ClassRange.getNameTh(Integer.parseInt(item.getItemProperty(ClassRoomSchema.CLASS_RANGE).getValue().toString())),
				Integer.parseInt(item.getItemProperty(ClassRoomSchema.NUMBER).getValue().toString()),
				item.getItemProperty(ClassRoomSchema.NAME).getValue(),
				Integer.parseInt(item.getItemProperty(ClassRoomSchema.CAPACITY).getValue().toString()),
				component
		},itemId);
		
		
	}
	
	/* เพิ่มข้อมูลจากฟอร์ม */
	@SuppressWarnings("unchecked")
	private boolean addData(){
		boolean status = false;
		if(!number.getValue().equals("") && 
				!name.getValue().equals("") && 
				!capacity.getValue().equals("")){
			
			classContainer.removeAllContainerFilters();
			
			/* Class item */
			Object itemId = classContainer.addItem();
			
			item = classContainer.getItem(itemId);
			item.getItemProperty(SchoolSchema.SCHOOL_ID).setValue(UI.getCurrent().getSession().getAttribute(SessionSchema.SCHOOL_ID));
			item.getItemProperty(ClassRoomSchema.CLASS_YEAR).setValue(classYear.getValue());
			item.getItemProperty(ClassRoomSchema.CLASS_RANGE).setValue(classRange.getValue());
			item.getItemProperty(ClassRoomSchema.NUMBER).setValue(Integer.parseInt(number.getValue().toString()));
			item.getItemProperty(ClassRoomSchema.NAME).setValue(name.getValue());
			item.getItemProperty(ClassRoomSchema.CAPACITY).setValue(Integer.parseInt(capacity.getValue().toString()));
			
			initFieldGroup();
			status = true;
		}
		return status;
	}
	
	/*อัพเดทค่าในตารางให้เป็นข้อมูลล่าสุด*/
	private void updateTable(){
		/*refresh ค่าใน ตาราง*/
		classContainer.refresh();
		System.err.println("ก่อน");
		table.removeAllItems();
		setTableData();
		
		/* รีเซ็ตค่าในฟอร์ม */
		item = null;
		initFieldGroup();
	}
	
	/* จัดกลุ่มของ ฟอร์มในการแก้ไข - เพิ่ม ข้อมูล */
	private void initFieldGroup(){		
		classRoomBinder = new FieldGroup(item);
		classRoomBinder.setBuffered(true);
		classRoomBinder.bind(classYear, ClassRoomSchema.CLASS_YEAR);
		classRoomBinder.bind(classRange, ClassRoomSchema.CLASS_RANGE);
		classRoomBinder.bind(number, ClassRoomSchema.NUMBER);
		classRoomBinder.bind(name, ClassRoomSchema.NAME);
		classRoomBinder.bind(capacity, ClassRoomSchema.CAPACITY);
	}	
	
	/* นับจำนวนข้อมูลในตาราง */
	private void countTotalData(){
		table.setColumnFooter(ClassRoomSchema.NAME, "ทั้งหมด: "+table.size() + " ห้อง");	
	}
}
