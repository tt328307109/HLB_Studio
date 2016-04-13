package com.lk.qf.pay.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

public class ExpandableTextViewUtils {

	ValueAnimator mAnimator;

	public void expand(View view) {
		view.setVisibility(View.VISIBLE);
		/**
		 * 设置高度
		 */
//		final int widthSpec = View.MeasureSpec.makeMeasureSpec(0,
//				View.MeasureSpec.UNSPECIFIED);
//		final int heightSpec = View.MeasureSpec.makeMeasureSpec(0,
//				View.MeasureSpec.UNSPECIFIED);
//		Log.i("result", "-----------------heightSpec---------"+heightSpec);
//		view.measure(widthSpec, heightSpec); // 这个很重要
//
//		mAnimator = slideAnimator(view, 0, view.getMeasuredHeight());
//		mAnimator.start();
	}

	public void collapse(View view) {
		view.setVisibility(View.GONE);
//		final View mView = view;
//		int finalHeight = view.getHeight();
//		
//		ValueAnimator mAnimator = slideAnimator(view, finalHeight, 0);
//		mAnimator.addListener(new AnimatorListenerAdapter() {
//			@Override
//			public void onAnimationEnd(Animator animator) {
//				// Height=0, but it set visibility to GONE
//				mView.setVisibility(View.GONE);
//			}
//		});
//		mAnimator.start();
	}

	/**
	 * 插值器
	 * 
	 * @param start
	 * @param end
	 * @return
	 */
	private ValueAnimator slideAnimator(View view, int start, int end) {
		final View mView = view;
		ValueAnimator animator = ValueAnimator.ofInt(start, end);
		animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator valueAnimator) {
				// Update Height
				int value = (Integer) valueAnimator.getAnimatedValue();

				ViewGroup.LayoutParams layoutParams = mView.getLayoutParams();
				layoutParams.height = value;
				mView.setLayoutParams(layoutParams);
			}
		});
		return animator;
	}
}
