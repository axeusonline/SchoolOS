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
import com.ies.schoolos.schema.fundamental.SubjectSchema;
import com.ies.schoolos.type.dynamic.LessonType;
import com.ies.schoolos.type.dynamic.SubjectType;
import com.ies.schoolos.utility.Notification;
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
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.CustomTable.ColumnGenerator;

public class SubjectView extends ContentPage{
	private static final long serialVersionUID = 1L;

	private boolean editMode = false;
	
	private SQLContainer sContainer = Container.getInstance().getSubjectContainer();
	
	private Item item;

	private HorizontalLayout subjectLayout;
	private FilterTable table;
	
	private FieldGroup subjectBinder;
	private FormLayout subjectForm;
	private TextField code;
	private TextField codeNd;
	private TextField name;
	private TextField nameNd;
	private NumberField weight;
	private NumberField hour;
	private ComboBox lessonType;
	private ComboBox subjectType;
	private TextArea description;
	private Button save;	
	
	public SubjectView() {
		super("รายวิชา");
		
		sContainer.refresh();
		sContainer.addContainerFilter(new Equal(SubjectSchema.SCHOOL_ID, UI.getCurrent().getSession().getAttribute(SessionSchema.SCHOOL_ID)));
		setSpacing(true);
		setMargin(true);
		
		buildMainLayout();
	}
	
