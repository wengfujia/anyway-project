package org.anyway.wechat.service;

import net.sf.json.JSONObject;

import org.anyway.wechat.entity.menu.Menu;
import org.anyway.wechat.util.WeixinUtil;
import org.apache.log4j.Logger;

/**
 * 菜单创建
 * @author lkl
 *
 */
public class MenuService {
	
	public static Logger log = Logger.getLogger(MenuService.class);
	
	/**
	 * 菜单创建（POST） 限100（次/天）
	 */
	public static String MENU_CREATE = "https://api.weixin.qq.com/cgi-bin/menu/create?access_token=ACCESS_TOKEN";

	/**
	 * 菜单查询
	 */
	public static String MENU_GET = "https://api.weixin.qq.com/cgi-bin/menu/get?access_token=ACCESS_TOKEN";
	
	/**
	 * 创建菜单
	 * @param jsonMenu json格式
	 * @return 状态 0 表示成功、其他表示失败
	 */
	public static Integer createMenu(String jsonMenu) {
		int result = 0;
		String token = WeixinUtil.getToken();
		if(token != null) {
			// 拼装创建菜单的url
			String url = MENU_CREATE.replace("ACCESS_TOKEN", token);
			// 调用接口创建菜单
			JSONObject jsonObject = WeixinUtil.httpsRequest(url, "POST", jsonMenu);
			
			if (null != jsonObject) {
				if (0 != jsonObject.getInt("errcode")) {
					result = jsonObject.getInt("errcode");
					log.error("创建菜单失败 errcode:" + jsonObject.getInt("errcode")
							+ "，errmsg:" + jsonObject.getString("errmsg"));
				}
			}
		}
		
		return result;
	}
	
	/**
	 * 创建菜单
	 * @param menu
	 * @return 0表示成功，其他值表示失败
	 */
	public static Integer createMenu(Menu menu) {
		return createMenu(JSONObject.fromObject(menu).toString());
	}
	
	/**
	 * 查询菜单
	 * @return 菜单结构json字符串
	 */
	public static JSONObject getMenuJson() {
		JSONObject result = null;
		String token = WeixinUtil.getToken();
		if (token != null) {
			String url = MENU_GET.replace("ACCESS_TOKEN", token);
			result = WeixinUtil.httpsRequest(url, "GET", null);
		}
		return result;
	}
	
	/**
	 * 查询菜单
	 * @return Menu菜单对象
	 */
	public static Menu getMenu() {
		JSONObject json = getMenuJson().getJSONObject("menu");
		Menu menu = (Menu) JSONObject.toBean(json, Menu.class);
		return menu;
	}

}
