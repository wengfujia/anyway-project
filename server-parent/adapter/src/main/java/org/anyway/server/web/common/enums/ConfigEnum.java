package org.anyway.server.web.common.enums;

public class ConfigEnum {
	
	public enum SectionEnum {
		
		SCHOOL("school", 0),
		CLASS("class", 1);
		
		private String name;
		private int index;
		
		private SectionEnum(String name, int index) {
			this.name = name;
	        this .index = index;  
	    }
		
		//获取名称
		public String getName() {
			return this.name;
		}
		//获取标识号
		public int getIndex() {
			return this.index;
		}
		
		@Override
		public String toString() {
			return String.valueOf(this.index);
		}
	}
	
}
