/**
 * netty相关工具类
 */
package org.anyway.common.utils;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

import io.netty.channel.Channel;

/**
 * @author wengfj
 *
 */
public class NettyUtil {
	
	public static final SocketAddress string2SocketAddress(final String addr) {
        String[] s = addr.split(":");
        InetSocketAddress isa = new InetSocketAddress(s[0], Integer.parseInt(s[1]));
        return isa;
    }
    
	public static final String parseChannelRemoteAddr(final Channel channel) {
        if (null == channel) {
            return "";
        }
        SocketAddress remote = channel.remoteAddress();
        final String addr = remote != null ? remote.toString() : "";

        if (addr.length() > 0) {
            int index = addr.lastIndexOf("/");
            if (index >= 0) {
                return addr.substring(index + 1);
            }

            return addr;
        }

        return "";
    }
	
	public static final String parseChannelRemoteName(final Channel channel) {
        if (null == channel) {
            return "";
        }
        final InetSocketAddress remote = (InetSocketAddress) channel.remoteAddress();
        if (remote != null) {
            return remote.getAddress().getHostName();
        }
        return "";
    }
	
	public static final String parseSocketAddressAddr(SocketAddress socketAddress) {
        if (socketAddress != null) {
            final String addr = socketAddress.toString();

            if (addr.length() > 0) {
                return addr.substring(1);
            }
        }
        return "";
    }

	public static final String parseSocketAddressName(SocketAddress socketAddress) {

        final InetSocketAddress addrs = (InetSocketAddress) socketAddress;
        if (addrs != null) {
            return addrs.getAddress().getHostName();
        }
        return "";
    }
	
}
