package com.jiangguo.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.jiangguo.transactios.R;
import com.lisao.treeview.utils.Node;
import com.lisao.treeview.utils.adapter.TreeListView;

public class TreeViewAdapter<T> extends TreeListView<T> {

	public TreeViewAdapter(ListView tree, Context context, List<T> datas,
			int defaultExpanLever) throws IllegalArgumentException,
			IllegalAccessException {
		super(tree, context, datas, defaultExpanLever);
	}

	@Override
	public View getConvertView(Node node, int position, View convertView,
			ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.list_item, parent, false);
			holder = new ViewHolder();
			holder.mIcon = (ImageView) convertView.findViewById(R.id.item_icon);
			holder.mText = (TextView) convertView.findViewById(R.id.item_text);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		if (node.getIcon() == -1) {
			holder.mIcon.setVisibility(View.INVISIBLE);
		} else {
			holder.mIcon.setVisibility(View.VISIBLE);
			holder.mIcon.setImageResource(node.getIcon());
		}
		holder.mText.setText(node.getName());
		return convertView;
	}

	private class ViewHolder {
		ImageView mIcon;
		TextView mText;
	}

}
