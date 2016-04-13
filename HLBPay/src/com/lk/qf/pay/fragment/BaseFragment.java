package com.lk.qf.pay.fragment;

import com.lk.bhb.pay.R;
import com.lk.qf.pay.dialog.MyDialog;
import com.lk.qf.pay.wedget.customdialog.AlertDialog;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;

public class BaseFragment extends Fragment {
	MyDialog dialog;

	public boolean isStop = false;

	public BaseFragment() {
		super();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

	}

	@Override
	public void onResume() {
		super.onResume();
		isStop = false;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {

		super.onActivityCreated(savedInstanceState);
	}

	public void launchSubPage(Activity root, Class<?> activity) {
		Intent in = new Intent(root, activity);
		in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		root.startActivity(in);
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onStop() {
		super.onStop();
		isStop = true;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	public void hideInput(View myview) {
		InputMethodManager imm = (InputMethodManager) getActivity()
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(myview.getWindowToken(), 0);
	}

	public void showInput(View myview) {
		InputMethodManager imm = (InputMethodManager) getActivity()
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.showSoftInput(myview, 0);
	}

	// 隱藏鍵盤
	public void hideInput() {
		if (getActivity() != null && getActivity().getCurrentFocus() != null
				&& getActivity().getCurrentFocus().getWindowToken() != null) {
			((InputMethodManager) getActivity().getSystemService(
					Context.INPUT_METHOD_SERVICE))
					.hideSoftInputFromWindow(getActivity().getCurrentFocus()
							.getApplicationWindowToken(),
							InputMethodManager.HIDE_NOT_ALWAYS);
		}

	}

	public void showInput() {
		InputMethodManager inputMethodManager = (InputMethodManager) getActivity()
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		inputMethodManager.toggleSoftInput(0,
				InputMethodManager.HIDE_NOT_ALWAYS);

	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		return false;
	}

	public void showProgressDialog() {
		ProgressDialog progressDialog = null;

		if (progressDialog != null) {
			progressDialog.cancel();
		}
		progressDialog = new ProgressDialog(getActivity());
		Drawable drawable = getResources().getDrawable(
				R.drawable.loading_animation);
		progressDialog.setIndeterminateDrawable(drawable);
		progressDialog.setIndeterminate(true);
		progressDialog.setCancelable(true);
		progressDialog.setMessage("正在加载...");
		progressDialog.setCanceledOnTouchOutside(false);
		progressDialog.show();
	}

	private void getDialogInstance() {

		if (dialog == null) {
			dialog = new MyDialog(getActivity());
			dialog.setCanceledOnTouchOutside(false);
		}
	}

	public void showLoadingDialog() {
		getDialogInstance();
		dialog.setCancelable(false);
		dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {

				getActivity().finish();

			}
		});
		dialog.show();
	}

	public void dismissLoadingDialog() {
		if (dialog != null && dialog.isShowing()) {
			dialog.dismiss();
		}
	}

	public void showDialog(String msg) {

		new AlertDialog(getActivity()).builder().setMsg(msg)
				.setNegativeButton("确定", new OnClickListener() {
					@Override
					public void onClick(View v) {

					}
				}).show();
//		BaseDialog mBackDialog = BaseDialog.getDialog(getActivity(), "提示", msg,
//				"确定", new DialogInterface.OnClickListener() {
//
//					@Override
//					public void onClick(DialogInterface dialog, int which) {
//						dialog.cancel();
//					}
//				});
//		mBackDialog.setCancelable(false);
//		mBackDialog.show();
	}

}
