package com.ies.schoolos.component.recruit;

import java.util.Collection;

import org.tepi.filtertable.numberfilter.NumberInterval;
import org.vaadin.haijian.ExcelExporter;

import com.ies.schoolos.component.ui.ContentPage;
import com.ies.schoolos.component.ui.TwinSelectTable;
import com.ies.schoolos.container.Container;
import com.ies.schoolos.filter.TableFilterDecorator;
import com.ies.schoolos.filter.TableFilterGenerator;
import com.ies.schoolos.report.excel.RecruitStudentToExcel;
import com.ies.schoolos.schema.SchoolSchema;
import com.ies.schoolos.schema.SessionSchema;
import com.ies.schoolos.schema.recruit.RecruitStudentSchema;
import com.ies.schoolos.type.ClassRange;
import com.ies.schoolos.type.Prename;
import com.vaadin.data.Item;
import com.vaadin.data.util.filter.And;
import com.vaadin.data.util.filter.Compare.Equal;
import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.CustomTable;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.UI;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CustomTable.ColumnGenerator;

public class RecruitStudentConfirmView extends ContentPage {
	private static final long serialVersionUID = 1L;

	private StringBuilder sqlBuilder = new StringBuilder();
	
	private SQLContainer leftContainer;
	private SQLContainer rightContainer = Container.getInstance().getRecruitStudentContainer();
	
	private HorizontalLayout toolbar;
	private TwinSelectTable twinSelect; 
	
	public RecruitStudentConfirmView() {
		super("มอบตัวนักเรียน");
		
		/* ดึงค่าตารางทางซ้าย */
		sqlBuilder.append(" SELECT * FROM " + RecruitStudentSchema.TABLE_NAME);
		sqlBuilder.append(" WHERE " + RecruitStudentSchema.SCHOOL_ID + "=" + UI.getCurrent().getSession().getAttribute(SessionSchema.SCHOOL_ID));
		sqlBuilder.append(" AND " + RecruitStudentSchema.IS_CONFIRM + " = " + false );
		leftContainer = new Container().getFreeFormContainer(sqlBuilder.toString(), RecruitStudentSchema.STUDENT_ID);
		
		/* ค้นหาตารางขวา */
		rightContainer.removeAllContainerFilters();
		rightContainer.addContainerFilter(new Equal(SchoolSchema.SCHOOL_ID, UI.getCurrent().getSession().getAttribute(SessionSchema.SCHOOL_ID)));
		
		buildMainLayout();
	}
	
	private void buildMainLayout(){
		/* Toolbar */
		/*toolbar = new HorizontalLayout();
		toolbar.setSpacing(true);
		addComponent(toolbar);
		
		ExcelExporter excelExporter = new ExcelExporter(new RecruitStudentToExcel());
		excelExporter.setIcon(FontAwesome.FILE_EXCEL_O);
		excelExporter.setCaption("ส่งออกไฟล์ Excel");
		toolbar.addComponent(excelExporter);*/
		
		/* ตารางรายการนักเรียน */
		twinSelect = new TwinSelectTable();
		twinSelect.setSizeFull();
		twinSelect.setSpacing(true);
		twinSelect.setSelectable(true);
		twinSelect.setMultiSelect(true);
		twinSelect.showFooterCount(true);
		twinSelect.setFooterUnit("คน");

		twinSelect.setFilterDecorator(new TableFilterDecorator());
		twinSelect.setFilterGenerator(new TableFilterGenerator());
		twinSelect.setFilterBarVisible(true);
		
		twinSelect.getLeftTable().setContainerDataSource(leftContainer);
		twinSelect.getRightTable().setContainerDataSource(rightContainer);
		setLeftData();
		setRightData();
		initTableStyle();
		
		twinSelect.setAddClick(addListener);
		twinSelect.setAddAllClick(addAllListener);
		twinSelect.setRemoveClick(removeListener);
		twinSelect.setRemoveAllClick(removeAllListener);
		
		addComponent(twinSelect);
		setExpandRatio(twinSelect, 1);
	}
	
