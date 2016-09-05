package org.anyway.server.web.http.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.CharsetUtil;

public class ResponseHandler {
	
     /**
     * http返回响应数据
     *
     * @param channel
     */
	@SuppressWarnings("deprecation")
	public static <T> boolean writeResponse(T content, ChannelHandlerContext ctx, FullHttpRequest fullHttpRequest) {
    	if (ctx.channel().isWritable() == false) {
        	return false;
        }
        // Convert the response content to a ChannelBuffer.
        ByteBuf sbuffer = null;
        if (content instanceof String) {
			sbuffer = Unpooled.copiedBuffer((String)content, CharsetUtil.UTF_8);
		}
		else if (content instanceof byte[]) {
			sbuffer = Unpooled.copiedBuffer((byte[])content);
		}
        
//      byte[] buffer = null;
//		try {
//			buffer = uNetUtils.getBytes(content, uGlobalVar.CharsetName);
//		} catch (UnsupportedEncodingException e) {
//			uLogger.printInfo(e.getMessage());
//			return false;
//		}
//		//生成发送缓存
//        ByteBuf sbuffer = ctx.alloc().buffer();
//        sbuffer.capacity(buffer.length);
//        sbuffer.writeBytes(buffer);
 
        // Decide whether to close the connection or not.
        boolean close = fullHttpRequest.headers().contains(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.CLOSE, true)
                || fullHttpRequest.getProtocolVersion().equals(HttpVersion.HTTP_1_0)
                && !fullHttpRequest.headers().contains(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.KEEP_ALIVE, true);
  
        // Build the response object.
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, sbuffer);
        //response.headers().set(HttpHeaders.Names.CONTENT_TYPE, "text/plain; charset=UTF-8");
        response.headers().set(HttpHeaders.Names.CONTENT_TYPE, "application/json; charset=UTF-8");

        if (!close) {
            // There's no need to add 'Content-Length' header
            // if this is the last response.
            response.headers().set(HttpHeaders.Names.CONTENT_LENGTH, sbuffer.readableBytes());
        }
        
        // Write the response.
        ChannelFuture future = ctx.writeAndFlush(response);
        // Close the connection after the write operation is done if necessary.
        if (close) {
            future.addListener(ChannelFutureListener.CLOSE);
        }
        return true;
    }

    /**
     * http返回响应数据
     * @param <T>
     * @param content
     * @param ctx
     * @param keepAlive
     * @param decoderResult
     * @return
     */
//	public <T> boolean writeResponse(T content, ChannelHandlerContext ctx, Boolean keepAlive, DecoderResult decoderResult) {
//		if (ctx.channel().isWritable() == false) {
//        	return false;
//        }
//
//        byte[] buffer = null;
//		try {
//			if (content instanceof String) {
//				buffer = uNetUtils.getBytes((String)content, uGlobalVar.CharsetName);
//			}
//			else if (content instanceof byte[]) {
//				buffer = (byte[])content;
//			}
//		} catch (UnsupportedEncodingException e) {
//			uLogger.printInfo(e.getMessage());
//			return false;
//		}
//
//		//生成发送缓存
//        ByteBuf sbuffer = ctx.alloc().buffer();
//        sbuffer.capacity(buffer.length);
//        sbuffer.writeBytes(buffer);
//		FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
//				decoderResult.isSuccess() ? HttpResponseStatus.OK : HttpResponseStatus.BAD_REQUEST,
//                		sbuffer);
//		
//        response.headers().set(HttpHeaders.Names.CONTENT_TYPE, "text/plain; charset=UTF-8");
//        if (keepAlive) {
//            response.headers().set(HttpHeaders.Names.CONTENT_LENGTH, response.content().readableBytes());
//            response.headers().set(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
//        }
//        ctx.writeAndFlush(response);
//        return keepAlive;
//    }
	
}
