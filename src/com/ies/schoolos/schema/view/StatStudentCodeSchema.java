package com.ies.schoolos.schema.view;

import com.ies.schoolos.schema.SessionSchema;
import com.vaadin.ui.UI;

public class StatStudentCodeSchema implements java.io.Serializable{
	private static final long serialVersionUID = 1L;

	public static final String TABLE_NAME = "stat_student_code";
	
	public static final String MIN_STUDENT_CODE = "min_student_code";
	public static final String MAX_STUDENT_CODE = "max_student_code";
	public static final String RECRUIT_CLASS_YEAR = "recruit_class_year";
	public static final String SCHOOL_ID = "school_id";
	
	private static StatStudentCodeSchema instance = null;  

    public static StatStudentCodeSchema getInstance() {  
        if (instance == null) {  
            instance = new StatStudentCodeSchema();  
        }  
        return instance;  
    }
    
    /* ดึงค่า หมายเลขสมัคร ที่มากที่สุด น้อยที่สุดของรหัสนักเรียน
     * จาก  โรงเรียน และ ชั้นปี
     * eg. SELECT * FROM stat_class_room WHERE class_year = ? AND school_id = ?*/
    public static String getQuery(int classYear){
    	String query = "SELECT * FROM " + TABLE_NAME
    			+ " WHERE " + RECRUIT_CLASS_YEAR + "=" + classYear
    			+ " AND " + SCHOOL_ID + "=" + UI.getCurrent().getSession().getAttribute(SessionSchema.SCHOOL_ID);

    	return query;
    }
    
    /* ดึงค่า หมายเลขสมัคร ที่มากที่สุด น้อยที่สุดของรหัสนักเรียน
     * จาก  โรงเรียน และ ชั้นปี
     * eg. SELECT * FROM stat_class_room WHERE class_year = ? AND school_id = ?*/
    public static String getQuery(){
    	String query = "SELECT MAX(" + MAX_STUDENT_CODE + ") AS " + MAX_STUDENT_CODE + " FROM " + TABLE_NAME
    			+ " WHERE " + SCHOOL_ID + "=" + UI.getCurrent().getSession().getAttribute(SessionSchema.SCHOOL_ID);
    	return query;
    }
    
}
