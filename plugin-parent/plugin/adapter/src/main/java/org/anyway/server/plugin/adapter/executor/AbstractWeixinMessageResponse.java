/*
 * 名称: WeixinMessageResponse
 * 描述: 微信消息反馈
 * 版本：  1.0.0
 * 作者： 翁富家
 * 修改:
 * 日期：2015年05月28日
 */

package org.anyway.server.plugin.adapter.executor;

import org.anyway.common.protocol.request.HttpRequest;
import org.anyway.exceptions.NoCacheException;
import org.anyway.server.adapter.cache.CacheManager;
import org.anyway.server.plugin.adapter.AbstractTcpBusinessExecutor;
import org.anyway.server.plugin.adapter.utils.ResponseHelper;

public abstract class AbstractWeixinMessageResponse extends AbstractTcpBusinessExecutor {
	
	protected CacheManager manager = null;
	protected HttpRequest<String> httprequest = null;
	
	/**
	 * 执行业务
	 * 从http线程池中获取连接,并把消息分解成json后返回http消息
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void run() {
		try {
			this.manager = CacheManager.getInstance();
		} catch (NoCacheException e) {
			return;
		}
		//获取连接，返回消息
		String seq = this.getRequest().getCStream().GetSequence();
		httprequest = (HttpRequest<String>) this.manager.getRequestCache().doneCache().get(seq);
		if (null != httprequest) { //找到http连接
			String content = msgEncode();
			sendResponse(content);
			//从连接池中删除连接
			httprequest.close();
			this.manager.getRequestCache().removeDone(seq);
		}
		return;
	}

	/**
	 * 返回结果
	 * 
	 * @param content
	 */
	protected void sendResponse(String content) {
		//发送网络包
		ResponseHelper.writeResponse(content, httprequest.getContext(), httprequest.getRequest());
	}

	/**
	 * 消息转换成微信
	 * 需要被继承
	 * @return
	 */
	protected abstract String msgEncode();
	
}
