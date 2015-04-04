package com.ies.schoolos.schema;

public class FamilySchema implements java.io.Serializable{
	private static final long serialVersionUID = 1L;
	
	public static final String TABLE_NAME = "family";
	
	public static final String FAMILY_ID = "family_id";
	public static final String PEOPLE_ID= "people_id";
	public static final String PEOPLE_ID_TYPE= "people_id_type";
	public static final String PRENAME= "prename";
	public static final String FIRSTNAME= "firstname";
	public static final String LASTNAME= "lastname";
	
	//ONLY FOR REPORT
	public static final String PRENAME_ND = "prename_nd";
	
	public static final String FIRSTNAME_ND= "firstname_nd";
	public static final String LASTNAME_ND= "lastname_nd";
	public static final String GENDER= "gender";
	public static final String RELIGION= "religion";
	public static final String RACE= "race";
	public static final String NATIONALITY= "nationality";
	public static final String BIRTH_DATE= "birth_date";
	public static final String TEL= "tel";
	public static final String MOBILE= "mobile";
	public static final String EMAIL= "email";
	public static final String SALARY= "salary";
	public static final String ALIVE_STATUS= "alive_status";
	public static final String OCCUPATION= "occupation";
	public static final String JOB_ADDRESS= "job_address";
	public static final String CURRENT_ADDRESS= "current_address";
	public static final String CURRENT_CITY_ID= "current_city_id";
	public static final String CURRENT_DISTRICT_ID= "current_district_id";
	public static final String CURRENT_PROVINCE_ID= "current_province_id";
	public static final String CURRENT_POSTCODE_ID= "current_postcode_id";
}
