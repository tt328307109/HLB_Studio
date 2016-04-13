package com.lk.qf.pay.aanewactivity.creditcard;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;

import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lk.bhb.pay.R;
import com.lk.qf.pay.aanewactivity.FuzzyQueryActivity;
import com.lk.qf.pay.activity.BaseActivity;
import com.lk.qf.pay.activity.ProtocolActivity;
import com.lk.qf.pay.activity.swing.SwingHXCardActivity;
import com.lk.qf.pay.beans.PosData;
import com.lk.qf.pay.beans.Xinyongkainfo;
import com.lk.qf.pay.golbal.Actions;
import com.lk.qf.pay.golbal.MApplication;
import com.lk.qf.pay.golbal.MyUrls;
import com.lk.qf.pay.indiana.activity.EditAddressActivity;
import com.lk.qf.pay.sharedpref.SharedPrefConstant;
import com.lk.qf.pay.tool.T;
import com.lk.qf.pay.utils.AmountUtils;
import com.lk.qf.pay.utils.MyMdFivePassword;
import com.lk.qf.pay.utils.StringUtils;
import com.lk.qf.pay.v50.PayByV50CardActivity;
import com.lk.qf.pay.wedget.CommonTitleBar;
import com.lk.qf.pay.wedget.customdialog.ActionSheetDialog;
import com.lk.qf.pay.wedget.customdialog.AlertDialog;
import com.lk.qf.pay.wedget.customdialog.ActionSheetDialog.OnSheetItemClickListener;
import com.lk.qf.pay.wedget.customdialog.ActionSheetDialog.SheetItemColor;
import com.lk.qf.pay.wedget.view.DialogWidget;
import com.lk.qf.pay.wedget.view.PassWdDialog;
import com.lk.qf.pay.wedget.view.PayListener;
import com.lk.qf.pay.wedget.view.PayPasswordView;
import com.lk.qf.pay.wedget.view.PayPasswordView.OnPayListener;
import com.lk.qf.pay.zxb.ZXBDeviceListActivity;

