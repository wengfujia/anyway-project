/**
 * 
 */
package org.anyway.common.future;

/**
 * @author wengfj
 *
 */
public class ResponseFuture {
	private int status;
	private int commandid;
	
	public ResponseFuture(int status, int commandId) {
		this.status = status;
		this.commandid = commandId;
	}
	
	public int getStatus() {
		return this.status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	
	public int getCommandID() {
		return this.commandid;
	}
	public void setCommandID(int commandid) {
		this.commandid = commandid;
	}
	
}
