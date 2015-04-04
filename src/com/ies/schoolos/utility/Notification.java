package com.ies.schoolos.utility;

import com.vaadin.server.Page;

public class Notification extends com.vaadin.ui.Notification {
	private static final long serialVersionUID = 1L;

	public Notification(String caption, Type type) {
		super(caption, type);
		setDelayMsec(2500);
	}
	
	public static void show(String caption, Type type) {
		new Notification(caption, type).show(Page.getCurrent());
	}
}
