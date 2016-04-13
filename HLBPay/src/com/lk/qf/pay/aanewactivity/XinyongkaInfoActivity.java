package com.lk.qf.pay.aanewactivity;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import android.R.bool;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lk.bhb.pay.R;
import com.lk.qf.pay.activity.BaseActivity;
import com.lk.qf.pay.adapter.XinyongkaListItemAdapter;
import com.lk.qf.pay.adapter.XykHkTradeListAdapter;
import com.lk.qf.pay.beans.XYKTradeListInfo;
import com.lk.qf.pay.beans.Xinyongkainfo;
import com.lk.qf.pay.golbal.MApplication;
import com.lk.qf.pay.golbal.MyUrls;
import com.lk.qf.pay.sharedpref.SharedPrefConstant;
import com.lk.qf.pay.tool.T;
import com.lk.qf.pay.utils.ImgOptions;
import com.lk.qf.pay.utils.StringUtils;
import com.lk.qf.pay.utils.TimeUtils;
import com.lk.qf.pay.wedget.CommonTitleBar;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class XinyongkaInfoActivity extends BaseActivity implements
		OnClickListener, OnRefreshListener2<ListView> {

	private ImageView logo;
	private TextView tvUserName, hklist, tvKeyong, tvzdZQDate, tvhkDate,tvDownload,tvIsPayOff;
	private EditText edEdu, edZdAccount, edYhAccount, edWhAccount;
	private ImageButton tvEditEdu, tvEditZd;
	private Xinyongkainfo info;
	private String eduAccount = "", zdAccount = "", yhAccount = "",
			whAccount = "", cardNum = "", decUse = "", keyongAccount, zdZQDate;
	private String eduAccount2 = "", zdAccount2 = "",userName="",name="";//name是卡开户名
	private PullToRefreshListView lv;
	private XykHkTradeListAdapter adapter;
	private List<XYKTradeListInfo> list;
	private List<XYKTradeListInfo> listReturn;
	private Map<String, String> map;
	private int page = 1;
	private int dataCount = 10;
	private CommonTitleBar title;
	private String strTitle;
	private String logoUrl;
	private DisplayImageOptions options;
	private ImageLoader imageLoader;
	private boolean isEduCanEdit = true;
	private boolean isZDCanEdit = true;
	private static int TYPE = 0;
	private String zdDate = "", hkDate = "";
	private String strzdDate = "", strhkDate1 = "", strhkDate2 = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.xinyongka_info_layout);
		init();
		Intent intent = getIntent();
		if (intent != null) {
			info = intent.getParcelableExtra("info");
			cardNum = info.getCardNum();
			strzdDate = info.getBilltime();//账单日
			strhkDate1 = info.getBei();// 当月还款日
			strhkDate2 = info.getReimtime();// 下月还款日
			name = info.getUserName();
			tvUserName.setText(name);
			TimeUtils tu = new TimeUtils();
			
			// 还款日
			boolean hk = tu.isBeforeDate(strhkDate1);
//			Log.i("result", "------dd-hkstrhkDate1-------"+strhkDate1);
//			Log.i("result", "------dd-hk--"+hk);
			String hkday = "";
			if (hk) {
				String strHk2 = strhkDate2.substring(4, 8);
//				Log.i("result", "------dd-huank2--"+strHk2);
				hkday=strHk2;
			} else {
				String strHk1 = strhkDate1.substring(4, 8);
//				Log.i("result", "------dd-huank1--"+strHk1);
				hkday=strHk1;
				
			}
			SimpleDateFormat sdf3 = new SimpleDateFormat("MM/dd");
			SimpleDateFormat sdf4 = new SimpleDateFormat("MMdd");
			try {
				hkday = sdf3.format(sdf4.parse(hkday));
				tvhkDate.setText(hkday);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// 账单周期
			String[] zdzq = tu.myFormatDate(strzdDate);
			tvzdZQDate.setText(zdzq[0] + "-" + zdzq[1]);

			if (!info.getImgUrl().equals("")) {

				logoUrl = MyUrls.ROOT_URL1 + info.getImgUrl();
				imageLoader.displayImage(logoUrl, logo, options);
			} else {
				logo.setBackground(getResources().getDrawable(
						R.drawable.yinlian));
			}

		} else {
			info = new Xinyongkainfo();
		}
		// imageLoader.displayImage(logoUrl, logo, options);
		postQueryBankCardListHttp();
	}



	/**
	 * 初始化
	 */
	private void init() {
		userName = MApplication.mSharedPref
				.getSharePrefString(SharedPrefConstant.USERNAME);
		title = (CommonTitleBar) findViewById(R.id.titlebar_xinyongka_info);
		title.setActName("信用卡");
		title.setCanClickDestory(this, true);
		imageLoader = ImageLoader.getInstance();
		imageLoader.init(ImageLoaderConfiguration
				.createDefault(XinyongkaInfoActivity.this));
		options = ImgOptions.initImgOptions();
		logo = (ImageView) findViewById(R.id.img_xyk_info_logo);

//		tvDownload = (TextView) findViewById(R.id.tv_download1);
		
		tvzdZQDate = (TextView) findViewById(R.id.tv_xyk_info_zq_time);
		tvhkDate = (TextView) findViewById(R.id.tv_xyk_info_hkDate);
		hklist = (TextView) findViewById(R.id.tv_xyk_huankuan_title);
		tvUserName = (TextView) findViewById(R.id.tv_xyk_info_name);
		tvIsPayOff = (TextView) findViewById(R.id.tv_xyk_info_isPayOff);

		edEdu = (EditText) findViewById(R.id.ed_xyk_info_edu_account);
		edZdAccount = (EditText) findViewById(R.id.ed_xyk_info_zhangdan_account);
		edYhAccount = (EditText) findViewById(R.id.ed_xyk_info_yihuan_account);
		edWhAccount = (EditText) findViewById(R.id.ed_xyk_info_weihuan_account);
		tvKeyong = (TextView) findViewById(R.id.ed_xyk_info_keyong_account);

		tvEditEdu = (ImageButton) findViewById(R.id.tv_xyk_info_edu_edit1);
		tvEditZd = (ImageButton) findViewById(R.id.tv_xyk_info_ezhangdan_edit2);
		tvEditEdu.setOnClickListener(this);
		tvEditZd.setOnClickListener(this);
		lv = (PullToRefreshListView) findViewById(R.id.myPull_refresh_list_xyk_info);
		list = new ArrayList<XYKTradeListInfo>();
		adapter = new XykHkTradeListAdapter(this, list);
		lv.setAdapter(adapter);
		findViewById(R.id.btn_xyk_info_hk).setOnClickListener(this);
		findViewById(R.id.ib_mofiy_xyk_xiugai).setOnClickListener(this);

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		postQueryBankCardListHttp();
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.btn_xyk_info_hk:// 还款
			Intent intent = new Intent(XinyongkaInfoActivity.this,
					XinyongkaHuanKuanActivity.class);
			intent.putExtra("cardNum", cardNum);
			intent.putExtra("decUse", decUse);
			startActivity(intent);
			break;
		case R.id.tv_xyk_info_edu_edit1:
			if (isEduCanEdit) {
				isEduCanEdit = false;
				edEdu.setEnabled(true);
				edEdu.setHint("");
				edEdu.setFocusable(true);
				edEdu.setFocusableInTouchMode(true);
				edEdu.requestFocus();
				edEdu.findFocus();
				edZdAccount.setEnabled(false);
				tvEditZd.setClickable(false);
				tvEditEdu
						.setBackgroundResource(R.drawable.btn_wancheng_selector);
			} else {
				eduAccount2 = edEdu.getText().toString();
				if (!eduAccount2.equals("")) {
					zdAccount2 = zdAccount;
					if (!eduAccount2.equals("") && !zdAccount2.equals("")) {
						double eAccount = Double.parseDouble(eduAccount2);
						double zAccount = Double.parseDouble(zdAccount2);
						if (eAccount < zAccount) {
							T.ss("信用卡额度不能低于账单金额");
							return;
						}
					}
				}

				addBankCard(0);
			}
			break;
		case R.id.tv_xyk_info_ezhangdan_edit2:

			if (isZDCanEdit) {
				isZDCanEdit = false;
				edZdAccount.setEnabled(true);
				edEdu.setEnabled(false);
				// 获得焦点
				edZdAccount.setFocusable(true);
				edZdAccount.setHint("");
				edZdAccount.setFocusableInTouchMode(true);
				edZdAccount.requestFocus();
				edZdAccount.findFocus();
				tvEditEdu.setClickable(false);
				tvEditZd.setBackgroundResource(R.drawable.btn_wancheng_selector);
			} else {
				zdAccount2 = edZdAccount.getText().toString();
				if (!zdAccount2.equals("")) {

					eduAccount2 = eduAccount;
					if (!eduAccount2.equals("") && !zdAccount2.equals("")) {
						double eAccount = Double.parseDouble(eduAccount2);
						double zAccount = Double.parseDouble(zdAccount2);
						if (eAccount < zAccount) {
							T.ss("信用卡额度不能低于账单金额");
							return;
						}
					}
				}

				addBankCard(1);
			}

			break;
		case R.id.ib_mofiy_xyk_xiugai:
			Intent intent2 = new Intent(XinyongkaInfoActivity.this,
					ModifyXinyongkaActivity.class);
			intent2.putExtra("cardNum", cardNum);
			intent2.putExtra("zdDate", zdDate);
			intent2.putExtra("name", name);
//			Log.i("result", "----------zd--------" + zdDate);
//			Log.i("result", "----------hk--------" + hkDate);
			intent2.putExtra("hkDate", hkDate);
			startActivity(intent2);
			break;
		default:
			break;
		}
	}

	/**
	 * 获取信用卡还款记录
	 * 
	 * @return
	 */
	private void postQueryBankCardListHttp() {
		showLoadingDialog();
		RequestParams params = new RequestParams();
		String url = MyUrls.CREDITCARDVIEW;

		map = new HashMap<String, String>();

		map.put("username", userName);
		map.put("bankcard", cardNum);
		map.put("pageSize", "" + dataCount);
		map.put("page", "" + page);

		String json = JSON.toJSONString(map);
		Log.i("result", "-------信用卡请求-----" + json);
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
				lv.onRefreshComplete();
				hklist.setText("");
			}

			@Override
			public void onSuccess(ResponseInfo<String> response) {
				// TODO Auto-generated method stub
				String strReturnLogin = response.result;
				Log.i("result", "---------------信用卡-returnjson---"
						+ strReturnLogin);

				jsonDetail(strReturnLogin);

				String returnCode = info.getCode();

				if (returnCode.equals("00")) {

					if (listReturn == null || listReturn.size() == 0) {
						// T.ss(getResources().getString(R.string.query_nothing));
					} else {

						if (page == 1) {
							list.clear();
							list = listReturn;
						} else {
							list.addAll(listReturn);// 追加跟新的数据
						}
						adapter.sendSata(list);
						adapter.notifyDataSetChanged();
					}
				} else {
					T.ss(info.getMessage());
				}
				lv.onRefreshComplete();// 告诉它 我们已经在后台数据请求完毕
				dismissLoadingDialog();
			}
		});
	}

	private void getWeiHuanAccount() {
		double ed = 0;
		double zd=0;
		if (eduAccount!=null && eduAccount.equals("")) {
			ed = Double.parseDouble(eduAccount);
		}
		if (zdAccount!=null && zdAccount.equals("")) {
			zd = Double.parseDouble(zdAccount);
		}
//		double yh = Double.parseDouble(yhAccount);
		double wh = zd;
		whAccount = String.valueOf(wh);
		if (zd<=0) {
			tvIsPayOff.setText("已还清");
		}else{
			tvIsPayOff.setText("");
		}
		edWhAccount.setHint(zdAccount);
//		edWhAccount.setHint("" + whAccount);
		edZdAccount.setHint("" + zdAccount);
		edEdu.setHint("" + eduAccount);
		tvKeyong.setText("" + (ed - wh));
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
			info = new Xinyongkainfo();
			info.setCode(obj.optString("CODE"));
			info.setMessage(obj.optString("MESSAGE"));
			listReturn = new ArrayList<XYKTradeListInfo>();
			if (obj.optString("CODE").equals("00")) {
				eduAccount = obj.optString("bankmoney");
				zdAccount = obj.optString("billmoney");
				yhAccount = obj.optString("huankuan");
				edYhAccount.setHint("" + yhAccount);
				zdDate = obj.optString("billtime");
				hkDate = obj.optString("reimtime");
				decUse = obj.optString("decUse");
				// tvzdZQDate.setText(text);
				getWeiHuanAccount();

			}
			int count = obj.optInt("count");
			Log.i("result", "---------Count-------" + count);
			if (count > 0) {
				hklist.setText("还款明细:");
				for (int i = 0; i < count; i++) {

					String total = obj.optJSONArray("date").optJSONObject(i)
							.optString("total");// 还款金额
					String usem = obj.optJSONArray("date").optJSONObject(i)
							.optString("usem");// 实际还款
					String tax = obj.optJSONArray("date").optJSONObject(i)
							.optString("tax");// 手续费
					String addtime = obj.optJSONArray("date").optJSONObject(i)
							.optString("addtime");// 时间
					Log.i("result", "------------time-----------" + addtime);
					String state = obj.optJSONArray("date").optJSONObject(i)
							.optString("state");// 状态

					// 给info设置数据
					XYKTradeListInfo xykinfo = new XYKTradeListInfo();
					xykinfo.setHkAccount(total);
					xykinfo.setType(state);
					xykinfo.setSxAccount(tax);
					xykinfo.setDate(addtime);

					listReturn.add(xykinfo);
				}
			} else {
				hklist.setText("");
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void addBankCard(int type) {
		TYPE = type;
		Log.i("result", "-------ed2----" + eduAccount2);
		Log.i("result", "-------zd2----" + zdAccount2);

		if (TYPE == 0) {
			if (eduAccount2.equals("")) {
				eduAccount2 = eduAccount;
			}
		}
		if (TYPE == 1) {
			if (zdAccount2.equals("")) {

				zdAccount2 = zdAccount;
			}
		}
		showLoadingDialog();
		RequestParams params = new RequestParams();
		HashMap<String, String> map = new HashMap<String, String>();

		map.put("username", MApplication.mSharedPref
				.getSharePrefString(SharedPrefConstant.USERNAME));
		map.put("name", name);//名字
		map.put("bankmoney", eduAccount2);// 额度
		map.put("bank", "");
		map.put("logo", "");
		map.put("billmoney", zdAccount2);// 账单金额
		Log.i("result", "---------carddd---------" + cardNum);
		map.put("bankcard", cardNum);
		map.put("billtime", "");
		map.put("reimtime", "");
		map.put("type", "1");// 0添加 1修改

		String json = JSON.toJSONString(map);
		Log.i("result", "----ddd--d---------" + json);
		try {
			StringEntity bodyEntity = new StringEntity(json, "UTF-8");
			params.setBodyEntity(bodyEntity);
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		HttpUtils httpUtils = new HttpUtils();
		String url = MyUrls.CREDITCARDADD;

		httpUtils.send(HttpMethod.POST, url, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						// TODO Auto-generated method stub
						Log.i("result", "--------------failure------------");
						T.ss("操作超时");
						getWeiHuanAccount();

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

								if (TYPE == 0) {
									isEduCanEdit = true;
									edEdu.setEnabled(false);
									tvEditEdu
											.setBackgroundResource(R.drawable.btn_bianji_1);
									tvEditZd.setClickable(true);
									edEdu.setText("");
									eduAccount = eduAccount2;
									getWeiHuanAccount();
								} else {
									isZDCanEdit = true;
									edZdAccount.setEnabled(false);
									edEdu.setEnabled(false);
									tvEditZd.setBackgroundResource(R.drawable.btn_bianji_1);
									tvEditEdu.setClickable(true);
									zdAccount = zdAccount2;
									edZdAccount.setText("");
									getWeiHuanAccount();

								}
							} else {
								T.ss(message);
							}

						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						dismissLoadingDialog();
					}
				});
	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		// TODO Auto-generated method stub
		page = 1;
		postQueryBankCardListHttp();
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		// TODO Auto-generated method stub
		page++;
		Log.i("result", "--------ss------" + page);
		postQueryBankCardListHttp();
	}

}
