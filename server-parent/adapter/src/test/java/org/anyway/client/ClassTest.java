package org.anyway.client;

import static org.junit.Assert.*;
import org.apache.commons.collections.functors.PredicateTransformer;
import org.junit.Test;

public class ClassTest {

	class child {
		private String ddd;
		public String getddd() {
			return ddd;
		}
	}
	
	class parent extends child{
		private String ccc;
		public String getccc() {
			return ccc;
		}
	}
	
	class testT<T> {
		T cc;
		void getT() {
			((child)cc).getddd();
		}
		
		void setT(T cc) {
			this.cc = cc;
		}
	}
	
	class doTest extends testT<parent> {
		@Override
		void getT() {
			super.getT();
		}
	}
	
	private void c(child cd) {
		doTest dod = new doTest();
		dod.setT(new parent());
		dod.getT();
		
		if (cd instanceof parent) {
			((parent)cd).getccc();
			System.out.println("parent");
		} else if (cd instanceof child) {
			System.out.println("child");
		}
	}
	
	@Test
	public void test() {
		c(new child());
		
		c(new parent());
	}

}
