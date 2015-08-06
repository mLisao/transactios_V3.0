package com.jiangguo.adapter;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.listener.CountListener;

import com.jiangguo.adapter.base.BaseListAdapter;
import com.jiangguo.adapter.base.ViewHolder;
import com.jiangguo.bean.Comment;
import com.jiangguo.bean.Goods;
import com.jiangguo.bean.MyUser;
import com.jiangguo.transactios.R;
import com.jiangguo.transactios.ShowActivity;
import com.loopj.android.image.RoundImageView;
import com.loopj.android.image.SmartImageView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ListViewAdapter extends BaseListAdapter<Goods> {
	private RoundImageView userHead = null;
	private ImageView sex = null;
	private TextView userName = null;
	private TextView time = null;
	private GridView pic = null;
	private TextView clickNum = null;
	private TextView askNum = null;
	private MyBaseAdapter mAdapter = null;
	private TextView content = null;
	private LinearLayout layout_like_click;//
	private ImageView click = null;
	@SuppressWarnings("unused")
	private ImageView ask = null;

	public ListViewAdapter(Context context, List<Goods> list) {
		super(context, list);
	}

	public interface HeadOnClick {
		void OnClick(MyUser user, int position);
	}

	private HeadOnClick mOnClick;

	public void setOnClick(HeadOnClick onClick) {
		this.mOnClick = onClick;
	}

	@Override
	public View bindView(final int Position, View convertView, ViewGroup parent) {
		convertView = mInflater.inflate(R.layout.listview, parent, false);// 获得视图
		// 取得控件
		userHead = ViewHolder.get(convertView, R.id.item_head_img);
		sex = ViewHolder.get(convertView, R.id.item_user_sex);
		userName = ViewHolder.get(convertView, R.id.item_user_name);
		pic = ViewHolder.get(convertView, R.id.item_gridview);
		content = ViewHolder.get(convertView, R.id.item_content);
		clickNum = ViewHolder.get(convertView, R.id.item_click);
		askNum = ViewHolder.get(convertView, R.id.item_ask);
		time = ViewHolder.get(convertView, R.id.item_time);
		pic = ViewHolder.get(convertView, R.id.item_gridview);
		layout_like_click = ViewHolder.get(convertView, R.id.layout_like_click);
		click = ViewHolder.get(convertView, R.id.item_like_click);
		click.setImageResource(R.drawable.item_click_nomal);
		userHead.setImageUrl(list.get(Position).getUser().getAvatar());
		layout_like_click.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Goods obj = list.get(Position);
				Integer num = obj.getPraise();
				obj.setPraise(num + 1);
				TextView text = (TextView) v.findViewById(R.id.item_click);
				text.setText((num + 1) + "");
				ImageView img = (ImageView) v
						.findViewById(R.id.item_like_click);
				img.setImageResource(R.drawable.item_click_pressed);
				obj.update(mContext);
				v.setClickable(false);
			}
		});
		// 因为暂时没有性别信息 默认为男 后续修改 去掉 == null 即可
		if (list.get(Position).getUser().getSex()) {
			sex.setImageResource(R.drawable.sex_man);
		} else {
			sex.setImageResource(R.drawable.sex_woman);
		}
		userHead.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mOnClick != null) {
					mOnClick.OnClick(list.get(Position).getUser(), Position);
				}
			}
		});
		// 设置显示昵称
		userName.setText(list.get(Position).getUser().getNick());
		// 设置显示 内容
		content.setText(list.get(Position).getContent());
		setCommentCount(askNum, list.get(Position));
		clickNum.setText(list.get(Position).getPraise() + "");
		// 发布日期
		time.setText(list.get(Position).getCreatedAt());
		// GridView的Adapter
		mAdapter = new MyBaseAdapter(mContext, list.get(Position).getPicture());
		// 数据源更改
		mAdapter.setDate(list.get(Position).getPicture());
		pic.setAdapter(mAdapter);
		pic.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int location, long id) {
				// 封装数据 传递参数 跳转
				int Count = list.get(Position).getPicture().size();
				Bundle mBundle = new Bundle();
				mBundle.putInt("pos", location);
				String[] mUrl = new String[Count];
				list.get(Position).getPicture().toArray(mUrl);
				mBundle.putStringArray("url", mUrl);
				Intent mIntent = new Intent();
				mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				mIntent.putExtras(mBundle);
				mIntent.setClass(mContext, ShowActivity.class);
				mContext.startActivity(mIntent);
			}
		});
		return convertView;
	}

	/**
	 * 设置评论的条数
	 * 
	 * @param askNum2
	 * @param goods
	 */
	private void setCommentCount(final TextView askNum2, final Goods goods) {
		BmobQuery<Comment> query = new BmobQuery<Comment>();
		query.addWhereRelatedTo("comment", new BmobPointer(goods));
		query.count(mContext, Comment.class, new CountListener() {

			@Override
			public void onFailure(int code, String msg) {
				askNum.setText(0 + "");// 留言数
			}

			@Override
			public void onSuccess(int n) {
				askNum.setText(n + "");// 留言数
			}
		});
	}
}

class MyBaseAdapter extends BaseListAdapter<String> {
	private SmartImageView mImageView = null;

	public MyBaseAdapter(Context context, List<String> list) {
		super(context, list);
	}

	public void setDate(List<String> list) {
		setList(list);
	}

	@Override
	public View bindView(int position, View convertView, ViewGroup parent) {
		convertView = mInflater.inflate(R.layout.item, parent, false);
		mImageView = ViewHolder.get(convertView, R.id.item_image);
		mImageView.setImageUrl(list.get(position));
		return convertView;
	}
}
