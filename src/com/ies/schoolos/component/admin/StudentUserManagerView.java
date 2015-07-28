package com.ies.schoolos.component.admin;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

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
import com.ies.schoolos.schema.CreateModifiedSchema;
import com.ies.schoolos.schema.SessionSchema;
import com.ies.schoolos.schema.UserSchema;
import com.ies.schoolos.schema.fundamental.ClassRoomSchema;
import com.ies.schoolos.schema.info.StudentClassRoomSchema;
import com.ies.schoolos.schema.info.StudentSchema;
import com.ies.schoolos.schema.info.StudentStudySchema;
import com.ies.schoolos.type.Feature;
import com.ies.schoolos.type.Prename;
import com.ies.schoolos.type.UserType;
import com.ies.schoolos.utility.BCrypt;
import com.ies.schoolos.utility.DateTimeUtil;
import com.vaadin.addon.tableexport.TemporaryFileDownloadResource;
import com.vaadin.data.Item;
import com.vaadin.data.Container.ItemSetChangeEvent;
import com.vaadin.data.Container.ItemSetChangeListener;
import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.ui.Button;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.ProgressBar;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Notification.Type;

public class StudentUserManagerView extends VerticalLayout {

	private static final long serialVersionUID = 1L;
	
	private Container container = new Container();
	
	public class WorkThread extends Thread {
		volatile double size = freeContainer.size();
		volatile double count = 1.0;
	    volatile double current = 0.0;
	    
	    public WorkThread() {
		}
	    
	    @Override
	    public void run() {
			// Count up until 1.0 is reached
	        while (current < 1.0) {
	        	for(final Object itemId: freeContainer.getItemIds()){	
					final Item item = freeContainer.getItem(itemId);
					current = count/size;
					count++;
					
		        	generatedUser(item);
		        	
		           /* สร้าง Thread */
		            try {
		                sleep(50); // Sleep for 50 milliseconds
		            } catch (InterruptedException e) {}
		  
		            // Update the UI thread-safely
		            UI.getCurrent().access(new Runnable() {
		                @Override
		                public void run() {
		                    progressBar.setValue(new Float(current));
		                    table.removeItem(itemId);
		                    setFooterData();
		                    
		                    if (current < 1.0)
		                        status.setValue("" +
		                            ((int)(current*100)) + "% เสร็จสิ้น");
		                    else
		                        status.setValue("100% เสร็จสิ้น");
		                    
		                }
		            });
	        	}
	        }
	    }
	}
	
	private SQLContainer freeContainer;
	private SQLContainer userContainer = container.getUserContainer();
	
	private FilterTable  table;
	
	private FormLayout userForm;
	private ProgressBar progressBar;
	private Label status;
	private Button generatedUser;
	
	public StudentUserManagerView() {	
		setSizeFull();
		setSpacing(true);
		setMargin(true);
		buildMainLayout();
		fetchData();
		setFooterData();
	}	
	
