/*
 * 名称: NettyClient
 * 描述: netty tcp客户端,单线程
 * 版本：  1.0.0
 * 作者： 翁富家
 * 修改:
 * 日期：2015年6月4日
 */

package org.anyway.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import org.anyway.client.common.uConfigVar;
import org.anyway.client.protocol.MessageCodecFactory;
import org.anyway.common.utils.uLogger;

public class NettyClient {
	private final String host;
    private final int port;
    
    private EventLoopGroup group;
	private Bootstrap bootstrap;
	private Channel channel;
	
	/**
	 * 构造函数
	 * @param host
	 * @param port
	 */
	public NettyClient(String host, int port) {
		this.host = host;
        this.port = port;
        //初始化
        init();
	}
	
	/**
	 * 初始化
	 */
	private synchronized void init() {
		group = new NioEventLoopGroup();
		bootstrap = new Bootstrap();
		bootstrap.group(group).channel(NioSocketChannel.class)
				.handler(new MessageCodecFactory());
		bootstrap//.option(ChannelOption.SO_TIMEOUT, uConfigVar.US_WaitTimeOut)
         .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, uConfigVar.US_IdleTimeOut)	                 
         .option(ChannelOption.TCP_NODELAY,true)
         .option(ChannelOption.SO_KEEPALIVE,true)
         .option(ChannelOption.SO_REUSEADDR,true); //重用地址
	}
	
	/**
	 * 连接
	 */
	public synchronized boolean connect() {
		if (null != channel && channel.isActive()) {
			return true;
		}
		
		try {
			/*ChannelFuture future = bootstrap.connect(host, port);
			if (future.isSuccess()) {
				channel = future.sync().channel();
	        }
			connected = future.isSuccess();	*/
			
			ChannelFuture future = bootstrap.connect(host, port);
			future.addListener(new ChannelFutureListener() {
                @Override 
                public void operationComplete(ChannelFuture future)
                        throws Exception {
                    if (future.isSuccess()) {
                    	channel = future.sync().channel();
                    }
                }
            });
			
			//channel = bootstrap.connect(host, port).sync().channel();
		} catch (Exception e) {
			uLogger.printInfo("socketclient连接失败：" + e.getMessage());
			group.shutdownGracefully();
			return false;
		}
		return true;
	}
	
	/**
	 * 获取地址
	 * @return
	 */
	public String getHost() {
		return this.host;
	}
	
	/**
	 * 获取端口号
	 * @return
	 */
	public int getPort() {
		return this.port;
	}
    
	/**
	 * 获取连接句柄
	 * @return
	 */
	public Channel getChannel() {
		return this.channel;
	}
	
	/**
	 * 判断是否连接
	 * @return
	 */
	public Boolean isActive() {
		return this.channel.isActive();
	}
}
