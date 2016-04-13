package com.lk.qf.pay.wedget.webview;

import com.lk.bhb.pay.R;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 
 * @version 1.1.5
 * 
 */
public class T {

	private static Context mContext;
	private View view;

	/**
	 * 禁止实例�?
	 */
	private T() {
	}

	/**
	 * 显示短Toast
	 * 
	 * @param text
	 */
	public static void ss(int text) {
		Toast.makeText(mContext, text, Toast.LENGTH_SHORT).show();
	}

	

	/**
	 * 显示短Toast
	 * 
	 * @param context
	 * @param text
	 */
	public static void ss(Context context, CharSequence text) {
		Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
	}

	/**
	 * 显示长Toast
	 * 
	 * @param text
	 */
	public static void sl(Context context,String text) {
		Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
	}

	

	

	

	

	/**
	 * 显示自定义时长Toast
	 * 
	 * @param context
	 * @param text
	 * @param duration
	 */
	public static void s(Context context, CharSequence text, int duration) {
		mContext = context;
		makeToast(context, text, duration).show();
	}

	

	public static Toast makeToast(Context context, CharSequence text,
			int duration) {
		Toast result = Toast.makeText(context, text, duration);
		result.setGravity(Gravity.BOTTOM, 0, 0);
		result.setDuration(duration);
		return result;
	}

	public static void showInCenterShort(Context con, String str) {
		try{
		Toast result = Toast.makeText(con, str, Toast.LENGTH_SHORT);
		result.setGravity(Gravity.CENTER, 0, 0);
		result.show();
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}

	public static void showInCenterLong(Context con, String str) {
		Toast result = Toast.makeText(con, str, Toast.LENGTH_LONG);
		result.setGravity(Gravity.CENTER, 0, 0);
		result.show();
	}

	/**
	 * Toast View �?LayoutParams
	 */
	private static final ViewGroup.LayoutParams M_LAYOUT_PARAMS = new ViewGroup.LayoutParams(
			ViewGroup.LayoutParams.WRAP_CONTENT,
			ViewGroup.LayoutParams.WRAP_CONTENT);



	/**
	 * 显示自定义Toast
	 * 
	 * @param context
	 * @param text
	 */
	public static void showCustomeShort(Context context, String text) {
		if(null==context){
			return;
		}
		View view = LayoutInflater.from(context).inflate(R.layout.toast, null);
		view.getBackground().setAlpha(180);
		Toast t = new Toast(context);
		t.setView(view);
		TextView tv = (TextView) view.findViewById(R.id.toast_txt);
		tv.setText(text);
		t.setDuration(Toast.LENGTH_SHORT);
		t.show();
	}


	/**
	 * 显示自定义Toast
	 * 
	 * @param context
	 * @param text
	 */
	public static void showCustomeLong(Context context, String text) {
		if(null==context){
			return;
		}
		View view = LayoutInflater.from(context).inflate(R.layout.toast, null);
		view.getBackground().setAlpha(180);
		Toast t = new Toast(context);
		t.setView(view);
		TextView tv = (TextView) view.findViewById(R.id.toast_txt);
		tv.setText(text);
		t.setDuration(Toast.LENGTH_SHORT);
		t.show();
	}
	public static void showCustomeOk(Context context,String val){
		View view = LayoutInflater.from(context).inflate(R.layout.toast_success, null);
		view.getBackground().setAlpha(180);
		Toast t = new Toast(context);
		t.setView(view);
		t.setGravity(Gravity.CENTER, 0, 0);
		TextView tv = (TextView) view.findViewById(R.id.toast_txt);
		tv.setText(val);
		t.setDuration(Toast.LENGTH_SHORT);
		t.show();
	}
	public static void showCustomeOk(Context context,String val,int time){
		View view = LayoutInflater.from(context).inflate(R.layout.toast_success, null);
		view.getBackground().setAlpha(180);
		Toast t = new Toast(context);
		t.setView(view);
		t.setGravity(Gravity.CENTER, 0, 0);
		TextView tv = (TextView) view.findViewById(R.id.toast_txt);
		tv.setText(val);
		t.setDuration(time);
		t.show();
	}

}
