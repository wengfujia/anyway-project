/*
 * 名称: RegisterClassMessage
 * 描述: 注册班级消息处理包
 * 说明: 
 * 版本：  1.0.0
 * 作者： 翁富家
 * 修改:
 * 日期：2015年12月8日
 */

package io.box.web.message;


import org.anyway.common.SystemConfig;
import org.anyway.common.annotation.MessageAnnotation;

import io.box.common.CommandID;

@MessageAnnotation(msgType = CommandID.CLASS_REGISTER)
public class RegisterClassMessage {

	/*
	 * 在原包后面需要添加班级序号
	 * */
	public byte[] decode(String body) {	
		String newbody = body;
		char last = newbody.charAt(newbody.length()-1);
		if (last != SystemConfig.MSG_SEPATATE) {
			newbody += String.valueOf(SystemConfig.MSG_SEPATATE);
		}
//		String[] bodys = StringUtils.splitPreserveAllTokens(newbody, uGlobalVar.MSG_SEPATATE);
//		String code = bodys[0] + bodys[1] + bodys[2];
//		try {
//			newbody += CacheManager.getInstance().getConfigCache().getNextSequenceId(SectionEnum.CLASS.getName(),
//					code);
//		} catch (NoCacheException e) {
//			e.printStackTrace();
//			return null;
//		}
		newbody += System.currentTimeMillis();
		byte[] result = newbody.getBytes();
		return result;
	}
	
}
