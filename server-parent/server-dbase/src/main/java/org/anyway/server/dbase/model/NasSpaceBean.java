package org.anyway.server.dbase.model;
import org.anyway.server.dbase.enums.StatusEnum;

@SuppressWarnings("serial")
public class NasSpaceBean extends DataBase {
	
	protected String nasSpaceId;
	protected String spaceName;
	protected long spaceSize;
	protected StatusEnum status;

	/**
	 * 获取空间名称
	 * @return String
	 */
	public String getSpaceName() {
		return spaceName;
	}
	/**
	 * 设置空间名称
	 * @param spaceName
	 */
	public void setSpaceName(String spaceName) {
		this.spaceName = spaceName;
	}
	
	/**
	 * 获取空间大小
	 * @return
	 */
	public long getSpaceSize() {
		return spaceSize;
	}
	/**
	 * 设置空间大小
	 * @param spaceSize
	 */
	public void setSpaceSize(long spaceSize) {
		this.spaceSize = spaceSize;
	}
	
	/**
	 * 状态
	 * @return StatusEnum
	 */
	public StatusEnum getStatus() {
		return status;
	}
	/**
	 * 状态
	 * @param status
	 */
	public void setStatus(StatusEnum status) {
		this.status = status;
	}
	/**
	 * 状态
	 * @param status
	 */
	public void setStatus(int status){
		this.status = StatusEnum.getEnum(status);
	}
}
