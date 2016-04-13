package com.lk.qf.pay.aanewactivity;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.Inflater;

import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.BounceInterpolator;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lk.bhb.pay.R;
import com.lk.qf.pay.aanewactivity.creditcard.AddCreditCardActivity;
import com.lk.qf.pay.activity.BaseActivity;
import com.lk.qf.pay.adapter.XinyongkaListItemAdapter;
import com.lk.qf.pay.beans.Xinyongkainfo;
import com.lk.qf.pay.golbal.MApplication;
import com.lk.qf.pay.golbal.MyUrls;
import com.lk.qf.pay.sharedpref.SharedPrefConstant;
import com.lk.qf.pay.tool.T;
import com.lk.qf.pay.utils.ImgOptions;
import com.lk.qf.pay.wedget.CommonTitleBar;
import com.lk.qf.pay.wedget.webview.CommonLoadingComponent;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class XinyongkaGuanliActivity extends BaseActivity implements
		OnClickListener {

	private TextView tvName, tvPhoneNum, tvZhangDanAccount, tvYiHuanAccount,
			tvWeiHuanAccount, tvAllEduAccount;
	private String name, phoneNum, zdAccount, yhAccount, whAccount;
	private CommonTitleBar title;
	private ListView lv;
	private XinyongkaListItemAdapter adapter;
	private List<Xinyongkainfo> list;
	private List<Xinyongkainfo> listReturn;
	private Xinyongkainfo info;
	private Map<String, String> map;
	private DisplayImageOptions options;
	private ImageLoader imageLoader;
	private int page = 1;
	private int dataCount = 10;
	private String date1 = "";
	private String date2 = "";
	private String type = "0";
	private Date date;
	private SimpleDateFormat sdf1;
	private double zd;
	private String bankCardNum = "";
	private AdapterContextMenuInfo selectedMenuInfo = null;
	private LinearLayout ll;
	private CommonLoadingComponent loading;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.xinyongka_guanli_layout);
		init();
	}

	private void init() {

		imageLoader = ImageLoader.getInstance();
		imageLoader.init(ImageLoaderConfiguration
				.createDefault(XinyongkaGuanliActivity.this));
		options = ImgOptions.initImgOptions();
		date = new Date();
		sdf1 = new SimpleDateFormat("yyyyMMdd");

		tvAllEduAccount = (TextView) findViewById(R.id.tv_xyk_all_edu_account);
		tvName = (TextView) findViewById(R.id.tv_xyk_userName);
		tvPhoneNum = (TextView) findViewById(R.id.tv_xyk_userPhoneNum);
		tvZhangDanAccount = (TextView) findViewById(R.id.tv_xyk_zhangdan_account);
		tvYiHuanAccount = (TextView) findViewById(R.id.tv_xyk_yihuan_account);
		tvWeiHuanAccount = (TextView) findViewById(R.id.tv_xyk_weihuan_account);
		findViewById(R.id.rl_xyk_add).setOnClickListener(this);
		lv = (ListView) findViewById(R.id.myPull_refresh_list_xyk);
		loading = (CommonLoadingComponent) findViewById(R.id.loading_xinyongka_manager);
		loading.setIsLoading("加载中...");
		title = (CommonTitleBar) findViewById(R.id.titlebar_xinyongkaguanjia);
		title.setActName("信用卡管理");
		title.setCanClickDestory(this, true);
		phoneNum = MApplication.mSharedPref
				.getSharePrefString(SharedPrefConstant.USERNAME);// 电话号
		phoneNum = phoneNum.substring(0, 3) + "****"
				+ phoneNum.substring(phoneNum.length() - 4);
		name = MApplication.mSharedPref
				.getSharePrefString(SharedPrefConstant.NINAME);// 姓名
		tvName.setText(name);
		tvPhoneNum.setText(phoneNum);

		list = new ArrayList<Xinyongkainfo>();
		adapter = new XinyongkaListItemAdapter(this, list, options, imageLoader);
		lv.setAdapter(adapter);
		registerForContextMenu(lv);
		lv.setOnItemClickListener(clickListener);

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		list.clear();
		postQueryBankCardListHttp();
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.rl_xyk_add:
			Intent intent = new Intent(XinyongkaGuanliActivity.this,
					AddXinyongkaActivity.class);
			startActivity(intent);
			break;

		default:
			break;
		}
	}

	OnItemClickListener clickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View arg1, int position,
				long arg3) {
			// TODO Auto-generated method stub
			Xinyongkainfo info = (Xinyongkainfo) parent
					.getItemAtPosition(position);
			Intent intent = new Intent(XinyongkaGuanliActivity.this,
					XinyongkaInfoActivity.class);
			intent.putExtra("info", info);
			startActivity(intent);
		}
	};

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenu.ContextMenuInfo menuInfo) {
		int position = 0;
		// if (menuInfo instanceof AdapterView.AdapterContextMenuInfo) {
		// AdapterView.AdapterContextMenuInfo info =
		// (AdapterView.AdapterContextMenuInfo) menuInfo;
		// position = info.position;
		// }
		// 添加生成 MenuItem
		Log.i("result", "-------------ss-------" + position);
		menu.add("删除");
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		AdapterContextMenuInfo mInfo = (AdapterContextMenuInfo) item
				.getMenuInfo();

		int mPosition = mInfo.position;
		if (list != null && !list.isEmpty()) {
			bankCardNum = list.get(mPosition).getCardNum();
			deleteBankCard();
		}
		return super.onContextItemSelected(item);
	}

	/**
	 * 删除信用卡
	 * 
	 * @return
	 */
	private void deleteBankCard() {
		showLoadingDialog();
		RequestParams params = new RequestParams();
		String url = MyUrls.CREDITCARDDELETE;

		map = new HashMap<String, String>();

		map.put("username", MApplication.mSharedPref
				.getSharePrefString(SharedPrefConstant.USERNAME));
		map.put("bankcard", bankCardNum);

		String json = JSON.toJSONString(map);
		Log.i("result", "-------删除信用卡请求-----" + json);
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
				String str = response.result;
				Log.i("result", "---------------信用卡-returnjson---" + str);
				String code = "";
				String message = "";
				JSONObject obj;
				try {
					obj = new JSONObject(str);
					code = obj.optString("CODE");
					message = obj.optString("MESSAGE");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (code.equals("00")) {
					list.clear();
					postQueryBankCardListHttp();
				}
				T.ss(message);
			}
		});
	}

	/**
	 * 获取信用卡列表
	 * 
	 * @return
	 */
	private void postQueryBankCardListHttp() {
		showLoadingDialog();
		RequestParams params = new RequestParams();
		String url = MyUrls.CREDITCARDLIST;

		map = new HashMap<String, String>();

		map.put("username", MApplication.mSharedPref
				.getSharePrefString(SharedPrefConstant.USERNAME));

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
				T.ss("操作超时!");
				dismissLoadingDialog();
				loading.hideLayout();
				// autoLoading.hideAll();
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
						T.ss(getResources().getString(R.string.query_nothing));
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
				loading.hideLayout();
				dismissLoadingDialog();
				// autoLoading.hideAll();
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
			info = new Xinyongkainfo();
			info.setCode(obj.optString("CODE"));
			info.setMessage(obj.optString("MESSAGE"));
			listReturn = new ArrayList<Xinyongkainfo>();
			if (obj.optString("CODE").equals("00")) {
				zdAccount = obj.optString("bill");
				yhAccount = obj.optString("reimtotal");
				tvAllEduAccount.setText(obj.optString("banktotal"));
				zd = Double.parseDouble(zdAccount);
				// double yh = Double.parseDouble(yhAccount);
				// BigDecimal b1 = new BigDecimal(zd);
				// BigDecimal b2 = new BigDecimal(yh);
				// b1.subtract(b2).doubleValue();
				// double wh = zd - yh;
				// double wh = b1.subtract(b2).doubleValue();
				// whAccount = String.valueOf(wh);

				tvZhangDanAccount.setText(zdAccount);
				tvYiHuanAccount.setText(yhAccount);
				tvWeiHuanAccount.setText(zdAccount);
				// tvWeiHuanAccount.setText(""+wh);
				date2 = obj.optString("time");
			}
			int count = obj.optInt("count");
			if (count > 0) {

				for (int i = 0; i < count; i++) {

					String userName = obj.optJSONArray("date").optJSONObject(i)
							.optString("bei3");
					String cardNum = obj.optJSONArray("date").optJSONObject(i)
							.optString("bankcard");

					String imgUrl = obj.optJSONArray("date").optJSONObject(i)
							.optString("logo");
					Log.i("result", "-------logo0----" + imgUrl);
					String bank = obj.optJSONArray("date").optJSONObject(i)
							.optString("bank");
					String bankmoney = obj.optJSONArray("date")
							.optJSONObject(i).optString("bankmoney");
					String billmoney = obj.optJSONArray("date")
							.optJSONObject(i).optString("billmoney");

					String billtime = obj.optJSONArray("date").optJSONObject(i)
							.optString("billtime");// 账单日
					String reimtime = obj.optJSONArray("date").optJSONObject(i)
							.optString("reimtime");// 下月还款日
					String bei = obj.optJSONArray("date").optJSONObject(i)
							.optString("bei");// 当月还款日
					Log.i("result", "----------bei--------" + bei);
					String bei2 = obj.optJSONArray("date").optJSONObject(i)
							.optString("bei2");// 当月已还金额

					double billAccount = Double.parseDouble(billmoney);
					// double bei2Account = Double.parseDouble(bei2);
					// double weihuanAccount = billAccount - bei2Account;

					String nowTime = sdf1.format(date);
					long a = 0;// 当时时间与当月还款日bei 比较
					long b = 0;// 当时时间与当月账单日billtime 比较
					long c = 0;// 当时时间与下月还款日reimtime 比较
					long d = 0;// 当时时间与下月账单日 比较
					long day = 0;
					long nowDate = 0;
					long beiDate = 0;
					long reimDate = 0;
					long billDate = 0;
					long nextDate = 0;
					// int aac=0;
					try {
						nowDate = sdf1.parse(nowTime).getTime();
						beiDate = sdf1.parse(bei).getTime();
						reimDate = sdf1.parse(reimtime).getTime();
						billDate = sdf1.parse(billtime).getTime();
						nextDate = sdf1.parse(stringToNextMonth(billtime))
								.getTime();

						a = sdf1.parse(nowTime).getTime()
								- sdf1.parse(bei).getTime();//
						// aac = daysBetween(sdf1.parse(nowTime),
						// sdf1.parse(bei));
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					try {
						b = sdf1.parse(nowTime).getTime()
								- sdf1.parse(billtime).getTime();//
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					try {
						c = sdf1.parse(nowTime).getTime()
								- sdf1.parse(reimtime).getTime();//
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					try {
						d = nextDate - sdf1.parse(nowTime).getTime();//
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					long aa = a / 1000 / 60 / 60 / 24;// 天
					long bb = b / 1000 / 60 / 60 / 24;// 天
					long cc = c / 1000 / 60 / 60 / 24;// 天
					long dd = d / 1000 / 60 / 60 / 24;// 天
					if (billAccount > 0 || billmoney.equals("")) {

						if (nowDate < beiDate) {
							day = Math.abs(aa);
							// day = aac;
							// Log.i("result","-----day----day-----"+aac);
							type = "1";// 还款
						} else if (nowDate == beiDate) {
							day = 0;
							type = "0";// 到期
						} else if (nowDate > beiDate && nowDate < billDate) {
							day = Math.abs(aa);
							type = "2";// 未出账
						} else if (nowDate == billDate) {
							day = 0;
							type = "3";// 出账
						} else if (nowDate > billDate && nowDate < reimDate) {
							day = Math.abs(cc);
							type = "1";// 还款
						} else if (nowDate == reimDate) {
							day = 0;
							type = "0";// 到期
						} else if (nowDate > reimDate) {
							day = dd;
							type = "2";
						}

						if (zd > 0) {

						} else {
							type = "3";
						}
					} else {
						day = 0;
						type = "4";
					}

					//
					// 给info设置数据
					Xinyongkainfo xykinfo = null;
					xykinfo = new Xinyongkainfo();
					xykinfo.setUserName(userName);// 用户名
					xykinfo.setCardNum(cardNum);// 卡号
					xykinfo.setDate("" + day);// 剩余日期
					xykinfo.setImgUrl(imgUrl);// 图片url

					xykinfo.setType(type);// 还款状态
					xykinfo.setBankName(bank);// 银行名
					xykinfo.setBankmoney(bankmoney);// 信用卡额度
					xykinfo.setBillmoney(billmoney);// 账单金额
					xykinfo.setBilltime(billtime);// 账单日
					xykinfo.setReimtime(reimtime);// 还款日
					xykinfo.setBei(bei);// 还款日当月
					Log.i("result", "----------bei--------" + bei);
					// xykinfo.setWeihuanAccount("" + weihuanAccount);// 未还金额
					xykinfo.setWeihuanAccount(billmoney);// 未还金额

					listReturn.add(xykinfo);
				}
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * 获取下月当天时间
	 * 
	 * @param strTime
	 * @return
	 */
	private String stringToNextMonth(String strTime) {
		Log.i("result", "----------strTime-----------" + strTime);
		DateFormat df = new SimpleDateFormat("yyyyMMdd");
		Date d = null;
		try {
			d = df.parse(strTime);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// 输出结果
		Calendar cal = Calendar.getInstance();
		cal.setTime(d);
		cal.add(Calendar.MONTH, 1);

		int day = cal.get(Calendar.DAY_OF_MONTH); // 日
		int month = cal.get(Calendar.MONTH); // 月(从0开始, 一般加1，实际是否 Calendar
												// 里面常量的值决定的)
		int year = cal.get(Calendar.YEAR); // 年
		String nextDate = df.format(cal.getTime());
		Log.i("result", "----------format1-----------" + nextDate);
		Log.i("result",
				"----------format2--s---------" + df.format(cal.getTime()));
		return nextDate;
	}

	/**
	 * 计算两个时间 天数差
	 * 
	 * @param smdate
	 * @param bdate
	 * @return
	 * @throws ParseException
	 */
	public static int daysBetween(Date smdate, Date bdate)
			throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		smdate = sdf.parse(sdf.format(smdate));
		bdate = sdf.parse(sdf.format(bdate));
		Calendar cal = Calendar.getInstance();
		cal.setTime(smdate);
		long time1 = cal.getTimeInMillis();
		cal.setTime(bdate);
		long time2 = cal.getTimeInMillis();
		long between_days = (time2 - time1) / (1000 * 3600 * 24);

		return Integer.parseInt(String.valueOf(between_days));
	}

}
