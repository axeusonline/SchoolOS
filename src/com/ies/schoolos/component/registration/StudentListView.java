package com.ies.schoolos.component.registration;

import org.tepi.filtertable.FilterTable;
import org.vaadin.dialogs.ConfirmDialog;

import com.ies.schoolos.container.Container;
import com.ies.schoolos.filter.TableFilterDecorator;
import com.ies.schoolos.filter.TableFilterGenerator;
import com.ies.schoolos.schema.SessionSchema;
import com.ies.schoolos.schema.info.StudentSchema;
import com.ies.schoolos.schema.info.StudentStudySchema;
import com.ies.schoolos.type.Prename;
import com.ies.schoolos.utility.Notification;
import com.vaadin.data.Item;
import com.vaadin.data.util.sqlcontainer.RowId;
import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.UI;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseListener;

public class StudentListView extends VerticalLayout {

	private static final long serialVersionUID = 1L;
	
	private SQLContainer freeContainer;
	
	private HorizontalLayout toolbar;
	private Button add;	
	private FilterTable table;
	
	public StudentListView() {			
		setSizeFull();
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
		addComponent(toolbar);
				
		add = new Button("เพิ่ม", FontAwesome.USER);
		add.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event) {
				Window addLayout = new Window();
				addLayout.setSizeFull();
				addLayout.setContent(new AddStudentView());
				addLayout.addCloseListener(new CloseListener() {
					private static final long serialVersionUID = 1L;

					@Override
					public void windowClose(CloseEvent e) {
						table.removeAllItems();
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
			/*	final Table tableEx = new RecruitStudentToExcel();
				tableEx.setVisible(false);
				addComponent(tableEx);
				
				ExcelExport excelExport = new ExcelExport(tableEx,"student");
                excelExport.excludeCollapsedColumns();
                excelExport.setReportTitle("Student");
				excelExport.setExportFileName("student.xls");
                excelExport.export();
                
                removeComponent(tableEx);*/
			}
		});
		//toolbar.addComponent(excelExport);	
		
		/* Content */
		table = new FilterTable();
		table.setSizeFull();
		table.setSelectable(true);
		table.setFooterVisible(true);        
		
        table.addContainerProperty(StudentStudySchema.STUDENT_CODE, String.class, null);
		table.addContainerProperty(StudentSchema.PRENAME, String.class, null);
		table.addContainerProperty(StudentSchema.FIRSTNAME, String.class, null);
		table.addContainerProperty(StudentSchema.LASTNAME, String.class, null);
		table.addContainerProperty("", HorizontalLayout.class, null);
		
		table.setFilterDecorator(new TableFilterDecorator());
		table.setFilterGenerator(new TableFilterGenerator());
        table.setFilterBarVisible(true);
	
		initTableStyle();
		
		table.sort(new Object[]{StudentStudySchema.STUDENT_CODE}, new boolean[]{true});

		table.setColumnReorderingAllowed(true);
		table.setColumnCollapsingAllowed(true);

		addComponent(table);
        setExpandRatio(table, 1);
	}
	
	/* ตั้งค่ารูปแบบแสดงของตาราง */
	private void initTableStyle(){		
		table.setColumnHeader(StudentStudySchema.STUDENT_CODE, "หมายเลขประจำตัว");
		table.setColumnHeader(StudentSchema.PRENAME, "ชื่อต้น");
		table.setColumnHeader(StudentSchema.FIRSTNAME, "ชื่อ");
		table.setColumnHeader(StudentSchema.LASTNAME, "สกุล");
		table.setColumnHeader("", "");
		
		table.setVisibleColumns(
				StudentStudySchema.STUDENT_CODE, 
				StudentSchema.PRENAME,
				StudentSchema.FIRSTNAME, 
				StudentSchema.LASTNAME,
				"");
	}
	
	private void fetchData(){		
		StringBuilder builder = new StringBuilder();
		builder.append(" SELECT * FROM " + StudentStudySchema.TABLE_NAME + " ss");
		builder.append(" INNER JOIN " + StudentSchema.TABLE_NAME + " s ON s." + StudentSchema.STUDENT_ID + "= ss." + StudentStudySchema.STUDENT_ID);
		builder.append(" WHERE ss." + StudentSchema.SCHOOL_ID + "=" + SessionSchema.getSchoolID());
		System.err.println(builder.toString());
		freeContainer = Container.getFreeFormContainer(builder.toString(), StudentStudySchema.STUDENT_STUDY_ID);
		for(final Object itemId:freeContainer.getItemIds()){
			Item item = freeContainer.getItem(itemId);
			table.addItem(new Object[]{
				item.getItemProperty(StudentStudySchema.STUDENT_CODE).getValue(),
				Prename.getNameTh((int)item.getItemProperty(StudentSchema.PRENAME).getValue()),
				item.getItemProperty(StudentSchema.FIRSTNAME).getValue(),
				item.getItemProperty(StudentSchema.LASTNAME).getValue(),
				initButtonLayout(item, itemId)
			}, itemId);
		}
	}
	
	private HorizontalLayout initButtonLayout(final Item item, final Object itemId){
		final HorizontalLayout buttonLayout = new HorizontalLayout();
			
		Button editButton = new Button(FontAwesome.EDIT);
		editButton.setId(itemId.toString());
		editButton.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 1L;
			@Override
			public void buttonClick(ClickEvent event) {
				
				Window editLayout = new Window();
				editLayout.setSizeFull();
				editLayout.setContent(new EditStudentView(itemId));
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
			                	SQLContainer studyContainer = Container.getStudentStudyContainer();
			                	SQLContainer ssontainer = Container.getStudentContainer();
			                	System.err.println(itemId.toString() + "," + itemId.getClass());
			                	Object studentId = studyContainer.getItem(itemId).getItemProperty(StudentStudySchema.STUDENT_ID).getValue();

			                	System.err.println(studentId.toString() + "," + studentId.getClass());
			                	if(studyContainer.removeItem(itemId)){
			                		try {
			                			studyContainer.commit();
			                			if(ssontainer.removeItem(new RowId(studentId))){
			                				ssontainer.commit();
			                				table.removeAllItems();
				                			fetchData();
				                			setFooterData();
			                			}
			                			
									}catch (Exception e1) {
										Notification.show("บันทึกไม่สำเร็จ กรุณาลองอีกครั้ง" , Type.WARNING_MESSAGE);
										e1.printStackTrace();
									}
			                	}else{
			                		System.err.println("ไม่สำเร็จ");
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
		table.setColumnFooter(StudentStudySchema.STUDENT_CODE, "ทั้งหมด: "+ table.size() + " คน");
	}
}
