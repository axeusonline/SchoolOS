package com.ies.schoolos;

import java.util.Date;

import javax.servlet.http.Cookie;

import org.vaadin.dialogs.ConfirmDialog;

import com.ies.schoolos.component.SchoolOSView;
import com.ies.schoolos.component.personnel.AddPersonnelView;
import com.ies.schoolos.component.recruit.AddRecruitStudentView;
import com.ies.schoolos.component.registration.AddStudentView;
import com.ies.schoolos.container.Container;
import com.ies.schoolos.schema.CookieSchema;
import com.ies.schoolos.schema.CreateModifiedSchema;
import com.ies.schoolos.schema.SchoolSchema;
import com.ies.schoolos.schema.SessionSchema;
import com.ies.schoolos.schema.UserSchema;
import com.ies.schoolos.type.Feature;
import com.ies.schoolos.type.dynamic.Province;
import com.ies.schoolos.utility.BCrypt;
import com.ies.schoolos.utility.DateTimeUtil;
import com.ies.schoolos.utility.Notification;
import com.vaadin.data.Item;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.filter.Compare.Equal;
import com.vaadin.data.util.sqlcontainer.RowId;
import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.data.util.sqlcontainer.query.QueryDelegate.RowIdChangeEvent;
import com.vaadin.data.util.sqlcontainer.query.QueryDelegate.RowIdChangeListener;
import com.vaadin.data.validator.EmailValidator;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Responsive;
import com.vaadin.server.VaadinService;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Window;

public class LoginView extends VerticalLayout{
	private static final long serialVersionUID = 1L;
	
	private Object schoolId;
	
	private SQLContainer schoolContainer = Container.getSchoolContainer();
	private SQLContainer userContainer = Container.getUserContainer();
	
	private Item schoolItem;
	private Item userItem;
	
	private GridLayout headerLayout;
	private Label branding;
	private Button recruit;
	
	private CssLayout contentLayout;

	//Login BOX
	private VerticalLayout loginLayout;
	private Label signonTopic;
	private TextField email;
	private PasswordField password;
	private Button signon;
	private CheckBox rememberPass;
	private Link forgetPass;
	
	//REGISTRATION BOX
	private FieldGroup registrationBinder;
	private FieldGroup userBinder;
	
	private VerticalLayout schoolRecruit;
	private Label freeNotice;
	private TextField schoolName;
	private ComboBox schoolProvinceId;
	private TextField firstname;
	private TextField lastname;
	private TextField emailRecruit;
	private PasswordField passwordSignup;
	private PasswordField passwordSignupAgain;
	private Button signup;
	
	//REGISTRATION BOX	
	private VerticalLayout studentAndPersonnelLayout;
	private Label signupNotice;
	private Button studentSignup;
	private Button personnelSignup;
	
	public LoginView() {
		buildMainLayout();
	}
	
	private void buildMainLayout(){
		setWidth("100%");
		setHeight("100%");
		setCaption("SchoolOS");
		initHeader();
		initContent();
		getCriteriaLogin();
		initSqlContainerRowIdChange();
		initSchoolFieldGroup();
		initUserFieldGroup();
		Responsive.makeResponsive(contentLayout);
	}
	
