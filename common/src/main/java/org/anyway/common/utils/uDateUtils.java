package org.anyway.common.utils;

/*
 * 名称: DateUtils
 * 描述: 日期函数转换类
 * 版本：  1.0.0
 * 作者： 翁富家
 * 修改:
 * 日期：2013年10月15日
 * 修改日期:
 */
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class uDateUtils {

	public static String[] chars = { "A", "B", "C", "D", "E", "F", "G", "H",
			"I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U",
			"V", "W", "X", "Y", "Z" };

	public static String[] weeks = { "星期日", "星期一", "星期二", "星期三", "星期四", "星期五",
			"星期六" };
	
	public static long time() {
		long result = 0;
		GregorianCalendar gc = new GregorianCalendar();
		result = gc.getTimeInMillis();
    	return result;
	}
	
	/*public static long getDateLong() {
		Date dt = new Date();
		return dt.getTime();
	}*/
	
	/**
	 * 获取日期
	 * @return
	 */
	public static Date getDateTime() {
		return getDateTime(null, null);
	}

	/**
	 * 获取指定格式日期
	 * @param format
	 * @return
	 */
	public static Date getDateTime(String format) {
		return getDateTime(format, null);
	}

	/**
	 * 日期转换成指定格式日期
	 * @param format
	 * @param date
	 * @return
	 */
	public static Date getDateTime(String format, Object date) {
		format = format == null || "".equals(format) ? "yyyy-MM-dd HH:mm:ss"
				: format;
		SimpleDateFormat frm = new SimpleDateFormat(format);
		try {
			if (date == null)
				return frm.parse(frm.format(Calendar.getInstance().getTime()));
			else if (date instanceof Date)
				return frm.parse(frm.format((Date) date));
			else if (date instanceof String)
				return frm.parse(date.toString());
			else
				return null;
		} catch (ParseException ex) {
			return null;
		}
	}
	
	/**
	 * 日期格式化
	 * @param val
	 * @param pattern
	 * @return
	 */
	public static String format(Object val, String pattern) {
		if (val == null)
			return "";
		Date tmp = null;
		if (val instanceof String) {
			String sval = val.toString();
			if (sval.endsWith(".0"))
				sval = sval.substring(0, sval.length() - 2);
			if (sval.length() < 10)
				return sval;

			try {
				SimpleDateFormat frm = new SimpleDateFormat(
						sval.length() > 11 ? "yyyy-MM-dd HH:mm:ss"
								: "yyyy-MM-dd");
				tmp = frm.parse(sval);
			} catch (Exception ex) {
				ex.printStackTrace();
				return val.toString();
			}
		} else if (uStringUtils.isInt(val.toString()))
			tmp = new Date(Long.parseLong(val.toString()));
		else {
			tmp = (Date) val;
		}

		SimpleDateFormat frm = new SimpleDateFormat(pattern);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(tmp);

		String strVal = frm.format(tmp);
		return strVal;
		/*String[] arr = strVal.split("(-|:|\\.|\\s)");
		pattern = pattern.toLowerCase();
		pattern = pattern.replace("yyyy", arr[0]);
		pattern = pattern.replace("yy", arr[0].substring(2));
		pattern = pattern.replace("mm", arr[1]);
		pattern = pattern.replace("m", arr[1].substring(1));
		pattern = pattern.replace("m", arr[1].substring(1));
		pattern = pattern.replace("dd", arr[2]);
		pattern = pattern.replace("d", arr[2].substring(1));
		pattern = pattern.replace("h", arr[3]);
		pattern = pattern.replace("i", arr[4]);
		pattern = pattern.replace("s", arr[5]);
		pattern = pattern.replace("w",
				weeks[calendar.get(Calendar.DAY_OF_WEEK) - 1]);

		return pattern;*/
	}
	
}
