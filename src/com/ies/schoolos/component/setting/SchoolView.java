package com.ies.schoolos.component.setting;

import javax.servlet.http.Cookie;

import org.vaadin.activelink.ActiveLink;

import com.ies.schoolos.component.ui.ContentPage;
import com.ies.schoolos.container.Container;
import com.ies.schoolos.schema.SchoolSchema;
import com.ies.schoolos.schema.SessionSchema;
import com.ies.schoolos.type.StudentCodeGenerateType;
import com.ies.schoolos.type.dynamic.Province;
import com.ies.schoolos.utility.BCrypt;
import com.ies.schoolos.utility.Notification;

import org.vaadin.activelink.ActiveLink.LinkActivatedEvent;
import org.vaadin.activelink.ActiveLink.LinkActivatedListener;

import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.filter.Compare.Equal;
import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.VaadinService;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Window;

public class SchoolView extends ContentPage {
	private static final long serialVersionUID = 1L;
	
	private SQLContainer schoolContainer = Container.getInstance().getSchoolContainer();
	
	private Item schoolItem = null;
	private String passwordHash = null;
	
	private FieldGroup schoolBinder;
	
	private GridLayout gridLayout;
	
	private FormLayout schoolForm;
	private Label schoolTitle;
	private TextField schoolName;
	private ComboBox schoolProvinceId;
	private TextField firstname;
	private TextField lastname;
	private TextField email;
	private ActiveLink passwordChange;
	private Button schoolSave;
	
	private FormLayout periodForm;
	private Label recruitTitle;
	private PopupDateField recruitStartDate;
	private PopupDateField recruitEndDate;
	private Button preriodSave;
	
	private FormLayout studentCodeForm;
	private Label studentCodeTitle;
	private OptionGroup codeGenerateType;
	private TextField studentCodeFirst;
	private Button studentCodeSave;
	
	private FormLayout shortUrlForm;
	private Label shortUrlTitle;
	private TextField shortUrl;
	private Button shortUrlSave;
	
	public SchoolView() {
		super("ตั้งค่าข้อมูลทั่วไป");
		
		schoolContainer.refresh();
		
		buildMainLayout();
	}
	
	private void buildMainLayout(){
		setHeight("-1px");
		
		gridLayout = new GridLayout();
		gridLayout.setSizeFull();
		gridLayout.setRows(3);
		gridLayout.setColumns(2);
		gridLayout.setSpacing(true);
		addComponent(gridLayout);
		setExpandRatio(gridLayout, 1);
		
		
		intSchoolLayout();
		initShortUrlLayout();
		initPeriodLayout();
		initStudentCodeLayout();
		initialDataBinding();
	}
	
