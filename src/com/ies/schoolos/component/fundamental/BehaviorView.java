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
import com.ies.schoolos.schema.fundamental.BehaviorSchema;
import com.ies.schoolos.type.SeverityType;
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

public class BehaviorView extends ContentPage{
	private static final long serialVersionUID = 1L;

	private boolean editMode = false;
	
	private SQLContainer sContainer = Container.getBehaviorContainer();
	
	private Item item;

	private HorizontalLayout behaviorLayout;
	private FilterTable table;
	
	private FieldGroup behaviorBinder;
	private FormLayout behaviorForm;
	private TextField name;
	private NumberField min_score;
	private NumberField max_score;
	private ComboBox severity_type;
	private TextArea description;
	private Button save;	
	
	public BehaviorView() {
		super("พฤติกรรม");
		
		sContainer.refresh();
		sContainer.addContainerFilter(new Equal(BehaviorSchema.SCHOOL_ID, SessionSchema.getSchoolID()));
		
		setSizeFull();
		setSpacing(true);
		setMargin(true);
		
		buildMainLayout();
	}
	
	private void buildMainLayout(){
		behaviorLayout = new HorizontalLayout();
		behaviorLayout.setSizeFull();
		behaviorLayout.setSpacing(true);
		addComponent(behaviorLayout);
		setExpandRatio(behaviorLayout, 1);

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

		table.setContainerDataSource(sContainer);
	    setFooterData();
		initTableStyle();

		table.setColumnReorderingAllowed(true);
		table.setColumnCollapsingAllowed(true);
		behaviorLayout.addComponent(table);
		behaviorLayout.setExpandRatio(table,(float)2.2);
		
		//Form		
		behaviorForm = new FormLayout();
		behaviorForm.setSpacing(true);
		behaviorForm.setMargin(true);
		behaviorForm.setStyleName("border-white");
		behaviorLayout.addComponent(behaviorForm);
		behaviorLayout.setExpandRatio(behaviorForm,1);
		
		Label formLab = new Label("พฤติกรรม");
		behaviorForm.addComponent(formLab);
		
		name = new TextField("ชื่อพฤติกรรม");
		name.setInputPrompt("ชื่อพฤติกรรม");
		name.setNullRepresentation("");
		name.setImmediate(false);
		name.setRequired(true);
		name.setWidth("-1px");
		name.setHeight("-1px");
		behaviorForm.addComponent(name);
		
		min_score = new NumberField("คะแนนต่ำสุด");
		min_score.setInputPrompt("คะแนนความผิดต่ำสุด");
		min_score.setNullRepresentation("");
		min_score.setImmediate(false);
		min_score.setRequired(true);
		min_score.setWidth("-1px");
		min_score.setHeight("-1px");
		behaviorForm.addComponent(min_score);
		
		max_score = new NumberField("คะแนนสูงสุด");
		max_score.setInputPrompt("คะแนนความผิดต่ำสุด");
		max_score.setNullRepresentation("");
		max_score.setImmediate(false);
		max_score.setRequired(true);
		max_score.setWidth("-1px");
		max_score.setHeight("-1px");
		behaviorForm.addComponent(max_score);
		
		severity_type = new ComboBox("ระดับความรุนแรง",new SeverityType());
		severity_type.setInputPrompt("กรุณาเลือก");
		severity_type.setItemCaptionPropertyId("name");
		severity_type.setImmediate(true);
		severity_type.setNullSelectionAllowed(false);
		severity_type.setRequired(true);
		severity_type.setWidth("-1px");
		severity_type.setHeight("-1px");
		severity_type.setFilteringMode(FilteringMode.CONTAINS);
		behaviorForm.addComponent(severity_type);
		
		description = new TextArea("รายละเอียด");
		description.setInputPrompt("รายละเอียดเพิ่มเติม");
		description.setImmediate(false);
		description.setWidth("-1px");
		description.setHeight("-1px");
		description.setNullRepresentation("");
		behaviorForm.addComponent(description);
		
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
						behaviorBinder.commit();
						sContainer.commit();
						editMode = false;
						Notification.show("บันทึกสำเร็จ", Type.HUMANIZED_MESSAGE);
					}else{
						if(!behaviorBinder.isValid()){
							Notification.show("กรุณากรอกข้อมูลให้ครบถ้วน", Type.WARNING_MESSAGE);
							return;
						}
							
						sContainer.removeAllContainerFilters();
						if(!saveFormData())
							return;
						
						sContainer.addContainerFilter(new Equal(BehaviorSchema.SCHOOL_ID, SessionSchema.getSchoolID()));
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
		behaviorForm.addComponent(save);
		
		initFieldGroup();
	}
	
