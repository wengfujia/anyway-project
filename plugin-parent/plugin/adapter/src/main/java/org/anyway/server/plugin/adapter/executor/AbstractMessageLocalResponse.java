/*
 * 名称: AbstractWebMessageLocalResponse
 * 描述: WEB消息本地业务直接反馈
 * 版本：  1.0.0
 * 作者： 翁富家
 * 修改:
 * 日期：2017年04月03日
 */

package org.anyway.server.plugin.adapter.executor;

import org.anyway.exceptions.NoCacheException;
import org.anyway.server.adapter.cache.CacheManager;
import org.anyway.server.plugin.adapter.AbstractHttpBusinessExecutor;

public abstract class AbstractMessageLocalResponse extends AbstractHttpBusinessExecutor {
	
	/**
	 * 执行业务
	 * 重写msgEncode，获取需要返回WEB的内容
	 */
	@Override
	public void run() {
		CacheManager cacheManager = null;
		try {
			cacheManager = CacheManager.getInstance();
		} catch (NoCacheException e) {
			invoke(-23);
			return;
		}

		int status = -23;
		if (null != this.getRequest()) { //找到http连接
			status = sendResponse(msgEncode());
			if (status == 0) {
				cacheManager.getRequestCache().removeDone(this.getRequest().getID());
			}
		}
		invoke(status);
		return;
	}

	/**
	 * 回调
	 * @param status
	 */
	protected abstract void invoke(int status);
	
	/**
	 * 消息转换成WEB返回包
	 * 需要被继承
	 * @return
	 */
	protected abstract String msgEncode();
	
}
