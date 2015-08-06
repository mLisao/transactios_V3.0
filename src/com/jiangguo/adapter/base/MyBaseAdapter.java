package com.jiangguo.adapter.base;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * 自定义的BaseAdapter类，无用
 * 
 * @author lisao
 * 
 * @param <E>
 */
@Deprecated
public abstract class MyBaseAdapter<E> extends BaseAdapter {
	protected List<E> list;
	protected Context mContext;
	protected LayoutInflater mInflater;

	@Override
	public int getCount() {
		return list.size();
	}

	public MyBaseAdapter(List<E> list, Context context) {
		this.list = list;
		this.mContext = context;
		mInflater = LayoutInflater.from(mContext);
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		convertView = bindView(position, convertView, parent);
		return convertView;
	}

	public abstract View bindView(int position, View convertView,
			ViewGroup parent);
}
