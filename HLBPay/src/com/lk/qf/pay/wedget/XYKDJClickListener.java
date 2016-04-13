package com.lk.qf.pay.wedget;

import com.lk.bhb.pay.R;

import android.view.View;
import android.view.View.OnClickListener;

public abstract class XYKDJClickListener implements OnClickListener {
	/**
	 * 基类的onClick方法
	 */
	@Override
	public void onClick(View v) {
		myOnClick((Integer) v.getTag(R.id.tag_first), v);
	}

	public abstract void myOnClick(int position, View v);
}
