package com.jiangguo.bean;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobRelation;

public class Tags extends BmobObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private BmobRelation goods;// 包含物品
	private String value;// 类别字段
	private String ParentID;// 父类ID
	private int Depth;// 深度
	private int Priority;// 优先级

	public Tags() {
	}

	public String getParentID() {
		return ParentID;
	}

	public void setParentID(String parentID) {
		ParentID = parentID;
	}

	public int getDepth() {
		return Depth;
	}

	public void setDepth(int depth) {
		Depth = depth;
	}

	public int getPriority() {
		return Priority;
	}

	public void setPriority(int priority) {
		Priority = priority;
	}

	public Tags(String value) {
		this.value = value;
	}

	public BmobRelation getGoods() {
		return goods;
	}

	public void setGoods(BmobRelation goods) {
		this.goods = goods;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
