/*
 * 名称: MessageCodecFactory.java
 * 描述: 网络包加解码工厂类
 * 版本：  1.0.0
 * 作者： 翁富家
 * 修改:
 * 日期：2013年08月20日
 * 修改日期:
 * 2014.7.12
 * 增加：EventExecutorGroup e1 = new DefaultEventExecutorGroup(uConfigVar.US_WorkThreadCount);
 * 多线程业务处理，用于业务超长响应时，造成服务无响应
 * 
 */

package org.anyway.server.adapter.socket.protocol;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;

import org.anyway.common.AdapterConfig;
import org.anyway.common.SystemConfig;
import org.anyway.common.factory.PriorityThreadFactory;
import org.anyway.netty.IdleTimeOutHandler;
import org.anyway.netty.socket.codec.MessageDecoder;
import org.anyway.netty.socket.codec.MessageEncoder;
import org.anyway.server.adapter.socket.handler.SocketHandler;

public class MessageCodecFactory  extends ChannelInitializer<SocketChannel> {
	
	final EventExecutorGroup e1 = new DefaultEventExecutorGroup(AdapterConfig.getInstance().getUSWorkThreadCount(),
			new PriorityThreadFactory("executionLogicHandlerThread+#", Thread.NORM_PRIORITY));
    
    @Override
    public void initChannel(SocketChannel ch) throws Exception {  	
        ChannelPipeline pipe = ch.pipeline();       
		pipe.addLast("idleAware", new IdleStateHandler(AdapterConfig.getInstance().getUSRWTimeOut(),
				AdapterConfig.getInstance().getUSRWTimeOut(), AdapterConfig.getInstance().getUSIdleTimeOut()));
        pipe.addLast("IdleTimeOutHandler", new IdleTimeOutHandler());
        // Add the number codec first, 
        pipe.addLast("decoder", new MessageDecoder(SystemConfig.CharsetName));
        pipe.addLast("encoder", new MessageEncoder());
        pipe.addLast(e1, "handler", new SocketHandler()); // 处理业务类 采用线程池 new NioEventLoopGroup(),
    }
    
}