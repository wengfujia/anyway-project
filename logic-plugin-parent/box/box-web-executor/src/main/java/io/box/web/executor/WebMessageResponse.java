/*
 * 名称: WebMessageResponse
 * 描述: web消息反馈
 * 版本：  1.0.0
 * 作者： 翁富家
 * 修改:
 * 日期：2015年05月28日
 */

package io.box.web.executor;

import java.util.StringTokenizer;

import org.anyway.common.uGlobalVar;
import org.anyway.common.enums.CryptEnum;
import org.anyway.exceptions.NoCacheException;
import org.anyway.server.api.CSHTMsgStream;
import org.anyway.server.api.HSHTMsgStream;
import org.anyway.server.data.http.HChrList;
import org.anyway.server.data.packages.HTTPREQUEST;
import org.anyway.server.web.cache.CacheManager;
import org.anyway.server.web.factory.TcpBusinessExecutorBase;
import org.anyway.server.web.http.handler.ResponseHandler;

public class WebMessageResponse extends TcpBusinessExecutorBase {
	
	protected CacheManager manager = null;
	protected HTTPREQUEST<String> httprequest = null;
	
	/**
	 * 执行业务
	 * 从http线程池中获取连接,并把消息分解成json后返回http消息
	 */
	@Override
	public void run() {
		try {
			this.manager = CacheManager.getInstance();
		} catch (NoCacheException e) {
			return;
		}
		
		//获取连接，返回消息
		String seq = this.getRequest().getCStream().GetSequence();
		httprequest = this.manager.getHttpCache().DoneCache().get(seq);
		if (null != httprequest) { //找到http连接
			boolean isLast = sendResponse();
			if (isLast) {
				//从连接池中删除连接
				this.manager.getHttpCache().removeDone(seq);
			}
		}
	}
	
	/**
	 * 返回结果
	 * 是否是最后一个发送包  真表示最后一个包了
	 * @param content
	 */
	protected boolean sendResponse() {
		boolean isLast = true;
		//组合成http json
		CSHTMsgStream cstream = this.getRequest().getCStream();	
		try
		{
			String body = cstream.GetString();
			//分解包体，转换成list
			HChrList hlist = new HChrList();
			StringTokenizer strToke = new StringTokenizer(body, String.valueOf(uGlobalVar.MSG_SEPATATE_LINE));
			while(strToke.hasMoreElements())
		    {
				//获取接收openid
				String line = strToke.nextToken(String.valueOf(uGlobalVar.MSG_SEPATATE_LINE));
				hlist.Append(line);
	  		}
			//转换成json
			HSHTMsgStream hstream = new HSHTMsgStream();
			hstream.SetNr(hlist);
			hstream.EncodeHeader(cstream.getHeader());
			byte[] content = hstream.LoadFromStream(CryptEnum.NONE); //不进行加密
			//发送网络包
			ResponseHandler.writeResponse(content, httprequest.getContext(), httprequest.getRequest());
			isLast = cstream.IsLastPacket();
		}
		finally {
			cstream.ClearStream();
		}
		return isLast;

	}
}
