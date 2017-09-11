/**
 * 
 */
package io.box.web.executor.impl;

import org.anyway.common.MessageAnnotation;
import org.anyway.common.enums.CryptEnum;
import org.anyway.common.utils.uLogger;
import org.anyway.server.api.CSHTMsgStream;
import org.anyway.server.api.HSHTMsgStream;
import org.anyway.server.data.packages.COMMANDID;
import org.anyway.server.data.packages.HEADER;
import org.anyway.server.data.packages.json.JBuffer;
import org.anyway.server.utils.uJsonUtil;

import io.box.web.executor.AbstractWebMessageLocalResponse;

/**
 * 
 * 测试
 * @author wengfj
 *
 */

@MessageAnnotation(msgType = 8002)
public class TestLocalReponse extends AbstractWebMessageLocalResponse {

	@Override
	public Integer call() {
		return super.call();
	}
	
	@Override
	protected String msgEncode() {
		int status = 0;
		//解析包
		JBuffer<String> LoginBuf = null;
		try {
    		LoginBuf = uJsonUtil.parseBuffer(getRequest().getJBody().getBody());
    	}
    	catch (Exception e) {
    		status = -23;
    		uLogger.printInfo(e.getMessage());
		}
		return HSHTMsgStream.toJsonString(0, "");
	}
}