	private void intSchoolLayout(){
		schoolForm = new FormLayout();
		schoolForm.setSizeFull();
		schoolForm.setStyleName("border-white");
		gridLayout.addComponent(schoolForm,0,0,0,1);
		
		schoolTitle = new Label("ข้อมูลโรงเรียน");
		schoolForm.addComponent(schoolTitle);

		schoolName = new TextField("ชื่อโรงเรียน");
		schoolName.setRequired(true);
		schoolName.setInputPrompt("ชื่อโรงเรียน");
		schoolForm.addComponent(schoolName);

		schoolProvinceId = new ComboBox("จังหวัด");
		schoolProvinceId.setRequired(true);
		schoolProvinceId.setContainerDataSource(new Province());
		schoolProvinceId.setInputPrompt("จังหวัด");
		schoolProvinceId.setItemCaptionPropertyId("name");
		schoolProvinceId.setImmediate(true);
		schoolProvinceId.setNullSelectionAllowed(false);
		schoolProvinceId.setFilteringMode(FilteringMode.CONTAINS);
		schoolForm.addComponent(schoolProvinceId);
		
		firstname = new TextField("ชื่อผู้ดูแลระบบ");
		firstname.setRequired(true);
		firstname.setInputPrompt("ชื่อจริง");
		schoolForm.addComponent(firstname);
		
		lastname = new TextField("สกุลผู้ดูแลระบบ");
		lastname.setRequired(true);
		lastname.setInputPrompt("นามสกุล");
		schoolForm.addComponent(lastname);

		email = new TextField("อีเมลล์");
		email.setRequired(true);
		email.setEnabled(false);
		email.setInputPrompt("อีเมลล์");
		schoolForm.addComponent(email);
		
		passwordChange = new ActiveLink();
		passwordChange.setCaption("เปลี่ยนรหัสผ่าน");
		passwordChange.addListener(new LinkActivatedListener() {
            private static final long serialVersionUID = -7680743472997645381L;

            public void linkActivated(LinkActivatedEvent event) {
              	Window passwordWD = new Window();
              	passwordWD.setWidth("400px");
              	passwordWD.setHeight("200px");
              	passwordWD.center();
              	passwordWD.setCaption("เปลี่ยนรหัสผ่าน");
              	UI.getCurrent().addWindow(passwordWD);
              	
              	FormLayout passwordForm = new FormLayout();
              	passwordForm.setSizeFull();
              	passwordWD.setContent(passwordForm);
              	
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
							schoolItem.getItemProperty(SchoolSchema.PASSWORD).setValue(passwordHash);
							try {
								schoolBinder.commit();
								schoolContainer.commit();
								
								Cookie emailCookie = new Cookie(SessionSchema.EMAIL, UI.getCurrent().getSession().getAttribute(SessionSchema.EMAIL).toString());
								emailCookie.setMaxAge(12000);
								emailCookie.setPath(VaadinService.getCurrentRequest().getContextPath());
								VaadinService.getCurrentResponse().addCookie(emailCookie);
								
								Cookie passwordCookie = new Cookie(SessionSchema.PASSWORD, passwordHash);
								passwordCookie.setMaxAge(12000);
								passwordCookie.setPath(VaadinService.getCurrentRequest().getContextPath());
								VaadinService.getCurrentResponse().addCookie(passwordCookie);
								
								Notification.show("บันทึกสำเร็จ", Type.HUMANIZED_MESSAGE);
							} catch (Exception e) {
								Notification.show("บันทึกไม่สำเร็จ", Type.HUMANIZED_MESSAGE);
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
		
		schoolSave = new Button("บันทึก",FontAwesome.SAVE);
		schoolSave.addClickListener(saveDataListener);
		schoolForm.addComponent(schoolSave);
	}
	
	private void initPeriodLayout(){
		periodForm = new FormLayout();
		periodForm.setSizeFull();
		periodForm.setStyleName("border-white");
		gridLayout.addComponent(periodForm);
		
		recruitTitle = new Label("ช่วงสมัครเรียน");
		periodForm.addComponent(recruitTitle);
		
		recruitStartDate = new PopupDateField();
		recruitStartDate.setInputPrompt("วันเริ่มสมัคร");
		periodForm.addComponent(recruitStartDate);
		
		recruitEndDate = new PopupDateField();
		recruitEndDate.setInputPrompt("วันสิ้นสุดสมัคร");
		periodForm.addComponent(recruitEndDate);
		
		preriodSave = new Button("บันทึก",FontAwesome.SAVE);
		preriodSave.addClickListener(saveDataListener);
		periodForm.addComponent(preriodSave);
	}

	private void initShortUrlLayout(){
		shortUrlForm = new FormLayout();
		shortUrlForm.setSizeFull();
		shortUrlForm.setStyleName("border-white");
		gridLayout.addComponent(shortUrlForm);
		
		shortUrlTitle = new Label("ลิ้งเข้าระบบ [www.schoolosplus.com/url]");
		shortUrlForm.addComponent(shortUrlTitle);
		
		shortUrl = new TextField();
		shortUrl.setInputPrompt("url");
		shortUrl.setNullRepresentation("");
		shortUrlForm.addComponent(shortUrl);
		
		shortUrlSave = new Button("บันทึก",FontAwesome.SAVE);
		shortUrlSave.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event) {
				schoolContainer.addContainerFilter(new Equal(SchoolSchema.SHORT_URL, shortUrl.getValue()));
				/* ตรวจสอบ Email ซ้ำ */
				if(schoolContainer.size() > 0){
					Notification.show("ไม่สามารถใช้ URL นี้ได้ กรุณาเปลี่ยนใหม่อีกครั้ง", Type.WARNING_MESSAGE);
					return;
				}					
				/* ลบ WHERE ออกจาก Query เพื่อป้องกันการค้างของคำสั่่งจากการทำงานอื่นที่เรียกตัวแปรไปใช้ */
				schoolContainer.removeAllContainerFilters();
				saveData();
			}
		});
		shortUrlForm.addComponent(shortUrlSave);		
	}
	
	private void initStudentCodeLayout(){
		studentCodeForm = new FormLayout();
		studentCodeForm.setSizeFull();
		studentCodeForm.setStyleName("border-white");
		gridLayout.addComponent(studentCodeForm);
		
		studentCodeTitle = new Label("รหัสนักเรียน");
		studentCodeForm.addComponent(studentCodeTitle);
		
		codeGenerateType = new OptionGroup();
		codeGenerateType.setContainerDataSource(new StudentCodeGenerateType());
		codeGenerateType.setItemCaptionPropertyId("name");
		codeGenerateType.setImmediate(true);
		codeGenerateType.setNullSelectionAllowed(false);
		codeGenerateType.setValue(0);
		codeGenerateType.addValueChangeListener(new ValueChangeListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				if(event.getProperty().getValue() != null){
					if(event.getProperty().getValue().toString().equals("0"))
						studentCodeFirst.setVisible(false);
					else
						studentCodeFirst.setVisible(true);
				}
					
			}
		});
		studentCodeForm.addComponent(codeGenerateType);
		