	/* ตั้งค่ารูปแบบแสดงของตาราง */
	private void initTableStyle(){
		twinSelect.setColumnHeader(RecruitStudentSchema.RECRUIT_CODE, "หมายเลขสมัคร");
		twinSelect.setColumnHeader(RecruitStudentSchema.CLASS_RANGE,"ช่วงชั้น");
		twinSelect.setColumnHeader(RecruitStudentSchema.PRENAME, "ชื่อต้น");
		twinSelect.setColumnHeader(RecruitStudentSchema.FIRSTNAME, "ชื่อ");
		twinSelect.setColumnHeader(RecruitStudentSchema.LASTNAME, "สกุล");
		
		twinSelect.setVisibleColumns(
				RecruitStudentSchema.RECRUIT_CODE, 
				RecruitStudentSchema.CLASS_RANGE,
				RecruitStudentSchema.PRENAME,
				RecruitStudentSchema.FIRSTNAME, 
				RecruitStudentSchema.LASTNAME);
		
		setColumnGenerator(RecruitStudentSchema.CLASS_RANGE, RecruitStudentSchema.PRENAME, "");
	}
	
	/* ตั้งค่ารูปแบบข้อมูลของค่า Fix */
	private void setColumnGenerator(Object... propertyIds){
		for(final Object propertyId:propertyIds){
			twinSelect.addGeneratedColumn(propertyId, new ColumnGenerator() {
				private static final long serialVersionUID = 1L;

				@Override
				public Object generateCell(CustomTable source, Object itemId, Object columnId) {
					Item item = source.getItem(itemId);
					Object value = new Object();
					
					if(RecruitStudentSchema.CLASS_RANGE.equals(propertyId))
						value = ClassRange.getNameTh(Integer.parseInt(item.getItemProperty(RecruitStudentSchema.CLASS_RANGE).getValue().toString()));
					else if(RecruitStudentSchema.PRENAME.equals(propertyId))
						value = Prename.getNameTh(Integer.parseInt(item.getItemProperty(RecruitStudentSchema.PRENAME).getValue().toString()));
					return value;
				}
			});
		}
	}
	
	/* จำนวนนักเรียนทีี่ค้นฟา */
	private void setLeftData(){
		twinSelect.setLeftCountFooter(RecruitStudentSchema.RECRUIT_CODE);
	}
	
	/* จำนวนนักเรียนที่ถูกเลือก */
	private void setRightData(){
		twinSelect.getRightTable().setFilterFieldValue(RecruitStudentSchema.IS_CONFIRM, true);
		twinSelect.setRightCountFooter(RecruitStudentSchema.RECRUIT_CODE);
	}
	
