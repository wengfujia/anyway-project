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

package org.anyway.server.api;

import java.io.UnsupportedEncodingException;

import org.apache.commons.lang.StringUtils;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.Unpooled;

import org.anyway.common.uConfigVar;
import org.anyway.common.uGlobalVar;
import org.anyway.common.enums.CryptEnum;
import org.anyway.common.types.pint;
import org.anyway.common.utils.uNetUtil;
import org.anyway.common.utils.uLogger;
import org.anyway.common.utils.uSecretUtil;
import org.anyway.common.utils.uStringUtil;
import org.anyway.server.data.packages.DBHEADER;
import org.anyway.server.data.packages.HEADER;

public class CSHTMsgStream {
	//包头各字段的长度定义
	public static int[] HEADER_STRLENS = {7, 6, 6, 17, 17, 5, 3, 35, 35, 4, 39};
	private static String charsetname = uConfigVar.CharsetName;
	
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
	private HEADER header = new HEADER();
	private DBHEADER dbheader = new DBHEADER();
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
	public CSHTMsgStream(){		
		ClearStream();
	}
    
    /**
     * EncodeHeader
     * @param h
     */
    public void EncodeHeader(HEADER h) {
    	header.Clear();
    	header = h;
    }
    
    /**
     * 获取包头
     * @return
     */
    public HEADER getHeader(){
    	return header;
    }
    
    /**
     * 设置数据库包头
     * @param h
     */
    public void SetDbHeader(DBHEADER h) {
    	dbheader.setSeqID(h.getSeqID());
    }
    
