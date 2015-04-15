package com.ies.schoolos.component.fundamental;

import org.tepi.filtertable.FilterTable;
import org.vaadin.dialogs.ConfirmDialog;

import com.ies.schoolos.component.ui.ContentPage;
import com.ies.schoolos.component.ui.NumberField;
import com.ies.schoolos.container.Container;
import com.ies.schoolos.filter.TableFilterDecorator;
import com.ies.schoolos.filter.TableFilterGenerator;
import com.ies.schoolos.schema.CreateModifiedSchema;
import com.ies.schoolos.schema.SessionSchema;
import com.ies.schoolos.schema.fundamental.ClassRoomSchema;
import com.ies.schoolos.type.ClassRange;
import com.ies.schoolos.type.ClassYear;
import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.filter.Compare.Equal;
import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.data.util.sqlcontainer.TemporaryRowId;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
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

public class ClassRoomView extends ContentPage{
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
		classContainer.addContainerFilter(new Equal(ClassRoomSchema.SCHOOL_ID, UI.getCurrent().getSession().getAttribute(SessionSchema.SCHOOL_ID)));
		setSpacing(true);
		setMargin(true);
		
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
					item = classContainer.getItem(event.getProperty().getValue());
					initFieldGroup();
				}
			}
		});
		
		table.setFilterDecorator(new TableFilterDecorator());
		table.setFilterGenerator(new TableFilterGenerator());
        table.setFilterBarVisible(true);

		table.setContainerDataSource(classContainer);
	    setFooterData();
		initTableStyle();

		table.setColumnReorderingAllowed(true);
		table.setColumnCollapsingAllowed(true);
		classRoomLayout.addComponent(table);
		
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
					
					StringBuilder sqlBuilder = new StringBuilder();
					sqlBuilder.append(" SELECT MAX(" + ClassRoomSchema.NUMBER + ") AS " + ClassRoomSchema.NUMBER);
					sqlBuilder.append(" FROM " + ClassRoomSchema.TABLE_NAME);
					sqlBuilder.append(" WHERE "	+ ClassRoomSchema.CLASS_YEAR + "=" + classYear );
					sqlBuilder.append(" AND " + ClassRoomSchema.SCHOOL_ID + "=" + UI.getCurrent().getSession().getAttribute(SessionSchema.SCHOOL_ID));
					
					
					SQLContainer freeContainer = Container.getInstance().getFreeFormContainer(sqlBuilder.toString(),ClassRoomSchema.NUMBER);
					
					Item item = freeContainer.getItem(freeContainer.getIdByIndex(0));
					
					String maxNumber = "1";
					if(item.getItemProperty(ClassRoomSchema.NUMBER).getValue() != null){
						maxNumber = (Integer.parseInt(item.getItemProperty(ClassRoomSchema.NUMBER).getValue().toString()) + 1) + "";
						
					}

					/* ลบ WHERE ออกจาก Query เพื่อป้องกันการค้างของคำสั่่งจากการทำงานอื่นที่เรียกตัวแปรไปใช้ */
					freeContainer.removeAllContainerFilters();
					
					number.setValue(maxNumber);
					
					classRange.setEnabled(false);
					number.setEnabled(false);
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
				try {
					/* ตรวจสอบสถานะการจัดการข้อมูล
					 *  กรณีเป็น แก้ไข จะทำการ Update โดยใช้ข้อมูลในฟอร์มเดิม
					 *  กรณี เป็น เพิ่ม จะทำการ Inser โดยใช้ข้อมูลใหม่ที่กรอกในฟอร์ม */
					if(editMode){
						classRoomBinder.commit();
						classContainer.commit();
						editMode = false;
						Notification.show("บันทึกสำเร็จ", Type.HUMANIZED_MESSAGE);
					}else{
						classContainer.removeAllContainerFilters();
						if(!classRoomBinder.isValid()){
							Notification.show("กรุณากรอกข้อมูลให้ครบถ้วน", Type.WARNING_MESSAGE);
							return;
						}
							
						if(!saveFormData())
							return;
						
						classContainer.addContainerFilter(new Equal(ClassRoomSchema.SCHOOL_ID, UI.getCurrent().getSession().getAttribute(SessionSchema.SCHOOL_ID)));
					}
					item = null;
					initFieldGroup();
					Notification.show("บันทึึกสำเร็จ", Type.HUMANIZED_MESSAGE);
				} catch (Exception e) {
					e.printStackTrace();
					Notification.show("บันทึกไม่สำเร็จ", Type.HUMANIZED_MESSAGE);
				}
			}
		});
		classRoomForm.addComponent(save);
		
		initFieldGroup();
		
	}
	
	/* ตั้งค่ารูปแบบแสดงของตาราง */
	private void initTableStyle(){		
		table.setColumnHeader(ClassRoomSchema.CLASS_YEAR, "ชั้นปี");
		table.setColumnHeader(ClassRoomSchema.CLASS_RANGE,"ช่วงชั้น");
		table.setColumnHeader(ClassRoomSchema.NUMBER, "หมายเลขห้อง");
		table.setColumnHeader(ClassRoomSchema.NAME,"ชื่อห้อง");
		table.setColumnHeader(ClassRoomSchema.CAPACITY, "จำนวนคนสูงสุด");

		table.setVisibleColumns(
				ClassRoomSchema.CLASS_YEAR, 
				ClassRoomSchema.CLASS_RANGE,
				ClassRoomSchema.NUMBER,
				ClassRoomSchema.NAME,
				ClassRoomSchema.CAPACITY);
		
		setColumnGenerator(ClassRoomSchema.CLASS_YEAR, ClassRoomSchema.CLASS_RANGE, "");
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
						if(ClassRoomSchema.CLASS_YEAR.equals(propertyId))
							value = ClassYear.getNameTh(Integer.parseInt(item.getItemProperty(propertyId).getValue().toString()));
						else if(ClassRoomSchema.CLASS_RANGE.equals(propertyId))
							value = ClassRange.getNameTh(Integer.parseInt(item.getItemProperty(propertyId).getValue().toString()));
						else if("".equals(propertyId))
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
				ConfirmDialog.show(UI.getCurrent(), "ลบรายวิชา","คุณต้องการลบรายวิชานี้ใช่หรือไม่?","ตกลง","ยกเลิก",
			        new ConfirmDialog.Listener() {
						private static final long serialVersionUID = 1L;
						public void onClose(ConfirmDialog dialog) {
			                if (dialog.isConfirmed()) {
			                	if(classContainer.removeItem(itemId)){
			                		try {
										classContainer.commit();
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
		table.setColumnFooter(ClassRoomSchema.CLASS_YEAR, "ทั้งหมด: "+ table.size() + " วิชา");
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
	
	/* กำหนดค่าภายใน FieldGroup ไปยัง Item */
	@SuppressWarnings({ "unchecked"})
	private boolean saveFormData(){
		try {				
			/* เพิ่มข้อมูล */
			Object tmpItem = classContainer.addItem();
			Item item = classContainer.getItem(tmpItem);
			for(Field<?> field: classRoomBinder.getFields()){
				/* หาชนิดตัวแปร ของข้อมูลภายใน Database ของแต่ละ Field */
				Class<?> clazz = item.getItemProperty(classRoomBinder.getPropertyId(field)).getType();				
				System.err.println(classRoomBinder.getPropertyId(field));
				String className = clazz.getName();;
				Object value = null;
				if(classRoomBinder.getField(classRoomBinder.getPropertyId(field)).getValue() != null && 
						!classRoomBinder.getField(classRoomBinder.getPropertyId(field)).getValue().equals("")){
					/* ตรวจสอบ Class ที่ต้องแปลงที่ได้จากการตรวจสอบภายใน Database จาก item.getItemProperty(classRoomBinder.getPropertyId(field)).getType()
					 *  กรณั เป็น Double ก็แปลง Object ด้วย parseDouble ซึ่งค่าที่แปลงต้องไม่เป็น Null
					 *  กรณั เป็น Integer ก็แปลง Object ด้วย parseInt ซึ่งค่าที่แปลงต้องไม่เป็น Null
					 *    */

					if(clazz == Double.class){
						value = Double.parseDouble(classRoomBinder.getField(classRoomBinder.getPropertyId(field)).getValue().toString());
					}else if(clazz == Integer.class){
						value = Integer.parseInt(classRoomBinder.getField(classRoomBinder.getPropertyId(field)).getValue().toString());
					}else{
						value = classRoomBinder.getField(classRoomBinder.getPropertyId(field)).getValue();
					}
				}
				
				Object data = Class.forName(className).cast(value);
				item.getItemProperty(classRoomBinder.getPropertyId(field)).setValue(data);
			}
			item.getItemProperty(ClassRoomSchema.SCHOOL_ID).setValue(UI.getCurrent().getSession().getAttribute(SessionSchema.SCHOOL_ID));
			CreateModifiedSchema.setCreateAndModified(item);
			classContainer.commit();
			
			return true;
		} catch (Exception e) {
			Notification.show("บันทึกไม่สำเร็จ", Type.WARNING_MESSAGE);
			e.printStackTrace();
			return false;
		}
	}
}
