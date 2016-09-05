/*
 * 名称: SimpleDefaultHandler
 * 描述: netty封装基类，允许被继承
 * 版本：  1.0.0
 * 作者： 翁富家
 * 修改:
 * 日期：2015年12月21日
 * 修改日期:
 */

package org.anyway.netty.socket.handler;

import java.io.IOException;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import org.anyway.common.utils.uLogger;

public abstract class SimpleDefaultHandler extends SimpleChannelInboundHandler<byte[]> {

	// 当一个客端端连结到服务器后
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
    	uLogger.println("[socket]Channel Connected");
    }
    
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
    	uLogger.println("[socket]Channel Closed");
    }
    
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {  
    	if (cause instanceof IOException) {
            uLogger.getLogger().error("[socket]Exception: ", cause);
        } else {
        	uLogger.getLogger().info("[socket]I/O error: " + cause.getMessage());
        }
    	ctx.close();
    }
    
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
    	ctx.flush();
    }
    
	/**
	 * SendPacket
	 * @param iosession
	 * @param buffer
	 * @param len
	 * @return WriteFuture
	 */
    protected ChannelFuture SendPacket(ChannelHandlerContext ctx, ByteBuf ibuffer, int len)
	{
		if (ctx==null)
			return null;
		
		ChannelFuture ret = null;
		try
		{
			if (ctx.channel().isWritable())
				ret = ctx.writeAndFlush(ibuffer);
			else
				ctx.close();
		}
		catch (Exception E)
		{
			uLogger.printInfo(E.getMessage());
		}
		return ret;
	}
	
}
