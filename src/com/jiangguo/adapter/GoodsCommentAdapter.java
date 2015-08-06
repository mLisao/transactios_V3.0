package com.jiangguo.adapter;

import java.util.List;

import com.jiangguo.adapter.base.BaseListAdapter;
import com.jiangguo.adapter.base.ViewHolder;
import com.jiangguo.bean.Comment;
import com.jiangguo.transactios.R;
import com.loopj.android.image.SmartImageView;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class GoodsCommentAdapter extends BaseListAdapter<Comment> {

	private TextView commtent_text;
	private SmartImageView commtent_user_head;
	private TextView comment_user_nickname;
	private TextView comment_creat_times;
	@SuppressWarnings("unused")
	private Button comment_reply;

	public GoodsCommentAdapter(Context context, List<Comment> data) {
		super(context, data);
	}

	@Override
	public View bindView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.comment, parent, false);
			commtent_text = ViewHolder.get(convertView, R.id.comment_text);
			commtent_user_head = ViewHolder.get(convertView,
					R.id.comment_user_head);
			comment_creat_times = ViewHolder.get(convertView,
					R.id.comment_creat_times);
			comment_user_nickname = ViewHolder.get(convertView,
					R.id.comment_user_nickname);
		}
		commtent_text.setText(list.get(position).getContent());
		comment_user_nickname.setText(list.get(position).getUser().getNick());
		comment_creat_times.setText("·¢±íÓÚ£º" + list.get(position).getCreatedAt());
		commtent_user_head
				.setImageUrl(list.get(position).getUser().getAvatar());
		return convertView;
	}
}
