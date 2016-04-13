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
import com.lk.qf.pay.aanewactivity.creditcard.ApplyRepaymentDetailActivity;
import com.lk.qf.pay.aanewactivity.licai.LicaiNewActivity;
import com.lk.qf.pay.activity.swing.SwingHXCardActivity;
import com.lk.qf.pay.adapter.MyPolyLoanAdapter;
import com.lk.qf.pay.beans.PolyLoanListInfo;
import com.lk.qf.pay.beans.PosData;
import com.lk.qf.pay.golbal.Actions;
import com.lk.qf.pay.golbal.MApplication;
import com.lk.qf.pay.golbal.MyUrls;
import com.lk.qf.pay.sharedpref.SharedPrefConstant;
import com.lk.qf.pay.tool.T;
import com.lk.qf.pay.utils.MyMdFivePassword;
import com.lk.qf.pay.v50.PayByV50CardActivity;
import com.lk.qf.pay.wedget.MyClickListener;
import com.lk.qf.pay.wedget.customdialog.ActionSheetDialog;
import com.lk.qf.pay.wedget.customdialog.ActionSheetDialog.OnSheetItemClickListener;
import com.lk.qf.pay.wedget.customdialog.ActionSheetDialog.SheetItemColor;
import com.lk.qf.pay.wedget.view.DialogWidget;
import com.lk.qf.pay.wedget.view.PassWdDialog;
import com.lk.qf.pay.wedget.view.PayListener;
import com.lk.qf.pay.wedget.view.PayPasswordView;
import com.lk.qf.pay.wedget.view.PayPasswordView.OnPayListener;
import com.lk.qf.pay.zxb.ZXBDeviceListActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnWindowFocusChangeListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MyPolyLoanFragment extends ListFragment implements OnClickListener
, PayListener{

	// private PullToRefreshListView lv;
	private ListView lv;
	private List<PolyLoanListInfo> list;
	private List<PolyLoanListInfo> listReturn;
	private int page = 1;
	private int dataCount = 30;
	private Map<String, String> map;
	private PolyLoanListInfo info;
	private MyPolyLoanAdapter adapter;
	private Context context;
	private View layoutView;
	private String huantype = "";
	private String account = "", pid = "", pwd = "", licaiMoney = "";
	private DialogWidget mDialogWidget;
	private TextView tvShow;
	private Button btnBuy;
	private RelativeLayout rl;
	PassWdDialog mPassWdDialog;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		layoutView = inflater.inflate(R.layout.my_poly_loan_fragment_layout,
				container, false);
		context = getActivity();
		lv = (ListView) layoutView.findViewById(android.R.id.list);
		tvShow = (TextView) layoutView.findViewById(R.id.tv_my_polyLoan);
		btnBuy = (Button) layoutView.findViewById(R.id.btn_polyLoan_goBuy);
		btnBuy.setOnClickListener(this);
		rl = (RelativeLayout) layoutView.findViewById(R.id.rl_my_polyLoan);
		list = new ArrayList<PolyLoanListInfo>();
		adapter = new MyPolyLoanAdapter(getActivity(), list, mListener);
		setListAdapter(adapter);
		return layoutView;
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		postQueryLoanHttp();
	}

	/**
	 * listview中button的事件
	 */
	private MyClickListener mListener = new MyClickListener() {
		@Override
		public void myOnClick(int position, View v) {
			// Intent intent = new Intent(context, AddLicaiGoodsActivity.class);
			// intent.putExtra("info", list.get(position));
			// startActivity(intent);
			PolyLoanListInfo pInfo = list.get(position);
			String isClick = pInfo.getLoanType();
			if (!isClick.equals("0")) {
				return;
			}
			account = pInfo.getLoanAccount();
			pid = pInfo.getPid();
			showDialog();
		}
	};

	/**
	 * 我的贷款
	 * 
	 * @return
	 */
	private void postQueryLoanHttp() {
		// showLoadingDialog();
		RequestParams params = new RequestParams();
		String url = MyUrls.BL_LOAHLIST;

		map = new HashMap<String, String>();

		map.put("username", MApplication.mSharedPref
				.getSharePrefString(SharedPrefConstant.USERNAME));
		map.put("pageSize", "10");
		map.put("page", "" + page);

		String json = JSON.toJSONString(map);
		Log.i("result", "-------订单请求-----" + json);
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
				// dismissLoadingDialog();
				// lv.onRefreshComplete();
			}

			@Override
			public void onSuccess(ResponseInfo<String> response) {
				// TODO Auto-generated method stub
				String strReturnLogin = response.result;
				Log.i("result", "---------------定单-returnjson---"
						+ strReturnLogin);
				jsonDetail(strReturnLogin);

				String returnCode = info.getCode();

				if (returnCode.equals("00")) {
					if (listReturn == null || listReturn.size() == 0) {
						tvShow.setText("暂未申请保理贷,需购买理财后才能申请.");
						btnBuy.setClickable(true);
						btnBuy.setBackgroundResource(R.drawable.loan_huankuan_selector);
						btnBuy.setText("去购买");

					} else {
						rl.setVisibility(View.GONE);//设置隐藏
						if (page == 1) {
							list.clear();
							list = listReturn;
						} else {
							list.addAll(listReturn);
						}
						adapter.sendSata(list);
						Log.i("result", "-------sendSata-----"
								+ list.get(0).getLoanAccount());
					}
				} else {
					T.ss(info.getMessage());
				}
				// lv.onRefreshComplete();
				// dismissLoadingDialog();
			}
		});
	}

	/**
	 * 解析 Json字符串 登录返回结果
	 * 
	 * @param str
	 * @return
	 */
	private void jsonDetail(String str) {

		try {
			JSONObject obj = new JSONObject(str);
			info = new PolyLoanListInfo();
			info.setCode(obj.optString("CODE"));
			info.setMessage(obj.optString("MESSAGE"));
			listReturn = new ArrayList<PolyLoanListInfo>();
			int count = obj.optInt("count");
			if (info.getCode().equals("00")) {
				licaiMoney = obj.optString("licaimoney");
			}
			Log.i("result", "---------Count-------" + count);
			if (count > 0) {

				Log.i("result", "---------page-------" + page);
				Log.i("result", "---------dataCount-------" + dataCount);
				for (int i = 0; i < count; i++) {

					String loanAccount = obj.optJSONArray("date")
							.optJSONObject(i).optString("loanMoney");
					String loanTime = obj.optJSONArray("date").optJSONObject(i)
							.optString("addtime");
					String loanType = obj.optJSONArray("date").optJSONObject(i)
							.optString("int1");
					String loanOrderNum = obj.optJSONArray("date")
							.optJSONObject(i).optString("loan_str");
					String proid = obj.optJSONArray("date").optJSONObject(i)
							.optString("id");

					// 给info设置数据
					PolyLoanListInfo info = new PolyLoanListInfo();
					info.setLoanAccount(loanAccount);
					info.setLoanTime(loanTime);
					info.setLoanType(loanType);
					info.setPid(proid);
					info.setLoanOrderNum(loanOrderNum);
					Log.i("result", "---------list---d---" + loanAccount + "--"
							+ loanTime + "--" + loanType + "---" + proid);
					listReturn.add(info);
				}
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void showDialog() {
		new ActionSheetDialog(getActivity())
		.builder()
		.setTitle("请选择还款途径")
		.setCancelable(false)
		.setCanceledOnTouchOutside(true)
		.addSheetItem("钱包余额", SheetItemColor.Blue,
				new OnSheetItemClickListener() {
			@Override
			public void onClick(int which) {
				huantype = "yue";
				//						mDialogWidget = new DialogWidget(getActivity(),
				//								getDecorViewDialog());
				//						mDialogWidget.show();
				mPassWdDialog = PassWdDialog.getInstance(getActivity() ,
						account	, PassWdDialog.YUAN_MARK);
				mPassWdDialog.setPayListener(MyPolyLoanFragment.this);
				mPassWdDialog.show();

			}
		})

		.addSheetItem("理财余额", SheetItemColor.Blue,
				new OnSheetItemClickListener() {
			@Override
			public void onClick(int which) {
				double licai = Double.parseDouble(licaiMoney);
				double huankuanAccount = Double.parseDouble(account);
				if (licai < huankuanAccount) {
					T.ss("可用理财金额不足");
					return;
				}
				huantype = "licai";
//				mDialogWidget = new DialogWidget(getActivity(),
//						getDecorViewDialog());
//				mDialogWidget.show();
				mPassWdDialog = PassWdDialog.getInstance(getActivity() ,
						account	, PassWdDialog.YUAN_MARK);
					mPassWdDialog.setPayListener(MyPolyLoanFragment.this);
					mPassWdDialog.show();
			}
		}).show();
	}

	/**
	 * 还款
	 */
	private void applyPolyLoan() {

		RequestParams params = new RequestParams();
		String url = MyUrls.BL_HUAN;
		Map<String, String> map = new HashMap<String, String>();
		map.put("username", MApplication.mSharedPref
				.getSharePrefString(SharedPrefConstant.USERNAME));
		map.put("token", MApplication.mSharedPref
				.getSharePrefString(SharedPrefConstant.TOKEN));
		map.put("money", account);
		map.put("loahid", pid);
		map.put("huantype", huantype);// yue钱包 licai理财
		map.put("paypwd", MyMdFivePassword.MD5(MyMdFivePassword.MD5(pwd)));

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
			}

			@Override
			public void onSuccess(ResponseInfo<String> response) {
				// TODO Auto-generated method stub
				String code = "";
				String message = "";

				String str = response.result;
				Log.i("result", "----str----s-------" + str);
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
				if (code.equals("00")) {
					// tvEdu.setText(obj.optString("linesCount"));
					postQueryLoanHttp();
				}
			}
		});
	}

