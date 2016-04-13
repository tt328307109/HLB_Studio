package com.lk.qf.pay.indiana.fragment;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lk.bhb.pay.R;
import com.lk.qf.pay.aanewactivity.QianBaoActivity;
import com.lk.qf.pay.activity.LoginActivity;
import com.lk.qf.pay.fragment.BaseFragment;
import com.lk.qf.pay.golbal.MApplication;
import com.lk.qf.pay.golbal.MyUrls;
import com.lk.qf.pay.indiana.activity.CommonProblemActivity;
import com.lk.qf.pay.indiana.activity.IndianaRecordListActivity;
import com.lk.qf.pay.indiana.activity.MyShippingAddressActivity;
import com.lk.qf.pay.indiana.activity.TopUpActivity;
import com.lk.qf.pay.indiana.activity.WinnerRecordActivity;
import com.lk.qf.pay.indiana.adapter.MyAddressAdapter;
import com.lk.qf.pay.indiana.saidan.SaiDanAfterActivity;
import com.lk.qf.pay.sharedpref.SharedPrefConstant;
import com.lk.qf.pay.tool.SystemBarTintManager;
import com.lk.qf.pay.tool.T;
import com.lk.qf.pay.utils.MyGetStatusUtils;

public class MyIndianaFragment extends BaseFragment {

	private View layoutView;
	private TextView tvPhoneNum, tvIndianaCoins, tvShowAddressType;
	private String userPhoneNum, isAddress;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		layoutView = inflater.inflate(R.layout.indiana_my_record_mine_layout,
				container, false);
		init();
		return layoutView;
	}

	@Override
	public void onResume() {
		super.onResume();
		qianbao();
	}

	private void init() {
		tvPhoneNum = (TextView) layoutView
				.findViewById(R.id.tv_indiana_mine_phoneNum);
		tvIndianaCoins = (TextView) layoutView
				.findViewById(R.id.tv_indiana_mine_coinNum);
		tvShowAddressType = (TextView) layoutView
				.findViewById(R.id.tv_indiana_mine_isHaveAddr);
		layoutView.findViewById(R.id.btn_indiana_mine_topUp)
				.setOnClickListener(clickListener);
		layoutView.findViewById(R.id.rl_indiana_mine_address)
				.setOnClickListener(clickListener);
		layoutView.findViewById(R.id.rl_indiana_mine_indianaRecord)
				.setOnClickListener(clickListener);
		layoutView.findViewById(R.id.rl_indiana_mine_winnerRecord)
				.setOnClickListener(clickListener);
		layoutView.findViewById(R.id.rl_indiana_mine_myShow)
				.setOnClickListener(clickListener);
		layoutView.findViewById(R.id.rl_indiana_mine_CommonProblems)
				.setOnClickListener(clickListener);

		userPhoneNum = MApplication.mSharedPref
				.getSharePrefString(SharedPrefConstant.USERNAME);
		tvPhoneNum.setText(userPhoneNum.substring(0, 3)
				+ "****"
				+ userPhoneNum.substring(userPhoneNum.length() - 4,
						userPhoneNum.length()));

		if (isStop) {

		}
		// 沉浸式状态栏
//		SystemBarTintManager tintManager = new SystemBarTintManager(
//				getActivity());
//		tintManager.setStatusBarTintEnabled(true);
//		tintManager.setStatusBarTintResource(R.color.titleBar_yellow);
		layoutView.findViewById(R.id.ll_indiana_my).setPadding(0,
				MyGetStatusUtils.getStatusBarHeight(getActivity()), 0, 0);
		// tvShowAddressType.setText("");
	}

	OnClickListener clickListener = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			switch (arg0.getId()) {
			case R.id.rl_indiana_mine_address:// 我的地址
				Intent intent = new Intent(getActivity(),
						MyShippingAddressActivity.class);
				startActivity(intent);
				break;
			case R.id.rl_indiana_mine_indianaRecord:// 夺宝记录
				Intent intent1 = new Intent(getActivity(),
						IndianaRecordListActivity.class);
				startActivity(intent1);
				break;
			case R.id.rl_indiana_mine_winnerRecord:// 中奖记录
				Intent intent2 = new Intent(getActivity(),
						WinnerRecordActivity.class);
				startActivity(intent2);
				break;
			case R.id.rl_indiana_mine_myShow:// 我的晒单
				Intent intent4 = new Intent(getActivity(),
						SaiDanAfterActivity.class);
				startActivity(intent4);
				break;
			case R.id.btn_indiana_mine_topUp:// 充值
				Intent intent3 = new Intent(getActivity(), TopUpActivity.class);
				startActivity(intent3);
				break;
			case R.id.rl_indiana_mine_CommonProblems:// 常见问题
				Intent intent5 = new Intent(getActivity(),
						CommonProblemActivity.class);
				startActivity(intent5);
				break;

			default:
				break;
			}
		}
	};

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

				T.ss("操作超时");
			}

			@Override
			public void onSuccess(ResponseInfo<String> response) {
				// TODO Auto-generated method stub
				String code = "";
				String message = "";

				String str = response.result;
				JSONObject obj = null;
				Log.i("result", "----qianbao----s-------" + str);
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
						tvIndianaCoins.setText(obj.optString("snause"));
					}
				} else {
					T.ss(message);
				}
			}
		});
	}

}
