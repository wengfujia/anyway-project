/*
 * 名称: NettyClient
 * 描述: netty tcp客户端,单线程
 * 版本：  1.0.0
 * 作者： 翁富家
 * 修改:
 * 日期：2015年6月4日
 */

package org.anyway.server.plugin.adapter.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.anyway.common.ClientConfig;
import org.anyway.common.utils.LoggerUtil;
import org.anyway.server.plugin.adapter.client.protocol.MessageCodecFactory;

public class NettyClient {
	
	private static final long LOCK_TIMEOUT_MILLIS = 3000;
	private final Lock lockChannelTables = new ReentrantLock();
	
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
	private void init() {
		try {
			if (this.lockChannelTables.tryLock(LOCK_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)) {
				group = new NioEventLoopGroup();
				bootstrap = new Bootstrap();
				bootstrap.group(group).channel(NioSocketChannel.class).handler(new MessageCodecFactory());
				bootstrap.option(ChannelOption.SO_TIMEOUT, ClientConfig.getInstance().getWaitTimeOut())
						.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, ClientConfig.getInstance().getConnectTimeOut())
						.option(ChannelOption.TCP_NODELAY, true)
						.option(ChannelOption.SO_KEEPALIVE, true)
						.option(ChannelOption.SO_REUSEADDR, true); // 重用地址
			}
		} catch (Exception e) {
			LoggerUtil.getLogger().error("socketclient初始化失败：{}", e);
		} finally {
			this.lockChannelTables.unlock();
		}
	}
	
	/**
	 * 连接
	 */
	public boolean connect() {
		if (null != channel && channel.isActive()) {
			return true;
		}
		
		try {
			if (this.lockChannelTables.tryLock(LOCK_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)) {
				try {
					ChannelFuture future = bootstrap.connect(host, port);
					if (future.awaitUninterruptibly(ClientConfig.getInstance().getConnectTimeOut())) {
						if (null != future.channel() && future.channel().isActive()) {
							channel = future.channel();
						} else {
							return false;
						}
					} else {
						closeChannel();
						LoggerUtil.printInfo("socketclient连接失败");
						return false;
					}
				} catch (Exception e) {
					closeChannel();
					LoggerUtil.getLogger().error("socketclient连接失败：{}", e);
					return false;
				} finally {
					this.lockChannelTables.unlock();
				}
			} else {
				return false;
			}
		} catch (Exception e) {
			LoggerUtil.getLogger().error("socketclient连接失败：{}", e);
			closeChannel();
			return false;
		}
		return true;
	}
	
	/**
	 * 关闭连接
	 */
	private void closeChannel() {
		if (null != channel) {
			channel.close();
			channel = null;
		}
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
		if (null == this.channel) {
			return false;
		}
		return this.channel.isActive();
	}
}
