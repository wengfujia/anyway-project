/*
 * 名称: WeixinMessageLocalResponse
 * 描述: 微信消息本地业务直接反馈
 * 版本：  1.0.0
 * 作者： 翁富家
 * 修改:
 * 日期：2015年05月28日
 */

package io.box.weixin.executor.impl;

import org.anyway.exceptions.NoCacheException;
import org.anyway.server.web.cache.CacheManager;
import org.anyway.server.web.factory.HttpBusinessExecutorBase;
import org.anyway.server.web.http.handler.ResponseHandler;

public class WeixinMessageLocalResponse extends HttpBusinessExecutorBase {
	
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
			String content = msgEncode();
			sendResponse(content);
		}
	}

	/**
	 * 返回结果
	 * 
	 * @param content
	 */
	protected void sendResponse(String content) {
		//发送网络包
		ResponseHandler.writeResponse(content, this.getRequest().getContext(), this.getRequest().getRequest());
//		byte[] result = null;
//		try {
//			result = uNetUtils.getBytes(content, uGlobalVar.CharsetName);
//		} catch (UnsupportedEncodingException e) {
//			return;
//		}
//		//发送网络包
//		boolean isKeepAlive = new ResponseHandler().writeResponse(result, this.getRequest().getContext(),
//				this.getRequest().getIsKeepAlive(), this.getRequest().getDecoderResult());
//		// If keep-alive is off, close the connection once the content is fully
//		// written.
//		if (!isKeepAlive) {
//			this.getRequest().getContext().writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
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
