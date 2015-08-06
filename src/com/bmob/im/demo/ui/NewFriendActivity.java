package com.bmob.im.demo.ui;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import cn.bmob.im.bean.BmobInvitation;
import cn.bmob.im.db.BmobDB;

import com.jiangguo.adapter.NewFriendAdapter;
import com.jiangguo.transactios.ActivityBase;
import com.jiangguo.transactios.MainActivity;
import com.jiangguo.transactios.R;
import com.jiangguo.transactios.view.ActionBar;
import com.jiangguo.transactios.view.ActionBar.onLeftButtonClickListener;
import com.jiangguo.transactios.view.dailog.DialogTips;

/**
 * ������
 * 
 * @ClassName: NewFriendActivity
 * @Description: TODO
 * @author smile
 * @date 2014-6-6 ����4:28:09
 */
public class NewFriendActivity extends ActivityBase implements
		OnItemLongClickListener {

	ListView listview;

	NewFriendAdapter adapter;

	String from = "";
	private ActionBar bar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_friend);
		from = getIntent().getStringExtra("from");
		initView();
	}

	private void initView() {
		bar = (ActionBar) findViewById(R.id.headbar);
		bar.setTitleAndLeftButton(R.drawable.back, "������");
		bar.setOnLeftButtonClickListener(new onLeftButtonClickListener() {

			@Override
			public void onClick() {
				finish();
			}
		});
		// initTopBarForLeft("������");
		listview = (ListView) findViewById(R.id.list_newfriend);
		listview.setOnItemLongClickListener(this);
		adapter = new NewFriendAdapter(this, BmobDB.create(this)
				.queryBmobInviteList());
		listview.setAdapter(adapter);
		if (from == null) {// ������֪ͨ���ĵ������λ�����һ��
			listview.setSelection(adapter.getCount());
		}
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
			int position, long arg3) {
		// TODO Auto-generated method stub
		BmobInvitation invite = (BmobInvitation) adapter.getItem(position);
		showDeleteDialog(position, invite);
		return true;
	}

	public void showDeleteDialog(final int position, final BmobInvitation invite) {
		DialogTips dialog = new DialogTips(this, invite.getFromname(),
				"ɾ����������", "ȷ��", true, true);
		// ���óɹ��¼�
		dialog.SetOnSuccessListener(new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialogInterface, int userId) {
				deleteInvite(position, invite);
			}
		});
		// ��ʾȷ�϶Ի���
		dialog.show();
		dialog = null;
	}

	/**
	 * ɾ������ deleteRecent
	 * 
	 * @param @param recent
	 * @return void
	 * @throws
	 */
	private void deleteInvite(int position, BmobInvitation invite) {
		adapter.remove(position);
		BmobDB.create(this).deleteInviteMsg(invite.getFromid(),
				Long.toString(invite.getTime()));
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (from == null) {
			startAnimActivity(MainActivity.class);
		}
	}

}
