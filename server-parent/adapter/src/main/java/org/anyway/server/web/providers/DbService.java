/*
 * 名称: DbService
 * 描述: 读取xml数据库静态基类
 * 版本：  1.0.0
 * 作者： 翁富家
 * 修改:
 * 日期：2015年05月25日
 * 修改日期:
 */

package org.anyway.server.web.providers;

import org.anyway.exceptions.NoCacheException;

public class DbService {
	
	/**
	 * 获取所有分类
	 * @throws NoCacheException 
	 */
	public static void FillCategories() throws NoCacheException {
		XmlProvider provider = new XmlProvider();
		provider.FillProvinceCategory();
		provider.FillCityCategory();
		provider.FillDistrictCategory();
		provider.FillSchoolCategory();
		provider.FillGradeCategory();
		provider.FillSpecialtyCategory();
		provider.FillCourseCategory();
		provider.FillAdvisoryCategory();
		provider.FillMessageCategory();
		provider.FillUserTypeCategory();
		provider = null;
	}
	
	/**
	 * 获取省列表
	 * @throws NoCacheException 
	 */
	public static void FillProvinceCategory() throws NoCacheException {
		XmlProvider provider = new XmlProvider();
		provider.FillProvinceCategory();
		provider = null;
	}

	/**
	 * 获取市列表
	 * @throws NoCacheException 
	 */
	public static void FillCityCategory() throws NoCacheException {
		XmlProvider provider = new XmlProvider();
		provider.FillCityCategory();
		provider = null;
	}

	/**
	 * 获取区列表
	 * @throws NoCacheException 
	 */
	public static void FillDistrictCategory() throws NoCacheException {
		XmlProvider provider = new XmlProvider();
		provider.FillDistrictCategory();
		provider = null;
	}

	/**
	 * 获取学校列表
	 * @throws NoCacheException 
	 */
	public static void FillSchoolCategory() throws NoCacheException {
		XmlProvider provider = new XmlProvider();
		provider.FillSchoolCategory();
		provider = null;
	}

	/**
	 * 获取年级列表
	 * @throws NoCacheException 
	 */
	public static void FillGradeCategory() throws NoCacheException {
		XmlProvider provider = new XmlProvider();
		provider.FillGradeCategory();
		provider = null;
	}

	/**
	 * 获取专业列表
	 * @throws NoCacheException 
	 */
	public static void FillSpecialtyCategory() throws NoCacheException {
		XmlProvider provider = new XmlProvider();
		provider.FillSpecialtyCategory();
		provider = null;
	}

	/**
	 * 获取成绩类型列表
	 * @throws NoCacheException 
	 */
	public static void FillCourseCategory() throws NoCacheException {
		XmlProvider provider = new XmlProvider();
		provider.FillCourseCategory();
		provider = null;
	}

	/**
	 * 获取咨询类型列表
	 * @throws NoCacheException 
	 */
	public static void FillAdvisoryCategory() throws NoCacheException {
		XmlProvider provider = new XmlProvider();
		provider.FillAdvisoryCategory();
		provider = null;
	}

	/**
	 * 获取消息类型列表
	 * @throws NoCacheException 
	 */
	public static void FillMessageCategory() throws NoCacheException {
		XmlProvider provider = new XmlProvider();
		provider.FillMessageCategory();
		provider = null;
	}

	/**
	 * 获取用户类型列表
	 * @throws NoCacheException 
	 */
	public static void FillUserTypeCategory() throws NoCacheException {
		XmlProvider provider = new XmlProvider();
		provider.FillUserTypeCategory();
		provider = null;
	}
	
	/**
	 * 读取关键字过滤列表
	 * @throws NoCacheException 
	 */
	public static void FillErrors() throws NoCacheException {
		XmlProvider provider = new XmlProvider();
		provider.FillErrors();
		provider = null;
	}
	
	/**
	 * 读取habase服务端IP列表
	 * @throws NoCacheException 
	 */
	public static void FillIpTables() throws NoCacheException {
		XmlProvider provider = new XmlProvider();
		provider.FillIpTables();
		provider = null;
	}
	
	/**
	 * 读取关键字过滤列表
	 * @throws NoCacheException 
	 */
	public static void FillStopWords() throws NoCacheException {
		XmlProvider provider = new XmlProvider();
		provider.FillStopWords();
		provider = null;
	}
	
}
