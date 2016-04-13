package com.lk.qf.pay.wedget;

import com.lk.bhb.pay.R;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * @author Ding
 */
public class CommonTitleBar extends LinearLayout implements OnClickListener {
	private String title;

	public CommonTitleBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		TypedArray mTypedArray = context.obtainStyledAttributes(attrs,
				R.styleable.TitleBar);
		title = mTypedArray.getString(R.styleable.TitleBar_titleContent);
		this.setBackgroundColor(getResources().getColor(R.color.transparent));
		initView();
	}

	public CommonTitleBar(Context context) {
		this(context,null);
	}

	private LinearLayout btn_back;
	private TextView tv_name;
	private TextView tv_more;
	private TextView tv_back_des;
	private String actName;
	private boolean canDestory = false;
	private Activity activity;
	View root;

	private void initView() {
		root = LayoutInflater.from(getContext()).inflate(
				R.layout.common_title_layout, null);
		btn_back = (LinearLayout) root.findViewById(R.id.common_title_back);
		btn_back.setOnClickListener(this);
		tv_name = (TextView) root.findViewById(R.id.common_title_name);
		tv_more = (TextView) root.findViewById(R.id.common_title_more);
		tv_more.setOnClickListener(this);
		tv_back_des = (TextView) root
				.findViewById(R.id.common_title_tv_btnback_des);
		if(!TextUtils.isEmpty(title)){
			tv_name.setText(title);
		}
		this.addView(root);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.common_title_back:
			if (canDestory) {
				if (activity != null) {
					try {
						activity.finish();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}

			// Toast.makeText(getContext(), "Click", 5000).show();
			break;
		case R.id.common_title_more:
			// Toast.makeText(getContext(), "Click1", 5000).show();
			break;
		}
	}

	public CommonTitleBar setCanClickDestory(Activity ctx, boolean enable) {
		this.activity = ctx;
		canDestory = enable;
		return this;
	}

	/**
	 * 隐藏标题栏
	 */
	@SuppressLint("NewApi")
	public void hideTitleBar() {
		ObjectAnimator ani = ObjectAnimator.ofFloat(root, "translationY", 0.0f,
				-root.getHeight());
		ani.addListener(new AnimatorListener() {

			@Override
			public void onAnimationStart(Animator animation) {
			}

			@Override
			public void onAnimationRepeat(Animator animation) {
			}

			@Override
			public void onAnimationEnd(Animator animation) {
				root.setVisibility(View.GONE);
			}

			@Override
			public void onAnimationCancel(Animator animation) {
			}
		});

		ani.setDuration(500).start();
		if (onTitleBarChange != null) {
			onTitleBarChange.onTitleBarHide();
		}
	}

	/**
	 * 显示标题栏
	 */
	@SuppressLint("NewApi")
	public void showTitleBar() {
		ObjectAnimator.ofFloat(root, "translationY", -root.getHeight(), 0.0f)
				.setDuration(500).start();
		root.setVisibility(View.VISIBLE);
		if (onTitleBarChange != null) {
			onTitleBarChange.onTitleBarShow();
		}
	}

	/**
	 * 切换标题栏 显示状态
	 */
	public void toggleTitleBar() {
		// this.setVisibility(this.getVisibility() == 0 ? 8 : 0);
		if (root.getVisibility() == View.VISIBLE) {
			hideTitleBar();
		} else {
			showTitleBar();
		}

	}

	public LinearLayout getBtn_back() {
		return btn_back;
	}

	public void setBtn_back(LinearLayout btn_back) {
		this.btn_back = btn_back;
	}

	public TextView getTv_name() {
		return tv_name;
	}

	public void setTv_name(TextView tv_name) {
		this.tv_name = tv_name;
	}

	public TextView getTv_more() {
		return tv_more;
	}

	public void setTvMoreName(String tv_more_name) {
		this.tv_more.setText(tv_more_name);
	}

	public TextView showTvMore() {
		this.tv_more.setVisibility(View.VISIBLE);
		return this.tv_more;
	}

	public void setTvMoreDrawable(Drawable drawable) {
		if (null == drawable) {
			this.tv_more.setCompoundDrawables(null, null, null, null);
		} else {

		}

	}

	public String getActName() {
		return actName;
	}

	/**
	 * 设置Activity标题
	 */
	public CommonTitleBar setActName(String actName) {
		this.actName = actName;
		this.tv_name.setText(actName);
		return this;
	}

	/**
	 * 设置返回按钮描述
	 */
	public void setBackButtonDescription(String str) {
		this.tv_back_des.setText(str);
	}

	public CommonTitleBar setDeawableRight(Drawable draw) {
		draw.setBounds(0, 0, draw.getMinimumWidth(), draw.getMinimumHeight());
		this.getTv_more().setCompoundDrawables(null, null, draw, null);
		return this;
	}

	/**
	 * 标题栏显示状态改变监听
	 */
	public void setOnTitleBarVisiableChangeListener(
			OnTitleBarChangeListener onchangeListener) {
		this.onTitleBarChange = onchangeListener;
	}

	/**
	 * 标题栏显示状态改变监听
	 */
	public interface OnTitleBarChangeListener {
		void onTitleBarHide();

		void onTitleBarShow();
	}

	public void hideTitleBarMoreBtn() {
		this.tv_more.setVisibility(GONE);
	}

	private OnTitleBarChangeListener onTitleBarChange;

}
