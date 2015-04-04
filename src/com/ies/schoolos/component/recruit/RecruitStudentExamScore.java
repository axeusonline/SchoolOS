package com.ies.schoolos.component.recruit;

import java.util.HashMap;

import org.tepi.filtertable.FilterTable;
import org.vaadin.haijian.ExcelExporter;

import com.ies.schoolos.component.ui.ContentPage;
import com.ies.schoolos.container.Container;
import com.ies.schoolos.filter.TableFilterDecorator;
import com.ies.schoolos.filter.TableFilterGenerator;
import com.ies.schoolos.report.excel.RecruitStudentToExcel;
import com.ies.schoolos.schema.BuildingSchema;
import com.ies.schoolos.schema.RecruitStudentSchema;
import com.ies.schoolos.schema.SchoolSchema;
import com.ies.schoolos.schema.SessionSchema;
import com.ies.schoolos.type.ClassRange;
import com.ies.schoolos.type.Gender;
import com.ies.schoolos.type.Prename;
import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.filter.Compare.Equal;
import com.vaadin.data.util.sqlcontainer.RowId;
import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Button;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CustomTable.Align;
import com.vaadin.ui.Notification.Type;

public class RecruitStudentExamScore extends ContentPage {
private static final long serialVersionUID = 1L;
	
	private int total= 0;
	
	private SQLContainer sContainer = Container.getInstance().getRecruitStudentContainer();
	private SQLContainer bContainer = Container.getInstance().getBuildingContainer();
	
	private Item item;
	
	private HashMap<String, Integer> genderQty = new HashMap<String, Integer>();
	private HashMap<String, Integer> classRangeQty = new HashMap<String, Integer>();
	
	private HorizontalLayout toolbar;
	
	private HorizontalLayout scoreLayout;
	private FilterTable  table;
	
	private FieldGroup scoreBinder;
	private FormLayout scoreForm;
	private TextField firstname;
	private TextField lastname;
	private TextField score;
	private Button save;	
	
	public RecruitStudentExamScore() {	
		super("คะแนนสอบ");

		sContainer.refresh();
		
		buildMainLayout();
	}	
	
