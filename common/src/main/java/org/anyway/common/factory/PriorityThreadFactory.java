/*
 * 名称: PriorityThreadFactory.java
 * 描述: 自定义线程池
 * 版本：  1.0.0
 * 作者： 翁富家
 * 修改:
 * 日期：2015年01月16日
 * 修改日期:
 */

package org.anyway.common.factory;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class PriorityThreadFactory implements ThreadFactory {
	 
    private int _prio;
    private String _name;
    private AtomicInteger _threadNumber = new AtomicInteger(1);
    private ThreadGroup _group;
 
    /**
     *
     * @param name 线程池名
     * @param priority   线程池优先级
     */
    public PriorityThreadFactory(String name, int priority){
        _prio = priority;
        _name = name;
        _group = new ThreadGroup(_name);
    }
 
    @Override
    public Thread newThread(Runnable r){
        Thread t = new Thread(_group, r);
        t.setName(_name + "-"+"#-" + _threadNumber.getAndIncrement());
        t.setPriority(_prio);
        return t;
    }
 
    public ThreadGroup getGroup(){
        return _group;
    }
}
