package com.lk.qf.pay.posloan;

import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lk.bhb.pay.R;
import com.lk.qf.pay.aanewactivity.JieSuanActivity;
import com.lk.qf.pay.aanewactivity.PoPhotoActivity;
import com.lk.qf.pay.activity.BaseActivity;
import com.lk.qf.pay.golbal.MApplication;
import com.lk.qf.pay.golbal.MyUrls;
import com.lk.qf.pay.sharedpref.SharedPrefConstant;
import com.lk.qf.pay.tool.T;
import com.lk.qf.pay.utils.ExpresssoinValidateUtil;
import com.lk.qf.pay.utils.StringUtils;
import com.lk.qf.pay.wedget.CommonTitleBar;

public class PosloanBasicActivity extends BaseActivity implements
		OnClickListener, OnCheckedChangeListener {

	private CommonTitleBar title;
	private TextView tvUserName, tvBrithday, tvPhoneNum, tvIdType, tvIdNum;
	private EditText edNowAddr, edUnitName, edUnitAddr, edUnitPhoneNum,
			edCountry, edEmail, edSitename;
	private String usetName, userPhoneNum, nowAddr = "", unitName = "",
			unitAddr = "", unitPhoneNum = "", country = "", email = "",
			idtype = "", sitename = "", sex = "", marriageState = "",
			brithday = "", idNum = "";
	private RadioGroup rgSex, rgMarriage;

	private Date date;
	private SimpleDateFormat sdf;
	private Calendar cal;
	private AlertDialog.Builder builder;
	private int mYear;
	private int mMonth;
	private int mDay;
	private View view;
	private DatePicker datePicker;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pos_loan_basic_layout);
		init();

	}

	private void init() {

		title = (CommonTitleBar) findViewById(R.id.titlebar_posLoan_basic);
		title.setActName("个人基本信息");
		title.setCanClickDestory(this, true);
		date = new Date();
		sdf = new SimpleDateFormat("yyyyMMdd");
		usetName = MApplication.mSharedPref
				.getSharePrefString(SharedPrefConstant.NINAME);
		userPhoneNum = MApplication.mSharedPref
				.getSharePrefString(SharedPrefConstant.USERNAME);

		tvPhoneNum = (TextView) findViewById(R.id.tv_posLoan_PhoneNum);
		tvUserName = (TextView) findViewById(R.id.tv_posLoan_userName);

		tvBrithday = (TextView) findViewById(R.id.tv_posLoan_birthday);
		tvBrithday.setOnClickListener(this);
		edEmail = (EditText) findViewById(R.id.ed_posLoan_email);
		edCountry = (EditText) findViewById(R.id.ed_posLoan_country);
		edNowAddr = (EditText) findViewById(R.id.ed_posLoan_addressNow);
		edUnitName = (EditText) findViewById(R.id.ed_posLoan_CompanyName);
		edUnitAddr = (EditText) findViewById(R.id.ed_posLoan_CompanyAddress);
		edUnitPhoneNum = (EditText) findViewById(R.id.ed_posLoan_CompanyPhoneNum);
		edSitename = (EditText) findViewById(R.id.ed_posLoan_sitename);
		findViewById(R.id.btn_posLoan_basic_next).setOnClickListener(this);
		rgSex = (RadioGroup) findViewById(R.id.rg_posLoan_sex);
		rgMarriage = (RadioGroup) findViewById(R.id.rg_posLoan_hunyin);
		rgSex.setOnCheckedChangeListener(this);
		rgMarriage.setOnCheckedChangeListener(this);

		tvUserName.setText(usetName);
		tvPhoneNum.setText(userPhoneNum);

	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.tv_posLoan_birthday:
			showDateDialog();
			break;
		case R.id.btn_posLoan_basic_next:
			getData();
			break;

		default:
			break;
		}

	}

	private void getData() {
		email = edEmail.getText().toString();
		nowAddr = edNowAddr.getText().toString();
		unitName = edUnitName.getText().toString();
		unitAddr = edUnitAddr.getText().toString();
		unitPhoneNum = edUnitPhoneNum.getText().toString();
		country = edCountry.getText().toString();
		sitename = edSitename.getText().toString();

		if (email == null || email.equals("")) {
			T.ss("请输入联系邮箱");
			return;
		}else if (!StringUtils.isEmail(email)) {
			T.ss("请输入合法的邮箱地址");
			return;
		}
		if (country == null || country.equals("")) {
			T.ss("请输入国籍");
			return;
		}
		if (nowAddr == null || nowAddr.equals("")) {
			T.ss("请输入现住地址");
			return;
		}
		if (unitName == null || unitName.equals("")) {
			T.ss("请输入单位名称");
			return;
		}

		if (unitAddr == null || unitAddr.equals("")) {
			T.ss("请输入单位地址");
			return;
		}
		if (unitPhoneNum == null || unitPhoneNum.equals("")) {
			T.ss("请输入单位电话");
			return;
		}
		if (sitename == null || sitename.equals("")) {
			T.ss("请输入所属民族");
			return;
		}
		if (brithday == null || brithday.equals("")) {
			T.ss("请选择出生日期");
			return;
		}
		sendMerPoslan();
	}

	@Override
	public void onCheckedChanged(RadioGroup arg0, int arg1) {
		// TODO Auto-generated method stub
		switch (arg1) {
		case R.id.rb_posLoan_sex_1:
			sex = "男";
			break;
		case R.id.rb_posLoan_sex2:
			sex = "女";

			break;
		case R.id.rb_posLoan_weihun:
			marriageState = "未婚";
			break;
		case R.id.rb_posLoan_yihun:
			marriageState = "已婚";

			break;

		default:
			break;
		}
	}

	/**
	 * 信息上传
	 */
	private void sendMerPoslan() {
		showLoadingDialog();
		RequestParams params = new RequestParams();

		String url = MyUrls.POSLOANADD;

		Map<String, String> map = new HashMap<String, String>();
		map.put("username", MApplication.mSharedPref
				.getSharePrefString(SharedPrefConstant.USERNAME));
		map.put("sex", sex);
		map.put("nation", country);// 国籍
		map.put("birth", brithday);// 出生时间
		map.put("idtype", "身份证");// 证件类型
		map.put("clazz", nowAddr);// 现住地址
		map.put("unit", unitName);// 单位
		map.put("site", marriageState);// 婚姻状态
		map.put("sitename", sitename);// 民族
		map.put("unitclazz", unitAddr);// 单位地址
		map.put("unitphone", unitPhoneNum);// 单位电话
		map.put("contact1phone", "");// 第一联系人电话
		map.put("contact1name", "");// 第一联系人姓名
		map.put("contact2name", "");// 2姓名
		map.put("contact2phone", "");// 2电话
		map.put("contact3phone", "");
		map.put("contact3name", "");
		map.put("posairimg", "");// 户口本复印件
		map.put("posrailwayimg", "");// 结婚证
		map.put("poscashimg2", "");// 房产证
		map.put("posotherimg2", "");// 单位水电气票据
		map.put("posotherimg3", "");// 住址水电气
		map.put("fathersort", email);// 邮箱

		String json = JSON.toJSONString(map);
		Log.i("result", "----ddd----s--PosLoan-----" + json);
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
				dismissLoadingDialog();
				Log.i("result", "----PosLoan----s---s----" + str);
				try {
					JSONObject obj = new JSONObject(str);
					code = obj.optString("CODE");
					message = obj.optString("MESSAGE");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				T.ss(message);
				if (code.equals("00")) {
					Intent intent = new Intent(PosloanBasicActivity.this,
							PosLoanFriendsInfoActivity.class);
					startActivity(intent);
				}
			}
		});

	}

	private void showDateDialog() {
		
		cal = Calendar.getInstance();
		mYear = cal.get(Calendar.YEAR);
		mMonth = cal.get(Calendar.MONTH);
		mDay = cal.get(Calendar.DAY_OF_MONTH);
		builder = new AlertDialog.Builder(this);
		view = getLayoutInflater().inflate(R.layout.custom_date_picker_layout,
				null);
		datePicker = (DatePicker) view.findViewById(R.id.custom_datePicker);
		// 设置当前时间
		cal.setTimeInMillis(System.currentTimeMillis());
		datePicker.init(mYear, mMonth, mDay, dateChangedListener);
		//1.在较低版本时 阻止点击datePicker弹出软键盘
		datePicker.setEnabled(false);
		
		builder.setView(view);
		builder.setTitle(getResources().getString(R.string.choose_start_time));
		builder.setPositiveButton(getResources().getString(R.string.confirm),
				new DialogInterface.OnClickListener() {
					// 确定
					@Override
					public void onClick(DialogInterface dialog, int arg1) {

						DecimalFormat mFormat = new DecimalFormat("00");
						String month = mFormat.format(Double
								.valueOf(mMonth + 1));
						String day = mFormat.format(Double.valueOf(mDay));
						tvBrithday.setText(mYear + "-" + month + "-" + day);
						brithday = "" + mYear + month + day;
						dialog.cancel();
					}
				});
		// 取消
		builder.setNegativeButton(getResources().getString(R.string.cancel),
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int arg1) {
						// TODO Auto-generated method stub
						dialog.cancel();
					}
				});

		builder.create().show();
		//2.在较低版本时 阻止点击datePicker弹出软键盘
		handler.obtainMessage(0).sendToTarget(); 
	}

	OnDateChangedListener dateChangedListener = new OnDateChangedListener() {

		@Override
		public void onDateChanged(DatePicker arg0, int year, int month, int day) {
			// TODO Auto-generated method stub
			mYear = year;
			mMonth = month;
			mDay = day;
		}
	};

	//3.在较低版本时 阻止点击datePicker弹出软键盘
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == 0) {
				datePicker.setEnabled(true);
			}
		}
	};
}