	private void buildMainLayout(){
		HorizontalLayout userLayout = new HorizontalLayout();
		userLayout.setSizeFull();
		userLayout.setSpacing(true);
		addComponent(userLayout);
		setExpandRatio(userLayout, 1);
		
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
		table.addContainerProperty(StudentStudySchema.STUDENT_CODE, String.class, null);
		table.addContainerProperty(StudentSchema.PEOPLE_ID, String.class, null);
		table.addContainerProperty(StudentSchema.PRENAME, String.class, null);
		table.addContainerProperty(StudentSchema.FIRSTNAME, String.class, null);
		table.addContainerProperty(StudentSchema.LASTNAME, String.class, null);

		table.setFilterDecorator(new TableFilterDecorator());
		table.setFilterGenerator(new TableFilterGenerator());
        table.setFilterBarVisible(true);
        
		initTableStyle();
		table.sort(new Object[]{StudentStudySchema.STUDENT_CODE}, new boolean[]{true});

		table.setColumnReorderingAllowed(true);
		table.setColumnCollapsingAllowed(true);
		userLayout.addComponent(table);
		userLayout.setExpandRatio(table, 2);
		
		userForm = new FormLayout();
		userForm.setSpacing(true);
		userForm.setStyleName("border-white");
		userLayout.addComponent(userForm);
		userLayout.setExpandRatio(userForm, 1);
		
		Label formLab = new Label("สร้างผู้ใช้งาน");
		userForm.addComponent(formLab);
		
		HorizontalLayout barbar = new HorizontalLayout();
		userForm.addComponent(barbar);
		
		progressBar = new ProgressBar();
		barbar.addComponent(progressBar);
		
		status = new Label("ยังไม่ได้ดำเนินการ");
		barbar.addComponent(status);
		
		generatedUser = new Button("สร้างบัญชี", FontAwesome.USERS);
		generatedUser.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event) {
				final WorkThread thread = new WorkThread();
		        thread.start();

		        UI.getCurrent().setPollInterval(500);

		        progressBar.setEnabled(true);
		        status.setValue("กำลังสร้าง...");
			}
		});
		userForm.addComponent(generatedUser);
		
		Button excelExport = new Button("ส่งออกไฟล์ Excel", FontAwesome.FILE_EXCEL_O);
		excelExport.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event) {
				exportUserToExcel();
			}
		});
		userForm.addComponent(excelExport);
		
	}
	
	/* ตั้งค่ารูปแบบแสดงของตาราง */
	private void initTableStyle(){		
		table.setColumnHeader(StudentStudySchema.STUDENT_CODE, "หมายเลขประจำตัว");
		table.setColumnHeader(StudentSchema.PEOPLE_ID, "หมายเลข ประชาชน");
		table.setColumnHeader(StudentSchema.PRENAME, "ชื่อต้น");
		table.setColumnHeader(StudentSchema.FIRSTNAME, "ชื่อ");
		table.setColumnHeader(StudentSchema.LASTNAME, "สกุล");
		
		table.setVisibleColumns(
				StudentStudySchema.STUDENT_CODE, 
				StudentSchema.PEOPLE_ID,
				StudentSchema.PRENAME,
				StudentSchema.FIRSTNAME, 
				StudentSchema.LASTNAME);
		
	}
	
	private void fetchData(){
		StringBuilder builder = new StringBuilder();
		builder.append(" SELECT * FROM " + StudentStudySchema.TABLE_NAME + " ss");
		builder.append(" INNER JOIN " + StudentSchema.TABLE_NAME + " s ON ss."+ StudentStudySchema.STUDENT_ID + "=s." + StudentSchema.STUDENT_ID);
		builder.append(" WHERE s." + StudentSchema.SCHOOL_ID + "=" + SessionSchema.getSchoolID());
		builder.append(" AND s." + StudentSchema.STUDENT_ID + " NOT IN (");
		builder.append(" SELECT " + UserSchema.REF_USER_ID + " FROM " + UserSchema.TABLE_NAME);
		builder.append(" WHERE " + UserSchema.SCHOOL_ID + "=" + SessionSchema.getSchoolID());
		builder.append(" AND " + UserSchema.REF_USER_TYPE + "=" + UserType.STUDENT + ")");

		table.removeAllItems();
		
		freeContainer = container.getFreeFormContainer(builder.toString(), StudentStudySchema.STUDENT_STUDY_ID);
		for(final Object itemId:freeContainer.getItemIds()){
			Item item = freeContainer.getItem(itemId);

			table.addItem(new Object[]{
				item.getItemProperty(StudentStudySchema.STUDENT_CODE).getValue(),
				item.getItemProperty(StudentSchema.PEOPLE_ID).getValue(),
				Prename.getNameTh((int)item.getItemProperty(StudentSchema.PRENAME).getValue()),
				item.getItemProperty(StudentSchema.FIRSTNAME).getValue(),
				item.getItemProperty(StudentSchema.LASTNAME).getValue(),			
			}, itemId);
		}
	}
	
	/*นำจำนวนที่นับ มาใส่ค่าในส่วนท้ายตาราง*/
	private void setFooterData(){
		table.setColumnFooter(StudentStudySchema.STUDENT_CODE, "ทั้งหมด: "+ table.size() + " คน");
	}
	
	/* กำหนดค่าภายใน FieldGroup ไปยัง Item */
	@SuppressWarnings({ "unchecked"})
	private boolean generatedUser(Item studentItem){
		try {				
			/* เพิ่มข้อมูล */
			Object tmpItem = userContainer.addItem();
			Item item = userContainer.getItem(tmpItem);

			item.getItemProperty(UserSchema.SCHOOL_ID).setValue(SessionSchema.getSchoolID());
			item.getItemProperty(UserSchema.FIRSTNAME).setValue(studentItem.getItemProperty(StudentSchema.FIRSTNAME).getValue());
			item.getItemProperty(UserSchema.LASTNAME).setValue(studentItem.getItemProperty(StudentSchema.LASTNAME).getValue());
			item.getItemProperty(UserSchema.EMAIL).setValue(studentItem.getItemProperty(StudentStudySchema.STUDENT_CODE).getValue());
			item.getItemProperty(UserSchema.PASSWORD).setValue(BCrypt.hashpw(studentItem.getItemProperty(StudentSchema.PEOPLE_ID).getValue().toString(), BCrypt.gensalt()));
			item.getItemProperty(UserSchema.STATUS).setValue(0);
			item.getItemProperty(UserSchema.REF_USER_ID).setValue(Integer.parseInt(studentItem.getItemProperty(StudentSchema.STUDENT_ID).getValue().toString()));
			item.getItemProperty(UserSchema.REF_USER_TYPE).setValue(UserType.STUDENT);
			Feature.setPermission(item, false);
			item.getItemProperty(UserSchema.IS_EDITED).setValue(false);
			CreateModifiedSchema.setCreateAndModified(item);
			userContainer.commit();
			return true;
		} catch (Exception e) {
			Notification.show("บันทึกไม่สำเร็จ", Type.WARNING_MESSAGE);
			e.printStackTrace();
			return false;
		}
	}
	
	@SuppressWarnings("deprecation")
	private void exportUserToExcel(){
		StringBuilder builder = new StringBuilder();
		builder.append(" SELECT s."+StudentSchema.PRENAME +", s."+StudentSchema.FIRSTNAME);
		builder.append(",s."+StudentSchema.LASTNAME+", s."+StudentSchema.PEOPLE_ID);
		builder.append(",ss."+StudentStudySchema.STUDENT_CODE + ", ss."+StudentStudySchema.STUDENT_STUDY_ID);
		builder.append(",cr."+ClassRoomSchema.NAME);
		builder.append(" FROM " + StudentStudySchema.TABLE_NAME + " ss");
		builder.append(" INNER JOIN " + StudentSchema.TABLE_NAME + " s ON ss."+ StudentStudySchema.STUDENT_ID + "=s." + StudentSchema.STUDENT_ID);
		builder.append(" INNER JOIN " + StudentClassRoomSchema.TABLE_NAME + " scr ON scr."+ StudentClassRoomSchema.STUDENT_STUDY_ID + "=ss." + StudentStudySchema.STUDENT_STUDY_ID);
		builder.append(" INNER JOIN " + ClassRoomSchema.TABLE_NAME + " cr ON cr."+ ClassRoomSchema.CLASS_ROOM_ID + "= scr." + StudentClassRoomSchema.CLASS_ROOM_ID);
		builder.append(" WHERE s." + StudentSchema.SCHOOL_ID + "=" + SessionSchema.getSchoolID());
		builder.append(" AND scr." + StudentClassRoomSchema.ACADEMIC_YEAR + "=" + DateTimeUtil.getBuddishYear());
		builder.append(" ORDER BY cr." + ClassRoomSchema.CLASS_YEAR);
		builder.append(" , cr." + ClassRoomSchema.NUMBER);
		builder.append(" , s." + StudentSchema.FIRSTNAME);
		builder.append(" , s." + StudentSchema.LASTNAME);

		table.removeAllItems();
		
		freeContainer = container.getFreeFormContainer(builder.toString(), StudentStudySchema.STUDENT_CODE);
		
		HSSFWorkbook workbook = new HSSFWorkbook(); 
		CellStyle cs = workbook.createCellStyle();
		cs.setWrapText(true);
		cs.setAlignment(CellStyle.ALIGN_CENTER);
		cs.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
		
		int row = 0;
		HSSFSheet sheet = null;
		/* เก็บชื่อของอาจารย์ ที่ไม่ซ้ำกัน 
		 * โดยที่ Key เก็บชื่อ */
		ArrayList<String> teachingAssigned = new ArrayList<String>();
		
		for(Object itemId:freeContainer.getItemIds()){
			Item item = freeContainer.getItem(itemId);
			String roomName = item.getItemProperty(ClassRoomSchema.NAME).getValue().toString().replace("/", "-");
			if(!teachingAssigned.contains(roomName)){
				teachingAssigned.add(roomName);
				row = 0;
				
				sheet = workbook.createSheet(roomName); 				
				
				/* ใส่หัวตาราง */
				HSSFRow headerRow = sheet.createRow(row);
				HSSFCell cell1 = headerRow.createCell(0);
				cell1.setCellValue(new HSSFRichTextString("ชื่อ สกุล"));
				cell1.setCellStyle(cs);
				
				HSSFCell cell2 = headerRow.createCell(1);
				cell2.setCellValue(new HSSFRichTextString("บัญชีผู้ใช้"));
				cell2.setCellStyle(cs);
				
				HSSFCell cell3 = headerRow.createCell(2);
				cell3.setCellValue(new HSSFRichTextString("รหัสผ่าน"));
				cell3.setCellStyle(cs);
			}else{
				StringBuilder name = new StringBuilder();
				name.append(Prename.getNameTh(Integer.parseInt(item.getItemProperty(StudentSchema.PRENAME).getValue().toString()))+ " ");
				name.append(item.getItemProperty(StudentSchema.FIRSTNAME).getValue().toString()+ " ");
				name.append(item.getItemProperty(StudentSchema.LASTNAME).getValue().toString());

				HSSFRow dataRow = sheet.createRow(row);
				HSSFCell cell1 = dataRow.createCell(0);
				cell1.setCellValue(new HSSFRichTextString(name.toString()));
				cell1.setCellStyle(cs);
				
				HSSFCell cell2 = dataRow.createCell(1);
				cell2.setCellValue(new HSSFRichTextString(item.getItemProperty(StudentStudySchema.STUDENT_CODE).getValue().toString()));
				cell2.setCellStyle(cs);
				
				HSSFCell cell3 = dataRow.createCell(2);
				cell3.setCellValue(new HSSFRichTextString(item.getItemProperty(StudentSchema.PEOPLE_ID).getValue().toString()));
				cell3.setCellStyle(cs);
			}
			row++;
		}
		
		try{			
			FileOutputStream fos = null; 
			File file = new File("ตารางสอน.xls"); 
			fos = new FileOutputStream(file); 
			workbook.write(fos); 
			TemporaryFileDownloadResource resource = new TemporaryFileDownloadResource(UI.getCurrent(),
		                "username.xls", "application/vnd.ms-excel", file);
			Page.getCurrent().open(resource, "_blank",false);

			fos.flush(); 
			fos.close(); 
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
}
