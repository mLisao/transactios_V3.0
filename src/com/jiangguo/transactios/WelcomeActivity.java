package com.jiangguo.transactios;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Window;
import android.view.WindowManager;
import cn.bmob.im.BmobChat;
import cn.bmob.v3.Bmob;

import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.baidu.mapapi.SDKInitializer;
import com.jiangguo.transactios.config.Config;
import com.jiangguo.transactios.config.CustomApplcation;

public class WelcomeActivity extends BaseActivity {

	private LocationClient mLocationClient;// ��λ��ȡ��ǰ�û��ĵ���λ��
	private BaiduReceiver mReceiver;// ע��㲥�����������ڼ��������Լ���֤key

	private static final int GO_HOME = 100;// ��ת��������
	private static final int GO_LOGIN = 200;// ��ת����½����

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// ����ʾ����ı�����
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// ����ʾϵͳ�ı�����
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_welcome);
		// ��ʼ��Bmob��SDK
		Bmob.initialize(getApplicationContext(), Config.applicationId);
		// ����Ϊ����ģʽ
		BmobChat.DEBUG_MODE = true;
		// BmobIM SDK��ʼ��--ֻ��Ҫ��һ�δ��뼴����ɳ�ʼ��
		// �뵽Bmob����(http://www.bmob.cn/)����ApplicationId,�����ַ:http://docs.bmob.cn/android/faststart/index.html?menukey=fast_start&key=start_android
		BmobChat.getInstance(this).init(Config.applicationId);

		// ������λ
		initLocClient();
		// ע���ͼ SDK �㲥������
		IntentFilter iFilter = new IntentFilter();
		iFilter.addAction(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR);
		iFilter.addAction(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR);
		mReceiver = new BaiduReceiver();
		registerReceiver(mReceiver, iFilter);
	}

	// ��ʼ����λ
	private void initLocClient() {
		mLocationClient = CustomApplcation.getInstance().mLocationClient;
		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(LocationMode.Hight_Accuracy);// ���ö�λģʽ:�߾���ģʽ
		option.setCoorType("bd09ll"); // ������������:�ٶȾ�γ��
		option.setScanSpan(1000);// ���÷���λ����ļ��ʱ��Ϊ1000ms:����1000Ϊ�ֶ���λһ�Σ����ڻ����1000��Ϊ��ʱ��λ
		option.setIsNeedAddress(false);// ����Ҫ������ַ��Ϣ
		mLocationClient.setLocOption(option);
		mLocationClient.start();
	}

	@Override
	protected void onResume() {
		if (userManager.getCurrentUser() != null) {
			// ÿ���Զ���½��ʱ�����Ҫ�����µ�ǰλ�úͺ��ѵ����ϣ���Ϊ���ѵ�ͷ���ǳ�ɶ���Ǿ����䶯��
			updateUserInfos();
			mHandler.sendEmptyMessageDelayed(GO_HOME, 2000);//
		} else {
			mHandler.sendEmptyMessageDelayed(GO_LOGIN, 2000);
		}
		super.onResume();
	}

	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case GO_HOME:
				startAnimActivity(MainActivity.class);
				finish();
				break;
			case GO_LOGIN:
				startAnimActivity(LoginActivity.class);
				finish();
				break;
			}
		}
	};

	/**
	 * ����㲥�����࣬���� SDK key ��֤�Լ������쳣�㲥
	 */
	public class BaiduReceiver extends BroadcastReceiver {
		public void onReceive(Context context, Intent intent) {
			String s = intent.getAction();
			if (s.equals(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR)) {
				ShowToast("key ��֤����! ���� AndroidManifest.xml �ļ��м�� key ����");
			} else if (s
					.equals(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR)) {
				ShowToast("��ǰ�������Ӳ��ȶ�������������������!");
			}
		}
	}

	@Override
	protected void onDestroy() {
		// �˳�ʱ���ٶ�λ
		if (mLocationClient != null && mLocationClient.isStarted()) {
			mLocationClient.stop();
		}
		unregisterReceiver(mReceiver);
		super.onDestroy();
	}
}
