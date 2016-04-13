package com.lk.qf.pay.indiana.adapter;

import java.util.List;
import com.lk.bhb.pay.R;
import com.lk.qf.pay.adapter.MyBaseAdapter;
import com.lk.qf.pay.golbal.MyUrls;
import com.lk.qf.pay.indiana.bean.IndianaGoodsInfo;
import com.lk.qf.pay.wedget.MyClickListener;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class IndianaIssueOpenAdapter extends MyBaseAdapter<IndianaGoodsInfo> {

	ImageLoader imgloader;
	DisplayImageOptions options;
//	private MyClickListener mListener;
	public IndianaIssueOpenAdapter(Context context,
			List<IndianaGoodsInfo> list, DisplayImageOptions options,
			ImageLoader imgloader) {
		super(context, list);
		// TODO Auto-generated constructor stub
		this.imgloader = imgloader;
		this.options = options;
//		this.mListener = mListener;
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
			view = inflater.inflate(R.layout.indiana_issue_winner_item_layout, null);

			holder.imgLogo = (ImageView) view
					.findViewById(R.id.img_indiana_announced_goods_icon);

			holder.tvWinnerName = (TextView) view
					.findViewById(R.id.tv_indiana_issue_winnerName);
			holder.tvTotalNum = (TextView) view
					.findViewById(R.id.tv_indiana_issue_participateInNum);
			holder.tvWinnerNum = (TextView) view
					.findViewById(R.id.tv_indiana_issue_winnerNum);
			holder.tvGoodsName = (TextView) view
					.findViewById(R.id.tv_indiana_issue_goodsName);
			holder.tvOpenTime = (TextView) view
					.findViewById(R.id.tv_indiana_issue_date);

			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}
		IndianaGoodsInfo info = list.get(position);

		String winnerName = info.getWinnerName();
		if (winnerName.length()>1) {
			winnerName = "*"+winnerName.substring(1, winnerName.length());
		}
		holder.tvWinnerName.setText(winnerName);
		holder.tvTotalNum.setText(""+info.getGoodsTotal());
		holder.tvWinnerNum.setText(""+info.getWinningNumber());
		holder.tvGoodsName.setText(""+info.getGoodsName());
		holder.tvOpenTime.setText(info.getOpenTime());

		if (info.getImgUrl() == null || info.getImgUrl().equals("")) {
			Log.i("result", "------------ser-1---------");
//			imgloader.displayImage(MyUrls.ROOT_URL2 + "/bank/yl.png",
//					holder.imgLogo, options);
		} else {
			imgloader.displayImage(MyUrls.ROOT_URL2 + info.getImgUrl(),
					holder.imgLogo, options);
		}
//		holder.imgLogo.setTag(position);
//		holder.imgLogo.setOnClickListener(mListener);
		
		return view;
	}

	class ViewHolder {
		ImageView imgLogo;
		TextView tvWinnerName;
		TextView tvTotalNum;
		TextView tvWinnerNum;
		TextView tvGoodsName;
		TextView tvOpenTime;//开奖时间

	}

}
