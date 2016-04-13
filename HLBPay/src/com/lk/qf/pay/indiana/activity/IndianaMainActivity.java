package com.lk.qf.pay.indiana.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.lk.bhb.pay.R;
import com.lk.qf.pay.activity.BaseFragmentActivity;
import com.lk.qf.pay.adapter.MyFragmentAdapter;
import com.lk.qf.pay.indiana.fragment.BaskSigleFragment;
import com.lk.qf.pay.indiana.fragment.IndianaShouYeFragment;
import com.lk.qf.pay.indiana.fragment.LatestAnnouncedFragment;
import com.lk.qf.pay.indiana.fragment.ListlingFragment;
import com.lk.qf.pay.indiana.fragment.MyIndianaFragment;

public class IndianaMainActivity extends BaseFragmentActivity {

	private RadioGroup rg;
	private boolean isExit = false;
	private Context mContext;

	private ViewPager pager;
	private MyFragmentAdapter adapter;
	private List<Fragment> list;
	private static int INDEX = 0;
	private String action;
	private LinearLayout ll;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.indiana_main_layout);
		mContext = this;
		// getUserInfo();
		init();
	}

	private void init() {
//		ll = (LinearLayout) findViewById(R.id.ll_indiana);
//		ll.setFitsSystemWindows(true);
		addFragment();
		Log.i("result", "---------init--------");
		rg = (RadioGroup) findViewById(R.id.indiana_rg_tab);
		pager = (ViewPager) findViewById(R.id.indiana_vp_main);

		adapter = new MyFragmentAdapter(getSupportFragmentManager(), list);
		pager.setAdapter(adapter);
		rg.setOnCheckedChangeListener(changeListener);
		pager.setOnPageChangeListener(pageChangeListener);
		pager.setCurrentItem(0);

		// 版本高于4.4
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			// //透明状态栏
			getWindow().addFlags(
					WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

		}

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Intent intent = getIntent();
		if (intent != null) {
			action = intent.getStringExtra("action");
			Log.i("result", "---------action--------" + action);
			if (action != null && action.equals("forFristIndex")) {
				pager.setCurrentItem(0);
				INDEX = 0;
			}
		}
		Log.i("result", "---------onResume--------");
	}

	/**
	 * 添加Fragment
	 */
	private void addFragment() {
		list = new ArrayList<Fragment>();

		list.add(new IndianaShouYeFragment());
		list.add(new LatestAnnouncedFragment());
		list.add(new BaskSigleFragment());
		list.add(new ListlingFragment());
		list.add(new MyIndianaFragment());

	}

	OnPageChangeListener pageChangeListener = new OnPageChangeListener() {

		@Override
		public void onPageSelected(int position) {
			// TODO Auto-generated method stub
			INDEX = position;
			RadioButton rb = (RadioButton) rg.getChildAt(position);
			rb.setChecked(true);
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
			// TODO Auto-generated method stub

		}
	};

	OnCheckedChangeListener changeListener = new OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(RadioGroup arg0, int checkedId) {
			// TODO Auto-generated method stub
			switch (checkedId) {

			case R.id.indiana_tab_1:
				pager.setCurrentItem(0);
				INDEX = 0;
				break;
			case R.id.indiana_tab_2:
				pager.setCurrentItem(1);
				INDEX = 1;
				break;

			case R.id.indiana_tab_3:
				pager.setCurrentItem(2);
				INDEX = 2;
				break;
			case R.id.indiana_tab_4:
				pager.setCurrentItem(3);
				INDEX = 3;
				break;
			case R.id.indiana_tab_5:
				pager.setCurrentItem(4);
				INDEX = 4;
				break;
			}
		}
	};
}
