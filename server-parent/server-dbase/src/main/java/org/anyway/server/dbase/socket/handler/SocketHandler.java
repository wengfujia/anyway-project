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

package org.anyway.server.dbase.socket.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import java.net.InetSocketAddress;

import org.anyway.netty.socket.handler.SimpleDefaultHandler;
import org.anyway.server.api.CSHTMsgStream;
import org.anyway.server.data.CChrList;
import org.anyway.server.data.packages.COMMANDID;
import org.anyway.server.data.packages.DBHEADER;
import org.anyway.server.data.packages.HEADER;
import org.anyway.server.dbase.Providers.Handle;
import org.anyway.server.dbase.cache.DBCache;
import org.anyway.server.dbase.common.uLoadVar;
import org.anyway.common.uConfigVar;
import org.anyway.common.enums.CryptEnum;
import org.anyway.common.types.pint;
import org.anyway.common.types.pstring;
import org.anyway.common.utils.uLogger;
import org.anyway.common.utils.uNetUtils;
import org.anyway.common.utils.uStringUtils;

public class SocketHandler extends SimpleDefaultHandler {

    // 当服务器端发送的消息到达时:
    @Override
    public void channelRead0(ChannelHandlerContext ctx, byte[] msg) {

    	//m_lastRecvTime = uFunctions.time();
    	int ret = 0;
    	CSHTMsgStream shtStream = new CSHTMsgStream();
    	//分解头与体
    	int ilen = shtStream.GetLength(msg);
		if (ilen != msg.length) {
    		uLogger.println("[socket]Receive The Wrong Message");
    		ctx.close();
    		return;
    	}
    	ret = shtStream.SaveToStream(msg, msg.length, CryptEnum.DES);
    	
    	if (ret > 0) 
    		HandleMsgStream(ctx, shtStream);
    	
		if (shtStream!=null) {shtStream.ClearStream();shtStream = null;}
    }

	//////////////////////////////////////////////////////////////////
	//下面不同应用程序不同
	///////////////////////////////////////////////////////////////////
    /**
     * HandleMsgStream
     * @param iosession
     * @param stream
     */
	private void HandleMsgStream(final ChannelHandlerContext ctx, CSHTMsgStream stream) {

		String clientIP =((InetSocketAddress)ctx.channel().remoteAddress()).getAddress().getHostAddress();
		//1.处理
		pint nrlen =new pint(0);
		byte[] reserve =null;// new byte[uGlobalVar.MAX_READBUFFER_SIZE];
		byte[] nr = stream.GetNr(nrlen);
		
		CChrList list = new CChrList();
		byte[] o_reserve = new byte[1024];
		DBHEADER dbheader = stream.GetDbHeader();
		
		StringBuffer log = new StringBuffer();
	    int commandid = stream.GetCommand();
	    int status = 0;
	    
		HEADER header = stream.getHeader();	
		header.setIP(clientIP);//设置IP
		try {    
			if (commandid == COMMANDID.TEST) {
		    	//list.Append(NetUtils.getBytes("[socket]Test Ok", uGlobalVar.CharsetName));
		    	uLogger.sprintf(log, "[socket]Test,User:%s,IP:%s,Mac:%s", header.getUser(), clientIP, header.getReserve());
		    } else if (commandid == COMMANDID.INIT_FINAL) {
		    	uLogger.println("[socket]Login Init Final!");
		    } else {
		    	//获取reserve
		    	String key = uLogger.sprintf("VER.%s", header.getSessionid());
			  	String ver = uLoadVar.GetVerValue("", key);
			  	//返回版本号\t错误说明
		    	if (ver.compareTo(header.getVersion())>0) {//判断版本
					status = -12;
					list.Append(ver); //返回当前版本号
			  	} else {
			  		status = Handle.G_Handle(header,dbheader,nr,nrlen.getInt(),reserve,1,list,o_reserve);			    		
			  	}
		    	
		    	if (status ==0) {
		    		uLogger.sprintf(log, "[socket]Sucess! CommandID:%s,User:%s,Content:%.10s,IP:%s", String.valueOf(header.getCommandID()), header.getUser(), uNetUtils.getString(nr, uConfigVar.CharsetName), clientIP);
		    	} else if (status != -12) {
		    		//查出错误含义
		    		if (list.Count()==0) {
		    			pstring result1 = new pstring();
		    			pstring result2 = new pstring();
		    			DBCache.GetErrorInfo(status, result1, result2);
		    			if (uStringUtils.empty(result2.getString())==false) //获取到错误代码解释
		    			{
		    				list.Append(result2.getString());
		    			}
		    		}
		    		uLogger.sprintf(log, "[socket]Fail! ErrorCode:%s， CommandID:%s,User:%s,Content:%.10s,IP:%s", status, header.getCommandID(), header.getUser(), uNetUtils.getString(nr, uConfigVar.CharsetName), clientIP);		    		
		    	}
		    }
	
		    //3.反馈给用户
			ChannelFuture retsend = null;
		    pint pstrlen = new pint(0);
		    byte[] pstr=list.First(pstrlen);
		    if (list.Count()<=1) {
		    	CSHTMsgStream streamResp = new CSHTMsgStream();
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
		    	retsend = SendPacket(ctx, ibuffer, len);//pstrlen.getInt()
		    	if (retsend==null) {
					uLogger.sprintf(log,"[socket]Fail To SendPacket,IP:%s",clientIP);
					ctx.close();
		    	}
		    } else {
		    	CSHTMsgStream streamResp = new CSHTMsgStream();
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
		    		retsend = SendPacket(ctx, ibuffer, len); //pstrlen.getInt()
		    		if (retsend==null) { 
						uLogger.sprintf(log,"[socket]Fail To SendPacket,IP:%s", clientIP);
						ctx.close();
						break;
		    		}
		    	}
		    	streamResp.ClearStream();
	    		streamResp = null;
		    }
		    //打印消息
		    if (log.length()>0)
		    	uLogger.println(log.toString());  
		} 
		catch (Exception e) {
			uLogger.println(e.getMessage());
			ctx.close();
		}
		finally {
			reserve=null;o_reserve = null;log = null;
			if (list!=null) {list.ClearAll();list = null;}
		}
	}

}
