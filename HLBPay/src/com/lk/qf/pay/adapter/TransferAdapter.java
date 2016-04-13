package com.lk.qf.pay.adapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.lk.bhb.pay.R;
import com.lk.qf.pay.beans.OrderInfo;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class TransferAdapter extends MyBaseAdapter<OrderInfo> {

	public TransferAdapter(Context context, List<OrderInfo> list) {
		super(context, list);
		// TODO Auto-generated constructor stub
	}

	public void sendSata(List<OrderInfo> list) {
		this.list = list;
		Log.i("result", "----------list---------"+list.size());
	}

	@Override
	public View getView(int position, View view, ViewGroup arg2) {
		// TODO Auto-generated method stub
		ViewHolder holder;
		if (view == null) {
			holder = new ViewHolder();
			view = inflater.inflate(R.layout.transfer_mx_item_layout, null);

			holder.tvTime = (TextView) view
					.findViewById(R.id.tv_transfer_item_time);
			holder.tvOrderNum = (TextView) view
					.findViewById(R.id.tv_transfer_item_type);
			holder.tvConsumptionAmount = (TextView) view
					.findViewById(R.id.tv_transfer_item_account);
			holder.tvSXAccount = (TextView) view
					.findViewById(R.id.tv_transfer_item_name);

			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}
		OrderInfo info = list.get(position);
		String strDate = info.getTradingTime();
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	    SimpleDateFormat sdf2=new SimpleDateFormat("yyyyMMddHHmmss");  
	    try {
			strDate=sdf1.format(sdf2.parse(strDate));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		holder.tvTime.setText(strDate);
		String type = info.getOrderNum();
		if (type.equals("ok")) {
			type = "转账成功";
		}
		holder.tvOrderNum.setText(type);//状态
		holder.tvConsumptionAmount.setText(info.getTradingInformation()+info.getConsumptionAmount());//金额
		holder.tvSXAccount.setText(info.getSxAccount());//姓名
		return view;
	}

	class ViewHolder {
		TextView tvTime;
		TextView tvOrderNum;
		TextView tvConsumptionAmount;
		TextView tvSXAccount;

	}

}
