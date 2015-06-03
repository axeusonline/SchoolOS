package com.ies.schoolos.component.personnel;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellStyle;
import org.tepi.filtertable.FilterTable;
import org.vaadin.dialogs.ConfirmDialog;

import com.ies.schoolos.container.Container;
import com.ies.schoolos.filter.TableFilterDecorator;
import com.ies.schoolos.filter.TableFilterGenerator;
import com.ies.schoolos.report.PersonnelReport;
import com.ies.schoolos.schema.CitySchema;
import com.ies.schoolos.schema.DistrictSchema;
import com.ies.schoolos.schema.PostcodeSchema;
import com.ies.schoolos.schema.ProvinceSchema;
import com.ies.schoolos.schema.SessionSchema;
import com.ies.schoolos.schema.info.PersonnelSchema;
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
import com.ies.schoolos.type.dynamic.Department;
import com.ies.schoolos.type.dynamic.JobPosition;
import com.ies.schoolos.utility.Notification;
import com.vaadin.addon.tableexport.TemporaryFileDownloadResource;
import com.vaadin.data.Item;
import com.vaadin.data.Container.ItemSetChangeEvent;
import com.vaadin.data.Container.ItemSetChangeListener;
import com.vaadin.data.util.filter.Compare.Equal;
import com.vaadin.data.util.sqlcontainer.RowId;
import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.UI;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseListener;

public class PersonnelListView extends VerticalLayout {

	private static final long serialVersionUID = 1L;
	
	private HashMap<String, String> mapTitle;

	private Container container = new Container();
	private SQLContainer provinceCon = container.getProvinceContainer();
	private SQLContainer districtCon = container.getDistrictContainer();
	private SQLContainer cityCon = container.getCityContainer();
	private SQLContainer postcodeCon = container.getPostcodeContainer();
	private SQLContainer pContainer = container.getPersonnelContainer();
	
	private Department dContainer = new Department();
	private JobPosition jContainer = new JobPosition();
	
	private HorizontalLayout toolbar;
	private Button add;	
	private FilterTable  table;
	
