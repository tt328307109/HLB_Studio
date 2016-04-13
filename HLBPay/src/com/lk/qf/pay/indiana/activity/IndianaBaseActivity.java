package com.lk.qf.pay.indiana.activity;

import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.lk.bhb.pay.R;
import com.lk.qf.pay.dialog.MyDialog;
import com.lk.qf.pay.tool.AppManager;
import com.lk.qf.pay.tool.MyHttpClient;
import com.lk.qf.pay.tool.SystemBarTintManager;
import com.lk.qf.pay.tool.T;
import com.lk.qf.pay.wedget.customdialog.AlertDialog;
import com.umeng.analytics.MobclickAgent;

public class IndianaBaseActivity extends FragmentActivity {

	MyDialog dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AppManager.getAppManager().addActivity(this);
		
		//版本高于4.4
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
//        	//透明状态栏  
//        	getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS); 
        	setTranslucentStatus(true);
        	
     }
        SystemBarTintManager tintManager = new SystemBarTintManager(this);
		tintManager.setStatusBarTintEnabled(true);
//		tintManager.setNavigationBarTintEnabled(false);
		tintManager.setStatusBarTintResource(R.color.titleBar_yellow);
		// loading.setCancelable(false);
	}
	
	/**
	 * 沉浸式状态栏utils
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

	private void getDialogInstance() {

		if (dialog == null) {
			dialog = new MyDialog(this);
			dialog.setCanceledOnTouchOutside(false);
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();

		MobclickAgent.onPause(this);
	}

	public void showLoadingDialog() {
		getDialogInstance();
		dialog.setCancelable(false);
		dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {

				IndianaBaseActivity.this.finish();

			}
		});
		dialog.show();
	}

	/**
	 * 绑定返回事件
	 */
	protected void bindBackButton() {
		findViewById(R.id.btn_back).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						finish();
					}
				});

	}

	// /**
	// *
	// */
	// protected void showLoadingDialogCancleable(){
	// getDialogInstance();
	// dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
	//
	// @Override
	// public void onCancel(DialogInterface dialog) {
	// BaseActivity.this.finish();
	// }
	// });
	// dialog.show();
	// }

	public void updateDialogDes(String str) {
		if (null != dialog) {
			if (dialog.isShowing()) {
				dialog.setText(str);
			}
		}
	}

	protected void showLoadingDialog(String text) {
		getDialogInstance();

		if (text != null) {
			dialog.setText(text);
		}
		if (dialog.isShowing()) {
			return;
		}
		dialog.setCanceledOnTouchOutside(false);

		dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {

				IndianaBaseActivity.this.finish();

			}
		});
		dialog.show();
	}

	/**
	 * 禁止关闭的dialog
	 * 
	 * @param text
	 */
	protected void showLoadingDialogCannotCancle(String text) {
		getDialogInstance();
		if (!TextUtils.isEmpty(text)) {
			dialog.setText(text);
		}
		dialog.setCanceledOnTouchOutside(false);
		dialog.setCancelable(false);
		dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {

			@Override
			public boolean onKey(DialogInterface dialog, int keyCode,
					KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK) {
					T.showCustomeShort(IndianaBaseActivity.this, "此阶段不能返回");
					return true;
				}
				return false;
			}
		});
		dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {

				IndianaBaseActivity.this.finish();

			}
		});
		dialog.show();
	}

	public void showToast(String text) {
		Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT)
				.show();
	}

	public void showDialog(String msg) {
		new AlertDialog(this).builder().setMsg(msg)
				.setNegativeButton("确定", new OnClickListener() {
					@Override
					public void onClick(View v) {

					}
				}).show();
	}

	public void dismissLoadingDialog() {
		if (dialog != null && dialog.isShowing()) {
			dialog.dismiss();
		}
	}

	/**
	 * 网络错误处理
	 * 
	 * @param statusCode
	 * @param error
	 */
	public void networkError(int statusCode, Throwable error) {
		T.sl("网络错误:" + error.getMessage() + statusCode);
	}

	/**
	 * 登录长时间未操作
	 */
	public void loginTimeOut() {

	}

	@Override
	protected void onDestroy() {
		// 结束Activity&从堆栈中移除
		MobclickAgent.onKillProcess(this);
		AppManager.getAppManager().finishActivity(this);
		MyHttpClient.cancleRequest(this);
		super.onDestroy();
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return null;
	}

}

