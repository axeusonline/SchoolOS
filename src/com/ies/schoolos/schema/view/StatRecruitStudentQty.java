package com.ies.schoolos.schema.view;

public class StatRecruitStudentQty implements java.io.Serializable{
	private static final long serialVersionUID = 1L;

	public static final String TABLE_NAME = "stat_recruit_student_qty";
	
	public static final String GENDER = "gender";
	public static final String CLASS_RANGE = "class_range";
	public static final String BLOOD = "blood";
	public static final String CURRENT_PROVINCE_ID = "current_province_id";
	public static final String STUDENT_QTY = "student_qty";
	
	private static StatRecruitStudentQty instance = null;  

    public static StatRecruitStudentQty getInstance() {  
        if (instance == null) {  
            instance = new StatRecruitStudentQty();  
        }  
        return instance;  
    }
}
