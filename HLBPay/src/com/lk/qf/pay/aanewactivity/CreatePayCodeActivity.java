package com.lk.qf.pay.aanewactivity;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
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
import com.lk.qf.pay.golbal.Actions;
import com.lk.qf.pay.golbal.MApplication;
import com.lk.qf.pay.golbal.MyUrls;
import com.lk.qf.pay.sharedpref.SharedPrefConstant;
import com.lk.qf.pay.tool.T;
import com.lk.qf.pay.utils.AmountUtils;
import com.lk.qf.pay.utils.CreatePayCodeUtils;
import com.lk.qf.pay.wedget.CommonTitleBar;
import com.lk.qf.pay.wedget.customdialog.AlertDialog;

public class CreatePayCodeActivity extends BaseActivity implements
		OnClickListener {

	private static int QR_WIDTH = 0;
	private ImageView imgPayCode;
	private TextView tvAccount, tvType, tvQueryType;
	private String account = "0", phone = "", beizhu = "扫码转账";
	private static final int REQUEST_PAY_CODE = 0;// 钱包转账
	private static final int REQUEST_PAY_CODE_ZZ = 1;// 易付宝
	private CommonTitleBar title;
	private String strTitle = "收款码", action = "";
	private Button btnSetAccount;
	private String appSign, returnUrl;
	private Bitmap bitmap;
	private String url, queryUrl, outTradeNo = "";
	private boolean isShow = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.create_pay_code_layout);
		init();
	}

	private void init() {
		imgPayCode = (ImageView) findViewById(R.id.img_pay_code);
		tvAccount = (TextView) findViewById(R.id.tv_create_pay_code_account);
		tvType = (TextView) findViewById(R.id.tv_pay_code_type);
		btnSetAccount = (Button) findViewById(R.id.btn_pay_code_setAccount);
		title = (CommonTitleBar) findViewById(R.id.titlebar_create_pay_code);
		tvQueryType = (TextView) findViewById(R.id.tv_pay_code_hd);
		tvQueryType.setOnClickListener(this);
		WindowManager wm = this.getWindowManager();
		QR_WIDTH = wm.getDefaultDisplay().getWidth() / 5 * 4;

		btnSetAccount.setOnClickListener(this);
		title.setActName(strTitle);
		title.getBtn_back().setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (!action.equals(Actions.ACTION_ZHUANZHANG)) {
					if (account != null && !account.equals("")) {
						queryPayType(false);
					} else {
						finish();
					}
				} else {
					finish();
				}
			}
		});
		phone = MApplication.mSharedPref
				.getSharePrefString(SharedPrefConstant.USERNAME);
		Intent intent = getIntent();
		if (intent != null) {
			action = intent.getAction();
			account = intent.getStringExtra("account");
		}

