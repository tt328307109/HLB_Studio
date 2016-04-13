package com.lk.qf.pay.adapter;

import java.util.List;
import com.lk.bhb.pay.R;
import com.lk.qf.pay.beans.AddCommonlyUserTwoInfo;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class AddCommonlyUserAdapter extends
		MyBaseAdapter<AddCommonlyUserTwoInfo> {

	public AddCommonlyUserAdapter(Context context,
			List<AddCommonlyUserTwoInfo> list) {
		super(context, list);
		// TODO Auto-generated constructor stub
	}

	public void sendSata(List<AddCommonlyUserTwoInfo> list) {
		this.list = list;
		Log.i("result", "----------list---------" + list.size());
	}

	@Override
	public View getView(int position, View view, ViewGroup arg2) {
		// TODO Auto-generated method stub
		ViewHolder holder;
		if (view == null) {
			holder = new ViewHolder();
			view = inflater.inflate(R.layout.commonly_user_item_layout,
					null);

			holder.tvPhone = (TextView) view
					.findViewById(R.id.tv_com_item_userPhone);
			holder.tvName = (TextView) view.findViewById(R.id.tv_com_item_userName);

			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}

		AddCommonlyUserTwoInfo info = list.get(position);
		Log.i("result", "----------tvPhone----a-----" + info.getPhone());
		holder.tvPhone.setText(info.getPhone());
		holder.tvName.setText(info.getName());
		return view;
	}

	class ViewHolder {
		TextView tvPhone;
		TextView tvName;
	}

}
