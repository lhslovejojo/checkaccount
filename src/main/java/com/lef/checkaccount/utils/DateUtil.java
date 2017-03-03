package com.lef.checkaccount.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public class DateUtil {
	public static boolean isValidDate(String str, String formatStr) {
		boolean convertSuccess = true;
		SimpleDateFormat format = new SimpleDateFormat(formatStr);
		try {
			format.setLenient(false);
			format.parse(str);
		} catch (ParseException e) {
			convertSuccess = false;
		} catch (NullPointerException ex) {
			convertSuccess = false;
		}
		return convertSuccess;
	}

}
