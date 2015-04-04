package com.ies.schoolos.component.recruit;

import java.util.Date;
import java.util.HashMap;

import org.tepi.filtertable.FilterTable;
import org.vaadin.dialogs.ConfirmDialog;
import org.vaadin.haijian.ExcelExporter;

import com.ies.schoolos.component.ui.ContentPage;
import com.ies.schoolos.container.Container;
import com.ies.schoolos.filter.TableFilterDecorator;
import com.ies.schoolos.filter.TableFilterGenerator;
import com.ies.schoolos.report.RecruitStudentReport;
import com.ies.schoolos.report.excel.RecruitStudentToExcel;
import com.ies.schoolos.schema.BuildingSchema;
import com.ies.schoolos.schema.RecruitStudentSchema;
import com.ies.schoolos.schema.SchoolSchema;
import com.ies.schoolos.schema.SessionSchema;
import com.ies.schoolos.type.ClassRange;
import com.ies.schoolos.type.Gender;
import com.ies.schoolos.type.Prename;
import com.vaadin.data.Item;
import com.vaadin.data.util.filter.Compare.Equal;
import com.vaadin.data.util.sqlcontainer.RowId;
import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomTable.Align;
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
	
	private int total= 0;
	
	private SQLContainer sContainer = Container.getInstance().getRecruitStudentContainer();
	private SQLContainer fContainer = Container.getInstance().getRecruitFamilyContainer();
	private SQLContainer bContainer = Container.getInstance().getBuildingContainer();
	
	private HashMap<String, Integer> genderQty = new HashMap<String, Integer>();
	private HashMap<String, Integer> classRangeQty = new HashMap<String, Integer>();
	
	private HorizontalLayout toolbar;
	private Button add;	
	private FilterTable  table;
	
	public RecruitStudentListView() {	
		super("รายชื่อผู้สมัครเรียน");

		sContainer.refresh();
		fContainer.refresh();
		bContainer.refresh();
		
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
						updateTable();
					}
				});
				UI.getCurrent().addWindow(addLayout);
			}
		});
		toolbar.addComponent(add);
		
		ExcelExporter excelExporter = new ExcelExporter(new RecruitStudentToExcel());
		excelExporter.setIcon(FontAwesome.FILE_EXCEL_O);
		excelExporter.setCaption("ส่งออกไฟล์ Excel");
		toolbar.addComponent(excelExporter);
		
		/* Content */
		table = new FilterTable();
		table.setSizeFull();
		table.setSelectable(true);
		table.setFooterVisible(true);        
        
        table.addContainerProperty(RecruitStudentSchema.RECRUIT_CODE, String.class, null);
		table.addContainerProperty(RecruitStudentSchema.CLASS_RANGE, String.class, null);
		table.addContainerProperty(RecruitStudentSchema.PRENAME, String.class, null);
		table.addContainerProperty(RecruitStudentSchema.FIRSTNAME, String.class, null);
		table.addContainerProperty(RecruitStudentSchema.LASTNAME, String.class, null);
		table.addContainerProperty(RecruitStudentSchema.REGISTER_DATE, Date.class, null);
		table.addContainerProperty(RecruitStudentSchema.EXAM_BUILDING_ID, String.class, null);
		table.addContainerProperty("note", HorizontalLayout.class, null);

		table.setFilterDecorator(new TableFilterDecorator());
		table.setFilterGenerator(new TableFilterGenerator());
        table.setFilterBarVisible(true);
        
		table.setColumnAlignment((Object)RecruitStudentSchema.RECRUIT_CODE,Align.CENTER);
		table.setColumnAlignment(RecruitStudentSchema.CLASS_RANGE,Align.CENTER);
		table.setColumnAlignment(RecruitStudentSchema.PRENAME,Align.CENTER);
		table.setColumnAlignment(RecruitStudentSchema.FIRSTNAME,Align.CENTER);
		table.setColumnAlignment(RecruitStudentSchema.LASTNAME,Align.CENTER);
		table.setColumnAlignment(RecruitStudentSchema.REGISTER_DATE,Align.CENTER);
		table.setColumnAlignment(RecruitStudentSchema.EXAM_BUILDING_ID,Align.CENTER);
		table.setColumnAlignment("note",Align.CENTER);

		table.setColumnHeader(RecruitStudentSchema.RECRUIT_CODE, "หมายเลขสมัคร");
		table.setColumnHeader(RecruitStudentSchema.CLASS_RANGE,"ช่วงชั้น");
		table.setColumnHeader(RecruitStudentSchema.PRENAME, "ชื่อต้น");
		table.setColumnHeader(RecruitStudentSchema.FIRSTNAME, "ชื่อ");
		table.setColumnHeader(RecruitStudentSchema.LASTNAME, "สกุล");
		table.setColumnHeader(RecruitStudentSchema.REGISTER_DATE, "วันที่สมัคร");
		table.setColumnHeader(RecruitStudentSchema.EXAM_BUILDING_ID, "ห้องสอบ");
		table.setColumnHeader("note", "");
		table.setVisibleColumns(
				RecruitStudentSchema.RECRUIT_CODE, 
				RecruitStudentSchema.CLASS_RANGE,
				RecruitStudentSchema.PRENAME,
				RecruitStudentSchema.FIRSTNAME, 
				RecruitStudentSchema.LASTNAME,
				RecruitStudentSchema.REGISTER_DATE,
				RecruitStudentSchema.EXAM_BUILDING_ID,
				"note");
		
		addComponent(table);
        setExpandRatio(table, 1);

		setTableData();
		setFooterData();
	}
	
	/*สร้าง Layout ของข้อมูลเพื่อนำไปใส่ในตาราง*/
	private void setTableData(){
		genderQty = new HashMap<String, Integer>();
		classRangeQty = new HashMap<String, Integer>();
		
		sContainer.addContainerFilter(new Equal(SchoolSchema.SCHOOL_ID,	UI.getCurrent().getSession().getAttribute(SessionSchema.SCHOOL_ID)));
		total= sContainer.size();
		for(final Object itemId:sContainer.getItemIds()){
			/* ลบ WHERE ออกจาก Query เพื่อป้องกันการค้างของคำสั่่งจากการทำงานอื่นที่เรียกตัวแปรไปใช้ */
			sContainer.removeAllContainerFilters();
			
			final Item studentItem = sContainer.getItem(itemId);
			
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
					editLayout.setContent(new EditRecruitStudentView(studentItem.getItemProperty(RecruitStudentSchema.STUDENT_ID).getValue()));
					editLayout.addCloseListener(new CloseListener() {
						private static final long serialVersionUID = 1L;

						@Override
						public void windowClose(CloseEvent e) {
							try {
								updateTable();	
							}catch (Exception e1) {
								Notification.show("บันทึกไม่สำเร็จ กรุณาลองอีกครั้ง" , Type.WARNING_MESSAGE);
								e1.printStackTrace();
							}
						}
					});
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
				                	table.removeItem(itemId);
				                	table.commit();
				                	if(sContainer.removeItem(itemId)){
				                		try {
				                			sContainer.commit();
						                	fContainer.removeItem(new RowId(studentItem.getItemProperty(RecruitStudentSchema.FATHER_ID).getValue()));
						                	fContainer.commit();
						                	fContainer.removeItem(new RowId(studentItem.getItemProperty(RecruitStudentSchema.MOTHER_ID).getValue()));
						                	fContainer.commit();
						                	fContainer.removeItem(new RowId(studentItem.getItemProperty(RecruitStudentSchema.GUARDIAN_ID).getValue()));
						                	fContainer.commit();
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
			addDataItem(studentItem, itemId, buttonLayout);
		}
		/* ลบ WHERE ออกจาก Query เพื่อป้องกันการค้างของคำสั่่งจากการทำงานอื่นที่เรียกตัวแปรไปใช้ */
		sContainer.removeAllContainerFilters();
	}
	
	/*นำ Layout มาใส่ในแต่ละแถวของตาราง*/
	private void addDataItem(Item item,Object itemId, Component component){
		String building = "ยังไม่ระบุห้องสอบ";
		/* ค้นหาห้องสอบ */
		if(item.getItemProperty(RecruitStudentSchema.EXAM_BUILDING_ID).getValue() != null){
			Item bItem = bContainer.getItem(new RowId(item.getItemProperty(RecruitStudentSchema.EXAM_BUILDING_ID).getValue()));
			
			Object value = bItem.getItemProperty(BuildingSchema.NAME).getValue() + 
					" (" + bItem.getItemProperty(BuildingSchema.ROOM_NUMBER).getValue() + ")";
			
			building = value.toString();
		}
		
		table.addItem(new Object[] {
				item.getItemProperty(RecruitStudentSchema.RECRUIT_CODE).getValue(), 
				ClassRange.getNameTh(Integer.parseInt(item.getItemProperty(RecruitStudentSchema.CLASS_RANGE).getValue().toString())),
				Prename.getNameTh(Integer.parseInt(item.getItemProperty(RecruitStudentSchema.PRENAME).getValue().toString())), 
				item.getItemProperty(RecruitStudentSchema.FIRSTNAME).getValue(), 
				item.getItemProperty(RecruitStudentSchema.LASTNAME).getValue(),
				item.getItemProperty(RecruitStudentSchema.REGISTER_DATE).getValue(),
				building,
				component
		},itemId);
		/* ใส่ข้อมูลเสร็จก็มานับ*/
		countItemFooter(item);
	}
	
	/*นับจำนวนของข้อมูล เพื่อแยกประเภท*/
	private void countItemFooter(Item item){		
		/*นับจำนวนแยกตามเพศ
		 * กรณียังไม่พบข้อมูล ก็ให้ใส่ค่าเริ่มต้น
		 * หากพบข้อมูลก็ทำการบวก*/
		if(genderQty.get(item.getItemProperty(RecruitStudentSchema.GENDER).getValue().toString()) == null){
			genderQty.put(item.getItemProperty(RecruitStudentSchema.GENDER).getValue().toString(), 1);
		}else{
			int gender = genderQty.get(item.getItemProperty(RecruitStudentSchema.GENDER).getValue().toString()) +1;
			genderQty.put(item.getItemProperty(RecruitStudentSchema.GENDER).getValue().toString(), gender);
		}
		/*นับจำนวนแยกตามช่วงชั้น
		 * กรณียังไม่พบข้อมูล ก็ให้ใส่ค่าเริ่มต้น
		 * หากพบข้อมูลก็ทำการบวก*/
		if(classRangeQty.get(item.getItemProperty(RecruitStudentSchema.CLASS_RANGE).getValue().toString()) == null){
			classRangeQty.put(item.getItemProperty(RecruitStudentSchema.CLASS_RANGE).getValue().toString(), 1);
		}else{
			int classRange = classRangeQty.get(item.getItemProperty(RecruitStudentSchema.CLASS_RANGE).getValue().toString()) +1;
			classRangeQty.put(item.getItemProperty(RecruitStudentSchema.CLASS_RANGE).getValue().toString(), classRange);
		}
	}
	
	/*นำจำนวนที่นับ มาใส่ค่าในส่วนท้ายตาราง*/
	private void setFooterData(){
		if(total > 0){
			table.setColumnFooter(RecruitStudentSchema.RECRUIT_CODE, "ทั้งหมด: "+ total + " คน");
			
			/* สร้าง String ในการแสดงสรุปท้ายตารางในส่วนของ เพศ */
			String genderStr = "";
			for(int i = 0; i < new Gender().size(); i++){
				if(genderQty.get(Integer.toString(i)) != null)
					genderStr += Gender.getNameTh(i) + ": " + genderQty.get(Integer.toString(i)) +" คน <br/>";
			}
			/* สร้าง String ในการแสดงสรุปท้ายตารางในส่วนของ ช่วงชั้น */
			String classRangeStr = "";
			for(int i = 0; i < new ClassRange().size(); i++){
				if(classRangeQty.get(Integer.toString(i)) != null)
					classRangeStr += ClassRange.getNameTh(i) + ": " + classRangeQty.get(Integer.toString(i)) +" คน <br/>";
			}
			table.setColumnFooter(RecruitStudentSchema.PRENAME, genderStr);
			table.setColumnFooter(RecruitStudentSchema.CLASS_RANGE, classRangeStr);
		}
	}
	
	/* อัพเดทข้อมูลในตาราง */
	private void updateTable(){
		sContainer.refresh();
		table.removeAllItems();
		setTableData();
		setFooterData();
	}
}
