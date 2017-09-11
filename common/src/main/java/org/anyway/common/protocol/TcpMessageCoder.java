/*
 * 名称: CSHTMsgStream.java
 * 描述: 网络包处理类
 * 版本：  1.0.0
 * 作者： 翁富家
 * 修改:
 * 日期：2013年08月16日
 * 修改日期:
 * 2015.8.18
 * 		用户名长度由17位修改为35位
 */

package org.anyway.common.protocol;

import java.io.UnsupportedEncodingException;

import org.apache.commons.lang.StringUtils;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.Unpooled;

import org.anyway.common.SystemConfig;
import org.anyway.common.enums.CryptEnum;
import org.anyway.common.types.pint;
import org.anyway.common.utils.NetUtil;
import org.anyway.common.utils.LoggerUtil;
import org.anyway.common.utils.SecretUtil;
import org.anyway.common.utils.StringUtil;
import org.anyway.common.protocol.header.DbHeader;
import org.anyway.common.protocol.header.Header;

public class TcpMessageCoder {
	//包头各字段的长度定义
	private static int[] HEADER_STRLENS = {7, 6, 6, 36, 17, 6, 4, 36, 36, 4, 40};
	//最大缓存
	private int maxCapacity  = 4096;
	
	private class M_MSG {
		private int nrlen;
		private byte[] nr;
		
		//包长 
	    public int getNrLen() {
	        return nrlen;
	    }
	    public void setNrLen(int iNrLen) {
	        this.nrlen = iNrLen;
	    }
	    
		//包体
	    public byte[] getNr() {
	        return nr;
	    }
	    public void setNr(byte[] Nr) {
	        this.nr = Nr;
	    }    
	    
	    @SuppressWarnings("unused")
		public void resetNrLen() {
	    	this.nrlen = this.nr.length;
	    }
	}

	private M_MSG m_msg = new M_MSG();
	private Header header = new Header();
	private DbHeader dbheader = new DbHeader();
	private String content = ""; 	//包体内容
	private String[] rows = null;	//行
	
	/**
	 * 清空
	 */
	public void ClearStream() {
		m_msg.setNr(null);
		m_msg.setNrLen(0);
		header.Clear();
		dbheader.Clear();
	}	
	
	/**
	 * 构造函数
	 */
	public TcpMessageCoder(int maxCapacity) {		
		ClearStream();
		this.maxCapacity = maxCapacity;
	}
    
    /**
     * EncodeHeader
     * @param h
     */
    public void EncodeHeader(Header h) {
    	header.Clear();
    	header = h;
    }
    
    /**
     * 获取包头
     * @return
     */
    public Header getHeader(){
    	return header;
    }
    
    /**
     * 设置数据库包头
     * @param h
     */
    public void SetDbHeader(DbHeader h) {
    	dbheader.setSeqID(h.getSeqID());
    }
    
    /**
     * 获取数据库包头
     * @return
     */
    public DbHeader GetDbHeader() {
      return dbheader;
    }

    /**
     * SetNr
     * @param p
     * @param len
     */
    public void SetNr(byte[] p,int len) {
    	byte[] buffer = null;
    	if (len > 0) {
    		buffer = new byte[len];   	
    		System.arraycopy(p, 0, buffer, 0, len);
    	}
    	m_msg.setNr(buffer);
    	m_msg.setNrLen(len);
    }

    /**
     * GetNr
     * 获取消息体
     * @param len
     * @return byte[]
     */
    public byte[] GetNr(pint len) {
    	len.setInt(m_msg.getNrLen());
    	return m_msg.getNr();
    }
    
    /**
     * GetString
     * 获取消息体
     * @return String
     * @throws UnsupportedEncodingException 
     */
    public String GetString() {
    	if (StringUtil.empty(this.content)) {
    		this.content = NetUtil.getString(m_msg.getNr(), SystemConfig.CharsetName);
    	}
    	return this.content;
    }

