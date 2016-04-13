package com.lk.qf.pay.aanewactivity.creditcard;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.View.OnClickListener;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.ListView;
import com.alibaba.fastjson.JSON;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lk.bhb.pay.R;
import com.lk.qf.pay.aanewactivity.creditcard.AddCreditCardActivity;
import com.lk.qf.pay.activity.BaseActivity;
import com.lk.qf.pay.adapter.CreditCardAdapter;
import com.lk.qf.pay.beans.Xinyongkainfo;
import com.lk.qf.pay.golbal.MApplication;
import com.lk.qf.pay.golbal.MyUrls;
import com.lk.qf.pay.sharedpref.SharedPrefConstant;
import com.lk.qf.pay.tool.T;
import com.lk.qf.pay.utils.StringUtils;
import com.lk.qf.pay.wedget.CommonTitleBar;
import com.lk.qf.pay.wedget.MyLongClickListener;
import com.lk.qf.pay.wedget.XYKDJClickListener;
import com.lk.qf.pay.wedget.customdialog.DeleteDialog;
import com.lk.qf.pay.wedget.customdialog.DeleteDialog.IDialogOnclickInterface;

public class CreditCardsListActivity extends BaseActivity implements
		OnClickListener, IDialogOnclickInterface, OnRefreshListener2<ListView> {

	private CommonTitleBar title;
	private PullToRefreshListView lv;
	private CreditCardAdapter adapter;
	private List<Xinyongkainfo> list;
	private List<Xinyongkainfo> listReturn;
	private Xinyongkainfo info;
	private Map<String, String> map;
	private int page = 1;
	private DeleteDialog myDialog;
	private View currentItemView;
	private int longClickPosition;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.credit_card_list_layout);
		init();
	}

	private void init() {
		Display display = this.getWindowManager().getDefaultDisplay();
		int mScreenWidth = display.getWidth();
		int mScreenHeight = display.getHeight();

		findViewById(R.id.tv_add_creditCard).setOnClickListener(this);
		lv = (PullToRefreshListView) findViewById(R.id.myPull_refresh_list_order_creditCard);
		lv.setMode(Mode.PULL_DOWN_TO_REFRESH);
		LayoutParams params = lv.getLayoutParams();
		params.width = mScreenWidth - 20;
		params.height = mScreenHeight - 50;
		lv.setLayoutParams(params);
		title = (CommonTitleBar) findViewById(R.id.titlebar_creditCard_list);
		title.setActName("信用卡管理");
		title.setCanClickDestory(this, true);
		list = new ArrayList<Xinyongkainfo>();
		adapter = new CreditCardAdapter(this, list, mListener,
				mLongClickLitener);
		lv.setAdapter(adapter);
		lv.setOnRefreshListener(this);
		myDialog = new DeleteDialog(this, R.style.MyDialogStyle);
		// lv.setMode(Mode.DISABLED);
		myDialog.setOnCancelListener(new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				// TODO Auto-generated method stub
				currentItemView.setAlpha(100);
			}
		});
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
		case R.id.tv_add_creditCard:
			// Intent intent = new Intent(CreditCardsListActivity.this,
			// CreditCardRepayListActivity.class);
			Intent intent = new Intent(CreditCardsListActivity.this,
					AddCreditCardActivity.class);
			startActivity(intent);
			break;

		default:
			break;
		}
	}

	/**
	 * listview中控件的事件
	 */
	private XYKDJClickListener mListener = new XYKDJClickListener() {
		@Override
		public void myOnClick(int position, View v) {
			Xinyongkainfo info = list.get(position);
			Intent intent = new Intent(CreditCardsListActivity.this,
					RepaymentWayActivity.class);
			intent.putExtra("info", info);
			startActivity(intent);
		}
	};

	private MyLongClickListener mLongClickLitener = new MyLongClickListener() {
		@Override
		public void myLongOnClick(int position, View view) {
			int[] location = new int[2];
			// 获取当前view在屏幕中的绝对位置
			// ,location[0]表示view的x坐标值,location[1]表示view的坐标值
			// view.getLocationOnScreen(location);
			view.setAlpha(200);
			currentItemView = view;
			longClickPosition = position;
			// DisplayMetrics displayMetrics = new DisplayMetrics();
			// Display display = CreditCardsListActivity.this.getWindowManager()
			// .getDefaultDisplay();
			// display.getMetrics(displayMetrics);
			// WindowManager.LayoutParams params = myDialog.getWindow()
			// .getAttributes();
			// params.gravity = Gravity.BOTTOM;
			// params.y = display.getHeight() * 14 / 15 - location[1];
			// myDialog.getWindow().setAttributes(params);
			// myDialog.setCanceledOnTouchOutside(true);
			// myDialog.show();
			LayoutInflater inflater = LayoutInflater
					.from(CreditCardsListActivity.this);
			// 引入窗口配置文件
			dv = inflater.inflate(R.layout.layout_dialog_delete, null);
			final PopupWindow pop = new PopupWindow(dv,
					LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, false);
			TextView b1 = (TextView) dv.findViewById(R.id.textview_one);
			TextView b2 = (TextView) dv.findViewById(R.id.textview_two);
			b1.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					pop.dismiss();
					currentItemView.setAlpha(20);
					// Toast.makeText(CreditCardsListActivity.this, "b1b1b1",
					// 2000).show();
				}
			});
			b2.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					pop.dismiss();
					currentItemView.setAlpha(20);
					// list.remove(longClickPosition);
					// adapter.notifyDataSetChanged();
					String id = list.get(longClickPosition).getId();
					deleteBankCard(id);
					// Toast.makeText(CreditCardsListActivity.this, "b2b2b2",
					// 2000).show();
				}
			});

			// 需要设置一下此参数，点击外边可消失
			pop.setBackgroundDrawable(new BitmapDrawable());
			// 设置点击窗口外边窗口消失
			pop.setOutsideTouchable(true);
			// 设置此参数获得焦点，否则无法点击
			pop.setFocusable(true);
			location = new int[2];
			view.getLocationOnScreen(location);
			pop.showAsDropDown(view, location[0], -view.getMeasuredHeight());
		};
	};
	View dv;

	/**
	 * 删除信用卡
	 * 
	 * @return
	 */
	private void deleteBankCard(String id) {

		RequestParams params = new RequestParams();
		String url = MyUrls.ROOT_URL_CREDIT_HANDLE;

		map = new HashMap<String, String>();
		map.put("token", MApplication.mSharedPref
				.getSharePrefString(SharedPrefConstant.TOKEN));

		map.put("username", MApplication.mSharedPref
				.getSharePrefString(SharedPrefConstant.USERNAME));
		map.put("id", id);
		map.put("Cmd", "DeleteCard");

		String json = JSON.toJSONString(map);
		showLoadingDialog();
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

				T.ss("操作超时!");
				dismissLoadingDialog();
			}

			@Override
			public void onSuccess(ResponseInfo<String> response) {
				// TODO Auto-generated method stub
				// dismissLoadingDialog();
				String str = response.result;
				Log.i("result", "---------------信用卡-returnjson---" + str);
				String code = "";
				String message = "";
				JSONObject obj;
				dismissLoadingDialog();
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
					// adapter.notifyDataSetChanged();
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
		RequestParams params = new RequestParams();
		String url = MyUrls.ROOT_URL_CREDIT_HANDLE;
		showLoadingDialog();
		map = new HashMap<String, String>();
		map.put("username", MApplication.mSharedPref
				.getSharePrefString(SharedPrefConstant.USERNAME));
		map.put("token", MApplication.mSharedPref
				.getSharePrefString(SharedPrefConstant.TOKEN));
		map.put("Cmd", "SelectCard");

		String json = JSON.toJSONString(map);
		Log.i("result", "-------信用卡list请求-----" + json);
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
				T.ss("操作超时!");
				lv.onRefreshComplete();
			}

			@Override
			public void onSuccess(ResponseInfo<String> response) {
				// TODO Auto-generated method stub
				dismissLoadingDialog();
				String strReturnLogin = response.result;
				Log.i("result", "---------------信用卡-returnjson---"
						+ strReturnLogin);

				jsonDetail(strReturnLogin);

				String returnCode = info.getCode();

				if (returnCode.equals("00")) {

					if (listReturn == null || listReturn.size() == 0) {
						// T.ss(getResources().getString(R.string.query_nothing));
						adapter.sendSata(list);
						adapter.notifyDataSetChanged();
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
				lv.onRefreshComplete();
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

			int count = obj.optInt("Count");
			if (count > 0) {

				for (int i = 0; i < count; i++) {
					JSONObject jsObj = obj.optJSONArray("data")
							.optJSONObject(i);
					// 给info设置数据
					Xinyongkainfo xykinfo = new Xinyongkainfo();
					xykinfo.setUserName("" + jsObj.optString("name"));// 用户名
					xykinfo.setCardNum("" + jsObj.optString("bankcard"));// 卡号
					xykinfo.setImgUrl("" + jsObj.optString("logo"));// 图片url
					String type = jsObj.optString("card_state");
					xykinfo.setType("" + type);// 还款状态
					xykinfo.setBankName("" + jsObj.optString("bank"));// 银行名
					xykinfo.setBankmoney("" + jsObj.optString("bankmoney"));// 信用卡额度
					String billmoney = "";
					if (type.equals("5")) {// 状态为5时显示账单金额
						billmoney = jsObj.optString("billmoney");// 格式化金额
						// billmoney = StringUtils.moneyFormat(
						// jsObj.optString("billmoney"), 4);// 格式化金额
					} else {
						billmoney = jsObj.optString("billmoney");
					}
					xykinfo.setBillmoney("" + billmoney);// 账单金额
					xykinfo.setId("" + jsObj.optString("id"));
					xykinfo.setBankCode("" + jsObj.optString("bank_code"));
					xykinfo.setIdCardNum("" + jsObj.optString("idcardno"));
					xykinfo.setPhoneNum("" + jsObj.optString("phone"));
					xykinfo.setAddress("" + jsObj.optString("get_address"));
					xykinfo.setRefundDay("" + jsObj.optString("refund_day"));
					xykinfo.setCardCode("" + jsObj.optString("card_code"));
					String reimmoney = "";
					if (type.equals("6")) {// 状态为6时显示已贷金额
						reimmoney = jsObj.optString("reimmoney");// 格式化金额
//						reimmoney = StringUtils.moneyFormat(
//								jsObj.optString("reimmoney"), 4);// 格式化金额
					} else {
						reimmoney = jsObj.optString("reimmoney");
					}
					xykinfo.setReimmoney("" + reimmoney);
					xykinfo.setPoundage("" + jsObj.optString("poundage"));
					xykinfo.setBilltime("" + jsObj.optString("billtime"));
					xykinfo.setReimtime("" + jsObj.optString("reimtime"));
					listReturn.add(xykinfo);
				}
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void leftOnclick() {
		// TODO Auto-generated method stub
		myDialog.dismiss();
		currentItemView.setAlpha(200);
		// currentItemView.setBackgroundColor(getResources().getColor(android.R.color.white));
		// list.remove(longClickPosition);
		// adapter.notifyDataSetChanged();
	}

	@Override
	public void rightOnclick() {
		// TODO Auto-generated method stub
		myDialog.dismiss();
		currentItemView.setAlpha(200);
		// list.remove(longClickPosition);
		// adapter.notifyDataSetChanged();
		String id = list.get(longClickPosition).getId();
		deleteBankCard(id);
	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		// TODO Auto-generated method stub
		postQueryBankCardListHttp();
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		// TODO Auto-generated method stub

	}
}
