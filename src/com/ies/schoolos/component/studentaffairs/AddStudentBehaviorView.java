package com.ies.schoolos.component.studentaffairs;

import java.util.Date;
import java.util.Locale;

import org.tepi.filtertable.FilterTable;
import org.vaadin.dialogs.ConfirmDialog;

import com.ies.schoolos.component.ui.NumberField;
import com.ies.schoolos.component.ui.SchoolOSLayout;
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
import com.vaadin.data.Container.ItemSetChangeEvent;
import com.vaadin.data.Container.ItemSetChangeListener;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.sqlcontainer.RowId;
import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Notification.Type;

public class AddStudentBehaviorView extends SchoolOSLayout {
	private static final long serialVersionUID = 1L;
	
	private boolean editMode = false;
	
	private Object studytId;
	private String firstnameStr;
	private String lastnameStr;
	private Double scoreBreak;
	private Item item;
	
	private SQLContainer freeContainer;
	private SQLContainer studentBehaviorContainer = container.getStudentBehaviorContainer();
			
	private FilterTable studentBehaviorTable;
	private FormLayout studentBehaviorTableForm;
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
		HorizontalLayout studentBehaviorTableLayout = new HorizontalLayout();
		studentBehaviorTableLayout.setSizeFull();
		studentBehaviorTableLayout.setSpacing(true);
		addComponent(studentBehaviorTableLayout);
		setExpandRatio(studentBehaviorTableLayout, 1);