    /**
     * HasRow
     * 是否有有效的行列
     * @return
     */
    public boolean HasRow() {
    	GetRows();
    	if (null == this.rows || this.rows.length == 0) {
    		return false;
    	}
    	else {
    		return true;
    	}
    }
    
    /**
     * GetRows
     * 获取包体中的所有行（以\n为一行）
     * @return
     */
    public String[] GetRows() {
    	GetString();
    	if (null == this.rows) {
    		this.rows = StringUtils.splitPreserveAllTokens(this.content, SystemConfig.MSG_SEPATATE_LINE);
    	}
    	
    	return this.rows;
    }
    
    /**
     * GetRow
     * 获取包体中的某一行，并按\t进行分隔成数组
     * 该函数会判断是否有行存在或是否获取的行数有效
     * @param index
     * @return
     */
    public String[] GetRow(int index) {  	
    	//判断是否有行存在或是否获取的行数有效
    	if (HasRow() == false || index>=this.rows.length) {
    		return null;
    	}

    	String[] results = null;
    	//获取一行，并进行\t分隔
    	String row = this.rows[index];
    	if (StringUtil.empty(row) == false) {
    		results = StringUtils.splitPreserveAllTokens(row, SystemConfig.MSG_SEPATATE);
    	}
    	
    	return results;
    }
    
    /**
     * GetCommand
     * @return int 
     */
    public int GetCommand() {
    	return header.getCommandID();
    }

    /**
     * GetLength
     * @return int
     */
    public int GetLength() {
      return header.getLen();
    }

    /**
     * GetStatus
     * @return int
     */
    public int GetStatus() {
      return header.getStatus();
    }

    /**
     * GetSequence
     * @return String
     */
    public String GetSequence() {
      return header.getSequence();
    }

    /**
     * GetLength
     * @param buffer
     * @return int
     */
    public int GetLength(byte[] buffer)
    {
      byte[] p = new byte[10];
      System.arraycopy(buffer, 0, p, 0, 7); //包长为7位长的char
      return NetUtil.chars2int(p, SystemConfig.CharsetName);
    }

    /**
     * IsLastPacket
     * @return boolean
     */
    public boolean IsLastPacket()  {
      if (header.getResptype() == 1) {
        return true;
      }else {
        return false;
      }
    }
	
