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
	private XListView content_list;// ����ListView���� �ұߵ�����
	private ListViewAdapter adapter;// �����Զ���������
	private ActionBar bar;// �Զ���ͷ��
	private ListView mCassify = null; // ��߷���
	private TreeViewAdapter<TreeTag> mAdapter = null; // ���� Adapter
	private List<TreeTag> mDatas = null; // ��������
	private int curPage = 0;// ��ǰҳ��

	private static final int STATE_REFRESH = 0;// ����ˢ��
	private static final int STATE_MORE = 1;// ���ظ���
	private static final int STATE_FIRST = -1;// ��һ�μ���
	List<Goods> data = new ArrayList<Goods>();// ����Դ

	private ActionBarDrawerToggle mDrawerToggle = null;
	private Tags tag = null;// ������ķ���

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
		// ����ͷ������
		bar = (ActionBar) findViewById(R.id.headbar);
		bar.SetTitleAndRightAndLeftButton(R.drawable.menu, R.drawable.search,
				"������Ʒ");
		bar.setOnLeftButtonClickListener(new onLeftButtonClickListener() {
			@Override
			public void onClick() {
				mDrawerLayout.openDrawer(mCassify);
			}
		});
		bar.setOnRightButtonClickListener(new onRightButtonClickListener() {
			@Override
			public void onClick() {
				ShowToast("�����������");
			}
		});

		// �����ListView
		mCassify = (ListView) getActivity().findViewById(R.id.classify_menu);
		mDatas = new ArrayList<TreeTag>();
		getDatas();// ��ȡ��������
		mAdapter = new TreeViewAdapter<TreeTag>(mCassify, getActivity()
				.getApplicationContext(), mDatas, 0);
		mCassify.setAdapter(mAdapter);
		mAdapter.setOnTreeNodeClickListener(new OnTreeNodeClickListener() {

			@Override
			public void onClick(Node node, int position) {
				if (node.isLeaf()) {// �����Ҷ�ӽڵ������Ӧ����
					tag = node.getTags();
					changeDataSource(tag);// ��������Դ
					mDrawerLayout.closeDrawer(mCassify);
				}
			}

		});

		// ��Ҫ��Ʒչʾ
		content_list = (XListView) getActivity()
				.findViewById(R.id.content_list);
		content_list.setPullLoadEnable(true);// ������ײ�����Ϊ�ɵ��
		adapter = new ListViewAdapter(mApplication, data);
		// ��һ�μ���Ĭ�ϴӻ����м��أ�Ĭ�ϱ�ǩΪNULL
		QueryData(0, STATE_FIRST, tag);// ��ѯ��һҳ���ݣ�Ĭ��tagΪnull
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
		// ���õ����ת
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

		// ���ü���XListView�ļ����¼�
		content_list.setXListViewListener(new IXListViewListener() {

			@Override
			public void onRefresh() {
				QueryData(0, STATE_REFRESH, tag);// ����ˢ��
			}

			@Override
			public void onLoadMore() {
				QueryData(curPage, STATE_MORE, tag);// ��ҳ��ȡ����
			}
		});

		/*
		 * ��ʼ��mDrawerLayout
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
		query.order("-createdAt");// ���շ���ʱ�䵹������
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
						// ���Ϊˢ�²������������Դ������ҳ��Ϊ0
						data.clear();
						curPage = 0;
					}
					// ����ȡ���������������Դ
					data.addAll(list);
					// ���·�ҳҳ��
					curPage++;
				} else {
					if (actionType == STATE_MORE) {
						Toast.makeText(getActivity(), "û�и���������",
								Toast.LENGTH_SHORT).show();
					}
					if (actionType == STATE_REFRESH) {
						Toast.makeText(getActivity(), "û������",
								Toast.LENGTH_SHORT).show();
					}
				}
				adapter.notifyDataSetChanged();// ����Դ������
				onLoad();// ListView�������
			}

			@Override
			public void onError(int arg0, String arg1) {
				onLoad();
			}
		});
	}

	private void getDatas() {
		// �ڴ� ��ȡ ����װ����
		Log.i("fenlei", mDatas.size() + "");
		BmobQuery<Tags> query = new BmobQuery<Tags>();
		query.findObjects(getActivity(), new FindListener<Tags>() {

			@Override
			public void onSuccess(List<Tags> list) {
				Log.i("fenlei", list.size() + "");
				mDatas.add(new TreeTag("lisao", "lisao", "ȫ����Ʒ", null));
				for (Tags tag : list) {
					mDatas.add(new TreeTag(tag.getObjectId(),
							tag.getParentID(), tag.getValue(), tag));
				}
				mAdapter.setDate(mDatas, 0);// ��������Դ
				Log.i("fenlei", mDatas.size() + "");
			}

			@Override
			public void onError(int arg0, String arg1) {
				Log.i("fenlei", arg1);
			}
		});
	}

	private void changeDataSource(Tags tags) {
		data.clear();// ���������Դ
		QueryData(0, STATE_REFRESH, tags);
	}

	// ˢ�����
	private void onLoad() {
		content_list.stopRefresh();// ֹͣˢ��
		content_list.stopLoadMore();// ֹͣ����
		Date date = new Date();
		SimpleDateFormat format = new SimpleDateFormat("MM��dd��HH:mm:ss");// ���ø���ʱ��ĸ�ʽ
		content_list.setRefreshTime(format.format(date));// ���ø���ʱ��
	}

}
