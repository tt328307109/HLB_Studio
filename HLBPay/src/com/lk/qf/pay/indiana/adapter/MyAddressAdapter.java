package com.lk.qf.pay.indiana.adapter;

import java.util.List;
import com.lk.bhb.pay.R;
import com.lk.qf.pay.adapter.MyBaseAdapter;
import com.lk.qf.pay.indiana.bean.IndianaGoodsInfo;
import com.lk.qf.pay.wedget.MyClickListener;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class MyAddressAdapter extends MyBaseAdapter<IndianaGoodsInfo> {

	private MyClickListener mListener;

	public MyAddressAdapter(Context context, List<IndianaGoodsInfo> list,
			MyClickListener mListener) {
		super(context, list);
		// TODO Auto-generated constructor stub
		this.mListener = mListener;
	}

	public void sendSata(List<IndianaGoodsInfo> list) {
		this.list = list;
		Log.i("result", "----------list---------" + list.size());
	}

	@Override
	public View getView(int position, View view, ViewGroup arg2) {
		// TODO Auto-generated method stub
		ViewHolder holder;
		if (view == null) {
			holder = new ViewHolder();
			view = inflater.inflate(R.layout.indiana_addr_list_item_layout,
					null);

			holder.tvUserName = (TextView) view
					.findViewById(R.id.ed_indiana_addr_Name);
			holder.tvUserPhoneNum = (TextView) view
					.findViewById(R.id.ed_indiana_addr_phoneNum);
			holder.tvUserAddr = (TextView) view
					.findViewById(R.id.ed_indiana_addr_addressInfo);
			holder.tvUserAddrName = (TextView) view
					.findViewById(R.id.tv_indiana_addr_address1);
			holder.tvEdit = (TextView) view
					.findViewById(R.id.tv_indiana_addr_editor);
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}
		IndianaGoodsInfo info = list.get(position);

		holder.tvUserName.setText(info.getUserName());
		holder.tvUserPhoneNum.setText(info.getUserPhoneNum());
		holder.tvUserAddr.setText(info.getUserAddress());
		holder.tvUserAddrName.setText("" + (position+1));
		holder.tvEdit.setTag(position);
		holder.tvEdit.setOnClickListener(mListener);
		return view;
	}

	class ViewHolder {

		TextView tvUserName;
		TextView tvUserPhoneNum;
		TextView tvUserAddr;
		TextView tvUserAddrName;
		TextView tvEdit;

	}

}
