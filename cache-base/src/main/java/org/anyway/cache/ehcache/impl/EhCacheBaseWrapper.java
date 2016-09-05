/*
 * 名称: EhCacheBaseWrapper
 * 描述: ehcache存取接口类
 * 版本：  1.0.0
 * 作者： 翁富家
 * 修改:
 * 日期：2014年6月12日
 * 修改日期:
 */

package org.anyway.cache.ehcache.impl;

public interface EhCacheBaseWrapper<K, V> {

	void put(K key, V value);

	void putWithFlush(final K key, final V value);

	void replace(final K key, final V value);

	void replaceWithFlush(final K key, final V value);

	V get(K key);

	boolean remove(K key);

	boolean removeQuiet(final K key);

	boolean isKeyInCache(final K key);
}
