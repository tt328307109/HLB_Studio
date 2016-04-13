package com.lk.qf.pay.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.lk.bhb.pay.R;
import com.lk.qf.pay.beans.Xinyongkainfo;
import com.lk.qf.pay.wedget.MyClickListener;

public class CreditCardRepayDateAdapter extends MyBaseAdapter<Xinyongkainfo> {

	private MyClickListener mListener;

	public CreditCardRepayDateAdapter(Context context,
			List<Xinyongkainfo> list, MyClickListener mListener) {
		super(context, list);
		// TODO Auto-generated constructor stub
		this.mListener = mListener;
	}

	public void sendSata(List<Xinyongkainfo> list) {
		this.list = list;
	}

	@Override
	public View getView(int position, View view, ViewGroup arg2) {
		// TODO Auto-generated method stub
		ViewHolder holder;
		if (view == null) {
			holder = new ViewHolder();
			view = inflater.inflate(
					R.layout.credit_card_repay_date_item_layout, null);
			holder.tvRepayDateNum = (TextView) view
					.findViewById(R.id.tv_creditCard_repayDate);
			holder.tvHKAccount = (TextView) view
					.findViewById(R.id.tv_creditCard_repaydateAccount);
			holder.tvPoundage = (TextView) view
					.findViewById(R.id.tv_creditCard_repaydatePoundage);
			holder.tvRepayType = (TextView) view
					.findViewById(R.id.tv_creditCard_repaydateType);
			holder.tvToView = (TextView) view
					.findViewById(R.id.tv_creditCard_repaydate_tosee);

			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}
		Xinyongkainfo info = list.get(position);
		holder.tvRepayDateNum.setText(info.getRepayDate());
		holder.tvHKAccount.setText(info.getReimmoney());
		holder.tvPoundage.setText(info.getPoundage());
		String type = info.getType();
		if (type.equals("0")) {
			holder.tvRepayType.setText("未还款");
		}else if(type.equals("1")){
			holder.tvRepayType.setText("贷款中");
		}else if(type.equals("2")){
			holder.tvRepayType.setText("成功");
		}
		holder.tvToView.setTag(position);
		holder.tvToView.setOnClickListener(mListener);
		return view;
	}

	class ViewHolder {
		TextView tvRepayDateNum;// 还款期数
		TextView tvHKAccount;// 还款金额
		TextView tvPoundage;// 手续费
		TextView tvRepayType;// 还款状态
		TextView tvToView;// 查看详情
	}

}
