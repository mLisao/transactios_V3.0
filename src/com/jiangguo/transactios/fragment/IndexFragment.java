package com.jiangguo.transactios.fragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.util.TimeUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobQuery.CachePolicy;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.FindListener;

import com.bmob.im.demo.ui.SetMyInfoActivity;
import com.jiangguo.adapter.ListViewAdapter;
import com.jiangguo.adapter.ListViewAdapter.HeadOnClick;
import com.jiangguo.adapter.TreeViewAdapter;
import com.jiangguo.bean.Goods;
import com.jiangguo.bean.MyUser;
import com.jiangguo.bean.Tags;
import com.jiangguo.bean.TreeTag;
import com.jiangguo.transactios.DetailActivity;
import com.jiangguo.transactios.R;
import com.jiangguo.transactios.util.TimeUtil;
import com.jiangguo.transactios.view.ActionBar;
import com.jiangguo.transactios.view.ActionBar.onLeftButtonClickListener;
import com.jiangguo.transactios.view.ActionBar.onRightButtonClickListener;
import com.jiangguo.xlistview.XListView;
import com.jiangguo.xlistview.XListView.IXListViewListener;
import com.lisao.treeview.utils.Node;
import com.lisao.treeview.utils.adapter.TreeListView.OnTreeNodeClickListener;

@SuppressWarnings("deprecation")
public class IndexFragment extends FragmentBase {
	private XListView content_list;// 创建ListView对象 右边的内容
	private ListViewAdapter adapter;// 创建自定义适配器
	private ActionBar bar;// 自定义头部
	private ListView mCassify = null; // 左边分类
	private TreeViewAdapter<TreeTag> mAdapter = null; // 分类 Adapter
	private List<TreeTag> mDatas = null; // 分类数据
	private int curPage = 0;// 当前页面

	private static final int STATE_REFRESH = 0;// 下拉刷新
	private static final int STATE_MORE = 1;// 加载更多
	private static final int STATE_FIRST = -1;// 第一次加载
	List<Goods> data = new ArrayList<Goods>();// 数据源

	private ActionBarDrawerToggle mDrawerToggle = null;
	private Tags tag = null;// 侧边栏的分类

