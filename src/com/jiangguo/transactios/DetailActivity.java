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
	private LinearLayout comment;// ���۰�ť
	// private Button btnchat;// ����
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
		good = (Goods) this.getIntent().getSerializableExtra("one");// ��ô��ݵ����л�����
		init();
		initEvent();
	}

	private void init() {
		// ȡ����ؼ�
		// initTopBarForLeft(good.getTitle());
		bar = (ActionBar) findViewById(R.id.headbar);
		bar.setTitleAndLeftButton(R.drawable.back, "����");
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
		nickname.setText(good.getUser().getNick());// �ǳ�
		good_createtime.setText(good.getCreatedAt());// ��Ʒ����ʱ��
		gallery.setAdapter(galleryAdpter = new GalleryAdpter(mApplication, good
				.getPicture()));
		user = good.getUser();// �����Ʒ�ķ����û�
		goodTitle.setText(good.getTitle());
		goodPrice.setText("   ��" + good.getPrice());
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
			startChat();// ��ʼ����
			break;
		case R.id.ping:
			sendComment();// ��������
			break;
		default:
			break;
		}

	}

	// ��������
	private void sendComment() {
		if (TextUtils.isEmpty(comment_text.getText())) {
			ShowToast("����˵һ������");
			return;
		}
		final Comment comment = new Comment();
		comment.setUser(BmobChatUser.getCurrentUser(getApplicationContext(),
				MyUser.class));
		comment.setContent(comment_text.getText().toString());
		comment.setGoods(good);// ����Ϊ��ǰ��Ʒ
		comment.setTouser(user);
		// ����ǰҪ���۵��������������������Դ������UI��Ȼ�����������ϸ���
		data.add(0, comment);
		adapter.notifyDataSetChanged();
		comment.save(DetailActivity.this, new SaveListener() {

			@Override
			public void onSuccess() {
				ShowToast("���۳ɹ�");
				comment_text.setText("");
				addCommentToGoods(comment);
			}

			@Override
			public void onFailure(int arg0, String arg1) {

			}
		});
	}

	// ��ӹ�����ϵ���������������Ʒ�б���
	private void addCommentToGoods(Comment comment) {
		BmobRelation RelationGoods = new BmobRelation();
		RelationGoods.add(comment);
		good.setComment(RelationGoods);
		good.update(this);
	}

	// ��ʼ����
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
		// ����urls position���� ��ת��Show
		Bundle bundle = new Bundle();
		Intent intent = new Intent();
		bundle.putStringArray("url", good.getPicture().toArray(new String[] {}));// ����ͼƬ�б��URL����
		bundle.putInt("pos", position);
		intent.setClass(DetailActivity.this, ShowActivity.class);
		intent.putExtras(bundle);
		startActivity(intent);

	}

	// ��ȡ����
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

	// ����ListViewͷ����
	private View createListHeadView() {
		LayoutInflater inflater = LayoutInflater.from(mApplication);
		View v = inflater.inflate(R.layout.activity_detail_head, null);
		gridViewPic = (GridView) v.findViewById(R.id.gridView_show);
		goodTitle = (TextView) v.findViewById(R.id.good_title);
		goodPrice = (TextView) v.findViewById(R.id.good_price);
		goodContent = (TextView) v.findViewById(R.id.good_content);
		head = (SmartImageView) v.findViewById(R.id.head);
		nickname = (TextView) v.findViewById(R.id.nickname);
		gallery = (Gallery) v.findViewById(R.id.gallery);// ����
		good_createtime = (TextView) v.findViewById(R.id.good_createtime);
		return v;
	}
}
