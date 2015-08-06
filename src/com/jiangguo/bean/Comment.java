package com.jiangguo.bean;

import cn.bmob.v3.BmobObject;

public class Comment extends BmobObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String content;// 评论内容
	private MyUser user;// 评论人
	private MyUser touser;// 被回复人
	private Goods goods;// 被评论商品
	private int Praise;// 点赞数

	public int getPraise() {
		return Praise;
	}

	public void setPraise(int praise) {
		Praise = praise;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public MyUser getUser() {
		return user;
	}

	public void setUser(MyUser user) {
		this.user = user;
	}

	public Goods getGoods() {
		return goods;
	}

	public MyUser getTouser() {
		return touser;
	}

	public void setTouser(MyUser touser) {
		this.touser = touser;
	}

	public void setGoods(Goods goods) {
		this.goods = goods;
	}

}
