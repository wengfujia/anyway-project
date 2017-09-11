/**
 * 执行http业务逻辑的基类
 * 实现Runnable接口
 * 
 * @author wfj
 *
 */

package org.anyway.server.plugin.adapter;

import org.anyway.common.protocol.request.HttpRequest;
import org.anyway.server.plugin.adapter.utils.ResponseHelper;

public abstract class AbstractHttpBusinessExecutor extends BusinessBaseExecutor<HttpRequest<String>> {

	/**
	 * 返回结果
	 * 
	 * @param content
	 */
	protected int sendResponse(String content) {
		boolean result = ResponseHelper.writeResponse(content, getRequest().getContext(), getRequest().getRequest());
		if (result) {
			return 0;
		}
		else {
			return -23;
		}
	}
	
}