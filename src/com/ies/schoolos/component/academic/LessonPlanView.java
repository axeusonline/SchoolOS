package com.ies.schoolos.component.academic;

import org.tepi.filtertable.FilterTable;
import org.vaadin.dialogs.ConfirmDialog;

import com.ies.schoolos.container.Container;
import com.ies.schoolos.filter.TableFilterDecorator;
import com.ies.schoolos.filter.TableFilterGenerator;
import com.ies.schoolos.schema.CreateModifiedSchema;
import com.ies.schoolos.schema.SessionSchema;
import com.ies.schoolos.schema.academic.LessonPlanSchema;
import com.ies.schoolos.schema.fundamental.ClassRoomSchema;
import com.ies.schoolos.schema.fundamental.SubjectSchema;
import com.ies.schoolos.type.ClassRange;
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
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.CustomTable.ColumnGenerator;

public class LessonPlanView extends VerticalLayout{
	private static final long serialVersionUID = 1L;

	private boolean editMode = false;
	
	private SQLContainer lContainer = Container.getInstance().getLessonPlanContainer();
	
	private Item item;

	private HorizontalLayout lessonPlanLayout;
	private FilterTable table;
	
	private FieldGroup lessonPlanBinder;
	private FormLayout lessonPlanForm;
	private TextField name;
	private ComboBox classRange;
	private TextArea description;
	private Button save;	
	
	public LessonPlanView() {
		lContainer.refresh();
		lContainer.addContainerFilter(new Equal(SubjectSchema.SCHOOL_ID, UI.getCurrent().getSession().getAttribute(SessionSchema.SCHOOL_ID)));
		
		setSpacing(true);
		setMargin(true);
		buildMainLayout();
	}
	
