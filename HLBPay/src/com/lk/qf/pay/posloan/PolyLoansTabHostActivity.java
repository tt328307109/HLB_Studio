package com.lk.qf.pay.posloan;

import com.lk.bhb.pay.R;
import com.lk.qf.pay.fragment.PolyLoanHKListFragment;
import com.lk.qf.pay.fragment.MyPolyLoanFragment;
import com.lk.qf.pay.wedget.CommonTitleBar;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.TabHost.TabSpec;

public class PolyLoansTabHostActivity extends FragmentActivity implements
		OnClickListener {

	private CommonTitleBar title;

	// 定义FragmentTabHost对象
	private FragmentTabHost fragmentTabHost;

	// 定义一个线性布局
	private LayoutInflater layoutInflater;
	// 定义数组来存放Fragment界面
	private Class[] fragmentArray = { MyPolyLoanFragment.class,
			PolyLoanHKListFragment.class };
	// 定义数组来存放按钮图片
	// private int[] mImageViewArray =
	// {R.drawable.tab_home_btn,R.drawable.tab_message_btn,R.drawable.tab_selfinfo_btn,
	// R.drawable.tab_square_btn,R.drawable.tab_more_btn};

	// Tab选项卡的文字
	private String[] mTextviewArray = { "我的理贷", "还款记录" };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.poly_loan_tab_layout);
		init();
	}

	private void init() {
		Log.i("result", "---------onCreate-------");
		title = (CommonTitleBar) findViewById(R.id.titlebar_poly_loans_back);
		title.setActName("保理贷");
		title.setCanClickDestory(this, true);
		findViewById(R.id.btn_applyPolyLoan).setOnClickListener(this);
		// 实例化布局
		layoutInflater = LayoutInflater.from(this);
		// 实例化TabHost对象，得到TabHost
		fragmentTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
		// fragmentTabHost.setup();
		fragmentTabHost.setup(this, getSupportFragmentManager(),
				R.id.fl_realtabcontent);
		// 得到fragment的个数
		int count = fragmentArray.length;
		for (int i = 0; i < count; i++) {
			// 为每一个Tab按钮设置图标、文字和内容
			TabSpec tabSpec = fragmentTabHost.newTabSpec(mTextviewArray[i])
					.setIndicator(getTabItemView(i));
			// 将Tab按钮添加进Tab选项卡中
			fragmentTabHost.addTab(tabSpec, fragmentArray[i], null);
			// // 设置Tab按钮的背景
			// fragmentTabHost.getTabWidget().getChildAt(i)
			// .setBackgroundResource(R.drawable.selector_tab_background);
		}
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		Intent intent = new Intent(PolyLoansTabHostActivity.this,
				ApplyPolyLoanByLicaiActivity.class);
		startActivity(intent);
		
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		// String str = PosData.getPosData().getRandom();
		// if (str != null) {
		// if (str.equals("isApplySuccess")) {
		// Log.i("result", "---------str------------" + str);
		// fragmentTabHost.setCurrentTab(1);
		// PosData.getPosData().clearPosData();
		// }
		// }
	}

	/**
	 * 给Tab按钮设置图标和文字
	 */
	private View getTabItemView(int i) {
		View view = layoutInflater.inflate(R.layout.tab_view_layout, null);
		// //设置图像
		// ImageView imageView = (ImageView) view.findViewById(R.id.imageview);
		// imageView.setImageResource(mImageViewArray[i]);
		// 设置文字
		TextView textView = (TextView) view.findViewById(R.id.tv_tab_poly_loan);
		textView.setText(mTextviewArray[i]);
		return view;
	}

	// @Override
	// public void onClick(View arg0) {
	// // TODO Auto-generated method stub
	// Intent intent = new Intent(PolyLoansTabHostActivity.this,
	// LoanHuanKuanListActivity.class);
	// startActivity(intent);
	// }

}
