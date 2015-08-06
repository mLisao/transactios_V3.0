package com.lisao.treeview.utils;

import java.util.ArrayList;
import java.util.List;

import com.jiangguo.bean.Tags;

public class Node {
	private String id;//
	private String pid = "";
	private String name;// 显示的标签
	private int level;// 深度
	private boolean isExpand = false;// 是否展开
	private int icon;// 图标
	private Node parent;// 父亲节点
	private List<Node> children = new ArrayList<Node>();// 孩子节点
	private Tags tags;// 具体分类

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getIcon() {
		return icon;
	}

	public void setIcon(int icon) {
		this.icon = icon;
	}

	public Node getParent() {
		return parent;
	}

	public void setParent(Node parent) {
		this.parent = parent;
	}

	public List<Node> getChildren() {
		return children;
	}

	public void setChildren(List<Node> children) {
		this.children = children;
	}

	public boolean isRoot() {
		return parent == null;
	}

	// 判断当前父节点的展开状态
	public boolean isParentExpand() {
		if (parent == null)
			return false;
		return parent.isExpand();
	}

	// 判断是否是叶子节点
	public boolean isLeaf() {
		return children.size() == 0;
	}

	// 动态计算深度
	public int getLevel() {
		return parent == null ? 0 : parent.level + 1;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	// 是否展开
	public boolean isExpand() {
		return isExpand;
	}

	// 递归设置子节点的展开状态
	public void setExpand(boolean isExpand) {
		this.isExpand = isExpand;
		if (!isExpand) {
			for (Node node : children) {
				node.setExpand(false);
			}
		}
	}

	public Tags getTags() {
		return tags;
	}

	public void setTags(Tags tags) {
		this.tags = tags;
	}

}
