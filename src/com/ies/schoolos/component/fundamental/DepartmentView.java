package com.ies.schoolos.component.fundamental;

import org.tepi.filtertable.FilterTable;
import org.vaadin.dialogs.ConfirmDialog;

import com.ies.schoolos.component.ui.ContentPage;
import com.ies.schoolos.container.Container;
import com.ies.schoolos.filter.TableFilterDecorator;
import com.ies.schoolos.filter.TableFilterGenerator;
import com.ies.schoolos.schema.CreateModifiedSchema;
import com.ies.schoolos.schema.SessionSchema;
import com.ies.schoolos.schema.fundamental.DepartmentSchema;
import com.ies.schoolos.utility.Notification;
import com.vaadin.data.Item;
import com.vaadin.data.Container.ItemSetChangeEvent;
import com.vaadin.data.Container.ItemSetChangeListener;
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
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.CustomTable.ColumnGenerator;

public class DepartmentView extends ContentPage{
	private static final long serialVersionUID = 1L;
	
	private boolean editMode = false;
	
	private SQLContainer dContainer = Container.getDepartmentContainer();
	
	private Item item;

	private HorizontalLayout departmentLayout;
	private FilterTable table;
	
	private FieldGroup departmentBinder;
	private FormLayout departmentForm;
	private TextField name;
	private TextField nameNd;
	private Button save;	
	
	public DepartmentView() {
		super("แผนก");
		dContainer.refresh();
		
		setSizeFull();
		setSpacing(true);
		setMargin(true);
		fetchData();
		buildMainLayout();
		setFooterData();
	}
	
