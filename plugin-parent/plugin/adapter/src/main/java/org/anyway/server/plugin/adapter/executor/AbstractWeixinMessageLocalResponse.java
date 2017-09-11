/*
 * 名称: WeixinMessageLocalResponse
 * 描述: 微信消息本地业务直接反馈
 * 版本：  1.0.0
 * 作者： 翁富家
 * 修改:
 * 日期：2015年05月28日
 */

package org.anyway.server.plugin.adapter.executor;

import org.anyway.exceptions.NoCacheException;
import org.anyway.server.adapter.cache.CacheManager;
import org.anyway.server.plugin.adapter.AbstractHttpBusinessExecutor;

public abstract class AbstractWeixinMessageLocalResponse extends AbstractHttpBusinessExecutor {
	
	protected CacheManager manager = null;

	/**
	 * 执行业务
	 * 重写msgEncode，获取需要返回微信的内容
	 */
	@Override
	public void run() {
		try {
			this.manager = CacheManager.getInstance();
		} catch (NoCacheException e) {
			return;
		}

		if (null != this.getRequest()) { //找到http连接
			sendResponse(msgEncode());
		}
		else {
			return;
		}
	}

	/**
	 * 消息转换成微信
	 * 需要被继承
	 * @return
	 */
	protected abstract String msgEncode();
	
}
