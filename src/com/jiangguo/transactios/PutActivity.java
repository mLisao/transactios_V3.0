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

	private static final int PHOTO_ZOOM = 2; // ������ȡ
	private static final int PHOTO_RESOULT = 3;// ���
	private ActionBar bar;
	// ����������
	private Spinner mSpinner;
	private SpinnerAdapter sp_adapter;
	@SuppressWarnings("unused")
	private TreeViewAdapter<TreeTag> mTreeViewAdapter;
	private List<Tags> tag;
	@SuppressWarnings("unused")
	private List<TreeTag> list;

	private EditText title_text;// ��Ʒ�ı���
	private EditText price_text;// ��Ʒ�ļ۸�
	private EditText context;// ��Ʒ������

	private Goods goods = null;// ��Ʒ
	private MyUser user = null;// �����û�

	// ��Ӱ�ť ͼ��
	final int add = R.drawable.camera;

	// ��������ͼƬ��Դ
	private List<Bitmap> data = null;
	private GridViewAdapter adapter = null;

	// ������ť
	private Button put = null;
	// GridView
	private GridView gridView = null;

	// �洢���ջ�õ�ͼƬ·����Դ������
	private String[] mUrl = null;
	private List<String> paths = null;

	// ��¼�û�����Item position��ȫ�ֱ���
	private int mPosition = 0;// position==mPosotion

	private ProgressDialog dialog;// �ϴ�ʱ��ʾ�Ľ�����

	private File photoFile = null;// ��Ƭ����·��

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

	// ��ʼ����ͼ ȡ�ø������
	private void initView() {

		// initTopBarForLeft("������Ʒ");
		bar = (ActionBar) findViewById(R.id.headbar);
		bar.setTitleAndLeftButton(R.drawable.back, "����");
		bar.setOnLeftButtonClickListener(new onLeftButtonClickListener() {

			@Override
			public void onClick() {
				PutActivity.this.finish();
			}
		});
		// ��ʼ�����������
		mSpinner = (Spinner) findViewById(R.id.tag_spinner);
		tag = new ArrayList<Tags>();
		tag = getTags();
		tag.add(0, new Tags("��ѡ�����"));
		sp_adapter = new SpinnerAdapter(mApplication, tag);
		// ѡ��ͼƬ��GridView
		gridView = (GridView) findViewById(R.id.gridView1);
		// ������ť
		put = (Button) findViewById(R.id.put_button);

		title_text = (EditText) findViewById(R.id.title_text);
		price_text = (EditText) findViewById(R.id.price_text);
		context = (EditText) findViewById(R.id.context);

		// ΪGridView�������Դ
		data = new ArrayList<Bitmap>();
		// ��ó�����Դ��������Դ�е�drawable��ͼƬ��Դת��ΪBitmap������ӽ�����Դ��
		Bitmap bmp = BitmapFactory.decodeResource(getResources(),
				R.drawable.camera);
		data.add(bmp);
		// ��������Դ��
		adapter = new GridViewAdapter(getApplicationContext(), data);
		paths = new ArrayList<String>();
	}

	// ��ʼ���¼�
	private void initEvent() {
		user = BmobUser.getCurrentUser(getApplicationContext(), MyUser.class);// ��ȡ��ǰ�����û�

		gridView.setAdapter(adapter);
		gridView.setOnItemClickListener(this);

		mSpinner.setAdapter(sp_adapter);
		/*
		 * ������ť ����¼� ·����Դ��mUrl���� ͼƬ��Ϊ imageItem.size()
		 */
		put.setOnClickListener(this);
	}

	// GridView����¼�
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		mPosition = position;// ��¼�û����λ��
		if (position == data.size() - 1) {// ͼƬ������ʱ
			if (data.size() == 6) {
				Toast.makeText(getApplicationContext(), "ֻ�������������ͼƬ",
						Toast.LENGTH_SHORT).show();
			} else {
				// ������ͷ�������ԭͼ
				getPhoteFromCamera();
				// ����ȡͼƬ
				// Intent intent = new Intent(Intent.ACTION_PICK, null);
				// intent.setDataAndType(
				// MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
				// IMAGE_UNSPECIFIED);
				// startActivityForResult(intent, PHOTO_ZOOM);
			}
		} else {// ���ɾ��
			delete(position);
		}
	}

	/**
	 * ���� ������ͷ����ȡԭͼ
	 */
	private void getPhoteFromCamera() {
		photoFile = new File(BmobConstants.BMOB_PICTURE_PATH
				+ System.currentTimeMillis() + ".jpg");// ͼƬ����·��
		if (!photoFile.getParentFile().exists()) {// ���ļ����������½��ļ�
			photoFile.getParentFile().mkdirs();
		}
		// �������
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);// �������
		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));// �������·����ɻ��ԭͼ
		startActivityForResult(intent, PHOTO_RESOULT);
	}

	/*
	 * ɾ������ position Ϊ�û����µ�λ��
	 */
	private void delete(final int position) {
		AlertDialog.Builder builder = new Builder(PutActivity.this);
		builder.setMessage("ȷ���Ƴ������ͼƬ��");
		builder.setTitle("��ʾ");
		builder.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				data.remove(position);// ����Դ�Ƴ�
				paths.remove(position);// �����浽��ַ�Ƴ�
				adapter.notifyDataSetChanged();// ������ͼ
			}
		});
		builder.setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {
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
		if (requestCode == PHOTO_RESOULT && resultCode == RESULT_OK) {// ���������
			Bitmap bmp = FileUtil.compressBySize(photoFile.getPath(), 480, 800);// ����·������λͼ
			if (bmp == null) {
				ShowToast("����ʧ�ܣ�����һ��");
				return;
			}
			paths.add(photoFile.getPath());// ��ͼƬURL������б�
			this.data.add(mPosition, bmp);// λͼ��ӽ�GridView����Դ
			adapter.notifyDataSetChanged();// ������ͼ

		}
		// ��ͼƬ
		if (resultCode == RESULT_OK && requestCode == PHOTO_ZOOM) {
			Uri uri = data.getData();
			if (!TextUtils.isEmpty(uri.getAuthority())) {
				// ��ѯѡ��ͼƬ
				Cursor cursor = getContentResolver().query(uri,
						new String[] { MediaStore.Images.Media.DATA }, null,
						null, null);
				// ���� û�ҵ�ѡ��ͼƬ
				if (null == cursor) {
					return;
				}
				// ����ƶ�����ͷ ��ȡͼƬ·��
				cursor.moveToFirst();
				paths.add(cursor.getString(cursor
						.getColumnIndex(MediaStore.Images.Media.DATA)));
				// ��ʾ ·��
				Toast.makeText(getApplicationContext(),
						"ͼƬ·����" + paths.get(mPosition), Toast.LENGTH_SHORT)
						.show();

				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inJustDecodeBounds = false;
				options.inSampleSize = 10;// �������Եı���
				// ʵ���� ��ö���
				Bitmap addbmp = BitmapFactory.decodeFile(paths.get(mPosition),
						options);
				this.data.add(mPosition, addbmp);

				adapter.notifyDataSetChanged();// ������ͼ
			}
		}

	}

	// ������ť����¼�
	@Override
	public void onClick(View v) {
		if (TextUtils.isEmpty(title_text.getText())) {
			Toast.makeText(getApplicationContext(), "����д����", Toast.LENGTH_SHORT)
					.show();
			return;
		}
		if (TextUtils.isEmpty(price_text.getText())) {
			Toast.makeText(getApplicationContext(), "����д�۸�", Toast.LENGTH_SHORT)
					.show();
			return;
		}
		if (TextUtils.isEmpty(context.getText())) {
			Toast.makeText(getApplicationContext(), "����д��Ʒ������",
					Toast.LENGTH_SHORT).show();
			return;
		}
		Tags tag = (Tags) mSpinner.getSelectedItem();
		if (tag.getObjectId() == null) {
			Toast.makeText(getApplicationContext(), "��ѡ�����", Toast.LENGTH_SHORT)
					.show();
			return;
		}
		if (paths.size() == 0) {
			Toast.makeText(getApplicationContext(), "���������һ����Ƭ",
					Toast.LENGTH_SHORT).show();
			return;
		}
		final int size = paths.size();// ��ô���ͼƬ��·����С
		mUrl = (String[]) paths.toArray(new String[size]);// Listת��Ϊ�ַ�������
		dialog = new ProgressDialog(PutActivity.this);
		dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		dialog.setTitle("ͼƬ�ϴ���...");
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
							AddData(Arrays.asList(urls));// ������Ʒ�������ϴ����ͼƬ��ַ
						}

					}

					@Override
					public void onProgress(int curIndex, int curPercent,
							int total, int totalPercent) {
						// 1��curIndex--��ʾ��ǰ�ڼ����ļ������ϴ�
						// 2��curPercent--��ʾ��ǰ�ϴ��ļ��Ľ���ֵ���ٷֱȣ�
						// 3��total--��ʾ�ܵ��ϴ��ļ���
						// 4��totalPercent--��ʾ�ܵ��ϴ����ȣ��ٷֱȣ�
						dialog.setProgress(curIndex);

					}
				});
	}

	// �������
	private void AddData(List<String> list) {
		// ��鵱ǰ�û������Ƿ�Ϊ�գ���Ϊ���򷵻����������û�����
		// ����Ϊ�գ��ٷ�����Ʒ
		if (TextUtils.isEmpty(user.getObjectId())) {
			Toast.makeText(getApplicationContext(), "��ǰ�û������objectΪ��",
					Toast.LENGTH_LONG).show();
			return;
		}
		goods = new Goods();
		goods.setUser(user);// ���õ�ǰ�û�
		goods.setTitle(title_text.getText().toString());// ������Ʒ����
		goods.setContent(context.getText().toString());// ��Ʒ����
		goods.setPrice(Double.parseDouble(price_text.getText().toString()));// ��Ʒ�۸�
		goods.setTag((Tags) mSpinner.getSelectedItem());
		goods.setPraise(0);
		if (list != null) {
			goods.setPicture(list);// �����ݹ�����list����
		}
		goods.save(getApplicationContext(), new SaveListener() {

			@Override
			public void onSuccess() {
				AddDateToUserToTag();
			}

			@Override
			public void onFailure(int arg0, String arg1) {
				Toast.makeText(getApplicationContext(), "�������" + arg0,
						Toast.LENGTH_LONG).show();
			}
		});
	}

	// ��ӹ�����ϵ
	private void AddDateToUserToTag() {

		BmobRelation RelationGoods = new BmobRelation();
		RelationGoods.add(goods);
		user.setGoods(RelationGoods);
		user.update(getApplicationContext(), new UpdateListener() {

			@Override
			public void onSuccess() {
				Toast.makeText(getApplicationContext(), "��Ʒ�������",
						Toast.LENGTH_LONG).show();
				PutActivity.this.finish();// ������ɺ� ������ǰActivity���ص���һ��Activity

			}

			@Override
			public void onFailure(int arg0, String arg1) {
				Toast.makeText(getApplicationContext(), "�������" + arg0,
						Toast.LENGTH_LONG).show();

			}
		});
	}

	// ��ȡ�����б�
	/**
	 * 
	 * @param depth���
	 * @param parent����ID
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
