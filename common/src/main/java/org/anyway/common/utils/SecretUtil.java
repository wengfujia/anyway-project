/*
 * 名称: uSecretUtils
 * 描述: SecretUtils
 * 版本：  1.0.0
 * 作者： 翁富家
 * 修改:
 * 日期：2013年08月18日
 * 修改日期:
 */

package org.anyway.common.utils;

import com.google.gson.Gson;
import java.io.UnsupportedEncodingException;

import org.anyway.common.crypto.DESedeCoder;
import org.anyway.common.uConfigVar;
import org.anyway.common.enums.CryptEnum;

public class uSecretUtil {
	/**
     * 加密方法，加码出错返回原数组
     * @param src 源数据的字节数组,mode加密模式
     * @return 
	 * @throws Exception 
     */
	public static byte[] Encrypt(byte[] src, CryptEnum mode) {
		byte[] result = null;
		
		if (mode==CryptEnum.DES) {
			try {
				result = DESedeCoder.encrypt(src);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} 
		else {
			result = src;
		}
		return result;
	}

	/**
     * 解密函数,解码出错返回原数组
     * @param src 密文的字节数组,mode解密模式
     * @return byte[]
	 * @throws Exception 
     */
	public static byte[] Decrypt(byte[] src, CryptEnum mode) {
		byte[] result = null;
		
		if (mode==CryptEnum.DES) {
			try {
				result = DESedeCoder.decrypt(src);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else {
			result = src;
		}
		
		return result;
	}
	
	/***
	 * 进行3DES加密再base64解码
	 * @param src
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static String Encrypt3Des(String source) throws UnsupportedEncodingException{		
		byte[] encrypt3Des = Encrypt(source.getBytes(uConfigVar.CharsetName), CryptEnum.DES);
    	String result = uStringUtil.base64Encode(encrypt3Des);
    	return result;
	}
	 
	/***
	 * 进行3DES加密并转换成json数据格式
	 * @param src
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static String Encrypt3DesToJson(String source) throws UnsupportedEncodingException{		
		byte[] encrypt3Des = Encrypt(source.getBytes(uConfigVar.CharsetName), CryptEnum.DES);
		Gson gson = new Gson();
		String str = gson.toJson(encrypt3Des);
		return str;
	}
	
	/***
	 * 进行Base64解码再3DES解密
	 * @param src
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static String Decrypt3Des(String des) throws UnsupportedEncodingException{
    	//Gson gson = new Gson();
    	//byte[] bt = gson.fromJson(des, byte[].class);
		byte[] bt = uStringUtil.base64Decode02(des);
    	String result = new String(Decrypt(bt, CryptEnum.DES), uConfigVar.CharsetName);
    	return result;
    }
	
	public static String Decrypt3Des(byte[] des) throws UnsupportedEncodingException{
	    String result = new String(Decrypt(des, CryptEnum.DES), uConfigVar.CharsetName);
	    return result;
	}
	
	/***
	 * 进行3DES解密并转换成json数据格式
	 * @param src
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static String Decrypt3DesToJson(String des) throws UnsupportedEncodingException{
    	Gson gson = new Gson();
    	byte[] bt = gson.fromJson(des, byte[].class);
    	String str = new String(Decrypt(bt, CryptEnum.DES), uConfigVar.CharsetName);
    	return str;
    }

	public static void test (CryptEnum mode) throws Exception
    {
    	String msg = "3DES";
    	String r = Encrypt3DesToJson(msg);
    	System.out.print(r);
    	String d = Decrypt3DesToJson(r);
    	System.out.print(d);
    }
}