	private DrawerLayout mDrawerLayout = null;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_index, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		try {
			initView();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	private void initView() throws IllegalArgumentException,
			IllegalAccessException {
		// 设置头部布局
		bar = (ActionBar) findViewById(R.id.headbar);
		bar.SetTitleAndRightAndLeftButton(R.drawable.menu, R.drawable.search,
				"最新商品");
		bar.setOnLeftButtonClickListener(new onLeftButtonClickListener() {
			@Override
			public void onClick() {
				mDrawerLayout.openDrawer(mCassify);
			}
		});
		bar.setOnRightButtonClickListener(new onRightButtonClickListener() {
			@Override
			public void onClick() {
				ShowToast("点击进行搜索");
			}
		});

		// 侧边栏ListView
		mCassify = (ListView) getActivity().findViewById(R.id.classify_menu);
		mDatas = new ArrayList<TreeTag>();
		getDatas();// 获取分类数据
		mAdapter = new TreeViewAdapter<TreeTag>(mCassify, getActivity()
				.getApplicationContext(), mDatas, 0);
		mCassify.setAdapter(mAdapter);
		mAdapter.setOnTreeNodeClickListener(new OnTreeNodeClickListener() {

			@Override
			public void onClick(Node node, int position) {
				if (node.isLeaf()) {// 如果是叶子节点才做响应处理
					tag = node.getTags();
					changeDataSource(tag);// 更改数据源
					mDrawerLayout.closeDrawer(mCassify);
				}
			}

		});

		// 主要商品展示
		content_list = (XListView) getActivity()
				.findViewById(R.id.content_list);
		content_list.setPullLoadEnable(true);// 设置最底部加载为可点击
		adapter = new ListViewAdapter(mApplication, data);
		// 第一次加载默认从缓存中加载，默认标签为NULL
		QueryData(0, STATE_FIRST, tag);// 查询第一页数据，默认tag为null
		adapter.setOnClick(new HeadOnClick() {

			@Override
			public void OnClick(MyUser user, int position) {
				Intent mIntent = new Intent();
				String thisuser = user.getObjectId();
				String currentuser = BmobUser.getCurrentUser(mApplication,
						MyUser.class).getObjectId();
				if (thisuser.equals(currentuser)) {
					mIntent.putExtra("from", "me");
					Log.i("user", "success");
				} else {
					mIntent.putExtra("from", "add");

				}
				mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				mIntent.putExtra("username", user.getUsername());
				mIntent.setClass(getActivity(), SetMyInfoActivity.class);
				startActivity(mIntent);
			}
		});
		content_list.setAdapter(adapter);
		content_list.setItemsCanFocus(true);
		content_list.setFootViewVisible(false);
		// 设置点击跳转
		content_list.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Bundle mBundle = new Bundle();
				mBundle.putSerializable("one", data.get(position - 1));
				Intent mIntent = new Intent();
				mIntent.putExtras(mBundle);
				mIntent.setClass(getActivity(), DetailActivity.class);
				startActivity(mIntent);
			}
		});

		// 设置加载XListView的加载事件
		content_list.setXListViewListener(new IXListViewListener() {

			@Override
			public void onRefresh() {
				QueryData(0, STATE_REFRESH, tag);// 下拉刷新
			}

			@Override
			public void onLoadMore() {
				QueryData(curPage, STATE_MORE, tag);// 分页获取数据
			}
		});

		/*
		 * 初始化mDrawerLayout
		 */
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
				GravityCompat.START);
		mDrawerToggle = new ActionBarDrawerToggle(getActivity(), mDrawerLayout,
				R.drawable.ic_drawer, R.string.drawer_open,
				R.string.drawer_close) {
			public void onDrawerClosed(View view) {
				getActivity().invalidateOptionsMenu();
			}

			public void onDrawerOpened(View drawerView) {
				getActivity().invalidateOptionsMenu();
			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);
	}

	private void QueryData(final int page, final int actionType, Tags tag) {
		BmobQuery<Goods> query = new BmobQuery<Goods>();
		query.order("-createdAt");// 按照发表时间倒叙排列
		if (tag != null) {
			query.addWhereEqualTo("tag", tag.getObjectId());
		}
		query.addWhereEqualTo("IsFinish", false);
		query.include("user");
		query.setLimit(10);
		query.setSkip(page * 10);
		if (actionType == STATE_FIRST) {
			query.setCachePolicy(CachePolicy.CACHE_ELSE_NETWORK);
		} else {
			query.setCachePolicy(CachePolicy.NETWORK_ELSE_CACHE);
			query.setMaxCacheAge(TimeUnit.DAYS.toMillis(3));
		}
		query.findObjects(getActivity(), new FindListener<Goods>() {

			@Override
			public void onSuccess(List<Goods> list) {
				if (list.size() > 0) {
					if (actionType == STATE_REFRESH) {
						// 如果为刷新操作，清除数据源，重置页码为0
						data.clear();
						curPage = 0;
					}
					// 将获取的数据添加至数据源
					data.addAll(list);
					// 更新分页页码
					curPage++;
				} else {
					if (actionType == STATE_MORE) {
						Toast.makeText(getActivity(), "没有更多数据了",
								Toast.LENGTH_SHORT).show();
					}
					if (actionType == STATE_REFRESH) {
						Toast.makeText(getActivity(), "没有数据",
								Toast.LENGTH_SHORT).show();
					}
				}
				adapter.notifyDataSetChanged();// 数据源做更改
				onLoad();// ListView加载完成
			}

			@Override
			public void onError(int arg0, String arg1) {
				onLoad();
			}
		});
	}

	private void getDatas() {
		// 在此 获取 并封装数据
		Log.i("fenlei", mDatas.size() + "");
		BmobQuery<Tags> query = new BmobQuery<Tags>();
		query.findObjects(getActivity(), new FindListener<Tags>() {

			@Override
			public void onSuccess(List<Tags> list) {
				Log.i("fenlei", list.size() + "");
				mDatas.add(new TreeTag("lisao", "lisao", "全部商品", null));
				for (Tags tag : list) {
					mDatas.add(new TreeTag(tag.getObjectId(),
							tag.getParentID(), tag.getValue(), tag));
				}
				mAdapter.setDate(mDatas, 0);// 更新数据源
				Log.i("fenlei", mDatas.size() + "");
			}

			@Override
			public void onError(int arg0, String arg1) {
				Log.i("fenlei", arg1);
			}
		});
	}

	private void changeDataSource(Tags tags) {
		data.clear();// 先清空数据源
		QueryData(0, STATE_REFRESH, tags);
	}

	// 刷新完成
	private void onLoad() {
		content_list.stopRefresh();// 停止刷新
		content_list.stopLoadMore();// 停止加载
		Date date = new Date();
		SimpleDateFormat format = new SimpleDateFormat("MM月dd日HH:mm:ss");// 设置更新时间的格式
		content_list.setRefreshTime(format.format(date));// 设置更新时间
	}

}
