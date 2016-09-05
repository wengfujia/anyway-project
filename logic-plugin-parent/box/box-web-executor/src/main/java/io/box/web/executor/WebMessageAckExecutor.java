/*
 * 名称: WebMessageAckExecutor
 * 描述: web消息转换应签处理器，
 * 说明: 
 * 		处理web消息传输到hbase服务层的应答消息
 * 		应答消息头为源消息头的负数
 * 版本：  1.0.0
 * 作者： 翁富家
 * 修改:
 * 日期：2015年05月28日
 */

package io.box.web.executor;

import org.anyway.exceptions.NoCacheException;
import org.anyway.server.web.cache.CacheManager;
import org.anyway.server.web.factory.TcpBusinessExecutorBase;
import org.anyway.server.data.packages.COMMANDID;
import org.anyway.server.data.packages.HTTPREQUEST;
import org.anyway.common.MessageAnnotation;

@MessageAnnotation(msgType = -COMMANDID.WEB_REQUEST)
public class WebMessageAckExecutor extends TcpBusinessExecutorBase {
	
	private CacheManager manager = null;
	
	/**
	 * 执行应签业务
	 * 目前主要用于处理消息状态的更改
	 * 1、获取消息标识唯一序号
	 * 2、设置状态
	 * 
	 */
	@Override
	public void run() {
		try {
			this.manager = CacheManager.getInstance();
		} catch (NoCacheException e) {
			return;
		}
		//设置状态为等待应答，放入已传输缓存 
		String seq = this.getRequest().getCStream().GetSequence();
		HTTPREQUEST<String> request = this.manager.getHttpCache().DoneCache().get(seq);
		request.setDone();
		this.manager.getHttpCache().DoneCache().replace(seq, request);
	}
}
