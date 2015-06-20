package com.ies.schoolos.component.admin;

import org.tepi.filtertable.FilterTable;
import org.vaadin.activelink.ActiveLink;
import org.vaadin.activelink.ActiveLink.LinkActivatedEvent;
import org.vaadin.activelink.ActiveLink.LinkActivatedListener;

import com.ies.schoolos.container.Container;
import com.ies.schoolos.filter.TableFilterDecorator;
import com.ies.schoolos.filter.TableFilterGenerator;
import com.ies.schoolos.schema.SessionSchema;
import com.ies.schoolos.schema.UserSchema;
import com.ies.schoolos.schema.info.PersonnelSchema;
import com.ies.schoolos.type.Prename;
import com.ies.schoolos.type.UserStatus;
import com.ies.schoolos.type.dynamic.JobPosition;
import com.ies.schoolos.utility.BCrypt;
import com.vaadin.data.Container.ItemSetChangeEvent;
import com.vaadin.data.Container.ItemSetChangeListener;
import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.filter.Compare.Equal;
import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.data.validator.EmailValidator;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Button;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.VerticalLayout;

public class UserManagerView extends VerticalLayout {

	private static final long serialVersionUID = 1L;
	
	private boolean isPassEdited = false;
	private Item userItem;
	private String passwordHashed = "";
	
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
	private ActiveLink passwordChange;
	private PasswordField password;
	private PasswordField passwordAgain;
	private Button save;	
	
	public UserManagerView() {	
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
					userItem = userContainer.getItem(event.getProperty().getValue());
					passwordHashed = userItem.getItemProperty(UserSchema.PASSWORD).getValue().toString();
					initFieldGroup();
					setHidePassword();
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
		table.addContainerProperty(UserSchema.STATUS, String.class, null);
		
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
		userForm.addComponent(passwordChange);
		
		password = new PasswordField("รหัสผ่าน");
		password.setWidth("90%");
		password.setInputPrompt("รหัสผ่าน");
		password.setVisible(false);
		password.setStyleName("input-form");
		password.setNullRepresentation("");
		password.addValueChangeListener(new ValueChangeListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				if(event.getProperty().getValue() != null)
					passwordHashed = BCrypt.hashpw(event.getProperty().getValue().toString(), BCrypt.gensalt());
			}
		});
		userForm.addComponent(password);
		
		passwordAgain = new PasswordField("รหัสผ่านอีกครั้ง");
		passwordAgain.setWidth("90%");
		passwordAgain.setInputPrompt("รหัสผ่าน");
		passwordAgain.setVisible(false);
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
					
					if(isPassEdited){
						if(password.getValue().toString().equals(passwordAgain.getValue())){
							password.setValue(passwordHashed);
						}else{
							Notification.show("รหัสผ่านไม่ตรงกัน กรุณาระบุใหม่อีกครั้ง", Type.WARNING_MESSAGE);
							return;
						}
					}
					
					if(!saveFormData())
						return;
					
					userBinder.setItemDataSource(null);
					isPassEdited = false;
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
		table.setColumnHeader(UserSchema.STATUS, "สถานะ");
		
		table.setVisibleColumns(
				PersonnelSchema.PERSONNEL_CODE, 
				PersonnelSchema.PRENAME,
				PersonnelSchema.FIRSTNAME, 
				PersonnelSchema.LASTNAME,
				PersonnelSchema.JOB_POSITION_ID,
				UserSchema.STATUS);
		
	}
	
	private void fetchData(){
		StringBuilder builder = new StringBuilder();
		builder.append(" SELECT * FROM " + UserSchema.TABLE_NAME + " u");
		builder.append(" INNER JOIN " + PersonnelSchema.TABLE_NAME + " p ON p."+PersonnelSchema.PERSONNEL_ID + "= u."+UserSchema.REF_USER_ID);
		builder.append(" WHERE u." + UserSchema.SCHOOL_ID + "=" + SessionSchema.getSchoolID());
		builder.append(" AND " + UserSchema.REF_USER_TYPE + "= 1");
		
		table.removeAllItems();
		
		freeContainer = container.getFreeFormContainer(builder.toString(), UserSchema.USER_ID);
		for(final Object itemId:freeContainer.getItemIds()){
			Item item = freeContainer.getItem(itemId);
						
			table.addItem(new Object[]{
				item.getItemProperty(PersonnelSchema.PERSONNEL_CODE).getValue(),
				Prename.getNameTh((int)item.getItemProperty(PersonnelSchema.PRENAME).getValue()),
				item.getItemProperty(PersonnelSchema.FIRSTNAME).getValue(),
				item.getItemProperty(PersonnelSchema.LASTNAME).getValue(),
				jContainer.getItem(item.getItemProperty(PersonnelSchema.JOB_POSITION_ID).getValue()).getItemProperty("name").getValue().toString(),	
				UserStatus.getNameTh(Integer.parseInt(item.getItemProperty(UserSchema.STATUS).getValue().toString())),				
			}, itemId);
		}
	}
	
	/*นำจำนวนที่นับ มาใส่ค่าในส่วนท้ายตาราง*/
	private void setFooterData(){
		table.setColumnFooter(PersonnelSchema.PERSONNEL_CODE, "ทั้งหมด: "+ table.size() + " คน");
	}
	
	/* จัดกลุ่มของ ฟอร์มในการแก้ไข - เพิ่ม ข้อมูล */
	private void initFieldGroup(){		
		userBinder = new FieldGroup(userItem);
		userBinder.setBuffered(true);
		userBinder.bind(firstname, UserSchema.FIRSTNAME);
		userBinder.bind(lastname, UserSchema.LASTNAME);
		userBinder.bind(email, UserSchema.EMAIL);
		userBinder.bind(password, UserSchema.PASSWORD);
	}	
	
	/* กำหนดค่าภายใน FieldGroup ไปยัง Item */
	private boolean saveFormData(){
		try {			
			userBinder.commit();
			userContainer.commit();
			setHidePassword();
			return true;
		} catch (Exception e) {
			Notification.show("บันทึกไม่สำเร็จ", Type.WARNING_MESSAGE);
			e.printStackTrace();
			return false;
		}
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
}
