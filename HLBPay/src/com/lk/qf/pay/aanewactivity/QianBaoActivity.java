package com.lk.qf.pay.aanewactivity;

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
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.alibaba.fastjson.JSON;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lk.bhb.pay.R;
import com.lk.qf.pay.activity.LoginActivity;
import com.lk.qf.pay.fragment.BaseFragment;
import com.lk.qf.pay.fragment.CashInFragmentActivity;
import com.lk.qf.pay.golbal.Actions;
import com.lk.qf.pay.golbal.MApplication;
import com.lk.qf.pay.golbal.MyUrls;
import com.lk.qf.pay.sharedpref.SharedPrefConstant;
import com.lk.qf.pay.tool.T;
import com.mining.app.zxing.MipcaActivityCapture;

public class QianBaoActivity extends BaseFragment implements
		OnClickListener {

	private TextView tvYue, tvJLJifen, tvKuaiyibi, tvDBbi ;
	// private EditText edTiXian, edTixianPwd;
	private String merName, xinyongdu, tixianAccount, yue, pwd, state;
//	private String typeId = "101";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

//		setContentView(R.layout.qianbao_layout);
	}
	View views;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		views = inflater.inflate(R.layout.qianbao_layout, null);
		init();
		return views;
	}
	private void init() {
		tvYue = (TextView) views.findViewById(R.id.tv_main_beifujin_account);
		tvJLJifen = (TextView) views.findViewById(R.id.tv_qianbao_jlJifen);
		tvKuaiyibi = (TextView) views.findViewById(R.id.tv_qianbao_kuaiyibi);
		tvDBbi = (TextView) views.findViewById(R.id.tv_qianbao_duibaobi);
		views.findViewById(R.id.rl_qianbao_tixian).setOnClickListener(this);// 提现
		// findViewById(R.id.rl_qianbao_jiaoyiguanli).setOnClickListener(this);//
		// 交易管理
		// findViewById(R.id.rl_qianbao_sy).setOnClickListener(this);// 收益管理
		views.findViewById(R.id.rl_qianbao_transfer).setOnClickListener(this);// 转账
		views.findViewById(R.id.common_title_back_qianbao).setOnClickListener(this);// 返回
		views.findViewById(R.id.tv_main_saoyisao).setOnClickListener(this);
		views.findViewById(R.id.tv_main_fukuan).setOnClickListener(this);
		merName = MApplication.mSharedPref
				.getSharePrefString(SharedPrefConstant.MERNAME);
//		qianbao();
		state = MApplication.mSharedPref
				.getSharePrefString(SharedPrefConstant.STATE);
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		Intent intent = null;
		switch (arg0.getId()) {

		case R.id.common_title_back_qianbao:
//			finish();
			break;
//		case R.id.rl_qianbao_chongzhi:
//			Intent i = new Intent(getActivity() ,CashInFragmentActivity.class );
//			startActivity(i);
//			break;
		case R.id.rl_qianbao_tixian:// 提现
			intent = new Intent(getActivity(),
					TixianSelectActivity.class);
			startActivity(intent);
			break;
		// case R.id.rl_qianbao_jiaoyiguanli://交易管理
		// intent = new Intent(QianBaoActivity.this,
		// JiaoYiGuanLiNewActivity.class);
		// startActivity(intent);
		// break;
		// case R.id.rl_qianbao_sy://收益查询
		// intent = new Intent(QianBaoActivity.this,
		// IncomeGuanliActivity.class);
		// startActivity(intent);
		// break;
		case R.id.rl_qianbao_transfer:// 转账
			intent = new Intent(getActivity(), TransferActivity.class);
			startActivity(intent);
			break;
		case R.id.tv_main_saoyisao:// 扫一扫
			intent = new Intent(getActivity(),
					MipcaActivityCapture.class);
			intent.setAction(Actions.ACTION_ZHUANZHANG);
			startActivity(intent);
			break;
		case R.id.tv_main_fukuan:// 付款码
			intent = new Intent(getActivity(),
					CreatePayCodeActivity.class);
			intent.setAction(Actions.ACTION_ZHUANZHANG);
			startActivity(intent);
			break;

		default:
			break;
		}

	}
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		qianbao();
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
						yue = obj.optString("posuse");// 余额
						tvYue.setText("¥" + yue);
						tvJLJifen.setText(obj.optString("inteuse"));
						tvKuaiyibi.setText(obj.optString("k_use"));
						tvDBbi.setText(obj.optString("snause"));
					}
				} else {
					T.ss(message);

					if (message.equals(getResources().getString(
							R.string.login_outtime))) {
						Intent intent = new Intent(getActivity(),
								LoginActivity.class);
						startActivity(intent);
						//finish();//-------
					}
				}
			}
		});
	}
}
