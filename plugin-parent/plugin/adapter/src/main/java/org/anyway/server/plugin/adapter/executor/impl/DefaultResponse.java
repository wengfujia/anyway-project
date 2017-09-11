/*
 * 名称: DefaultResponse
 * 描述: 默认返回
 * 说明: 
 * 版本：  1.0.0
 * 作者： 翁富家
 * 修改:
 * 日期：2015年07月08日
 */

package org.anyway.server.plugin.adapter.executor.impl;

import org.anyway.common.annotation.MessageAnnotation;
import org.anyway.common.protocol.header.CommandID;
import org.anyway.server.plugin.adapter.executor.AbstractMessageResponse;

@MessageAnnotation(msgType = CommandID.DEFAULTRESPONSE)
public class DefaultResponse extends AbstractMessageResponse {
	
	@Override
	protected void invoke(int status) {
		super.invoke(status, CommandID.DEFAULTRESPONSE);
	}

}
