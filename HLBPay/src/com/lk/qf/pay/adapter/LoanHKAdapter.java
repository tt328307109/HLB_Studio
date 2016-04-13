package com.lk.qf.pay.adapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import com.lk.bhb.pay.R;
import com.lk.qf.pay.beans.LiCaiListItemInfo;
import com.lk.qf.pay.beans.LoanHuanInfo;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class LoanHKAdapter extends MyBaseAdapter<LoanHuanInfo> {

	public LoanHKAdapter(Context context, List<LoanHuanInfo> list) {
		super(context, list);
		// TODO Auto-generated constructor stub
	}

	public void sendSata(List<LoanHuanInfo> list) {
		this.list = list;
		Log.i("result", "----------list---------" + list.size());
	}

	@Override
	public View getView(int position, View view, ViewGroup arg2) {
		// TODO Auto-generated method stub
		ViewHolder holder;
		if (view == null) {
			holder = new ViewHolder();
			view = inflater.inflate(R.layout.loan_hk_litem_layout, null);

			holder.tvTime = (TextView) view
					.findViewById(R.id.tv_polyloan_hk_time);
			holder.tvType = (TextView) view
					.findViewById(R.id.tv_polyloan_hk_type);
			holder.tvAccount = (TextView) view
					.findViewById(R.id.tv_polyloan_hk_account);
			holder.tvOrderNum = (TextView) view
					.findViewById(R.id.tv_polyloan_hk_state);

			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}
		LoanHuanInfo info = list.get(position);
		String strDate = info.getHkTime();
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMddHHmmss");
		try {
			strDate = sdf1.format(sdf2.parse(strDate));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		holder.tvTime.setText(strDate);
		String type = info.getHkType();
		if (type.equals("ben")) {
			type = "本金";
		} else {
			type = "利息";
		}
		holder.tvType.setText(type);
		holder.tvOrderNum.setText(info.getDingdanNum());
		holder.tvAccount.setText("-"+info.getReimMoney());
		return view;
	}

	class ViewHolder {
		TextView tvTime;
		TextView tvType;
		TextView tvAccount;
		TextView tvOrderNum;

	}

}
