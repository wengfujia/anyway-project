/**
 * 
 */
package org.anyway.server.adapter.moniter;

import java.util.List;

import org.anyway.common.AdapterConfig;
import org.anyway.common.SystemConfig;
import org.anyway.common.protocol.request.BaseRequest;
import org.anyway.common.protocol.request.HttpRequest;
import org.anyway.common.protocol.request.TcpRequest;
import org.anyway.common.utils.LoggerUtil;
import org.anyway.server.adapter.cache.CacheManager;
import org.anyway.server.plugin.adapter.dispatcher.Dispatcher;

import net.sf.ehcache.search.Result;
import net.sf.ehcache.search.Results;

/*
 * 名称: RequestMoniter
 * 描述: 请求超时重发与清理
 * 版本：  1.0.0
 * 作者： 翁富家
 * 日期：2017年09月02日
 * 
 */

public class RequestMoniter implements Runnable {

	@Override
	public void run() {
		while (true) {
			try {
				Thread.sleep(1000);
				CacheManager manager = CacheManager.getInstance();
				Results results = manager.getRequestCache().queryTimeOut(AdapterConfig.getInstance().getHTIdleTimeOut());
				List<Result> resultList = results.all();
				if (resultList != null && !resultList.isEmpty()) {
					for (Result result : resultList) {
						BaseRequest request = (BaseRequest) result.getValue();
						if (request.getRetry() >= SystemConfig.RETRY || !request.getContext().channel().isActive()) {
							// 从处理线程池中移除
							manager.getRequestCache().removeDone(request.getID());
							continue;
						}

						LoggerUtil.printInfo("[cleantask]retry to execute the request");
						int status = 0;
						request.setIpTable(null);
						if (request instanceof HttpRequest<?>) {
							status = Dispatcher.submit(((HttpRequest<?>) request), ((HttpRequest<?>) request).getMsgType());
						} else if (request instanceof TcpRequest) {
							status = Dispatcher.submit(((TcpRequest) request), ((TcpRequest) request).getMsgType());
						}
						if (status == 0) {
							// 增加重试次数
							request.incRetry();
							// 设置为等待状态
							request.setWait();
						}
					}
				}
			} catch (Exception e) {
				LoggerUtil.getLogger().error("RequestMoniter,Exception：", e);
			}
		}
	}
	
}
