package com.ies.schoolos.component;

import java.lang.reflect.Constructor;
import java.util.HashMap;

import javax.servlet.http.Cookie;

import org.vaadin.dialogs.ConfirmDialog;

import com.ies.schoolos.LoginView;
import com.ies.schoolos.component.fundamental.BuildingView;
import com.ies.schoolos.component.fundamental.ClassRoomView;
import com.ies.schoolos.component.recruit.RecruitStudentConfirmView;
import com.ies.schoolos.component.recruit.RecruitStudentExamRoom;
import com.ies.schoolos.component.recruit.RecruitStudentExamScore;
import com.ies.schoolos.component.recruit.RecruitStudentListView;
import com.ies.schoolos.component.recruit.RecruitStudentClassRoomTmpView;
import com.ies.schoolos.component.recruit.RecruitToStudentView;
import com.ies.schoolos.component.setting.SchoolView;
import com.ies.schoolos.schema.SessionSchema;
import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.event.LayoutEvents.LayoutClickListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.VaadinService;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.AbstractSelect.ItemCaptionMode;
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
import com.vaadin.ui.Tree;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;

public class SchoolOSView extends HorizontalSplitPanel{
	private static final long serialVersionUID = 1L;
	
	private boolean isSplit = true;
	
	/* รหัสเมนู */
	private static int STUDENT_RECRUIT = 10000;
	private static int FUNDAMENTAL = 11000;
	private static int SETTING = 12000;

	private HashMap<Integer, Class<?>> _factoryData; 
	private Component currentComponent;
	
	/* เมนู Slide */
	private Tree menues;
	
	/* เนื้อหา */
	private VerticalLayout rightLayout;
	private GridLayout headerLayout;
	private Button menu;
	private MenuBar menuBar;
	private Label branding;
	
	public SchoolOSView() {
		initMenuComponent();
		buildMainLayout();
		initDefaultComponent();
	}
	
	/* ลงทะเบียนเมนูในแต่ละหมวด */
	private void initMenuComponent(){
		_factoryData = new HashMap<Integer, Class<?>>();
		_factoryData.put(STUDENT_RECRUIT, null);
		_factoryData.put(STUDENT_RECRUIT+1, RecruitStudentListView.class);
		_factoryData.put(STUDENT_RECRUIT+2, RecruitStudentExamRoom.class);
		_factoryData.put(STUDENT_RECRUIT+3, RecruitStudentExamScore.class);
		_factoryData.put(STUDENT_RECRUIT+4, RecruitStudentClassRoomTmpView.class);
		_factoryData.put(STUDENT_RECRUIT+5, RecruitStudentConfirmView.class);
		_factoryData.put(STUDENT_RECRUIT+6, RecruitToStudentView.class);

		_factoryData.put(FUNDAMENTAL, null);
		_factoryData.put(FUNDAMENTAL+1, BuildingView.class);
		_factoryData.put(FUNDAMENTAL+2, ClassRoomView.class);
		
		_factoryData.put(SETTING, null);
		_factoryData.put(SETTING+1, SchoolView.class);
	}
	
