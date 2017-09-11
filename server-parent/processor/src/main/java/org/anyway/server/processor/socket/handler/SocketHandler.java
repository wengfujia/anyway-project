/*
 * 名称: SocketHandler
 * 描述: 网络包接收与解析类
 * 版本：  1.0.0
 * 作者： 翁富家
 * 修改:
 * 日期：2013年08月18日
 * 修改日期:
 * 2014.7.12
 * 增加：retsend.addListeners用于等待返回结果，如果失败则关闭连接。
 * 2016.7.6
 * 由 int nrlen =0;改为pint nrlen =new pint(0);
 */

package org.anyway.server.processor.socket.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.anyway.netty.socket.handler.SimpleDefaultHandler;
import org.anyway.server.processor.Providers.Handle;
import org.anyway.server.processor.cache.DBCache;
import org.anyway.common.ProcesserConfig;
import org.anyway.common.SystemConfig;
import org.anyway.common.enums.CryptEnum;
import org.anyway.common.protocol.TcpMessageCoder;
import org.anyway.common.protocol.buffer.impl.CChrList;
import org.anyway.common.protocol.header.CommandID;
import org.anyway.common.protocol.header.DbHeader;
import org.anyway.common.protocol.header.Header;
import org.anyway.common.types.pint;
import org.anyway.common.types.pstring;
import org.anyway.common.utils.LoggerUtil;
import org.anyway.common.utils.NetUtil;
import org.anyway.common.utils.StringUtil;

public class SocketHandler extends SimpleDefaultHandler {

	static ExecutorService executor = Executors.newCachedThreadPool();
	
    // 当服务器端发送的消息到达时:
    @Override
    public void channelRead0(ChannelHandlerContext ctx, byte[] msg) {

    	int ret = 0;
    	TcpMessageCoder shtStream = new TcpMessageCoder(ProcesserConfig.getInstance().getUSMaxSendBufferSize());
    	//分解头与体
    	int ilen = shtStream.GetLength(msg);
		if (ilen != msg.length) {
    		LoggerUtil.println("[socket]Receive The Wrong Message");
    		ctx.close();
    		return;
    	}
    	ret = shtStream.SaveToStream(msg, msg.length, CryptEnum.DES);
    	
    	if (ret > 0) {
    		executor.submit(new RequestTask(ctx, shtStream));
    	}
    	else {
			if (shtStream != null) {
				shtStream.ClearStream();
				shtStream = null;
			}
    	}
    }

}

class RequestTask implements Runnable {
	
	private final ChannelHandlerContext ctx;
	private TcpMessageCoder stream;
	
	public RequestTask(final ChannelHandlerContext ctx, TcpMessageCoder stream) {
		this.ctx = ctx;
		this.stream = stream;
	}

