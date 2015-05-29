package com.ies.schoolos.component.recruit;

import java.util.HashMap;
import java.util.Map;

import org.tepi.filtertable.FilterTable;
import org.vaadin.dialogs.ConfirmDialog;

import com.ies.schoolos.container.Container;
import com.ies.schoolos.filter.TableFilterDecorator;
import com.ies.schoolos.filter.TableFilterGenerator;
import com.ies.schoolos.report.RecruitStudentReport;
import com.ies.schoolos.report.excel.RecruitStudentToExcel;
import com.ies.schoolos.schema.SessionSchema;
import com.ies.schoolos.schema.recruit.RecruitStudentSchema;
import com.ies.schoolos.type.ClassRange;
import com.ies.schoolos.type.Gender;
import com.ies.schoolos.type.Prename;
import com.ies.schoolos.utility.DateTimeUtil;
import com.vaadin.addon.tableexport.ExcelExport;
import com.vaadin.data.Item;
import com.vaadin.data.Container.ItemSetChangeEvent;
import com.vaadin.data.Container.ItemSetChangeListener;
import com.vaadin.data.util.filter.And;
import com.vaadin.data.util.filter.Compare.Equal;
import com.vaadin.data.util.filter.Compare.Greater;
import com.vaadin.data.util.filter.Compare.Less;
import com.vaadin.data.util.sqlcontainer.RowId;
import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.data.util.sqlcontainer.TemporaryRowId;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CustomTable;
import com.vaadin.ui.CustomTable.ColumnGenerator;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Table;
import com.vaadin.ui.UI;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseListener;

public class RecruitStudentListView extends VerticalLayout{

	private static final long serialVersionUID = 1L;
	
	private SQLContainer sContainer = Container.getRecruitStudentContainer();
	private SQLContainer fContainer = Container.getRecruitFamilyContainer();
	private SQLContainer bContainer = Container.getBuildingContainer();
	
	private HashMap<Object, HashMap<Object, Object>> summarizes = new HashMap<Object, HashMap<Object, Object>>();
	
	
	private HorizontalLayout toolbar;
	private Button add;	
	private FilterTable  table;
	private Label summarize;
	
	public RecruitStudentListView() {			
		sContainer.refresh();
		fContainer.refresh();
		bContainer.refresh();
		
		sContainer.removeAllContainerFilters();
		sContainer.addContainerFilter(new And(new Equal(RecruitStudentSchema.SCHOOL_ID,SessionSchema.getSchoolID()),
				new Greater(RecruitStudentSchema.REGISTER_DATE,DateTimeUtil.getFirstDateOfYear()),
				new Less(RecruitStudentSchema.REGISTER_DATE,DateTimeUtil.getLastDateOfYear())));
		
		setSpacing(true);
		setMargin(true);
		
		buildMainLayout();
		setSummarize();
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
				addLayout.setContent(new AddRecruitStudentView());
				addLayout.addCloseListener(new CloseListener() {
					private static final long serialVersionUID = 1L;

					@Override
					public void windowClose(CloseEvent e) {
						sContainer.addContainerFilter(new And(new Equal(RecruitStudentSchema.SCHOOL_ID,SessionSchema.getSchoolID()),
								new Greater(RecruitStudentSchema.REGISTER_DATE,DateTimeUtil.getFirstDateOfYear()),
								new Less(RecruitStudentSchema.REGISTER_DATE,DateTimeUtil.getLastDateOfYear())));
						setSummarize();
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
				final Table tableEx = new RecruitStudentToExcel();
				tableEx.setVisible(false);
				addComponent(tableEx);
				
				ExcelExport excelExport = new ExcelExport(tableEx,"student");
                excelExport.excludeCollapsedColumns();
                excelExport.setReportTitle("Recruit Student");
				excelExport.setExportFileName("recruit_student.xls");
                excelExport.export();
                
                removeComponent(tableEx);
			}
		});
		toolbar.addComponent(excelExport);		
		
		/* Content */
		HorizontalLayout studentsLayout = new HorizontalLayout();
		studentsLayout.setWidth("100%");
		addComponent(studentsLayout);
        setExpandRatio(studentsLayout, 1);
        
		table = new FilterTable();
		table.setSelectable(true);
		table.setFooterVisible(true);        
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
		studentsLayout.addComponent(table);
		studentsLayout.setExpandRatio(table, 4);
		
		summarize = new Label();
        summarize.setWidth("100%");
        summarize.setContentMode(ContentMode.HTML);
        studentsLayout.addComponent(summarize);
        studentsLayout.setExpandRatio(summarize, 1);
		
	}
	
	/* ตั้งค่ารูปแบบแสดงของตาราง */
	private void initTableStyle(){		
		table.setColumnHeader(RecruitStudentSchema.RECRUIT_CODE, "หมายเลขสมัคร");
		table.setColumnHeader(RecruitStudentSchema.CLASS_RANGE,"ช่วงชั้น");
		table.setColumnHeader(RecruitStudentSchema.PRENAME, "ชื่อต้น");
		table.setColumnHeader(RecruitStudentSchema.FIRSTNAME, "ชื่อ");
		table.setColumnHeader(RecruitStudentSchema.LASTNAME, "สกุล");
		table.setColumnHeader(RecruitStudentSchema.REGISTER_DATE, "วันที่สมัคร");
		table.setColumnHeader(RecruitStudentSchema.EXAM_BUILDING_ID, "ห้องสอบ");
		
		table.setVisibleColumns(
				RecruitStudentSchema.RECRUIT_CODE, 
				RecruitStudentSchema.CLASS_RANGE,
				RecruitStudentSchema.PRENAME,
				RecruitStudentSchema.FIRSTNAME, 
				RecruitStudentSchema.LASTNAME,
				RecruitStudentSchema.REGISTER_DATE);
		
		setColumnGenerator(RecruitStudentSchema.CLASS_RANGE,
				RecruitStudentSchema.PRENAME,
				"");
	}
	
