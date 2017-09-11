/*
 * 名称: LocationMessage
 * 描述: 坐标消息处理
 * 说明: 
 * 版本：  1.0.0
 * 作者： 翁富家
 * 修改:
 * 日期：2015年11月6日
 */

package io.box.weixin.message;

import java.util.Map;

import org.anyway.common.annotation.MessageAnnotation;

import io.box.common.CommandID;

@MessageAnnotation(msgType = CommandID.WEIXIN_LOCATION)
public class LocationMessage {

	/*
	 * 分解消息
	 * 把坐标信息组合成包
	 * */
	public byte[] decode(Map<String, String> requestMap) {
		String content = "";
		String msgType = requestMap.get("MsgType").toLowerCase();
		if (msgType.equals("location")) { //获取位置信息
			content = msgType+"\t"+requestMap.get("Location_X")+"\t"+requestMap.get("Location_Y")+"\t"+requestMap.get("Scale")+"\t"+requestMap.get("Label");
		}
		else if (msgType.toLowerCase().equals("event")) { //获取微信推送的位置信息
			content = msgType+"\t"+requestMap.get("Latitude")+"\t"+requestMap.get("Longitude")+"\t"+requestMap.get("Precision");
		}
		else {
			return null;
		}
		byte[] result = content.getBytes();
		return result;
		
	}
	
}
