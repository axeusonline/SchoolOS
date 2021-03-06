package com.ies.schoolos.component.academic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.tepi.filtertable.FilterTable;
import org.vaadin.dialogs.ConfirmDialog;

import com.ies.schoolos.container.Container;
import com.ies.schoolos.filter.TableFilterDecorator;
import com.ies.schoolos.filter.TableFilterGenerator;
import com.ies.schoolos.schema.CreateModifiedSchema;
import com.ies.schoolos.schema.SessionSchema;
import com.ies.schoolos.schema.academic.ClassRoomLessonPlanSchema;
import com.ies.schoolos.schema.academic.LessonPlanSubjectSchema;
import com.ies.schoolos.schema.academic.TeachingSchema;
import com.ies.schoolos.schema.academic.TimetableSchema;
import com.ies.schoolos.schema.fundamental.ClassRoomSchema;
import com.ies.schoolos.schema.info.PersonnelSchema;
import com.ies.schoolos.type.ClassYear;
import com.ies.schoolos.type.Days;
import com.ies.schoolos.type.Semester;
import com.ies.schoolos.type.dynamic.ClassRoom;
import com.ies.schoolos.type.dynamic.Teaching;
import com.ies.schoolos.utility.DateTimeUtil;
import com.ies.schoolos.utility.Notification;
import com.ies.schoolos.utility.Utility;
import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.filter.And;
import com.vaadin.data.util.filter.Compare.Equal;
import com.vaadin.data.util.sqlcontainer.RowId;
import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.UI;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CustomTable.Align;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class TimetableView extends VerticalLayout {

	private static final long serialVersionUID = 1L;

	private String semesterStr = "";
	private String CHECK_BOX_FIELD = "checkbox";
	
	private ArrayList<Object> multiRoomIds = new ArrayList<>();
	
	/* ที่เก็บข้อมูลตารางสอน อาจารย์ในแต่ละคาบ มารวมเป็น แถวเดียว 
	 *  Object แสดงถึง Index ของวัน 
     *  Object[] แสดง index ของคาบ โดยภายในเก็บ timetableId */
	private HashMap<Object, Object[]> teachingCmbs;
	/* ที่เก็บข้อมูลตารางสอนทั้งหมด ในแต่ห้องและคาบ มารวมเป็น แถวเดียว แยกตามวัน
	 *   Object แสดงถึง Index ของวัน 
	 *   HashMap<Object, Object[]> 
	 *     > Object แสดงถึง Id ของห้องเรียน
	 *     > Object[] แสดง index ของคาบ โดยภายในเก็บ timetableId */
	private HashMap<Object, HashMap<Object, Object[]>> timetables;

	private Container container = new Container();
	private ClassRoom classRoom = new ClassRoom();
	private SQLContainer teachingCmbContainer = container.getTeachingContainer();
	private SQLContainer classRoomContainer = container.getClassRoomContainer();
	private SQLContainer timetableContainer = container.getTimetableContainer();
	private SQLContainer freeFormContainer;
	
	private Teaching teachingSemesterAndDay;
	private Teaching teachings = new Teaching();
	
	private FormLayout settingForm;
	private ComboBox classYear;
	private ComboBox semester;
	private OptionGroup days;
	private ComboBox teachingCmb;
	
	private FilterTable teachingCmbTable;
	private FilterTable allTimeteachingCmbTable;
	
	public TimetableView() {	
		classRoomContainer.addContainerFilter(new Equal(ClassRoomSchema.SCHOOL_ID,SessionSchema.getSchoolID()));
		teachingCmbContainer.addContainerFilter(new And(new Equal(TeachingSchema.SCHOOL_ID,SessionSchema.getSchoolID()),
				new Equal(TeachingSchema.ACADEMIC_YEAR,DateTimeUtil.getBuddishYear())));
		
		setSpacing(true);
		setMargin(true);
		buildMainLayout();
	}
	
	private void buildMainLayout(){
		HorizontalLayout teachingCmbLayout = new HorizontalLayout();
		teachingCmbLayout.setWidth("100%");
		addComponent(teachingCmbLayout);
		
		settingForm = new FormLayout();
		settingForm.setStyleName("border-white");
		teachingCmbLayout.addComponent(settingForm);
		teachingCmbLayout.setExpandRatio(settingForm, 1);
		
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
				/* ดึงข้อมูล ผู้สอนตามวิชาที่ชั้นปีสอน*/
				if(event.getProperty().getValue() != null
						&& semester.getValue() != null){
					teachingSemesterAndDay= new Teaching(event.getProperty().getValue(), semester.getValue(),Utility.sortOptionGroup(days.getValue()));
					teachingCmb.setContainerDataSource(teachingSemesterAndDay);
				}
				/* ตรวจสอบ เงื่อนไขการค้นหาข้อมูล */
				if(days.getValue() != null &&
						teachingCmb.getValue() != null && 
						semester.getValue() != null && 
						event.getProperty().getValue() != null){
					setTeachingTimetable();
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
				/* ดึงข้อมูล ผู้สอนตามวิชาที่ชั้นปีสอน*/
				if(event.getProperty().getValue() != null){
					semesterStr = event.getProperty().getValue().toString();
					fetchTimetable();

					if(classYear.getValue() != null){
						teachingSemesterAndDay= new Teaching(classYear.getValue(), event.getProperty().getValue(),Utility.sortOptionGroup(days.getValue()));
						teachingCmb.setContainerDataSource(teachingSemesterAndDay);
					}
				}
				
				/* ตรวจสอบ เงื่อนไขการค้นหาข้อมูล */
				if(classYear.getValue() != null &&
						days.getValue() != null &&
						teachingCmb.getValue() != null
						&& event.getProperty().getValue() != null){
					setTeachingTimetable();
					setAllTimetable();
				}
			}
		});
		settingForm.addComponent(semester);
		
		days = new OptionGroup("เลือกวันหยุด", new Days());
		days.setMultiSelect(true);
		days.setRequired(true);
		days.setItemCaptionPropertyId("name");
        days.setNullSelectionAllowed(false);
        days.setHtmlContentAllowed(true);
        days.setImmediate(true);
        days.addValueChangeListener(new ValueChangeListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				/* ดึงข้อมูล ผู้สอนตามวิชาที่ชั้นปีสอน*/
				if(semester.getValue() != null &&
						classYear.getValue() != null){
					teachingSemesterAndDay= new Teaching(classYear.getValue(), semester.getValue(),Utility.sortOptionGroup(event.getProperty().getValue()));
					teachingCmb.setContainerDataSource(teachingSemesterAndDay);
				}
				
				/* ตรวจสอบ เงื่อนไขการค้นหาข้อมูล */
				if(classYear.getValue() != null &&
						semester.getValue() != null && 
						teachingCmb.getValue() != null &&
						event.getProperty().getValue() != null){
					setTeachingTimetable();
					setAllTimetable();
				}
			}
		});
        settingForm.addComponent(days);
        
        teachingCmb = new ComboBox("ผู้สอน");
        teachingCmb.setRequired(true);
        teachingCmb.setInputPrompt("เลือกข้อมูล");
        teachingCmb.setItemCaptionPropertyId("name");
        teachingCmb.setImmediate(true);
		teachingCmb.setNullSelectionAllowed(false);
		teachingCmb.setFilteringMode(FilteringMode.CONTAINS);
		teachingCmb.addValueChangeListener(new ValueChangeListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				/* ตรวจสอบ เงื่อนไขการค้นหาข้อมูล */
				if(classYear.getValue() != null &&
						semester.getValue() != null && 
						days.getValue() != null &&
						event.getProperty().getValue() != null){
					setTeachingTimetable();
					setAllTimetable();
				}
			}
		});
		settingForm.addComponent(teachingCmb);
		
		teachingCmbTable = new FilterTable();
		teachingCmbTable.setWidth("100%");
		teachingCmbTable.setHeight("400px");
		teachingCmbTable.setCaption("ตารางสอนอาจารย์");
		teachingCmbTable.setSelectable(true);
		teachingCmbTable.setFooterVisible(true);  
		setTableStyle(teachingCmbTable);
		teachingCmbLayout.addComponent(teachingCmbTable);
		teachingCmbLayout.setExpandRatio(teachingCmbTable,(float) 2.9);
		teachingCmbTable.setFilterDecorator(new TableFilterDecorator());
		teachingCmbTable.setFilterGenerator(new TableFilterGenerator());
        teachingCmbTable.setFilterBarVisible(true);
        
        allTimeteachingCmbTable = new FilterTable();
		allTimeteachingCmbTable.setWidth("100%");
		allTimeteachingCmbTable.setCaption("ตารางรวม");
		allTimeteachingCmbTable.setSelectable(true);
		allTimeteachingCmbTable.setFooterVisible(true);  
		setTableStyle(allTimeteachingCmbTable);
		allTimeteachingCmbTable.setFilterDecorator(new TableFilterDecorator());
		allTimeteachingCmbTable.setFilterGenerator(new TableFilterGenerator());
        allTimeteachingCmbTable.setFilterBarVisible(true);
        addComponent(allTimeteachingCmbTable);
        setExpandRatio(allTimeteachingCmbTable, 2);
	}
	
	private void setTableStyle(FilterTable table){
		if(table != allTimeteachingCmbTable){
			table.addContainerProperty(TimetableSchema.WORKING_DAY, String.class, null);
			table.addContainerProperty("1", Button.class, null);
			table.addContainerProperty("2", Button.class, null);
			table.addContainerProperty("3", Button.class, null);
			table.addContainerProperty("4", Button.class, null);
			table.addContainerProperty("5", Button.class, null);
			table.addContainerProperty("6", Button.class, null);
			table.addContainerProperty("7", Button.class, null);
			table.addContainerProperty("8", Button.class, null);
			table.addContainerProperty("9", Button.class, null);
			table.addContainerProperty("10", Button.class, null);
			
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
		}else{
			table.addContainerProperty(TimetableSchema.WORKING_DAY, String.class, null);
			table.addContainerProperty(TimetableSchema.CLASS_ROOM_ID, String.class, null);
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
		
		
	}
	
	/* เพิ่มข้อมูลตารางสอนในตารางผู้สอน */
	private void setTeachingTimetable(){
		teachingCmbTable.removeAllItems();
		seachTeachingTimetable();
		String[] daysClosed = getDays();
		
		for (int i=0; i < 7;i++) {
			final int weekDay = i;
			/* ArrayList 10 index 
			 *  Index แรกแสดงถึงวัน ระหว่าง 0-6 (อ-ส) ที่เหลือแสดงถึง คาบ 9 คาบ*/
			ArrayList<Object> data = new ArrayList<Object>();
			
			/* ตรวจสอบจำนวนข้อมูลตารางสอนที่พบ
			 *  กรณีพบตารางสอนที่เคยใส่ก่อนหน้า จะถูกนำมากำหนดปุ่มบนตาราง โดยทำเป็นสีแดง 
			 *  กรณีไม่พบตารางสอน จะใส่ปุ่มสีเขียวทั้งหมด */
			if(teachingCmbs.size() > 0){
				/* ตรวจสอบว่ามีการใส่ค่าของ index วัน บน Map หรือยัง
				 *  ถ้าใส่แล้วก็ดึงข้อมูลเก่ามาอัพเดทคาบเรียน ให้ครบทุกคาบ 
				 *  ถ้ายังไม่ใส่ ก็มาใส่ปุ่มสีเขียว โดยตรวจสอบว่าเป็นวันหยุดหรือไม่ */
				if(teachingCmbs.containsKey(weekDay)){
					if(!Arrays.asList(daysClosed).contains(Integer.toString(weekDay))){
						data.add(Days.getNameTh(weekDay));

						Object timetableIdArray[] = teachingCmbs.get(weekDay);
						/*ข้อมูลตารางสอน ระหว่างคาบ โดยมี 9 ช่อง หรือ 9 คาบ
						 *  Index ต่อมาแสดงถึงคาบ 9 คาบ ระหว่าง 0-8 (1-9) */
						for(int j=0; j < 10; j++){
							final int section =j;
							NativeButton button = new NativeButton();
							button.setWidth("80px");
							button.setHeight("-1px");
							/* ตรวจสอบว่า คาบดังกล่่าวถูกระบุ timetableId ใว้หรือยัง
							 *  กรณียังว่าง จะกำหนด Caption เป็นว่าง
							 *  กรณี ระบุและว จะกำหนด Caption เป็นชื่อวิชา (อจ) พร้อมตั้งค่า id บนปุ่ม*/
							if(timetableIdArray[j] == null){
								button.setCaption("ว่าง");
								button.setStyleName("green-button");
								button.addClickListener(new ClickListener() {
									private static final long serialVersionUID = 1L;

									@Override
									public void buttonClick(ClickEvent event) {
										initTimetableLayout(weekDay,section);
									}
								});
							}else{
								String teachingId = "";
								final Object timetableId = timetableIdArray[j];
								Item timetableItem = timetableContainer.getItem(new RowId(timetableId));
								
								if(timetableId.toString().contains(",")){
									String[] timetableIds = timetableId.toString().split(",");
									timetableItem = timetableContainer.getItem(new RowId(Integer.parseInt(timetableIds[0])));
									String caption = getTeachingName(teachings.
											getItem(new RowId(timetableItem.getItemProperty(TimetableSchema.TEACHING_ID).getValue())).
											getItemProperty("name").getValue().toString()) + " \n";
									String as[];
                                    int l = (as = timetableIds).length;
                                    for(int k = 0; k < l; k++)
                                    {
                                        String id = as[k];
                                        timetableItem = timetableContainer.getItem(new RowId(Integer.parseInt(id)));
                                        Item classRoomItem = classRoomContainer.getItem(new RowId(timetableItem.getItemProperty(ClassRoomSchema.CLASS_ROOM_ID).getValue()));
                                        if(teachingId.equals(""))
                                        {
                                            caption = caption + " " + classRoomItem.getItemProperty("name").getValue();
                                            teachingId = timetableItem.getItemProperty(TimetableSchema.TEACHING_ID).getValue().toString();
                                        } else
                                        if(!teachingId.equals(timetableItem.getItemProperty(TimetableSchema.TEACHING_ID).getValue().toString()))
                                        {
                                            caption = getTeachingName(teachings.getItem(new RowId(Integer.parseInt(teachingId))).getItemProperty("name").getValue().toString()) + ",\n" 
                                            		+ getTeachingName(teachings.getItem(new RowId(timetableItem.getItemProperty(TimetableSchema.TEACHING_ID).getValue())).getItemProperty("name").getValue().toString()) + " \n"
                                            		+ classRoomItem.getItemProperty("name").getValue();
                                        } else
                                        {
                                            caption = " " + classRoomItem.getItemProperty("name").getValue();
                                            teachingId = timetableItem.getItemProperty(TimetableSchema.TEACHING_ID).getValue().toString();
                                        }
                                    }
                                    
									button.setCaption(caption);
									button.setId(timetableId.toString());
									button.setStyleName("red-button");
									button.addClickListener(new ClickListener() {
										private static final long serialVersionUID = 1L;

										@Override
										public void buttonClick(ClickEvent event) {
											removeTimetableLayout(timetableId);
										}
									});
								}else if(timetableItem != null){
									Item classRoomItem = classRoomContainer.getItem(new RowId(timetableItem.getItemProperty(TimetableSchema.CLASS_ROOM_ID).getValue()));
									/* แสดง ชื่ออาจารย์ /n ห้องเรียน*/
									String caption = getTeachingName(teachings.
											getItem(new RowId(timetableItem.getItemProperty(TimetableSchema.TEACHING_ID).getValue())).
											getItemProperty("name").getValue().toString()) + " \n" +										
											classRoomItem.getItemProperty(ClassRoomSchema.NAME).getValue();
									
									button.setCaption(caption);
									button.setId(timetableId.toString());
									button.setStyleName("red-button");
									button.addClickListener(new ClickListener() {
										private static final long serialVersionUID = 1L;

										@Override
										public void buttonClick(ClickEvent event) {
											removeTimetableLayout(timetableId);
										}
									});
								}else{
									button.setCaption("ว่าง");
									button.setStyleName("green-button");
									button.addClickListener(new ClickListener() {
										private static final long serialVersionUID = 1L;

										@Override
										public void buttonClick(ClickEvent event) {
											initTimetableLayout(weekDay,section);
										}
									});
								}
							}
							data.add(button);
						}
						teachingCmbTable.addItem(data.toArray(),weekDay);
					}
				}else if(!Arrays.asList(daysClosed).contains(Integer.toString(weekDay))){
					data.add(Days.getNameTh(weekDay));	
					for(int j=0; j < 10; j++){
						final int section =j;
						NativeButton button = new NativeButton("ว่าง");
						button.setWidth("80px");
						button.setHeight("-1px");
						button.setStyleName("green-button");
						button.addClickListener(new ClickListener() {
							private static final long serialVersionUID = 1L;
	
							@Override
							public void buttonClick(ClickEvent event) {
								initTimetableLayout(weekDay,section);
							}
						});
						data.add(button);
					}
					teachingCmbTable.addItem(data.toArray(),weekDay);
				}
			}else{
				if(!Arrays.asList(daysClosed).contains(Integer.toString(weekDay))){	
					data.add(Days.getNameTh(weekDay));	
					for(int j=0; j < 10; j++){
						final int section =j;
						Button button = new Button("ว่าง");
						button.setStyleName("green-button");
						button.addClickListener(new ClickListener() {
							private static final long serialVersionUID = 1L;
	
							@Override
							public void buttonClick(ClickEvent event) {
								initTimetableLayout(weekDay,section);
							}
						});
						data.add(button);
					}
					teachingCmbTable.addItem(data.toArray(),weekDay);
				}
			}
		}
	}
	
	/* เพิ่มข้อมูลตารางสอนทั้มหงด */
	private void setAllTimetable(){
		allTimeteachingCmbTable.removeAllItems();
		seachAllTimetable();
		
		/* วนลูบเรียงตามวัน อาทิตย์ ถึง เสาร์ */
		for (int i=0; i < 7;i++) {
			final int weekDay = i;
			/* ดึงห้องเรียนทั้งหมดในปีการศึกษา */
			for(Object itemId:searchClassRoomLessonPlan().getItemIds()){
				Object classRoomId = searchClassRoomLessonPlan().getItem(itemId).getItemProperty(ClassRoomLessonPlanSchema.CLASS_ROOM_ID).getValue();
				
				ArrayList<Object> data = new ArrayList<Object>();	
				data.add(Days.getNameTh(weekDay));
				data.add(classRoom.getItem(classRoomId).getItemProperty("name").getValue());
				
				/* ตรวจสอบจำนวนข้อมูลตารางสอนที่พบ
				 *  กรณีพบตารางสอนที่เคยใส่ก่อนหน้า จะตรวจคาบใหนที่กำหนดแล้ว (สีแดง) หรือ ยังไม่กำหนด (เขียว) 
				 *  กรณีไม่พบตารางสอน จะใส่ปุ่มสีเขียวทั้งหมด */
				if(timetables.size() > 0){
					if(timetables.containsKey(weekDay)){
						HashMap<Object, Object[]> teachingCmbsTmp = timetables.get(weekDay);
						/* ตรวจสอบว่าวันดัวกล่าวมีการกำหนดคาบอย่างน้อยหนึ่งคาบหรือไม่
						 *  กรณีมีการกำหนด ก็จะทำการกดหนด ตาบสีแดง ในวันดังกล่าง
						 *  กรณีไม่มีการกำหนดคาบได ๆ เลย จะกำหนด สีเขียวทั้งหมดของห้องดังกล่าว */
						if(teachingCmbsTmp.containsKey(classRoomId)){	

							Object timetableIdArray[] = teachingCmbsTmp.get(classRoomId);
							
							/* วนลูบจำนวนคาบ 9 คาบ */
							for(int j=0; j < 10; j++){
								Label label = new Label();
								label.setWidth("90px");
								label.setHeight("100%");
								label.setContentMode(ContentMode.HTML);
								/* ตรวจสอบว่า คาบดังกล่่าวถูกระบุใว้หรือยัง
								 *  กรณียังว่าง จะกำหนด Caption เป็น "ว่าง"
								 *  กรณี ระบุ จะกำหนด Caption เป็นชื่อวิชา (อจ) พร้อมตั้งค่า id บนปุ่ม*/
								if(timetableIdArray[j] == null){
									label.setValue("ว่าง");
									label.setStyleName("green-label");
								}else{
									Object timetableId = timetableIdArray[j];
									Item timetableItem = timetableContainer.getItem(new RowId(timetableId));
									if(timetableId.toString().contains(",")){
										String caption = "";
										String[] timetableIds = timetableId.toString().split(",");
										for(String id:timetableIds){
											timetableItem = timetableContainer.getItem(new RowId(Integer.parseInt(id)));
											caption += teachings.
													getItem(new RowId(timetableItem.getItemProperty(TimetableSchema.TEACHING_ID).getValue())).
													getItemProperty("name").getValue().toString()+",</br>";
										}
										label.setValue(getTeachingNameHtml(caption));
										label.setId(timetableId.toString());
										label.setStyleName("red-label");
									}else if(timetableItem != null){
										String caption = teachings.
												getItem(new RowId(timetableItem.getItemProperty(TimetableSchema.TEACHING_ID).getValue())).
												getItemProperty("name").getValue().toString();
										
										label.setValue(getTeachingNameHtml(caption));
										label.setId(timetableId.toString());
										label.setStyleName("red-label");
									}else{
										label.setValue("ว่าง");
										label.setStyleName("green-label");
									}
								}
								data.add(label);
							}
						}else{
							for(int j=0; j < 10; j++){
								Label label = new Label("ว่าง");
								label.setWidth("90px");
								label.setHeight("100%");
								label.setStyleName("green-label");
								data.add(label);
							}
						}
					}else{
						for(int j=0; j < 10; j++){
							Label label = new Label("ว่าง");
							label.setWidth("90px");
							label.setHeight("100%");
							label.setStyleName("green-label");
							data.add(label);
						}
					}

					/* เก็บ id เป็น วัน,ห้องเรียน (index ของวัน ,id ห้องเรียน) */
					allTimeteachingCmbTable.addItem(data.toArray(),i+","+classRoomId.toString());
				}else{
					for(int j=0; j < 10; j++){
						Label label = new Label("ว่าง");
						label.setWidth("90px");
						label.setHeight("100%");
						label.setStyleName("green-label");
						data.add(label);
					}
					/* เก็บ id เป็น วัน,ห้องเรียน (index ของวัน ,id ห้องเรียน) */
					allTimeteachingCmbTable.addItem(data.toArray(),i+","+classRoomId.toString());
				}
			}
		}
	}
	
	/* เพิ่มข้อมูลตารางสอน */
	private void initTimetableLayout(final Object workDay,final Object section){
		final Window window = new Window();
		window.setWidth("60%");
		window.setHeight("60%");
		window.center();
		UI.getCurrent().addWindow(window);
		
		TabSheet sheet = new TabSheet();
		sheet.setSizeFull();
		window.setContent(sheet);
		
		FilterTable availableTable = initSelectTable();
		/* Layout คาบร่วม*/
		multiRoomIds = new ArrayList<>();
		
		VerticalLayout multiRoomLayout = new VerticalLayout();
		multiRoomLayout.setSizeFull();
		
		FilterTable multiRoomTable = initSelectMulitiRoomTable();
		multiRoomLayout.addComponent(multiRoomTable);
		multiRoomLayout.setExpandRatio(multiRoomTable, 1);
		
		Button multiRoomSelectButton = new Button("เลือก", FontAwesome.SAVE);
		multiRoomSelectButton.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 1L;

			@SuppressWarnings("unchecked")
			@Override
			public void buttonClick(ClickEvent event) {
				if(multiRoomIds.size()>0){
					for(Object classId: multiRoomIds){
						try{
							timetableContainer.removeAllContainerFilters();
							Object tmpId = timetableContainer.addItem();
							Item timetableItem = timetableContainer.getItem(tmpId);
							timetableItem.getItemProperty(TimetableSchema.SCHOOL_ID).setValue(SessionSchema.getSchoolID());
							timetableItem.getItemProperty(TimetableSchema.CLASS_ROOM_ID).setValue(Integer.parseInt(classId.toString()));
							timetableItem.getItemProperty(TimetableSchema.TEACHING_ID).setValue(Integer.parseInt(teachingCmb.getValue().toString()));
							timetableItem.getItemProperty(TimetableSchema.SECTION).setValue((int)section);
							timetableItem.getItemProperty(TimetableSchema.WORKING_DAY).setValue((int)workDay);
							timetableItem.getItemProperty(TimetableSchema.SEMESTER).setValue(Integer.parseInt(semester.getValue().toString()));
							timetableItem.getItemProperty(TimetableSchema.ACADEMIC_YEAR).setValue(DateTimeUtil.getBuddishYear());
							CreateModifiedSchema.setCreateAndModified(timetableItem);
							timetableContainer.commit();
							Notification.show("บันทึกสำเร็จ", Type.HUMANIZED_MESSAGE);
						} catch (Exception e) {
							Notification.show("บันทึกไม่สำเร็จ", Type.WARNING_MESSAGE);
							e.printStackTrace();
						}
					}
					fetchTimetable();
					window.close();
					setTeachingTimetable();
					setAllTimetable();
				}else{
					Notification.show("กรุณาเลือกห้องเรียน", Type.WARNING_MESSAGE);
				}
			}
		});
		multiRoomLayout.addComponent(multiRoomSelectButton);
		multiRoomLayout.setComponentAlignment(multiRoomSelectButton, Alignment.TOP_RIGHT);
		
		FilterTable combineSubjectTable = initSelectTable();
		
		sheet.addTab(availableTable,"คาบว่าง", FontAwesome.CALENDAR);
		sheet.addTab(multiRoomLayout,"คาบร่วม", FontAwesome.CALENDAR);
		sheet.addTab(combineSubjectTable,"วิชาร่วม", FontAwesome.CALENDAR);
		
		
		for (Object itemId:allTimeteachingCmbTable.getItemIds()) {
			/* เก็บ id เป็น วัน,ห้องเรียน (index ของวัน ,id ห้องเรียน) */
			final String[] itemIdSplit = itemId.toString().split(",");
			Item item = allTimeteachingCmbTable.getItem(itemId);
			if(itemIdSplit[0].equals(workDay.toString())){
				Label sectionLabel = (Label) item.getItemProperty(Integer.toString((int)section+1)).getValue();
				if(sectionLabel.getId() == null){
					Button addButton = new Button("เลือก", FontAwesome.SAVE);
					addButton.setSizeFull();
					addButton.addClickListener(new ClickListener() {
						private static final long serialVersionUID = 1L;

						@SuppressWarnings("unchecked")
						@Override
						public void buttonClick(ClickEvent event) {
							try{
								timetableContainer.removeAllContainerFilters();
								Object tmpId = timetableContainer.addItem();
								Item timetableItem = timetableContainer.getItem(tmpId);
								timetableItem.getItemProperty(TimetableSchema.SCHOOL_ID).setValue(SessionSchema.getSchoolID());
								timetableItem.getItemProperty(TimetableSchema.CLASS_ROOM_ID).setValue(Integer.parseInt(itemIdSplit[1]));
								timetableItem.getItemProperty(TimetableSchema.TEACHING_ID).setValue(Integer.parseInt(teachingCmb.getValue().toString()));
								timetableItem.getItemProperty(TimetableSchema.SECTION).setValue((int)section);
								timetableItem.getItemProperty(TimetableSchema.WORKING_DAY).setValue((int)workDay);
								timetableItem.getItemProperty(TimetableSchema.SEMESTER).setValue(Integer.parseInt(semester.getValue().toString()));
								timetableItem.getItemProperty(TimetableSchema.ACADEMIC_YEAR).setValue(DateTimeUtil.getBuddishYear());
								CreateModifiedSchema.setCreateAndModified(timetableItem);
								timetableContainer.commit();
								fetchTimetable();
								window.close();
								setTeachingTimetable();
								setAllTimetable();
								Notification.show("บันทึกสำเร็จ", Type.HUMANIZED_MESSAGE);
							} catch (Exception e) {
								Notification.show("บันทึกไม่สำเร็จ", Type.WARNING_MESSAGE);
								e.printStackTrace();
							}
						}
					});
					availableTable.addItem(new Object[]{
							Days.getNameTh(Integer.parseInt(itemIdSplit[0])),
							Integer.toString((int)section+1),
							classRoom.getItem(Integer.parseInt(itemIdSplit[1])).getItemProperty("name").getValue().toString(),
							addButton
					}, itemId);
					
					final CheckBox checkbox = new CheckBox();
					checkbox.setId(itemIdSplit[1]);
					checkbox.addValueChangeListener(new ValueChangeListener() {
						private static final long serialVersionUID = 1L;

						@Override
						public void valueChange(ValueChangeEvent event) {
							if(event.getProperty().getValue() != null){
								if((boolean)event.getProperty().getValue())
									multiRoomIds.add(checkbox.getId());
								else
									multiRoomIds.remove(checkbox.getId());
							}
								
						}
					});
					
					multiRoomTable.addItem(new Object[]{
							checkbox,
							Days.getNameTh(Integer.parseInt(itemIdSplit[0])),
							Integer.toString((int)section+1),
							classRoom.getItem(Integer.parseInt(itemIdSplit[1])).getItemProperty("name").getValue().toString()
					}, itemId);
				}else{
					Button addButton = new Button("เลือก", FontAwesome.SAVE);
					addButton.setSizeFull();
					addButton.addClickListener(new ClickListener() {
						private static final long serialVersionUID = 1L;

						@SuppressWarnings("unchecked")
						@Override
						public void buttonClick(ClickEvent event) {
							try{
								timetableContainer.removeAllContainerFilters();
								Object tmpId = timetableContainer.addItem();
								Item timetableItem = timetableContainer.getItem(tmpId);
								timetableItem.getItemProperty(TimetableSchema.SCHOOL_ID).setValue(SessionSchema.getSchoolID());
								timetableItem.getItemProperty(TimetableSchema.CLASS_ROOM_ID).setValue(Integer.parseInt(itemIdSplit[1]));
								timetableItem.getItemProperty(TimetableSchema.TEACHING_ID).setValue(Integer.parseInt(teachingCmb.getValue().toString()));
								timetableItem.getItemProperty(TimetableSchema.SECTION).setValue((int)section);
								timetableItem.getItemProperty(TimetableSchema.WORKING_DAY).setValue((int)workDay);
								timetableItem.getItemProperty(TimetableSchema.SEMESTER).setValue(Integer.parseInt(semester.getValue().toString()));
								timetableItem.getItemProperty(TimetableSchema.ACADEMIC_YEAR).setValue(DateTimeUtil.getBuddishYear());
								CreateModifiedSchema.setCreateAndModified(timetableItem);
								timetableContainer.commit();
								fetchTimetable();
								window.close();
								setTeachingTimetable();
								setAllTimetable();
								Notification.show("บันทึกสำเร็จ", Type.HUMANIZED_MESSAGE);
							} catch (Exception e) {
								Notification.show("บันทึกไม่สำเร็จ", Type.WARNING_MESSAGE);
								e.printStackTrace();
							}
						}
					});
					combineSubjectTable.addItem(new Object[]{
							Days.getNameTh(Integer.parseInt(itemIdSplit[0])),
							Integer.toString((int)section+1),
							classRoom.getItem(Integer.parseInt(itemIdSplit[1])).getItemProperty("name").getValue().toString(),
							addButton
					}, itemId);
				}
			}
		}
	}
	
	/* ตารางคาบว่างทั้งหมดตาม วันและคาบ ที่กำหนด  */
	private FilterTable initSelectTable(){
		FilterTable availableTable = new FilterTable();
		availableTable.setSizeFull();
		availableTable.setCaption("ตารางว่าง");
		availableTable.setSelectable(true);
		availableTable.setFooterVisible(true);  
		
		availableTable.addContainerProperty(TimetableSchema.WORKING_DAY, String.class, null);
		availableTable.addContainerProperty(TimetableSchema.SECTION, String.class, null);
		availableTable.addContainerProperty(TimetableSchema.CLASS_ROOM_ID, String.class, null);
		availableTable.addContainerProperty("add", Button.class, null);
		
		availableTable.setFilterDecorator(new TableFilterDecorator());
		availableTable.setFilterGenerator(new TableFilterGenerator());
        availableTable.setFilterBarVisible(true);
        
        availableTable.setColumnAlignment(TimetableSchema.WORKING_DAY,Align.CENTER);
		availableTable.setColumnAlignment(TimetableSchema.SECTION,Align.CENTER);
		availableTable.setColumnAlignment(TimetableSchema.CLASS_ROOM_ID,Align.CENTER);
		availableTable.setColumnAlignment("add",Align.CENTER);
		
		availableTable.setColumnHeader(TimetableSchema.WORKING_DAY,"วัน");
		availableTable.setColumnHeader(TimetableSchema.SECTION,"คาบ");
		availableTable.setColumnHeader(TimetableSchema.CLASS_ROOM_ID,"ชั้นเรียน");
		availableTable.setColumnHeader("add","");
		
		availableTable.setVisibleColumns(
				TimetableSchema.WORKING_DAY,
				TimetableSchema.SECTION,
				TimetableSchema.CLASS_ROOM_ID,
				"add");
        
        return availableTable;
	}
	
	/* ตารางคาบว่างทั้งหมดตาม วันและคาบ ที่กำหนด  */
	private FilterTable initSelectMulitiRoomTable(){
		FilterTable availableTable = new FilterTable();
		availableTable.setSizeFull();
		availableTable.setCaption("ตารางว่าง");
		availableTable.setSelectable(true);
		availableTable.setFooterVisible(true);  
		
		availableTable.addContainerProperty(CHECK_BOX_FIELD, CheckBox.class, null);
		availableTable.addContainerProperty(TimetableSchema.WORKING_DAY, String.class, null);
		availableTable.addContainerProperty(TimetableSchema.SECTION, String.class, null);
		availableTable.addContainerProperty(TimetableSchema.CLASS_ROOM_ID, String.class, null);
		
		availableTable.setFilterDecorator(new TableFilterDecorator());
		availableTable.setFilterGenerator(new TableFilterGenerator());
        availableTable.setFilterBarVisible(true);
        
        availableTable.setColumnAlignment(TimetableSchema.WORKING_DAY,Align.CENTER);
		availableTable.setColumnAlignment(TimetableSchema.SECTION,Align.CENTER);
		availableTable.setColumnAlignment(TimetableSchema.CLASS_ROOM_ID,Align.CENTER);
		
		availableTable.setColumnHeader(CHECK_BOX_FIELD,"");
		availableTable.setColumnHeader(TimetableSchema.WORKING_DAY,"วัน");
		availableTable.setColumnHeader(TimetableSchema.SECTION,"คาบ");
		availableTable.setColumnHeader(TimetableSchema.CLASS_ROOM_ID,"ชั้นเรียน");
		
		availableTable.setVisibleColumns(
				CHECK_BOX_FIELD,
				TimetableSchema.WORKING_DAY,
				TimetableSchema.SECTION,
				TimetableSchema.CLASS_ROOM_ID);
        
        return availableTable;
	}
		
	private void removeTimetableLayout(final Object timetableId) {
	        if(timetableId.toString().contains(",")){
	            ConfirmDialog.show(UI.getCurrent(), "ลบตารางสอน", "คุณต้องการลบตารางสอนนี้ใช่หรือไม่?", "ตกลง", "ยกเลิก",  new ConfirmDialog.Listener() {
	            	private static final long serialVersionUID = 1L;
	                public void onClose(ConfirmDialog dialog) {
	                    if(dialog.isConfirmed())
	                        try
	                        {
	                            String timetableIds[] = timetableId.toString().split(",");
	                            String as[];
	                            int j = (as = timetableIds).length;
	                            for(int i = 0; i < j; i++)
	                            {
	                                String id = as[i];
	                                timetableContainer.removeItem(new RowId(Integer.parseInt(id)));
	                                timetableContainer.commit();
	                            }

	                            setTeachingTimetable();
	                            setAllTimetable();
	                            Notification.show("ลบข้อมูลสำเร็จ", Type.HUMANIZED_MESSAGE);
	                        }catch(Exception e){
	                            Notification.show("ลบข้อมูลไม่สำเร็จ", Type.WARNING_MESSAGE);
	                            e.printStackTrace();
	                        }
	                }
	            });
	        }else{
	            final Item timetableItem = timetableContainer.getItem(new RowId(timetableId));
	            if(teachingCmb.getValue().toString().equals(timetableItem.getItemProperty(TimetableSchema.TEACHING_ID).getValue().toString()))
	                ConfirmDialog.show(UI.getCurrent(), "ลบตารางสอน", "คุณต้องการลบตารางสอนนี้ใช่หรือไม่?", "ตกลง", "ยกเลิก",  new ConfirmDialog.Listener() {
						private static final long serialVersionUID = 1L;

						public void onClose(ConfirmDialog dialog)
	                    {
	                        if(dialog.isConfirmed())
	                            try
	                            {
	                                timetableContainer.removeItem(new RowId(timetableId));
	                                timetableContainer.commit();
	                                setTeachingTimetable();
	                                setAllTimetable();
	                                Notification.show("ลบข้อมูลสำเร็จ", Type.HUMANIZED_MESSAGE);
		                        }catch(Exception e){
		                            Notification.show("ลบข้อมูลไม่สำเร็จ", Type.WARNING_MESSAGE);
		                            e.printStackTrace();
		                        }
	                    }
	                });
	            else
	                ConfirmDialog.show(UI.getCurrent(), "วิชาร่วม", "คุณต้องการเพิ่มวิชาร่วมใช่หรือไม่?", "ใช่", "ไม่ใช่", new ConfirmDialog.Listener() {
						private static final long serialVersionUID = 1L;

						@SuppressWarnings("unchecked")
						public void onClose(ConfirmDialog dialog){
	                        if(dialog.isConfirmed())
	                            try{
	                                timetableContainer.removeAllContainerFilters();
	                                Object tmpId = timetableContainer.addItem();
	                                Item timetableTmpItem = timetableContainer.getItem(tmpId);
	                                timetableTmpItem.getItemProperty(TimetableSchema.SCHOOL_ID).setValue(SessionSchema.getSchoolID());
	                                timetableTmpItem.getItemProperty(TimetableSchema.CLASS_ROOM_ID).setValue(timetableItem.getItemProperty(TimetableSchema.CLASS_ROOM_ID).getValue());
	                                timetableTmpItem.getItemProperty(TimetableSchema.TEACHING_ID).setValue(Integer.valueOf(Integer.parseInt(teachingCmb.getValue().toString())));
	                                timetableTmpItem.getItemProperty(TimetableSchema.SECTION).setValue(timetableItem.getItemProperty(TimetableSchema.SECTION).getValue());
	                                timetableTmpItem.getItemProperty(TimetableSchema.WORKING_DAY).setValue(timetableItem.getItemProperty(TimetableSchema.WORKING_DAY).getValue());
	                                timetableTmpItem.getItemProperty(TimetableSchema.SEMESTER).setValue(Integer.valueOf(Integer.parseInt(semester.getValue().toString())));
	                                timetableTmpItem.getItemProperty(TimetableSchema.ACADEMIC_YEAR).setValue(DateTimeUtil.getBuddishYear());
	                                CreateModifiedSchema.setCreateAndModified(timetableTmpItem);
	                                timetableContainer.commit();
	                                fetchTimetable();
	                                setTeachingTimetable();
	                                setAllTimetable();
	                                Notification.show("บันทึกสำเร็จ", Type.HUMANIZED_MESSAGE);
		                        }catch(Exception e){
		                            Notification.show("บันทึกไม่สำเร็จ", Type.WARNING_MESSAGE);
		                            e.printStackTrace();
		                        }
	                    }
	                }
				);
	        }
	    }
	
	/* ค้นหาข้อมูลตารางสอนของอาจารย์ทั้งหมด */
	private void seachTeachingTimetable(){
		teachingCmbs = new HashMap<Object, Object[]>();
		Item teachingCmbItem = teachingCmbContainer.getItem(teachingCmb.getValue());
		freeFormContainer = container.getFreeFormContainer(getTeachingSQL(teachingCmbItem), TimetableSchema.TIMETABLE_ID);
		/* นำข้อมูลที่ได้มาใส่ใน Object โดยในฐานข้อมูลจะเก็บ 1 คาบ 1 แถว แต่มาใส่ในตารางจะต้องมารวมทุกคาบมาเป็นแถวเดียวโดยแยกตามวัน */
		for (Object itemId:freeFormContainer.getItemIds()) {

			Item timetableItem = freeFormContainer.getItem(itemId);
			Object timetableId = timetableItem.getItemProperty(TimetableSchema.TIMETABLE_ID).getValue();
			Object workDay = timetableItem.getItemProperty(TimetableSchema.WORKING_DAY).getValue();
			Object section = timetableItem.getItemProperty(TimetableSchema.SECTION).getValue();
			
			/* ตรวจสอบ ข้อมูลตารางเรียนจาก Key ที่แทนด้วย index ของวัน 0-6
			 *  กรณีมีข้อมูลถูกระบุแล้ว จะทำการดึงข้อมูลแล้วใส่ ตารางใน Value ของ คาบช่วงระหว่าง 0-8
			 *  กรณีไม่มีข้อมูล ก็ทำการเพิ่ม Key ใหม่ โดยกำหนด Value ของคาบช่วงระหว่าง 0-8*/
			if(teachingCmbs.containsKey(workDay)){
				Object timetableIdArray[] = teachingCmbs.get(workDay);
				if(timetableIdArray[(int)section] != null){
					timetableIdArray[(int)section] = timetableIdArray[(int)section] + "," + timetableId;
				}else
					timetableIdArray[(int)section] = timetableId;
				teachingCmbs.put(workDay, timetableIdArray);
			}else{
				Object timetableIdArray[] = new Object[10];
				timetableIdArray[(int)section] = timetableId;
				teachingCmbs.put(workDay, timetableIdArray);
			}
		}
	}
	
	/* ค้นหาข้อมูลตารางสอนทั้งหมด */
	private void seachAllTimetable(){
		timetables = new HashMap<Object, HashMap<Object, Object[]>>();
		freeFormContainer = container.getFreeFormContainer(getAllTimetable(), TimetableSchema.TIMETABLE_ID);
		/* นำข้อมูลที่ได้มาใส่ใน Object โดยในฐานข้อมูลจะเก็บ 1 คาบ 1 แถว แต่มาใส่ในตารางจะต้องมารวมทุกคาบมาเป็นแถวเดียวโดยแยกตามวัน */
		for (Object itemId:freeFormContainer.getItemIds()) {
			Item timetableItem = freeFormContainer.getItem(itemId);
			Object timetableId = timetableItem.getItemProperty(TimetableSchema.TIMETABLE_ID).getValue();
			Object classRoomId = timetableItem.getItemProperty(TimetableSchema.CLASS_ROOM_ID).getValue();
			Object workDay = timetableItem.getItemProperty(TimetableSchema.WORKING_DAY).getValue();
			Object section = timetableItem.getItemProperty(TimetableSchema.SECTION).getValue();

			HashMap<Object, Object[]>  classRoomMap = new HashMap<Object, Object[]>();
			
			/* ตรวจสอบว่ามีการกำหนด ตารางสอนใน timetables Map ด้วย index วัน
			 *  กรณีมี ก็ให้ไปหา classRooms Map 
			 *  กรณีไม่มี ก็ทำการกำหนดค่า Default ของ Map ทั้งหมด */
			if(timetables.containsKey(workDay)){
				classRoomMap = timetables.get(workDay);
				/* ตรวจสอบว่ามีการกำหนดใน classRooms Map  หรือยัง
				 *  กรณีมี ก็ให้กำหนดค่า timetableId ของแต่ละค่าบ
				 *  กรณีไม่มี ก็ทำการกำหนดค่าของ teachingCmb Map */
				
				if(classRoomMap.containsKey(classRoomId)){
					Object timetableIdArray[] = classRoomMap.get(classRoomId);
					
					if(timetableIdArray[(int)section] != null){
						timetableIdArray[(int)section] = timetableIdArray[(int)section]+ "," + timetableId;
					}else
						timetableIdArray[(int)section] = timetableId;
					classRoomMap.put(classRoomId, timetableIdArray);					
				}else{
					Object timetableIdArray[] = new Object[10];
					timetableIdArray[(int)section] = timetableId;
					classRoomMap.put(classRoomId, timetableIdArray);
				}
				timetables.put(workDay, classRoomMap);
			}else{
				Object timetableIdArray[] = new Object[10];
				timetableIdArray[(int)section] = timetableId;
				classRoomMap.put(classRoomId, timetableIdArray);
				timetables.put(workDay, classRoomMap);
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
	
	/* ดึงข้อมูลการสอนทั้งหมด ของครูผู้สอน */
	private String getTeachingSQL(Item item){
		StringBuilder sql = new StringBuilder();
		if(item.getItemProperty(TeachingSchema.PERSONNEL_ID).getValue() != null){
			/* SQL สำหรับดึงข้อมูล
			* SELECT * FROM timetable tt 
			* INNER JOIN teachingCmb t ON tt.teachingCmb_id=t.teachingCmb_id 
			* INNER JOIN personnel p ON p.personnel_id=t.personnel_id 
			* WHERE p.personnel_id=? 
			* AND tt.school_id = ?*/
					 
			sql.append(" SELECT * FROM " + TimetableSchema.TABLE_NAME +" tt");
			sql.append(" INNER JOIN " + TeachingSchema.TABLE_NAME + " t ON tt." + TeachingSchema.TEACHING_ID + "=" + "t." + TimetableSchema.TEACHING_ID);
			sql.append(" INNER JOIN " + PersonnelSchema.TABLE_NAME + " p ON p." + PersonnelSchema.PERSONNEL_ID + "=" + "t." + TeachingSchema.PERSONNEL_ID);
			sql.append(" WHERE p." + PersonnelSchema.PERSONNEL_ID + "=" + item.getItemProperty(TeachingSchema.PERSONNEL_ID).getValue());
		}else{		
			/*SQL สำหรับดึงข้อมูล
			 * SELECT * FROM timetable tt 
			 * INNER JOIN teachingCmb t ON tt.teachingCmb_id=t.teachingCmb_id 
			 * WHERE t.personnel_name_tmp=?
			 * AND tt.school_id = ?*/
			
			sql.append(" SELECT * FROM " + TimetableSchema.TABLE_NAME +" tt");
			sql.append(" INNER JOIN " + TeachingSchema.TABLE_NAME + " t ON tt." + TeachingSchema.TEACHING_ID + "=" + "t." + TimetableSchema.TEACHING_ID);
			sql.append(" WHERE t." + TeachingSchema.PERSONNEL_NAME_TMP + "='" + item.getItemProperty(TeachingSchema.PERSONNEL_NAME_TMP).getValue()+"'");
		}

		sql.append(" AND tt." + TeachingSchema.SCHOOL_ID + "=" + SessionSchema.getSchoolID());

		return sql.toString();
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
		* INNER JOIN teachingCmb tc ON tc.teachingCmb_id = tt.teachingCmb_id
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

	/* ดึงวันยุดที่เลือก */
	private String[] getDays(){
		//รูปแบบของ Form ที่เลือกจะเก็บในรุปแบบของ [5, 6]
		String daysClosed = days.getValue().toString();
		daysClosed = daysClosed.replace("[", "");
		daysClosed = daysClosed.replace("]", "");
		daysClosed = daysClosed.replace(" ", "");

		String[] daysArray = daysClosed.split(",");
		return daysArray;
	}
	
	/* ตัดคำ จากชื่อของผู้สอน ซึ่งอยู่ในรูปของ 
	 *  ท1101:ภาษาไทย1 (อ.ทดลอง ทดสอบ) หรือ
	 *  แนะแนว (อ.ทดลอง ทดสอบ) 
	 *  ให้อยู่ในรํปของ
	 *    - ถ้าไม่มีรหัสวิชา แนะแนว  
	 *    - ถ้ามีรหัสวิชา ท1101
	 */
	private String getTeachingName(String name){
		String styles = "";
		if(name.indexOf(":") == -1)
			styles = name.substring(0, name.indexOf("("))+" \n" +
    				name.substring(name.indexOf("(")+1, name.lastIndexOf(" "));
    	else
    		styles = name.substring(name.indexOf(":")+1, name.indexOf("("))+" \n"  +
    				name.substring(name.indexOf("(")+1, name.lastIndexOf(" "));
		return styles;
	}
	
	/* ตัดคำ จากชื่อของผู้สอน ซึ่งอยู่ในรูปของ 
	 *  ท1101:ภาษาไทย1 (อ.ทดลอง ทดสอบ) หรือ
	 *  แนะแนว (อ.ทดลอง ทดสอบ) 
	 *  ให้อยู่ในรํปของ 
	 *    - กรณีไม่มีรหัสวิชา แสดง  ชื่อวิชา <br/> อ.ทดลอง ทดสอบ
	 *    - ถ้ามีรหัสวิชา แสดง รหัส <br/>  ชื่อวิชา <br/> อ.ทดลอง ทดสอบ */
	private String getTeachingNameHtml(String name){
		String styles = "";
		if(name.indexOf(":") == -1)
			styles = name.substring(0, name.indexOf("("))+"<br/>" +
    				name.substring(name.indexOf("(")+1, name.lastIndexOf(" "));
    	else
    		styles = name.substring(0, name.indexOf(":"))+"<br/>" +
    				name.substring(name.indexOf(":")+1, name.indexOf("("))+"<br/>" +
    				name.substring(name.indexOf("(")+1, name.lastIndexOf(" "));
		return styles;
	}
	
	private void fetchTimetable(){
		timetableContainer.removeAllContainerFilters();
		timetableContainer.addContainerFilter(new And(new Equal(TimetableSchema.SCHOOL_ID, SessionSchema.getSchoolID()),
				new Equal(TimetableSchema.SEMESTER,Integer.parseInt(semesterStr)),
				new Equal(TimetableSchema.ACADEMIC_YEAR, DateTimeUtil.getBuddishYear())));
	}
}
