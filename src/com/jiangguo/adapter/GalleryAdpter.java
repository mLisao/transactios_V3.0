package com.jiangguo.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.Gallery.LayoutParams;
import android.widget.ImageView.ScaleType;

import com.loopj.android.image.SmartImageView;

@SuppressWarnings("deprecation")
public class GalleryAdpter extends BaseAdapter {
	private List<String> data;
	private Context mContext;

	public GalleryAdpter(Context context, List<String> list) {
		this.data = list;
		this.mContext = context;
	}

	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		SmartImageView imageView = new SmartImageView(mContext);
		imageView.setImageUrl(data.get(position));
		imageView.setLayoutParams(new Gallery.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		imageView.setScaleType(ScaleType.CENTER_CROP);
		return imageView;
	}
}
