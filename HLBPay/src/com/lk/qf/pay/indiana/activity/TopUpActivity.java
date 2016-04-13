package com.lk.qf.pay.indiana.activity;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.alibaba.fastjson.JSON;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lk.bhb.pay.R;
import com.lk.qf.pay.aanewactivity.creditcard.ApplyRepaymentDetailActivity;
import com.lk.qf.pay.activity.BaseActivity;
import com.lk.qf.pay.golbal.MApplication;
import com.lk.qf.pay.golbal.MyUrls;
import com.lk.qf.pay.sharedpref.SharedPrefConstant;
import com.lk.qf.pay.tool.T;
import com.lk.qf.pay.utils.MyMdFivePassword;
import com.lk.qf.pay.utils.TimeUtils;
import com.lk.qf.pay.wedget.CommonTitleBarYellow;
import com.lk.qf.pay.wedget.view.DialogWidget;
import com.lk.qf.pay.wedget.view.PassWdDialog;
import com.lk.qf.pay.wedget.view.PayListener;
import com.lk.qf.pay.wedget.view.PayPasswordView;
import com.lk.qf.pay.wedget.view.PayPasswordView.OnPayListener;

public class TopUpActivity extends IndianaBaseActivity implements OnClickListener ,  PayListener{

	private RadioGroup rg1, rg2;
	private EditText edCoin;
	private CharSequence temp;
	private String coinNum = "10";
	private int coinNumber = 10, totalKCoin = 0;
	private String topUpType = "", pwd;
	private DialogWidget mDialogWidget;
	private CommonTitleBarYellow title;
	private TextView tvShow1, tvShow2, tvShow3, tvShow4, tvKCoin;
	private RelativeLayout rlKCoin;
	PassWdDialog mPassWdDialog;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.top_up_layout);
		init();
	}

	private void init() {
		edCoin = (EditText) findViewById(R.id.ed_top_up_other);
		tvKCoin = (TextView) findViewById(R.id.tv_topup_jifen);
		tvShow1 = (TextView) findViewById(R.id.tv_topup_jifen1);
		tvShow2 = (TextView) findViewById(R.id.tv_topup_jifen2);
		tvShow3 = (TextView) findViewById(R.id.tv_topup_jifen3);
		tvShow4 = (TextView) findViewById(R.id.tv_topup_jifen4);

		rlKCoin = (RelativeLayout) findViewById(R.id.rl_jifen_chongzhi);
		rg1 = (RadioGroup) findViewById(R.id.yi_group);
		rg2 = (RadioGroup) findViewById(R.id.er_group);
		findViewById(R.id.radio_10).setOnClickListener(this);
		findViewById(R.id.radio_20).setOnClickListener(this);
		findViewById(R.id.radio_50).setOnClickListener(this);
		findViewById(R.id.radio_100).setOnClickListener(this);
		findViewById(R.id.radio_200).setOnClickListener(this);
		// rg1.setOnCheckedChangeListener(this);
		// rg2.setOnCheckedChangeListener(this);
		title = (CommonTitleBarYellow) findViewById(R.id.titlebar_indiana_topUp);
		title.setActName("充值");
		title.setCanClickDestory(this, true);
		findViewById(R.id.rl_qianbao_chongzhi).setOnClickListener(this);
		findViewById(R.id.rl_jifen_chongzhi).setOnClickListener(this);

		edCoin.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View arg0, boolean arg1) {
				// TODO Auto-generated method stub
				Log.i("result", "-------------arg1------" + arg1);
				if (arg1) {// 此处为得到焦点时的处理内容
					// rg1.clearCheck();
					// rg2.clearCheck();
				} else {

				}
			}
		});

		edCoin.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				// TODO Auto-generated method stub
				temp = arg0;
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int start,
					int before, int count) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub

				if (temp.length() != 0) {
					edCoin.setBackground(getResources().getDrawable(R.drawable.chongzhi_kuang_1));
					rg1.clearCheck();
					rg2.clearCheck();
					coinNum = temp.toString();
					if (coinNum != null && !coinNum.equals("")) {
						coinNumber = Integer.parseInt(coinNum);
					}
					Log.i("result", "-------------coinNumber-tmp-----"
							+ coinNumber);
					Log.i("result", "-------------coinNum-tmp-----" + coinNum);
					if (coinNumber > totalKCoin) {
						tvShow1.setTextColor(getResources().getColor(
								R.color.lightgray));
						tvShow2.setTextColor(getResources().getColor(
								R.color.lightgray));
						tvShow3.setTextColor(getResources().getColor(
								R.color.lightgray));
						tvShow4.setTextColor(getResources().getColor(
								R.color.lightgray));
						tvKCoin.setTextColor(getResources().getColor(
								R.color.lightgray));
					} else {
						tvShow1.setTextColor(getResources().getColor(
								R.color.topup_blue));
						tvShow2.setTextColor(getResources().getColor(
								R.color.topup_blue));
						tvShow3.setTextColor(getResources().getColor(
								R.color.topup_blue));
						tvShow4.setTextColor(getResources().getColor(
								R.color.topup_red));
						tvKCoin.setTextColor(getResources().getColor(
								R.color.topup_blue));
					}
				}else{
					edCoin.setBackground(getResources().getDrawable(R.drawable.chongzhi_kuang_2));
				}
			}
		});
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		qianbao();
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.rl_qianbao_chongzhi:
			topUpType = "yue";
