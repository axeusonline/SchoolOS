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
import com.ies.schoolos.component.fundamental.JobPositionView;
import com.ies.schoolos.component.fundamental.SubjectView;
import com.ies.schoolos.component.info.StudentTimetableView;
import com.ies.schoolos.component.info.TeachingtableView;
import com.ies.schoolos.component.personnel.EditPersonnelView;
import com.ies.schoolos.component.personnel.PersonnelGraduatedHistoryView;
import com.ies.schoolos.component.registration.EditStudentView;
import com.ies.schoolos.component.setting.SchoolView;
import com.ies.schoolos.container.Container;
import com.ies.schoolos.schema.SessionSchema;
import com.ies.schoolos.schema.UserSchema;
import com.ies.schoolos.schema.info.StudentStudySchema;
import com.ies.schoolos.type.Feature;
import com.ies.schoolos.type.UserType;
import com.ies.schoolos.utility.BCrypt;
import com.ies.schoolos.utility.Notification;
import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.filter.Compare.Equal;
import com.vaadin.data.util.sqlcontainer.RowId;
import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.data.validator.EmailValidator;
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

public class SchoolOSView extends VerticalLayout{
	private static final long serialVersionUID = 1L;
	
	private Object userId;	
	private Boolean isFirstTimeStudent;
	private boolean isPassEdited = false;
	private boolean isSplit = true;
	private String passwordHashed = "";
	private Item userItem = null;

	private Container container = new Container();
	private SQLContainer userContainer = container.getUserContainer();
	private SQLContainer studyContainer = container.getStudentStudyContainer();
	
	private String[] permissions;
	
	private FieldGroup userBinder;
	private Component currentComponent;
	
	/* เนื้อหา */
	private HorizontalSplitPanel splitLayout;
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
	private PasswordField password;
	private PasswordField passwordAgain;
	
	public SchoolOSView() {
		userContainer.addContainerFilter(new Equal(UserSchema.USER_ID, SessionSchema.getUserID()));
		userItem = userContainer.getItem(new RowId(SessionSchema.getUserID()));

		if(userItem.getItemProperty(UserSchema.PERMISSION).getValue() != null){
			permissions = userItem.getItemProperty(UserSchema.PERMISSION).getValue().toString().split(",");
			if(userItem.getItemProperty(UserSchema.REF_USER_TYPE).getValue().toString().equals(Integer.toString(UserType.EMPLOYEE))){
				userId = userItem.getItemProperty(UserSchema.REF_USER_ID).getValue();
			}else if(userItem.getItemProperty(UserSchema.REF_USER_TYPE).getValue().toString().equals(Integer.toString(UserType.STUDENT))){
	    		studyContainer.addContainerFilter(new Equal(StudentStudySchema.STUDENT_ID,userItem.getItemProperty(UserSchema.REF_USER_ID).getValue()));  
	    		userId = Integer.parseInt(studyContainer.getIdByIndex(0).toString());
			}
		}
		
		buildMainLayout();
		
		 /* กรณี ไม่มี Permission เลยให้แสดงข้อมูลส่วนตัว */
        if(currentComponent == null){
			if(userItem.getItemProperty(UserSchema.REF_USER_TYPE).getValue().toString().equals(Integer.toString(UserType.ADMIN)))
        		initComponent(new SchoolView());
        	else if(userItem.getItemProperty(UserSchema.REF_USER_TYPE).getValue().toString().equals(Integer.toString(UserType.EMPLOYEE)))
        		initComponent(new EditPersonnelView(userId));
        	else if(userItem.getItemProperty(UserSchema.REF_USER_TYPE).getValue().toString().equals(Integer.toString(UserType.STUDENT))){
        		if(! (boolean) userItem.getItemProperty(UserSchema.IS_EDITED).getValue()){
        			isFirstTimeStudent = true;
        			initUserLayout();
        			initialDataBinding();
        		}else{
        			isFirstTimeStudent = false;        		
        			initComponent(new EditStudentView(userId,false));
        		}
        	}
        }
	}
	
	private void buildMainLayout(){
		setSizeFull();
		
		splitLayout = new HorizontalSplitPanel();
		splitLayout.setSizeFull();
		splitLayout.setSplitPosition(200, Unit.PIXELS);
		addComponent(splitLayout);
		showOrHideMenu();
		
        initRightContentLayout();
        initLeftMenuLayout();
        

        
	}

