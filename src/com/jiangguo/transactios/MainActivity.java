package com.jiangguo.transactios;

import com.jiangguo.bean.MyUser;
import com.jiangguo.transactios.fragment.ContactFragment;
import com.jiangguo.transactios.fragment.IndexFragment;
import com.jiangguo.transactios.fragment.MyFragment;
import com.jiangguo.transactios.fragment.RecentFragment;
import com.jiangguo.transactios.getuserinfo.NetUtils;
import com.pgyersdk.update.PgyUpdateManager;
import com.pgyersdk.update.UpdateManagerListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.UpdateListener;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("HandlerLeak")
public class MainActivity extends ActivityBase implements OnClickListener {

	private String json = null;// 接收到的JSON字符串
	private String from = null;//

	private static final int QQ_INFO = 0;
	// 底部菜单五个按钮
	private ImageButton btnIndex = null;
	private ImageButton btnPut = null;
	private ImageButton btnChat = null;
	private ImageButton btnMy = null;
	private ImageButton btnContact = null;
	// 底部四个文字
	private TextView tvIndex = null;
	private TextView tvChat = null;
	private TextView tvMy = null;
	private TextView tvContact = null;
	// 四个LinearLayout
	private LinearLayout mTabIndex;
	private LinearLayout mTabChat;
	private LinearLayout mTabClassfiy;
	private LinearLayout mTabMy;

	private IndexFragment index;// 主页
	private ContactFragment contactFragment;// 好友
	private MyFragment my;// 个人中心
	private RecentFragment chat;// 消息

	private int LastIndex = 0;// 上一次 点击按钮的索引 用来设置切换效果
	// 记录BACK键的时间
	private long exitTime = 0;
	// 页面列表
	private ArrayList<Fragment> fragmentList = null;

