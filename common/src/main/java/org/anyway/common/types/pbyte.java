package org.anyway.common.types;
/**
 * 自定义byte类型
 * 作用：用于函数可返回值参数类型
 * @author wengfj
 *
 */
public class pbyte {
	private byte[] b = null;
	
	public pbyte() {
	}
	public pbyte(byte[] B) {
		this.b = B;
	}
	
	public void setByte(byte[] B)
	{
		this.b = B;
	}
	public byte[] getByte() 
	{
		return this.b;
	}
}
