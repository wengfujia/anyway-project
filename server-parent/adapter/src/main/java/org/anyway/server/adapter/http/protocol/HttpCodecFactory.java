package org.anyway.server.adapter.http.protocol;

import javax.net.ssl.SSLEngine;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpContentCompressor;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;

import org.anyway.common.uConfigVar;
import org.anyway.common.crypto.SSLContextFactory;
import org.anyway.common.factory.PriorityThreadFactory;
import org.anyway.netty.IdleTimeOutHandler;
import org.anyway.server.adapter.http.handler.HttpHandler;

public class HttpCodecFactory extends ChannelInitializer<SocketChannel> {
	final EventExecutorGroup e1 = new DefaultEventExecutorGroup(uConfigVar.HT_WorkThreadCount, new PriorityThreadFactory("executionLogicHandlerThread+#", Thread.NORM_PRIORITY ));
	
	@Override
	public void initChannel(SocketChannel ch) throws Exception {
	    // Create a default pipeline implementation.
        ChannelPipeline pipeline = ch.pipeline();
        
        //设置IP过滤
        //IpFilterRuleHandler ipFilterRuleHandler = new IpFilterRuleHandler();
        //ipFilterRuleHandler.addAll(new IpFilterRuleList("+i:192.168.*"+ ", -i:*"));
        //p.addLast("ipFilter", ipFilterRuleHandler);
        
        if (uConfigVar.HT_IsHttps) {
            SSLEngine engine = SSLContextFactory.getServerContext().createSSLEngine();
            engine.setUseClientMode(false);
            pipeline.addLast("ssl", new SslHandler(engine));
        }
        
        // 超时设置
	    pipeline.addLast("idleAware", new IdleStateHandler(uConfigVar.HT_RWTimeOut, uConfigVar.HT_RWTimeOut, uConfigVar.HT_IdleTimeOut));
        pipeline.addLast("IdleTimeOutHandler", new IdleTimeOutHandler());
	    /**
	     * http-response解码器
	     * http服务器端对response编码
	    **/
	    pipeline.addLast("encoder", new HttpResponseEncoder());
		/**
		* http-request解码器
		* http服务器端对request解码
		**/
	    pipeline.addLast("decoder", new HttpRequestDecoder());

	    /**usually we receive http message infragment,if we want full http message, 
         * we should bundle HttpObjectAggregator and we can get FullHttpRequest。 
         * 我们通常接收到的是一个http片段，如果要想完整接受一次请求的所有数据，我们需要绑定HttpObjectAggregator，然后我们 
         * 就可以收到一个FullHttpRequest-是一个完整的请求信息。 
        **/  
	    //pipeline.addLast("servercodec",new HttpServerCodec()); 
        pipeline.addLast("aggegator", new HttpObjectAggregator(1024*64));//定义缓冲数据量  
        pipeline.addLast("streamer", new ChunkedWriteHandler());
        
	    /**
	     * 压缩
	     * Compresses an HttpMessage and an HttpContent in gzip or deflate encoding
	     * while respecting the "Accept-Encoding" header.
	     * If there is no matching encoding, no compression is done.
	    **/
	    pipeline.addLast("deflater", new HttpContentCompressor());
        
	    // 自定义业务事件
	    pipeline.addLast(e1, "handler", new HttpHandler());
	}
}
