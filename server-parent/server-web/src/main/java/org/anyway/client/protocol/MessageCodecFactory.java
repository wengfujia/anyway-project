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

package org.anyway.client.protocol;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;

import org.anyway.client.common.uConfigVar;
import org.anyway.client.handler.SocketClientHandler;
import org.anyway.netty.IdleTimeOutHandler;
import org.anyway.netty.socket.codec.MessageDecoder;
import org.anyway.netty.socket.codec.MessageEncoder;
import org.anyway.server.factory.PriorityThreadFactory;

public class MessageCodecFactory  extends ChannelInitializer<SocketChannel> {
	
	final EventExecutorGroup e1 = new DefaultEventExecutorGroup(uConfigVar.US_WorkThreadCount, new PriorityThreadFactory("clientLogicHandlerThread+#", Thread.NORM_PRIORITY ));
    
    @Override
    public void initChannel(SocketChannel ch) throws Exception {  	
        ChannelPipeline pipe = ch.pipeline();
        
        pipe.addLast("idleAware", new IdleStateHandler(uConfigVar.US_RWTimeOut, uConfigVar.US_RWTimeOut, uConfigVar.US_IdleTimeOut));
        pipe.addLast("IdleTimeOutHandler", new IdleTimeOutHandler());
        // Add the number codec first, 
        pipe.addLast("decoder", new MessageDecoder(uConfigVar.CharsetName));
        pipe.addLast("encoder", new MessageEncoder());
        pipe.addLast(e1, "handler", new SocketClientHandler()); // 处理业务类 采用线程池 new NioEventLoopGroup(),
    }
}