package org.anyway.server.dbase.model;
/*
 * 名称: DataBase
 * 描述: 缓存数据类基类
 * 版本：  1.0.0
 * 作者： 翁富家
 * 修改:
 * 日期：2013年10月15日
 * 修改日期:
 */
import java.util.UUID;
import java.util.Date;

@SuppressWarnings("serial")
public abstract class DataBase implements java.io.Serializable {
	private int rowid;
	private Date datecreated;
	private Date datemodified;
	
	public static final UUID GenerateGUID(){
		UUID uuid = UUID.randomUUID();
		return uuid;		
	}
	
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
	 * ID  GUID号
	 * @return String
	 */
	public String getID() {
        return GenerateGUID().toString();
    }

    /**
     * 创建日期
     * @return Date
     */
    public Date getDateCreated() {
        return datecreated;
    }
    /**
     * 创建日期
     * @param DateCreated
     */
    public void setDateCreated(Date DateCreated) {
        this.datecreated = DateCreated;
    }
    
    /**
     * 修改日期
     * @return Date
     */
    public Date getDateModified() {
        return datemodified;
    }
    /**
     * 修改日期
     * @param DateModified
     */
    public void setDateModified(Date DateModified) {
        this.datemodified = DateModified;
    }
    
    
}
