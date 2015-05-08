package com.ies.schoolos.component.personnel;

import java.util.Date;

import org.vaadin.dialogs.ConfirmDialog;

import com.ies.schoolos.component.ui.NumberField;
import com.ies.schoolos.container.Container;
import com.ies.schoolos.schema.UserSchema;
import com.ies.schoolos.schema.info.FamilySchema;
import com.ies.schoolos.schema.info.PersonnelSchema;
import com.ies.schoolos.type.AliveStatus;
import com.ies.schoolos.type.BankAccountType;
import com.ies.schoolos.type.Blood;
import com.ies.schoolos.type.EmployeeType;
import com.ies.schoolos.type.Gender;
import com.ies.schoolos.type.LicenseLecturerType;
import com.ies.schoolos.type.MaritalStatus;
import com.ies.schoolos.type.Nationality;
import com.ies.schoolos.type.Occupation;
import com.ies.schoolos.type.PeopleIdType;
import com.ies.schoolos.type.PersonnelCodeGenerateType;
import com.ies.schoolos.type.PersonnelStatus;
import com.ies.schoolos.type.Prename;
import com.ies.schoolos.type.Race;
import com.ies.schoolos.type.Religion;
import com.ies.schoolos.type.dynamic.City;
import com.ies.schoolos.type.dynamic.Department;
import com.ies.schoolos.type.dynamic.District;
import com.ies.schoolos.type.dynamic.JobPosition;
import com.ies.schoolos.type.dynamic.Postcode;
import com.ies.schoolos.type.dynamic.Province;
import com.ies.schoolos.utility.DateTimeUtil;
import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.filter.Compare.Equal;
import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.data.validator.EmailValidator;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Field;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

public class PersonnelGraduatedView extends TabSheet {
private static final long serialVersionUID = 1L;
	
	public boolean isEdit = false;
	public boolean isInsertParents = true;
	public boolean isDuplicateFather = false;
	public boolean isDuplicateMother = false;
	public boolean isDuplicateSpouse = false;
	
	/* ที่เก็บ Id Auto Increment เมื่อมีการ Commit SQLContainer 
	 * 0 แทนถึง id บิดา
	 * 1 แทนถึง id มารดา
	 * 2 แทนถึง id คู่สมรส
	 * 3 แทนถึง id เจ้าหน้าที่
	 * */
	public Object pkStore[] = new Object[4];

	public SQLContainer pSqlContainer = Container.getPersonnelContainer();
	public SQLContainer fSqlContainer = Container.getFamilyContainer();
	public SQLContainer userSqlContainer = Container.getUserContainer();
	
	public FieldGroup personnelBinder;
	public FieldGroup fatherBinder;
	public FieldGroup motherBinder;
	public FieldGroup spouseBinder;

	private FormLayout generalForm;
	private OptionGroup peopleIdType;
	private TextField peopleId;
	private ComboBox prename;
	private TextField firstname;
	private TextField lastname;
	private TextField firstnameNd;
	private TextField lastnameNd;
	private TextField firstnameRd;
	private TextField lastnameRd;
	private TextField nickname;
	private OptionGroup gender;
	private ComboBox religion;
	private ComboBox race;
	private ComboBox nationality;
	private ComboBox maritalStatus;
	private PopupDateField birthDate;
	private ComboBox blood;
	private NumberField height;
	private NumberField weight;
	private TextField congenitalDisease;
	private Button workNext;
	
	/*private FormLayout graduatedForm;
	private TextField institute;
	private ComboBox graduatedLevelId;
	private TextField degree;
	private TextField major;
	private TextField minor;
	private TextArea description;
	private TextField graduatedYear;
	private TextArea location;
	private ComboBox instituteProvinceId;
	private Button generalBack;
	private Button addressNext;*/
	
	private FormLayout workForm;
	private ComboBox jobPosition;
	private OptionGroup autoGenerate;
	private TextField personnelCode;
	private ComboBox personnelStatus;
	private PopupDateField startWorkDate;
	private ComboBox department;
	private ComboBox employmentType;
	private TextField bankName;
	private TextField bankAccountNumber;
	private ComboBox bankAccountType;
	private TextField bankaccountName;
	private TextField bankaccountBranch;
	private ComboBox bankProvinceId;
	private Button generalBack;
	private Button licenseeNext;
	
	private FormLayout licenseeForm;
	private TextField licenseLecturerNumber;
	private ComboBox licenseLecturerType;
	private PopupDateField licenseLecturerIssuedDate;
	private PopupDateField licenseLecturerExpiredDate;
	private TextField license11Number;
	private TextField licenseIssueArea;
	private ComboBox licenseIssueProvinceId;
	private TextField license17Number;
	private TextField license18Number;
	private TextField license19Number;
	private TextField fillDegreePost;
	private PopupDateField fillDegreePostDate;
	private Button workBack;
	private Button addressNext;
	
	private FormLayout addressForm;
	private TextField tel;
	private TextField mobile;
	private TextField email;
	private TextArea currentAddress;
	private ComboBox currentCity;
	private ComboBox currentDistrict;
	private ComboBox currentProvince;
	private ComboBox currentPostcode;
	private CheckBox isSameCurrentAddress;
	private TextArea censusAddress;
	private ComboBox censusCity;
	private ComboBox censusDistrict;
	private ComboBox censusProvince;
	private ComboBox censusPostcode;
	private Button licensessBack;
	private Button fatherNext;
	
	private FormLayout fatherForm;
	private OptionGroup fPeopleIdType;
	private TextField fPeopleid;
	private ComboBox fPrename;
	private TextField fFirstname;
	private TextField fLastname;
	private TextField fFirstnameNd;
	private TextField fLastnameNd;
	private OptionGroup fGender;
	private ComboBox fReligion;
	private ComboBox fRace;
	private ComboBox fNationality;
	private PopupDateField fBirthDate;
	private TextField fTel;
	private TextField fMobile;
	private TextField fEmail;
	private NumberField fSalary;
	private ComboBox fAliveStatus;
	private ComboBox fOccupation;
	private TextArea fJobAddress;
	private TextArea fCurrentAddress;
	private ComboBox fCurrentCity;
	private ComboBox fCurrentDistrict;
	private ComboBox fCurrentProvinceId;
	private ComboBox fCurrentPostcode;
	private Button addressBack;
	private Button motherNext;
	
	private FormLayout motherForm;
	private OptionGroup mPeopleIdType;
	private TextField mPeopleid;
	private ComboBox mPrename;
	private TextField mFirstname;
	private TextField mLastname;
	private TextField mFirstnameNd;
	private TextField mLastnameNd;
	private OptionGroup mGender;
	private ComboBox mReligion;
	private ComboBox mRace;
	private ComboBox mNationality;
	private PopupDateField mBirthDate;	
	private TextField mTel;
	private TextField mMobile;
	private TextField mEmail;
	private NumberField mSalary;
	private ComboBox mAliveStatus;
	private ComboBox mOccupation;
	private TextArea mJobAddress;
	private TextArea mCurrentAddress;
	private ComboBox mCurrentCity;
	private ComboBox mCurrentDistrict;
	private ComboBox mCurrentProvinceId;
	private ComboBox mCurrentPostcode;
	private Button fatherBack;
	private Button spouseNext;
	
	private FormLayout spouseForm;
	private OptionGroup sPeopleIdType;
	private TextField sPeopleid;
	private ComboBox sPrename;
	private TextField sFirstname;
	private TextField sLastname;
	private TextField sFirstnameNd;
	private TextField sLastnameNd;
	private OptionGroup sGender;
	private ComboBox sReligion;
	private ComboBox sRace;
	private ComboBox sNationality;
	private PopupDateField sBirthDate;	
	private TextField sTel;
	private TextField sMobile;
	private TextField sEmail;
	private NumberField sSalary;
	private ComboBox sAliveStatus;
	private ComboBox sOccupation;
	private TextArea sJobAddress;
	private TextArea sCurrentAddress;
	private ComboBox sCurrentCity;
	private ComboBox sCurrentDistrict;
	private ComboBox sCurrentProvinceId;
	private ComboBox sCurrentPostcode;
	private Button motherBack;
	private Button finish;
	private Button print;
	
	public PersonnelGraduatedView() {
		buildMainLayout();
	}
	
	private void buildMainLayout()  {
		setWidth("100%");
		setHeight("100%");
		generalInfoLayout();
		workForm();
		licenseeForm();
		addressForm();
		fatherForm();
		motherForm();
		spouseForm();
		initFieldGroup();
	}
	
	/*สร้าง Layout สำหรับข้อมูลทั่วไปนักเรียน*/
	private void generalInfoLayout()  {
		generalForm = new FormLayout();
		generalForm.setSizeUndefined();
		generalForm.setMargin(true);
		addTab(generalForm,"ข้อมูลทั่วไป", FontAwesome.CHILD);
		
		peopleIdType = new OptionGroup("ประเภทบัตร",new PeopleIdType());
		peopleIdType.setItemCaptionPropertyId("name");
		peopleIdType.setImmediate(true);
		peopleIdType.setRequired(true);
		peopleIdType.setNullSelectionAllowed(false);
		peopleIdType.setWidth("-1px");
		peopleIdType.setHeight("-1px");
		generalForm.addComponent(peopleIdType);
		
		peopleId = new TextField("หมายเลขประชาชน");
		peopleId.setInputPrompt("หมายเลขประชาชน");
		peopleId.setImmediate(false);
		peopleId.setRequired(true);
		peopleId.setWidth("-1px");
		peopleId.setHeight("-1px");
		peopleId.setNullRepresentation("");
		peopleId.addValidator(new StringLengthValidator("ข้อมูลไม่ถูกต้อง", 13, 20, false));
		peopleId.addTextChangeListener(new TextChangeListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void textChange(TextChangeEvent event) {
				if(event.getText() != null){
					if(event.getText().length() >= 13){
						pSqlContainer.addContainerFilter(new Equal(PersonnelSchema.PEOPLE_ID,event.getText()));
						if(pSqlContainer.size() > 0){
							disableDuplicatePeopleIdForm();
							Notification.show("หมายเลขประชาชนถูกใช้งานแล้ว กรุณาระบุใหม่อีกครั้ง", Type.WARNING_MESSAGE);
						}else{
							enableDuplicatePeopleIdForm();
						}
						pSqlContainer.removeAllContainerFilters();
					}
				}
			}
		});
		generalForm.addComponent(peopleId);
		
		prename = new ComboBox("ชื่อต้น",new Prename());
		prename.setInputPrompt("กรุณาเลือก");
		prename.setItemCaptionPropertyId("name");
		prename.setImmediate(true);
        prename.setNullSelectionAllowed(false);
        prename.setRequired(true);
		prename.setWidth("-1px");
		prename.setHeight("-1px");
		prename.setFilteringMode(FilteringMode.CONTAINS);
		generalForm.addComponent(prename);
		
		firstname = new TextField("ชื่อ");
		firstname.setInputPrompt("ชื่อ");
		firstname.setImmediate(false);
		firstname.setRequired(true);
		firstname.setWidth("-1px");
		firstname.setHeight("-1px");
		firstname.setNullRepresentation("");
		generalForm.addComponent(firstname);
		
		lastname = new TextField("สกุล");
		lastname.setInputPrompt("สกุล");
		lastname.setImmediate(false);
		lastname.setRequired(true);
		lastname.setWidth("-1px");
		lastname.setHeight("-1px");
		lastname.setNullRepresentation("");
		generalForm.addComponent(lastname);

		firstnameNd = new TextField("ชื่ออังกฤษ");
		firstnameNd.setInputPrompt("ชื่ออังกฤษ");
		firstnameNd.setImmediate(false);
		firstnameNd.setWidth("-1px");
		firstnameNd.setHeight("-1px");
		firstnameNd.setNullRepresentation("");
		generalForm.addComponent(firstnameNd);
		
		lastnameNd = new TextField("สกุลอังกฤษ");
		lastnameNd.setInputPrompt("สกุลอังกฤษ");
		lastnameNd.setImmediate(false);
		lastnameNd.setWidth("-1px");
		lastnameNd.setHeight("-1px");
		lastnameNd.setNullRepresentation("");
		generalForm.addComponent(lastnameNd);
		
		firstnameRd = new TextField("ชื่อภาษาที่สาม");
		firstnameRd.setInputPrompt("ชื่อภาษาที่สาม");
		firstnameRd.setImmediate(false);
		firstnameRd.setWidth("-1px");
		firstnameRd.setHeight("-1px");
		firstnameRd.setNullRepresentation("");
		generalForm.addComponent(firstnameRd);
		
		lastnameRd = new TextField("สกุลภาษาที่สาม");
		lastnameRd.setInputPrompt("สกุลภาษาที่สาม");
		lastnameRd.setImmediate(false);
		lastnameRd.setWidth("-1px");
		lastnameRd.setHeight("-1px");
		lastnameRd.setNullRepresentation("");
		generalForm.addComponent(lastnameRd);
		
		nickname = new TextField("ชื่อเล่น");
		nickname.setInputPrompt("ชื่อเล่น");
		nickname.setImmediate(false);
		nickname.setWidth("-1px");
		nickname.setHeight("-1px");
		nickname.setNullRepresentation("");
		generalForm.addComponent(nickname);
		
		gender = new OptionGroup("เพศ",new Gender());
		gender.setItemCaptionPropertyId("name");
		gender.setImmediate(true);
		gender.setNullSelectionAllowed(false);
		gender.setRequired(true);
		gender.setWidth("-1px");
		gender.setHeight("-1px");
		generalForm.addComponent(gender);
		
		religion = new ComboBox("ศาสนา",new Religion());
		religion.setInputPrompt("กรุณาเลือก");
		religion.setItemCaptionPropertyId("name");
		religion.setImmediate(true);
		religion.setNullSelectionAllowed(false);
		religion.setRequired(true);
		religion.setWidth("-1px");
		religion.setHeight("-1px");
		religion.setFilteringMode(FilteringMode.CONTAINS);
		generalForm.addComponent(religion);
		
