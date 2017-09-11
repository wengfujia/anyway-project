/*
 * 名称: DefaultResponse
 * 描述: 默认返回
 * 说明: 
 * 版本：  1.0.0
 * 作者： 翁富家
 * 修改:
 * 日期：2015年07月08日
 */

package io.box.web.executor.impl;

import io.box.web.executor.AbstractWebMessageResponse;

import org.anyway.common.MessageAnnotation;
import org.anyway.server.data.packages.COMMANDID;

@MessageAnnotation(msgType = COMMANDID.DEFAULTRESPONSE)
public class DefaultResponse extends AbstractWebMessageResponse {

	@Override
	public Integer call() {
		return super.call();
	}
}
