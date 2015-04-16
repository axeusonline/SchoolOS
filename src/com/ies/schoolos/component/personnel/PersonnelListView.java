package com.ies.schoolos.component.personnel;

import org.tepi.filtertable.FilterTable;
import org.vaadin.dialogs.ConfirmDialog;

import com.ies.schoolos.container.Container;
import com.ies.schoolos.filter.TableFilterDecorator;
import com.ies.schoolos.filter.TableFilterGenerator;
import com.ies.schoolos.schema.SessionSchema;
import com.ies.schoolos.schema.info.PersonnelSchema;
import com.ies.schoolos.type.Prename;
import com.ies.schoolos.type.dynamic.JobPosition;
import com.ies.schoolos.utility.Notification;
import com.vaadin.data.Item;
import com.vaadin.data.util.filter.And;
import com.vaadin.data.util.filter.Compare.Equal;
import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.data.util.sqlcontainer.TemporaryRowId;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CustomTable;
import com.vaadin.ui.CustomTable.ColumnGenerator;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.UI;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseListener;

public class PersonnelListView extends VerticalLayout {

	private static final long serialVersionUID = 1L;
	
	private SQLContainer pContainer = Container.getInstance().getPersonnelContainer();
	private SQLContainer fContainer = Container.getInstance().getFamilyContainer();
	
	private HorizontalLayout toolbar;
	private Button add;	
	private FilterTable  table;
	
	public PersonnelListView() {	
		pContainer.refresh();
		fContainer.refresh();
		
		pContainer.removeAllContainerFilters();
		pContainer.addContainerFilter(new And(
				new Equal(PersonnelSchema.SCHOOL_ID,UI.getCurrent().getSession().getAttribute(SessionSchema.SCHOOL_ID)),
				new Equal(PersonnelSchema.PERSONEL_STATUS, 0)));
		
		setSizeFull();
		setSpacing(true);
		setMargin(true);
		buildMainLayout();
	}	
	
