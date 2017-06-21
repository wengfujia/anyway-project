/*
 * 名称: HexUtils
 * 描述: 二进制、16进制转换类
 * 版本：  1.0.0
 * 作者： 翁富家
 * 修改:
 * 日期：2013年10月15日
 * 修改日期:
 */

package org.anyway.common.utils;

import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;

/**
 * @Description: 进制转换工具类，常用为十六进制 二进制 八进制 字符串 byte及数组转换
 */
public class uHexUtil {

	@SuppressWarnings("unused")
	private static final String PATTERN_LEN_TEN = "0000000000";// Len(2^32)=10

	private static final DecimalFormat numberformater = new DecimalFormat();

	/**
	 * hex to char
	 * 
	 * @param arg0
	 *            two number orgnized to hex string
	 * @return char
	 */
	public static char toChar(String arg0) {
		return Character.toChars(Integer.parseInt(arg0, 16))[0];
	}

	/**
	 * hex string to int
	 * 
	 * @param arg0
	 * @return
	 */
	public static int toInt(String arg0) {
		return Integer.parseInt(arg0, 16);
	}

	/**
	 * hex string to binary string
	 * 
	 * @param hexstr
	 * @param radix
	 * @return
	 */
	public static String toBinaryStr(String hexstr, int radix) {
		String binstr = Integer.toBinaryString(Integer.parseInt(hexstr, 16));
		StringBuffer b = new StringBuffer(binstr);

		while (b.length() < radix) {
			b.insert(0, '0');
		}
		return b.toString();
	}

	/**
	 * hex string to binary string, format to 32 char
	 * 
	 * @param arg0
	 * @return
	 */
	public static String toBinaryStr32(String hexstr) {
		int radix = 16;

		String binstr = Long.toBinaryString(Long.parseLong(hexstr, radix));
		;
		StringBuffer b = new StringBuffer(binstr);
		while (b.length() < radix) {
			b.insert(0, '0');
		}
		return b.toString();
	}

	/**
	 * binary to String, 16 chars
	 * 
	 * @return
	 */
	public static String toBinaryStr16(String hexstr) {
		return toBinaryStr(hexstr, 16);
	}

	/**
	 * binary to String, 8 chars
	 * 
	 * @return
	 */
	public static String toBinaryStr8(byte bin) {
		String binstr = Integer.toBinaryString(bin);
		StringBuffer b = new StringBuffer(binstr);

		while (b.length() < 8) {
			b.insert(0, '0');
		}
		return b.toString();
	}

	/**
	 * hex string to byte array
	 * 
	 * @param hexString
	 * @return byte[]
	 */
	public static byte[] hexStringToByte(String hex) {
		hex = hex.toUpperCase();// 大小写转换，引起重视
		int len = (hex.length() / 2);
		byte[] result = new byte[len];
		char[] achar = hex.toCharArray();
		for (int i = 0; i < len; i++) {
			int pos = i * 2;
			int ten = toByte(achar[pos]);
			int one = toByte(achar[pos + 1]);
			result[i] = (byte) (ten << 4 | one);
		}
		return result;
	}

	/**
	 * binary String to hex byte array
	 * 
	 * @return
	 */
	public static byte[] binaryStrToHexByte(String val) {
		int bi2 = Integer.parseInt(val, 2);
		numberformater.applyPattern("00000000");
		int res_16 = Integer.valueOf(Integer.toHexString(bi2));
		String result = numberformater.format(res_16);
		byte[] b = uHexUtil.hexStringToByte(result);
		return b;
	}

	private static int toByte(char c) {
		byte b = (byte) "0123456789ABCDEF".indexOf(c);
		return b;
	}

	/**
	 * String to hex String
	 */
	public static String str2HexStr(String str) {
		char[] chars = "0123456789ABCDEF".toCharArray();
		StringBuilder sb = new StringBuilder("");
		byte[] bs = str.getBytes();
		int bit;
		for (int i = 0; i < bs.length; i++) {
			bit = (bs[i] & 0x0f0) >> 4;
			sb.append(chars[bit]);
			bit = bs[i] & 0x0f;
			sb.append(chars[bit]);
		}
		return sb.toString();
	}

	/**
	 * byte array to hex string
	 * 
	 * @param byte[]
	 * @return HexString
	 */
	public static final String bytesToHexString(byte[] bArray) {
		StringBuffer sb = new StringBuffer(bArray.length);
		String sTemp;
		for (int i = 0; i < bArray.length; i++) {
			sTemp = Integer.toHexString(0xFF & bArray[i]);
			if (sTemp.length() < 2)
				sb.append(0);
			sb.append(sTemp.toUpperCase());
		}
		return sb.toString();
	}

	/**
	 * bytes to int4
	 * 
	 * @param b
	 * @return
	 */
	public static int bytesToInt4(byte[] b) {
		int mask = 0xff;
		int temp = 0;
		int n = 0;
		for (int i = 0; i < 4; i++) {
			n <<= 8;
			temp = b[i] & mask;
			n |= temp;
		}
		return n;
	}

	/**
	 * string to hex string
	 * 
	 * @return
	 */
	public String stringToHexString(String string) {
		return uHexUtil.bytesToHexString(string.getBytes());
	}

	/**
	 * int to byte[], length=1
	 * 
	 * @param i
	 * @return
	 */
	public static final byte[] intToSimpleByteArray(int intValue) {
		byte[] result = null;
		if (intValue <= 255) {
			result = new byte[1];
			result[0] = (byte) (intValue & 0xFF);
		}
		return result;
	}

