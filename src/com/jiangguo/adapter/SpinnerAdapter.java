package com.jiangguo.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jiangguo.adapter.base.BaseListAdapter;
import com.jiangguo.adapter.base.ViewHolder;
import com.jiangguo.bean.Tags;
import com.jiangguo.transactios.R;

public class SpinnerAdapter extends BaseListAdapter<Tags> {

	private TextView textView;

	public SpinnerAdapter(Context context, List<Tags> list) {
		super(context, list);
	}

	@Override
	public View bindView(int position, View convertView, ViewGroup parent) {
		convertView = mInflater.inflate(R.layout.tag_spinner_item, parent,
				false);
		textView = ViewHolder.get(convertView, R.id.tag_string);
		textView.setText(list.get(position).getValue());
		return convertView;
	}
}