		//Table
		studentBehaviorTable = new FilterTable();
		studentBehaviorTable.setSizeFull();
		studentBehaviorTable.setSelectable(true);
		studentBehaviorTable.setFooterVisible(true);        
		studentBehaviorTable.addValueChangeListener(new ValueChangeListener() {
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
		studentBehaviorTable.addItemSetChangeListener(new ItemSetChangeListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void containerItemSetChange(ItemSetChangeEvent event) {
				setFooterData();
			}
		});
		studentBehaviorTable.addContainerProperty(BehaviorSchema.NAME, String.class, null);
		studentBehaviorTable.addContainerProperty(StudentBehaviorSchema.SCORE, Double.class, null);
		studentBehaviorTable.addContainerProperty(StudentBehaviorSchema.DATE, Date.class, null);
		studentBehaviorTable.addContainerProperty(StudentBehaviorSchema.DESCRIPTION, String.class, null);
		studentBehaviorTable.addContainerProperty("", HorizontalLayout.class, null);
		
		studentBehaviorTable.setFilterDecorator(new TableFilterDecorator());
		studentBehaviorTable.setFilterGenerator(new TableFilterGenerator());
        studentBehaviorTable.setFilterBarVisible(true);

	    setFooterData();
		initTableStyle();

		studentBehaviorTable.setColumnReorderingAllowed(true);
		studentBehaviorTable.setColumnCollapsingAllowed(true);
		studentBehaviorTableLayout.addComponent(studentBehaviorTable);
		studentBehaviorTableLayout.setExpandRatio(studentBehaviorTable,(float)2.2);
		
		//Form		
		studentBehaviorTableForm = new FormLayout();
		studentBehaviorTableForm.setSpacing(true);
		studentBehaviorTableForm.setMargin(true);
		studentBehaviorTableForm.setStyleName("border-white");
		studentBehaviorTableLayout.addComponent(studentBehaviorTableForm);
		studentBehaviorTableLayout.setExpandRatio(studentBehaviorTableForm,1);
		
		Label formLab = new Label("พฤติกรรม");
		studentBehaviorTableForm.addComponent(formLab);
		
		firstname = new TextField("ชื่อ");
		firstname.setInputPrompt("ชื่อ");
		firstname.setNullRepresentation("");
		firstname.setImmediate(false);
		firstname.setRequired(true);
		firstname.setWidth("-1px");
		firstname.setHeight("-1px");
		studentBehaviorTableForm.addComponent(firstname);
		
		lastname = new TextField("สกุล");
		lastname.setInputPrompt("สกุล");
		lastname.setNullRepresentation("");
		lastname.setImmediate(false);
		lastname.setRequired(true);
		lastname.setWidth("-1px");
		lastname.setHeight("-1px");
		studentBehaviorTableForm.addComponent(lastname);
		
		behavior = new ComboBox("พฤติกรรม",new Behavior());
		behavior.setInputPrompt("กรุณาเลือก");
		behavior.setItemCaptionPropertyId("name");
		behavior.setImmediate(true);
		behavior.setNullSelectionAllowed(false);
		behavior.setRequired(true);
		behavior.setWidth("-1px");
		behavior.setHeight("-1px");
		behavior.setFilteringMode(FilteringMode.CONTAINS);
		studentBehaviorTableForm.addComponent(behavior);
		
		score = new NumberField("คะแนนที่หัก");
		score.setInputPrompt("คะแนนที่หัก");
		score.setNullRepresentation("");
		score.setImmediate(false);
		score.setRequired(true);
		score.setWidth("-1px");
		score.setHeight("-1px");
		studentBehaviorTableForm.addComponent(score);

		date = new PopupDateField("วัน เดือน ปี ที่หัก");
		date.setInputPrompt("วว/ดด/ปปปป");
		date.setImmediate(false);
		date.setRequired(true);
		date.setWidth("-1px");
		date.setHeight("-1px");
		date.setDateFormat("dd/MM/yyyy");
		date.setLocale(new Locale("th", "TH"));
		studentBehaviorTableForm.addComponent(date);
		
		description = new TextArea("รายละเอียด");
		description.setInputPrompt("รายละเอียดเพิ่มเติม");
		description.setImmediate(false);
		description.setWidth("-1px");
		description.setHeight("-1px");
		description.setNullRepresentation("");
		studentBehaviorTableForm.addComponent(description);
		
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
						Item studentBehaviorTableItem = studentBehaviorContainer.getItem(new RowId(item.getItemProperty(StudentBehaviorSchema.STUDENT_BEHAVIOR_ID).getValue()));
						studentBehaviorTableItem.getItemProperty(StudentBehaviorSchema.SCHOOL_ID).setValue(SessionSchema.getSchoolID());
						studentBehaviorTableItem.getItemProperty(StudentBehaviorSchema.STUDENT_STUDY_ID).setValue(Integer.parseInt(studytId.toString()));
						studentBehaviorTableItem.getItemProperty(StudentBehaviorSchema.BEHAVIOR_ID).setValue(Integer.parseInt(behavior.getValue().toString()));
						studentBehaviorTableItem.getItemProperty(StudentBehaviorSchema.SCORE).setValue(Double.parseDouble(score.getValue().toString()));
						studentBehaviorTableItem.getItemProperty(StudentBehaviorSchema.DATE).setValue(date.getValue());
						studentBehaviorTableItem.getItemProperty(StudentBehaviorSchema.DESCRIPTION).setValue(description.getValue());
						CreateModifiedSchema.setCreateAndModified(studentBehaviorTableItem);
						studentBehaviorContainer.commit();
						Notification.show("บันทึกสำเร็จ", Type.HUMANIZED_MESSAGE);
					}else{
						if(!behavior.isValid() &&
								!score.isValid() &&
								!date.isValid()){
							Notification.show("กรุณากรอกข้อมูลให้ครบถ้วน", Type.WARNING_MESSAGE);
							return;
						}
							
						freeContainer.removeAllContainerFilters();
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
					Notification.show("บันทึกไม่สำเร็จ", Type.WARNING_MESSAGE);
				}
			}
		});
		studentBehaviorTableForm.addComponent(save);
	}
	
	/*นำจำนวนที่นับ มาใส่ค่าในส่วนท้ายตาราง*/
	private void setFooterData(){
		studentBehaviorTable.setColumnFooter(BehaviorSchema.NAME, "ทั้งหมด: "+ studentBehaviorTable.size() + " พฤติกรรม");
		studentBehaviorTable.setColumnFooter(StudentBehaviorSchema.SCORE, "ทั้งหมด: "+ scoreBreak + " คะแนน");
	}
	
	/* ตั้งค่ารูปแบบแสดงของตาราง */
	private void initTableStyle(){		
		studentBehaviorTable.setColumnHeader(BehaviorSchema.NAME, "ชื่อ");
		studentBehaviorTable.setColumnHeader(StudentBehaviorSchema.SCORE, "คะแนนที่ตัด");
		studentBehaviorTable.setColumnHeader(StudentBehaviorSchema.DATE, "วันที่ตัด");
		studentBehaviorTable.setColumnHeader(StudentBehaviorSchema.DESCRIPTION, "รายละเอียด");
		studentBehaviorTable.setColumnHeader("", "");
			
		studentBehaviorTable.setVisibleColumns(
				BehaviorSchema.NAME, 
				StudentBehaviorSchema.SCORE,
				StudentBehaviorSchema.DATE, 
				StudentBehaviorSchema.DESCRIPTION, 
				"");
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
		studentBehaviorTable.removeAllItems();
		scoreBreak = 0.0;
		StringBuilder builder = new StringBuilder();
		builder.append(" SELECT * FROM " + StudentBehaviorSchema.TABLE_NAME + " sb");
		builder.append(" INNER JOIN " + StudentStudySchema.TABLE_NAME + " ss ON ss." + StudentStudySchema.STUDENT_STUDY_ID + "= sb." + StudentBehaviorSchema.STUDENT_STUDY_ID);
		builder.append(" INNER JOIN " + StudentSchema.TABLE_NAME + " s ON s." + StudentSchema.STUDENT_ID + "= ss." + StudentStudySchema.STUDENT_ID);
		builder.append(" INNER JOIN " + BehaviorSchema.TABLE_NAME + " b ON b." + BehaviorSchema.BEHAVIOR_ID + "= sb." + StudentBehaviorSchema.BEHAVIOR_ID);
		builder.append(" WHERE ss." + StudentBehaviorSchema.STUDENT_STUDY_ID + "=" + studytId);

		freeContainer = Container.getFreeFormContainer(builder.toString(), StudentBehaviorSchema.STUDENT_BEHAVIOR_ID);
		for(Object itemId:freeContainer.getItemIds()){
			Item studentBehaviorTableItem = freeContainer.getItem(itemId);
			studentBehaviorTable.addItem(new Object[] {
					studentBehaviorTableItem.getItemProperty(BehaviorSchema.NAME).getValue(),
					studentBehaviorTableItem.getItemProperty(StudentBehaviorSchema.SCORE).getValue(),
					studentBehaviorTableItem.getItemProperty(StudentBehaviorSchema.DATE).getValue(),
					studentBehaviorTableItem.getItemProperty(StudentBehaviorSchema.DESCRIPTION).getValue(),
					initButtonLayout(studentBehaviorTableItem, itemId)
				},itemId);
			scoreBreak += (Double) studentBehaviorTableItem.getItemProperty(StudentBehaviorSchema.SCORE).getValue();
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
	
	private HorizontalLayout initButtonLayout(final Item item, final Object itemId){
		final HorizontalLayout buttonLayout = new HorizontalLayout();
		Button removeButton = new Button(FontAwesome.TRASH_O);
		removeButton.setId(itemId.toString());
		removeButton.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 1L;
			@Override
			public void buttonClick(ClickEvent event) {
				ConfirmDialog.show(UI.getCurrent(), "ลบพฤติกรรม","คุณต้องการลบพฤติกรรมดังกล่าวใช่หรือไม่?","ตกลง","ยกเลิก",
			        new ConfirmDialog.Listener() {
						private static final long serialVersionUID = 1L;
						public void onClose(ConfirmDialog dialog) {
			                if (dialog.isConfirmed()) {
			                	Item behaviorItem = studentBehaviorTable.getItem(itemId);
	                			scoreBreak -= Double.parseDouble(behaviorItem.getItemProperty(StudentBehaviorSchema.SCORE).getValue().toString());
	                			
			                	studentBehaviorTable.removeItem(itemId);
			                	if(studentBehaviorContainer.removeItem(itemId)){
			                		try {
			                			studentBehaviorContainer.commit();
			                			setFooterData();		                			
									}catch (Exception e1) {
										Notification.show("ลบข้อมูลไม่สำเร็จ กรุณาลองใหม่อีกครั้ง" , Type.WARNING_MESSAGE);
										e1.printStackTrace();
									}
			                	}
			                }
			            }
			        });
			}
		});
		buttonLayout.addComponent(removeButton);
		buttonLayout.setComponentAlignment(removeButton, Alignment.MIDDLE_CENTER);
		return buttonLayout;

	}
	
	private void setFirstAndLastname(){
		firstname.setValue(firstnameStr);
		firstname.setReadOnly(true);
		lastname.setValue(lastnameStr);
		lastname.setReadOnly(true);
	}
}
