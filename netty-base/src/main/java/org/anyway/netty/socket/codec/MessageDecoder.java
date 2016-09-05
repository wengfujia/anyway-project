/*
 * 名称: MessageDecoder.java
 * 描述: 网络包解码类
 * 版本：  1.0.0
 * 作者： 翁富家
 * 修改:
 * 日期：2013年08月20日
 * 修改日期:
 */

package org.anyway.netty.socket.codec;

import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import org.anyway.common.utils.uNetUtils;

public class MessageDecoder extends ByteToMessageDecoder {
	private final String charsetname;

	public MessageDecoder(String CharsetName) {
		this.charsetname = CharsetName;
	}

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
		
		if (!ctx.channel().isActive()) {
            in.clear();
            return;
        }

		// Wait until the length prefix is available.
        if (in.readableBytes() < 7) {
            return;
        }

        in.markReaderIndex();
        byte[] sizeBytes = new byte[7];
        in.getBytes(0, sizeBytes);//, 0, 7
        int dataLength = uNetUtils.chars2int(sizeBytes, charsetname);
        //Wrong the packet length
        if (dataLength==0) {
        	in.clear();
        	return;
        }
        // Wait until the whole data is available.
        if (in.readableBytes() < dataLength) {
            in.resetReaderIndex();
            return;
        }

        // Convert the received data into a new BigInteger.
        byte[] decoded = new byte[dataLength];
        in.readBytes(decoded);

        out.add(decoded);
	}
}
