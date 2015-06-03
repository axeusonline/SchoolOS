package com.ies.schoolos.component.admin;

import org.tepi.filtertable.FilterTable;

import com.ies.schoolos.container.Container;
import com.ies.schoolos.filter.TableFilterDecorator;
import com.ies.schoolos.filter.TableFilterGenerator;
import com.ies.schoolos.schema.CreateModifiedSchema;
import com.ies.schoolos.schema.SchoolSchema;
import com.ies.schoolos.schema.SessionSchema;
import com.ies.schoolos.schema.UserSchema;
import com.ies.schoolos.schema.info.PersonnelSchema;
import com.ies.schoolos.type.Feature;
import com.ies.schoolos.type.Prename;
import com.ies.schoolos.type.dynamic.JobPosition;
import com.ies.schoolos.utility.BCrypt;
import com.ies.schoolos.utility.EmailSender;
import com.vaadin.data.Item;
import com.vaadin.data.Container.ItemSetChangeEvent;
import com.vaadin.data.Container.ItemSetChangeListener;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.filter.Compare.Equal;
import com.vaadin.data.util.sqlcontainer.RowId;
import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.data.validator.EmailValidator;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.UI;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Notification.Type;

public class PerseonnelUserManagerView extends VerticalLayout {

	private static final long serialVersionUID = 1L;
	
	private Object personnelId;
	
	private Container container = new Container();
	private SQLContainer freeContainer;
	private SQLContainer userContainer = container.getUserContainer();
	
	private JobPosition jContainer = new JobPosition();
	
	private FilterTable  table;
	
	private FieldGroup userBinder;
	private FormLayout userForm;
	private TextField firstname;
	private TextField lastname;
	private TextField email;
	private PasswordField password;
	private PasswordField passwordAgain;
	private Button save;	
	
	public PerseonnelUserManagerView() {			
		setSizeFull();
		setSpacing(true);
		setMargin(true);
		buildMainLayout();
		initFieldGroup();
		fetchData();
		setFooterData();
	}	
	
