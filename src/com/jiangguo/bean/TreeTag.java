package com.jiangguo.bean;

import com.lisao.treeview.utils.annotation.TreeNodeId;
import com.lisao.treeview.utils.annotation.TreeNodeLabel;
import com.lisao.treeview.utils.annotation.TreeNodePid;
import com.lisao.treeview.utils.annotation.TreeNodeTag;

public class TreeTag {

	@TreeNodeId
	private String id;
	@TreeNodePid
	private String pid;
	@TreeNodeLabel
	private String name;
	@TreeNodeTag
	private Tags tag;

	public Tags getTag() {
		return tag;
	}

	public void setTag(Tags tag) {
		this.tag = tag;
	}

	public TreeTag(String id, String pid, String name, Tags tag) {
		super();
		this.id = id;
		this.pid = pid;
		this.name = name;
		this.tag = tag;
	}

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

}