	private void initHeader(){
		headerLayout = new GridLayout();
		headerLayout.setStyleName("nav-bar-login");
		headerLayout.setWidth("100%");
		headerLayout.setHeight("60px");
		headerLayout.setColumns(3);
		headerLayout.setRows(1);
		addComponent(headerLayout);
		
		branding = new Label("SchoolOS");
		branding.setStyleName("branding");
		headerLayout.addComponent(branding,1,0);
		
		HorizontalLayout headerRighLayout = new HorizontalLayout();
		headerRighLayout.setSpacing(true);
		headerRighLayout.setWidth("90%");
		headerRighLayout.setHeight("100%");
		headerLayout.addComponent(headerRighLayout,2,0);
		
		recruit = new Button("สมัครเรียน");
		recruit.setStyleName("student-recruit");
		recruit.setWidth("140px");
		recruit.setHeight("40px");
		recruit.setVisible(false);
		recruit.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 1L;
			@Override
			public void buttonClick(ClickEvent event) {
				if(SessionSchema.getSchoolID() != null)
					UI.getCurrent().addWindow(initStudentRecruitForm());
				else
					Notification.show("ไม่อนุญาติให้สมัครโดยไม่ผ่าน ลิ้งของโรงเรียน", Type.WARNING_MESSAGE);
			}
		});
		headerRighLayout.addComponent(recruit);
		headerRighLayout.setComponentAlignment(recruit, Alignment.MIDDLE_RIGHT);
		
		contentLayout = new CssLayout();
		contentLayout.setStyleName("login-layout");
		contentLayout.setSizeFull();
		addComponent(contentLayout);
		setExpandRatio(contentLayout, 1);	
	}
	
	private void initContent(){
		//############## RIGH CONTENT ##############
		VerticalLayout righContentLayout = new VerticalLayout();
		righContentLayout.setSpacing(true);
		righContentLayout.setMargin(true);
		righContentLayout.setStyleName("content-form");
		righContentLayout.setSizeUndefined();
		contentLayout.addComponent(righContentLayout);
		
		initLoginBox();
		righContentLayout.addComponent(loginLayout);
		righContentLayout.setComponentAlignment(loginLayout, Alignment.MIDDLE_CENTER);
		
		initSigupBox();
		righContentLayout.addComponent(schoolRecruit);
		righContentLayout.setComponentAlignment(schoolRecruit, Alignment.MIDDLE_CENTER);	
		righContentLayout.setExpandRatio(schoolRecruit, 1);
		
		initStudentAndPersonnel();
		righContentLayout.addComponent(studentAndPersonnelLayout);
		righContentLayout.setComponentAlignment(studentAndPersonnelLayout, Alignment.MIDDLE_CENTER);	
		righContentLayout.setExpandRatio(studentAndPersonnelLayout, 1);
	}
	
	private void initLoginBox(){		
		loginLayout = new VerticalLayout();
		loginLayout.setStyleName("login-box");
		
		signonTopic = new Label("เข้าใช้งาน");
		signonTopic.setWidth("90%");
		signonTopic.setStyleName("signon-topic");
		loginLayout.addComponent(signonTopic);
		loginLayout.setComponentAlignment(signonTopic, Alignment.MIDDLE_CENTER);
		
		email = new TextField();
		email.setStyleName("input-form");
		email.setInputPrompt("อีเมล์");
		email.addValidator(new EmailValidator("ข้อมูลไม่ถูกต้อง"));
		loginLayout.addComponent(email);
		loginLayout.setComponentAlignment(email, Alignment.MIDDLE_CENTER);
		
		HorizontalLayout passwordLayout = new HorizontalLayout();
		passwordLayout.setSpacing(true);
		passwordLayout.setWidth("90%");
		loginLayout.addComponent(passwordLayout);
		loginLayout.setComponentAlignment(passwordLayout, Alignment.MIDDLE_CENTER);
		
		password = new PasswordField();
		password.setWidth("100%");
		password.setInputPrompt("รหัสผ่าน");
		passwordLayout.addComponent(password);
		
		signon = new Button("เข้าสู่ระบบ");
		signon.setWidth("100%");
		signon.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 1L;
			@Override
			public void buttonClick(ClickEvent event) {
				login(email.getValue(),password.getValue());
			}
		});
		passwordLayout.addComponent(signon);
		
		HorizontalLayout noticeLayout = new HorizontalLayout();
		noticeLayout.setStyleName("password-notice");
		noticeLayout.setWidth("90%");
		loginLayout.addComponent(noticeLayout);
		loginLayout.setComponentAlignment(noticeLayout, Alignment.MIDDLE_CENTER);
		
		rememberPass = new CheckBox("จำรหัสผ่าน");
		rememberPass.setStyleName("password-text");
		rememberPass.setWidth("100%");
		noticeLayout.addComponent(rememberPass);
		
		forgetPass = new Link();
		forgetPass.setStyleName("password-text");
		forgetPass.setCaption("ลืมรหัสผ่าน");
		forgetPass.setWidth("100%");
		noticeLayout.addComponent(forgetPass);
		
	}
	
	private void initSigupBox(){
		schoolRecruit = new VerticalLayout();
		schoolRecruit.setStyleName("school-sigon");
		
		freeNotice = new Label("สมัครใช้งานฟรี");
		freeNotice.setWidth("90%");
		freeNotice.setStyleName("free-notice");
		schoolRecruit.addComponent(freeNotice);
		schoolRecruit.setComponentAlignment(freeNotice, Alignment.MIDDLE_CENTER);

		schoolName = new TextField();
		schoolName.setWidth("90%");
		schoolName.setStyleName("input-form");
		schoolName.setInputPrompt("ชื่อโรงเรียน");
		schoolName.setNullRepresentation("");
		schoolRecruit.addComponent(schoolName);
		schoolRecruit.setComponentAlignment(schoolName, Alignment.MIDDLE_CENTER);

		schoolProvinceId = new ComboBox();
		schoolProvinceId.setWidth("90%");
		schoolProvinceId.setStyleName("input-form");
		schoolProvinceId.setContainerDataSource(new Province());
		schoolProvinceId.setInputPrompt("จังหวัด");
		schoolProvinceId.setItemCaptionPropertyId("name");
		schoolProvinceId.setImmediate(true);
		schoolProvinceId.setNullSelectionAllowed(false);
		schoolProvinceId.setFilteringMode(FilteringMode.CONTAINS);
		schoolRecruit.addComponent(schoolProvinceId);
		schoolRecruit.setComponentAlignment(schoolProvinceId, Alignment.MIDDLE_CENTER);
		
		HorizontalLayout nameLayout = new HorizontalLayout();
		nameLayout.setSpacing(true);
		nameLayout.setStyleName("two-columns-form");
		nameLayout.setWidth("90%");
		schoolRecruit.addComponent(nameLayout);
		schoolRecruit.setComponentAlignment(nameLayout, Alignment.MIDDLE_CENTER);
		
		firstname = new TextField();
		firstname.setInputPrompt("ชื่อจริง");
		firstname.setNullRepresentation("");
		nameLayout.addComponent(firstname);
		
		lastname = new TextField();
		lastname.setInputPrompt("นามสกุล");
		lastname.setNullRepresentation("");
		nameLayout.addComponent(lastname);
		
		emailRecruit = new TextField();
		emailRecruit.setStyleName("input-form");
		emailRecruit.setInputPrompt("อีเมล์");
		emailRecruit.setNullRepresentation("");
		emailRecruit.addValidator(new EmailValidator("ข้อมูลไม่ถูกต้อง"));
		schoolRecruit.addComponent(emailRecruit);
		schoolRecruit.setComponentAlignment(emailRecruit, Alignment.MIDDLE_CENTER);
		
		passwordSignup = new PasswordField();
		passwordSignup.setWidth("90%");
		passwordSignup.setInputPrompt("รหัสผ่าน");
		passwordSignup.setStyleName("input-form");
		passwordSignup.setNullRepresentation("");
		schoolRecruit.addComponent(passwordSignup);
		schoolRecruit.setComponentAlignment(passwordSignup, Alignment.MIDDLE_CENTER);
		
		passwordSignupAgain = new PasswordField();
		passwordSignupAgain.setWidth("90%");
		passwordSignupAgain.setInputPrompt("รหัสผ่านอีกครั้ง");
		passwordSignupAgain.setStyleName("input-form");
		passwordSignupAgain.setNullRepresentation("");
		schoolRecruit.addComponent(passwordSignupAgain);
		schoolRecruit.setComponentAlignment(passwordSignupAgain, Alignment.MIDDLE_CENTER);
		
		signup = new Button("สมัครใช้งาน");
		signup.setSizeFull();
		signup.setStyleName("signup-button");
		signup.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 1L;

			@SuppressWarnings("unchecked")
			@Override
			public void buttonClick(ClickEvent event) {
				/* ตรวจสอบ Email */
				if(!emailRecruit.isValid()){
					Notification.show("อีเมล์ไม่ถูกต้อง", Type.WARNING_MESSAGE);
					return ;
				}
				
				//สมัครสมาชิก
				if(registrationBinder.getField(SchoolSchema.NAME).getValue() != null &&
					registrationBinder.getField(SchoolSchema.PROVINCE_ID).getValue() != null &&
					userBinder.getField(UserSchema.FIRSTNAME).getValue() != null &&
					userBinder.getField(UserSchema.LASTNAME).getValue() != null &&
					userBinder.getField(UserSchema.EMAIL).getValue() != null &&
					userBinder.getField(UserSchema.PASSWORD).getValue() != null){
					
					userContainer.addContainerFilter(new Equal(UserSchema.EMAIL, userBinder.getField(UserSchema.EMAIL).getValue().toString()));
					/* ตรวจสอบ Email ซ้ำ */
					if(userContainer.size() > 0){
						Notification.show("ไม่สามารถใช้อีเมล์ดังกล่าว เนื่องจากมีผู้ใช้ในการสมัครแล้ว", Type.WARNING_MESSAGE);
						return;
					}					
					/* ลบ WHERE ออกจาก Query เพื่อป้องกันการค้างของคำสั่่งจากการทำงานอื่นที่เรียกตัวแปรไปใช้ */
					userContainer.removeAllContainerFilters();
					
					if(passwordSignup.getValue().equals(passwordSignupAgain.getValue())){
							try {								
								/* เพิ่มข้อมูลโรงเรียน */
								Object schoolTmpId = schoolContainer.addItem();
								schoolItem = schoolContainer.getItem(schoolTmpId);
								schoolItem.getItemProperty(SchoolSchema.NAME).setValue(registrationBinder.getField(SchoolSchema.NAME).getValue());
								schoolItem.getItemProperty(SchoolSchema.PROVINCE_ID).setValue(registrationBinder.getField(SchoolSchema.PROVINCE_ID).getValue());
								initSchoolFieldGroup();
								registrationBinder.commit();
								schoolContainer.commit();
								
								/* เพิ่มข้อมูลโรงเรียน */
								Object userTmpId = userContainer.addItem();
								userItem = userContainer.getItem(userTmpId);
								userItem.getItemProperty(UserSchema.SCHOOL_ID).setValue(Integer.parseInt(schoolId.toString()));
								userItem.getItemProperty(UserSchema.FIRSTNAME).setValue(userBinder.getField(UserSchema.FIRSTNAME).getValue());
								userItem.getItemProperty(UserSchema.LASTNAME).setValue(userBinder.getField(UserSchema.LASTNAME).getValue());
								userItem.getItemProperty(UserSchema.EMAIL).setValue(userBinder.getField(UserSchema.EMAIL).getValue());
								userItem.getItemProperty(UserSchema.PASSWORD).setValue(BCrypt.hashpw(userBinder.getField(UserSchema.PASSWORD).getValue().toString(), BCrypt.gensalt()));
								userItem.getItemProperty(UserSchema.STATUS).setValue(0);
								userItem.getItemProperty(UserSchema.REF_USER_ID).setValue(Integer.parseInt(schoolId.toString()));
								userItem.getItemProperty(UserSchema.REF_USER_TYPE).setValue(0);
								userItem.getItemProperty(CreateModifiedSchema.CREATED_BY_ID).setValue(Integer.parseInt(schoolId.toString()));
								userItem.getItemProperty(CreateModifiedSchema.CREATED_DATE).setValue(new Date());
								
								Feature.setPermission(userItem, true);
								initUserFieldGroup();
								userBinder.commit();
								userContainer.commit();
								
								ConfirmDialog.show(UI.getCurrent(),"สมัครสมาชิก", "การสมัครเสร็จสิ้น พร้อมใช้งาน SchoolOS. คุณต้องการเข้าใช้งานขณะนี้ใช่หรือไม่?", "ตกลง", "ยกเลิก", new ConfirmDialog.Listener() {
									private static final long serialVersionUID = 1L;
									public void onClose(ConfirmDialog dialog) {
						                if (dialog.isConfirmed()) {
						                	login(emailRecruit.getValue(), passwordSignupAgain.getValue());
						                }
						            }
						        });
								
							} catch (Exception e) {
								Notification.show("สมัครไม่สำเร็จ กรุณาลองใหม่อีกครั้ง", Type.WARNING_MESSAGE);
								e.printStackTrace();
							}
					}else{
						Notification.show("รหัสผ่านไม่ตรงกัน กรุณาระบุใหม่อีกครั้ง", Type.WARNING_MESSAGE);
					}			
				}else{
					Notification.show("กรุณากรอกข้อมูลให้ครบถ้วน", Type.WARNING_MESSAGE);
				}	
			}
		});
		schoolRecruit.addComponent(signup);
		schoolRecruit.setComponentAlignment(signup, Alignment.MIDDLE_CENTER);
	}
	
	private void initStudentAndPersonnel(){
		studentAndPersonnelLayout = new VerticalLayout();
		studentAndPersonnelLayout.setSpacing(true);
		studentAndPersonnelLayout.setMargin(true);
		studentAndPersonnelLayout.setVisible(false);
		studentAndPersonnelLayout.setStyleName("school-sigon");
		studentAndPersonnelLayout.setWidth("90%");

		signupNotice = new Label("ลงทะเบียน");
		signupNotice.setWidth("90%");
		signupNotice.setStyleName("free-notice");
		studentAndPersonnelLayout.addComponent(signupNotice);
		studentAndPersonnelLayout.setComponentAlignment(signupNotice, Alignment.MIDDLE_CENTER);
		
		studentSignup = new Button("สำหรับนักเรียน", FontAwesome.CUBE);
		studentSignup.setWidth("300px");
		studentSignup.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event) {
				final Window window = new Window("กรุณาพิมพ์รหัสผ่าน");
				window.setWidth("400px");
				window.setHeight("150px");
				window.center();
				UI.getCurrent().addWindow(window);
				
				VerticalLayout passwordLayout = new VerticalLayout();
				passwordLayout.setSpacing(true);
				passwordLayout.setMargin(true);
				window.setContent(passwordLayout);
				
				final TextField passwordStudent = new TextField();
				passwordStudent.setInputPrompt("พิมพ์รหัสผ่าน");
				passwordLayout.addComponent(passwordStudent);
				passwordLayout.setComponentAlignment(passwordStudent, Alignment.MIDDLE_CENTER);
				
				Button accepButton = new Button("ตกลง", FontAwesome.SEND);
				accepButton.addClickListener(new ClickListener() {
					private static final long serialVersionUID = 1L;

					@Override
					public void buttonClick(ClickEvent event) {
						Item item = schoolContainer.getItem(new RowId(SessionSchema.getSchoolID()));
						if(item.getItemProperty(SchoolSchema.STUDENT_SIGNUP_PASS).getValue().equals(passwordStudent.getValue())){
							window.close();
							
							final Window addStudentWindow = new Window("กรุณาพิมพ์รหัสผ่าน");
							addStudentWindow.setSizeFull();
							addStudentWindow.setPositionX(0);
							addStudentWindow.setPositionY(0);
							addStudentWindow.setContent(new AddStudentView());
							UI.getCurrent().addWindow(addStudentWindow);
						}else{
							Notification.show("รหัสไม่ถูกต้อง", Type.WARNING_MESSAGE);
						}
					}
				});
				passwordLayout.addComponent(accepButton);
				passwordLayout.setComponentAlignment(accepButton, Alignment.MIDDLE_CENTER);
			}
		});
		studentAndPersonnelLayout.addComponent(studentSignup);
		studentAndPersonnelLayout.setComponentAlignment(studentSignup, Alignment.MIDDLE_CENTER);
		
		personnelSignup = new Button("สำหรับเจ้าหน้าที่", FontAwesome.CUBE);
		personnelSignup.setWidth("300px");
		personnelSignup.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event) {
				final Window window = new Window("กรุณาพิมพ์รหัสผ่าน");
				window.setWidth("400px");
				window.setHeight("150px");
				window.center();
				UI.getCurrent().addWindow(window);
				
				VerticalLayout passwordLayout = new VerticalLayout();
				passwordLayout.setSpacing(true);
				passwordLayout.setMargin(true);
				window.setContent(passwordLayout);
				
				final TextField passwordPersonnel = new TextField();
				passwordPersonnel.setInputPrompt("พิมพ์รหัสผ่าน");
				passwordLayout.addComponent(passwordPersonnel);
				passwordLayout.setComponentAlignment(passwordPersonnel, Alignment.MIDDLE_CENTER);
				
				Button accepButton = new Button("ตกลง", FontAwesome.SEND);
				accepButton.addClickListener(new ClickListener() {
					private static final long serialVersionUID = 1L;

					@Override
					public void buttonClick(ClickEvent event) {
						ConfirmDialog.show(UI.getCurrent(), "อีเมล์จะถูกใช้เป็นบัญชีเข้าใช้งาน","คุณมีอีเมล์ใช่แล้วหรือไม่?","มีอีเมล์","ไม่มีอีเมล์",
					        new ConfirmDialog.Listener() {
								private static final long serialVersionUID = 1L;
								public void onClose(ConfirmDialog dialog) {
					                if (dialog.isConfirmed()) {
										Item item = schoolContainer.getItem(new RowId(SessionSchema.getSchoolID()));
										if(item.getItemProperty(SchoolSchema.PERSONNEL_SIGNUP_PASS).getValue().equals(passwordPersonnel.getValue())){
											window.close();
											
											final Window addPersonnelWindow = new Window("ประวัติส่วนตัว");
											addPersonnelWindow.setSizeFull();
											addPersonnelWindow.setPositionX(0);
											addPersonnelWindow.setPositionY(0);
											addPersonnelWindow.setContent(new AddPersonnelView());
											UI.getCurrent().addWindow(addPersonnelWindow);						
										}else{
											Notification.show("รหัสไม่ถูกต้อง", Type.WARNING_MESSAGE);
										}
					                }else{
					                	window.close();
					                }
					            }
					        });
					
					}
				});
				passwordLayout.addComponent(accepButton);
				passwordLayout.setComponentAlignment(accepButton, Alignment.MIDDLE_CENTER);
			}
		});
		studentAndPersonnelLayout.addComponent(personnelSignup);
		studentAndPersonnelLayout.setComponentAlignment(personnelSignup, Alignment.MIDDLE_CENTER);
	}

	/* กำหนดค่า PK Auto Increment หลังการบันทึก */
	private void initSqlContainerRowIdChange(){
		/* นักเรียน */
		schoolContainer.addRowIdChangeListener(new RowIdChangeListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void rowIdChange(RowIdChangeEvent arg0) {
				schoolId = arg0.getNewRowId();
			}
		});
		
	}

	//จัดกลุ่มสำหรับ Field สมัครสมาชิก
	private void initSchoolFieldGroup(){			
		registrationBinder = new FieldGroup(schoolItem);
		registrationBinder.setBuffered(true);
		registrationBinder.bind(schoolName, SchoolSchema.NAME);
		registrationBinder.bind(schoolProvinceId, SchoolSchema.PROVINCE_ID);
	}	
	
	//จัดกลุ่มสำหรับ Field ผุ้ใช้
	private void initUserFieldGroup(){
		userBinder = new FieldGroup(userItem);
		userBinder.setBuffered(true);
		userBinder.bind(firstname, UserSchema.FIRSTNAME);
		userBinder.bind(lastname, UserSchema.LASTNAME);
		userBinder.bind(emailRecruit, UserSchema.EMAIL);
		userBinder.bind(passwordSignup, UserSchema.PASSWORD);
	}
	
	//เข้าสู่ระบบ
	private void login(String username, String password){	
		userContainer.addContainerFilter(new Equal(UserSchema.EMAIL,username));

		if(userContainer.size() != 0){
			Item userItem = userContainer.getItem(userContainer.getIdByIndex(0));
			Item schoolItem = schoolContainer.getItem(new RowId(userItem.getItemProperty(SchoolSchema.SCHOOL_ID).getValue()));

			String passwordHash = userItem.getItemProperty(UserSchema.PASSWORD).getValue().toString();
			if(BCrypt.checkpw(password, passwordHash)){
				UI ui = UI.getCurrent();
				SessionSchema.setSession(
						Integer.parseInt(userItem.getItemProperty(UserSchema.SCHOOL_ID).getValue().toString()),
						Integer.parseInt(userItem.getItemProperty(UserSchema.USER_ID).getValue().toString()),
						schoolItem.getItemProperty(SchoolSchema.NAME).getValue(),
						userItem.getItemProperty(UserSchema.FIRSTNAME).getValue(),
						userItem.getItemProperty(UserSchema.EMAIL).getValue());
				ui.setContent(new SchoolOSView());	
				/* จำบัญชีผู้ใช้และรหัสผ่าน */
				if(rememberPass.getValue()){
					Cookie emailCookie = new Cookie(CookieSchema.EMAIL, username);
					emailCookie.setMaxAge(12000);
					emailCookie.setPath(VaadinService.getCurrentRequest().getContextPath());
					VaadinService.getCurrentResponse().addCookie(emailCookie);
					
					Cookie passwordCookie = new Cookie(CookieSchema.PASSWORD, passwordHash);
					passwordCookie.setMaxAge(12000);
					passwordCookie.setPath(VaadinService.getCurrentRequest().getContextPath());
					VaadinService.getCurrentResponse().addCookie(passwordCookie);
				}
			}else{
				Notification.show("บัญชีผู้ใช้ หรือ รหัสผิดพลาด กรุณาลองใหม่อีกครั้ง", Type.WARNING_MESSAGE);
			}
		}else{
			Notification.show("ไม่พบบัญชีผู้ใช้", Type.WARNING_MESSAGE);
		}

		/* ลบ WHERE ออกจาก Query เพื่อป้องกันการค้างของคำสั่่งจากการทำงานอื่นที่เรียกตัวแปรไปใช้ */
		schoolContainer.removeAllContainerFilters();
	}
	
	/* ปิดหน้าต่างรับสมัครเรียน */
	private Window initStudentRecruitForm(){
		Window loginWindow = new Window();
		loginWindow.center();
		loginWindow.setSizeFull();
		loginWindow.setResizable(false);
		loginWindow.setDraggable(false);
		loginWindow.setContent(new AddRecruitStudentView(true,true));
		
		return loginWindow;
	}
	
	private void getCriteriaLogin(){
		/* ถ้าเข้ากับ Url โรงเรียนให้ทำการปิดรับสมัคร แล้วขึ้นชื่อโรงเรียนแทน */
		if(SessionSchema.getSchoolID() != null){
			schoolRecruit.setVisible(false);
			studentAndPersonnelLayout.setVisible(true);

			Item item = schoolContainer.getItem(new RowId(SessionSchema.getSchoolID()));
			signonTopic.setValue(item.getItemProperty(SchoolSchema.NAME).getValue().toString());
			
			/* ตรวจสอบว่า อยู่ช่วงรับสมัครใหม */
			if(item.getItemProperty(SchoolSchema.RECRUIT_START_DATE).getValue() != null &&
					item.getItemProperty(SchoolSchema.RECRUIT_END_DATE).getValue() != null){
				if(DateTimeUtil.compareBetweenDate((Date)item.getItemProperty(SchoolSchema.RECRUIT_START_DATE).getValue(), 
						(Date)item.getItemProperty(SchoolSchema.RECRUIT_END_DATE).getValue())){
					recruit.setVisible(true);
				}
			}
		}
	}
}