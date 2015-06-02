package com.ies.schoolos;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;

import org.vaadin.googleanalytics.tracking.GoogleAnalyticsTracker;

import com.ies.schoolos.component.SchoolOSView;
import com.ies.schoolos.component.ui.SchoolOSLayout;
import com.ies.schoolos.schema.SchoolSchema;
import com.ies.schoolos.schema.SessionSchema;
import com.ies.schoolos.schema.UserSchema;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.data.Item;
import com.vaadin.data.util.filter.And;
import com.vaadin.data.util.filter.Compare.Equal;
import com.vaadin.data.util.sqlcontainer.RowId;
import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.UI;

@SuppressWarnings("serial")
@Theme("schoolos")
public class SchoolOSUI extends UI {

	private SQLContainer schoolContainer = SchoolOSLayout.container.getSchoolContainer();
	private SQLContainer userContainer = SchoolOSLayout.container.getUserContainer();
	
	@WebServlet(value = "/*", asyncSupported = true)
	@VaadinServletConfiguration(productionMode = false, ui = SchoolOSUI.class, widgetset = "com.ies.schoolos.widgetset.SchoolosWidgetset")
	public static class Servlet extends VaadinServlet {
	}

	@Override
	protected void init(VaadinRequest request) {
		GoogleAnalyticsTracker tracker = new GoogleAnalyticsTracker("UA-63545885-1","schoolosplus.com");
		tracker.extend(this);
		tracker.trackPageview("/samplecode/googleanalytics");
		
		getUrlParameter();
		autoLogin();
	}
	
	/*ค้นหาหน้าของโรงเรียนด้วย url เพื่อใช้ในการสมัครเรียนโดยไม่ต้อง Login */
	private void getUrlParameter(){				
		String path = Page.getCurrent().getLocation().getPath();
		path = path.substring(path.lastIndexOf("/")+1);
		if(!path.equals("")){
			schoolContainer.addContainerFilter(new Equal(SchoolSchema.SHORT_URL,path));
			if(schoolContainer.size() > 0){
				Item item = schoolContainer.getItem(schoolContainer.getIdByIndex(0));
				SessionSchema.setSchoolId(Integer.parseInt(item.getItemProperty(SchoolSchema.SCHOOL_ID).getValue().toString()));
				SessionSchema.setEmail(item.getItemProperty(SchoolSchema.CONTACT_EMAIL).getValue().toString());
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
			if(SessionSchema.getUserID() != null){
				setContent(new SchoolOSView());				
			}else{
				setContent(new LoginView());
			}
		}else{
			userContainer.addContainerFilter(new And(
					new Equal(UserSchema.EMAIL,email.getValue()),
					new Equal(UserSchema.PASSWORD,password.getValue())));

			if(userContainer.size() != 0){
				Item item = userContainer.getItem(userContainer.getIdByIndex(0));
				Item schoolItem = schoolContainer.getItem(new RowId(item.getItemProperty(UserSchema.SCHOOL_ID).getValue()));
				SessionSchema.setSession(
						Integer.parseInt(item.getItemProperty(UserSchema.SCHOOL_ID).getValue().toString()),
						Integer.parseInt(item.getItemProperty(UserSchema.USER_ID).getValue().toString()),
						schoolItem.getItemProperty(SchoolSchema.NAME).getValue(),
						item.getItemProperty(UserSchema.FIRSTNAME).getValue(),
						schoolItem.getItemProperty(SchoolSchema.CONTACT_EMAIL).getValue());
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
}