package com.ies.schoolos.component.recruit;

import org.tepi.filtertable.FilterTable;
import org.vaadin.dialogs.ConfirmDialog;

import com.ies.schoolos.component.ui.ContentPage;
import com.ies.schoolos.container.Container;
import com.ies.schoolos.filter.TableFilterDecorator;
import com.ies.schoolos.filter.TableFilterGenerator;
import com.ies.schoolos.report.RecruitStudentReport;
import com.ies.schoolos.schema.SessionSchema;
import com.ies.schoolos.schema.recruit.RecruitStudentSchema;
import com.ies.schoolos.type.ClassRange;
import com.ies.schoolos.type.Prename;
import com.vaadin.data.Item;
import com.vaadin.data.util.filter.Compare.Equal;
import com.vaadin.data.util.sqlcontainer.RowId;
import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.data.util.sqlcontainer.TemporaryRowId;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CustomTable;
import com.vaadin.ui.CustomTable.ColumnGenerator;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.UI;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseListener;

public class RecruitStudentListView  extends ContentPage{

	private static final long serialVersionUID = 1L;
	
	private SQLContainer sContainer = Container.getInstance().getRecruitStudentContainer();
	private SQLContainer fContainer = Container.getInstance().getRecruitFamilyContainer();
	private SQLContainer bContainer = Container.getInstance().getBuildingContainer();
	
	private HorizontalLayout toolbar;
	private Button add;	
	private FilterTable  table;
	
	public RecruitStudentListView() {	
		super("รายชื่อผู้สมัครเรียน");
		
		sContainer.refresh();
		fContainer.refresh();
		bContainer.refresh();
		
		sContainer.removeAllContainerFilters();
		sContainer.addContainerFilter(new Equal(RecruitStudentSchema.SCHOOL_ID,UI.getCurrent().getSession().getAttribute(SessionSchema.SCHOOL_ID)));
		
		buildMainLayout();
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
						sContainer.addContainerFilter(new Equal(RecruitStudentSchema.SCHOOL_ID,UI.getCurrent().getSession().getAttribute(SessionSchema.SCHOOL_ID)));
						setFooterData();
					}
				});
				UI.getCurrent().addWindow(addLayout);
			}
		});
		toolbar.addComponent(add);
		
		/*ExcelExporter excelExporter = new ExcelExporter(new RecruitStudentToExcel());
		excelExporter.setIcon(FontAwesome.FILE_EXCEL_O);
		excelExporter.setCaption("ส่งออกไฟล์ Excel");
		toolbar.addComponent(excelExporter);*/
		
		/* Content */
		table = new FilterTable();
		table.setSizeFull();
		table.setSelectable(true);
		table.setFooterVisible(true);        
		
		table.setFilterDecorator(new TableFilterDecorator());
		table.setFilterGenerator(new TableFilterGenerator());
        table.setFilterBarVisible(true);

		table.setContainerDataSource(sContainer);
	    setFooterData();
		initTableStyle();

		table.setColumnReorderingAllowed(true);
		table.setColumnCollapsingAllowed(true);

		addComponent(table);
        setExpandRatio(table, 1);
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
										Notification.show("บันทึกไม่สำเร็จ กรุณาลองอีกครั้ง" , Type.WARNING_MESSAGE);
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
}