	@Override
	public void run() {
		try {
			handleMsgStream();
		} finally {
			if (stream != null) {
				stream.ClearStream();
				stream = null;
			}
		}
	}
	
    
    /**
     * 处理消息
     */
	private void handleMsgStream() {
		String clientIP =((InetSocketAddress)ctx.channel().remoteAddress()).getAddress().getHostAddress();
		//1.处理
		pint nrlen =new pint(0);
		byte[] reserve =null;// new byte[uGlobalVar.MAX_READBUFFER_SIZE];
		byte[] nr = stream.GetNr(nrlen);
		
		CChrList list = new CChrList();
		byte[] o_reserve = new byte[1024];
		DbHeader dbheader = stream.GetDbHeader();
		
		StringBuffer log = new StringBuffer();
	    int commandid = stream.GetCommand();
	    int status = 0;
	    
		Header header = stream.getHeader();	
		header.setIP(clientIP);//设置IP
		try {    
			if (commandid == CommandID.TEST) {
		    	//list.Append(NetUtils.getBytes("[socket]Test Ok", uGlobalVar.CharsetName));
		    	LoggerUtil.sprintf(log, "[socket]Test,User:%s,IP:%s,Mac:%s", header.getUser(), clientIP, header.getReserve());
		    } else if (commandid == CommandID.INIT_FINAL) {
		    	LoggerUtil.println("[socket]Login Init Final!");
		    } else {
		    	//获取reserve
		    	String key = LoggerUtil.sprintf("VER.%s", header.getSessionid());
			  	String ver = ProcesserConfig.getInstance().GetVerValue("", key);
			  	//返回版本号\t错误说明
		    	if (ver.compareTo(header.getVersion())>0) {//判断版本
					status = -12;
					list.Append(ver); //返回当前版本号
			  	} else {
			  		status = Handle.G_Handle(header,dbheader,nr,nrlen.getInt(),reserve,1,list,o_reserve);			    		
			  	}
		    	
		    	if (status ==0) {
		    		LoggerUtil.sprintf(log, "[socket]Sucess! CommandID:%s,User:%s,Content:%.10s,IP:%s", String.valueOf(header.getCommandID()), header.getUser(), NetUtil.getString(nr, SystemConfig.CharsetName), clientIP);
		    	} else if (status != -12) {
		    		//查出错误含义
		    		if (list.Count()==0) {
		    			pstring result1 = new pstring();
		    			pstring result2 = new pstring();
		    			DBCache.GetErrorInfo(header.getSessionid(), header.getCommandID(), status, result1, result2);
		    			if (StringUtil.empty(result2.getString())==false) //获取到错误代码解释
		    			{
		    				list.Append(result2.getString());
		    			}
		    		}
		    		LoggerUtil.sprintf(log, "[socket]Fail! ErrorCode:%s， CommandID:%s,User:%s,Content:%.10s,IP:%s", status, header.getCommandID(), header.getUser(), NetUtil.getString(nr, SystemConfig.CharsetName), clientIP);		    		
		    	}
		    }
	
		    //3.反馈给用户
			ChannelFuture retsend = null;
		    pint pstrlen = new pint(0);
		    byte[] pstr=list.First(pstrlen);
		    if (list.Count()<=1) {
		    	TcpMessageCoder streamResp = new TcpMessageCoder(ProcesserConfig.getInstance().getUSMaxSendBufferSize());
		    	header.setCommandID(commandid);
		    	header.setStatus(status);
		    	header.setResptype(1);
		    	streamResp.SetNr(pstr, pstrlen.getInt());
		    	streamResp.EncodeHeader(header);
		    	//ibuffer = streamResp.LoadFromStream(pstrlen, CryptEnum.DES);
		    	ByteBuf ibuffer = ctx.alloc().buffer(); 
		    	int len = streamResp.LoadFromStream(ibuffer, CryptEnum.DES);
		    	streamResp.ClearStream();
		    	streamResp = null;
		    	retsend = sendPacket(ibuffer, len);//pstrlen.getInt()
		    	if (retsend==null) {
					LoggerUtil.sprintf(log,"[socket]Fail To SendPacket,IP:%s",clientIP);
					ctx.close();
		    	}
		    } else {
		    	TcpMessageCoder streamResp = new TcpMessageCoder(ProcesserConfig.getInstance().getUSMaxSendBufferSize());
		    	for (; !list.Eof(); pstr = list.Next(pstrlen)) {		    		
		    		header.setCommandID(commandid);
		    		header.setStatus(status);
		    		if (list.IsLast())
		    			header.setResptype(1);
		    		else
		    			header.setResptype(0);
		    		streamResp.SetNr(pstr, pstrlen.getInt());
		    		streamResp.EncodeHeader(header);
		    		//ibuffer = streamResp.LoadFromStream(pstrlen, CryptEnum.DES);
		    		ByteBuf ibuffer = ctx.alloc().buffer(); 
		    		int len = streamResp.LoadFromStream(ibuffer, CryptEnum.DES);
		    		retsend = sendPacket(ibuffer, len); //pstrlen.getInt()
		    		if (retsend==null) { 
						LoggerUtil.sprintf(log,"[socket]Fail To SendPacket,IP:%s", clientIP);
						ctx.close();
						break;
		    		}
		    	}
		    	streamResp.ClearStream();
	    		streamResp = null;
		    }
		    //打印消息
		    if (log.length()>0)
		    	LoggerUtil.println(log.toString());  
		} 
		catch (Exception e) {
			LoggerUtil.println(e.getMessage());
			ctx.close();
		}
		finally {
			reserve=null;o_reserve = null;log = null;
			if (list!=null) {list.ClearAll();list = null;}
		}
	}
	
	/**
	 * 发送
	 * @param ctx
	 * @param ibuffer
	 * @param len
	 * @return
	 */
	private ChannelFuture sendPacket(ByteBuf ibuffer, int len)
	{
		if (ctx == null)
			return null;
		
		ChannelFuture ret = null;
		try
		{
			if (ctx.channel().isWritable())
				ret = ctx.writeAndFlush(ibuffer);
			else
				ctx.channel().disconnect();
		}
		catch (Exception E)
		{
			LoggerUtil.getLogger().error("RequestTask处理出错：{}", E.getMessage());
		}
		return ret;
	}
	
}

