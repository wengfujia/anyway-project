/*
 * 名称: WeixinMessageResponse
 * 描述: 微信消息反馈
 * 版本：  1.0.0
 * 作者： 翁富家
 * 修改:
 * 日期：2015年05月28日
 */

package io.box.weixin.executor.impl;

import org.anyway.exceptions.NoCacheException;
import org.anyway.server.data.packages.HTTPREQUEST;
import org.anyway.server.web.cache.CacheManager;
import org.anyway.server.web.factory.TcpBusinessExecutorBase;
import org.anyway.server.web.http.handler.ResponseHandler;

public class WeixinMessageResponse extends TcpBusinessExecutorBase {
	
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
			String content = msgEncode();
			sendResponse(content);
			//从连接池中删除连接
			httprequest.Close();
			this.manager.getHttpCache().removeDone(seq);
		}
	}

	/**
	 * 返回结果
	 * 
	 * @param content
	 */
	protected void sendResponse(String content) {
		//发送网络包
		ResponseHandler.writeResponse(content, httprequest.getContext(), httprequest.getRequest());
		
//		byte[] result = null;
//		try {
//			result = uNetUtils.getBytes(content, uGlobalVar.CharsetName);
//		} catch (UnsupportedEncodingException e) {
//			return;
//		}
//		//发送网络包
//		boolean isKeepAlive = new ResponseHandler().writeResponse(result, httprequest.getContext(),
//				httprequest.getIsKeepAlive(), httprequest.getDecoderResult());
//		// If keep-alive is off, close the connection once the content is fully
//		// written.
//		if (!isKeepAlive) {
//			httprequest.getContext().writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
//		}
	}

	/**
	 * 消息转换成微信
	 * 需要被继承
	 * @return
	 */
	protected String msgEncode() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