	public PersonnelListView() {	
		setSizeFull();
		setSpacing(true);
		setMargin(true);
		buildMainLayout();
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
				
		add = new Button("เพิ่ม", FontAwesome.USER);
		add.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event) {
				Window addLayout = new Window();
				addLayout.setSizeFull();
				addLayout.setContent(new AddPersonnelView());
				addLayout.addCloseListener(new CloseListener() {
					private static final long serialVersionUID = 1L;

					@Override
					public void windowClose(CloseEvent e) {
						fetchData();
						setFooterData();
					}
				});
				UI.getCurrent().addWindow(addLayout);
			}
		});
		toolbar.addComponent(add);
		
		Button excelExport = new Button("ส่งออกไฟล์ Excel", FontAwesome.FILE_EXCEL_O);
		excelExport.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event) {
				exportExcel();
			}
		});
		toolbar.addComponent(excelExport);	
		
		/* Content */
		table = new FilterTable();
		table.setSizeFull();
		table.setSelectable(true);
		table.setFooterVisible(true);        
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
		table.addContainerProperty("", HorizontalLayout.class, null);
		
		table.setFilterDecorator(new TableFilterDecorator());
		table.setFilterGenerator(new TableFilterGenerator());
        table.setFilterBarVisible(true);

		initTableStyle();
		table.sort(new Object[]{PersonnelSchema.PERSONNEL_CODE}, new boolean[]{true});

		table.setColumnReorderingAllowed(true);
		table.setColumnCollapsingAllowed(true);

		addComponent(table);
        setExpandRatio(table, 1);
	}
	
	/* ตั้งค่ารูปแบบแสดงของตาราง */
	private void initTableStyle(){		
		table.setColumnHeader(PersonnelSchema.PERSONNEL_CODE, "หมายเลขประจำตัว");
		table.setColumnHeader(PersonnelSchema.PRENAME, "ชื่อต้น");
		table.setColumnHeader(PersonnelSchema.FIRSTNAME, "ชื่อ");
		table.setColumnHeader(PersonnelSchema.LASTNAME, "สกุล");
		table.setColumnHeader(PersonnelSchema.JOB_POSITION_ID, "ตำแหน่ง");
		table.setColumnHeader(PersonnelSchema.PERSONNEL_STATUS, "สถานะ");
		table.setColumnHeader("", "");
		
		table.setVisibleColumns(
				PersonnelSchema.PERSONNEL_CODE, 
				PersonnelSchema.PRENAME,
				PersonnelSchema.FIRSTNAME, 
				PersonnelSchema.LASTNAME,
				PersonnelSchema.JOB_POSITION_ID,
				PersonnelSchema.PERSONNEL_STATUS,
				"");
	}
	
	private void fetchData(){
		pContainer.refresh();
		
		pContainer.removeAllContainerFilters();
		pContainer.addContainerFilter(new Equal(PersonnelSchema.SCHOOL_ID,SessionSchema.getSchoolID()));
		pContainer.sort(new Object[]{PersonnelSchema.PERSONNEL_CODE,PersonnelSchema.DEPARTMENT_ID}, new boolean[]{true,true});
		for(final Object itemId:pContainer.getItemIds()){
			Item item = pContainer.getItem(itemId);
			
			table.addItem(new Object[]{
				item.getItemProperty(PersonnelSchema.PERSONNEL_CODE).getValue(),
				Prename.getNameTh((int)item.getItemProperty(PersonnelSchema.PRENAME).getValue()),
				item.getItemProperty(PersonnelSchema.FIRSTNAME).getValue(),
				item.getItemProperty(PersonnelSchema.LASTNAME).getValue(),
				jContainer.getItem(item.getItemProperty(PersonnelSchema.JOB_POSITION_ID).getValue()).getItemProperty("name").getValue().toString(),
				PersonnelStatus.getNameTh((int)item.getItemProperty(PersonnelSchema.PERSONNEL_STATUS).getValue()),
				initButtonLayout(item, itemId)
			}, itemId);
		}
		
		table.setFilterFieldValue(PersonnelSchema.PERSONNEL_STATUS, PersonnelStatus.getNameTh(0));
	}
	
	private HorizontalLayout initButtonLayout(final Item item, final Object itemId){
		final HorizontalLayout buttonLayout = new HorizontalLayout();
			
		Button personnelGraduatedButton = new Button(FontAwesome.GRADUATION_CAP);
		personnelGraduatedButton.setId(itemId.toString());
		personnelGraduatedButton.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 1L;
			@Override
			public void buttonClick(ClickEvent event) {
				Window graduatedLayout = new Window();
				graduatedLayout.setSizeFull();
				graduatedLayout.setContent(new PersonnelGraduatedHistoryView(item.getItemProperty(PersonnelSchema.PERSONNEL_ID).getValue()));
				UI.getCurrent().addWindow(graduatedLayout);
			}
		});
		buttonLayout.addComponent(personnelGraduatedButton);
		buttonLayout.setComponentAlignment(personnelGraduatedButton, Alignment.MIDDLE_CENTER);
		
		Button	print = new Button("พิมพ์ประวัติ",FontAwesome.PRINT);
		print.setWidth("100%");
		print.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event) {
				new PersonnelReport(Integer.parseInt(itemId.toString()));
			}
		});
		buttonLayout.addComponent(print);
		buttonLayout.setComponentAlignment(print, Alignment.MIDDLE_CENTER);
		
		Button editButton = new Button(FontAwesome.EDIT);
		editButton.setId(itemId.toString());
		editButton.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 1L;
			@Override
			public void buttonClick(ClickEvent event) {
				Window editLayout = new Window();
				editLayout.setSizeFull();
				editLayout.setContent(new EditPersonnelView(item.getItemProperty(PersonnelSchema.PERSONNEL_ID).getValue()));
				UI.getCurrent().addWindow(editLayout);
			}
		});
		buttonLayout.addComponent(editButton);
		buttonLayout.setComponentAlignment(editButton, Alignment.MIDDLE_CENTER);
		
		Button removeButton = new Button(FontAwesome.TRASH_O);
		removeButton.setId(itemId.toString());
		removeButton.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 1L;
			@Override
			public void buttonClick(ClickEvent event) {
				ConfirmDialog.show(UI.getCurrent(), "ลบบุคลากร","การลบข้อมูลจะส่งผลต่อข้อมูลประวัติการทำงาน และการสอนทั้งหมด คุณต้องการลบบุคลากรนี้ใช่หรือไม่?","ตกลง","ยกเลิก",
			        new ConfirmDialog.Listener() {
						private static final long serialVersionUID = 1L;
						public void onClose(ConfirmDialog dialog) {
			                if (dialog.isConfirmed()) {
			                	table.removeItem(itemId);
			                	if(pContainer.removeItem(itemId)){
			                		try {
			                			pContainer.commit();
			                			setFooterData();
									}catch (Exception e1) {
										Notification.show("ลบข้อมูลไม่สำเร็จ กรุณาลองใหม่อีกครั้ง" , Type.WARNING_MESSAGE);
										e1.printStackTrace();
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
		table.setColumnFooter(PersonnelSchema.PERSONNEL_CODE, "ทั้งหมด: "+ table.size() + " คน");
	}
	
	@SuppressWarnings("deprecation")
	private void exportExcel(){
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
		for(Object colHead:pContainer.getContainerPropertyIds()){
			if(mapTitle.containsKey(colHead.toString())){
				HSSFCell cell = headerRow.createCell(column++);
				cell.setCellValue(new HSSFRichTextString(getTitleColumn(colHead.toString())));
				cell.setCellStyle(cs);
			}
		}
		
		for(Object itemId:pContainer.getItemIds()){
			Item item = pContainer.getItem(itemId);
			HSSFRow row = sheet.createRow(rowIdex++);
			row.setHeightInPoints((3*sheet.getDefaultRowHeightInPoints()));
			column = 0;
			for(Object colHead:pContainer.getContainerPropertyIds()){
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
		ArrayList<Object> department = new ArrayList<Object>();
		HSSFSheet sheetDepartment=null;
		for(final Object itemId:pContainer.getItemIds()){
			Item item = pContainer.getItem(itemId);
			if(!department.contains(item.getItemProperty(PersonnelSchema.DEPARTMENT_ID).getValue())){
				rowIdex = 0;
				String departmentStr = dContainer.getItem(item.getItemProperty(PersonnelSchema.DEPARTMENT_ID).getValue()).getItemProperty("name").getValue().toString();
				
				sheetDepartment = workbook.createSheet(departmentStr);

				
				HSSFRow headerRowDepartment = sheetDepartment.createRow(rowIdex++);
				/* ใส่หัวตาราง */					
				column = 0;
				for(Object colHead:pContainer.getContainerPropertyIds()){
					if(mapTitle.containsKey(colHead.toString())){
						HSSFCell cell = headerRowDepartment.createCell(column++);
						cell.setCellValue(new HSSFRichTextString(getTitleColumn(colHead.toString())));
						cell.setCellStyle(cs);
					}
				}
				department.add(item.getItemProperty(PersonnelSchema.DEPARTMENT_ID).getValue());
			}
			
			
			HSSFRow row = sheetDepartment.createRow(rowIdex++);
			row.setHeightInPoints((3*sheetDepartment.getDefaultRowHeightInPoints()));
			column = 0;
			for(Object colHead:pContainer.getContainerPropertyIds()){
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
		                "personnel.xls", "application/vnd.ms-excel", file);
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
