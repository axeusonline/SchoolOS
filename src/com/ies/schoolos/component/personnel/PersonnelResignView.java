package com.ies.schoolos.component.personnel;

import org.tepi.filtertable.FilterTable;

import com.ies.schoolos.container.Container;
import com.ies.schoolos.filter.TableFilterDecorator;
import com.ies.schoolos.filter.TableFilterGenerator;
import com.ies.schoolos.report.excel.RecruitStudentToExcel;
import com.ies.schoolos.schema.CreateModifiedSchema;
import com.ies.schoolos.schema.SessionSchema;
import com.ies.schoolos.schema.UserSchema;
import com.ies.schoolos.schema.info.PersonnelSchema;
import com.ies.schoolos.type.Prename;
import com.ies.schoolos.type.ResignType;
import com.ies.schoolos.type.dynamic.JobPosition;
import com.ies.schoolos.utility.Notification;
import com.vaadin.addon.tableexport.ExcelExport;
import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.filter.And;
import com.vaadin.data.util.filter.Compare.Equal;
import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Notification.Type;

public class PersonnelResignView extends VerticalLayout {
	private static final long serialVersionUID = 1L;

	private SQLContainer pContainer = Container.getPersonnelContainer();
	private SQLContainer userContainer = Container.getUserContainer();
	
	private Item item;
	
	private HorizontalLayout toolbar;
	private FilterTable  table;
	
	private FieldGroup resignBinder;
	private TextField firstname;
	private TextField lastname;
	private FormLayout resignForm;
	private ComboBox resignType;
	private PopupDateField resignDate;
	private TextArea description;
	private Button save;	
	
	public PersonnelResignView() {	
		pContainer.refresh();
		setSpacing(true);
		setMargin(true);
		buildMainLayout();
		fetchData();
		setFooterData();
	}	
	
