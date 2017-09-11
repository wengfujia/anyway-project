/*
 * 名称: StringUtils
 * 描述: 字符转换类
 * 版本：  1.0.0
 * 作者： 翁富家
 * 修改:
 * 日期：2013年10月15日
 * 修改日期:
 */

package org.anyway.common.utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import org.apache.commons.codec.binary.Base64;

public class StringUtil {
	
	static String charsetName = "utf-8";
	
	/**
	 * 设置该类字符串处理的字符编 码
	 * 只对本类设置有效
	 * @param value
	 */
	public static void setCharsetName(String value) {
		charsetName = value;
	}
	
	/**
	 * 字符拷贝函数 功能:遇到\t \n进行返回
	 * @param des
	 * @param source
	 * @return des的长度
	 */
	public static int charcpy(String des,String source)
	{
	  for (int i=0; i<source.length(); i++)
	  {
	    if (source.charAt(i) == '\t' || source.charAt(i) == '\n')
	      return i;
	    des.toCharArray()[i] = source.charAt(i);
	  }
	  return 0;
	}
	
	/**
	 * 字符串数组转换成String,","分隔
	 * @param array
	 * @return String
	 */
	public static String getArrayString(String[] array) {
		return getArrayString(array, ",");
	}

	/**
	 * 字符串数组转换成String,自定义space分隔
	 * @param array
	 * @param space
	 * @return String
	 */
	public static String getArrayString(String[] array, String space) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < array.length; i++)
			sb.append(array[i] + space);
		return sb.length() > 0 ? sb.substring(0, sb.length() - 1) : sb
				.toString();
	}

	/**
	 * 获取随机数
	 * @return
	 */
	public static String getRandom() {
		return String.valueOf(Math.round(Math.random() * 8999999 + 1000000));
	}

	/**
	 * 获取UUID号
	 * @return
	 */
	public static String getUUID() {
		return UUID.randomUUID().toString().replace("-", "");
	}

	/**
	 * 字符串转换成integer
	 * @param str
	 * @return int
	 */
	public static int getInteger(String str) {
		if (null == str)
			return 0;
		else
			return isInt(str) ? Integer.parseInt(str) : 0;
	}

	/**
	 * object转换成integer
	 * @param val
	 * @return int
	 */
	public static int getInteger(Object val) {
		if (null == val)
			return 0;
		else
			return getInteger(val.toString());
	}

	/**
	 * 字符串转换长整弄
	 * @param str
	 * @return
	 */
	public static long getLong(String str) {
		if (null == str)
			return 0;
		else
			return isInt(str) ? Long.parseLong(str) : 0;
	}

	public static float getFloat(String str) {
		if (null == str)
			return 0.0F;
		else
			return isNumber(str) ? Float.parseFloat(str) : 0.0F;
	}

	public static long getLong(Object val) {
		if (null == val)
			return 0;
		else
			return getLong(val.toString());
	}

	public static float getFloat(Object val) {
		if (null == val)
			return 0.0F;
		else
			return getFloat(val.toString());
	}

	public static boolean isAnsi(String str) {
		return empty(str) ? false : str.matches("^\\w+$");
	}

	public static boolean isBoolean(String str) {
		if (empty(str))
			return false;
		else {
			if ("1".equals(str))
				return true;
			else if ("true".equals(str))
				return true;
			else if ("0".equals(str))
				return false;
			else if ("false".equals(str))
				return false;
			else
				return true;
		}
	}

	public static boolean isString(String str) {
		return empty(str) ? false : str.matches("^[\\w\u4e00-\u9fa5]+$");
	}

	public static boolean isNumber(String str) {
		return empty(str) ? false : str.matches("^([0-9])[0-9]*(\\.[0-9]*)?$");
	}

	public static boolean isEmail(String str) {
		return empty(str) ? false
				: str.matches("^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$");
	}

	public static boolean isTime(String str) {
		return empty(str) ? false
				: str.matches("^((([0-1]?[0-9])|(2[0-3])):([0-5]?[0-9])(:[0-5]?[0-9])?)$");
	}

	public static boolean isIP(String str) {
		return empty(str) ? false
				: str.matches("^((2[0-4]\\d|25[0-5]|[01]?\\d\\d?).){3}(2[0-4]\\d|25[0-5]|[01]?\\d\\d?)$");
	}

	public static boolean isDateString(String str) {
		return empty(str) ? false : str
				.matches("(\\d{4})-(\\d{1,2})-(\\d{1,2})");
	}

	public static boolean isInt(String str) {
		return empty(str) ? false : str.matches("^[+-]?[0-9]+$");
	}

	public static boolean isIDCardNumber(String str) {
		return empty(str) ? false : str.matches("^[0-9]{15,18}$");
	}

	public static boolean isMobile(String str) {
		return empty(str) ? false : str.matches("^1[0-9]{10}$");
	}

	public static boolean isUUID(String str) {
		return empty(str) ? false : str.matches("^[A-Za-z0-9]{32}$");
	}

	public static boolean isUniqueID(Object object) {
		return object == null ? false : isUniqueID(object.toString());
	}

	public static boolean isUniqueID(String str) {
		return empty(str) ? false : str.matches("^\\d{14,15}$");
	}

	public static boolean isImgFile(String filename) {
		String ext = filename.substring(filename.lastIndexOf("."));
		return (ext == "jpg" || ext == "jpeg" || ext == "png" || ext == "bmp" || ext == "gif");
	}

	public static String urlEncode(String val) {
		try {
			return java.net.URLEncoder.encode(val, charsetName);
		} catch (Exception ex) {
			return null;
		}
	}

	public static String base64Encode(String str) throws UnsupportedEncodingException {	
		return Base64.encodeBase64String(str.getBytes(charsetName)); 
	}

	public static String base64Encode(byte[] str) throws UnsupportedEncodingException {
		return new String(Base64.encodeBase64(str), charsetName);
	}
	
	public static String base64Decode(String str) {
		try {
			return new String(Base64.decodeBase64(str), charsetName);
		} catch (Exception ex) {
			return null;
		}
	}

	public static byte[] base64Decode02(String str) {
		try {
			return Base64.decodeBase64(str);
		} catch (Exception ex) {
			return null;
		}
	}
	
	public static String hash(String str, String type) {
		try {
			MessageDigest m = MessageDigest.getInstance(type);
			m.update(str.getBytes());		
			byte[] d = m.digest();
			return base64Encode(d);
		} catch (Exception ex) {
			return null;
		}
	}

	public static String md5(String val) {
		char[] hexDegits = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
				'a', 'b', 'c', 'd', 'e', 'f' };
		try {
			java.security.MessageDigest md = java.security.MessageDigest
					.getInstance("MD5");
			md.update(val.getBytes(charsetName));
			byte[] tmp = md.digest();
			char[] tmpStr = new char[32];
			int k = 0;
			for (int i = 0; i < 16; i++) {
				byte b = tmp[i];
				tmpStr[k++] = hexDegits[b >>> 4 & 0xf];
				tmpStr[k++] = hexDegits[b & 0xf];
			}

			return new String(tmpStr);
		} catch (Exception ex) {
			return null;
		}
	}

	public static String sha1(String value) {
		try {
			MessageDigest md;
			md = MessageDigest.getInstance("SHA-1");
			md.update(value.getBytes(charsetName));
			byte[] digest = md.digest();
			return HexUtil.bytesToHexString(digest);
		} catch (Exception ex) {
			return null;
		}
	}
	
	public static String size(long fileSize) {
		DecimalFormat decimalFormat = new DecimalFormat("#.00");
		if (fileSize < 1024)
			return decimalFormat.format((double) fileSize) + "B";
		else if (fileSize < 1048576)
			return decimalFormat.format((double) fileSize / 1024) + "K";
		else if (fileSize < 1073741824)
			return decimalFormat.format((double) fileSize / 1048576) + "M";
		else
			return decimalFormat.format((double) fileSize / 1073741824) + "G";
	}

	public static String random(int length) {
		return random(length, true);
	}

	public static String random(int length, boolean numbers) {
		String base = numbers ? "0123456789"
				: "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
		Random random = new Random();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < length; i++) {
			int number = random.nextInt(base.length());
			sb.append(base.charAt(number));
		}
		return sb.toString();
	}

	public static String substring(String val, int len) {
		if (val == null)
			return null;
		else if (val.length() <= len)
			return val;
		else
			return val.substring(0, len);
	}

	public static String substring(String val, int len, String cat) {
		if (val == null)
			return null;
		else if (val.length() <= len)
			return val;
		else
			return val.substring(0, len) + cat;
	}

	public static String utf8Substring(String val, int len) {
		if (val == null)
			return null;
		else if (val.length() <= len)
			return val;
		else {
			int temp = 0;
			int chars = 0;
			try {
				for (int i = 0; i < val.length(); i++) {
					temp += (val.charAt(i) + "").getBytes("utf-8").length;
					if (temp > len * 3)
						break;
					chars++;
				}
			} catch (Exception ex) {

			}
			return val.substring(0, chars);
		}
	}

	public static String utf8Substring(String val, int len, String cat) {
		if (val == null)
			return null;
		else if (val.length() <= len)
			return val;
		else {
			int temp = 0;
			int chars = 0;
			try {
				for (int i = 0; i < val.length(); i++) {
					temp += (val.charAt(i) + "").getBytes("utf-8").length;
					if (temp > len * 3)
						break;
					chars++;
				}
			} catch (Exception ex) {

			}
			return val.substring(0, chars) + cat;
		}
	}

	public static String cnSubstring(String val, int len) {
		if (val == null)
			return null;
		else if (val.length() <= len)
			return val;
		else {
			int temp = 0;
			int chars = 0;
			try {
				for (int i = 0; i < val.length(); i++) {
					temp += (val.charAt(i) + "").getBytes("GB2312").length;
					if (temp > len * 2)
						break;
					chars++;
				}
			} catch (Exception ex) {
				return null;
			}
			return val.substring(0, chars);
		}
	}

	public static String cnSubstring(String val, int len, String cat) {
		if (val == null)
			return null;
		else if (val.length() <= len)
			return val;
		else {
			int temp = 0;
			int chars = 0;
			try {
				for (int i = 0; i < val.length(); i++) {
					temp += (val.charAt(i) + "").getBytes("GB2312").length;
					if (temp > len * 2)
						break;
					chars++;
				}
			} catch (Exception ex) {
				return null;
			}
			return val.substring(0, chars) + cat;
		}
	}

	public static boolean empty(Object val) {
		if (val == null)
			return true;
		else
			return empty(val.toString());
	}

	public static boolean empty(String val) {
		if (val == null)
			return true;
		else {
			String str = val.toString();
			if (str.length() == 0 || "null".equals(str))
				return true;
			else
				return false;
		}
	}

	public static String nullOf(String val, String of) {
		return val == null ? of : val;
	}

	public static Object nullOf(Object val, String of) {
		return val == null ? of : val;
	}

	public static String stripScripts(String str) {
		return empty(str) ? str : str.replaceAll(
				"(?:<script.*?>)((\\n|\\r|.)*?)(?:<\\/script>)", "");
	}

	public static String stripTags(String str) {
		return empty(str) ? str : str.replaceAll("<\\/?[^>]+>", "");
	}

	public static long ip2long(String strIp) {
		long[] ip = new long[4];
		int position1 = strIp.indexOf(".");
		int position2 = strIp.indexOf(".", position1 + 1);
		int position3 = strIp.indexOf(".", position2 + 1);
		ip[0] = Long.parseLong(strIp.substring(0, position1));
		ip[1] = Long.parseLong(strIp.substring(position1 + 1, position2));
		ip[2] = Long.parseLong(strIp.substring(position2 + 1, position3));
		ip[3] = Long.parseLong(strIp.substring(position3 + 1));
		return (ip[0] << 24) + (ip[1] << 16) + (ip[2] << 8) + ip[3];
	}

	public static String long2ip(long longIp) {
		String str = String.valueOf((longIp >>> 24));
		str += ".";
		str += String.valueOf((longIp & 0x00FFFFFF) >>> 16);
		str += ".";
		str += String.valueOf((longIp & 0x0000FFFF) >>> 8);
		str += ".";
		str += String.valueOf((longIp & 0x000000FF));
		return str;
	}

	public static String[] split(String ctn, int length) {
		ArrayList<String> arr = new ArrayList<String>();
		int len = ctn.length();
		if (len <= length)
			arr.add(ctn);
		else {
			int size = (int) Math.floor((double) len / (double) length);
			for (int i = 0; i < size; i++) {
				arr.add(ctn.substring(i * length, (i + 1) * length));
			}

			if (len % length != 0)
				arr.add(ctn.substring(size * length));
		}
		return arr.toArray(new String[] {});
	}

	public static boolean contains(String str, int cstr) {
		return str.indexOf(cstr + "") == -1 ? false : true;
	}

	public static boolean contains(String str, int cstr, String pre) {
		return str.indexOf(pre + cstr) == -1 ? false : true;
	}

	public static boolean contains(String str, int cstr, String pre, String next) {
		return str.indexOf(pre + cstr + next) == -1 ? false : true;
	}

	public static boolean contains(String str, String cstr) {
		return str.indexOf(cstr) == -1 ? false : true;
	}

	public static boolean contains(String str, String cstr, String pre) {
		return str.indexOf(pre + cstr) == -1 ? false : true;
	}

	public static boolean contains(String str, String cstr, String pre,
			String next) {

		return str.indexOf(pre + cstr + next) == -1 ? false : true;
	}

	public static int indexOf(String str, String cstr) {
		return str.indexOf(cstr);
	}

	public static String replace(String str, String reg, String cstr) {
		return str.replaceAll(reg, cstr);
	}

	public static boolean startsWith(String str, String cstr) {
		return str.startsWith(cstr);
	}

	public static boolean endsWith(String str, String cstr) {
		return str.endsWith(cstr);
	}

	public static String toLowerCase(String str) {
		return str.toLowerCase();
	}

	public static String toUpperCase(String str) {
		return str.toUpperCase();
	}

	public static String hashOfMd5(String val) {
		if (val == null)
			return null;
		return "{MD5}" + hash(val, "MD5");
	}

	public static String nlToBr(String val) {
		return val == null ? "" : val.replaceAll("\\n", "<br/>");
	}

	public static long timeMillisSpan(long startTimeMillis) {
		return System.currentTimeMillis() - startTimeMillis;
	}

	public static Map<String, Object> getPageNumMap(int total, int currentPage) {
		return getPageNumMap((long) total, currentPage, 10, 10);
	}

	public static Map<String, Object> getPageNumMap(int total, int currentPage,
			int pageSize) {
		return getPageNumMap((long) total, currentPage, pageSize, 10);
	}

	public static Map<String, Object> getPageNumMap(int total, int currentPage,
			int pageSize, int stepPage) {
		return getPageNumMap((long) total, currentPage, pageSize, stepPage);
	}

	public static Map<String, Object> getPageNumMap(long total, int currentPage) {
		return getPageNumMap(total, currentPage, 10, 10);
	}

	public static Map<String, Object> getPageNumMap(long total,
			int currentPage, int pageSize) {
		return getPageNumMap(total, currentPage, pageSize, 10);
	}

	public static Map<String, Object> getPageNumMap(long total, int currentPage, int pageSize, int stepPage) {
		Map<String, Object> pageNumMap = new HashMap<String, Object>();
		List<Integer> fromTo = new ArrayList<Integer>();
		stepPage = stepPage <= 0 ? 10 : stepPage;

		int from = 1;
		int to = 1;
		int pages = 0;
		int offset = 4;

		if (total > pageSize) {
			pages = (int) Math.ceil((total * 1.00) / (pageSize * 1.00));
			if (stepPage > pages) {
				from = 1;
				to = pages;
			} else {
				from = currentPage - offset;
				to = from + stepPage - 1;
				if (from < 1) {
					to = currentPage + 1 - from;
					from = 1;
					if (to - from < stepPage) {
						to = stepPage;
					}
				} else {
					if (to > pages) {
						from = pages - stepPage + 1;
						to = pages;
					}
				}
			}
		}

		for (int i = from; i <= to; i++) {
			fromTo.add(i);
		}
		
		pageNumMap.put("from", from);
		pageNumMap.put("to", to);
		pageNumMap.put("pages", pages);
		pageNumMap.put("offset", offset);
		pageNumMap.put("fromTo", fromTo);

		return pageNumMap;
	}
}