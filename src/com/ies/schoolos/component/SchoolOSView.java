package com.ies.schoolos.component;

import java.lang.reflect.Constructor;

import javax.servlet.http.Cookie;

import org.vaadin.dialogs.ConfirmDialog;

import com.ies.schoolos.LoginView;
import com.ies.schoolos.component.fundamental.BuildingView;
import com.ies.schoolos.component.fundamental.ClassRoomView;
import com.ies.schoolos.component.fundamental.SubjectView;
import com.ies.schoolos.component.setting.SchoolView;
import com.ies.schoolos.schema.SessionSchema;
import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.event.LayoutEvents.LayoutClickListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.VaadinService;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Label;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.Command;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;

public class SchoolOSView extends HorizontalSplitPanel{
	private static final long serialVersionUID = 1L;
	
	private boolean isSplit = true;
	
	private Component currentComponent;
	
	/* เนื้อหา */
	private VerticalLayout rightLayout;
	private GridLayout headerLayout;
	private Button menu;
	private MenuBar menuBar;
	private Label branding;
	
	public SchoolOSView() {
		buildMainLayout();
	}
	
	private void buildMainLayout(){
		setSizeFull();
        setSplitPosition(200, Unit.PIXELS);
        showOrHideMenu();
        
        initLeftMenuLayout();
        initRightContentLayout();
        initComponent(new RecruitStudentMainView());
	}

	/* เมนูซ้ายมือ */
	private void initLeftMenuLayout(){
		
		/* พื้นที่สำหรับเมนู */
		Panel leftPanel = new Panel();
		leftPanel.setWidth("100%");
		leftPanel.setHeight("-1px");
		leftPanel.setStyleName("menu-left-panel");
		setFirstComponent(leftPanel);
		
		/* พื้นที่สำหรับใส่กล่องเมนู */
		VerticalLayout menuBoxLayout = new VerticalLayout();
		menuBoxLayout.setWidth("100%");	
		menuBoxLayout.setMargin(true);
		menuBoxLayout.setSpacing(true);
		leftPanel.setContent(menuBoxLayout);
		
		Label hearthLabel = new Label("I " + FontAwesome.HEART.getHtml() + " SchoolOS",ContentMode.HTML);
		hearthLabel.setStyleName("heart-red");
		hearthLabel.setSizeFull();
		menuBoxLayout.addComponent(hearthLabel);
		
		/* กล่องเมนู */
		VerticalLayout menuBoxContent = new VerticalLayout();
		menuBoxContent.setCaption("เมนู");
		menuBoxContent.setSizeFull();
		menuBoxContent.setStyleName("menu-box-blue");
		menuBoxLayout.addComponent(menuBoxContent);

		Button recruit = new Button("สมัครเรียน", FontAwesome.GROUP);
		recruit.setWidth("100%");
		menuBoxContent.addComponent(recruit);
		menuBoxContent.setComponentAlignment(recruit, Alignment.MIDDLE_LEFT);
		initMenu(recruit, RecruitStudentMainView.class);
		
		Button personnel = new Button("ฝ่ายบุคคล", FontAwesome.USER);
		personnel.setWidth("100%");
		menuBoxContent.addComponent(personnel);
		menuBoxContent.setComponentAlignment(personnel, Alignment.MIDDLE_LEFT);
		initMenu(personnel, PersonnelMainView.class);
		
		Button accademic = new Button("ฝ่ายวิชาการ", FontAwesome.BOOK);
		accademic.setWidth("100%");
		menuBoxContent.addComponent(accademic);
		menuBoxContent.setComponentAlignment(accademic, Alignment.MIDDLE_LEFT);
		initMenu(accademic, AcademicMainView.class);
		
		/* กล่องข้อมูลพื้นฐาน */
		VerticalLayout fundamentalBoxContent = new VerticalLayout();
		fundamentalBoxContent.setCaption("ข้อมูลพื้นฐาน");
		fundamentalBoxContent.setSizeFull();
		fundamentalBoxContent.setStyleName("menu-box-green");
		menuBoxLayout.addComponent(fundamentalBoxContent);
		
		Button building = new Button("อาคาร", FontAwesome.BUILDING);
		building.setWidth("100%");
		fundamentalBoxContent.addComponent(building);
		fundamentalBoxContent.setComponentAlignment(building, Alignment.MIDDLE_LEFT);
		initMenu(building, BuildingView.class);
		
		Button classRomm = new Button("ชั้นเรียน", FontAwesome.UNIVERSITY);
		classRomm.setWidth("100%");
		fundamentalBoxContent.addComponent(classRomm);
		fundamentalBoxContent.setComponentAlignment(classRomm, Alignment.MIDDLE_LEFT);
		initMenu(classRomm, ClassRoomView.class);
		
		Button subject = new Button("รายวิชาที่สอน", FontAwesome.PENCIL_SQUARE);
		subject.setWidth("100%");
		fundamentalBoxContent.addComponent(subject);
		fundamentalBoxContent.setComponentAlignment(subject, Alignment.MIDDLE_LEFT);
		initMenu(subject, SubjectView.class);
		
		/* ตั้งค่า */
		VerticalLayout settingBoxContent = new VerticalLayout();
		settingBoxContent.setCaption("ตั้งค่าการใช้งาน");
		settingBoxContent.setSizeFull();
		settingBoxContent.setStyleName("menu-box-red");
		menuBoxLayout.addComponent(settingBoxContent);
		
		Button general = new Button("ข้อมูลทั่วไป", FontAwesome.BUILDING);
		general.setWidth("100%");
		settingBoxContent.addComponent(general);
		settingBoxContent.setComponentAlignment(general, Alignment.MIDDLE_LEFT);
		initMenu(general, SchoolView.class);
	}
	
