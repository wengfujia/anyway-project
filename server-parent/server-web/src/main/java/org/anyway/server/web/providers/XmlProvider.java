/*
 * 名称: XmlProvider
 * 描述: 读取xml数据库
 * 版本：  1.0.0
 * 作者： 翁富家
 * 修改:
 * 日期：2015年05月25日
 * 修改日期:
 */

package org.anyway.server.web.providers;

import java.io.File;
import java.util.Iterator;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import org.anyway.common.utils.uStringUtils;
import org.anyway.exceptions.NoCacheException;
import org.anyway.server.data.models.ErrorDescBean;
import org.anyway.server.data.models.IpTableBean;
import org.anyway.server.web.cache.CacheManager;
import org.anyway.server.web.models.CategoryBean;

public class XmlProvider implements Provider {

	private String path = "./data/";
	private CacheManager manager = null;
	
	protected XmlProvider() throws NoCacheException	{
		this.manager = CacheManager.getInstance();
	}
	
	/**
	 * 列取省
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void FillProvinceCategory() {
		SAXReader reader = new SAXReader();
		try {
			Document doc = reader.read(new File(path + "provinceCategory.xml"));
			Element root =doc.getRootElement();

			Iterator<Element> nodes = root.elementIterator("type");
			while(nodes.hasNext()) {
				Element node = nodes.next();
				CategoryBean cate = new CategoryBean();
				cate.setKey(uStringUtils.getInteger(node.attributeValue("id")));
				cate.setValue(node.attributeValue("value"));
				this.manager.getDbCache().ProvinceCache().put(cate.getKey(), cate);
			}
		} catch (DocumentException e) {
			e.printStackTrace();
		} finally {
			reader = null;
		}
	}

	/**
	 * 列取市
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void FillCityCategory() {
		SAXReader reader = new SAXReader();
		try {
			Document doc = reader.read(new File(path + "cityCategory.xml"));
			Element root =doc.getRootElement();
			Iterator<Element> nodes = root.elementIterator("province"); //读取省
			while(nodes.hasNext()) {
				Element node = nodes.next();
				int pid = uStringUtils.getInteger(node.attributeValue("id"));
				//读取市
				Iterator<Element> citynodes = root.elementIterator("type"); //读取市
				while(citynodes.hasNext()) {
					Element city = citynodes.next();
					int cid = pid+uStringUtils.getInteger(city.attributeValue("id")); //根据省id+市id，组成新的市id
					CategoryBean cate = new CategoryBean();
					cate.setKey(uStringUtils.getInteger(cid));
					cate.setValue(city.attributeValue("value"));
					this.manager.getDbCache().CityCache().put(cate.getKey(), cate);
				}
			}
		} catch (DocumentException e) {
			e.printStackTrace();
		} finally {
			reader = null;
		}
		
	}

	/**
	 * 列取区
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void FillDistrictCategory() {
		SAXReader reader = new SAXReader();
		try {
			Document doc = reader.read(new File(path + "districtCategory.xml"));
			Element root =doc.getRootElement();
			Iterator<Element> nodes = root.elementIterator("province"); //读取省
			while(nodes.hasNext()) {
				Element node = nodes.next();
				int pid = uStringUtils.getInteger(node.attributeValue("id"));
				//读取市
				Iterator<Element> citynodes = root.elementIterator("city"); //读取市
				while(citynodes.hasNext()) {
					Element city = citynodes.next();
					int cid = pid + uStringUtils.getInteger(city.attributeValue("id")); //根据省id+市id，组成新的市id
					//读取区
					Iterator<Element> districtnodes = root.elementIterator("type");
					while(districtnodes.hasNext()) {
						Element district = districtnodes.next();
						int did = cid + uStringUtils.getInteger(district.attributeValue("id")); //根据市id+区id，组成新的区id
						//放入缓存
						CategoryBean cate = new CategoryBean();
						cate.setKey(uStringUtils.getInteger(did));
						cate.setValue(district.attributeValue("value"));
						this.manager.getDbCache().DistrictCache().put(cate.getKey(), cate);
					}
				}
			}
		} catch (DocumentException e) {
			e.printStackTrace();
		} finally {
			reader = null;
		}
	}

	/**
	 * 列取学校分类
	 */
	@Override
	public void FillSchoolCategory() {
		SAXReader reader = new SAXReader();
		try {
			Document doc = reader.read(new File(path + "schoolCategory.xml"));
			Element root =doc.getRootElement();

			@SuppressWarnings("unchecked")
			Iterator<Element> nodes = root.elementIterator("type");
			while(nodes.hasNext()) {
				Element node = nodes.next();
				CategoryBean cate = new CategoryBean();
				cate.setKey(uStringUtils.getInteger(node.attributeValue("id")));
				cate.setValue(node.attributeValue("value"));
				this.manager.getDbCache().SchoolCache().put(cate.getKey(), cate);
			}
		} catch (DocumentException e) {
			e.printStackTrace();
		} finally {
			reader = null;
		}
	}

