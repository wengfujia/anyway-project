/*
 * 名称: MessageEncoder.java
 * 描述: 网络包加码类
 * 版本：  1.0.0
 * 作者： 翁富家
 * 修改:
 * 日期：2013年08月20日
 * 修改日期:
 */

package org.anyway.netty.socket.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class MessageEncoder extends MessageToByteEncoder<ByteBuf> {
	
	/*public MessageEncoder(){
        super(false);
    }*/
	
    @Override
    protected void encode(ChannelHandlerContext ctx, ByteBuf buf, ByteBuf out) throws Exception
    {
    	out.writeBytes(buf, buf.readerIndex(), buf.readableBytes());
    	buf.clear();
    }

}