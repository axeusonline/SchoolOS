package com.ies.schoolos.component.registration;

import java.util.Locale;

import org.tepi.filtertable.FilterTable;

import com.ies.schoolos.container.Container;
import com.ies.schoolos.filter.TableFilterDecorator;
import com.ies.schoolos.filter.TableFilterGenerator;
import com.ies.schoolos.report.excel.RecruitStudentToExcel;
import com.ies.schoolos.schema.CreateModifiedSchema;
import com.ies.schoolos.schema.SessionSchema;
import com.ies.schoolos.schema.UserSchema;
import com.ies.schoolos.schema.info.StudentSchema;
import com.ies.schoolos.schema.info.StudentStudySchema;
import com.ies.schoolos.type.Prename;
import com.ies.schoolos.type.ResignType;
import com.ies.schoolos.type.StudentStatus;
import com.ies.schoolos.utility.Notification;
import com.vaadin.addon.tableexport.ExcelExport;
import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.filter.And;
import com.vaadin.data.util.filter.Compare.Equal;
import com.vaadin.data.util.sqlcontainer.RowId;
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

public class ResignStudentView extends VerticalLayout {
	private static final long serialVersionUID = 1L;

	private SQLContainer freeContainer;
	private SQLContainer ssContainer = Container.getStudentStudyContainer();
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
	private TextArea resignDesription;
	private Button save;	
	
