/*
 * 名称: HMessageBuffer
 * 描述: Http的消息类
 * 版本：  1.0.0
 * 作者： 翁富家
 * 修改:
 * 日期：2013年10月15日
 * 修改日期:
 */

package org.anyway.server.data.http;

import java.util.ArrayList;
import java.util.List;

public class HMessageBuffer {
	private int result ;
	private String userName;
	private String passWord;
	private int commandId;
	private List<CBody> data = new ArrayList<CBody>();
	
	//结果
    public int getResult() {
        return result;
    }
    public void setResult(int iResult) {
        this.result = iResult;
    }
   
    public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	public String getPassword() {
		return passWord;
	}
	public void setPassword(String password) {
		this.passWord = password;
	}
	
	public int getCommandId() {
		return commandId;
	}
	public void setCommandId(int iCommandid) {
		this.commandId = iCommandid;
	}
	
	//包体
    public List<CBody> getData() {
        return data;
    }
    
    public void setData(List<CBody> Datas) {
        this.data = Datas;
    }
    
    public void Clear() {
    	result = 0;
    	userName = "";
    	passWord = "";
    	commandId = 0;
    	if (data!=null) {data.clear(); data = null;}
    }
    
    //包体类，用于记录集的序列化
    public static class CBody
    {
        private String content;

        public CBody(String Content) {
            this.content = Content;
        }
       
        //设置内容
        public String getContent() {
            return content;
        }  
        public void setContent(String Content) {
            this.content = Content;
        }
    }
}
