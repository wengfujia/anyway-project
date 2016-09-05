package org.anyway.wechat.service;

import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONObject;

import org.anyway.wechat.entity.user.WeChatOpenIdList;
import org.anyway.wechat.entity.user.WeChatUserInfo;
import org.anyway.wechat.entity.user.WeChatOpenIdList.OpenId;
import org.anyway.wechat.util.StringUtil;
import org.anyway.wechat.util.WeixinUtil;
import org.apache.log4j.Logger;

/**
 * 用户管理
 * @author beinfo
 *
 */
public class UserService {
	public static Logger log = Logger.getLogger(UserService.class);
	
	/**
	 * 获取用户详细信息
	 */
	public static String GET_USER_INFO = "https://api.weixin.qq.com/cgi-bin/user/info?access_token=ACCESS_TOKEN&openid=OPENID&lang=zh_CN";

	/**
	 * 获取用户openid列表
	 */
	public static String GET_USER_OPENID_LIST = "https://api.weixin.qq.com/cgi-bin/user/get?access_token=ACCESS_TOKEN&next_openid=NEXT_OPENID";
	
	/**
	 * 获取用户详细信息
	 * @param openid 微信用户唯一ID
	 * @return WeChatUserInfo对象
	 */
	public static WeChatUserInfo getUserInfo(String openid) {
		String token = WeixinUtil.getToken();

		WeChatUserInfo user = null;

		if (token != null) {
			String url = GET_USER_INFO.replace("ACCESS_TOKEN", token).replace("OPENID", openid);
			JSONObject jsonObject = WeixinUtil.httpsRequest(url, "POST", null);

			if (null != jsonObject) {
				if (StringUtil.isNotEmpty(jsonObject.get("errcode")) && jsonObject.get("errcode") != "0") {
					log.error("获取用户信息失败 errcode:" + jsonObject.getInt("errcode") + "，errmsg:" + jsonObject.getString("errmsg"));
				} else {
					user = new WeChatUserInfo();
					user.setSubscribe(jsonObject.getInt("subscribe"));
					user.setOpenid(jsonObject.getString("openid"));
					user.setNickname(jsonObject.getString("nickname"));
					user.setSex(jsonObject.getInt("sex"));
					user.setCity(jsonObject.getString("city"));
					user.setCountry(jsonObject.getString("country"));
					user.setProvince(jsonObject.getString("province"));
					user.setLanguage(jsonObject.getString("language"));
					user.setHeadimgurl(jsonObject.getString("headimgurl"));
					long subscibeTime = jsonObject.getLong("subscribe_time");
					user.setSubscribe_time(subscibeTime);
				}
			}
		}
		
		return user;
	}
	
	/**
	 * 获取关注者用户列表（OPENID的列表）
	 * @param openIdList 所有微信关注者对象
	 * @return WeChatOpenIdList 对象
	 */
	public static WeChatOpenIdList getUserOpenIdList(WeChatOpenIdList openIdList) {
		String token = WeixinUtil.getToken();
		List<OpenId> list = null;
		if (token != null) {
			String url = GET_USER_OPENID_LIST.replace("ACCESS_TOKEN", token)
					.replace("NEXT_OPENID", "");

			JSONObject jsonObject = WeixinUtil.httpsRequest(url, "POST", null);

			if (null != jsonObject) {
				if (StringUtil.isNotEmpty(jsonObject.get("errcode")) && jsonObject.get("errcode") != "0") {
					log.error("获取关注用户列表失败 errcode:" + jsonObject.getInt("errcode") + "，errmsg:" + jsonObject.getString("errmsg"));
				} else {
					openIdList.setTotal(jsonObject.getLong("total"));
					openIdList.setCount(jsonObject.getInt("count"));
					
					list = new ArrayList<OpenId>();
					JSONObject data = jsonObject.getJSONObject("data");
					String openidStr = data.getString("openid");
					openidStr = openidStr.substring(1, openidStr.length() - 1);
					openidStr = openidStr.replace("\"", "");
					String openidArr[] = openidStr.split(",");
					for (int i = 0; i < openidArr.length; i++) {
						list.add(i, new OpenId(openidArr[i]));
					}
					openIdList.setData(list);
					openIdList.setNext_openid(jsonObject.getString("next_openid"));
				}
			}
		}
		
		return openIdList;
	}
	
	/**
	 * 获取关注者列表
	 * @param openIdList
	 * @return List<WeChatUserInfo>
	 */
	public static List<WeChatUserInfo> getUserList(WeChatOpenIdList openIdList) {
		List<WeChatUserInfo> list = new ArrayList<WeChatUserInfo>();

		// 获取关注用户openid列表
		openIdList = getUserOpenIdList(openIdList);

		if (openIdList.getData() == null || openIdList.getData().size() == 0) {
			return null;
		}
		for (int i = 0; i < openIdList.getData().size(); i++) {
			// 根据openid查询用户信息
			WeChatUserInfo user = getUserInfo(openIdList.getData().get(i).getOpenid());
			if (user != null) {
				list.add(user);
			}
		}
		return list;
	}
}
