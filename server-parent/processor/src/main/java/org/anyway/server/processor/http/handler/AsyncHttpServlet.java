/*
 * 名称: AsyncHttpServlet
 * 描述: HTTP接收与解析类
 * 传入：commandid,user,pwd,body(3DES加密串)
 * 版本：  1.0.1
 * 作者： 翁富家
 * 修改:  翁富家
 * 日期：2013年10月15日
 * 修改日期:2014年1月15日
 * 修改说明：
 * 2014年1月15日
 * 增加sessionid与version两个字段
 * sessionid用于标识终端类型
 */

package org.anyway.server.processor.http.handler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.anyway.common.ProcesserConfig;
import org.anyway.common.enums.CryptEnum;
import org.anyway.common.protocol.HttpMessageCoder;
import org.anyway.common.protocol.body.JBuffer;
import org.anyway.common.protocol.buffer.impl.http.HChrList;
import org.anyway.common.protocol.header.CommandID;
import org.anyway.common.protocol.header.Header;
import org.anyway.common.types.pstring;
import org.anyway.common.utils.JsonUtil;
import org.anyway.common.utils.LoggerUtil;
import org.anyway.common.utils.SecretUtil;
import org.anyway.common.utils.StringUtil;
import org.anyway.server.processor.Providers.Handle;
import org.anyway.server.processor.cache.DBCache;

@SuppressWarnings("serial")
public class AsyncHttpServlet extends HttpServlet {

	private ExecutorService executor = Executors.newFixedThreadPool(ProcesserConfig.getInstance().getHTWorkThreadCount());
	
	@Override	
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
	          throws ServletException, IOException {
		//调用post
		doPost(request, response);
	}
	
	@Override	
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
	          throws ServletException, IOException 
	{
		String sId = request.getParameter("sId");
		if (StringUtil.empty(sId)) { //获取流
			InputStream inputStream = request.getInputStream();
	        ByteArrayOutputStream out = new ByteArrayOutputStream();
	        byte[] b = new byte[1024];
	        int i = 0;

	        while ((i = inputStream.read(b, 0, 1024)) > 0) {
	            out.write(b, 0, i);
	        }
	        sId = out.toString();
		}
		
		int ret = 0;
		String err = "";
		if (StringUtil.empty(sId)) {
			ret = -10;
			err = "非法参数！";
		}
		
		if (ret != 0) {//错误返回，不进入业务处理流程
			String callback = request.getParameter("callback");
			//返回组合包
			if (StringUtil.empty(callback)) {
				callback = SecretUtil.Encrypt3Des(HttpMessageCoder.toJsonString(ret, err));
			} else {
				callback = callback + "(" + SecretUtil.Encrypt3DesToJson(HttpMessageCoder.toJsonString(ret, err)) + ")";
			}
			
			response.setContentType("text/html;charset=UTF-8");
			response.setContentType("application/json");
			PrintWriter out = response.getWriter();
			out.write(callback);
			out.flush();
		} else { //异步调用
			AsyncContext ctx = request.startAsync();
			ctx.setTimeout(ProcesserConfig.getInstance().getHTWaitTimeOut());
			executor.submit(new DecodeMessage(ctx, sId));
		}
	}
	
	//消息分解
	class DecodeMessage implements Runnable {
		final String aId;
		final AsyncContext actx;
		final ServletRequest request;
		
		/**
		 * 构造函数，初始化参数
		 * @param ctx
		 * @param Id
		 */
		DecodeMessage(AsyncContext ctx, String Id){
			this.aId = Id;
			this.actx = ctx;
			this.request = ctx.getRequest();
		}
		
