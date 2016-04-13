package com.lk.qf.pay.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lk.bhb.pay.R;
import com.lk.pay.communication.AsyncHttpResponseHandler;
import com.lk.qf.pay.beans.BasicResponse;
import com.lk.qf.pay.golbal.MApplication;
import com.lk.qf.pay.golbal.Urls;
import com.lk.qf.pay.golbal.User;
import com.lk.qf.pay.tool.AppManager;
import com.lk.qf.pay.tool.AppUpdateUtil;
import com.lk.qf.pay.tool.MyHttpClient;
import com.lk.qf.pay.tool.T;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;
import com.umeng.update.UpdateStatus;

import org.apache.http.Header;
import org.json.JSONException;

import java.util.HashMap;

public class MoreActivity extends BaseActivity implements OnClickListener {
	private TextView more_share_text;// 分享
	private TextView more_about_text;// 关于我们
	private TextView more_feedback_text;// 意见反馈
	private TextView more_help_text;// 使用帮助
	private Button back;// 返回
	private LinearLayout more_version_layout;// 版本更新
	private LinearLayout merchant_contact_layout;// 拨打客服电话
	private String userName;
	private MApplication mApplication;
	private View layoutView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_more);
		mApplication = (MApplication) this.getApplication();
		userName = User.uName;
		initDate();
	}

	private void initDate() {
		more_share_text = (TextView) findViewById(R.id.more_share_text);
		more_share_text.setOnClickListener(this);
		more_version_layout = (LinearLayout) findViewById(R.id.more_version_layout);
		more_version_layout.setOnClickListener(this);
		more_about_text = (TextView) findViewById(R.id.more_about_text);
		more_about_text.setOnClickListener(this);
		merchant_contact_layout = (LinearLayout) findViewById(R.id.merchant_contact_layout);
		merchant_contact_layout.setOnClickListener(this);
		more_feedback_text = (TextView) findViewById(R.id.more_feedback_text);
		more_feedback_text.setOnClickListener(this);
		more_help_text = (TextView) findViewById(R.id.more_help_text);
		more_help_text.setOnClickListener(this);
		back = (Button) findViewById(R.id.btn_back);
		findViewById(R.id.btn_more_safe_quit).setOnClickListener(this);
		back.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {

		case R.id.more_share_text:
			share();
			break;
		case R.id.more_version_layout:
			// startActivity(new Intent(getActivity(),
			// VersionInfoActivity.class));
//			checkUpdate();
			UmengUpdateAgent.setUpdateListener(updateListener);
			UmengUpdateAgent.forceUpdate(MoreActivity.this);
			break;
		case R.id.more_about_text:
			startActivity(new Intent(MoreActivity.this, AboutActivity.class));
			break;
		case R.id.merchant_contact_layout:
			dialTelephone();
			break;
		case R.id.more_feedback_text:
			feedback();
			break;
		case R.id.more_help_text:
			startActivity(new Intent(this, HelpActivity.class));
			break;
		case R.id.btn_back:
			finish();
			break;
		case R.id.btn_more_safe_quit:
			goQuit(); // 安全退出
			break;
		}

	}

	/**
	 * 分享功能
	 */
	private void share() {
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("image/*");
		intent.putExtra(Intent.EXTRA_SUBJECT, "分享");
		intent.putExtra(
				Intent.EXTRA_TEXT,
				" 欢迎您使用" + getResources().getString(R.string.app_name)
						+ ",下载地址:"
						+ getResources().getString(R.string.download_add));
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(Intent.createChooser(intent, this.getTitle()));
		// this.overridePendingTransition(R.anim.share_in_from_bottom, 0);
	}

	private void checkUpdate() {
		PackageInfo pkg = null;
		try {
			pkg = getPackageManager().getPackageInfo(
					getApplication().getPackageName(), 0);
		} catch (NameNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String appName = pkg.applicationInfo.loadLabel(getPackageManager())
				.toString();
		// System.out.println("=========================================>>>>"+appName);
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("appName", appName);
		MyHttpClient.post(MoreActivity.this, Urls.CHECK_UPDATE, params,
				new AsyncHttpResponseHandler() {

					@Override
					public void onSuccess(int statusCode, Header[] headers,
							byte[] responseBody) {
						// Logger.json(new String(responseBody));
						try {
							BasicResponse response = new BasicResponse(
									responseBody).getResult();
							if (response.isSuccess()) {
								String status = response.getJsonBody()
										.optString("checkState");
								// Log.i("result",
								// "--------status----------"+status);
								if (status.equals("0")) {
									T.ss("已经是最新版本");
								} else if (status.equals("1")) {
									String note = response.getJsonBody()
											.optString("fileDesc");
									String url = response.getJsonBody()
											.optString("fileUrl");
									AppUpdateUtil update = new AppUpdateUtil(
											MoreActivity.this, url);
									update.showUpdateNoticeDialog(note);
								} else if (status.equals("2")) {
									String note = response.getJsonBody()
											.optString("fileDesc");
									String url = response.getJsonBody()
											.optString("fileUrl");
									AppUpdateUtil update = new AppUpdateUtil(
											MoreActivity.this, url);
									update.showUpdateNoticeDialog(note);
								}
							} else {
								T.ss("已经是最新版本!");
								// T.ss(response.getMsg());
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}

					@Override
					public void onFailure(int statusCode, Header[] headers,
							byte[] responseBody, Throwable error) {
						T.ss("网络错误:" + error.getMessage());
					}
				});
	}
	
	UmengUpdateListener updateListener = new UmengUpdateListener() {

		@Override
		public void onUpdateReturned(int updateStatus, UpdateResponse updateInfo) {
			// TODO Auto-generated method stub
			switch (updateStatus) {
			case UpdateStatus.Yes: // has update
				UmengUpdateAgent.showUpdateDialog(MoreActivity.this, updateInfo);
				break;
			case UpdateStatus.No: // has no update
				Toast.makeText(MoreActivity.this,
						getResources().getString(R.string.no_updating),
						Toast.LENGTH_SHORT).show();
				break;
			case UpdateStatus.Timeout: // time out
				Toast.makeText(MoreActivity.this,
						getResources().getString(R.string.update_timeout),
						Toast.LENGTH_SHORT).show();
				break;
			}
		}
	};

	/**
	 * 弹出客服电话对话框
	 */
	private void dialTelephone() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(getResources().getString(R.string.app_name));

		builder.setMessage("服务热线:400-0898-114");
		builder.setPositiveButton("拨打客服电话",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
						call();
					}
				});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();

			}
		});
		AlertDialog dialog = builder.create();
		dialog.show();
	}

	/**
	 * 进入拨打电话页面
	 */
	private void call() {
		Intent it = new Intent();
		it.setAction("android.intent.action.DIAL");
		it.setData(Uri.parse("tel:4000898114"));
		startActivity(it);
	}

	/**
	 * 意见反馈
	 */
	private void feedback() {
		Intent data = new Intent(Intent.ACTION_SENDTO);
		data.setType("text/plain");
		data.setData(Uri.parse("cqahjkj@ahjkj.com"));
		data.putExtra(Intent.EXTRA_SUBJECT, "用户：" + userName + " 意见反馈");
		data.putExtra(Intent.EXTRA_TEXT, "请输入反馈意见内容");
		data.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		try {
			startActivity(data);
		} catch (Exception e) {
			Toast.makeText(this, "客户端没有安装邮件", Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * 安全退出
	 */
	private void goQuit() {
		AlertDialog.Builder builder = new AlertDialog.Builder(MoreActivity.this);
		builder.setMessage("您确定要退出登录吗？");
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
				MoreActivity.this.onBackPressed();
				AppManager.getAppManager().finishAllActivity();
				AppManager.getAppManager().AppExit(MoreActivity.this);
				finish();
				Intent intent = new Intent(MoreActivity.this,
						LoginActivity.class);
				startActivity(intent);
			}
		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();

			}
		});
		AlertDialog dialog = builder.create();
		dialog.show();
	}

}