		studentCodeFirst = new TextField();
		studentCodeFirst.setInputPrompt("รหัสเริ่มต้น");
		studentCodeFirst.setVisible(false);
		studentCodeFirst.setNullRepresentation("");
		studentCodeForm.addComponent(studentCodeFirst);
		
		studentCodeSave = new Button("บันทึก",FontAwesome.SAVE);
		studentCodeSave.addClickListener(saveDataListener);
		studentCodeForm.addComponent(studentCodeSave);
	}

	private void initialDataBinding(){	
		schoolContainer.addContainerFilter(new Equal(SchoolSchema.SCHOOL_ID,
				UI.getCurrent().getSession().getAttribute(SessionSchema.SCHOOL_ID)));

		for(Object itemId:schoolContainer.getItemIds()){
			schoolItem = schoolContainer.getItem(itemId);
		}
		schoolContainer.removeAllContainerFilters();
		
		schoolBinder = new FieldGroup(schoolItem);
		schoolBinder.setBuffered(true);
		schoolBinder.bind(schoolName, SchoolSchema.NAME);
		schoolBinder.bind(schoolProvinceId, SchoolSchema.PROVINCE_ID);
		schoolBinder.bind(firstname, SchoolSchema.FIRSTNAME);
		schoolBinder.bind(lastname, SchoolSchema.LASTNAME);
		schoolBinder.bind(email, SchoolSchema.EMAIL);
		schoolBinder.bind(recruitStartDate, SchoolSchema.RECRUIT_START_DATE);
		schoolBinder.bind(recruitEndDate, SchoolSchema.RECRUIT_END_DATE);
		schoolBinder.bind(codeGenerateType, SchoolSchema.STUDENT_CODE_GENERATE_TYPE);
		schoolBinder.bind(studentCodeFirst, SchoolSchema.STUDENT_CODE_FIRST);
		schoolBinder.bind(shortUrl, SchoolSchema.SHORT_URL);
		
		email.setReadOnly(true);
	}

	private void saveData(){
		try {
			if(schoolBinder.isValid()){
				schoolBinder.commit();
				schoolContainer.commit();
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

	private ClickListener saveDataListener = new ClickListener() {
		private static final long serialVersionUID = 1L;

		@Override
		public void buttonClick(ClickEvent event) {
			saveData();
		}
	};
}
