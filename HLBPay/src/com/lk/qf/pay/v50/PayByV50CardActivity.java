package com.lk.qf.pay.v50;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.cbapay.bean.V50ICCardBean;
import com.cbapay.bean.V50MagneticCardBean;
import com.cbapay.bean.V50S_EConsumeBean;
import com.cbapay.bean.V50S_EICCardBean;
import com.cbapay.bean.V50S_EMagneticCardBean;
import com.cbapay.main.V50SDK;
import com.cbapay.main.V50SSDK;
import com.cbapay.main.V50SSDK.CpuIdCallBack;
import com.cbapay.main.V50SSDK.InitCallBack;
import com.cbapay.main.V50SSDK.SetPinkeyCallBack;
import com.cbapay.main.V50SSDK.SwingCardCallBack;
import com.cbapay.util.Utils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lk.bhb.pay.R;
import com.lk.pay.utils.MD5Util;
import com.lk.qf.pay.activity.BaseActivity;
import com.lk.qf.pay.activity.EquAddConfirmActivity;
import com.lk.qf.pay.activity.swing.SignaturePadActivity;
import com.lk.qf.pay.beans.PosData;
import com.lk.qf.pay.golbal.Actions;
import com.lk.qf.pay.golbal.Constant;
import com.lk.qf.pay.golbal.MApplication;
import com.lk.qf.pay.golbal.MyUrls;
import com.lk.qf.pay.golbal.User;
import com.lk.qf.pay.sharedpref.SharedPrefConstant;
import com.lk.qf.pay.tool.T;
import com.lk.qf.pay.utils.AmountUtils;
import com.lk.qf.pay.utils.MyJson;
import com.lk.qf.pay.utils.MyUtilss;
import com.lk.qf.pay.utils.PinDKey;
import com.lk.qf.pay.wedget.CommonTitleBar;

