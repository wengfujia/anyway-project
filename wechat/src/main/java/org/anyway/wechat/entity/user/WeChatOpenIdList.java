package org.anyway.wechat.entity.user;

import java.util.ArrayList;
import java.util.List;

public class WeChatOpenIdList {

	private long total;
	private int count;
	private List<OpenId> data =  new ArrayList<OpenId>();
	private String next_openid;
	
	public static class OpenId{
		private String openid;

		public OpenId(String openid) {
			super();
			this.openid = openid;
		}

		public String getOpenid() {
			return openid;
		}

		public void setOpenid(String openid) {
			this.openid = openid;
		}
		
	}

	public long getTotal() {
		return total;
	}

	public void setTotal(long total) {
		this.total = total;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public List<OpenId> getData() {
		return data;
	}

	public void setData(List<OpenId> data) {
		this.data = data;
	}

	public String getNext_openid() {
		return next_openid;
	}

	public void setNext_openid(String next_openid) {
		this.next_openid = next_openid;
	}
}
