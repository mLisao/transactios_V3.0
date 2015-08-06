package com.jiangguo.transactios;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

import com.bmob.BmobProFile;
import com.jiangguo.adapter.GridViewAdapter;
import com.jiangguo.adapter.SpinnerAdapter;
import com.jiangguo.adapter.TreeViewAdapter;
import com.jiangguo.bean.Goods;
import com.jiangguo.bean.MyUser;
import com.jiangguo.bean.Tags;
import com.jiangguo.bean.TreeTag;
import com.jiangguo.file.FileUtil;
import com.jiangguo.transactios.config.BmobConstants;
import com.jiangguo.transactios.view.ActionBar;
import com.jiangguo.transactios.view.ActionBar.onLeftButtonClickListener;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Spinner;
import android.widget.Toast;

public class PutActivity extends ActivityBase implements OnItemClickListener,
		OnClickListener {

	private static final int PHOTO_ZOOM = 2; // 从相册读取
	private static final int PHOTO_RESOULT = 3;// 结果
	private ActionBar bar;
	// 两个下拉框
	private Spinner mSpinner;
	private SpinnerAdapter sp_adapter;
	@SuppressWarnings("unused")
	private TreeViewAdapter<TreeTag> mTreeViewAdapter;
	private List<Tags> tag;
	@SuppressWarnings("unused")
	private List<TreeTag> list;

	private EditText title_text;// 物品的标题
	private EditText price_text;// 物品的价格
	private EditText context;// 物品的描述

	private Goods goods = null;// 物品
	private MyUser user = null;// 缓存用户

	// 添加按钮 图标
	final int add = R.drawable.camera;

	// 适配器的图片资源
	private List<Bitmap> data = null;
	private GridViewAdapter adapter = null;

	// 发布按钮
	private Button put = null;
	// GridView
	private GridView gridView = null;

	// 存储拍照获得的图片路径资源的数组
	private String[] mUrl = null;
	private List<String> paths = null;

	// 记录用户摁下Item position的全局变量
	private int mPosition = 0;// position==mPosotion

	private ProgressDialog dialog;// 上传时显示的进度条

	private File photoFile = null;// 照片保存路径

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN
						| WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		setContentView(R.layout.activity_put);
		initView();
		initEvent();
	}

	// 初始化视图 取得各个组件
	private void initView() {

		// initTopBarForLeft("发布商品");
		bar = (ActionBar) findViewById(R.id.headbar);
		bar.setTitleAndLeftButton(R.drawable.back, "发布");
		bar.setOnLeftButtonClickListener(new onLeftButtonClickListener() {

			@Override
			public void onClick() {
				PutActivity.this.finish();
			}
		});
		// 初始化下拉框相关
		mSpinner = (Spinner) findViewById(R.id.tag_spinner);
		tag = new ArrayList<Tags>();
		tag = getTags();
		tag.add(0, new Tags("请选择分类"));
		sp_adapter = new SpinnerAdapter(mApplication, tag);
		// 选择图片的GridView
		gridView = (GridView) findViewById(R.id.gridView1);
		// 发布按钮
		put = (Button) findViewById(R.id.put_button);

		title_text = (EditText) findViewById(R.id.title_text);
		price_text = (EditText) findViewById(R.id.price_text);
		context = (EditText) findViewById(R.id.context);

		// 为GridView添加数据源
		data = new ArrayList<Bitmap>();
		// 获得程序资源，并将资源中的drawable的图片资源转换为Bitmap类型添加进数据源中
		Bitmap bmp = BitmapFactory.decodeResource(getResources(),
				R.drawable.camera);
		data.add(bmp);
		// 设置数据源绑定
		adapter = new GridViewAdapter(getApplicationContext(), data);
		paths = new ArrayList<String>();
	}

	// 初始化事件
	private void initEvent() {
		user = BmobUser.getCurrentUser(getApplicationContext(), MyUser.class);// 获取当前缓存用户

		gridView.setAdapter(adapter);
		gridView.setOnItemClickListener(this);

		mSpinner.setAdapter(sp_adapter);
		/*
		 * 发布按钮 点击事件 路径资源在mUrl数组 图片数为 imageItem.size()
		 */
		put.setOnClickListener(this);
	}

	// GridView点击事件
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		mPosition = position;// 记录用户点的位置
		if (position == data.size() - 1) {// 图片满五张时
			if (data.size() == 6) {
				Toast.makeText(getApplicationContext(), "只能添加五张以内图片",
						Toast.LENGTH_SHORT).show();
			} else {
				// 打开摄像头，并获得原图
				getPhoteFromCamera();
				// 相册获取图片
				// Intent intent = new Intent(Intent.ACTION_PICK, null);
				// intent.setDataAndType(
				// MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
				// IMAGE_UNSPECIFIED);
				// startActivityForResult(intent, PHOTO_ZOOM);
			}
		} else {// 点击删除
			delete(position);
		}
	}

	/**
	 * 拍照 打开摄像头并获取原图
	 */
	private void getPhoteFromCamera() {
		photoFile = new File(BmobConstants.BMOB_PICTURE_PATH
				+ System.currentTimeMillis() + ".jpg");// 图片储存路径
		if (!photoFile.getParentFile().exists()) {// 若文件不存在则新建文件
			photoFile.getParentFile().mkdirs();
		}
		// 启动相机
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);// 启动相机
		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));// 设置输出路径则可获得原图
		startActivityForResult(intent, PHOTO_RESOULT);
	}

	/*
	 * 删除方法 position 为用户摁下的位置
	 */
	private void delete(final int position) {
		AlertDialog.Builder builder = new Builder(PutActivity.this);
		builder.setMessage("确认移除已添加图片吗？");
		builder.setTitle("提示");
		builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				data.remove(position);// 数据源移除
				paths.remove(position);// 将保存到地址移除
				adapter.notifyDataSetChanged();// 更新视图
			}
		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		builder.create().show();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == PHOTO_RESOULT && resultCode == RESULT_OK) {// 照相机返回
			Bitmap bmp = FileUtil.compressBySize(photoFile.getPath(), 480, 800);// 根据路径返回位图
			if (bmp == null) {
				ShowToast("拍照失败，再试一次");
				return;
			}
			paths.add(photoFile.getPath());// 将图片URL添加至列表
			this.data.add(mPosition, bmp);// 位图添加进GridView数据源
			adapter.notifyDataSetChanged();// 更新视图

		}
		// 打开图片
		if (resultCode == RESULT_OK && requestCode == PHOTO_ZOOM) {
			Uri uri = data.getData();
			if (!TextUtils.isEmpty(uri.getAuthority())) {
				// 查询选择图片
				Cursor cursor = getContentResolver().query(uri,
						new String[] { MediaStore.Images.Media.DATA }, null,
						null, null);
				// 返回 没找到选择图片
				if (null == cursor) {
					return;
				}
				// 光标移动至开头 获取图片路径
				cursor.moveToFirst();
				paths.add(cursor.getString(cursor
						.getColumnIndex(MediaStore.Images.Media.DATA)));
				// 显示 路径
				Toast.makeText(getApplicationContext(),
						"图片路径：" + paths.get(mPosition), Toast.LENGTH_SHORT)
						.show();

				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inJustDecodeBounds = false;
				options.inSampleSize = 10;// 设置缩略的倍数
				// 实例化 获得对象
				Bitmap addbmp = BitmapFactory.decodeFile(paths.get(mPosition),
						options);
				this.data.add(mPosition, addbmp);

				adapter.notifyDataSetChanged();// 更新视图
			}
		}

	}

	// 发布按钮点击事件
	@Override
	public void onClick(View v) {
		if (TextUtils.isEmpty(title_text.getText())) {
			Toast.makeText(getApplicationContext(), "请填写标题", Toast.LENGTH_SHORT)
					.show();
			return;
		}
		if (TextUtils.isEmpty(price_text.getText())) {
			Toast.makeText(getApplicationContext(), "请填写价格", Toast.LENGTH_SHORT)
					.show();
			return;
		}
		if (TextUtils.isEmpty(context.getText())) {
			Toast.makeText(getApplicationContext(), "请填写物品的描述",
					Toast.LENGTH_SHORT).show();
			return;
		}
		Tags tag = (Tags) mSpinner.getSelectedItem();
		if (tag.getObjectId() == null) {
			Toast.makeText(getApplicationContext(), "请选择分类", Toast.LENGTH_SHORT)
					.show();
			return;
		}
		if (paths.size() == 0) {
			Toast.makeText(getApplicationContext(), "请至少添加一张照片",
					Toast.LENGTH_SHORT).show();
			return;
		}
		final int size = paths.size();// 获得储存图片的路径大小
		mUrl = (String[]) paths.toArray(new String[size]);// List转化为字符串数组
		dialog = new ProgressDialog(PutActivity.this);
		dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		dialog.setTitle("图片上传中...");
		dialog.setIndeterminate(false);
		dialog.setCancelable(false);
		dialog.setCanceledOnTouchOutside(false);
		dialog.setMax(paths.size());
		dialog.show();
		BmobProFile.getInstance(PutActivity.this).uploadBatch(mUrl,
				new com.bmob.btp.callback.UploadBatchListener() {

					@Override
					public void onError(int statuscode, String errormsg) {
						dialog.dismiss();
						// Toast.makeText(getApplicationContext(), errormsg,
						// Toast.LENGTH_SHORT).show();
					}

					@Override
					public void onSuccess(boolean isFinish, String[] fileNames,
							String[] urls) {
						if (isFinish) {
							dialog.dismiss();
							for (int i = 0; i < urls.length; i++) {
								urls[i] += "?t=2&a=eaa1c13bee00901d971b461a439b3c35";
							}
							AddData(Arrays.asList(urls));// 发表物品，传入上传后的图片地址
						}

					}

					@Override
					public void onProgress(int curIndex, int curPercent,
							int total, int totalPercent) {
						// 1、curIndex--表示当前第几个文件正在上传
						// 2、curPercent--表示当前上传文件的进度值（百分比）
						// 3、total--表示总的上传文件数
						// 4、totalPercent--表示总的上传进度（百分比）
						dialog.setProgress(curIndex);

					}
				});
	}

	// 添加数据
	private void AddData(List<String> list) {
		// 检查当前用户对象是否为空，若为空则返回重新设置用户对象
		// 若不为空，再发表物品
		if (TextUtils.isEmpty(user.getObjectId())) {
			Toast.makeText(getApplicationContext(), "当前用户对象的object为空",
					Toast.LENGTH_LONG).show();
			return;
		}
		goods = new Goods();
		goods.setUser(user);// 设置当前用户
		goods.setTitle(title_text.getText().toString());// 设置物品标题
		goods.setContent(context.getText().toString());// 物品描述
		goods.setPrice(Double.parseDouble(price_text.getText().toString()));// 物品价格
		goods.setTag((Tags) mSpinner.getSelectedItem());
		goods.setPraise(0);
		if (list != null) {
			goods.setPicture(list);// 将传递过来的list加入
		}
		goods.save(getApplicationContext(), new SaveListener() {

			@Override
			public void onSuccess() {
				AddDateToUserToTag();
			}

			@Override
			public void onFailure(int arg0, String arg1) {
				Toast.makeText(getApplicationContext(), "错误代码" + arg0,
						Toast.LENGTH_LONG).show();
			}
		});
	}

	// 添加关联关系
	private void AddDateToUserToTag() {

		BmobRelation RelationGoods = new BmobRelation();
		RelationGoods.add(goods);
		user.setGoods(RelationGoods);
		user.update(getApplicationContext(), new UpdateListener() {

			@Override
			public void onSuccess() {
				Toast.makeText(getApplicationContext(), "物品发表完成",
						Toast.LENGTH_LONG).show();
				PutActivity.this.finish();// 发表完成后， 结束当前Activity，回到上一个Activity

			}

			@Override
			public void onFailure(int arg0, String arg1) {
				Toast.makeText(getApplicationContext(), "错误代码" + arg0,
						Toast.LENGTH_LONG).show();

			}
		});
	}

	// 获取分类列表
	/**
	 * 
	 * @param depth深度
	 * @param parent父类ID
	 * @return
	 */
	private List<Tags> getTags() {
		final List<Tags> Tags = new ArrayList<Tags>();
		BmobQuery<Tags> query = new BmobQuery<Tags>();
		query.setLimit(1000);
		query.findObjects(this.getApplicationContext(),
				new FindListener<Tags>() {

					@Override
					public void onSuccess(List<Tags> list) {
						Tags.addAll(list);
					}

					@Override
					public void onError(int arg0, String arg1) {
					}
				});
		return Tags;
	}

	@Override
	public void finish() {
		super.finish();
	}
}
