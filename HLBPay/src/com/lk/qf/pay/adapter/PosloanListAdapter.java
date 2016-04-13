package com.lk.qf.pay.adapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.lk.bhb.pay.R;
import com.lk.qf.pay.beans.OrderInfo;
import com.lk.qf.pay.beans.PosloanListInfo;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class PosloanListAdapter extends MyBaseAdapter<PosloanListInfo> {

	public PosloanListAdapter(Context context, List<PosloanListInfo> list) {
		super(context, list);
		// TODO Auto-generated constructor stub
	}

	public void sendSata(List<PosloanListInfo> list) {
		this.list = list;
		Log.i("result", "----------list---------" + list.size());
	}

	@Override
	public View getView(int position, View view, ViewGroup arg2) {
		// TODO Auto-generated method stub
		ViewHolder holder;
		if (view == null) {
			holder = new ViewHolder();
			view = inflater.inflate(R.layout.posloan_list_item_layout, null);

			holder.tvTotalAccount = (TextView) view
					.findViewById(R.id.tv_posloan_list_item_totalAccount);
			holder.tvXuhuanAccount = (TextView) view
					.findViewById(R.id.tv_posloan_list_item_xuhuanAccount);
			holder.tvYihuanAccount = (TextView) view
					.findViewById(R.id.tv_posloan_list_item_yihuanAccount);
			holder.tvTime = (TextView) view
					.findViewById(R.id.tv_posloan_list_item_time);
			holder.tvHuankuanDate = (TextView) view
					.findViewById(R.id.tv_posloan_list_item_huankuanDate);
			holder.tvFenqi = (TextView) view
					.findViewById(R.id.tv_posloan_list_item_qixian);
			holder.tvmmhuanAccount = (TextView) view
					.findViewById(R.id.tv_posloan_list_item_meiyueAccount);
			holder.tvstate = (TextView) view
					.findViewById(R.id.tv_posloan_list_item_state);

			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}
		PosloanListInfo info = list.get(position);
		String strDate = info.getHuankuanDate();
		if (strDate.equals("0")) {
			strDate = "审核中";
		} else {

			SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy/MM/dd");
			SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMddHHmmss");
			try {
				strDate = sdf1.format(sdf2.parse(strDate));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		

		holder.tvTotalAccount.setText(info.getTotalAccount());
		holder.tvXuhuanAccount.setText(info.getXuhuanAccount());
		holder.tvYihuanAccount.setText(info.getYihuanAccount());

		String state = info.getState();

		String huankDate = info.getHuankuanDate();
		if (huankDate.equals("0")) {
			huankDate = "审核中";
		} else {

			huankDate = "每月" + huankDate + "日";
		}
		
		holder.tvFenqi.setText(info.getFenqi() + "期");
		holder.tvmmhuanAccount.setText(info.getMmhuanAccount());

		if (state.equals("trial")) {
			state = "初审";
		} else if (state.equals("review")) {

			state = "复审";
		} else if (state.equals("issued")) {
			state = "终审";
		} else if (state.equals("retu")) {
			state = "退回";
			huankDate = "退回";
			strDate = "退回";
		} else {
			state = "通过";
		}
		holder.tvstate.setText(state);
		holder.tvTime.setText(strDate);
		holder.tvHuankuanDate.setText(huankDate);
		return view;
	}

	class ViewHolder {
		TextView tvTotalAccount;
		TextView tvXuhuanAccount;
		TextView tvYihuanAccount;
		TextView tvTime;
		TextView tvHuankuanDate;
		TextView tvFenqi;
		TextView tvmmhuanAccount;
		TextView tvstate;

	}

}
