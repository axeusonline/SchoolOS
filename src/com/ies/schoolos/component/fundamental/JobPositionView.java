package com.ies.schoolos.component.fundamental;

import org.tepi.filtertable.FilterTable;
import org.vaadin.dialogs.ConfirmDialog;

import com.ies.schoolos.component.ui.ContentPage;
import com.ies.schoolos.container.Container;
import com.ies.schoolos.filter.TableFilterDecorator;
import com.ies.schoolos.filter.TableFilterGenerator;
import com.ies.schoolos.schema.CreateModifiedSchema;
import com.ies.schoolos.schema.SessionSchema;
import com.ies.schoolos.schema.fundamental.JobPositionSchema;
import com.ies.schoolos.utility.Notification;
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
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.CustomTable.ColumnGenerator;

public class JobPositionView extends ContentPage{
	private static final long serialVersionUID = 1L;
	
	private boolean editMode = false;
	
	private SQLContainer jContainer = Container.getJobPositionContainer();
	
	private Item item;

	private HorizontalLayout jobPositionLayout;
	private FilterTable table;
	
	private FieldGroup jobPositionBinder;
	private FormLayout jobPositionForm;
	private TextField name;
	private TextField nameNd;
	private Button save;	
	
	public JobPositionView() {
		super("ตำแหน่ง");
		jContainer.refresh();
		
		setSpacing(true);
		setMargin(true);
		fetchData();
		buildMainLayout();
		setFooterData();
	}
	
