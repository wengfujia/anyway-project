/*
 * 名称: DispatcherExecutor
 * 描述: 业务相关定时器
 * 版本：  1.0.0
 * 作者： 翁富家
 * 日期：2015年1203月29日
 * 
 * 修改:
 * 		2015.12.3 
 * 		取消定时业务逻辑处理器
 * 
 */

package org.anyway.server.web.cache.thread;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.anyway.common.uConfigVar;
import org.anyway.common.uGlobalVar;
import org.anyway.exceptions.NoCacheException;
import org.anyway.server.data.packages.HTTPREQUEST;
import org.anyway.server.web.cache.CacheManager;
import org.anyway.server.web.dispatcher.Dispatcher;

import net.sf.ehcache.search.Result;
import net.sf.ehcache.search.Results;

public class DispatcherExecutor {
	
	//定时任务线程
	private ScheduledExecutorService scheduExec = null; 

	private static DispatcherExecutor INSTANCE;
	
	public DispatcherExecutor() throws NoCacheException {
		//任务线程池
		if (null == scheduExec) {
			scheduExec = Executors.newScheduledThreadPool(4);// .newSingleThreadScheduledExecutor();
		}
	}
	
	/**
	 * 获取Instance
	 * @return Instance
	 * @throws NoCacheException 
	 * @throws Exception 
	 */
	public static void Start() throws NoCacheException {
		if (INSTANCE == null) {
			INSTANCE = new DispatcherExecutor();
		}
		INSTANCE.lanuchTask();
	}
	
	/**
	 * 开启定时任务
	 * @throws NoCacheException
	 */
    private void lanuchTask() throws NoCacheException {
//        scheduExec.scheduleAtFixedRate(new logictask(), 1000, 100, TimeUnit.MILLISECONDS); 
        scheduExec.scheduleAtFixedRate(new cleantask(), 1500, 1800, TimeUnit.MILLISECONDS); //1.8秒清理一次
    }
    
	/**
	 * 等待业务定时任务
	 * @author wengfj
	 *
	 */
//	class logictask implements Runnable{  
//		
//		private CacheManager manager;
//		
//		protected logictask() throws NoCacheException {
//			this.manager = CacheManager.getInstance();
//		}
//		
//		/**
//		 * 判断连接是否有效
//		 * @return
//		 */
//		private boolean isValid(HTTPREQUEST<String> request) {
//			boolean result = false;
//			if (request.getHttpMethod().equals(HttpMethod.POST)) { //http post需要判断连接通道是否允许写
//				result = request.getContext().channel().isActive();
//			}
//			return result;
//		}
//		
//		@Override
//	    public void run() {
//			for (Object key:manager.getHttpCache().getWaitKeys()) {
//        		HTTPREQUEST<String> request = manager.getHttpCache().WaitCache().get((String)key);
//            	try {
//            		if (isValid(request)) {
//            			Dispatcher.submit(request, request.getJBody().getCommandId());
//            		}
//            		else {
//            			request.Close();
//            		}
//				} catch (InstantiationException e) {
//					e.printStackTrace();
//				} catch (IllegalAccessException e) {
//					e.printStackTrace();
//				}
//            	//从等待线程池中移除
//            	manager.getHttpCache().removeQuiteWait((String)key);
//        	}
//	    }
//	} 
	
	/**
	 * 消息处理清理任务  由ehcache缓存进行清理
	 * @author wengfj
	 *
	 */
	class cleantask implements Runnable{  
		   
		private CacheManager manager;
		
		protected cleantask() throws NoCacheException {
			this.manager = CacheManager.getInstance();
		}
		
		@Override
	    public void run() {
	    	Results results = manager.getHttpCache().queryTimeOut(uConfigVar.HT_IdleTimeOut);
	    	List<Result> resultList = results.all();
	    	if (resultList != null && !resultList.isEmpty()) {
	    		for (Result result : resultList) {
		    		@SuppressWarnings("unchecked")
					HTTPREQUEST<String> request = (HTTPREQUEST<String>)result.getValue();
		    		if (request.getRetry() >= uGlobalVar.RETRY || !request.getContext().channel().isActive()) {
		    			//从处理线程池中移除
	            		manager.getHttpCache().removeDone(request.getID());
	            		continue;
		    		}
		    		
		    		try {
						int status = Dispatcher.<HTTPREQUEST<String>>submit(request, request.getJBody().getCommandId());
						if (status == 0) {
							//设置为等待状态
				    		request.setWait();
						}
		    		} catch (InstantiationException | IllegalAccessException e) {
						e.printStackTrace();
					} catch (Exception e) {
						e.printStackTrace();
					}
		    		
//		    		//放回等待缓存池
//            		request.setWait();
//            		manager.getHttpCache().addWait(request);
//            		//从处理线程池中移除
//            		manager.getHttpCache().removeQuiteDone(request.getID());
		    	}
	    	}
	    }
		
	}
	
}