    /**
     * 获取数据库包头
     * @return
     */
    public DBHEADER GetDbHeader() {
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
    	if (uStringUtil.empty(this.content)) {
    		this.content = uNetUtil.getString(m_msg.getNr(), uConfigVar.CharsetName);
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
    		this.rows = StringUtils.splitPreserveAllTokens(this.content, uGlobalVar.MSG_SEPATATE_LINE);
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
    	if (uStringUtil.empty(row) == false) {
    		results = StringUtils.splitPreserveAllTokens(row, uGlobalVar.MSG_SEPATATE);
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
      return uNetUtil.chars2int(p, charsetname);
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
    public static int SetHeaderToBuffer(ByteBuf ibuffer, HEADER aheader){
    	if (ibuffer == null)
    		ibuffer = Unpooled.buffer(uGlobalVar.HEADER_LENGTH, uConfigVar.US_MaxSendBufferSize);

    	int ilen = 0;
    	byte[] tempbuff = uNetUtil.int2chars(aheader.getLen());  	    	
    	tempbuff = uNetUtil.expandBytes(tempbuff, HEADER_STRLENS[0]);
    	ibuffer.writeBytes(tempbuff);
    	//System.arraycopy(tempbuff, 0, buffer, 0, HEADER_STRLENS[0]);
    	ilen += HEADER_STRLENS[0];   	
    	tempbuff = uNetUtil.int2chars(aheader.getCommandID());
    	tempbuff = uNetUtil.expandBytes(tempbuff, HEADER_STRLENS[1]);
    	ibuffer.writeBytes(tempbuff);
    	//System.arraycopy(tempbuff, 0, buffer, ilen, HEADER_STRLENS[1]);
    	ilen += HEADER_STRLENS[1];
    	tempbuff = uNetUtil.int2chars(aheader.getStatus());  	    	
    	tempbuff = uNetUtil.expandBytes(tempbuff, HEADER_STRLENS[2]);
    	ibuffer.writeBytes(tempbuff);
    	//System.arraycopy(tempbuff, 0, buffer, ilen, HEADER_STRLENS[2]);
    	ilen += HEADER_STRLENS[2];
    	tempbuff = uNetUtil.expandBytes(aheader.getSequence().getBytes(), HEADER_STRLENS[3]);
    	ibuffer.writeBytes(tempbuff);
    	//System.arraycopy(tempbuff, 0, buffer, ilen, HEADER_STRLENS[3]);
    	ilen += HEADER_STRLENS[3];
    	tempbuff = uNetUtil.expandBytes(aheader.getAcknowledge().getBytes(), HEADER_STRLENS[4]);
    	ibuffer.writeBytes(tempbuff);
    	//System.arraycopy(tempbuff, 0, buffer, ilen, HEADER_STRLENS[4]);
    	ilen += HEADER_STRLENS[4];
    	tempbuff = uNetUtil.expandBytes(aheader.getSessionid().getBytes(), HEADER_STRLENS[5]);
    	ibuffer.writeBytes(tempbuff);
    	//System.arraycopy(tempbuff, 0, buffer, ilen, HEADER_STRLENS[5]);
    	ilen += HEADER_STRLENS[5];
    	tempbuff = uNetUtil.int2chars(aheader.getResptype());  	    	
    	tempbuff = uNetUtil.expandBytes(tempbuff, HEADER_STRLENS[6]);
    	ibuffer.writeBytes(tempbuff);
    	//System.arraycopy(tempbuff, 0, buffer, ilen, HEADER_STRLENS[6]);
    	ilen += HEADER_STRLENS[6];    	
    	tempbuff = uNetUtil.expandBytes(aheader.getUser().getBytes(), HEADER_STRLENS[7]);
    	ibuffer.writeBytes(tempbuff);
    	//System.arraycopy(tempbuff, 0, buffer, ilen, HEADER_STRLENS[7]);
    	ilen += HEADER_STRLENS[7];
    	tempbuff = uNetUtil.expandBytes(aheader.getPwd().getBytes(), HEADER_STRLENS[8]);
    	ibuffer.writeBytes(tempbuff);
    	//System.arraycopy(tempbuff, 0, buffer, ilen, HEADER_STRLENS[8]);
    	ilen += HEADER_STRLENS[8];
    	tempbuff = uNetUtil.expandBytes(aheader.getVersion().getBytes(), HEADER_STRLENS[9]);
    	ibuffer.writeBytes(tempbuff);
    	//System.arraycopy(tempbuff, 0, buffer, ilen, HEADER_STRLENS[9]);
    	ilen += HEADER_STRLENS[9];
    	tempbuff = uNetUtil.expandBytes(aheader.getReserve().getBytes(), HEADER_STRLENS[10]);
    	ibuffer.writeBytes(tempbuff);
    	//System.arraycopy(tempbuff, 0, buffer, ilen, HEADER_STRLENS[10]);
    	ilen += HEADER_STRLENS[10];
    	return ilen;
    }
	
    public static int GetHeaderFromBuffer(byte[] buffer, HEADER aheader) throws Exception {
    	int i = uNetUtil.readInt(buffer, 0, HEADER_STRLENS[0]);	
    	aheader.setLen(i);
		int ileft = HEADER_STRLENS[0];
		i = uNetUtil.readInt(buffer, ileft, HEADER_STRLENS[1]);
		aheader.setCommandID(i);
		ileft += HEADER_STRLENS[1];
		i = uNetUtil.readInt(buffer, ileft, HEADER_STRLENS[2]);
		aheader.setStatus(i);
		ileft += HEADER_STRLENS[2];
		
		String s = uNetUtil.getString(buffer, ileft, HEADER_STRLENS[3], charsetname);
		aheader.setSequence(s);
		ileft += HEADER_STRLENS[3];
		s = uNetUtil.getString(buffer, ileft, HEADER_STRLENS[4], charsetname);
		aheader.setAcknowledge(s);
		ileft += HEADER_STRLENS[4];
		s = uNetUtil.getString(buffer, ileft, HEADER_STRLENS[5], charsetname);
		aheader.setSessionid(s);
		ileft += HEADER_STRLENS[5];
		i = uNetUtil.readInt(buffer, ileft, HEADER_STRLENS[6]);		
		aheader.setResptype(i);
		ileft += HEADER_STRLENS[6];
		s = uNetUtil.getString(buffer, ileft, HEADER_STRLENS[7], charsetname);
		aheader.setUser(s);
		ileft += HEADER_STRLENS[7];
		s = uNetUtil.getString(buffer, ileft, HEADER_STRLENS[8], charsetname);
		aheader.setPwd(s);
		ileft += HEADER_STRLENS[8];
		s = uNetUtil.getString(buffer, ileft, HEADER_STRLENS[9], charsetname);
		aheader.setVersion(s);
		ileft += HEADER_STRLENS[9];
		s = uNetUtil.getString(buffer, ileft, HEADER_STRLENS[10], charsetname);
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
	public int SaveToStream(HEADER aheader, byte[] body, CryptEnum decrypt) {       
		int nrlen = 0;
		try {
			ClearStream();		
			header = aheader;
			//解析包体
			byte[] p =  null;
			if (body != null) {
				p = uSecretUtil.Decrypt(body, decrypt);  			
			}
			if (p != null) {//再次检测解密后的包是否存在
				m_msg.setNr(p);
				nrlen = p.length;
			}
			m_msg.setNrLen(nrlen);
			header.setLen(uGlobalVar.HEADER_LENGTH + nrlen);
		} 
		catch(OutOfMemoryError oome) {
			nrlen = 0;
			uLogger.println(oome.getMessage());
		}
		catch (Throwable t) {
			nrlen = 0;
			uLogger.println(t.getMessage());
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
			byte[] body = uNetUtil.readBytes(buffer, ileft, len - uGlobalVar.HEADER_LENGTH -1);
			int nrlen = 0;
			if (body != null && body.length>0) //3DES
			{
				body = uSecretUtil.Decrypt(body, decrypt);
			}
			if (body != null) {//再次检测解密后的包是否存在
				m_msg.setNr(body);
				nrlen = body.length;
			}			
			m_msg.setNrLen(nrlen);
			header.setLen(uGlobalVar.HEADER_LENGTH + nrlen +1);//包长重组，解密后长度会变动
			result = header.getLen();
		} 
		catch(OutOfMemoryError oome) {
			result = 0;
			//p = null;
			uLogger.println(oome.getMessage());
		}
		catch (Throwable t) {
			result = 0;
			//p = null;
			uLogger.println(t.getMessage());
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
				body = uSecretUtil.Encrypt(m_msg.getNr(), encrypt); 
				len = body.length;
			}	
			//包头
			len += uGlobalVar.HEADER_LENGTH + 1;
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
			uLogger.println(oome.getMessage());
		}
		catch (Throwable t) {
			len = 0;
			uLogger.println(t.getMessage());
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
				body = uSecretUtil.Encrypt(m_msg.getNr(), encrypt); 
				len = body.length;
				//System.arraycopy(body, 0, buffer, uGlobalVar.HEADER_LENGTH, m_msg.getNrLen());
			}
			//包头	  
			len += uGlobalVar.HEADER_LENGTH + 1; //加一位'\0'
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
			uLogger.println(oome.getMessage());
		}
		catch (Throwable t) {
			if (ibuffer!=null) {ibuffer.clear(); ibuffer = null;}
			uLogger.println(t.getMessage());
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
			uNetUtil.memset(temp, (byte) 0x0000, temp.length);	  
			temp = uNetUtil.int2chars(i);			
		  	//k = Math.min(k, ilen);
		  	ibuffer.writeBytes(temp);
		  	ibuffer.writeByte((byte) 0x0000);//最后一位为网络字节0
		  	result = temp.length + 1;
		} 
		catch(OutOfMemoryError oome) {
			result = 0;
			temp = null;
			uLogger.println(oome.getMessage());
		}
		catch (Throwable t) {
			result = 0;
			temp = null;
			uLogger.println(t.getMessage());
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
			ibuffer.writeBytes(str.getBytes(uConfigVar.CharsetName));
			ibuffer.writeByte((byte) 0x0000); //最后一位赋网络字节0
			result = str.length() + 1;
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch(OutOfMemoryError oome) {
			result = 0;
			uLogger.println(oome.getMessage());
		}
		catch (Throwable t) {
			result = 0;
			uLogger.println(t.getMessage());
		}
		//NetUtils.memset(buffer, Byte.parseByte(" "), slen);
		//buffer = str.getBytes(Charset.defaultCharset());
		//buffer[slen-1] = '\0'; //最后一位赋网络字节0
		return result;
	}
}
