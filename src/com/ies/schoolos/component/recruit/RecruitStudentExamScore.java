package com.ies.schoolos.component.recruit;

import org.tepi.filtertable.FilterTable;

import com.ies.schoolos.component.ui.ContentPage;
import com.ies.schoolos.container.Container;
import com.ies.schoolos.filter.TableFilterDecorator;
import com.ies.schoolos.filter.TableFilterGenerator;
import com.ies.schoolos.schema.SessionSchema;
import com.ies.schoolos.schema.recruit.RecruitStudentSchema;
import com.ies.schoolos.type.ClassRange;
import com.ies.schoolos.type.Prename;
import com.ies.schoolos.utility.DateTimeUtil;
import com.vaadin.data.Item;
import com.vaadin.data.Container.ItemSetChangeEvent;
import com.vaadin.data.Container.ItemSetChangeListener;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.filter.And;
import com.vaadin.data.util.filter.Compare.Equal;
import com.vaadin.data.util.filter.Compare.Greater;
import com.vaadin.data.util.filter.Compare.Less;
import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Button;
import com.vaadin.ui.CustomTable;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CustomTable.ColumnGenerator;
import com.vaadin.ui.Notification.Type;

public class RecruitStudentExamScore extends ContentPage {
private static final long serialVersionUID = 1L;

	private Container container = new Container();
	private SQLContainer sContainer = container.getRecruitStudentContainer();
	
	private Item item;
	
	private HorizontalLayout scoreLayout;
	private FilterTable  table;
	
	private FieldGroup scoreBinder;
	private FormLayout scoreForm;
	private TextField firstname;
	private TextField lastname;
	private TextField score;
	private Button save;	
	
	public RecruitStudentExamScore() {	
		super("คะแนนสอบ");
		sContainer.refresh();
		sContainer.removeAllContainerFilters();
		sContainer.addContainerFilter(new And(
				new Equal(RecruitStudentSchema.SCHOOL_ID, SessionSchema.getSchoolID()),
				new Greater(RecruitStudentSchema.REGISTER_DATE,DateTimeUtil.getFirstDateOfYear()),
				new Less(RecruitStudentSchema.REGISTER_DATE,DateTimeUtil.getLastDateOfYear())));
		
		buildMainLayout();
	}	
	
	private void buildMainLayout(){		
		/* Content */
		scoreLayout = new HorizontalLayout();
		scoreLayout.setSpacing(true);
		scoreLayout.setWidth("-1px");
		scoreLayout.setHeight("100%");
		addComponent(scoreLayout);	
		setExpandRatio(scoreLayout, 1);
		
		/* ==== ตารางรายการนักเรียน ==== */
		table = new FilterTable();
		table.setSelectable(true);
		table.setFooterVisible(true);   
		table.setSizeFull();
		table.addValueChangeListener(new ValueChangeListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				if(event.getProperty().getValue() != null){
					item = sContainer.getItem(event.getProperty().getValue());
					initFieldGroup();
					setEditMode();
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
		table.setFilterDecorator(new TableFilterDecorator());
		table.setFilterGenerator(new TableFilterGenerator());
        table.setFilterBarVisible(true);

		table.setContainerDataSource(sContainer);
	    setFooterData();
		initTableStyle();

		table.setColumnReorderingAllowed(true);
		table.setColumnCollapsingAllowed(true);
		
		scoreLayout.addComponent(table);
		scoreLayout.setExpandRatio(table, 1);
		
		/* ==== Form จัดการคะแนนสอบ ==== */
		scoreForm = new FormLayout();
		scoreForm.setWidth("250px");
		scoreForm.setHeight("-1px");
		scoreForm.setSpacing(true);
		scoreForm.setStyleName("border-white");
		scoreLayout.addComponent(scoreForm);
		
		Label formLab = new Label("ข้อมูลนักเรียน");
		scoreForm.addComponent(formLab);
		
		firstname = new TextField();
		firstname.setInputPrompt("ชื่อ");
		firstname.setNullRepresentation("");
		firstname.setImmediate(false);
		firstname.setWidth("-1px");
		firstname.setHeight("-1px");
		scoreForm.addComponent(firstname);

		lastname = new TextField();
		lastname.setInputPrompt("สกุล");
		lastname.setNullRepresentation("");
		lastname.setImmediate(false);
		lastname.setWidth("-1px");
		lastname.setHeight("-1px");
		scoreForm.addComponent(lastname);
		
		score = new TextField();
		score.setInputPrompt("คะแนน");
		score.setNullRepresentation("");
		score.setImmediate(false);
		score.setWidth("-1px");
		score.setHeight("-1px");
		scoreForm.addComponent(score);

		save = new Button("บันทึก", FontAwesome.SAVE);
		save.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event) {
				try {
					scoreBinder.commit();
					sContainer.commit();
					updateTable();
					Notification.show("บันทึกสำเร็จ", Type.HUMANIZED_MESSAGE);
				} catch (Exception e) {
					Notification.show("บันทึกไม่สำเร็จ", Type.WARNING_MESSAGE);
				}
			}
		});
		scoreForm.addComponent(save);
		
