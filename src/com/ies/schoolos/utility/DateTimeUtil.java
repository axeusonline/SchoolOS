package com.ies.schoolos.utility;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateTimeUtil {
	
	public static String getDDMMYYYY(Date date){
		String dateStr = new SimpleDateFormat("dd/MM/yyyy").format(date);
    	String year = dateStr.substring(6);
    	if(Integer.parseInt(year) > 2500)
    		dateStr = dateStr.replace(year, Integer.parseInt(year)-543+"");
		return dateStr;
	}
	
	public static String getDDMMYYYYBD(Date date){
		String dateStr = new SimpleDateFormat("dd/MM/yyyy").format(date);
    	String year = dateStr.substring(6);
    	if(Integer.parseInt(year) < 2500)
    		dateStr = dateStr.replace(year, Integer.parseInt(year)+543+"");
		return dateStr;
	}
	
	/* กำหนดวัน เป็น dd/MM/YYYY จาก YYYY-MM-dd */
	public static String getDDMMYYYYBD(String date){
		String[] dates = date.split("-");
    	String dateStr = dates[2] + "/" + dates[1] + "/";
    	if(Integer.parseInt(dates[0]) < 2500)
    		dateStr += (Integer.parseInt(dates[0])+543) + "";
    	else
    		dateStr = dates[0];
		return dateStr;
	}
	
	/* แสดงวันแรกของปี 1 มค ปี */
	@SuppressWarnings("deprecation")
	public static Date getFirstDateOfYear(){
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, new Date().getYear()+1900);
		cal.set(Calendar.DAY_OF_YEAR, 1);    
		return cal.getTime();
	}
	
	/* แสดงวันสุดท้ายของปี  31 ธค ปี */
	@SuppressWarnings("deprecation")
	public static Date getLastDateOfYear(){
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, new Date().getYear()+1900);
		cal.set(Calendar.DAY_OF_YEAR, 11);    
		cal.set(Calendar.DAY_OF_MONTH, 31);
		return cal.getTime();
	}
	
	@SuppressWarnings("deprecation")
	public static String getChristianYear(){
		return Integer.toString(1900+new Date().getYear());
	}
	
	@SuppressWarnings("deprecation")
	public static String getBuddishYear(){
		return Integer.toString(2443+new Date().getYear());
	}
	
	public static boolean compareBetweenDate(Date beginDate, Date endDate){
		boolean isBetween = false;
		Date todayDate = new Date();
		if((todayDate.equals(beginDate) || todayDate.equals(endDate)) ||
				(todayDate.after(beginDate) && todayDate.before(endDate))) {
			isBetween = true;
		}
		return isBetween;
	}
}
