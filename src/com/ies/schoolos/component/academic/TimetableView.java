package com.ies.schoolos.component.academic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.tepi.filtertable.FilterTable;
import org.vaadin.dialogs.ConfirmDialog;

import com.ies.schoolos.container.Container;
import com.ies.schoolos.container.DbConnection;
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
import com.vaadin.data.util.sqlcontainer.RowId;
import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.data.util.sqlcontainer.query.FreeformQuery;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CustomTable.Align;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Window;

public class TimetableView extends VerticalLayout {

	private static final long serialVersionUID = 1L;

	/* ที่เก็บข้อมูลตารางสอน อาจารย์ในแต่ละคาบ มารวมเป็น แถวเดียว 
	 *  Object แสดงถึง Index ของวัน 
     *  Object[] แสดง index ของคาบ โดยภายในเก็บ timetableId */
	private HashMap<Object, Object[]> teachings;
	/* ที่เก็บข้อมูลตารางสอนทั้งหมด ในแต่ห้องและคาบ มารวมเป็น แถวเดียว แยกตามวัน
	 *   Object แสดงถึง Index ของวัน 
	 *   HashMap<Object, Object[]> 
	 *     > Object แสดงถึง Id ของห้องเรียน
	 *     > Object[] แสดง index ของคาบ โดยภายในเก็บ timetableId */
	private HashMap<Object, HashMap<Object, Object[]>> timetables;

	private SQLContainer teachingContainer = Container.getTeachingContainer();
	private SQLContainer classRoomContainer = Container.getClassRoomContainer();
	private SQLContainer timetableContainer = Container.getTimetableContainer();
	private SQLContainer freeFormContainer;
	
	private FormLayout settingForm;
	private ComboBox classYear;
	private ComboBox semester;
	private OptionGroup days;
	private ComboBox teaching;
	
	private FilterTable teachingTable;
	private FilterTable allTimeteachingTable;
	
