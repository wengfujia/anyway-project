package org.anyway.exceptions;
/*
 * 名称: MyException
 * 描述: 自定义Exception
 * 版本：  1.0.0
 * 作者： 翁富家
 * 修改:
 * 日期：2013年10月15日
 * 修改日期:
 */
public class MyException extends Exception{
	/**
	 * 
	 */
	private static final long serialVersionUID = 3117037333965625264L;

	public MyException() {
		super();
	}

	public MyException(Exception e) {
		super(e);
	}

	public MyException(String s) {
		super(s);
	}

	public MyException(String s, Exception e) {
		super(s, e);
	}
}
