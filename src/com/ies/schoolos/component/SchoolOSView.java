package com.ies.schoolos.component;

import java.lang.reflect.Constructor;

import javax.servlet.http.Cookie;

import org.vaadin.activelink.ActiveLink;
import org.vaadin.activelink.ActiveLink.LinkActivatedEvent;
import org.vaadin.activelink.ActiveLink.LinkActivatedListener;
import org.vaadin.dialogs.ConfirmDialog;

import com.ies.schoolos.LoginView;
import com.ies.schoolos.component.fundamental.BehaviorView;
import com.ies.schoolos.component.fundamental.BuildingView;
import com.ies.schoolos.component.fundamental.ClassRoomView;
import com.ies.schoolos.component.fundamental.DepartmentView;
import com.ies.schoolos.component.fundamental.SubjectView;
import com.ies.schoolos.component.personnel.EditPersonnelView;
import com.ies.schoolos.component.personnel.PersonnelGraduatedHistoryView;
import com.ies.schoolos.component.registration.EditStudentView;
import com.ies.schoolos.component.setting.SchoolView;
import com.ies.schoolos.container.Container;
import com.ies.schoolos.schema.SessionSchema;
import com.ies.schoolos.schema.UserSchema;
import com.ies.schoolos.utility.BCrypt;
import com.ies.schoolos.utility.Notification;
import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.sqlcontainer.RowId;
import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.event.LayoutEvents.LayoutClickListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.VaadinService;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Label;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.Command;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Panel;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Window;

public class SchoolOSView extends HorizontalSplitPanel{
	private static final long serialVersionUID = 1L;
	
	private boolean isSplit = true;

	private String passwordHash = null;
	private Item userItem = null;
	
	private FieldGroup userBinder;
	
	private Component currentComponent;
	
	private SQLContainer userContainer = Container.getUserContainer();
	
	/* เนื้อหา */
	private VerticalLayout rightLayout;
	private GridLayout headerLayout;
	private Button menu;
	private MenuBar menuBar;
	private Label branding;
	
	/* ข้อมูลส่วนตัว */
	private TextField firstname;
	private TextField lastname;
	private TextField email;
	private ActiveLink passwordChange;
	
	public SchoolOSView() {
		buildMainLayout();
	}
	
