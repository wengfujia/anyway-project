/*
 * 名称: HttpHandler
 * 描述: 微信http接收与解析类
 * 版本：  1.0.0
 * 作者： 翁富家
 * 修改:
 * 日期：2015年05月18日
 * 修改日期:
 * 
 * 2016.9.8
 * messageReceived函数中的Dispatcher.submit改成：Dispatcher.execute
 */

package org.anyway.server.web.http.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpObject;
import io.netty.util.CharsetUtil;

import org.anyway.common.uConfigVar;
import org.anyway.common.utils.uLogger;
import org.anyway.common.utils.uSecretUtil;
import org.anyway.server.data.packages.COMMANDID;
import org.anyway.server.data.packages.HTTPREQUEST;
import org.anyway.server.data.packages.json.JBuffer;
import org.anyway.server.web.cache.CacheManager;
import org.anyway.server.web.common.uLoadVar;
import org.anyway.server.web.dispatcher.Dispatcher;

public class HttpHandler extends SimpleChannelInboundHandler<HttpObject> {

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
    	ctx.close();
    }
  
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    	uLogger.println(cause.getMessage());
        ctx.close();
    }
  
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception { 
    	if (msg instanceof FullHttpRequest) {
    		messageReceived(ctx, msg);
        }
    	else { //关闭连接
    		ctx.close();
    	}
    }

    /***
     * HTTP包接收并分解
     * @param ctx
     * @param msg
     * @throws Exception
     */
    public void messageReceived(ChannelHandlerContext ctx, HttpObject msg) throws Exception {
    	// 获取request
    	FullHttpRequest fullRequest = (FullHttpRequest) msg;
    	if (fullRequest.method().equals(HttpMethod.POST)) {
	    	if (ctx.channel().isActive() == false) {
	    		ctx.close();
	    		fullRequest = null;
	    		return;
	        }
	        else if (fullRequest.content().capacity() == 0) {
	        	ctx.close();
	        	fullRequest = null;
	        	return;
	        }
	    	uLogger.println("[http]connect is closed!");
    	}
    	
    	//分解消息
    	String content = fullRequest.content().toString(CharsetUtil.UTF_8);
    	
		JBuffer<String> LoginBuf = new JBuffer<String>();
		if (uLoadVar.IsWeixinServer()) { //微信接入			
			LoginBuf.setCommandId(COMMANDID.WEIXIN_REQUEST);
			uLogger.println("[http]weixin is comein!");
		}
		else { //web接入
			LoginBuf.setCommandId(COMMANDID.WEB_REQUEST);
			if (uConfigVar.HT_Crypt == 1) { //启用加密，需要解密
				content = uSecretUtil.Decrypt3Des(content);
	    	}
			uLogger.println("[http]web is comein!");
		}
		LoginBuf.setBody(content);
		
		//保存到已处理缓存
		HTTPREQUEST<String> request = new HTTPREQUEST<String>();
		request.setJBody(LoginBuf);
		request.setDecoderResult(fullRequest.decoderResult());
		request.setRequest(fullRequest);
		request.setContext(ctx);
		request.setUri(fullRequest.uri());
		request.setHttpMethod(fullRequest.method());
		request.setWait(); 
		CacheManager.getInstance().getHttpCache().addDone(request);
		//业务处理
		int status = Dispatcher.<HTTPREQUEST<String>>submit(request, request.getJBody().getCommandId());
		uLogger.println("[http]final status=%s", status);
    }
   
}
