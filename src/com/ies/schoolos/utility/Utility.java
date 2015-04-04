package com.ies.schoolos.utility;

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
}
