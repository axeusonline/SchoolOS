package com.ies.schoolos.component.studentaffairs;

import java.util.Date;

import org.tepi.filtertable.FilterTable;

import com.ies.schoolos.component.ui.NumberField;
import com.ies.schoolos.container.Container;
import com.ies.schoolos.filter.TableFilterDecorator;
import com.ies.schoolos.filter.TableFilterGenerator;
import com.ies.schoolos.schema.CreateModifiedSchema;
import com.ies.schoolos.schema.SessionSchema;
import com.ies.schoolos.schema.fundamental.BehaviorSchema;
import com.ies.schoolos.schema.info.StudentSchema;
import com.ies.schoolos.schema.info.StudentStudySchema;
import com.ies.schoolos.schema.studentaffairs.StudentBehaviorSchema;
import com.ies.schoolos.type.dynamic.Behavior;
import com.ies.schoolos.utility.Notification;
import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.sqlcontainer.RowId;
import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Notification.Type;

public class AddStudentBehaviorView extends VerticalLayout {
	private static final long serialVersionUID = 1L;
	
	private boolean editMode = false;
	
	private Object studytId;
	private String firstnameStr;
	private String lastnameStr;
	private Double scoreBreak;
	private Item item;
	
	private SQLContainer freeContainer;
	private SQLContainer studentBehaviorContainer = Container.getStudentBehaviorContainer();
			
	private FilterTable studentBehavior;
	private FormLayout studentBehaviorForm;
	private TextField firstname;
	private TextField lastname;
	private ComboBox behavior;
	private TextField score;
	private PopupDateField date;
	private TextArea description;
	private Button save;	 
	
	public AddStudentBehaviorView(Object studytId) {
		this.studytId = studytId;
		
		setSizeFull();
		setSpacing(true);
		setMargin(true);
		buildMainLayout();
		fetchData();
		setInitialDataForm();
		setFirstAndLastname();
		setFooterData();
	}
	
