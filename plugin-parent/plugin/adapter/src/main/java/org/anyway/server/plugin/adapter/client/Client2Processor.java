/*
 * 名称: TcpClient
 * 描述: netty tcp客户端,采用线程池
 * 版本：  1.0.0
 * 作者： 翁富家
 * 修改:
 * 日期：2015年6月4日
 */

package org.anyway.server.plugin.adapter.client;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;

import org.anyway.common.utils.LoggerUtil;

public class Client2Processor {
	
	private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
	
	private static List<NettyClient> queues = new ArrayList<NettyClient>();
	
	private NettyClient nettyClient = null;
	
	/**
	 * 构造函数
	 * @param host
	 * @param port
	 */
	public Client2Processor(String host, int port) {
        //根据host与port取出client
		try {
			lock.readLock().lock();
			for (NettyClient client : queues) {
				if (client.getHost() == host && client.getPort() == port) {
					nettyClient = client;
					break;
				}
			}
		}
		catch (Exception e) {
			LoggerUtil.getLogger().error("get client error:{}", e);
		}
		finally {
			lock.readLock().unlock();
		}
		
		//如果客户端连接为空，创建
        if (null == nettyClient) {
        	nettyClient = new NettyClient(host, port);
        	boolean isConnected = nettyClient.connect();
        	if (isConnected) {
        		try {
        			lock.writeLock().lock();
        			//连接成功存入线程池
                	queues.add(nettyClient);
        		} catch (Exception e) {
        			LoggerUtil.getLogger().error("set client error:{}", e);
				}
        		finally {
        			lock.writeLock().unlock();
				}
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
	public boolean send(ByteBuf buf) {
		if (null == nettyClient)
			return false;
		
		boolean result = nettyClient.isActive();
		try
		{
			if (!result) {
				result = nettyClient.connect();
			}
			if (result) {
				nettyClient.getChannel().writeAndFlush(buf);
			}
		}
		catch (Exception E)
		{
			LoggerUtil.getLogger().error("TcpCLient error:", E);
		}
		return result;
	}
	
	/**
	 * 发送消息
	 * @param buf
	 * @param retry 重试次数
	 */
	public boolean send(ByteBuf buf, int retry) {
		boolean result = false;
		for (int i=0; i<retry; i++) {
			result = send(buf);
			if (result) break; //发送成功，退出
		}
		return result;
	}

	/**
	 * 获取连接句柄
	 * @return
	 */
	public Channel getChannel() {
		return nettyClient.getChannel();
	}
	
}
