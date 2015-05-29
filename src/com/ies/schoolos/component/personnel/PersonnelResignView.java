package com.ies.schoolos.component.personnel;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellStyle;
import org.tepi.filtertable.FilterTable;

import com.ies.schoolos.container.Container;
import com.ies.schoolos.filter.TableFilterDecorator;
import com.ies.schoolos.filter.TableFilterGenerator;
import com.ies.schoolos.schema.CitySchema;
import com.ies.schoolos.schema.CreateModifiedSchema;
import com.ies.schoolos.schema.DistrictSchema;
import com.ies.schoolos.schema.PostcodeSchema;
import com.ies.schoolos.schema.ProvinceSchema;
import com.ies.schoolos.schema.SessionSchema;
import com.ies.schoolos.schema.UserSchema;
import com.ies.schoolos.schema.info.PersonnelSchema;
import com.ies.schoolos.type.AliveStatus;
import com.ies.schoolos.type.BankAccountType;
import com.ies.schoolos.type.Blood;
import com.ies.schoolos.type.EmploymentType;
import com.ies.schoolos.type.Gender;
import com.ies.schoolos.type.LicenseLecturerType;
import com.ies.schoolos.type.MaritalStatus;
import com.ies.schoolos.type.Nationality;
import com.ies.schoolos.type.PersonnelStatus;
import com.ies.schoolos.type.Prename;
import com.ies.schoolos.type.Race;
import com.ies.schoolos.type.Religion;
import com.ies.schoolos.type.ResignType;
import com.ies.schoolos.type.dynamic.Department;
import com.ies.schoolos.type.dynamic.JobPosition;
import com.ies.schoolos.utility.Notification;
import com.vaadin.addon.tableexport.TemporaryFileDownloadResource;
import com.vaadin.data.Item;
import com.vaadin.data.Container.ItemSetChangeEvent;
import com.vaadin.data.Container.ItemSetChangeListener;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.filter.And;
import com.vaadin.data.util.filter.Compare.Equal;
import com.vaadin.data.util.sqlcontainer.RowId;
import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Window;

public class PersonnelResignView extends VerticalLayout {
	private static final long serialVersionUID = 1L;

	private HashMap<String, String> mapTitle;
	
	private SQLContainer provinceCon = Container.getProvinceContainer();
	private SQLContainer districtCon = Container.getDistrictContainer();
	private SQLContainer cityCon = Container.getCityContainer();
	private SQLContainer postcodeCon = Container.getPostcodeContainer();
	private SQLContainer pContainer = Container.getPersonnelContainer();
	private SQLContainer userContainer = Container.getUserContainer();
	private SQLContainer freeContainer;

	private Department dContainer = new Department();
	private JobPosition jContainer = new JobPosition();
	
	private Item item;
	
	private HorizontalLayout toolbar;
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
	
