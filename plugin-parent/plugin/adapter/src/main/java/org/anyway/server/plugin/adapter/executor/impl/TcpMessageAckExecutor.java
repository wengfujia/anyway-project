/*
 * 名称: TcpMessageAckExecutor
 * 描述: TCP消息转换应签处理器，
 * 说明: 
 * 		处理web消息传输到hbase服务层的应答消息
 * 		应答消息头为源消息头的负数
 * 版本：  1.0.0
 * 作者： 翁富家
 * 修改:
 * 日期：2017年07月29日
 */

package org.anyway.server.plugin.adapter.executor.impl;

import org.anyway.common.annotation.MessageAnnotation;
import org.anyway.common.protocol.header.CommandID;
import org.anyway.common.protocol.request.BaseRequest;
import org.anyway.exceptions.NoCacheException;
import org.anyway.server.adapter.cache.CacheManager;
import org.anyway.server.plugin.adapter.AbstractTcpBusinessExecutor;

@MessageAnnotation(msgType = -CommandID.TCP_REQUEST)
public class TcpMessageAckExecutor extends AbstractTcpBusinessExecutor {
	
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
		BaseRequest request = this.manager.getRequestCache().doneCache().get(seq);
		request.setDone();
		this.manager.getRequestCache().doneCache().replace(seq, request);
		return ;
	}
	
}
