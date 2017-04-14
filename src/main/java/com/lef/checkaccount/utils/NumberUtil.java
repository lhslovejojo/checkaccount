package com.lef.checkaccount.utils;

import java.math.BigDecimal;

public class NumberUtil {
	public static Long getLongFromStr(String longStr) {
		if (longStr != null && !"".equals(longStr.trim()))
			return Long.parseLong(longStr);
		else
			return 0l;
	}

	public static Integer getIntegerFromStr(String intStr) {
		if (intStr != null && !"".equals(intStr.trim()))
			return Integer.parseInt(intStr);
		else
			return 0;
	}

	public static Double getDoubleFromStr(String doubleStr) {
		if (doubleStr != null && !"".equals(doubleStr.trim()))
			return Double.parseDouble(doubleStr);
		else
			return 0d;
	}

	public static BigDecimal getBigDecimalFromStr(String BigDecimalStr) {
		if (BigDecimalStr != null && !"".equals(BigDecimalStr.trim()))
			return new BigDecimal(BigDecimalStr);
		else
			return new BigDecimal(0);
	}
}
