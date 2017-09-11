/*
 * 名称: MoniterManager
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

package org.anyway.server.adapter.moniter;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.anyway.exceptions.NoCacheException;

public class MoniterManager {
	
	private ExecutorService exec = null; 
	
	private static MoniterManager INSTANCE;
	
	public MoniterManager() throws NoCacheException {
		//任务线程池
		if (null == exec) {
			exec = Executors.newFixedThreadPool(2);
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
			INSTANCE = new MoniterManager();
		}
		INSTANCE.lanuchTask();
	}
	
	/**
	 * 开启定时任务
	 * @throws NoCacheException
	 */
    private void lanuchTask() throws NoCacheException {
    	exec.execute(new RequestMoniter());
    	exec.execute(new ProcessorMoniter());
    }
	
}
