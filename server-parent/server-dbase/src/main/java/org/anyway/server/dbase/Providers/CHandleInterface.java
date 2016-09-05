/*
 * 名称: CHandleInterface
 * 描述: 存储过程调用类
 * 版本：  1.0.1
 * 作者： 翁富家
 * 修改:
 * 日期：2013年10月15日
 * 修改日期:
 * 修改说明：
 * 2014.1.24
 * 1、根据cmdid，匹配setting.ini中的body节段，判断包体格式		 
 * 2、json为json格式数据包
 * json数据范例：{"head":"","body":"[{"line":""},{"line",""}]"
					 * 1.判断是否为json数据，字符串头为{"head":为json，否则为普通字符串
					 * 2.取出json的head,body列
					 * 3.如果head不为空，表示需要跟body组合成一个新字符串
					 * 4.循环插入，如果第一次调用过程不成功，则退出循环。
	2014.1.27
	Handle_DB过程中ChrList参数用接口类代替了泛型与类型强转换
	
	2014.2.3
	Handle_DB(byte[] nr=>Handle_DB(T nr
	
	2014.7.11
     classname.equalsIgnoreCase=>instanceof 来判断类型
 */

package org.anyway.server.dbase.Providers;

import java.io.UnsupportedEncodingException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.StringTokenizer;

import org.anyway.common.uConfigVar;
import org.anyway.common.uGlobalVar;
import org.anyway.common.utils.uLogger;
import org.anyway.common.utils.uNetUtils;
import org.anyway.common.utils.uStringUtils;
import org.anyway.server.data.contracts.IChrList;
import org.anyway.server.dbase.Providers.db.CJdbcPool;

public class CHandleInterface<T> extends CInterface<T> {

	/**
	 * socket调用Produce数据库类
	 * 
	 * @param <T>
	 * @throws Exception
	 * @throws SQLException
	 */
	@Override
	protected int Handle_DB(T nr, int nrlen, IChrList list, byte[] o_reserve) {
		int Result = 0;
		// ConnectionHelper pdb = GetConnection();
		Connection connection = null;
		CallableStatement cs = null;
		try {
			// Connection connection = (pdb != null)?pdb.getDBSession() : null;
			connection = CJdbcPool.datasource.getConnection();
			if (connection != null && !connection.isClosed()) {

				String name = m_header.getUser().trim();
				String pwd = m_header.getPwd().trim();
				/* ip地址+标识号+版本号 */
				String headinfo = m_header.getIP() + uGlobalVar.MSG_SEPATATE + m_header.getSessionid()
						+ uGlobalVar.MSG_SEPATATE + m_header.getVersion();
				String body = "";
				if (nr != null) {
					if (nr instanceof byte[]) // nr.getClass().getSimpleName().equals("byte[]")
						body = uNetUtils.getString((byte[]) nr, uConfigVar.CharsetName);
					else
						body = (String) nr;
				}
				int bodylen = nrlen;
				int cmdid = m_header.getCommandID();

				/**
				 * 1、根据cmdid，匹配setting.ini中的body节段，判断包体格式
				 * 2、json为json格式数据包,SPLIT为分隔
				 */
				// 获取数据库
				cs = connection.prepareCall("{call i_main_enter(?,?,?,?,?,?,?,?,?)}");
				cs.registerOutParameter(1, java.sql.Types.INTEGER);
				cs.setInt(2, cmdid);
				cs.setString(3, name);
				cs.setString(4, pwd);
				cs.setString(5, headinfo);

				String value = GetBodyType(cmdid);
				if (value != null && value.equalsIgnoreCase("SPLIT")) {
					String head = "";
					// 1.分析JSON数据
					StringTokenizer strToke = new StringTokenizer(body, "/|");
					if (body.indexOf("/") > 0) {
						head = strToke.nextToken("/");
					}
					// 2.
					while (strToke.hasMoreElements()) {
						body = head + strToke.nextToken("/|");

						cs.setString(6, body);
						cs.setInt(7, body.length());
						Result = CallPro(cs, list, o_reserve);
						// 第一个循环出错便退出
						if (Result != uGlobalVar.RETURN_SUCCESS)
							break;
					}
				} else {
					cs.setString(6, body);
					cs.setInt(7, bodylen);
					Result = CallPro(cs, list, o_reserve);
				}
			}
		} catch (Exception e) {
			Result = -21;
			uLogger.println(e.getMessage());
		} finally {
			if (cs != null)
				try {
					cs.close();
					cs = null;
				} catch (Exception ignore) {
					;
				}
			if (connection != null)
				try {
					connection.close();
					connection = null;
				} catch (Exception ignore) {
					;
				}
			// CDatabasePool.ReleaseConnection(pdb);
		}
		return Result;
	}

	/**
	 * 调用存储过程
	 * 
	 * @param cs
	 * @param list
	 * @param o_reserve
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	private int CallPro(CallableStatement cs, IChrList list, byte[] o_reserve) throws UnsupportedEncodingException {
		int Result = uGlobalVar.RETURN_SUCCESS;
		try {
			cs.registerOutParameter(8, java.sql.Types.VARCHAR);
			cs.registerOutParameter(9, java.sql.Types.VARCHAR);
			cs.execute();

			Result = cs.getInt(1);
			String s1 = cs.getString(8) == null ? "" : cs.getString(8);
			String s2 = cs.getString(9) == null ? "" : cs.getString(9);
			o_reserve = uNetUtils.getBytes(s2, uConfigVar.CharsetName);

			if (uStringUtils.empty(s1) == false && list != null) {
				// 用接口类代替了泛型与类型强转换
				list.Append(s1);
			}
		} catch (SQLException e) {
			Result = -23;
			uLogger.println(e.getMessage());
		}
		return Result;
	}
}
