package com.jiangguo.transactios;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.listener.DeleteListener;
import cn.bmob.v3.listener.FindListener;

import com.jiangguo.adapter.SellingAdapter;
import com.jiangguo.bean.Goods;
import com.jiangguo.bean.MyUser;
import com.jiangguo.transactios.view.ActionBar;
import com.jiangguo.transactios.view.ActionBar.onLeftButtonClickListener;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class SelledActivity extends ActivityBase implements OnItemClickListener,
		OnClickListener {
	private ActionBar bar;
	private ListView selled_list;
	private List<Goods> goods_list = null;
	private SellingAdapter adapter;
	private MyUser user;// 缓存用户
	private Button sell_id_select;
	private List<BmobObject> select = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_selling);
		init();
	}

	private void init() {
		bar = (ActionBar) findViewById(R.id.headbar);
		bar.setTitleAndLeftButton(R.drawable.back, "已经出售 ");
		bar.setOnLeftButtonClickListener(new onLeftButtonClickListener() {
			@Override
			public void onClick() {
				finish();
			}
		});
		selled_list = (ListView) findViewById(R.id.selling_list);
		sell_id_select = (Button) findViewById(R.id.sell_id_select);
		sell_id_select.setText("删除");
		user = BmobUser.getCurrentUser(mApplication, MyUser.class);

		goods_list = new ArrayList<Goods>();
		adapter = new SellingAdapter(this, goods_list);
		selled_list.setAdapter(adapter);
		getData();
		selled_list.setOnItemClickListener(this);
		sell_id_select.setOnClickListener(this);
	}

	private void getData() {
		BmobQuery<Goods> query = new BmobQuery<Goods>();
		query.addWhereEqualTo("user", new BmobPointer(user));
		query.include("user");
		query.order("-createdAt");
		query.addWhereEqualTo("IsFinish", true);
		query.findObjects(getApplicationContext(), new FindListener<Goods>() {

			@Override
			public void onSuccess(List<Goods> list) {
				adapter.setSize(list.size());
				goods_list.addAll(list);
				adapter.notifyDataSetChanged();
			}

			@Override
			public void onError(int arg0, String arg1) {
			}
		});

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		adapter.set(position);
		adapter.notifyDataSetChanged();
	}

	@Override
	public void onClick(View v) {
		select = new ArrayList<BmobObject>();
		Goods good = null;
		for (int i = 0; i < adapter.check.length; i++) {
			if (adapter.check[i]) {
				good = new Goods();
				good.setObjectId(goods_list.get(i).getObjectId());
				select.add(good);
			}
		}

		new BmobObject().deleteBatch(getApplicationContext(), select,
				new DeleteListener() {

					@Override
					public void onSuccess() {
						List<Goods> sel = new ArrayList<Goods>();
						for (int i = 0; i < adapter.check.length; i++) {
							if (adapter.check[i] == false) {
								sel.add(goods_list.get(i));
							}
						}

						adapter.setlist(sel);
						adapter.notifyDataSetChanged();

						ShowToast("清除成功！");
					}

					@Override
					public void onFailure(int arg0, String arg1) {
						ShowToast("未知错误，请稍后重试！");
					}
				});
	}

}
