package io.box.db.executor;

import org.anyway.common.annotation.MessageAnnotation;
import org.anyway.common.protocol.buffer.IChrList;
import org.anyway.common.protocol.header.Header;
import org.anyway.plugin.processor.BaseExecutor;

@MessageAnnotation(msgType = 10)
public class testClass extends BaseExecutor {

	public int execute(Header header, byte[] nr, IChrList list) {
		list.Append("ok".getBytes());
		return 0;
	}

}
