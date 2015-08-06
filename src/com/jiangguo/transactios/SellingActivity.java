package com.jiangguo.transactios;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

import com.jiangguo.adapter.SellingAdapter;
import com.jiangguo.bean.Goods;
import com.jiangguo.bean.MyUser;
import com.jiangguo.transactios.view.ActionBar;
import com.jiangguo.transactios.view.ActionBar.onLeftButtonClickListener;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;

public class SellingActivity extends ActivityBase implements OnClickListener {
	private ActionBar bar;
	private ListView selling_list;
	private List<Goods> goods_list = null;
	private SellingAdapter adapter;
	private MyUser user;// 缓存用户
	private Button sell_id_select;
	private List<BmobObject> select = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_selling);
		initView();
		initEvent();
	}

	private void initView() {
		bar = (ActionBar) findViewById(R.id.headbar);
		bar.setTitleAndLeftButton(R.drawable.back, "正在出售");
		bar.setOnLeftButtonClickListener(new onLeftButtonClickListener() {
			@Override
			public void onClick() {
				finish();
			}
		});
		selling_list = (ListView) findViewById(R.id.selling_list);
		sell_id_select = (Button) findViewById(R.id.sell_id_select);
		user = BmobUser.getCurrentUser(mApplication, MyUser.class);
	}

	private void initEvent() {
		goods_list = new ArrayList<Goods>();
		adapter = new SellingAdapter(this, goods_list);
		selling_list.setAdapter(adapter);
		getData();
		sell_id_select.setOnClickListener(this);
		selling_list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				adapter.set(arg2);
				adapter.notifyDataSetChanged();
			}
		});

	}

	private void getData() {
		BmobQuery<Goods> query = new BmobQuery<Goods>();
		query.addWhereEqualTo("user", new BmobPointer(user));
		query.include("user");
		query.order("-createdAt");
		query.addWhereEqualTo("IsFinish", false);
		query.findObjects(getApplicationContext(), new FindListener<Goods>() {

			@Override
			public void onSuccess(List<Goods> list) {
				adapter.setSize(list.size());
				goods_list = new ArrayList<Goods>();
				goods_list.addAll(list);
				adapter.setlist(goods_list);
				adapter.notifyDataSetChanged();

			}

			@Override
			public void onError(int arg0, String arg1) {
			}
		});
	}

	@Override
	public void onClick(View v) {
		select = new ArrayList<BmobObject>();

		Goods good = null;

		for (int i = 0; i < adapter.check.length; i++) {
			if (adapter.check[i]) {
				good = new Goods();
				good.setObjectId(goods_list.get(i).getObjectId());
				good.setIsFinish(true);
				select.add(good);
			}
		}

		new BmobObject().updateBatch(getApplicationContext(), select,
				new UpdateListener() {

					@Override
					public void onSuccess() {
						ShowToast("操作成功！");
						List<Goods> sel = new ArrayList<Goods>();
						
						for(int i = 0; i < adapter.check.length;i++){
							if( !adapter.check[i]){
								sel.add(goods_list.get(i));
							}
						}
						
						adapter.setlist(sel);
						adapter.notifyDataSetChanged();
					}

					@Override
					public void onFailure(int arg0, String arg1) {
						ShowToast("未知错误，稍后重试！");
					}
				});

	}
}