	/**
     * 头转换成byte[]
     * @return int
     */
    public int SetHeaderToBuffer(ByteBuf ibuffer, Header aheader){
    	if (ibuffer == null)
    		ibuffer = Unpooled.buffer(SystemConfig.HEADER_LENGTH, maxCapacity );

    	int ilen = 0;
    	byte[] tempbuff = NetUtil.int2chars(aheader.getLen());  	    	
    	tempbuff = NetUtil.expandBytes(tempbuff, HEADER_STRLENS[0]);
    	ibuffer.writeBytes(tempbuff);
    	//System.arraycopy(tempbuff, 0, buffer, 0, HEADER_STRLENS[0]);
    	ilen += HEADER_STRLENS[0];   	
    	tempbuff = NetUtil.int2chars(aheader.getCommandID());
    	tempbuff = NetUtil.expandBytes(tempbuff, HEADER_STRLENS[1]);
    	ibuffer.writeBytes(tempbuff);
    	//System.arraycopy(tempbuff, 0, buffer, ilen, HEADER_STRLENS[1]);
    	ilen += HEADER_STRLENS[1];
    	tempbuff = NetUtil.int2chars(aheader.getStatus());  	    	
    	tempbuff = NetUtil.expandBytes(tempbuff, HEADER_STRLENS[2]);
    	ibuffer.writeBytes(tempbuff);
    	//System.arraycopy(tempbuff, 0, buffer, ilen, HEADER_STRLENS[2]);
    	ilen += HEADER_STRLENS[2];
    	tempbuff = NetUtil.expandBytes(aheader.getSequence().getBytes(), HEADER_STRLENS[3]);
    	ibuffer.writeBytes(tempbuff);
    	//System.arraycopy(tempbuff, 0, buffer, ilen, HEADER_STRLENS[3]);
    	ilen += HEADER_STRLENS[3];
    	tempbuff = NetUtil.expandBytes(aheader.getAcknowledge().getBytes(), HEADER_STRLENS[4]);
    	ibuffer.writeBytes(tempbuff);
    	//System.arraycopy(tempbuff, 0, buffer, ilen, HEADER_STRLENS[4]);
    	ilen += HEADER_STRLENS[4];
    	tempbuff = NetUtil.expandBytes(aheader.getSessionid().getBytes(), HEADER_STRLENS[5]);
    	ibuffer.writeBytes(tempbuff);
    	//System.arraycopy(tempbuff, 0, buffer, ilen, HEADER_STRLENS[5]);
    	ilen += HEADER_STRLENS[5];
    	tempbuff = NetUtil.int2chars(aheader.getResptype());  	    	
    	tempbuff = NetUtil.expandBytes(tempbuff, HEADER_STRLENS[6]);
    	ibuffer.writeBytes(tempbuff);
    	//System.arraycopy(tempbuff, 0, buffer, ilen, HEADER_STRLENS[6]);
    	ilen += HEADER_STRLENS[6];    	
    	tempbuff = NetUtil.expandBytes(aheader.getUser().getBytes(), HEADER_STRLENS[7]);
    	ibuffer.writeBytes(tempbuff);
    	//System.arraycopy(tempbuff, 0, buffer, ilen, HEADER_STRLENS[7]);
    	ilen += HEADER_STRLENS[7];
    	tempbuff = NetUtil.expandBytes(aheader.getPwd().getBytes(), HEADER_STRLENS[8]);
    	ibuffer.writeBytes(tempbuff);
    	//System.arraycopy(tempbuff, 0, buffer, ilen, HEADER_STRLENS[8]);
    	ilen += HEADER_STRLENS[8];
    	tempbuff = NetUtil.expandBytes(aheader.getVersion().getBytes(), HEADER_STRLENS[9]);
    	ibuffer.writeBytes(tempbuff);
    	//System.arraycopy(tempbuff, 0, buffer, ilen, HEADER_STRLENS[9]);
    	ilen += HEADER_STRLENS[9];
    	tempbuff = NetUtil.expandBytes(aheader.getReserve().getBytes(), HEADER_STRLENS[10]);
    	ibuffer.writeBytes(tempbuff);
    	//System.arraycopy(tempbuff, 0, buffer, ilen, HEADER_STRLENS[10]);
    	ilen += HEADER_STRLENS[10];
    	return ilen;
    }
	
    public static int GetHeaderFromBuffer(byte[] buffer, Header aheader) throws Exception {
    	int i = NetUtil.readInt(buffer, 0, HEADER_STRLENS[0]);	
    	aheader.setLen(i);
		int ileft = HEADER_STRLENS[0];
		i = NetUtil.readInt(buffer, ileft, HEADER_STRLENS[1]);
		aheader.setCommandID(i);
		ileft += HEADER_STRLENS[1];
		i = NetUtil.readInt(buffer, ileft, HEADER_STRLENS[2]);
		aheader.setStatus(i);
		ileft += HEADER_STRLENS[2];
		
		String s = NetUtil.getString(buffer, ileft, HEADER_STRLENS[3], SystemConfig.CharsetName);
		aheader.setSequence(s);
		ileft += HEADER_STRLENS[3];
		s = NetUtil.getString(buffer, ileft, HEADER_STRLENS[4], SystemConfig.CharsetName);
		aheader.setAcknowledge(s);
		ileft += HEADER_STRLENS[4];
		s = NetUtil.getString(buffer, ileft, HEADER_STRLENS[5], SystemConfig.CharsetName);
		aheader.setSessionid(s);
		ileft += HEADER_STRLENS[5];
		i = NetUtil.readInt(buffer, ileft, HEADER_STRLENS[6]);		
		aheader.setResptype(i);
		ileft += HEADER_STRLENS[6];
		s = NetUtil.getString(buffer, ileft, HEADER_STRLENS[7], SystemConfig.CharsetName);
		aheader.setUser(s);
		ileft += HEADER_STRLENS[7];
		s = NetUtil.getString(buffer, ileft, HEADER_STRLENS[8], SystemConfig.CharsetName);
		aheader.setPwd(s);
		ileft += HEADER_STRLENS[8];
		s = NetUtil.getString(buffer, ileft, HEADER_STRLENS[9], SystemConfig.CharsetName);
		aheader.setVersion(s);
		ileft += HEADER_STRLENS[9];
		s = NetUtil.getString(buffer, ileft, HEADER_STRLENS[10], SystemConfig.CharsetName);
		aheader.setReserve(s);
		ileft += HEADER_STRLENS[10];
		return ileft;
    }
   
