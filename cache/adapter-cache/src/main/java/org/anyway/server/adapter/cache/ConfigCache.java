/*
 * 名称: ConfigCache
 * 描述: 配置信息缓存
 * 版本：  1.0.0
 * 作者： 翁富家
 * 修改:
 * 日期：2015年05月28日
 */

package org.anyway.server.adapter.cache;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.anyway.common.SystemConfig;
import org.anyway.common.enums.StatusEnum;
import org.anyway.common.models.ErrorDescBean;
import org.anyway.common.models.IpTableBean;
import org.anyway.common.utils.FileUtil;
import org.anyway.common.utils.StringUtil;
import org.anyway.exceptions.NoCacheException;
import org.anyway.server.adapter.cache.common.config.WeixinConfig;
import org.anyway.cache.ehcache.EhCacheFactory;
import org.anyway.cache.ehcache.EhCacheWrapper;
import org.anyway.wechat.entity.AccessToken;
import org.anyway.wechat.util.WeixinUtil;

public class ConfigCache {
	
	private String path = "./cfg/";
	
	private EhCacheFactory ehcachemanager = null;
	private EhCacheWrapper<String, WeixinConfig> weixincache; 	//微信配置信息缓存
	private EhCacheWrapper<String, List<Map<String, String>>> weixincache_cmdid;	//微信转换成业务头的配置缓存
	
	private Map<String, IpTableBean> routesCache = null;	//processor群IP列表缓存
	/**
	 * key为commandid + "@" + sessionid;
	 * value为iptable的name
	 */
	private Map<String, String> commandIdRouteCache = null;
	private Map<Integer, ErrorDescBean> errorsCache = null;	//错误定义缓存
	
	/**
	 * 构造函数
	 * @throws Exception 
	 */
	public ConfigCache(EhCacheFactory manager) throws NoCacheException {
		ehcachemanager = null;
		if (null != manager) {
			this.ehcachemanager = manager;
			
			weixincache = new EhCacheWrapper<String, WeixinConfig>("WeixinConfigCache", ehcachemanager.getManager());
			weixincache_cmdid = new EhCacheWrapper<String, List<Map<String, String>>>("WeixinCommandIDCache", ehcachemanager.getManager());
			loadWeixinConfig();
			
			this.routesCache = new ConcurrentHashMap<String, IpTableBean>();
			this.commandIdRouteCache = new HashMap<String, String>();
			this.errorsCache = new HashMap<Integer, ErrorDescBean>();
			this.loadRoutes();
			this.loadCommandIdRouteCache();
			this.loadErrors();
		}
		else throw new NoCacheException("manager不能为空，configcache线程缓存池创建失败！");
	}
	
