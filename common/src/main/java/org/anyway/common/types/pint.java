package org.anyway.common.types;
/**
 * 自定义int类型
 * 作用：用于函数可返回值参数类型
 * @author wengfj
 *
 */
public class pint implements java.io.Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -840425706227701875L;
	
	private int i = 0;
	
	public pint() {		
	}
	public pint(int I) {
		this.i = I;
	}
	
    public void setInt(int I)
    {
        this.i = I;
    }
    public int getInt()
    {
        return this.i;
    }
    
}
