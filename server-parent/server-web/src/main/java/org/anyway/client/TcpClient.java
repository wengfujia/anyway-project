/*
 * 名称: TcpClient
 * 描述: netty tcp客户端,采用线程池
 * 版本：  1.0.0
 * 作者： 翁富家
 * 修改:
 * 日期：2015年6月4日
 */

package org.anyway.client;

import java.util.ArrayList;
import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;

import org.anyway.common.utils.uLogger;

public class TcpClient {
	
	private volatile static List<NettyClient> queues = new ArrayList<NettyClient>();
	
	private NettyClient nettyClient = null;
	
	/**
	 * 构造函数
	 * @param host
	 * @param port
	 */
	public TcpClient(String host, int port) {
        //根据host与port取出client
		for (NettyClient client : queues) {
			if (client.getHost() == host && client.getPort() == port) {
				nettyClient = client;
				break;
			}
		}
		//如果客户端连接为空，创建
        if (null == nettyClient) {
        	nettyClient = new NettyClient(host, port);
        	boolean isConnected = nettyClient.connect();
        	if (isConnected) {
        		//连接成功存入线程池
            	queues.add(nettyClient);
        	}
        }
		
    }
	
	/**
	 * 连接
	 */
	public void connect() {
		nettyClient.connect();
	}
	
	/**
	 * 发送消息(不做任何判断)
	 */
	public void send(ByteBuf buf) {
		if (null == nettyClient || null == nettyClient.getChannel())
			return;
		
		try
		{
			if (nettyClient.getChannel().isWritable())
				nettyClient.getChannel().writeAndFlush(buf);
			else
				nettyClient.getChannel().close();
		}
		catch (Exception E)
		{
			uLogger.printInfo(E.getMessage());
		}
	}
	
	/**
	 * 发送消息
	 * @param buf
	 * @param retry 重试次数
	 */
	public void send(ByteBuf buf, int retry) {
		if (null == nettyClient)
			return;
		
		for (int i=0; i<retry; i++) {
			if (null == nettyClient.getChannel() || nettyClient.getChannel().isActive() == false) { //进行连接
				nettyClient.connect();
			}
			if (null != nettyClient.getChannel()) {
				if (nettyClient.getChannel().isWritable()) {
					nettyClient.getChannel().writeAndFlush(buf);
					break;
				}
				else {
					nettyClient.getChannel().close();
				}
			}
		}
	}

	/**
	 * 获取连接句柄
	 * @return
	 */
	public Channel getChannel() {
		return nettyClient.getChannel();
	}
}