	private void buildMainLayout(){
		departmentLayout = new HorizontalLayout();
		departmentLayout.setSizeFull();
		departmentLayout.setSpacing(true);
		addComponent(departmentLayout);
		setExpandRatio(departmentLayout, 1);

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
					item = dContainer.getItem(event.getProperty().getValue());
					initFieldGroup();
				}
			}
		});
		table.addItemSetChangeListener(new ItemSetChangeListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void containerItemSetChange(ItemSetChangeEvent event) {
				setFooterData();
			}
		});
		table.setFilterDecorator(new TableFilterDecorator());
		table.setFilterGenerator(new TableFilterGenerator());
        table.setFilterBarVisible(true);

		table.setContainerDataSource(dContainer);
	    setFooterData();
		initTableStyle();

		table.setColumnReorderingAllowed(true);
		table.setColumnCollapsingAllowed(true);
		departmentLayout.addComponent(table);
		departmentLayout.setExpandRatio(table,1);
		
		//Form		
		departmentForm = new FormLayout();
		departmentForm.setSpacing(true);
		departmentForm.setMargin(true);
		departmentForm.setStyleName("border-white");
		departmentLayout.addComponent(departmentForm);
		departmentLayout.setExpandRatio(departmentForm,(float)1.2);
		
		Label formLab = new Label("แผนก");
		departmentForm.addComponent(formLab);
		
		name = new TextField("ชื่อแผนก");
		name.setInputPrompt("ชื่อแผนก");
		name.setNullRepresentation("");
		name.setImmediate(false);
		name.setRequired(true);
		name.setWidth("-1px");
		name.setHeight("-1px");
		departmentForm.addComponent(name);
		
		nameNd = new TextField("ชื่อแผนก ภาษาที่สอง");
		nameNd.setInputPrompt("ชื่อแผนก ภาษาที่สอง");
		nameNd.setNullRepresentation("");
		nameNd.setImmediate(false);
		nameNd.setWidth("-1px");
		nameNd.setHeight("-1px");
		departmentForm.addComponent(nameNd);
		
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
						departmentBinder.commit();
						dContainer.commit();
						editMode = false;
						Notification.show("บันทึกสำเร็จ", Type.HUMANIZED_MESSAGE);
					}else{
						if(!departmentBinder.isValid()){
							Notification.show("กรุณากรอกข้อมูลให้ครบถ้วน", Type.WARNING_MESSAGE);
							return;
						}
							
						dContainer.removeAllContainerFilters();
						if(!saveFormData())
							return;
						
						dContainer.addContainerFilter(new Equal(DepartmentSchema.SCHOOL_ID, SessionSchema.getSchoolID()));
					}
					item = null;
					save.setCaption("บันทึก");
					initFieldGroup();
					Notification.show("บันทึกสำเร็จ", Type.HUMANIZED_MESSAGE);
				} catch (Exception e) {
					e.printStackTrace();
					Notification.show("บันทึกไม่สำเร็จ", Type.WARNING_MESSAGE);
				}
			}
		});
		departmentForm.addComponent(save);
		
		initFieldGroup();
	}
	
	/* ตั้งค่ารูปแบบแสดงของตาราง */
	private void initTableStyle(){		
		table.setColumnHeader(DepartmentSchema.NAME, "ชื่อแผนก");
		table.setColumnHeader(DepartmentSchema.NAME_ND, "ชื่อแผนก (ภาษาที่สอง)");
		
		table.setVisibleColumns(
				DepartmentSchema.NAME,
				DepartmentSchema.NAME_ND);
		
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
				ConfirmDialog.show(UI.getCurrent(), "ลบแผนก","คุณต้องการลบแผนกนี้ใช่หรือไม่?","ตกลง","ยกเลิก",
			        new ConfirmDialog.Listener() {
						private static final long serialVersionUID = 1L;
						public void onClose(ConfirmDialog dialog) {
			                if (dialog.isConfirmed()) {
			                	if(dContainer.removeItem(itemId)){
			                		try {
										dContainer.commit();
										setFooterData();
									} catch (Exception e) {
										Notification.show("ลบข้อมูลไม่สำเร็จ กรุณาลองใหม่อีกครั้ง", Type.WARNING_MESSAGE);
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
		table.setColumnFooter(DepartmentSchema.NAME, "ทั้งหมด: "+ table.size() + " แผนก");
	}
	
	/* จัดกลุ่มของ ฟอร์มในการแก้ไข - เพิ่ม ข้อมูล */
	private void initFieldGroup(){		
		departmentBinder = new FieldGroup(item);
		departmentBinder.setBuffered(true);
		departmentBinder.bind(name, DepartmentSchema.NAME);
		departmentBinder.bind(nameNd, DepartmentSchema.NAME_ND);
	}
	
	/* กำหนดค่าภายใน FieldGroup ไปยัง Item */
	@SuppressWarnings({ "unchecked"})
	private boolean saveFormData(){
		try {				
			/* เพิ่มข้อมูล */
			Object tmpItem = dContainer.addItem();
			Item item = dContainer.getItem(tmpItem);
			for(Field<?> field: departmentBinder.getFields()){
				/* หาชนิดตัวแปร ของข้อมูลภายใน Database ของแต่ละ Field */
				Class<?> clazz = item.getItemProperty(departmentBinder.getPropertyId(field)).getType();
				String className = clazz.getName();;
				Object value = null;
				if(departmentBinder.getField(departmentBinder.getPropertyId(field)).getValue() != null && 
						!departmentBinder.getField(departmentBinder.getPropertyId(field)).getValue().equals("")){
					/* ตรวจสอบ Class ที่ต้องแปลงที่ได้จากการตรวจสอบภายใน Database จาก item.getItemProperty(departmentBinder.getPropertyId(field)).getType()
					 *  กรณั เป็น Double ก็แปลง Object ด้วย parseDouble ซึ่งค่าที่แปลงต้องไม่เป็น Null
					 *  กรณั เป็น Integer ก็แปลง Object ด้วย parseInt ซึ่งค่าที่แปลงต้องไม่เป็น Null
					 *    */

					if(clazz == Double.class){
						value = Double.parseDouble(departmentBinder.getField(departmentBinder.getPropertyId(field)).getValue().toString());
					}else if(clazz == Integer.class){
						value = Integer.parseInt(departmentBinder.getField(departmentBinder.getPropertyId(field)).getValue().toString());
					}else{
						value = departmentBinder.getField(departmentBinder.getPropertyId(field)).getValue();
					}
				}
				
				Object data = Class.forName(className).cast(value);
				item.getItemProperty(departmentBinder.getPropertyId(field)).setValue(data);
			}
			item.getItemProperty(DepartmentSchema.SCHOOL_ID).setValue(SessionSchema.getSchoolID());
			CreateModifiedSchema.setCreateAndModified(item);
			dContainer.commit();
			setFooterData();
			return true;
		} catch (Exception e) {
			Notification.show("บันทึกไม่สำเร็จ", Type.WARNING_MESSAGE);
			e.printStackTrace();
			return false;
		}
	}
	
	private void fetchData(){
		dContainer.addContainerFilter(new Equal(DepartmentSchema.SCHOOL_ID, SessionSchema.getSchoolID()));
	}
}