	private void buildMainLayout(){
		setSizeFull();
		setSpacing(true);

		lessonPlanLayout = new HorizontalLayout();
		lessonPlanLayout.setSizeFull();
		lessonPlanLayout.setSpacing(true);
		addComponent(lessonPlanLayout);
		setExpandRatio(lessonPlanLayout, 1);

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
					item = lContainer.getItem(event.getProperty().getValue());
					initFieldGroup();
				}
			}
		});
		
		table.setFilterDecorator(new TableFilterDecorator());
		table.setFilterGenerator(new TableFilterGenerator());
        table.setFilterBarVisible(true);

		table.setContainerDataSource(lContainer);
	    setFooterData();
		initTableStyle();

		table.setColumnReorderingAllowed(true);
		table.setColumnCollapsingAllowed(true);
		lessonPlanLayout.addComponent(table);
		lessonPlanLayout.setExpandRatio(table, 2);
		
		//Form		
		lessonPlanForm = new FormLayout();
		lessonPlanForm.setSpacing(true);
		lessonPlanForm.setStyleName("border-white");
		lessonPlanLayout.addComponent(lessonPlanForm);
		lessonPlanLayout.setExpandRatio(lessonPlanForm, 1);
		
		Label formLab = new Label("แผนการเรียน");
		lessonPlanForm.addComponent(formLab);
		
		name = new TextField("ชื่อแผนการเรียน");
		name.setInputPrompt("ชื่อแผนการเรียน");
		name.setNullRepresentation("");
		name.setImmediate(false);
		name.setWidth("-1px");
		name.setHeight("-1px");
		lessonPlanForm.addComponent(name);
		
		classRange = new ComboBox("ช่วงชั้น",new ClassRange());
		classRange.setInputPrompt("กรุณาเลือก");
		classRange.setItemCaptionPropertyId("name");
		classRange.setImmediate(true);
		classRange.setNullSelectionAllowed(false);
		classRange.setRequired(true);
		classRange.setWidth("-1px");
		classRange.setHeight("-1px");
		classRange.setFilteringMode(FilteringMode.CONTAINS);
		lessonPlanForm.addComponent(classRange);
		
		description = new TextArea("รายละเอียด");
		description.setInputPrompt("รายละเอียด");
		description.setNullRepresentation("");
		description.setImmediate(false);
		description.setWidth("-1px");
		description.setHeight("-1px");
		lessonPlanForm.addComponent(description);
		
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
						lessonPlanBinder.commit();
						lContainer.commit();
						editMode = false;
						Notification.show("บันทึกสำเร็จ", Type.HUMANIZED_MESSAGE);
					}else{
						lContainer.removeAllContainerFilters();
						if(!lessonPlanBinder.isValid()){
							Notification.show("กรุณากรอกข้อมูลให้ครบถ้วน", Type.WARNING_MESSAGE);
							return;
						}
							
						if(!saveFormData())
							return;
						
						lContainer.addContainerFilter(new Equal(SubjectSchema.SCHOOL_ID, UI.getCurrent().getSession().getAttribute(SessionSchema.SCHOOL_ID)));
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
		lessonPlanForm.addComponent(save);
		
		initFieldGroup();
		
	}
	
	/* ตั้งค่ารูปแบบแสดงของตาราง */
	private void initTableStyle(){		
		table.setColumnHeader(LessonPlanSchema.NAME, "ชื่อแผนการเรียน");
		table.setColumnHeader(LessonPlanSchema.CLASS_RANGE,"ช่วงชั้น");
		
		table.setVisibleColumns(
				LessonPlanSchema.NAME, 
				LessonPlanSchema.CLASS_RANGE);
		
		setColumnGenerator(LessonPlanSchema.CLASS_RANGE, "");
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
						if(ClassRoomSchema.CLASS_RANGE.equals(propertyId))
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
		buttonLayout.setSpacing(true);
		
		Button addSubjectButton = new Button("เพิ่มรายวิชา",FontAwesome.INDENT);
		addSubjectButton.setId(itemId.toString());
		addSubjectButton.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 1L;
			@Override
			public void buttonClick(ClickEvent event) {
				Object lessonPlanId = item.getItemProperty(LessonPlanSchema.LESSON_PLAN_ID).getValue();
				Object classRange = item.getItemProperty(LessonPlanSchema.CLASS_RANGE).getValue();
				Window editLayout = new Window();
				editLayout.setSizeFull();
				editLayout.setContent(new AddLessonPlanSubject(lessonPlanId,classRange));
				UI.getCurrent().addWindow(editLayout);
			}
		});
		buttonLayout.addComponent(addSubjectButton);
		buttonLayout.setComponentAlignment(addSubjectButton, Alignment.MIDDLE_CENTER);
		
		Button classRoomButton = new Button("เลือกชั้นเรียน",FontAwesome.UNIVERSITY);
		classRoomButton.setId(itemId.toString());
		classRoomButton.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 1L;
			@Override
			public void buttonClick(ClickEvent event) {
				Object lessonPlanId = item.getItemProperty(LessonPlanSchema.LESSON_PLAN_ID).getValue();
				Object classRange = item.getItemProperty(LessonPlanSchema.CLASS_RANGE).getValue();
				Window editLayout = new Window();
				editLayout.setSizeFull();
				editLayout.setContent(new AddClassRoomLessonPlan(lessonPlanId,classRange));
				UI.getCurrent().addWindow(editLayout);
			}
		});
		buttonLayout.addComponent(classRoomButton);
		buttonLayout.setComponentAlignment(classRoomButton, Alignment.MIDDLE_CENTER);
		
		Button removeButton = new Button(FontAwesome.TRASH_O);
		removeButton.setId(itemId.toString());
		removeButton.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 1L;
			@Override
			public void buttonClick(ClickEvent event) {
				ConfirmDialog.show(UI.getCurrent(), "ลบแผนการเรียน","คุณต้องการลบแผนการเรียนนี้ใช่หรือไม่?","ตกลง","ยกเลิก",
			        new ConfirmDialog.Listener() {
						private static final long serialVersionUID = 1L;
						public void onClose(ConfirmDialog dialog) {
			                if (dialog.isConfirmed()) {
			                	if(lContainer.removeItem(itemId)){
			                		try {
										lContainer.commit();
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
		table.setColumnFooter(LessonPlanSchema.NAME, "ทั้งหมด: "+ table.size() + " วิชา");
	}
	
	/* จัดกลุ่มของ ฟอร์มในการแก้ไข - เพิ่ม ข้อมูล */
	private void initFieldGroup(){		
		lessonPlanBinder = new FieldGroup(item);
		lessonPlanBinder.setBuffered(true);
		lessonPlanBinder.bind(name, LessonPlanSchema.NAME);
		lessonPlanBinder.bind(classRange, LessonPlanSchema.CLASS_RANGE);
		lessonPlanBinder.bind(description, LessonPlanSchema.DESCRIPTION);
	}	
	
	/* กำหนดค่าภายใน FieldGroup ไปยัง Item */
	@SuppressWarnings({ "unchecked"})
	private boolean saveFormData(){
		try {				
			/* เพิ่มข้อมูล */
			Object tmpItem = lContainer.addItem();
			Item item = lContainer.getItem(tmpItem);
			for(Field<?> field: lessonPlanBinder.getFields()){
				/* หาชนิดตัวแปร ของข้อมูลภายใน Database ของแต่ละ Field */
				Class<?> clazz = item.getItemProperty(lessonPlanBinder.getPropertyId(field)).getType();				
				System.err.println(lessonPlanBinder.getPropertyId(field));
				String className = clazz.getName();;
				Object value = null;
				if(lessonPlanBinder.getField(lessonPlanBinder.getPropertyId(field)).getValue() != null && 
						!lessonPlanBinder.getField(lessonPlanBinder.getPropertyId(field)).getValue().equals("")){
					/* ตรวจสอบ Class ที่ต้องแปลงที่ได้จากการตรวจสอบภายใน Database จาก item.getItemProperty(lessonPlanBinder.getPropertyId(field)).getType()
					 *  กรณั เป็น Double ก็แปลง Object ด้วย parseDouble ซึ่งค่าที่แปลงต้องไม่เป็น Null
					 *  กรณั เป็น Integer ก็แปลง Object ด้วย parseInt ซึ่งค่าที่แปลงต้องไม่เป็น Null
					 *    */

					if(clazz == Double.class){
						value = Double.parseDouble(lessonPlanBinder.getField(lessonPlanBinder.getPropertyId(field)).getValue().toString());
					}else if(clazz == Integer.class){
						value = Integer.parseInt(lessonPlanBinder.getField(lessonPlanBinder.getPropertyId(field)).getValue().toString());
					}else{
						value = lessonPlanBinder.getField(lessonPlanBinder.getPropertyId(field)).getValue();
					}
				}
				
				Object data = Class.forName(className).cast(value);
				item.getItemProperty(lessonPlanBinder.getPropertyId(field)).setValue(data);
			}
			item.getItemProperty(LessonPlanSchema.SCHOOL_ID).setValue(UI.getCurrent().getSession().getAttribute(SessionSchema.SCHOOL_ID));
			CreateModifiedSchema.setCreateAndModified(item);
			lContainer.commit();
			
			return true;
		} catch (Exception e) {
			Notification.show("บันทึกไม่สำเร็จ", Type.WARNING_MESSAGE);
			e.printStackTrace();
			return false;
		}
	}
}
