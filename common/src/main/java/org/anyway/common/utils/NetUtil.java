/*
 * 名称: NetUtils
 * 描述: 网络数据处理函数集类
 * 版本：  1.0.0
 * 作者： 翁富家
 * 修改:
 * 日期：2013年08月18日
 * 修改日期:
 */

package org.anyway.common.utils;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;

import org.anyway.common.SystemConfig;
import org.anyway.exceptions.NotEnoughDataInByteBufferException;

public class NetUtil {
	private static final byte SZ_BYTE = 1;
	private static final byte SZ_SHORT = 2;
	private static final byte SZ_INT = 4;
	//private static final byte SZ_LONG = 8;
	
	/**
	 * memset byte[]
	 * @param buff
	 * @param val
	 * @param ilen
	 */
	public static void memset(byte[] buff, byte val, int ilen)
    {
        for (int i = 0; i < ilen; i++)
            buff[i] = val;
    }
	
	/**
	 * chars转换bytes
	 * @param chars
	 * @return byte[]
	 */
	public static byte[] getBytes (char[] chars) {
	   Charset cs = Charset.forName (SystemConfig.CharsetName);
	   CharBuffer cb = CharBuffer.allocate (chars.length);
	   cb.put (chars);
	   cb.flip ();
	   ByteBuffer bb = cs.encode (cb);
	  
	   return bb.array();
	}
	
	/**
	 * bytes转换chars
	 * @param bytes
	 * @return char[]
	 */
	public static char[] getChars (byte[] bytes) {
		Charset cs = Charset.forName (SystemConfig.CharsetName);
		ByteBuffer bb = ByteBuffer.allocate (bytes.length);
		bb.put (bytes);
        bb.flip ();
        CharBuffer cb = cs.decode (bb);
		  
		return cb.array();
	}
		
    /** 
     * 从指定位置载取ilen长度，转为字符串 
     * @see 如果系统不支持所传入的<code>charset</code>字符集,则按照系统默认字符集进行转换 
     */  
    public static String getString(byte[] buffer, int frmIndx, int ilen, String charset) {  
        if(StringUtil.empty(buffer)){  
            return "";  
        }  
        
        int icount = Math.min(buffer.length, ilen);
        if (frmIndx>buffer.length) {
        	return "";
        }
        
        byte[] data = new byte[icount];
        System.arraycopy(buffer, frmIndx, data, 0, icount);
        
        if(StringUtil.empty(charset)){  
            return new String(data);  
        } 
        
        try {  
            return new String(data, charset);  
        } catch (UnsupportedEncodingException e) {  
        	return new String(data);  
        }  
    }  
    
    /** 
     * 字节数组转为字符串 
     * @see 如果系统不支持所传入的<code>charset</code>字符集,则按照系统默认字符集进行转换 
     */  
    public static String getString(byte[] data, String charset) {  
        if(StringUtil.empty(data)){  
            return "";  
        }  
        if(StringUtil.empty(charset)){  
            return new String(data);  
        } 
        
        try {  
            return new String(data, charset);  
        } catch (UnsupportedEncodingException e) {  
        	return new String(data);  
        }  
    }  
      
    /** 
     * 字符串转为字节数组 
     * @see 如果系统不支持所传入的<code>charset</code>字符集,则按照系统默认字符集进行转换 
     */  
    public static byte[] getBytes(String data, String charset) throws UnsupportedEncodingException {  
        data = (data==null ? "" : data);  
        if (StringUtil.empty(charset)) {  
            return data.getBytes();  
        }  
        
        try {  
            return data.getBytes(charset);  
        } catch (UnsupportedEncodingException e) {  
        	return data.getBytes();  
        }  
    }  
    
    /**
     * int转换byte[]
     * @param data
     * @return byte[]
     */
    public static byte[] int2bytes(int data) {
	
		byte[] targets = new byte[SZ_INT];

        targets[3] = (byte) (data & 0xff);// 最低位
        targets[2] = (byte) ((data >> 8) & 0xff);// 次低位
        targets[1] = (byte) ((data >> 16) & 0xff);// 次高位
        targets[0] = (byte) (data >>> 24);// 最高位,无符号右移。
    	return targets;
    }

    /**
     * byte[]转成int
     * @param buffer
     * @return int
     * @throws NotEnoughDataInByteBufferException
     */
    public static int bytes2int(byte[] buffer) throws NotEnoughDataInByteBufferException {
		int targets = 0;
		int len = buffer.length;
		if (len >= SZ_INT) {
			targets = (buffer[3] & 0xff) | ((buffer[2] << 8) & 0xff00) | ((buffer[1] << 24) >>> 8) | (buffer[0] << 24);
			return targets;
		} else {
			throw new NotEnoughDataInByteBufferException(len, 4);
		}
	}
    
    /**
     * char转换成int(客户端发送过来的是char)
     * @param buffer
     * @return int
     * @throws UnsupportedEncodingException 
     */
    public static int chars2int(byte[] buffer, String charset) {
		int targets = 0;
		String s = getString(buffer, charset).trim();
		if (s.isEmpty()==false && StringUtil.isInt(s)) {
			targets = Integer.parseInt(s);
		}
		
		return targets;
	}
    
