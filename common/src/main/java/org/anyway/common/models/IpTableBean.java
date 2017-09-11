package org.anyway.common.models;

import java.util.concurrent.atomic.AtomicInteger;

/*
 * 名称: KeywordBean
 * 描述: 缓存数据类基类
 * 版本：  1.0.0
 * 作者： 翁富家
 * 修改:
 * 日期：2015年05月28日
 * 修改日期:
 */

@SuppressWarnings("serial")
public class IpTableBean implements java.io.Serializable {
	private String name;
	private String address;
	private int port;
	private int maxthreads;
	private AtomicInteger curthreads; //设置为线程安全
	private int status;

	/**
	 * 构造函数
	 */
	public IpTableBean() {
		this.curthreads = new AtomicInteger(0);
	}
	
	/**
	 * 获取配置名称
	 * @return
	 */
	public String getName() {
		return name;
	}
	/**
	 * 设置配置名称
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}
	
    /**
	 * 获取ip地址
	 * @return String
	 */
	public String getAddress() {
        return address;
    }
	/**
	 * 设置ip地址
	 * @param address
	 */
    public void setAddress(String addr) {
        this.address = addr;
    }
    
    /**
     * 获取端口号
     * @return
     */
    public int getPort() {
    	return this.port;
    }
    /**
     * 设置端口号
     * @param port
     */
    public void setPort(int port) {
    	this.port = port;
    }
    
    /**
     * 获取最大线程数
     * @return
     */
    public int getMaxthreads() {
    	return this.maxthreads;
    }
    /**
     * 设置最大线程数
     * @param max
     */
    public void setMaxthread(int max) {
    	this.maxthreads = max;
    }
    
    /**
     * 获取当前线程数
     * @return
     */
    public int getCurthreads() {
    	return this.curthreads.get();
    }
    
    /**
     * 增加线程数
     */
    public int incCurthreads() {
    	return this.curthreads.incrementAndGet();
    }
    /**
     * 减少线程数
     */
    public int decCurthreads() {
    	return this.curthreads.decrementAndGet();
    }
    
    /**
     * 获取有效的线程数
     */
    public int getValidthreads() {
    	return this.maxthreads - this.curthreads.get();
    }
    
    /**
     * 获取状态 0无效，1有效
     * @return
     */
    public int getStatus() {
    	return this.status;
    }
    /**
     * 设置状态
     * @param status
     */
    public void setStatus(int status) {
    	this.status = status;
    }
    
    /**
     * 判断该连接是否可用
     * @return
     */
    public Boolean isSucess() {
    	return this.status==1 ? true : false; //this.curthreads<this.maxthreads && 加个这个条件出现找不到可能IP资源，可能因为线程报错造成IP资源没有释放
    }
    
}