	/* เนื้อหาทางขวามือ */
	private void initRightContentLayout(){
		/* ##### Initial Right Layout ###### */
        rightLayout = new VerticalLayout();
        rightLayout.setSizeFull();
        rightLayout.setStyleName("right-layout");
        rightLayout.addLayoutClickListener(new LayoutClickListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void layoutClick(LayoutClickEvent event) {
				hideMenu();
			}
		});
        setSecondComponent(rightLayout);
        /* ==== Header === */
        headerLayout = new GridLayout();
		headerLayout.setStyleName("header");
		headerLayout.setWidth("100%");
		headerLayout.setHeight("60px");
		headerLayout.setColumns(3);
		headerLayout.setRows(1);
		headerLayout.addLayoutClickListener(new LayoutClickListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void layoutClick(LayoutClickEvent event) {
				hideMenu();
			}
		});
		rightLayout.addComponent(headerLayout);
		
		/* Layout Button ซ้าย Header สำหรับเปิดเมนู */
		HorizontalLayout menuButtonLayout = new HorizontalLayout();
		menuButtonLayout.setSizeFull();
		headerLayout.addComponent(menuButtonLayout);
		
		menu = new Button();
		menu.setIcon(FontAwesome.BARS);
		menu.setStyleName("header-button");
		menu.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 1L;
			@Override
			public void buttonClick(ClickEvent event) {
				showOrHideMenu();
			}
		});
		menuButtonLayout.addComponent(menu);
		menuButtonLayout.setComponentAlignment(menu, Alignment.MIDDLE_LEFT);
		
		/* Layout Branding กลาง Header */
		branding = new Label("SchoolOS");
		branding.setStyleName("branding");
		headerLayout.addComponent(branding);
		
		/* Layout User Info ขวา Header */
		HorizontalLayout menuBarLayout = new HorizontalLayout();
		menuBarLayout.setSizeFull();
		headerLayout.addComponent(menuBarLayout);

		menuBar = new MenuBar();
		menuBar.setStyleName("header-button");
		menuBarLayout.addComponent(menuBar);
		menuBarLayout.setComponentAlignment(menuBar, Alignment.MIDDLE_RIGHT);
		
		MenuItem menuItem = menuBar.addItem(UI.getCurrent().getSession().getAttribute(SessionSchema.FIRSTNAME).toString(), null, null);
		menuItem.setEnabled(true);
		menuItem.setIcon(FontAwesome.USER);
		menuItem.addItem("ออกจากระบบ", null, new Command() {
			private static final long serialVersionUID = 1L;

			@Override
			public void menuSelected(MenuItem selectedItem) {
				ConfirmDialog.show(UI.getCurrent(), "ออกจากระบบ", "คุณต้องการออกจากระบบใช่หรือไม่?","ตกลง","ยกเลิก", new ConfirmDialog.Listener() {
					private static final long serialVersionUID = 1L;
					public void onClose(ConfirmDialog dialog) {
		                if (dialog.isConfirmed()) {
		                	logout();
		                }
		            }
		        });
			}
		});	
	}
	
	/* กำหนด menu */
	private void initMenu(Button button, final Class<?> clazz){
		button.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event) {
				try {
					rightLayout.removeComponent(currentComponent);
					Constructor<?> constructor = clazz.getConstructor();
					Object object = constructor.newInstance();
					initComponent((Component)object);
				}catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	/* กำหนดเมนู Main Content */
	private void initComponent(Component component){
		Panel panel = new Panel();
		panel.setWidth("100%");
		panel.setHeight("100%");
		panel.setStyleName("menu-content");
		panel.setContent(component);
		
		currentComponent = panel;
		
		rightLayout.addComponent(currentComponent);
		rightLayout.setExpandRatio(currentComponent, 1);
	}
	
	/* ซ่อนเมนู */
	private void hideMenu(){
		setSplitPosition(0);
		isSplit = false;
	}
	
	/* ซ่อนหรือแสดงเมนูตามสถานะปัจจุบัน
	 * ใช้กรณีคลิ๊กบนพื้นที่หน้าจอ
	 *  */
	private void showOrHideMenu(){
		if(isSplit){
			setSplitPosition(0);
			isSplit = false;
		}else{
			setSplitPosition(200);
			isSplit = true;
		}
	}
	
	/* ออกจากระบบ */
	private void logout(){
		UI ui = UI.getCurrent();
    	ui.setContent(new LoginView());
    	
    	Cookie emailCookie = new Cookie(SessionSchema.EMAIL, "");
		emailCookie.setMaxAge(0);
		emailCookie.setPath(VaadinService.getCurrentRequest().getContextPath());
		VaadinService.getCurrentResponse().addCookie(emailCookie);
		
		Cookie passwordCookie = new Cookie(SessionSchema.PASSWORD, "");
		passwordCookie.setMaxAge(0);
		passwordCookie.setPath(VaadinService.getCurrentRequest().getContextPath());
		VaadinService.getCurrentResponse().addCookie(passwordCookie);
	}
}
