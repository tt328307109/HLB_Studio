package com.lk.qf.pay.aanewactivity.licai;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lk.bhb.pay.R;
import com.lk.qf.pay.activity.BaseActivity;
import com.lk.qf.pay.activity.ProtocolActivity;
import com.lk.qf.pay.activity.swing.SwingHXCardActivity;
import com.lk.qf.pay.beans.LicaiNewGoodsInfo;
import com.lk.qf.pay.beans.PosData;
import com.lk.qf.pay.golbal.Actions;
import com.lk.qf.pay.golbal.MApplication;
import com.lk.qf.pay.golbal.MyUrls;
import com.lk.qf.pay.sharedpref.SharedPrefConstant;
import com.lk.qf.pay.tool.T;
import com.lk.qf.pay.utils.AmountUtils;
import com.lk.qf.pay.utils.TimeUtils;
import com.lk.qf.pay.utils.MyUtilss;
import com.lk.qf.pay.v50.PayByV50CardActivity;
import com.lk.qf.pay.wedget.customdialog.ActionSheetDialog;
import com.lk.qf.pay.wedget.customdialog.ActionSheetDialog.OnSheetItemClickListener;
import com.lk.qf.pay.wedget.customdialog.ActionSheetDialog.SheetItemColor;
import com.lk.qf.pay.zxb.ZXBDeviceListActivity;