	//GENERATE MENUES
	/* ชื่อเมนู */
	private static HierarchicalContainer getMenues() {
        HierarchicalContainer menuContainer = new HierarchicalContainer();
        menuContainer.addContainerProperty("name", String.class, null);
        
        //สมัครเรียน
        initMenu(null, STUDENT_RECRUIT, "สมัครเรียน", menuContainer);
        initMenu(STUDENT_RECRUIT, STUDENT_RECRUIT+1, "ผู้สมัคร", menuContainer); 
        initMenu(STUDENT_RECRUIT, STUDENT_RECRUIT+2, "จัดห้องสอบ", menuContainer); 
        initMenu(STUDENT_RECRUIT, STUDENT_RECRUIT+3, "คะแนนสอบ", menuContainer); 
        initMenu(STUDENT_RECRUIT, STUDENT_RECRUIT+4, "จัดห้องเรียนชั่วคราว", menuContainer); 
        initMenu(STUDENT_RECRUIT, STUDENT_RECRUIT+5, "มอบตัวนักเรียน", menuContainer); 
        initMenu(STUDENT_RECRUIT, STUDENT_RECRUIT+6, "กำหนดรหัสนักเรียน", menuContainer); 
        
        //ข้อมูลพื้นฐาน
        initMenu(null, FUNDAMENTAL, "ข้อมูลพื้นฐาน", menuContainer);
        initMenu(FUNDAMENTAL, FUNDAMENTAL+1, "อาคารเรียน", menuContainer);
        initMenu(FUNDAMENTAL, FUNDAMENTAL+2, "ชั้นเรียน", menuContainer);
        
        //ตั้งค่าระบบ
        initMenu(null, SETTING, "ตั้งค่า", menuContainer);
        initMenu(SETTING, SETTING+1, "ข้อมูลโรงเรียน", menuContainer);
        
        return menuContainer;
    }
	
	private void buildMainLayout(){
		setSizeFull();
        setSplitPosition(200, Unit.PIXELS);
        showOrHideMenu();
        
        initLeftMenuLayout();
        initRightContentLayout();
	}

	/* เมนูซ้ายมือ */
	private void initLeftMenuLayout(){
		 //##### Initial Left Layout ######        
        menues = new Tree();
        menues.setCaption("การทำงาน");
        menues.setSizeFull();
        menues.setStyleName("menu-tree");
        menues.setContainerDataSource(getMenues());
        menues.addValueChangeListener(new ValueChangeListener() {
			private static final long serialVersionUID = 1L;
			@Override
			public void valueChange(com.vaadin.data.Property.ValueChangeEvent event) {
				if(event.getProperty().getValue() != null){
					try {
						/* Initial Object เมนูจาก Class ใน Register Menu */
						Class<?> clazz = Class.forName(_factoryData.get(Integer.parseInt(event.getProperty().getValue().toString())).getName());
						Constructor<?> ctor = clazz.getConstructor();
						Object object = ctor.newInstance();
						
						Panel panel = new Panel();
						panel.setWidth("100%");
						panel.setHeight("580px");
						panel.setStyleName("menu-content");
						
						rightLayout.removeComponent(currentComponent);
						/* แทนค่า Object เมนูจากค่าที่ดึงจากการคลิ๊กเมนู*/
						panel.setContent((Component)object);
						currentComponent = panel;
						rightLayout.addComponent(currentComponent);
						rightLayout.setExpandRatio(currentComponent, 1);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		});
        menues.setImmediate(true);
        menues.setItemCaptionPropertyId("name");
        menues.setItemCaptionMode(ItemCaptionMode.PROPERTY);
        setFirstComponent(menues);
        
        for (final Object id : menues.rootItemIds()) {
        	menues.expandItemsRecursively(id);
        }
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
	
	/* หน้าแรก */
	private void initDefaultComponent(){
		Panel panel = new Panel();
		panel.setWidth("100%");
		panel.setHeight("100%");
		panel.setStyleName("menu-content");
		panel.setContent(new SchoolView());
		
		currentComponent = panel;

		rightLayout.addComponent(currentComponent);
		rightLayout.setExpandRatio(currentComponent, 1);
	}
	
	/* ใส่ค่าเมนูบน Layout */
	@SuppressWarnings("unchecked")
	private static void initMenu(Integer parentId, int itemId, String value, HierarchicalContainer menuContainer){
		Item item = null;
		item = menuContainer.addItem(itemId);
        item.getItemProperty("name").setValue(value);
        menuContainer.setChildrenAllowed(itemId, true);
        if(parentId == null){
            menuContainer.setChildrenAllowed(itemId, true);
        }else{
        	menuContainer.setParent(itemId, parentId);
            menuContainer.setChildrenAllowed(itemId, false);
        }
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