	private void buildMainLayout(){
		/* Toolbar */
		toolbar = new HorizontalLayout();
		toolbar.setSpacing(true);
		addComponent(toolbar);
				
		add = new Button("เพิ่ม", FontAwesome.USER);
		add.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event) {
				Window addLayout = new Window();
				addLayout.setSizeFull();
				addLayout.setContent(new AddPersonnelView());
				addLayout.addCloseListener(new CloseListener() {
					private static final long serialVersionUID = 1L;

					@Override
					public void windowClose(CloseEvent e) {
						pContainer.addContainerFilter(new And(
								new Equal(PersonnelSchema.SCHOOL_ID,UI.getCurrent().getSession().getAttribute(SessionSchema.SCHOOL_ID)),
								new Equal(PersonnelSchema.PERSONEL_STATUS, 0)));
						pContainer.refresh();
						setFooterData();
					}
				});
				UI.getCurrent().addWindow(addLayout);
			}
		});
		toolbar.addComponent(add);
		
		/*ExcelExporter excelExporter = new ExcelExporter(new PersonnelToExcel());
		excelExporter.setIcon(FontAwesome.FILE_EXCEL_O);
		excelExporter.setCaption("ส่งออกไฟล์ Excel");
		toolbar.addComponent(excelExporter);*/
		
		/* Content */
		table = new FilterTable();
		table.setSizeFull();
		table.setSelectable(true);
		table.setFooterVisible(true);        
		
		table.setFilterDecorator(new TableFilterDecorator());
		table.setFilterGenerator(new TableFilterGenerator());
        table.setFilterBarVisible(true);

		table.setContainerDataSource(pContainer);
	    setFooterData();
		initTableStyle();

		table.setColumnReorderingAllowed(true);
		table.setColumnCollapsingAllowed(true);

		addComponent(table);
        setExpandRatio(table, 1);
	}
	
	/* ตั้งค่ารูปแบบแสดงของตาราง */
	private void initTableStyle(){		
		table.setColumnHeader(PersonnelSchema.PERSONEL_CODE, "หมายเลขประจำตัว");
		table.setColumnHeader(PersonnelSchema.PRENAME, "ชื่อต้น");
		table.setColumnHeader(PersonnelSchema.FIRSTNAME, "ชื่อ");
		table.setColumnHeader(PersonnelSchema.LASTNAME, "สกุล");
		table.setColumnHeader(PersonnelSchema.JOB_POSITION, "ตำแหน่ง");
		
		table.setVisibleColumns(
				PersonnelSchema.PERSONEL_CODE, 
				PersonnelSchema.PRENAME,
				PersonnelSchema.FIRSTNAME, 
				PersonnelSchema.LASTNAME,
				PersonnelSchema.JOB_POSITION);
		
		setColumnGenerator(PersonnelSchema.PRENAME, PersonnelSchema.JOB_POSITION, "");
	}
	
	/* ตั้งค่ารูปแบบข้อมูลของค่า Fix */
	private void setColumnGenerator(Object... propertyIds){
		for(final Object propertyId:propertyIds){
			table.addGeneratedColumn(propertyId, new ColumnGenerator() {
				private static final long serialVersionUID = 1L;

				@Override
				public Object generateCell(CustomTable source, Object itemId, Object columnId) {
					Object value = null;
					Item item = source.getItem(itemId);
					if(item != null && itemId.getClass() != TemporaryRowId.class){
						if(PersonnelSchema.PRENAME.equals(propertyId))
							value = Prename.getNameTh(Integer.parseInt(item.getItemProperty(propertyId).getValue().toString()));
						else if(PersonnelSchema.JOB_POSITION.equals(propertyId))
							value = JobPosition.getNameTh(Integer.parseInt(item.getItemProperty(propertyId).getValue().toString()));
						else if("".equals(propertyId))
							value = initButtonLayout(item, itemId);
					}
					return value;
				}
			});
		}
	}
	
	private HorizontalLayout initButtonLayout(final Item item, final Object itemId){
		final HorizontalLayout buttonLayout = new HorizontalLayout();
			
		Button editButton = new Button(FontAwesome.EDIT);
		editButton.setId(itemId.toString());
		editButton.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 1L;
			@Override
			public void buttonClick(ClickEvent event) {
				
				Window editLayout = new Window();
				editLayout.setSizeFull();
				editLayout.setContent(new EditPersonnelView(item.getItemProperty(PersonnelSchema.PERSONNEL_ID).getValue()));
				UI.getCurrent().addWindow(editLayout);
			}
		});
		buttonLayout.addComponent(editButton);
		buttonLayout.setComponentAlignment(editButton, Alignment.MIDDLE_CENTER);
		
		Button removeButton = new Button(FontAwesome.TRASH_O);
		removeButton.setId(itemId.toString());
		removeButton.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 1L;
			@Override
			public void buttonClick(ClickEvent event) {
				ConfirmDialog.show(UI.getCurrent(), "ลบบุคลากร","การลบข้อมูลจะส่งผลต่อข้อมูลประวัติการทำงาน และการสอนทั้งหมด คุณต้องการลบบุคลากรนี้ใช่หรือไม่?","ตกลง","ยกเลิก",
			        new ConfirmDialog.Listener() {
						private static final long serialVersionUID = 1L;
						public void onClose(ConfirmDialog dialog) {
			                if (dialog.isConfirmed()) {
			                	if(pContainer.removeItem(itemId)){
			                		try {
			                			pContainer.commit();
			                			setFooterData();
									}catch (Exception e1) {
										Notification.show("บันทึกไม่สำเร็จ กรุณาลองอีกครั้ง" , Type.WARNING_MESSAGE);
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
	
	/*นำจำนวนที่นับ มาใส่ค่าในส่วนท้ายตาราง*/
	private void setFooterData(){
		table.setColumnFooter(PersonnelSchema.PERSONEL_CODE, "ทั้งหมด: "+ table.size() + " คน");
	}
}
