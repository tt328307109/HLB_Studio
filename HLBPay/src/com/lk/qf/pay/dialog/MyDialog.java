package com.lk.qf.pay.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.Window;
import android.widget.TextView;

import com.lk.bhb.pay.R;

public class MyDialog extends Dialog {
	private TextView tvText;
	private String str="加载中...";
	public MyDialog(Context context, int theme) {
		super(context, R.style.FullScreenDialogAct);
		init();
	}

	public MyDialog(Context context) {
		super(context, R.style.FullScreenDialogAct);
		init();
	}
	private void init(){
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.common_loading_dialog);
		tvText=(TextView) findViewById(R.id.tv_common_dialog_loading);
		tvText.setText(str);
	}
	public void setText(String str){
		tvText.setText(str);
	}
}
