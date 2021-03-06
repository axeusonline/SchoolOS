package com.ies.schoolos.component.academic;

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
import org.apache.poi.ss.util.CellRangeAddress;

import com.ies.schoolos.container.Container;
import com.ies.schoolos.schema.SessionSchema;
import com.ies.schoolos.schema.academic.ClassRoomLessonPlanSchema;
import com.ies.schoolos.schema.academic.LessonPlanSubjectSchema;
import com.ies.schoolos.schema.academic.TeachingSchema;
import com.ies.schoolos.schema.academic.TimetableSchema;
import com.ies.schoolos.schema.fundamental.ClassRoomSchema;
import com.ies.schoolos.type.ClassYear;
import com.ies.schoolos.type.Days;
import com.ies.schoolos.type.Semester;
import com.ies.schoolos.type.dynamic.ClassRoom;
import com.ies.schoolos.type.dynamic.Teaching;
import com.ies.schoolos.utility.DateTimeUtil;
import com.vaadin.addon.tableexport.TemporaryFileDownloadResource;
import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.sqlcontainer.RowId;
import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

public class TimetableExportView extends VerticalLayout {

	private static final long serialVersionUID = 1L;

	/* ที่เก็บข้อมูลตารางสอนทั้งหมด ในแต่ห้องและคาบ มารวมเป็น แถวเดียว แยกตามวัน
	 *   Object แสดงถึง Index ของวัน 
	 *   HashMap<Object, Object[]> 
	 *     > Object แสดงถึง Id ของห้องเรียน
	 *     > Object[] แสดง index ของคาบ โดยภายในเก็บ timetableId */
	private HashMap<Object, HashMap<Object, Object[]>> timetableArray;

	private Container container = new Container();
	private ClassRoom classRoom = new ClassRoom();
	private SQLContainer freeFormContainer;
	
	private Teaching teachingAll = new Teaching();
	
	private FormLayout settingForm;
	private ComboBox classYear;
	private ComboBox semester;
	
	private VerticalLayout timetableLayout;
	
	public TimetableExportView() {
		setSpacing(true);
		setMargin(true);
		buildMainLayout();
	}
	
