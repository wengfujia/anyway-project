/*
 * 名称: DBHEADER.java
 * 描述: 数据库包头定义 类
 * 版本：  1.0.0
 * 作者： 翁富家
 * 修改:
 * 日期：2013年08月16日
 * 修改日期:
 */
package org.anyway.server.data.packages;

@SuppressWarnings("serial")
public class DBHEADER implements java.io.Serializable {
	private String seq_id = "";
	
	//唯 一序号  20位
    public String getSeqID() {
        return seq_id;
    }
    public void setSeqID(String sSeq_id) {
        this.seq_id = sSeq_id;
    } 
    
    public void Clear() {
    	seq_id = "";
    }
}