		race = new ComboBox("เชื้อชาติ",new Race());
		race.setInputPrompt("กรุณาเลือก");
		race.setItemCaptionPropertyId("name");
		race.setImmediate(true);
		race.setNullSelectionAllowed(false);
		race.setRequired(true);
		race.setWidth("-1px");
		race.setHeight("-1px");
		race.setFilteringMode(FilteringMode.CONTAINS);
		generalForm.addComponent(race);
		
		nationality = new ComboBox("สัญชาติ",new Nationality());
		nationality.setInputPrompt("กรุณาเลือก");
		nationality.setItemCaptionPropertyId("name");
		nationality.setImmediate(true);
		nationality.setNullSelectionAllowed(false);
		nationality.setRequired(true);
		nationality.setWidth("-1px");
		nationality.setHeight("-1px");
		nationality.setFilteringMode(FilteringMode.CONTAINS);
		generalForm.addComponent(nationality);
		
		maritalStatus = new ComboBox("สถานภาพ",new MaritalStatus());
		maritalStatus.setInputPrompt("กรุณาเลือก");
		maritalStatus.setItemCaptionPropertyId("name");
		maritalStatus.setImmediate(true);
		maritalStatus.setNullSelectionAllowed(false);
		maritalStatus.setRequired(true);
		maritalStatus.setWidth("-1px");
		maritalStatus.setHeight("-1px");
		maritalStatus.setFilteringMode(FilteringMode.CONTAINS);
		generalForm.addComponent(maritalStatus);
		
		birthDate = new PopupDateField("วัน เดือน ปี เกิด");
		birthDate.setInputPrompt("วว/ดด/ปปปป(คศ)");
		birthDate.setImmediate(false);
		birthDate.setRequired(true);
		birthDate.setWidth("-1px");
		birthDate.setHeight("-1px");
		generalForm.addComponent(birthDate);
		
		blood = new ComboBox("หมู่เลือด",new Blood());
		blood.setInputPrompt("กรุณาเลือก");
		blood.setItemCaptionPropertyId("name");
		blood.setImmediate(true);
		blood.setNullSelectionAllowed(false);
		blood.setRequired(true);
		blood.setWidth("-1px");
		blood.setHeight("-1px");
		blood.setFilteringMode(FilteringMode.CONTAINS);
		generalForm.addComponent(blood);
		
		height = new NumberField("ส่วนสูง");
		height.setInputPrompt("ส่วนสูง");
		height.setImmediate(false);
		height.setWidth("-1px");
		height.setHeight("-1px");
		height.setNullRepresentation("");
		generalForm.addComponent(height);
		
		weight = new NumberField("น้ำหนัก");
		weight.setInputPrompt("น้ำหนัก");
		weight.setImmediate(false);
		weight.setWidth("-1px");
		weight.setHeight("-1px");
		weight.setNullRepresentation("");
		generalForm.addComponent(weight);
		
		congenitalDisease = new TextField("โรคประจำตัว");
		congenitalDisease.setInputPrompt("โรคประจำตัว");
		congenitalDisease.setImmediate(false);
		congenitalDisease.setWidth("-1px");
		congenitalDisease.setHeight("-1px");
		congenitalDisease.setNullRepresentation("");
		generalForm.addComponent(congenitalDisease);
		
		HorizontalLayout buttonLayout = new HorizontalLayout();
		buttonLayout.setSpacing(true);
		buttonLayout.setWidth("100%");
		generalForm.addComponent(buttonLayout);
		
