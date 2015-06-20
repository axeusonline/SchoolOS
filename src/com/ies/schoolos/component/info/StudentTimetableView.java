package com.ies.schoolos.component.info;

import java.util.ArrayList;
import java.util.HashMap;

import com.ies.schoolos.container.Container;
import com.ies.schoolos.schema.academic.LessonPlanSubjectSchema;
import com.ies.schoolos.schema.academic.TeachingSchema;
import com.ies.schoolos.schema.academic.TimetableSchema;
import com.ies.schoolos.schema.fundamental.ClassRoomSchema;
import com.ies.schoolos.type.Days;
import com.ies.schoolos.type.Semester;
import com.ies.schoolos.type.dynamic.ClassRoomFromStudentClassRoom;
import com.ies.schoolos.type.dynamic.Teaching;
import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.sqlcontainer.RowId;
import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.JavaScript;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.UI;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class StudentTimetableView extends VerticalLayout {

	private static final long serialVersionUID = 1L;

	private Object studyId;
	/* ที่เก็บข้อมูลตารางสอนทั้งหมด ในแต่ห้องและคาบ มารวมเป็น แถวเดียว แยกตามวัน
	 *   Object แสดงถึง Index ของวัน 
	 *   HashMap<Object, Object[]> 
	 *     > Object แสดงถึง Id ของห้องเรียน
	 *     > Object[] แสดง index ของคาบ โดยภายในเก็บ timetableId */
	private HashMap<Object, HashMap<Object, Object[]>> timetableArray;
	private Teaching teachingAll = new Teaching();
	
	private Container container = new Container();
	private SQLContainer freeFormContainer;
	
	private ComboBox classRoom;
	private ComboBox semester;
	private Button print;
	
	private Table printedTable;
	
	private VerticalLayout timetableLayout;
	
	public StudentTimetableView(Object studyId) {
		this.studyId = studyId;
		setSizeFull();
		setSpacing(true);
		setMargin(true);
		buildMainLayout();
	}
	
	private void buildMainLayout(){
		HorizontalLayout settingLayout = new HorizontalLayout();
		settingLayout.setWidth("100%");
		addComponent(settingLayout);
		
		classRoom = new ComboBox("ห้องเรียน", new ClassRoomFromStudentClassRoom(studyId));
		classRoom.setRequired(true);
		classRoom.setInputPrompt("เลือกข้อมูล");
		classRoom.setItemCaptionPropertyId("name");
		classRoom.setImmediate(true);
		classRoom.setNullSelectionAllowed(false);
		classRoom.setFilteringMode(FilteringMode.CONTAINS);
		classRoom.addValueChangeListener(new ValueChangeListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				/* ตรวจสอบ เงื่อนไขการค้นหาข้อมูล */
				if(semester.getValue() != null && 
						event.getProperty().getValue() != null){
					timetableLayout.removeAllComponents();
					setTimetable();
				}
			}
		});
		settingLayout.addComponent(classRoom);
		
		semester = new ComboBox("ภาคเรียน", new Semester());
		semester.setRequired(true);
		semester.setInputPrompt("เลือกข้อมูล");
		semester.setItemCaptionPropertyId("name");
		semester.setImmediate(true);
		semester.setNullSelectionAllowed(false);
		semester.setFilteringMode(FilteringMode.CONTAINS);
		semester.addValueChangeListener(new ValueChangeListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				/* ตรวจสอบ เงื่อนไขการค้นหาข้อมูล */
				if(classRoom.getValue() != null &&
						event.getProperty().getValue() != null){
					timetableLayout.removeAllComponents();
					setTimetable();
				}
			}
		});
		settingLayout.addComponent(semester);
        
		print = new Button("พิมพ์", FontAwesome.PRINT);
		print.setVisible(false);
		print.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event) {
				Window window = new Window("ตารางสอน ชั้น " + classRoom.getItemCaption(Integer.parseInt(classRoom.getValue().toString())));
				window.setSizeFull();
                window.setContent(printedTable);
				UI.getCurrent().addWindow(window);

				JavaScript.getCurrent().execute(
				            "setTimeout(function() {" +
				            "  print(); self.close();}, 0);");
			}
		});
		settingLayout.addComponent(print);
		
		timetableLayout = new VerticalLayout();
		timetableLayout.setWidth("100%");
		timetableLayout.setHeight("-1px");
		timetableLayout.setSpacing(true);
		addComponent(timetableLayout);
        setExpandRatio(timetableLayout, 2);
              
	}
	
	private void setTableStyle(Table table){
		table.addContainerProperty(TimetableSchema.WORKING_DAY, String.class, null);
		table.addContainerProperty("1", Label.class, null);
		table.addContainerProperty("2", Label.class, null);
		table.addContainerProperty("3", Label.class, null);
		table.addContainerProperty("4", Label.class, null);
		table.addContainerProperty("5", Label.class, null);
		table.addContainerProperty("6", Label.class, null);
		table.addContainerProperty("7", Label.class, null);
		table.addContainerProperty("8", Label.class, null);
		table.addContainerProperty("9", Label.class, null);
		table.addContainerProperty("10", Label.class, null);
		
		table.setColumnAlignment(TimetableSchema.WORKING_DAY,Align.CENTER);
		table.setColumnAlignment("1",Align.CENTER);
		table.setColumnAlignment("2",Align.CENTER);
		table.setColumnAlignment("3",Align.CENTER);
		table.setColumnAlignment("4",Align.CENTER);
		table.setColumnAlignment("5",Align.CENTER);
		table.setColumnAlignment("6",Align.CENTER);
		table.setColumnAlignment("7",Align.CENTER);
		table.setColumnAlignment("8",Align.CENTER);
		table.setColumnAlignment("9",Align.CENTER);
		table.setColumnAlignment("10",Align.CENTER);
		
		table.setColumnHeader(TimetableSchema.WORKING_DAY,"วัน");
		table.setColumnHeader("1","1");
		table.setColumnHeader("2","2");
		table.setColumnHeader("3","3");
		table.setColumnHeader("4","4");
		table.setColumnHeader("5","5");
		table.setColumnHeader("6","6");
		table.setColumnHeader("7","7");
		table.setColumnHeader("8","8");
		table.setColumnHeader("9","9");
		table.setColumnHeader("10","10");
		
		table.setVisibleColumns(
				TimetableSchema.WORKING_DAY,
				"1","2","3","4","5","6","7","8","9","10");
	}
	
	/* เพิ่มข้อมูลตารางสอนทั้มหงด */
	private void setTimetable(){
		seachTimetable();
		
		Table exportedTable = new Table();
		exportedTable.setWidth("100%");
		exportedTable.setHeight("340px");
		setTableStyle(exportedTable);
		timetableLayout.addComponent(exportedTable);
		
		printedTable = new Table();
		printedTable.setSizeFull();
		setTableStyle(printedTable);

		//String[] daysClosed = getDays();
		/* วนลูบเรียงตามวัน อาทิตย์ ถึง เสาร์ */
		for (int i=0; i < 7;i++) {
			final int weekDay = i;
			ArrayList<Object> data = new ArrayList<Object>();
			data.add(Days.getNameTh(weekDay));
			ArrayList<Object> dataPrint = new ArrayList<Object>();
			dataPrint.add(Days.getNameTh(weekDay));
			
			/* ตรวจสอบจำนวนข้อมูลตารางสอนที่พบ
			 *  กรณีพบตารางสอนที่เคยใส่ก่อนหน้า จะตรวจคาบใหนที่กำหนดแล้ว (สีแดง) หรือ ยังไม่กำหนด (เขียว) 
			 *  กรณีไม่พบตารางสอน จะใส่ปุ่มสีเขียวทั้งหมด */
			if(timetableArray.size() > 0){
				if(timetableArray.containsKey(weekDay)){
					HashMap<Object, Object[]> teachingsTmp = timetableArray.get(weekDay);
					/* ตรวจสอบว่าวันดัวกล่าวมีการกำหนดคาบอย่างน้อยหนึ่งคาบหรือไม่
					 *  กรณีมีการกำหนด ก็จะทำการกดหนด ตาบสีแดง ในวันดังกล่าง
					 *  กรณีไม่มีการกำหนดคาบได ๆ เลย จะกำหนด สีเขียวทั้งหมดของห้องดังกล่าว */
					if(teachingsTmp.containsKey(classRoom.getValue())){	

						Object timetableIdArray[] = teachingsTmp.get(classRoom.getValue());
						/* วนลูบจำนวนคาบ 9 คาบ */
						for(int j=0; j < 10; j++){
							String content = "";
							Label label = getTimetableLabel();
							Label printLabel = getTimetableLabel();
							
							/* ตรวจสอบว่า คาบดังกล่่าวถูกระบุใว้หรือยัง
							 *  กรณียังว่าง จะกำหนด Caption เป็น "ว่าง"
							 *  กรณี ระบุ จะกำหนด Caption เป็นชื่อวิชา (อจ) พร้อมตั้งค่า id บนปุ่ม*/
							if(timetableIdArray[j] == null){
								label.setStyleName("green-label");
								printLabel.setStyleName("green-label");
								content = "ว่าง";
							}else{
								Object timetableId = timetableIdArray[j];

								Item timetableItem = freeFormContainer.getItem(new RowId(timetableId));
								String caption = teachingAll.
										getItem(new RowId(timetableItem.getItemProperty(TimetableSchema.TEACHING_ID).getValue())).
										getItemProperty("name").getValue().toString();
								label.setStyleName("red-label");
								content = getTeachingNameHtml(caption);
							}
							label.setValue(content);
							printLabel.setValue(content);
							data.add(label);
							dataPrint.add(printLabel);
						}
					}else{
						for(int j=0; j < 10; j++){
							String content = "ว่าง";
							
							Label label = getTimetableLabel();
							label.setStyleName("green-label");
							label.setValue(content);
							
							Label printLabel = getTimetableLabel();
							printLabel.setStyleName("green-label");
							printLabel.setValue(content);
							
							data.add(label);
							dataPrint.add(printLabel);
						}
					}
				}

				/* เก็บ id เป็น วัน,ห้องเรียน (index ของวัน ,id ห้องเรียน) */
				exportedTable.addItem(data.toArray(),i+","+classRoom.getValue());
				printedTable.addItem(dataPrint.toArray(),i+","+classRoom.getValue());
			}else{
				for(int j=0; j < 10; j++){
					String content = "ว่าง";
					
					Label label = getTimetableLabel();
					label.setStyleName("green-label");
					label.setValue(content);
					
					Label printLabel = getTimetableLabel();
					printLabel.setStyleName("green-label");
					printLabel.setValue(content);
					
					data.add(label);
					dataPrint.add(printLabel);
				}
				/* เก็บ id เป็น วัน,ห้องเรียน (index ของวัน ,id ห้องเรียน) */
				exportedTable.addItem(data.toArray(),i+","+classRoom.getValue());
				printedTable.addItem(dataPrint.toArray(),i+","+classRoom.getValue());
			}
		}
		/*HSSFWorkbook workbook = new HSSFWorkbook(); 
		CellStyle cs = workbook.createCellStyle();
		cs.setWrapText(true);
		cs.setAlignment(CellStyle.ALIGN_CENTER);
		cs.setVerticalAlignment(CellStyle.VERTICAL_CENTER);*/
		
		/* ดึงห้องเรียนทั้งหมดในปีการศึกษา */
		/*for(Object itemId:classRoomLessonPlanContainer.getItemIds()){
			Object classRoomId = classRoomLessonPlanContainer.getItem(itemId).getItemProperty(ClassRoomLessonPlanSchema.CLASS_ROOM_ID).getValue();
			//String roomNumber = classRoomLessonPlanContainer.getItem(itemId).getItemProperty(ClassRoomSchema.NUMBER).getValue().toString();
			//String roomName = classRoomLessonPlanContainer.getItem(itemId).getItemProperty(ClassRoomSchema.NAME).getValue().toString();
			
			
			
			HSSFSheet sheet = workbook.createSheet(roomNumber); 				
			sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 10));
			
			
			 ใส่หัวตาราง 
			HSSFRow headerRow = sheet.createRow(1);
			int column = 0;
			for(Object colHead:exportedTable.getColumnHeaders()){
				HSSFCell cell = headerRow.createCell(column);
				cell.setCellValue(new HSSFRichTextString(colHead.toString()));
				cell.setCellStyle(cs);
				column++;
			}
			
			 ใส่หัวตาราง 
			int rowIndex = 2;
			int totalSec = -5;
			for(Object exportId: exportedTable.getItemIds()){
				Item item = exportedTable.getItem(exportId);
				HSSFRow row = sheet.createRow(rowIndex);
				row.setHeightInPoints((3*sheet.getDefaultRowHeightInPoints()));
				
				column = 0;
				for(Object property:exportedTable.getContainerPropertyIds()){
					String value = item.getItemProperty(property).getValue().toString();
					sheet.autoSizeColumn(column);
					HSSFCell cell = row.createCell(column); 
					cell.setCellValue(value);
					cell.setCellStyle(cs);
					column++;
					if(!value.equals("ว่าง")){
						totalSec++;
					}
				}
				rowIndex++;
			}	
			
			 ใส่หัวข้อตารางบรรทัดบนสุด ซึ่งต้องผ่านการนับจำนวนคาบ 
			HSSFRow titleRow = sheet.createRow(0);
			HSSFCell cell = titleRow.createCell(0);
			cell.setCellValue(new HSSFRichTextString("ตารางสอน ชั้น" + roomName + " จำนวน " + totalSec + " คาบ"));
			cell.setCellStyle(cs);
		}*/
		
		/*try{			
			FileOutputStream fos = null; 
			File file = new File("ตารางสอน.xls"); 
			fos = new FileOutputStream(file); 
			workbook.write(fos); 
			TemporaryFileDownloadResource resource = new TemporaryFileDownloadResource(UI.getCurrent(),
		                "timetable.xls", "application/vnd.ms-excel", file);
			Page.getCurrent().open(resource, "_blank",false);

			fos.flush(); 
			fos.close(); 
		}catch(Exception ex){
			ex.printStackTrace();
		}*/
		
	}
	
	/* ค้นหาข้อมูลตารางสอนทั้งหมด */
	private void seachTimetable(){
		timetableArray = new HashMap<Object, HashMap<Object, Object[]>>();
		freeFormContainer = container.getFreeFormContainer(getAllTimetable(), TimetableSchema.TIMETABLE_ID);
        
		/* นำข้อมูลที่ได้มาใส่ใน Object โดยในฐานข้อมูลจะเก็บ 1 คาบ 1 แถว แต่มาใส่ในตารางจะต้องมารวมทุกคาบมาเป็นแถวเดียวโดยแยกตามวัน */
		for (Object itemId:freeFormContainer.getItemIds()) {
			Item timetableItem = freeFormContainer.getItem(itemId);
			Object timetableId = timetableItem.getItemProperty(TimetableSchema.TIMETABLE_ID).getValue();
			Object classRoomId = timetableItem.getItemProperty(TimetableSchema.CLASS_ROOM_ID).getValue();
			Object workDay = timetableItem.getItemProperty(TimetableSchema.WORKING_DAY).getValue();
			Object section = timetableItem.getItemProperty(TimetableSchema.SECTION).getValue();

			HashMap<Object, Object[]>  classRoomMap = new HashMap<Object, Object[]>();
			
			/* ตรวจสอบว่ามีการกำหนด ตารางสอนใน timetableArray Map ด้วย index วัน
			 *  กรณีมี ก็ให้ไปหา classRooms Map 
			 *  กรณีไม่มี ก็ทำการกำหนดค่า Default ของ Map ทั้งหมด */
			if(timetableArray.containsKey(workDay)){
				classRoomMap = timetableArray.get(workDay);
				/* ตรวจสอบว่ามีการกำหนดใน classRooms Map  หรือยัง
				 *  กรณีมี ก็ให้กำหนดค่า timetableId ของแต่ละค่าบ
				 *  กรณีไม่มี ก็ทำการกำหนดค่าของ teaching Map */
				if(classRoomMap.containsKey(classRoomId)){
					Object timetableIdArray[] = classRoomMap.get(classRoomId);
					timetableIdArray[(int)section] = timetableId;
					classRoomMap.put(classRoomId, timetableIdArray);
				}else{
					Object timetableIdArray[] = new Object[10];
					timetableIdArray[(int)section] = timetableId;
					classRoomMap.put(classRoomId, timetableIdArray);
				}
				timetableArray.put(workDay, classRoomMap);
			}else{
				Object timetableIdArray[] = new Object[10];
				timetableIdArray[(int)section] = timetableId;
				classRoomMap.put(classRoomId, timetableIdArray);
				timetableArray.put(workDay, classRoomMap);
			}
		}
		
		print.setVisible(true);
	}

	/* ดึงข้อมูลตารางสอนทั้งหมดของชั้นปี */
	private String getAllTimetable(){		
		StringBuilder sql = new StringBuilder();	
		sql.append(" SELECT * FROM " + TimetableSchema.TABLE_NAME +" tt");
		sql.append(" INNER JOIN " + ClassRoomSchema.TABLE_NAME + " cr ON cr." + ClassRoomSchema.CLASS_ROOM_ID + "=" + "tt." + TimetableSchema.CLASS_ROOM_ID);
		sql.append(" INNER JOIN " + TeachingSchema.TABLE_NAME + " tc ON tc." + TeachingSchema.TEACHING_ID + "=" + "tt." + TimetableSchema.TEACHING_ID);
		sql.append(" INNER JOIN " + LessonPlanSubjectSchema.TABLE_NAME + " lps ON lps." + LessonPlanSubjectSchema.SUBJECT_ID + "=" + "tc." + TeachingSchema.SUBJECT_ID);
		sql.append(" WHERE tt." + TimetableSchema.CLASS_ROOM_ID + "=" + classRoom.getValue());
		sql.append(" AND lps." + LessonPlanSubjectSchema.SEMESTER + "=" + semester.getValue());
		
		return sql.toString();
	}
	
	/* ตัดคำ จากชื่อของผู้สอน ซึ่งอยู่ในรูปของ 
	 *  ท1101:ภาษาไทย1 (อ.ทดลอง ทดสอบ) หรือ
	 *  แนะแนว (อ.ทดลอง ทดสอบ) 
	 *  ให้อยู่ในรํปของ 
	 *    - ถ้ามีรหัสวิชา ท1101 <br/> อ.ทดลอง ทดสอบ
	 *    - ถ้าไม่มีรหัสวิชา แนะแนว อ.ทดลอง ทดสอบ */
	private String getTeachingNameHtml(String name){
		String styles = name.substring(0, name.indexOf("("))+"<br/>" +
				name.substring(name.indexOf("(")+1, name.lastIndexOf(")"));

		return styles;
	}
	
	private Label getTimetableLabel(){
		Label label = new Label();
		label.setWidth("90px");
		label.setHeight("100%");
		label.setContentMode(ContentMode.HTML);
		return label;
	}
}
