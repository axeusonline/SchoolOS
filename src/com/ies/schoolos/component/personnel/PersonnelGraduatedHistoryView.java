package com.ies.schoolos.component.personnel;

import org.tepi.filtertable.FilterTable;
import org.vaadin.dialogs.ConfirmDialog;

import com.ies.schoolos.component.ui.NumberField;
import com.ies.schoolos.component.ui.SchoolOSLayout;
import com.ies.schoolos.filter.TableFilterDecorator;
import com.ies.schoolos.filter.TableFilterGenerator;
import com.ies.schoolos.schema.CreateModifiedSchema;
import com.ies.schoolos.schema.SessionSchema;
import com.ies.schoolos.schema.info.PersonnelGraduatedHistorySchema;
import com.ies.schoolos.type.GraduatedLevel;
import com.ies.schoolos.type.dynamic.Province;
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

public class PersonnelGraduatedHistoryView extends SchoolOSLayout {
	private static final long serialVersionUID = 1L;

	private boolean editMode = false;
	
	private SQLContainer pgContainer = container.getPersonnelGraduatedHistoryContainer();
	
	private Item item;
	private Object personnelId;

	private HorizontalLayout graduatedHistoryLayout;
	private FilterTable table;
	
	private FieldGroup graduatedHistoryBinder;
	private FormLayout graduatedHistoryForm;
	private TextField institute;
	private ComboBox graduatedLevel;
	private TextField degree;
	private TextField major;
	private TextField minor;
	private NumberField year;
	private TextField location;
	private ComboBox provinceId;
	private TextArea description;
	private Button save;	
	
	public PersonnelGraduatedHistoryView(Object personnelId) {		
		this.personnelId = personnelId;
		setSizeFull();
		setSpacing(true);
		setMargin(true);
		buildMainLayout();
		fetchData();
	}
	
