/*
 * 名称: CMEventFactory
 * 描述: CacheManager监听事件工厂类，则该工厂类协调处理监听,此类不作具体实现。目前缓顾存个数固定
 * 版本：  1.0.0
 * 作者： 翁富家
 * 修改:
 * 日期：2014年6月16日
 * 修改日期:
 */

package org.anyway.cache.ehcache.event;

import java.util.Properties;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.event.CacheManagerEventListener;
import net.sf.ehcache.event.CacheManagerEventListenerFactory;

public class CMEventFactory extends CacheManagerEventListenerFactory {
	
	@Override
    public CacheManagerEventListener createCacheManagerEventListener(CacheManager cacheManager,
            Properties properties) {
        return new CMEvent();
    }
}