	private void buildMainLayout(){
		HorizontalLayout userLayout = new HorizontalLayout();
		userLayout.setSizeFull();
		userLayout.setSpacing(true);
		addComponent(userLayout);
		setExpandRatio(userLayout, 1);
		
		/* Content */
		table = new FilterTable();
		table.setSizeFull();
		table.setSelectable(true);
		table.setFooterVisible(true);        
		table.addValueChangeListener(new ValueChangeListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				if(event.getProperty().getValue() != null){
					Item personnelItem = freeContainer.getItem(event.getProperty().getValue());
					
					personnelId = event.getProperty().getValue();
					firstname.setValue(personnelItem.getItemProperty(PersonnelSchema.FIRSTNAME).getValue().toString());
					lastname.setValue(personnelItem.getItemProperty(PersonnelSchema.LASTNAME).getValue().toString());
					
					if(personnelItem.getItemProperty(PersonnelSchema.EMAIL).getValue() != null)
						email.setValue(personnelItem.getItemProperty(PersonnelSchema.EMAIL).getValue().toString());
					password.setValue(personnelItem.getItemProperty(PersonnelSchema.PEOPLE_ID).getValue().toString());
					passwordAgain.setValue(personnelItem.getItemProperty(PersonnelSchema.PEOPLE_ID).getValue().toString());
				}
			}
		});
		table.addItemSetChangeListener(new ItemSetChangeListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void containerItemSetChange(ItemSetChangeEvent event) {
				setFooterData();
			}
		});
		table.addContainerProperty(PersonnelSchema.PERSONNEL_CODE, String.class, null);
		table.addContainerProperty(PersonnelSchema.PRENAME, String.class, null);
		table.addContainerProperty(PersonnelSchema.FIRSTNAME, String.class, null);
		table.addContainerProperty(PersonnelSchema.LASTNAME, String.class, null);
		table.addContainerProperty(PersonnelSchema.JOB_POSITION_ID, String.class, null);
		
		table.setFilterDecorator(new TableFilterDecorator());
		table.setFilterGenerator(new TableFilterGenerator());
        table.setFilterBarVisible(true);
        
		initTableStyle();
		table.sort(new Object[]{PersonnelSchema.PERSONNEL_CODE}, new boolean[]{true});

		table.setColumnReorderingAllowed(true);
		table.setColumnCollapsingAllowed(true);
		userLayout.addComponent(table);
		userLayout.setExpandRatio(table, 2);
		
		userForm = new FormLayout();
		userForm.setSpacing(true);
		userForm.setStyleName("border-white");
		userLayout.addComponent(userForm);
		userLayout.setExpandRatio(userForm, 1);
		
		Label formLab = new Label("ผู้ใช้งาน");
		userForm.addComponent(formLab);
		
		firstname = new TextField("ชื่อ");
		firstname.setInputPrompt("ชื่อ");
		firstname.setNullRepresentation("");
		firstname.setImmediate(false);
		firstname.setRequired(true);
		firstname.setWidth("-1px");
		firstname.setHeight("-1px");
		userForm.addComponent(firstname);
		
		lastname = new TextField("สกุล");
		lastname.setInputPrompt("สกุล");
		lastname.setNullRepresentation("");
		lastname.setImmediate(false);
		lastname.setRequired(true);
		lastname.setWidth("-1px");
		lastname.setHeight("-1px");
		userForm.addComponent(lastname);
		
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
				if(event.getText() != null){
					if(event.getText().length() >= 13){
						
						userContainer.addContainerFilter(new Equal(UserSchema.EMAIL,event.getText()));
						if(userContainer.size() > 0){
							save.setEnabled(false);
							Notification.show("อีเมล์ถูกใช้งานแล้ว กรุณาระบุใหม่อีกครั้ง", Type.WARNING_MESSAGE);
						}else{
							save.setEnabled(true);
						}
						userContainer.removeAllContainerFilters();
					}
				}
			}
		});
		userForm.addComponent(email);
		
		password = new PasswordField("รหัสผ่าน");
		password.setWidth("90%");
		password.setInputPrompt("รหัสผ่าน");
		password.setStyleName("input-form");
		password.setNullRepresentation("");
		userForm.addComponent(password);
		
		passwordAgain = new PasswordField("รหัสผ่านอีกครั้ง");
		passwordAgain.setWidth("90%");
		passwordAgain.setInputPrompt("รหัสผ่าน");
		passwordAgain.setStyleName("input-form");
		passwordAgain.setNullRepresentation("");
		userForm.addComponent(passwordAgain);
		
		save = new Button("บันทึก", FontAwesome.SAVE);
		save.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event) {
				try {
					if(!userBinder.isValid()){
						Notification.show("กรุณากรอกข้อมูลให้ครบถ้วน", Type.WARNING_MESSAGE);
						return;
					}

					userContainer.removeAllContainerFilters();
					if(!saveFormData())
						return;
					
					userBinder.setItemDataSource(null);
					fetchData();
					
					Notification.show("บันทึกสำเร็จ", Type.HUMANIZED_MESSAGE);
				} catch (Exception e) {
					e.printStackTrace();
					Notification.show("บันทึกไม่สำเร็จ", Type.WARNING_MESSAGE);
				}
			}
		});
		userForm.addComponent(save);
	}
	
	/* ตั้งค่ารูปแบบแสดงของตาราง */
	private void initTableStyle(){		
		table.setColumnHeader(PersonnelSchema.PERSONNEL_CODE, "หมายเลขประจำตัว");
		table.setColumnHeader(PersonnelSchema.PRENAME, "ชื่อต้น");
		table.setColumnHeader(PersonnelSchema.FIRSTNAME, "ชื่อ");
		table.setColumnHeader(PersonnelSchema.LASTNAME, "สกุล");
		table.setColumnHeader(PersonnelSchema.JOB_POSITION_ID, "ตำแหน่ง");
		
		table.setVisibleColumns(
				PersonnelSchema.PERSONNEL_CODE, 
				PersonnelSchema.PRENAME,
				PersonnelSchema.FIRSTNAME, 
				PersonnelSchema.LASTNAME,
				PersonnelSchema.JOB_POSITION_ID);
		
	}
	
	private void fetchData(){
		StringBuilder builder = new StringBuilder();
		builder.append(" SELECT * FROM " + PersonnelSchema.TABLE_NAME);
		builder.append(" WHERE " + PersonnelSchema.SCHOOL_ID + "=" + SessionSchema.getSchoolID());
		builder.append(" AND " + PersonnelSchema.PERSONNEL_ID + " NOT IN (");
		builder.append(" SELECT " + UserSchema.REF_USER_ID + " FROM " + UserSchema.TABLE_NAME);
		builder.append(" WHERE " + UserSchema.SCHOOL_ID + "=" + SessionSchema.getSchoolID());
		builder.append(" AND " + UserSchema.REF_USER_TYPE + "=" + 1 + ")");
		
		table.removeAllItems();
		
		freeContainer = container.getFreeFormContainer(builder.toString(), PersonnelSchema.PERSONNEL_ID);
		for(final Object itemId:freeContainer.getItemIds()){
			Item item = freeContainer.getItem(itemId);
						
			table.addItem(new Object[]{
				item.getItemProperty(PersonnelSchema.PERSONNEL_CODE).getValue(),
				Prename.getNameTh((int)item.getItemProperty(PersonnelSchema.PRENAME).getValue()),
				item.getItemProperty(PersonnelSchema.FIRSTNAME).getValue(),
				item.getItemProperty(PersonnelSchema.LASTNAME).getValue(),
				jContainer.getItem(item.getItemProperty(PersonnelSchema.JOB_POSITION_ID).getValue()).getItemProperty("name").getValue().toString(),				
			}, itemId);
		}
	}
	
	/*นำจำนวนที่นับ มาใส่ค่าในส่วนท้ายตาราง*/
	private void setFooterData(){
		table.setColumnFooter(PersonnelSchema.PERSONNEL_CODE, "ทั้งหมด: "+ table.size() + " คน");
	}
	
	/* จัดกลุ่มของ ฟอร์มในการแก้ไข - เพิ่ม ข้อมูล */
	private void initFieldGroup(){		
		userBinder = new FieldGroup();
		userBinder.setBuffered(true);
		userBinder.bind(firstname, UserSchema.FIRSTNAME);
		userBinder.bind(lastname, UserSchema.LASTNAME);
		userBinder.bind(email, UserSchema.EMAIL);
		userBinder.bind(password, UserSchema.PASSWORD);
	}	
	
	/* กำหนดค่าภายใน FieldGroup ไปยัง Item */
	@SuppressWarnings({ "unchecked"})
	private boolean saveFormData(){
		try {				
			/* เพิ่มข้อมูล */
			Object tmpItem = userContainer.addItem();
			Item item = userContainer.getItem(tmpItem);

			item.getItemProperty(UserSchema.SCHOOL_ID).setValue(SessionSchema.getSchoolID());
			item.getItemProperty(UserSchema.FIRSTNAME).setValue(firstname.getValue());
			item.getItemProperty(UserSchema.LASTNAME).setValue(lastname.getValue());
			item.getItemProperty(UserSchema.EMAIL).setValue(email.getValue());
			item.getItemProperty(UserSchema.PASSWORD).setValue(BCrypt.hashpw(password.getValue(), BCrypt.gensalt()));
			item.getItemProperty(UserSchema.STATUS).setValue(0);
			item.getItemProperty(UserSchema.REF_USER_ID).setValue(Integer.parseInt(personnelId.toString()));
			item.getItemProperty(UserSchema.REF_USER_TYPE).setValue(1);
			Feature.setPermission(item, false);
			CreateModifiedSchema.setCreateAndModified(item);
			userContainer.commit();
			
			final Window window = new Window("รหัสเข้าใช้ระบบ กรุณาจดบันทึก");
			window.setWidth("400px");
			window.setHeight("150px");
			window.center();
			UI.getCurrent().addWindow(window);
			
			VerticalLayout labelLayout = new VerticalLayout();
			labelLayout.setWidth("100%");
			labelLayout.setMargin(true);
			window.setContent(labelLayout);
			
			StringBuilder builder = new StringBuilder();
			String schoolName = "";
			if(SessionSchema.getSchoolName() == null){
				Item schoolItem = container.getSchoolContainer().getItem(new RowId(SessionSchema.getSchoolID()));
				schoolName += schoolItem.getItemProperty(SchoolSchema.NAME).getValue().toString();
			}else{
				schoolName = SessionSchema.getSchoolName().toString();
			}
			
			builder.append(schoolName + "<br/>");
			builder.append("ชื่อ-สกุล :" + firstname.getValue() + " " + lastname.getValue() + "<br/>");
			builder.append("บัญชีผู้ใช้ :" + email.getValue() + "<br/>");
			builder.append("รหัสผ่าน:" + password.getValue());
			
			Label username = new Label(builder.toString());
			username.setContentMode(ContentMode.HTML);
			labelLayout.addComponent(username);
			
			sendEmail(email.getValue(), firstname.getValue() + " " + lastname.getValue(), email.getValue(), password.getValue());
			
			return true;
		} catch (Exception e) {
			Notification.show("บันทึกไม่สำเร็จ", Type.WARNING_MESSAGE);
			e.printStackTrace();
			return false;
		}
	}
	
	/* ส่งอีเมล์ใบสมัคร */
	private void sendEmail(String email,String person, String username, String password){
		String subject = "บัญชีผู้ใช้งาน SchoolOS";
		StringBuilder description = new StringBuilder();
		description.append("เรียนคุณ " + person);
		description.append(System.getProperty("line.separator"));
		description.append("ทางครอบครัว SchoolOS ได้ทำการจัดส่งบัญชีผู้ใช้จากการตั้งค่าของ เจ้าหน้าที่ IT โรงเรียน โดยรายละเอียดการเข้าใช้อธิบายดังข้างล่างนี้");
		description.append(System.getProperty("line.separator"));
		description.append("บัญชี:" + username);
		description.append(System.getProperty("line.separator"));
		description.append("รหัสผ่าน:" + password);
		description.append(System.getProperty("line.separator"));
		description.append("ทั้งนี้หากมีข้อสงสัยกรุณาส่งกลับที่ " + SessionSchema.getEmail());
		description.append(System.getProperty("line.separator"));
		description.append("ด้วยความเคารพ");
		description.append(System.getProperty("line.separator"));
		description.append("ครอบครัว SchoolOS");
		
		
		new EmailSender(email,subject,description.toString(),null, null);	   
	}
}