	/**
	 * SaveToStream 把buffer里的东西放到Stream里
	 * @param aheader
	 * @param body
	 * @param decrypt 是否需要加密
	 * @return
	 */
	public int SaveToStream(Header aheader, byte[] body, CryptEnum decrypt) {       
		int nrlen = 0;
		try {
			ClearStream();		
			header = aheader;
			//解析包体
			byte[] p =  null;
			if (body != null) {
				p = SecretUtil.Decrypt(body, decrypt);  			
			}
			if (p != null) {//再次检测解密后的包是否存在
				m_msg.setNr(p);
				nrlen = p.length;
			}
			m_msg.setNrLen(nrlen);
			header.setLen(SystemConfig.HEADER_LENGTH + nrlen);
		} 
		catch(OutOfMemoryError oome) {
			nrlen = 0;
			LoggerUtil.println(oome.getMessage());
		}
		catch (Throwable t) {
			nrlen = 0;
			LoggerUtil.println(t.getMessage());
		}
		return nrlen;
	}
	
	/**
	 * 把buffer里的东西放到Stream里
	 * @param buffer
	 * @param len
	 * @param decrypt
	 * @return
	 * @throws Exception
	 */
	public int SaveToStream(byte[] buffer, int len, CryptEnum decrypt) {
		int result = len;
		//byte[] p = new byte[uGlobalVar.HEADER_LENGTH]; //del by wfj 2014.11.12
		try {
			ClearStream();
			
			int ileft = 0;			
			//System.arraycopy(buffer, 0, p, 0, uGlobalVar.HEADER_LENGTH); //del by wfj 2014.11.12
			ileft = GetHeaderFromBuffer(buffer, header);

			//解析包体
			byte[] body = NetUtil.readBytes(buffer, ileft, len - SystemConfig.HEADER_LENGTH -1);
			int nrlen = 0;
			if (body != null && body.length>0) //3DES
			{
				body = SecretUtil.Decrypt(body, decrypt);
			}
			if (body != null) {//再次检测解密后的包是否存在
				m_msg.setNr(body);
				nrlen = body.length;
			}			
			m_msg.setNrLen(nrlen);
			header.setLen(SystemConfig.HEADER_LENGTH + nrlen +1);//包长重组，解密后长度会变动
			result = header.getLen();
		} 
		catch(OutOfMemoryError oome) {
			result = 0;
			//p = null;
			LoggerUtil.println(oome.getMessage());
		}
		catch (Throwable t) {
			result = 0;
			//p = null;
			LoggerUtil.println(t.getMessage());
		}
		return result;
	}
	
	/**
	 * LoadFromStream 把Stream里的东西读出到buffer里  ,不去长度的
	 * @param buffer
	 * @param encrypt
	 * @return
	 * @throws Exception 
	 */
	public int LoadFromStream(ByteBuf ibuffer, CryptEnum encrypt) {
		int len = 0;
		//包体		
		byte[] body = m_msg.getNr();
		try {
			if (body != null) 
			{
				body = SecretUtil.Encrypt(m_msg.getNr(), encrypt); 
				len = body.length;
			}	
			//包头
			len += SystemConfig.HEADER_LENGTH + 1;
			if (ibuffer.capacity()<len)
				ibuffer.capacity(len);//容量重新分配
			header.setLen(len);	
			SetHeaderToBuffer(ibuffer, header);
			//存入包体
			if (body != null) {
				ibuffer.writeBytes(body);
			}
			ibuffer.writeByte((byte) 0x0000);//buffer[len] = '\0';
		} 
		catch(OutOfMemoryError oome) {
			len = 0;
			LoggerUtil.println(oome.getMessage());
		}
		catch (Throwable t) {
			len = 0;
			LoggerUtil.println(t.getMessage());
		}
		return len;
	}
	
