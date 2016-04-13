package com.lk.qf.pay.adapter;

import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lk.bhb.pay.R;
import com.lk.qf.pay.beans.XYKTradeListInfo;
import com.lk.qf.pay.utils.TimeUtils;

public class CreditCardRepayAdapter extends MyBaseAdapter<XYKTradeListInfo> {

	public CreditCardRepayAdapter(Context context, List<XYKTradeListInfo> list) {
		super(context, list);
		// TODO Auto-generated constructor stub
	}

	public void sendSata(List<XYKTradeListInfo> list) {
		this.list = list;
	}

	@Override
	public View getView(int position, View view, ViewGroup arg2) {
		// TODO Auto-generated method stub
		ViewHolder holder;
		if (view == null) {
			holder = new ViewHolder();
			view = inflater
					.inflate(R.layout.credit_card_repay_list_item_layout, null);
			holder.tvRepayTime = (TextView) view
					.findViewById(R.id.tv_creditCard_repayTime);
//			holder.tvYHAccount = (TextView) view
//					.findViewById(R.id.tv_creditCard_YHAccount);
			holder.tvAMAccount = (TextView) view
					.findViewById(R.id.tv_creditCard_AMAccount);
			holder.tvAMType = (TextView) view
					.findViewById(R.id.tv_creditCard_AMType);
			holder.tvHKTimeType = (TextView) view
					.findViewById(R.id.tv_creditCard_am);
//			holder.tvPMAccount = (TextView) view
//					.findViewById(R.id.tv_creditCard_PMAccount);
//			holder.tvPMType = (TextView) view
//					.findViewById(R.id.tv_creditCard_PMType);

			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}
		XYKTradeListInfo info = list.get(position);
		String time = info.getDate();
		if (time!=null) {
			time = time.substring(0,14);
//			Log.i("result", "---------------time-------"+time);
		}
		holder.tvRepayTime.setText(""+TimeUtils.changeDateFormat("yyyy-MM-dd",time));
//		Log.i("result", "---------------time-s------"+TimeUtils.changeDateFormat("yyyy-MM-dd",time));
//		holder.tvYHAccount.setText(info.getHkAccount());
		holder.tvAMAccount.setText("￥"+info.getHkAccount());//还款金额
		String hkType = info.getType();
		if (hkType.equals("0")) {
			holder.tvAMType.setText("未贷");
			holder.tvAMType.setTextColor(context.getResources().getColor(R.color.credit_repay_mx_tv));
		}else{
			holder.tvAMType.setText("已贷");
			holder.tvAMType.setTextColor(context.getResources().getColor(R.color.credit_card_type));
		}
		String strTimeType = info.getPmType();
		if (strTimeType.equals("AM")) {
			holder.tvHKTimeType.setText("上午");
		}else{
			holder.tvHKTimeType.setText("下午");
		}
//		holder.tvPMAccount.setText(info.getPmAccount());
//		holder.tvPMType.setText(info.getPmType());
		return view;
	}

	class ViewHolder {
		TextView tvRepayTime;// 还款时间
		TextView tvHKTimeType;// 还款时间状态  上午或下午
		TextView tvAMAccount;// 应还金额
		TextView tvAMType;// 还款状态
//		TextView tvPMAccount;// 下午还款状态
//		TextView tvPMType;// 下午还款状态
	}

}
