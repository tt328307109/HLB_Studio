/**
 * 功能：全局变量
 * 类名：TCPayApplication.java
 * 日期：2013-11-26
 * 作者：lukejun
 */
package com.lk.qf.pay.golbal;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.HashMap;

import org.apache.http.Header;
import org.json.JSONException;

import cn.jpush.android.api.JPushInterface;

import com.lk.pay.communication.AsyncHttpResponseHandler;
import com.lk.qf.pay.beans.BasicResponse;
import com.lk.qf.pay.sharedpref.SharedPref;
import com.lk.qf.pay.sharedpref.SharedPrefConstant;
import com.lk.qf.pay.tool.MyHttpClient;
import com.lk.qf.pay.tool.T;
import com.lk.qf.pay.tool.UnCeHandle;
import com.lk.qf.pay.utils.Logger;

import android.app.Application;
import android.content.res.Resources;
import android.util.Log;
import android.view.View;


/**
 * @ClassName: TCPayApplication
 * @Description: 全局变量
 * @author lukejun
 * @date 2013-11-26 下午1:32:44
 * 
 */
public class MApplication extends Application implements UncaughtExceptionHandler {

	public static MApplication mApplicationContext = null;
	public static SharedPref mSharedPref = null;
	public int screenWidth=0;
	public int screenHeight=0;
	private String SERVER_TYPE = null;
	private String Blutooth;


	public String getBlutooth() {
		return Blutooth;
	}



	public void setBlutooth(String blutooth) {
		Blutooth = blutooth;
	}



	public String getSERVER_TYPE() {
		return SERVER_TYPE;
	}



	public void setSERVER_TYPE(String sERVER_TYPE) {
		SERVER_TYPE = sERVER_TYPE;
	}


	private static final String TAG = "JPush";
	@Override
	public void onCreate() {
		super.onCreate();
		mApplicationContext = this;
		mSharedPref = SharedPref
				.getInstance(SharedPrefConstant.PREF_NAME, this); // 单例sp
		UnCeHandle unCatch=new UnCeHandle(this);
		Log.d(TAG, "[ExampleApplication] onCreate");
		JPushInterface.setDebugMode(false); 	// 设置开启日志,发布时请关闭日志
		JPushInterface.init(this);    
	}
	
	

	public boolean  chechStatus(){
		if(User.uStatus==2){
			return true;
		}else if(User.uStatus==1){
			T.ss("实名认证审核中");
			return false;
		}else if(User.uStatus==0){
			T.ss("尚未实名认证");	
			return false;
		}else if(User.uStatus==3){
			T.ss("实名认证未通过");
			return false;
		}
		return false;
	}
	/**
	 * 清除用户状态信息
	 * <h1>不清除会导致登录用户状态错误</h1>
	 */
	public void clearSavedInfo(){
		User.uAccount=null;
		User.login=false;
		User.uName=null;
		User.cardNum=0;
		User.termNum=0;
		User.uId=null;
		User.uStatus=0;
		User.bindStatus=0;
		User.amtT0=null;
		User.amtT1=null;
		User.bindDevices=null;
		User.totalAmt=null;
		User.cardInfo=null;
		MApplication.mSharedPref.clear();
	}
	
	public void refreshUserInfo(){
		System.out.println("刷新用户信息");
		HashMap<String, String> params=new HashMap<String, String>();
		MyHttpClient.post(mApplicationContext, Urls.GET_USER_INFO, params, new AsyncHttpResponseHandler() {
			
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
				System.out.println("刷新用户信息---");
                 try {
					BasicResponse r=new BasicResponse(responseBody).getResult();
					if(r.isSuccess()){
						User.uStatus=r.getJsonBody().optInt("custStatus");
						User.termNum=r.getJsonBody().optInt("termNum");
						User.cardNum=r.getJsonBody().optInt("cardNum");
					}else{
					}
				} catch (JSONException e) {
					// TODO 自动生成的 catch 块
					e.printStackTrace();
				}				
                 
			}
			
			@Override
			public void onFailure(int statusCode, Header[] headers,
					byte[] responseBody, Throwable error) {
				// TODO 自动生成的方法存根
				
			}
		});
	}
	

	@Override
	public void uncaughtException(Thread thread, Throwable ex) {

		Logger.d("Test", "system crush!!!");
		android.os.Process.killProcess(android.os.Process.myPid());

	}

	public int getScreenWidth() {
		return screenWidth;
	}

	public void setScreenWidth(int screenWidth) {
		this.screenWidth = screenWidth;
	}

	public int getScreenHeight() {
		return screenHeight;
	}

	public void setScreenHeight(int screenHeight) {
		this.screenHeight = screenHeight;
	}
}
