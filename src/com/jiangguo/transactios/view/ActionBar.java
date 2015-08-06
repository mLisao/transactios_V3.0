package com.jiangguo.transactios.view;


import com.jiangguo.transactios.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ActionBar extends LinearLayout {

	protected ImageButton LeftButton;
	protected ImageButton RightButton;
	protected TextView CenterText;
	protected View bar;
	protected onLeftButtonClickListener mLeftButtonClickListener;
	protected onRightButtonClickListener mRightButtonClickListener;

	public ActionBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater.from(context).inflate(R.layout.include_headerbar, this);
		LeftButton = (ImageButton) findViewById(R.id.actionbat_left_button);
		RightButton = (ImageButton) findViewById(R.id.actionbat_right_button);
		CenterText = (TextView) findViewById(R.id.actionbar_text);
	}

	// 设置标题
	public void setOnlyTitle(String title) {
		CenterText.setText(title);
		LeftButton.setVisibility(View.GONE);
		RightButton.setVisibility(View.GONE);
	}

	// 设置左边按钮和标题
	public void setTitleAndLeftButton(int res, String title) {
		LeftButton.setImageResource(res);
		LeftButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mLeftButtonClickListener != null) {
					mLeftButtonClickListener.onClick();
				}
			}
		});
		CenterText.setText(title);
	}

	// 设置右边按钮和标题
	public void setTitleAndRightButton(int res, String title) {
		RightButton.setImageResource(res);
		RightButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mRightButtonClickListener != null) {
					mRightButtonClickListener.onClick();
				}
			}
		});
		CenterText.setText(title);
	}

	// 设置左边按钮、标题、右边按钮
	public void SetTitleAndRightAndLeftButton(int lres, int rres, String title) {
		LeftButton.setImageResource(lres);
		LeftButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mLeftButtonClickListener != null) {
					mLeftButtonClickListener.onClick();
				}
			}
		});
		RightButton.setImageResource(rres);
		RightButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mRightButtonClickListener != null) {
					mRightButtonClickListener.onClick();
				}
			}
		});
		CenterText.setText(title);
	}

	public interface onLeftButtonClickListener {
		void onClick();
	}

	public interface onRightButtonClickListener {
		void onClick();
	}

	public void setOnLeftButtonClickListener(
			onLeftButtonClickListener mLeftButtonClickListener) {
		this.mLeftButtonClickListener = mLeftButtonClickListener;
	}

	public void setOnRightButtonClickListener(
			onRightButtonClickListener mRightButtonClickListener) {
		this.mRightButtonClickListener = mRightButtonClickListener;
	}

}
