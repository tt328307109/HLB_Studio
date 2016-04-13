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

public class WinnerRecordAdapter extends MyBaseAdapter<IndianaGoodsInfo> {

	public WinnerRecordAdapter(Context context, List<IndianaGoodsInfo> list) {
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
			view = inflater.inflate(R.layout.winner_record_item_layout, null);

			holder.tvGoodsName = (TextView) view
					.findViewById(R.id.tv_winner_record_goodsName);
			holder.tvTotalNum = (TextView) view
					.findViewById(R.id.tv_winner_record_totalNum);
			holder.tvWinnerNum = (TextView) view
					.findViewById(R.id.tv_winner_record_winnerNum);
			holder.tvBuyNum = (TextView) view
					.findViewById(R.id.tv_winner_record_canyuNum);
			holder.tvOpenTime = (TextView) view
					.findViewById(R.id.tv_winner_record_openTime);
			holder.tvgoodsType = (TextView) view
					.findViewById(R.id.tv_winner_record_goodsType);
			holder.imgIcon = (ImageView) view
					.findViewById(R.id.img_winner_record);
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}
		IndianaGoodsInfo info = list.get(position);

		holder.tvGoodsName.setText(info.getGoodsName());
		holder.tvTotalNum.setText(""+info.getGoodsTotal());
		holder.tvWinnerNum.setText(info.getWinningNumber());
		holder.tvBuyNum.setText(""+info.getBoughtNum());
		holder.tvOpenTime.setText(info.getOpenTime());
		
		String type = info.getIsxd();
		if (type.equals("0")) {
			holder.tvgoodsType.setText("未确认收货地址");
		}else if (type.equals("1")){
			holder.tvgoodsType.setText("待收货");
		}else if (type.equals("2")){
			holder.tvgoodsType.setText("未晒单");
		}else{
			holder.tvgoodsType.setText("已完成");
		}
		return view;
	}

	class ViewHolder {
		ImageView imgIcon;
		TextView tvGoodsName;
		TextView tvTotalNum;
		TextView tvWinnerNum;
		TextView tvBuyNum;
		TextView tvOpenTime;
		TextView tvgoodsType;
	}

}