	private void buildMainLayout(){
		settingForm = new FormLayout();
		settingForm.setStyleName("border-white");
		addComponent(settingForm);
		
		Label lblSetting = new Label(FontAwesome.GEAR.getHtml() + " ตั้งค่า");
		lblSetting.setContentMode(ContentMode.HTML);
		settingForm.addComponent(lblSetting);
		
		classYear = new ComboBox("ชั้นปี", new ClassYear());
		classYear.setRequired(true);
		classYear.setInputPrompt("เลือกข้อมูล");
		classYear.setItemCaptionPropertyId("name");
		classYear.setImmediate(true);
		classYear.setNullSelectionAllowed(false);
		classYear.setFilteringMode(FilteringMode.CONTAINS);
		classYear.addValueChangeListener(new ValueChangeListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				/* ตรวจสอบ เงื่อนไขการค้นหาข้อมูล */
				if(semester.getValue() != null && 
						event.getProperty().getValue() != null){
					timetableLayout.removeAllComponents();
					setAllTimetable();
				}
			}
		});
		settingForm.addComponent(classYear);
		
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
				if(classYear.getValue() != null &&
						event.getProperty().getValue() != null){
					timetableLayout.removeAllComponents();
					setAllTimetable();
				}
			}
		});
		settingForm.addComponent(semester);
        
		timetableLayout = new VerticalLayout();
		timetableLayout.setWidth("100%");
		timetableLayout.setHeight("-1px");
		timetableLayout.setSpacing(true);
		addComponent(timetableLayout);
        setExpandRatio(timetableLayout, 2);
              
	}
	
	private void setTableStyle(Table table){
		table.addContainerProperty(TimetableSchema.WORKING_DAY, String.class, null);
		table.addContainerProperty(TimetableSchema.CLASS_ROOM_ID, String.class, null);
		table.addContainerProperty("1", String.class, null);
		table.addContainerProperty("2", String.class, null);
		table.addContainerProperty("3", String.class, null);
		table.addContainerProperty("4", String.class, null);
		table.addContainerProperty("5", String.class, null);
		table.addContainerProperty("6", String.class, null);
		table.addContainerProperty("7", String.class, null);
		table.addContainerProperty("8", String.class, null);
		table.addContainerProperty("9", String.class, null);
		table.addContainerProperty("10", String.class, null);
		
		table.setColumnAlignment(TimetableSchema.WORKING_DAY,Align.CENTER);
		table.setColumnAlignment(TimetableSchema.CLASS_ROOM_ID,Align.CENTER);
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
		table.setColumnHeader(TimetableSchema.CLASS_ROOM_ID,"ชั้นเรียน");
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
				TimetableSchema.CLASS_ROOM_ID,
				"1","2","3","4","5","6","7","8","9","10");
	}
	
	
	/* เพิ่มข้อมูลตารางสอนทั้มหงด */
	@SuppressWarnings("deprecation")
	private void setAllTimetable(){
		seachAllTimetable();
		
		HSSFWorkbook workbook = new HSSFWorkbook(); 
		CellStyle cs = workbook.createCellStyle();
		cs.setWrapText(true);
		cs.setAlignment(CellStyle.ALIGN_CENTER);
		cs.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
		
		SQLContainer classRoomLessonPlanContainer = searchClassRoomLessonPlan();
		
		/* ดึงห้องเรียนทั้งหมดในปีการศึกษา */
		for(Object itemId:classRoomLessonPlanContainer.getItemIds()){
			Object classRoomId = classRoomLessonPlanContainer.getItem(itemId).getItemProperty(ClassRoomLessonPlanSchema.CLASS_ROOM_ID).getValue();
			String roomNumber = classRoomLessonPlanContainer.getItem(itemId).getItemProperty(ClassRoomSchema.NUMBER).getValue().toString();
			String roomName = classRoomLessonPlanContainer.getItem(itemId).getItemProperty(ClassRoomSchema.NAME).getValue().toString();
			
			Table exportTable = new Table();
			exportTable.setWidth("100%");
			exportTable.setHeight("340px");
			setTableStyle(exportTable);
			timetableLayout.addComponent(exportTable);
			
			//String[] daysClosed = getDays();
			/* วนลูบเรียงตามวัน อาทิตย์ ถึง เสาร์ */
			for (int i=0; i < 7;i++) {
				final int weekDay = i;
				ArrayList<Object> data = new ArrayList<Object>();	
				data.add(Days.getNameTh(weekDay));
				data.add(classRoom.getItem(classRoomId).getItemProperty("name").getValue());
				
				/* ตรวจสอบจำนวนข้อมูลตารางสอนที่พบ
				 *  กรณีพบตารางสอนที่เคยใส่ก่อนหน้า จะตรวจคาบใหนที่กำหนดแล้ว (สีแดง) หรือ ยังไม่กำหนด (เขียว) 
				 *  กรณีไม่พบตารางสอน จะใส่ปุ่มสีเขียวทั้งหมด */
				if(timetableArray.size() > 0){
					if(timetableArray.containsKey(weekDay)){
						HashMap<Object, Object[]> teachingsTmp = timetableArray.get(weekDay);
						/* ตรวจสอบว่าวันดัวกล่าวมีการกำหนดคาบอย่างน้อยหนึ่งคาบหรือไม่
						 *  กรณีมีการกำหนด ก็จะทำการกดหนด ตาบสีแดง ในวันดังกล่าง
						 *  กรณีไม่มีการกำหนดคาบได ๆ เลย จะกำหนด สีเขียวทั้งหมดของห้องดังกล่าว */
						if(teachingsTmp.containsKey(classRoomId)){	

							Object timetableIdArray[] = teachingsTmp.get(classRoomId);
							/* วนลูบจำนวนคาบ 9 คาบ */
							for(int j=0; j < 10; j++){								
								String content = "";
								/* ตรวจสอบว่า คาบดังกล่่าวถูกระบุใว้หรือยัง
								 *  กรณียังว่าง จะกำหนด Caption เป็น "ว่าง"
								 *  กรณี ระบุ จะกำหนด Caption เป็นชื่อวิชา (อจ) พร้อมตั้งค่า id บนปุ่ม*/
                                if(timetableIdArray[j] == null)
                                {
                                	content = "ว่าง";
                                } else
                                {
                                    Object timetableId = timetableIdArray[j];
                                    Item timetableItem = freeFormContainer.getItem(new RowId(new Object[] {
                                        timetableId
                                    }));
                                    if(timetableId.toString().contains(","))
                                    {
                                        String timetableIds[] = timetableId.toString().split(",");
                                        timetableItem = freeFormContainer.getItem(new RowId(new Object[] {
                                            Integer.valueOf(Integer.parseInt(timetableIds[0]))
                                        }));
                                        String captionOriginal = (new StringBuilder(String.valueOf(teachingAll.getItem(new RowId(new Object[] {
                                            timetableItem.getItemProperty(TimetableSchema.TEACHING_ID).getValue()
                                        })).getItemProperty("name").getValue().toString()))).append(",\n").toString();
                                        String caption = captionOriginal;
                                        int round = 0;
                                        String as1[];
                                        int j1 = (as1 = timetableIds).length;
                                        for(int i1 = 0; i1 < j1; i1++)
                                        {
                                            String id = as1[i1];
                                            if(round != 0)
                                            {
                                                timetableItem = freeFormContainer.getItem(new RowId(new Object[] {
                                                    Integer.valueOf(Integer.parseInt(id))
                                                }));
                                                caption = (new StringBuilder(String.valueOf(caption))).append(teachingAll.getItem(new RowId(new Object[] {
                                                    timetableItem.getItemProperty(TimetableSchema.TEACHING_ID).getValue()
                                                })).getItemProperty("name").getValue().toString()).append(",\n").toString();
                                            }
                                            round++;
                                        }

                                        content = caption.substring(0, caption.length());
                                    } else
                                    if(timetableItem != null)
                                    {
                                        String caption = teachingAll.getItem(new RowId(new Object[] {
                                            timetableItem.getItemProperty(TimetableSchema.TEACHING_ID).getValue()
                                        })).getItemProperty("name").getValue().toString();
                                        content = caption;
                                    } else
                                    {
                                    	content = "ว่าง";
                                    }
                                }
                                data.add(content);
							}
						}else{
							for(int j=0; j < 10; j++){
								String content = "ว่าง";
								data.add(content);
							}
						}
					}

					/* เก็บ id เป็น วัน,ห้องเรียน (index ของวัน ,id ห้องเรียน) */
					exportTable.addItem(data.toArray(),i+","+classRoomId.toString());
				}else{
					for(int j=0; j < 10; j++){
						String content = "";
						content = "ว่าง";
						data.add(content);
					}
					/* เก็บ id เป็น วัน,ห้องเรียน (index ของวัน ,id ห้องเรียน) */
					exportTable.addItem(data.toArray(),i+","+classRoomId.toString());
				}

			}

			HSSFSheet sheet = workbook.createSheet(roomNumber); 				
			sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 10));
			
			
			/* ใส่หัวตาราง */
			HSSFRow headerRow = sheet.createRow(1);
			int column = 0;
			for(Object colHead:exportTable.getColumnHeaders()){
				HSSFCell cell = headerRow.createCell(column);
				cell.setCellValue(new HSSFRichTextString(colHead.toString()));
				cell.setCellStyle(cs);
				column++;
			}
			
			/* ใส่หัวตาราง */
			int rowIndex = 2;
			int totalSec = -5;
			for(Object exportId: exportTable.getItemIds()){
				Item item = exportTable.getItem(exportId);
				HSSFRow row = sheet.createRow(rowIndex);
				row.setHeightInPoints((3*sheet.getDefaultRowHeightInPoints()));
				
				column = 0;
				for(Object property:exportTable.getContainerPropertyIds()){
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
			
			/* ใส่หัวข้อตารางบรรทัดบนสุด ซึ่งต้องผ่านการนับจำนวนคาบ */
			HSSFRow titleRow = sheet.createRow(0);
			HSSFCell cell = titleRow.createCell(0);
			cell.setCellValue(new HSSFRichTextString("ตารางสอน ชั้น" + roomName + " จำนวน " + totalSec + " คาบ"));
			cell.setCellStyle(cs);
		}
		
		try{			
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
		}
		
	}
	
	/* ค้นหาข้อมูลตารางสอนทั้งหมด */
	private void seachAllTimetable(){
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
					if(timetableIdArray[((Integer)section).intValue()] != null && timetableIdArray[((Integer)section).intValue()] != timetableId)
                        timetableIdArray[((Integer)section).intValue()] = (new StringBuilder()).append(timetableIdArray[((Integer)section).intValue()]).append(",").append(timetableId).toString();
                    else
                        timetableIdArray[((Integer)section).intValue()] = timetableId;
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
	}

	/* ค้นหาห้องเรียนทั้งหมด */
	private SQLContainer searchClassRoomLessonPlan(){
		try{	
			SQLContainer freeContainer = container.getFreeFormContainer(getAllClassRoom(), ClassRoomLessonPlanSchema.CLASS_ROOM_LESSON_PLAN_ID);
			return freeContainer;
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	/* ดึงข้อมูลห้องเรียนจากชั้นปีในปีการศึกษาปัจจุบัน */
	private String getAllClassRoom(){
		/*SELECT * FROM class_room_lesson_plan crl
		INNER JOIN class_room cr ON cr.class_room_id = crl.class_room_id
		WHERE cr.class_year = ? AND crl.academic_year = ?;*/
		
		StringBuilder sql = new StringBuilder();	
		sql.append(" SELECT * FROM " + ClassRoomLessonPlanSchema.TABLE_NAME +" crl");
		sql.append(" INNER JOIN " + ClassRoomSchema.TABLE_NAME + " cr ON cr." + ClassRoomSchema.CLASS_ROOM_ID +" = crl." + ClassRoomLessonPlanSchema.CLASS_ROOM_ID);
		sql.append(" WHERE cr." + ClassRoomSchema.CLASS_YEAR + "=" + classYear.getValue());
		sql.append(" AND crl." + ClassRoomLessonPlanSchema.SCHOOL_ID + "=" + SessionSchema.getSchoolID());
		sql.append(" AND crl." + ClassRoomLessonPlanSchema.ACADEMIC_YEAR + "='" + DateTimeUtil.getBuddishYear()+"'");

		return sql.toString();
	}
	
	/* ดึงข้อมูลตารางสอนทั้งหมดของชั้นปี */
	private String getAllTimetable(){
		/* SELECT * FROM timetable tt
		* INNER JOIN class_room cr ON cr.class_room_id = tt.timetable_id
		* INNER JOIN teaching tc ON tc.teaching_id = tt.teaching_id
		* INNER JOIN lesson_plan_subject lps ON lps.subject_id = tc.subject_id
		* WHERE lps.class_year = ? 
		* AND cr.class_year = ? 
		* AND lps.semester = ?
		* AND tc.academic_year = ?; */
		
		StringBuilder sql = new StringBuilder();	
		sql.append(" SELECT * FROM " + TimetableSchema.TABLE_NAME +" tt");
		sql.append(" INNER JOIN " + ClassRoomSchema.TABLE_NAME + " cr ON cr." + ClassRoomSchema.CLASS_ROOM_ID + "=" + "tt." + TimetableSchema.CLASS_ROOM_ID);
		sql.append(" INNER JOIN " + TeachingSchema.TABLE_NAME + " tc ON tc." + TeachingSchema.TEACHING_ID + "=" + "tt." + TimetableSchema.TEACHING_ID);
		sql.append(" INNER JOIN " + LessonPlanSubjectSchema.TABLE_NAME + " lps ON lps." + LessonPlanSubjectSchema.SUBJECT_ID + "=" + "tc." + TeachingSchema.SUBJECT_ID);
		sql.append(" WHERE lps." + LessonPlanSubjectSchema.CLASS_YEAR + "=" + classYear.getValue());
		sql.append(" AND cr." + LessonPlanSubjectSchema.CLASS_YEAR + "=" + classYear.getValue());
		sql.append(" AND lps." + LessonPlanSubjectSchema.SEMESTER + "=" + semester.getValue());
		sql.append(" AND tc." + TeachingSchema.ACADEMIC_YEAR + "='" + DateTimeUtil.getBuddishYear()+"'");	
		sql.append(" AND tc." + TimetableSchema.SCHOOL_ID + "=" + SessionSchema.getSchoolID());	
		
		return sql.toString();
	}
}
