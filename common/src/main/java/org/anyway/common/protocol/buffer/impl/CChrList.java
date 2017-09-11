/*
 * 名称: CChrList
 * 描述: 自定义List类，用于socket
 * 版本：  1.0.0
 * 作者： 翁富家
 * 修改:
 * 日期：2013年10月15日
 * 修改日期:
 */

package org.anyway.common.protocol.buffer.impl;

import org.anyway.common.types.pint;
import org.anyway.common.utils.NetUtil;
import org.anyway.exceptions.NotEnoughDataInByteBufferException;
import org.anyway.common.SystemConfig;
import org.anyway.common.protocol.buffer.IChrList;

public class CChrList implements IChrList{
	
	class STRUCTCHAR {
		
		private STRUCTCHAR pre;
		private byte[] resp;
		private int len;
		private STRUCTCHAR next;
		
		/**
		 * getPre
		 * @return STRUCTCHAR
		 */
		public STRUCTCHAR getPre() {
	        return pre;
	    }
		/**
		 * setPre
		 * @param Pre
		 */
	    public void setPre(STRUCTCHAR Pre) {
	        this.pre = Pre;
	    } 
	    
	    /**
	     * getResp
	     * @return byte[]
	     */
	    public byte[] getResp() {
	        return resp;
	    }
	    /**
	     * setResp
	     * @param Resp
	     */
	    public void setResp(byte[] Resp) {
	    	this.resp = null;
	    	this.resp = Resp;
	    }
	    
	    /**
	     * getLen
	     * @return int
	     */
	    public int getLen() {
	        return len;
	    }
	    /**
	     * setLen
	     * @param Len
	     */
	    public void setLen(int Len) {
	        this.len = Len;
	    }
	    
	    /**
	     * getNext
	     * @return STRUCTCHAR
	     */
	    public STRUCTCHAR getNext() {
	        return next;
	    }
	    /**
	     * setNext
	     * @param Next
	     */
	    public void setNext(STRUCTCHAR Next) {
	        this.next = Next;
	    } 
	}
	
	//final class STRUCTCHAR extends structchar{};	
	private STRUCTCHAR head;
	private STRUCTCHAR tail;
	private STRUCTCHAR cur;
	private int m_Count;
	private int m_BufferSize = 1024*16; //最大分页尺寸，超出分包
	
	/**
	 * CChrList
	 */
	public CChrList() {
		head = null;
		cur = null;
		tail = null;
		m_Count = 0;
	}
	
	/**
	 * ClearAll
	 */
	public void  ClearAll() {
		for (STRUCTCHAR pS = tail; pS!=null; ) {
			STRUCTCHAR p = pS;
			pS = pS.getPre();
			if (pS != null)
				pS.setNext(null);
			tail = pS;
			m_Count--;
			p.setLen(0);
			p.setResp(null);
			p = null;
		}
		head = null;
		cur = null;
		tail = null;
		m_Count = 0;
	}

	/**
	 * 个数
	 * @return
	 */
	public int Count() {
		return m_Count;
	}
	
	/**
	 * Append
	 * @param nr
	 */
	public void Append(byte[] nr) {
		if (nr[0]=='\0') return;
	  
		int  len = nr.length;
		try {
			Append(nr,len);
		} catch (NotEnoughDataInByteBufferException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Append
	 * @param str
	 */
	public void Append(String str) {
		try {
			byte[] b = NetUtil.getBytes(str, SystemConfig.CharsetName);
			int  len = b.length;
			Append(b, len);	
		} catch (Exception e) {
			e.printStackTrace();
		}	
	}
	
	/**
	 * Append
	 * @param str
	 * @param len
	 * @throws NotEnoughDataInByteBufferException 
	 */
	public void Append(byte[] str,int len) throws NotEnoughDataInByteBufferException {
		int oldlen   = 0;
		if (tail != null) {
		    oldlen  = tail.getLen();
		}
		int nextlen = oldlen+len;
		STRUCTCHAR pS = tail;
		if ((oldlen == 0)||(nextlen >= m_BufferSize)) { //需要NEW
		    pS = new STRUCTCHAR();
		    pS.setPre(tail);
		    pS.setNext(null);
		    pS.setLen(len);
		    pS.setResp(str);
		    if (head == null) {
		      head = pS;
		      tail = pS;
		    }else {
		      tail.setNext(pS);
		      tail = pS;
		    }
		    m_Count++;
		}else { //不需要NEW
		    byte[] p = pS.getResp();
		    if (nextlen > pS.getLen()) {  //如果新的长度超过原来的长度，则进行扩长
		    	p = new byte[nextlen];
		    	System.arraycopy(pS.getResp(), 0, p, 0, pS.getLen());
		    	System.arraycopy(str, 0, p, pS.getLen(), len);
		    	pS.setResp(p);
		    }else { //追中在原数组后面
		    	System.arraycopy(str, 0, p, pS.getLen(), len);
		    }
		    pS.setLen(oldlen+len);
		}
	}
	
	/**
	 * First
	 * @param len
	 * @return byte[]
	 */
	public byte[] First(pint len) {
	  cur = head;
	  if (cur != null) {
	    len.setInt(cur.getLen());
	    return cur.getResp();
	  }else {
	    return null;
	  }
	}

	/**
	 * Next
	 * @param len
	 * @return byte[]
	 */
	public byte[] Next(pint len) {
	  if (cur != null) {
	    cur = cur.getNext();
	    if (cur != null) {
	      len.setInt(cur.getLen());
	      return cur.getResp();
	    }else {
	      return null;
	    }
	  }else {
	    return null;
	  }
	}

	/**
	 * Eof
	 * @return boolean
	 */
	public boolean Eof() {
	  if (cur == null) {
	    return true;
	  }else {
	    return false;
	  }
	}
	
	/**
	 * IsLast
	 * @return boolean
	 */
	public boolean IsLast() {
	  return (cur == tail);
	}
}
