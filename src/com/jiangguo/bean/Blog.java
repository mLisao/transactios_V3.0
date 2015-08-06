package com.jiangguo.bean;

import cn.bmob.v3.BmobObject;

public class Blog extends BmobObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String brief;

	public MyUser getUser() {
		return user;
	}

	public void setUser(MyUser user) {
		this.user = user;
	}

	private MyUser user;

	public String getBrief() {
		return brief;
	}

	public void setBrief(String brief) {
		this.brief = brief;
	}
}
