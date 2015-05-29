package com.ies.schoolos.utility;

import java.util.Arrays;

public class Utility {
	
	/* ตรวจสอบว่าเป็น Integer หรือ String ในรูปของ Text 
	 * เช่น "1" เป็น int
	 * "ตัวอย่าง" เป็น int 
	 *    */
	public static boolean isInteger(Object value) {
		boolean parsable = true;
		try {
			Integer.parseInt(value.toString());
		} catch (NumberFormatException e) {
			parsable = false;
		}
		return parsable;
	}

	public static String sortOptionGroup(Object value) {
		String day = value.toString();
		String[] days = day.substring(day.indexOf("[")+1, day.indexOf("]")).split(", ");
		
		Arrays.sort(days);
		day = "[";
		for (String dayObject:days) {
			day += dayObject + ", ";;
		}
		day += "]";
		day = day.replace(", ]",  "]");
		return day;
	}
}
