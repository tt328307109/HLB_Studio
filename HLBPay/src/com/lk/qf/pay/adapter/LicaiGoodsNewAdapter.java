package com.lk.qf.pay.adapter;

import java.util.List;
import com.lk.bhb.pay.R;
import com.lk.qf.pay.beans.LicaiNewGoodsInfo;
import com.lk.qf.pay.wedget.MyClickListener;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class LicaiGoodsNewAdapter extends MyBaseAdapter<LicaiNewGoodsInfo> {

	private String positionNum;
//	private MyClickListener mListener;
	public LicaiGoodsNewAdapter(Context context, List<LicaiNewGoodsInfo> list) {
		super(context, list);
		// TODO Auto-generated constructor stub
//		this.mListener = mListener;
	}

	public void sendSata(List<LicaiNewGoodsInfo> list) {
		this.list = list;
	}

	@Override
	public View getView(int position, View view, ViewGroup arg2) {
		// TODO Auto-generated method stub
		ViewHolder holder;
		if (view == null) {
			holder = new ViewHolder();
			view = inflater.inflate(R.layout.licai_goods_item_layout, null);

			holder.tvNameTitle = (TextView) view
					.findViewById(R.id.tv_licai_new_goods_title);
			holder.tvYearEaraings = (TextView) view
					.findViewById(R.id.tv_licai_new_goods_nh);
			holder.tvTimeLimit = (TextView) view
					.findViewById(R.id.tv_licai_new_goods_qx);
			holder.tvBuyAccount = (TextView) view
					.findViewById(R.id.tv_licai_new_nowLicai_Account);
			holder.tvQgAccount = (TextView) view
					.findViewById(R.id.tv_licai_new_goods_qg);
//			holder.btnBuy = (ImageButton) view.findViewById(R.id.btn_licai_goods_buy);
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}
		LicaiNewGoodsInfo info = list.get(position);

		holder.tvNameTitle.setText(info.getNameTitle());
		holder.tvTimeLimit.setText(info.getTimeLimit());
		holder.tvYearEaraings.setText(info.getYearEarnings()+"%");
		String buyAccount = info.getBuyAccount();
		double acc =0;
		if (!buyAccount.equals("")&&buyAccount!=null) {
			
			acc = Double.parseDouble(buyAccount);
		}
		if (acc>0) {
			
			holder.tvBuyAccount.setText("正在理财(元): "+buyAccount);
		}else{
			holder.tvBuyAccount.setText("");
		}
		holder.tvQgAccount.setText(info.getQgAccount());
//		holder.btnBuy.setTag(position);
//		holder.btnBuy.setOnClickListener(mListener);
		return view;
	}

	class ViewHolder {
		TextView tvNameTitle;
		TextView tvYearEaraings;
		TextView tvTimeLimit;
		TextView tvBuyAccount;
		TextView tvQgAccount;
//		ImageButton btnBuy;
	}

}
