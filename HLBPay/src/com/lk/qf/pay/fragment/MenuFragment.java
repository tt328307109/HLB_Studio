package com.lk.qf.pay.fragment;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import com.alibaba.fastjson.JSON;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lk.bhb.pay.R;
import com.lk.qf.pay.aanewactivity.AddDaiLiMerchantActivity;
import com.lk.qf.pay.aanewactivity.IncomeGuanliActivity;
import com.lk.qf.pay.aanewactivity.JiaoYiGuanLiNewActivity;
import com.lk.qf.pay.aanewactivity.JieSuanActivity;
import com.lk.qf.pay.aanewactivity.KeFuActivity;
import com.lk.qf.pay.aanewactivity.PoPhotoActivity;
import com.lk.qf.pay.aanewactivity.QianBaoActivity;
import com.lk.qf.pay.aanewactivity.UserInformationActivity;
import com.lk.qf.pay.aanewactivity.UserManagementActivity;
import com.lk.qf.pay.aanewactivity.UserManagementFGSActivity;
import com.lk.qf.pay.aanewactivity.XinyongkaGuanliActivity;
import com.lk.qf.pay.aanewactivity.creditcard.CreditCardsListActivity;
import com.lk.qf.pay.aanewactivity.licai.LicaiNewActivity;
import com.lk.qf.pay.activity.CashInActivity;
import com.lk.qf.pay.activity.FindPwdActivity;
import com.lk.qf.pay.activity.LoginActivity;
import com.lk.qf.pay.golbal.MApplication;
import com.lk.qf.pay.golbal.MyUrls;
import com.lk.qf.pay.indiana.activity.IndianaMainActivity;
import com.lk.qf.pay.indiana.activity.IndianaProtocolActivity;
import com.lk.qf.pay.indiana.activity.SignInActivity;
import com.lk.qf.pay.indiana.activity.WinnerRecordActivity;
import com.lk.qf.pay.posloan.PosLoanActivity;
import com.lk.qf.pay.sharedpref.SharedPrefConstant;
import com.lk.qf.pay.tool.T;
import com.lk.qf.pay.wedget.customdialog.AlertDialog;
import com.lk.qf.pay.wedget.scrollview.ScrollForeverTextView;
import com.lk.qf.pay.wedget.view.AutoPagerView;
import com.squareup.picasso.Picasso;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MenuFragment extends BaseFragment implements OnClickListener {

	private View layoutView;
	private int[] ids = new int[] { R.id.menu_2, R.id.menu_3, R.id.menu_4,
			R.id.menu_6, R.id.menu_7, R.id.menu_10, R.id.menu_11, R.id.menu_12,
			R.id.menu_14, R.id.menu_shoukuan, R.id.menu_null1, R.id.menu_null2 };
	private Context mContext;
	private String state = "", cont, tuihuiMessage = "", userType = "",
			loanState = "", loanNote = "", usesort = "";
	private TextView tvTG;
	private ScrollForeverTextView tvCont;
	private RelativeLayout rl, rlBackground, rlUser;
	private String isSetPwd = "", isWin, isSign;
	private AutoPagerView pagerView;
	private String isFirstinIndiana = "0";
	private List<String> adList;
	/**
	 * 查询轮播图code
	 */
	private String code1 = "";
	/**
	 * 查询轮播图message1
	 */
	private String message1 = "";

	public static BaseFragment newInstance() {
		BaseFragment fragment = new MenuFragment();
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		adList = new ArrayList<String>();
		layoutView = inflater.inflate(R.layout.fragment_menu, container, false);
		queryNotify();
		Display display = getActivity().getWindowManager().getDefaultDisplay();
		int mScreenWidth = display.getWidth();

		for (int i = 0; i < ids.length; i++) {
			layoutView.findViewById(ids[i]).setOnClickListener(this);
			LayoutParams para = layoutView.findViewById(ids[i])
					.getLayoutParams();
			para.width = (mScreenWidth - 2) / 3;
			para.height = para.width;
			layoutView.findViewById(ids[i]).setLayoutParams(para);
		}
		// imaUser = (ImageView) layoutView.findViewById(R.id.img_main_user);
		// tvUserType = (TextView) layoutView.findViewById(R.id.tv_main_user);
		tvTG = (TextView) layoutView.findViewById(R.id.tv_tt);
		tvCont = (ScrollForeverTextView) layoutView
				.findViewById(R.id.tv_main_tonggao);
		rl = (RelativeLayout) layoutView.findViewById(R.id.rl_tonggao);
		rlBackground = (RelativeLayout) layoutView
				.findViewById(R.id.rl_menu_title);
		// layoutView.findViewById(R.id.tv_main_saoyisao).setOnClickListener(this);//
		// 扫一扫
		// layoutView.findViewById(R.id.tv_main_fukuan).setOnClickListener(this);//
		// 付款
		// layoutView.findViewById(R.id.tv_main_fujin).setOnClickListener(this);//
		// 附近
		layoutView.findViewById(R.id.tv_meun_qiandao).setOnClickListener(this);
		layoutView.findViewById(R.id.img_meun_mine).setOnClickListener(this);
		mContext = getActivity();
		// getAdUrl();

		loanNote = MApplication.mSharedPref
				.getSharePrefString(SharedPrefConstant.LOANNOTE);
		loanState = MApplication.mSharedPref
				.getSharePrefString(SharedPrefConstant.LOANSTATE);
		isWin = MApplication.mSharedPref
				.getSharePrefString(SharedPrefConstant.ISWIN);
		isSign = MApplication.mSharedPref
				.getSharePrefString(SharedPrefConstant.ISSIGN);

		if (loanState.equals("retu")) {
			showLoanState(loanNote);
		}

		isSetPwd = MApplication.mSharedPref
				.getSharePrefString(SharedPrefConstant.ISPWD);

		getData();
		getAdList();

		if (isSetPwd.equals("False")) {
			isSetPwd();
		}
		if (isSign.equals("False")) {
			isSign();
		}
		if (isWin.equals("True")) {
			isWin();
		}
		// initGallery();

		return layoutView;
	}

	OnTouchListener onTouch = new OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {

			return true;
		}
	};

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		getUser();
		userType = MApplication.mSharedPref
				.getSharePrefString(SharedPrefConstant.AGLEVEL);
		isFirstinIndiana = MApplication.mSharedPref
				.getSharePrefString(SharedPrefConstant.ISFIRSTININDIANA);
		tuihuiMessage = MApplication.mSharedPref
				.getSharePrefString(SharedPrefConstant.POS_NOTEURL);
		state = MApplication.mSharedPref
				.getSharePrefString(SharedPrefConstant.STATE);
		usesort = MApplication.mSharedPref
				.getSharePrefString(SharedPrefConstant.USESORT);

		if (state.equals("retu")) {
			showMessage(tuihuiMessage);
		}
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		// case R.id.tv_main_saoyisao:// 扫一扫
		// if (getData()) {
		// return;
		// }
		// if (showUserState()) {
		// return;
		// }
		// Intent intentSS = new Intent(mContext, MipcaActivityCapture.class);
		// intentSS.setAction(Actions.ACTION_ZHUANZHANG);
		// startActivity(intentSS);
		// break;
		// case R.id.tv_main_fukuan:// 付款码
		// if (getData()) {
		// return;
		// }
		// if (showUserState()) {
		// return;
		// }
		// Intent intentSK = new Intent(mContext, CreatePayCodeActivity.class);
		// intentSK.setAction(Actions.ACTION_ZHUANZHANG);
		// startActivity(intentSK);
		// break;
		//
		case R.id.img_meun_mine:// 个人信息
			if (getData()) {
				return;
			}
			if (showUserState()) {
				return;
			}
			Intent intent1 = new Intent(mContext, UserInformationActivity.class);
			startActivity(intent1);
			break;
		// case R.id.menu_1:// 钱包
		// if (getData()) {
		// return;
		// }
		// if (showUserState()) {
		// return;
		// }
		// Intent intent2 = new Intent(mContext, QianBaoActivity.class);
		// startActivity(intent2);
		// break;

		case R.id.menu_14:// 用户管理
			if (getData()) {
				return;
			}
			if (showUserState()) {
				return;
			}
			Intent intent5 = null;
			Log.i("result", "-----------userType------" + userType);
			if (userType.equals("分公司")) {
				intent5 = new Intent(getActivity(),
						UserManagementFGSActivity.class);
			} else {

				intent5 = new Intent(getActivity(),
						UserManagementActivity.class);
			}
			startActivity(intent5);

			break;
		case R.id.menu_2:// 理财
			if (getData()) {
				return;
			}
			if (showUserState()) {
				return;
			}
			Intent intent6 = new Intent(mContext, LicaiNewActivity.class);
			startActivity(intent6);
			break;
		case R.id.menu_3:// 即刷即到
			if (getData()) {
				return;
			}
			if (showUserState()) {
				return;
			}
			// if (usesort.equals("0")) {
			// T.ss("商户未缴纳押金");
			// return;
			// }
			Intent intent3 = new Intent(mContext, CashInActivity.class);
			startActivity(intent3);
			break;
		case R.id.menu_6:// 收益管理
			if (getData()) {
				return;
			}
			if (showUserState()) {
				return;
			} else {
				Intent intentSY = new Intent(mContext,
						IncomeGuanliActivity.class);
				startActivity(intentSY);
			}
			break;
		case R.id.menu_10:// 夺宝
			// T.ss("建设中");
			if (getData()) {
				return;
			}
			if (showUserState()) {
				return;
			}
			if (!isFirstinIndiana.equals("1")) {
				Intent intent = new Intent(getActivity(),
						IndianaProtocolActivity.class);
				startActivity(intent);
				return;
			}
			Intent intent8 = new Intent(mContext, IndianaMainActivity.class);
			startActivity(intent8);
			break;

		case R.id.menu_7:// 交易管理
			if (getData()) {
				return;
			}
			if (showUserState()) {
				return;
			}
			Intent intentJY = new Intent(mContext,
					JiaoYiGuanLiNewActivity.class);
			startActivity(intentJY);
			// Intent intent = new Intent(getActivity(),
			// ShiMingChaXunActivity.class);
			// startActivity(intent);
			break;
		case R.id.menu_11:// pos贷
			if (getData()) {
				return;
			}
			if (showUserState()) {
				return;
			}
			Intent intent11 = new Intent(getActivity(), PosLoanActivity.class);
			startActivity(intent11);
			break;
		case R.id.menu_4:// 信用卡管理
			if (getData()) {
				return;
			}
			if (showUserState()) {
				return;
			}
			Intent intent12 = new Intent(getActivity(),
					XinyongkaGuanliActivity.class);
			startActivity(intent12);

			break;
		// case R.id.menu_5:// 信用卡代还
		// Intent intent13 = new Intent(getActivity(),
		// CreditCardsListActivity.class);
		// startActivity(intent13);
		// break;
		case R.id.menu_12:// 客服服务

			Intent intent14 = new Intent(getActivity(), KeFuActivity.class);
			startActivity(intent14);
			break;

		case R.id.tv_meun_qiandao:// 签到

			Intent intentQiandao = new Intent(getActivity(),
					SignInActivity.class);
			startActivity(intentQiandao);
			break;
		case R.id.menu_shoukuan://收款
			
			Intent intentShouKuan = new Intent(getActivity(),
					CashInFragmentActivity.class);
			startActivity(intentShouKuan);
			break;
		default:
			return;
		}
	}

	/**
	 * 显示商户状态
	 */
	private boolean showUserState() {
		boolean is = false;
		Log.i("result", "---state-------" + state);
		if (!state.equals("en")) {
			T.ss("商户未审核");
			is = true;
		} else if (state.equals("retu")) {
			showMessage(tuihuiMessage);
			is = true;
		}
		// else if (usesort.equals("0")) {
		// T.ss("商户未缴纳押金");
		// is = true;
		// }
		return is;

	}

	private boolean getData() {
		String strShiming = "";
		String strJiesuan = "";
		String strPhoto = "";
		boolean is = false;
		strShiming = MApplication.mSharedPref
				.getSharePrefString(SharedPrefConstant.MERENTERPRISERADD);
		strJiesuan = MApplication.mSharedPref
				.getSharePrefString(SharedPrefConstant.MERBANKADD);
		strPhoto = MApplication.mSharedPref
				.getSharePrefString(SharedPrefConstant.MERCERTIFICATEADD);
		if (strShiming.equals("0")) {
			showUser(0, "您尚未进行实名认证,是否认证?");
			is = true;
		} else if (strJiesuan.equals("0")) {

			showUser(1, "您尚未填写结算信息,是否填写?");
			is = true;
		} else if (strPhoto.equals("0")) {

			showUser(2, "您尚未上传证件照,是否上传?");
			is = true;
		}
		return is;
	}

	private void getUser() {

		RequestParams params = new RequestParams();
		HashMap<String, String> map = new HashMap<String, String>();

		map.put("username", MApplication.mSharedPref
				.getSharePrefString(SharedPrefConstant.USERNAME));
		map.put("pwd", MApplication.mSharedPref
				.getSharePrefString(SharedPrefConstant.PASSWORD));
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
		String url = MyUrls.TXMONEY;

		httpUtils.send(HttpMethod.POST, url, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						// TODO Auto-generated method stub

						// T.ss("发送失败!" + arg0.getExceptionCode());
					}

					@Override
					public void onSuccess(ResponseInfo<String> response) {
						// TODO Auto-generated method stub

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

								MApplication.mSharedPref.putSharePrefString(
										SharedPrefConstant.POSUSE,
										obj.optString("posuse"));// 账户余额
								MApplication.mSharedPref.putSharePrefString(
										SharedPrefConstant.BEIFUJIN,
										obj.optString("zijinchi"));// 备付金
								MApplication.mSharedPref.putSharePrefString(
										SharedPrefConstant.USER_BANK_NUM,
										obj.optString("combankcarnum"));
								MApplication.mSharedPref.putSharePrefString(
										SharedPrefConstant.USER_BANK_NAME,
										obj.optString("combankname"));
								MApplication.mSharedPref.putSharePrefString(
										SharedPrefConstant.AGLEVEL,
										obj.optString("aglevel"));// 用户级别
								userType = obj.optString("aglevel");

								if (userType.equals("0")) {
									userType = "普通商户";
									// imaUser.setBackgroundResource(R.drawable.icon_yonghu);
								} else if (userType.equals("2")) {
									userType = "银牌服务商";
									// imaUser.setBackgroundResource(R.drawable.icon_yonghu_yinpai);
								} else if (userType.equals("3")) {
									userType = "金牌服务商";
									// imaUser.setBackground(getResources().getDrawable(R.drawable.icon_yonghu_jinpai));
								} else if (userType.equals("4")) {
									userType = "分公司";
									// imaUser.setBackgroundResource(R.drawable.icon_yonghu_jinpai);
								}

								// tvUserType.setText(userType);
							} else {
								T.ss(message);
							}
							// Log.i("result", "----message-----------" +
							// message);
							if (message.equals("登录超时，请重新登录!")) {
								Intent intent = new Intent(getActivity(),
										LoginActivity.class);
								startActivity(intent);
								getActivity().finish();
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});
	}

	/**
	 * 公告查询
	 */
	private void queryNotify() {

		RequestParams params = new RequestParams();
		HashMap<String, String> map = new HashMap<String, String>();

		String json = JSON.toJSONString(map);
		try {
			StringEntity bodyEntity = new StringEntity(json, "UTF-8");
			params.setBodyEntity(bodyEntity);
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		HttpUtils httpUtils = new HttpUtils();
		String url = MyUrls.NOTICE;

		httpUtils.send(HttpMethod.POST, url, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onSuccess(ResponseInfo<String> response) {
						// TODO Auto-generated method stub

						String str = response.result;
						Log.i("result", "----ddd-----------" + str);
						String code = "";
						String message = "";
						String cont = "";
						String title = "";
						int count;

						JSONObject obj;
						try {
							obj = new JSONObject(str);
							count = obj.optInt("count");
							String[] strTitle = new String[count];
							String[] strCont = new String[count];
							StringBuffer sb = new StringBuffer();
							for (int i = 0; i < count; i++) {

								title = obj.optJSONArray("date")
										.optJSONObject(i).optString("title");
								cont = obj.optJSONArray("date")
										.optJSONObject(i).optString("cont");
								sb.append(cont);
								// strTitle[i]=title;
								// strCont[i]=cont;
							}
							if (sb.length() != 0) {
								tvTG.setText("通告:");
								tvCont.setText("" + sb);
								// rlBackground.setBackground(getResources()
								// .getDrawable(R.drawable.menu_title));
								// rl.setBackground(getResources().getDrawable(
								// R.drawable.bg_tonggao));

							}
							Log.i("result", "----ddd---sb--------" + sb);
							// MApplication.mSharedPref.putSharePrefString(
							// SharedPrefConstant.CONT, cont);
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						//
					}
				});
	}

	/**
	 * 提示信息
	 */
	private void showUser(int stpe, String msg) {
		final int code = stpe;
		new AlertDialog(getActivity()).builder().setTitle("提示").setMsg(msg)
				.setPositiveButton("确定", new OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent intent = null;
						if (code == 0) {

							intent = new Intent(mContext,
									AddDaiLiMerchantActivity.class);

						} else if (code == 1) {
							intent = new Intent(mContext, JieSuanActivity.class);
						} else if (code == 2) {
							intent = new Intent(mContext, PoPhotoActivity.class);
						}
						intent.putExtra("type", "0");
						intent.putExtra(
								"userName",
								MApplication.mSharedPref
										.getSharePrefString(SharedPrefConstant.USERNAME));
						startActivity(intent);
					}
				}).setNegativeButton("取消", new OnClickListener() {
					@Override
					public void onClick(View v) {

					}
				}).show();
	}

	/**
	 * 提示退回信息
	 */
	private void showMessage(String message) {
		String str = "";
		if (message.length() != 0) {
			str = "退回理由:";
		}

		new AlertDialog(getActivity()).builder().setTitle("提示")
				.setMsg("您的信息已被退回,是否重新填写?" + "\n\n" + str + message)
				.setPositiveButton("确定", new OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent intent = new Intent(getActivity(),
								AddDaiLiMerchantActivity.class);
						intent.putExtra("type", "0");
						intent.putExtra(
								"userName",
								MApplication.mSharedPref
										.getSharePrefString(SharedPrefConstant.USERNAME));
						startActivity(intent);
					}
				}).setNegativeButton("取消", new OnClickListener() {
					@Override
					public void onClick(View v) {

					}
				}).show();
	}

	/**
	 * 提示是否设置支付密码
	 */
	private void isSetPwd() {

		new AlertDialog(getActivity()).builder().setTitle("提示")
				.setMsg("您尚未设置支付密码,是否马上设置?")
				.setPositiveButton("确定", new OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent intent = new Intent(getActivity(),
								FindPwdActivity.class);
						intent.setAction(FindPwdActivity.ACTION_SET_PAY_PWD);
						startActivity(intent);
					}
				}).setNegativeButton("取消", new OnClickListener() {
					@Override
					public void onClick(View v) {

					}
				}).show();
	}

	/**
	 * 提示是否中奖
	 */
	private void isWin() {

		new AlertDialog(getActivity()).builder().setTitle("提示")
				.setMsg("恭喜您在快夺宝中有新的中奖记录!")
				.setPositiveButton("马上查看", new OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent intent = new Intent(getActivity(),
								WinnerRecordActivity.class);
						startActivity(intent);
					}
				}).setNegativeButton("知道了", new OnClickListener() {
					@Override
					public void onClick(View v) {

					}
				}).show();
	}

	/**
	 * 提示是否中奖
	 */
	private void isSign() {

		new AlertDialog(getActivity()).builder().setTitle("提示")
				.setMsg("您今日还未签到,是否立即签到?")
				.setPositiveButton("马上签到", new OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent intent = new Intent(getActivity(),
								SignInActivity.class);
						startActivity(intent);
					}
				}).setNegativeButton("知道了", new OnClickListener() {
					@Override
					public void onClick(View v) {

					}
				}).show();
	}

	/**
	 * 提示pos贷状态
	 */
	private void showLoanState(String message) {
		String str = "";
		if (message.length() != 0) {
			str = "退回理由:";
		}

		new AlertDialog(getActivity()).builder().setTitle("提示")
				.setMsg("您申请的贷款已被退回" + "\n\n" + str + message)
				.setPositiveButton("确定", new OnClickListener() {
					@Override
					public void onClick(View v) {

					}
				}).show();
	}

	

	/**
	 * 初始化viewPager
	 */
	private void initGallery() {
		List<View> pagePic = new ArrayList<View>();
		for (int i = 0; i < adList.size(); i++) {
			ImageView imageview = new ImageView(getActivity());

			Picasso.with(getActivity()).load(MyUrls.ROOT_URL2 + adList.get(i))
					.fit().into(imageview);

			// 把List保存到sharedPref中
			SharedPreferences sp = getActivity().getSharedPreferences(
					"VPList_Menue", Context.MODE_PRIVATE);
			SharedPreferences.Editor editor = sp.edit();
			editor.clear().commit();
			editor.putInt("Status_size", adList.size());
			for (int j = 0; j < adList.size(); j++) {
				// editor.remove("Status_" + i);
				editor.putString("Status_" + j, adList.get(j));
			}
			editor.commit();
			pagePic.add(imageview);
		}
		pagerView = (AutoPagerView) layoutView
				.findViewById(R.id.auto_pagerview_shouye);
		pagerView.setPageViewPics(pagePic);
		pagerView.setOnTouchListener(onTouch);
	}

	/**
	 * 联网失败的时候这样初始化vp
	 */
	private void initGallery2() {
		List<View> pagePic = new ArrayList<View>();
		for (int i = 0; i < adList.size(); i++) {
			ImageView imageview = new ImageView(getActivity());
			Picasso.with(getActivity()).load(MyUrls.ROOT_URL2 + adList.get(i))
					.fit().into(imageview);

			pagePic.add(imageview);
		}
		pagerView = (AutoPagerView) layoutView
				.findViewById(R.id.auto_pagerview_shouye);
		pagerView.setPageViewPics(pagePic);
		pagerView.setOnTouchListener(onTouch);
	}

	/**
	 * 解析 Json字符串 登录返回结果
	 * 
	 * @param str
	 * @return
	 */
	private void jsonDetailVp(String str) {

		try {
			JSONObject obj = new JSONObject(str);

			code1 = obj.optString("CODE");
			message1 = obj.optString("MESSAGE");
			Log.i("result", "---------22-------");
			// adList = new ArrayList<String>();
			int count = obj.optInt("count");
			if (count > 0) {
				for (int i = 0; i < count; i++) {
					String imgUrl1 = obj.optJSONArray("date").optJSONObject(i)
							.optString("image1");// 图片
					String imgUrl2 = obj.optJSONArray("date").optJSONObject(i)
							.optString("image2");// 图片
					String imgUrl3 = obj.optJSONArray("date").optJSONObject(i)
							.optString("image3");// 图片
					String imgUrl4 = obj.optJSONArray("date").optJSONObject(i)
							.optString("image4");// 图片
					String imgUrl5 = obj.optJSONArray("date").optJSONObject(i)
							.optString("image5");// 图片
					String imgUrl6 = obj.optJSONArray("date").optJSONObject(i)
							.optString("image6");// 图片

					String note = obj.optJSONArray("date").optJSONObject(i)
							.optString("note");// note
					String type = obj.optJSONArray("date").optJSONObject(i)
							.optString("type");// type

					String id = obj.optJSONArray("date").optJSONObject(i)
							.optString("id");// id
					if (imgUrl1.length() > 0) {
						adList.add(imgUrl1);
					}
					if (imgUrl2.length() > 0) {
						adList.add(imgUrl2);
					}
					if (imgUrl3.length() > 0) {
						adList.add(imgUrl3);
					}
					if (imgUrl4.length() > 0) {
						adList.add(imgUrl4);
					}
					if (imgUrl5.length() > 0) {
						adList.add(imgUrl5);
					}
					if (imgUrl6.length() > 0) {
						adList.add(imgUrl6);
					}
					initGallery();
				}
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void getAdList() {
		Map<String, String> map = new HashMap<String, String>();
		RequestParams params = new RequestParams();
		String url = MyUrls.VPLIST;
		map = new HashMap<String, String>();
		map.put("type", "01");
		String json = JSON.toJSONString(map);
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
				// T.ss("查询失败");
				SharedPreferences sp = getActivity().getSharedPreferences(
						"VPList_Menue", Context.MODE_PRIVATE);
				adList.clear();
				int size = sp.getInt("Status_size", 0);
				if (size != 0) {
					for (int i = 0; i < size; i++) {
						adList.add(sp.getString("Status_" + i, null));
					}
					initGallery2();
				}
			}

			@Override
			public void onSuccess(ResponseInfo<String> response) {
				// TODO Auto-generated method stub
				String strReturnLogin = response.result;
				Log.i("result", "---------------定单-returnjson---"
						+ strReturnLogin);
				jsonDetailVp(strReturnLogin);
				Log.i("result", "---------------11---");
				if (code1.equals("00")) {
				} else {
					T.ss(message1);
				}
			}
		});

	}

}
