package com.jiangguo.adapter;

import java.util.List;

import com.jiangguo.adapter.base.BaseListAdapter;
import com.jiangguo.adapter.base.ViewHolder;
import com.jiangguo.bean.Goods;
import com.jiangguo.transactios.R;
import com.loopj.android.image.SmartImageView;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class SellingAdapter extends BaseListAdapter<Goods> {

	public boolean[] check = null;
	private TextView selling_id_name;
	private ImageView iv = null;
	private SmartImageView image = null;

	public SellingAdapter(Context context, List<Goods> list) {
		super(context, list);
	}

	@Override
	public View bindView(int position, View convertView, ViewGroup parent) {
		convertView = mInflater.inflate(R.layout.selling_list_item, parent,
				false);
		image = (SmartImageView) convertView.findViewById(R.id.selling_id_pic);
		image.setImageUrl(list.get(position).getPicture().get(0));
		iv = (ImageView) convertView.findViewById(R.id.check);
		iv.setVisibility(View.GONE);
		if (check != null) {
			if (check[position])
				iv.setVisibility(View.VISIBLE);
		}
		selling_id_name = ViewHolder.get(convertView, R.id.selling_id_name);

		selling_id_name.setText(list.get(position).getTitle());

		return convertView;
	}

	public void set(int arg2) {
		check[arg2] = !check[arg2];
	}

	public void setSize(int size) {
		check = new boolean[size];
		for (int i = 0; i < size; i++) {
			check[i] = false;
		}
	}

	public void setlist(List<Goods> sel) {
		list = sel;
		check = new boolean[list.size()];
		for (int i = 0; i < list.size(); i++) {
			check[i] = false;
		}
	}

}
