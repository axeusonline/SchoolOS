package com.ies.schoolos;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;

import com.ies.schoolos.component.SchoolOSView;
import com.ies.schoolos.container.Container;
import com.ies.schoolos.schema.SchoolSchema;
import com.ies.schoolos.schema.SessionSchema;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.data.Item;
import com.vaadin.data.util.filter.And;
import com.vaadin.data.util.filter.Compare.Equal;
import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.UI;

@SuppressWarnings("serial")
@Theme("schoolos")
public class SchoolOSUI extends UI {

	private SQLContainer schoolContainer = Container.getInstance().getSchoolContainer();
	
	@WebServlet(value = "/*", asyncSupported = true)
	@VaadinServletConfiguration(productionMode = false, ui = SchoolOSUI.class, widgetset = "com.ies.schoolos.widgetset.SchoolosWidgetset")
	public static class Servlet extends VaadinServlet {
	}

	@Override
	protected void init(VaadinRequest request) {
		getUrlParameter();
		autoLogin();
		
		/*final AppletIntegration applet = new AppletIntegration() {

            private static final long serialVersionUID = 1L;

            @Override
            public void attach() {
            	super.attach();
            	
                setAppletArchives(Arrays.asList(new String[] { "smartcard-applet.jar", 
                		"commons-lang-2.4.jar",
                        "commons-logging-1.0.2.jar", 
                        "jna.jar", 
                        "SmartCard.jar", 
                        "SmartcardDataAccess.jar",
                		"log4j.jar",
                		"log4j-core.jar"}));
            	
                setAppletArchives(Arrays.asList(new String[] { "SmartCardApplet.jar",
                		"log4j.jar",
                		"commons-lang-2.4.jar",}));
               
                //setCodebase("https://storage.googleapis.com/schoolos/applets/");

            	setAppletArchives(Arrays.asList(new String[] { "smartcard-applet.jar", 
                		"commons-lang-2.4.jar",
                        "commons-logging-1.0.2.jar", 
                        "jna.jar", 
                        "SmartCard.jar", 
                        "SmartcardDataAccess.jar"}));
            	
                setCodebase("http://java.sun.com/update/1.6.0/jinstall-6u30-windows-i586.cab#Version=1,6,0,0");
                setAppletClass("SmartCard.class");
                //setAppletClass("com/iMed/iMedApp/iMedOPDApp/Smartcard/SmartCard");
            	
            	
                setWidth("800px");
                setHeight("500px");
                setId("smartcard");
                
                //System.err.println(getCodebase());
                JavaScript.getCurrent().addFunction("connectSmartcard",  new JavaScriptFunction() {
        			@Override
        			public void call(JsonArray arguments) {
        				
        				Notification.show("asdfdsf", Type.WARNING_MESSAGE);
        			}
        		});
        		
        		Link link = new Link("Get Smartcard", new ExternalResource("connectSmartcard()"));
                setContent(link);
            }
        };
   
        setContent(applet);*/

	}
	
	/*ค้นหาหน้าของโรงเรียนด้วย url เพื่อใช้ในการสมัครเรียนโดยไม่ต้อง Login */
	private void getUrlParameter(){				
		String path = Page.getCurrent().getLocation().getPath();
		path = path.substring(path.lastIndexOf("/")+1);
		if(!path.equals("")){
			schoolContainer.addContainerFilter(new Equal(SchoolSchema.SHORT_URL,path));
			if(schoolContainer.size() > 0){
				for(Object itemId: schoolContainer.getItemIds()){
					Item item = schoolContainer.getItem(itemId);
					setSession(item);
				}
			}
			//ลบ WHERE ออกจาก Query เพื่อป้องกันการค้างของคำสั่่งจากการทำงานอื่นที่เรียกตัวแปรไปใช้
			schoolContainer.removeAllContainerFilters();
		}
	}
	
	/*Login อัตโนมัติจาก Cookie */
	private void autoLogin(){	
		Cookie email = getCookieByName(SessionSchema.EMAIL);
		Cookie password = getCookieByName(SessionSchema.PASSWORD);
		
		if(email == null && password == null){
			setContent(new LoginView());
		}else{
			schoolContainer.addContainerFilter(new And(
					new Equal(SchoolSchema.EMAIL,email.getValue()),
					new Equal(SchoolSchema.PASSWORD,password.getValue())));
			
			if(schoolContainer.size() != 0){
				for(Object itemId: schoolContainer.getItemIds()){
					Item item = schoolContainer.getItem(itemId);
					setSession(item);
				}
				setContent(new SchoolOSView());
			}else{
				setContent(new LoginView());
			}
			schoolContainer.removeAllContainerFilters();
		}
	}
	
	private Cookie getCookieByName(String name){
		Cookie cookie = null;
		for(Cookie object:VaadinService.getCurrentRequest().getCookies()){
			 if(object.getName().equals(name)) {
				 cookie = object;  
		    }
		}
		return cookie;
	}
	
	private void setSession(Item item){
		getSession().setAttribute(SessionSchema.IS_ROOT, true);
		getSession().setAttribute(SessionSchema.SCHOOL_ID, item.getItemProperty(SchoolSchema.SCHOOL_ID).getValue());
		getSession().setAttribute(SessionSchema.USER_ID, item.getItemProperty(SchoolSchema.SCHOOL_ID).getValue());
		getSession().setAttribute(SessionSchema.SCHOOL_NAME, item.getItemProperty(SchoolSchema.NAME).getValue());
		getSession().setAttribute(SessionSchema.FIRSTNAME, item.getItemProperty(SchoolSchema.FIRSTNAME).getValue());
		getSession().setAttribute(SessionSchema.EMAIL, item.getItemProperty(SchoolSchema.EMAIL).getValue());
	}
}