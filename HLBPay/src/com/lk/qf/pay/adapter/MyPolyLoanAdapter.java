package com.lk.qf.pay.adapter;

import java.util.List;
import com.lk.bhb.pay.R;
import com.lk.qf.pay.beans.LicaiNewGoodsInfo;
import com.lk.qf.pay.beans.PolyLoanListInfo;
import com.lk.qf.pay.utils.TimeUtils;
import com.lk.qf.pay.wedget.MyClickListener;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class MyPolyLoanAdapter extends BaseAdapter {

	private MyClickListener mListener;
	private List<PolyLoanListInfo> list;
	private Context context;
	private LayoutInflater inflater;

	public MyPolyLoanAdapter(Context context, List<PolyLoanListInfo> list,
			MyClickListener mListener) {
		// TODO Auto-generated constructor stub
		this.mListener = mListener;
		this.list = list;
		this.context = context;
		inflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list == null ? 0 : list.size();
	}

	public void sendSata(List<PolyLoanListInfo> list) {
		this.list = list;
		notifyDataSetChanged();
	}

	@Override
	public View getView(int position, View view, ViewGroup arg2) {
		// TODO Auto-generated method stub
		ViewHolder holder;
		if (view == null) {
			holder = new ViewHolder();
			view = inflater.inflate(R.layout.my_loan_list_item_layout, null);

			holder.tvLoanAccount = (TextView) view
					.findViewById(R.id.tv_poly_loan_myAccount);
			holder.tvLoanTime = (TextView) view
					.findViewById(R.id.tv_poly_loan_time);
			holder.tvLoanOrderNum = (TextView) view
					.findViewById(R.id.tv_poly_loan_orderNum);
			holder.btnType = (Button) view
					.findViewById(R.id.btn_poly_loan_type);
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}
		PolyLoanListInfo info = list.get(position);
		String time = TimeUtils.changeDateFormat("yyyy/MM/dd",
				info.getLoanTime());
		holder.tvLoanAccount.setText(info.getLoanAccount());
		holder.tvLoanOrderNum.setText("单号: " + info.getLoanOrderNum());
		holder.tvLoanTime.setText(time);

		String hkType = info.getLoanType();
		if (hkType.equals("0")) {
			holder.btnType.setText("还款");
			holder.btnType
					.setBackgroundResource(R.drawable.loan_huankuan_selector);
		} else {
			holder.btnType.setText("已还款");
			holder.btnType
					.setBackgroundResource(R.drawable.loan_yihuan_selector);
		}
		holder.btnType.setTag(position);
		holder.btnType.setOnClickListener(mListener);
		Log.i("result", "----------list--view---"+(view==null?0:1));
		return view;
	}

	class ViewHolder {
		TextView tvLoanAccount;
		TextView tvLoanTime;
		TextView tvLoanType;
		TextView tvLoanOrderNum;
		Button btnType;

	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return list.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

}
