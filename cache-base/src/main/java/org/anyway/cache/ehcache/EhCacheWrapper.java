/*
 * 名称: EhCacheWrapper
 * 描述: ehcache存取接口类
 * 版本：  1.0.0
 * 作者： 翁富家
 * 修改:
 * 日期：2014年6月12日
 * 修改日期:
 */

package org.anyway.cache.ehcache;

import org.anyway.cache.ehcache.impl.EhCacheBaseWrapper;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;

public class EhCacheWrapper<K, V> implements EhCacheBaseWrapper<K, V>
{
	private final String cacheName;
    private final CacheManager cacheManager;

    private Ehcache cache = null;
    
    public EhCacheWrapper(final String cacheName, final CacheManager cacheManager)
    {
        this.cacheName = cacheName;
        this.cacheManager = cacheManager;
        cache = cacheManager.getEhcache(cacheName);
    }

    /**
     * 保存元素
     */
    @Override
	public void put(final K key, final V value)
    {
    	if (cache != null)
    		cache.put(new Element(key, value)); 
    }
    
    /**
     * 保存元素并写到磁盘
     */
    @Override
	public void putWithFlush(final K key, final V value)
    {
    	if (cache != null) {
    		cache.put(new Element(key, value)); 
    		cache.flush();
    	}
    }
    
    
    /**
     * 替换元素
     * @param element
     */
    public void replace(Element element)
    {
    	if (cache != null)
    		cache.replace(element);
    }
    
    /**
     * 替换元素
     */
    @Override
    public void replace(final K key, final V value)
    {
    	if (cache != null)
    		cache.replace(new Element(key, value)); 
    }
    
    /**
     * 替换元素并写到磁盘
     */
    @Override
    public void replaceWithFlush(final K key, final V value)
    {
    	if (cache != null) {
    		cache.replace(new Element(key, value));
    		cache.flush();
    	}
    }
    
    /**
     * 获取元素
     */
	@Override
	@SuppressWarnings({ "unchecked", "deprecation" })
	public V get(final K key) 
    {
    	if (cache == null)
    		return null;
    	
        Element element = getCache().get(key);
        if (element != null) {
        	if (element.isSerializable())
        		return (V) element.getValue();
        	else
        		return (V) element.getObjectValue();
        }
        return null;
    }
    
	/**
	 * 移除元素，触发remove事件
	 * @param key
	 * @return
	 */
	@Override
	public boolean remove(final K key)
	{
		if (cache == null)
    		return false;
		
		return cache.remove(key);
	}
	
	/**
	 * 移除元素，但是不触发remove事件
	 * @param key
	 * @return
	 */
	@Override
	public boolean removeQuiet(final K key)
	{
		if (cache == null)
    		return false;
		
		return cache.removeQuiet(key);
	}
	
	/**
	 * 是否存在键
	 * @param key
	 * @return
	 */
	@Override
	public boolean isKeyInCache(final K key)
	{
		if (cache == null)
    		return false;
		
		return cache.isKeyInCache(key);
	}
	
	/**
	 * 是否存在值
	 * @param value
	 * @return
	 */
	public boolean isValueInCache(final Object value)
	{
		if (cache == null)
    		return false;
		
		return cache.isValueInCache(value);
	}
	
	/**
	 * 获取缓存
	 * @return
	 */
    public Ehcache getCache() 
    {    	
    	if (cache == null)
    		cache = cacheManager.getEhcache(cacheName);
    	return cache;
    }
}
