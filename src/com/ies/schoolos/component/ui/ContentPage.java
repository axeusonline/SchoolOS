package com.ies.schoolos.component.ui;

import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

public class ContentPage extends VerticalLayout {
	private static final long serialVersionUID = 1L;

	private String title;
	
	private Label titleLbl;
	
	public ContentPage(String title) {
		this.title = title;
		buildMainLayout();
	}
	
	private void buildMainLayout(){
		setSizeFull();
		setSpacing(true);
		setMargin(true);
		
		titleLbl = new Label(title);
		titleLbl.setWidth("190px");
		titleLbl.setStyleName("content-title");
		addComponent(titleLbl);
	}
}