	/**
	 * 列取年级分类
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void FillGradeCategory() {
		SAXReader reader = new SAXReader();
		try {
			Document doc = reader.read(new File(path + "gradeCategory.xml"));
			Element root =doc.getRootElement();
			Iterator<Element> nodes = root.elementIterator("school"); //读取学校
			while(nodes.hasNext()) {
				Element node = nodes.next();
				int pid = uStringUtils.getInteger(node.attributeValue("id"));
				//读取市
				Iterator<Element> citynodes = root.elementIterator("type"); //读取年级
				while(citynodes.hasNext()) {
					Element city = citynodes.next();
					int cid = pid+uStringUtils.getInteger(city.attributeValue("id")); //根据学校id+年级id，组成新的年级id
					CategoryBean cate = new CategoryBean();
					cate.setKey(uStringUtils.getInteger(cid));
					cate.setValue(city.attributeValue("value"));
					this.manager.getDbCache().GradeCache().put(cate.getKey(), cate);
				}
			}
		} catch (DocumentException e) {
			e.printStackTrace();
		} finally {
			reader = null;
		}
	}

	/**
	 * 列取专业分类
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void FillSpecialtyCategory() {
		SAXReader reader = new SAXReader();
		try {
			Document doc = reader.read(new File(path + "specialtyCategory.xml"));
			Element root =doc.getRootElement();
			Iterator<Element> nodes = root.elementIterator("school"); //读取学校
			while(nodes.hasNext()) {
				Element node = nodes.next();
				int pid = uStringUtils.getInteger(node.attributeValue("id"));
				//读取市
				Iterator<Element> citynodes = root.elementIterator("type"); //读取专业
				while(citynodes.hasNext()) {
					Element city = citynodes.next();
					int cid = pid+uStringUtils.getInteger(city.attributeValue("id")); //根据学校id+专业id，组成新的专业id
					CategoryBean cate = new CategoryBean();
					cate.setKey(uStringUtils.getInteger(cid));
					cate.setValue(city.attributeValue("value"));
					this.manager.getDbCache().SpecialtyCache().put(cate.getKey(), cate);
				}
			}
		} catch (DocumentException e) {
			e.printStackTrace();
		} finally {
			reader = null;
		}
	}

	/**
	 * 列取成绩分类
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void FillCourseCategory() {
		SAXReader reader = new SAXReader();
		try {
			Document doc = reader.read(new File(path + "courseCategory.xml"));
			Element root =doc.getRootElement();
			Iterator<Element> nodes = root.elementIterator("school"); //读取学校
			while(nodes.hasNext()) {
				Element node = nodes.next();
				int pid = uStringUtils.getInteger(node.attributeValue("id"));
				//读取市
				Iterator<Element> citynodes = root.elementIterator("type"); //读取专业
				while(citynodes.hasNext()) {
					Element city = citynodes.next();
					int cid = pid+uStringUtils.getInteger(city.attributeValue("id")); //根据学校id+专业id，组成新的专业id
					CategoryBean cate = new CategoryBean();
					cate.setKey(uStringUtils.getInteger(cid));
					cate.setValue(city.attributeValue("value"));
					this.manager.getDbCache().SpecialtyCache().put(cate.getKey(), cate);
				}
			}
		} catch (DocumentException e) {
			e.printStackTrace();
		} finally {
			reader = null;
		}
	}

	/**
	 * 列取咨询分类
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void FillAdvisoryCategory() {
		SAXReader reader = new SAXReader();
		try {
			Document doc = reader.read(new File(path + "advisoryCategory.xml"));
			Element root =doc.getRootElement();
			Iterator<Element> nodes = root.elementIterator("type");
			while(nodes.hasNext()) {
				Element node = nodes.next();
				CategoryBean cate = new CategoryBean();
				cate.setKey(uStringUtils.getInteger(node.attributeValue("id")));
				cate.setValue(node.attributeValue("value"));
				this.manager.getDbCache().SchoolCache().put(cate.getKey(), cate);
			}
		} catch (DocumentException e) {
			e.printStackTrace();
		} finally {
			reader = null;
		}
	}

	/**
	 * 列取消息分类
	 */
	@Override
	public void FillMessageCategory() {
		SAXReader reader = new SAXReader();
		try {
			Document doc = reader.read(new File(path + "messageCategory.xml"));
			Element root =doc.getRootElement();

			@SuppressWarnings("unchecked")
			Iterator<Element> nodes = root.elementIterator("type");
			while(nodes.hasNext()) {
				Element node = nodes.next();
				CategoryBean cate = new CategoryBean();
				cate.setKey(uStringUtils.getInteger(node.attributeValue("id")));
				cate.setValue(node.attributeValue("value"));
				this.manager.getDbCache().MessageCache().put(cate.getKey(), cate);
			}
		} catch (DocumentException e) {
			e.printStackTrace();
		} finally {
			reader = null;
		}
	}
	
