package org.anyway.dbserver.sokcet;

import org.anyway.common.types.pint;
import org.anyway.common.utils.uNetUtil;
import org.anyway.server.data.CChrList;
import org.junit.Test;

import junit.framework.TestCase;

public class CChrListTest extends TestCase {
	
	@Test
	public void test() {
//		Runnable OpenSocket = new Runnable(){
//			@Override
//			public void run(){
//				long aa = System.currentTimeMillis();
//				System.out.println(aa);
//			}
//		};
//		
//		for (int i=0; i<100; i++) {
//			Thread thread = new Thread(OpenSocket);
//			thread.start();	
//		}
	
		CChrList list = new CChrList();
		StringBuilder dd =  new StringBuilder();
		for (int i=0; i<2;i++) {
			
			dd.append(i+"dddddddddddddddddd\tccccccccccccccccccccccccc\teeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee");
		}
		list.Append("first"+dd.toString());
		
		dd.setLength(0);
		assertEquals(dd.length(), 0);

		for (int i=0; i<4;i++) {
			
			dd.append(i+"dddddddddddddddddd\tccccccccccccccccccccccccc\teeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee");
		}
		list.Append("second"+dd.toString());
		
		dd.setLength(0);
		for (int i=0; i<6;i++) {
			
			dd.append(i+"dddddddddddddddddd\tccccccccccccccccccccccccc\teeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee");
		}
		list.Append("third"+dd.toString());
		
		pint pstrlen = new pint(0);
	    byte[] pstr=list.First(pstrlen);
	    System.out.println( uNetUtil.getString(pstr, "UTF-8"));
		for (; !list.Eof(); pstr = list.Next(pstrlen)) {
			System.out.println( uNetUtil.getString(pstr, "UTF-8"));
			if (list.IsLast()) {
				System.out.println("IsLast");
			}
    	}
	}
}
