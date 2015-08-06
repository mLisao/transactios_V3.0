package com.bmob.im.demo.ui;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.db.BmobDB;
import cn.bmob.v3.listener.UpdateListener;

import com.jiangguo.adapter.BlackListAdapter;
import com.jiangguo.transactios.ActivityBase;
import com.jiangguo.transactios.R;
import com.jiangguo.transactios.config.CustomApplcation;
import com.jiangguo.transactios.util.CollectionUtils;
import com.jiangguo.transactios.view.HeaderLayout;
import com.jiangguo.transactios.view.dailog.DialogTips;

/**
 * �������б�
 * 
 * @ClassName: BlackListActivity
 * @Description: TODO
 * @author smile
 * @date 2014-6-24 ����5:17:50
 */
public class BlackListActivity extends ActivityBase implements
		OnItemClickListener {

	ListView listview;
	BlackListAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_blacklist);
		initView();
	}

	private void initView() {
		mHeaderLayout = (HeaderLayout) findViewById(R.id.common_actionbar);
		initTopBarForLeft("������");
		adapter = new BlackListAdapter(this, BmobDB.create(this).getBlackList());
		listview = (ListView) findViewById(R.id.list_blacklist);
		listview.setOnItemClickListener(this);
		listview.setAdapter(adapter);
	}

	/**
	 * ��ʾ�Ƴ��������Ի���
	 * 
	 * @Title: showRemoveBlackDialog
	 * @Description: TODO
	 * @param @param position
	 * @param @param invite
	 * @return void
	 * @throws
	 */
	public void showRemoveBlackDialog(final int position,
			final BmobChatUser user) {
		DialogTips dialog = new DialogTips(this, "�Ƴ�������", "��ȷ����"
				+ user.getUsername() + "�Ƴ���������?", "ȷ��", true, true);
		// ���óɹ��¼�
		dialog.SetOnSuccessListener(new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialogInterface, int userId) {
				adapter.remove(position);
				userManager.removeBlack(user.getUsername(),
						new UpdateListener() {

							@Override
							public void onSuccess() {
								// TODO Auto-generated method stub
								ShowToast("�Ƴ��������ɹ�");
								// �����������ڴ��б���ĺ����б�
								CustomApplcation.getInstance().setContactList(
										CollectionUtils.list2map(BmobDB.create(
												getApplicationContext())
												.getContactList()));
							}

							@Override
							public void onFailure(int arg0, String arg1) {
								// TODO Auto-generated method stub
								ShowToast("�Ƴ�������ʧ��:" + arg1);
							}
						});
			}
		});
		// ��ʾȷ�϶Ի���
		dialog.show();
		dialog = null;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		BmobChatUser invite = (BmobChatUser) adapter.getItem(arg2);
		showRemoveBlackDialog(arg2, invite);
	}

}
