/*
 * 名称: CInterface
 * 描述: 过程调用基类
 * 版本：  1.0.0
 * 作者： 翁富家
 * 修改:
 * 日期：2013年10月15日
 * 修改日期:
 */

package org.anyway.server.dbase.Providers;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.SQLException;

import org.anyway.common.utils.uClassUtil;
import org.anyway.common.utils.uLogger;
import org.anyway.server.data.contracts.IChrList;
import org.anyway.server.data.packages.DBHEADER;
import org.anyway.server.data.packages.HEADER;
import org.anyway.server.dbase.cache.DBCache;
import org.anyway.server.dbase.common.uLoadVar;
import org.anyway.server.dbase.common.enums.StatusEnum;

public abstract class CInterface<T> {
	HEADER m_header;
	DBHEADER m_dbheader;
	T m_nr;
	byte[] m_reserve;// = new byte[uGlobalVar.MAX_SENDBUFFER_SIZE];
	int m_type;

	protected CInterface() {
		m_header = null;// new HEADER();
		m_nr = null;
		m_dbheader = null;// new DBHEADER();
		m_type = 0;
		m_reserve = null;
	}

	protected void Clear() {
		m_header.Clear();
		m_nr = null;
		m_dbheader.Clear();
		m_reserve = null;
	}

	/*
	 * protected ConnectionHelper GetConnection() throws SQLException, Exception
	 * { //CDatabasePool pool = new CDatabasePool(5); //return
	 * pool.GetConnection(true, "", "", ""); return
	 * CDatabasePool.GetConnection(true, "", "", ""); }
	 */

	/**
	 * 得到错误代码
	 * 
	 * @param classname
	 * @param message
	 * @return
	 */
	protected int GetExceptCode(final String classname, final String message) {
		if (classname == "EOracleError") {
			return -21;
		} else if (message == "-23") {
			return -23;
		}
		return -22;
	}

	/**
	 * 获取body中的格式类型
	 * 
	 * @param cmdid
	 * @return
	 */
	protected String GetBodyType(int cmdid) {
		String key = uLogger.sprintf("CMD.%d", cmdid);
		return uLoadVar.GetBodyValue("", key);
	}

	/**
	 * 虚函数，等待继承类复盖
	 * 
	 * @param nr
	 * @param nrlen
	 * @param list
	 * @param o_reserve
	 * @return int
	 * @throws Exception
	 * @throws SQLException
	 */
	protected abstract int Handle_DB(T nr, int nrlen, IChrList list, byte[] o_reserve) throws SQLException, Exception;

	/**
	 * 处理消息入口
	 * @param header 包头
	 * @param dbheader 暂不用
	 * @param nr 内容
	 * @param nrlen 长度
	 * @param reserve 保留值
	 * @param type 1 直接返回，2入库以后处理,目前暂时是只有直接返回方式
	 * @param list 返回的字符串
	 * @param o_reserve 返回的保留值，备用
	 * @return 返回值：错误码，0表示正确 
	 */
	protected int Handle(HEADER header, DBHEADER dbheader, T nr, int nrlen, byte[] reserve, int type, IChrList list,
			byte[] o_reserve) {
		
		m_dbheader = dbheader;
		return this.Handle(header, nr, nrlen, reserve, type, list, o_reserve);
	}


	/**
	 * 处理消息入口
	 * @param header 包头
	 * @param nr 内容
	 * @param nrlen 长度
	 * @param reserve 保留值
	 * @param type 1 直接返回，2入库以后处理,目前暂时是2方式
	 * @param list 返回的字符串
	 * @param o_reserve 返回的保留值，备用
	 * @return 返回值：错误码，0表示正确 
	 */
	protected int Handle(HEADER header, T nr, int nrlen, byte[] reserve, int type, IChrList list, byte[] o_reserve) {
		/*
		 * 1.赋值m_header，m_nr,m_reserve,m_type 2.放到数据库中处理
		 */
		int result = 0;

		try {
			m_header = header;
			m_nr = nr;
			m_reserve = reserve;
//			if (reserve!=null)
//				System.arraycopy(reserve, 0, m_reserve, 0, reserve.length);
			m_type = type;

			result = Handle_DB(m_nr, nrlen, list, o_reserve);
		} catch (Exception E) {
			result = GetExceptCode(E.getClass().getName(), E.getMessage());
		}
		return result;
	}
	
	/**
	 * 处理消息入口
	 * 此入口为执行插件方式提供（setting.properties中cmd设为CLASS值）
	 * @param header 包头
	 * @param nr 内容
	 * @param nrlen 长度
	 * @param reserve 保留值
	 * @param list 返回值
	 * @return 错误码，0表示正确 
	 */
	protected int Handle(HEADER header, T nr, int nrlen, byte[] reserve, IChrList list) {
		
		Class<?> classType = uClassUtil.getMsgClassByType(header.getCommandID());
  		if (classType==null) { //找不到，退出
  			return -23;
  		}
  		
  		int result = 0;
  		Object invokerMessage = null;
		try {
			invokerMessage = classType.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			return -23;
		}
		try {
			if (nr instanceof byte[]) {
				Method decodeMethod = classType.getMethod("execute", new Class[] { HEADER.class, byte[].class, IChrList.class });
				result = (int) decodeMethod.invoke(invokerMessage, new Object[] {
						 header, (byte[])nr, list });
	  		}
	  		else if (nr instanceof String) {
	  			Method decodeMethod = classType.getMethod("execute", new Class[] { HEADER.class, String.class, IChrList.class });
				result = (int) decodeMethod.invoke(invokerMessage, new Object[] {
						 header, (String)nr, list });
	  		}
		} catch (NoSuchMethodException | SecurityException e) {
			result = GetExceptCode(e.getClass().getName(), e.getMessage());
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			result = GetExceptCode(e.getClass().getName(), e.getMessage());
		}
  		
		return result;
	}
	
	/**
	 * 检查用户是否合格
	 * 
	 * @return UserStatus
	 */
	protected StatusEnum CheckUser() {
		String name = m_header.getUser();
		String pwd = m_header.getPwd();
		return DBCache.CheckUser(name, pwd);
	}

}