	private void buildMainLayout(){

		setWidth("100%");
		setHeight("-1px");
		setSpacing(true);

		graduatedHistoryLayout = new HorizontalLayout();
		graduatedHistoryLayout.setSizeFull();
		graduatedHistoryLayout.setSpacing(true);
		addComponent(graduatedHistoryLayout);
		setExpandRatio(graduatedHistoryLayout, 1);

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
					item = pgContainer.getItem(event.getProperty().getValue());
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

		table.setContainerDataSource(pgContainer);
	    setFooterData();
		initTableStyle();

		table.setColumnReorderingAllowed(true);
		table.setColumnCollapsingAllowed(true);
		graduatedHistoryLayout.addComponent(table);
		graduatedHistoryLayout.setExpandRatio(table,(float)2.2);
		
		//Form		
		graduatedHistoryForm = new FormLayout();
		graduatedHistoryForm.setSpacing(true);
		graduatedHistoryForm.setMargin(true);
		graduatedHistoryForm.setStyleName("border-white");
		graduatedHistoryLayout.addComponent(graduatedHistoryForm);
		graduatedHistoryLayout.setExpandRatio(graduatedHistoryForm,1);
		
		Label formLab = new Label("วุฒิการศึกษา");
		graduatedHistoryForm.addComponent(formLab);
		
		institute = new TextField("สถาบัน");
		institute.setInputPrompt("สถาบัน");
		institute.setNullRepresentation("");
		institute.setImmediate(false);
		institute.setRequired(true);
		institute.setWidth("-1px");
		institute.setHeight("-1px");
		graduatedHistoryForm.addComponent(institute);
		
		graduatedLevel = new ComboBox("ระดับการศึกษา",new GraduatedLevel());
		graduatedLevel.setInputPrompt("กรุณาเลือก");
		graduatedLevel.setItemCaptionPropertyId("name");
		graduatedLevel.setImmediate(true);
		graduatedLevel.setNullSelectionAllowed(false);
		graduatedLevel.setRequired(true);
		graduatedLevel.setWidth("-1px");
		graduatedLevel.setHeight("-1px");
		graduatedLevel.setFilteringMode(FilteringMode.CONTAINS);
		graduatedHistoryForm.addComponent(graduatedLevel);
		
		degree = new TextField("วุฒิการศึกษา");
		degree.setInputPrompt("วุฒิการศึกษา");
		degree.setNullRepresentation("");
		degree.setImmediate(false);
		degree.setWidth("-1px");
		degree.setHeight("-1px");
		graduatedHistoryForm.addComponent(degree);
		
		major = new TextField("วิชาเอก");
		major.setInputPrompt("วิชาเอก");
		major.setNullRepresentation("");
		major.setImmediate(false);
		major.setWidth("-1px");
		major.setHeight("-1px");
		graduatedHistoryForm.addComponent(major);
		
		minor = new TextField("วิชาโท");
		minor.setInputPrompt("วิชาโท");
		minor.setNullRepresentation("");
		minor.setImmediate(false);
		minor.setWidth("-1px");
		minor.setHeight("-1px");
		graduatedHistoryForm.addComponent(minor);
		
		year = new NumberField("ปีที่จบ");
		year.setInputPrompt("ปีที่จบ");
		year.setNullRepresentation("");
		year.setImmediate(false);
		year.setWidth("-1px");
		year.setHeight("-1px");
		graduatedHistoryForm.addComponent(year);
		
		location = new TextField("ประเทศ");
		location.setInputPrompt("ประเทศ");
		location.setNullRepresentation("");
		location.setImmediate(false);
		location.setWidth("-1px");
		location.setHeight("-1px");
		graduatedHistoryForm.addComponent(location);
		
		provinceId = new ComboBox("จังหวัดสถาบัน",new Province());
		provinceId.setInputPrompt("กรุณาเลือก");
		provinceId.setItemCaptionPropertyId("name");
		provinceId.setImmediate(true);
		provinceId.setNullSelectionAllowed(false);
		provinceId.setWidth("-1px");
		provinceId.setHeight("-1px");
		provinceId.setFilteringMode(FilteringMode.CONTAINS);
		graduatedHistoryForm.addComponent(provinceId);
		
		description = new TextArea("รายละเอียด");
		description.setInputPrompt("รายละเอียดเพิ่มเติม");
		description.setImmediate(false);
		description.setWidth("-1px");
		description.setHeight("-1px");
		description.setNullRepresentation("");
		graduatedHistoryForm.addComponent(description);
		
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
						graduatedHistoryBinder.commit();
						pgContainer.commit();
						editMode = false;
						Notification.show("บันทึกสำเร็จ", Type.HUMANIZED_MESSAGE);
					}else{
						if(!graduatedHistoryBinder.isValid()){
							Notification.show("กรุณากรอกข้อมูลให้ครบถ้วน", Type.WARNING_MESSAGE);
							return;
						}
							
						pgContainer.removeAllContainerFilters();
						if(!saveFormData())
							return;
						
						pgContainer.addContainerFilter(new Equal(PersonnelGraduatedHistorySchema.SCHOOL_ID, SessionSchema.getSchoolID()));
					}
					item = null;
					save.setCaption("บันทึก");
					initFieldGroup();
					fetchData();
					Notification.show("บันทึกสำเร็จ", Type.HUMANIZED_MESSAGE);
				} catch (Exception e) {
					e.printStackTrace();
					Notification.show("บันทึกไม่สำเร็จ", Type.WARNING_MESSAGE);
				}
			}
		});
		graduatedHistoryForm.addComponent(save);
		
		initFieldGroup();
	}
	
	/* ตั้งค่ารูปแบบแสดงของตาราง */
	private void initTableStyle(){		
		table.setColumnHeader(PersonnelGraduatedHistorySchema.GRADUATED_LEVEL, "ระดับการศึกษา");
		table.setColumnHeader(PersonnelGraduatedHistorySchema.INSTITUTE, "สถาบัน");
		table.setColumnHeader(PersonnelGraduatedHistorySchema.DEGREE, "วุฒิการศึกษา");
		table.setColumnHeader(PersonnelGraduatedHistorySchema.MAJOR, "วิชาเอก");
		
		table.setVisibleColumns(
				PersonnelGraduatedHistorySchema.GRADUATED_LEVEL, 
				PersonnelGraduatedHistorySchema.INSTITUTE,
				PersonnelGraduatedHistorySchema.DEGREE, 
				PersonnelGraduatedHistorySchema.MAJOR);
		
		setColumnGenerator(PersonnelGraduatedHistorySchema.GRADUATED_LEVEL, "");
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
						if(PersonnelGraduatedHistorySchema.GRADUATED_LEVEL.equals(propertyId))
							value = GraduatedLevel.getNameTh(Integer.parseInt(item.getItemProperty(propertyId).getValue().toString()));
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
				ConfirmDialog.show(UI.getCurrent(), "ลบวุฒิการศึกษา","คุณต้องการลบวุฒิการศึกษานี้ใช่หรือไม่?","ตกลง","ยกเลิก",
			        new ConfirmDialog.Listener() {
						private static final long serialVersionUID = 1L;
						public void onClose(ConfirmDialog dialog) {
			                if (dialog.isConfirmed()) {
			                	if(pgContainer.removeItem(itemId)){
			                		try {
										pgContainer.commit();
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
		table.setColumnFooter(PersonnelGraduatedHistorySchema.GRADUATED_LEVEL, "ทั้งหมด: "+ table.size() + " วุฒิการศึกษา");
	}
	
	/* จัดกลุ่มของ ฟอร์มในการแก้ไข - เพิ่ม ข้อมูล */
	private void initFieldGroup(){		
		graduatedHistoryBinder = new FieldGroup(item);
		graduatedHistoryBinder.setBuffered(true);
		graduatedHistoryBinder.bind(institute, PersonnelGraduatedHistorySchema.INSTITUTE);
		graduatedHistoryBinder.bind(graduatedLevel, PersonnelGraduatedHistorySchema.GRADUATED_LEVEL);
		graduatedHistoryBinder.bind(degree, PersonnelGraduatedHistorySchema.DEGREE);
		graduatedHistoryBinder.bind(major, PersonnelGraduatedHistorySchema.MAJOR);
		graduatedHistoryBinder.bind(minor, PersonnelGraduatedHistorySchema.MINOR);
		graduatedHistoryBinder.bind(year, PersonnelGraduatedHistorySchema.YEAR);
		graduatedHistoryBinder.bind(location, PersonnelGraduatedHistorySchema.LOCATION);
		graduatedHistoryBinder.bind(provinceId, PersonnelGraduatedHistorySchema.PROVINCE_ID);
		graduatedHistoryBinder.bind(description, PersonnelGraduatedHistorySchema.DESCRIPTION);
	}
	
	/* กำหนดค่าภายใน FieldGroup ไปยัง Item */
	@SuppressWarnings({ "unchecked"})
	private boolean saveFormData(){
		try {				
			/* เพิ่มข้อมูล */
			Object tmpItem = pgContainer.addItem();
			Item item = pgContainer.getItem(tmpItem);
			for(Field<?> field: graduatedHistoryBinder.getFields()){
				/* หาชนิดตัวแปร ของข้อมูลภายใน Database ของแต่ละ Field */
				Class<?> clazz = item.getItemProperty(graduatedHistoryBinder.getPropertyId(field)).getType();
				String className = clazz.getName();;
				Object value = null;
				if(graduatedHistoryBinder.getField(graduatedHistoryBinder.getPropertyId(field)).getValue() != null && 
						!graduatedHistoryBinder.getField(graduatedHistoryBinder.getPropertyId(field)).getValue().equals("")){
					/* ตรวจสอบ Class ที่ต้องแปลงที่ได้จากการตรวจสอบภายใน Database จาก item.getItemProperty(graduatedHistoryBinder.getPropertyId(field)).getType()
					 *  กรณั เป็น Double ก็แปลง Object ด้วย parseDouble ซึ่งค่าที่แปลงต้องไม่เป็น Null
					 *  กรณั เป็น Integer ก็แปลง Object ด้วย parseInt ซึ่งค่าที่แปลงต้องไม่เป็น Null
					 *    */

					if(clazz == Double.class){
						value = Double.parseDouble(graduatedHistoryBinder.getField(graduatedHistoryBinder.getPropertyId(field)).getValue().toString());
					}else{
						value = graduatedHistoryBinder.getField(graduatedHistoryBinder.getPropertyId(field)).getValue();
					}
				}
				
				Object data = Class.forName(className).cast(value);
				item.getItemProperty(graduatedHistoryBinder.getPropertyId(field)).setValue(data);
			}
			item.getItemProperty(PersonnelGraduatedHistorySchema.SCHOOL_ID).setValue(SessionSchema.getSchoolID());
			item.getItemProperty(PersonnelGraduatedHistorySchema.PERSONNEL_ID).setValue(personnelId);
			CreateModifiedSchema.setCreateAndModified(item);
			pgContainer.commit();
			setFooterData();
			return true;
		} catch (Exception e) {
			Notification.show("บันทึกไม่สำเร็จ", Type.WARNING_MESSAGE);
			e.printStackTrace();
			return false;
		}
	}
	
	private void fetchData(){
		pgContainer.refresh();
		pgContainer.addContainerFilter(new Equal(PersonnelGraduatedHistorySchema.PERSONNEL_ID, personnelId));
	}
}