	/* เมนูซ้ายมือ */
	private void initLeftMenuLayout(){
		/* พื้นที่สำหรับเมนู */
		Panel leftPanel = new Panel();
		leftPanel.setWidth("100%");
		leftPanel.setHeight("-1px");
		leftPanel.setStyleName("menu-left-panel");
		splitLayout.setFirstComponent(leftPanel);
		
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

		Button recruit = new Button(Feature.getNameTh(Feature.RECRUIT_STUDENT), FontAwesome.GROUP);
		recruit.setWidth("100%");
		menuBoxContent.addComponent(recruit);
		menuBoxContent.setComponentAlignment(recruit, Alignment.MIDDLE_LEFT);
		initMenu(recruit, RecruitStudentMainView.class);
		setPermission(Feature.RECRUIT_STUDENT, recruit, RecruitStudentMainView.class);
		
		Button personnel = new Button(Feature.getNameTh(Feature.PERSONNEL), FontAwesome.USER);
		personnel.setWidth("100%");
		menuBoxContent.addComponent(personnel);
		menuBoxContent.setComponentAlignment(personnel, Alignment.MIDDLE_LEFT);
		initMenu(personnel, PersonnelMainView.class);
		setPermission(Feature.PERSONNEL, personnel, PersonnelMainView.class);
		
		Button academic = new Button(Feature.getNameTh(Feature.ACADEMIC), FontAwesome.BOOK);
		academic.setWidth("100%");
		menuBoxContent.addComponent(academic);
		menuBoxContent.setComponentAlignment(academic, Alignment.MIDDLE_LEFT);
		initMenu(academic, AcademicMainView.class);
		setPermission(Feature.ACADEMIC, academic, AcademicMainView.class);
		
		Button registration = new Button(Feature.getNameTh(Feature.REGISTRATION), FontAwesome.PENCIL_SQUARE_O);
		registration.setWidth("100%");
		menuBoxContent.addComponent(registration);
		menuBoxContent.setComponentAlignment(registration, Alignment.MIDDLE_LEFT);
		initMenu(registration, RegistrationMainView.class);
		setPermission(Feature.REGISTRATION, registration, RegistrationMainView.class);
		
		Button studentAffairs = new Button(Feature.getNameTh(Feature.STUDENT_AFFAIRS), FontAwesome.LEGAL);
		studentAffairs.setWidth("100%");
		menuBoxContent.addComponent(studentAffairs);
		menuBoxContent.setComponentAlignment(studentAffairs, Alignment.MIDDLE_LEFT);
		initMenu(studentAffairs, StudentAffairsMainView.class);
		setPermission(Feature.STUDENT_AFFAIRS, studentAffairs, StudentAffairsMainView.class);
		
		Button admin = new Button(Feature.getNameTh(Feature.ADMIN), FontAwesome.DESKTOP);
		admin.setWidth("100%");
		menuBoxContent.addComponent(admin);
		menuBoxContent.setComponentAlignment(admin, Alignment.MIDDLE_LEFT);
		initMenu(admin, AdminMainView.class);
		setPermission(Feature.ADMIN, admin, AdminMainView.class);
		
		if(userItem.getItemProperty(UserSchema.REF_USER_TYPE).getValue().toString().equals(Integer.toString(UserType.EMPLOYEE))){
			Button timetable = new Button("ตารางสอน", FontAwesome.CALENDAR);
			timetable.setWidth("100%");
			timetable.addClickListener(new ClickListener() {
				private static final long serialVersionUID = 1L;

				@Override
				public void buttonClick(ClickEvent event) {
					rightLayout.removeComponent(currentComponent);
					initComponent(new TeachingtableView(userId));
				}
			});
			menuBoxContent.addComponent(timetable);
			menuBoxContent.setComponentAlignment(timetable, Alignment.MIDDLE_LEFT);
		}else if(userItem.getItemProperty(UserSchema.REF_USER_TYPE).getValue().toString().equals(Integer.toString(UserType.STUDENT))){
			/*Button info = new Button("ประวัติ", FontAwesome.USER);
			info.setWidth("100%");
			info.addClickListener(new ClickListener() {
				private static final long serialVersionUID = 1L;

				@Override
				public void buttonClick(ClickEvent event) {
					rightLayout.removeComponent(currentComponent);
					initComponent(new EditStudentView(userId, false));
				}
			});
			menuBoxContent.addComponent(info);
			menuBoxContent.setComponentAlignment(info, Alignment.MIDDLE_LEFT);*/
			

			Button timetable = new Button("ตารางเรียน", FontAwesome.CALENDAR);
			timetable.setWidth("100%");
			timetable.addClickListener(new ClickListener() {
				private static final long serialVersionUID = 1L;

				@Override
				public void buttonClick(ClickEvent event) {
					rightLayout.removeComponent(currentComponent);
					initComponent(new StudentTimetableView(userId));
				}
			});
			menuBoxContent.addComponent(timetable);
			menuBoxContent.setComponentAlignment(timetable, Alignment.MIDDLE_LEFT);
		}
		
		/* กล่องข้อมูลพื้นฐาน */
		VerticalLayout fundamentalBoxContent = new VerticalLayout();
		fundamentalBoxContent.setCaption("ข้อมูลพื้นฐาน");
		fundamentalBoxContent.setSizeFull();
		fundamentalBoxContent.setVisible(false);
		fundamentalBoxContent.setStyleName("menu-box-green");
		menuBoxLayout.addComponent(fundamentalBoxContent);
		
		Button building = new Button("อาคาร", FontAwesome.BUILDING);
		building.setWidth("100%");
		fundamentalBoxContent.addComponent(building);
		fundamentalBoxContent.setComponentAlignment(building, Alignment.MIDDLE_LEFT);
		initMenu(building, BuildingView.class);
		setFundamentalPermission(Feature.RECRUIT_STUDENT, building, fundamentalBoxContent);
		
		Button classRoom = new Button("ชั้นเรียน", FontAwesome.UNIVERSITY);
		classRoom.setWidth("100%");
		fundamentalBoxContent.addComponent(classRoom);
		fundamentalBoxContent.setComponentAlignment(classRoom, Alignment.MIDDLE_LEFT);
		initMenu(classRoom, ClassRoomView.class);
		setFundamentalPermission(Feature.ACADEMIC, classRoom, fundamentalBoxContent);
		
		Button subject = new Button("รายวิชาที่สอน", FontAwesome.PENCIL_SQUARE);
		subject.setWidth("100%");
		fundamentalBoxContent.addComponent(subject);
		fundamentalBoxContent.setComponentAlignment(subject, Alignment.MIDDLE_LEFT);
		initMenu(subject, SubjectView.class);
		setFundamentalPermission(Feature.ACADEMIC, subject, fundamentalBoxContent);
		
		Button behavior = new Button("พฤติกรรม", FontAwesome.SHIELD);
		behavior.setWidth("100%");
		fundamentalBoxContent.addComponent(behavior);
		fundamentalBoxContent.setComponentAlignment(behavior, Alignment.MIDDLE_LEFT);
		initMenu(behavior, BehaviorView.class);
		setFundamentalPermission(Feature.STUDENT_AFFAIRS, behavior, fundamentalBoxContent);
		
		Button department = new Button("แผนก", FontAwesome.SITEMAP);
		department.setWidth("100%");
		fundamentalBoxContent.addComponent(department);
		fundamentalBoxContent.setComponentAlignment(department, Alignment.MIDDLE_LEFT);
		initMenu(department, DepartmentView.class);
		setFundamentalPermission(Feature.PERSONNEL, department, fundamentalBoxContent);
		
		Button jobPosition = new Button("ตำแหน่ง", FontAwesome.STAR);
		jobPosition.setWidth("100%");
		fundamentalBoxContent.addComponent(jobPosition);
		fundamentalBoxContent.setComponentAlignment(jobPosition, Alignment.MIDDLE_LEFT);
		initMenu(jobPosition, JobPositionView.class);
		setFundamentalPermission(Feature.PERSONNEL, jobPosition, fundamentalBoxContent);
		
		if(userItem.getItemProperty(UserSchema.REF_USER_TYPE).getValue().toString().equals("0")){
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
        splitLayout.setSecondComponent(rightLayout);
        
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
				passwordHashed = userItem.getItemProperty(UserSchema.PASSWORD).getValue().toString();
				initUserLayout();
				initialDataBinding();
			}
		});	
		if(!userItem.getItemProperty(UserSchema.REF_USER_TYPE).getValue().toString().equals(Integer.toString(UserType.ADMIN))){

			menuItem.addItem("ข้อมูลส่วนตัว", null, new Command() {
				private static final long serialVersionUID = 1L;

				@Override
				public void menuSelected(MenuItem selectedItem) {
					if(isFirstTimeStudent == null)
						initUserInfoLayout();
					else{
						if(!isFirstTimeStudent)
							initUserInfoLayout();
						else
							Notification.show("กรุณากำหนดบัญชีผู้ใช้และรหัสผ่าน", Type.WARNING_MESSAGE);						
					}
				}
			});	
			if(!userItem.getItemProperty(UserSchema.REF_USER_TYPE).getValue().toString().equals("2")){
				menuItem.addItem("ข้อมูลการศึกษา", null, new Command() {
					private static final long serialVersionUID = 1L;

					@Override
					public void menuSelected(MenuItem selectedItem) {
						initUserGraduatedHistoryLayout();
					}
				});	
			}
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
		splitLayout.setSplitPosition(0);
		isSplit = false;
	}
	
	/* ซ่อนหรือแสดงเมนูตามสถานะปัจจุบัน
	 * ใช้กรณีคลิ๊กบนพื้นที่หน้าจอ
	 *  */
	private void showOrHideMenu(){
		if(isSplit){
			splitLayout.setSplitPosition(0);
			isSplit = false;
		}else{
			splitLayout.setSplitPosition(200);
			isSplit = true;
		}
	}
	
	/* หน้าต่างแก้บัญชีผู้ใช้ */
	private void initUserLayout(){
		Window userWD = new Window("บัญชีผู้ใช้");
		userWD.setWidth("50%");
		userWD.setHeight("70%");
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
				
		firstname = new TextField("ชื่อผู้ใช้งาน");
		firstname.setRequired(true);
		firstname.setInputPrompt("ชื่อจริง");
		schoolForm.addComponent(firstname);
		
		lastname = new TextField("สกุลผู้ใช้งาน");
		lastname.setRequired(true);
		lastname.setInputPrompt("นามสกุล");
		schoolForm.addComponent(lastname);

		email = new TextField("อีเมล์");
		email.setRequired(true);
		email.setInputPrompt("อีเมล์");
		email.setNullRepresentation("");
		email.addValidator(new EmailValidator("ข้อมูลไม่ถูกต้อง"));
		schoolForm.addComponent(email);
		
		passwordChange = new ActiveLink();
		passwordChange.setCaption("เปลี่ยนรหัสผ่าน");
		passwordChange.addListener(new LinkActivatedListener() {
            private static final long serialVersionUID = -7680743472997645381L;

            public void linkActivated(LinkActivatedEvent event) {
            	if(!isPassEdited){
            		setShowPassword();
            	}else{
            		setHidePassword();
            	}
            }
        });
		schoolForm.addComponent(passwordChange);
		
		password = new PasswordField("รหัสผ่าน");
		password.setWidth("90%");
		password.setInputPrompt("รหัสผ่าน");
		password.setVisible(false);
		password.setNullRepresentation("");
		password.addValueChangeListener(new ValueChangeListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				if(event.getProperty().getValue() != null)
					passwordHashed = BCrypt.hashpw(event.getProperty().getValue().toString(), BCrypt.gensalt());
			}
		});
		schoolForm.addComponent(password);
		
		passwordAgain = new PasswordField("รหัสผ่านอีกครั้ง");
		passwordAgain.setWidth("90%");
		passwordAgain.setInputPrompt("รหัสผ่าน");
		passwordAgain.setVisible(false);
		passwordAgain.setNullRepresentation("");
		schoolForm.addComponent(passwordAgain);

		Button userSave = new Button("บันทึก",FontAwesome.SAVE);
		userSave.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 1L;

			@SuppressWarnings("unchecked")
			@Override
			public void buttonClick(ClickEvent event) {
				try {
					if(userBinder.isValid()){
						if(isPassEdited){
							if(password.getValue().toString().equals(passwordAgain.getValue())){
								password.setValue(passwordHashed);
							}else{
								Notification.show("รหัสผ่านไม่ตรงกัน กรุณาระบุใหม่อีกครั้ง", Type.WARNING_MESSAGE);
								return;
							}
						}
						
						if(isFirstTimeStudent != null){
							if(isFirstTimeStudent){
								userItem.getItemProperty(UserSchema.IS_EDITED).setValue(true);
								isFirstTimeStudent = false;
								initComponent(new EditStudentView(userId,false));
							}
						}
						
						userBinder.commit();
						userContainer.commit();
						email.setReadOnly(true);
						setHidePassword();
						
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
		if(userItem.getItemProperty(UserSchema.REF_USER_TYPE).getValue().toString().equals(Integer.toString(UserType.EMPLOYEE)))
			userWD.setContent(new EditPersonnelView(userId));
		else if(userItem.getItemProperty(UserSchema.REF_USER_TYPE).getValue().toString().equals(Integer.toString(UserType.STUDENT))){
			userWD.setContent(new EditStudentView(userId,false));
		}
		UI.getCurrent().addWindow(userWD);
	}
	
	/* หน้าต่างแก้ข้อมูลผู้ใช้ */
	private void initUserGraduatedHistoryLayout(){
		Window userWD = new Window("ข้อมูลการศึกษา");
		userWD.setSizeFull();
		userWD.center();
		if(userItem.getItemProperty(UserSchema.REF_USER_TYPE).getValue().toString().equals(Integer.toString(UserType.EMPLOYEE)))
			userWD.setContent(new PersonnelGraduatedHistoryView(userId));
		UI.getCurrent().addWindow(userWD);
	}
	
	private void setShowPassword(){
		isPassEdited = true;
		password.setValue(null);
		passwordAgain.setValue(null);
    	password.setVisible(true);
    	passwordAgain.setVisible(true);
	}
	
	private void setHidePassword(){
		isPassEdited = false;
		password.setValue(passwordHashed);
		passwordAgain.setValue(passwordHashed);
    	password.setVisible(false);
    	passwordAgain.setVisible(false);
	}
	
	private void initialDataBinding(){
		userBinder = new FieldGroup(userItem);
		userBinder.setBuffered(true);
		userBinder.bind(firstname, UserSchema.FIRSTNAME);
		userBinder.bind(lastname, UserSchema.LASTNAME);
		userBinder.bind(email, UserSchema.EMAIL);
		userBinder.bind(password, UserSchema.PASSWORD);
		
		if(isFirstTimeStudent == null){
			email.setReadOnly(true);
		}else{
			if(isFirstTimeStudent){
				email.setValue(null);
				email.setReadOnly(false);
			}else{
				email.setReadOnly(true);
			}
		}
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
		//ui.close();
    	ui.setContent(new LoginView());
	}
	
	/* ตั้งค่า Session */
	private void resetSession(){
		UI.getCurrent().getSession().setAttribute(SessionSchema.SCHOOL_ID, null);
		UI.getCurrent().getSession().setAttribute(SessionSchema.SCHOOL_NAME, null);
		UI.getCurrent().getSession().setAttribute(SessionSchema.USER_ID, null);
		UI.getCurrent().getSession().setAttribute(SessionSchema.REF_ID, null);
		UI.getCurrent().getSession().setAttribute(SessionSchema.FIRSTNAME, null);
		UI.getCurrent().getSession().setAttribute(SessionSchema.EMAIL, null);
	}
	
	private void setPermission(int index, Button button, Class<?> clazz){
		if(permissions != null){
			if(permissions[index].equals("0")){
				button.setVisible(false);
			}else{
				button.setVisible(true);
			}
		}else{
			button.setVisible(false);
		}
	}
	
	private void setFundamentalPermission(int index, Button button, VerticalLayout layout){
		if(permissions != null){
			if(permissions[index].equals("0")){
				button.setVisible(false);
			}else{
				layout.setVisible(true);
				button.setVisible(true);
			}
		}else{
			button.setVisible(false);
		}
	}
}