	private void buildMainLayout(){
		userItem = userContainer.getItem(new RowId(SessionSchema.getUserID()));
		
		setSizeFull();
        setSplitPosition(200, Unit.PIXELS);
        showOrHideMenu();
        
        initLeftMenuLayout();
        initRightContentLayout();
        initComponent(new AcademicMainView());
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
		
		Button academic = new Button("ฝ่ายวิชาการ", FontAwesome.BOOK);
		academic.setWidth("100%");
		menuBoxContent.addComponent(academic);
		menuBoxContent.setComponentAlignment(academic, Alignment.MIDDLE_LEFT);
		initMenu(academic, AcademicMainView.class);
		
		Button registration = new Button("ฝ่ายทะเบียน", FontAwesome.PENCIL_SQUARE_O);
		registration.setWidth("100%");
		menuBoxContent.addComponent(registration);
		menuBoxContent.setComponentAlignment(registration, Alignment.MIDDLE_LEFT);
		initMenu(registration, RegistrationMainView.class);
		
		Button studentAffairs = new Button("กิจการนักเรียน", FontAwesome.LEGAL);
		studentAffairs.setWidth("100%");
		menuBoxContent.addComponent(studentAffairs);
		menuBoxContent.setComponentAlignment(studentAffairs, Alignment.MIDDLE_LEFT);
		initMenu(studentAffairs, StudentAffairsMainView.class);
		
		Button admin = new Button("ผู้ดูแลระบบ", FontAwesome.DESKTOP);
		admin.setWidth("100%");
		menuBoxContent.addComponent(admin);
		menuBoxContent.setComponentAlignment(admin, Alignment.MIDDLE_LEFT);
		initMenu(admin, AdminMainView.class);
		
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
		
		Button behavior = new Button("พฤติกรรม", FontAwesome.SHIELD);
		behavior.setWidth("100%");
		fundamentalBoxContent.addComponent(behavior);
		fundamentalBoxContent.setComponentAlignment(behavior, Alignment.MIDDLE_LEFT);
		initMenu(behavior, BehaviorView.class);
		
		/*Button department = new Button("แผนก", FontAwesome.SITEMAP);
		department.setWidth("100%");
		fundamentalBoxContent.addComponent(department);
		fundamentalBoxContent.setComponentAlignment(department, Alignment.MIDDLE_LEFT);
		initMenu(department, DepartmentView.class);*/
		
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
		
		MenuItem menuItem = menuBar.addItem(SessionSchema.getFirstname().toString(), null, null);
		menuItem.setEnabled(true);
		menuItem.setIcon(FontAwesome.USER);
		menuItem.addItem("บัญชีผุ้ใช้", null, new Command() {
			private static final long serialVersionUID = 1L;

			@Override
			public void menuSelected(MenuItem selectedItem) {
				initUserLayout();
				initialDataBinding();
			}
		});	
		if(!userItem.getItemProperty(UserSchema.REF_USER_TYPE).getValue().toString().equals("0")){
			System.err.println(userItem.getItemProperty(UserSchema.REF_USER_TYPE).getValue());
			menuItem.addItem("ข้อมูลส่วนตัว", null, new Command() {
				private static final long serialVersionUID = 1L;

				@Override
				public void menuSelected(MenuItem selectedItem) {
					initUserInfoLayout();
				}
			});	
			menuItem.addItem("ข้อมูลการศึกษา", null, new Command() {
				private static final long serialVersionUID = 1L;

				@Override
				public void menuSelected(MenuItem selectedItem) {
					initUserGraduatedHistoryLayout();
				}
			});	
		}
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
	
	/* หน้าต่างแก้บัญชีผู้ใช้ */
	private void initUserLayout(){
		Window userWD = new Window("บัญชีผู้ใช้");
		userWD.setWidth("50%");
		userWD.setHeight("60%");
		userWD.center();
		UI.getCurrent().addWindow(userWD);
		
		VerticalLayout formLayout = new VerticalLayout();
		formLayout.setSizeFull();
		formLayout.setSpacing(true);
		formLayout.setMargin(true);
		userWD.setContent(formLayout);
		
		FormLayout schoolForm = new FormLayout();
		schoolForm.setSizeFull();
		schoolForm.setStyleName("border-white");
		formLayout.addComponent(schoolForm);
				
		firstname = new TextField("ชื่อผู้ดูแลระบบ");
		firstname.setRequired(true);
		firstname.setInputPrompt("ชื่อจริง");
		schoolForm.addComponent(firstname);
		
		lastname = new TextField("สกุลผู้ดูแลระบบ");
		lastname.setRequired(true);
		lastname.setInputPrompt("นามสกุล");
		schoolForm.addComponent(lastname);

		email = new TextField("อีเมล์");
		email.setRequired(true);
		email.setEnabled(false);
		email.setInputPrompt("อีเมล์");
		schoolForm.addComponent(email);
		
		passwordChange = new ActiveLink();
		passwordChange.setCaption("เปลี่ยนรหัสผ่าน");
		passwordChange.addListener(new LinkActivatedListener() {
            private static final long serialVersionUID = -7680743472997645381L;

            public void linkActivated(LinkActivatedEvent event) {
              	Window passwordWD = new Window();
              	passwordWD.setWidth("400px");
              	passwordWD.setHeight("300px");
              	passwordWD.center();
              	passwordWD.setCaption("เปลี่ยนรหัสผ่าน");
              	UI.getCurrent().addWindow(passwordWD);
              	
              	VerticalLayout formLayout = new VerticalLayout();
        		formLayout.setSizeFull();
        		formLayout.setSpacing(true);
        		formLayout.setMargin(true);
              	passwordWD.setContent(formLayout);
        		
        		FormLayout schoolForm = new FormLayout();
        		schoolForm.setSizeFull();
        		schoolForm.setStyleName("border-white");
        		
              	FormLayout passwordForm = new FormLayout();
              	passwordForm.setSizeFull();
              	formLayout.addComponent(passwordForm);
              	
              	final PasswordField password = new PasswordField("รหัสผ่าน");
              	password.setRequired(true);
              	password.setInputPrompt("รหัสผ่าน");
              	password.addValueChangeListener(new ValueChangeListener() {
					private static final long serialVersionUID = 1L;

					@Override
					public void valueChange(ValueChangeEvent event) {
						passwordHash = BCrypt.hashpw(event.getProperty().getValue().toString(), BCrypt.gensalt());
					}
				});
              	passwordForm.addComponent(password);
              	
              	final PasswordField passwordAgain = new PasswordField("รหัสผ่านอีกครั้ง");
              	passwordAgain.setRequired(true);
              	passwordAgain.setInputPrompt("รหัสผ่าน");
              	passwordForm.addComponent(passwordAgain);
              	
              	Button savePass = new Button("ตกลง", FontAwesome.SAVE);
              	savePass.addClickListener(new ClickListener() {
					private static final long serialVersionUID = 1L;
					@SuppressWarnings("unchecked")
					@Override
					public void buttonClick(ClickEvent event) {
						if(password.getValue().equals(passwordAgain.getValue())){
							userItem.getItemProperty(UserSchema.PASSWORD).setValue(passwordHash);
							try {
								userBinder.commit();
								userContainer.commit();
								
								Cookie emailCookie = new Cookie(SessionSchema.EMAIL, SessionSchema.getEmail().toString());
								emailCookie.setMaxAge(12000);
								emailCookie.setPath(VaadinService.getCurrentRequest().getContextPath());
								VaadinService.getCurrentResponse().addCookie(emailCookie);
								
								Cookie passwordCookie = new Cookie(SessionSchema.PASSWORD, passwordHash);
								passwordCookie.setMaxAge(12000);
								passwordCookie.setPath(VaadinService.getCurrentRequest().getContextPath());
								VaadinService.getCurrentResponse().addCookie(passwordCookie);
								
								Notification.show("บันทึกสำเร็จ", Type.HUMANIZED_MESSAGE);
							} catch (Exception e) {
								Notification.show("บันทึกไม่สำเร็จ", Type.WARNING_MESSAGE);
								e.printStackTrace();
							}
						}else{
							Notification.show("รหัสผ่านไม่ตรงกัน กรุณาระบุใหม่อีกครั้ง", Type.WARNING_MESSAGE);
						}
					}
				});
              	passwordForm.addComponent(savePass);
              	
            }
        });
		schoolForm.addComponent(passwordChange);
		
		Button userSave = new Button("บันทึก",FontAwesome.SAVE);
		userSave.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event) {
				try {
					if(userBinder.isValid()){
						userBinder.commit();
						userContainer.commit();
						email.setReadOnly(true);
						Notification.show("บันทึกสำเร็จ", Type.HUMANIZED_MESSAGE);
					}else{
						Notification.show("กรุณากรอกข้อมูลให้ครบถ้วน", Type.WARNING_MESSAGE);
					}
				} catch (Exception e) {
					Notification.show("บันทึกไม่สำเร็จ กรุณาลองใหม่อีกครั้งค่ะ", Type.ERROR_MESSAGE);
					e.printStackTrace();
				}
			}
		});
		schoolForm.addComponent(userSave);
	}

	/* หน้าต่างแก้ข้อมูลผู้ใช้ */
	private void initUserInfoLayout(){
		Window userWD = new Window("ข้อมูลผู้ใช้");
		userWD.setSizeFull();
		userWD.center();
		if(userItem.getItemProperty(UserSchema.REF_USER_TYPE).getValue().equals("1"))
			userWD.setContent(new EditPersonnelView(userItem.getItemProperty(UserSchema.REF_USER_ID).getValue()));
		else if(userItem.getItemProperty(UserSchema.REF_USER_TYPE).getValue().equals("2"))
			userWD.setContent(new EditStudentView(userItem.getItemProperty(UserSchema.REF_USER_ID).getValue()));
		UI.getCurrent().addWindow(userWD);
	}
	
	/* หน้าต่างแก้ข้อมูลผู้ใช้ */
	private void initUserGraduatedHistoryLayout(){
		Window userWD = new Window("ข้อมูลการศึกษา");
		userWD.setSizeFull();
		userWD.center();
		if(userItem.getItemProperty(UserSchema.REF_USER_TYPE).getValue().equals("1"))
			userWD.setContent(new PersonnelGraduatedHistoryView(userItem.getItemProperty(UserSchema.REF_USER_ID).getValue()));
		UI.getCurrent().addWindow(userWD);
	}
	
	private void initialDataBinding(){
		userBinder = new FieldGroup(userItem);
		userBinder.setBuffered(true);
		userBinder.bind(firstname, UserSchema.FIRSTNAME);
		userBinder.bind(lastname, UserSchema.LASTNAME);
		userBinder.bind(email, UserSchema.EMAIL);
		
		email.setReadOnly(true);
	}
	
	/* ออกจากระบบ */
	private void logout(){
		Cookie emailCookie = new Cookie(SessionSchema.EMAIL, "");
		emailCookie.setMaxAge(0);
		emailCookie.setPath(VaadinService.getCurrentRequest().getContextPath());
		VaadinService.getCurrentResponse().addCookie(emailCookie);
		
		Cookie passwordCookie = new Cookie(SessionSchema.PASSWORD, "");
		passwordCookie.setMaxAge(0);
		passwordCookie.setPath(VaadinService.getCurrentRequest().getContextPath());
		VaadinService.getCurrentResponse().addCookie(passwordCookie);
		
		resetSession();
		
		UI ui = UI.getCurrent();
    	ui.setContent(new LoginView());
	}
	
	/* ตั้งค่า Session */
	private void resetSession(){
		UI.getCurrent().getSession().setAttribute(SessionSchema.SCHOOL_ID, null);
		UI.getCurrent().getSession().setAttribute(SessionSchema.SCHOOL_NAME, null);
		UI.getCurrent().getSession().setAttribute(SessionSchema.USER_ID, null);
		UI.getCurrent().getSession().setAttribute(SessionSchema.FIRSTNAME, null);
		UI.getCurrent().getSession().setAttribute(SessionSchema.EMAIL, null);
	}
	
}
