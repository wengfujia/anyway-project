/*
 * 名称: WebMessageExecutor
 * 描述: web消息处理器，
 * 说明: 
 * 		1、先进行消息合法过滤
 * 		2、转换成网络传输包
 * 		3、判断是否有可用的hbase连接
 * 		4、有可用连接，通过socket把数据传至hbase，并把缓存移入已处理
 * 		5、如果没有可用连接，把缓存移回到等待缓存区
 * 版本：  1.0.0
 * 作者： 翁富家
 * 修改:
 * 日期：2015年05月28日
 * 
 * 2016.9.8
 * doDecode函数中的Dispatcher.submit改成：Dispatcher.execute
 */

package io.box.web.executor;

import io.netty.buffer.ByteBuf;
import io.netty.util.internal.StringUtil;

import java.io.UnsupportedEncodingException;

import org.anyway.common.MessageAnnotation;
import org.anyway.common.uConfigVar;
import org.anyway.common.enums.CryptEnum;
import org.anyway.common.types.pstring;
import org.anyway.common.utils.uLogger;
import org.anyway.common.utils.uStringUtil;
import org.anyway.server.web.cache.CacheManager;
import org.anyway.server.web.common.uLoadVar;
import org.anyway.server.web.dispatcher.Dispatcher;
import org.anyway.server.web.factory.HttpBusinessExecutorBase;
import org.anyway.server.api.CSHTMsgStream;
import org.anyway.server.api.HSHTMsgStream;
import org.anyway.server.data.packages.COMMANDID;
import org.anyway.server.data.packages.HEADER;
import org.anyway.server.data.packages.HTTPREQUEST;
import org.anyway.server.data.packages.json.JBuffer;
import org.anyway.server.utils.uJsonUtil;

@MessageAnnotation(msgType = COMMANDID.WEB_REQUEST)
public class WebMessageExecutor extends HttpBusinessExecutorBase {

	private CacheManager cachemanager = null;
	private JBuffer<String> LoginBuf = null;
	
	/**
	 * 消息过滤 是否有效消息：true有效，false无效
	 * 
	 * @return
	 */
	private int doFilter() {
		int status = 0;

		//解析包
		try {
    		LoginBuf = uJsonUtil.parseBuffer(getRequest().getJBody().getBody());
    	}
    	catch (Exception e) {
    		status = -23;
    		uLogger.printInfo(e.getMessage());
		}
		
		if (status == 0) {
			//判断用户名与密码
			if (uStringUtil.empty(LoginBuf.getUserName())) {// || uStringUtils.empty(LoginBuf.getPassWord())) {
				status = -18;
			}
			else {
				//判断版本号
				String key = uLogger.sprintf("VER.%s", LoginBuf.getSessionId());
			  	String ver = uLoadVar.GetVerValue("", key);
				if (StringUtil.isNullOrEmpty(ver) || ver.compareTo(LoginBuf.getVersion())>0) {
					uLogger.sprintf("错误的版本号,sessionid:%s", LoginBuf.getSessionId());
					status = -12;
				}
			}	
		}
		
		if (status != 0) {
			this.sendError(status);//发送错误消息
		}
		return status;
	}

	/**
	 * 消息分解
	 */
	private int doDecode(String commandValue, ByteBuf bytebuf) {
		int status = 0;
		byte[] buffer = null;
		
		//判断业务是否需要传入hbase服务端
		if ("HBASE".equalsIgnoreCase(commandValue)) { //需要转到数据库服务层处理
			String body = LoginBuf.getBody();
			if (uStringUtil.empty(body)==false) {
				try {
					buffer = body.getBytes(uConfigVar.CharsetName); //转换成byte[]
				} catch (UnsupportedEncodingException e) {
					status = -23;
					uLogger.printInfo(e.getMessage());
				} catch (Exception e) {
					status = -23;
					uLogger.printInfo(e.getMessage());
				}
			}
		}
		else if ("DECODE".equalsIgnoreCase(commandValue)) { //需要进行解码处理
			buffer = super.decodeMessage(LoginBuf.getCommandId(), LoginBuf.getBody());
		}	
		
		//返回结果
	  	if (status == 0) {
	  		// 组合tcp网络包
			HEADER header = new HEADER();
			header.setCommandID(LoginBuf.getCommandId());
			header.setStatus(0);
			header.setResptype(1);
			header.setUser(LoginBuf.getUserName());
			header.setPwd(LoginBuf.getPassWord());
			header.setSessionid(LoginBuf.getSessionId());
			header.setSequence(getRequest().getID());
			header.setVersion(LoginBuf.getVersion());
			CSHTMsgStream result = new CSHTMsgStream();
			if (null != buffer) {
				result.SetNr(buffer, buffer.length);
			}
			result.EncodeHeader(header);
			int len = result.LoadFromStream(bytebuf, CryptEnum.DES);
			if (len <= 0) {
				status = -23;
			}
			//清空缓存
			result.ClearStream();
			result = null;
	  	}
	  	else {
	  		this.sendError(status);
	  	}
		return status;
	}
	
	/**
	 * 跳转到数据层
	 * @param commandValue
	 * @return
	 */
	private int route(String commandValue) {
		ByteBuf ibuffer = this.getRequest().getContext().alloc().buffer();
		int status = doDecode(commandValue, ibuffer); // 进行解码，转换成内部业务消息包
		if (status == 0) {
			// 发送到数据层
			sendTo(ibuffer);
			this.cachemanager.getHttpCache().replaceDone(this.getRequest());
		}
		return status;
	}

	/**
	 * 执行业务
	 */
	@Override
	public Integer call() {
		//判断缓存是否有效并连接是否合法
		if (null == this.getCacheManager() || null == this.getRequest()) {
			uLogger.printInfo("找不到相应的缓存");
			return -10;
		}

		cachemanager = this.getCacheManager();
		int status = doFilter();
		if (status == 0) {
			int commandId = LoginBuf.getCommandId();
			String key = uLogger.sprintf("CMD.%d", commandId);
			String commandValue = uLoadVar.GetValue("", key);
			if (uStringUtil.empty(commandValue)) { //业务头为空
				status = -20;
		  	}
			else if ("LOCAL".equalsIgnoreCase(commandValue)) { //直接处理本地业务逻辑
		  		try {
		  			status = Dispatcher.<HTTPREQUEST<String>>submit(this.getRequest(), commandId);
				} catch (InstantiationException | IllegalAccessException e) {
					status = -23;
					uLogger.printInfo(e.getMessage());
				} catch (Exception e) {
					status = -23;
					uLogger.printInfo(e.getMessage());
				}
		  	}
			else {
				return route(commandValue);
			}
		}
		this.cachemanager.getHttpCache().removeDone(this.getRequest().getID());
		return status;
	}
	
	/**
	 * 发送错误消息
	 * @param status
	 */
	void sendError(int status) {
		//获取错误解释
  		pstring description = new pstring(), response = new pstring();
  		this.cachemanager.GetErrorInfo(status, description, response);
		super.sendResponse(HSHTMsgStream.toJsonString(-12, response.getString()));
		this.Release();
	}
	
	/**
	 * 清空缓存
	 */
	void Release() {
		if (null != LoginBuf) {
			LoginBuf.Clear();
			LoginBuf = null;	
		}
	}
}
