package com.lk.qf.pay.adapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import com.lk.bhb.pay.R;
import com.lk.qf.pay.beans.OrderInfo;
import com.lk.qf.pay.utils.TimeUtils;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class TixianListAdapter extends MyBaseAdapter<OrderInfo> {

	public TixianListAdapter(Context context, List<OrderInfo> list) {
		super(context, list);
		// TODO Auto-generated constructor stub
	}

	public void sendSata(List<OrderInfo> list) {
		this.list = list;
		Log.i("result", "----------list---------" + list.size());
	}

	@Override
	public View getView(int position, View view, ViewGroup arg2) {
		// TODO Auto-generated method stub
		ViewHolder holder;
		if (view == null) {
			holder = new ViewHolder();
			view = inflater.inflate(R.layout.tixian_item_new_layout, null);

			holder.tvTime = (TextView) view
					.findViewById(R.id.tv_tixian_item_new_time1);
			holder.tvType = (TextView) view
					.findViewById(R.id.tv_tixian_item_new_type1);
			holder.tvConsumptionAmount = (TextView) view
					.findViewById(R.id.tv_tixian_item_new_money1);
			holder.tvTax = (TextView) view
					.findViewById(R.id.tv_tixian_item_new_poundage1);
			holder.tvWeek = (TextView) view
					.findViewById(R.id.tv_tixian_item_new_day);
			holder.tvWay = (TextView) view
					.findViewById(R.id.tv_tixian_item_new_way1);

			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}

		OrderInfo info = list.get(position);

		String strDate = info.getTradingTime();
		strDate = TimeUtils.changeDateFormat("yyyy-MM-dd HH:mm", strDate);
		holder.tvTime.setText(strDate);
		int week = 0;
		try {
			week = TimeUtils.dayForWeek(info.getTradingTime().substring(0, 8));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		switch (week) {
		case 1:
			holder.tvWeek.setText("星期一");
			break;
		case 2:
			holder.tvWeek.setText("星期二");

			break;
		case 3:
			holder.tvWeek.setText("星期三");

			break;
		case 4:
			holder.tvWeek.setText("星期四");

			break;
		case 5:
			holder.tvWeek.setText("星期五");

			break;
		case 6:
			holder.tvWeek.setText("星期六");

			break;
		case 7:
			holder.tvWeek.setText("星期七");

			break;

		default:
			break;
		}
		String state = info.getState();
		if (state.equals("ok")) {
			state = "成功";
		} else if (state.equals("fail")) {

			state = "失败";
		} else if (state.equals("deal")) {

			state = "处理中";
		} else if (state.equals("norm")) {
			holder.tvType.setTextColor(context.getResources().getColor(R.color.credit_card_type));
			state = "未处理";
		} else if (state.equals("frezz")) {

			state = "冻结";
		}
		holder.tvType.setText(state);
		holder.tvConsumptionAmount.setText("￥" + info.getConsumptionAmount());
		String t1Ort0 = info.getT0Ort1();
		if (t1Ort0.equals("T0")) {
			holder.tvWay.setText("T+0提现");
		} else if (t1Ort0.equals("T1")){
			holder.tvWay.setText("T+1提现");
		}
		holder.tvTax.setText("￥" + info.getSxAccount());
		
		return view;
	}

	class ViewHolder {
		TextView tvTime;// 时间
		TextView tvWeek;// 星期几
		TextView tvWay;// 方式
		TextView tvType;// 状态
		TextView tvConsumptionAmount;// 金额
		TextView tvTax;// 手续费
	}

}
