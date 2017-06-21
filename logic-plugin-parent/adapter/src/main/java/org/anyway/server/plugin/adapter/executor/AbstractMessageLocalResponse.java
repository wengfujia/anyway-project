/*
 * 名称: AbstractWebMessageLocalResponse
 * 描述: WEB消息本地业务直接反馈
 * 版本：  1.0.0
 * 作者： 翁富家
 * 修改:
 * 日期：2017年04月03日
 */

package io.box.web.executor;

import org.anyway.exceptions.NoCacheException;
import org.anyway.server.web.cache.CacheManager;
import org.anyway.server.web.factory.HttpBusinessExecutorBase;

public abstract class AbstractWebMessageLocalResponse extends HttpBusinessExecutorBase {
	
	protected CacheManager manager = null;

	/**
	 * 执行业务
	 * 重写msgEncode，获取需要返回WEB的内容
	 */
	@Override
	public Integer call() {
		try {
			this.manager = CacheManager.getInstance();
		} catch (NoCacheException e) {
			return -23;
		}

		int status = -23;
		if (null != this.getRequest()) { //找到http连接
			status = sendResponse(msgEncode());
		}
		return status;
	}

	/**
	 * 消息转换成WEB返回包
	 * 需要被继承
	 * @return
	 */
	protected abstract String msgEncode();
	
}
