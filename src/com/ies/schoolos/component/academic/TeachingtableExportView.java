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
import com.ies.schoolos.schema.academic.LessonPlanSubjectSchema;
import com.ies.schoolos.schema.academic.TeachingSchema;
import com.ies.schoolos.schema.academic.TimetableSchema;
import com.ies.schoolos.schema.fundamental.ClassRoomSchema;
import com.ies.schoolos.schema.info.PersonnelSchema;
import com.ies.schoolos.type.Days;
import com.ies.schoolos.type.Semester;
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
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

public class TeachingtableExportView extends VerticalLayout {

	private static final long serialVersionUID = 1L;

	/* ที่เก็บข้อมูลตารางสอน อาจารย์ในแต่ละคาบ มารวมเป็น แถวเดียว 
	 *  Object แสดงถึง Index ของวัน 
     *  Object[] แสดง index ของคาบ โดยภายในเก็บ timetableId */
	private HashMap<Object, Object[]> teachingArrays;
	/* เก็บชื่อของอาจารย์ ที่ไม่ซ้ำกัน 
	 * โดยที่ Key เก็บชื่อ */
	private ArrayList<String> teachingAssigned = new ArrayList<String>();

	private Container container = new Container();
	private SQLContainer teachingContainer;
	private SQLContainer timetableFreeFormContainer;
	private SQLContainer freeFormContainer;

	private Teaching teaching;
	private Teaching teachingAll = new Teaching();
	
	private FormLayout settingForm;
	private ComboBox semester;
	
	private VerticalLayout timetableLayout;
	
	public TeachingtableExportView() {
		teachingContainer = container.getTeachingContainer();
		setSpacing(true);
		setMargin(true);
		buildMainLayout();
	}
	
