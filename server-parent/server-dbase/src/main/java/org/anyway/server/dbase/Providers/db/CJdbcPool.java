package org.anyway.server.dbase.Providers.db;

import java.util.Map;

import org.anyway.common.uConfigVar;
import org.anyway.server.dbase.common.uLoadVar;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.PoolProperties;
/*
 * 名称: CJdbcPool
 * 描述: 数据库连接池
 * 版本：  1.0.0
 * 作者： 翁富家
 * 修改:
 * 日期：2014年2月16日
 * 修改日期:
 */
public class CJdbcPool {
	public static DataSource datasource;
	/**
	 * 初始化数据连接池
	 * @return
	 */
	public static DataSource Initial() {
		
		Map<String,String> result = uLoadVar.LoadJdbc();
		PoolProperties p = new PoolProperties();
		p.setUrl(uConfigVar.SID);
		p.setDriverClassName(result.get("driverClassName"));
		p.setUsername(uConfigVar.UserID);
		p.setPassword(uConfigVar.Pwd);
		p.setJmxEnabled(Boolean.parseBoolean(result.get("jmxEnabled")));
		p.setTestWhileIdle(Boolean.parseBoolean(result.get("testWhileIdle")));
		p.setTestOnBorrow(Boolean.parseBoolean(result.get("testOnBorrow")));
		p.setTestOnConnect(Boolean.parseBoolean(result.get("testOnConnect")));
		p.setValidationQuery(result.get("validationQuery"));
		p.setTestOnReturn(Boolean.parseBoolean(result.get("testOnReturn")));	
		p.setValidationInterval(Integer.parseInt(result.get("validationInterval")));
		p.setTimeBetweenEvictionRunsMillis(Integer.parseInt(result.get("timeBetweenEvictionRunsMillis")));
		p.setMaxActive(Integer.parseInt(result.get("maxActive")));
		p.setInitialSize(Integer.parseInt(result.get("initialSize")));
		p.setMaxWait(Integer.parseInt(result.get("maxWait")));
		p.setRemoveAbandonedTimeout(Integer.parseInt(result.get("removeAbandonedTimeout")));
		p.setMinEvictableIdleTimeMillis(Integer.parseInt(result.get("minEvictableIdleTimeMillis")));
		p.setMaxIdle(Integer.parseInt(result.get("maxIdle")));
		p.setMinIdle(Integer.parseInt(result.get("minIdle")));
		p.setLogAbandoned(Boolean.parseBoolean(result.get("logAbandoned")));
		p.setRemoveAbandoned(Boolean.parseBoolean(result.get("removeAbandoned")));
		p.setJdbcInterceptors(result.get("jdbcInterceptors"));		
		datasource = new DataSource();
		datasource.setPoolProperties(p);
		return datasource;
	}
}
