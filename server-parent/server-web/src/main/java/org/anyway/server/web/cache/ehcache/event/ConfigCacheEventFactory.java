package org.anyway.server.web.cache.ehcache.event;

/*
 * 名称: CacheEventListener
 * 描述: ConfigCacheEvent监听事件工厂类，则该工厂类协调处理监听
 * 版本：  1.0.0
 * 作者： 翁富家
 * 修改:
 * 日期：2015年6月4日
 * 修改日期:
 */
import java.util.Properties;

import net.sf.ehcache.event.CacheEventListener;
import net.sf.ehcache.event.CacheEventListenerFactory;

public class ConfigCacheEventFactory extends CacheEventListenerFactory {

	@Override
    public CacheEventListener createCacheEventListener(Properties properties) {
        return new ConfigCacheEvent();
    }
}
