package com.ies.schoolos.schema.view;

import com.ies.schoolos.schema.SessionSchema;

public class StatClassRoomSchema implements java.io.Serializable{
	private static final long serialVersionUID = 1L;

	public static final String TABLE_NAME = "stat_class_room";
	
	public static final String MAX_CODE = "max_number";
	public static final String MIN_CODE = "min_number";
	public static final String CLASS_YEAR = "class_year";
	public static final String SCHOOL_ID = "school_id";
	
	private static StatClassRoomSchema instance = null;  

    public static StatClassRoomSchema getInstance() {  
        if (instance == null) {  
            instance = new StatClassRoomSchema();  
        }  
        return instance;  
    }
    
    /* ดึงค่า หมายเลขห้อง ที่มากที่สุด น้อยที่สุดของห้องเรียน
     * จาก ชั้นปี และ โรงเรียน
     * eg. SELECT * FROM stat_class_room WHERE class_year = ? AND school_id = ?*/
    public static String getQuery(int classYear){
    	String query = "SELECT * FROM " + TABLE_NAME 
		+ " WHERE "	+ CLASS_YEAR + "=" + classYear 
		+ " AND " + SCHOOL_ID + "=" + SessionSchema.getSchoolID();
    	return query;
    }
}
