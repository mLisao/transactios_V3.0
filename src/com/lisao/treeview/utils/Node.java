package com.lisao.treeview.utils;

import java.util.ArrayList;
import java.util.List;

import com.jiangguo.bean.Tags;

public class Node {
	private String id;//
	private String pid = "";
	private String name;// ��ʾ�ı�ǩ
	private int level;// ���
	private boolean isExpand = false;// �Ƿ�չ��
	private int icon;// ͼ��
	private Node parent;// ���׽ڵ�
	private List<Node> children = new ArrayList<Node>();// ���ӽڵ�
	private Tags tags;// �������

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

	// �жϵ�ǰ���ڵ��չ��״̬
	public boolean isParentExpand() {
		if (parent == null)
			return false;
		return parent.isExpand();
	}

	// �ж��Ƿ���Ҷ�ӽڵ�
	public boolean isLeaf() {
		return children.size() == 0;
	}

	// ��̬�������
	public int getLevel() {
		return parent == null ? 0 : parent.level + 1;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	// �Ƿ�չ��
	public boolean isExpand() {
		return isExpand;
	}

	// �ݹ������ӽڵ��չ��״̬
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
