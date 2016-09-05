package org.anyway.server.data.models;
/*
 * 名称: TbErrorBean
 * 描述: 错误信息类
 * 版本：  1.0.0
 * 作者： 翁富家
 * 修改:
 * 日期：2013年10月15日
 * 修改日期:
 */
@SuppressWarnings("serial")
public class ErrorDescBean implements java.io.Serializable {
	private int rowid;
	private int errorcode;
	private String description;
	private String response;
	
	/**
	 * 唯 一序号
	 * @return int
	 */
	public int getRowID() {
        return rowid;
    }
	/**
	 * 唯 一序号
	 * @param iRowID
	 */
    public void setRowID(int iRowID) {
        this.rowid = iRowID;
    }
    
    /**
	 * 错误代码
	 * @return int
	 */
	public int getErrorCode() {
        return errorcode;
    }
	/**
	 * 错误代码
	 * @param sErrorCode
	 */
    public void setErrorCode(int sErrorCode) {
        this.errorcode = sErrorCode;
    }
    
    /**
	 * 错误代码说明
	 * @return String
	 */
	public String getDescription() {
        return description;
    }
	/**
	 * 错误代码说明
	 * @param sDescription
	 */
    public void setDescription(String sDescription) {
        this.description = sDescription;
    }
    
    /**
	 * 显示的错误说明
	 * @return String
	 */
	public String getResponse() {
        return response;
    }
	/**
	 * 显示的错误说明
	 * @param sResponse
	 */
    public void setResponse(String sResponse) {
        this.response = sResponse;
    }
    
}
