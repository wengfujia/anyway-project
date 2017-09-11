/**
 * 
 */
package org.anyway.server.adapter.moniter;

import java.util.Map;

import org.anyway.common.AdapterConfig;
import org.anyway.common.SystemConfig;
import org.anyway.common.enums.CryptEnum;
import org.anyway.common.enums.StatusEnum;
import org.anyway.common.models.IpTableBean;
import org.anyway.common.protocol.TcpMessageCoder;
import org.anyway.common.protocol.header.CommandID;
import org.anyway.common.protocol.header.Header;
import org.anyway.common.types.pint;
import org.anyway.common.utils.LoggerUtil;
import org.anyway.common.utils.StringUtil;
import org.anyway.server.adapter.cache.CacheManager;
import org.anyway.server.plugin.adapter.client.Client2Processor;

import io.netty.buffer.ByteBuf;

/*
 * 名称: ProcessorMoniter
 * 描述: 处理层服务端健康检查
 * 版本：  1.0.0
 * 作者： 翁富家
 * 日期：2017年09月02日
 * 
 */
public class ProcessorMoniter implements Runnable {

	/**
	 * 
	 */
	@Override
	public void run() {
		while (true) {
			LoggerUtil.println("正在检测。。。。。。。。");
			try {
				Thread.sleep(10000);
				CacheManager manager = CacheManager.getInstance();
				for (Map.Entry<String, IpTableBean> route : manager.getConfigCache().getRoutesCache().entrySet()) {
					IpTableBean ipTableBean = route.getValue();
					if (health(ipTableBean.getAddress(), ipTableBean.getPort())) {
						LoggerUtil.println("检测成功：" + ipTableBean.getAddress() + "：" + ipTableBean.getPort());
						//心跳成功，把连接源改为有效
						if (StatusEnum.INVALID.getValue() == ipTableBean.getStatus()) {
							ipTableBean.setStatus(StatusEnum.EFFECTIVE.getValue());
						}
					} else if (StatusEnum.EFFECTIVE.getValue() == ipTableBean.getStatus()) {
						LoggerUtil.println("检测失败：" + ipTableBean.getAddress() + "：" + ipTableBean.getPort());
						// 心跳失败，把连接源改为无效
						ipTableBean.setStatus(StatusEnum.INVALID.getValue());
					}
				}
			} catch (Exception e) {
				LoggerUtil.getLogger().error("ProcessorMoniter, Exception:", e);
			}
		}
	}

	private boolean health(String host, int port) {
		Header header = new Header();
		header.setStatus(0);
		header.setResptype(1);
		header.setSequence(StringUtil.getUUID());
		header.setCommandID(CommandID.TEST);
		header.setUser("test");
		header.setPwd("test");
		TcpMessageCoder result = new TcpMessageCoder(AdapterConfig.getInstance().getUSMaxSendBufferSize());
		result.EncodeHeader(header);
		//检测报文
		pint len = new pint();
		ByteBuf bytebuf = result.LoadFromStream(len, CryptEnum.DES);
		//发送报文
		Client2Processor client = new Client2Processor(host, port);
		return client.send(bytebuf, SystemConfig.RETRY);
	}
	
}
