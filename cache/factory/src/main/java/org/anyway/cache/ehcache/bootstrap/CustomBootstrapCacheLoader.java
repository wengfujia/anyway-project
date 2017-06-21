package org.anyway.cache.ehcache.bootstrap;

import net.sf.ehcache.CacheException;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.bootstrap.BootstrapCacheLoader;

public class CustomBootstrapCacheLoader implements BootstrapCacheLoader {
	
	boolean	asynchronous; 
	
	/**  
     * @see net.sf.ehcache.bootstrap.BootstrapCacheLoader#load(net.sf.ehcache.Ehcache) 
     */
	@Override
    public void load(Ehcache cache) throws CacheException {
		
    } 
    
    /**  
     * @see net.sf.ehcache.bootstrap.BootstrapCacheLoader#isAsynchronous() 
     */
    @Override
    public boolean isAsynchronous() {  
        return asynchronous;  
    }  
  
    /**  
     * @see java.lang.Object#clone() 
     */  
    @Override  
    public Object clone() throws CloneNotSupportedException {  
  
        return super.clone();  
  
    }  

    /** 
     * Setter method for property <tt>asynchronous</tt>. 
     *  
     * @param asynchronous value to be assigned to property asynchronous 
     */
    public void setAsynchronous(boolean asynchronous) {  
        this.asynchronous = asynchronous;  
    }  
    
}
