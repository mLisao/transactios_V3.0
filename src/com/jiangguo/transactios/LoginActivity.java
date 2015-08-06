package com.jiangguo.transactios;

import org.json.JSONObject;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.OtherLoginListener;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;

public class LoginActivity extends BaseActivity implements OnClickListener {

	// private EditText username;
	// private EditText password;
	private Button login;
	// private TextView register_text;
	private ProgressDialog dialog = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);// ��ֹ���뷨����̸��������
		setContentView(R.layout.activity_login);
		initView();
		initEvent();
	}

	private void initEvent() {
		login.setOnClickListener(this);
	}

	private void initView() {
		// username = (EditText) findViewById(R.id.et_username);
		// password = (EditText) findViewById(R.id.et_password);
		login = (Button) findViewById(R.id.btn_qq_login);
		//
		// register_text = (TextView) findViewById(R.id.btn_register);

		dialog = new ProgressDialog(this);
		dialog.setMessage("���ڵ�½�����Ժ�......");
		dialog.setCanceledOnTouchOutside(false);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_qq_login:// ��½��ť
			dialog.show();
			UserLogin();
			break;

		default:
			break;
		}
	}

	private void UserLogin() {
		BmobUser.qqLogin(this, "100330589", new OtherLoginListener() {
			@Override
			public void onSuccess(JSONObject userAuth) {
				dialog.dismiss();
				Log.i("login", "QQ��½�ɹ�����:" + userAuth.toString());
				Intent intent = new Intent(LoginActivity.this,
						MainActivity.class);
				intent.putExtra("json", userAuth.toString());
				intent.putExtra("from", "qq");
				startActivity(intent);
				finish();
			}

			@Override
			public void onFailure(int code, String msg) {
				dialog.dismiss();
				ShowToast("��������½ʧ�ܣ�" + msg);
			}

			@Override
			public void onCancel() {
				dialog.dismiss();

			}
		});
		// BmobUser user = new MyUser();
		// String Uname = username.getText().toString();
		// String PWD = password.getText().toString();
		// // �ж��û��������Ƿ�Ϊ��
		// if (TextUtils.isEmpty(Uname)) {
		// Toast.makeText(getApplicationContext(), "�û�������Ϊ��",
		// Toast.LENGTH_SHORT).show();
		// return;
		// }
		// if (TextUtils.isEmpty(PWD)) {
		// Toast.makeText(getApplicationContext(), "���벻��Ϊ��",
		// Toast.LENGTH_SHORT).show();
		// return;
		// }
		//
		// dialog.show();
		// user.setUsername(Uname);
		// user.setPassword(PWD);
		// user.login(getApplicationContext(), new SaveListener() {
		//
		// @Override
		// public void onSuccess() {
		// dialog.dismiss();
		// Intent intent = new Intent();
		// intent.setClass(LoginActivity.this, MainActivity.class);
		// startActivity(intent);
		// Toast.makeText(getApplicationContext(), "��½�ɹ�",
		// Toast.LENGTH_SHORT).show();
		// finish();
		// }
		//
		// @Override
		// public void onFailure(int code, String msg) {
		// dialog.dismiss();
		// Log.e("login", msg);
		// Toast.makeText(getApplicationContext(), "��½ʧ��" + msg,
		// Toast.LENGTH_SHORT).show();
		// }
		// });
	}
}
