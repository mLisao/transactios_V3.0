package com.jiangguo.bean;

import java.util.List;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobRelation;

public class Goods extends BmobObject {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String title;// ����
	private String content;// ��Ʒ����
	private List<String> picture;// ��Ʒ��������ͼƬ
	private MyUser user;// ��Ʒ�ķ�����
	private BmobRelation comment;// ��������
	private double price;// �۸�
	private Integer Praise;// ������
	private Tags tag;// �������
	private boolean IsFinish;// �Ƿ�����

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
