package com.lk.qf.pay.adapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import com.lk.bhb.pay.R;
import com.lk.qf.pay.beans.KefuProblemInfo;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class KefuListAdapter extends MyBaseAdapter<KefuProblemInfo> {

	public KefuListAdapter(Context context, List<KefuProblemInfo> list) {
		super(context, list);
		// TODO Auto-generated constructor stub
	}

	public void sendSata(List<KefuProblemInfo> list) {
		this.list = list;
	}

	@Override
	public View getView(int position, View view, ViewGroup arg2) {
		// TODO Auto-generated method stub
		ViewHolder holder;
		if (view == null) {
			holder = new ViewHolder();
			view = inflater.inflate(R.layout.kefu_item_layout, null);

			holder.tvAddTime = (TextView) view
					.findViewById(R.id.tv_kefu_item_addTime);
			holder.tvProblem = (TextView) view
					.findViewById(R.id.tv_kefu_item_problem);
			holder.tvState = (TextView) view
					.findViewById(R.id.tv_kefu_item_state);

			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}

		KefuProblemInfo info = list.get(position);

		String strDate = info.getAddTime();
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMddHHmmss");
		try {
			strDate = sdf1.format(sdf2.parse(strDate));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		holder.tvAddTime.setText(strDate);
		String state = info.getState();
		if (state.equals("0")) {
			state = "已处理";
		} else {
			state = "处理中";
		}

		holder.tvState.setText(state);
		holder.tvProblem.setText("问题: "+info.getProblem());

		return view;
	}

	class ViewHolder {
		TextView tvAddTime;
		TextView tvProblem;
		TextView tvState;
	}

}
