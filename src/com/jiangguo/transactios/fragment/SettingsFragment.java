package com.jiangguo.transactios.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.bmob.im.BmobUserManager;

import com.jiangguo.transactios.BlackListActivity;
import com.jiangguo.transactios.FragmentBase;
import com.jiangguo.transactios.LoginActivity;
import com.jiangguo.transactios.R;
import com.jiangguo.transactios.SetMyInfoActivity;
import com.jiangguo.transactios.config.CustomApplcation;
import com.jiangguo.transactios.util.SharePreferenceUtil;

/**
 * ����
 * 
 * @ClassName: SetFragment
 * @Description: TODO
 * @author smile
 * @date 2014-6-7 ����1:00:27
 */
@SuppressLint("SimpleDateFormat")
public class SettingsFragment extends FragmentBase implements OnClickListener{

	Button btn_logout;
	TextView tv_set_name;
	RelativeLayout layout_info, rl_switch_notification, rl_switch_voice,
			rl_switch_vibrate,layout_blacklist;

	ImageView iv_open_notification, iv_close_notification, iv_open_voice,
			iv_close_voice, iv_open_vibrate, iv_close_vibrate;
	
	View view1,view2;
	SharePreferenceUtil mSharedUtil;
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mSharedUtil = mApplication.getSpUtil();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return inflater.inflate(R.layout.fragment_set, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		initView();
		initData();
	}

	private void initView() {
		initTopBarForOnlyTitle("����");
		//�������б�
		layout_blacklist = (RelativeLayout) findViewById(R.id.layout_blacklist);
		
		layout_info = (RelativeLayout) findViewById(R.id.layout_info);
		rl_switch_notification = (RelativeLayout) findViewById(R.id.rl_switch_notification);
		rl_switch_voice = (RelativeLayout) findViewById(R.id.rl_switch_voice);
		rl_switch_vibrate = (RelativeLayout) findViewById(R.id.rl_switch_vibrate);
		rl_switch_notification.setOnClickListener(this);
		rl_switch_voice.setOnClickListener(this);
		rl_switch_vibrate.setOnClickListener(this);

		iv_open_notification = (ImageView) findViewById(R.id.iv_open_notification);
		iv_close_notification = (ImageView) findViewById(R.id.iv_close_notification);
		iv_open_voice = (ImageView) findViewById(R.id.iv_open_voice);
		iv_close_voice = (ImageView) findViewById(R.id.iv_close_voice);
		iv_open_vibrate = (ImageView) findViewById(R.id.iv_open_vibrate);
		iv_close_vibrate = (ImageView) findViewById(R.id.iv_close_vibrate);
		view1 = (View) findViewById(R.id.view1);
		view2 = (View) findViewById(R.id.view2);

		tv_set_name = (TextView) findViewById(R.id.tv_set_name);
		btn_logout = (Button) findViewById(R.id.btn_logout);

		// ��ʼ��
		boolean isAllowNotify = mSharedUtil.isAllowPushNotify();
		
		if (isAllowNotify) {
			iv_open_notification.setVisibility(View.VISIBLE);
			iv_close_notification.setVisibility(View.INVISIBLE);
		} else {
			iv_open_notification.setVisibility(View.INVISIBLE);
			iv_close_notification.setVisibility(View.VISIBLE);
		}
		boolean isAllowVoice = mSharedUtil.isAllowVoice();
		if (isAllowVoice) {
			iv_open_voice.setVisibility(View.VISIBLE);
			iv_close_voice.setVisibility(View.INVISIBLE);
		} else {
			iv_open_voice.setVisibility(View.INVISIBLE);
			iv_close_voice.setVisibility(View.VISIBLE);
		}
		boolean isAllowVibrate = mSharedUtil.isAllowVibrate();
		if (isAllowVibrate) {
			iv_open_vibrate.setVisibility(View.VISIBLE);
			iv_close_vibrate.setVisibility(View.INVISIBLE);
		} else {
			iv_open_vibrate.setVisibility(View.INVISIBLE);
			iv_close_vibrate.setVisibility(View.VISIBLE);
		}
		btn_logout.setOnClickListener(this);
		layout_info.setOnClickListener(this);
		layout_blacklist.setOnClickListener(this);

	}

	private void initData() {
		tv_set_name.setText(BmobUserManager.getInstance(getActivity())
				.getCurrentUser().getUsername());
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.layout_blacklist:// ������������ҳ��
			startAnimActivity(new Intent(getActivity(),BlackListActivity.class));
			break;
		case R.id.layout_info:// ��������������ҳ��
			Intent intent =new Intent(getActivity(),SetMyInfoActivity.class);
			intent.putExtra("from", "me");
			startActivity(intent);
			break;
		case R.id.btn_logout:
			CustomApplcation.getInstance().logout();
			getActivity().finish();
			startActivity(new Intent(getActivity(), LoginActivity.class));
			break;
		case R.id.rl_switch_notification:
			if (iv_open_notification.getVisibility() == View.VISIBLE) {
				iv_open_notification.setVisibility(View.INVISIBLE);
				iv_close_notification.setVisibility(View.VISIBLE);
				mSharedUtil.setPushNotifyEnable(false);
				rl_switch_vibrate.setVisibility(View.GONE);
				rl_switch_voice.setVisibility(View.GONE);
				view1.setVisibility(View.GONE);
				view2.setVisibility(View.GONE);
			} else {
				iv_open_notification.setVisibility(View.VISIBLE);
				iv_close_notification.setVisibility(View.INVISIBLE);
				mSharedUtil.setPushNotifyEnable(true);
				rl_switch_vibrate.setVisibility(View.VISIBLE);
				rl_switch_voice.setVisibility(View.VISIBLE);
				view1.setVisibility(View.VISIBLE);
				view2.setVisibility(View.VISIBLE);
			}

			break;
		case R.id.rl_switch_voice:
			if (iv_open_voice.getVisibility() == View.VISIBLE) {
				iv_open_voice.setVisibility(View.INVISIBLE);
				iv_close_voice.setVisibility(View.VISIBLE);
				mSharedUtil.setAllowVoiceEnable(false);
			} else {
				iv_open_voice.setVisibility(View.VISIBLE);
				iv_close_voice.setVisibility(View.INVISIBLE);
				mSharedUtil.setAllowVoiceEnable(true);
			}

			break;
		case R.id.rl_switch_vibrate:
			if (iv_open_vibrate.getVisibility() == View.VISIBLE) {
				iv_open_vibrate.setVisibility(View.INVISIBLE);
				iv_close_vibrate.setVisibility(View.VISIBLE);
				mSharedUtil.setAllowVibrateEnable(false);
			} else {
				iv_open_vibrate.setVisibility(View.VISIBLE);
				iv_close_vibrate.setVisibility(View.INVISIBLE);
				mSharedUtil.setAllowVibrateEnable(true);
			}
			break;

		}
	}

}
