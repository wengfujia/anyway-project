/*
 * 名称: DBCache
 * 描述: 数据库缓存类(各分类，错误信息，用户信息)
 * 版本：  1.0.0
 * 作者： 翁富家
 * 修改:
 * 日期：2015年05月28日
 * 修改日期：2015.6.1
 * 修改：由ehcache改为map
 */

package org.anyway.server.web.cache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.anyway.server.data.models.ErrorDescBean;
import org.anyway.server.data.models.IpTableBean;
import org.anyway.server.web.models.CategoryBean;

public class DbCache {
	//private EhCacheFactory ehcachemanager = null;
	private Map<Integer, CategoryBean> advisoryCache = null;	//咨询分类缓存
	private Map<Integer, CategoryBean> messageCache = null;		//消息分类缓存
	
	private Map<Integer, CategoryBean> provinceCache = null;	//省分类缓存
	private Map<Integer, CategoryBean> cityCache = null;		//市分类缓存
	private Map<Integer, CategoryBean> districtCache = null;	//区分类缓存
	
	private Map<Integer, CategoryBean> schoolCache = null;		//学校分类缓存
	private Map<Integer, CategoryBean> gradeCache = null;		//年级分类缓存
	private Map<Integer, CategoryBean> specialtyCache = null;	//专业分类缓存
	private Map<Integer, CategoryBean> courseCache = null;		//成绩分类缓存，不含大学与培训班成绩
	
	private Map<Integer, CategoryBean> userTypeCache = null;	//用户分类缓存
	
	private Map<Integer, ErrorDescBean> errordescsCache = null;	//错误定义缓存
	
	private Map<String, IpTableBean> iptablesCache = null;		//habase群IP列表缓存
	
	private List<String> stopwordsCache = null;	//关键字缓存
	
	/**
	 * 构造函数
	 * @throws Exception 
	 */
	/*public DbCache(EhCacheFactory manager) throws Exception {
		ehcachemanager = null;
		if (null != manager) {
			this.ehcachemanager = manager;
		}
		else throw new Exception("manager不能为空，DbCache数据库缓存创建失败！");
	}*/
	
	/**
	 * 咨询分类缓存
	 * @return
	 */
	public Map<Integer, CategoryBean> AdvisoryCache() {
		if (null == advisoryCache) {
			advisoryCache = new HashMap<Integer, CategoryBean>();
		}
		return advisoryCache;
	}
	
	/**
	 * 消息分类缓存
	 * @return
	 */
	public Map<Integer, CategoryBean> MessageCache() {
		if (null == messageCache) {
			messageCache = new HashMap<Integer, CategoryBean>();
		}
		return messageCache;
	}
	
	/**
	 * 省分类缓存
	 * @return
	 */
	public Map<Integer, CategoryBean> ProvinceCache() {
		if (null == provinceCache) {
			provinceCache = new HashMap<Integer, CategoryBean>();
		}
		return provinceCache;
	}
	
	/**
	 * 市分类缓存
	 * @return
	 */
	public Map<Integer, CategoryBean> CityCache() {
		if (null == cityCache) {
			cityCache = new HashMap<Integer, CategoryBean>();
		}
		return cityCache;
	}
	
	/**
	 * 消息分类缓存
	 * @return
	 */
	public Map<Integer, CategoryBean> DistrictCache() {
		if (null == districtCache) {
			districtCache = new HashMap<Integer, CategoryBean>();
		}
		return districtCache;
	}
	
	/**
	 * 学校分类缓存
	 * @return
	 */
	public Map<Integer, CategoryBean> SchoolCache() {
		if (null == schoolCache) {
			schoolCache = new HashMap<Integer, CategoryBean>();
		}
		return schoolCache;
	}
	
	/**
	 * 年级分类缓存
	 * @return
	 */
	public Map<Integer, CategoryBean> GradeCache() {
		if (null == gradeCache) {
			gradeCache = new HashMap<Integer, CategoryBean>();
		}
		return gradeCache;
	}
	
	/**
	 * 专业分类缓存
	 * @return
	 */
	public Map<Integer, CategoryBean> SpecialtyCache() {
		if (null == specialtyCache) {
			specialtyCache = new HashMap<Integer, CategoryBean>();
		}
		return specialtyCache;
	}
	
	/**
	 * 成绩分类缓存
	 * @return
	 */
	public Map<Integer, CategoryBean> CourseCache() {
		if (null == courseCache) {
			courseCache = new HashMap<Integer, CategoryBean>();
		}
		return courseCache;
	}
	
	/**
	 * 用户类型分类缓存
	 * @return
	 */
	public Map<Integer, CategoryBean> UserTypeCache() {
		if (null == userTypeCache) {
			userTypeCache = new HashMap<Integer, CategoryBean>();
		}
		return userTypeCache;
	}
	
	/**
	 * 错误定义缓存
	 * @return
	 */
	public Map<Integer, ErrorDescBean> ErrorDescsCache() {
		if (null == errordescsCache) {
			errordescsCache = new HashMap<Integer, ErrorDescBean>();
		}
		return errordescsCache;
	}

	/**
	 * habase群IP列表缓存
	 * @return
	 */
	public Map<String, IpTableBean> IpTablesCache() {
		if (null == iptablesCache) {
			iptablesCache = new HashMap<String, IpTableBean>();
		}
		return iptablesCache;
	}
	
	/**
	 * 关键字缓存
	 * @return
	 */
	public List<String> StopWordsCache() {
		if (null == stopwordsCache) {
			stopwordsCache = new ArrayList<String>();
		}
		return stopwordsCache;
	}
	
	/**
	 * 是否含有非法关键字
	 * @param content
	 * @return
	 */
	public boolean hasStopWord(String content) {
		return stopwordsCache.indexOf(content) >= 0 ? true:false;
	}
}
