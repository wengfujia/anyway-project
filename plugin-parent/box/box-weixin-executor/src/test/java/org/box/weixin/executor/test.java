package org.box.weixin.executor;

import static org.junit.Assert.*;

import org.anyway.common.types.pint;
import org.anyway.common.utils.LoggerUtil;
import org.junit.Test;

public class test {

	@Test
	public void test() {
		String ddd = LoggerUtil.sprintf("CMD.%s", "33");
		
		pint i= new pint();
		i.setInt(2);
		t(i);
		System.out.println(i);
		
		String dString ="aaaaa";
		s(dString);
		System.out.println(dString);
	}
	
	void t(pint i) {
		i.setInt(3);
	}
	
	void s(String s) {
		s = "asf";
	}

}
