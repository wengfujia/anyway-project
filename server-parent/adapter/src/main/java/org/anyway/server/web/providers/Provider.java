package org.anyway.server.web.providers;

/*
 * 名称: Provider
 * 描述: 数据库读取表的基类
 * 版本：  1.0.0
 * 作者： 翁富家
 * 修改:
 * 日期：2013年10月15日
 * 修改日期:
 */

public interface Provider {

	/**
	 * 读取城市->省分类
	 */
	public void FillProvinceCategory();
	
	/**
	 * 读取城市->市的分类
	 */
	public void FillCityCategory();
	
	/**
	 * 读取城市->区的分类
	 */
	public void FillDistrictCategory();
	
	/**
	 * 读取学校分类
	 */
	public void FillSchoolCategory();
	
	
	/**
	 * 读取年级分类
	 */
	public void FillGradeCategory();
	
	/**
	 * 读取专业分类
	 */
	public void FillSpecialtyCategory();
	
	/**
	 * 读取成绩分类
	 */
	public void FillCourseCategory();

	/**
	 * 读取咨询分类
	 */
	public void FillAdvisoryCategory();
	
	/**
	 * 读取消息分类
	 */
	public void FillMessageCategory();
	
	/**
	 * 读取用户类型分类
	 */
	public void FillUserTypeCategory();
	
	/**
	 * 读取错误定义表
	 */
	public void FillErrors();
	
	/**
	 * 读取habase服务端IP列表
	 */
	public void FillIpTables();
	
	/**
	 * 读取关键字过滤列表
	 */
	public void FillStopWords();
	
}
