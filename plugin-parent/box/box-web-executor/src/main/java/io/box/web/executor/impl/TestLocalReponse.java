/**
 * 
 */
package io.box.web.executor.impl;

import org.anyway.common.annotation.MessageAnnotation;
import org.anyway.common.protocol.HttpMessageCoder;
import org.anyway.common.protocol.body.JBuffer;
import org.anyway.common.utils.JsonUtil;
import org.anyway.common.utils.LoggerUtil;
import org.anyway.server.plugin.adapter.executor.AbstractMessageLocalResponse;

/**
 * 
 * 测试
 * @author wengfj
 *
 */

@MessageAnnotation(msgType = 8002)
public class TestLocalReponse extends AbstractMessageLocalResponse {

	@Override
	protected String msgEncode() {
		int status = 0;
		//解析包
		JBuffer<String> LoginBuf = null;
		try {
    		LoginBuf = JsonUtil.parseBuffer(getRequest().getJBody().getBody());
    	}
    	catch (Exception e) {
    		status = -23;
    		LoggerUtil.printInfo(e.getMessage());
		}
		return HttpMessageCoder.toJsonString(0, "");
	}

	@Override
	protected void invoke(int status) {
		super.invoke(status, 8002);
	}
	
}
