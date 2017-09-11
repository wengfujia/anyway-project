package org.anyway.plugin.processor.pool;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.anyway.common.SystemConfig;
import org.anyway.common.utils.FileUtil;
import org.anyway.common.utils.StringUtil;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

/*
 * 名称: CJdbcPool
 * 描述: 数据库连接池
 * 版本：  1.0.0
 * 作者： 翁富家
 * 修改:
 * 日期：2014年2月16日
 * 修改日期:2017.7.16
 * 	修改为：HikariCP连接池，使用多个数据源进行配置
 */
public class DataSourcePool {
	
	/**
	 * key为datasource名称
	 */
	private Map<String, DataSource> sources = null;
	/**
	 * key为commandid + "@" + sessionid;
	 * value为datasource的name
	 */
	private Map<String, String> commandidRoutes = null;
	
	private static DataSourcePool instance = null;
	
	public static DataSourcePool getInstance() {
		if (null == instance) {
			synchronized (DataSourcePool.class) {
				if (null == instance) {
					instance = new DataSourcePool();
					instance.sources = new HashMap<String, DataSource>();
					instance.commandidRoutes = new HashMap<String, String>();
				}
			}
		}
		return instance;
	}
	
	/**
	 * 获取所有可用数据源
	 * @return
	 */
	public Map<String, DataSource> getDataSources() {
		return this.sources;
	}
	
	/**
	 * 获取数据源
	 * @param name
	 * @return
	 */
	public DataSource getDataSource(String name) {
		return this.sources.get(name);
	}
	
	/**
	 * 获取数据源名称
	 * @param sessionid
	 * @param commandid
	 * @return
	 */
	public String getDataSourceName(String sessionid, Integer commandid) {
		String key = commandid + SystemConfig.KEY_SEPATATE +sessionid;
		String dataName = this.commandidRoutes.get(key);
		if (StringUtil.empty(dataName)) {
			for (Map.Entry<String, DataSource> entry : this.sources.entrySet()) {
				if (entry.getValue().isdefault) {
					return entry.getValue().getName();
				}
			}
		}
		return dataName;
	}
	
	/**
	 * 获取连接池
	 * @param name
	 * @return
	 * @throws SQLException 
	 */
	public Connection getConnection(String name) throws SQLException {
		return this.sources.get(name).getDataSource().getConnection();
	}
	
	/**
	 * 根据sessionid与commandid获取连接池
	 * @param sessionid
	 * @param commandid
	 * @return
	 * @throws SQLException
	 */
	public Connection getConnection(String sessionid, Integer commandid) throws SQLException {
		String dataName = getDataSourceName(sessionid, commandid);
		DataSource dataSource = this.sources.get(dataName);
		return null == dataSource ? null : dataSource.getDataSource().getConnection();
	}
	
	/**
	 * 配置业务标识号与终端需要连接到哪个数据源
	 * @param dir
	 * @param configFile
	 */
	@SuppressWarnings("unchecked")
	private void loadCommandids(String configFile) {
		SAXReader reader = new SAXReader();
		try {
			Document doc = reader.read(new File(configFile));
			Element root =(Element) doc.getRootElement().elementIterator("commandids").next();
			if (null == root) {
				return;
			}
			Iterator<Element> nodes = root.elementIterator();
			while(nodes.hasNext()) {
				Element node = nodes.next();
				String key = node.attributeValue("value") + SystemConfig.KEY_SEPATATE + node.attributeValue("sessionid");
				this.commandidRoutes.put(key, node.attributeValue("datasource"));
			}
		} catch (DocumentException e) {
			e.printStackTrace();
		} finally {
			reader = null;
		}
	}
	
	/**
	 * 获取配置信息
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private void loadDataSourceConfig(String configFile) {
		SAXReader reader = new SAXReader();
		try {
			Document doc = reader.read(new File(configFile));
			Element root =(Element) doc.getRootElement().elementIterator("datasources").next();
			if (null == root) {
				return;
			}
			Iterator<Element> nodes = root.elementIterator();
			while(nodes.hasNext()) {
				Element node = nodes.next();
				if (StringUtil.getInteger(node.attributeValue("status")) == 1) {
					DataSource dataSource = new DataSource(node.attributeValue("name"), node.attributeValue("properties"),
							node.attributeValue("default"));
					HikariConfig config = new HikariConfig(FileUtil.toFileName(dataSource.getProperties()));
					dataSource.setDataSource(new HikariDataSource(config));
					
					sources.put(dataSource.getName(), dataSource);
				}
			}
		} catch (DocumentException e) {
			e.printStackTrace();
		} finally {
			reader = null;
		}
	}
	
	/**
	 * 初始化数据连接池
	 * @return
	 */
	public void Initial() {
		String configFile = FileUtil.toFileName("./cfg/dataSourceConfig.xml");
		
		loadDataSourceConfig(configFile);
		loadCommandids(configFile);
	}
	
	public class DataSource {
		private String name;
		private String properties;
		private boolean isdefault;
		private HikariDataSource dataSource;
		
		public DataSource(String name, String properties, String isdefault) {
			this.name = name;
			this.properties = properties;
			this.isdefault = "1".equals(isdefault) ? true : false;
		}
		
		public String getName() {
			return this.name;
		}
		public void setName(String name) {
			this.name = name;
		}
		
		public String getProperties() {
			return this.properties;
		}
		public void setProperties(String properties) {
			this.properties = properties;
		}
		
		public boolean getDefault() {
			return this.isdefault;
		}
		public void setName(Boolean isdefault) {
			this.isdefault = isdefault;
		}
		
		public HikariDataSource getDataSource() {
			return this.dataSource;
		}
		public void setDataSource(HikariDataSource dataSource) {
			this.dataSource = dataSource;
		}
		
	}
	
}
