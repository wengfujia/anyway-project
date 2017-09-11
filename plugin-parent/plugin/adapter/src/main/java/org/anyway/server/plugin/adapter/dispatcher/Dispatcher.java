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

package org.anyway.server.plugin.adapter.dispatcher;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.anyway.common.AdapterConfig;
import org.anyway.common.future.InvokeCallback;
import org.anyway.common.future.ResponseFuture;
import org.anyway.common.protocol.request.BaseRequest;
import org.anyway.common.protocol.request.HttpRequest;
import org.anyway.common.protocol.request.TcpRequest;
import org.anyway.common.utils.ClassUtil;
import org.anyway.common.utils.LoggerUtil;
import org.anyway.server.plugin.adapter.BusinessBaseExecutor;

public class Dispatcher {

	private static ExecutorService dispatcherService = null;
	static{
		int treadMaxCount = 0;
		if (AdapterConfig.getInstance().getUSActive()) {
			treadMaxCount += AdapterConfig.getInstance().getUSWorkThreadCount();
		}
		if (AdapterConfig.getInstance().getHTActive()) {
			treadMaxCount += AdapterConfig.getInstance().getHTWorkThreadCount();
		}
		dispatcherService = Executors.newFixedThreadPool(treadMaxCount);
	}
	
	/**
	 * 使用sumbit业务分发
	 * @param request
	 * @param msgType
	 * @param invokeCallback
	 * @return
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static <T> int submit(T request, int msgType, InvokeCallback invokeCallback) throws InstantiationException, IllegalAccessException {
		int result = 0;
		Class<?> executorClass = ClassUtil.getExecutorClassByType(msgType);
		if (executorClass==null && AdapterConfig.getInstance().getDefaultResponseCommandId()>=0) { //判断是否有默认返回类  2015.9.22
			executorClass = ClassUtil.getExecutorClassByType(AdapterConfig.getInstance().getDefaultResponseCommandId());
		}
		if (executorClass != null) {
			BusinessBaseExecutor executor = (BusinessBaseExecutor) executorClass.newInstance();
			if (request instanceof HttpRequest<?>) {
				((HttpRequest<String>)request).setTimeOutMillis(AdapterConfig.getInstance().getHTIdleTimeOut());
				executor.setRequest(request);
			}
			else if (request instanceof TcpRequest) {
				((TcpRequest) request).setTimeOutMillis(AdapterConfig.getInstance().getUSIdleTimeOut());
				executor.setRequest(request);
			}
			if (null != invokeCallback) {
				executor.setInvokeCallback(invokeCallback);
			}
			
			dispatcherService.submit(executor);
		}
		else {
			LoggerUtil.printInfo("没有找到" + msgType + "相应的执行类");
			result = -10;
		}
		
		if (null != invokeCallback) {
			invokeCallback.operationComplete(new ResponseFuture(result, msgType));
		}
		return result;
	}
	
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
	public static <T> int submit(T request, int msgType) throws InstantiationException, IllegalAccessException {
		return submit(request, msgType, ((BaseRequest)request).getInvokeCallback());
	}
	
}