package org.anyway.exceptions;
/*
 * 名称: NotEnoughDataInByteBufferException
 * 描述: 自定义NotEnoughDataInByteBufferException
 * 版本：  1.0.0
 * 作者： 翁富家
 * 修改:
 * 日期：2013年10月15日
 * 修改日期:
 */
public class NotEnoughDataInByteBufferException extends MyException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5084315197182138348L;
	private int available;
	private int expected;

	public NotEnoughDataInByteBufferException(int p_available, int p_expected) {
		super("Not enough data in byte buffer. " + "Expected " + p_expected + ", available: " + p_available + ".");
		available = p_available;
		expected = p_expected;
	}

	public NotEnoughDataInByteBufferException(String s) {
		super(s);
		available = 0;
		expected = 0;
	}

	public int getAvailable() {
		return available;
	}

	public int getExpected() {
		return expected;
	}
}
