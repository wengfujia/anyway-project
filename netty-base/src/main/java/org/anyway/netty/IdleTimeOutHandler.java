/*
 * 名称: IdleTimeOutHandler
 * 描述: 自定义超时单元
 * 版本：  1.0.0
 * 作者： 翁富家
 * 修改:
 * 日期：2015年01月16日
 * 修改日期:
 */


package org.anyway.netty;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.ReadTimeoutException;
import io.netty.handler.timeout.WriteTimeoutException;

public class IdleTimeOutHandler extends ChannelDuplexHandler {
	
	// 设置读写超时进入的单元
	@Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (cause instanceof ReadTimeoutException) {
        	ctx.close();
        } else if (cause instanceof WriteTimeoutException) {
        	ctx.close();
        } else {
            super.exceptionCaught(ctx, cause);
        }
    }
	
	// 设置idle超时进入的单元
	@Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent e = (IdleStateEvent) evt;
            if (e.state() == IdleState.READER_IDLE) {
                ctx.close();
            } else if (e.state() == IdleState.WRITER_IDLE) {
                //ctx.writeAndFlush(new PingMessage());
                ctx.close();
            } else if (e.state() == IdleState.ALL_IDLE) {
            	ctx.close();
            }
            
        }
    }
}
