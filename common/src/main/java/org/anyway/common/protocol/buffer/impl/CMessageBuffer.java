package org.anyway.common.protocol.buffer.impl;
/*
 * 名称: CMessageBuffer
 * 描述: socket的消息类
 * 版本：  1.0.0
 * 作者： 翁富家
 * 修改:
 * 日期：2013年10月15日
 * 修改日期:
 */
public class CMessageBuffer {
	private int len ;
	private int left;
	private int buflen;
	private byte[] header;// = new byte[uGlobalVar.HEADER_LENGTH];	
	private byte[] buffer;
	
	public CMessageBuffer() {
		Clear();
	}
	
	public void Clear() {
		len = 0;
		left = 0;
		header = null;
		buflen = 0;
		buffer = null;
	}
	
	//包长 
    public int getLen() {
        return len;
    }
    public void setLen(int iLen) {
        this.len = iLen;
    }
    
    //留包长 
    public int getLeft() {
        return left;
    }
    public void setLeft(int iLeft) {
        this.left = iLeft;
    }
    
    //包体长
    public int getBufLen() {
        return buflen;
    }
    public void setBufLen(int iBufLen) {
        this.buflen = iBufLen;
    }
    
    //包头
    public byte[] getHeader() {
        return header;
    }
    public void setHeader(byte[] Buffer) {
        this.header = Buffer;
    }  
    
	//包体
    public byte[] getBuffer() {
        return buffer;
    }
    public void setBuffer(byte[] Buffer) {
        this.buffer = Buffer;
    }   
}

