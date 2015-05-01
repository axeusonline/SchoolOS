package com.ies.schoolos.component.admin;

import java.util.Random;

import com.ies.schoolos.container.Container;
import com.ies.schoolos.schema.SchoolSchema;
import com.ies.schoolos.schema.SessionSchema;
import com.ies.schoolos.utility.Notification;
import com.vaadin.data.Item;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.sqlcontainer.RowId;
import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Notification.Type;

public class SignupPasswordView extends VerticalLayout {
	private static final long serialVersionUID = 1L;

	private SQLContainer schoolContainer = Container.getSchoolContainer();
	
	private FieldGroup schoolBinder;
	
	private TextField studentPassText;
	private TextField personnelPassText;
	private Button generateButton;
	private Button saveButton;
	
	public SignupPasswordView() {
		setSpacing(true);
		setMargin(true);
		buildMainLayout();
		initFieldGroup();
	}
	
	private void buildMainLayout(){
		FormLayout schoolForm = new FormLayout();
		schoolForm.setSizeFull();
		schoolForm.setStyleName("border-white");
		addComponent(schoolForm);
		
		Label title = new Label("รหัสสสมัครใช้งาน");
		schoolForm.addComponent(title);

		studentPassText = new TextField("รหัสสำหรับนักเรียน");
		studentPassText.setRequired(true);
		studentPassText.setInputPrompt("รหัสสำหรับนักเรียน");
		studentPassText.setNullRepresentation("");
		schoolForm.addComponent(studentPassText);
		
		personnelPassText = new TextField("รหัสสำหรับบุคคล");
		personnelPassText.setRequired(true);
		personnelPassText.setInputPrompt("รหัสสำหรับบุคคล");
		personnelPassText.setNullRepresentation("");
		schoolForm.addComponent(personnelPassText);
		
		HorizontalLayout buttonLayout = new HorizontalLayout();
		schoolForm.addComponent(buttonLayout);
		
		generateButton = new Button("สร้างรหัส",FontAwesome.UNLOCK_ALT);
		generateButton.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event) {
				studentPassText.setValue(randomPassword());
				personnelPassText.setValue(randomPassword());
			}
		});
		buttonLayout.addComponent(generateButton);
		
		saveButton = new Button("บันทึก",FontAwesome.SAVE);
		saveButton.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event) {
				try{
					schoolBinder.commit();
					schoolContainer.commit();
					Notification.show("บันทึกสำเร็จ", Type.HUMANIZED_MESSAGE);
				}catch(Exception e){
					Notification.show("บันทึกไม่สำเร็จ", Type.WARNING_MESSAGE);
					e.printStackTrace();
				}
			}
		});
		buttonLayout.addComponent(saveButton);
	}
	
	private void initFieldGroup(){
		schoolContainer.getItem(new RowId(SessionSchema.getSchoolID()));
		Item schoolItem = schoolContainer.getItem(schoolContainer.getIdByIndex(0));
		
		schoolBinder = new FieldGroup(schoolItem);
		schoolBinder.setBuffered(true);
		schoolBinder.bind(studentPassText, SchoolSchema.STUDENT_SIGNUP_PASS);
		schoolBinder.bind(personnelPassText, SchoolSchema.PERSONNEL_SIGNUP_PASS);
	}
	
	private String randomPassword(){
		char[] chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
		StringBuilder sb = new StringBuilder();
		Random random = new Random();
		for (int i = 0; i < 5; i++) {
		    char c = chars[random.nextInt(chars.length)];
		    sb.append(c);
		}
		return sb.toString();
	}
}