	private void buildMainLayout(){
		/* Toolbar */
		toolbar = new HorizontalLayout();
		toolbar.setSpacing(true);
		//addComponent(toolbar);
				
		Button excelExport = new Button("ส่งออกไฟล์ Excel", FontAwesome.FILE_EXCEL_O);
		excelExport.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event) {
				final Table tableEx = new RecruitStudentToExcel();
				tableEx.setVisible(false);
				addComponent(tableEx);
				
				ExcelExport excelExport = new ExcelExport(tableEx,"student");
                excelExport.excludeCollapsedColumns();
                excelExport.setReportTitle("Personnel");
				excelExport.setExportFileName("personnel.xls");
                excelExport.export();
                
                removeComponent(tableEx);
			}
		});
		//toolbar.addComponent(excelExport);	
		
		HorizontalLayout resignLayout = new HorizontalLayout();
		resignLayout.setSpacing(true);
		addComponent(resignLayout);
		setExpandRatio(resignLayout, 1);
		
		/* Content */
		table = new FilterTable();
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
		
		table.addContainerProperty(PersonnelSchema.PERSONEL_CODE, String.class, null);
		table.addContainerProperty(PersonnelSchema.PRENAME, String.class, null);
		table.addContainerProperty(PersonnelSchema.FIRSTNAME, String.class, null);
		table.addContainerProperty(PersonnelSchema.LASTNAME, String.class, null);
		table.addContainerProperty(PersonnelSchema.JOB_POSITION, String.class, null);
		
		table.setFilterDecorator(new TableFilterDecorator());
		table.setFilterGenerator(new TableFilterGenerator());
        table.setFilterBarVisible(true);

	    setFooterData();
		initTableStyle();
		table.sort(new Object[]{PersonnelSchema.PERSONEL_CODE}, new boolean[]{true});

		table.setColumnReorderingAllowed(true);
		table.setColumnCollapsingAllowed(true);
		resignLayout.addComponent(table);
		resignLayout.setExpandRatio(table, 2);
        
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
		resignDate.setInputPrompt("วว/ดด/ปปปป(คศ)");
		resignDate.setImmediate(false);
		resignDate.setRequired(true);
		resignDate.setWidth("-1px");
		resignDate.setHeight("-1px");
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
				try {
					normalMode();
					
					item.getItemProperty(PersonnelSchema.PERSONEL_STATUS).setValue(Integer.parseInt(resignType.getValue().toString())+1);
					CreateModifiedSchema.setCreateAndModified(item);
					resignBinder.commit();
					pContainer.commit();

					userContainer.addContainerFilter(new And(
							new Equal(UserSchema.REF_USER_ID, Integer.parseInt(item.getItemProperty(PersonnelSchema.PERSONNEL_ID).getValue().toString())),
							new Equal(UserSchema.REF_USER_TYPE, 1),
							new Equal(UserSchema.SCHOOL_ID, SessionSchema.getSchoolID())));
					if(userContainer.size() > 0){
						Item userItem = userContainer.getItem(userContainer.getIdByIndex(0));
						userItem.getItemProperty(UserSchema.STATUS).setValue(1);
						CreateModifiedSchema.setCreateAndModified(userItem);
						userContainer.commit();
					}
					
					Notification.show("บันทึกสำเร็จ", Type.HUMANIZED_MESSAGE);
					item = null;
					initFieldGroup();

					readOnlyMode();
					
					fetchData();
					setFooterData();
					Notification.show("บันทึกสำเร็จ", Type.HUMANIZED_MESSAGE);
				} catch (Exception e) {
					e.printStackTrace();
					Notification.show("บันทึกไม่สำเร็จ", Type.WARNING_MESSAGE);
				}
			}
		});
		resignForm.addComponent(save);
	}
	
	/* ตั้งค่ารูปแบบแสดงของตาราง */
	private void initTableStyle(){		
		table.setColumnHeader(PersonnelSchema.PERSONEL_CODE, "หมายเลขประจำตัว");
		table.setColumnHeader(PersonnelSchema.PRENAME, "ชื่อต้น");
		table.setColumnHeader(PersonnelSchema.FIRSTNAME, "ชื่อ");
		table.setColumnHeader(PersonnelSchema.LASTNAME, "สกุล");
		table.setColumnHeader(PersonnelSchema.JOB_POSITION, "ตำแหน่ง");
		
		table.setVisibleColumns(
				PersonnelSchema.PERSONEL_CODE, 
				PersonnelSchema.PRENAME,
				PersonnelSchema.FIRSTNAME, 
				PersonnelSchema.LASTNAME,
				PersonnelSchema.JOB_POSITION);
		
	}
	
	private void fetchData(){
		table.removeAllItems();
		pContainer.removeAllContainerFilters();
		pContainer.addContainerFilter(new And(
				new Equal(PersonnelSchema.SCHOOL_ID,SessionSchema.getSchoolID()),
				new Equal(PersonnelSchema.PERSONEL_STATUS, 0)));
		for(final Object itemId:pContainer.getItemIds()){
			Item item = pContainer.getItem(itemId);
						
			table.addItem(new Object[]{
				item.getItemProperty(PersonnelSchema.PERSONEL_CODE).getValue(),
				Prename.getNameTh((int)item.getItemProperty(PersonnelSchema.PRENAME).getValue()),
				item.getItemProperty(PersonnelSchema.FIRSTNAME).getValue(),
				item.getItemProperty(PersonnelSchema.LASTNAME).getValue(),
				JobPosition.getNameTh((int)item.getItemProperty(PersonnelSchema.JOB_POSITION).getValue())
			}, itemId);
		}
	}
	
	/*นำจำนวนที่นับ มาใส่ค่าในส่วนท้ายตาราง*/
	private void setFooterData(){
		table.setColumnFooter(PersonnelSchema.PERSONEL_CODE, "ทั้งหมด: "+ table.size() + " คน");
	}
	
	/* จัดกลุ่มของ ฟอร์มในการแก้ไข - เพิ่ม ข้อมูล */
	private void initFieldGroup(){		
		resignBinder = new FieldGroup(item);
		resignBinder.setBuffered(true);
		resignBinder.bind(firstname, PersonnelSchema.FIRSTNAME);
		resignBinder.bind(lastname, PersonnelSchema.LASTNAME);
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
