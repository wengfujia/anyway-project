/**
 * 抽象了分发器
 * 多线程执行
 * 某个消息对象msgObject指定某个业务逻辑对象executor
 * submit到线程池中
 * @author wfj
 * 
 * 2015.9.22
 * 如果没有找到相应的类，则判断是否有默认
 *
 */

package org.anyway.server.web.dispatcher;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.anyway.common.uConfigVar;
import org.anyway.common.utils.uClassUtil;
import org.anyway.common.utils.uLogger;
import org.anyway.server.data.packages.HTTPREQUEST;
import org.anyway.server.data.packages.TCPREQUEST;
import org.anyway.server.web.factory.HttpBusinessExecutorBase;
import org.anyway.server.web.factory.TcpBusinessExecutorBase;

public class Dispatcher {

	private static ExecutorService httpdispatcherService = Executors.newFixedThreadPool(uConfigVar.HT_WorkThreadCount);
	private static ExecutorService tcpdispatcherService = Executors.newFixedThreadPool(uConfigVar.US_WorkThreadCount);
	
	/**
	 * 用于http业务分发
	 * @param <T>
	 * @param context
	 * @param request
	 * @param msgType
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	public static int submit(HTTPREQUEST<String> request, int msgType) throws InstantiationException, IllegalAccessException {
		int result = 0;
		Class<?> executorClass = uClassUtil.getExecutorClassByType(msgType);
		if (executorClass==null && uConfigVar.DEF_RESPONSE>=0) { //判断是否有默认返回类  2015.9.22
			executorClass = uClassUtil.getExecutorClassByType(uConfigVar.DEF_RESPONSE);
		}
		if (executorClass!=null) {
			HttpBusinessExecutorBase executor = (HttpBusinessExecutorBase) executorClass.newInstance();
			executor.setRequest(request);
			//执行
			httpdispatcherService.submit(executor);
		}
		else {
			uLogger.printInfo("没有找到http相应的执行类");
			result = -10;
		}
		return result;
	}
	
	/**
	 * 用于socket业务分发
	 * @param request
	 * @param msgType
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	public static int submit(TCPREQUEST request, int msgType) throws InstantiationException, IllegalAccessException {
		int result = 0;
		Class<?> executorClass = uClassUtil.getExecutorClassByType(msgType);
		if (executorClass==null && uConfigVar.DEF_RESPONSE>=0) { //判断是否有默认返回类  2015.9.22
			executorClass = uClassUtil.getExecutorClassByType(uConfigVar.DEF_RESPONSE);
		}
		if (executorClass!=null) {
			TcpBusinessExecutorBase executor = (TcpBusinessExecutorBase) executorClass.newInstance();
			executor.setRequest(request);
			//执行
			tcpdispatcherService.submit(executor);
		}
		else { 
			uLogger.printInfo("没有找到tcp相应的执行类");
			result = -10;
		}
		uLogger.printInfo(executorClass.getName());
		return result;
	}
	
}