    /**
     * int转换成byte[](客户端采用的是char)
     * @param data
     * @return byte[]
     */
    public static byte[] int2chars(int data) {
    	String s = String.valueOf(data);
    	return s.getBytes();
    }
    
    /**
     * 扩展包长度
     * @param buffer
     * @param ilen
     * @return byte[]
     */
    public static byte[] expandBytes(byte[] buffer, int ilen) {
    	if (ilen > buffer.length) {
    		byte[] newBuf = new byte[ilen];
    		System.arraycopy(buffer, 0, newBuf, 0, buffer.length);
    		newBuf[ilen-1] = '\0';
    		return newBuf;
    	} else {
    		return buffer;
    	}
    }
    
    /**
     * 追加两个byte[]的数组，新包为 des（count） + source
     * @param des
     * @param source
     * @param count
     * @return newbuff
     * @throws NotEnoughDataInByteBufferException
     */
	public static byte[] appendBytes(byte[] des, byte[] source, int count) throws NotEnoughDataInByteBufferException {
		if (des != null) {		
			if (count > source.length) {
				count = source.length;
			}
			int len = des.length;
			
			byte[] newBuf = new byte[len + count];
			if (len > 0) {
				System.arraycopy(des, 0, newBuf, 0, len);
			}
			System.arraycopy(source, 0, newBuf, len, count);
			return newBuf;
		} else
			return null;
	}

	/**
     * 追加两个byte[]的数组,新包为des+source的长
     * @param des
     * @param source
     * @param count
     * @return newbuff
     * @throws NotEnoughDataInByteBufferException
     */
	public static byte[] appendBytes(byte[] des, byte[] source) throws NotEnoughDataInByteBufferException {
		if (des != null) {
			int count = source.length;
			int len = des.length;
			byte[] newBuf = new byte[len + count];
			if (len > 0) {
				System.arraycopy(des, 0, newBuf, 0, len);
			}
			System.arraycopy(source, 0, newBuf, len, count);
			return newBuf;
		} else
			return null;
	}

	/**
	 * 指定位置的byte[]开始取len长度，转换成int
	 * @param buffer
	 * @param fromIdx
	 * @param ilen
	 * @return int
	 * @throws UnsupportedEncodingException
	 */
	public static int readInt(byte[] buffer, int fromIdx, int ilen) throws UnsupportedEncodingException {
		int result = 0;
		byte[] tmp = new byte[ilen];
		System.arraycopy(buffer, fromIdx, tmp, 0, ilen);
		result = chars2int(tmp, SystemConfig.CharsetName);
		return result;
	}
	
	/**
	 * 指定位置的byte[]开始转换成int
	 * @param buffer
	 * @param fromIdx
	 * @return int
	 * @throws NotEnoughDataInByteBufferException
	 */
	public static int readInt(byte[] buffer, int fromIdx) throws NotEnoughDataInByteBufferException {
		int result = 0;
		int len = buffer.length;
		if (len >= SZ_INT) {
			result |= buffer[fromIdx] & 0xff;
			result <<= 8;
			result |= buffer[fromIdx+1] & 0xff;
			result <<= 8;
			result |= buffer[fromIdx+2] & 0xff;
			result <<= 8;
			result |= buffer[fromIdx+3] & 0xff;
			return result;
		} else {
			throw new NotEnoughDataInByteBufferException(len, 4);
		}
	}

	/**
	 * 指定位置的byte[]开始转换成short
	 * @param buffer
	 * @param fromIdx
	 * @return short
	 * @throws NotEnoughDataInByteBufferException
	 */
	public static short readShort(byte[] buffer, int fromIdx) throws NotEnoughDataInByteBufferException {
		short result = 0;
		int len = buffer.length;
		if (len >= SZ_INT) {
			result |= buffer[fromIdx] & 0xff;
			result <<= 8;
			result |= buffer[fromIdx+1] & 0xff;
			return result;
		} else {
			throw new NotEnoughDataInByteBufferException(len, 4);
		}
	}

	/**
	 * 读取指定长度的byte[]
	 * @param buffer
	 * @param count
	 * @return byte[]
	 * @throws NotEnoughDataInByteBufferException
	 */
	public static byte[] readBytes(byte[] buffer, int frmIndx, int count) throws NotEnoughDataInByteBufferException {
		int len = buffer.length;
		if (frmIndx > len) {
			return null;
		}
		
		byte[] result = null;		
		if (count > 0) {
			if (len >= count) {
				byte[] resBuf = new byte[count];
				System.arraycopy(buffer, frmIndx, resBuf, 0, count);
				result = resBuf;
				return result;
			} else {
				throw new NotEnoughDataInByteBufferException(len, count);
			}
		} else {
			return result; // just null as wanted count = 0
		}
	}	
	