	//把Stream里的东西读出到buffer里   //不去长度的
	public ByteBuf LoadFromStream(pint plen, CryptEnum encrypt) {
		int len = 0;
		ByteBuf ibuffer = null;
		byte[] body = m_msg.getNr();
		try {
			if (body != null) 
			{
				body = SecretUtil.Encrypt(m_msg.getNr(), encrypt); 
				len = body.length;
				//System.arraycopy(body, 0, buffer, uGlobalVar.HEADER_LENGTH, m_msg.getNrLen());
			}
			//包头	  
			len += SystemConfig.HEADER_LENGTH + 1; //加一位'\0'
			ibuffer = ByteBufAllocator.DEFAULT.buffer(len);
			header.setLen(len);
			SetHeaderToBuffer(ibuffer, header);
			//参数返回
			if (body != null) {
				ibuffer.writeBytes(body);
			}
			plen.setInt(len); //返回长度
			ibuffer.writeByte((byte) 0x0000);
		} 
		catch(OutOfMemoryError oome) {
			if (ibuffer!=null) {ibuffer.clear(); ibuffer = null;}
			LoggerUtil.println(oome.getMessage());
		}
		catch (Throwable t) {
			if (ibuffer!=null) {ibuffer.clear(); ibuffer = null;}
			LoggerUtil.println(t.getMessage());
		}
		//buffer[len] = '\0';// array()[len - 1] = '\0';
		return ibuffer;
	}
	
	/**
	 * int转换成buffer,写到buffer里
	 * @param buffer
	 * @param i
	 * @param ilen
	 * @return 返回写入的长度
	 */
	public static int SetNextIntegerToBuffer(ByteBuf ibuffer,int i) {
		int result = 0;
		byte[] temp = new byte[40]; 
		try {		
			NetUtil.memset(temp, (byte) 0x0000, temp.length);	  
			temp = NetUtil.int2chars(i);			
		  	//k = Math.min(k, ilen);
		  	ibuffer.writeBytes(temp);
		  	ibuffer.writeByte((byte) 0x0000);//最后一位为网络字节0
		  	result = temp.length + 1;
		} 
		catch(OutOfMemoryError oome) {
			result = 0;
			temp = null;
			LoggerUtil.println(oome.getMessage());
		}
		catch (Throwable t) {
			result = 0;
			temp = null;
			LoggerUtil.println(t.getMessage());
		}
	  	//System.arraycopy(temp, 0, buffer, 0, k);
	  	//buffer[ilen-1] = '\0'; //最后一位赋网络字节0
	  	return result;
	}
	
	/***
	 * string写到buffer里
	 * @param buffer
	 * @param str
	 * @param slen
	 * @return 返回写入的长度
	 */
	public static int SetNextStrToBuffer_Fix(ByteBuf ibuffer, String str) {  		
		int result = 0;
		try { 
			ibuffer.writeBytes(str.getBytes(SystemConfig.CharsetName));
			ibuffer.writeByte((byte) 0x0000); //最后一位赋网络字节0
			result = str.length() + 1;
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch(OutOfMemoryError oome) {
			result = 0;
			LoggerUtil.println(oome.getMessage());
		}
		catch (Throwable t) {
			result = 0;
			LoggerUtil.println(t.getMessage());
		}
		//NetUtils.memset(buffer, Byte.parseByte(" "), slen);
		//buffer = str.getBytes(Charset.defaultCharset());
		//buffer[slen-1] = '\0'; //最后一位赋网络字节0
		return result;
	}
}
