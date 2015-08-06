package com.jiangguo.transactios;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

public class ShowActivity extends Activity {

	private ViewPager mViewPager;
	ZoomImageView[] mImageViews = null;

	private String[] mUrl = null;

	private TextView cur;
	private TextView sum;
	private int pos = -1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.vp);

		Bundle mBundle = this.getIntent().getExtras();
		mUrl = mBundle.getStringArray("url");
		pos = mBundle.getInt("pos");
		cur = (TextView) this.findViewById(R.id.pic_cur);
		sum = (TextView) this.findViewById(R.id.pic_sum);
		mViewPager = (ViewPager) findViewById(R.id.id_viewpager);

		if (mUrl.length == 0) {
			Toast.makeText(getApplicationContext(), "mUrl000_未知参数错误",
					Toast.LENGTH_SHORT).show();
			ShowActivity.this.finish();
		}
		sum.setText(String.valueOf(mUrl.length));// 设置总总的界面数
		cur.setText("1");// 默认显示界面为第一个
		mImageViews = new ZoomImageView[mUrl.length];
		mViewPager.setAdapter(new PagerAdapter() {

			@Override
			public Object instantiateItem(ViewGroup container, int position) {

				ZoomImageView imageView = new ZoomImageView(
						getApplicationContext());
				imageView.setImageUrl(mUrl[position]);
				container.addView(imageView);
				mImageViews[position] = imageView;
				return imageView;
			}

			@Override
			public void destroyItem(ViewGroup container, int position,
					Object object) {
				container.removeView(mImageViews[position]);

			}

			@Override
			public boolean isViewFromObject(View arg0, Object arg1) {
				return arg0 == arg1;
			}

			@Override
			public int getCount() {
				return mUrl.length;
			}
		});
		mViewPager.setOnPageChangeListener(new ViewChange());
		if (pos >= 0 && pos <= mUrl.length) {
			mViewPager.setCurrentItem(pos);
		}

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			ShowActivity.this.finish();
			return true;
		} else {
			return super.onKeyDown(keyCode, event);
		}
	}

	/**
	 * 监听ViewPager的滑动
	 * 
	 * @author lisao
	 * 
	 */
	class ViewChange implements OnPageChangeListener {

		@Override
		public void onPageScrollStateChanged(int arg0) {

		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {

		}

		@Override
		public void onPageSelected(int arg0) {
			// 当界面被选中是更新界面中的页面标号
			cur.setText(String.valueOf(arg0 + 1));
		}
	}
}