	/* ตั้งค่ารูปแบบข้อมูลของค่า Fix */
	private void setColumnGenerator(Object... propertyIds){
		for(final Object propertyId:propertyIds){
			table.addGeneratedColumn(propertyId, new ColumnGenerator() {
				private static final long serialVersionUID = 1L;

				@Override
				public Object generateCell(CustomTable source, Object itemId, Object columnId) {
					Item item = source.getItem(itemId);
					Object value = null;
					if(item != null && itemId.getClass() != TemporaryRowId.class){
						if(RecruitStudentSchema.CLASS_RANGE.equals(propertyId))
							value = ClassRange.getNameTh(Integer.parseInt(item.getItemProperty(propertyId).getValue().toString()));
						else if(RecruitStudentSchema.PRENAME.equals(propertyId))
							value = Prename.getNameTh(Integer.parseInt(item.getItemProperty(propertyId).getValue().toString()));
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
		buttonLayout.setSpacing(true);
		
		Button	print = new Button("พิมพ์ใบสมัคร",FontAwesome.PRINT);
		print.setWidth("100%");
		print.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event) {
				new RecruitStudentReport(Integer.parseInt(itemId.toString()));
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
				editLayout.setContent(new EditRecruitStudentView(item.getItemProperty(RecruitStudentSchema.STUDENT_ID).getValue()));
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
				ConfirmDialog.show(UI.getCurrent(), "ลบนักเรียน","คุณต้องการลบนักเรียนนี้ใช่หรือไม่?","ตกลง","ยกเลิก",
			        new ConfirmDialog.Listener() {
						private static final long serialVersionUID = 1L;
						public void onClose(ConfirmDialog dialog) {
			                if (dialog.isConfirmed()) {
			                	if(sContainer.removeItem(itemId)){
			                		try {
			                			sContainer.commit();
					                	fContainer.removeItem(new RowId(item.getItemProperty(RecruitStudentSchema.FATHER_ID).getValue()));
					                	fContainer.commit();
					                	fContainer.removeItem(new RowId(item.getItemProperty(RecruitStudentSchema.MOTHER_ID).getValue()));
					                	fContainer.commit();
					                	fContainer.removeItem(new RowId(item.getItemProperty(RecruitStudentSchema.GUARDIAN_ID).getValue()));
					                	fContainer.commit();

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
		table.setColumnFooter(RecruitStudentSchema.RECRUIT_CODE, "ทั้งหมด: "+ table.size() + " คน");
	}
	
	private void setSummarize(){
		
		summarizes.clear();
		
		/*SELECT gender , COUNT(class_range) AS class_range 
		FROM recruit_student 
		WHERE school_id = 9 GROUP BY class_range,gender ORDER BY class_range ASC;*/
		StringBuilder builder = new StringBuilder();
		builder.append(" SELECT " + RecruitStudentSchema.STUDENT_ID + "," + RecruitStudentSchema.GENDER + "," + RecruitStudentSchema.CLASS_RANGE + ", COUNT("+RecruitStudentSchema.CLASS_RANGE+") AS sum");
		builder.append(" FROM " + RecruitStudentSchema.TABLE_NAME);
		builder.append(" WHERE " + RecruitStudentSchema.SCHOOL_ID + "=" + SessionSchema.getSchoolID());
		builder.append(" GROUP BY " +  RecruitStudentSchema.CLASS_RANGE + "," + RecruitStudentSchema.GENDER);
		builder.append(" ORDER BY " +  RecruitStudentSchema.CLASS_RANGE + " ASC");

		SQLContainer freeCon = Container.getFreeFormContainer(builder.toString(), RecruitStudentSchema.STUDENT_ID);
		
		HashMap<Object, Object> genderMap = null;
		StringBuilder sumStr = new StringBuilder();
		int currentClassRange = -1;
		for (Object itemId:freeCon.getItemIds()) {
			Item item = freeCon.getItem(itemId);
			
			int classRange = Integer.parseInt(item.getItemProperty(RecruitStudentSchema.CLASS_RANGE).getValue().toString());
			int gender = Integer.parseInt(item.getItemProperty(RecruitStudentSchema.GENDER).getValue().toString());
			int sum = Integer.parseInt(item.getItemProperty("sum").getValue().toString());
			
			if(currentClassRange < classRange){
				genderMap = new HashMap<Object, Object>();
				currentClassRange = classRange;
				summarizes.put(classRange, genderMap);
			}

			genderMap.put(gender, sum);
			
		}
		
		for ( Map.Entry<Object, HashMap<Object, Object>> entry : summarizes.entrySet()) {
			String genderStr = "";
			int total = 0;
			int classRange =(int) entry.getKey();
		    HashMap<Object, Object> genders = entry.getValue();
		    
		    for(Map.Entry<Object, Object> genderEntry : genders.entrySet()){
		    	int genderKey =(int) genderEntry.getKey();
		    	int genderSum = (int) genderEntry.getValue();
		    	genderStr += ("<b>"+Gender.getNameTh(genderKey)+"</b> " + genderSum)+" คน<br/>";
		    	total += genderSum;
		    }
		    sumStr.append("<b>" + ClassRange.getNameTh(classRange) +"</b> " + total + " คน </br>"+ genderStr + "</br>");
		}
		summarize.setValue(sumStr.toString());
	}
}
