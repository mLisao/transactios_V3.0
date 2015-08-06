package com.jiangguo.transactios;

import java.util.List;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Toast;
import cn.bmob.im.BmobChatManager;
import cn.bmob.im.BmobUserManager;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.config.BmobConfig;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

import com.jiangguo.bean.MyUser;
import com.jiangguo.transactios.config.CustomApplcation;
import com.jiangguo.transactios.util.CollectionUtils;
import com.jiangguo.transactios.view.HeaderLayout;
import com.jiangguo.transactios.view.HeaderLayout.HeaderStyle;
import com.jiangguo.transactios.view.HeaderLayout.onLeftImageButtonClickListener;
import com.jiangguo.transactios.view.HeaderLayout.onRightImageButtonClickListener;
import com.jiangguo.transactios.view.dailog.DialogTips;

/**
 * 所有activity的基类
 * 
 * @ClassName: BaseActivity
 * @Description: TODO
 * @author smile
 * @date 2014-6-13 下午5:05:38
 */
public class BaseActivity extends FragmentActivity {

	protected BmobUserManager userManager;// 用户管理类--所有和用户有关的操作均使用此类：登陆、退出、获取好友列表、获取当前登陆用户、删除好友、添加好友等
	protected BmobChatManager manager;// 聊天管理-用于管理聊天：包括发送（聊天消息、Tag消息）、保存消息、绑定用户等

	protected CustomApplcation mApplication;// 自定义Application
	protected HeaderLayout mHeaderLayout;// 自定义头布局