//	protected View getDecorViewDialog() {
//		// TODO Auto-generated method stub
//		return PayPasswordView.getInstance("¥"+account, context,
//				new OnPayListener() {
//
//			@Override
//			public void onSurePay(String password) {
//				// TODO Auto-generated method stub
//				mDialogWidget.dismiss();
//				mDialogWidget = null;
//				// payTextView.setText(password);
//				pwd = password;
//				applyPolyLoan();
//
//				// Toast.makeText(getApplicationContext(), "交易成功",
//				// Toast.LENGTH_SHORT).show();
//			}
//
//			@Override
//			public void onCancelPay() {
//				// TODO Auto-generated method stub
//				mDialogWidget.dismiss();
//				mDialogWidget = null;
//				// Toast.makeText(getApplicationContext(), "交易已取消",
//				// Toast.LENGTH_SHORT).show();
//
//			}
//
//			@Override
//			public void onClose() {
//				// TODO Auto-generated method stub
//				mDialogWidget.dismiss();
//			}
//		}).getView();
//	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		Intent intent = new Intent(getActivity(), LicaiNewActivity.class);
		startActivity(intent);
		getActivity().finish();
	}

	@Override
	public void sure(String pwd) {
		// TODO Auto-generated method stub
		mPassWdDialog.dismiss();
		mPassWdDialog = null;
		// payTextView.setText(password);
		this.pwd = pwd;
		applyPolyLoan();
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
