package com.jiangguo.transactios.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.bmob.v3.BmobUser;

import com.jiangguo.bean.MyUser;
import com.jiangguo.transactios.LoginActivity;
import com.jiangguo.transactios.R;
import com.jiangguo.transactios.SelledActivity;
import com.jiangguo.transactios.SellingActivity;
import com.loopj.android.image.SmartImageView;

@SuppressWarnings("unused")
public class MyFragment extends FragmentBase implements OnClickListener {
	private MyUser user = null;

	private SmartImageView imageView;
	private TextView userName;
	private TextView qianming;

	private LinearLayout layout_id_selling;
	private LinearLayout layout_id_selled;
	private LinearLayout layout_id_logout;
	private LinearLayout layout_id_collect;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_my, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initView();// 初始化视图
		initEvent();// 初始化时间
	}

	// 初始化视图
	private void initView() {

		user = BmobUser.getCurrentUser(getActivity(), MyUser.class);
		imageView = (SmartImageView) getActivity().findViewById(
				R.id.headPicture);
		userName = (TextView) getActivity().findViewById(R.id.userName);
		qianming = (TextView) getActivity().findViewById(R.id.qianming);
		layout_id_selling = (LinearLayout) findViewById(R.id.layout_id_selling);
		layout_id_selled = (LinearLayout) findViewById(R.id.layout_id_selled);
		layout_id_logout = (LinearLayout) findViewById(R.id.layout_id_logout);
		layout_id_logout.setOnClickListener(this);
		layout_id_collect = (LinearLayout) findViewById(R.id.layout_id_collect);
		layout_id_collect.setOnClickListener(this);
	}

	// 初始化事件
	private void initEvent() {
		layout_id_selling.setOnClickListener(this);
		layout_id_selled.setOnClickListener(this);
		imageView.setImageUrl(user.getAvatar());
		userName.setText(user.getNick());
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.layout_id_selling:
			startAnimActivity(SellingActivity.class);
			break;
		case R.id.layout_id_selled:
			startAnimActivity(SelledActivity.class);
			break;
		case R.id.layout_id_logout:
			BmobUser.logOut(getActivity());
			startAnimActivity(LoginActivity.class);
			break;
		default:
			ShowToast("暂未开放,敬请期待！");
			break;
		}
	}
}
