package com.ies.schoolos.schema;

import com.vaadin.ui.UI;

public class SessionSchema implements java.io.Serializable{
	private static final long serialVersionUID = 1L;
	
	public static final String SCHOOL_ID = "schoolos_school_id";
	public static final String SCHOOL_NAME = "schoolos_school_name";
	public static final String USER_ID = "schoolos_user_id";
	public static final String EMAIL = "schoolos_email";
	public static final String PASSWORD = "schoolos_password";
	public static final String FIRSTNAME = "schoolos_firstname";
	
	public static void setSession(Object schoolId, Object userId,
			Object schoolName, Object firstname, Object email){
		
		UI.getCurrent().getSession().setAttribute(SCHOOL_ID, schoolId);
		UI.getCurrent().getSession().setAttribute(USER_ID, userId);
		UI.getCurrent().getSession().setAttribute(SCHOOL_NAME, schoolName);
		UI.getCurrent().getSession().setAttribute(FIRSTNAME, firstname);
		UI.getCurrent().getSession().setAttribute(EMAIL, email);
	}
	
	public static void setSchoolId(Object schoolId){
		UI.getCurrent().getSession().setAttribute(SCHOOL_ID, schoolId);
	}
	
	public static Object getSchoolID(){
		return UI.getCurrent().getSession().getAttribute(SCHOOL_ID);
	}
	
	public static Object getUserID(){
		return UI.getCurrent().getSession().getAttribute(USER_ID);
	}
	
	public static Object getSchoolName(){
		return UI.getCurrent().getSession().getAttribute(SCHOOL_NAME);
	}
	
	public static Object getFirstname(){
		return UI.getCurrent().getSession().getAttribute(FIRSTNAME);
	}
	
	public static void setEmail(Object email){
		UI.getCurrent().getSession().setAttribute(EMAIL, email);
	}
	
	public static Object getEmail(){
		return UI.getCurrent().getSession().getAttribute(EMAIL);
	}
}
