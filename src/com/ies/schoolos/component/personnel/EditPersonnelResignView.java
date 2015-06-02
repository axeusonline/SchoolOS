package com.ies.schoolos.component.personnel;

import java.util.Date;
import java.util.Locale;

import org.tepi.filtertable.FilterTable;

import com.ies.schoolos.component.ui.SchoolOSLayout;
import com.ies.schoolos.filter.TableFilterDecorator;
import com.ies.schoolos.filter.TableFilterGenerator;
import com.ies.schoolos.schema.SessionSchema;
import com.ies.schoolos.schema.info.PersonnelSchema;
import com.ies.schoolos.type.AliveStatus;
import com.ies.schoolos.type.PersonnelStatus;
import com.ies.schoolos.type.Prename;
import com.ies.schoolos.type.ResignType;
import com.ies.schoolos.type.dynamic.JobPosition;
import com.ies.schoolos.utility.Notification;
import com.vaadin.data.Item;
import com.vaadin.data.Container.ItemSetChangeEvent;
import com.vaadin.data.Container.ItemSetChangeListener;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.filter.And;
import com.vaadin.data.util.filter.Compare.Equal;
import com.vaadin.data.util.filter.Not;
import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Notification.Type;

public class EditPersonnelResignView extends SchoolOSLayout {
	private static final long serialVersionUID = 1L;

	private SQLContainer pContainer = container.getPersonnelContainer();

	private JobPosition jContainer = new JobPosition();
	
	private Item item;

	private FilterTable  table;
	
	private FieldGroup resignBinder;
	private TextField firstname;
	private TextField lastname;
	private ComboBox aliveStatus;
	private FormLayout resignForm;
	private ComboBox resignType;
	private PopupDateField resignDate;
	private TextArea description;
	private Button save;	
	
	public EditPersonnelResignView() {	
		pContainer.refresh();
		setSpacing(true);
		setMargin(true);
		buildMainLayout();
		initFieldGroup();
		fetchData();
		setFooterData();
	}	
	
