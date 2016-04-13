package com.lk.qf.pay.indiana.adapter;

import java.util.List;
import com.lk.bhb.pay.R;
import com.lk.qf.pay.adapter.MyBaseAdapter;
import com.lk.qf.pay.indiana.bean.IndianaGoodsInfo;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class IndianaNumAdapter extends MyBaseAdapter<IndianaGoodsInfo> {

	public IndianaNumAdapter(Context context, List<IndianaGoodsInfo> list) {
		super(context, list);
		// TODO Auto-generated constructor stub
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
			view = inflater.inflate(R.layout.single_textview_layout, null);
			holder.tvBuyNum = (TextView) view
					.findViewById(R.id.tv_item_single);
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}
		IndianaGoodsInfo info = list.get(position);

		holder.tvBuyNum.setText(""+info.getPeriodsNum());
		return view;
	}

	class ViewHolder {
		TextView tvBuyNum;
	}

}
