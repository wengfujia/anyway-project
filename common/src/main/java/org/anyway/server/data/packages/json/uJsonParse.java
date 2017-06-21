package org.anyway.server.data.packages.json;

import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class uJsonParse {
	
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
	public static JBuffer<String> parseBuffer(String data) {
		JBuffer<String> jbuffer = new JBuffer<String>();
		Gson gson = new Gson();
		jbuffer = gson.fromJson(data, new TypeToken<JBuffer<String>>(){}.getType());
		return jbuffer;
	}
	
}