	private void buildMainLayout(){
		HorizontalLayout teachingLayout = new HorizontalLayout();
		teachingLayout.setWidth("100%");
		addComponent(teachingLayout);
		
		settingForm = new FormLayout();
		settingForm.setStyleName("border-white");
		teachingLayout.addComponent(settingForm);
		teachingLayout.setExpandRatio(settingForm, 1);
		
		Label lblSetting = new Label(FontAwesome.GEAR.getHtml() + " ตั้งค่า");
		lblSetting.setContentMode(ContentMode.HTML);
		settingForm.addComponent(lblSetting);
		
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
				/* ดึงข้อมูล ผู้สอนตามวิชาที่ชั้นปีสอน*/
				if(event.getProperty().getValue() != null ){
					teaching = new Teaching((int)event.getProperty().getValue());
					timetableLayout.removeAllComponents();
					seachAllTimetable();
					exportAllTeachingtable();
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
	
	/* เพิ่มข้อมูลตารางสอนในตารางผู้สอน */
	@SuppressWarnings("deprecation")
	private void exportAllTeachingtable(){
		HSSFWorkbook workbook = new HSSFWorkbook(); 
		CellStyle cs = workbook.createCellStyle();
		cs.setWrapText(true);
		cs.setAlignment(CellStyle.ALIGN_CENTER);
		cs.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
		
		int tab = 1;
		
		for (Object itemId: teaching.getItemIds()) {
			Item teachingItem = teaching.getItem(itemId);
			seachTeachingTimetable(itemId);
			String lecturerName = getLecturerName(teachingItem.getItemProperty("name").getValue().toString());

			if(!teachingAssigned.contains(lecturerName)){
				teachingAssigned.add(lecturerName);
				Table exportTable = new Table();
				exportTable.setWidth("100%");
				exportTable.setHeight("340px");
				setTableStyle(exportTable);
				timetableLayout.addComponent(exportTable);
				
				for (int i=0; i < 7;i++) {
					final int workDay = i;
					/* ArrayList 10 index 
					 *  Index แรกแสดงถึงวัน ระหว่าง 0-6 (อ-ส) ที่เหลือแสดงถึง คาบ 9 คาบ*/
					ArrayList<Object> data = new ArrayList<Object>();
					data.add(Days.getNameTh(workDay));
					/* ตรวจสอบจำนวนข้อมูลตารางสอนที่พบ
					 *  กรณีพบตารางสอนที่เคยใส่ก่อนหน้า จะถูกนำมากำหนดปุ่มบนตาราง โดยทำเป็นสีแดง 
					 *  กรณีไม่พบตารางสอน จะใส่ปุ่มสีเขียวทั้งหมด */
					if(teachingArrays.size() > 0){
						/* ตรวจสอบว่ามีการใส่ค่าของ index วัน บน Map หรือยัง
						 *  ถ้าใส่แล้วก็ดึงข้อมูลเก่ามาอัพเดทคาบเรียน ให้ครบทุกคาบ 
						 *  ถ้ายังไม่ใส่ ก็มาใส่ปุ่มสีเขียว โดยตรวจสอบว่าเป็นวันหยุดหรือไม่ */
						if(teachingArrays.containsKey(workDay)){
							Object timetableIdArray[] = teachingArrays.get(workDay);
							/*ข้อมูลตารางสอน ระหว่างคาบ โดยมี 9 ช่อง หรือ 9 คาบ
							 *  Index ต่อมาแสดงถึงคาบ 9 คาบ ระหว่าง 0-8 (1-9) */
							for(int j=0; j < 10; j++){		
								String content = "";
								/* ตรวจสอบว่า คาบดังกล่่าวถูกระบุ timetableId ใว้หรือยัง
								 *  กรณียังว่าง จะกำหนด Caption เป็นว่าง
								 *  กรณี ระบุและว จะกำหนด Caption เป็นชื่อวิชา (อจ) พร้อมตั้งค่า id บนปุ่ม*/
                                if(timetableIdArray[j] == null)
                                {
                                    content = "ว่าง";
                                } else
                                {
                                    String teachingId = "";
                                    Object timetableId = timetableIdArray[j];
                                    Item timetableItem = timetableFreeFormContainer.getItem(new RowId(new Object[] {
                                        timetableId
                                    }));
                                    if(timetableId.toString().contains(","))
                                    {
                                        String timetableIds[] = timetableId.toString().split(",");
                                        timetableItem = timetableFreeFormContainer.getItem(new RowId(new Object[] {
                                            Integer.valueOf(Integer.parseInt(timetableIds[0]))
                                        }));
                                        String captionOriginal = (new StringBuilder(String.valueOf(getTeachingName(teachingAll.getItem(new RowId(new Object[] {
                                            timetableItem.getItemProperty(TimetableSchema.TEACHING_ID).getValue()
                                        })).getItemProperty("name").getValue().toString())))).append(" \n").toString();
                                        String caption = captionOriginal;
                                        String as1[];
                                        int j1 = (as1 = timetableIds).length;
                                        for(int i1 = 0; i1 < j1; i1++)
                                        {
                                            String id = as1[i1];
                                            timetableItem = timetableFreeFormContainer.getItem(new RowId(new Object[] {
                                                Integer.valueOf(Integer.parseInt(id))
                                            }));
                                            if(teachingId.equals(""))
                                            {
                                                caption = (new StringBuilder(String.valueOf(caption))).append(" ").append(timetableItem.getItemProperty("name").getValue()).toString();
                                                teachingId = timetableItem.getItemProperty(TimetableSchema.TEACHING_ID).getValue().toString();
                                            } else
                                            if(!teachingId.equals(timetableItem.getItemProperty(TimetableSchema.TEACHING_ID).getValue().toString()))
                                            {
                                                caption = (new StringBuilder(String.valueOf(getTeachingName(teachingAll.getItem(new RowId(new Object[] {
                                                    Integer.valueOf(Integer.parseInt(teachingId))
                                                })).getItemProperty("name").getValue().toString())))).append(",\n").append(getTeachingName(teachingAll.getItem(new RowId(new Object[] {
                                                    timetableItem.getItemProperty(TimetableSchema.TEACHING_ID).getValue()
                                                })).getItemProperty("name").getValue().toString())).append(" \n").append(" ").append(timetableItem.getItemProperty("name").getValue()).toString();
                                            } else
                                            {
                                                caption = (new StringBuilder(String.valueOf(caption))).append(" ").append(timetableItem.getItemProperty("name").getValue()).toString();
                                                teachingId = timetableItem.getItemProperty(TimetableSchema.TEACHING_ID).getValue().toString();
                                            }
                                        }

                                        content = caption;
                                    } else
                                    if(timetableItem != null)
                                        content = (new StringBuilder(String.valueOf(getTeachingName(teachingAll.getItem(new RowId(new Object[] {
                                            timetableItem.getItemProperty(TimetableSchema.TEACHING_ID).getValue()
                                        })).getItemProperty("name").getValue().toString())))).append(" \n").append(timetableItem.getItemProperty("name").getValue()).toString();
                                    else
                                        content = "ว่าง";
                                }
                                data.add(content);

							}
							exportTable.addItem(data.toArray(),workDay);
						}
					}else{
						for(int j=0; j < 10; j++){
							String content = "ว่าง";
							data.add(content);
						}
						exportTable.addItem(data.toArray(),workDay);
					}
				}
					
				HSSFSheet sheet = workbook.createSheet(tab + "." + lecturerName); 				
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
				int totalSec = 0;
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

				/* จำนวนคาบทั้งหมด ลบด้วย 2 บรรทัดแรก(สรุปคาบ และ แถว หัวตาราง) และ จำนวนวันที่สอน เพราะระบบนับ วันที่ระบุในแถววิชาเป็นหนึ่งคาบ ด้วย */
				totalSec = totalSec - (rowIndex - 2);
				/* ใส่หัวข้อตารางบรรทัดบนสุด ซึ่งต้องผ่านการนับจำนวนคาบ */
				HSSFRow titleRow = sheet.createRow(0);
				HSSFCell cell = titleRow.createCell(0);
				cell.setCellValue(new HSSFRichTextString("ตารางสอนอาจารย์ " + lecturerName + " จำนวน " + totalSec + " คาบ"));
				cell.setCellStyle(cs);
				tab++;
			}
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

	/* ค้นหาข้อมูลตารางสอนของอาจารย์ทั้งหมด */
	private void seachTeachingTimetable(Object teachingId){
		teachingArrays = new HashMap<Object, Object[]>();
		Item teachingItem = teachingContainer.getItem(teachingId);

		freeFormContainer = container.getFreeFormContainer(getTeachingSQL(teachingItem), TimetableSchema.TIMETABLE_ID);
		/* นำข้อมูลที่ได้มาใส่ใน Object โดยในฐานข้อมูลจะเก็บ 1 คาบ 1 แถว แต่มาใส่ในตารางจะต้องมารวมทุกคาบมาเป็นแถวเดียวโดยแยกตามวัน */
		for (Object itemId:freeFormContainer.getItemIds()) {
	
			Item timetableItem = freeFormContainer.getItem(itemId);
			Object timetableId = timetableItem.getItemProperty(TimetableSchema.TIMETABLE_ID).getValue();
			Object workDay = timetableItem.getItemProperty(TimetableSchema.WORKING_DAY).getValue();
			Object section = timetableItem.getItemProperty(TimetableSchema.SECTION).getValue();
			
			/* ตรวจสอบ ข้อมูลตารางเรียนจาก Key ที่แทนด้วย index ของวัน 0-6
			 *  กรณีมีข้อมูลถูกระบุแล้ว จะทำการดึงข้อมูลแล้วใส่ ตารางใน Value ของ คาบช่วงระหว่าง 0-8
			 *  กรณีไม่มีข้อมูล ก็ทำการเพิ่ม Key ใหม่ โดยกำหนด Value ของคาบช่วงระหว่าง 0-8*/
			if(teachingArrays.containsKey(workDay)){
				Object timetableIdArray[] = teachingArrays.get(workDay);
				if(timetableIdArray[((Integer)section).intValue()] != null && timetableIdArray[((Integer)section).intValue()] != timetableId)
                    timetableIdArray[((Integer)section).intValue()] = (new StringBuilder()).append(timetableIdArray[((Integer)section).intValue()]).append(",").append(timetableId).toString();
                else
                    timetableIdArray[((Integer)section).intValue()] = timetableId;
				teachingArrays.put(workDay, timetableIdArray);
			}else{
				Object timetableIdArray[] = new Object[10];
				timetableIdArray[(int)section] = timetableId;
				teachingArrays.put(workDay, timetableIdArray);
			}
		}
	}

	/* ค้นหาข้อมูลตารางสอนทั้งหมด */
	private void seachAllTimetable(){
		/* SELECT * FROM timetable tt
		* INNER JOIN class_room cr ON cr.class_room_id = tt.timetable_id
		* INNER JOIN teaching tc ON tc.teaching_id = tt.teaching_id
		* INNER JOIN lesson_plan_subject lps ON lps.subject_id = tc.subject_id
		* WHERE lps.semester = ?
		* AND tc.academic_year = ?; */
		
		StringBuilder sql = new StringBuilder();	
		sql.append(" SELECT tt.*,cr.* FROM " + TimetableSchema.TABLE_NAME +" tt");
		sql.append(" INNER JOIN " + ClassRoomSchema.TABLE_NAME + " cr ON cr." + ClassRoomSchema.CLASS_ROOM_ID + "=" + "tt." + TimetableSchema.CLASS_ROOM_ID);
		sql.append(" INNER JOIN " + TeachingSchema.TABLE_NAME + " tc ON tc." + TeachingSchema.TEACHING_ID + "=" + "tt." + TimetableSchema.TEACHING_ID);
		sql.append(" INNER JOIN " + LessonPlanSubjectSchema.TABLE_NAME + " lps ON lps." + LessonPlanSubjectSchema.SUBJECT_ID + "=" + "tc." + TeachingSchema.SUBJECT_ID);
		sql.append(" WHERE lps." + LessonPlanSubjectSchema.SEMESTER + "=" + semester.getValue());
		sql.append(" AND tc." + TeachingSchema.ACADEMIC_YEAR + "='" + DateTimeUtil.getBuddishYear()+"'");	
		sql.append(" AND tc." + TimetableSchema.SCHOOL_ID + "=" + SessionSchema.getSchoolID());	
		
		timetableFreeFormContainer = container.getFreeFormContainer(sql.toString(), TimetableSchema.TIMETABLE_ID);
	}
		
	/* ดึงข้อมูลการสอนทั้งหมด ของครูผู้สอน */
	private String getTeachingSQL(Item item){
		StringBuilder sql = new StringBuilder();
		if(item.getItemProperty(TeachingSchema.PERSONNEL_ID).getValue() != null){
			/* SQL สำหรับดึงข้อมูล
			* SELECT * FROM timetable tt 
			* INNER JOIN teaching t ON tt.teaching_id=t.teaching_id 
			* INNER JOIN personnel p ON p.personnel_id=t.personnel_id 
			* WHERE p.personnel_id=? 
			* AND tt.school_id = ?*/
					 
			sql.append(" SELECT tt.* FROM " + TimetableSchema.TABLE_NAME +" tt");
			sql.append(" INNER JOIN " + TeachingSchema.TABLE_NAME + " t ON tt." + TeachingSchema.TEACHING_ID + "=" + "t." + TimetableSchema.TEACHING_ID);
			sql.append(" INNER JOIN " + PersonnelSchema.TABLE_NAME + " p ON p." + PersonnelSchema.PERSONNEL_ID + "=" + "t." + TeachingSchema.PERSONNEL_ID);
			sql.append(" WHERE p." + PersonnelSchema.PERSONNEL_ID + "=" + item.getItemProperty(TeachingSchema.PERSONNEL_ID).getValue());
		}else{		
			/*SQL สำหรับดึงข้อมูล
			 * SELECT * FROM timetable tt 
			 * INNER JOIN teaching t ON tt.teaching_id=t.teaching_id 
			 * WHERE t.personnel_name_tmp=?
			 * AND tt.school_id = ?*/
			
			sql.append(" SELECT tt.* FROM " + TimetableSchema.TABLE_NAME +" tt");
			sql.append(" INNER JOIN " + TeachingSchema.TABLE_NAME + " t ON tt." + TeachingSchema.TEACHING_ID + "=" + "t." + TimetableSchema.TEACHING_ID);
			sql.append(" WHERE t." + TeachingSchema.PERSONNEL_NAME_TMP + "='" + item.getItemProperty(TeachingSchema.PERSONNEL_NAME_TMP).getValue()+"'");
		}

		sql.append(" AND tt." + TeachingSchema.SCHOOL_ID + "=" + SessionSchema.getSchoolID());
		return sql.toString();
	}
	
	/* ตัดคำ จากชื่อของผู้สอน ซึ่งอยู่ในรูปของ 
	 *  ท1101:ภาษาไทย1 (อ.ทดลอง ทดสอบ) หรือ
	 *  แนะแนว (อ.ทดลอง ทดสอบ) 
	 *  ให้อยู่ในรํปของ 
	 *    - ถ้ามีรหัสวิชา ท1101
	 *    - ถ้าไม่มีรหัสวิชา แนะแนว อ.ทดลอง ทดสอบ */
	private String getTeachingName(String name){
		String styles = name.substring(0, name.indexOf("("))+"\n" +
				name.substring(name.indexOf("(")+1, name.lastIndexOf(")"));
		return styles;
	}
	
	/* ตัดคำ จากชื่อของผู้สอน ซึ่งอยู่ในรูปของ 
	 *  ท1101:ภาษาไทย1 (อ.ทดลอง ทดสอบ) หรือ
	 *  แนะแนว (อ.ทดลอง ทดสอบ) 
	 *  ให้อยู่ในรํปของ อ.ทดลอง ทดสอบ */
	private String getLecturerName(String name){
		String styles = name.substring(name.indexOf("(")+1, name.indexOf(")"));;
		return styles;
	}
	
}
