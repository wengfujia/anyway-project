package org.anyway.common.crypto;

import org.anyway.common.utils.uHexUtils;
import org.junit.Before;
import org.junit.Test;

public class DESedeCoderTest extends DESedeCoder {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void test() throws Exception {
		byte[] key = PASSWORD_CRYPT_KEY.getBytes();
		//System.out.println("key："+ showByteArray(key));
		
		//Key k = toKey(key); 
        
		String data ="wengfujia";
		//String data ="12345678";
		System.out.println("加密前数据: string:"+data);
		//System.out.println("加密前数据: byte[]:"+showByteArray(data.getBytes()));
		System.out.println();
		byte[] encryptData = DESedeCoder.encrypt(data.getBytes());
		System.out.println("加密后数据: :"+encryptData);
		//System.out.println("加密后数据: byte[]:"+showByteArray(encryptData));
		System.out.println("加密后数据: hexStr:"+uHexUtils.bytesToHexString(encryptData));
		System.out.println();
		//byte[] decryptData = decrypt(encryptData,k);
		//System.out.println("解密后数据: byte[]:"+showByteArray(decryptData));
		//System.out.println("解密后数据: string:"+new String(decryptData));
	}

}
