package com.lk.qf.pay.aanewactivity;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import com.alibaba.fastjson.JSON;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lk.bhb.pay.R;
import com.lk.qf.pay.activity.BaseActivity;
import com.lk.qf.pay.activity.swing.SwingHXCardActivity;
import com.lk.qf.pay.beans.PosData;
import com.lk.qf.pay.golbal.Actions;
import com.lk.qf.pay.golbal.MApplication;
import com.lk.qf.pay.golbal.MyUrls;
import com.lk.qf.pay.sharedpref.SharedPrefConstant;
import com.lk.qf.pay.tool.T;
import com.lk.qf.pay.utils.AmountUtils;
import com.lk.qf.pay.v50.PayByV50CardActivity;
import com.lk.qf.pay.wedget.CommonTitleBar;
import com.lk.qf.pay.wedget.customdialog.ActionSheetDialog;
import com.lk.qf.pay.wedget.customdialog.ActionSheetDialog.OnSheetItemClickListener;
import com.lk.qf.pay.wedget.customdialog.ActionSheetDialog.SheetItemColor;
import com.lk.qf.pay.zxb.ZXBDeviceListActivity;

public class UpgradeUserActivity extends BaseActivity implements
		OnClickListener {

	private TextView tvRules, tvRules2;
	private Button btnUp, btnZhijieUp;
	private String action = "";
	private CommonTitleBar title;
	AudioManager localAudioManager;
	private String amt = "", userType = "", normalToSliver = "",
			sliverToGolden = "", normalToGolden = "", payType = "",
			uplevel = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.upgrade_user_layout);
		init();

	}

	private void init() {
		title = (CommonTitleBar) findViewById(R.id.titlebar_upgrade_user_layout);
		title.setCanClickDestory(this, true);
		Intent intent = getIntent();
		tvRules = (TextView) findViewById(R.id.tv_upRules_1);
		tvRules2 = (TextView) findViewById(R.id.tv_upRules_2);
		findViewById(R.id.btn_upgrade).setOnClickListener(this);
		findViewById(R.id.btn_upgrade_zhijie).setOnClickListener(this);
		userType = MApplication.mSharedPref
				.getSharePrefString(SharedPrefConstant.AGLEVEL);
		Log.i("result", "----userType-----------" + userType);
		localAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		if (intent != null) {
			action = intent.getAction();
			if (action.equals("gold")) {
				uplevel = "3";
				title.setActName("升级金牌");
				tvRules.setText("发展一级金牌用户达到10个,可升级为金牌合作商.");
				tvRules2.setText("普通商户缴纳20000元加盟费,银牌加盟商缴纳15000万元加盟费");
			} else {
				uplevel = "2";
				title.setActName("升级银牌");
				tvRules.setText("发展一级银牌用户达到10个,可升级为银牌加盟商.");
				tvRules2.setText("普通商户缴纳10000元加盟费.");
			}

		}

		queryUpAccount();
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub

		if (action.equals("gold")) {
			title.setActName("升级金牌");
			if (userType.equals("3")) {
				T.ss("您已是金牌合作商");
				return;
			}
		} else {
			title.setActName("升级银牌");
			if (userType.equals("2")) {
				T.ss("您已是银牌合作商");
				return;
			} else if (userType.equals("3")) {
				T.ss("您已是金牌合作商");
				return;
			}
		}
		if (amt.equals("")) {
			T.ss("暂不能升级,请稍后重试.");
			return;
		}
		switch (arg0.getId()) {
		case R.id.btn_upgrade:
			bindDevice();
			break;
		case R.id.btn_upgrade_zhijie:
			upgradeMer();
			break;

		default:
			break;
		}
	}

	private void bindDevice() {
		new ActionSheetDialog(UpgradeUserActivity.this)
				.builder()
				.setTitle("请选择刷卡器类型")
				.setCancelable(false)
				.setCanceledOnTouchOutside(true)
				.addSheetItem("音频刷卡器", SheetItemColor.Blue,
						new OnSheetItemClickListener() {
							@Override
							public void onClick(int which) {
								Intent intent1 = new Intent(
										UpgradeUserActivity.this,
										SwingHXCardActivity.class);
								if (localAudioManager.isWiredHeadsetOn()) {
									PosData.getPosData().setPayAmt(amt);
									intent1.setAction(Actions.ACTION_CASHIN);
									PosData.getPosData().setPayType(payType);
									startActivity(intent1);
								} else {
									showDialog("请插入刷卡器！");
								}
							}
						})
				.addSheetItem("蓝牙刷卡器", SheetItemColor.Blue,
						new OnSheetItemClickListener() {
							@Override
							public void onClick(int which) {
								Intent it = new Intent(
										UpgradeUserActivity.this,
										ZXBDeviceListActivity.class);
								PosData.getPosData().setPayAmt(amt);
								it.setAction(Actions.ACTION_CASHIN);
								PosData.getPosData().setPayType(payType);
								startActivity(it);
							}
						})

				.addSheetItem("键盘蓝牙刷卡器", SheetItemColor.Blue,
						new OnSheetItemClickListener() {
							@Override
							public void onClick(int which) {
								Intent it3 = new Intent(
										UpgradeUserActivity.this,
										PayByV50CardActivity.class);

								PosData.getPosData().setPayAmt(amt);
								it3.setAction(Actions.ACTION_CASHIN);
								PosData.getPosData().setPayType(payType);
								startActivity(it3);
							}
						}).show();
	}

	/**
	 * 升级查询金额
	 */
	private void queryUpAccount() {
		showLoadingDialog();
		RequestParams params = new RequestParams();
		HashMap<String, String> map = new HashMap<String, String>();

		map.put("username", MApplication.mSharedPref
				.getSharePrefString(SharedPrefConstant.USERNAME));
		map.put("token", MApplication.mSharedPref
				.getSharePrefString(SharedPrefConstant.TOKEN));

		String json = JSON.toJSONString(map);
		Log.i("result", "----ddd-----------" + json);
		try {
			StringEntity bodyEntity = new StringEntity(json, "UTF-8");
			params.setBodyEntity(bodyEntity);
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		HttpUtils httpUtils = new HttpUtils();
		String url = MyUrls.UPLEVELPARAM;

		httpUtils.send(HttpMethod.POST, url, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						// TODO Auto-generated method stub
						dismissLoadingDialog();
					}

					@Override
					public void onSuccess(ResponseInfo<String> response) {
						// TODO Auto-generated method stub
						dismissLoadingDialog();
						String str = response.result;
						Log.i("result", "----ddd-----------" + str);
						String code = "";
						String message = "";
						JSONObject obj = null;
						try {
							obj = new JSONObject(str);
							code = obj.optString("CODE");
							message = obj.optString("MESSAGE");
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						if (code.equals("00")) {
							normalToGolden = obj.optString("normal_to_golden");
							normalToSliver = obj.optString("normal_to_sliver");
							sliverToGolden = obj.optString("sliver_to_golden");
							long ntg = Long.parseLong(normalToGolden.substring(
									0, normalToGolden.lastIndexOf(".")));
							long nts = Long.parseLong(normalToSliver.substring(
									0, normalToSliver.lastIndexOf(".")));
							long stg = Long.parseLong(sliverToGolden.substring(
									0, sliverToGolden.lastIndexOf(".")));
							ntg = ntg * 100;
							nts = nts * 100;
							stg = stg * 100;
							if (action.equals("gold")) {
								tvRules2.setText("普通商户缴纳" + normalToGolden
										+ "元加盟费,银牌加盟商缴纳" + sliverToGolden
										+ "元加盟费");
								if (userType.equals("0")) {

									amt = "" + ntg;
									payType = "06";
								} else if (userType.equals("2")) {
									payType = "05";
									amt = "" + stg;

								}
							} else {
								tvRules2.setText("普通商户缴纳" + normalToSliver
										+ "元加盟费.");
								if (userType.equals("0")) {
									amt = "" + nts;
									payType = "04";
								}
							}

						} else {
							T.ss(message);
						}
					}
				});
	}

	/**
	 * 升级商户
	 */
	private void upgradeMer() {
		showLoadingDialog();
		RequestParams params = new RequestParams();
		HashMap<String, String> map = new HashMap<String, String>();

		map.put("username", MApplication.mSharedPref
				.getSharePrefString(SharedPrefConstant.USERNAME));
		map.put("token", MApplication.mSharedPref
				.getSharePrefString(SharedPrefConstant.TOKEN));
		map.put("uplevel", uplevel);

		String json = JSON.toJSONString(map);
		Log.i("result", "----ddd-----------" + json);
		try {
			StringEntity bodyEntity = new StringEntity(json, "UTF-8");
			params.setBodyEntity(bodyEntity);
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		HttpUtils httpUtils = new HttpUtils();
		String url = MyUrls.UPLEVEL;

		httpUtils.send(HttpMethod.POST, url, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						// TODO Auto-generated method stub
						T.ss("操作超时");
						dismissLoadingDialog();
					}

					@Override
					public void onSuccess(ResponseInfo<String> response) {
						// TODO Auto-generated method stub
						dismissLoadingDialog();
						String str = response.result;
						Log.i("result", "----ddd-----------" + str);
						String code = "";
						String message = "";
						JSONObject obj = null;
						try {
							obj = new JSONObject(str);
							code = obj.optString("CODE");
							message = obj.optString("MESSAGE");
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						T.ss(message);
					}
				});
	}
}
