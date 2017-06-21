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
 */

package org.anyway.server.adapter.socket.handler;

import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;

import org.anyway.common.AdapterConfig;
import org.anyway.common.uConfigVar;
import org.anyway.common.enums.CryptEnum;
import org.anyway.common.protocol.TcpMessageCoder;
import org.anyway.common.protocol.header.CommandID;
import org.anyway.common.protocol.header.Header;
import org.anyway.common.protocol.request.TcpRequest;
import org.anyway.common.types.pint;
import org.anyway.common.types.pstring;
import org.anyway.common.utils.NetUtil;
import org.anyway.common.utils.LoggerUtil;
import org.anyway.common.utils.StringUtil;
import org.anyway.exceptions.NoCacheException;
import org.anyway.netty.socket.handler.SimpleDefaultHandler;
import org.anyway.server.adapter.cache.CacheManager;
import org.anyway.server.plugin.adapter.dispatcher.Dispatcher;

public class SocketHandler extends SimpleDefaultHandler {

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
    	
    	TcpMessageCoder shtStream = new TcpMessageCoder();
    	//获取包头中的长度
    	int ilen = shtStream.GetLength(msg);
    	//判断包中的包长是否跟接收到的包长相符
		if (ilen == msg.length) {
			//分解包
			int ret = shtStream.SaveToStream(msg, msg.length, CryptEnum.DES);
	    	if (ret > 0) {
	    		//消息处理
	    		HandleMsgStream(ctx, shtStream);
	    		return;
	    	} 
	    	else {
	    		LoggerUtil.sprintf("[socket]Receive The Wrong Message,IP:%s",clientIP);
	    		ctx.close();
	    	}
		} 
		else {
			LoggerUtil.sprintf("[socket]Receive The Wrong Message,IP:%s",clientIP);
			ctx.close();
    	}
		
		if (shtStream!=null) {shtStream.ClearStream();shtStream = null;} 
		
		//ReferenceCountUtil.release(msg);
    } 
    
	/**
	 * HandleMsgStream
	 * 
	 * @param iosession
	 * @param stream
	 * @throws NoCacheException
	 */
	protected void HandleMsgStream(final ChannelHandlerContext ctx, TcpMessageCoder stream) throws NoCacheException {

		// 1.处理
		Header header = stream.getHeader();
		header.setIP(clientIP);// 设置IP

		int status = 0;
		int commandid = stream.GetCommand();

		if (commandid == CommandID.TEST) {
			LoggerUtil.printInfo("[socket]Test,User:%s,IP:%s,Mac:%s", header.getUser(), clientIP, header.getReserve());
		} else if (commandid == CommandID.INIT_FINAL) {
			LoggerUtil.println("[socket]Login Init Final!");
		} else {
			// 获取版本号
			String key = LoggerUtil.sprintf("VER.%s", header.getSessionid());
			String ver = AdapterConfig.getInstance().GetVerValue("", key);
			if (ver.compareTo(header.getVersion()) > 0) {// 判断版本
				status = -12;
			} else {
				// 2.业务分发
				TcpRequest request = new TcpRequest();
				request.setContext(ctx);
				request.setCStream(stream);
				try {
					status = Dispatcher.submit(request, stream.GetCommand());
				} catch (InstantiationException | IllegalAccessException e) {
					LoggerUtil.printInfo("[socket]Dispatcher Init Fail!" + e.getMessage() + ",IP:%s", clientIP);
					status = -10;
				}
			}
		}

		// 3.反馈给用户
		if (status != 0) { // 返馈错误信息
			pstring result1 = new pstring();
			pstring result2 = new pstring();
			CacheManager.getInstance().GetErrorInfo(status, result1, result2);
			if (StringUtil.empty(result2.getString()) == false) {// 获取到错误代码解释
				result2.setString("非知错误");
			}

			pint pstrlen = new pint(0);
			byte[] pstr;
			try {
				pstr = NetUtil.getBytes(result2.getString(), uConfigVar.CharsetName);
				TcpMessageCoder streamResp = new TcpMessageCoder();
				header.setCommandID(commandid);
				header.setStatus(status);
				header.setResptype(1);
				streamResp.SetNr(pstr, pstrlen.getInt());
				streamResp.EncodeHeader(header);
				ByteBuf ibuffer = ctx.alloc().buffer();
				int len = streamResp.LoadFromStream(ibuffer, CryptEnum.DES);
				streamResp.ClearStream();
				streamResp = null;
				ChannelFuture retsend = SendPacket(ctx, ibuffer, len);
				if (retsend == null) {
					LoggerUtil.printInfo("[socket]Fail To SendPacket,IP:%s", clientIP);
					ctx.close();
				}
				LoggerUtil.printInfo("[socket]Fail! ErrorCode:%s， CommandID:%s,User:%s,IP:%s", status, header.getCommandID(),
						header.getUser(), clientIP);
			} catch (UnsupportedEncodingException e) {
				LoggerUtil.printInfo(e.getMessage());
			}
		} else if (AdapterConfig.getInstance().IsArk(commandid)) { // 发送反馈包，表示接收成功
			header.setCommandID(-commandid);
			SendResp(ctx, header);
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
    	TcpMessageCoder streamResp = new TcpMessageCoder();
    	Header headerResp = new Header(header);
    	try
    	{
    		headerResp.setCommandID(-header.getCommandID());
    		headerResp.setStatus(0);
	    	ByteBuf ibuffer = ctx.alloc().buffer(); 
	    	int len = streamResp.LoadFromStream(ibuffer, CryptEnum.DES);
	    	
	    	ChannelFuture retsend = SendPacket(ctx, ibuffer, len);
	    	if (retsend==null) {
				LoggerUtil.sprintf("[socket]Fail To SendPacket,IP:%s",clientIP);
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