//			mDialogWidget = new DialogWidget(this, getDecorViewDialog("￥",coinNum));
//			mDialogWidget.show();
			//弹出对话框
			mPassWdDialog = PassWdDialog.getInstance(TopUpActivity.this ,
					coinNum	, PassWdDialog.YUAN_MARK);
			mPassWdDialog.setPayListener(TopUpActivity.this);
			mPassWdDialog.show();
			break;
		case R.id.rl_jifen_chongzhi:

			topUpType = "kuai";
			Log.i("result", "-------------coinNumber------" + coinNumber);
			Log.i("result", "-------------totalKCoin------" + totalKCoin);
			if (coinNumber > totalKCoin) {
				return;
			}
			int intCoinNum = Integer.parseInt(coinNum);
			intCoinNum = intCoinNum * 10;
//			mDialogWidget = new DialogWidget(this, getDecorViewDialog("",intCoinNum+"个快易币"));
//			mDialogWidget.show();
			//弹出对话框
			mPassWdDialog = PassWdDialog.getInstance(TopUpActivity.this ,
					intCoinNum + ""	, PassWdDialog.KUAIYIBI_MARK);
			mPassWdDialog.setPayListener(TopUpActivity.this);
			mPassWdDialog.show();
			break;

		case R.id.radio_10:
			coinNum = "10";
			rg2.clearCheck();
			rg1.check(R.id.radio_10);
			edCoin.setText("");
			edCoin.clearFocus();
			break;
		case R.id.radio_20:
			coinNum = "20";
			rg2.clearCheck();
			rg1.check(R.id.radio_20);
			edCoin.setText("");
			edCoin.clearFocus();
			break;
		case R.id.radio_50:
			coinNum = "50";
			rg2.clearCheck();
			rg1.check(R.id.radio_50);
			edCoin.setText("");
			edCoin.clearFocus();
			break;
		case R.id.radio_100:
			coinNum = "100";
			rg1.clearCheck();
			rg2.check(R.id.radio_100);
			edCoin.setText("");
			edCoin.clearFocus();
			break;
		case R.id.radio_200:
			coinNum = "200";
			rg1.clearCheck();
			rg2.check(R.id.radio_200);
			edCoin.setText("");
			edCoin.clearFocus();
			break;
		default:
			break;
		}
		if (!coinNum.equals("")) {
			coinNumber = Integer.parseInt(coinNum);
		}
		// edCoin.setText("");
		if (coinNumber > totalKCoin) {
			tvShow1.setTextColor(getResources().getColor(R.color.lightgray));
			tvShow2.setTextColor(getResources().getColor(R.color.lightgray));
			tvShow3.setTextColor(getResources().getColor(R.color.lightgray));
			tvShow4.setTextColor(getResources().getColor(R.color.lightgray));
			tvKCoin.setTextColor(getResources().getColor(R.color.lightgray));
		} else {
			tvShow1.setTextColor(getResources().getColor(R.color.topup_blue));
			tvShow2.setTextColor(getResources().getColor(R.color.topup_blue));
			tvShow3.setTextColor(getResources().getColor(R.color.topup_blue));
			tvShow4.setTextColor(getResources().getColor(R.color.topup_red));
			tvKCoin.setTextColor(getResources().getColor(R.color.topup_blue));
		}

	}

	// @Override
	// public void onCheckedChanged(RadioGroup arg0, int arg1) {
	// // TODO Auto-generated method stub
	//
	// switch (arg1) {
	// case R.id.radio_10:
	// coinNum = "10";
	// rg2.clearCheck();
	// rg1.check(R.id.radio_10);
	// edCoin.setText("");
	// edCoin.clearFocus();
	// break;
	// case R.id.radio_20:
	// coinNum = "20";
	// rg2.clearCheck();
	// rg1.check(R.id.radio_20);
	// edCoin.setText("");
	// edCoin.clearFocus();
	// break;
	// case R.id.radio_50:
	// coinNum = "50";
	// rg2.clearCheck();
	// rg1.check(R.id.radio_50);
	// edCoin.setText("");
	// edCoin.clearFocus();
	// break;
	// case R.id.radio_100:
	// coinNum = "100";
	// rg1.clearCheck();
	// rg2.check(R.id.radio_100);
	// edCoin.setText("");
	// edCoin.clearFocus();
	// break;
	// case R.id.radio_200:
	// coinNum = "200";
	// rg1.clearCheck();
	// rg2.check(R.id.radio_200);
	// edCoin.setText("");
	// edCoin.clearFocus();
	// break;
	//
	// default:
	// break;
	// }
	// Log.i("result", "-------------coinNum---coinNum---" + coinNum);
	// if (!coinNum.equals("")) {
	// coinNumber = Integer.parseInt(coinNum);
	// }
	// // edCoin.setText("");
	// if (coinNumber > totalKCoin) {
	// tvShow1.setTextColor(getResources().getColor(R.color.lightgray));
	// tvShow2.setTextColor(getResources().getColor(R.color.lightgray));
	// tvShow3.setTextColor(getResources().getColor(R.color.lightgray));
	// tvShow4.setTextColor(getResources().getColor(R.color.lightgray));
	// tvKCoin.setTextColor(getResources().getColor(R.color.lightgray));
	// } else {
	// tvShow1.setTextColor(getResources().getColor(R.color.topup_blue));
	// tvShow2.setTextColor(getResources().getColor(R.color.topup_blue));
	// tvShow3.setTextColor(getResources().getColor(R.color.topup_blue));
	// tvShow4.setTextColor(getResources().getColor(R.color.topup_red));
	// tvKCoin.setTextColor(getResources().getColor(R.color.topup_blue));
	// }
	// }

	protected View getDecorViewDialog(String str,String coinNum) {
		// TODO Auto-generated method stub
		return PayPasswordView.getInstance(str + coinNum, this,
				new OnPayListener() {

					@Override
					public void onSurePay(String password) {
						// TODO Auto-generated method stub
						mDialogWidget.dismiss();
						mDialogWidget = null;
						// payTextView.setText(password);
						pwd = password;
						topUp();
					}

					@Override
					public void onCancelPay() {
						// TODO Auto-generated method stub
						mDialogWidget.dismiss();
						mDialogWidget = null;
					}

					@Override
					public void onClose() {
						// TODO Auto-generated method stub
						mDialogWidget.dismiss();
					}
				}).getView();
	}

	private void topUp() {
		showLoadingDialog();
		RequestParams params = new RequestParams();
		HashMap<String, String> map = new HashMap<String, String>();

		map.put("username", MApplication.mSharedPref
				.getSharePrefString(SharedPrefConstant.USERNAME));
		map.put("paypwd", MyMdFivePassword.MD5(MyMdFivePassword.MD5(pwd)));
		map.put("money", coinNum);
		map.put("type", topUpType);
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
		String url = MyUrls.ROOT_URL_DBB_EXCHANGE;
		Log.i("result", "----url-----------" + url);

		httpUtils.send(HttpMethod.POST, url, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						// TODO Auto-generated method stub
						Log.i("result", "--------------failure------------");
						dismissLoadingDialog();
						T.ss("操作超时");
					}

					@Override
					public void onSuccess(ResponseInfo<String> response) {
						// TODO Auto-generated method stub
						dismissLoadingDialog();
						String str = response.result;
						String code = "";
						String message = "";
						Log.i("result", "----ddd-----------" + str);

						JSONObject obj;
						try {
							obj = new JSONObject(str);
							code = obj.optString("CODE");
							message = obj.optString("MESSAGE");
							if (code.equals("00")) {
								T.ss("充值成功");
								finish();
							} else {
								T.ss(message);
							}

						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});
	}

	/**
	 * 获取余额
	 */
	private void qianbao() {
		RequestParams params = new RequestParams();
		String url = MyUrls.TXMONEY;
		Map<String, String> map = new HashMap<String, String>();
		map.put("username", MApplication.mSharedPref
				.getSharePrefString(SharedPrefConstant.USERNAME));
		map.put("pwd", MApplication.mSharedPref
				.getSharePrefString(SharedPrefConstant.PASSWORD));
		map.put("token", MApplication.mSharedPref
				.getSharePrefString(SharedPrefConstant.TOKEN));

		String json = JSON.toJSONString(map);
		Log.i("result", "----ddd----s-------" + json);
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
				dismissLoadingDialog();
				// T.ss("获取数据失败,错误码:" + arg0.getExceptionCode());
			}

			@Override
			public void onSuccess(ResponseInfo<String> response) {
				// TODO Auto-generated method stub
				String code = "";
				String message = "";

				String str = response.result;
				JSONObject obj = null;
				// Log.i("result", "----qianbao----s-------" + str);
				try {
					obj = new JSONObject(str);
					code = obj.optString("CODE");
					message = obj.optString("MESSAGE");

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				if (code.equals("00")) {
					if (obj != null) {
						totalKCoin = obj.optInt("k_use");
						tvKCoin.setText("" + totalKCoin);
						if (coinNumber > totalKCoin) {
							tvShow1.setTextColor(getResources().getColor(
									R.color.lightgray));
							tvShow2.setTextColor(getResources().getColor(
									R.color.lightgray));
							tvShow3.setTextColor(getResources().getColor(
									R.color.lightgray));
							tvShow4.setTextColor(getResources().getColor(
									R.color.lightgray));
							tvKCoin.setTextColor(getResources().getColor(
									R.color.lightgray));
						} else {
							tvShow1.setTextColor(getResources().getColor(
									R.color.topup_blue));
							tvShow2.setTextColor(getResources().getColor(
									R.color.topup_blue));
							tvShow3.setTextColor(getResources().getColor(
									R.color.topup_blue));
							tvShow4.setTextColor(getResources().getColor(
									R.color.topup_red));
							tvKCoin.setTextColor(getResources().getColor(
									R.color.topup_blue));
						}
					}
				} else {
					T.ss(message);
				}
			}
		});
	}

	@Override
	public void sure(String pwd) {
		// TODO Auto-generated method stub
		mPassWdDialog.dismiss();
		mPassWdDialog = null;
		// payTextView.setText(password);
		this.pwd = pwd;
		topUp();
	}

	@Override
	public void cacel() {
		// TODO Auto-generated method stub
		mPassWdDialog.dismiss();
		mPassWdDialog = null;
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub
		
	}

}