	private void buildMainLayout(){

		setWidth("100%");
		setHeight("-1px");
		setSpacing(true);

		jobPositionLayout = new HorizontalLayout();
		jobPositionLayout.setSizeFull();
		jobPositionLayout.setSpacing(true);
		addComponent(jobPositionLayout);
		setExpandRatio(jobPositionLayout,(float)1.2);

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
					item = jContainer.getItem(event.getProperty().getValue());
					initFieldGroup();
				}
			}
		});
		
		table.setFilterDecorator(new TableFilterDecorator());
		table.setFilterGenerator(new TableFilterGenerator());
        table.setFilterBarVisible(true);

		table.setContainerDataSource(jContainer);
	    setFooterData();
		initTableStyle();

		table.setColumnReorderingAllowed(true);
		table.setColumnCollapsingAllowed(true);
		jobPositionLayout.addComponent(table);
		jobPositionLayout.setExpandRatio(table,2);
		
		//Form		
		jobPositionForm = new FormLayout();
		jobPositionForm.setSpacing(true);
		jobPositionForm.setMargin(true);
		jobPositionForm.setStyleName("border-white");
		jobPositionLayout.addComponent(jobPositionForm);
		jobPositionLayout.setExpandRatio(jobPositionForm,1);
		
		Label formLab = new Label("ตำแหน่ง");
		jobPositionForm.addComponent(formLab);
		
		name = new TextField("ชื่อตำแหน่ง");
		name.setInputPrompt("ชื่อตำแหน่ง");
		name.setNullRepresentation("");
		name.setImmediate(false);
		name.setRequired(true);
		name.setWidth("-1px");
		name.setHeight("-1px");
		jobPositionForm.addComponent(name);
		
		nameNd = new TextField("ชื่อตำแหน่ง ภาษาที่สอง");
		nameNd.setInputPrompt("ชื่อตำแหน่ง ภาษาที่สอง");
		nameNd.setNullRepresentation("");
		nameNd.setImmediate(false);
		nameNd.setWidth("-1px");
		nameNd.setHeight("-1px");
		jobPositionForm.addComponent(nameNd);
		
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
						jobPositionBinder.commit();
						jContainer.commit();
						editMode = false;
						Notification.show("บันทึกสำเร็จ", Type.HUMANIZED_MESSAGE);
					}else{
						if(!jobPositionBinder.isValid()){
							Notification.show("กรุณากรอกข้อมูลให้ครบถ้วน", Type.WARNING_MESSAGE);
							return;
						}
							
						jContainer.removeAllContainerFilters();
						if(!saveFormData())
							return;
						
						jContainer.addContainerFilter(new Equal(JobPositionSchema.SCHOOL_ID, SessionSchema.getSchoolID()));
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
		jobPositionForm.addComponent(save);
		
		initFieldGroup();
	}
	
	/* ตั้งค่ารูปแบบแสดงของตาราง */
	private void initTableStyle(){		
		table.setColumnHeader(JobPositionSchema.NAME, "ชื่อตำแหน่ง");
		table.setColumnHeader(JobPositionSchema.NAME_ND, "ชื่อตำแหน่ง (ภาษาที่สอง)");
		
		table.setVisibleColumns(
				JobPositionSchema.NAME,
				JobPositionSchema.NAME_ND);
		
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
				ConfirmDialog.show(UI.getCurrent(), "ลบตำแหน่ง","คุณต้องการลบตำแหน่งนี้ใช่หรือไม่?","ตกลง","ยกเลิก",
			        new ConfirmDialog.Listener() {
						private static final long serialVersionUID = 1L;
						public void onClose(ConfirmDialog dialog) {
			                if (dialog.isConfirmed()) {
			                	if(jContainer.removeItem(itemId)){
			                		try {
										jContainer.commit();
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
		table.setColumnFooter(JobPositionSchema.NAME, "ทั้งหมด: "+ table.size() + " ตำแหน่ง");
	}
	
	/* จัดกลุ่มของ ฟอร์มในการแก้ไข - เพิ่ม ข้อมูล */
	private void initFieldGroup(){		
		jobPositionBinder = new FieldGroup(item);
		jobPositionBinder.setBuffered(true);
		jobPositionBinder.bind(name, JobPositionSchema.NAME);
		jobPositionBinder.bind(nameNd, JobPositionSchema.NAME_ND);
	}
	
	/* กำหนดค่าภายใน FieldGroup ไปยัง Item */
	@SuppressWarnings({ "unchecked"})
	private boolean saveFormData(){
		try {				
			/* เพิ่มข้อมูล */
			Object tmpItem = jContainer.addItem();
			Item item = jContainer.getItem(tmpItem);
			for(Field<?> field: jobPositionBinder.getFields()){
				/* หาชนิดตัวแปร ของข้อมูลภายใน Database ของแต่ละ Field */
				Class<?> clazz = item.getItemProperty(jobPositionBinder.getPropertyId(field)).getType();
				String className = clazz.getName();;
				Object value = null;
				if(jobPositionBinder.getField(jobPositionBinder.getPropertyId(field)).getValue() != null && 
						!jobPositionBinder.getField(jobPositionBinder.getPropertyId(field)).getValue().equals("")){
					/* ตรวจสอบ Class ที่ต้องแปลงที่ได้จากการตรวจสอบภายใน Database จาก item.getItemProperty(jobPositionBinder.getPropertyId(field)).getType()
					 *  กรณั เป็น Double ก็แปลง Object ด้วย parseDouble ซึ่งค่าที่แปลงต้องไม่เป็น Null
					 *  กรณั เป็น Integer ก็แปลง Object ด้วย parseInt ซึ่งค่าที่แปลงต้องไม่เป็น Null
					 *    */

					if(clazz == Double.class){
						value = Double.parseDouble(jobPositionBinder.getField(jobPositionBinder.getPropertyId(field)).getValue().toString());
					}else if(clazz == Integer.class){
						value = Integer.parseInt(jobPositionBinder.getField(jobPositionBinder.getPropertyId(field)).getValue().toString());
					}else{
						value = jobPositionBinder.getField(jobPositionBinder.getPropertyId(field)).getValue();
					}
				}
				
				Object data = Class.forName(className).cast(value);
				item.getItemProperty(jobPositionBinder.getPropertyId(field)).setValue(data);
			}
			item.getItemProperty(JobPositionSchema.SCHOOL_ID).setValue(SessionSchema.getSchoolID());
			CreateModifiedSchema.setCreateAndModified(item);
			jContainer.commit();
			setFooterData();
			return true;
		} catch (Exception e) {
			Notification.show("บันทึกไม่สำเร็จ", Type.WARNING_MESSAGE);
			e.printStackTrace();
			return false;
		}
	}
	
	private void fetchData(){
		jContainer.addContainerFilter(new Equal(JobPositionSchema.SCHOOL_ID, SessionSchema.getSchoolID()));
	}
}
