package com.lk.qf.pay.wedget;

import com.lk.bhb.pay.R;

import android.view.View;
import android.view.View.OnLongClickListener;

public abstract class MyLongClickListener implements OnLongClickListener {
		
//	public static int pos;
		@Override
		public boolean onLongClick(View v) {
			myLongOnClick((Integer) v.getTag(R.id.tag_first), v);
			return false;
		}
		public abstract void myLongOnClick(int position, View v);
		
	}