		/**
		 * 消息处理主程序
		 * @return
		 * @throws UnsupportedEncodingException
		 */
		private String HandleMsgStream() throws UnsupportedEncodingException {//synchronized 取消了同步标识
			String sId = SecretUtil.Decrypt3Des(aId);	
			JBuffer<String> LoginBuf = JsonUtil.parseBuffer(sId);

			String result = null;
			
			String ip = request.getRemoteAddr();
			int commandid = LoginBuf.getCommandId(); //LoginMap.get("commandId");
			if (commandid<0) {
				if (LoginBuf!=null) {LoginBuf.Clear(); LoginBuf = null;}
				result = HttpMessageCoder.toJsonString(-10, "非法的头标识号");
				LoggerUtil.println("[http]Fail! ErrorCode:-10,The CommandId Is Error,IP:" + ip);
				return result;
			}
			
			/*参数：
			 * sessionid:0:pc,1:android,2:iphone,3:mac,4:web;
			 * username:登录帐号
			 * password：登录密码
			 * version：版本号
			 * body：内容
			 * */
			String sessionid = LoginBuf.getSessionId();//LoginMap.get("sessionid"); 
			String suser = LoginBuf.getUserName();//LoginMap.get("userName");
			String spwd = LoginBuf.getPassWord();//LoginMap.get("password"); 
			String sversion = LoginBuf.getVersion();//LoginMap.get("version"); 
			String sbody = LoginBuf.getBody();//LoginMap.get("body"); 
			if (LoginBuf!=null) { //清除
				LoginBuf.Clear(); LoginBuf = null;
			}
			//判断用户名与密码
			if (StringUtil.empty(suser) || StringUtil.empty(spwd)) {
				result = HttpMessageCoder.toJsonString(-301, "非法的用户名或密码");
				LoggerUtil.println("[http]Fail! ErrorCode:-301,The User or Password Is Error,IP:" + ip);
				return result;
			}
			//判断版本号
			String key = LoggerUtil.sprintf("VER.%s", sessionid);
		  	String ver = ProcesserConfig.getInstance().GetVerValue("", key);
			if (StringUtil.empty(sversion)==false && ver.compareTo(sversion)>0) {
				/*String err = "您的版本过旧，现在为您升级";
				pstring result1 = new pstring();
	    		pstring result2 = new pstring();
    			Cache.GetErrorInfo(-12, result1, result2);
    			if (!uFunctions.isEmpty(result2.getString())) //获取到错误代码解释
    			{
    				err = result2.getString();
    			}
    			err = ver + uGlobalVar.MSG_SEPATATE + err;*/
				result = HttpMessageCoder.toJsonString(-12, ver);
				return result;
			}
			
			byte[] o_reserve = new byte[1024];
			HttpMessageCoder hstream = new HttpMessageCoder();
			HChrList list = hstream.GetNr();
			
			Header header = hstream.getHeader();
			if (!StringUtil.empty(sessionid))
				header.setSessionid(sessionid);
			header.setIP(ip);
			header.setCommandID(commandid);
			header.setUser(suser);
			header.setPwd(spwd);
			if (!StringUtil.empty(sversion))
				header.setVersion(sversion);

			StringBuffer log = new StringBuffer();
		    int status = 0;
		    
			try {
			    if (commandid == CommandID.TEST) {
			    	hstream.SetStringToBuffer("检测成功");
			    	LoggerUtil.sprintf(log, "[http]Test,User:%s,IP:%s,Mac:%s", suser, ip, "");
			    }else if (commandid == CommandID.INIT_FINAL) {
			    	LoggerUtil.println("[http]Login Init Final!"); 
			    }else {
			    	status = Handle.G_Handle(header, sbody, 1, list, o_reserve);
			    	if (status ==0) {
			    		LoggerUtil.sprintf(log, "[http]Sucess! CommandID:%d,User:%s,Content:%.10s,IP:%s", commandid, suser, sbody, ip);
			    	}else {
			    		//查出错误含义		    			
			    		pstring result1 = new pstring();
			    		pstring result2 = new pstring();
						DBCache.GetErrorInfo(header.getSessionid(), header.getCommandID(), status, result1, result2);
		    			if (!StringUtil.empty(result2.getString())) //获取到错误代码解释
		    			{
		    				list.Clear();
		    				list.Append(result2.getString());
		    			}
			    		LoggerUtil.sprintf(log, "[http]Fail! ErrorCode:%s， CommandID:%d,User:%s,Content:%.10s,IP:%s", status, commandid, suser, sbody, ip);
			    	}
			    } 
			    //打印消息
			    LoggerUtil.println(log.toString());
			    //3.反馈给用户
			    header.setStatus(status);
			    
			    result = hstream.LoadFromStreamString(CryptEnum.NONE);
			} 
			catch (Exception e) {
				result = null;
				e.printStackTrace();
			}
			finally {
				log = null;o_reserve = null;
				if (hstream!=null) {hstream.ClearStream(); hstream=null;}
				if (list!=null) {list.Release(); list = null;}
			}
			return result;
		}
		
		/**
		 * 运行线程
		 */
		@Override
		public void run() {
			try {
				String result = HandleMsgStream();
				if (result != null) 
				{
					ServletResponse response = actx.getResponse();
					
					response.setContentType("text/html;charset=UTF-8");
					String callback = request.getParameter("callback");
					response.setContentType("application/json");
					PrintWriter out = response.getWriter();
					//返回包组合
					if (StringUtil.empty(callback)) {
						callback = SecretUtil.Encrypt3Des(result);
					} else { //这个是jquery，需要进行json再返回，否则收不到
						callback = callback + "(" + SecretUtil.Encrypt3DesToJson(result) + ")";
					}
					
					out.write(callback);
					out.flush();
				}
			} catch (Exception e) {
				LoggerUtil.printInfo(e.getMessage());
			}		
			actx.complete();//完成异步
		}
	}

}
