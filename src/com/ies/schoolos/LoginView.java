package com.ies.schoolos;

import java.util.Date;

import javax.servlet.http.Cookie;

import org.vaadin.dialogs.ConfirmDialog;

import com.ies.schoolos.component.SchoolOSView;
import com.ies.schoolos.component.recruit.AddRecruitStudentView;
import com.ies.schoolos.container.Container;
import com.ies.schoolos.schema.CookieSchema;
import com.ies.schoolos.schema.SchoolSchema;
import com.ies.schoolos.schema.SessionSchema;
import com.ies.schoolos.type.dynamic.Province;
import com.ies.schoolos.utility.BCrypt;
import com.ies.schoolos.utility.DateTimeUtil;
import com.ies.schoolos.utility.Notification;
import com.vaadin.data.Item;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.filter.Compare.Equal;
import com.vaadin.data.util.sqlcontainer.RowId;
import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.data.validator.EmailValidator;
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
	
	private SQLContainer schoolContainer = Container.getInstance().getSchoolContainer();
	private Item schoolItem;
	
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
				if(UI.getCurrent().getSession().getAttribute(SessionSchema.SCHOOL_ID) != null)
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
		email.setInputPrompt("อีเมลล์");
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
		emailRecruit.setInputPrompt("อีเมลล์");
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
		passwordSignupAgain.setInputPrompt("รหัสผ่าน");
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
				//สมัครสมาชิก
				if(registrationBinder.getField(SchoolSchema.NAME).getValue() != null &&
					registrationBinder.getField(SchoolSchema.PROVINCE_ID).getValue() != null &&
					registrationBinder.getField(SchoolSchema.FIRSTNAME).getValue() != null &&
					registrationBinder.getField(SchoolSchema.LASTNAME).getValue() != null &&
					registrationBinder.getField(SchoolSchema.EMAIL).getValue() != null &&
					registrationBinder.getField(SchoolSchema.PASSWORD).getValue() != null){
					
					schoolContainer.addContainerFilter(new Equal(SchoolSchema.EMAIL, registrationBinder.getField(SchoolSchema.EMAIL).getValue().toString()));
					/* ตรวจสอบ Email ซ้ำ */
					if(schoolContainer.size() > 0){
						Notification.show("ไม่สามารถใช้ Email ดังกล่าว เนื่องจากมีผู้ใช้ในการสมัครแล้ว", Type.WARNING_MESSAGE);
						return;
					}					
					/* ลบ WHERE ออกจาก Query เพื่อป้องกันการค้างของคำสั่่งจากการทำงานอื่นที่เรียกตัวแปรไปใช้ */
					schoolContainer.removeAllContainerFilters();
					
					if(passwordSignup.getValue().equals(passwordSignupAgain.getValue())){
							try {								
								/* เพิ่มข้อมูล */
								Object tmpItem = schoolContainer.addItem();
								schoolItem = schoolContainer.getItem(tmpItem);
								schoolItem.getItemProperty(SchoolSchema.NAME).setValue(registrationBinder.getField(SchoolSchema.NAME).getValue());
								schoolItem.getItemProperty(SchoolSchema.PROVINCE_ID).setValue(registrationBinder.getField(SchoolSchema.PROVINCE_ID).getValue());
								schoolItem.getItemProperty(SchoolSchema.FIRSTNAME).setValue(registrationBinder.getField(SchoolSchema.FIRSTNAME).getValue());
								schoolItem.getItemProperty(SchoolSchema.LASTNAME).setValue(registrationBinder.getField(SchoolSchema.LASTNAME).getValue());
								schoolItem.getItemProperty(SchoolSchema.EMAIL).setValue(registrationBinder.getField(SchoolSchema.EMAIL).getValue());
								schoolItem.getItemProperty(SchoolSchema.PASSWORD).setValue(BCrypt.hashpw(registrationBinder.getField(SchoolSchema.PASSWORD).getValue().toString(), BCrypt.gensalt()));
								initSchoolFieldGroup();
								
								registrationBinder.commit();
								schoolContainer.commit();
								ConfirmDialog.show(UI.getCurrent(),"สมัครสมาชิก", "การสมัครเสร็จสิ้น พร้อมใช้งาน SchoolOS. คุณต้องการเข้าใช้งานขณะนี้ใช่หรือไม่?", "ตกลง", "ยกเลิก", new ConfirmDialog.Listener() {
									private static final long serialVersionUID = 1L;
									public void onClose(ConfirmDialog dialog) {
						                if (dialog.isConfirmed()) {
						                	login(emailRecruit.getValue(), passwordSignupAgain.getValue());
						                }
						            }
						        });
							} catch (Exception e) {
								Notification.show("สมัครไม่สำเร็จ กรุณาลองใหม่อีกครั้ง");
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
		
		initSchoolFieldGroup();
	}
	
	//จัดกลุ่มสำหรับ Field สมัครสมาชิก
	private void initSchoolFieldGroup(){			
		registrationBinder = new FieldGroup(schoolItem);
		registrationBinder.setBuffered(true);
		registrationBinder.bind(schoolName, SchoolSchema.NAME);
		registrationBinder.bind(schoolProvinceId, SchoolSchema.PROVINCE_ID);
		registrationBinder.bind(firstname, SchoolSchema.FIRSTNAME);
		registrationBinder.bind(lastname, SchoolSchema.LASTNAME);
		registrationBinder.bind(emailRecruit, SchoolSchema.EMAIL);
		registrationBinder.bind(passwordSignup, SchoolSchema.PASSWORD);
	}	
	
	//เข้าสู่ระบบ
	private void login(String username, String password){	
		schoolContainer.addContainerFilter(new Equal(SchoolSchema.EMAIL,username));

		if(schoolContainer.size() != 0){
			Item item = schoolContainer.getItem(schoolContainer.getIdByIndex(0));
			String passwordHash = item.getItemProperty(SchoolSchema.PASSWORD).getValue().toString();

			if(BCrypt.checkpw(password, passwordHash)){
				UI ui = UI.getCurrent();
				setSession(item);
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
	
	/* ตั้งค่า Session */
	private void setSession(Item item){
		getSession().setAttribute(SessionSchema.IS_ROOT, true);
		getSession().setAttribute(SessionSchema.SCHOOL_ID, item.getItemProperty(SchoolSchema.SCHOOL_ID).getValue());
		getSession().setAttribute(SessionSchema.SCHOOL_NAME, item.getItemProperty(SchoolSchema.NAME).getValue());
		getSession().setAttribute(SessionSchema.FIRSTNAME, item.getItemProperty(SchoolSchema.FIRSTNAME).getValue());
		getSession().setAttribute(SessionSchema.EMAIL, item.getItemProperty(SchoolSchema.EMAIL).getValue());
	}
	
	private void getCriteriaLogin(){
		/* ถ้าเข้ากับ Url โรงเรียนให้ทำการปิดรับสมัคร แล้วขึ้นชื่อโรงเรียนแทน */
		if(UI.getCurrent().getSession().getAttribute(SessionSchema.SCHOOL_ID) != null){
			schoolRecruit.setVisible(false);
			signonTopic.setValue(UI.getCurrent().getSession().getAttribute(SessionSchema.SCHOOL_NAME).toString());
			
			Item item = schoolContainer.getItem(new RowId(UI.getCurrent().getSession().getAttribute(SessionSchema.SCHOOL_ID)));
			
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