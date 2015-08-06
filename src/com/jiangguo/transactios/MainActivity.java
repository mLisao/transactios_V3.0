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

	private String json = null;// ���յ���JSON�ַ���
	private String from = null;//

	private static final int QQ_INFO = 0;
	// �ײ��˵������ť
	private ImageButton btnIndex = null;
	private ImageButton btnPut = null;
	private ImageButton btnChat = null;
	private ImageButton btnMy = null;
	private ImageButton btnContact = null;
	// �ײ��ĸ�����
	private TextView tvIndex = null;
	private TextView tvChat = null;
	private TextView tvMy = null;
	private TextView tvContact = null;
	// �ĸ�LinearLayout
	private LinearLayout mTabIndex;
	private LinearLayout mTabChat;
	private LinearLayout mTabClassfiy;
	private LinearLayout mTabMy;

	private IndexFragment index;// ��ҳ
	private ContactFragment contactFragment;// ����
	private MyFragment my;// ��������
	private RecentFragment chat;// ��Ϣ

	private int LastIndex = 0;// ��һ�� �����ť������ ���������л�Ч��
	// ��¼BACK����ʱ��
	private long exitTime = 0;
	// ҳ���б�
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
		dialog.setTitle("���°汾");
		dialog.setIcon(android.R.drawable.ic_dialog_info);
		dialog.setMessage("�����°汾���Ƿ���£�");
		dialog.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {

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
		dialog.setNegativeButton("ȡ��", null);
		dialog.show();
	}

	// ��ʼ����ͼ
	private void initView() {
		// ���մ�LoginActivity���ݵ���Ϣ
		json = getIntent().getStringExtra("json");
		from = getIntent().getStringExtra("from");
		// ���������Ϣ
		if (from != null && !from.equals("")) {
			if (from.equals("weibo")) {// ����΢����½
				// getWeiboInfo();
			} else if (from.equals("qq")) {// QQ��½
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

	// ��ʼ�� �����¼�
	private void initEvent() {
		btnPut.setOnClickListener(this);// ������ť�ĵ���¼�

		mTabIndex.setOnClickListener(this);
		mTabChat.setOnClickListener(this);
		mTabClassfiy.setOnClickListener(this);
		mTabMy.setOnClickListener(this);

	}

	/**
	 * �л�ͼƬ������Ϊ��ɫ
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

	// ����Fragment���л�����
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
	 *            ��� ��ť �л�ҳ�� �����ص�ǰ������ʾ��Fragement
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
		// �л�ͼƬ��ѡ��״̬
		// �л�������������
		if (i == LastIndex)
			return;
		resetImages();// �л�����Ϊ��

		FragmentTransaction transaction = obtainFragmentTransaction(i);
		hiddenFragement(transaction);// ���ص�ǰ��ʾ Fragement
		// ��fragment�����ڣ��ȴ���������ʾ��
		// ����ͼ�꣬����Ϊ��ɫ
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
		// ������Ϊ�Լ���APPID����Ȼ��ȡ�����Լ����û���Ϣ������Ҫ
		// ����http://wiki.connect.qq.com/get_user_info�ṩ��API�ĵ�����Ҫ��ȡQQ�û�����Ϣ������Ҫ�Լ����ýӿڣ������Ӧ�Ĳ���
		new Thread() {
			@Override
			public void run() {
				try {
					JSONObject obj = new JSONObject(json);
					Map<String, String> params = new HashMap<String, String>();
					params.put("access_token", obj.getJSONObject("qq")
							.getString("access_token"));// ��ΪQQ��½�ɹ�֮�󷵻�access_token
					params.put("openid",
							obj.getJSONObject("qq").getString("openid"));
					params.put("oauth_consumer_key", "100330589");// oauth_consumer_keyΪ����QQ��¼�ɹ��󣬷����Ӧ�õ�appid
					params.put("format", "json");// ��ʽ--�Ǳ�����
					String result = NetUtils.getRequest(
							"https://graph.qq.com/user/get_user_info", params);
					Log.d("login", "QQ�ĸ�����Ϣ��" + result);
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
						user.setNick(json.getString("nickname"));// �����ǳ�ΪQQ�ǳ�
						user.setSex(json.getString("gender").equals("��") ? true
								: false);
						user.setAvatar(json.getString("figureurl_qq_2"));// ����ͷ��
						user.update(getApplicationContext(),
								new UpdateListener() {

									@Override
									public void onSuccess() {
										toast("��½�ɹ�");
									}

									@Override
									public void onFailure(int code, String msg) {
										toast("���޷������û���Ϣ");
										ShowLog(msg);// ��log
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

	// ��ȡFragmentManager
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

	// // ActionBar�����˵�
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
	// case R.id.sell:// �����µĵ���Ʒ
	// intent.setClass(MainActivity.this, Put.class);
	// startActivity(intent);
	// break;
	// case R.id.aboutus:// ��������
	// ShowToast(item.getTitle().toString());
	// break;
	// case R.id.connectus:// ���˵��
	// ShowToast(item.getTitle().toString());
	// break;
	// case R.id.logout:// �ǳ���ǰ�û����ص���½����
	// BmobUser.logOut(this); // ��������û�����
	// // BmobUser currentUser = BmobUser.getCurrentUser(this); //
	// // ���ڵ�currentUser��null��
	// // ��ת����½����
	// intent.setClass(MainActivity.this, LoginActivity.class);
	// startActivity(intent);
	// this.finish();// ����MainActivity,��ֹ�û������ؼ���������MainActivity
	// break;
	//
	// default:
	// break;
	// }
	// return true;
	// }

	// ˫���˳�
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			if ((System.currentTimeMillis() - exitTime) > 2000) {
				Toast.makeText(getApplicationContext(), "�ٴε���˳�",
						Toast.LENGTH_SHORT).show();
				exitTime = System.currentTimeMillis();
			} else {
				// ��Ӧ�ó����ں�̨����
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