//		Log.i("result", "-------onResume----------" + url);
		if (account == null || account.equals("")) {
			tvQueryType.setVisibility(View.GONE);
			tvAccount.setVisibility(View.GONE);
			tvType.setText("快易付app下载");
			bitmap = CreatePayCodeUtils.createQRImage(
					getResources().getString(R.string.download_add), QR_WIDTH);
			imgPayCode.setImageBitmap(bitmap);
		} else {
			setUrl();
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

	}

	/**
	 * 设置url 获取二维码
	 */
	private void setUrl() {
		if (action.equals(Actions.ACTION_ZHUANZHANG)) {// 钱包转账
			tvQueryType.setVisibility(View.VISIBLE);
			tvAccount.setText("￥" + account);
			tvAccount.setVisibility(View.VISIBLE);
			tvType.setText("钱包转账付款码");
			bitmap = CreatePayCodeUtils.toJsonPayCode(phone, account, beizhu,
					"1", QR_WIDTH);
		} else {

			if (action.equals(Actions.ACTION_WEIXIN)) {// 微信
				url = MyUrls.WEIXIN_NATIVEPAYPAGE;
				queryUrl = MyUrls.WEIXIN_ORDERPAYPAGE;
			} else if (action.equals(Actions.ACTION_ZHIFUBAO)) {// 支付宝
				url = MyUrls.ZHIFUBAO_NATIVEPAYPAGE;
				queryUrl = MyUrls.ZHIFUBAO_ORDERPAYPAGE;
			} else if (action.equals(Actions.ACTION_YIFUBAO)) {// 易付宝
				url = MyUrls.YFB_CREATECODE;
				queryUrl = MyUrls.QUERYORDER;
			} else if (action.equals(Actions.ACTION_BAIDU)) {// 百度钱包
				url = MyUrls.BAIDU_GETCODE;
				queryUrl = MyUrls.BAIDU_QUERYORDER;
			}
			getYfbPayCode();
		}
		imgPayCode.setImageBitmap(bitmap);
	}
	
	/**
	 * 当请求网络成功后  获取二维码地址后  设置显示金额 和对应的type
	 */
	private void setShowAccount(){
		tvQueryType.setVisibility(View.VISIBLE);
		tvAccount.setText("￥" + account);
		tvAccount.setVisibility(View.VISIBLE);
		
		if (action.equals(Actions.ACTION_WEIXIN)) {// 微信
			tvType.setText("微信付款码");
		} else if (action.equals(Actions.ACTION_ZHIFUBAO)) {// 支付宝
			tvType.setText("支付宝付款码");
		} else if (action.equals(Actions.ACTION_YIFUBAO)) {// 易付宝
			tvType.setText("易付宝付款码");
		} else if (action.equals(Actions.ACTION_BAIDU)) {// 百度钱包
			tvType.setText("百付宝付款码");
		}
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.btn_pay_code_setAccount:
			Intent intent = new Intent(CreatePayCodeActivity.this,
					CreatePaytCodeAccountActivity.class);
			intent.setAction(action);
			intent.putExtra("type", false);
			if (action.equals(Actions.ACTION_ZHUANZHANG)) {
				startActivityForResult(intent, REQUEST_PAY_CODE);
			} else {
				startActivityForResult(intent, REQUEST_PAY_CODE_ZZ);
			}
			break;
		case R.id.tv_pay_code_hd:
			queryPayType(true);
			break;

		default:
			break;
		}
	}

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		// TODO Auto-generated method stub
		super.onActivityResult(arg0, arg1, arg2);
		switch (arg0) {
		case REQUEST_PAY_CODE:
			if (arg1 == Activity.RESULT_OK) {
				account = arg2.getStringExtra("account");
				tvAccount.setText("￥" + account);
				tvAccount.setVisibility(View.VISIBLE);
				tvType.setText("钱包转账付款码");
				beizhu = arg2.getStringExtra("beizhu");
				Log.i("result", "--------account-1---" + account);
				bitmap = CreatePayCodeUtils.toJsonPayCode(phone, account,
						beizhu, "1", QR_WIDTH);
				imgPayCode.setImageBitmap(bitmap);
			}
			break;
		case REQUEST_PAY_CODE_ZZ:
			if (arg1 == Activity.RESULT_OK) {
				account = arg2.getStringExtra("account");
//				tvAccount.setText("￥" + account);
				beizhu = arg2.getStringExtra("beizhu");
				setUrl();
				Log.i("result", "--------account--s--" + account);
			}
			break;

		default:
			break;
		}
	}

	/**
	 * 获取二维码
	 */
	private void getYfbPayCode() {

		showLoadingDialog();
		String money = AmountUtils.changeY2F(account);
		String[] str = { "phoneNum=" + phone, "OrderAmount=" + money };
		appSign = CreatePayCodeUtils.createSign(str);// 签名
		RequestParams params = new RequestParams();
		Map<String, String> map = new HashMap<String, String>();
		map.put("phoneNum", phone);
		map.put("appSign", appSign);
		map.put("OrderAmount", money);
		String json = JSON.toJSONString(map);
		Log.i("result", "--------------json-----------" + json);
		Log.i("result", "--------------url-----------" + url);
		try {
			StringEntity bodyEntity = new StringEntity(json, "UTF-8");
			params.setBodyEntity(bodyEntity);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		HttpUtils utils = new HttpUtils();
		utils.send(HttpMethod.POST, url, params, new RequestCallBack<String>() {

			@Override
			public void onFailure(HttpException arg0, String arg1) {
				T.ss("操作超时");
				dismissLoadingDialog();
			}

			@Override
			public void onSuccess(ResponseInfo<String> response) {
				// TODO Auto-generated method stub
				String code = "";
				String message = "";
				String str = response.result;
				Log.i("result", "----获取----s-------" + str);
				JSONObject obj = null;
				try {
					obj = new JSONObject(str);
					code = obj.optString("Code");
					message = obj.optString("Message");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				if (code.equals("0028")) {
					returnUrl = obj.optString("shortUrl");
					bitmap = CreatePayCodeUtils.createQRImage(returnUrl,
							QR_WIDTH);
					imgPayCode.setImageBitmap(bitmap);
					outTradeNo = obj.optString("outTradeNo");
					setShowAccount();
				} else {
					T.ss(message);
				}
				dismissLoadingDialog();
			}
		});
	}

	/**
	 * 查询支付状态
	 */
	private void queryPayType(boolean isShowDialog) {
		isShow = isShowDialog;
		showLoadingDialog();
		String[] str = { "phoneNum=" + phone, "OutTradeNo=" + outTradeNo };
		appSign = CreatePayCodeUtils.createSign(str);// 签名
		RequestParams params = new RequestParams();
		Map<String, String> map = new HashMap<String, String>();
		map.put("phoneNum", phone);
		map.put("appSign", appSign);
		map.put("OutTradeNo", outTradeNo);
		String json = JSON.toJSONString(map);
		Log.i("result", "--------------json-----------" + json);
		Log.i("result", "--------------url-----------" + queryUrl);
		try {
			StringEntity bodyEntity = new StringEntity(json, "UTF-8");
			params.setBodyEntity(bodyEntity);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		HttpUtils utils = new HttpUtils();
		utils.send(HttpMethod.POST, queryUrl, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						if (isShow) {
							showMsg("操作超时", "");
						} else {
							finish();
						}
						dismissLoadingDialog();
					}

					@Override
					public void onSuccess(ResponseInfo<String> response) {
						// TODO Auto-generated method stub
						String code = "";
						String message = "";
						String str = response.result;
						Log.i("result", "----获取----s-------" + str);
						JSONObject obj = null;
						try {
							obj = new JSONObject(str);
							code = obj.optString("Code");
							message = obj.optString("Message");
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						dismissLoadingDialog();
						if (isShow) {
							showMsg("支付状态", message);
						} else {
							finish();
						}
					}
				});
	}

	/**
	 * 显示交易信息状态
	 */
	private void showMsg(String title, String message) {

		new AlertDialog(CreatePayCodeActivity.this).builder().setTitle(title)
				.setMsg(message).setPositiveButton("确认", new OnClickListener() {
					@Override
					public void onClick(View v) {

					}
				}).show();
	}
}
