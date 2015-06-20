package com.ies.schoolos.component;

import java.lang.reflect.Constructor;
import java.util.ArrayList;

import com.ies.schoolos.component.registration.ResignStudentView;
import com.ies.schoolos.component.registration.StudentClassRoomView;
import com.ies.schoolos.component.registration.StudentListView;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.TabSheet.CloseHandler;
import com.vaadin.ui.TabSheet.Tab;

public class RegistrationMainView extends VerticalLayout {
	private static final long serialVersionUID = 1L;
	
	ArrayList<String> addedTab = new ArrayList<String>();
	
	private GridLayout dashboar;
	private TabSheet tabSheet;
	
	public RegistrationMainView() {
		setSizeFull();
		setSpacing(true);
		
		buildMainLayout();
		
	}
	
	private void buildMainLayout(){
		dashboar = new GridLayout(6, 1);
		dashboar.setSpacing(true);
		dashboar.setWidth("100%");
		addComponent(dashboar);
		
		Button student = new Button("1.ข้อมูลนักเรียน", FontAwesome.CUBE);
		student.setSizeFull();
		dashboar.addComponent(student);
		addClickListener(student, StudentListView.class);
		
		Button studentClassRoom = new Button("2.จัดชั้นเรียน", FontAwesome.CUBE);
		studentClassRoom.setSizeFull();
		dashboar.addComponent(studentClassRoom);
		addClickListener(studentClassRoom, StudentClassRoomView.class);
		
		Button resignRoom = new Button("3.ลาออก-ไล่ออก", FontAwesome.CUBE);
		resignRoom.setSizeFull();
		dashboar.addComponent(resignRoom);
		addClickListener(resignRoom, ResignStudentView.class);
		
		/*Button studentClassRoomForward = new Button("4.เลื่อนชั้นเรียน", FontAwesome.CUBE);
		studentClassRoomForward.setSizeFull();
		dashboar.addComponent(studentClassRoomForward);
		addClickListener(studentClassRoomForward, StudentClassRoomForward.class);*/
		
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
		initTab(new StudentListView(), student.getCaption());
		addedTab.add(student.getCaption());
		
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