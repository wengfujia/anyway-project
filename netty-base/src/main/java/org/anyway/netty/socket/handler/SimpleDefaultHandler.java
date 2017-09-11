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

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import org.anyway.common.AdapterConfig;
import org.anyway.common.enums.CryptEnum;
import org.anyway.common.protocol.TcpMessageCoder;
import org.anyway.common.protocol.header.Header;
import org.anyway.common.utils.LoggerUtil;

public abstract class SimpleDefaultHandler extends SimpleChannelInboundHandler<byte[]> {
    
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
				ctx.channel().disconnect();
		}
		catch (Exception E)
		{
			LoggerUtil.printInfo(E.getMessage());
		}
		return ret;
	}

    /**
     * 发送反馈包
     * @param ctx
     * @param header
     */
    protected void SendResp(final ChannelHandlerContext ctx, Header header) {  	
		TcpMessageCoder streamResp = new TcpMessageCoder(AdapterConfig.getInstance().getUSMaxSendBufferSize());
    	Header headerResp = new Header(header);
    	try
    	{
    		headerResp.setCommandID(-header.getCommandID());
    		headerResp.setStatus(0);
	    	ByteBuf ibuffer = ctx.alloc().buffer(); 
	    	int len = streamResp.LoadFromStream(ibuffer, CryptEnum.DES);
	    	
	    	ChannelFuture retsend = SendPacket(ctx, ibuffer, len);
	    	if (retsend==null) {
				LoggerUtil.sprintf("[socket]Fail To SendPacket,IP:%s",header.getIP());
				ctx.close();
	    	}	
    	}
    	finally { 
    		//清空
    		streamResp.ClearStream();
	    	streamResp = null;
	    	headerResp.Clear();
	    	headerResp = null;
    	}
    }
    
}
