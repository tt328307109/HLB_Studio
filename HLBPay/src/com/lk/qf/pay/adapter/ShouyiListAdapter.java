package com.lk.qf.pay.adapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.lk.bhb.pay.R;
import com.lk.qf.pay.beans.TradingRunInfo;
import com.lk.qf.pay.beans.OrderInfo;
import com.lk.qf.pay.beans.IncomeInfo;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ShouyiListAdapter extends MyBaseAdapter<IncomeInfo> {

	public ShouyiListAdapter(Context context, List<IncomeInfo> list) {
		super(context, list);
		// TODO Auto-generated constructor stub
	}

	public void sendSata(List<IncomeInfo> list) {
		this.list = list;
		Log.i("result", "----------list---------" + list.size());
	}

	@Override
	public View getView(int position, View view, ViewGroup arg2) {
		// TODO Auto-generated method stub
		ViewHolder holder;
		if (view == null) {
			holder = new ViewHolder();
			view = inflater.inflate(R.layout.sylist_item_layout, null);

			holder.tvTradingAccount = (TextView) view
					.findViewById(R.id.tv_sylist_item_jyAccount);
			holder.tvLevel = (TextView) view
					.findViewById(R.id.tv_sylist_item_level);
			holder.tvIncomeAccount = (TextView) view
					.findViewById(R.id.tv_sylist_item_syAccount);
			holder.tvTime = (TextView) view
					.findViewById(R.id.tv_sylist_item_time);

			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}
		IncomeInfo info = list.get(position);
		String strDate = info.getTime();
		String strDateYear ="";
		String strDateMin ="";
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sdf3 = new SimpleDateFormat("HH:mm:ss");
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMddHHmmss");

		try {
			strDateYear = sdf1.format(sdf2.parse(strDate));
			strDateMin = sdf3.format(sdf2.parse(strDate));
			Log.i("result", "---------strDateMin------------"+strDateMin);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		holder.tvTradingAccount.setText(info.getTradingAccount());
		String level = info.getLevel();
		if (level.equals("1")) {
			level = "一级";
		}else if (level.equals("2")){
			level = "二级";
		}else{
			
			level = "三级";
		}
		holder.tvLevel.setText(level);
		holder.tvIncomeAccount.setText(info.getIncomeAccount());
		holder.tvTime.setText(strDateYear+"\n"+strDateMin);

		return view;
	}

	class ViewHolder {
		TextView tvTradingAccount;
		TextView tvLevel;
		TextView tvIncomeAccount;
		TextView tvTime;

	}

}
