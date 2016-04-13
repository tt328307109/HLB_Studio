package com.lk.qf.pay.adapter;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
import com.lk.qf.pay.beans.OrderInfo;
import com.lk.qf.pay.golbal.Actions;
import com.lk.qf.pay.golbal.MApplication;
import com.lk.qf.pay.golbal.MyUrls;
import com.lk.qf.pay.sharedpref.SharedPrefConstant;
import com.lk.qf.pay.utils.CreatePayCodeUtils;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class YiFuBaoListAdapter extends MyBaseAdapter<OrderInfo> {

	private String appSign, queryUrl;
	private String action;
	private ViewHolder holder;

	public YiFuBaoListAdapter(Context context, List<OrderInfo> list,
			String action) {
		super(context, list);
		// TODO Auto-generated constructor stub
		this.action = action;
	}

	public void sendSata(List<OrderInfo> list) {
		this.list = list;
	}

	@Override
	public View getView(int position, View view, ViewGroup arg2) {
		// TODO Auto-generated method stub

		if (view == null) {
			holder = new ViewHolder();
			view = inflater.inflate(R.layout.tixian_item_layout, null);

			holder.tvTime = (TextView) view
					.findViewById(R.id.tv_tixian_item_time);
			holder.tvOrderNum = (TextView) view
					.findViewById(R.id.tv_tixian_item_num);
			holder.tvAccount = (TextView) view
					.findViewById(R.id.tv_tixian_item_account);
			holder.tvType = (TextView) view
					.findViewById(R.id.tv_tixian_item_sxAccount);
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}
		OrderInfo info = list.get(position);
		// String strDate = info.getTradingTime();
		// SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy/MM/dd HH:mm");
		// SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMddHHmmss");
		// try {
		// strDate = sdf1.format(sdf2.parse(strDate));
		// } catch (ParseException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		if (action != null) {
			if (action.equals(Actions.ACTION_WEIXIN)) {
				queryUrl = MyUrls.WEIXIN_ORDERPAYPAGE;
			} else if (action.equals(Actions.ACTION_ZHIFUBAO)) {
				queryUrl = MyUrls.ZHIFUBAO_ORDERPAYPAGE;
			} else if (action.equals(Actions.ACTION_BAIDU)) {
				queryUrl = MyUrls.BAIDU_QUERYORDER;

			} else if (action.equals(Actions.ACTION_YIFUBAO)) {
				queryUrl = MyUrls.QUERYORDER;
			}
		}

		holder.tvTime.setText(info.getTradingTime());
		String outTradeNo = info.getOrderNum();
		holder.tvOrderNum.setText(outTradeNo);
		holder.tvAccount.setText("￥" + info.getConsumptionAmount());

		String infomation = info.getTradingInformation();
		Log.i("result","------------position-position----------="+position);
		if (infomation.equals("1")) {
			 holder.tvType.setText("待支付(点击查询)");
		} else if (infomation.equals("0")) {
			 holder.tvType.setText("支付失败");
		} else if (infomation.equals("3")) {
			holder.tvType.setText("订单退款");
		} else if (infomation.equals("4")) {
			Log.i("result","------------款成功-------position----="+infomation);
			holder.tvType.setText("退款成功");
		} else if (infomation.equals("2")) {
			holder.tvType.setText("支付成功");
		}
		return view;
	}

	class ViewHolder {
		TextView tvOrderNum;
		TextView tvTime;
		TextView tvAccount;
		TextView tvType;

	}

//	/**
//	 * 查询支付状态
//	 */
//	private void queryPayType(String outTradeNo) {
//		String phoneNum = MApplication.mSharedPref
//				.getSharePrefString(SharedPrefConstant.USERNAME);
//		String[] str = { "phoneNum=" + phoneNum, "OutTradeNo=" + outTradeNo };
//		appSign = CreatePayCodeUtils.createSign(str);// 签名
//		RequestParams params = new RequestParams();
//		Map<String, String> map = new HashMap<String, String>();
//		map.put("phoneNum", phoneNum);
//		map.put("appSign", appSign);
//		map.put("OutTradeNo", outTradeNo);
//		String json = JSON.toJSONString(map);
//		Log.i("result", "----请求----s-------" + json);
//		try {
//			StringEntity bodyEntity = new StringEntity(json, "UTF-8");
//			params.setBodyEntity(bodyEntity);
//		} catch (UnsupportedEncodingException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//		HttpUtils utils = new HttpUtils();
//		utils.send(HttpMethod.POST, queryUrl, params,
//				new RequestCallBack<String>() {
//
//					@Override
//					public void onFailure(HttpException arg0, String arg1) {
////						holder.tvType.setText("支付失败1");
//					}
//
//					@Override
//					public void onSuccess(ResponseInfo<String> response) {
//						// TODO Auto-generated method stub
//						String code = "";
//						String message = "";
//						String str = response.result;
//						Log.i("result", "----获取----s-------" + str);
//						JSONObject obj = null;
//						try {
//							obj = new JSONObject(str);
//							code = obj.optString("Code");
//							message = obj.optString("Message");
//							Log.i("result", "----qqqqqqqqqqq-------" + message);
////							holder.tvType.setText(message);
//						} catch (JSONException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}
//
//					}
//				});
//	}
}