	/**
	 * 读取指定长度的byte[]
	 * @param buffer
	 * @param count
	 * @return byte[]
	 * @throws NotEnoughDataInByteBufferException
	 */
	public static byte[] readBytes(byte[] buffer, int count) throws NotEnoughDataInByteBufferException {
		int len = buffer.length;
		byte[] result = null;
		if (count > 0) {
			if (len >= count) {
				byte[] resBuf = new byte[count];
				System.arraycopy(buffer, 0, resBuf, 0, count);
				result = resBuf;
				return result;
			} else {
				throw new NotEnoughDataInByteBufferException(len, count);
			}
		} else {
			return result; // just null as wanted count = 0
		}
	}	

	/**
	 * 移动byte
	 * @param buffer
	 * @return 被移动的byte
	 * @throws NotEnoughDataInByteBufferException
	 */
	public static byte removeByte(byte[] buffer) throws NotEnoughDataInByteBufferException {
		byte result = 0;
		byte[] resBuff = removeBytes(buffer, SZ_BYTE);
		result = resBuff[0];
		return result;
	}

	/**
	 * 移动short
	 * @param buffer
	 * @return 被移动的short
	 * @throws NotEnoughDataInByteBufferException
	 */
	public static short removeShort(byte[] buffer) throws NotEnoughDataInByteBufferException {
		short result = 0;
		byte[] resBuff = removeBytes(buffer, SZ_SHORT);
		result |= resBuff[0] & 0xff;
		result <<= 8;
		result |= resBuff[1] & 0xff;
		return result;
	}

	/**
	 * 移动int
	 * @param buffer
	 * @return 被移动的int
	 * @throws NotEnoughDataInByteBufferException
	 */
	public static int removeInt(byte[] buffer) throws NotEnoughDataInByteBufferException {
		int result = readInt(buffer, 0);
		removeBytes(buffer, SZ_INT);
		return result;
	}

	/**
	 * 移徐字符串,并指定转换编码
	 * @param buffer
	 * @param size
	 * @return 被移动的String
	 * @throws NotEnoughDataInByteBufferException
	 */
	public static String removeString(byte[] buffer, int size, String encoding)
		throws NotEnoughDataInByteBufferException, UnsupportedEncodingException {
		int len = buffer.length;
		if (len < size) {
			throw new NotEnoughDataInByteBufferException(len, size);
		}
		UnsupportedEncodingException encodingException = null;
		String result = null;
		if (len > 0) {
			if (encoding != null) {
				try {
					result = new String(buffer, 0, size, encoding);
				} catch (UnsupportedEncodingException e) {
					LoggerUtil.println("Unsupported encoding exception " + e);
					encodingException = e;
				}
			} else {
				result = new String(buffer, 0, size);
			}
			removeBytes(buffer, size);
		} else {
			result = new String("");
		}
		if (encodingException != null) {
			throw encodingException;
		}
		return result;
	}

	/**
	 * 移徐字符串
	 * @param buffer
	 * @param size
	 * @return String
	 * @throws NotEnoughDataInByteBufferException
	 */
	public static String removeStringEx(byte[] buffer, int size) throws NotEnoughDataInByteBufferException {
		int len = buffer.length;
		int zeroPos = 0;
		if (len < size) {
			throw new NotEnoughDataInByteBufferException(0, 1);
		}
		while ((zeroPos < size) && (buffer[zeroPos] != 0)) {
			zeroPos++;
		}

		String result = null;
		if (len > 0) {
			result = new String(buffer, 0, zeroPos);
			removeBytes(buffer, size);
		} else {
			result = new String("");
		}
		return result;
	}

	/**
	 * 
	 * @param buffer
	 * @param count
	 * @return byte[]
	 * @throws NotEnoughDataInByteBufferException
	 */
	public static byte[] removeBytes(byte[] buffer, int count) throws NotEnoughDataInByteBufferException {
		byte[] result = null;
		int len = buffer.length;
		int lefts = len - count;
		if (lefts > 0) {
			byte[] newBuf = new byte[lefts];
			System.arraycopy(buffer, count, newBuf, 0, lefts);
			result = newBuf;
		}
		return result;
	}

	/**
	 * byte[]转换16进制
	 * @param buffer
	 * @return String
	 */
	public static String getHexDump(byte[] buffer) {
		String dump = "";
		try {
			int dataLen = buffer.length;
			for (int i = 0; i < dataLen; i++) {
				dump += Character.forDigit((buffer[i] >> 4) & 0x0f, 16);
				dump += Character.forDigit(buffer[i] & 0x0f, 16);
			}
		} catch (Throwable t) {
			// catch everything as this is for debug
			dump = "Throwable caught when dumping = " + t;
		}
		return dump;
	}
	
	/**
	 * 编码
	 * 把字符串集转换成separate分隔的字符串
	 * @param params
	 * @return
	 */
	public static String encodeString(String separate, String... params) {
		String result = "";
		for (String param : params) {
			if (result == "") {
				result = param;
			}
			else {
				result += String.valueOf(separate) + param;
			}
		}
		return result;
	}
}
