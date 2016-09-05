package org.anyway.wechat.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.anyway.wechat.entity.message.resp.Article;
import org.anyway.wechat.entity.message.resp.NewsMessage;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.core.util.QuickWriter;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.xml.PrettyPrintWriter;
import com.thoughtworks.xstream.io.xml.XppDriver;

/**
 * 消息工具类
 * @author beinfo
 *
 */
public class MessageUtil {
	
	/**
	 * 解析微信发来的XML请求
	 * @param request
	 * @return map
	 */
	public static Map<String, String> parseXml(HttpServletRequest request) {  
		//将解析结果存储在HashMap中  
	    Map<String, String> map = new HashMap<String, String>(); 
	    InputStream inputStream = null;
	    
	    try{
	    	//从request中取得输入流  
		    inputStream = request.getInputStream();  
		    //建立XML文件读取对象SAXReader
			SAXReader saxReader = new SAXReader();
			//将XML文件流读取到创建的document对象里
			Document document = saxReader.read(inputStream); 
			
			// 得到xml根元素  
		    Element root = document.getRootElement();  
		    // 得到根元素的所有子节点  
		    @SuppressWarnings("unchecked")
		    List<Element> elementList = root.elements();  
		  
		    // 遍历所有子节点  
		    for (Element e : elementList){
		        map.put(e.getName(), e.getText()); 
		    }
		      
	    }catch(IOException ioe){
	    	ioe.printStackTrace();
	    }catch(DocumentException de){
	    	de.printStackTrace();
	    }finally{
	    	// 释放资源  
		    try {
				inputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		    inputStream = null;  
	    }
	   
	    return map;
	}
	
	/**
	 * 解析微信发来的XML请求
	 * @param inputStream
	 * @return map
	 */
	public static Map<String, String> parseXml(InputStream inputStream) {  
		//将解析结果存储在HashMap中  
	    Map<String, String> map = new HashMap<String, String>(); 
	    
	    try{
		    //建立XML文件读取对象SAXReader
			SAXReader saxReader = new SAXReader();
			//将XML文件流读取到创建的document对象里
			Document document = saxReader.read(inputStream); 
			
			// 得到xml根元素  
		    Element root = document.getRootElement();  
		    // 得到根元素的所有子节点  
		    @SuppressWarnings("unchecked")
		    List<Element> elementList = root.elements();  
		  
		    // 遍历所有子节点  
		    for (Element e : elementList){
		        map.put(e.getName(), e.getText()); 
		    }
		      
	    }catch(DocumentException de){
	    	de.printStackTrace();
	    }finally{
	    	// 释放资源  
		    try {
				inputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		    inputStream = null;  
	    }
	   
	    return map;
	}
	
	/**
	 * 扩展xstream，使其支持CDATA块
	 */
	private static XStream xstream = new XStream(new XppDriver() {
		public HierarchicalStreamWriter createWriter(Writer out) {
			return new PrettyPrintWriter(out) {
				// 对所有xml节点的转换都增加CDATA标记
				boolean cdata = true;
				
				protected void writeText(QuickWriter writer, String text) {
					if (cdata) {
						writer.write("<![CDATA[");
						writer.write(text);
						writer.write("]]>");
					} else {
						writer.write(text);
					}
				}
			};
		}
	});
	
//	/**
//	 * 文本消息对象转换成xml
//	 * @param textMessage
//	 * @return
//	 */
//	public static String textMessageToXml(TextMessage textMessage) {  
//        xstream.alias("xml", textMessage.getClass());  
//        return xstream.toXML(textMessage);  
//    }  
//	
//	/**
//	 * 多媒体（图片/语音）消息对象转换成xml
//	 * @param mediaMessage
//	 * @return
//	 */
//	public static String voiceMessageToXml(MediaMessage mediaMessage) {  
//        xstream.alias("xml", mediaMessage.getClass());  
//        return xstream.toXML(mediaMessage);  
//    }  
//	
//	/**
//	 * 视频消息对象转换成xml
//	 * @param videoMessage
//	 * @return
//	 */
//	public static String videoMessageToXml(VideoMessage videoMessage) {  
//        xstream.alias("xml", videoMessage.getClass());  
//        return xstream.toXML(videoMessage);  
//    }  
//	
//	/**
//	 * 音乐消息对象转换成xml
//	 * @param musicMessage
//	 * @return
//	 */
//	public static String musicMessageToXml(MusicMessage musicMessage) {  
//        xstream.alias("xml", musicMessage.getClass());  
//        return xstream.toXML(musicMessage);  
//    }  
	
	/**
	 * 消息(文本/图片/语音/视频/音乐)对象转换成xml
	 * @param message 消息对象
	 * @return xml
	 */
	public static String messageToXml(Object message){
		xstream.alias("xml", message.getClass());
		return xstream.toXML(message);
	}
	
	/**
	 * 图文消息对象转换成xml
	 * @param newsMessage 图文消息对象
	 * @return xml
	 */
	public static String newsMessageToXml(NewsMessage newsMessage) {  
        xstream.alias("xml", newsMessage.getClass());  
        xstream.alias("item", new Article().getClass());  
        return xstream.toXML(newsMessage);  
    }  
	
	/**
	 * emoji表情转换(hex to utf-16)
	 * @param hexEmoji <br>
	 * 将表情代码表中的U+替换为0x，再调用emoji方法
	 * @return String
	 */
	public static String emoji(int hexEmoji) {
		return String.valueOf(Character.toChars(hexEmoji));
	}
}
