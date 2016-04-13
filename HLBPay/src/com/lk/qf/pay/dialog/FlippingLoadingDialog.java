package com.lk.qf.pay.dialog;

import com.lk.bhb.pay.R;

import android.app.Dialog;
import android.content.Context;
import android.widget.TextView;

public class FlippingLoadingDialog extends Dialog {

	private TextView mHtvText;
	private String mText;

	public FlippingLoadingDialog(Context context, String text) {
		super(context);
		mText = text;
		init();
	}

	private void init() {
		setContentView(R.layout.common_loading_dialog);
		mHtvText=(TextView) findViewById(R.id.tv_common_dialog_loading);
		mHtvText.setText(mText);
	}

	public void setText(String text) {
		mText = text;
		mHtvText.setText(mText);
	}

	@Override
	public void dismiss() {
		if (isShowing()) {
			super.dismiss();
		}
	}
}
