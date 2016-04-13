package com.lk.qf.pay.adapter;

import java.util.List;
import com.lk.bhb.pay.R;
import com.lk.qf.pay.beans.BankBranch;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class BankZHAdapter extends MyBaseAdapter<BankBranch> {

	public BankZHAdapter(Context context, List<BankBranch> list) {
		super(context, list);
		// TODO Auto-generated constructor stub
	}

	public void sendSata(List<BankBranch> list) {
		this.list = list;
		Log.i("result", "----------list---------" + list.size());
	}

	@Override
	public View getView(int position, View view, ViewGroup arg2) {
		// TODO Auto-generated method stub
		ViewHolder holder;
		if (view == null) {
			holder = new ViewHolder();
			view = inflater.inflate(R.layout.bank_list_layout, null);

			holder.tvName = (TextView) view
					.findViewById(R.id.tv_bankzh_name);

			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}
		BankBranch info = list.get(position);
		holder.tvName.setText(info.getBankbankName());

		return view;
	}

	class ViewHolder {
		TextView tvName;

	}

}
