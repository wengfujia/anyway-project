/*
 * 名称: Handle
 * 描述: Http/socket调用数据库的入口类
 * 版本：  1.0.0
 * 作者： 翁富家
 * 修改:
 * 日期：2013年10月15日
 * 修改日期:
 */

package org.anyway.server.processor.Providers;

import org.anyway.common.ProcesserConfig;
import org.anyway.common.protocol.buffer.impl.CChrList;
import org.anyway.common.protocol.buffer.impl.http.HChrList;
import org.anyway.common.protocol.header.DbHeader;
import org.anyway.common.protocol.header.Header;
import org.anyway.common.utils.LoggerUtil;
import org.anyway.common.utils.StringUtil;

public class Handle {

	/*
	 * 数据处理类 header头，body体，type类型，list返回记录集 CChrList
	 */
	public static int G_Handle(Header header, String body, int type, HChrList list, byte[] o_reserve)
			throws Exception {
		
		int ret = 0;
		String value = getCommandType(header);
		
		if (StringUtil.empty(value)) {
			ret = 20;
			list.Append("[http]不支持该业务");
		} else {
			int nrlen = body.length();
			header.setLen(nrlen);
			if (value.equalsIgnoreCase("NORMAL")) {
				CInterface<String> pHI = new CHandleInterface<String>();
				ret = pHI.Handle(header, body, nrlen, null, type, list, o_reserve);
			} else if (value.equalsIgnoreCase("CURSOR")) {
				CInterface<String> pHI = new CHandleInterfaceCursor<String>();
				ret = pHI.Handle(header, body, nrlen, null, type, list, o_reserve);
			} else if (value.equalsIgnoreCase("CLASS")) {
				CInterface<String> pHI = new CHandleInterfaceCursor<String>();
				ret = pHI.Handle(header, body, nrlen, o_reserve, list);
			} else {
				ret = 20;
				list.Append("[http]不支持该业务");
			}
		}
		return ret;
	}

	/**
	 * G_Handle
	 * 
	 * @param header
	 * @param dbheader
	 * @param nr
	 * @param nrlen
	 * @param reserve
	 * @param type
	 * @param list
	 * @param o_reserve
	 * @return
	 * @throws Exception
	 */
	public static int G_Handle(Header header, DbHeader dbheader, byte[] nr, int nrlen, byte[] reserve, int type,
			CChrList list, byte[] o_reserve) throws Exception {
		
		int ret = 0;
		String value = getCommandType(header);
		
		if (StringUtil.empty(value)) {
			ret = 20;
			list.Append("[socket]不支持该业务");
		} else {
			if (value.equalsIgnoreCase("NORMAL")) {
				CInterface<byte[]> pHI = new CHandleInterface<byte[]>();
				ret = pHI.Handle(header, dbheader, nr, nrlen, reserve, type, list, o_reserve);
			} else if (value.equalsIgnoreCase("CURSOR")) {
				CInterface<byte[]> pHI = new CHandleInterfaceCursor<byte[]>();
				ret = pHI.Handle(header, dbheader, nr, nrlen, reserve, type, list, o_reserve);
			} else if (value.equalsIgnoreCase("CLASS")) {
				CInterface<byte[]> pHI = new CHandleInterfaceCursor<byte[]>();
				ret = pHI.Handle(header, nr, nrlen, o_reserve, list);
			} else {
				ret = 20;
				list.Append("[socket]不支持该业务");
			}
		}
		return ret;
	}
	
	/**
	 * 获取业务标识头对应的类型
	 * @param header
	 * @return
	 */
	final static String getCommandType(Header header) {
		int commandid = header.getCommandID();

		String key = LoggerUtil.sprintf("CMD.%d", commandid);
		String type = ProcesserConfig.getInstance().GetValue("", key);
		
		return type;
	}
	
}
