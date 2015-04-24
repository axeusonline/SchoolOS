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
					SessionSchema.setSession(
							true,
							item.getItemProperty(SchoolSchema.SCHOOL_ID).getValue(),
							item.getItemProperty(SchoolSchema.SCHOOL_ID).getValue(),
							item.getItemProperty(SchoolSchema.NAME).getValue(),
							item.getItemProperty(SchoolSchema.FIRSTNAME).getValue(),
							item.getItemProperty(SchoolSchema.EMAIL).getValue());
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
					SessionSchema.setSession(
							true,
							item.getItemProperty(SchoolSchema.SCHOOL_ID).getValue(),
							item.getItemProperty(SchoolSchema.SCHOOL_ID).getValue(),
							item.getItemProperty(SchoolSchema.NAME).getValue(),
							item.getItemProperty(SchoolSchema.FIRSTNAME).getValue(),
							item.getItemProperty(SchoolSchema.EMAIL).getValue());
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
}