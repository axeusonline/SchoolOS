package com.ies.schoolos.component.studentaffairs;

import org.tepi.filtertable.FilterTable;

import com.ies.schoolos.component.info.StudentView;
import com.ies.schoolos.container.Container;
import com.ies.schoolos.filter.TableFilterDecorator;
import com.ies.schoolos.filter.TableFilterGenerator;
import com.ies.schoolos.schema.SessionSchema;
import com.ies.schoolos.schema.info.StudentSchema;
import com.ies.schoolos.schema.info.StudentStudySchema;
import com.ies.schoolos.type.Prename;
import com.vaadin.data.Item;
import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.CloseListener;
import com.vaadin.ui.Window;

public class StudentBehaviorView extends VerticalLayout {

	private static final long serialVersionUID = 1L;
	
	private SQLContainer freeContainer;
	
	private FilterTable table;
	
	public StudentBehaviorView() {			
		setSizeFull();
		setSpacing(true);
		setMargin(true);
		buildMainLayout();
		fetchData();
		setFooterData();
	}	
	
	private void buildMainLayout(){
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
		builder.append(" AND ( ss." + StudentStudySchema.STUDENT_STATUS + "=" + 0);
		builder.append(" OR ss." + StudentStudySchema.STUDENT_STATUS + "=" + 2 + ")");

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
			
		Button addBehavior = new Button("พฤติกรรม", FontAwesome.EYE_SLASH);
		addBehavior.setId(itemId.toString());
		addBehavior.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 1L;
			@Override
			public void buttonClick(ClickEvent event) {
				Window editLayout = new Window();
				editLayout.setSizeFull();
				UI.getCurrent().addWindow(editLayout);
				
				VerticalLayout studentBehavior = new VerticalLayout();
				studentBehavior.setSizeFull();
				studentBehavior.addComponent(new AddStudentBehaviorView(itemId.toString()));
				editLayout.setContent(studentBehavior);
			}
		});
		buttonLayout.addComponent(addBehavior);
		buttonLayout.setComponentAlignment(addBehavior, Alignment.MIDDLE_CENTER);
		
		Button editButton = new Button(FontAwesome.EDIT);
		editButton.setId(itemId.toString());
		editButton.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 1L;
			@Override
			public void buttonClick(ClickEvent event) {
				
				Window editWindow = new Window();
				editWindow.setSizeFull();
				editWindow.setContent(new StudentView(itemId));
				editWindow.addCloseListener(new CloseListener() {
					private static final long serialVersionUID = 1L;

					@Override
					public void windowClose(CloseEvent e) {
						table.removeAllItems();
						fetchData();
						setFooterData();
					}
				});
				UI.getCurrent().addWindow(editWindow);
			}
		});
		buttonLayout.addComponent(editButton);
		buttonLayout.setComponentAlignment(editButton, Alignment.MIDDLE_CENTER);
		
		
		
		
		return buttonLayout;
	}
	
	/*นำจำนวนที่นับ มาใส่ค่าในส่วนท้ายตาราง*/
	private void setFooterData(){
		table.setColumnFooter(StudentStudySchema.STUDENT_CODE, "ทั้งหมด: "+ table.size() + " คน");
	}
}
