package org.anyway.common.protocol.buffer.impl.http;

public class HResponseBuffer {
	private byte[] result;
	
	//返回
    public byte[] getResult() {
        return result;
    }
    public void setResult(byte[] buffer) {
        this.result = buffer;
    }
}
