package com.lk.qf.pay.fragment;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lk.bhb.pay.R;
import com.lk.qf.pay.aanewactivity.JiaoYiGuanLiActivity;
import com.lk.qf.pay.activity.EquListActivity;
import com.lk.qf.pay.activity.MoreActivity;
import com.lk.qf.pay.golbal.MApplication;
import com.lk.qf.pay.golbal.User;
import com.lk.qf.pay.sharedpref.SharedPrefConstant;
import com.lk.qf.pay.tool.AppManager;

public class MerchantFragment extends BaseFragment implements OnClickListener {
	public static BaseFragment newInstance() {
		BaseFragment fragment = new MerchantFragment();
		return fragment;
	}
	private View layoutView;
	private TextView tv_name, tv_account;
	private ImageView image_shuax;
	private Context mContext;
	
	private String[] terminalNo = new String[0];
	private String[] terminalType = new String[0];
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		layoutView = inflater.inflate(R.layout.fragment_merchant, container,
				false);
		initViews();
		return layoutView;
	}

	private void initViews() {
		mContext = getActivity();
		layoutView.findViewById(R.id.merchant_account_layout).setOnClickListener(this);
		layoutView.findViewById(R.id.tx_more).setOnClickListener(this);
		//layoutView.findViewById(R.id.merchant_auth_text).setOnClickListener(this);
		layoutView.findViewById(R.id.merchant_notify_text).setOnClickListener(this);
//		layoutView.findViewById(R.id.merchant_pwd_text).setOnClickListener(this);
//		layoutView.findViewById(R.id.merchant_help_text).setOnClickListener(this);
		//layoutView.findViewById(R.id.merchant_notice_text).setOnClickListener(this);
		
//		layoutView.findViewById(R.id.merchant_bankcard_modify_layout).setOnClickListener(this);
//		layoutView.findViewById(R.id.merchant_contact_layout).setOnClickListener(this);
//		layoutView.findViewById(R.id.merchant_bankcard_layout).setOnClickListener(this);
		
//		tv_cardnum = (TextView) layoutView.findViewById(R.id.merchant_bankcardnum_text);
		image_shuax =  (ImageView) layoutView.findViewById(R.id.image_shuax);
		image_shuax.setOnClickListener(this);
		tv_name = findViewById(R.id.tv_uname);
		tv_name.setText(User.uName);
		tv_account = findViewById(R.id.tv_uaccount);
		tv_account.setText(User.uAccount);
//		tv_status = (TextView) layoutView.findViewById(R.id.merchant_contact_text);
		String str = MApplication.mSharedPref.getSharePrefString(SharedPrefConstant.STATE);
		
		if (str.equals("en")) {
			
		}
//		if (User.uStatus == 0) {
//			tv_status.setText("未认证");
//		} else if (User.uStatus == 1) {
//			tv_status.setText("审核中");
//		} else if (User.uStatus == 2) {
//			tv_status.setText("已通过");
//		} else if (User.uStatus == 3) {
//			tv_status.setText("未通过");
//		}
//		if (User.cardBundingStatus == 2) {
//			tv_cardnum.setText("已绑定");
//		} else if (User.cardBundingStatus == 0) {
//			tv_cardnum.setText("未绑定");
//		} else if (User.cardBundingStatus == 1) {
//			tv_cardnum.setText("审核中");
//		} else if (User.cardBundingStatus == 3) {
//			tv_cardnum.setText("审核未通过");
//		}
		
	}
//	public void reFreshStatus(){
//		handler.sendEmptyMessageDelayed(10, 1000);
//		
//	}
//	
//	private void getUserInfo() {
//		dialog=new MyDialog(mContext);
//		dialog.setCancelable(false);
//		dialog.setText("请稍后...");
//		dialog.show();
//		HashMap<String, String> map = new HashMap<String, String>();
//		map.put("custMobile", User.uAccount);
//		MyHttpClient.post(getActivity(), Urls.GET_USER_INFO, map,
//				new AsyncHttpResponseHandler() {
//
//					@Override
//					public void onSuccess(int statusCode, Header[] headers,
//							byte[] responseBody) {
//						if (responseBody != null) {
//							Logger.json(new String(responseBody));
//							try {
//								
//								JSONObject json = new JSONObject(new String(responseBody))
//										.getJSONObject("REP_BODY");
//								if (json.getString("RSPCOD").equals("000000")) {
//									User.uName = json.optString("custName");
//									User.cardNum = json.optInt("cardNum");
//									User.termNum = json.optInt("termNum");
//									User.uStatus = json.optInt("custStatus");
//									User.cardBundingStatus=json.optInt("cardBundingStatus");
//									
//								} else {
//									showDialog(json.getString("RSPMSG"));
//								}
//								
//							} catch (JSONException e) {
//								e.printStackTrace();
//							}
//
//						}
//					}
//					 @Override
//					public void onFinish() {
//						 dialog.dismiss();
//					}
//					@Override
//					public void onFailure(int statusCode, Header[] headers,
//							byte[] responseBody, Throwable error) {
//						T.showCustomeShort(mContext, "网络错误");
//					}
//
//				});
//	}
	
