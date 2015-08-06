package com.lisao.treeview.utils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import com.jiangguo.bean.Tags;
import com.jiangguo.transactios.R;
import com.lisao.treeview.utils.annotation.TreeNodeId;
import com.lisao.treeview.utils.annotation.TreeNodeLabel;
import com.lisao.treeview.utils.annotation.TreeNodePid;
import com.lisao.treeview.utils.annotation.TreeNodeTag;

public class TreeHelper {
	// 将用户数据转化为树形数据
	public static <T> List<Node> convertDatasToNodes(List<T> datas)
			throws IllegalArgumentException, IllegalAccessException {
		// 创建树形数据
		List<Node> nodes = new ArrayList<Node>();
		Node node = null;
		for (T t : datas) {
			node = new Node();
			// 通过反射拿到用户传递过来的数据属性上的注解
			Class clazz = t.getClass();
			Field[] fields = clazz.getDeclaredFields();
			for (Field field : fields) {
				// 树的ID
				if (field.getAnnotation(TreeNodeId.class) != null) {
					field.setAccessible(true);
					node.setId((String) field.get(t));
				}
				// 父亲ID
				if (field.getAnnotation(TreeNodePid.class) != null) {
					field.setAccessible(true);
					node.setPid((String) field.get(t));
				}
				// 显示文字
				if (field.getAnnotation(TreeNodeLabel.class) != null) {
					field.setAccessible(true);
					node.setName((String) field.get(t));
				}
				//
				if (field.getAnnotation(TreeNodeTag.class) != null) {
					field.setAccessible(true);
					node.setTags((Tags) field.get(t));
				}
			}
			nodes.add(node);
		}
		// 设置节点的关联关系
		for (int i = 0; i < nodes.size(); i++) {
			Node n = nodes.get(i);
			for (int j = i + 1; j < nodes.size(); j++) {
				Node m = nodes.get(j);
				if (m.getPid().equals(n.getId())) {
					n.getChildren().add(m);
					m.setParent(n);

				} else if (n.getPid().equals(m.getId())) {
					m.getChildren().add(n);
					n.setParent(m);
				} else {

				}
			}

		}
		for (Node n : nodes) {
			setNodeIcon(n);
		}
		return nodes;
	}

	// 排序
	public static <T> List<Node> getSortedNodes(List<T> datas,
			int defaultExpandLevel) throws IllegalArgumentException,
			IllegalAccessException {
		List<Node> result = new ArrayList<Node>();
		List<Node> nodes = convertDatasToNodes(datas);
		List<Node> rootNodes = getRootNodes(nodes);
		for (Node node : rootNodes) {
			addNode(result, node, defaultExpandLevel, 1);
		}
		return result;
	}

	private static void addNode(List<Node> result, Node node,
			int defaultExpandLevel, int currentLeval) {
		result.add(node);
		if (defaultExpandLevel >= currentLeval) {
			node.setExpand(true);
		}
		if (node.isLeaf())
			return;
		for (int i = 0; i < node.getChildren().size(); i++) {
			addNode(result, node.getChildren().get(i), defaultExpandLevel,
					currentLeval + 1);
		}

	}

	// 过滤出可见节点
	public static List<Node> fiterVisibleNode(List<Node> nodes) {
		List<Node> result = new ArrayList<Node>();
		for (Node node : nodes) {
			if (node.isRoot() || node.isParentExpand()) {
				setNodeIcon(node);
				result.add(node);
			}
		}
		return result;
	}

	// 从所有节点中过滤出根节点
	private static List<Node> getRootNodes(List<Node> nodes) {

		List<Node> root = new ArrayList<Node>();
		for (Node n : nodes) {
			if (n.isRoot()) {
				root.add(n);
			}
		}
		return root;
	}

	// 设置节点的图标
	private static void setNodeIcon(Node n) {
		// 有孩子 并且是展开的
		if (n.getChildren().size() > 0 && n.isExpand()) {
			n.setIcon(R.drawable.tree_ec);
		} else if (n.getChildren().size() > 0 && !n.isExpand()) {
			n.setIcon(R.drawable.tree_ex);
		} else {
			n.setIcon(-1);
		}
	}
}
