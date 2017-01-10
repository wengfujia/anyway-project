/*
 * 名称: ConfigCache
 * 描述: 配置信息缓存
 * 版本：  1.0.0
 * 作者： 翁富家
 * 修改:
 * 日期：2015年05月28日
 */

package org.anyway.server.web.cache;

import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.anyway.common.uGlobalVar;
import org.anyway.common.utils.uStringUtil;
import org.anyway.exceptions.NoCacheException;
import org.anyway.server.web.common.uLoadVar;
import org.anyway.server.web.models.SeqSectionBean;
import org.anyway.server.web.packages.WEIXINCONFIG;
import org.anyway.cache.ehcache.EhCacheFactory;
import org.anyway.cache.ehcache.EhCacheWrapper;
import org.anyway.wechat.entity.AccessToken;
import org.anyway.wechat.util.WeixinUtil;

public class ConfigCache {
	private EhCacheFactory ehcachemanager = null;
	private volatile EhCacheWrapper<String, WEIXINCONFIG> weixincache; 	//微信配置信息缓存
	private volatile EhCacheWrapper<String, List<Map<String, String>>> weixincache_cmdid;	//微信转换成业务头的配置缓存
	
	private EhCacheWrapper<String, Integer> seqidcache;	//各递增序号缓存  <setctioname+code, seqid>
	private Map<String, SeqSectionBean> seqsection;	//序号允许的号段 <sectioname, SeqSectionBean>
	
	/**
	 * 构造函数
	 * @throws Exception 
	 */
	public ConfigCache(EhCacheFactory manager) throws NoCacheException {
		ehcachemanager = null;
		if (null != manager) {
			this.ehcachemanager = manager;
			if (uLoadVar.IsWeixinServer()) { //如果是微信服务端，则开启相关缓存
				weixincache = new EhCacheWrapper<String, WEIXINCONFIG>("WeixinConfigCache", ehcachemanager.getManager());
				weixincache_cmdid = new EhCacheWrapper<String, List<Map<String, String>>>("WeixinCommandIDCache", ehcachemanager.getManager());
				LoadWeixinConfig();
			}
			else {
				seqidcache = new EhCacheWrapper<String, Integer>("SequenceIdCache", ehcachemanager.getManager());			
				loadSeqSection();
				loadSeqidCache();
			}
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
	private void LoadWeixinConfig() {
		
		SAXReader reader = new SAXReader();
		try {
			Document doc = reader.read(new File("./cfg/weixin.xml"));
			Element root =doc.getRootElement();

			//获取微信token配置信息
			Iterator<Element> nodes = root.elementIterator("tokens");
			while(nodes.hasNext()) {
				Element node = nodes.next();
				WEIXINCONFIG config = new WEIXINCONFIG();
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
	 * 读取各递增序号的号段
	 */
	@SuppressWarnings("unchecked")
	public void loadSeqSection() {
		seqsection = new HashMap<String, SeqSectionBean>();
		SAXReader reader = new SAXReader();
		try {
			Document doc = reader.read(new File("./cfg/config.xml"));
			Element root = doc.getRootElement();
			Iterator<Element> nodes = root.elementIterator("sections");
			if (nodes.hasNext()) {
				Element node = nodes.next();
				Iterator<Element> childnodes = node.elementIterator("section");	
				while(childnodes.hasNext()) {
					Element childnode = childnodes.next();
					SeqSectionBean section = new SeqSectionBean();
					section.setSectionName(childnode.attributeValue("name"));
					section.setMinID(Integer.valueOf(childnode.attributeValue("minid")));
					section.setMaxID(Integer.valueOf(childnode.attributeValue("maxid")));
					section.setLength(Integer.valueOf(childnode.attributeValue("length")));
					seqsection.put(section.getSectionName(), section);	
				}
			}
		} catch (DocumentException e) {
			e.printStackTrace();
		} finally {
			reader = null;
		}
	}
	
	/**
	 * 加载自增序号缓存
	 */
	public void loadSeqidCache() {
		try {
			if (null != this.seqidcache) {
				// 开启指定的文件
				File file = new File(uGlobalVar.AppPath + "/data/seqid.txt");
				if (file.exists()) {
					BufferedReader read = new BufferedReader(new FileReader(file));
					String line = "";
					while ((line=read.readLine()) != null) {
						String[] values = line.split("\t");
						if (null != values && values.length == 2) {
							this.seqidcache.put(values[0], Integer.valueOf(values[1]));
						}
					}
					read.close();
				}
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 获取自增序号缓存
	 * @return
	 */
	public EhCacheWrapper<String, Integer> getSeqIdCache() {
		return this.seqidcache;
	}
	
////////////////////////////////////////////////////////////////////////////////////////////////////////////	
//微信缓存相关函数
	
	/**
	 * 获取微信配置缓存
	 * @return
	 */
	public EhCacheWrapper<String, WEIXINCONFIG> WeixinCache() {
		if (null == weixincache) {
			weixincache = new EhCacheWrapper<String, WEIXINCONFIG>("WeixinConfigCache", ehcachemanager.getManager());
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
			WEIXINCONFIG config = this.weixincache.get(key);
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
			WEIXINCONFIG config = this.weixincache.get(key);
			return config.getAccessToken().getToken();	
		}
	}
	
	/**
	 * 获取微信配置信息
	 * @return WEIXINCONFIG
	 */
	public WEIXINCONFIG getWeixinConfig(String key) {
		WEIXINCONFIG config = this.weixincache.get(key);
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
				if (uStringUtil.empty(msgkey)) { //如果没有msgkey进行匹配，说明是根据消息类型匹配
					result = map.get("commandid");
				}
				String lvalue = map.get(msgkey).toLowerCase(); 		//需要匹配的值
				String wvalue = weixinMap.get(msgkey).toLowerCase();//微信转入的值
				if (!uStringUtil.empty(lvalue) && !uStringUtil.empty(wvalue)) {//判断是否为精确匹配
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
			WEIXINCONFIG config = this.weixincache.get((String)key);
			AccessToken accesstoken = WeixinUtil.getAccessToken(config.getAppID(), config.getAppSecret());
			config.setAccessToken(accesstoken);
		}
	}
	
	/**
	 * 从微信获取令牌
	 */
	public void getTokenFormWeixin(Object key) {
		WEIXINCONFIG config = this.weixincache.get((String)key);
		AccessToken accesstoken = WeixinUtil.getAccessToken(config.getAppID(), config.getAppSecret());
		config.setAccessToken(accesstoken);
	}

///////////////////////////////////////////////////////////////////////////////////////////////////////////	
//其它缓存相关函数
	
	/**
	 * 获取序号
	 * @param sectioname 名称如：学校为shcool
	 * @param code 名称对应的编号如：学校的前7位编号
	 * @return
	 */
	public String getNextSequenceId(String sectioname, String code) {
		int SequenceId;
		String key = sectioname+code;
		SeqSectionBean section = this.seqsection.get(sectioname);
		if (this.seqidcache.isKeyInCache(key)) { //存在的序号进行递增
			SequenceId = this.seqidcache.get(key) + 1;
			//判断是否超出最大允许值
			if (SequenceId>section.getMaxID()*section.getLength()) {
				SequenceId = -1; //设置返回-1，表示值超出预设限定
			}
			else {
				//保存当前值
				this.seqidcache.replace(key, SequenceId);
			}
		}
		else { //如果不存在表示是新的序号	
			SequenceId = section.getMinID() * section.getLength();
			//保存到缓存
			this.seqidcache.put(key, SequenceId);
		}
		return String.valueOf(SequenceId);
	}

}