	public PersonnelResignView() {	
		pContainer.refresh();
		setSpacing(true);
		setMargin(true);
		buildMainLayout();
		initFieldGroup();
		fetchData();
		setFooterData();
		
		mapTitle = new HashMap<String, String>();
		mapTitle.put(PersonnelSchema.PEOPLE_ID,"เลขประจำตัวประชาชน");
		mapTitle.put(PersonnelSchema.PERSONNEL_CODE,"เลขประจำตัว");
		mapTitle.put(PersonnelSchema.PRENAME,"ชื่อต้น");
		mapTitle.put(PersonnelSchema.FIRSTNAME,"ชื่อ");
		mapTitle.put(PersonnelSchema.LASTNAME,"สกุล");
		mapTitle.put(PersonnelSchema.FIRSTNAME_ND,"ชื่อภาษาที่สอง");
		mapTitle.put(PersonnelSchema.LASTNAME_ND,"สกุลภาษาที่สอง");
		mapTitle.put(PersonnelSchema.FIRSTNAME_RD,"ชื่อภาษาที่สาม");
		mapTitle.put(PersonnelSchema.LASTNAME_RD,"ชื่อภาษาที่สาม");
		mapTitle.put(PersonnelSchema.NICKNAME,"ชื่อเล่น");
		mapTitle.put(PersonnelSchema.GENDER,"เพศ");
		mapTitle.put(PersonnelSchema.RELIGION,"ศาสนา");
		mapTitle.put(PersonnelSchema.RACE,"เชื้อชาติ");
		mapTitle.put(PersonnelSchema.NATIONALITY,"สัญชาติ");
		mapTitle.put(PersonnelSchema.MARITAL_STATUS,"สถานภาพ");
		mapTitle.put(PersonnelSchema.BIRTH_DATE,"วัน เดือน ปี เกิด");
		mapTitle.put(PersonnelSchema.BLOOD,"หมู่เลือด");
		mapTitle.put(PersonnelSchema.HEIGHT,"ส่วนสูง");
		mapTitle.put(PersonnelSchema.WEIGHT,"น้ำหนัก");
		mapTitle.put(PersonnelSchema.CONGENITAL_DISEASE,"โรคประจำตัว");
		mapTitle.put(PersonnelSchema.PERSONNEL_STATUS,"สถานะ");
		mapTitle.put(PersonnelSchema.JOB_POSITION_ID,"ตำแหน่ง");
		mapTitle.put(PersonnelSchema.DEPARTMENT_ID,"แผนก");
		mapTitle.put(PersonnelSchema.LICENSE_LECTURER_NUMBER,"เลขที่ใบประกอบวิชาชีพครู");
		mapTitle.put(PersonnelSchema.LICENSE_LECTURER_TYPE,"ประเภทใบประกอบวิชาชีพ");
		mapTitle.put(PersonnelSchema.LICENSE_LECTURER_ISSUED_DATE,"วัน เดือน ปี ที่ได้หมายเลขครู");
		mapTitle.put(PersonnelSchema.LICENSE_LECTURER_EXPIRED_DATE,"วัน เดือน ปี ที่หมดอายุหมายเลขครู");
		mapTitle.put(PersonnelSchema.LICENSE_11_NUMBER,"เลขที่ใบอนุญาติ สช 11");
		mapTitle.put(PersonnelSchema.LICENSE_ISSUE_AREA,"เขตพื้นที่ ที่ออก");
		mapTitle.put(PersonnelSchema.LICENSE_ISSUE_PROVINCE_ID,"ออกโดย(จังหวัด)");
		mapTitle.put(PersonnelSchema.LICENSE_17_NUMBER,"เลขที่ใบอนุญาติ สช 17");
		mapTitle.put(PersonnelSchema.LICENSE_18_NUMBER,"เลขที่ใบอนุญาติ สช 18");
		mapTitle.put(PersonnelSchema.LICENSE_19_NUMBER,"เลขที่ใบอนุญาติ สช 19");
		mapTitle.put(PersonnelSchema.FILL_DEGREE_POST,"วุฒิที่ได้รับการบรรจุ");
		mapTitle.put(PersonnelSchema.FILL_DEGREE_POST_DATE,"วัน เดือน ปีที่ได้รับการบรรจุ");
		mapTitle.put(PersonnelSchema.TEL,"โทร");
		mapTitle.put(PersonnelSchema.MOBILE,"มือถือ");
		mapTitle.put(PersonnelSchema.EMAIL,"อีเมล์");
		mapTitle.put(PersonnelSchema.CENSUS_ADDRESS,"ที่อยู่ตามทะเบียนบ้าน");
		mapTitle.put(PersonnelSchema.CENSUS_CITY_ID,"ตำบลตามทะเบียนบ้าน");
		mapTitle.put(PersonnelSchema.CENSUS_DISTRICT_ID,"อำเภอตามทะเบียนบ้าน");
		mapTitle.put(PersonnelSchema.CENSUS_PROVINCE_ID,"จังหวัดตามทะเบียนบ้าน");
		mapTitle.put(PersonnelSchema.CENSUS_POSTCODE_ID,"ไปรษณีย์");
		mapTitle.put(PersonnelSchema.CURRENT_ADDRESS,"ที่อยู่ปัจจุบัน");
		mapTitle.put(PersonnelSchema.CURRENT_CITY_ID,"ตำบลปัจจุบัน");
		mapTitle.put(PersonnelSchema.CURRENT_DISTRICT_ID,"อำเภอปัจจุบัน");
		mapTitle.put(PersonnelSchema.CURRENT_PROVINCE_ID,"จังหวัดปัจจุบัน");
		mapTitle.put(PersonnelSchema.CURRENT_POSTCODE_ID,"ไปรษณีย์ปัจจุบัน");
		mapTitle.put(PersonnelSchema.EMPLOYMENT_TYPE,"การว่าจ้าง");
		mapTitle.put(PersonnelSchema.START_WORK_DATE,"วันเริ่มทำงาน");
		mapTitle.put(PersonnelSchema.BANK_NAME,"ธนาคาร");
		mapTitle.put(PersonnelSchema.BANK_ACCOUNT_NUMBER,"หมายเลขบัญชี");
		mapTitle.put(PersonnelSchema.BANK_ACCOUNT_TYPE,"ประเภทบัญชี");
		mapTitle.put(PersonnelSchema.BANK_ACCOUNT_NAME,"ชื่อบัญชี");
		mapTitle.put(PersonnelSchema.BANK_ACCOUNT_BRANCH,"สาขา");
		mapTitle.put(PersonnelSchema.BANK_ACCOUNT_PROVINCE_ID,"จังหวัดธนาคาร");	
	}	
	
