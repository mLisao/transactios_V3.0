package com.jiangguo.bean;

import java.util.List;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobRelation;

public class Goods extends BmobObject {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String title;// 标题
	private String content;// 商品详情
	private List<String> picture;// 商品所包含的图片
	private MyUser user;// 商品的发表人
	private BmobRelation comment;// 包含评论
	private double price;// 价格
	private Integer Praise;// 点赞数
	private Tags tag;// 从属类别
	private boolean IsFinish;// 是否卖出

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public boolean isIsFinish() {
		return IsFinish;
	}

	public void setIsFinish(boolean isFinish) {
		IsFinish = isFinish;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public List<String> getPicture() {
		return picture;
	}

	public void setPicture(List<String> picture) {
		this.picture = picture;
	}

	public MyUser getUser() {
		return user;
	}

	public void setUser(MyUser user) {
		this.user = user;
	}

	public BmobRelation getComment() {
		return comment;
	}

	public void setComment(BmobRelation comment) {
		this.comment = comment;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public Integer getPraise() {
		return Praise;
	}

	public void setPraise(Integer praise) {
		Praise = praise;
	}

	public Tags getTag() {
		return tag;
	}

	public void setTag(Tags tag) {
		this.tag = tag;
	}

}
