package com.lk.qf.pay.activity;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.lk.bhb.pay.R;
import com.lk.qf.pay.aanewactivity.AddDaiLiMerchantActivity;
import com.lk.qf.pay.aanewactivity.JieSuanActivity;
import com.lk.qf.pay.aanewactivity.PoPhotoActivity;
import com.lk.qf.pay.aanewactivity.QianBaoActivity;
import com.lk.qf.pay.adapter.MyFragmentAdapter;
import com.lk.qf.pay.fragment.MenuFragment;
import com.lk.qf.pay.fragment.MoreFragment;
import com.lk.qf.pay.fragment.NullFragment;
import com.lk.qf.pay.golbal.MApplication;
import com.lk.qf.pay.sharedpref.SharedPrefConstant;
import com.lk.qf.pay.tool.AppManager;
import com.lk.qf.pay.tool.SystemBarTintManager;
import com.lk.qf.pay.tool.T;
import com.lk.qf.pay.wedget.customdialog.AlertDialog;

import java.util.ArrayList;
import java.util.List;

public class MenuActivity extends BaseFragmentActivity {

	private RadioGroup rg;
	private boolean isExit = false;
	private Context mContext;

	ViewPager pager;
	MyFragmentAdapter adapter;
	List<Fragment> list;
	static int INDEX = 0;
	private String state = "" , tuihuiMessage = "";

	@Override
	protected void onCreate(Bundle arg0) {

		super.onCreate(arg0);
		setContentView(R.layout.activity_menu);
		Button btn = (Button) findViewById(R.id.btn_base1);
		// getNavigationBarHeight(this);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			// //透明状态栏
			// getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			setTranslucentStatus(true);

		}
		tuihuiMessage = MApplication.mSharedPref
				.getSharePrefString(SharedPrefConstant.POS_NOTEURL);
		state = MApplication.mSharedPref
				.getSharePrefString(SharedPrefConstant.STATE);

		if (state.equals("retu")) {
			showMessage(tuihuiMessage);
		}
		SystemBarTintManager tintManager = new SystemBarTintManager(this);
		tintManager.setStatusBarTintEnabled(true);
		// tintManager.setNavigationBarTintEnabled(false);
		tintManager.setStatusBarTintResource(R.color.blue_main);