	private AlertDialog.Builder dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		initView();
		PgyUpdateManager.register(MainActivity.this,
				"caa583deca0a8a5679df9ee45a794c06",
				new UpdateManagerListener() {
					@Override
					public void onUpdateAvailable(final String result) {
						UpdateNewVersion(result);
					}

					@Override
					public void onNoUpdateAvailable() {
						Log.i("update", "noupdate");
					}
				});
		initEvent();
		setSelect(1);
	}

	private void UpdateNewVersion(final String result) {
		dialog = new AlertDialog.Builder(this);
		dialog.setTitle("更新版本");
		dialog.setIcon(android.R.drawable.ic_dialog_info);
		dialog.setMessage("发现新版本，是否更新？");
		dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				try {
					JSONObject json = new JSONObject(result);
					JSONObject obj = json.getJSONObject("data");
					String downloadUrl = obj.getString("downloadURL");
					UpdateManagerListener.startDownloadTask(MainActivity.this,
							downloadUrl);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});
		dialog.setNegativeButton("取消", null);
		dialog.show();
	}

	// 初始化视图
	private void initView() {
		// 接收从LoginActivity传递的信息
		json = getIntent().getStringExtra("json");
		from = getIntent().getStringExtra("from");
		// 请求个人信息
		if (from != null && !from.equals("")) {
			if (from.equals("weibo")) {// 新浪微博登陆
				// getWeiboInfo();
			} else if (from.equals("qq")) {// QQ登陆
				getQQInfo();
			}
		}

		btnChat = (ImageButton) findViewById(R.id.btn_chat);
		btnContact = (ImageButton) findViewById(R.id.btn_classify);
		btnIndex = (ImageButton) findViewById(R.id.btn_index);
		btnMy = (ImageButton) findViewById(R.id.btn_my);
		btnPut = (ImageButton) findViewById(R.id.btn_put);
		tvIndex = (TextView) findViewById(R.id.tv_index);
		tvContact = (TextView) findViewById(R.id.tv_classify);
		tvChat = (TextView) findViewById(R.id.tv_chat);
		tvMy = (TextView) findViewById(R.id.tv_my);

		mTabIndex = (LinearLayout) findViewById(R.id.tab_index);
		mTabChat = (LinearLayout) findViewById(R.id.tab_chat);
		mTabClassfiy = (LinearLayout) findViewById(R.id.tab_classify);
		mTabMy = (LinearLayout) findViewById(R.id.tab_my);

	}

	// 初始化 所有事件
	private void initEvent() {
		btnPut.setOnClickListener(this);// 发布按钮的点击事件

		mTabIndex.setOnClickListener(this);
		mTabChat.setOnClickListener(this);
		mTabClassfiy.setOnClickListener(this);
		mTabMy.setOnClickListener(this);

	}

	/**
	 * 切换图片，文字为暗色
	 */
	private void resetImages() {
		btnIndex.setImageResource(R.drawable.tab01_nomal);
		btnChat.setImageResource(R.drawable.tab02_nomal);
		btnContact.setImageResource(R.drawable.tab03_nomal);
		btnMy.setImageResource(R.drawable.tab04_nomal);
		tvIndex.setTextColor(Color.rgb(162, 150, 134));
		tvContact.setTextColor(Color.rgb(162, 150, 134));
		tvChat.setTextColor(Color.rgb(162, 150, 134));
		tvMy.setTextColor(Color.rgb(162, 150, 134));
	}

	// 设置Fragment的切换动画
	private FragmentTransaction obtainFragmentTransaction(int index) {
		FragmentTransaction ft = this.getSupportFragmentManager()
				.beginTransaction();
		if (index > LastIndex) {
			ft.setCustomAnimations(R.anim.slide_left_in, R.anim.slide_left_out);
		} else {
			ft.setCustomAnimations(R.anim.slide_right_in,
					R.anim.slide_right_out);
		}
		return ft;
	}

	/**
	 * @author lisao
	 * @param transaction
	 *            点击 按钮 切换页面 先隐藏当前正在显示的Fragement
	 */
	private void hiddenFragement(FragmentTransaction transaction) {
		if (chat != null) {
			transaction.hide(chat);
		}
		if (contactFragment != null) {
			transaction.hide(contactFragment);
		}
		if (my != null) {
			transaction.hide(my);
		}
		if (index != null) {
			transaction.hide(index);
		}
	}

	private void setSelect(int i) {
		// 切换图片置选中状态
		// 切换设置内容区域
		if (i == LastIndex)
			return;
		resetImages();// 切换背景为暗

		FragmentTransaction transaction = obtainFragmentTransaction(i);
		hiddenFragement(transaction);// 隐藏当前显示 Fragement
		// 若fragment不存在，先创建，后显示。
		// 设置图标，文字为亮色
		switch (i) {
		case 1:
			if (index == null) {
				index = new IndexFragment();
				transaction.add(R.id.tab_content, index);
			} else {
				transaction.show(index);
			}
			btnIndex.setImageResource(R.drawable.tab01_pressed);
			tvIndex.setTextColor(Color.rgb(255, 215, 92));
			LastIndex = 1;
			break;
		case 2:
			if (chat == null) {
				chat = new RecentFragment();
				transaction.add(R.id.tab_content, chat);
			} else {
				transaction.show(chat);
			}
			btnChat.setImageResource(R.drawable.tab02_press);
			tvChat.setTextColor(Color.rgb(255, 215, 92));
			LastIndex = 2;
			break;
		case 4:
			if (contactFragment == null) {
				// classify = new Classify();
				contactFragment = new ContactFragment();
				transaction.add(R.id.tab_content, contactFragment);
			} else {
				transaction.show(contactFragment);
			}
			btnContact.setImageResource(R.drawable.tab03_press);
			tvContact.setTextColor(Color.rgb(255, 215, 92));
			LastIndex = 4;
			break;
		case 5:
			if (my == null) {
				my = new MyFragment();
				transaction.add(R.id.tab_content, my);
			} else {
				transaction.show(my);
			}
			btnMy.setImageResource(R.drawable.tab04_press);
			tvMy.setTextColor(Color.rgb(255, 215, 92));
			LastIndex = 5;
			break;
		}
		transaction.commit();
	}

	public void getQQInfo() {
		// 若更换为自己的APPID后，仍然获取不到自己的用户信息，则需要
		// 根据http://wiki.connect.qq.com/get_user_info提供的API文档，想要获取QQ用户的信息，则需要自己调用接口，传入对应的参数
		new Thread() {
			@Override
			public void run() {
				try {
					JSONObject obj = new JSONObject(json);
					Map<String, String> params = new HashMap<String, String>();
					params.put("access_token", obj.getJSONObject("qq")
							.getString("access_token"));// 此为QQ登陆成功之后返回access_token
					params.put("openid",
							obj.getJSONObject("qq").getString("openid"));
					params.put("oauth_consumer_key", "100330589");// oauth_consumer_key为申请QQ登录成功后，分配给应用的appid
					params.put("format", "json");// 格式--非必填项
					String result = NetUtils.getRequest(
							"https://graph.qq.com/user/get_user_info", params);
					Log.d("login", "QQ的个人信息：" + result);
					Message msg = new Message();
					msg.obj = result;
					msg.what = QQ_INFO;
					handler.sendMessage(msg);
				} catch (JSONException e) {
					e.printStackTrace();
				}

			}

		}.start();
	}

	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case QQ_INFO:
				String result = (String) msg.obj;
				if (result != null) {
					JSONObject json;
					try {
						json = new JSONObject(result);
						MyUser user = BmobUser.getCurrentUser(
								getApplicationContext(), MyUser.class);
						user.setNick(json.getString("nickname"));// 设置昵称为QQ昵称
						user.setSex(json.getString("gender").equals("男") ? true
								: false);
						user.setAvatar(json.getString("figureurl_qq_2"));// 设置头像
						user.update(getApplicationContext(),
								new UpdateListener() {

									@Override
									public void onSuccess() {
										toast("登陆成功");
									}

									@Override
									public void onFailure(int code, String msg) {
										toast("暂无法更新用户信息");
										ShowLog(msg);// 打log
									}
								});
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
				break;
			default:
				break;
			}
		}
	};

	// 获取FragmentManager
	public class MyViewPagerAdapter extends FragmentPagerAdapter {
		public MyViewPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			return fragmentList.get(position);
		}

		@Override
		public int getCount() {
			return fragmentList.size();
		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tab_index:
			setSelect(1);
			break;
		case R.id.tab_chat:
			setSelect(2);
			break;
		case R.id.btn_put:
			// RotateAnimation animation = new RotateAnimation(0, +90,
			// Animation.RELATIVE_TO_SELF, 0.5f,
			// Animation.RELATIVE_TO_SELF, 0.5f);
			// animation.setDuration(300);
			// v.findViewById(R.id.btn_put).startAnimation(animation);
			startAnimActivity(PutActivity.class);
			break;
		case R.id.tab_classify:
			setSelect(4);
			break;
		case R.id.tab_my:
			setSelect(5);
			break;
		}
	}

	// // ActionBar创建菜单
	// @Override
	// public boolean onCreateOptionsMenu(Menu menu) {
	// getMenuInflater().inflate(R.menu.main, menu);
	// return true;
	// }
	//
	// @Override
	// public boolean onOptionsItemSelected(MenuItem item) {
	// Intent intent = new Intent();
	// switch (item.getItemId()) {
	// case R.id.sell:// 发布新的的物品
	// intent.setClass(MainActivity.this, Put.class);
	// startActivity(intent);
	// break;
	// case R.id.aboutus:// 关于我们
	// ShowToast(item.getTitle().toString());
	// break;
	// case R.id.connectus:// 软件说明
	// ShowToast(item.getTitle().toString());
	// break;
	// case R.id.logout:// 登出当前用户，回到登陆界面
	// BmobUser.logOut(this); // 清除缓存用户对象
	// // BmobUser currentUser = BmobUser.getCurrentUser(this); //
	// // 现在的currentUser是null了
	// // 跳转到登陆界面
	// intent.setClass(MainActivity.this, LoginActivity.class);
	// startActivity(intent);
	// this.finish();// 销毁MainActivity,防止用户按返回键重新启动MainActivity
	// break;
	//
	// default:
	// break;
	// }
	// return true;
	// }

	// 双击退出
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			if ((System.currentTimeMillis() - exitTime) > 2000) {
				Toast.makeText(getApplicationContext(), "再次点击退出",
						Toast.LENGTH_SHORT).show();
				exitTime = System.currentTimeMillis();
			} else {
				// 将应用程序在后台运行
				finish();
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void toast(String msg) {
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}
}