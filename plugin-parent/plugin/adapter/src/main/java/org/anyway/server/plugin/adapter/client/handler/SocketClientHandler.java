/*
 * 名称: SocketHandler
 * 描述: 与hbase通讯的网络包接收与解析类
 * 版本：  1.0.0
 * 作者： 翁富家
 * 修改:
 * 日期：2013年08月18日
 * 修改日期:
 * 2014.7.12
 * 增加：retsend.addListeners用于等待返回结果，如果失败则关闭连接。
 * 2016.9.8
 * HandleMsgStream函数中的Dispatcher.submit改成：Dispatcher.execute
 */

package org.anyway.server.plugin.adapter.client.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;

import java.net.InetSocketAddress;

import org.anyway.common.AdapterConfig;
import org.anyway.common.enums.CryptEnum;
import org.anyway.common.protocol.TcpMessageCoder;
import org.anyway.common.protocol.header.CommandID;
import org.anyway.common.protocol.header.Header;
import org.anyway.common.protocol.request.TcpRequest;
import org.anyway.common.utils.LoggerUtil;
import org.anyway.exceptions.NoCacheException;
import org.anyway.netty.socket.handler.SimpleDefaultHandler;
import org.anyway.server.plugin.adapter.dispatcher.Dispatcher;

public class SocketClientHandler extends SimpleDefaultHandler {

	private String clientIP;
	
    // 当服务器端发送的消息到达时:
    /**
     * 收到需要处理的消息后二步骤操作，
     * 1、直接反馈消息唯一号，表示消息接收成功
     * 2、进行业务处理
     * @throws NoCacheException 
     */
    @Override
    public void channelRead0(ChannelHandlerContext ctx, byte[] msg) throws NoCacheException {

    	//获取IP地址
    	clientIP = ((InetSocketAddress)ctx.channel().remoteAddress()).getAddress().getHostAddress();
    	
    	TcpMessageCoder shtStream = new TcpMessageCoder(AdapterConfig.getInstance().getUSMaxSendBufferSize());
    	//获取包头中的长度
    	int ilen = shtStream.GetLength(msg);
    	//判断包中的包长是否跟接收到的包长相符
		if (ilen == msg.length) {
			//分解包
			int ret = shtStream.SaveToStream(msg, msg.length, CryptEnum.DES);
	    	if (ret > 0) {
	    		//消息处理
	    		handleMsgStream(ctx, shtStream);
	    		return;
	    	} 
	    	else {
	    		LoggerUtil.sprintf("[client]Receive The Wrong Message,IP:%s",clientIP);
	    	}
		} 
		else {
			LoggerUtil.sprintf("[client]Receive The Wrong Message,IP:%s",clientIP);
    	}
		
		if (shtStream!=null) {shtStream.ClearStream();shtStream = null;} 
		
		//ReferenceCountUtil.release(msg);
    }    
    
    /**
     * HandleMsgStream
     * @param iosession
     * @param stream
     * @throws NoCacheException 
     */
	protected void handleMsgStream(final ChannelHandlerContext ctx, TcpMessageCoder stream) throws NoCacheException {

		//1.处理
		Header header = stream.getHeader();	
		if (CommandID.TEST == header.getCommandID()) {
			LoggerUtil.printInfo("[client]Test health! ErrorCode:%s，User:%s,IP:%s,Mac:%s", header.getStatus(), header.getUser(), clientIP, header.getReserve());
			return;
		}
		if (stream.GetStatus() == -12) { //版本号不对
			LoggerUtil.printInfo("[client]Error Version,User:%s,IP:%s,Mac:%s", header.getUser(), clientIP, header.getReserve());
		}
		else if (stream.GetStatus() !=0) {//其它错误 打印日志
			LoggerUtil.printInfo("[client]Fail! ErrorCode:%s，User:%s,IP:%s,Mac:%s", header.getStatus(), header.getUser(), clientIP, header.getReserve());
		}

		//2.接收消息返回，消息返回到信息发送源
  		TcpRequest request = new TcpRequest();
  		request.setContext(ctx);
  		request.setCStream(stream);
		try {
			Dispatcher.submit(request, header.getCommandID());
		} catch (InstantiationException | IllegalAccessException e) {
			LoggerUtil.getLogger().error("[client]Dispatcher Init Fail!" + e.getMessage() + ",IP:%s",clientIP);
		}
		catch (Exception e) {
			LoggerUtil.getLogger().error(e.getMessage());
		}
	}
	    
	//////////////////////////////////////////////////////////////////
	//消息发送函数
	///////////////////////////////////////////////////////////////////
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
				LoggerUtil.sprintf("[client]Fail To SendPacket,IP:%s",clientIP);
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
