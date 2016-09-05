package org.anyway.wechat.constant;

import org.anyway.wechat.util.ConfigUtil;

/**
 * 微信常量
 * @author lkl
 *
 */
public class ConstantWeChat {
	/**
	 * 与接口配置信息中的Token要一致
	 */
	public static String TOKEN = ConfigUtil.get("token");
	
	/**
	 * 第三方用户唯一凭证
	 */
	public static String APPID = ConfigUtil.get("appId");
	
	/**
	 * 第三方用户唯一凭证密钥
	 */
	public static String APPSECRET = ConfigUtil.get("appSecret");
	
	/**
	 * 请求消息类型：文本
	 */
	public static final String REQ_MESSAGE_TYPE_TEXT = "text";
	
	/**
	 * 请求消息类型：图片
	 */
	public static final String REQ_MESSAGE_TYPE_IMAGE = "image";
	
	/**
	 * 请求消息类型：音频
	 */
	public static final String REQ_MESSAGE_TYPE_VOICE = "voice";
	
	/**
	 * 请求消息类型：视频
	 */
	public static final String REQ_MESSAGE_TYPE_VIDEO = "video"; 
	
	/**
	 * 请求消息类型：小视频
	 */
	public static final String REQ_MESSAGE_TYPE_SHORTVIDEO = "shortvideo";  
	
	/**
	 * 请求消息类型：地理位置
	 */
	public static final String REQ_MESSAGE_TYPE_LOCATION = "location";
	
	/**
	 * 请求消息类型：链接
	 */
	public static final String REQ_MESSAGE_TYPE_LINK = "link"; 
	
	/**
	 * 请求消息类型：事件
	 */
	public static final String REQ_MESSAGE_TYPE_EVENT = "event";
	
	/**
	 * 事件类型：subscribe(关注)
	 */
	public static final String EVENT_TYPE_SUBSCRIBE = "subscribe";
	
	/**
	 * 事件类型：unsubscribe(取消关注)
	 */
	public static final String EVENT_TYPE_UNSUBSCRIBE = "unsubscribe";
	
	/**
	 * 事件类型: LOCATION(上报地理位置事件)
	 */
	public static final String EVENT_TYPE_LOCATION = "LOCATION";
	
	/**
	 * 事件类型：SCAN(二维码扫描事件)
	 */
	public static final String EVENT_TYPE_SCAN = "SCAN";
	
	/**
	 * 事件类型: CLICK(自定义菜单点击事:点击菜单拉取消息时的事件推送)
	 */
	public static final String EVENT_TYPE_CLICK = "CLICK";
	
	/**
	 * 事件类型: VIEW(自定义菜单点击事:点击菜单跳转链接时的事件推送)
	 */
	public static final String EVENT_TYPE_VIEW = "VIEW";
	
	/**
	 * 事件类型: scancode_push(自定义菜单点击事:点击菜单扫码推事件的事件推送)
	 */
	public static final String EVENT_TYPE_SCANCODE_PUSH= "scancode_push";
	
	/**
	 * 事件类型: (自定义菜单点击事:扫码推事件且弹出“消息接收中”提示框的事件推送)
	 */
	public static final String EVENT_TYPE_SCANCODE_WAITMSG= "scancode_waitmsg";
	
	/**
	 * 事件类型: pic_sysphoto(自定义菜单点击事:弹出系统拍照发图的事件推送)
	 */
	public static final String EVENT_TYPE_PIC_SYSPHOTO= "pic_sysphoto";
	
	/**
	 * 事件类型: pic_weixin(自定义菜单点击事:弹出微信相册发图器的事件推送)
	 */
	public static final String EVENT_TYPE_PIC_WEIXIN= "pic_weixin";
	
	/**
	 * 事件类型: pic_photo_or_album(自定义菜单点击事:弹出拍照或者相册发图的事件推送)
	 */
	public static final String EVENT_TYPE_PIC_PHOTO_OR_ALBUM= "pic_photo_or_album";
	//
	/**
	 * 事件类型: location_select(自定义菜单点击事:弹出地理位置选择器的事件推送)
	 */
	public static final String EVENT_TYPE_LOCATION_SELECT= "location_select";
	
	/**
	 * 返回消息类型：文本
	 */
	public static final String RESP_MESSAGE_TYPE_TEXT = "text"; 
	
	/**
	 * 返回消息类型：图片
	 */
	public static final String RESP_MESSAGE_TYPE_IMAGE = "image"; 
	
	/**
	 * 返回消息类型：语音
	 */
	public static final String RESP_MESSAGE_TYPE_VOICE = "voice"; 
	
	/**
	 * 返回消息类型：视频
	 */
	public static final String RESP_MESSAGE_TYPE_VIDEO = "video";  
	
	/**
	 * 返回消息类型：音乐
	 */
	public static final String RESP_MESSAGE_TYPE_MUSIC = "music"; 
	
	/**
	 * 返回消息类型：图文
	 */
	public static final String RESP_MESSAGE_TYPE_NEWS = "news"; 
	
	/**
	 * 网页授权作用域:不弹出授权页面，直接跳转，只能获取用户openid
	 */
	public static final String SCOPE_SNSAPI_BASE = "snsapi_base";
	
	/**
	 * 网页授权作用域:弹出授权页面，可通过openid拿到昵称、性别、所在地
	 */
	public static final String SCOPE_SNSAPI_USERINFO = "snsapi_userinfo";
}
