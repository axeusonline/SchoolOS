package com.ies.schoolos.component;

import java.lang.reflect.Constructor;
import java.util.ArrayList;

import com.ies.schoolos.component.recruit.RecruitStudentClassRoomTmpView;
import com.ies.schoolos.component.recruit.RecruitStudentConfirmView;
import com.ies.schoolos.component.recruit.RecruitStudentExamRoom;
import com.ies.schoolos.component.recruit.RecruitStudentExamScore;
import com.ies.schoolos.component.recruit.RecruitStudentListView;
import com.ies.schoolos.component.recruit.RecruitToStudentView;
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

public class RecruitStudentMainView  extends VerticalLayout {
	private static final long serialVersionUID = 1L;
	
	ArrayList<String> addedTab = new ArrayList<String>();
	
	private GridLayout dashboar;
	private TabSheet tabSheet;
	
	public RecruitStudentMainView() {
		setSizeFull();
		setSpacing(true);
		
		buildMainLayout();
		
	}
	
	private void buildMainLayout(){
		dashboar = new GridLayout(6, 1);
		dashboar.setSpacing(true);
		dashboar.setWidth("100%");
		addComponent(dashboar);
		
		Button recruitList = new Button("1.ข้อมูลผู้สมัครเรียน", FontAwesome.CUBE);
		recruitList.setSizeFull();
		dashboar.addComponent(recruitList);
		addClickListener(recruitList, RecruitStudentListView.class);
		
		Button examRoom = new Button("2.จัดห้องสอบ", FontAwesome.CUBE);
		examRoom.setSizeFull();
		dashboar.addComponent(examRoom);
		addClickListener(examRoom, RecruitStudentExamRoom.class);
		
		Button score = new Button("3.ผลคะแนนสอบ", FontAwesome.CUBE);
		score.setSizeFull();
		dashboar.addComponent(score);
		addClickListener(score, RecruitStudentExamScore.class);
		
		Button classRoom = new Button("4.จัดชั้นเรียน", FontAwesome.CUBE);
		classRoom.setSizeFull();
		dashboar.addComponent(classRoom);
		addClickListener(classRoom, RecruitStudentClassRoomTmpView.class);
		
		Button confirm = new Button("5.มอบตัวนักเรียน", FontAwesome.CUBE);
		confirm.setSizeFull();
		dashboar.addComponent(confirm);
		addClickListener(confirm, RecruitStudentConfirmView.class);
		
		Button studentCode = new Button("6.กำหนดรหัสนักเรียน", FontAwesome.CUBE);
		studentCode.setSizeFull();
		dashboar.addComponent(studentCode);
		addClickListener(studentCode, RecruitToStudentView.class);
		
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
		initTab(new RecruitStudentListView(), recruitList.getCaption());
		addedTab.add(recruitList.getCaption());
		
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
