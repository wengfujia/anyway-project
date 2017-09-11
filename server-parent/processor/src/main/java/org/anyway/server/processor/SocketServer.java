/*
 * 名称: SocketServer
 * 描述: Socket服务端
 * 版本：  1.0.0
 * 作者： 翁富家
 * 修改:
 * 日期：2013年10月15日
 * 修改日期:
 */

package org.anyway.server.processor;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import org.anyway.common.ProcesserConfig;
import org.anyway.common.SystemConfig;
import org.anyway.common.factory.PriorityThreadFactory;
import org.anyway.common.utils.LoggerUtil;
import org.anyway.server.processor.socket.protocol.MessageCodecFactory;

public class SocketServer {
	private final int port;
	private Thread thread = null;
	
	public SocketServer(int port) {
		this.port = port;		
	}
	
	Runnable OpenSocket = new Runnable(){
		public void run(){
			//绑定端口，启动socket服务器
			try {
				NioEventLoopGroup bossGroup=new NioEventLoopGroup(2, new PriorityThreadFactory("@+监听连接线程",Thread.NORM_PRIORITY)); //mainReactor 2个线程 //
		        NioEventLoopGroup workerGroup=new NioEventLoopGroup(Runtime.getRuntime().availableProcessors()+ 1, new PriorityThreadFactory("@+I/O线程",Thread.NORM_PRIORITY)); //subReactor 线程数量等价于cpu个数+1
	            try {
	                ServerBootstrap b = new ServerBootstrap();
	                b.group(bossGroup, workerGroup) //设置时间循环对象，前者用来处理accept事件，后者用于处理已经建立的连接的io  
	                 .channel(NioServerSocketChannel.class) //用它来建立新accept的连接，用于构造serversocketchannel的工厂类  
	                 .option(ChannelOption.SO_BACKLOG, 128)
	                 .option(ChannelOption.SO_RCVBUF, ProcesserConfig.getInstance().getUSMaxReadBufferSize())
	                 .option(ChannelOption.SO_SNDBUF, ProcesserConfig.getInstance().getUSMaxSendBufferSize())
	                 .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
	                 .option(ChannelOption.SO_TIMEOUT, ProcesserConfig.getInstance().getUSWaitTimeOut())
	                 .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, ProcesserConfig.getInstance().getUSIdleTimeOut())	                 
	                 .option(ChannelOption.TCP_NODELAY,true)
	                 .option(ChannelOption.SO_KEEPALIVE,true)
	                 .option(ChannelOption.SO_REUSEADDR,true); //重用地址
	                	                
	                b.childHandler(new MessageCodecFactory()); //为当前的channel的pipeline添加自定义的处理函数 
	                if (SystemConfig.DEBUG) {
	                	b.handler(new LoggingHandler(LogLevel.INFO));
	            	}
	                //打开
	                b.bind(port).sync().channel().closeFuture().sync();
	            } finally {
	                bossGroup.shutdownGracefully();
	                workerGroup.shutdownGracefully();
	            }
	        } catch (Exception e) {
	            LoggerUtil.getLogger().error("Fail to open the socket service !"+e.getMessage());
	        }
	   }
	};
	
	public void start() 
	{	 
		//run();
		this.thread = new Thread(OpenSocket);
		thread.start();
		LoggerUtil.println("The socket service is runing! Port:"+ProcesserConfig.getInstance().getUSPort());   
	}
}
