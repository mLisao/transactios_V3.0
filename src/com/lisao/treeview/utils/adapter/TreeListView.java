package com.lisao.treeview.utils.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.lisao.treeview.utils.Node;
import com.lisao.treeview.utils.TreeHelper;

public abstract class TreeListView<T> extends BaseAdapter {

	protected Context mContext;//
	protected List<Node> mAllNodes;// 所有的树形节点
	protected List<Node> mVisibleNodes;// 当前可见的节点
	protected LayoutInflater mInflater;
	protected ListView mTree;

	public interface OnTreeNodeClickListener {
		void onClick(Node node, int position);
	}

	private OnTreeNodeClickListener mListener;

	public void setOnTreeNodeClickListener(OnTreeNodeClickListener mListener) {
		this.mListener = mListener;
	}

	public TreeListView(ListView tree, Context context, List<T> datas,
			int defaultExpandLevel) throws IllegalArgumentException,
			IllegalAccessException {
		this.mContext = context;
		mInflater = LayoutInflater.from(mContext);
		this.mAllNodes = TreeHelper.getSortedNodes(datas, defaultExpandLevel);
		this.mVisibleNodes = TreeHelper.fiterVisibleNode(mAllNodes);
		this.mTree = tree;
		mTree.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				expandOrCollapse(position);
				if (mListener != null) {
					mListener.onClick(mVisibleNodes.get(position), position);
				}
			}

		});
	}

	private void expandOrCollapse(int position) {
		Node node = mVisibleNodes.get(position);
		if (node != null) {
			if (node.isLeaf())
				return;
			node.setExpand(!node.isExpand());
			mVisibleNodes = TreeHelper.fiterVisibleNode(mAllNodes);
			notifyDataSetChanged();
		}
	}

	@Override
	public int getCount() {
		return mVisibleNodes.size();
	}

	@Override
	public Object getItem(int position) {
		return mVisibleNodes.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Node node = mVisibleNodes.get(position);
		convertView = getConvertView(node, position, convertView, parent);
		convertView.setPadding(node.getLevel() * 30, 3, 3, 3);
		return convertView;
	}

	public abstract View getConvertView(Node node, int position,
			View convertView, ViewGroup parent);

	public void setDate(List<T> datas, int defaultExpandLevel) {
		try {
			this.mAllNodes = TreeHelper.getSortedNodes(datas,
					defaultExpandLevel);
			this.mVisibleNodes = TreeHelper.fiterVisibleNode(mAllNodes);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		notifyDataSetChanged();
	}
}
