/*
 * 名称: AbstractWebMessageResponse
 * 描述: web消息反馈
 * 版本：  1.0.0
 * 作者： 翁富家
 * 修改:
 * 日期：2015年05月28日
 */

package org.anyway.server.plugin.adapter.executor;

import java.util.StringTokenizer;

import org.anyway.common.SystemConfig;
import org.anyway.common.enums.CryptEnum;
import org.anyway.common.protocol.TcpMessageCoder;
import org.anyway.common.protocol.buffer.impl.http.HChrList;
import org.anyway.common.protocol.HttpMessageCoder;
import org.anyway.common.protocol.request.HttpRequest;
import org.anyway.common.utils.LoggerUtil;
import org.anyway.exceptions.NoCacheException;
import org.anyway.server.adapter.cache.CacheManager;
import org.anyway.server.plugin.adapter.AbstractTcpBusinessExecutor;
import org.anyway.server.plugin.adapter.utils.ResponseHelper;

public abstract class AbstractMessageResponse extends AbstractTcpBusinessExecutor {
	
	protected abstract void invoke(int status);
	
	/**
	 * 允许子类重写
	 * @param httprequest
	 * @return
	 */
	protected int execute(HttpRequest<String> httprequest) {
		return 0;
	}
	
	/**
	 * 执行业务
	 * 从http线程池中获取连接,并把消息分解成json后返回http消息
	 */
	@Override
	public final void run() {
		CacheManager cacheManager = null;
		try {
			cacheManager = CacheManager.getInstance();
		} catch (NoCacheException e) {
			LoggerUtil.getLogger().error("AbstractMessageResponse:{}", e);
			invoke(-23);
			return;
		}
		
		int status = -23;
		//获取连接，返回消息
		String seq = this.getRequest().getCStream().GetSequence();
		@SuppressWarnings("unchecked")
		HttpRequest<String> httprequest = (HttpRequest<String>) cacheManager.getRequestCache().doneCache().get(seq);
		if (null != httprequest) { //找到http连接
			status = execute(httprequest);
			if (status == 0) {
				boolean isLast = sendResponse(httprequest);
				if (isLast) {
					//从连接池中删除连接
					cacheManager.getRequestCache().removeDone(seq);
				}
			}
		}
		invoke(status);
		return;
	}
	
	/**
	 * 返回结果
	 * 是否是最后一个发送包  真表示最后一个包了
	 * @param content
	 */
	protected boolean sendResponse(HttpRequest<String> httprequest) {
		boolean isLast = true;
		//组合成http json
		TcpMessageCoder cstream = this.getRequest().getCStream();	
		try
		{
			String body = cstream.GetString();
			//分解包体，转换成list
			HChrList hlist = new HChrList();
			StringTokenizer strToke = new StringTokenizer(body, String.valueOf(SystemConfig.MSG_SEPATATE_LINE));
			while(strToke.hasMoreElements())
		    {
				//获取接收openid
				String line = strToke.nextToken(String.valueOf(SystemConfig.MSG_SEPATATE_LINE));
				hlist.Append(line);
	  		}
			//转换成json
			HttpMessageCoder hstream = new HttpMessageCoder();
			hstream.SetNr(hlist);
			hstream.EncodeHeader(cstream.getHeader());
			byte[] content = hstream.LoadFromStream(CryptEnum.NONE); //不进行加密
			//发送网络包
			ResponseHelper.writeResponse(content, httprequest.getContext(), httprequest.getRequest());
			isLast = cstream.IsLastPacket();
		}
		finally {
			cstream.ClearStream();
		}
		return isLast;

	}
}