	/**
	 * 构造函数
	 * @throws Exception 
	 */
	public ConfigCache(EhCacheFactory manager, boolean IsWeixinServer) throws NoCacheException {
		ehcachemanager = null;
		if (null != manager) {
			this.ehcachemanager = manager;
			if (IsWeixinServer) { //如果是微信服务端，则开启相关缓存
				weixincache = new EhCacheWrapper<String, WeixinConfig>("WeixinConfigCache", ehcachemanager.getManager());
				weixincache_cmdid = new EhCacheWrapper<String, List<Map<String, String>>>("WeixinCommandIDCache", ehcachemanager.getManager());
				loadWeixinConfig();
			}
			
			this.routesCache = new HashMap<String, IpTableBean>();
			this.commandIdRouteCache = new HashMap<String, String>();
			this.errorsCache = new HashMap<Integer, ErrorDescBean>();
			this.loadRoutes();
			this.loadCommandIdRouteCache();
			this.loadErrors();
		}
		else throw new NoCacheException("manager不能为空，configcache线程缓存池创建失败！");
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//缓存读取
	
	/**
	 * 加载微信配置文件
	 * @throws Exception 
	 */
	@SuppressWarnings("unchecked")
	private void loadWeixinConfig() {
		SAXReader reader = new SAXReader();
		try {
			Document doc = reader.read(new File(FileUtil.toFileName(path + "weixin.xml")));
			Element root =doc.getRootElement();

			//获取微信token配置信息
			Iterator<Element> nodes = root.elementIterator("tokens");
			while(nodes.hasNext()) {
				Element node = nodes.next();
				WeixinConfig config = new WeixinConfig();
				config.setKey(node.attributeValue("key"));
				config.setAppID(node.attributeValue("appid"));
				config.setAppSecret(node.attributeValue("appsecret"));
				config.setToken(node.attributeValue("token"));
				weixincache.put(config.getKey(), config);
			}
			//获取微信转换业务配置信息
			String key = "";
			List<Map<String, String>> list = null;
			nodes = root.elementIterator("commandids");
			while(nodes.hasNext()) {
				Element node = nodes.next();
				if (key.equalsIgnoreCase(node.attributeValue("key"))==false) { //根据key判断是否为同一个微信公众号
					key = node.attributeValue("key");
					list = new ArrayList<Map<String, String>>();
				}
				Map<String, String> map = new HashMap<String, String>();
				for(Iterator<Attribute> it = node.attributeIterator(); it.hasNext(); ) {
	                Attribute attribute = (Attribute) it.next();
	                map.put(attribute.getName(), attribute.getValue());
	            }
				list.add(map);
				//存入缓存
				if (null != list && list.isEmpty() == false) {
					weixincache_cmdid.put(key, list);
				}
			}
		} catch (DocumentException e) {
			e.printStackTrace();
		} finally {
			reader = null;
		}
	}
	
	/**
	 * 读取habase服务端IP列表
	 */
	@SuppressWarnings("unchecked")
	private void loadRoutes() {
		SAXReader reader = new SAXReader();
		try {
			Document doc = reader.read(new File(FileUtil.toFileName(path + "routes.xml")));
			Element root =(Element) doc.getRootElement().elementIterator("iptables").next();
			if (null == root) {
				return;
			}
			Iterator<Element> nodes = root.elementIterator();
			while(nodes.hasNext()) {
				Element node = nodes.next();
				int status = StringUtil.getInteger(node.attributeValue("status"));
				if (status == StatusEnum.EFFECTIVE.getValue()) {
					IpTableBean iptable = new IpTableBean();
					iptable.setName(node.attributeValue("name"));
					iptable.setAddress(node.attributeValue("addr"));
					iptable.setPort(StringUtil.getInteger(node.attributeValue("port")));
					iptable.setMaxthread(StringUtil.getInteger(node.attributeValue("maxthread")));
					iptable.setStatus(status);
					this.routesCache.put(iptable.getName(), iptable);
				}
			}
		} catch (DocumentException e) {
			e.printStackTrace();
		} finally {
			reader = null;
		}
	}
	
	/**
	 * 配置业务标识号与终端需要连接到哪个数据源
	 * @param dir
	 * @param configFile
	 */
	@SuppressWarnings("unchecked")
	private void loadCommandIdRouteCache() {
		SAXReader reader = new SAXReader();
		try {
			Document doc = reader.read(new File(FileUtil.toFileName(path + "routes.xml")));
			Element root =(Element) doc.getRootElement().elementIterator("commandids").next();
			if (null == root) {
				return;
			}
			Iterator<Element> nodes = root.elementIterator();
			while(nodes.hasNext()) {
				Element node = nodes.next();
				
				String iptableNames = "";
				for (String iptableName : node.attributeValue("iptable").split(SystemConfig.ROUTE_SEPATATE)) {
					if (this.routesCache.containsKey(iptableName)) { //判断iptable是否存在
						iptableNames += SystemConfig.ROUTE_SEPATATE + iptableName;
					}
				}
				if (!StringUtil.empty(iptableNames)) {
					String key = node.attributeValue("value") + SystemConfig.KEY_SEPATATE + node.attributeValue("sessionid");
					this.commandIdRouteCache.put(key, iptableNames.substring(1)); //去掉最前的分隔符
				}
			}
		} catch (DocumentException e) {
			e.printStackTrace();
		} finally {
			reader = null;
		}
	}
	
	/**
	 * 读取错误定义表
	 */
	@SuppressWarnings("unchecked")
	private void loadErrors() {
		SAXReader reader = new SAXReader();
		try {
			Document doc = reader.read(new File(FileUtil.toFileName(path + "errorDescriptions.xml")));
			Element root =doc.getRootElement();
			//ErrorCode="10" Description="系统错误" Response
			Iterator<Element> nodes = root.elementIterator("error");
			while(nodes.hasNext()) {
				Element node = nodes.next();
				ErrorDescBean tberror = new ErrorDescBean();
	        	tberror.setErrorCode(StringUtil.getInteger(node.attributeValue("ErrorCode")));
	        	tberror.setDescription(node.attributeValue("Description"));
	        	tberror.setResponse(node.attributeValue("Response"));
	        	this.errorsCache.put(tberror.getErrorCode(), tberror);
			}
		} catch (DocumentException e) {
			e.printStackTrace();
		} finally {
			reader = null;
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////	
	//微信缓存相关函数
	
	/**
	 * 获取微信配置缓存
	 * @return
	 */
	public EhCacheWrapper<String, WeixinConfig> WeixinCache() {
		if (null == weixincache) {
			weixincache = new EhCacheWrapper<String, WeixinConfig>("WeixinConfigCache", ehcachemanager.getManager());
		}
		return weixincache;
	}
	
	/**
	 * 获取微信业务代码配置缓存
	 * @return
	 */
	public EhCacheWrapper<String, List<Map<String, String>>> WeixinCacheCmdID() {
		if (null == weixincache_cmdid) {
			weixincache_cmdid = new EhCacheWrapper<String, List<Map<String, String>>>("WeixinCommandIDCache", ehcachemanager.getManager());
		}
		return weixincache_cmdid;
	}
	
	/***
	 * 微信地址签权口令
	 * @param key
	 * @return
	 */
	public String getToken(String key) {
		if (this.weixincache.isKeyInCache(key) == false) {
			return "";
		}
		else {
			WeixinConfig config = this.weixincache.get(key);
			return config.getToken();	
		}
	}
	
	/**
	 * 获取令牌
	 * @param key
	 * @return
	 */
	public String getAccessToken(String key) {
		if (this.weixincache.isKeyInCache(key) == false) {
			return "";
		}
		else {
			WeixinConfig config = this.weixincache.get(key);
			return config.getAccessToken().getToken();	
		}
	}
	
	/**
	 * 获取微信配置信息
	 * @return WEIXINCONFIG
	 */
	public WeixinConfig getWeixinConfig(String key) {
		WeixinConfig config = this.weixincache.get(key);
		return config;
	}
	
	/**
	 * 获取微信业务代码配置信息
	 * 不区分大小写，全字匹配
	 * @param key 区分公众号的key
	 * @param weixinMap 微信包
	 * @return
	 */
	public String getWeixinCommandid(String key, Map<String, String> weixinMap) {
		String result = "";
		List<Map<String, String>> maps = this.weixincache_cmdid.get(key);
		
		//获取commandid
		for (Map<String, String> map : maps) {
			//记录匹配
			if (map.get("msgtype").equalsIgnoreCase(weixinMap.get("MsgType"))) { //消息类型匹配
				String msgkey = map.get("msgkey");
				if (StringUtil.empty(msgkey)) { //如果没有msgkey进行匹配，说明是根据消息类型匹配
					result = map.get("commandid");
				}
				String lvalue = map.get(msgkey).toLowerCase(); 		//需要匹配的值
				String wvalue = weixinMap.get(msgkey).toLowerCase();//微信转入的值
				if (!StringUtil.empty(lvalue) && !StringUtil.empty(wvalue)) {//判断是否为精确匹配
					String _value = lvalue.replace("?", ""); //替换掉模湖匹配关键字
					if (wvalue.length() == lvalue.length() && wvalue.indexOf(_value) >= 0) { //微信转入的值是否包含设定的匹配值
						result = map.get("commandid");
						break;
					}
				}
			}
		}
		
		return result;
	}
	
	/**
	 * 从微信获取令牌
	 */
	public void fetchTokenFormWeixin() {
		for (Object key : this.weixincache.getCache().getKeys()) {
			WeixinConfig config = this.weixincache.get((String)key);
			AccessToken accesstoken = WeixinUtil.getAccessToken(config.getAppID(), config.getAppSecret());
			config.setAccessToken(accesstoken);
		}
	}
	
	/**
	 * 从微信获取令牌
	 */
	public void getTokenFormWeixin(Object key) {
		WeixinConfig config = this.weixincache.get((String)key);
		AccessToken accesstoken = WeixinUtil.getAccessToken(config.getAppID(), config.getAppSecret());
		config.setAccessToken(accesstoken);
	}
	
	///其它缓存
	
	/**
	 * 错误定义缓存
	 * @return
	 */
	public Map<Integer, ErrorDescBean> getErrorDescsCache() {
		return this.errorsCache;
	}

	/**
	 * habase群IP列表缓存
	 * @return
	 */
	public Map<String, IpTableBean> getRoutesCache() {
		return this.routesCache;
	}
	
	/**
	 * 获取业务标识号路由信息
	 * @return
	 */
	public Map<String, String> getCommandIdRouteCache() {
		return this.commandIdRouteCache;
	}
	
}
