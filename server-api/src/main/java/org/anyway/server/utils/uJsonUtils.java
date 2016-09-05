/*
 * 名称: uJsonUtils.java
 * 描述: json解析类
 * 版本：  1.0.0
 * 作者： 翁富家
 * 修改:
 * 日期：2016年07月06日
 * */
package org.anyway.server.utils;

import java.util.Map;

import org.anyway.server.data.packages.json.JBuffer;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class uJsonUtils {
	
	/**
	 * parseData解析json数据
	 * @param data
	 * @return
	 */
	public static Map<String, String> parseData(String data){
		Gson gson = new Gson();
		Map<String, String> map = gson.fromJson(data, new TypeToken<Map<String, String>>() {}.getType()); 
		return map;
	}
	
	/**
	 * parseBuffer解析成jBuffer
	 * @param data
	 * @return
	 */
	public static <T> JBuffer<T> parseBuffer(String data) {
		JBuffer<T> jbuffer = new JBuffer<T>();
		Gson gson = new Gson();
		jbuffer = gson.fromJson(data, new TypeToken<JBuffer<T>>(){}.getType());
		return jbuffer;
	}
	
}