		mContext = this;
		// getUserInfo();
		init();

	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
	}

	/**
	 * 沉浸式状态栏utils
	 * 
	 * @param on
	 */
	@TargetApi(19)
	private void setTranslucentStatus(boolean on) {
		Window win = getWindow();
		WindowManager.LayoutParams winParams = win.getAttributes();
		final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
		if (on) {
			winParams.flags |= bits;
		} else {
			winParams.flags &= ~bits;
		}
		win.setAttributes(winParams);
	}

	@SuppressLint("NewApi")
	private void init() {
		addFragment();
		rg = (RadioGroup) findViewById(R.id.rg_tab);
		pager = (ViewPager) findViewById(R.id.vp_main);

		adapter = new MyFragmentAdapter(getSupportFragmentManager(), list);
		pager.setAdapter(adapter);
		rg.setOnCheckedChangeListener(changeListener);
		pager.setOnPageChangeListener(pageChangeListener);
		pager.setCurrentItem(1);

		// rg.setPadding(0, 0, 0, getNavigationBarHeight(this));
	}

	/**
	 * 添加Fragment
	 */
	private void addFragment() {
		list = new ArrayList<Fragment>();

//		list.add(new CashInFragment());
		if (getData() == false) {
			if (showUserState() == false) {
				list.add(new QianBaoActivity());
//				list.add(new NullFragment() );
			}
		}
		if(getData()||showUserState()) {
			list.add(new NullFragment() );
			
		}
		
		
		list.add(new MenuFragment());
		// list.add(MerchantFragment.newInstance());
		list.add(MoreFragment.newInstance());

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

			case R.id.home_btn_shop:
				pager.setCurrentItem(1);
				INDEX = 1;
				break;
			case R.id.wallet_btn:
				pager.setCurrentItem(0);
				INDEX = 0;
				break;

			case R.id.set_btn:
				pager.setCurrentItem(2);
				INDEX = 2;
				break;
			}
		}
	};

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_DOWN
				&& event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			exitApp();
			return false;
		} else {
			return super.dispatchKeyEvent(event);
		}
	}

	/**
	 * @Title: exitApp
	 * @Description: 退出app
	 */
	private void exitApp() {
		if (!isExit) {
			isExit = true;
			T.ss(getResources().getString(R.string.quit_app));
			mHandler.sendEmptyMessageDelayed(0, 2000);
		} else {
			MApplication.mSharedPref.putSharePrefBoolean(SharedPrefConstant.ISLOGIN, false);//是否登录 
			onBackPressed();
//			AppManager.getAppManager().finishActivity();
			AppManager.getAppManager().AppExit(this);
		}
	}

	/**
	 * 关闭所有Activity
	 */
	public void onBackPressed() {
		Intent i = new Intent(Intent.ACTION_MAIN);
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		i.addCategory(Intent.CATEGORY_HOME);
		startActivity(i);
	}

	Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			isExit = false;
		}
	};

	private boolean getData() {
		String strShiming = "";
		String strJiesuan = "";
		String strPhoto = "";
		boolean is = false;
		strShiming = MApplication.mSharedPref
				.getSharePrefString(SharedPrefConstant.MERENTERPRISERADD);
		strJiesuan = MApplication.mSharedPref
				.getSharePrefString(SharedPrefConstant.MERBANKADD);
		strPhoto = MApplication.mSharedPref
				.getSharePrefString(SharedPrefConstant.MERCERTIFICATEADD);
		if (strShiming.equals("0")) {
			showUser(0, "您尚未进行实名认证,是否认证?");
			is = true;
		} else if (strJiesuan.equals("0")) {

			showUser(1, "您尚未填写结算信息,是否填写?");
			is = true;
		} else if (strPhoto.equals("0")) {

			showUser(2, "您尚未上传证件照,是否上传?");
			is = true;
		}
		return is;
	}
	
	/**
	 * 提示信息
	 */
	private void showUser(int stpe, String msg) {
		final int code = stpe;
		new AlertDialog(MenuActivity.this).builder().setTitle("提示").setMsg(msg)
				.setPositiveButton("确定", new OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent intent = null;
						if (code == 0) {

							intent = new Intent(mContext,
									AddDaiLiMerchantActivity.class);

						} else if (code == 1) {
							intent = new Intent(mContext, JieSuanActivity.class);
						} else if (code == 2) {
							intent = new Intent(mContext, PoPhotoActivity.class);
						}
						intent.putExtra("type", "0");
						intent.putExtra(
								"userName",
								MApplication.mSharedPref
										.getSharePrefString(SharedPrefConstant.USERNAME));
						startActivity(intent);
					}
				}).setNegativeButton("取消", new OnClickListener() {
					@Override
					public void onClick(View v) {

					}
				}).show();
	}
	
	/**
	 * 显示商户状态
	 */
	private boolean showUserState() {
		boolean is = false;
		Log.i("result", "---state-------" + state);
		if (!state.equals("en")) {
			T.ss("商户未审核");
			is = true;
		} else if (state.equals("retu")) {
			showMessage(tuihuiMessage);
			is = true;
		}
		// else if (usesort.equals("0")) {
		// T.ss("商户未缴纳押金");
		// is = true;
		// }
		return is;

	}
	
	/**
	 * 提示退回信息
	 */
	private void showMessage(String message) {
		String str = "";
		if (message.length() != 0) {
			str = "退回理由:";
		}

		new AlertDialog(MenuActivity.this).builder().setTitle("提示")
				.setMsg("您的信息已被退回,是否重新填写?" + "\n\n" + str + message)
				.setPositiveButton("确定", new OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent intent = new Intent(MenuActivity.this,
								AddDaiLiMerchantActivity.class);
						intent.putExtra("type", "0");
						intent.putExtra(
								"userName",
								MApplication.mSharedPref
										.getSharePrefString(SharedPrefConstant.USERNAME));
						startActivity(intent);
					}
				}).setNegativeButton("取消", new OnClickListener() {
					@Override
					public void onClick(View v) {

					}
				}).show();
	}

}