public class PayByV50CardActivity extends BaseActivity {
	private String amount = "";// 充值数量
	private String action;
	private static final int REQUEST_CONNECT_DEVICE = 1;
	private CommonTitleBar title;
	private TextView cashin_account_text;// 充值钱数
	private TextView cashin_show_msg_text;// 刷卡状态
	// private boolean isInit = false;
	private String zpinkey,zpinkeyCv, termType;//2.加校验值的pinkey 3刷卡器类型 01-音频 02-蓝牙 03-V50 04-V50e
	private V50MagneticCardBean magneticCardBean;
	private V50ICCardBean icCardBean;
	private boolean isV50e;
	private LinearLayout llShowAccount;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.paybyv50_swing_layout);
		amount = PosData.getPosData().getPayAmt();
		Log.i("result", "--------amount------------" + amount);
		initData();
		initUI();
	}

	private void initUI() {
		title = (CommonTitleBar) findViewById(R.id.titlebar_swing_ldcard_v50);
		title.setCanClickDestory(this, true);
	}

	private void initData() {
		cashin_account_text = (TextView) findViewById(R.id.cashin_account_text_v50);
		cashin_account_text.setText(AmountUtils.changeFen2Yuan(amount) + "元");

		llShowAccount = (LinearLayout) findViewById(R.id.cashin_step_two_layout_v50);
		cashin_show_msg_text = (TextView) findViewById(R.id.cashin_show_msg_text_v50);
		title = (com.lk.qf.pay.wedget.CommonTitleBar) findViewById(R.id.titlebar_swing_ldcard_v50);
		action = getIntent().getAction();
		Intent serverIntent = new Intent(this, FindBluetoothActivity.class);
		if (action.equals(Actions.ACTION_CASHIN)) {
			title.setActName("刷卡支付");
			serverIntent.setAction(Actions.ACTION_CASHIN);
			startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
		} else if (action.equals(Actions.ACTION_CHECK)) {
			title.setActName("设备绑定");
			llShowAccount.setVisibility(View.GONE);
			serverIntent.setAction(Actions.ACTION_CHECK);
			startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);

		} else if (action.equals(Actions.ACTION_CHECK_1)) {// 连接设备后
			llShowAccount.setVisibility(View.GONE);
			title.setActName("设备绑定");
			initializePos();

		} else if (action.equals(Actions.ACTION_CASHIN_1)) {// 连接设备后
			title.setActName("刷卡支付");
			initializePos();

		}
		Log.i("result", "--------action------------" + action);
		// startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
	}

	/**
	 * 初始化设备
	 */
	private void initializePos() {
		showLoadingDialog();
		// 如果是v50e
		if (isV50e) {
			V50SSDK.init(new InitCallBack() {

				@Override
				public void onSuccess() {
					getSN();
				}

				@Override
				public void onFail(String arg0) {
					T.ss("初始化失败" + arg0);
				}
			}, PayByV50CardActivity.this, true, Tools.socket);
		} else {

			V50SDK.init(mHandler, PayByV50CardActivity.this, Tools.socket);
		}
		dismissLoadingDialog();
	}

	/**
	 * 获取sn号
	 */
	private void getSN() {
		// 获取SN
		V50SSDK.getSN(new CpuIdCallBack() {

			@Override
			public void onSuccess(String sn) {

				if (action.equals(Actions.ACTION_CHECK)
						|| action.equals(Actions.ACTION_CHECK_1)) {// 连接设备后 绑定
					Intent it = new Intent(PayByV50CardActivity.this,
							EquAddConfirmActivity.class);
					it.putExtra("ksn", "" + sn);
					it.putExtra("blueTootchAddress", "11");
					startActivity(it);
					finish();

				} else if (action.equals(Actions.ACTION_CASHIN)) {// 连接设备后 支付
					PosData.getPosData().setTermNo(sn);
					downloadPineky();
					Log.i("result", "-------------dddgee---------" + zpinkey);
				}
			}

			@Override
			public void onFail(String arg0) {
				showDialog(arg0);
			}
		});
	}

	/**
	 * 终端签到请求
	 * 
	 * @param context
	 * @param url
	 * @param params
	 */
	private void downloadPineky() {
		showLoadingDialog();
		// 终端签到
		HashMap<String, String> params = new HashMap<String, String>();
		// params.put("termNo", PosData.getPosData().getTermNo());

		System.out.println("---------------->"
				+ PosData.getPosData().getTermNo());
		params.put("termNo", PosData.getPosData().getTermNo());
		if (isV50e) {
			termType = "04";
		} else {
			termType = "03";
		}
		PosData.getPosData().setTermType(termType);
		params.put("termType", PosData.getPosData().getTermType());
		params.put("custId", MApplication.mSharedPref
				.getSharePrefString(SharedPrefConstant.MERCHANTID));

		if (params.containsKey("custPwd")) {
			params.put("custPwd",
					MD5Util.generatePassword(params.get("custPwd")));
		}

		if (params.containsKey("newPwd")) {
			params.put("newPwd", MD5Util.generatePassword(params.get("newPwd")));
		}
		if (params.containsKey("payPwd")) {
			params.put("payPwd", MD5Util.generatePassword(params.get("payPwd")));
		}
		params.put("sysType", Constant.SYS_TYPE);
		params.put("sysVersion", Constant.SYS_VERSIN);
		params.put("appVersion", MyUtilss.getVersion(this));
		params.put("sysTerNo", MyUtilss.getLocalIpAddress());
		params.put("txnDate", MyUtilss.getCurrentDate("yyMMdd"));
		params.put("txnTime", MyUtilss.getCurrentDate("HHmmss"));
		params.put("payType", PosData.getPosData().getPayType());
		params.put("custMobile", MApplication.mSharedPref
				.getSharePrefString(SharedPrefConstant.USERNAME));
		params.put("token", MApplication.mSharedPref
				.getSharePrefString(SharedPrefConstant.TOKEN));
		if (User.login) {
			// params.put("custId", User.uId);
			params.put("custMobile", MApplication.mSharedPref
					.getSharePrefString(SharedPrefConstant.USERNAME));
		}

		String jsonAll = MyJson.getRequest(params);

		RequestParams requestParams = new RequestParams();
		StringEntity bodyEntity;
		try {
			bodyEntity = new StringEntity(jsonAll, "UTF-8");
			requestParams.setBodyEntity(bodyEntity);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Log.i("result", "----------签到请求-----------" + jsonAll);
		// httpClient.post(MApplication.mApplicationContext, url, requestParams,
		// responseHandler);

		HttpUtils utils = new HttpUtils();
		utils.configSoTimeout(1000 * 30);
		utils.send(HttpMethod.POST, MyUrls.SIGN, requestParams,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						dismissLoadingDialog();
//						Log.i("result",
//								"-----------denglkk----------"
//										+ arg0.getExceptionCode() + "  " + arg1);
						showDialog("终端签到异常,错误码:" + arg0.getExceptionCode());
					}

					@Override
					public void onSuccess(ResponseInfo<String> response) {
						// TODO Auto-generated method stub

						String strReturnLogin = response.result;
						String code = "";
						String message = "";
						String zpincv = "";// 校验值 V50e机器使用
//						Log.i("result", "----------签到-result-------"
//								+ strReturnLogin);
						try {
							JSONObject obj = new JSONObject(strReturnLogin);
							// code=obj.optString("CODE");
							// message=obj.optString("MESSAGE");

							String rspmsg = obj.optJSONObject("REP_BODY")
									.optString("RSPMSG");
							if (rspmsg.equals("签到成功")) {
								zpinkey = obj.optJSONObject("REP_BODY")
										.optString("zpinkey");
								zpincv = obj.optJSONObject("REP_BODY")
										.optString("zpincv");
								if (isV50e) {
									zpinkeyCv = zpinkey + zpincv;
								}
//								Log.i("result", "----------签到--------"
//										+ zpinkeyCv);
								setPinkey();

							} else {
								cashin_show_msg_text.setText("设备签到失败");
								showDialog(rspmsg);
							}
						} catch (JSONException e) {

							e.printStackTrace();
						}
						// Log.i("result", "----------请求---1--------" +
						// strReturnLogin);

						dismissLoadingDialog();
					}
				});
	}

	/**
	 * 设置pinkey
	 */
	private void setPinkey() {
		if (isV50e) {
//			Log.i("result", "-----setPinkey-----zpinkeyCv---------------"+zpinkeyCv);
			V50SSDK.setPinKey(zpinkeyCv, new SetPinkeyCallBack() {

				@Override
				public void onSuccess() {
					// TODO Auto-generated method stub
					swingCard();
				}

				@Override
				public void onFail() {
					// TODO Auto-generated method stub
					showDialog("操作失败,请重新操作!");
				}
			});
		} else {
			V50SDK.setPinKey(zpinkey, mHandler);
		}

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d("result", "onActivityResult: requestCode=" + requestCode
				+ ", resultCode=" + resultCode);
		switch (requestCode) {
		case REQUEST_CONNECT_DEVICE:
//			Log.i("result", "------in---"+resultCode);
			// When DeviceListActivity returns with a device to connect
			if (resultCode == Activity.RESULT_OK) {
				Log.i("result", "------in2---");
				cashin_show_msg_text.setText("已连接MPOS设备...");
				isV50e = data.getBooleanExtra("isV50e", false);
				initializePos();
			}else{
				cashin_show_msg_text.setText("操作失败,请重新操作!");
			}
			break;
		}
	}

	/**
	 * 刷卡或插卡操作
	 */
	private void swingCard() {
		V50SSDK.swingCard(new SwingCardCallBack() {

			@Override
			public void onSuccess(V50S_EConsumeBean arg0) {
				V50S_EConsumeBean consumeBean = arg0;
				boolean b = arg0.isICCard();
				if (b) {
					Log.e("YJL", "刷IC卡成功");
					/** 刷IC卡成功 */
					cashin_show_msg_text.setText("刷卡成功,加载中...");
					showLoadingDialog();
					V50S_EICCardBean iCardBean = new V50S_EICCardBean();
					iCardBean.setCardNo(consumeBean.getCardno());
					iCardBean.setPin(consumeBean.getCiphertextPass());
					iCardBean.setTrackTwo(consumeBean.getCardTwoData());
					iCardBean.setData55(consumeBean.getIc_data55());
					iCardBean.setData23(consumeBean.getIc_data23());
					iCardBean.setExpireDate(consumeBean.getExpireDate());

					PosData.getPosData().setCardNo(iCardBean.getCardNo());
					String trackTwoIC = PinDKey.UnionDecryptData(zpinkey,
							iCardBean.getTrackTwo());
					// if (trackTwoIC.length() > 38) {
					//
					// trackTwoIC = trackTwoIC.substring(0, trackTwoIC.length()
					// - (trackTwoIC.length() - 38));
					// }
					// String trackTwooIC =
					trackTwoIC = trackTwoIC.substring(0,
							trackTwoIC.indexOf("f"));
					Log.i("result", "--------b--------");
					String trackdataIC = trackTwoIC + "f|";

					PosData.getPosData().setTrack(trackdataIC);
					PosData.getPosData().setMediaType("02");// 芯片卡
					// PosData.getPosData().setPayType("01");// 消费类型
					PosData.getPosData().setIcdata(iCardBean.getData55());// 55域
					PosData.getPosData().setPinblok(iCardBean.getPin());// 密码
					String cardNum = "";
					if (iCardBean.getData23() != null
							&& iCardBean.getData23().length() == 4) {
						cardNum = iCardBean.getData23().substring(3);
						Log.i("result", "------------ddgcaa--------" + cardNum);
					}
					PosData.getPosData().setCrdnum(cardNum);
					PosData.getPosData().setPeriod(iCardBean.getExpireDate());// 有效期
					dismissLoadingDialog();
					Intent intentToQianIC = new Intent(
							PayByV50CardActivity.this,
							SignaturePadActivity.class);
					intentToQianIC.setAction("ACTION_V50_PAY");
					startActivity(intentToQianIC);
					Log.i("result", "------------ic--------");
					finish();
				} else {
					/** 刷磁条卡成功 */
					cashin_show_msg_text.setText("刷卡成功,加载中...");
					showLoadingDialog();
					V50S_EMagneticCardBean mCardBean = new V50S_EMagneticCardBean();
					mCardBean.setCardNo(consumeBean.getCardno());
					mCardBean.setPin(consumeBean.getCiphertextPass());
					mCardBean.setExpireDate(consumeBean.getExpireDate());
					mCardBean.setTwoData(consumeBean.getCardTwoData());
					mCardBean.setThreeData(consumeBean.getCardThreeData());
					Log.i("result",
							"----------citiao----------" + "刷磁条卡成功\n卡号:"
									+ mCardBean.getCardNo() + "\n有效期:"
									+ mCardBean.getExpireDate() + "\n密码:"
									+ mCardBean.getPin() + "\n二磁道:"
									+ mCardBean.getTwoData() + "\n三磁道:"
									+ mCardBean.getThreeData());

					PosData.getPosData().setCardNo(mCardBean.getCardNo());

					String trackTwo = PinDKey.UnionDecryptData(zpinkey,
							mCardBean.getTwoData());
					Log.i("result", "-------解密---zpinkey--------" + zpinkey);

					if (trackTwo.length() > 38) {
						trackTwo = trackTwo.substring(0, trackTwo.length()
								- (trackTwo.length() - 38));
					}
					String trackThree = PinDKey.UnionDecryptData(zpinkey,
							mCardBean.getThreeData());

					if (trackThree == null) {
						trackThree = "";
					}
					String trackdata = trackTwo + "|" + trackThree;
					PosData.getPosData().setTrack(trackdata);
					PosData.getPosData().setMediaType("01");// 磁条卡
					// PosData.getPosData().setPayType("01");
					PosData.getPosData().setIcdata("");// 55域
					PosData.getPosData().setPinblok(mCardBean.getPin());// 密码
					// PosData.getPosData().setPinblok(pwd2);// 密码
					PosData.getPosData().setCrdnum("00");
					PosData.getPosData().setPeriod(mCardBean.getExpireDate());// 有效期

					dismissLoadingDialog();
					Intent intentToQian = new Intent(PayByV50CardActivity.this,
							SignaturePadActivity.class);
					intentToQian.setAction("ACTION_V50_PAY");
					startActivity(intentToQian);
					finish();
				}
			}

			@Override
			public void onFail(String arg0) {
				// TODO Auto-generated method stub
				T.ss(arg0);
				endBule();
			}

			@Override
			public void onCancel() {
				// TODO Auto-generated method stub
				T.ss("刷卡失败,请重新操作!");
				endBule();
			}
		}, amount, true);
	}

	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {

			case Utils.INIT_SUCCESS:
				/** 初始化成功 */
				dismissLoadingDialog();
				Log.i("result", "-------------获取sn号---------");
				V50SDK.getSN(mHandler);
				break;

			case Utils.INIT_FAIL:
				/** 初始化失败 */
				cashin_show_msg_text.setText("设备初始化失败" + msg.obj);
				dismissLoadingDialog();
				// endBule();
				break;

			case Utils.UPDATING:
				/** 正在更新 */
				Log.d("xuetao", "正在更新");
				cashin_show_msg_text.setText("设备正在更新");
				showLoadingDialog();
				break;

			case Utils.UPDATE_COMPLETE:
				/** 更新完毕 */
				dismissLoadingDialog();
				// Tools.showDialog("更新完毕", PayByV50CardActivity.this);
				break;

			case Utils.START_MAGNETIC_SUCCESS:
				/** 刷磁条卡成功 */
				cashin_show_msg_text.setText("刷卡成功,加载中...");
				showLoadingDialog();
				magneticCardBean = (V50MagneticCardBean) msg.obj;
				Log.i("result", "----------citiao----------" + "刷磁条卡成功\n卡号:"
						+ magneticCardBean.getCardNo() + "\n有效期:"
						+ magneticCardBean.getExpireDate() + "\n密码:"
						+ magneticCardBean.getPin() + "\n二磁道:"
						+ magneticCardBean.getTrackTwo() + "\n三磁道:"
						+ magneticCardBean.getTrackThree());

				PosData.getPosData().setCardNo(magneticCardBean.getCardNo());

				String trackTwo = PinDKey.UnionDecryptData(zpinkey,
						magneticCardBean.getTrackTwo());
				
				if (trackTwo.length() > 38) {

					trackTwo = trackTwo.substring(0, trackTwo.length()
							- (trackTwo.length() - 38));
				}
				String trackThree = PinDKey.UnionDecryptData(zpinkey,
						magneticCardBean.getTrackThree());

				if (trackThree == null) {
					trackThree = "";
				}
				String trackdata = trackTwo + "|" + trackThree;
				PosData.getPosData().setTrack(trackdata);
				PosData.getPosData().setMediaType("01");// 磁条卡
				// PosData.getPosData().setPayType("01");
				PosData.getPosData().setTermType("03");// 蓝牙
				PosData.getPosData().setIcdata("");// 55域
				PosData.getPosData().setPinblok(magneticCardBean.getPin());// 密码
				// PosData.getPosData().setPinblok(pwd2);// 密码
				PosData.getPosData().setCrdnum("00");
				PosData.getPosData()
						.setPeriod(magneticCardBean.getExpireDate());// 有效期

				dismissLoadingDialog();
				Intent intentToQian = new Intent(PayByV50CardActivity.this,
						SignaturePadActivity.class);
				intentToQian.setAction("ACTION_V50_PAY");
				startActivity(intentToQian);
				finish();
				break;

			case Utils.START_IC_SUCCESS:
				/** 插IC卡成功 */
				cashin_show_msg_text.setText("刷卡成功,加载中");
				showLoadingDialog();
				if (msg == null) {
					T.ss("读取IC卡信息失败,请重试!");
					return;
				}
				icCardBean = (V50ICCardBean) msg.obj;
				// Tools.showDialog(
				// "插IC卡成功\n卡号:" + icCardBean.getCardNo() + "\n有效期:"
				// + icCardBean.getExpireDate() + "\n密码:"
				// + icCardBean.getPin() + "\n二磁道:"
				// + icCardBean.getTrackTwo() + "\n卡片序列号:"
				// + icCardBean.getData23() + "\n55域数据:"
				// + icCardBean.getData55(), PayByV50CardActivity.this);

				PosData.getPosData().setCardNo(icCardBean.getCardNo());
				String trackTwoIC = PinDKey.UnionDecryptData(zpinkey,
						icCardBean.getTrackTwo());
				// if (trackTwoIC.length() > 38) {
				//
				// trackTwoIC = trackTwoIC.substring(0, trackTwoIC.length()
				// - (trackTwoIC.length() - 38));
				// }
				// String trackTwooIC =
				trackTwoIC = trackTwoIC.substring(0, trackTwoIC.indexOf("f"));
				Log.i("result", "--------b--------");
				String trackdataIC = trackTwoIC + "f|";

				PosData.getPosData().setTrack(trackdataIC);
				PosData.getPosData().setMediaType("02");// 芯片卡
				// PosData.getPosData().setPayType("01");// 消费类型
				PosData.getPosData().setTermType("03");// 蓝牙
				PosData.getPosData().setIcdata(icCardBean.getData55());// 55域
				PosData.getPosData().setPinblok(icCardBean.getPin());// 密码
				String cardNum = "";
				if (icCardBean.getData23() != null
						&& icCardBean.getData23().length() == 4) {
					cardNum = icCardBean.getData23().substring(3);
					Log.i("result", "------------ddgcaa--------" + cardNum);
				}
				PosData.getPosData().setCrdnum(cardNum);
				PosData.getPosData().setPeriod(icCardBean.getExpireDate());// 有效期
				dismissLoadingDialog();
				Intent intentToQianIC = new Intent(PayByV50CardActivity.this,
						SignaturePadActivity.class);
				intentToQianIC.setAction("ACTION_V50_PAY");
				startActivity(intentToQianIC);
				Log.i("result", "------------ic--------");
				finish();
				break;

			case Utils.START_POS_FAIL:
				/** 刷卡失败 */
				cashin_show_msg_text.setText("" + msg.obj);
				endBule();
				break;

			case Utils.WAITING_FOR_CARD:
				/** 等待用户刷卡 */
				T.ss("等待用户刷卡");
				break;

			case Utils.WAITING_FOR_PASSWORD:
				/** 等待用户输入密码 */
				T.ss("等待用户输入密码");
				cashin_show_msg_text.setText("等待用户输入密码...");
				break;

			case Utils.CANCEL_POS_SUCCESS:
				/** 取消刷卡成功 */
				// Tools.showDialog("取消刷卡成功", PayByV50CardActivity.this);
				endBule();
				break;

			case Utils.CANCEL_POS_FAIL:
				/** 取消刷卡失败 */
				// Tools.showDialog("取消刷卡失败", PayByV50CardActivity.this);
				endBule();
				break;

			case Utils.GET_SN_SUCCESS:
				/** 获取SN成功 */
				// Tools.showDialog("获取SN成功:" + msg.obj,
				// PayByV50CardActivity.this);
				if (action.equals(Actions.ACTION_CHECK)
						|| action.equals(Actions.ACTION_CHECK_1)) {// 连接设备后 绑定
					Intent it = new Intent(PayByV50CardActivity.this,
							EquAddConfirmActivity.class);
					it.putExtra("ksn", "" + msg.obj);
					it.putExtra("blueTootchAddress", "11");
					startActivity(it);
					finish();

				} else if (action.equals(Actions.ACTION_CASHIN)) {// 连接设备后 支付

					PosData.getPosData().setTermNo("" + msg.obj);
					Log.i("result", "----------de--------" + msg.obj);
					// isInit = true;
					downloadPineky();
					Log.i("result", "-------------dddgee---------" + zpinkey);
					// V50SDK.startPOS(mHandler, amount);// 刷卡
				}
				break;

			case Utils.GET_SN_FAIL:
				/** 获取SN失败 */
				cashin_show_msg_text.setText("" + msg.obj);
				// Tools.showDialog("获取SN失败:" + msg.obj,
				// PayByV50CardActivity.this);
				break;

			case Utils.GET_CARDNUM_SUCCESS:
				/** 读取卡号成功 */
				// Tools.showDialog("读取卡号成功:" + msg.obj,
				// PayByV50CardActivity.this);
				break;

			case Utils.GET_CARDNUM_FAIL:
				/** 读取卡号失败 */
				// Tools.showDialog("读取卡号失败:" + msg.obj,
				// PayByV50CardActivity.this);
				break;

			case Utils.GET_CARDNUM_WAIT:
				/** 获取卡号时等待用户刷卡 */
				T.ss("等待用户刷卡");
				break;
			case Utils.SETKEY_SUCCESS:
				/** 设置pinkey成功 */
				cashin_show_msg_text.setText("请刷卡/插卡...");
				V50SDK.startPOS(mHandler, amount, true);// 刷卡
				break;

			default:
				break;
			}
		};
	};

	/**
	 * 断开蓝牙连接
	 */
	private void endBule() {
		if (Tools.socket != null) {
			try {
				Tools.socket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			finish();
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Log.i("result", "----------onDestroy--");
		endBule();
	}

}
