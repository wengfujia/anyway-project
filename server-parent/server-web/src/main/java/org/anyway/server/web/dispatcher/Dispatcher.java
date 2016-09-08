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
 * 2016.9.8
 * submit函数request参数改为T,增加了execute函数
 * submit,execute区别是
 * 	submit可以反馈线程执行结果（我没有实现），其中一个线程出错，整个线程池将不在运行
 * 	execute不反馈运行结果
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
	 * 使用sumbit业务分发
	 * @param <T>
	 * @param <T>
	 * @param context
	 * @param request
	 * @param msgType
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	@SuppressWarnings("unchecked")
	public static <T> int submit(T request, int msgType) throws InstantiationException, IllegalAccessException {
		int result = 0;
		Class<?> executorClass = uClassUtil.getExecutorClassByType(msgType);
		if (executorClass==null && uConfigVar.DEF_RESPONSE>=0) { //判断是否有默认返回类  2015.9.22
			executorClass = uClassUtil.getExecutorClassByType(uConfigVar.DEF_RESPONSE);
		}
		if (executorClass!=null) {
			if (request instanceof HTTPREQUEST<?>) {				
				HttpBusinessExecutorBase executor = (HttpBusinessExecutorBase) executorClass.newInstance();
				executor.setRequest((HTTPREQUEST<String>)request);
				//执行
				httpdispatcherService.submit(executor);
			}
			else if (request instanceof TCPREQUEST) {				
				TcpBusinessExecutorBase executor = (TcpBusinessExecutorBase) executorClass.newInstance();
				executor.setRequest((TCPREQUEST)request);
				//执行
				tcpdispatcherService.submit(executor);
			}
		}
		else {
			uLogger.printInfo("没有找到"+msgType+"相应的执行类");
			result = -10;
		}
		return result;
	}
	
	/**
	 * 使用execute业务分发
	 * @param <T>
	 * @param <T>
	 * @param context
	 * @param request
	 * @param msgType
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	@SuppressWarnings("unchecked")
	public static <T> int execute(T request, int msgType) throws InstantiationException, IllegalAccessException {
		int result = 0;
		Class<?> executorClass = uClassUtil.getExecutorClassByType(msgType);
		if (executorClass==null && uConfigVar.DEF_RESPONSE>=0) { //判断是否有默认返回类  2015.9.22
			executorClass = uClassUtil.getExecutorClassByType(uConfigVar.DEF_RESPONSE);
		}
		if (executorClass!=null) {
			if (request instanceof HTTPREQUEST<?>) {				
				HttpBusinessExecutorBase executor = (HttpBusinessExecutorBase) executorClass.newInstance();
				executor.setRequest((HTTPREQUEST<String>)request);
				//执行
				httpdispatcherService.execute(executor);
			}
			else if (request instanceof TCPREQUEST) {				
				TcpBusinessExecutorBase executor = (TcpBusinessExecutorBase) executorClass.newInstance();
				executor.setRequest((TCPREQUEST)request);
				//执行
				tcpdispatcherService.execute(executor);
			}
		}
		else {
			uLogger.printInfo("没有找到"+msgType+"相应的执行类");
			result = -10;
		}
		return result;
	}

}