	public ResignStudentView() {	
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
                excelExport.setReportTitle("Student");
				excelExport.setExportFileName("personnel.xls");
                excelExport.export();
                
                removeComponent(tableEx);
			}
		});
		//toolbar.addComponent(excelExport);	
		
		HorizontalLayout resignLayout = new HorizontalLayout();
		resignLayout.setWidth("100%");
		resignLayout.setSpacing(true);
		addComponent(resignLayout);
		setExpandRatio(resignLayout, 1);
		
		/* Content */
		table = new FilterTable();
		table.setSelectable(true);
		table.setFooterVisible(true); 
		table.setWidth("100%");
		table.addValueChangeListener(new ValueChangeListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				if(event.getProperty().getValue() != null){
					firstname.setReadOnly(true);
					lastname.setReadOnly(true);
					
					normalMode();
					
					save.setCaption("แก้ไข");
					item = freeContainer.getItem(event.getProperty().getValue());
					initFieldGroup();

					readOnlyMode();
				}
			}
		});
		
		table.addContainerProperty(StudentStudySchema.STUDENT_CODE, String.class, null);
		table.addContainerProperty(StudentSchema.PRENAME, String.class, null);
		table.addContainerProperty(StudentSchema.FIRSTNAME, String.class, null);
		table.addContainerProperty(StudentSchema.LASTNAME, String.class, null);
		table.addContainerProperty(StudentStudySchema.STUDENT_STATUS, String.class, null);
		
		table.setFilterDecorator(new TableFilterDecorator());
		table.setFilterGenerator(new TableFilterGenerator());
        table.setFilterBarVisible(true);

	    setFooterData();
		initTableStyle();
		table.sort(new Object[]{StudentStudySchema.STUDENT_CODE}, new boolean[]{true});

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
		firstname.setReadOnly(true);
		firstname.setWidth("-1px");
		firstname.setHeight("-1px");
		resignForm.addComponent(firstname);
		
		lastname = new TextField("สกุล");
		lastname.setInputPrompt("สกุล");
		lastname.setNullRepresentation("");
		lastname.setImmediate(false);
		lastname.setRequired(true);
		lastname.setReadOnly(true);
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
		resignDate.setInputPrompt("วว/ดด/ปปปป");
		resignDate.setImmediate(false);
		resignDate.setRequired(true);
		resignDate.setWidth("-1px");
		resignDate.setHeight("-1px");
		resignDate.setDateFormat("dd/MM/yyyy");
		resignDate.setLocale(new Locale("th", "TH"));
		resignForm.addComponent(resignDate);
		
		resignDesription = new TextArea("รายละเอียด");
		resignDesription.setInputPrompt("รายละเอียดเพิ่มเติม");
		resignDesription.setImmediate(false);
		resignDesription.setWidth("-1px");
		resignDesription.setHeight("-1px");
		resignDesription.setNullRepresentation("");
		resignForm.addComponent(resignDesription);
		
		save = new Button("บันทึก", FontAwesome.SAVE);
		save.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 1L;

			@SuppressWarnings("unchecked")
			@Override
			public void buttonClick(ClickEvent event) {
				try {
					normalMode();

					Item studentItem = ssContainer.getItem(new RowId(item.getItemProperty(StudentStudySchema.STUDENT_STUDY_ID).getValue()));
					studentItem.getItemProperty(StudentStudySchema.STUDENT_STATUS).setValue(Integer.parseInt(resignType.getValue().toString())+3);
					studentItem.getItemProperty(StudentStudySchema.RESIGN_BY_ID).setValue(SessionSchema.getUserID());
					studentItem.getItemProperty(StudentStudySchema.RESIGN_TYPE).setValue(Integer.parseInt(resignType.getValue().toString()));
					studentItem.getItemProperty(StudentStudySchema.RESIGN_DATE).setValue(resignDate.getValue());
					studentItem.getItemProperty(StudentStudySchema.RESIGN_DESCRIPTION).setValue(resignDesription.getValue());
					CreateModifiedSchema.setCreateAndModified(studentItem);
					ssContainer.commit();

					userContainer.addContainerFilter(new And(
							new Equal(UserSchema.REF_USER_ID, Integer.parseInt(item.getItemProperty(StudentStudySchema.STUDENT_ID).getValue().toString())),
							new Equal(UserSchema.REF_USER_TYPE, 2),
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
		table.setColumnHeader(StudentStudySchema.STUDENT_CODE, "หมายเลขประจำตัว");
		table.setColumnHeader(StudentSchema.PRENAME, "ชื่อต้น");
		table.setColumnHeader(StudentSchema.FIRSTNAME, "ชื่อ");
		table.setColumnHeader(StudentSchema.LASTNAME, "สกุล");
		table.setColumnHeader(StudentStudySchema.STUDENT_STATUS, "ตำแหน่ง");
		
		table.setVisibleColumns(
				StudentStudySchema.STUDENT_CODE, 
				StudentSchema.PRENAME,
				StudentSchema.FIRSTNAME, 
				StudentSchema.LASTNAME,
				StudentStudySchema.STUDENT_STATUS);
		
	}
	
	private void fetchData(){
		table.removeAllItems();

		StringBuilder builder = new StringBuilder();
		builder.append(" SELECT * FROM " + StudentStudySchema.TABLE_NAME + " ss");
		builder.append(" INNER JOIN " + StudentSchema.TABLE_NAME + " s ON s." + StudentSchema.STUDENT_ID + "=ss." +StudentStudySchema.STUDENT_ID);
		builder.append(" WHERE (" + StudentStudySchema.STUDENT_STATUS + "=" + 0);
		builder.append(" OR " + StudentStudySchema.STUDENT_STATUS + "=" + 2 + ")");
		builder.append(" AND ss." + StudentStudySchema.SCHOOL_ID + "=" + SessionSchema.getSchoolID());

		freeContainer = Container.getFreeFormContainer(builder.toString(), StudentStudySchema.STUDENT_STUDY_ID);
		for(final Object itemId:freeContainer.getItemIds()){
			Item item = freeContainer.getItem(itemId);
						
			table.addItem(new Object[]{
				item.getItemProperty(StudentStudySchema.STUDENT_CODE).getValue(),
				Prename.getNameTh((int)item.getItemProperty(StudentSchema.PRENAME).getValue()),
				item.getItemProperty(StudentSchema.FIRSTNAME).getValue(),
				item.getItemProperty(StudentSchema.LASTNAME).getValue(),
				StudentStatus.getNameTh((int)item.getItemProperty(StudentStudySchema.STUDENT_STATUS).getValue())
			}, itemId);
		}
	}
	
	/*นำจำนวนที่นับ มาใส่ค่าในส่วนท้ายตาราง*/
	private void setFooterData(){
		table.setColumnFooter(StudentStudySchema.STUDENT_CODE, "ทั้งหมด: "+ table.size() + " คน");
	}
	
	/* จัดกลุ่มของ ฟอร์มในการแก้ไข - เพิ่ม ข้อมูล */
	private void initFieldGroup(){		
		resignBinder = new FieldGroup(item);
		resignBinder.setBuffered(true);
		resignBinder.bind(firstname, StudentSchema.FIRSTNAME);
		resignBinder.bind(lastname, StudentSchema.LASTNAME);
		resignBinder.bind(resignType, StudentStudySchema.RESIGN_TYPE);
		resignBinder.bind(resignDate, StudentStudySchema.RESIGN_DATE);
		resignBinder.bind(resignDesription, StudentStudySchema.RESIGN_DESCRIPTION);
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