	private void buildMainLayout(){

		setWidth("100%");
		setHeight("-1px");
		setSpacing(true);

		subjectLayout = new HorizontalLayout();
		subjectLayout.setSizeFull();
		subjectLayout.setSpacing(true);
		addComponent(subjectLayout);
		setExpandRatio(subjectLayout, 1);

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
					item = sContainer.getItem(event.getProperty().getValue());
					initFieldGroup();
				}
			}
		});
		
		table.setFilterDecorator(new TableFilterDecorator());
		table.setFilterGenerator(new TableFilterGenerator());
        table.setFilterBarVisible(true);

		table.setContainerDataSource(sContainer);
	    setFooterData();
		initTableStyle();

		table.setColumnReorderingAllowed(true);
		table.setColumnCollapsingAllowed(true);
		subjectLayout.addComponent(table);
		
		//Form		
		subjectForm = new FormLayout();
		subjectForm.setSpacing(true);
		subjectForm.setStyleName("border-white");
		subjectLayout.addComponent(subjectForm);
		
		Label formLab = new Label("รายวิชา");
		subjectForm.addComponent(formLab);
		
		code = new TextField("รหัสวิชา");
		code.setInputPrompt("รหัสวิชา");
		code.setNullRepresentation("");
		code.setImmediate(false);
		code.setWidth("-1px");
		code.setHeight("-1px");
		subjectForm.addComponent(code);
		
		codeNd = new TextField("รหัสวิชา ภาษาที่สอง");
		codeNd.setInputPrompt("รหัสวิชา ภาษาที่สอง");
		codeNd.setNullRepresentation("");
		codeNd.setImmediate(false);
		codeNd.setWidth("-1px");
		codeNd.setHeight("-1px");
		subjectForm.addComponent(codeNd);
		
		name = new TextField("ชื่อวิชา");
		name.setInputPrompt("ชื่อวิชา");
		name.setNullRepresentation("");
		name.setImmediate(false);
		name.setRequired(true);
		name.setWidth("-1px");
		name.setHeight("-1px");
		subjectForm.addComponent(name);
		
		nameNd = new TextField("ชื่อวิชา ภาษาที่สอง");
		nameNd.setInputPrompt("ชื่อวิชา ภาษาที่สอง");
		nameNd.setNullRepresentation("");
		nameNd.setImmediate(false);
		nameNd.setWidth("-1px");
		nameNd.setHeight("-1px");
		subjectForm.addComponent(nameNd);
		
		weight = new NumberField("น้ำหนัก");
		weight.setInputPrompt("น้ำหนัก");
		weight.setNullRepresentation("");
		weight.setImmediate(false);
		weight.setWidth("-1px");
		weight.setHeight("-1px");
		subjectForm.addComponent(weight);
		
		hour = new NumberField("ชั่วโมงที่สอน");
		hour.setInputPrompt("ชั่วโมงที่สอน");
		hour.setNullRepresentation("");
		hour.setImmediate(false);
		hour.setWidth("-1px");
		hour.setHeight("-1px");
		subjectForm.addComponent(hour);
		
		lessonType = new ComboBox("สาระการเรียนรู็",new LessonType());
		lessonType.setInputPrompt("กรุณาเลือก");
		lessonType.setItemCaptionPropertyId("name");
		lessonType.setImmediate(true);
		lessonType.setNullSelectionAllowed(false);
		lessonType.setRequired(true);
		lessonType.setWidth("-1px");
		lessonType.setHeight("-1px");
		lessonType.setFilteringMode(FilteringMode.CONTAINS);
		subjectForm.addComponent(lessonType);
		
		subjectType = new ComboBox("ประเภทวิชา",new SubjectType());
		subjectType.setInputPrompt("กรุณาเลือก");
		subjectType.setItemCaptionPropertyId("name");
		subjectType.setImmediate(true);
		subjectType.setNullSelectionAllowed(false);
		subjectType.setRequired(true);
		subjectType.setWidth("-1px");
		subjectType.setHeight("-1px");
		subjectType.setFilteringMode(FilteringMode.CONTAINS);
		subjectForm.addComponent(subjectType);
		
		description = new TextArea("รายละเอียด");
		description.setInputPrompt("รายละเอียดเพิ่มเติม");
		description.setImmediate(false);
		description.setWidth("-1px");
		description.setHeight("-1px");
		description.setNullRepresentation("");
		subjectForm.addComponent(description);
		
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
						subjectBinder.commit();
						sContainer.commit();
						editMode = false;
						Notification.show("บันทึกสำเร็จ", Type.HUMANIZED_MESSAGE);
					}else{
						sContainer.removeAllContainerFilters();
						if(!subjectBinder.isValid()){
							Notification.show("กรุณากรอกข้อมูลให้ครบถ้วน", Type.WARNING_MESSAGE);
							return;
						}
							
						if(!saveFormData())
							return;
						
						sContainer.addContainerFilter(new Equal(SubjectSchema.SCHOOL_ID, UI.getCurrent().getSession().getAttribute(SessionSchema.SCHOOL_ID)));
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
		subjectForm.addComponent(save);
		
		initFieldGroup();
	}
	
	/* ตั้งค่ารูปแบบแสดงของตาราง */
	private void initTableStyle(){		
		table.setColumnHeader(SubjectSchema.CODE, "รหัสวิชา");
		table.setColumnHeader(SubjectSchema.NAME, "ชื่อวิชา");
		table.setColumnHeader(SubjectSchema.WEIGHT, "น้ำหนัก");
		table.setColumnHeader(SubjectSchema.LESSON_TYPE, "สาระการเรียนรู็");
		table.setColumnHeader(SubjectSchema.SUBJECT_TYPE, "ประเภทวิชา");
		
		table.setVisibleColumns(
				SubjectSchema.CODE, 
				SubjectSchema.NAME,
				SubjectSchema.WEIGHT, 
				SubjectSchema.LESSON_TYPE,
				SubjectSchema.SUBJECT_TYPE);
		
		setColumnGenerator(SubjectSchema.LESSON_TYPE, SubjectSchema.SUBJECT_TYPE, "");
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
						if(SubjectSchema.LESSON_TYPE.equals(propertyId))
							value = LessonType.getNameTh(Integer.parseInt(item.getItemProperty(propertyId).getValue().toString()));
						else if(SubjectSchema.SUBJECT_TYPE.equals(propertyId))
							value = SubjectType.getNameTh(Integer.parseInt(item.getItemProperty(propertyId).getValue().toString()));
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
			                	if(sContainer.removeItem(itemId)){
			                		try {
										sContainer.commit();
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
		table.setColumnFooter(SubjectSchema.CODE, "ทั้งหมด: "+ table.size() + " วิชา");
	}
	
	/* จัดกลุ่มของ ฟอร์มในการแก้ไข - เพิ่ม ข้อมูล */
	private void initFieldGroup(){		
		subjectBinder = new FieldGroup(item);
		subjectBinder.setBuffered(true);
		subjectBinder.bind(code, SubjectSchema.CODE);
		subjectBinder.bind(codeNd, SubjectSchema.CODE_ND);
		subjectBinder.bind(name, SubjectSchema.NAME);
		subjectBinder.bind(nameNd, SubjectSchema.NAME_ND);
		subjectBinder.bind(weight, SubjectSchema.WEIGHT);
		subjectBinder.bind(hour, SubjectSchema.HOURS);
		subjectBinder.bind(lessonType, SubjectSchema.LESSON_TYPE);
		subjectBinder.bind(subjectType, SubjectSchema.SUBJECT_TYPE);
		subjectBinder.bind(description, SubjectSchema.DESCRIPTION);
	}
	
	/* กำหนดค่าภายใน FieldGroup ไปยัง Item */
	@SuppressWarnings({ "unchecked"})
	private boolean saveFormData(){
		try {				
			/* เพิ่มข้อมูล */
			Object tmpItem = sContainer.addItem();
			Item item = sContainer.getItem(tmpItem);
			for(Field<?> field: subjectBinder.getFields()){
				/* หาชนิดตัวแปร ของข้อมูลภายใน Database ของแต่ละ Field */
				Class<?> clazz = item.getItemProperty(subjectBinder.getPropertyId(field)).getType();
				String className = clazz.getName();;
				Object value = null;
				if(subjectBinder.getField(subjectBinder.getPropertyId(field)).getValue() != null && 
						!subjectBinder.getField(subjectBinder.getPropertyId(field)).getValue().equals("")){
					/* ตรวจสอบ Class ที่ต้องแปลงที่ได้จากการตรวจสอบภายใน Database จาก item.getItemProperty(subjectBinder.getPropertyId(field)).getType()
					 *  กรณั เป็น Double ก็แปลง Object ด้วย parseDouble ซึ่งค่าที่แปลงต้องไม่เป็น Null
					 *  กรณั เป็น Integer ก็แปลง Object ด้วย parseInt ซึ่งค่าที่แปลงต้องไม่เป็น Null
					 *    */

					if(clazz == Double.class){
						value = Double.parseDouble(subjectBinder.getField(subjectBinder.getPropertyId(field)).getValue().toString());
					}else if(clazz == Integer.class){
						value = Integer.parseInt(subjectBinder.getField(subjectBinder.getPropertyId(field)).getValue().toString());
					}else{
						value = subjectBinder.getField(subjectBinder.getPropertyId(field)).getValue();
					}
				}
				
				Object data = Class.forName(className).cast(value);
				item.getItemProperty(subjectBinder.getPropertyId(field)).setValue(data);
			}
			item.getItemProperty(SubjectSchema.SCHOOL_ID).setValue(UI.getCurrent().getSession().getAttribute(SessionSchema.SCHOOL_ID));
			CreateModifiedSchema.setCreateAndModified(item);
			sContainer.commit();
			
			return true;
		} catch (Exception e) {
			Notification.show("บันทึกไม่สำเร็จ", Type.WARNING_MESSAGE);
			e.printStackTrace();
			return false;
		}
	}
}
