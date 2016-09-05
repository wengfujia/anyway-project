package org.anyway.common.crypto;

import java.security.Key;

import org.anyway.common.utils.uHexUtils;
import org.junit.Before;
import org.junit.Test;

public class AESCoderTest extends AESCoder {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void test() throws Exception {
		byte[] key = PASSWORD_CRYPT_KEY.getBytes();
		System.out.println("key："+showByteArray(key));
		
		Key k = toKey(key);
		
		String data ="wengfujia@123";
		System.out.println("加密前数据: string:"+data);
		System.out.println("加密前数据: byte[]:"+showByteArray(data.getBytes()));
		System.out.println();
		byte[] encryptData = encrypt(data.getBytes(), k);
		System.out.println("加密后数据: byte[]:"+showByteArray(encryptData));
		System.out.println("加密后数据: hexStr:"+uHexUtils.bytesToHexString(encryptData));
		System.out.println();
		byte[] decryptData = decrypt(encryptData, k);
		System.out.println("解密后数据: byte[]:"+showByteArray(decryptData));
		System.out.println("解密后数据: string:"+new String(decryptData));
	}

}
