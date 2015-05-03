package com.ies.schoolos.component;

import java.lang.reflect.Constructor;
import java.util.ArrayList;

import com.ies.schoolos.component.academic.LessonPlanView;
import com.ies.schoolos.component.academic.TeachingView;
import com.ies.schoolos.component.academic.TeachingtableExportView;
import com.ies.schoolos.component.academic.TimetableExportView;
import com.ies.schoolos.component.academic.TimetableView;
import com.ies.schoolos.component.registration.TeacherHomeroomView;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.CloseHandler;
import com.vaadin.ui.TabSheet.Tab;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

public class AcademicMainView extends VerticalLayout {
	private static final long serialVersionUID = 1L;
	
	ArrayList<String> addedTab = new ArrayList<String>();
	
	private GridLayout dashboar;
	private TabSheet tabSheet;
	
	public AcademicMainView() {
		setSizeFull();
		setSpacing(true);
		
		buildMainLayout();
		
	}
	
	private void buildMainLayout(){
		dashboar = new GridLayout(6, 1);
		dashboar.setSpacing(true);
		dashboar.setWidth("100%");
		addComponent(dashboar);
		
		Button lessonPlan = new Button("1.แผนการเรียน", FontAwesome.CUBE);
		lessonPlan.setSizeFull();
		dashboar.addComponent(lessonPlan);
		addClickListener(lessonPlan, LessonPlanView.class);
		
		Button teaching = new Button("2.กำหนดผู้สอน", FontAwesome.CUBE);
		teaching.setSizeFull();
		dashboar.addComponent(teaching);
		addClickListener(teaching, TeachingView.class);
		
		Button timetable = new Button("3.ตารางสอน", FontAwesome.CUBE);
		timetable.setSizeFull();
		dashboar.addComponent(timetable);
		addClickListener(timetable, TimetableView.class);
		
		Button timetableExcel = new Button("3.1 Excel ตารางเรียน", FontAwesome.FILE_EXCEL_O);
		timetableExcel.setSizeFull();
		dashboar.addComponent(timetableExcel);
		addClickListener(timetableExcel, TimetableExportView.class);
		
		Button studytableExcel = new Button("3.2 Excel ตารางสอน", FontAwesome.FILE_EXCEL_O);
		studytableExcel.setSizeFull();
		dashboar.addComponent(studytableExcel);
		addClickListener(studytableExcel, TeachingtableExportView.class);
		
		Button teacherHomeroomExcel = new Button("4 อาจารย์ประจำชั้น", FontAwesome.CUBE);
		teacherHomeroomExcel.setSizeFull();
		dashboar.addComponent(teacherHomeroomExcel);
		addClickListener(teacherHomeroomExcel, TeacherHomeroomView.class);
		
		tabSheet = new TabSheet();
		tabSheet.setWidth("95%");
		tabSheet.setHeight("100%");
		tabSheet.setStyleName("border-white");
		tabSheet.setCloseHandler(new CloseHandler() {
			private static final long serialVersionUID = 1L;

			@Override
		    public void onTabClose(TabSheet tabsheet, Component tabContent) {
		        Tab tab = tabsheet.getTab(tabContent);
		        tabsheet.removeTab(tab);
		        addedTab.remove(tab.getCaption());
		    }
		});
		initTab(new LessonPlanView(), lessonPlan.getCaption());
		addedTab.add(lessonPlan.getCaption());
		
		addComponent(tabSheet);
		setExpandRatio(tabSheet, 1);
	}
	
	/* กำหนด menu */
	private void addClickListener(final Button button,final Class<?> clazz){
		button.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 1L;
			@Override
			public void buttonClick(ClickEvent event) {
				try {
					Constructor<?> constructor = clazz.getConstructor();
					Component component =(Component) constructor.newInstance();
					/* ตรวจสอบว่ามีเมนูใน ArrayList ไหม
					 *  ถ้าไม่มีก็ให้ทำการสร้างใหม่ แล้วเปิดตัวใหม่*/
					if(!addedTab.contains(button.getCaption())){
						initTab(component, button.getCaption());
						addedTab.add(button.getCaption());
					}
				}catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	private void initTab(Component component, String caption){
		tabSheet.addTab(component, caption , FontAwesome.HOME);
		tabSheet.getTab(component).setClosable(true);
		tabSheet.setSelectedTab(component);
	}
}