	private void buildMainLayout(){
		HorizontalLayout studentBehaviorLayout = new HorizontalLayout();
		studentBehaviorLayout.setSizeFull();
		studentBehaviorLayout.setSpacing(true);
		addComponent(studentBehaviorLayout);
		setExpandRatio(studentBehaviorLayout, 1);

		//Table
		studentBehavior = new FilterTable();
		studentBehavior.setSizeFull();
		studentBehavior.setSelectable(true);
		studentBehavior.setFooterVisible(true);        
		studentBehavior.addValueChangeListener(new ValueChangeListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				if(event.getProperty().getValue() != null){
					editMode = true;
					save.setCaption("แก้ไข");
					item = freeContainer.getItem(event.getProperty().getValue());

					setFirstAndLastname();
					behavior.setValue(Integer.parseInt(item.getItemProperty(StudentBehaviorSchema.BEHAVIOR_ID).getValue().toString()));
					score.setValue(item.getItemProperty(StudentBehaviorSchema.SCORE).getValue().toString());
					date.setValue((Date)item.getItemProperty(StudentBehaviorSchema.DATE).getValue());
					if(item.getItemProperty(StudentBehaviorSchema.DESCRIPTION).getValue() != null)
						description.setValue(item.getItemProperty(StudentBehaviorSchema.DESCRIPTION).getValue().toString());
				}
			}
		});

		studentBehavior.addContainerProperty(BehaviorSchema.NAME, String.class, null);
		studentBehavior.addContainerProperty(StudentBehaviorSchema.SCORE, Double.class, null);
		studentBehavior.addContainerProperty(StudentBehaviorSchema.DATE, Date.class, null);
		studentBehavior.addContainerProperty(StudentBehaviorSchema.DESCRIPTION, String.class, null);
		
		studentBehavior.setFilterDecorator(new TableFilterDecorator());
		studentBehavior.setFilterGenerator(new TableFilterGenerator());
        studentBehavior.setFilterBarVisible(true);

	    setFooterData();
		initTableStyle();

		studentBehavior.setColumnReorderingAllowed(true);
		studentBehavior.setColumnCollapsingAllowed(true);
		studentBehaviorLayout.addComponent(studentBehavior);
		studentBehaviorLayout.setExpandRatio(studentBehavior,(float)2.2);
		
		//Form		
		studentBehaviorForm = new FormLayout();
		studentBehaviorForm.setSpacing(true);
		studentBehaviorForm.setMargin(true);
		studentBehaviorForm.setStyleName("border-white");
		studentBehaviorLayout.addComponent(studentBehaviorForm);
		studentBehaviorLayout.setExpandRatio(studentBehaviorForm,1);
		
		Label formLab = new Label("พฤติกรรม");
		studentBehaviorForm.addComponent(formLab);
		
		firstname = new TextField("ชื่อ");
		firstname.setInputPrompt("ชื่อ");
		firstname.setNullRepresentation("");
		firstname.setImmediate(false);
		firstname.setRequired(true);
		firstname.setWidth("-1px");
		firstname.setHeight("-1px");
		studentBehaviorForm.addComponent(firstname);
		
		lastname = new TextField("สกุล");
		lastname.setInputPrompt("สกุล");
		lastname.setNullRepresentation("");
		lastname.setImmediate(false);
		lastname.setRequired(true);
		lastname.setWidth("-1px");
		lastname.setHeight("-1px");
		studentBehaviorForm.addComponent(lastname);
		
		behavior = new ComboBox("พฤติกรรม",new Behavior());
		behavior.setInputPrompt("กรุณาเลือก");
		behavior.setItemCaptionPropertyId("name");
		behavior.setImmediate(true);
		behavior.setNullSelectionAllowed(false);
		behavior.setRequired(true);
		behavior.setWidth("-1px");
		behavior.setHeight("-1px");
		behavior.setFilteringMode(FilteringMode.CONTAINS);
		studentBehaviorForm.addComponent(behavior);
		
		score = new NumberField("คะแนนที่หัก");
		score.setInputPrompt("คะแนนที่หัก");
		score.setNullRepresentation("");
		score.setImmediate(false);
		score.setRequired(true);
		score.setWidth("-1px");
		score.setHeight("-1px");
		studentBehaviorForm.addComponent(score);

		date = new PopupDateField("วัน เดือน ปี ที่หัก");
		date.setInputPrompt("วว/ดด/ปปปป(คศ)");
		date.setImmediate(false);
		date.setRequired(true);
		date.setWidth("-1px");
		date.setHeight("-1px");
		studentBehaviorForm.addComponent(date);
		
		description = new TextArea("รายละเอียด");
		description.setInputPrompt("รายละเอียดเพิ่มเติม");
		description.setImmediate(false);
		description.setWidth("-1px");
		description.setHeight("-1px");
		description.setNullRepresentation("");
		studentBehaviorForm.addComponent(description);
		
		save = new Button("บันทึก", FontAwesome.SAVE);
		save.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 1L;

			@SuppressWarnings("unchecked")
			@Override
			public void buttonClick(ClickEvent event) {
				try {
					/* ตรวจสอบสถานะการจัดการข้อมูล
					 *  กรณีเป็น แก้ไข จะทำการ Update โดยใช้ข้อมูลในฟอร์มเดิม
					 *  กรณี เป็น เพิ่ม จะทำการ Inser โดยใช้ข้อมูลใหม่ที่กรอกในฟอร์ม */
					if(editMode){
						editMode = false;
						Item studentBehaviorItem = studentBehaviorContainer.getItem(new RowId(item.getItemProperty(StudentBehaviorSchema.STUDENT_BEHAVIOR_ID).getValue()));
						studentBehaviorItem.getItemProperty(StudentBehaviorSchema.SCHOOL_ID).setValue(SessionSchema.getSchoolID());
						studentBehaviorItem.getItemProperty(StudentBehaviorSchema.STUDENT_STUDY_ID).setValue(Integer.parseInt(studytId.toString()));
						studentBehaviorItem.getItemProperty(StudentBehaviorSchema.BEHAVIOR_ID).setValue(Integer.parseInt(behavior.getValue().toString()));
						studentBehaviorItem.getItemProperty(StudentBehaviorSchema.SCORE).setValue(Double.parseDouble(score.getValue().toString()));
						studentBehaviorItem.getItemProperty(StudentBehaviorSchema.DATE).setValue(date.getValue());
						studentBehaviorItem.getItemProperty(StudentBehaviorSchema.DESCRIPTION).setValue(description.getValue());
						CreateModifiedSchema.setCreateAndModified(studentBehaviorItem);
						studentBehaviorContainer.commit();
						Notification.show("บันทึกสำเร็จ", Type.HUMANIZED_MESSAGE);
					}else{
						freeContainer.removeAllContainerFilters();
						if(!behavior.isValid() &&
								!score.isValid() &&
								!date.isValid()){
							Notification.show("กรุณากรอกข้อมูลให้ครบถ้วน", Type.WARNING_MESSAGE);
							return;
						}
							
						if(!saveFormData())
							return;						
					}
					item = null;
					save.setCaption("บันทึก");
					fetchData();
					setFooterData();
					setFirstAndLastname();
					behavior.setValue(null);
					score.setValue(null);
					date.setValue(null);
					description.setValue(null);
					Notification.show("บันทึกสำเร็จ", Type.HUMANIZED_MESSAGE);
				} catch (Exception e) {
					e.printStackTrace();
					Notification.show("บันทึกไม่สำเร็จ", Type.HUMANIZED_MESSAGE);
				}
			}
		});
		studentBehaviorForm.addComponent(save);
	}
	
	/*นำจำนวนที่นับ มาใส่ค่าในส่วนท้ายตาราง*/
	private void setFooterData(){
		studentBehavior.setColumnFooter(BehaviorSchema.NAME, "ทั้งหมด: "+ studentBehavior.size() + " พฤติกรรม");
		studentBehavior.setColumnFooter(StudentBehaviorSchema.SCORE, "ทั้งหมด: "+ scoreBreak + " คะแนน");
	}
	
	/* ตั้งค่ารูปแบบแสดงของตาราง */
	private void initTableStyle(){		
		studentBehavior.setColumnHeader(BehaviorSchema.NAME, "ชื่อ");
		studentBehavior.setColumnHeader(StudentBehaviorSchema.SCORE, "คะแนนที่ตัด");
		studentBehavior.setColumnHeader(StudentBehaviorSchema.DATE, "วันที่ตัด");
		studentBehavior.setColumnHeader(StudentBehaviorSchema.DESCRIPTION, "รายละเอียด");
			
		studentBehavior.setVisibleColumns(
				BehaviorSchema.NAME, 
				StudentBehaviorSchema.SCORE,
				StudentBehaviorSchema.DATE, 
				StudentBehaviorSchema.DESCRIPTION);
	}
	
	/* กำหนดค่าภายใน FieldGroup ไปยัง Item */
	@SuppressWarnings({ "unchecked"})
	private boolean saveFormData(){
		try {				
			/* เพิ่มข้อมูล */
			Object tmpItem = studentBehaviorContainer.addItem();
			Item item = studentBehaviorContainer.getItem(tmpItem);

			item.getItemProperty(StudentBehaviorSchema.SCHOOL_ID).setValue(SessionSchema.getSchoolID());
			item.getItemProperty(StudentBehaviorSchema.STUDENT_STUDY_ID).setValue(Integer.parseInt(studytId.toString()));
			item.getItemProperty(StudentBehaviorSchema.BEHAVIOR_ID).setValue(Integer.parseInt(behavior.getValue().toString()));
			item.getItemProperty(StudentBehaviorSchema.SCORE).setValue(Double.parseDouble(score.getValue().toString()));
			item.getItemProperty(StudentBehaviorSchema.DATE).setValue(date.getValue());
			item.getItemProperty(StudentBehaviorSchema.DESCRIPTION).setValue(description.getValue());
			CreateModifiedSchema.setCreateAndModified(item);
			studentBehaviorContainer.commit();
			fetchData();
			setFooterData();
			return true;
		} catch (Exception e) {
			Notification.show("บันทึกไม่สำเร็จ", Type.WARNING_MESSAGE);
			e.printStackTrace();
			return false;
		}
	}
	
	private void fetchData(){
		studentBehavior.removeAllItems();
		scoreBreak = 0.0;
		StringBuilder builder = new StringBuilder();
		builder.append(" SELECT * FROM " + StudentBehaviorSchema.TABLE_NAME + " sb");
		builder.append(" INNER JOIN " + StudentStudySchema.TABLE_NAME + " ss ON ss." + StudentStudySchema.STUDENT_STUDY_ID + "= sb." + StudentBehaviorSchema.STUDENT_STUDY_ID);
		builder.append(" INNER JOIN " + StudentSchema.TABLE_NAME + " s ON s." + StudentSchema.STUDENT_ID + "= ss." + StudentStudySchema.STUDENT_ID);
		builder.append(" INNER JOIN " + BehaviorSchema.TABLE_NAME + " b ON b." + BehaviorSchema.BEHAVIOR_ID + "= sb." + StudentBehaviorSchema.BEHAVIOR_ID);
		builder.append(" WHERE ss." + StudentBehaviorSchema.STUDENT_STUDY_ID + "=" + studytId);
		System.err.println(builder.toString());
		freeContainer = Container.getFreeFormContainer(builder.toString(), StudentBehaviorSchema.STUDENT_BEHAVIOR_ID);
		for(Object itemId:freeContainer.getItemIds()){
			Item studentBehaviorItem = freeContainer.getItem(itemId);
			studentBehavior.addItem(new Object[] {
					studentBehaviorItem.getItemProperty(BehaviorSchema.NAME).getValue(),
					studentBehaviorItem.getItemProperty(StudentBehaviorSchema.SCORE).getValue(),
					studentBehaviorItem.getItemProperty(StudentBehaviorSchema.DATE).getValue(),
					studentBehaviorItem.getItemProperty(StudentBehaviorSchema.DESCRIPTION).getValue()
				},itemId);
			scoreBreak += (Double) studentBehaviorItem.getItemProperty(StudentBehaviorSchema.SCORE).getValue();
		}
	}
	
	private void setInitialDataForm(){
		StringBuilder studyBuilder = new StringBuilder();
		studyBuilder.append(" SELECT * FROM " + StudentStudySchema.TABLE_NAME + " ss");
		studyBuilder.append(" INNER JOIN " + StudentSchema.TABLE_NAME + " s ON s." + StudentSchema.STUDENT_ID + "= ss." + StudentStudySchema.STUDENT_ID);
		studyBuilder.append(" WHERE " + StudentStudySchema.STUDENT_STUDY_ID + "=" + studytId);

		SQLContainer freeFirstnameContainer = Container.getFreeFormContainer(studyBuilder.toString(), StudentStudySchema.STUDENT_STUDY_ID);
		Item studyItem = freeFirstnameContainer.getItem(freeFirstnameContainer.getIdByIndex(0));
		
		firstnameStr = studyItem.getItemProperty(StudentSchema.FIRSTNAME).getValue().toString();
		lastnameStr = studyItem.getItemProperty(StudentSchema.LASTNAME).getValue().toString();
	}
	
	private void setFirstAndLastname(){
		firstname.setValue(firstnameStr);
		firstname.setReadOnly(true);
		lastname.setValue(lastnameStr);
		lastname.setReadOnly(true);
	}
}
