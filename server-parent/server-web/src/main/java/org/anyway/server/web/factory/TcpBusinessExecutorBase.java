/**
 * 执行tcp业务逻辑的基类
 * 实现Runnable接口
 * 
 * @author wfj
 *
 */

package org.anyway.server.web.factory;

import java.util.concurrent.Callable;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;

import org.anyway.common.enums.CryptEnum;
import org.anyway.common.types.pint;
import org.anyway.common.utils.uLogger;
import org.anyway.exceptions.NoCacheException;
import org.anyway.server.api.CSHTMsgStream;
import org.anyway.server.data.CChrList;
import org.anyway.server.data.packages.HEADER;
import org.anyway.server.data.packages.TCPREQUEST;
import org.anyway.server.web.cache.CacheManager;

public abstract class TcpBusinessExecutorBase implements Callable<Integer> {

	private TCPREQUEST request;

	public CacheManager getCacheManager() {
		try {
			return CacheManager.getInstance();
		} catch (NoCacheException e) {
			uLogger.printInfo(e.getMessage());
			return null;
		}
	}
	
	/**
	 * 设置request
	 * @return
	 */
	public TCPREQUEST getRequest() {
		return request;
	}
	public void setRequest(TCPREQUEST request) {
		this.request = request;
	}
	
	@Override
	public Integer call() {
		return 0;
	}
	
	/**
	 * 返回无包体的消息
	 * @param header
	 * @return
	 */
	protected ChannelFuture Response(HEADER header) {
		ChannelHandlerContext ctx = this.getRequest().getContext();
		if (ctx == null)
			return null;

		CSHTMsgStream streamResp = new CSHTMsgStream();
    	header.setResptype(1);
    	streamResp.EncodeHeader(header);
    	ByteBuf ibuffer = ctx.alloc().buffer(); 
    	int len = streamResp.LoadFromStream(ibuffer, CryptEnum.DES);
    	streamResp.ClearStream();
    	streamResp = null;
    	ChannelFuture retsend = SendPacket(ctx, ibuffer, len);
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
	protected ChannelFuture Response(HEADER header, CChrList list) {
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
	    	CSHTMsgStream streamResp = new CSHTMsgStream();
	    	header.setResptype(1);
	    	streamResp.SetNr(pstr, pstrlen.getInt());
	    	streamResp.EncodeHeader(header);
	    	ByteBuf ibuffer = ctx.alloc().buffer(); 
	    	int len = streamResp.LoadFromStream(ibuffer, CryptEnum.DES);
	    	streamResp.ClearStream();
	    	streamResp = null;
	    	retsend = SendPacket(ctx, ibuffer, len);
	    	if (retsend==null) {
				ctx.close();
	    	}
	    } else {
	    	for (; !list.Eof(); pstr = list.Next(pstrlen)) {
	    		CSHTMsgStream streamResp = new CSHTMsgStream();
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
	    		retsend = SendPacket(ctx, ibuffer, len);
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
	protected ChannelFuture SendPacket(ChannelHandlerContext ctx, ByteBuf ibuffer, int len)
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
			uLogger.printInfo(E.getMessage());
		}
		return ret;
	}
	
}