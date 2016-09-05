package org.anyway.cache.ehcache.bootstrap;

import java.util.Properties;

import net.sf.ehcache.bootstrap.BootstrapCacheLoader;
import net.sf.ehcache.bootstrap.BootstrapCacheLoaderFactory;

@SuppressWarnings("rawtypes")
public class CustomBootstrapCacheLoaderFactory extends BootstrapCacheLoaderFactory {
	
	private final String ASYNCHRONOUS_PROPERTY_KEY = "ASYNCHRONOUS";  
	
	public CustomBootstrapCacheLoaderFactory() {  
        super();  
        // TODO Auto-generated constructor stub   
    }  
   
    @Override  
    public BootstrapCacheLoader createBootstrapCacheLoader(Properties properties) {  
        CustomBootstrapCacheLoader loader = new CustomBootstrapCacheLoader();   
        loader.setAsynchronous(getAsyncFromProperty(properties));  
  
        return loader;  
    }  
  
    private boolean getAsyncFromProperty(Properties properties) {  
        String asynchronous = properties.getProperty(ASYNCHRONOUS_PROPERTY_KEY);  
  
        return Boolean.valueOf(asynchronous);  
    }
    
}
