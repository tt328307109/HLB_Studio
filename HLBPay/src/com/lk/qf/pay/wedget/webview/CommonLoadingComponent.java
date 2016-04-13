package com.lk.qf.pay.wedget.webview;

import com.lk.bhb.pay.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;


public class CommonLoadingComponent extends LinearLayout {

	private LinearLayout panel_loading;
	private LinearLayout panel_loaderror;
	private TextView tv_error_msg, tv_loading_tooltip;
	private View view;
	private boolean error_panel_init=false;

	public CommonLoadingComponent(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public CommonLoadingComponent(Context context) {
		super(context);
		init();
	}

	private void init() {
		view = LayoutInflater.from(getContext()).inflate(
				R.layout.common_loding_component, null);
		panel_loaderror = (LinearLayout) view
				.findViewById(R.id.panel_load_error);
		panel_loaderror.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				onScreenTouch.onTouch();				
			}
		});
		panel_loading = (LinearLayout) view.findViewById(R.id.panel_isloading);
		tv_error_msg = (TextView) view
				.findViewById(R.id.common_loading_error_msg);
		tv_loading_tooltip = (TextView) view
				.findViewById(R.id.common_loading_tooltip);
		LayoutParams params=new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		addView(view,params);
	}

	public void hideLayout(){
		this.setVisibility(View.GONE);
	}
	/**
	 * 显示加载组件
	 * @param isloading 初始化加载状�?
	 */
	public void showLayout(boolean isloading){
		if(isloading){
			changeState(false,null);
		}else{
			changeState(true,null);
		}
	}
	/**
	 * 显示重试操作
	 * @param error  错误消息
	 */
	public void setLoadError(String error) {
		changeState(true, error);
	}

	public void setLoadError() {
		changeState(true, null);
	}

	public void setIsLoading(String msg) {
		changeState(false, msg);
	}

	public void setIsLoading() {
		changeState(false, null);
	}

	private void changeState(boolean isError, String msg) {
		if (isError) {
			if (msg != null) {
				tv_error_msg.setText(msg);
			}
			panel_loading.setVisibility(View.GONE);
			panel_loaderror.setVisibility(View.VISIBLE);
		} else {
			if (null != msg) {
				tv_loading_tooltip.setText(msg);
			}
			panel_loaderror.setVisibility(View.GONE);
			panel_loading.setVisibility(View.VISIBLE);
		}
		this.setVisibility(View.VISIBLE);
	}
	
	private OnScreenTouchListener onScreenTouch;
	public void setOnRetryListener(OnScreenTouchListener listener){
		this.onScreenTouch=listener;
	}
	public interface OnScreenTouchListener{
		public void onTouch();
	}

}