//	@Override
//	public void onResume() {
//		super.onResume();
//		reFreshStatus();
//	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tx_more:
			Intent more = new Intent(mContext,MoreActivity.class);
			startActivity(more);
			break;
//		case R.id.image_shuax:
//			getUserInfo();
//			reFreshStatus();
//			break;
//		case R.id.merchant_account_layout:
//			Intent accInfo = new Intent(mContext,
//					AccountInfoActivity.class);
//			startActivity(accInfo);
//			break;
		/*case R.id.merchant_auth_text:
			if (User.cardBundingStatus != 2) {
				T.ss("请绑定银行卡通过后再操作");
				return;
			}
			if (MApplication.mApplicationContext.chechStatus())
				startActivity(new Intent(mContext,
						AccountWithdrawActivity.class));
			break;*/
		case R.id.merchant_notify_text:
			Intent equlist = new Intent(mContext,
					EquListActivity.class);
			startActivity(equlist);
			break;
//		case R.id.merchant_pwd_text:
//			Intent pwd = new Intent(mContext, AddBasicInformationActivity.class);
//			startActivity(pwd);
//			break;
		case R.id.merchant_help_text:
			Intent helpIntent = new Intent(mContext,
					JiaoYiGuanLiActivity.class);
			startActivity(helpIntent);
			break;
//		case R.id.merchant_contact_layout:
//			if (User.uStatus == 1 || User.uStatus == 2) {
//				return;
//			}
//			getActivity().startActivity(
//					new Intent(mContext,
//							RealNameAuthenticationActivity.class));
//			break;
//		case R.id.merchant_bankcard_layout:
//			if (User.cardBundingStatus == 0 || User.cardBundingStatus == 3) {
//				startActivity(new Intent(mContext, BindBankCardActivity.class).setAction("绑定银行卡"));
//				return;
//			}
//			break;
//		case R.id.merchant_bankcard_modify_layout:
//			
//			if (User.cardBundingStatus == 2) {
//				startActivity(new Intent(mContext, BindBankCardActivity.class).setAction("修改银行卡"));
//				return;
//			}else {
//				T.ss("请绑定银行卡！");
//			}
//		
//			break;
		/*case R.id.merchant_notice_text:
			startActivity(new Intent(mContext, NoticeActivity.class));
			break;*/
		default:
			break;
		}
	}
	
	
	/**
	 * 安全退出
	 */
	private void goQuit() {
		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		builder.setMessage("您确定要退出吗？");
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
				AppManager.getAppManager().AppExit(mContext);
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
	
	
	
//	Handler handler = new Handler() {
//		public void handleMessage(android.os.Message msg) {
//			if (msg.what == 10) {
//				if (User.uStatus == 0) {
//					tv_status.setText("未认证");
//				} else if (User.uStatus == 1) {
//					tv_status.setText("审核中");
//				} else if (User.uStatus == 2) {
//					tv_status.setText("已通过");
//				} else if (User.uStatus == 3) {
//					tv_status.setText("未通过");
//				}
//				if (User.cardBundingStatus == 2) {
//					tv_cardnum.setText("已绑定");
//				} else if (User.cardBundingStatus == 0) {
//					tv_cardnum.setText("未绑定");
//				} else if (User.cardBundingStatus == 1) {
//					tv_cardnum.setText("审核中");
//				}else if (User.cardBundingStatus == 3) {
//					tv_cardnum.setText("审核未通过");
//				}
//			}
//		};
//	};
	public <T extends View> T findViewById(int id) {
		return (T) layoutView.findViewById(id);
	}
}
