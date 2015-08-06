package com.jiangguo.transactios;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;

import com.jiangguo.adapter.GalleryAdpter;
import com.jiangguo.adapter.GoodsCommentAdapter;
import com.jiangguo.adapter.base.BaseListAdapter;
import com.jiangguo.adapter.base.ViewHolder;
import com.jiangguo.bean.Comment;
import com.jiangguo.bean.Goods;
import com.jiangguo.bean.MyUser;
import com.jiangguo.transactios.view.ActionBar;
import com.jiangguo.transactios.view.ActionBar.onLeftButtonClickListener;
import com.loopj.android.image.SmartImageView;

@SuppressWarnings("deprecation")
public class DetailActivity extends ActivityBase implements OnClickListener,
		OnItemClickListener {

	private GridView gridViewPic = null;
	private TextView goodTitle = null;
	private TextView goodPrice = null;
	private TextView goodContent = null;
	private SmartImageView head = null;
	private SmartImageView mImageView = null;
	private EditText comment_text;
	private LinearLayout comment;// 评论按钮
	// private Button btnchat;// 聊天
	private LinearLayout chat;
	private ListView commentlist;
	private Goods good = null;//
	private MyUser user = null;
	private List<Comment> data = null;
	private GoodsCommentAdapter adapter;
	private ActionBar bar;
	private Gallery gallery;
	private TextView nickname;
	private TextView good_createtime;
	private GalleryAdpter galleryAdpter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
						| WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		setContentView(R.layout.activity_detail);
		good = (Goods) this.getIntent().getSerializableExtra("one");// 获得传递的序列化对象
		init();
		initEvent();
	}

	private void init() {
		// 取得诸控件
		// initTopBarForLeft(good.getTitle());
		bar = (ActionBar) findViewById(R.id.headbar);
		bar.setTitleAndLeftButton(R.drawable.back, "详情");
		bar.setOnLeftButtonClickListener(new onLeftButtonClickListener() {

			@Override
			public void onClick() {
				finish();
			}
		});

		// btnchat = (Button) findViewById(R.id.chat);
		chat = (LinearLayout) findViewById(R.id.chat);
		comment = (LinearLayout) findViewById(R.id.ping);
		comment_text = (EditText) findViewById(R.id.good_pinglun);
		commentlist = (ListView) findViewById(R.id.pinglun);
		data = new ArrayList<Comment>();
		adapter = new GoodsCommentAdapter(getApplication(), data);
		commentlist.addHeaderView(createListHeadView());
		commentlist.setAdapter(adapter);
		nickname.setText(good.getUser().getNick());// 昵称
		good_createtime.setText(good.getCreatedAt());// 物品发表时间
		gallery.setAdapter(galleryAdpter = new GalleryAdpter(mApplication, good
				.getPicture()));
		user = good.getUser();// 获得商品的发表用户
		goodTitle.setText(good.getTitle());
		goodPrice.setText("   ￥" + good.getPrice());
		goodContent.setText(good.getContent());
		head.setImageUrl(good.getUser().getAvatar());
		gridViewPic.setAdapter(new myAdapter(this, good.getPicture()));
		getComment();
	}

	private void initEvent() {
		chat.setOnClickListener(this);
		comment.setOnClickListener(this);
		gridViewPic.setOnItemClickListener(this);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			DetailActivity.this.finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.chat:
			startChat();// 开始聊天
			break;
		case R.id.ping:
			sendComment();// 发表评论
			break;
		default:
			break;
		}

	}

	// 发送评论
	private void sendComment() {
		if (TextUtils.isEmpty(comment_text.getText())) {
			ShowToast("至少说一个字嘛");
			return;
		}
		final Comment comment = new Comment();
		comment.setUser(BmobChatUser.getCurrentUser(getApplicationContext(),
				MyUser.class));
		comment.setContent(comment_text.getText().toString());
		comment.setGoods(good);// 设置为当前商品
		comment.setTouser(user);
		// 将当前要评论的内容先添加至本地数据源，更行UI，然后再在网络上更新
		data.add(0, comment);
		adapter.notifyDataSetChanged();
		comment.save(DetailActivity.this, new SaveListener() {

			@Override
			public void onSuccess() {
				ShowToast("评论成功");
				comment_text.setText("");
				addCommentToGoods(comment);
			}

			@Override
			public void onFailure(int arg0, String arg1) {

			}
		});
	}

	// 添加关联关系，将评论添加至物品列表中
	private void addCommentToGoods(Comment comment) {
		BmobRelation RelationGoods = new BmobRelation();
		RelationGoods.add(comment);
		good.setComment(RelationGoods);
		good.update(this);
	}

	// 开始聊天
	private void startChat() {
		// Intent intent = new Intent(this, SetMyInfoActivity.class);
		// intent.putExtra("username", username);
		// intent.putExtra("from", "add");
		Intent intent = new Intent(this, ChatActivity.class);
		intent.putExtra("user", user);
		startAnimActivity(intent);
	}

	class myAdapter extends BaseListAdapter<String> {

		public myAdapter(Context context, List<String> list) {
			super(context, list);
		}

		@Override
		public View bindView(int position, View convertView, ViewGroup parent) {
			convertView = mInflater
					.inflate(R.layout.gridpicshow, parent, false);
			mImageView = ViewHolder.get(convertView, R.id.image);
			mImageView.setImageUrl(list.get(position));
			return convertView;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// 传递urls position参数 跳转到Show
		Bundle bundle = new Bundle();
		Intent intent = new Intent();
		bundle.putStringArray("url", good.getPicture().toArray(new String[] {}));// 传递图片列表的URL数组
		bundle.putInt("pos", position);
		intent.setClass(DetailActivity.this, ShowActivity.class);
		intent.putExtras(bundle);
		startActivity(intent);

	}

	// 获取评论
	private void getComment() {
		BmobQuery<Comment> query = new BmobQuery<Comment>();
		query.addWhereRelatedTo("comment", new BmobPointer(good));
		query.include("user,touser");
		query.order("-createdAt");
		query.findObjects(getApplicationContext(), new FindListener<Comment>() {

			@Override
			public void onSuccess(List<Comment> list) {
				data.addAll(list);
				adapter.notifyDataSetChanged();
			}

			@Override
			public void onError(int arg0, String arg1) {

			}
		});
	}

	// 创建ListView头布局
	private View createListHeadView() {
		LayoutInflater inflater = LayoutInflater.from(mApplication);
		View v = inflater.inflate(R.layout.activity_detail_head, null);
		gridViewPic = (GridView) v.findViewById(R.id.gridView_show);
		goodTitle = (TextView) v.findViewById(R.id.good_title);
		goodPrice = (TextView) v.findViewById(R.id.good_price);
		goodContent = (TextView) v.findViewById(R.id.good_content);
		head = (SmartImageView) v.findViewById(R.id.head);
		nickname = (TextView) v.findViewById(R.id.nickname);
		gallery = (Gallery) v.findViewById(R.id.gallery);// 画廊
		good_createtime = (TextView) v.findViewById(R.id.good_createtime);
		return v;
	}
}
