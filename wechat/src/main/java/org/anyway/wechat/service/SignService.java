package org.anyway.wechat.service;

import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;

import org.anyway.wechat.constant.ConstantWeChat;
import org.anyway.wechat.util.DigestUtil;

/**
 * 验证签名
 * @author beinfo
 *
 */
public class SignService {
	
	/**
	 * 认证微信签名
	 * @param request
	 * @return 是否成功
	 */
	public static boolean checkSignature(HttpServletRequest request) {
		// 微信加密签名，signature结合了开发者填写的token参数和请求中的timestamp参数、nonce参数。
		String signature = request.getParameter("signature");
		// 时间戳
		String timestamp = request.getParameter("timestamp");
		// 随机数
		String nonce = request.getParameter("nonce");
		
		return checkSignature(signature,timestamp,nonce);
	}
	
	/**
	 * 验证签名
	 * @param token 令牌
	 * @param signature 微信加密签名
	 * @param timestamp 时间戳
	 * @param nonce 随机数
	 * @return 是否验证成功
	 */
	public static boolean checkSignature(String token, String signature, String timestamp, String nonce) {
		String[] array = new String[] { token, timestamp, nonce };
		// 将token、timestamp、nonce三个参数进行字典排序
		Arrays.sort(array);
		StringBuffer sb = new StringBuffer();
		for (String str : array) {
			sb.append(str);
		}
		String tempSignature = DigestUtil.SHA1(sb.toString());
		// 将sha1加密后的字符串可与signature对比
		return tempSignature != null ? tempSignature.equalsIgnoreCase(signature) : false;
	}
	
	/**
	 * 验证签名
	 * @param signature 微信加密签名
	 * @param timestamp 时间戳
	 * @param nonce 随机数
	 * @return 是否验证成功
	 */
	private static boolean checkSignature(String signature, String timestamp, String nonce) {
		return checkSignature(ConstantWeChat.TOKEN, signature, timestamp, nonce);
	}
	
}