public class ApplyRepaymentDetailActivity extends BaseActivity implements
		OnClickListener  , PayListener{

	/**
	 * bankName = 开户行 zhangDanDate = 账单日 pro = 省 ； city = 市
	 */
	private TextView bankName, zhangDanDate, pro, city, serviceAgreement,
			ed_Name, ed_PhoneNum, ed_CardNum;
	/**
	 * 还款日
	 */
	TextView tv_applyCard_zhangdanDate2;
	/**
	 * addr = 详细地址 ； count = 还款期数 ； pwd = 密码 ； money = 还款金额
	 */
	/**
	 * 付款对话框
	 */
	public PassWdDialog mPassWdDialog;
	private EditText addr, count, money;
	private String pwdNum = "";
	private Xinyongkainfo info;
	private String provinceID = "110000", provinceString = "北京市",
			cityString = "市辖区";
	private CommonTitleBar title;
	private Button commit;
	private DialogWidget mDialogWidget;
	private Context context;
	private String countt = "";
	private String payMeney = "";
	private String id = "", mm;
	private CheckBox cb;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.apply_repayment_layout);
		Intent intent = getIntent();
		info = (Xinyongkainfo) intent.getParcelableExtra("info");
		title = (CommonTitleBar) findViewById(R.id.titlebar_apply);
		title.setActName("申请贷款");
		title.setCanClickDestory(this, true);
		init();
	}

	private void init() {
		findViewById(R.id.tv_add_creditCard_agree).setOnClickListener(this);
		context = ApplyRepaymentDetailActivity.this;
		commit = (Button) findViewById(R.id.btn_applyCard_tijiao);
		commit.setOnClickListener(this);
		ed_Name = (TextView) findViewById(R.id.ed_applyCard_Name);

		ed_PhoneNum = (TextView) findViewById(R.id.ed_applyCard_phoneNum);
		ed_CardNum = (TextView) findViewById(R.id.ed_applyCard_bankCardNum);

		bankName = (TextView) findViewById(R.id.tv_applyCard_bankName);
		zhangDanDate = (TextView) findViewById(R.id.tv_applyCard_zhangdanDate);
		pro = (TextView) findViewById(R.id.tv_applyCard_addressPro);
		city = (TextView) findViewById(R.id.tv_applyCard_addressCity);
		addr = (EditText) findViewById(R.id.ed_applyCard_address);
		pro.setOnClickListener(this);
		city.setOnClickListener(this);
		count = (EditText) findViewById(R.id.ed_ransom);
		money = (EditText) findViewById(R.id.ed_ransom_money);
		tv_applyCard_zhangdanDate2 = (TextView) findViewById(R.id.tv_applyCard_zhangdanDate2);
		tv_applyCard_zhangdanDate2.setText(info.getRefundDay() + " 天");
		ed_Name.setText(info.getUserName());
		ed_PhoneNum.setText(info.getPhoneNum());
		ed_CardNum.setText(info.getCardNum());
		bankName.setText(info.getBankName());
		zhangDanDate.setText("每月 " + info.getBilltime() + " 日");
		cb = (CheckBox) findViewById(R.id.cb_add_creditCard_agree);
//		String inMoney = StringUtils.moneyFormat(info.getBankmoney(), 4);
		String inMoney = info.getBankmoney();
		money.setText("" + inMoney);
		money.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				String str = money.getText().toString();
				if (str.length() == 0) {
					return;
				}
				if (Double.parseDouble(str) > Double.parseDouble(info
						.getBankmoney())) {
					money.setText(info.getBankmoney());
					money.setSelection(money.getText().toString().length());
					T.ss("贷款金额大于信用卡额度！");
				}
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {

			}

			@Override
			public void afterTextChanged(Editable arg0) {

			}
		});

	}

	/**
	 * 申请还款
	 */
	private void applyRepayment() {
//		if (!cb.isChecked()) {
//			T.ss("请勾选同意服务协议");
//			return;
//		}
		RequestParams params = new RequestParams();
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("username", MApplication.mSharedPref
				.getSharePrefString(SharedPrefConstant.USERNAME));
		map.put("token", MApplication.mSharedPref
				.getSharePrefString(SharedPrefConstant.TOKEN));
		map.put("id", info.getId());
		if (money.getText().toString().length() < 1) {
			T.ss("请输入贷款金额");
			return;
		} else {
			map.put("refund_money", money.getText().toString());
		}

		showLoadingDialog();
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
		String url = MyUrls.ROOT_URL_REFUNDAPPLY;

		httpUtils.send(HttpMethod.POST, url, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						// TODO Auto-generated method stub

						T.ss("操作超时!");
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

						JSONObject obj;
						try {
							obj = new JSONObject(str);
							code = obj.optString("CODE");
							message = obj.optString("MESSAGE");
							countt = obj.optString("count");
							payMeney = obj.optString("payMeney");
							id = obj.optString("id");

						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						if (code.endsWith("00")) {
							// DecimalFormat df = new java.text.DecimalFormat(
							// "#.###");
							// double d = Double.parseDouble(payMeney);
							// mm = df.format(d);
							mm = payMeney;
//							mm = StringUtils.moneyFormat(payMeney, 4);
							setSXF(mm);
						} else {
							T.ss(message);
						}
						//
					}
				});

	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.tv_applyCard_addressCity) {// 市
			Intent intent2 = new Intent(ApplyRepaymentDetailActivity.this,
					FuzzyQueryActivity.class);
			intent2.putExtra("showType", "city");
			intent2.putExtra("provinceID", provinceID);
			startActivityForResult(intent2, EditAddressActivity.REQUEST_CITY);
		} else if (v.getId() == R.id.tv_applyCard_addressPro) {// 省
			Intent intent1 = new Intent(ApplyRepaymentDetailActivity.this,
					FuzzyQueryActivity.class);
			intent1.putExtra("showType", "province");
			startActivityForResult(intent1,
					EditAddressActivity.REQUEST_PROVINCE);
		} else if (v.getId() == R.id.btn_applyCard_tijiao) {
			applyRepayment();
		} else if (v.getId() == R.id.tv_add_creditCard_agree) {
			Intent it = new Intent(this, ProtocolActivity.class);
			it.putExtra("title", "信用卡贷款服务协议");
			startActivity(it);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {
		case EditAddressActivity.REQUEST_PROVINCE:
			if (resultCode == Activity.RESULT_OK) {
				provinceString = data.getStringExtra("bankName");
				provinceID = data.getStringExtra("bankId");
				// Log.i("result", "----------province---s--" + provinceString);
				if (provinceString.length() > 5) {
					String strName = provinceString.substring(0, 5);
					Log.i("result", "------hang1---" + strName);
					pro.setText(strName + "...");
				} else {
					// Log.i("result", "----------province--s-s--" +
					// provinceString);
					pro.setText(provinceString);
				}
			}
			break;
		case EditAddressActivity.REQUEST_CITY:
			if (resultCode == Activity.RESULT_OK) {
				cityString = data.getStringExtra("bankName");
				// Log.i("result", "----------citys--" + city);
				if (city.length() > 10) {
					String strName = cityString.substring(0, 5);
					// Log.i("result", "------hacity--" + strName);
					city.setText(strName + "...");
				} else {
					// Log.i("result", "------hcity-zh--" + cityString);
					city.setText(cityString);
				}
			}
			break;

		default:
			break;
		}
	}



	/**
	 * 钱包支付
	 */
	private void topUp() {
		showLoadingDialog();
		RequestParams params = new RequestParams();
		HashMap<String, String> map = new HashMap<String, String>();

		map.put("username", MApplication.mSharedPref
				.getSharePrefString(SharedPrefConstant.USERNAME));
		map.put("pwd", MyMdFivePassword.MD5(MyMdFivePassword.MD5(pwdNum)));
		map.put("payMoney", payMeney);
		map.put("id", id);
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
		String url = MyUrls.ROOT_URL_PAYSXF;
		Log.i("result", "----url-----------" + url);

		httpUtils.send(HttpMethod.POST, url, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						// TODO Auto-generated method stub
						Log.i("result", "--------------failure------------");
						dismissLoadingDialog();
						T.ss("操作超时!");
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
								T.ss("付款成功");
								Intent intent = new Intent(
										ApplyRepaymentDetailActivity.this,
										CreditCardsListActivity.class);
								startActivity(intent);
								finish();
							} else {
								T.ss(message);
							}

						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				});
	}

	/**
	 * 提示手续费
	 */
	private void setSXF(String money) {

		new AlertDialog(context).builder().setTitle("提示")
				.setMsg("手续费:￥" + money)
				.setPositiveButton("支付", new OnClickListener() {
					@Override
					public void onClick(View v) {
						nextStep();
					}
				}).setNegativeButton("取消", new OnClickListener() {
					@Override
					public void onClick(View v) {

					}
				}).show();
	}

	private void nextStep() {
		new ActionSheetDialog(context)
				.builder()
				.setTitle("请选择支付途径")
				.setCancelable(false)
				.setCanceledOnTouchOutside(true)
				.addSheetItem("钱包余额", SheetItemColor.Blue,
						new OnSheetItemClickListener() {
							@Override
							public void onClick(int which) {
//								mDialogWidget = new DialogWidget(
//										ApplyRepaymentDetailActivity.this,
//										getDecorViewDialog("手续费:￥", mm));
//								mDialogWidget.show();
								
								//弹出对话框
								mPassWdDialog = PassWdDialog.getInstance(ApplyRepaymentDetailActivity.this ,
									mm	,PassWdDialog.YUAN_MARK);
								mPassWdDialog.setPayListener(ApplyRepaymentDetailActivity.this);
								mPassWdDialog.show();
							}
						})
				.addSheetItem("刷卡", SheetItemColor.Blue,
						new OnSheetItemClickListener() {
							@Override
							public void onClick(int which) {
								nextStep2();
							}
						}).show();
	}

	private void nextStep2() {
		PosData.getPosData().setPayType("08");
		PosData.getPosData().setPrdordNo(id);// 还款订单id
		PosData.getPosData().setPayAmt(AmountUtils.changeY2F(mm));// 金额
		new ActionSheetDialog(context)
				.builder()
				.setTitle("请选择刷卡器类型")
				.setCancelable(false)
				.setCanceledOnTouchOutside(true)
				.addSheetItem("音频刷卡器", SheetItemColor.Blue,
						new OnSheetItemClickListener() {
							@Override
							public void onClick(int which) {

								startActivity(new Intent(context,
										SwingHXCardActivity.class)
										.setAction(Actions.ACTION_CASHIN));
							}
						})
				.addSheetItem("蓝牙刷卡器", SheetItemColor.Blue,
						new OnSheetItemClickListener() {
							@Override
							public void onClick(int which) {
								Intent it = new Intent(context,
										ZXBDeviceListActivity.class);
								it.setAction(Actions.ACTION_CASHIN);
								startActivity(it);
							}
						})

				.addSheetItem("键盘蓝牙刷卡器", SheetItemColor.Blue,
						new OnSheetItemClickListener() {
							@Override
							public void onClick(int which) {
								startActivity(new Intent(context,
										PayByV50CardActivity.class)
										.setAction(Actions.ACTION_CASHIN));
							}
						}).show();
	}

	@Override
	public void sure(String pwd) {
		mPassWdDialog.dismiss();
		mPassWdDialog = null;
		// payTextView.setText(password);
		pwdNum = pwd;
		topUp();
	}

	@Override
	public void cacel() {
		mPassWdDialog.dismiss();
		mPassWdDialog = null;
	}

	@Override
	public void close() {
		
	}
}