	public TimetableView() {
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
				if(event.getProperty().getValue() != null &&
						semester.getValue() != null){
					teaching.setContainerDataSource(new Teaching(event.getProperty().getValue(), semester.getValue(),Utility.sortOptionGroup(days.getValue())));
				}
				/* ตรวจสอบ เงื่อนไขการค้นหาข้อมูล */
				if(days.getValue() != null &&
						teaching.getValue() != null && 
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
				if(event.getProperty().getValue() != null &&
						classYear.getValue() != null){
					teaching.setContainerDataSource(new Teaching(classYear.getValue(), event.getProperty().getValue(),Utility.sortOptionGroup(days.getValue())));
				}
				/* ตรวจสอบ เงื่อนไขการค้นหาข้อมูล */
				if(classYear.getValue() != null &&
						days.getValue() != null &&
						teaching.getValue() != null
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
					teaching.setContainerDataSource(new Teaching(classYear.getValue(), semester.getValue(), Utility.sortOptionGroup(event.getProperty().getValue())));
				}
				
				/* ตรวจสอบ เงื่อนไขการค้นหาข้อมูล */
				if(classYear.getValue() != null &&
						semester.getValue() != null && 
						teaching.getValue() != null &&
						event.getProperty().getValue() != null){
					setTeachingTimetable();
					setAllTimetable();
				}
			}
		});
        settingForm.addComponent(days);
        
        teaching = new ComboBox("ผู้สอน");
        teaching.setRequired(true);
        teaching.setInputPrompt("เลือกข้อมูล");
        teaching.setItemCaptionPropertyId("name");
        teaching.setImmediate(true);
		teaching.setNullSelectionAllowed(false);
		teaching.setFilteringMode(FilteringMode.CONTAINS);
		teaching.addValueChangeListener(new ValueChangeListener() {
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
		settingForm.addComponent(teaching);
		
		teachingTable = new FilterTable();
		teachingTable.setWidth("100%");
		teachingTable.setHeight("400px");
		teachingTable.setCaption("ตารางสอนอาจารย์");
		teachingTable.setSelectable(true);
		teachingTable.setFooterVisible(true);  
		setTableStyle(teachingTable);
		teachingLayout.addComponent(teachingTable);
		teachingLayout.setExpandRatio(teachingTable,(float) 2.9);
		teachingTable.setFilterDecorator(new TableFilterDecorator());
		teachingTable.setFilterGenerator(new TableFilterGenerator());
        teachingTable.setFilterBarVisible(true);
        
        allTimeteachingTable = new FilterTable();
		allTimeteachingTable.setWidth("100%");
		allTimeteachingTable.setCaption("ตารางรวม");
		allTimeteachingTable.setSelectable(true);
		allTimeteachingTable.setFooterVisible(true);  
		setTableStyle(allTimeteachingTable);
		allTimeteachingTable.setFilterDecorator(new TableFilterDecorator());
		allTimeteachingTable.setFilterGenerator(new TableFilterGenerator());
        allTimeteachingTable.setFilterBarVisible(true);
        addComponent(allTimeteachingTable);
        setExpandRatio(allTimeteachingTable, 2);
	}
	
	private void setTableStyle(FilterTable table){
		if(table != allTimeteachingTable){
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
		teachingTable.removeAllItems();
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
			if(teachings.size() > 0){
				/* ตรวจสอบว่ามีการใส่ค่าของ index วัน บน Map หรือยัง
				 *  ถ้าใส่แล้วก็ดึงข้อมูลเก่ามาอัพเดทคาบเรียน ให้ครบทุกคาบ 
				 *  ถ้ายังไม่ใส่ ก็มาใส่ปุ่มสีเขียว โดยตรวจสอบว่าเป็นวันหยุดหรือไม่ */
				if(teachings.containsKey(weekDay)){
					if(!Arrays.asList(daysClosed).contains(Integer.toString(weekDay))){
						data.add(Days.getNameTh(weekDay));

						Object timetableIdArray[] = teachings.get(weekDay);
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
								final Object timetableId = timetableIdArray[j];
								Item timetableItem = timetableContainer.getItem(new RowId(timetableId));
								Item classRoomItem = classRoomContainer.getItem(new RowId(timetableItem.getItemProperty(TimetableSchema.CLASS_ROOM_ID).getValue()));
								
								/* แสดง ชื่ออาจารย์ /n ห้องเรียน*/
								String caption = getTeachingName(new Teaching().
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
							}
							data.add(button);
						}
						teachingTable.addItem(data.toArray(),weekDay);
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
					teachingTable.addItem(data.toArray(),weekDay);
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
					teachingTable.addItem(data.toArray(),weekDay);
				}
			}
		}
		
	}
	
	/* เพิ่มข้อมูลตารางสอนทั้มหงด */
	private void setAllTimetable(){
		allTimeteachingTable.removeAllItems();
		seachAllTimetable();
		
		/* วนลูบเรียงตามวัน อาทิตย์ ถึง เสาร์ */
		for (int i=0; i < 7;i++) {
			final int weekDay = i;
			/* ดึงห้องเรียนทั้งหมดในปีการศึกษา */
			for(Object itemId:searchClassRoomLessonPlan().getItemIds()){
				Object classRoomId = searchClassRoomLessonPlan().getItem(itemId).getItemProperty(ClassRoomLessonPlanSchema.CLASS_ROOM_ID).getValue();
				
				ArrayList<Object> data = new ArrayList<Object>();	
				data.add(Days.getNameTh(weekDay));
				data.add(new ClassRoom().getItem(classRoomId).getItemProperty("name").getValue());
				
				/* ตรวจสอบจำนวนข้อมูลตารางสอนที่พบ
				 *  กรณีพบตารางสอนที่เคยใส่ก่อนหน้า จะตรวจคาบใหนที่กำหนดแล้ว (สีแดง) หรือ ยังไม่กำหนด (เขียว) 
				 *  กรณีไม่พบตารางสอน จะใส่ปุ่มสีเขียวทั้งหมด */
				if(timetables.size() > 0){
					if(timetables.containsKey(weekDay)){
						HashMap<Object, Object[]> teachingsTmp = timetables.get(weekDay);
						/* ตรวจสอบว่าวันดัวกล่าวมีการกำหนดคาบอย่างน้อยหนึ่งคาบหรือไม่
						 *  กรณีมีการกำหนด ก็จะทำการกดหนด ตาบสีแดง ในวันดังกล่าง
						 *  กรณีไม่มีการกำหนดคาบได ๆ เลย จะกำหนด สีเขียวทั้งหมดของห้องดังกล่าว */
						if(teachingsTmp.containsKey(classRoomId)){	

							Object timetableIdArray[] = teachingsTmp.get(classRoomId);
							/* วนลูบจำนวนคาบ 9 คาบ */
							for(int j=0; j < 10; j++){
								Label lable = new Label();
								lable.setWidth("90px");
								lable.setHeight("100%");
								lable.setContentMode(ContentMode.HTML);
								/* ตรวจสอบว่า คาบดังกล่่าวถูกระบุใว้หรือยัง
								 *  กรณียังว่าง จะกำหนด Caption เป็น "ว่าง"
								 *  กรณี ระบุ จะกำหนด Caption เป็นชื่อวิชา (อจ) พร้อมตั้งค่า id บนปุ่ม*/
								if(timetableIdArray[j] == null){
									lable.setValue("ว่าง");
									lable.setStyleName("green-label");
								}else{
									Object timetableId = timetableIdArray[j];

									Item timetableItem = timetableContainer.getItem(new RowId(timetableId));
									String caption = new Teaching().
											getItem(new RowId(timetableItem.getItemProperty(TimetableSchema.TEACHING_ID).getValue())).
											getItemProperty("name").getValue().toString();
									lable.setValue(getTeachingNameHtml(caption));
									lable.setId(timetableId.toString());
									lable.setStyleName("red-label");
								}
								data.add(lable);
							}
						}else{
							for(int j=0; j < 10; j++){
								Label lable = new Label("ว่าง");
								lable.setWidth("90px");
								lable.setHeight("100%");
								lable.setStyleName("green-label");
								data.add(lable);
							}
						}
					}else{
						for(int j=0; j < 10; j++){
							Label lable = new Label("ว่าง");
							lable.setWidth("90px");
							lable.setHeight("100%");
							lable.setStyleName("green-label");
							data.add(lable);
						}
					}

					/* เก็บ id เป็น วัน,ห้องเรียน (index ของวัน ,id ห้องเรียน) */
					allTimeteachingTable.addItem(data.toArray(),i+","+classRoomId.toString());
				}else{
					for(int j=0; j < 10; j++){
						Label lable = new Label("ว่าง");
						lable.setWidth("90px");
						lable.setHeight("100%");
						lable.setStyleName("green-label");
						data.add(lable);
					}
					/* เก็บ id เป็น วัน,ห้องเรียน (index ของวัน ,id ห้องเรียน) */
					allTimeteachingTable.addItem(data.toArray(),i+","+classRoomId.toString());
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
		
		FilterTable availableTable = initAvailableTable();
		window.setContent(availableTable);
		for (Object itemId:allTimeteachingTable.getItemIds()) {
			/* เก็บ id เป็น วัน,ห้องเรียน (index ของวัน ,id ห้องเรียน) */
			final String[] itemIdPlit = itemId.toString().split(",");
			Item item = allTimeteachingTable.getItem(itemId);
			if(itemIdPlit[0].equals(workDay.toString())){
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
								timetableItem.getItemProperty(TimetableSchema.CLASS_ROOM_ID).setValue(Integer.parseInt(itemIdPlit[1]));
								timetableItem.getItemProperty(TimetableSchema.TEACHING_ID).setValue(Integer.parseInt(teaching.getValue().toString()));
								timetableItem.getItemProperty(TimetableSchema.SECTION).setValue((int)section);
								timetableItem.getItemProperty(TimetableSchema.WORKING_DAY).setValue((int)workDay);
								CreateModifiedSchema.setCreateAndModified(timetableItem);
								timetableContainer.commit();
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
							Days.getNameTh(Integer.parseInt(itemIdPlit[0])),
							Integer.toString((int)section+1),
							new ClassRoom().getItem(Integer.parseInt(itemIdPlit[1])).getItemProperty("name").getValue().toString(),
							addButton
					}, itemId);
				}
			}
		}
	}
	
	/* ตารางคาบว่างทั้งหมดตาม วันและคาบ ที่กำหนด  */
	private FilterTable initAvailableTable(){
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
	
	/* ลบข้อมูลตารางสอน */
	private void removeTimetableLayout(final Object timetableId){
		ConfirmDialog.show(UI.getCurrent(), "ลบตารางสอน", "คุณต้องการลบตารางสอนนี้ใช่หรือไม่?", "ตกลง", "ยกเลิก",  new ConfirmDialog.Listener() {
			private static final long serialVersionUID = 1L;
			public void onClose(ConfirmDialog dialog) {
                if (dialog.isConfirmed()) {
                	try{
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
            }
        });
	}
	
	/* ค้นหาข้อมูลตารางสอนของอาจารย์ทั้งหมด */
	private void seachTeachingTimetable(){
		teachings = new HashMap<Object, Object[]>();
		Item teachingItem = teachingContainer.getItem(teaching.getValue());
		freeFormContainer = Container.getFreeFormContainer(getTeachingSQL(teachingItem), TimetableSchema.TIMETABLE_ID);
		/* นำข้อมูลที่ได้มาใส่ใน Object โดยในฐานข้อมูลจะเก็บ 1 คาบ 1 แถว แต่มาใส่ในตารางจะต้องมารวมทุกคาบมาเป็นแถวเดียวโดยแยกตามวัน */
		for (Object itemId:freeFormContainer.getItemIds()) {

			Item timetableItem = freeFormContainer.getItem(itemId);
			Object timetableId = timetableItem.getItemProperty(TimetableSchema.TIMETABLE_ID).getValue();
			Object workDay = timetableItem.getItemProperty(TimetableSchema.WORKING_DAY).getValue();
			Object section = timetableItem.getItemProperty(TimetableSchema.SECTION).getValue();
			
			/* ตรวจสอบ ข้อมูลตารางเรียนจาก Key ที่แทนด้วย index ของวัน 0-6
			 *  กรณีมีข้อมูลถูกระบุแล้ว จะทำการดึงข้อมูลแล้วใส่ ตารางใน Value ของ คาบช่วงระหว่าง 0-8
			 *  กรณีไม่มีข้อมูล ก็ทำการเพิ่ม Key ใหม่ โดยกำหนด Value ของคาบช่วงระหว่าง 0-8*/
			if(teachings.containsKey(workDay)){
				Object timetableIdArray[] = teachings.get(workDay);
				timetableIdArray[(int)section] = timetableId;
				teachings.put(workDay, timetableIdArray);
			}else{
				Object timetableIdArray[] = new Object[10];
				timetableIdArray[(int)section] = timetableId;
				teachings.put(workDay, timetableIdArray);
			}
		}
	}
	
	/* ค้นหาข้อมูลตารางสอนทั้งหมด */
	private void seachAllTimetable(){
		timetables = new HashMap<Object, HashMap<Object, Object[]>>();
		freeFormContainer = Container.getFreeFormContainer(getAllTimetable(), TimetableSchema.TIMETABLE_ID);
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
			FreeformQuery tq = new FreeformQuery(getAllClassRoom(), DbConnection.getConnection(),ClassRoomLessonPlanSchema.CLASS_ROOM_LESSON_PLAN_ID);		
			SQLContainer freeContainer = new SQLContainer(tq);
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
			* INNER JOIN teaching t ON tt.teaching_id=t.teaching_id 
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
			 * INNER JOIN teaching t ON tt.teaching_id=t.teaching_id 
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
    		styles = //name.substring(0, name.indexOf(":"))+" \n"  +
    				name.substring(name.indexOf(":")+1, name.indexOf("("))+" \n"  +
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
}