public class AddLicaiGoodsActivity extends LicaiBaseActivity implements
		OnClickListener {

	private TextView tvGoodsName, tvYearEarnings, tvQxDay, tvQgAccount,
			tvQxDate, tvDqDate;
	private EditText edAccount;
	private CheckBox cb;
	private String goodsName = "", yearEarnings = "", qxDay = "",
			qgAccount = "", qxDate = "", dqDate = "", account = "";
	private String proid = "";
	private LicaiNewGoodsInfo info;
	private int day = 0;
	private Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_licai_goods_layout);
		init();
	}

	private void init() {
		context = AddLicaiGoodsActivity.this;
		tvGoodsName = (TextView) findViewById(R.id.tv_licai_new_goods_title);
		tvYearEarnings = (TextView) findViewById(R.id.tv_licai_new_add_yearEarnings);
		tvQxDay = (TextView) findViewById(R.id.tv_add_licai_qixian);
		tvQgAccount = (TextView) findViewById(R.id.tv_add_licai_qigou_account);
		tvQxDate = (TextView) findViewById(R.id.tv_add_licai_qixiDay);
		tvDqDate = (TextView) findViewById(R.id.tv_add_licai_daoqiDay);
		edAccount = (EditText) findViewById(R.id.ed_licai_buy_account);
		cb = (CheckBox) findViewById(R.id.cb_add_licai_agree);

		findViewById(R.id.tv_add_licai_agree).setOnClickListener(this);
		findViewById(R.id.common_title_back_licai_add).setOnClickListener(this);
		findViewById(R.id.btn_buy_licai).setOnClickListener(this);
		
		Intent intent = getIntent();
		if (intent != null) {
			info = intent.getParcelableExtra("info");
		}

		if (info != null) {
			qxDay = info.getTimeLimit();
			qgAccount = info.getQgAccount();
			yearEarnings = info.getYearEarnings();
			proid = info.getProid();
			day = Integer.parseInt(qxDay);
			tvQgAccount.setText(qgAccount);
			tvQxDay.setText(qxDay);
			tvYearEarnings.setText(yearEarnings);
			tvGoodsName.setText(info.getNameTitle());
		}

		try {
			tvDqDate.setText(TimeUtils.getStatetime("yyyy.MM.dd", (day + 1)));
			tvQxDate.setText(TimeUtils.getStatetime("yyyy.MM.dd", 1));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.common_title_back_licai_add:
			finish();
			break;
		case R.id.tv_add_licai_agree:
			Intent it = new Intent(this, ProtocolActivity.class);
			it.putExtra("title", "快易付理财服务协议");
			startActivity(it);
			break;
		case R.id.btn_buy_licai:
			account = edAccount.getText().toString();
			if (account.equals("")) {
				T.ss("请输入金额");
				return;
			}
			if (!cb.isChecked()) {
				T.ss("请勾选同意协议");
				return;
			}
			if (MyUtilss.noPayYajin()) {
				T.ss("商户未缴纳押金");
				return;
			}
			nextStep();

			break;

		default:
			break;
		}
	}

	/**
	 * 购买理财产品
	 */
	private void buyLicaiGoods() {

		RequestParams params = new RequestParams();
		String url = MyUrls.PAYLICAI;
		showLoadingDialog();
		Map<String, String> map = new HashMap<String, String>();
		map.put("username", MApplication.mSharedPref
				.getSharePrefString(SharedPrefConstant.USERNAME));
		map.put("token", MApplication.mSharedPref
				.getSharePrefString(SharedPrefConstant.TOKEN));
		map.put("proid", proid);
		map.put("total", account);

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
				T.ss("操作超时");
			}

			@Override
			public void onSuccess(ResponseInfo<String> response) {
				// TODO Auto-generated method stub
				String code = "";
				String message = "";

				String str = response.result;
				Log.i("result", "----str----s-------" + str);
				try {
					JSONObject obj = new JSONObject(str);
					code = obj.optString("CODE");
					message = obj.optString("MESSAGE");

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				T.ss(message);
				dismissLoadingDialog();
				if (code.equals("00")) {
					edAccount.setText("");
					finish();
				}
			}
		});
	}

	private void nextStep() {

		new ActionSheetDialog(context)
				.builder()
				.setTitle("请选择买入途径类型")
				.setCancelable(false)
				.setCanceledOnTouchOutside(true)
				.addSheetItem("刷卡", SheetItemColor.Blue,
						new OnSheetItemClickListener() {
							@Override
							public void onClick(int which) {
								nextStep2();
							}
						})
				.addSheetItem("钱包余额", SheetItemColor.Blue,
						new OnSheetItemClickListener() {
							@Override
							public void onClick(int which) {
								buyLicaiGoods();
							}
						}).show();
	}

	private void nextStep2() {
		new ActionSheetDialog(context)
				.builder()
				.setTitle("请选择刷卡器类型")
				.setCancelable(false)
				.setCanceledOnTouchOutside(true)
				.addSheetItem("音频刷卡器", SheetItemColor.Blue,
						new OnSheetItemClickListener() {
							@Override
							public void onClick(int which) {
								PosData.getPosData().setPayType("07");
								PosData.getPosData().setPrdordNo(proid);// 理财产品id
								account = AmountUtils.changeY2F(account);
								PosData.getPosData().setPayAmt(account);
								startActivity(new Intent(context,
										SwingHXCardActivity.class)
										.setAction(Actions.ACTION_CASHIN));
							}
						})
				.addSheetItem("蓝牙刷卡器", SheetItemColor.Blue,
						new OnSheetItemClickListener() {
							@Override
							public void onClick(int which) {
								PosData.getPosData().setPayType("07");
								PosData.getPosData().setPrdordNo(proid);// 理财产品id
								account = AmountUtils.changeY2F(account);
								PosData.getPosData().setPayAmt(account);
								Intent it = new Intent(context,
										ZXBDeviceListActivity.class);
								// Intent it = new Intent(ctx,
								// PayByBuleCardActivity.class);
								it.setAction(Actions.ACTION_CASHIN);
								startActivity(it);
							}
						})

				.addSheetItem("键盘蓝牙刷卡器", SheetItemColor.Blue,
						new OnSheetItemClickListener() {
							@Override
							public void onClick(int which) {
								PosData.getPosData().setPayType("07");
								PosData.getPosData().setPrdordNo(proid);// 理财产品id
								account = AmountUtils.changeY2F(account);
								PosData.getPosData().setPayAmt(account);
								startActivity(new Intent(context,
										PayByV50CardActivity.class)
										.setAction(Actions.ACTION_CASHIN));
							}
						}).show();
	}

}