	/**
	 * int 转 byte数组， 2个字节或者4个字节 int to byte[], length=2,4
	 * 
	 * @param i
	 * @return
	 */
	public static final byte[] intToByteArray(int intValue) {
		byte[] result = null;
		if (intValue <= 65535) {
			result = new byte[2];
			result[0] = (byte) ((intValue & 0xFF00) >> 8);
			result[1] = (byte) (intValue & 0x00FF);
		} else if (intValue > 65535 && intValue <= Integer.MAX_VALUE) {
			result = new byte[4];
			result[0] = (byte) ((intValue & 0xFF000000) >> 24);
			result[1] = (byte) ((intValue & 0x00FF0000) >> 16);
			result[2] = (byte) ((intValue & 0x0000FF00) >> 8);
			result[3] = (byte) ((intValue & 0x000000FF));
		}
		return result;
	}

	/**
	 * int 转 byte数组， 4个字节
	 * 
	 * @param intValue
	 * @return
	 */
	public static final byte[] intToByte4Array(int intValue) {
		byte[] result = null;
		if (intValue <= 65535) {
			result = new byte[4];
			result[0] = 0x00;
			result[1] = 0x00;
			result[2] = (byte) ((intValue & 0xFF00) >> 8);
			result[3] = (byte) (intValue & 0x00FF);
		} else if (intValue > 65535 && intValue <= Integer.MAX_VALUE) {
			result = new byte[4];
			result[0] = (byte) ((intValue & 0xFF000000) >> 24);
			result[1] = (byte) ((intValue & 0x00FF0000) >> 16);
			result[2] = (byte) ((intValue & 0x0000FF00) >> 8);
			result[3] = (byte) ((intValue & 0x000000FF));
		}
		return result;
	}

	/**
	 * int转byte数组，3个字节 int to byte[], length=3
	 * 
	 * @param i
	 * @return
	 */
	public static final byte[] intToThreeByteArray(int intValue) {
		byte[] result = null;
		if (intValue > 65535 && intValue <= 16777215) {
			result = new byte[3];
			result[0] = (byte) ((intValue & 0xFF0000) >> 16);
			result[1] = (byte) ((intValue & 0x00FF00) >> 8);
			result[2] = (byte) ((intValue & 0x0000FF));
		}
		return result;
	}

	/**
	 * int 转数组，有符号，单字节 int to byte[], contain sign
	 * 
	 * @param signIntVal
	 * @return
	 */
	public static final byte[] intToSignByteArray(int signIntVal) {
		byte[] b = uHexUtil.intToByteArray(Math.abs(signIntVal));

		if (signIntVal < 0) {
			b[0] = (byte) 0x80;
		}
		return b;
	}

	/**
	 * encode to gbk
	 * 
	 * @param b
	 * @return
	 */
	public static final byte[] encodeGBK(String s) {
		try {
			return s.getBytes("GBK");
		} catch (UnsupportedEncodingException e) {
			uLogger.getLogger().error(e.getMessage(), e);
		}
		return new byte[0];
	}

	/**
	 * encode to utf-16 little endian
	 * 
	 * @param b
	 * @return
	 */
	public static final byte[] encodeUTF16LE(String s) {
		try {
			return s.getBytes("UTF-16LE");
		} catch (UnsupportedEncodingException e) {
			uLogger.getLogger().error(e.getMessage(), e);
		}
		return new byte[0];
	}

	/**
	 * encode to utf-16 big endian
	 * 
	 * @param b
	 * @return
	 */
	public static final byte[] encodeUTF16BE(String s) {
		try {
			return s.getBytes("UTF-16BE");
		} catch (UnsupportedEncodingException e) {
			uLogger.getLogger().error(e.getMessage(), e);
		}
		return new byte[0];
	}

	/**
	 * byte[] decode to gbk
	 * 
	 * @param b
	 * @return
	 */
	public static final String decodeGBK(byte[] b) {
		try {
			return new String(b, "GBK");
		} catch (UnsupportedEncodingException e) {
			uLogger.getLogger().error(e.getMessage(), e);
		}
		return "";
	}

	/**
	 * byte[] decode to utf16 little endian
	 * 
	 * @param b
	 * @return
	 */
	public static final String decodeUTF16LE(byte[] b) {
		try {
			return new String(b, "UTF-16LE");
		} catch (UnsupportedEncodingException e) {
			uLogger.getLogger().error(e.getMessage(), e);
		}
		return "";
	}

	/**
	 * byte[] decode to utf 16 big endian
	 * 
	 * @param b
	 * @return
	 */
	public static final String decodeUTF16BE(byte[] b) {
		try {
			return new String(b, "UTF-16BE");
		} catch (UnsupportedEncodingException e) {
			uLogger.getLogger().error(e.getMessage(), e);
		}
		return "";
	}

	/**
	 * 源自JDK1.6的方法，从一个byte[]数组中复制byte
	 * 
	 * @since 1.6
	 */
	public static byte[] copyOfRange(byte[] original, int from, int to) {
		int newLength = to - from;
		if (newLength < 0)
			throw new IllegalArgumentException(from + " > " + to);
		byte[] copy = new byte[newLength];
		System.arraycopy(original, from, copy, 0, Math.min(original.length
				- from, newLength));
		return copy;
	}

	/**
	 * short to byte[]
	 * 
	 * @param n
	 * @return
	 */
	public static byte[] shortToBytes(short n) {
		byte[] b = new byte[2];
		b[1] = (byte) (n & 0xff);
		b[0] = (byte) ((n >> 8) & 0xff);
		return b;
	}

}