	private void buildMainLayout(){		
		HorizontalLayout resignLayout = new HorizontalLayout();
		resignLayout.setSpacing(true);
		addComponent(resignLayout);
		setExpandRatio(resignLayout, 1);
		
		/* Content */
		table = new FilterTable();
		table.setSizeFull();
		table.setSelectable(true);
		table.setFooterVisible(true);        	
		table.addValueChangeListener(new ValueChangeListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				if(event.getProperty().getValue() != null){
					normalMode();
					
					save.setCaption("แก้ไข");
					item = pContainer.getItem(event.getProperty().getValue());
					initFieldGroup();

					readOnlyMode();
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
		table.addContainerProperty(PersonnelSchema.PERSONNEL_CODE, String.class, null);
		table.addContainerProperty(PersonnelSchema.PRENAME, String.class, null);
		table.addContainerProperty(PersonnelSchema.FIRSTNAME, String.class, null);
		table.addContainerProperty(PersonnelSchema.LASTNAME, String.class, null);
		table.addContainerProperty(PersonnelSchema.JOB_POSITION_ID, String.class, null);
		table.addContainerProperty(PersonnelSchema.PERSONNEL_STATUS, String.class, null);
		
		table.setFilterDecorator(new TableFilterDecorator());
		table.setFilterGenerator(new TableFilterGenerator());
        table.setFilterBarVisible(true);

	    setFooterData();
		initTableStyle();
		table.sort(new Object[]{PersonnelSchema.PERSONNEL_CODE}, new boolean[]{true});

		table.setColumnReorderingAllowed(true);
		table.setColumnCollapsingAllowed(true);
		resignLayout.addComponent(table);
		resignLayout.setExpandRatio(table, 3);
        
        resignForm = new FormLayout();
		resignForm.setSpacing(true);
		resignForm.setStyleName("border-white");
		resignLayout.addComponent(resignForm);
		resignLayout.setExpandRatio(resignForm, 1);
		
		Label formLab = new Label("จำหน่ายออก");
		resignForm.addComponent(formLab);
		
		firstname = new TextField("ชื่อ");
		firstname.setInputPrompt("ชื่อ");
		firstname.setNullRepresentation("");
		firstname.setImmediate(false);
		firstname.setRequired(true);
		firstname.setWidth("-1px");
		firstname.setHeight("-1px");
		resignForm.addComponent(firstname);
		
		lastname = new TextField("สกุล");
		lastname.setInputPrompt("สกุล");
		lastname.setNullRepresentation("");
		lastname.setImmediate(false);
		lastname.setRequired(true);
		lastname.setWidth("-1px");
		lastname.setHeight("-1px");
		resignForm.addComponent(lastname);
		
		aliveStatus = new ComboBox("สถานะการมีชีวิต",new AliveStatus());
		aliveStatus.setInputPrompt("กรุณาเลือก");
		aliveStatus.setItemCaptionPropertyId("name");
		aliveStatus.setImmediate(true);
		aliveStatus.setNullSelectionAllowed(false);
		aliveStatus.setRequired(true);
		aliveStatus.setWidth("-1px");
		aliveStatus.setHeight("-1px");
		aliveStatus.setFilteringMode(FilteringMode.CONTAINS);
		aliveStatus.setValue(0);
		aliveStatus.addValueChangeListener(new ValueChangeListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				if(event.getProperty().getValue() != null){
					if(event.getProperty().getValue().toString().equals("1")){
						resignType.setValue(1);
						resignDate.setValue(new Date());
						description.setValue("จำหน่ายออก เนื่องจากเสียชีวิต");
					}else{
						resignType.setValue(null);
						resignDate.setValue(null);
						description.setValue(null);
					}	
				}
			}
		});
		resignForm.addComponent(aliveStatus);
		
		resignType = new ComboBox("ประเภทจำหน่าย",new ResignType());
		resignType.setInputPrompt("กรุณาเลือก");
		resignType.setItemCaptionPropertyId("name");
		resignType.setImmediate(true);
		resignType.setNullSelectionAllowed(false);
		resignType.setRequired(true);
		resignType.setWidth("-1px");
		resignType.setHeight("-1px");
		resignType.setFilteringMode(FilteringMode.CONTAINS);
		resignForm.addComponent(resignType);
		
		resignDate = new PopupDateField("วัน เดือน ปี จำหน่าย");
		resignDate.setInputPrompt("วว/ดด/ปปปป");
		resignDate.setImmediate(false);
		resignDate.setRequired(true);
		resignDate.setWidth("-1px");
		resignDate.setHeight("-1px");
		resignDate.setDateFormat("dd/MM/yyyy");
		resignDate.setLocale(new Locale("th", "TH"));
		resignForm.addComponent(resignDate);
		
		description = new TextArea("รายละเอียด");
		description.setInputPrompt("รายละเอียดเพิ่มเติม");
		description.setImmediate(false);
		description.setWidth("-1px");
		description.setHeight("-1px");
		description.setNullRepresentation("");
		resignForm.addComponent(description);
		
		save = new Button("บันทึก", FontAwesome.SAVE);
		save.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 1L;

			@SuppressWarnings("unchecked")
			@Override
			public void buttonClick(ClickEvent event) {
				if(resignBinder.isValid()){
					try {
						normalMode();
						
						item.getItemProperty(PersonnelSchema.PERSONNEL_STATUS).setValue(Integer.parseInt(resignType.getValue().toString())+1);
						resignBinder.commit();
						pContainer.commit();
						
						item = null;
						initFieldGroup();

						readOnlyMode();
						
						fetchData();
						setFooterData();
						save.setCaption("บันทึก");
						Notification.show("บันทึกสำเร็จ", Type.HUMANIZED_MESSAGE);
					} catch (Exception e) {
						e.printStackTrace();
						Notification.show("บันทึกไม่สำเร็จ", Type.WARNING_MESSAGE);
					}
				}
			}
		});
		resignForm.addComponent(save);
	}
	
	/* ตั้งค่ารูปแบบแสดงของตาราง */
	private void initTableStyle(){		
		table.setColumnHeader(PersonnelSchema.PERSONNEL_CODE, "หมายเลขประจำตัว");
		table.setColumnHeader(PersonnelSchema.PRENAME, "ชื่อต้น");
		table.setColumnHeader(PersonnelSchema.FIRSTNAME, "ชื่อ");
		table.setColumnHeader(PersonnelSchema.LASTNAME, "สกุล");
		table.setColumnHeader(PersonnelSchema.JOB_POSITION_ID, "ตำแหน่ง");
		table.setColumnHeader(PersonnelSchema.PERSONNEL_STATUS, "สถานะ");
		
		table.setVisibleColumns(
				PersonnelSchema.PERSONNEL_CODE, 
				PersonnelSchema.PRENAME,
				PersonnelSchema.FIRSTNAME, 
				PersonnelSchema.LASTNAME,
				PersonnelSchema.JOB_POSITION_ID,
				PersonnelSchema.PERSONNEL_STATUS);
		
	}
	
	private void fetchData(){
		table.removeAllItems();
		pContainer.removeAllContainerFilters();
		pContainer.addContainerFilter(new And(
				new Equal(PersonnelSchema.SCHOOL_ID,SessionSchema.getSchoolID()),
				new Not(new Equal(PersonnelSchema.PERSONNEL_STATUS, 0))));
		for(final Object itemId:pContainer.getItemIds()){
			Item item = pContainer.getItem(itemId);
						
			table.addItem(new Object[]{
				item.getItemProperty(PersonnelSchema.PERSONNEL_CODE).getValue(),
				Prename.getNameTh((int)item.getItemProperty(PersonnelSchema.PRENAME).getValue()),
				item.getItemProperty(PersonnelSchema.FIRSTNAME).getValue(),
				item.getItemProperty(PersonnelSchema.LASTNAME).getValue(),
				jContainer.getItem(item.getItemProperty(PersonnelSchema.JOB_POSITION_ID).getValue()).getItemProperty("name").getValue().toString(),
				PersonnelStatus.getNameTh((int)item.getItemProperty(PersonnelSchema.PERSONNEL_STATUS).getValue())
			}, itemId);
		}
	}
	
	/*นำจำนวนที่นับ มาใส่ค่าในส่วนท้ายตาราง*/
	private void setFooterData(){
		table.setColumnFooter(PersonnelSchema.PERSONNEL_CODE, "ทั้งหมด: "+ table.size() + " คน");
	}
	
	/* จัดกลุ่มของ ฟอร์มในการแก้ไข - เพิ่ม ข้อมูล */
	private void initFieldGroup(){		
		resignBinder = new FieldGroup(item);
		resignBinder.setBuffered(true);
		resignBinder.bind(firstname, PersonnelSchema.FIRSTNAME);
		resignBinder.bind(lastname, PersonnelSchema.LASTNAME);		
		resignBinder.bind(aliveStatus, PersonnelSchema.ALIVE_STATUS);
		resignBinder.bind(resignType, PersonnelSchema.RESIGN_TYPE);
		resignBinder.bind(resignDate, PersonnelSchema.RESIGN_DATE);
		resignBinder.bind(description, PersonnelSchema.RESIGN_DESCRIPTION);
	}	
	
	/* ปิดการแก้ไข */
	private void readOnlyMode(){
		firstname.setReadOnly(true);
		lastname.setReadOnly(true);
	}
	
	/* เปิดการแก้ไข */
	private void normalMode(){
		firstname.setReadOnly(false);
		lastname.setReadOnly(false);
	}
}
