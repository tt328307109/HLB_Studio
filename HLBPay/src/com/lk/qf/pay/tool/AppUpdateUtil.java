package com.lk.qf.pay.tool;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.lk.bhb.pay.R;
import com.lk.qf.pay.golbal.Urls;

public class AppUpdateUtil {
	private Context context;
	private ProgressBar mProgress;
	private Dialog downloadDialog;
	private String apkUrl;
	private int progress;
	private static final int DOWN_UPDATE = 1;
	private static final int DOWN_OVER = 2;
	private static final String APK_NAME = "QianFPay.apk";
	private Handler mHandler = new Handler(){
	public void handleMessage(android.os.Message msg) {			
			switch (msg.what) {				
				case DOWN_UPDATE:
					mProgress.setProgress(progress);
					break;
				case DOWN_OVER:
					Toast.makeText(context, "下载完毕", 2000).show();
					downloadDialog.dismiss();
					installApk();
					break;
				default:
					break;
			}
		};
	};
	
	public AppUpdateUtil(Context context, String apkUrl){
		this.context = context;
		this.apkUrl = apkUrl;
	}
	
	public void showUpdateNoticeDialog(String apknote){
		AlertDialog.Builder builder = new Builder(context);
		builder.setTitle("发现新版本");
		builder.setMessage(apknote);
		builder.setPositiveButton("立即更新", new DialogInterface.OnClickListener() {			
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				showDownloadDialog();
			}
		});
		builder.setNegativeButton("稍后再说", new DialogInterface.OnClickListener() {			
			public void onClick(DialogInterface dialog, int which) {				
				dialog.dismiss();				
			}
		});
		builder.create().show();
	}
	
	private void showDownloadDialog() {
		AlertDialog.Builder builder = new Builder(context);
		builder.setTitle("软件版本更新");
		builder.setCancelable(false);
		final LayoutInflater inflater = LayoutInflater.from(context);
		View v = inflater.inflate(R.layout.progress_version_update, null);
		mProgress = (ProgressBar) v.findViewById(R.id.progress_version_update_pb);
		builder.setView(v);		
		downloadDialog =builder.create();
		downloadDialog.show();
		//下载apk安装包
		downloadApk();	
	}
	
	private void downloadApk() {		
		Thread downLoadThread = new Thread(mdownApk);
		downLoadThread.start();		
	}
	
	private Runnable mdownApk = new Runnable() {		
		@Override
		public void run() {
			HttpClient client = new DefaultHttpClient();    
            // params[0]代表连接的url    
            HttpGet get = new HttpGet("http://192.168.0.116/pay/"+apkUrl);
            HttpResponse response;    
            try {    
                response = client.execute(get);    
                HttpEntity entity = response.getEntity();    
                long length = entity.getContentLength();    
                InputStream is = entity.getContent();    
                FileOutputStream fileOutputStream = null;    
                if (is != null) {    
                    File file = new File(Environment    
                            .getExternalStorageDirectory(), APK_NAME);    
                    fileOutputStream = new FileOutputStream(file);                           
                    byte[] buf = new byte[1024];    
                    int ch = -1;    
                    int count = 0;    
                    while ((ch = is.read(buf)) != -1) {                             
                        fileOutputStream.write(buf, 0, ch);    
                        count += ch;    
                        progress = (int)(((float)count / length) * 100);            
                      //更新进展
                        if(mProgress.getProgress()<progress){
                        	mHandler.sendEmptyMessage(DOWN_UPDATE);
                        }    					
                    }    
                }    
                fileOutputStream.flush();    
                if (fileOutputStream != null) {    
                    fileOutputStream.close();    
                }    
                mHandler.sendEmptyMessage(DOWN_OVER);    
            } catch (ClientProtocolException e) {                
                e.printStackTrace();    
            } catch (IOException e) {                  
                e.printStackTrace();    
            }    			
		}
	};
	
	private void installApk(){
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setDataAndType(Uri.fromFile(new File("/sdcard/"+APK_NAME)), "application/vnd.android.package-archive");
		context.startActivity(i);
	}
}
