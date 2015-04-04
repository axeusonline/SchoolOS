package com.ies.schoolos.component.ui;

import org.tepi.filtertable.FilterTable;

import com.ies.schoolos.filter.TableFilterDecorator;
import com.ies.schoolos.filter.TableFilterGenerator;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;

public class TwinSelectTable extends HorizontalLayout {
	private static final long serialVersionUID = 1L;

	private String unit = "";
	
	private FilterTable leftTable;
	private Button add;
	private Button addAll;
	private Button remove;
	private Button removeAll;
	private FilterTable rightTable;
	
	public TwinSelectTable() {
		buildMainLayou();
	}
	
	private void buildMainLayou(){
		
		leftTable = new FilterTable();
		leftTable.setSizeFull();
				
		addComponent(leftTable);
		setExpandRatio(leftTable, 2);
		
		VerticalLayout buttonLayout = new VerticalLayout();
		buttonLayout.setWidth("-1px");
		buttonLayout.setSpacing(true);
		addComponent(buttonLayout);
		
		add = new Button(FontAwesome.ANGLE_RIGHT);
		add.setWidth("40px");
		buttonLayout.addComponent(add);
		buttonLayout.setComponentAlignment(add,Alignment.MIDDLE_CENTER);
		
		addAll = new Button(FontAwesome.ANGLE_DOUBLE_RIGHT);
		addAll.setWidth("40px");
		buttonLayout.addComponent(addAll);
		buttonLayout.setComponentAlignment(addAll,Alignment.MIDDLE_CENTER);
		
		remove = new Button(FontAwesome.ANGLE_LEFT);
		remove.setWidth("40px");
		buttonLayout.addComponent(remove);
		buttonLayout.setComponentAlignment(remove,Alignment.MIDDLE_CENTER);
		
		removeAll = new Button(FontAwesome.ANGLE_DOUBLE_LEFT);
		removeAll.setWidth("40px");
		buttonLayout.addComponent(removeAll);
		buttonLayout.setComponentAlignment(removeAll,Alignment.MIDDLE_CENTER);
		
		rightTable = new FilterTable();
		rightTable.setSizeFull();
		
		addComponent(rightTable);
		setExpandRatio(rightTable, 2);
	}
	
	public void setLeftCaption(String caption){
		leftTable.setCaption(caption);
	}
	
	public void setRightCaption(String caption){
		rightTable.setCaption(caption);
	}
	
	public void setFooterUnit(String unit){
		this.unit = unit;
	}
	
	public void setMultiSelect(boolean isMultiSelect){
		leftTable.setMultiSelect(isMultiSelect);
		rightTable.setMultiSelect(isMultiSelect);
	}

	public void setSelectable(boolean isSelectable){
		leftTable.setSelectable(isSelectable);
		rightTable.setSelectable(isSelectable);
	}
	
	public void setLeftSelectable(boolean isSelectable){
		leftTable.setSelectable(isSelectable);
	}
	
	public void setRightSelectable(boolean isSelectable){
		rightTable.setSelectable(isSelectable);
	}
	
	public void setLeftCountFooter(Object propertyId){
		leftTable.setColumnFooter(propertyId, "ทั้งหมด: "+ leftTable.size() + " " + unit);
	}
	
	public void setRightCountFooter(Object propertyId){
		rightTable.setColumnFooter(propertyId, "ทั้งหมด: "+ rightTable.size() + " " + unit);
	}
	
	public void setAddClick(ClickListener click){
		add.addClickListener(click);
	}
	
	public void setAddAllClick(ClickListener click){
		addAll.addClickListener(click);
	}
	
	public void setRemoveClick(ClickListener click){
		remove.addClickListener(click);
	}
	
	public void setRemoveAllClick(ClickListener click){
		removeAll.addClickListener(click);
	}

	public FilterTable getLeftTable(){
		return leftTable;
	}
	
	public FilterTable getRightTable(){
		return rightTable;
	}
	
	public void showFooterCount(boolean isVisible){
		leftTable.setFooterVisible(isVisible);
		rightTable.setFooterVisible(isVisible);
	}
		
	public void removeAllLeftItem(){
		leftTable.removeAllItems();
	}
	
	public void removeAllRightItem(){
		rightTable.removeAllItems();
	}
	
	public void addLeftItem(Object itemId, String caption){
		leftTable.addItem(new Object[] {caption},itemId);
	}
	
	public void addRightItem(Object itemId, String caption){
		rightTable.addItem(new Object[] {caption},itemId);
	}
	
	public void addContainerProperty(Object propertyId, Class<?> clazz, Object defaultValue){
		leftTable.addContainerProperty(propertyId, clazz, defaultValue);
		rightTable.addContainerProperty(propertyId, clazz, defaultValue);
	}
	
	public void setFilterDecorator(TableFilterDecorator decorator){
		leftTable.setFilterDecorator(decorator);
		rightTable.setFilterDecorator(decorator);
	}
	
	public void setFilterGenerator(TableFilterGenerator generater){
		leftTable.setFilterGenerator(generater);
		rightTable.setFilterGenerator(generater);
	}
	
	public void setFilterBarVisible(boolean visible){
		leftTable.setFilterBarVisible(visible);
		rightTable.setFilterBarVisible(visible);
	}
	
	public void setColumnHeader(String propertyId, String title){
		leftTable.setColumnHeader(propertyId, title);
		rightTable.setColumnHeader(propertyId, title);
	}
	
	public void setVisibleColumns(Object... columns){
		leftTable.setVisibleColumns(columns);
		rightTable.setVisibleColumns(columns);
	}
}