	/* ย้ายข้างจากซ้ายไปขวาจากที่ถูกเลือก */
	@SuppressWarnings("unchecked")
	private void selectData(Object... itemIds){
		for(Object itemId: itemIds){
			try {
				/* ก่อนแก้ไข เพิ่มต้องลบ Filter ก่อนหน้า */
				rightContainer.removeAllContainerFilters();
				
				Item studentItem = rightContainer.getItem(itemId);
				studentItem.getItemProperty(RecruitStudentSchema.IS_CONFIRM).setValue(true);
				rightContainer.commit();
				
				/* Refresh ข้อมูลาราง */
				rightContainer.addContainerFilter(new And(new Equal(RecruitStudentSchema.IS_CONFIRM, true),
						new Equal(SchoolSchema.SCHOOL_ID, UI.getCurrent().getSession().getAttribute(SessionSchema.SCHOOL_ID))));
				leftContainer.refresh();
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
		twinSelect.setLeftCountFooter(RecruitStudentSchema.RECRUIT_CODE);
		twinSelect.setRightCountFooter(RecruitStudentSchema.RECRUIT_CODE);
	}
	
	/* ย้ายข้างจากซ้ายไปขวาจากทั้งหมด*/
	@SuppressWarnings("unchecked")
	private void selectAllData(){
		Collection<?> itemIds = twinSelect.getLeftTable().getItemIds();
		for(Object itemId: itemIds){
			try {
				/* ก่อนแก้ไข เพิ่มต้องลบ Filter ก่อนหน้า */
				rightContainer.removeAllContainerFilters();
				
				Item studentItem = rightContainer.getItem(itemId);
				studentItem.getItemProperty(RecruitStudentSchema.IS_CONFIRM).setValue(true);
				rightContainer.commit();
				
				/* Refresh ข้อมูลาราง */
				rightContainer.addContainerFilter(new And(new Equal(RecruitStudentSchema.IS_CONFIRM, true),
						new Equal(SchoolSchema.SCHOOL_ID, UI.getCurrent().getSession().getAttribute(SessionSchema.SCHOOL_ID))));
				leftContainer.refresh();
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
		twinSelect.getLeftTable().removeAllItems();
		twinSelect.setLeftCountFooter(RecruitStudentSchema.RECRUIT_CODE);
		twinSelect.setRightCountFooter(RecruitStudentSchema.RECRUIT_CODE);
	}

	/* ย้ายข้างจากขวาไปซ้ายจากที่เลือก */
	@SuppressWarnings("unchecked")
	private void removeData(Object... itemIds){
		for(Object itemId: itemIds){
			try {
				/* ก่อนแก้ไข เพิ่มต้องลบ Filter ก่อนหน้า */
				rightContainer.removeAllContainerFilters();
				
				Item studentItem = rightContainer.getItem(itemId);
				studentItem.getItemProperty(RecruitStudentSchema.IS_CONFIRM).setValue(null);
				rightContainer.commit();
				
				/* Refresh ข้อมูลาราง */
				rightContainer.addContainerFilter(new And(new Equal(RecruitStudentSchema.IS_CONFIRM, true),
						new Equal(SchoolSchema.SCHOOL_ID, UI.getCurrent().getSession().getAttribute(SessionSchema.SCHOOL_ID))));
				leftContainer.refresh();
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
		twinSelect.setLeftCountFooter(RecruitStudentSchema.RECRUIT_CODE);
		twinSelect.setRightCountFooter(RecruitStudentSchema.RECRUIT_CODE);
	}
	
	/* ย้ายข้างจากขวาไปซ้ายจากจำนวนทั้งหมด */
	@SuppressWarnings("unchecked")
	private void removeAllData(){
		for(Object itemId: twinSelect.getRightTable().getItemIds()){
			try {
				/* ก่อนแก้ไข เพิ่มต้องลบ Filter ก่อนหน้า */
				rightContainer.removeAllContainerFilters();
				
				Item studentItem = rightContainer.getItem(itemId);
				studentItem.getItemProperty(RecruitStudentSchema.IS_CONFIRM).setValue(null);
				rightContainer.commit();
				
				/* Refresh ข้อมูลาราง */
				rightContainer.addContainerFilter(new And(new Equal(RecruitStudentSchema.IS_CONFIRM, true),
						new Equal(SchoolSchema.SCHOOL_ID, UI.getCurrent().getSession().getAttribute(SessionSchema.SCHOOL_ID))));
				leftContainer.refresh();
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
		twinSelect.getRightTable().removeAllItems();
		twinSelect.setLeftCountFooter(RecruitStudentSchema.RECRUIT_CODE);
		twinSelect.setRightCountFooter(RecruitStudentSchema.RECRUIT_CODE);
	}

	/* ปุ่มเลือกนักเรียนจากซ้ายไปขวา จากนักเรียนที่เลือก */
	private ClickListener addListener = new ClickListener() {
		private static final long serialVersionUID = 1L;
		@Override
		public void buttonClick(ClickEvent event) {
			Collection<?> itemIds = (Collection<?>)twinSelect.getLeftTable().getValue();
						
			for(Object itemId:itemIds){
				selectData(itemId);
			}
		}
	};
	
	/* ปุ่มเลือกนักเรียนจากซ้ายไปขวา จากนักเรียนทั้งหมด */
	private ClickListener addAllListener = new ClickListener() {
		private static final long serialVersionUID = 1L;
		@Override
		public void buttonClick(ClickEvent event) {
			selectAllData();
		}
	};
	
	/* ปุ่มเลือกนักเรียนจากขวาไปซ้าย จากนักเรียนที่เลือก */
	private ClickListener removeListener = new ClickListener() {
		private static final long serialVersionUID = 1L;
		@Override
		public void buttonClick(ClickEvent event) {
			for(Object itemId:(Collection<?>)twinSelect.getRightTable().getValue()){
				removeData(itemId);
			}
		}
	};
	
	/* ปุ่มเลือกนักเรียนจากขวาไปซ้าย จากนักเรียนทั้งหมด */
	private ClickListener removeAllListener = new ClickListener() {
		private static final long serialVersionUID = 1L;
		@Override
		public void buttonClick(ClickEvent event) {
			removeAllData();
		}
	};
}