		setNormalMode();
	}
	
	/* ตั้งค่ารูปแบบแสดงของตาราง */
	private void initTableStyle(){		
		table.setColumnHeader(RecruitStudentSchema.RECRUIT_CODE, "หมายเลขสมัคร");
		table.setColumnHeader(RecruitStudentSchema.CLASS_RANGE,"ช่วงชั้น");
		table.setColumnHeader(RecruitStudentSchema.PRENAME, "ชื่อต้น");
		table.setColumnHeader(RecruitStudentSchema.FIRSTNAME, "ชื่อ");
		table.setColumnHeader(RecruitStudentSchema.LASTNAME, "สกุล");
		table.setColumnHeader(RecruitStudentSchema.SCORE, "คะแนนสอบ");
		table.setVisibleColumns(
				RecruitStudentSchema.RECRUIT_CODE, 
				RecruitStudentSchema.CLASS_RANGE,
				RecruitStudentSchema.PRENAME,
				RecruitStudentSchema.FIRSTNAME, 
				RecruitStudentSchema.LASTNAME,
				RecruitStudentSchema.SCORE);
				
		setColumnGenerator(RecruitStudentSchema.CLASS_RANGE,
				RecruitStudentSchema.PRENAME,
				"");
	}
	
	/* ตั้งค่ารูปแบบข้อมูลของค่า Fix */
	private void setColumnGenerator(Object... propertyIds){
		for(final Object propertyId:propertyIds){
			table.addGeneratedColumn(propertyId, new ColumnGenerator() {
				private static final long serialVersionUID = 1L;

				@Override
				public Object generateCell(CustomTable source, Object itemId, Object columnId) {
					Item item = source.getItem(itemId);
					Object value = null;
					
					if(RecruitStudentSchema.CLASS_RANGE.equals(propertyId))
						value = ClassRange.getNameTh(Integer.parseInt(item.getItemProperty(propertyId).getValue().toString()));
					else if(RecruitStudentSchema.PRENAME.equals(propertyId))
						value = Prename.getNameTh(Integer.parseInt(item.getItemProperty(propertyId).getValue().toString()));
										
					return value;
				}
			});
		}
	}
	
	/*นำจำนวนที่นับ มาใส่ค่าในส่วนท้ายตาราง*/
	private void setFooterData(){
		table.setColumnFooter(RecruitStudentSchema.RECRUIT_CODE, "ทั้งหมด: "+ table.size() + " คน");
	}
	
	/* อัพเดทข้อมูลในตาราง */
	private void updateTable(){
		setFooterData();
		
		/* รีเซ็ตค่าในฟอร์ม */
		item = null;
		setNormalMode();
		initFieldGroup();
	}
	
	/* จัดกลุ่มของ ฟอร์มในการแก้ไข - เพิ่ม ข้อมูล */
	private void initFieldGroup(){		
		scoreBinder = new FieldGroup(item);
		scoreBinder.setBuffered(true);
		scoreBinder.bind(firstname, RecruitStudentSchema.FIRSTNAME);
		scoreBinder.bind(lastname, RecruitStudentSchema.LASTNAME);
		scoreBinder.bind(score, RecruitStudentSchema.SCORE);
	}	
	
	/* ตั้งค่าโหมดของปกติ คือ ปิดการแก้ไขบนฟอร์ม */
	private void setNormalMode(){
		firstname.setEnabled(false);
		lastname.setEnabled(false);
		score.setEnabled(false);
		save.setEnabled(false);
	}

	/* ตั้งค่าโหมดของแก้ไข คือ เปิดการแก้ไขบนฟอร์ม */
	private void setEditMode(){
		firstname.setEnabled(false);
		lastname.setEnabled(false);
		score.setEnabled(true);
		save.setEnabled(true);
	}
	
	
}