	private void buildMainLayout(){
		/* Toolbar */
		toolbar = new HorizontalLayout();
		toolbar.setSpacing(true);
		addComponent(toolbar);
				
		Button resignList = new Button("รายชื่อจำหน่ายออก", FontAwesome.FILE_TEXT_O);
		resignList.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event) {
				Window window = new Window();
				window.setSizeFull();
				window.setContent(new EditPersonnelResignView());
				UI.getCurrent().addWindow(window);
			}
		});
		toolbar.addComponent(resignList);	
		
		Button excelExport = new Button("ส่งออกไฟล์ Excel", FontAwesome.FILE_EXCEL_O);
		excelExport.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event) {
				exportExcel();
			}
		});
		toolbar.addComponent(excelExport);	
		
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
				new Equal(PersonnelSchema.PERSONNEL_STATUS, 0)));
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
	
	@SuppressWarnings("deprecation")
	private void exportExcel(){
		StringBuilder builder = new StringBuilder();
		builder.append(" SELECT * FROM " + PersonnelSchema.TABLE_NAME);
		builder.append(" WHERE " + PersonnelSchema.SCHOOL_ID + "=" + SessionSchema.getSchoolID());
		builder.append(" AND " + PersonnelSchema.PERSONNEL_STATUS + " <> " + 0);
		freeContainer = Container.getFreeFormContainer(builder.toString(), PersonnelSchema.PERSONNEL_ID);
		

		HSSFWorkbook workbook = new HSSFWorkbook(); 
		CellStyle cs = workbook.createCellStyle();
		cs.setWrapText(true);
		cs.setAlignment(CellStyle.ALIGN_CENTER);
		cs.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
		
		int rowIdex = 0;
		
		/* ################## สร้างทั้งหมด ################### */
		HSSFSheet sheet = workbook.createSheet("ทั้งหมด"); 	
		
		HSSFRow headerRow = sheet.createRow(rowIdex++);
		/* ใส่หัวตาราง */					
		int column = 0;
		
		for(Object colHead:freeContainer.getContainerPropertyIds()){
			if(mapTitle.containsKey(colHead.toString())){
				HSSFCell cell = headerRow.createCell(column++);
				cell.setCellValue(new HSSFRichTextString(getTitleColumn(colHead.toString())));
				cell.setCellStyle(cs);
			}
		}
		
		for(Object itemId:freeContainer.getItemIds()){
			Item item = freeContainer.getItem(itemId);
			HSSFRow row = sheet.createRow(rowIdex++);
			row.setHeightInPoints((3*sheet.getDefaultRowHeightInPoints()));
			column = 0;
			for(Object colHead:freeContainer.getContainerPropertyIds()){
				if(mapTitle.containsKey(colHead.toString())){
					String value = "";
					if(item.getItemProperty(colHead).getValue() != null){
						if(colHead.equals(PersonnelSchema.PRENAME))
							value = Prename.getNameTh(Integer.parseInt(item.getItemProperty(colHead).getValue().toString()));
						else if(colHead.equals(PersonnelSchema.GENDER))
							value = Gender.getNameTh(Integer.parseInt(item.getItemProperty(colHead).getValue().toString()));
						else if(colHead.equals(PersonnelSchema.RELIGION))
							value = Religion.getNameTh(Integer.parseInt(item.getItemProperty(colHead).getValue().toString()));
						else if(colHead.equals(PersonnelSchema.MARITAL_STATUS))
							value = MaritalStatus.getNameTh(Integer.parseInt(item.getItemProperty(colHead).getValue().toString()));
						else if(colHead.equals(PersonnelSchema.BLOOD))
							value = Blood.getNameTh(Integer.parseInt(item.getItemProperty(colHead).getValue().toString()));
						else if(colHead.equals(PersonnelSchema.PERSONNEL_STATUS))
							value = PersonnelStatus.getNameTh(Integer.parseInt(item.getItemProperty(colHead).getValue().toString()));
						else if(colHead.equals(PersonnelSchema.NATIONALITY))
							value = Nationality.getNameTh(Integer.parseInt(item.getItemProperty(colHead).getValue().toString()));
						else if(colHead.equals(PersonnelSchema.RACE))
							value = Race.getNameTh(Integer.parseInt(item.getItemProperty(colHead).getValue().toString()));
						else if(colHead.equals(PersonnelSchema.DEPARTMENT_ID))
							value = dContainer.getItem(item.getItemProperty(colHead).getValue()).getItemProperty("name").getValue().toString();
						else if(colHead.equals(PersonnelSchema.JOB_POSITION_ID))
							value = jContainer.getItem(item.getItemProperty(colHead).getValue()).getItemProperty("name").getValue().toString();
						else if(colHead.equals(PersonnelSchema.EMPLOYMENT_TYPE))
							value = EmploymentType.getNameTh(Integer.parseInt(item.getItemProperty(colHead).getValue().toString()));
						else if(colHead.equals(PersonnelSchema.LICENSE_LECTURER_TYPE))
							value = LicenseLecturerType.getNameTh(Integer.parseInt(item.getItemProperty(colHead).getValue().toString()));
						else if(colHead.equals(PersonnelSchema.BANK_ACCOUNT_TYPE))
							value = BankAccountType.getNameTh(Integer.parseInt(item.getItemProperty(colHead).getValue().toString()));
						else if(colHead.equals(PersonnelSchema.CENSUS_PROVINCE_ID) || 
								colHead.equals(PersonnelSchema.CURRENT_PROVINCE_ID) || 
								colHead.equals(PersonnelSchema.LICENSE_ISSUE_PROVINCE_ID) ||
								colHead.equals(PersonnelSchema.BANK_ACCOUNT_PROVINCE_ID))
							value = getProvinceName(Integer.parseInt(item.getItemProperty(colHead).getValue().toString()));
						else if(colHead.equals(PersonnelSchema.CENSUS_DISTRICT_ID) || colHead.equals(PersonnelSchema.CURRENT_DISTRICT_ID))
							value = getDistrictName(Integer.parseInt(item.getItemProperty(colHead).getValue().toString()));
						else if(colHead.equals(PersonnelSchema.CENSUS_CITY_ID) || colHead.equals(PersonnelSchema.CURRENT_CITY_ID))
							value = getCityName(Integer.parseInt(item.getItemProperty(colHead).getValue().toString()));
						else if(colHead.equals(PersonnelSchema.CENSUS_POSTCODE_ID) || colHead.equals(PersonnelSchema.CURRENT_POSTCODE_ID))
							value = getPostcodeName(Integer.parseInt(item.getItemProperty(colHead).getValue().toString()));
						else
							value = item.getItemProperty(colHead).getValue().toString();
					}
					
					HSSFCell cell = row.createCell(column++);
					cell.setCellValue(new HSSFRichTextString(value));
					cell.setCellStyle(cs);
				}
			}
		}
		
		/* ################## แยกแผนก ################### */
		ArrayList<Object> personnelStatus = new ArrayList<Object>();
		HSSFSheet sheetDepartment=null;
		for(final Object itemId:freeContainer.getItemIds()){
			Item item = freeContainer.getItem(itemId);
			if(!personnelStatus.contains(item.getItemProperty(PersonnelSchema.PERSONNEL_STATUS).getValue())){
				rowIdex = 0;
				sheetDepartment = workbook.createSheet(PersonnelStatus.getNameTh(Integer.parseInt(item.getItemProperty(PersonnelSchema.PERSONNEL_STATUS).getValue().toString()))); 					
				HSSFRow headerRowDepartment = sheetDepartment.createRow(rowIdex++);
				/* ใส่หัวตาราง */					
				column = 0;
				for(Object colHead:freeContainer.getContainerPropertyIds()){
					if(mapTitle.containsKey(colHead.toString())){
						HSSFCell cell = headerRowDepartment.createCell(column++);
						cell.setCellValue(new HSSFRichTextString(getTitleColumn(colHead.toString())));
						cell.setCellStyle(cs);
					}
				}
				personnelStatus.add(item.getItemProperty(PersonnelSchema.PERSONNEL_STATUS).getValue());
			}
			
			
			HSSFRow row = sheetDepartment.createRow(rowIdex++);
			row.setHeightInPoints((3*sheetDepartment.getDefaultRowHeightInPoints()));
			column = 0;
			for(Object colHead:freeContainer.getContainerPropertyIds()){
				if(mapTitle.containsKey(colHead.toString())){
					String value = "";
					if(item.getItemProperty(colHead).getValue() != null){
						if(colHead.equals(PersonnelSchema.PRENAME))
							value = Prename.getNameTh(Integer.parseInt(item.getItemProperty(colHead).getValue().toString()));
						else if(colHead.equals(PersonnelSchema.GENDER))
							value = Gender.getNameTh(Integer.parseInt(item.getItemProperty(colHead).getValue().toString()));
						else if(colHead.equals(PersonnelSchema.RELIGION))
							value = Religion.getNameTh(Integer.parseInt(item.getItemProperty(colHead).getValue().toString()));
						else if(colHead.equals(PersonnelSchema.MARITAL_STATUS))
							value = MaritalStatus.getNameTh(Integer.parseInt(item.getItemProperty(colHead).getValue().toString()));
						else if(colHead.equals(PersonnelSchema.BLOOD))
							value = Blood.getNameTh(Integer.parseInt(item.getItemProperty(colHead).getValue().toString()));
						else if(colHead.equals(PersonnelSchema.PERSONNEL_STATUS))
							value = PersonnelStatus.getNameTh(Integer.parseInt(item.getItemProperty(colHead).getValue().toString()));
						else if(colHead.equals(PersonnelSchema.NATIONALITY))
							value = Nationality.getNameTh(Integer.parseInt(item.getItemProperty(colHead).getValue().toString()));
						else if(colHead.equals(PersonnelSchema.RACE))
							value = Race.getNameTh(Integer.parseInt(item.getItemProperty(colHead).getValue().toString()));
						else if(colHead.equals(PersonnelSchema.DEPARTMENT_ID))
							value = dContainer.getItem(item.getItemProperty(colHead).getValue()).getItemProperty("name").getValue().toString();
						else if(colHead.equals(PersonnelSchema.JOB_POSITION_ID))
							value = jContainer.getItem(item.getItemProperty(colHead).getValue()).getItemProperty("name").getValue().toString();
						else if(colHead.equals(PersonnelSchema.EMPLOYMENT_TYPE))
							value = EmploymentType.getNameTh(Integer.parseInt(item.getItemProperty(colHead).getValue().toString()));
						else if(colHead.equals(PersonnelSchema.LICENSE_LECTURER_TYPE))
							value = LicenseLecturerType.getNameTh(Integer.parseInt(item.getItemProperty(colHead).getValue().toString()));
						else if(colHead.equals(PersonnelSchema.BANK_ACCOUNT_TYPE))
							value = BankAccountType.getNameTh(Integer.parseInt(item.getItemProperty(colHead).getValue().toString()));
						else if(colHead.equals(PersonnelSchema.CENSUS_PROVINCE_ID) || 
								colHead.equals(PersonnelSchema.CURRENT_PROVINCE_ID) || 
								colHead.equals(PersonnelSchema.LICENSE_ISSUE_PROVINCE_ID) ||
								colHead.equals(PersonnelSchema.BANK_ACCOUNT_PROVINCE_ID))
							value = getProvinceName(Integer.parseInt(item.getItemProperty(colHead).getValue().toString()));
						else if(colHead.equals(PersonnelSchema.CENSUS_DISTRICT_ID) || colHead.equals(PersonnelSchema.CURRENT_DISTRICT_ID))
							value = getDistrictName(Integer.parseInt(item.getItemProperty(colHead).getValue().toString()));
						else if(colHead.equals(PersonnelSchema.CENSUS_CITY_ID) || colHead.equals(PersonnelSchema.CURRENT_CITY_ID))
							value = getCityName(Integer.parseInt(item.getItemProperty(colHead).getValue().toString()));
						else if(colHead.equals(PersonnelSchema.CENSUS_POSTCODE_ID) || colHead.equals(PersonnelSchema.CURRENT_POSTCODE_ID))
							value = getPostcodeName(Integer.parseInt(item.getItemProperty(colHead).getValue().toString()));
						else
							value = item.getItemProperty(colHead).getValue().toString();
					}
					
					HSSFCell cell = row.createCell(column++);
					cell.setCellValue(new HSSFRichTextString(value));
					cell.setCellStyle(cs);
				}
			}
		}
		
		try{
			FileOutputStream fos = null; 
			File file = new File("รายชื่อบุคลากร.xls"); 
			fos = new FileOutputStream(file); 
			workbook.write(fos); 
			TemporaryFileDownloadResource resource = new TemporaryFileDownloadResource(UI.getCurrent(),
		                "personnel_resign.xls", "application/vnd.ms-excel", file);
			Page.getCurrent().open(resource, "_blank",false);

			fos.flush(); 
			fos.close(); 
		}catch(Exception ex){
			ex.printStackTrace();
		}
		
	}
	
	private String getTitleColumn(String key){
		return mapTitle.get(key);
	}
	
	private String getProvinceName(int itemId){
		String name = provinceCon.getItem(new RowId(itemId)).getItemProperty(ProvinceSchema.NAME).getValue().toString();		
		return name;
	}

	private String getDistrictName(int itemId){
		String name = districtCon.getItem(new RowId(itemId)).getItemProperty(DistrictSchema.NAME).getValue().toString();		
		return  name;
	}

	private String getCityName(int itemId){
		String name = cityCon.getItem(new RowId(itemId)).getItemProperty(CitySchema.NAME).getValue().toString();		
		return name;
	}

	private String getPostcodeName(int itemId){
		String code = postcodeCon.getItem(new RowId(itemId)).getItemProperty(PostcodeSchema.CODE).getValue().toString();		
		return code;
	}
}