	/* ตั้งค่ารูปแบบแสดงของตาราง */
	private void initTableStyle(){		
		table.setColumnHeader(BehaviorSchema.NAME, "ชื่อ");
		table.setColumnHeader(BehaviorSchema.MIN_SCORE, "คะแนนต่ำสุด");
		table.setColumnHeader(BehaviorSchema.MAX_SCORE, "คะแนนสูงสุด");
		table.setColumnHeader(BehaviorSchema.SEVERITY_TYPE, "ระดับความรุนแรง");
		
		table.setVisibleColumns(
				BehaviorSchema.NAME, 
				BehaviorSchema.MIN_SCORE,
				BehaviorSchema.MAX_SCORE, 
				BehaviorSchema.SEVERITY_TYPE);
		
		setColumnGenerator(BehaviorSchema.SEVERITY_TYPE, "");
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
						if(BehaviorSchema.SEVERITY_TYPE.equals(propertyId))
							value = SeverityType.getNameTh(Integer.parseInt(item.getItemProperty(propertyId).getValue().toString()));
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
				ConfirmDialog.show(UI.getCurrent(), "ลบพฤติกรรม","คุณต้องการลบพฤติกรรมนี้ใช่หรือไม่?","ตกลง","ยกเลิก",
			        new ConfirmDialog.Listener() {
						private static final long serialVersionUID = 1L;
						public void onClose(ConfirmDialog dialog) {
			                if (dialog.isConfirmed()) {
			                	if(sContainer.removeItem(itemId)){
			                		try {
										sContainer.commit();
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
		table.setColumnFooter(BehaviorSchema.NAME, "ทั้งหมด: "+ table.size() + " พฤติกรรม");
	}
	
	/* จัดกลุ่มของ ฟอร์มในการแก้ไข - เพิ่ม ข้อมูล */
	private void initFieldGroup(){		
		behaviorBinder = new FieldGroup(item);
		behaviorBinder.setBuffered(true);
		behaviorBinder.bind(name, BehaviorSchema.NAME);
		behaviorBinder.bind(min_score, BehaviorSchema.MIN_SCORE);
		behaviorBinder.bind(max_score, BehaviorSchema.MAX_SCORE);
		behaviorBinder.bind(severity_type, BehaviorSchema.SEVERITY_TYPE);
		behaviorBinder.bind(description, BehaviorSchema.DESCRIPTION);
	}
	
	/* กำหนดค่าภายใน FieldGroup ไปยัง Item */
	@SuppressWarnings({ "unchecked"})
	private boolean saveFormData(){
		try {				
			/* เพิ่มข้อมูล */
			Object tmpItem = sContainer.addItem();
			Item item = sContainer.getItem(tmpItem);
			for(Field<?> field: behaviorBinder.getFields()){
				/* หาชนิดตัวแปร ของข้อมูลภายใน Database ของแต่ละ Field */
				Class<?> clazz = item.getItemProperty(behaviorBinder.getPropertyId(field)).getType();
				String className = clazz.getName();;
				Object value = null;
				if(behaviorBinder.getField(behaviorBinder.getPropertyId(field)).getValue() != null && 
						!behaviorBinder.getField(behaviorBinder.getPropertyId(field)).getValue().equals("")){
					/* ตรวจสอบ Class ที่ต้องแปลงที่ได้จากการตรวจสอบภายใน Database จาก item.getItemProperty(behaviorBinder.getPropertyId(field)).getType()
					 *  กรณั เป็น Double ก็แปลง Object ด้วย parseDouble ซึ่งค่าที่แปลงต้องไม่เป็น Null
					 *  กรณั เป็น Integer ก็แปลง Object ด้วย parseInt ซึ่งค่าที่แปลงต้องไม่เป็น Null
					 *    */

					if(clazz == Double.class){
						value = Double.parseDouble(behaviorBinder.getField(behaviorBinder.getPropertyId(field)).getValue().toString());
					}else{
						value = behaviorBinder.getField(behaviorBinder.getPropertyId(field)).getValue();
					}
				}
				
				Object data = Class.forName(className).cast(value);
				item.getItemProperty(behaviorBinder.getPropertyId(field)).setValue(data);
			}
			item.getItemProperty(BehaviorSchema.SCHOOL_ID).setValue(SessionSchema.getSchoolID());
			CreateModifiedSchema.setCreateAndModified(item);
			sContainer.commit();
			setFooterData();
			return true;
		} catch (Exception e) {
			Notification.show("บันทึกไม่สำเร็จ", Type.WARNING_MESSAGE);
			e.printStackTrace();
			return false;
		}
	}
}
