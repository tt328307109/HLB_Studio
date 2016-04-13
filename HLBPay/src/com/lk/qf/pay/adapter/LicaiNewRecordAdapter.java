package com.lk.qf.pay.adapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import com.lk.bhb.pay.R;
import com.lk.qf.pay.beans.LicaiNewGoodsInfo;
import com.lk.qf.pay.wedget.MyClickListener;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

public class LicaiNewRecordAdapter extends MyBaseAdapter<LicaiNewGoodsInfo> {

	private MyClickListener mListener;

	public LicaiNewRecordAdapter(Context context, List<LicaiNewGoodsInfo> list,
			MyClickListener mListener) {
		super(context, list);
		// TODO Auto-generated constructor stub
		this.mListener = mListener;
	}

	public void sendSata(List<LicaiNewGoodsInfo> list) {
		this.list = list;
		Log.i("result", "----------list---------" + list.size());
	}

	@Override
	public View getView(int position, View view, ViewGroup arg2) {
		// TODO Auto-generated method stub
		ViewHolder holder;
		if (view == null) {
			holder = new ViewHolder();
			view = inflater
					.inflate(R.layout.licai_new_record_item_layout, null);

			holder.tvNameTitle = (TextView) view
					.findViewById(R.id.tv_licai_new_goods_record_title);
			holder.tvYearEaraings = (TextView) view
					.findViewById(R.id.tv_licai_new_goods_record_nh);
			holder.tvTimeLimit = (TextView) view
					.findViewById(R.id.tv_licai_new_goods_record_qx);
			holder.tvBuyAccount = (TextView) view
					.findViewById(R.id.tv_licai_new_goods_buyaccount_record);
			holder.tvBuyTime = (TextView) view
					.findViewById(R.id.tv_licai_record_time);
			holder.btnInfo = (ImageButton) view
					.findViewById(R.id.btn_licai_goods_info);
			holder.tvEarings = (TextView) view
					.findViewById(R.id.tv_show_licaiEaringsAccount);
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}
		LicaiNewGoodsInfo info = list.get(position);

		holder.tvNameTitle.setText(info.getNameTitle());
		holder.tvTimeLimit.setText(info.getTimeLimit());
		holder.tvYearEaraings.setText(info.getYearEarnings() + "%");
		holder.tvBuyAccount.setText(info.getBuyAccount());
		String earnings = info.getEarnings();
		if (earnings!=null && !earnings.equals("")) {
			holder.tvEarings.setText("预期收益: "+earnings);
		}

		String strDate = info.getBuyTime();
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy/MM/dd");
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMddHHmmss");
		try {
			strDate = sdf1.format(sdf2.parse(strDate));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		holder.tvBuyTime.setText(strDate);

		holder.btnInfo.setTag(position);
		holder.btnInfo.setOnClickListener(mListener);
		return view;
	}

	class ViewHolder {
		TextView tvNameTitle;
		TextView tvYearEaraings;
		TextView tvTimeLimit;
		TextView tvBuyAccount;
		TextView tvBuyTime;
		TextView tvEarings;//预期收益
		ImageButton btnInfo;

	}

}