	protected int mScreenWidth;// 屏幕像(px)宽度
	protected int mScreenHeight;// 屏幕像素(px)高度

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		userManager = BmobUserManager.getInstance(this);// 使用单例模式创建--双重锁定
		manager = BmobChatManager.getInstance(this);// 使用单例模式创建
		mApplication = CustomApplcation.getInstance();// 返回当前Application实例
		// 获取设备屏幕的像素
		DisplayMetrics metric = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metric);
		mScreenWidth = metric.widthPixels;
		mScreenHeight = metric.heightPixels;
	}

	Toast mToast;

	public void ShowToast(final String text) {
		if (!TextUtils.isEmpty(text)) {
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					if (mToast == null) {
						mToast = Toast.makeText(getApplicationContext(), text,
								Toast.LENGTH_LONG);
					} else {
						mToast.setText(text);
					}
					mToast.show();
				}
			});

		}
	}

	public void ShowToast(final int resId) {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				if (mToast == null) {
					mToast = Toast.makeText(
							BaseActivity.this.getApplicationContext(), resId,
							Toast.LENGTH_LONG);
				} else {
					mToast.setText(resId);
				}
				mToast.show();
			}
		});
	}

	/**
	 * 打Log ShowLog
	 * 
	 * @return void
	 * @throws
	 */
	public void ShowLog(String msg) {
		Log.i("lisao", msg);
	}

	/**
	 * 只有title initTopBarLayoutByTitle
	 * 
	 * @Title: initTopBarLayoutByTitle
	 * @throws
	 */
	public void initTopBarForOnlyTitle(String titleName) {
		mHeaderLayout = (HeaderLayout) findViewById(R.id.common_actionbar);
		mHeaderLayout.init(HeaderStyle.DEFAULT_TITLE);
		mHeaderLayout.setDefaultTitle(titleName);
	}

	/**
	 * 初始化标题栏-带左右按钮
	 * 
	 * @return void
	 * @throws
	 */
	public void initTopBarForBoth(String titleName, int rightDrawableId,
			String text, onRightImageButtonClickListener listener) {
		mHeaderLayout = (HeaderLayout) findViewById(R.id.common_actionbar);
		mHeaderLayout.init(HeaderStyle.TITLE_DOUBLE_IMAGEBUTTON);
		mHeaderLayout.setTitleAndLeftImageButton(titleName,
				R.drawable.base_action_bar_back_bg_selector,
				new OnLeftButtonClickListener());
		mHeaderLayout.setTitleAndRightButton(titleName, rightDrawableId, text,
				listener);
	}

	public void initTopBarForBoth(String titleName, int rightDrawableId,
			onRightImageButtonClickListener listener) {
		mHeaderLayout = (HeaderLayout) findViewById(R.id.common_actionbar);
		mHeaderLayout.init(HeaderStyle.TITLE_DOUBLE_IMAGEBUTTON);
		mHeaderLayout.setTitleAndLeftImageButton(titleName,
				R.drawable.base_action_bar_back_bg_selector,
				new OnLeftButtonClickListener());
		mHeaderLayout.setTitleAndRightImageButton(titleName, rightDrawableId,
				listener);
	}

	/**
	 * 只有左边按钮和Title initTopBarLayout
	 * 
	 * @throws
	 */
	public void initTopBarForLeft(String titleName) {
		mHeaderLayout = (HeaderLayout) findViewById(R.id.common_actionbar);
		mHeaderLayout.init(HeaderStyle.TITLE_DOUBLE_IMAGEBUTTON);
		mHeaderLayout.setTitleAndLeftImageButton(titleName,
				R.drawable.base_action_bar_back_bg_selector,
				new OnLeftButtonClickListener());
	}

	/**
	 * 显示下线的对话框 showOfflineDialog
	 * 
	 * @return void
	 * @throws
	 */
	public void showOfflineDialog(final Context context) {
		DialogTips dialog = new DialogTips(this, "您的账号已在其他设备上登录!", "重新登录");
		// 设置成功事件
		dialog.SetOnSuccessListener(new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialogInterface, int userId) {
				CustomApplcation.getInstance().logout();
				startActivity(new Intent(context, LoginActivity.class));
				finish();
				dialogInterface.dismiss();
			}
		});
		// 显示确认对话框
		dialog.show();
		dialog = null;
	}

	// 左边按钮的点击事件
	public class OnLeftButtonClickListener implements
			onLeftImageButtonClickListener {

		@Override
		public void onClick() {
			finish();
		}
	}

	public void startAnimActivity(Class<?> cla) {
		this.startActivity(new Intent(this, cla));
		// this.overridePendingTransition(R.anim.activity_open, 0);
	}

	public void startAnimActivity(Intent intent) {
		this.startActivity(intent);
		// this.overridePendingTransition(R.anim.activity_open, 0);
	}

	/**
	 * 用于登陆或者自动登陆情况下的用户资料及好友资料的检测更新
	 * 
	 * @Title: updateUserInfos
	 * @Description: TODO
	 * @param
	 * @return void
	 * @throws
	 */
	public void updateUserInfos() {
		// 更新地理位置信息
		updateUserLocation();
		// 查询该用户的好友列表(这个好友列表是去除黑名单用户的哦),目前支持的查询好友个数为100，如需修改请在调用这个方法前设置BmobConfig.LIMIT_CONTACTS即可。
		// 这里默认采取的是登陆成功之后即将好于列表存储到数据库中，并更新到当前内存中,
		userManager.queryCurrentContactList(new FindListener<BmobChatUser>() {

			@Override
			public void onError(int arg0, String arg1) {
				// TODO Auto-generated method stub
				if (arg0 == BmobConfig.CODE_COMMON_NONE) {
					ShowLog(arg1);
				} else {
					ShowLog("查询好友列表失败：" + arg1);
				}
			}

			@Override
			public void onSuccess(List<BmobChatUser> arg0) {
				// TODO Auto-generated method stub
				// 保存到application中方便比较
				CustomApplcation.getInstance().setContactList(
						CollectionUtils.list2map(arg0));
			}
		});
	}

	/**
	 * 更新用户的经纬度信息
	 * 
	 * @Title: uploadLocation
	 * @Description: TODO
	 * @param
	 * @return void
	 * @throws
	 */
	public void updateUserLocation() {
		if (CustomApplcation.lastPoint != null) {
			String saveLatitude = mApplication.getLatitude();
			String saveLongtitude = mApplication.getLongtitude();
			String newLat = String.valueOf(CustomApplcation.lastPoint
					.getLatitude());
			String newLong = String.valueOf(CustomApplcation.lastPoint
					.getLongitude());
			// ShowLog("saveLatitude ="+saveLatitude+",saveLongtitude = "+saveLongtitude);
			// ShowLog("newLat ="+newLat+",newLong = "+newLong);
			if (!saveLatitude.equals(newLat) || !saveLongtitude.equals(newLong)) {// 只有位置有变化就更新当前位置，达到实时更新的目的
				MyUser u = (MyUser) userManager.getCurrentUser(MyUser.class);
				final MyUser user = new MyUser();
				user.setLocation(CustomApplcation.lastPoint);
				user.setObjectId(u.getObjectId());
				user.update(this, new UpdateListener() {
					@Override
					public void onSuccess() {
						// TODO Auto-generated method stub
						CustomApplcation.getInstance()
								.setLatitude(
										String.valueOf(user.getLocation()
												.getLatitude()));
						CustomApplcation.getInstance().setLongtitude(
								String.valueOf(user.getLocation()
										.getLongitude()));
						// ShowLog("经纬度更新成功");
					}

					@Override
					public void onFailure(int code, String msg) {
						// TODO Auto-generated method stub
						// ShowLog("经纬度更新 失败:"+msg);
					}
				});
			} else {
				// ShowLog("用户位置未发生过变化");
			}
		}
	}
}
