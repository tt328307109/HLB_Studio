package com.lk.qf.pay.indiana.fragment;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
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
import com.lk.qf.pay.aanewactivity.creditcard.ApplyRepaymentDetailActivity;
import com.lk.qf.pay.fragment.BaseFragment;
import com.lk.qf.pay.golbal.MApplication;
import com.lk.qf.pay.golbal.MyUrls;
import com.lk.qf.pay.indiana.activity.IndianaGoodsInfoActivity;
import com.lk.qf.pay.indiana.activity.MyPwdActivity;
import com.lk.qf.pay.indiana.adapter.IndianaBuyCarListAdapter;
import com.lk.qf.pay.indiana.adapter.IndianaGoodsListAdapter;
import com.lk.qf.pay.indiana.bean.IndianaGoodsInfo;
import com.lk.qf.pay.indiana.myinterface.MyEditTextListener;
import com.lk.qf.pay.sharedpref.SharedPrefConstant;
import com.lk.qf.pay.tool.T;
import com.lk.qf.pay.utils.ImgOptions;
import com.lk.qf.pay.utils.MyGetStatusUtils;
import com.lk.qf.pay.utils.MyMdFivePassword;
import com.lk.qf.pay.wedget.MyClickListener;
import com.lk.qf.pay.wedget.view.DialogWidget;
import com.lk.qf.pay.wedget.view.PassWdDialog;
import com.lk.qf.pay.wedget.view.PayListener;
import com.lk.qf.pay.wedget.view.PayPasswordView;
import com.lk.qf.pay.wedget.view.PayPasswordView.OnPayListener;
import com.msafepos.sdk.PBOC.TVRBit;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class ListlingFragment extends BaseFragment implements
		OnRefreshListener2<ListView>, MyEditTextListener , PayListener {

	private View layoutView;
	private PullToRefreshListView lv;
	private DisplayImageOptions options;
	private ImageLoader imageLoader;
	private List<IndianaGoodsInfo> list;
	private List<IndianaGoodsInfo> listReturn;
	private int page = 1;
	private int pageSize = 15;
	private Map<String, String> map;
	private IndianaGoodsInfo orderInfo;// 登录后返回的用户信息
	private IndianaBuyCarListAdapter adapter;
	private IndianaGoodsInfo info;
	private Button btnToBuy;
	private Context context;
	private LinearLayout llBottom, llDefault;
	private int goodsNum, goodsTotalAccount = 0;
	private TextView tvTotalAccount, tvGoodsNum, tvGotoSee;
	private DialogWidget mDialogWidget;
	private String pwd;
	private int size;
	private boolean isGo;
	private LinearLayout llBack;
	PassWdDialog mPassWdDialog;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		layoutView = inflater.inflate(R.layout.indiana_buycar_layout,
				container, false);
		init();
		return layoutView;
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Log.i("result", "------------onResume-------");
	}

	private void init() {
		context = getActivity();
		imageLoader = ImageLoader.getInstance();
		imageLoader.init(ImageLoaderConfiguration.createDefault(context));
		options = ImgOptions.initImgOptions();
		llDefault = (LinearLayout) layoutView
				.findViewById(R.id.ll_indiana_buycar_default);
		llBottom = (LinearLayout) layoutView
				.findViewById(R.id.ll_buyCar_bottom);
		tvTotalAccount = (TextView) layoutView
				.findViewById(R.id.tv_indaina_bc_total_account);
		tvGoodsNum = (TextView) layoutView
				.findViewById(R.id.tv_indaina_bc_total_buyNum);
		tvGotoSee = (TextView) layoutView.findViewById(R.id.tv_goShop);
		btnToBuy = (Button) layoutView.findViewById(R.id.btn_indaina_bc_goPay);
		llBack = (LinearLayout) layoutView.findViewById(R.id.ll_buyCar_back2);
		llBack.setVisibility(View.GONE);
		btnToBuy.setOnClickListener(clickListener);
		list = new ArrayList<IndianaGoodsInfo>();
		postQueryBuyCarList();
		lv = (PullToRefreshListView) layoutView
				.findViewById(R.id.ll_indiana_buycar_list);
		adapter = new IndianaBuyCarListAdapter(context, list, mListener,
				options, imageLoader);
		adapter.setlistener(this);
		lv.setAdapter(adapter);
		
		layoutView.findViewById(R.id.rl_indiana_buycar).setPadding(0,
				MyGetStatusUtils.getStatusBarHeight(getActivity()), 0, 0);

	}

	OnClickListener clickListener = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			switch (arg0.getId()) {
			case R.id.btn_indaina_bc_goPay:
				
				if (goodsTotalAccount > 0) {
					size = list.size();
					isGo = true;
//					Intent intent = new Intent(getActivity(),
//							MyPwdActivity.class);
//					intent.putExtra("account", "" + goodsTotalAccount);
//					startActivityForResult(intent, 000);
//					 mDialogWidget = new DialogWidget(getActivity(),
//					 getDecorViewDialog());
//					 mDialogWidget.show();
					//弹出对话框
					mPassWdDialog = PassWdDialog.getInstance(getActivity() ,
							goodsTotalAccount + ""	, PassWdDialog.INDIANA_MARK);
					mPassWdDialog.setPayListener(ListlingFragment.this);
					mPassWdDialog.show();
				} else {
					T.ss("购买数量不能为0");
				}

				break;

			default:
				break;
			}
		}
	};

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {
		case 000:
			if (resultCode == Activity.RESULT_OK) {
				pwd = data.getStringExtra("pwd");
				Log.i("result", "------pwd-----" + pwd);
				String goodsId = list.get(0).getGoodsId2();
				int buyNum = list.get(0).getUserCanyuNum();
				int remainingNum = list.get(0).getRemainingNum();
				inOrder(goodsId, buyNum, buyNum,remainingNum);
			}
			break;

		default:
			break;
		}
	}

	/**
	 * listview中button的事件
	 */
	private MyClickListener mListener = new MyClickListener() {
		@Override
		public void myOnClick(int positio, View v) {
			Object tag = v.getTag();
			IndianaGoodsInfo info = list.get(positio);
			switch (v.getId()) {
			case R.id.ib_indiana_bc_Add: // 点击添加数量按钮，执行相应的处理
				// 获取 Adapter 中设置的 Tag
				if (tag != null && tag instanceof Integer) { // 解决问题：如何知道你点击的按钮是哪一个列表项中的，通过Tag的position
					int position = (Integer) tag;
					// 更改集合的数据
					int num = list.get(position).getUserCanyuNum();
					num++;
					goodsTotalAccount++;
					if (num > list.get(position).getRemainingNum()) {
						return;
					}
					list.get(position).setUserCanyuNum(num); // 修改集合中商品数量
					goodsNum = num;
					// addToBuyCar(list.get(position).getGoodsId(), goodsNum,
					// goodsNum, "Updata");
					tvTotalAccount.setText(""+goodsTotalAccount+"个夺宝币");
					// list.get(position).setPrice(position*num); //修改集合中该商品总价
					// 数量*单价
					// 解决问题：点击某个按钮的时候，如果列表项所需的数据改变了，如何更新UI
					Log.i("result",
							"--------------ss--------"
									+ list.get(position).getGoodsTotal());
					adapter.notifyDataSetChanged();
					addToBuyCar(list.get(position).getGoodsId2(), goodsNum,
							goodsNum, "Updata");
				}
				break;
			case R.id.ib_indiana_bc_Reduction: // 点击减少数量按钮 ，执行相应的处理
				// 获取 Adapter 中设置的 Tag
				if (tag != null && tag instanceof Integer) {
					int position = (Integer) tag;
					// 更改集合的数据
					int num = list.get(position).getUserCanyuNum();
					if (num > 1) {
						num--;
						goodsTotalAccount--;
						goodsNum = num;
						list.get(position).setUserCanyuNum(num); // 修改集合中商品数量
						// addToBuyCar(list.get(position).getGoodsId(),
						// goodsNum, goodsNum, "Updata");
						tvTotalAccount.setText(""+goodsTotalAccount+"个夺宝币");
						adapter.notifyDataSetChanged();
						addToBuyCar(list.get(position).getGoodsId2(), goodsNum,
								goodsNum, "Updata");
					}
				}
				break;
			case R.id.indiana_bc_delete:
				// 获取 Adapter 中设置的 Tag
				if (tag != null && tag instanceof Integer) {
					int position = (Integer) tag;
					// 更改集合的数据
					String goodsId = list.get(position).getGoodsId();
					addToBuyCar(goodsId, 0, 0, "Delete");
				}
				break;
			case R.id.img_indiana_bc_icon:
				Intent intent = new Intent(getActivity(),
						IndianaGoodsInfoActivity.class);
				intent.putExtra("goodsInfo", info);
				intent.setAction("buycar");
				startActivity(intent);
				break;
			}
		}
	};

	/**
	 * 查询购物车列表
	 */
	private void postQueryBuyCarList() {
		showLoadingDialog();
		RequestParams params = new RequestParams();
		String url = MyUrls.ROOT_URL_INDIANA_CART;
		map = new HashMap<String, String>();
		map.put("username", MApplication.mSharedPref
				.getSharePrefString(SharedPrefConstant.USERNAME));
		map.put("gmCount", "");// 购买商品数量
		map.put("goodsId", "");// 商品id
		map.put("tbCoin", "");// 商品单价
		map.put("totalCoin", "");// 需要支付中的夺宝币
		map.put("Cmd", "Select");// 操作命令Add(添加),Updata(更新),Delete(删除)
		map.put("token", MApplication.mSharedPref
				.getSharePrefString(SharedPrefConstant.TOKEN));
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
				dismissLoadingDialog();
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
						llDefault.setVisibility(View.VISIBLE);
						tvGotoSee.setVisibility(View.GONE);
						lv.setVisibility(View.GONE);
						llBottom.setVisibility(View.GONE);
					} else {
						llBottom.setVisibility(View.VISIBLE);
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

	/**
	 * 解析 Json字符串
	 * 
	 * @param str
	 * @return
	 */
	private void jsonDetail(String str) {

		try {
			JSONObject obj = new JSONObject(str);
			info = new IndianaGoodsInfo();
			info.setCode(obj.optString("CODE"));
			info.setMessage(obj.optString("MESSAGE"));
			listReturn = new ArrayList<IndianaGoodsInfo>();
			int count = obj.optInt("Count");
			// goodsTotalAccount = count;
			// tvTotalAccount.setText("¥" + goodsTotalAccount);
			tvGoodsNum.setText("共计" + count + "件产品");
			Log.i("result", "---------Count-------" + count);
			if (count > 0) {
				int allNum = 0;
				for (int i = 0; i < count; i++) {
					String goodsName = obj.optJSONArray("date")
							.optJSONObject(i).optString("goodsName");
					String imgUrl = obj.optJSONArray("date").optJSONObject(i)
							.optString("imgurl1");
					String imgUrl2 = obj.optJSONArray("date").optJSONObject(i)
							.optString("imgurl3");
					String imgUrl3 = obj.optJSONArray("date").optJSONObject(i)
							.optString("imgurl4");
					String imgUrl4 = obj.optJSONArray("date").optJSONObject(i)
							.optString("imgurl5");
					String imgUrl5 = obj.optJSONArray("date").optJSONObject(i)
							.optString("imgurl6");
					int totalNum = obj.optJSONArray("date").optJSONObject(i)
							.optInt("count");// 总需
					int currentNum = obj.optJSONArray("date").optJSONObject(i)
							.optInt("current_count");// 当前购买
					String id = obj.optJSONArray("date").optJSONObject(i)
							.optString("id");
					String goodsId = obj.optJSONArray("date").optJSONObject(i)
							.optString("goodsId");
					Log.i("result", "-------id-----" + id);
					Log.i("result", "-------goodsId-----" + goodsId);
					int userBuyNum = obj.optJSONArray("date").optJSONObject(i)
							.optInt("gmCount");// 购买数量
					String tbCoin = obj.optJSONArray("date").optJSONObject(i)
							.optString("tbCoin");// 单价
					String totalCoin = obj.optJSONArray("date")
							.optJSONObject(i).optString("totalCoin");
					String describe = obj.optJSONArray("date").optJSONObject(i)
							.optString("describe");
					allNum += userBuyNum;
					// 给info设置数据
					IndianaGoodsInfo info = new IndianaGoodsInfo();
					info.setGoodsName(goodsName);
					info.setImgUrl(imgUrl);
					info.setImgUrl2(imgUrl2);
					info.setImgUrl3(imgUrl3);
					info.setImgUrl4(imgUrl4);
					info.setImgUrl5(imgUrl5);
					info.setGoodsTotal(totalNum);
					info.setUserCanyuNum(userBuyNum);
					info.setTotalAccount(totalCoin);
					info.setRemainingNum(totalNum - currentNum);
					info.setGoodsPrice(tbCoin);
					info.setGoodsId(id);// id
					info.setGoodsId2(goodsId);// goodsid
					info.setDescribe(describe);
					listReturn.add(info);
				}
				tvTotalAccount.setText(""+allNum+"个夺宝币");
				goodsTotalAccount = allNum;
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * 下单
	 */
	private void inOrder(String id, int buyNum, int totalCoin, int totalNum) {
		if (buyNum > totalNum) {
			T.ss("购买失败,剩余商品数量小于购买数量!");
			return;
		}
		RequestParams params = new RequestParams();
		String url = MyUrls.ROOT_URL_INDIANA_INDANA_INORDER;
		map = new HashMap<String, String>();
		map.put("username", MApplication.mSharedPref
				.getSharePrefString(SharedPrefConstant.USERNAME));
		map.put("gmCount", "" + buyNum);
		map.put("goodsId", id);
		map.put("tbCoin", "1");
		map.put("pwd", MyMdFivePassword.MD5(MyMdFivePassword.MD5(pwd)));
		map.put("totalCoin", "" + totalCoin);
		map.put("token", MApplication.mSharedPref
				.getSharePrefString(SharedPrefConstant.TOKEN));
		String json = JSON.toJSONString(map);
		Log.i("result", "--下单-----订单请求-----" + json);
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
				String strReturnLogin = response.result;
				Log.i("result", "---------------定单-returnjson---"
						+ strReturnLogin);
				String code = "", message = "";
				JSONObject obj = null;
				try {
					obj = new JSONObject(strReturnLogin);
					code = obj.optString("CODE");
					message = obj.optString("MESSAGE");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (code.equals("00")) {
					isGo = true;
					Log.i("result", "----------size------------" + size);
					while (isGo && size > 1) {
						String goodsId = list.get(size - 1).getGoodsId2();
						int buyNum = list.get(size - 1).getUserCanyuNum();
						int remainingNum = list.get(size - 1).getRemainingNum();
						inOrder(goodsId, buyNum, buyNum,remainingNum);
						size--;
						isGo = false;
					}
					if (size == 1) {
						T.ss("购买成功");
						postQueryBuyCarList();
					}
				} else {
					T.ss(message);

				}
			}
		});
	}

	/**
	 * 加入购物车
	 * 
	 * @param goodsId
	 *            商品id
	 * @param totalNum
	 *            需要支付的快易币
	 * @param goodsNum
	 *            购买的商品数量
	 * @param cmd
	 *            操作命令Add(添加),Updata(更新),Delete(删除)
	 */
	private void addToBuyCar(String goodsId, int totalNum, int goodsNum,
			String cmd) {
		showLoadingDialog();
		final String strCmd = cmd;
		RequestParams params = new RequestParams();
		String url = MyUrls.ROOT_URL_INDIANA_CART;
		map = new HashMap<String, String>();
		map.put("username", MApplication.mSharedPref
				.getSharePrefString(SharedPrefConstant.USERNAME));
		map.put("gmCount", "" + goodsNum);// 购买商品数量
		map.put("goodsId", goodsId);// 商品id
		map.put("tbCoin", "1");// 商品单价
		map.put("totalCoin", "" + totalNum);// 需要支付的夺宝币
		map.put("Cmd", cmd);
		map.put("token", MApplication.mSharedPref
				.getSharePrefString(SharedPrefConstant.TOKEN));
		String json = JSON.toJSONString(map);
		Log.i("result", "-------加入购物车请求-----" + json);
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
				String strReturnLogin = response.result;
				Log.i("result", "---------------定单-returnjson---"
						+ strReturnLogin);
				String code = "", message = "";
				JSONObject obj = null;
				try {
					obj = new JSONObject(strReturnLogin);
					code = obj.optString("CODE");
					message = obj.optString("MESSAGE");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (!strCmd.equals("Updata")) {
					T.ss(message);
				}
				if (code.equals("00")) {
					postQueryBuyCarList();
				}
				dismissLoadingDialog();
			}
		});
	}

	protected View getDecorViewDialog() {
		// TODO Auto-generated method stub
		return PayPasswordView.getInstance(goodsTotalAccount+"个夺宝币",
				getActivity(), new OnPayListener() {

					@Override
					public void onSurePay(String password) {
						// TODO Auto-generated method stub
						mDialogWidget.dismiss();
						mDialogWidget = null;
						// payTextView.setText(password);
						pwd = password;

						String goodsId = list.get(0).getGoodsId2();
						int buyNum = list.get(0).getUserCanyuNum();
						int remainingNum = list.get(size - 1).getRemainingNum();
						inOrder(goodsId, buyNum, buyNum,remainingNum);
						// for (int i = 0; i < list.size(); i++) {
						// String goodsId = list.get(i).getGoodsId();
						// int buyNum = list.get(i).getUserCanyuNum();
						// inOrder(goodsId, buyNum, buyNum);
						// }
						// postQueryBuyCarList();
					}

					@Override
					public void onCancelPay() {
						// TODO Auto-generated method stub
						mDialogWidget.dismiss();
						mDialogWidget = null;
					}

					@Override
					public void onClose() {
						// TODO Auto-generated method stub
						mDialogWidget.dismiss();
					}
				}).getView();
	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		// TODO Auto-generated method stub
		postQueryBuyCarList();
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		// TODO Auto-generated method stub

	}

	@Override
	public void getBuyNum(int num) {
		// TODO Auto-generated method stub
		Log.i("result", "------------getBuyNum-----------" + num);
		goodsTotalAccount += num;
		tvTotalAccount.setText("" + goodsTotalAccount+"个夺宝币");
	}

	@Override
	public void sure(String pwd) {
		// TODO Auto-generated method stub
		mPassWdDialog.dismiss();
		mPassWdDialog = null;
		// payTextView.setText(password);
		this.pwd = pwd;

		String goodsId = list.get(0).getGoodsId2();
		int buyNum = list.get(0).getUserCanyuNum();
		int remainingNum = list.get(size - 1).getRemainingNum();
		inOrder(goodsId, buyNum, buyNum,remainingNum);
	}

	@Override
	public void cacel() {
		// TODO Auto-generated method stub
		mPassWdDialog.dismiss();
		mPassWdDialog = null;
	}

	@Override
	public void close() {
		
	}

}
