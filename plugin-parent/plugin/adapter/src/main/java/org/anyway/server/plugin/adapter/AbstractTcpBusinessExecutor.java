/**
 * 执行tcp业务逻辑的基类
 * 实现Runnable接口
 * 
 * @author wfj
 *
 */

package org.anyway.server.plugin.adapter;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;

import org.anyway.common.AdapterConfig;
import org.anyway.common.SystemConfig;
import org.anyway.common.enums.CryptEnum;
import org.anyway.common.protocol.TcpMessageCoder;
import org.anyway.common.protocol.buffer.impl.CChrList;
import org.anyway.common.protocol.header.Header;
import org.anyway.common.protocol.request.TcpRequest;
import org.anyway.common.types.pint;
import org.anyway.common.types.pstring;
import org.anyway.common.utils.LoggerUtil;
import org.anyway.common.utils.NetUtil;

public abstract class AbstractTcpBusinessExecutor extends BusinessBaseExecutor<TcpRequest> {

	/**
	 * 返回无包体的消息
	 * @param header
	 * @return
	 */
	protected ChannelFuture Response(Header header) {
		ChannelHandlerContext ctx = this.getRequest().getContext();
		if (ctx == null)
			return null;
		
		pstring description = new pstring(), response = new pstring();
		
		byte[] pstr = null;
		try {
			getCacheManager().GetErrorInfo(header.getStatus(), description, response);
			pstr = NetUtil.getBytes(response.getString(), SystemConfig.CharsetName);
		} catch (Exception e) {
			LoggerUtil.printInfo(e.getMessage());
			return null;
		}

		TcpMessageCoder streamResp = new TcpMessageCoder(AdapterConfig.getInstance().getUSMaxSendBufferSize());
    	header.setResptype(1);
    	streamResp.EncodeHeader(header);
    	streamResp.SetNr(pstr, pstr.length);
    	
    	ByteBuf ibuffer = ctx.alloc().buffer(); 
    	int len = streamResp.LoadFromStream(ibuffer, CryptEnum.DES);
    	streamResp.ClearStream();
    	streamResp = null;
    	ChannelFuture retsend = sendPacket(ctx, ibuffer, len);
    	if (retsend==null) {
			ctx.close();
    	}
		return retsend;	
	}
	
	/**
	 * 返回消息
	 * @param header
	 * @param hlist
	 * @return
	 */
	protected ChannelFuture Response(Header header, CChrList list) {
		if (null == list) { //判断包体是否为空
			return Response(header);
		}
		
		ChannelHandlerContext ctx = this.getRequest().getContext();
		if (ctx == null)
			return null;

		ChannelFuture retsend = null;
	    pint pstrlen = new pint(0);
	    byte[] pstr=list.First(pstrlen);
	    if (list.Count()<=1) {
	    	TcpMessageCoder streamResp = new TcpMessageCoder(AdapterConfig.getInstance().getUSMaxSendBufferSize());
	    	header.setResptype(1);
	    	streamResp.SetNr(pstr, pstrlen.getInt());
	    	streamResp.EncodeHeader(header);
	    	ByteBuf ibuffer = ctx.alloc().buffer(); 
	    	int len = streamResp.LoadFromStream(ibuffer, CryptEnum.DES);
	    	streamResp.ClearStream();
	    	streamResp = null;
	    	retsend = sendPacket(ctx, ibuffer, len);
	    	if (retsend==null) {
				ctx.close();
	    	}
	    } else {
	    	for (; !list.Eof(); pstr = list.Next(pstrlen)) {
	    		TcpMessageCoder streamResp = new TcpMessageCoder(AdapterConfig.getInstance().getUSMaxSendBufferSize());
	    		if (list.IsLast())
	    			header.setResptype(1);
	    		else
	    			header.setResptype(0);
	    		streamResp.SetNr(pstr, pstrlen.getInt());
	    		streamResp.EncodeHeader(header);
	    		ByteBuf ibuffer = ctx.alloc().buffer(); 
	    		int len = streamResp.LoadFromStream(ibuffer, CryptEnum.DES);
	    		streamResp.ClearStream();
	    		streamResp = null;
	    		retsend = sendPacket(ctx, ibuffer, len);
	    		if (retsend==null) { 
					ctx.close();
					break;
	    		}
	    	}
	    }
		return retsend;
	}
	
	/**
	 * SendPacket
	 * @param iosession
	 * @param buffer
	 * @param len
	 * @return WriteFuture
	 */
	protected ChannelFuture sendPacket(ChannelHandlerContext ctx, ByteBuf ibuffer, int len)
	{
		if (ctx==null)
			return null;
		
		ChannelFuture ret = null;
		try
		{
			if (ctx.channel().isWritable())
				ret = ctx.writeAndFlush(ibuffer);
			else
				ctx.close();
		}
		catch (Exception E)
		{
			LoggerUtil.printInfo(E.getMessage());
		}
		return ret;
	}
	
}