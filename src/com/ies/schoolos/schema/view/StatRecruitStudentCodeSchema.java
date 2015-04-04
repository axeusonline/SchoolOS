package com.ies.schoolos.schema.view;

import com.ies.schoolos.schema.SessionSchema;
import com.vaadin.ui.UI;

public class StatRecruitStudentCodeSchema implements java.io.Serializable{
	private static final long serialVersionUID = 1L;

	public static final String TABLE_NAME = "stat_recruit_student_code";
	
	public static final String MAX_CODE = "max_code";
	public static final String MIN_CODE = "min_code";
	public static final String SCHOOL_ID = "school_id";
	
	private static StatRecruitStudentCodeSchema instance = null;  

    public static StatRecruitStudentCodeSchema getInstance() {  
        if (instance == null) {  
            instance = new StatRecruitStudentCodeSchema();  
        }  
        return instance;  
    }
    
    /* ดึงค่า หมายเลขสมัคร ที่มากที่สุด น้อยที่สุดของห้องเรียน
     * จากโรงเรียน
     * eg. SELECT * FROM stat_class_room WHERE class_year = ? AND school_id = ?*/
    public static String getQuery(){
    	String query = "SELECT * FROM " + TABLE_NAME + 
    			" WHERE " + SCHOOL_ID + "=" + UI.getCurrent().getSession().getAttribute(SessionSchema.SCHOOL_ID);
    	return query;
    }
    
    
}
