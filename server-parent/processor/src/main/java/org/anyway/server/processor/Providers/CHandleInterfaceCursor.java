/*
 * 名称: CHandleInterfaceCursor
 * 描述: cursor过程调用类
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
 * 2014.1.27
	Handle_DB过程中ChrList参数用接口类代替了泛型与类型强转换
	
   2014.2.3
	Handle_DB(byte[] nr=>Handle_DB(T nr

   2014.7.11
     classname.equalsIgnoreCase=>instanceof 来判断类型
 */

package org.anyway.server.processor.Providers;

import java.io.UnsupportedEncodingException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.StringTokenizer;

import org.anyway.common.SystemConfig;
import org.anyway.common.protocol.buffer.IChrList;
import org.anyway.common.protocol.buffer.impl.CChrList;
import org.anyway.common.utils.LoggerUtil;
import org.anyway.common.utils.NetUtil;
import org.anyway.common.utils.StringUtil;
import org.anyway.plugin.processor.pool.DataSourcePool;

public class CHandleInterfaceCursor<T> extends CInterface<T> {

	/**
	 * socket调用Cursor数据库类
	 * 
	 * @param <T>
	 * @throws Exception
	 * @throws SQLException
	 */
	@Override
	protected int Handle_DB(T nr, int nrlen, IChrList list, byte[] o_reserve) {
		// ConnectionHelper pdb = GetConnection();
		int Result = 0;
		Connection connection = null;
		CallableStatement cs = null;
		try {
			// Connection connection = (pdb != null)?pdb.getDBSession() : null;
			connection = DataSourcePool.getInstance().getConnection(m_header.getSessionid(), m_header.getCommandID());
			if (connection != null && !connection.isClosed()) {

				String name = m_header.getUser().trim();
				String pwd = m_header.getPwd().trim();
				/* ip地址+标识号+版本号 */
				String headinfo = m_header.getIP() + SystemConfig.MSG_SEPATATE + m_header.getSessionid()
						+ SystemConfig.MSG_SEPATATE + m_header.getVersion();
				String body = "";
				if (nr != null) {
					if (nr instanceof byte[]) 
						body = NetUtil.getString((byte[]) nr, SystemConfig.CharsetName);
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
				cs = connection.prepareCall("{call pack_i_main_enter_cursor(?,?,?,?,?,?,?,?,?,?)}");
				cs.registerOutParameter(1, java.sql.Types.INTEGER);
				cs.setInt(2, cmdid);
				cs.setString(3, name);
				cs.setString(4, pwd);
				cs.setString(5, headinfo);

				String value = GetBodyType(cmdid);
				if (value != null && value.equalsIgnoreCase("SPLIT")) {
					String head = "";
					// 1.分析SPLIT数据
					StringTokenizer strToke = new StringTokenizer(body, "/|");
					if (body.indexOf("/") > 0) {
						head = strToke.nextToken("/");
					}
					// 2.
					while (strToke.hasMoreElements()) {
						body = head + strToke.nextToken("/|");
						cs.setString(6, body);
						cs.setInt(7, body.length());
						// 读取数据库
						Result = CallPro(cs, list, o_reserve);
						// 第一个循环出错便退出
						if (Result != SystemConfig.RETURN_SUCCESS)
							break;
					}
				} else {
					cs.setString(6, body);
					cs.setInt(7, bodylen);
					// 读取数据库
					Result = CallPro(cs, list, o_reserve);
				}
			}
		} catch (Exception e) {
			Result = -23;
			LoggerUtil.println(e.getMessage());
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
		int Result = SystemConfig.RETURN_SUCCESS;
		ResultSet rs = null;
		try {
			cs.registerOutParameter(8, java.sql.Types.VARCHAR);
			cs.registerOutParameter(9, java.sql.Types.VARCHAR);
			cs.registerOutParameter(10, java.sql.Types.VARCHAR);
			boolean hadResults = cs.execute();

			Result = cs.getInt(1);
			String s1 = cs.getString(8) == null ? "" : cs.getString(8);
			String s2 = cs.getString(9) == null ? "" : cs.getString(9);
			o_reserve = NetUtil.getBytes(s2, SystemConfig.CharsetName);
			if (list == null)
				return Result;

			// String classname = list.getClass().getSimpleName();
			if (StringUtil.empty(s1) == false) {
				// 用接口类代替了泛型与类型强转换
				if (list instanceof CChrList) {// classname.equalsIgnoreCase("CChrList")
					s1 += SystemConfig.MSG_SEPATATE;// socket需要用\t分行
				}
				list.Append(s1);
			}
			String line = "";
			// 获取记录集
			while (hadResults) {
				rs = cs.getResultSet();
				while (rs != null && rs.next()) {
					line = rs.getString("line");
					// 用接口类代替了泛型与类型强转换
					if (list instanceof CChrList) // classname.equalsIgnoreCase("CChrList")
					{
						line += SystemConfig.MSG_SEPATATE_LINE;
					}
					list.Append(line);
				}
				hadResults = cs.getMoreResults(); // 检查是否存在更多结果集
			}
		} catch (SQLException e) {
			Result = -23;
			LoggerUtil.println(e.getMessage());
		} finally {
			if (rs != null)
				try {
					rs.close();
					rs = null;
				} catch (Exception ignore) {
					;
				}
		}
		return Result;
	}
}
