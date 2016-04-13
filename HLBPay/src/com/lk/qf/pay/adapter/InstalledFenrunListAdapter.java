package com.lk.qf.pay.adapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.lk.bhb.pay.R;
import com.lk.qf.pay.beans.TradingRunInfo;
import com.lk.qf.pay.beans.OrderInfo;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class InstalledFenrunListAdapter extends MyBaseAdapter<TradingRunInfo> {

	public InstalledFenrunListAdapter(Context context, List<TradingRunInfo> list) {
		super(context, list);
		// TODO Auto-generated constructor stub
	}

	public void sendSata(List<TradingRunInfo> list) {
		this.list = list;
		Log.i("result", "----------list---------"+list.size());
	}

	@Override
	public View getView(int position, View view, ViewGroup arg2) {
		// TODO Auto-generated method stub
		ViewHolder holder;
		if (view == null) {
			holder = new ViewHolder();
			view = inflater.inflate(R.layout.jiaoyi_item_layout, null);

			holder.tvTime = (TextView) view
					.findViewById(R.id.tv_install_glfenrun_time);
			holder.tvFenRun1 = (TextView) view
					.findViewById(R.id.tv_install_glfenrun_account);
			holder.tvFenRun2 = (TextView) view
					.findViewById(R.id.tv_install_jyfenrun_account);

			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}
		TradingRunInfo info = list.get(position);
		String strDate = info.getTime();
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
	    SimpleDateFormat sdf2=new SimpleDateFormat("yyyyMMdd");  
	    
	    try {
			strDate=sdf1.format(sdf2.parse(strDate));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		holder.tvTime.setText(strDate);
		holder.tvFenRun1.setText(info.getZjFenRun());//1
		holder.tvFenRun2.setText(info.getJyFenRun());//2
		
		return view;
	}

	class ViewHolder {
		TextView tvTime;
		TextView tvFenRun1;
		TextView tvFenRun2;

	}

}