	/**
	 * 读取用户类型分类
	 */
	@Override
	public void FillUserTypeCategory() {
		SAXReader reader = new SAXReader();
		try {
			Document doc = reader.read(new File(path + "userTypeCategory.xml"));
			Element root =doc.getRootElement();

			@SuppressWarnings("unchecked")
			Iterator<Element> nodes = root.elementIterator("type");
			while(nodes.hasNext()) {
				Element node = nodes.next();
				CategoryBean cate = new CategoryBean();
				cate.setKey(uStringUtils.getInteger(node.attributeValue("id")));
				cate.setValue(node.attributeValue("value"));
				this.manager.getDbCache().UserTypeCache().put(cate.getKey(), cate);
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
	@Override
	public void FillErrors() {
		SAXReader reader = new SAXReader();
		try {
			Document doc = reader.read(new File(path + "errorDescriptions.xml"));
			Element root =doc.getRootElement();
			//ErrorCode="10" Description="系统错误" Response
			Iterator<Element> nodes = root.elementIterator("error");
			while(nodes.hasNext()) {
				Element node = nodes.next();
				ErrorDescBean tberror = new ErrorDescBean();
	        	tberror.setErrorCode(uStringUtils.getInteger(node.attributeValue("ErrorCode")));
	        	tberror.setDescription(node.attributeValue("Description"));
	        	tberror.setResponse(node.attributeValue("Response"));
	        	this.manager.getDbCache().ErrorDescsCache().put(tberror.getErrorCode(), tberror);
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
	@Override
	public void FillIpTables() {
		SAXReader reader = new SAXReader();
		try {
			Document doc = reader.read(new File(path + "ipTables.xml"));
			Element root =doc.getRootElement();
			Iterator<Element> nodes = root.elementIterator("iptable");
			while(nodes.hasNext()) {
				Element node = nodes.next();
				IpTableBean iptable = new IpTableBean();
				iptable.setAddress(node.attributeValue("addr"));
	        	iptable.setPort(uStringUtils.getInteger(node.attributeValue("port")));
	        	iptable.setMaxthread(uStringUtils.getInteger(node.attributeValue("maxthread")));
	        	iptable.setStatus(uStringUtils.getInteger(node.attributeValue("status")));
	        	String key = iptable.getAddress()+String.valueOf(iptable.getPort()); //ip+port做为key
	        	this.manager.getDbCache().IpTablesCache().put(key, iptable);
			}
		} catch (DocumentException e) {
			e.printStackTrace();
		} finally {
			reader = null;
		}
	}

	/**
	 * 读取关键字过滤列表
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void FillStopWords() {
		SAXReader reader = new SAXReader();
		try {
			Document doc = reader.read(new File(path + "stopWords.xml"));
			Element root =doc.getRootElement();
			Iterator<Element> nodes = root.elementIterator("stopword");
			while(nodes.hasNext()) {
				Element node = nodes.next();
				this.manager.getDbCache().StopWordsCache().add(node.attributeValue("word"));
			}
		} catch (DocumentException e) {
			e.printStackTrace();
		} finally {
			reader = null;
		}
	}

}
