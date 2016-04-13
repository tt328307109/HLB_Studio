package com.lk.qf.pay.utils;

import com.lk.qf.pay.wedget.customdialog.AlertDialog;

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;
import android.view.View.OnClickListener;

/**
 * 
 * @author Administrator
 * ����Ȩ��
 *  <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>
 *  <uses-permission android:name="android.permission.INTERNET"/>
 */
public class ConnectionUtil {
	
	
	public static boolean isConn(Context context){
        boolean bisConnFlag=false;
        ConnectivityManager conManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo network = conManager.getActiveNetworkInfo();
        if(network!=null){
            bisConnFlag=conManager.getActiveNetworkInfo().isAvailable();
        }
        return bisConnFlag;
    }
	
	
	public static void setNetworkMethod(final Context context){
      
		new AlertDialog(context).builder().setTitle("网络设置提示")
		.setMsg("网络连接不可用,是否进行设置?")
		.setPositiveButton("设置", new OnClickListener() {
			@Override
			public void onClick(View v) {
				 Intent intent=null;
	                
	                if(android.os.Build.VERSION.SDK_INT>10){
	                    intent = new Intent(android.provider.Settings.ACTION_SETTINGS);
	                }else{
	                	intent = new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS);
//	                    intent = new Intent();
//	                    ComponentName component = new ComponentName("com.android.settings","com.android.settings.WirelessSettings");
//	                    intent.setComponent(component);
//	                    intent.setAction("android.intent.action.VIEW");
	                }
	                context.startActivity(intent);
			}
		}).setNegativeButton("取消", new OnClickListener() {
			@Override
			public void onClick(View v) {

			}
		}).show();
		
    }
	
}