		workNext = new Button(FontAwesome.ARROW_RIGHT);
		workNext.setWidth("100%");
		workNext.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 1L;
			@Override
			public void buttonClick(ClickEvent event) {
				setSelectedTab(workForm);
			}
		});
		buttonLayout.addComponent(workNext);	
	}
	
	/* สร้าง Layout สำหรับข้อมูลการทำงาน */
	private void workForm(){
		workForm = new FormLayout();
		workForm.setSizeUndefined();
		workForm.setMargin(true);
		addTab(workForm,"ข้อมูลการทำงาน", FontAwesome.GRADUATION_CAP);

		jobPosition = new ComboBox("ตำแหน่ง",new JobPosition());
		jobPosition.setInputPrompt("กรุณาเลือก");
		jobPosition.setItemCaptionPropertyId("name");
		jobPosition.setImmediate(true);
		jobPosition.setNullSelectionAllowed(false);
		jobPosition.setRequired(true);
		jobPosition.setWidth("-1px");
		jobPosition.setHeight("-1px");
		jobPosition.setFilteringMode(FilteringMode.CONTAINS);
		jobPosition.addValueChangeListener(new ValueChangeListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				if(event.getProperty().getValue() != null){
					if(autoGenerate.getValue() != null){
						generatePersonnelCode(event.getProperty().getValue().toString(), autoGenerate.getValue().toString());
					}
				}
			}
		});
		workForm.addComponent(jobPosition);
		
		
		autoGenerate = new OptionGroup("กำหนดรหัสประจำตัว",new PersonnelCodeGenerateType());
		autoGenerate.setItemCaptionPropertyId("name");
		autoGenerate.setImmediate(true);
		autoGenerate.setRequired(true);
		autoGenerate.setNullSelectionAllowed(false);
		autoGenerate.setWidth("-1px");
		autoGenerate.setHeight("-1px");
		autoGenerate.setValue(1);
		autoGenerate.addValueChangeListener(new ValueChangeListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				if(event.getProperty().getValue() != null){
					if(jobPosition.getValue() != null){
						generatePersonnelCode(jobPosition.getValue().toString(), event.getProperty().getValue().toString());
					}else{
						if(event.getProperty().getValue().equals("0"))
							Notification.show("กรุณาระบุุตำแหน่งเพื่อสร้างรหัสประจำตัวอัตโนมัติ", Type.WARNING_MESSAGE);
					}
				}
					
			}
		});
		workForm.addComponent(autoGenerate);
		
		personnelCode = new TextField("รหัสประจำตัว");
		personnelCode.setInputPrompt("รหัสประจำตัว");
		personnelCode.setImmediate(false);
		personnelCode.setRequired(true);
		personnelCode.setEnabled(false);
		personnelCode.setWidth("-1px");
		personnelCode.setHeight("-1px");
		personnelCode.setNullRepresentation("");
		workForm.addComponent(personnelCode);
		
		personnelStatus = new ComboBox("สถานะบุคลากร",new PersonnelStatus());
		personnelStatus.setInputPrompt("กรุณาเลือก");
		personnelStatus.setItemCaptionPropertyId("name");
		personnelStatus.setImmediate(true);
		personnelStatus.setNullSelectionAllowed(false);
		personnelStatus.setRequired(true);
		personnelStatus.setWidth("-1px");
		personnelStatus.setHeight("-1px");
		personnelStatus.setFilteringMode(FilteringMode.CONTAINS);
		workForm.addComponent(personnelStatus);
		
		startWorkDate = new PopupDateField("วัน เดือน ปี เริ่มทำงาน");
		startWorkDate.setInputPrompt("วว/ดด/ปปปป(คศ)");
		startWorkDate.setImmediate(false);
		startWorkDate.setRequired(true);
		startWorkDate.setWidth("-1px");
		startWorkDate.setHeight("-1px");
		workForm.addComponent(startWorkDate);
		
		department = new ComboBox("แผนก",new Department());
		department.setInputPrompt("กรุณาเลือก");
		department.setItemCaptionPropertyId("name");
		department.setImmediate(true);
		department.setNullSelectionAllowed(false);
		department.setRequired(true);
		department.setWidth("-1px");
		department.setHeight("-1px");
		department.setFilteringMode(FilteringMode.CONTAINS);
		workForm.addComponent(department);

		employmentType = new ComboBox("ประเภทการว่าจ้าง",new EmployeeType());
		employmentType.setInputPrompt("กรุณาเลือก");
		employmentType.setItemCaptionPropertyId("name");
		employmentType.setImmediate(true);
		employmentType.setNullSelectionAllowed(false);
		employmentType.setRequired(true);
		employmentType.setWidth("-1px");
		employmentType.setHeight("-1px");
		employmentType.setFilteringMode(FilteringMode.CONTAINS);
		workForm.addComponent(employmentType);
		
		bankaccountName = new TextField("ชื่อบัญชี");
		bankaccountName.setInputPrompt("ชื่อบัญชี");
		bankaccountName.setImmediate(false);
		bankaccountName.setWidth("-1px");
		bankaccountName.setHeight("-1px");
		bankaccountName.setNullRepresentation("");
		workForm.addComponent(bankaccountName);
		
		bankAccountNumber = new TextField("เลขบัญชี");
		bankAccountNumber.setInputPrompt("เลขบัญชี");
		bankAccountNumber.setImmediate(false);
		bankAccountNumber.setWidth("-1px");
		bankAccountNumber.setHeight("-1px");
		bankAccountNumber.setNullRepresentation("");
		workForm.addComponent(bankAccountNumber);
		
		bankAccountType = new ComboBox("ประเภทบัญชี",new BankAccountType());
		bankAccountType.setInputPrompt("กรุณาเลือก");
		bankAccountType.setItemCaptionPropertyId("name");
		bankAccountType.setImmediate(true);
		bankAccountType.setNullSelectionAllowed(false);
		bankAccountType.setWidth("-1px");
		bankAccountType.setHeight("-1px");
		bankAccountType.setFilteringMode(FilteringMode.CONTAINS);
		workForm.addComponent(bankAccountType);

		bankName = new TextField("ชื่อธนาคาร");
		bankName.setInputPrompt("ชื่อธนาคาร");
		bankName.setImmediate(false);
		bankName.setWidth("-1px");
		bankName.setHeight("-1px");
		bankName.setNullRepresentation("");
		workForm.addComponent(bankName);
		
		bankaccountBranch = new TextField("สาขาธนาคาร");
		bankaccountBranch.setInputPrompt("สาขาธนาคาร");
		bankaccountBranch.setImmediate(false);
		bankaccountBranch.setWidth("-1px");
		bankaccountBranch.setHeight("-1px");
		bankaccountBranch.setNullRepresentation("");
		workForm.addComponent(bankaccountBranch);
		
		bankProvinceId = new ComboBox("จังหวัดธนาคาร",new Province());
		bankProvinceId.setInputPrompt("กรุณาเลือก");
		bankProvinceId.setItemCaptionPropertyId("name");
		bankProvinceId.setImmediate(true);
		bankProvinceId.setNullSelectionAllowed(false);
		bankProvinceId.setWidth("-1px");
		bankProvinceId.setHeight("-1px");
		bankProvinceId.setFilteringMode(FilteringMode.CONTAINS);
		bankProvinceId.addValueChangeListener(new ValueChangeListener() {
			private static final long serialVersionUID = 1L;
			@Override
			public void valueChange(ValueChangeEvent event) {
				if(event.getProperty().getValue() != null)
					currentDistrict.setContainerDataSource(new District(Integer.parseInt(event.getProperty().getValue().toString())));
			}
		});
		workForm.addComponent(bankProvinceId);
		
		HorizontalLayout buttonLayout = new HorizontalLayout();
		buttonLayout.setWidth("100%");
		buttonLayout.setSpacing(true);
		workForm.addComponent(buttonLayout);
		
		generalBack = new Button(FontAwesome.ARROW_LEFT);
		generalBack.setWidth("100%");
		generalBack.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 1L;
			@Override
			public void buttonClick(ClickEvent event) {
				setSelectedTab(generalForm);
			}
		});
		buttonLayout.addComponents(generalBack);
		

		licenseeNext = new Button(FontAwesome.ARROW_RIGHT);
		licenseeNext.setWidth("100%");
		licenseeNext.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 1L;
			@Override
			public void buttonClick(ClickEvent event) {
				setSelectedTab(licenseeForm);				
			}
		});
		
		buttonLayout.addComponents(licenseeNext);
	}
	
	private void licenseeForm(){
		licenseeForm = new FormLayout();
		licenseeForm.setSizeUndefined();
		licenseeForm.setMargin(true);
		addTab(licenseeForm,"ข้อมูลวิชาชีพ", FontAwesome.BRIEFCASE);

		licenseLecturerNumber = new TextField("เลขวิชาชีพครู");
		licenseLecturerNumber.setInputPrompt("เลขวิชาชีพครู");
		licenseLecturerNumber.setImmediate(false);
		licenseLecturerNumber.setWidth("-1px");
		licenseLecturerNumber.setHeight("-1px");
		licenseLecturerNumber.setNullRepresentation("");
		licenseeForm.addComponent(licenseLecturerNumber);
		
		licenseLecturerType = new ComboBox("ประเภทใบประกอบวิชาชีพ",new LicenseLecturerType());
		licenseLecturerType.setInputPrompt("กรุณาเลือก");
		licenseLecturerType.setItemCaptionPropertyId("name");
		licenseLecturerType.setImmediate(true);
		licenseLecturerType.setNullSelectionAllowed(false);
		licenseLecturerType.setWidth("-1px");
		licenseLecturerType.setHeight("-1px");
		licenseLecturerType.setFilteringMode(FilteringMode.CONTAINS);
		licenseeForm.addComponent(licenseLecturerType);
		
		licenseLecturerIssuedDate = new PopupDateField("วัน เดือน ปี ที่ออก");
		licenseLecturerIssuedDate.setInputPrompt("วว/ดด/ปปปป(คศ)");
		licenseLecturerIssuedDate.setImmediate(false);
		licenseLecturerIssuedDate.setWidth("-1px");
		licenseLecturerIssuedDate.setHeight("-1px");
		licenseeForm.addComponent(licenseLecturerIssuedDate);

		licenseLecturerExpiredDate = new PopupDateField("วัน เดือน ปี หมดอายุ");
		licenseLecturerExpiredDate.setInputPrompt("วว/ดด/ปปปป(คศ)");
		licenseLecturerExpiredDate.setImmediate(false);
		licenseLecturerExpiredDate.setWidth("-1px");
		licenseLecturerExpiredDate.setHeight("-1px");
		licenseeForm.addComponent(licenseLecturerExpiredDate);

		license11Number = new TextField("เลขที่ใบอนุญาติ สช 11");
		license11Number.setInputPrompt("เลขที่ใบอนุญาติ สช 11");
		license11Number.setImmediate(false);
		license11Number.setWidth("-1px");
		license11Number.setHeight("-1px");
		license11Number.setNullRepresentation("");
		licenseeForm.addComponent(license11Number);

		licenseIssueArea = new TextField("เขตพื้นที่ ที่ออก");
		licenseIssueArea.setInputPrompt("เขตพื้นที่ ที่ออก");
		licenseIssueArea.setImmediate(false);
		licenseIssueArea.setWidth("-1px");
		licenseIssueArea.setHeight("-1px");
		licenseIssueArea.setNullRepresentation("");
		licenseeForm.addComponent(licenseIssueArea);

		licenseIssueProvinceId = new ComboBox("จังหวัดที่ออก",new Province());
		licenseIssueProvinceId.setInputPrompt("กรุณาเลือก");
		licenseIssueProvinceId.setItemCaptionPropertyId("name");
		licenseIssueProvinceId.setImmediate(true);
		licenseIssueProvinceId.setNullSelectionAllowed(false);
		licenseIssueProvinceId.setWidth("-1px");
		licenseIssueProvinceId.setHeight("-1px");
		licenseIssueProvinceId.setFilteringMode(FilteringMode.CONTAINS);
		licenseeForm.addComponent(bankProvinceId);
		
		license17Number = new TextField("เลขที่ใบอนุญาติ สช 17");
		license17Number.setInputPrompt("เลขที่ใบอนุญาติ สช 17");
		license17Number.setImmediate(false);
		license17Number.setWidth("-1px");
		license17Number.setHeight("-1px");
		license17Number.setNullRepresentation("");
		licenseeForm.addComponent(license17Number);

		license18Number = new TextField("เลขที่ใบอนุญาติ สช 18");
		license18Number.setInputPrompt("เลขที่ใบอนุญาติ สช 18");
		license18Number.setImmediate(false);
		license18Number.setWidth("-1px");
		license18Number.setHeight("-1px");
		license18Number.setNullRepresentation("");
		licenseeForm.addComponent(license18Number);

		license19Number = new TextField("เลขที่ใบอนุญาติ สช 19");
		license19Number.setInputPrompt("เลขที่ใบอนุญาติ สช 19");
		license19Number.setImmediate(false);
		license19Number.setWidth("-1px");
		license19Number.setHeight("-1px");
		license19Number.setNullRepresentation("");
		licenseeForm.addComponent(license19Number);

		fillDegreePost = new TextField("วุฒิที่ได้รับการบรรจุ");
		fillDegreePost.setInputPrompt("วุฒิที่ได้รับการบรรจุ");
		fillDegreePost.setImmediate(false);
		fillDegreePost.setWidth("-1px");
		fillDegreePost.setHeight("-1px");
		fillDegreePost.setNullRepresentation("");
		licenseeForm.addComponent(fillDegreePost);

		fillDegreePostDate = new PopupDateField("วัน เดือน ปีที่ได้รับการบรรจุ");
		fillDegreePostDate.setInputPrompt("วว/ดด/ปปปป(คศ)");
		fillDegreePostDate.setImmediate(false);
		fillDegreePostDate.setWidth("-1px");
		fillDegreePostDate.setHeight("-1px");
		licenseeForm.addComponent(fillDegreePostDate);
		
		HorizontalLayout buttonLayout = new HorizontalLayout();
		buttonLayout.setWidth("100%");
		buttonLayout.setSpacing(true);
		licenseeForm.addComponent(buttonLayout);
		
		workBack = new Button(FontAwesome.ARROW_LEFT);
		workBack.setWidth("100%");
		workBack.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 1L;
			@Override
			public void buttonClick(ClickEvent event) {
				setSelectedTab(workForm);				
			}
		});
		buttonLayout.addComponents(workBack);
		
		addressNext = new Button(FontAwesome.ARROW_RIGHT);
		addressNext.setWidth("100%");
		addressNext.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 1L;
			@Override
			public void buttonClick(ClickEvent event) {
				setSelectedTab(addressForm);				
			}
		});
		buttonLayout.addComponents(addressNext);
	}
	
	/*สร้าง Layout สำหรับประวัติการศึกษาของนักเรียน*/
	/*private void graduatedForm(){
		graduatedForm = new FormLayout();
		graduatedForm.setSizeUndefined();
		graduatedForm.setMargin(true);
		addTab(graduatedForm,"ข้อมูลการศึกษา", FontAwesome.GRADUATION_CAP);
		
		institute = new TextField("โรงเรียนที่จบ");
		institute.setInputPrompt("ชื่อโรงเรียน");
		institute.setImmediate(false);
		institute.setRequired(true);
		institute.setWidth("-1px");
		institute.setHeight("-1px");
		institute.setNullRepresentation("");
		graduatedForm.addComponent(institute);

		instituteProvinceId = new ComboBox("จังหวัด",new Province());
		instituteProvinceId.setInputPrompt("กรุณาเลือก");
		instituteProvinceId.setItemCaptionPropertyId("name");
		instituteProvinceId.setImmediate(true);
		instituteProvinceId.setNullSelectionAllowed(false);
		instituteProvinceId.setRequired(true);
		instituteProvinceId.setWidth("-1px");
		instituteProvinceId.setHeight("-1px");
		instituteProvinceId.setFilteringMode(FilteringMode.CONTAINS);
		graduatedForm.addComponent(instituteProvinceId);

		graduatedGpa = new NumberField("ผลการเรียนเฉลี่ย");
		graduatedGpa.setInputPrompt("ผลการเรียน");
		graduatedGpa.setImmediate(false);
		graduatedGpa.setRequired(true);
		graduatedGpa.setWidth("-1px");
		graduatedGpa.setHeight("-1px");
		graduatedGpa.setNullRepresentation("");
		//graduatedGpa.addValidator(new DoubleRangeValidator("ข้อมูลไม่ถูกต้อง", 0.0, 4.0));
		graduatedForm.addComponent(graduatedGpa);

		graduatedYear = new TextField("ปีที่จบ");
		graduatedYear.setInputPrompt("ปีที่จบ");
		graduatedYear.setImmediate(false);
		graduatedYear.setRequired(true);
		graduatedYear.setWidth("-1px");
		graduatedYear.setHeight("-1px");
		graduatedYear.setNullRepresentation("");
		//graduatedYear.addValidator(new IntegerRangeValidator("ข้อมูลไม่ถูกต้อง", 1900, 2600));
		graduatedForm.addComponent(graduatedYear);
		
		HorizontalLayout buttonLayout = new HorizontalLayout();
		buttonLayout.setWidth("100%");
		buttonLayout.setSpacing(true);
		graduatedForm.addComponent(buttonLayout);
		
		generalBack = new Button(FontAwesome.ARROW_LEFT);
		generalBack.setWidth("100%");
		generalBack.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 1L;
			@Override
			public void buttonClick(ClickEvent event) {
				setSelectedTab(generalForm);
			}
		});
		buttonLayout.addComponents(generalBack);
		
		addressNext = new Button(FontAwesome.ARROW_RIGHT);
		addressNext.setWidth("100%");
		addressNext.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 1L;
			@Override
			public void buttonClick(ClickEvent event) {
				setSelectedTab(addressForm);
			}
		});
		buttonLayout.addComponent(addressNext);
		
	}*/
	
	/*สร้าง Layout สำหรับที่อยู่ปัจจุบันของนักเรียน*/
	private void addressForm(){
		addressForm = new FormLayout();
		addressForm.setSizeUndefined();
		addressForm.setMargin(true);
		addTab(addressForm,"ข้อมูลติดต่อ", FontAwesome.BOOK);
		
		tel = new TextField("เบอร์โทร");
		tel.setInputPrompt("เบอร์โทร");
		tel.setImmediate(false);
		tel.setWidth("-1px");
		tel.setHeight("-1px");
		tel.setNullRepresentation("");
		addressForm.addComponent(tel);
		
		mobile = new TextField("มือถือ");
		mobile.setInputPrompt("มือถือ");
		mobile.setImmediate(false);
		mobile.setWidth("-1px");
		mobile.setHeight("-1px");
		mobile.setNullRepresentation("");
		addressForm.addComponent(mobile);
		
		email = new TextField("อีเมล์");
		email.setInputPrompt("อีเมล์");
		email.setImmediate(false);
		email.setWidth("-1px");
		email.setHeight("-1px");
		email.setRequired(true);
		email.addValidator(new EmailValidator("ข้อมูลไม่ถูกต้อง"));
		email.setNullRepresentation("");
		email.addTextChangeListener(new TextChangeListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void textChange(TextChangeEvent event) {
				if(!isEdit){
					if(event.getText() != null){
						if(event.getText().length() >= 13){
							
							userSqlContainer.addContainerFilter(new Equal(UserSchema.EMAIL,event.getText()));
							if(userSqlContainer.size() > 0){
								disableDuplicateEmailForm();
								Notification.show("อีเมล์ถูกใช้งานแล้ว กรุณาระบุใหม่อีกครั้ง", Type.WARNING_MESSAGE);
							}else{
								enableDuplicateEmailForm();
							}
							userSqlContainer.removeAllContainerFilters();
						}
					}
				}
			}
		});
		addressForm.addComponent(email);
		
		Label currentLabel = new Label("ที่อยู่ปัจจุบัน");
		addressForm.addComponent(currentLabel);
		
		currentAddress = new TextArea("ที่อยู่ปัจจุบัน");
		currentAddress.setInputPrompt("บ้านเลขที่ ซอย ถนน");
		currentAddress.setImmediate(false);
		currentAddress.setWidth("-1px");
		currentAddress.setHeight("-1px");
		currentAddress.setNullRepresentation("");
		addressForm.addComponent(currentAddress);
		
		currentProvince = new ComboBox("จังหวัด",new Province());
		currentProvince.setInputPrompt("กรุณาเลือก");
		currentProvince.setItemCaptionPropertyId("name");
		currentProvince.setImmediate(true);
		currentProvince.setNullSelectionAllowed(false);
		currentProvince.setWidth("-1px");
		currentProvince.setHeight("-1px");
		currentProvince.setFilteringMode(FilteringMode.CONTAINS);
		currentProvince.addValueChangeListener(new ValueChangeListener() {
			private static final long serialVersionUID = 1L;
			@Override
			public void valueChange(ValueChangeEvent event) {
				if(event.getProperty().getValue() != null)
					currentDistrict.setContainerDataSource(new District(Integer.parseInt(event.getProperty().getValue().toString())));
			}
		});
		addressForm.addComponent(currentProvince);
		
		currentDistrict = new ComboBox("อำเภอ");
		currentDistrict.setInputPrompt("กรุณาเลือก");
		currentDistrict.setItemCaptionPropertyId("name");
		currentDistrict.setImmediate(true);
		currentDistrict.setNullSelectionAllowed(false);
		currentDistrict.setWidth("-1px");
		currentDistrict.setHeight("-1px");
		currentDistrict.setFilteringMode(FilteringMode.CONTAINS);
		currentDistrict.addValueChangeListener(new ValueChangeListener() {
			private static final long serialVersionUID = 1L;
			@Override
			public void valueChange(ValueChangeEvent event) {
				if(event.getProperty().getValue() != null){
					currentCity.setContainerDataSource(new City(Integer.parseInt(event.getProperty().getValue().toString())));
					currentPostcode.setContainerDataSource(new Postcode(Integer.parseInt(event.getProperty().getValue().toString())));
				}
			}
		});
		addressForm.addComponent(currentDistrict);
		
		currentCity = new ComboBox("ตำบล");
		currentCity.setInputPrompt("กรุณาเลือก");
		currentCity.setItemCaptionPropertyId("name");
		currentCity.setImmediate(true);
		currentCity.setNullSelectionAllowed(false);
		currentCity.setWidth("-1px");
		currentCity.setHeight("-1px");
		currentCity.setFilteringMode(FilteringMode.CONTAINS);
		addressForm.addComponent(currentCity);
		
		currentPostcode = new ComboBox("รหัสไปรษณีย์");
		currentPostcode.setInputPrompt("กรุณาเลือก");
		currentPostcode.setItemCaptionPropertyId("name");
		currentPostcode.setImmediate(true);
		currentPostcode.setNullSelectionAllowed(false);
		currentPostcode.setWidth("-1px");
		currentPostcode.setHeight("-1px");
		currentPostcode.setFilteringMode(FilteringMode.CONTAINS);
		addressForm.addComponent(currentPostcode);
		
		isSameCurrentAddress = new CheckBox("ข้อมูลเดียวกับที่อยู่ปัจจุบัน");
		isSameCurrentAddress.setImmediate(true);
		isSameCurrentAddress.setWidth("-1px");
		isSameCurrentAddress.setHeight("-1px");
		isSameCurrentAddress.addValueChangeListener(new ValueChangeListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				if(event.getProperty().getValue() != null){
					if((boolean)event.getProperty().getValue()){
						censusAddress.setValue(currentAddress.getValue());
						censusProvince.setValue(currentProvince.getValue());
						censusDistrict.setValue(currentDistrict.getValue());
						censusCity.setValue(currentCity.getValue());
						censusPostcode.setValue(currentPostcode.getValue());
					}else{
						censusAddress.setValue(null);
						censusProvince.setValue(null);
						censusDistrict.setValue(null);
						censusCity.setValue(null);
						censusPostcode.setValue(null);
					}
				}
			}
		});
		addressForm.addComponent(isSameCurrentAddress);
		
		censusAddress = new TextArea("ที่อยู่ตามทะเบียนบ้าน");
		censusAddress.setInputPrompt("บ้านเลขที่ ซอย ถนน");
		censusAddress.setImmediate(false);
		censusAddress.setWidth("-1px");
		censusAddress.setHeight("-1px");
		censusAddress.setNullRepresentation("");
		addressForm.addComponent(censusAddress);
		
		censusProvince = new ComboBox("จังหวัดตามทะเบียนบ้าน",new Province());
		censusProvince.setInputPrompt("กรุณาเลือก");
		censusProvince.setItemCaptionPropertyId("name");
		censusProvince.setImmediate(true);
		censusProvince.setNullSelectionAllowed(false);
		censusProvince.setWidth("-1px");
		censusProvince.setHeight("-1px");
		censusProvince.setFilteringMode(FilteringMode.CONTAINS);
		censusProvince.addValueChangeListener(new ValueChangeListener() {
			private static final long serialVersionUID = 1L;
			@Override
			public void valueChange(ValueChangeEvent event) {
				if(event.getProperty().getValue() != null)
					censusDistrict.setContainerDataSource(new District(Integer.parseInt(event.getProperty().getValue().toString())));
			}
		});
		addressForm.addComponent(censusProvince);
		
		censusDistrict = new ComboBox("อำเภอตามทะเบียนบ้าน");
		censusDistrict.setInputPrompt("กรุณาเลือก");
		censusDistrict.setItemCaptionPropertyId("name");
		censusDistrict.setImmediate(true);
		censusDistrict.setNullSelectionAllowed(false);
		censusDistrict.setWidth("-1px");
		censusDistrict.setHeight("-1px");
		censusDistrict.setFilteringMode(FilteringMode.CONTAINS);
		censusDistrict.addValueChangeListener(new ValueChangeListener() {
			private static final long serialVersionUID = 1L;
			@Override
			public void valueChange(ValueChangeEvent event) {
				if(event.getProperty().getValue() != null){
					censusCity.setContainerDataSource(new City(Integer.parseInt(event.getProperty().getValue().toString())));
					censusPostcode.setContainerDataSource(new Postcode(Integer.parseInt(event.getProperty().getValue().toString())));
				}
			}
		});
		addressForm.addComponent(censusDistrict);
		
		censusCity = new ComboBox("ตำบลตามทะเบียนบ้าน");
		censusCity.setInputPrompt("กรุณาเลือก");
		censusCity.setItemCaptionPropertyId("name");
		censusCity.setImmediate(true);
		censusCity.setNullSelectionAllowed(false);
		censusCity.setWidth("-1px");
		censusCity.setHeight("-1px");
		censusCity.setFilteringMode(FilteringMode.CONTAINS);
		addressForm.addComponent(censusCity);
		
		censusPostcode = new ComboBox("รหัสไปรษณีย์ตามทะเบียนบ้าน");
		censusPostcode.setInputPrompt("กรุณาเลือก");
		censusPostcode.setItemCaptionPropertyId("name");
		censusPostcode.setImmediate(true);
		censusPostcode.setNullSelectionAllowed(false);
		censusPostcode.setWidth("-1px");
		censusPostcode.setHeight("-1px");
		censusPostcode.setFilteringMode(FilteringMode.CONTAINS);
		addressForm.addComponent(censusPostcode);
		
		HorizontalLayout buttonLayout = new HorizontalLayout();
		buttonLayout.setSpacing(true);
		buttonLayout.setWidth("100%");
		addressForm.addComponent(buttonLayout);
		
		licensessBack = new Button(FontAwesome.ARROW_LEFT);
		licensessBack.setWidth("100%");
		licensessBack.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 1L;
			@Override
			public void buttonClick(ClickEvent event) {
				setSelectedTab(licenseeForm);
			}
		});
		buttonLayout.addComponents(licensessBack);
		
		fatherNext = new Button(FontAwesome.SAVE);
		fatherNext.setWidth("100%");
		fatherNext.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 1L;
			@Override
			public void buttonClick(ClickEvent event) {
				ConfirmDialog.show(UI.getCurrent(), "ความพร้อมข้อมูล", "คุณต้องการเพิ่มข้อมูล บิดา มารดา คู่สมรส ใช่หรือไม่?", "ใช่", "ไม่", new ConfirmDialog.Listener() {
					private static final long serialVersionUID = 1L;
					public void onClose(ConfirmDialog dialog) {
						/* ตรวจสอบว่ามีข้อมูลบิดา มารดา คู่สมรสหรือไม่?
						 *  กรณี มีก็จะเข้าไปหน้าเพิ่มข้อมูลเจ้าหน้าที่
						 *  กรณี ไม่มี ก็จะบันทึกข้อมูลเลย */
		                if (dialog.isConfirmed()) {
		                	isInsertParents = true;
		                	setSelectedTab(fatherForm);
		                }else{
		                	isInsertParents = false;

		                	finish.click();
		                }
		            }
		        });
			}
		});
		
		buttonLayout.addComponents(fatherNext);
	}
	
	/*สร้าง Layout สำหรับบิดา*/
	private void fatherForm(){
		fatherForm = new FormLayout();
		fatherForm.setSizeUndefined();
		fatherForm.setMargin(true);
		addTab(fatherForm,"ข้อมูลบิดา", FontAwesome.MALE);
		
		fPeopleIdType = new OptionGroup("ประเภทบัตร",new PeopleIdType());
		fPeopleIdType.setItemCaptionPropertyId("name");
		fPeopleIdType.setImmediate(true);
		fPeopleIdType.setNullSelectionAllowed(false);
		fPeopleIdType.setRequired(true);
		fPeopleIdType.setWidth("-1px");
		fPeopleIdType.setHeight("-1px");
		fatherForm.addComponent(fPeopleIdType);
		
		fPeopleid = new TextField("หมายเลขประชาชน");
		fPeopleid.setInputPrompt("หมายเลขประชาชน");
		fPeopleid.setImmediate(false);
		fPeopleid.setRequired(true);
		fPeopleid.setWidth("-1px");
		fPeopleid.setHeight("-1px");
		fPeopleid.setNullRepresentation("");
		fPeopleid.addValidator(new StringLengthValidator("ข้อมูลไม่ถูกต้อง", 13, 20, false));
		fPeopleid.addTextChangeListener(new TextChangeListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void textChange(TextChangeEvent event) {
				if(event.getText() != null){
					if(event.getText().length() >= 13){
						fSqlContainer.addContainerFilter(new Equal(FamilySchema.PEOPLE_ID,event.getText()));
						if(fSqlContainer.size() > 0){
							Item item = fSqlContainer.getItem(fSqlContainer.getIdByIndex(0));
							fatherBinder.setItemDataSource(item);
							pkStore[0] = item.getItemProperty(FamilySchema.FAMILY_ID).getValue();
							fatherBinder.setEnabled(false);
							isDuplicateFather = true;
						}
						fSqlContainer.removeAllContainerFilters();
					}
				}
			}
		});
		fatherForm.addComponent(fPeopleid);
		
		fPrename = new ComboBox("ชื่อต้น",new Prename());
		fPrename.setInputPrompt("กรุณาเลือก");
		fPrename.setValue("ชาย");
		fPrename.setItemCaptionPropertyId("name");
		fPrename.setImmediate(true);
		fPrename.setNullSelectionAllowed(false);
		fPrename.setRequired(true);
		fPrename.setWidth("-1px");
		fPrename.setHeight("-1px");
		fPrename.setFilteringMode(FilteringMode.CONTAINS);
		fatherForm.addComponent(fPrename);
		
		fFirstname = new TextField("ชื่อ");
		fFirstname.setInputPrompt("ชื่อ");
		fFirstname.setImmediate(false);
		fFirstname.setRequired(true);
		fFirstname.setWidth("-1px");
		fFirstname.setHeight("-1px");
		fFirstname.setNullRepresentation("");
		fatherForm.addComponent(fFirstname);
		
		fLastname = new TextField("สกุล");
		fLastname.setInputPrompt("สกุล");
		fLastname.setImmediate(false);
		fLastname.setRequired(true);
		fLastname.setWidth("-1px");
		fLastname.setHeight("-1px");
		fLastname.setNullRepresentation("");
		fatherForm.addComponent(fLastname);

		fFirstnameNd = new TextField("ชื่ออังกฤษ");
		fFirstnameNd.setInputPrompt("ชื่ออังกฤษ");
		fFirstnameNd.setImmediate(false);
		fFirstnameNd.setWidth("-1px");
		fFirstnameNd.setHeight("-1px");
		fFirstnameNd.setNullRepresentation("");
		fatherForm.addComponent(fFirstnameNd);
		
		fLastnameNd = new TextField("สกุลอังกฤษ");
		fLastnameNd.setInputPrompt("สกุลอังกฤษ");
		fLastnameNd.setImmediate(false);
		fLastnameNd.setWidth("-1px");
		fLastnameNd.setHeight("-1px");
		fLastnameNd.setNullRepresentation("");
		fatherForm.addComponent(fLastnameNd);
			
		fGender = new OptionGroup("เพศ",new Gender());
		fGender.setItemCaptionPropertyId("name");
		fGender.setImmediate(true);
		fGender.setNullSelectionAllowed(false);
		fGender.setRequired(true);
		fGender.setWidth("-1px");
		fGender.setHeight("-1px");
		fatherForm.addComponent(fGender);
		
		fReligion = new ComboBox("ศาสนา",new Religion());
		fReligion.setInputPrompt("กรุณาเลือก");
		fReligion.setItemCaptionPropertyId("name");
		fReligion.setImmediate(true);
		fReligion.setNullSelectionAllowed(false);
		fReligion.setRequired(true);
		fReligion.setWidth("-1px");
		fReligion.setHeight("-1px");
		fReligion.setFilteringMode(FilteringMode.CONTAINS);
		fatherForm.addComponent(fReligion);
		
		fRace = new ComboBox("เชื้อชาติ",new Race());
		fRace.setInputPrompt("กรุณาเลือก");
		fRace.setItemCaptionPropertyId("name");
		fRace.setImmediate(true);
		fRace.setNullSelectionAllowed(false);
		fRace.setRequired(true);
		fRace.setWidth("-1px");
		fRace.setHeight("-1px");
		fRace.setFilteringMode(FilteringMode.CONTAINS);
		fatherForm.addComponent(fRace);
		
		fNationality = new ComboBox("สัญชาติ",new Nationality());
		fNationality.setInputPrompt("กรุณาเลือก");
		fNationality.setItemCaptionPropertyId("name");
		fNationality.setImmediate(true);
		fNationality.setNullSelectionAllowed(false);
		fNationality.setRequired(true);
		fNationality.setWidth("-1px");
		fNationality.setHeight("-1px");
		fNationality.setFilteringMode(FilteringMode.CONTAINS);
		fatherForm.addComponent(fNationality);
		
		fBirthDate = new PopupDateField("วัน เดือน ปี เกิด");
		fBirthDate.setInputPrompt("วว/ดด/ปปปป(คศ)");
		fBirthDate.setImmediate(false);
		fBirthDate.setWidth("-1px");
		fBirthDate.setHeight("-1px");
		fatherForm.addComponent(fBirthDate);
		
		fTel = new TextField("เบอร์โทร");
		fTel.setInputPrompt("เบอร์โทร");
		fTel.setImmediate(false);
		fTel.setWidth("-1px");
		fTel.setHeight("-1px");
		fTel.setNullRepresentation("");
		fatherForm.addComponent(fTel);
		
		fMobile = new TextField("มือถือ");
		fMobile.setInputPrompt("มือถือ");
		fMobile.setImmediate(false);
		fMobile.setRequired(true);
		fMobile.setWidth("-1px");
		fMobile.setHeight("-1px");
		fMobile.setNullRepresentation("");
		fatherForm.addComponent(fMobile);
		
		fEmail = new TextField("อีเมล์");
		fEmail.setInputPrompt("อีเมล์");
		fEmail.setImmediate(false);
		fEmail.setWidth("-1px");
		fEmail.setHeight("-1px");
		fEmail.setNullRepresentation("");
		fEmail.addValidator(new EmailValidator("ข้อมูลไม่ถูกต้อง"));
		fatherForm.addComponent(fEmail);
		
		fSalary = new NumberField("รายได้");
		fSalary.setInputPrompt("รายได้");
		fSalary.setImmediate(false);
		fSalary.setWidth("-1px");
		fSalary.setHeight("-1px");
		fSalary.setNullRepresentation("");
		fatherForm.addComponent(fSalary);
		
		fAliveStatus = new ComboBox("สถานภาพ",new AliveStatus());
		fAliveStatus.setInputPrompt("กรุณาเลือก");
		fAliveStatus.setItemCaptionPropertyId("name");
		fAliveStatus.setImmediate(true);
		fAliveStatus.setNullSelectionAllowed(false);
		fAliveStatus.setRequired(true);
		fAliveStatus.setWidth("-1px");
		fAliveStatus.setHeight("-1px");
		fAliveStatus.setFilteringMode(FilteringMode.CONTAINS);
		fatherForm.addComponent(fAliveStatus);
		
		fOccupation = new ComboBox("อาชีพ",new Occupation());
		fOccupation.setInputPrompt("กรุณาเลือก");
		fOccupation.setItemCaptionPropertyId("name");
		fOccupation.setImmediate(true);
		fOccupation.setNullSelectionAllowed(false);
		fOccupation.setRequired(true);
		fOccupation.setWidth("-1px");
		fOccupation.setHeight("-1px");
		fOccupation.setFilteringMode(FilteringMode.CONTAINS);
		fatherForm.addComponent(fOccupation);
		
		fJobAddress = new TextArea("สถานที่ทำงาน");
		fJobAddress.setInputPrompt("บ้านเลขที่ ซอย ถนน");
		fJobAddress.setImmediate(false);
		fJobAddress.setWidth("-1px");
		fJobAddress.setHeight("-1px");
		fJobAddress.setNullRepresentation("");
		fatherForm.addComponent(fJobAddress);
		
		fCurrentAddress = new TextArea("ที่อยู่ปัจจุบัน");
		fCurrentAddress.setInputPrompt("บ้านเลขที่ ซอย ถนน");
		fCurrentAddress.setImmediate(false);
		fCurrentAddress.setWidth("-1px");
		fCurrentAddress.setHeight("-1px");
		fCurrentAddress.setNullRepresentation("");
		fatherForm.addComponent(fCurrentAddress);
		
		fCurrentProvinceId = new ComboBox("จังหวัด",new Province());
		fCurrentProvinceId.setInputPrompt("กรุณาเลือก");
		fCurrentProvinceId.setItemCaptionPropertyId("name");
		fCurrentProvinceId.setImmediate(true);
		fCurrentProvinceId.setNullSelectionAllowed(false);
		fCurrentProvinceId.setWidth("-1px");
		fCurrentProvinceId.setHeight("-1px");
		fCurrentProvinceId.setFilteringMode(FilteringMode.CONTAINS);
		fCurrentProvinceId.addValueChangeListener(new ValueChangeListener() {
			private static final long serialVersionUID = 1L;
			@Override
			public void valueChange(ValueChangeEvent event) {
				if(event.getProperty().getValue() != null)
					fCurrentDistrict.setContainerDataSource(new District(Integer.parseInt(event.getProperty().getValue().toString())));
			}
		});
		fatherForm.addComponent(fCurrentProvinceId);
		
		fCurrentDistrict = new ComboBox("อำเภอ");
		fCurrentDistrict.setInputPrompt("กรุณาเลือก");
		fCurrentDistrict.setItemCaptionPropertyId("name");
		fCurrentDistrict.setImmediate(true);
		fCurrentDistrict.setNullSelectionAllowed(false);
		fCurrentDistrict.setWidth("-1px");
		fCurrentDistrict.setHeight("-1px");
		fCurrentDistrict.setFilteringMode(FilteringMode.CONTAINS);
		fCurrentDistrict.addValueChangeListener(new ValueChangeListener() {
			private static final long serialVersionUID = 1L;
			@Override
			public void valueChange(ValueChangeEvent event) {
				if(event.getProperty().getValue() != null){
					fCurrentCity.setContainerDataSource(new City(Integer.parseInt(event.getProperty().getValue().toString())));
					fCurrentPostcode.setContainerDataSource(new Postcode(Integer.parseInt(event.getProperty().getValue().toString())));
				}
			}
		});
		fatherForm.addComponent(fCurrentDistrict);
		
		fCurrentCity = new ComboBox("ตำบล");
		fCurrentCity.setInputPrompt("กรุณาเลือก");
		fCurrentCity.setItemCaptionPropertyId("name");
		fCurrentCity.setImmediate(true);
		fCurrentCity.setNullSelectionAllowed(false);
		fCurrentCity.setWidth("-1px");
		fCurrentCity.setHeight("-1px");
		fCurrentCity.setFilteringMode(FilteringMode.CONTAINS);
		fatherForm.addComponent(fCurrentCity);
		
		fCurrentPostcode = new ComboBox("รหัสไปรษณีย์");
		fCurrentPostcode.setInputPrompt("กรุณาเลือก");
		fCurrentPostcode.setItemCaptionPropertyId("name");
		fCurrentPostcode.setImmediate(true);
		fCurrentPostcode.setNullSelectionAllowed(false);
		fCurrentPostcode.setWidth("-1px");
		fCurrentPostcode.setHeight("-1px");
		fCurrentPostcode.setFilteringMode(FilteringMode.CONTAINS);
		fatherForm.addComponent(fCurrentPostcode);
		
		HorizontalLayout buttonLayout = new HorizontalLayout();
		buttonLayout.setSpacing(true);
		buttonLayout.setWidth("100%");
		fatherForm.addComponent(buttonLayout);
		
		addressBack = new Button(FontAwesome.ARROW_LEFT);
		addressBack.setWidth("100%");
		addressBack.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 1L;
			@Override
			public void buttonClick(ClickEvent event) {
				setSelectedTab(addressForm);
			}
		});
		buttonLayout.addComponents(addressBack);
		
		motherNext = new Button(FontAwesome.ARROW_RIGHT);
		motherNext.setWidth("100%");
		motherNext.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 1L;
			@Override
			public void buttonClick(ClickEvent event) {
				setSelectedTab(motherForm);
			}
		});
		buttonLayout.addComponents(motherNext);
	}
	
	/*สร้าง Layout สำหรับมารดา*/
	private void motherForm(){
		motherForm = new FormLayout();
		motherForm.setSizeUndefined();
		motherForm.setMargin(true);
		addTab(motherForm,"ข้อมูลมารดา", FontAwesome.FEMALE);
		
		mPeopleIdType = new OptionGroup("ประเภทบัตร",new PeopleIdType());
		mPeopleIdType.setItemCaptionPropertyId("name");
		mPeopleIdType.setImmediate(true);
		mPeopleIdType.setNullSelectionAllowed(false);
		mPeopleIdType.setRequired(true);
		mPeopleIdType.setWidth("-1px");
		mPeopleIdType.setHeight("-1px");
		motherForm.addComponent(mPeopleIdType);
		
		mPeopleid = new TextField("หมายเลขประชาชน");
		mPeopleid.setInputPrompt("หมายเลขประชาชน");
		mPeopleid.setImmediate(false);
		mPeopleid.setRequired(true);
		mPeopleid.setWidth("-1px");
		mPeopleid.setHeight("-1px");
		mPeopleid.setNullRepresentation("");
		mPeopleid.addValidator(new StringLengthValidator("ข้อมูลไม่ถูกต้อง", 13, 20, false));
		mPeopleid.addTextChangeListener(new TextChangeListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void textChange(TextChangeEvent event) {
				if(event.getText() != null){
					if(event.getText().length() >= 13){
						fSqlContainer.addContainerFilter(new Equal(FamilySchema.PEOPLE_ID,event.getText()));
						if(fSqlContainer.size() > 0){
							Item item = fSqlContainer.getItem(fSqlContainer.getIdByIndex(0));
							motherBinder.setItemDataSource(item);
							pkStore[1] = item.getItemProperty(FamilySchema.FAMILY_ID).getValue();
							motherBinder.setEnabled(false);
							isDuplicateMother = true;
						}
						fSqlContainer.removeAllContainerFilters();
					}
				}
			}
		});
		motherForm.addComponent(mPeopleid);
		
		mPrename = new ComboBox("ชื่อต้น",new Prename());
		mPrename.setInputPrompt("กรุณาเลือก");
		mPrename.setItemCaptionPropertyId("name");
		mPrename.setImmediate(true);
		mPrename.setNullSelectionAllowed(false);
		mPrename.setRequired(true);
		mPrename.setWidth("-1px");
		mPrename.setHeight("-1px");
		mPrename.setFilteringMode(FilteringMode.CONTAINS);
		motherForm.addComponent(mPrename);
		
		mFirstname = new TextField("ชื่อ");
		mFirstname.setInputPrompt("ชื่อ");
		mFirstname.setImmediate(false);
		mFirstname.setRequired(true);
		mFirstname.setWidth("-1px");
		mFirstname.setHeight("-1px");
		mFirstname.setNullRepresentation("");
		motherForm.addComponent(mFirstname);
		
		mLastname = new TextField("สกุล");
		mLastname.setInputPrompt("สกุล");
		mLastname.setImmediate(false);
		mLastname.setRequired(true);
		mLastname.setWidth("-1px");
		mLastname.setHeight("-1px");
		mLastname.setNullRepresentation("");
		motherForm.addComponent(mLastname);

		mFirstnameNd = new TextField("ชื่ออังกฤษ");
		mFirstnameNd.setInputPrompt("ชื่ออังกฤษ");
		mFirstnameNd.setImmediate(false);
		mFirstnameNd.setWidth("-1px");
		mFirstnameNd.setHeight("-1px");
		mFirstnameNd.setNullRepresentation("");
		motherForm.addComponent(mFirstnameNd);
		
		mLastnameNd = new TextField("สกุลอังกฤษ");
		mLastnameNd.setInputPrompt("สกุลอังกฤษ");
		mLastnameNd.setImmediate(false);
		mLastnameNd.setWidth("-1px");
		mLastnameNd.setHeight("-1px");
		mLastnameNd.setNullRepresentation("");
		motherForm.addComponent(mLastnameNd);
			
		mGender = new OptionGroup("เพศ",new Gender());
		mGender.setItemCaptionPropertyId("name");
		mGender.setImmediate(true);
		mGender.setNullSelectionAllowed(false);
		mGender.setRequired(true);
		mGender.setWidth("-1px");
		mGender.setHeight("-1px");
		motherForm.addComponent(mGender);
		
		mReligion = new ComboBox("ศาสนา",new Religion());
		mReligion.setInputPrompt("กรุณาเลือก");
		mReligion.setItemCaptionPropertyId("name");
		mReligion.setImmediate(true);
		mReligion.setNullSelectionAllowed(false);
		mReligion.setRequired(true);
		mReligion.setWidth("-1px");
		mReligion.setHeight("-1px");
		mReligion.setFilteringMode(FilteringMode.CONTAINS);
		motherForm.addComponent(mReligion);
		
		mRace = new ComboBox("เชื้อชาติ",new Race());
		mRace.setInputPrompt("กรุณาเลือก");
		mRace.setItemCaptionPropertyId("name");
		mRace.setImmediate(true);
		mRace.setNullSelectionAllowed(false);
		mRace.setRequired(true);
		mRace.setWidth("-1px");
		mRace.setHeight("-1px");
		mRace.setFilteringMode(FilteringMode.CONTAINS);
		motherForm.addComponent(mRace);
		
		mNationality = new ComboBox("สัญชาติ",new Nationality());
		mNationality.setInputPrompt("กรุณาเลือก");
		mNationality.setItemCaptionPropertyId("name");
		mNationality.setImmediate(true);
		mNationality.setNullSelectionAllowed(false);
		mNationality.setRequired(true);
		mNationality.setWidth("-1px");
		mNationality.setHeight("-1px");
		mNationality.setFilteringMode(FilteringMode.CONTAINS);
		motherForm.addComponent(mNationality);

		mBirthDate = new PopupDateField("วัน เดือน ปี เกิด");
		mBirthDate.setInputPrompt("วว/ดด/ปปปป(คศ)");
		mBirthDate.setImmediate(false);
		mBirthDate.setWidth("-1px");
		mBirthDate.setHeight("-1px");
		motherForm.addComponent(mBirthDate);
		
		mTel = new TextField("เบอร์โทร");
		mTel.setInputPrompt("เบอร์โทร");
		mTel.setImmediate(false);
		mTel.setWidth("-1px");
		mTel.setHeight("-1px");
		mTel.setNullRepresentation("");
		motherForm.addComponent(mTel);
		
		mMobile = new TextField("มือถือ");
		mMobile.setInputPrompt("มือถือ");
		mMobile.setImmediate(false);
		mMobile.setRequired(true);
		mMobile.setWidth("-1px");
		mMobile.setHeight("-1px");
		mMobile.setNullRepresentation("");
		motherForm.addComponent(mMobile);
		
		mEmail = new TextField("อีเมล์");
		mEmail.setInputPrompt("อีเมล์");
		mEmail.setImmediate(false);
		mEmail.setWidth("-1px");
		mEmail.setHeight("-1px");
		mEmail.setNullRepresentation("");
		mEmail.addValidator(new EmailValidator("ข้อมูลไม่ถูกต้อง"));
		motherForm.addComponent(mEmail);
		
		mSalary = new NumberField("รายได้");
		mSalary.setInputPrompt("รายได้");
		mSalary.setImmediate(false);
		mSalary.setWidth("-1px");
		mSalary.setHeight("-1px");
		mSalary.setNullRepresentation("");
		motherForm.addComponent(mSalary);
		
		mAliveStatus = new ComboBox("สถานภาพ",new AliveStatus());
		mAliveStatus.setInputPrompt("กรุณาเลือก");
		mAliveStatus.setItemCaptionPropertyId("name");
		mAliveStatus.setImmediate(true);
		mAliveStatus.setNullSelectionAllowed(false);
		mAliveStatus.setRequired(true);
		mAliveStatus.setWidth("-1px");
		mAliveStatus.setHeight("-1px");
		mAliveStatus.setFilteringMode(FilteringMode.CONTAINS);
		motherForm.addComponent(mAliveStatus);
		
		mOccupation = new ComboBox("อาชีพ",new Occupation());
		mOccupation.setInputPrompt("กรุณาเลือก");
		mOccupation.setItemCaptionPropertyId("name");
		mOccupation.setImmediate(true);
		mOccupation.setNullSelectionAllowed(false);
		mOccupation.setRequired(true);
		mOccupation.setWidth("-1px");
		mOccupation.setHeight("-1px");
		mOccupation.setFilteringMode(FilteringMode.CONTAINS);
		motherForm.addComponent(mOccupation);
		
		mJobAddress = new TextArea("สถานที่ทำงาน");
		mJobAddress.setInputPrompt("บ้านเลขที่ ซอย ถนน");
		mJobAddress.setImmediate(false);
		mJobAddress.setWidth("-1px");
		mJobAddress.setHeight("-1px");
		mJobAddress.setNullRepresentation("");
		motherForm.addComponent(mJobAddress);
		
		mCurrentAddress = new TextArea("ที่อยู่ปัจจุบัน");
		mCurrentAddress.setInputPrompt("บ้านเลขที่ ซอย ถนน");
		mCurrentAddress.setImmediate(false);
		mCurrentAddress.setWidth("-1px");
		mCurrentAddress.setHeight("-1px");
		mCurrentAddress.setNullRepresentation("");
		motherForm.addComponent(mCurrentAddress);
		
		mCurrentProvinceId = new ComboBox("จังหวัด",new Province());
		mCurrentProvinceId.setInputPrompt("กรุณาเลือก");
		mCurrentProvinceId.setItemCaptionPropertyId("name");
		mCurrentProvinceId.setImmediate(true);
		mCurrentProvinceId.setNullSelectionAllowed(false);
		mCurrentProvinceId.setWidth("-1px");
		mCurrentProvinceId.setHeight("-1px");
		mCurrentProvinceId.setFilteringMode(FilteringMode.CONTAINS);
		mCurrentProvinceId.addValueChangeListener(new ValueChangeListener() {
			private static final long serialVersionUID = 1L;
			@Override
			public void valueChange(ValueChangeEvent event) {
				if(event.getProperty().getValue() != null)
					mCurrentDistrict.setContainerDataSource(new District(Integer.parseInt(event.getProperty().getValue().toString())));
			}
		});
		motherForm.addComponent(mCurrentProvinceId);
		
		mCurrentDistrict = new ComboBox("อำเภอ");
		mCurrentDistrict.setInputPrompt("กรุณาเลือก");
		mCurrentDistrict.setItemCaptionPropertyId("name");
		mCurrentDistrict.setImmediate(true);
		mCurrentDistrict.setNullSelectionAllowed(false);
		mCurrentDistrict.setWidth("-1px");
		mCurrentDistrict.setHeight("-1px");
		mCurrentDistrict.setFilteringMode(FilteringMode.CONTAINS);
		mCurrentDistrict.addValueChangeListener(new ValueChangeListener() {
			private static final long serialVersionUID = 1L;
			@Override
			public void valueChange(ValueChangeEvent event) {
				if(event.getProperty().getValue() != null){
					mCurrentCity.setContainerDataSource(new City(Integer.parseInt(event.getProperty().getValue().toString())));
					mCurrentPostcode.setContainerDataSource(new Postcode(Integer.parseInt(event.getProperty().getValue().toString())));
				}
			}
		});
		motherForm.addComponent(mCurrentDistrict);
		
		mCurrentCity = new ComboBox("ตำบล");
		mCurrentCity.setInputPrompt("กรุณาเลือก");
		mCurrentCity.setItemCaptionPropertyId("name");
		mCurrentCity.setImmediate(true);
		mCurrentCity.setNullSelectionAllowed(false);
		mCurrentCity.setWidth("-1px");
		mCurrentCity.setHeight("-1px");
		mCurrentCity.setFilteringMode(FilteringMode.CONTAINS);
		motherForm.addComponent(mCurrentCity);
		
		mCurrentPostcode = new ComboBox("รหัสไปรษณีย์");
		mCurrentPostcode.setInputPrompt("กรุณาเลือก");
		mCurrentPostcode.setItemCaptionPropertyId("name");
		mCurrentPostcode.setImmediate(true);
		mCurrentPostcode.setNullSelectionAllowed(false);
		mCurrentPostcode.setWidth("-1px");
		mCurrentPostcode.setHeight("-1px");
		mCurrentPostcode.setFilteringMode(FilteringMode.CONTAINS);
		motherForm.addComponent(mCurrentPostcode);
				
		HorizontalLayout buttonLayout = new HorizontalLayout();
		buttonLayout.setSpacing(true);
		buttonLayout.setWidth("100%");
		motherForm.addComponent(buttonLayout);
		
		fatherBack = new Button(FontAwesome.ARROW_LEFT);
		fatherBack.setWidth("100%");
		fatherBack.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 1L;
			@Override
			public void buttonClick(ClickEvent event) {
				setSelectedTab(fatherForm);
			}
		});
		buttonLayout.addComponents(fatherBack);
		
		spouseNext = new Button(FontAwesome.ARROW_RIGHT);
		spouseNext.setWidth("100%");
		spouseNext.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 1L;
			@Override
			public void buttonClick(ClickEvent event) {
				setSelectedTab(spouseForm);
			}
		});
		buttonLayout.addComponents(spouseNext);
	}
	
	/*สร้าง Layout สำหรับคู่สมรส*/
	private void spouseForm(){
		spouseForm = new FormLayout();
		spouseForm.setSizeUndefined();
		spouseForm.setMargin(true);
		addTab(spouseForm,"ข้อมูลคู่สมรส", FontAwesome.USER);
		
		sPeopleIdType = new OptionGroup("ประเภทบัตร",new PeopleIdType());
		sPeopleIdType.setItemCaptionPropertyId("name");
		sPeopleIdType.setImmediate(true);
		sPeopleIdType.setNullSelectionAllowed(false);
		sPeopleIdType.setRequired(true);
		sPeopleIdType.setWidth("-1px");
		sPeopleIdType.setHeight("-1px");
		spouseForm.addComponent(sPeopleIdType);
		
		sPeopleid = new TextField("หมายเลขประชาชน");
		sPeopleid.setInputPrompt("หมายเลขประชาชน");
		sPeopleid.setImmediate(false);
		sPeopleid.setRequired(true);
		sPeopleid.setNullRepresentation("");
		sPeopleid.setWidth("-1px");
		sPeopleid.setHeight("-1px");
		sPeopleid.setNullRepresentation("");
		sPeopleid.addValidator(new StringLengthValidator("ข้อมูลไม่ถูกต้อง", 13, 20, false));
		sPeopleid.addTextChangeListener(new TextChangeListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void textChange(TextChangeEvent event) {
				if(event.getText() != null){
					if(event.getText().length() >= 13){
						fSqlContainer.addContainerFilter(new Equal(FamilySchema.PEOPLE_ID,event.getText()));
						if(fSqlContainer.size() > 0){
							Item item = fSqlContainer.getItem(fSqlContainer.getIdByIndex(0));
							spouseBinder.setItemDataSource(item);
							pkStore[2] = item.getItemProperty(FamilySchema.FAMILY_ID).getValue();
							spouseBinder.setEnabled(false);
							isDuplicateSpouse = true;
						}
						fSqlContainer.removeAllContainerFilters();
					}
				}
			}
		});
		spouseForm.addComponent(sPeopleid);
		
		sPrename = new ComboBox("ชื่อต้น",new Prename());
		sPrename.setInputPrompt("กรุณาเลือก");
		sPrename.setItemCaptionPropertyId("name");
		sPrename.setImmediate(true);
		sPrename.setNullSelectionAllowed(false);
		sPrename.setRequired(true);
		sPrename.setWidth("-1px");
		sPrename.setHeight("-1px");
		sPrename.setFilteringMode(FilteringMode.CONTAINS);
		spouseForm.addComponent(sPrename);
		
		sFirstname = new TextField("ชื่อ");
		sFirstname.setInputPrompt("ชื่อ");
		sFirstname.setImmediate(false);
		sFirstname.setRequired(true);
		sFirstname.setWidth("-1px");
		sFirstname.setHeight("-1px");
		sFirstname.setNullRepresentation("");
		spouseForm.addComponent(sFirstname);
		
		sLastname = new TextField("สกุล");
		sLastname.setInputPrompt("สกุล");
		sLastname.setImmediate(false);
		sLastname.setRequired(true);
		sLastname.setWidth("-1px");
		sLastname.setHeight("-1px");
		sLastname.setNullRepresentation("");
		spouseForm.addComponent(sLastname);

		sFirstnameNd = new TextField("ชื่ออังกฤษ");
		sFirstnameNd.setInputPrompt("ชื่ออังกฤษ");
		sFirstnameNd.setImmediate(false);
		sFirstnameNd.setWidth("-1px");
		sFirstnameNd.setHeight("-1px");
		sFirstnameNd.setNullRepresentation("");
		spouseForm.addComponent(sFirstnameNd);
		
		sLastnameNd = new TextField("สกุลอังกฤษ");
		sLastnameNd.setInputPrompt("สกุลอังกฤษ");
		sLastnameNd.setImmediate(false);
		sLastnameNd.setWidth("-1px");
		sLastnameNd.setHeight("-1px");
		sLastnameNd.setNullRepresentation("");
		spouseForm.addComponent(sLastnameNd);
			
		sGender = new OptionGroup("เพศ",new Gender());
		sGender.setItemCaptionPropertyId("name");
		sGender.setImmediate(true);
		sGender.setNullSelectionAllowed(false);
		sGender.setRequired(true);
		sGender.setWidth("-1px");
		sGender.setHeight("-1px");
		spouseForm.addComponent(sGender);
		
		sReligion = new ComboBox("ศาสนา",new Religion());
		sReligion.setInputPrompt("กรุณาเลือก");
		sReligion.setItemCaptionPropertyId("name");
		sReligion.setImmediate(true);
		sReligion.setNullSelectionAllowed(false);
		sReligion.setRequired(true);
		sReligion.setWidth("-1px");
		sReligion.setHeight("-1px");
		sReligion.setFilteringMode(FilteringMode.CONTAINS);
		spouseForm.addComponent(sReligion);
		
		sRace = new ComboBox("เชื้อชาติ",new Race());
		sRace.setInputPrompt("กรุณาเลือก");
		sRace.setItemCaptionPropertyId("name");
		sRace.setImmediate(true);
		sRace.setNullSelectionAllowed(false);
		sRace.setRequired(true);
		sRace.setWidth("-1px");
		sRace.setHeight("-1px");
		sRace.setFilteringMode(FilteringMode.CONTAINS);
		spouseForm.addComponent(sRace);
		
		sNationality = new ComboBox("สัญชาติ",new Nationality());
		sNationality.setInputPrompt("กรุณาเลือก");
		sNationality.setItemCaptionPropertyId("name");
		sNationality.setImmediate(true);
		sNationality.setNullSelectionAllowed(false);
		sNationality.setRequired(true);
		sNationality.setWidth("-1px");
		sNationality.setHeight("-1px");
		sNationality.setFilteringMode(FilteringMode.CONTAINS);
		spouseForm.addComponent(sNationality);
		
		sBirthDate = new PopupDateField("วัน เดือน ปี เกิด");
		sBirthDate.setInputPrompt("วว/ดด/ปปปป(คศ)");
		sBirthDate.setImmediate(false);
		sBirthDate.setWidth("-1px");
		sBirthDate.setHeight("-1px");
		spouseForm.addComponent(sBirthDate);
		
		sTel = new TextField("เบอร์โทร");
		sTel.setInputPrompt("เบอร์โทร");
		sTel.setImmediate(false);
		sTel.setWidth("-1px");
		sTel.setHeight("-1px");
		sTel.setNullRepresentation("");
		spouseForm.addComponent(sTel);
		
		sMobile = new TextField("มือถือ");
		sMobile.setInputPrompt("มือถือ");
		sMobile.setImmediate(false);
		sMobile.setRequired(true);
		sMobile.setWidth("-1px");
		sMobile.setHeight("-1px");
		sMobile.setNullRepresentation("");
		spouseForm.addComponent(sMobile);
		
		sEmail = new TextField("อีเมล์");
		sEmail.setInputPrompt("อีเมล์");
		sEmail.setImmediate(false);
		sEmail.setWidth("-1px");
		sEmail.setHeight("-1px");
		sEmail.setNullRepresentation("");
		sEmail.addValidator(new EmailValidator("ข้อมูลไม่ถูกต้อง"));
		spouseForm.addComponent(sEmail);
		
		sSalary = new NumberField("รายได้");
		sSalary.setInputPrompt("รายได้");
		sSalary.setImmediate(false);
		sSalary.setWidth("-1px");
		sSalary.setHeight("-1px");
		sSalary.setNullRepresentation("");
		spouseForm.addComponent(sSalary);
		
		sAliveStatus = new ComboBox("สถานภาพ",new AliveStatus());
		sAliveStatus.setInputPrompt("กรุณาเลือก");
		sAliveStatus.setItemCaptionPropertyId("name");
		sAliveStatus.setImmediate(true);
		sAliveStatus.setNullSelectionAllowed(false);
		sAliveStatus.setRequired(true);
		sAliveStatus.setWidth("-1px");
		sAliveStatus.setHeight("-1px");
		sAliveStatus.setFilteringMode(FilteringMode.CONTAINS);
		spouseForm.addComponent(sAliveStatus);
		
		sOccupation = new ComboBox("อาชีพ",new Occupation());
		sOccupation.setInputPrompt("กรุณาเลือก");
		sOccupation.setItemCaptionPropertyId("name");
		sOccupation.setImmediate(true);
		sOccupation.setNullSelectionAllowed(false);
		sOccupation.setRequired(true);
		sOccupation.setWidth("-1px");
		sOccupation.setHeight("-1px");
		sOccupation.setFilteringMode(FilteringMode.CONTAINS);
		spouseForm.addComponent(sOccupation);
		
		sJobAddress = new TextArea("สถานที่ทำงาน");
		sJobAddress.setInputPrompt("บ้านเลขที่ ซอย ถนน");
		sJobAddress.setImmediate(false);
		sJobAddress.setWidth("-1px");
		sJobAddress.setHeight("-1px");
		sJobAddress.setNullRepresentation("");
		spouseForm.addComponent(sJobAddress);
		
		sCurrentAddress = new TextArea("ที่อยู่ปัจจุบัน");
		sCurrentAddress.setInputPrompt("บ้านเลขที่ ซอย ถนน");
		sCurrentAddress.setImmediate(false);
		sCurrentAddress.setWidth("-1px");
		sCurrentAddress.setHeight("-1px");
		sCurrentAddress.setNullRepresentation("");
		spouseForm.addComponent(sCurrentAddress);
		
		sCurrentProvinceId = new ComboBox("จังหวัด",new Province());
		sCurrentProvinceId.setInputPrompt("กรุณาเลือก");
		sCurrentProvinceId.setItemCaptionPropertyId("name");
		sCurrentProvinceId.setImmediate(true);
		sCurrentProvinceId.setNullSelectionAllowed(false);
		sCurrentProvinceId.setWidth("-1px");
		sCurrentProvinceId.setHeight("-1px");
		sCurrentProvinceId.setFilteringMode(FilteringMode.CONTAINS);
		sCurrentProvinceId.addValueChangeListener(new ValueChangeListener() {
			private static final long serialVersionUID = 1L;
			@Override
			public void valueChange(ValueChangeEvent event) {
				if(event.getProperty().getValue() != null)
					sCurrentDistrict.setContainerDataSource(new District(Integer.parseInt(event.getProperty().getValue().toString())));
			}
		});
		spouseForm.addComponent(sCurrentProvinceId);
		
		sCurrentDistrict = new ComboBox("อำเภอ",new Blood());
		sCurrentDistrict.setInputPrompt("กรุณาเลือก");
		sCurrentDistrict.setItemCaptionPropertyId("name");
		sCurrentDistrict.setImmediate(true);
		sCurrentDistrict.setNullSelectionAllowed(false);
		sCurrentDistrict.setWidth("-1px");
		sCurrentDistrict.setHeight("-1px");
		sCurrentDistrict.setFilteringMode(FilteringMode.CONTAINS);
		sCurrentDistrict.addValueChangeListener(new ValueChangeListener() {
			private static final long serialVersionUID = 1L;
			@Override
			public void valueChange(ValueChangeEvent event) {
				if(event.getProperty().getValue() != null){
					sCurrentCity.setContainerDataSource(new City(Integer.parseInt(event.getProperty().getValue().toString())));
					sCurrentPostcode.setContainerDataSource(new Postcode(Integer.parseInt(event.getProperty().getValue().toString())));
				}
			}
		});
		spouseForm.addComponent(sCurrentDistrict);
		
		sCurrentCity = new ComboBox("ตำบล");
		sCurrentCity.setInputPrompt("กรุณาเลือก");
		sCurrentCity.setItemCaptionPropertyId("name");
		sCurrentCity.setImmediate(true);
		sCurrentCity.setNullSelectionAllowed(false);
		sCurrentCity.setWidth("-1px");
		sCurrentCity.setHeight("-1px");
		sCurrentCity.setFilteringMode(FilteringMode.CONTAINS);
		spouseForm.addComponent(sCurrentCity);
		
		sCurrentPostcode = new ComboBox("รหัสไปรษณีย์");
		sCurrentPostcode.setInputPrompt("กรุณาเลือก");
		sCurrentPostcode.setItemCaptionPropertyId("name");
		sCurrentPostcode.setImmediate(true);
		sCurrentPostcode.setNullSelectionAllowed(false);
		sCurrentPostcode.setWidth("-1px");
		sCurrentPostcode.setHeight("-1px");
		sCurrentPostcode.setFilteringMode(FilteringMode.CONTAINS);
		spouseForm.addComponent(sCurrentPostcode);
		
		HorizontalLayout buttonLayout = new HorizontalLayout();
		buttonLayout.setSpacing(true);
		buttonLayout.setWidth("100%");
		spouseForm.addComponent(buttonLayout);
		
		motherBack = new Button(FontAwesome.ARROW_LEFT);
		motherBack.setWidth("100%");
		motherBack.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 1L;
			@Override
			public void buttonClick(ClickEvent event) {
				setSelectedTab(motherForm);
			}
		});
		buttonLayout.addComponents(motherBack);
		
		finish = new Button("ตกลง",FontAwesome.SAVE);
		finish.setWidth("100%");
		buttonLayout.addComponents(finish);
		
		print = new Button("พิมพ์ใบสมัคร",FontAwesome.PRINT);
		print.setVisible(false);
		print.setWidth("100%");
		buttonLayout.addComponents(print);
	}
	
	/*กำหนดค่าเริ่มต้นภายในฟอร์ม นักเรียน บิดา มารดา*/
	private void initFieldGroup(){		
		personnelBinder = new FieldGroup();
		personnelBinder.setBuffered(true);
		personnelBinder.bind(peopleIdType, PersonnelSchema.PEOPLE_ID_TYPE);
		personnelBinder.bind(peopleId, PersonnelSchema.PEOPLE_ID);
		personnelBinder.bind(prename, PersonnelSchema.PRENAME);
		personnelBinder.bind(firstname, PersonnelSchema.FIRSTNAME);
		personnelBinder.bind(lastname, PersonnelSchema.LASTNAME);
		personnelBinder.bind(firstnameNd, PersonnelSchema.FIRSTNAME_ND);
		personnelBinder.bind(lastnameNd, PersonnelSchema.LASTNAME_ND);
		personnelBinder.bind(firstnameRd, PersonnelSchema.FIRSTNAME_RD);
		personnelBinder.bind(lastnameRd, PersonnelSchema.LASTNAME_RD);		
		personnelBinder.bind(nickname, PersonnelSchema.NICKNAME);
		personnelBinder.bind(gender, PersonnelSchema.GENDER);
		personnelBinder.bind(religion, PersonnelSchema.RELIGION);
		personnelBinder.bind(race, PersonnelSchema.RACE);
		personnelBinder.bind(nationality, PersonnelSchema.NATIONALITY);
		personnelBinder.bind(maritalStatus, PersonnelSchema.MARITAL_STATUS);
		personnelBinder.bind(birthDate, PersonnelSchema.BIRTH_DATE);
		personnelBinder.bind(blood, PersonnelSchema.BLOOD);
		personnelBinder.bind(height, PersonnelSchema.HEIGHT);
		personnelBinder.bind(weight, PersonnelSchema.WEIGHT);
		personnelBinder.bind(congenitalDisease, PersonnelSchema.CONGENITAL_DISEASE);
		
		personnelBinder.bind(personnelCode, PersonnelSchema.PERSONEL_CODE);
		personnelBinder.bind(personnelStatus, PersonnelSchema.PERSONEL_STATUS);
		personnelBinder.bind(startWorkDate, PersonnelSchema.START_WORK_DATE);
		personnelBinder.bind(jobPosition, PersonnelSchema.JOB_POSITION);
		personnelBinder.bind(department, PersonnelSchema.DEPARTMENT);
		personnelBinder.bind(employmentType, PersonnelSchema.EMPLOYMENT_TYPE);
		personnelBinder.bind(bankName, PersonnelSchema.BANK_NAME);
		personnelBinder.bind(bankAccountNumber, PersonnelSchema.BANK_ACCOUNT_NUMBER);
		personnelBinder.bind(bankAccountType, PersonnelSchema.BANK_ACCOUNT_TYPE);
		personnelBinder.bind(bankaccountName, PersonnelSchema.BANK_ACCOUNT_NAME);
		personnelBinder.bind(bankaccountBranch, PersonnelSchema.BANK_ACCOUNT_BRANCH);
		personnelBinder.bind(bankProvinceId, PersonnelSchema.BANK_ACCOUNT_PROVINCE_ID);
		
		personnelBinder.bind(licenseLecturerNumber, PersonnelSchema.LICENSE_LECTURER_NUMBER);
		personnelBinder.bind(licenseLecturerType, PersonnelSchema.LICENSE_LECTURER_TYPE);
		personnelBinder.bind(licenseLecturerIssuedDate, PersonnelSchema.LICENSE_LECTURER_ISSUED_DATE);
		personnelBinder.bind(licenseLecturerExpiredDate, PersonnelSchema.LICENSE_LECTURER_EXPIRED_DATE);
		personnelBinder.bind(license11Number, PersonnelSchema.LICENSE_11_NUMBER);
		personnelBinder.bind(licenseIssueArea, PersonnelSchema.LICENSE_ISSUE_AREA);
		personnelBinder.bind(licenseIssueProvinceId, PersonnelSchema.LICENSE_ISSUE_PROVINCE_ID);
		personnelBinder.bind(license17Number, PersonnelSchema.LICENSE_17_NUMBER);
		personnelBinder.bind(license18Number, PersonnelSchema.LICENSE_18_NUMBER);
		personnelBinder.bind(license19Number, PersonnelSchema.LICENSE_19_NUMBER);
		personnelBinder.bind(fillDegreePost, PersonnelSchema.FILL_DEGREE_POST);
		personnelBinder.bind(fillDegreePostDate, PersonnelSchema.FILL_DEGREE_POST_DATE);
		
		personnelBinder.bind(tel, PersonnelSchema.TEL);
		personnelBinder.bind(mobile, PersonnelSchema.MOBILE);
		personnelBinder.bind(email, PersonnelSchema.EMAIL);
		personnelBinder.bind(currentAddress, PersonnelSchema.CURRENT_ADDRESS);
		personnelBinder.bind(currentProvince, PersonnelSchema.CURRENT_PROVINCE_ID);
		personnelBinder.bind(currentDistrict, PersonnelSchema.CURRENT_DISTRICT_ID);
		personnelBinder.bind(currentCity, PersonnelSchema.CURRENT_CITY_ID);
		personnelBinder.bind(currentPostcode, PersonnelSchema.CURRENT_POSTCODE_ID);
		personnelBinder.bind(censusAddress, PersonnelSchema.CENSUS_ADDRESS);
		personnelBinder.bind(censusProvince, PersonnelSchema.CENSUS_PROVINCE_ID);
		personnelBinder.bind(censusDistrict, PersonnelSchema.CENSUS_DISTRICT_ID);
		personnelBinder.bind(censusCity, PersonnelSchema.CENSUS_CITY_ID);
		personnelBinder.bind(censusPostcode, PersonnelSchema.CENSUS_POSTCODE_ID);
		
		fatherBinder = new FieldGroup();
		fatherBinder.setBuffered(true);
		fatherBinder.bind(fPeopleIdType, FamilySchema.PEOPLE_ID_TYPE);
		fatherBinder.bind(fPeopleid, FamilySchema.PEOPLE_ID);
		fatherBinder.bind(fPrename, FamilySchema.PRENAME);
		fatherBinder.bind(fFirstname, FamilySchema.FIRSTNAME);
		fatherBinder.bind(fLastname, FamilySchema.LASTNAME);
		fatherBinder.bind(fFirstnameNd, FamilySchema.FIRSTNAME_ND);
		fatherBinder.bind(fLastnameNd, FamilySchema.LASTNAME_ND);
		fatherBinder.bind(fGender, FamilySchema.GENDER);
		fatherBinder.bind(fReligion, FamilySchema.RELIGION);
		fatherBinder.bind(fRace, FamilySchema.RACE);
		fatherBinder.bind(fNationality, FamilySchema.NATIONALITY);
		fatherBinder.bind(fBirthDate, FamilySchema.BIRTH_DATE);
		fatherBinder.bind(fTel, FamilySchema.TEL);
		fatherBinder.bind(fMobile, FamilySchema.MOBILE);
		fatherBinder.bind(fEmail, FamilySchema.EMAIL);
		fatherBinder.bind(fSalary, FamilySchema.SALARY);
		fatherBinder.bind(fAliveStatus, FamilySchema.ALIVE_STATUS);
		fatherBinder.bind(fOccupation, FamilySchema.OCCUPATION);
		fatherBinder.bind(fJobAddress, FamilySchema.JOB_ADDRESS);
		fatherBinder.bind(fCurrentAddress, FamilySchema.CURRENT_ADDRESS);
		fatherBinder.bind(fCurrentProvinceId, FamilySchema.CURRENT_PROVINCE_ID);
		fatherBinder.bind(fCurrentDistrict, FamilySchema.CURRENT_DISTRICT_ID);
		fatherBinder.bind(fCurrentCity, FamilySchema.CURRENT_CITY_ID);
		fatherBinder.bind(fCurrentPostcode, FamilySchema.CURRENT_POSTCODE_ID);
		
		motherBinder = new FieldGroup();
		motherBinder.setBuffered(true);
		motherBinder.bind(mPeopleIdType, FamilySchema.PEOPLE_ID_TYPE);
		motherBinder.bind(mPeopleid, FamilySchema.PEOPLE_ID);
		motherBinder.bind(mPrename, FamilySchema.PRENAME);
		motherBinder.bind(mFirstname, FamilySchema.FIRSTNAME);
		motherBinder.bind(mLastname, FamilySchema.LASTNAME);
		motherBinder.bind(mFirstnameNd, FamilySchema.FIRSTNAME_ND);
		motherBinder.bind(mLastnameNd, FamilySchema.LASTNAME_ND);
		motherBinder.bind(mGender, FamilySchema.GENDER);
		motherBinder.bind(mReligion, FamilySchema.RELIGION);
		motherBinder.bind(mRace, FamilySchema.RACE);
		motherBinder.bind(mNationality, FamilySchema.NATIONALITY);
		motherBinder.bind(mBirthDate, FamilySchema.BIRTH_DATE);
		motherBinder.bind(mTel, FamilySchema.TEL);
		motherBinder.bind(mMobile, FamilySchema.MOBILE);
		motherBinder.bind(mEmail, FamilySchema.EMAIL);
		motherBinder.bind(mSalary, FamilySchema.SALARY);
		motherBinder.bind(mAliveStatus, FamilySchema.ALIVE_STATUS);
		motherBinder.bind(mOccupation, FamilySchema.OCCUPATION);
		motherBinder.bind(mJobAddress, FamilySchema.JOB_ADDRESS);
		motherBinder.bind(mCurrentAddress, FamilySchema.CURRENT_ADDRESS);
		motherBinder.bind(mCurrentProvinceId, FamilySchema.CURRENT_PROVINCE_ID);
		motherBinder.bind(mCurrentDistrict, FamilySchema.CURRENT_DISTRICT_ID);
		motherBinder.bind(mCurrentCity, FamilySchema.CURRENT_CITY_ID);
		motherBinder.bind(mCurrentPostcode, FamilySchema.CURRENT_POSTCODE_ID);
		
		spouseBinder = new FieldGroup();
		spouseBinder.setBuffered(true);
		spouseBinder.bind(sPeopleIdType, FamilySchema.PEOPLE_ID_TYPE);
		spouseBinder.bind(sPeopleid, FamilySchema.PEOPLE_ID);
		spouseBinder.bind(sPrename, FamilySchema.PRENAME);
		spouseBinder.bind(sFirstname, FamilySchema.FIRSTNAME);
		spouseBinder.bind(sLastname, FamilySchema.LASTNAME);
		spouseBinder.bind(sFirstnameNd, FamilySchema.FIRSTNAME_ND);
		spouseBinder.bind(sLastnameNd, FamilySchema.LASTNAME_ND);
		spouseBinder.bind(sGender, FamilySchema.GENDER);
		spouseBinder.bind(sReligion, FamilySchema.RELIGION);
		spouseBinder.bind(sRace, FamilySchema.RACE);
		spouseBinder.bind(sNationality, FamilySchema.NATIONALITY);
		spouseBinder.bind(sBirthDate, FamilySchema.BIRTH_DATE);
		spouseBinder.bind(sTel, FamilySchema.TEL);
		spouseBinder.bind(sMobile, FamilySchema.MOBILE);
		spouseBinder.bind(sEmail, FamilySchema.EMAIL);
		spouseBinder.bind(sSalary, FamilySchema.SALARY);
		spouseBinder.bind(sAliveStatus, FamilySchema.ALIVE_STATUS);
		spouseBinder.bind(sOccupation, FamilySchema.OCCUPATION);
		spouseBinder.bind(sJobAddress, FamilySchema.JOB_ADDRESS);
		spouseBinder.bind(sCurrentAddress, FamilySchema.CURRENT_ADDRESS);
		spouseBinder.bind(sCurrentProvinceId, FamilySchema.CURRENT_PROVINCE_ID);
		spouseBinder.bind(sCurrentDistrict, FamilySchema.CURRENT_DISTRICT_ID);
		spouseBinder.bind(sCurrentCity, FamilySchema.CURRENT_CITY_ID);
		spouseBinder.bind(sCurrentPostcode, FamilySchema.CURRENT_POSTCODE_ID);
	}
	
	private void generatePersonnelCode(String jobPosition, String autoGenerate){
		personnelCode.setEnabled(true);
		if(autoGenerate.equals("0")){
			/* รหัสเริ่มต้น 5801*/
			String personalCode = DateTimeUtil.getBuddishYear().substring(2) + jobPosition;
			
			/* ดึง รหัสที่มาทที่สุด SELECT MAX(personnel_code) FROM personnel WHERE personnel_code LIKE 'ตำแหน่ง%' */
			StringBuilder sqlBuilder = new StringBuilder();
			sqlBuilder.append(" SELECT MAX(" + PersonnelSchema.PERSONEL_CODE + ") AS " + PersonnelSchema.PERSONEL_CODE);
			sqlBuilder.append(" FROM " + PersonnelSchema.TABLE_NAME);
			sqlBuilder.append(" WHERE " + PersonnelSchema.PERSONEL_CODE + " LIKE '" + personalCode + "%'");

			personalCode += "01";
			
			SQLContainer freeContainer = Container.getFreeFormContainer(sqlBuilder.toString(), PersonnelSchema.PERSONEL_CODE);
			if(freeContainer.size() > 0){
				Item item = freeContainer.getItem(freeContainer.getIdByIndex(0));
				
				if(item.getItemProperty(PersonnelSchema.PERSONEL_CODE).getValue() != null){
					personalCode = (Integer.parseInt(item.getItemProperty(PersonnelSchema.PERSONEL_CODE).getValue().toString()) + 1) + "";
					
				}
			}

			freeContainer.removeAllContainerFilters();
			personnelCode.setValue(personalCode);
			personnelCode.setEnabled(false);
		}else{
			if(personnelBinder.getItemDataSource() == null)
				personnelCode.setValue(null);
			else{
				Item item = personnelBinder.getItemDataSource();
				personnelCode.setValue(item.getItemProperty(PersonnelSchema.PERSONEL_CODE).getValue().toString());
			}
		}
	}
	
	/* ปีดการกรอกข้อมูลหากข้อมูล ประชาชนซ้ำหรือยังไม่ได้ตรวจสอบ */
	private void disableDuplicatePeopleIdForm(){
		for(Field<?> field: personnelBinder.getFields()){
			if(!personnelBinder.getPropertyId(field).equals(PersonnelSchema.PEOPLE_ID) &&
					!personnelBinder.getPropertyId(field).equals(PersonnelSchema.PEOPLE_ID_TYPE))
				field.setEnabled(false);
		}
		for(Field<?> field: fatherBinder.getFields()){
			field.setEnabled(false);
		}
		for(Field<?> field: motherBinder.getFields()){
			field.setEnabled(false);
		}
		for(Field<?> field: spouseBinder.getFields()){
			field.setEnabled(false);
		}
		workNext.setEnabled(false);
		generalBack.setEnabled(false);
		addressNext.setEnabled(false);
		workBack.setEnabled(false);
		fatherNext.setEnabled(false);
		addressBack.setEnabled(false);
		motherNext.setEnabled(false);
		fatherBack.setEnabled(false);
		spouseNext.setEnabled(false);
		motherBack.setEnabled(false);
		finish.setEnabled(false);
	}
	
	/* ปีดการกรอกข้อมูลหากข้อมูล อีเมล์หรือยังไม่ได้ตรวจสอบ */
	private void disableDuplicateEmailForm(){
		for(Field<?> field: personnelBinder.getFields()){
			if(!personnelBinder.getPropertyId(field).equals(PersonnelSchema.EMAIL))
				field.setEnabled(false);
		}
		for(Field<?> field: fatherBinder.getFields()){
			field.setEnabled(false);
		}
		for(Field<?> field: motherBinder.getFields()){
			field.setEnabled(false);
		}
		for(Field<?> field: spouseBinder.getFields()){
			field.setEnabled(false);
		}
		fatherNext.setEnabled(false);
		addressBack.setEnabled(false);
		motherNext.setEnabled(false);
		fatherBack.setEnabled(false);
		spouseNext.setEnabled(false);
		motherBack.setEnabled(false);
		finish.setEnabled(false);
	}
	
	/* เปีดการกรอกข้อมูลหากข้อมูล ประชาชนยังไม่ได้ถูกใช้งาน */
	private void enableDuplicatePeopleIdForm(){
		for(Field<?> field: personnelBinder.getFields()){
			field.setEnabled(true);
		}
		for(Field<?> field: fatherBinder.getFields()){
			field.setEnabled(true);
		}
		for(Field<?> field: motherBinder.getFields()){
			field.setEnabled(true);
		}
		for(Field<?> field: spouseBinder.getFields()){
			field.setEnabled(true);
		}
		workNext.setEnabled(true);
		generalBack.setEnabled(true);
		addressNext.setEnabled(true);
		workBack.setEnabled(true);
		fatherNext.setEnabled(true);
		addressBack.setEnabled(true);
		motherNext.setEnabled(true);
		fatherBack.setEnabled(true);
		spouseNext.setEnabled(true);
		motherBack.setEnabled(true);
		finish.setEnabled(true);
	}

	/* ปีดการกรอกข้อมูลหากข้อมูล อีเมล์หรือยังไม่ได้ตรวจสอบ */
	private void enableDuplicateEmailForm(){
		for(Field<?> field: personnelBinder.getFields()){
			field.setEnabled(true);
		}
		for(Field<?> field: fatherBinder.getFields()){
			field.setEnabled(true);
		}
		for(Field<?> field: motherBinder.getFields()){
			field.setEnabled(true);
		}
		for(Field<?> field: spouseBinder.getFields()){
			field.setEnabled(true);
		}
		fatherNext.setEnabled(true);
		addressBack.setEnabled(true);
		motherNext.setEnabled(true);
		fatherBack.setEnabled(true);
		spouseNext.setEnabled(true);
		motherBack.setEnabled(true);
		finish.setEnabled(true);
	}
	
	/*กรณีทดสอบ ของการเพิ่มข้อมูล*/
	private void testData(){
		 peopleIdType.setValue(0);
		 peopleId.setValue("1959900163320");
		 prename.setValue(3);
		 firstname.setValue("ทดลอง");
		 lastname.setValue("ทดสอบ");
		 firstnameNd.setValue("Test");
		 lastnameNd.setValue("Test");
		 firstnameRd.setValue("");
		 lastnameRd.setValue("");
		 nickname.setValue("");
		 gender.setValue(0);
		 religion.setValue(0);
		 race.setValue(0);
		 nationality.setValue(0);
		 maritalStatus.setValue(1);
		 birthDate.setValue(new Date());
		 blood.setValue(0);
		 height.setValue("0");
		 weight.setValue("0");
		 jobPosition.setValue(0);
		 autoGenerate.setValue(1);
		 personnelCode.setValue("47612");
		 personnelStatus.setValue(0);
		 startWorkDate.setValue(new Date());
		 department.setValue(0);
		 employmentType.setValue(0);
		 bankName.setValue("ธนาคาร");
		 bankAccountNumber.setValue("123");
		 bankAccountType.setValue(0);
		 bankaccountName.setValue("นาย ทดลอง");
		 bankaccountBranch.setValue("ตลาดใหญ่");
		 bankProvinceId.setValue(0);
		 tel.setValue("123");
		 mobile.setValue("123");
		 email.setValue("aaa@sss.com");
		 
		 currentAddress.setValue("asfdasf");
		 currentProvince.setValue(1);
		 currentDistrict.setValue(1);
		 currentCity.setValue(1);
		 currentPostcode.setValue(1);
		 censusAddress.setValue("asfdasf");
		 censusProvince.setValue(1);
		 censusDistrict.setValue(1);
		 censusCity.setValue(1);
		 censusPostcode.setValue(1);
		 
		 fPeopleIdType.setValue(0);
		 fPeopleid.setValue("1959900163321");
		 fPrename.setValue(0);
		 fFirstname.setValue("asfadsf");
		 fLastname.setValue("asdfdasf");
		 fFirstnameNd.setValue("asdfadsf");
		 fLastnameNd.setValue("asdfdasf");
		 fGender.setValue(0);
		 fReligion.setValue(0);
		 fRace.setValue(0);
		 fNationality.setValue(0);
		 fBirthDate.setValue(new Date());
		 fTel.setValue("0732174283");
		 fMobile.setValue("0897375348");
		 fEmail.setValue("asdfdas@asdf.com");
		 fSalary.setValue("0");
		 fAliveStatus.setValue(0);
		 fOccupation.setValue(0);
		 fJobAddress.setValue("asfdasf");
		 fCurrentAddress.setValue("asfdasf");
		 fCurrentProvinceId.setValue(1);
		 fCurrentDistrict.setValue(1);
		 fCurrentCity.setValue(1);
		 fCurrentPostcode.setValue(1); 

		 mPeopleIdType.setValue(0);
		 mPeopleid.setValue("1959900163322");
		 mPrename.setValue(0);
		 mFirstname.setValue("asfadsf");
		 mLastname.setValue("asdfdasf");
		 mFirstnameNd.setValue("asdfadsf");
		 mLastnameNd.setValue("asdfdasf");
		 mGender.setValue(0);
		 mReligion.setValue(0);
		 mRace.setValue(0);
		 mNationality.setValue(0);
		 mBirthDate.setValue(new Date());
		 mTel.setValue("0732174283");
		 mMobile.setValue("0897375348");
		 mEmail.setValue("asdfdas@asdf.com");
		 mSalary.setValue("0");
		 mAliveStatus.setValue(0);
		 mOccupation.setValue(0);
		 mJobAddress.setValue("asfdasf");
		 mCurrentAddress.setValue("asfdasf");
		 mCurrentProvinceId.setValue(1);
	 	 mCurrentDistrict.setValue(1);
		 mCurrentCity.setValue(1);
		 mCurrentPostcode.setValue(1);
	 	
		 sPeopleIdType.setValue(0);
		 sPeopleid.setValue("1959900163323");
		 sPrename.setValue(0);
		 sFirstname.setValue("asfadsf");
		 sLastname.setValue("asdfdasf");
		 sFirstnameNd.setValue("asdfadsf");
		 sLastnameNd.setValue("asdfdasf");
		 sGender.setValue(0);
		 sReligion.setValue(0);
		 sRace.setValue(0);
		 sNationality.setValue(0);
		 sBirthDate.setValue(new Date());
		 sTel.setValue("0732174283");
		 sMobile.setValue("0897375348");
		 sEmail.setValue("asdfdas@asdf.com");
		 sSalary.setValue("0");
		 sAliveStatus.setValue(0);
		 sOccupation.setValue(0);
		 sJobAddress.setValue("asfdasf");
		 sCurrentAddress.setValue("asfdasf");
		 sCurrentProvinceId.setValue(1);
		 sCurrentDistrict.setValue(1);
		 sCurrentCity.setValue(1);
		 sCurrentPostcode.setValue(1);
	}

	/* ==================== PUBLIC ==================== */
	
	public void selectSpouseFormTab(){
		setSelectedTab(spouseForm);
	}
	/* ตั้งค่า Mode ว่าต้องการให้กำหนดข้อมูลเริ่มต้นให้เลยไหม*/
	public void setDebugMode(boolean debugMode){
		if(debugMode)
			testData();
	}
	
	/* ตั้งค่า Event สถาณภาพ */
	public void setMaritalValueChange(ValueChangeListener maritalValueChange){
		maritalStatus.addValueChangeListener(maritalValueChange);
	}
	
	/* ตั้งค่า Event ของปุ่มบันทึก */
	public void setFinishhClick(ClickListener finishClick){
		finish.addClickListener(finishClick);
	}
	
	/*อนุญาติแก้ไขฟอร์ม คู่สมรส
	 * กรณี เลือกคู่สมรสเป็นอื่น ๆ 
	 * */
	public void enableSpouseBinder(){
		spouseBinder.setEnabled(true);
		spouseBinder.setReadOnly(false);
	}
	
	/*ปิดการแก้ไขฟอร์ม คู่สมรส
	 * กรณี เลือกคู่สมรสเป็น บิดา มารดา
	 * */
	public void disableSpouseBinder(){
		spouseBinder.setEnabled(false);
		spouseBinder.setReadOnly(true);
	}
	
	/* Reset ค่าภายในฟอร์ม คู่สมรส กรณีเลือก เป็นอื่น ๆ */
	public void resetSpouse(){
		sPeopleIdType.setValue(null);
		sPeopleid.setValue(null);
		sPrename.setValue(null);
		sFirstname.setValue(null);
		sLastname.setValue(null);
		sFirstnameNd.setValue(null);
		sLastnameNd.setValue(null);
		sGender.setValue(null);
		sReligion.setValue(null);
		sRace.setValue(null);
		sNationality.setValue(null);
		sBirthDate.setValue(null);
		sTel.setValue(null);
		sMobile.setValue(null);
		sEmail.setValue(null);
		sSalary.setValue((Double)null);
		sAliveStatus.setValue(null);
		sOccupation.setValue(null);
		sJobAddress.setValue(null);
		sCurrentAddress.setValue(null);
		sCurrentProvinceId.setValue(null);
		sCurrentDistrict.setValue(null);
		sCurrentCity.setValue(null);
		sCurrentPostcode.setValue(null);
	}

	/* พิมพ์เอกสารการสมัคร*/
	public void visiblePrintButton(){
		print.setVisible(true);
	}
	
	/* ตรวจสอบข้อมูลครบถ้วน */
	public boolean validateForms(){
		/* ตรวจสอบว่าต้องการใส่ข้อมูลบิดา มาร หรือไม่*/
		if(isInsertParents){
			/* ตรวจสอบว่าข้อมูลบิดา มารดา ครบถ้วนหรือไม่*/
			if(fatherBinder.isValid() && motherBinder.isValid())
				return true;
			else{
				/* ตรวจสอบว่าสถานภาพว่า สมรส หรือไม่ */
				if(maritalStatus.equals("1")){
					/* ตรวจสอบว่าข้อมูลคู่สมรส ครบถ้วนหรือไม่*/
					if(spouseBinder.isValid())
						return true;
					else
						return false;
				}else{
					return false;
				}
			}
			
		}else{
			/* ตรวจสอบว่าข้อมูลบุคลากร ครบถ้วนหรือไม่*/
			if(personnelBinder.isValid()){
				return true;
			}
		}

		return false;
	}
}
