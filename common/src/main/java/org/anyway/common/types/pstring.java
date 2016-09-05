package org.anyway.common.types;
/**
 * 自定义String类型
 * 作用：用于函数可返回值参数类型
 * @author wengfj
 *
 */
public class pstring {
	private String s = "";
	
	public pstring() {		
	}
	public pstring(String S) {
		this.s = S;
	}
	
    public void setString(String S)
    {
        this.s = S;
    }
    public String getString()
    {
        return this.s;
    }
}
