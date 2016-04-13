package com.lk.qf.pay.adapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.lk.bhb.pay.R;
import com.lk.qf.pay.beans.LiCaiListItemInfo;
import com.lk.qf.pay.beans.OrderInfo;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class EarningsListAdapter extends MyBaseAdapter<LiCaiListItemInfo> {

	public EarningsListAdapter(Context context, List<LiCaiListItemInfo> list) {
		super(context, list);
		// TODO Auto-generated constructor stub
	}

	public void sendSata(List<LiCaiListItemInfo> list) {
		this.list = list;
		Log.i("result", "----------list---------" + list.size());
	}

	@Override
	public View getView(int position, View view, ViewGroup arg2) {
		// TODO Auto-generated method stub
		ViewHolder holder;
		if (view == null) {
			holder = new ViewHolder();
			view = inflater.inflate(R.layout.licai_item_layout, null);

			holder.tvTime = (TextView) view
					.findViewById(R.id.tv_licai_item_time);
			holder.tvType = (TextView) view
					.findViewById(R.id.tv_licai_item_type);
			holder.tvLCAccount = (TextView) view
					.findViewById(R.id.tv_licai_item_account);

			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}
		LiCaiListItemInfo info = list.get(position);
		String strDate = info.getTime();
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMddHHmmss");
		try {
			strDate = sdf1.format(sdf2.parse(strDate));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		holder.tvTime.setText(strDate);
		String type = info.getLiCaitype();
		holder.tvType.setText("收益");
		holder.tvLCAccount.setText("￥" + info.getAccount());
		return view;
	}

	class ViewHolder {
		TextView tvTime;
		TextView tvType;
		TextView tvLCAccount;

	}

}