	private void buildMainLayout(){
		/* Toolbar */
		toolbar = new HorizontalLayout();
		toolbar.setSpacing(true);
		addComponent(toolbar);
		
		ExcelExporter excelExporter = new ExcelExporter(new RecruitStudentToExcel());
		excelExporter.setIcon(FontAwesome.FILE_EXCEL_O);
		excelExporter.setCaption("ส่งออกไฟล์ Excel");
		toolbar.addComponent(excelExporter);
		
		/* Content */
		scoreLayout = new HorizontalLayout();
		scoreLayout.setSpacing(true);
		scoreLayout.setWidth("-1px");
		scoreLayout.setHeight("100%");
		addComponent(scoreLayout);	
		setExpandRatio(scoreLayout, 1);
		
		/* ==== ตารางรายการนักเรียน ==== */
		table = new FilterTable();
		table.setSelectable(true);
		table.setFooterVisible(true);   
		table.setSizeFull();
		table.addValueChangeListener(new ValueChangeListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				if(event.getProperty().getValue() != null){
					item = sContainer.getItem(event.getProperty().getValue());
					initFieldGroup();
					setEditMode();
				}
			}
		});
		
        table.addContainerProperty(RecruitStudentSchema.RECRUIT_CODE, String.class, null);
		table.addContainerProperty(RecruitStudentSchema.CLASS_RANGE, String.class, null);
		table.addContainerProperty(RecruitStudentSchema.PRENAME, String.class, null);
		table.addContainerProperty(RecruitStudentSchema.FIRSTNAME, String.class, null);
		table.addContainerProperty(RecruitStudentSchema.LASTNAME, String.class, null);
		table.addContainerProperty(RecruitStudentSchema.EXAM_BUILDING_ID, String.class, null);
		table.addContainerProperty(RecruitStudentSchema.SCORE, Double.class, null);

		table.setFilterDecorator(new TableFilterDecorator());
		table.setFilterGenerator(new TableFilterGenerator());
        table.setFilterBarVisible(true);
        
		table.setColumnAlignment((Object)RecruitStudentSchema.RECRUIT_CODE,Align.CENTER);
		table.setColumnAlignment(RecruitStudentSchema.CLASS_RANGE,Align.CENTER);
		table.setColumnAlignment(RecruitStudentSchema.PRENAME,Align.CENTER);
		table.setColumnAlignment(RecruitStudentSchema.FIRSTNAME,Align.CENTER);
		table.setColumnAlignment(RecruitStudentSchema.LASTNAME,Align.CENTER);
		table.setColumnAlignment(RecruitStudentSchema.EXAM_BUILDING_ID,Align.CENTER);
		table.setColumnAlignment(RecruitStudentSchema.SCORE,Align.CENTER);

		table.setColumnHeader(RecruitStudentSchema.RECRUIT_CODE, "หมายเลขสมัคร");
		table.setColumnHeader(RecruitStudentSchema.CLASS_RANGE,"ช่วงชั้น");
		table.setColumnHeader(RecruitStudentSchema.PRENAME, "ชื่อต้น");
		table.setColumnHeader(RecruitStudentSchema.FIRSTNAME, "ชื่อ");
		table.setColumnHeader(RecruitStudentSchema.LASTNAME, "สกุล");
		table.setColumnHeader(RecruitStudentSchema.EXAM_BUILDING_ID, "ห้องสอบ");
		table.setColumnHeader(RecruitStudentSchema.SCORE, "คะแนนสอบ");
		table.setVisibleColumns(
				RecruitStudentSchema.RECRUIT_CODE, 
				RecruitStudentSchema.CLASS_RANGE,
				RecruitStudentSchema.PRENAME,
				RecruitStudentSchema.FIRSTNAME, 
				RecruitStudentSchema.LASTNAME,
				RecruitStudentSchema.EXAM_BUILDING_ID,
				RecruitStudentSchema.SCORE);
		
		scoreLayout.addComponent(table);
		scoreLayout.setExpandRatio(table, 1);

		setTableData();
		setFooterData();
		
		/* ==== Form จัดการคะแนนสอบ ==== */
		scoreForm = new FormLayout();
		scoreForm.setWidth("250px");
		scoreForm.setHeight("-1px");
		scoreForm.setSpacing(true);
		scoreForm.setStyleName("border-white");
		scoreLayout.addComponent(scoreForm);
		
		Label formLab = new Label("ข้อมูลนักเรียน");
		scoreForm.addComponent(formLab);
		
		firstname = new TextField();
		firstname.setInputPrompt("ชื่อ");
		firstname.setNullRepresentation("");
		firstname.setImmediate(false);
		firstname.setWidth("-1px");
		firstname.setHeight("-1px");
		scoreForm.addComponent(firstname);

		lastname = new TextField();
		lastname.setInputPrompt("สกุล");
		lastname.setNullRepresentation("");
		lastname.setImmediate(false);
		lastname.setWidth("-1px");
		lastname.setHeight("-1px");
		scoreForm.addComponent(lastname);
		
		score = new TextField();
		score.setInputPrompt("คะแนน");
		score.setNullRepresentation("");
		score.setImmediate(false);
		score.setWidth("-1px");
		score.setHeight("-1px");
		scoreForm.addComponent(score);

		save = new Button("บันทึก", FontAwesome.SAVE);
		save.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event) {
				try {
					scoreBinder.commit();
					sContainer.commit();
					updateTable();
					Notification.show("บันทึกสำเร็จ", Type.HUMANIZED_MESSAGE);
				} catch (Exception e) {
					Notification.show("บันทึกไม่สำเร็จ", Type.WARNING_MESSAGE);
				}
			}
		});
		scoreForm.addComponent(save);
		
		setNormalMode();
	}
	
	/*สร้าง Layout ของข้อมูลเพื่อนำไปใส่ในตาราง*/
	private void setTableData(){
		genderQty = new HashMap<String, Integer>();
		classRangeQty = new HashMap<String, Integer>();
		
		sContainer.addContainerFilter(new Equal(SchoolSchema.SCHOOL_ID,	UI.getCurrent().getSession().getAttribute(SessionSchema.SCHOOL_ID)));;
		total= sContainer.size();
		for(final Object itemId:sContainer.getItemIds()){
			/* ลบ WHERE ออกจาก Query เพื่อป้องกันการค้างของคำสั่่งจากการทำงานอื่นที่เรียกตัวแปรไปใช้ */
			sContainer.removeAllContainerFilters();
			
			final Item studentItem = sContainer.getItem(itemId);			
			addDataItem(studentItem, itemId);
		}
		/* ลบ WHERE ออกจาก Query เพื่อป้องกันการค้างของคำสั่่งจากการทำงานอื่นที่เรียกตัวแปรไปใช้ */
		sContainer.removeAllContainerFilters();
	}
	
	/*นำ Layout มาใส่ในแต่ละแถวของตาราง*/
	private void addDataItem(Item item,Object itemId){
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
				building,
				item.getItemProperty(RecruitStudentSchema.SCORE).getValue()
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
		
		/* รีเซ็ตค่าในฟอร์ม */
		item = null;
		setNormalMode();
		initFieldGroup();
	}
	
	/* จัดกลุ่มของ ฟอร์มในการแก้ไข - เพิ่ม ข้อมูล */
	private void initFieldGroup(){		
		scoreBinder = new FieldGroup(item);
		scoreBinder.setBuffered(true);
		scoreBinder.bind(firstname, RecruitStudentSchema.FIRSTNAME);
		scoreBinder.bind(lastname, RecruitStudentSchema.LASTNAME);
		scoreBinder.bind(score, RecruitStudentSchema.SCORE);
	}	
	
	/* ตั้งค่าโหมดของปกติ คือ ปิดการแก้ไขบนฟอร์ม */
	private void setNormalMode(){
		firstname.setReadOnly(false);
		lastname.setReadOnly(false);
		score.setEnabled(false);
		save.setEnabled(false);
	}

	/* ตั้งค่าโหมดของแก้ไข คือ เปิดการแก้ไขบนฟอร์ม */
	private void setEditMode(){
		firstname.setReadOnly(false);
		lastname.setReadOnly(false);
		score.setEnabled(true);
		save.setEnabled(true);
	}
	
	
}
