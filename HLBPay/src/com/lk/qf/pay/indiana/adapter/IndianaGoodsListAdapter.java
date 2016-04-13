package com.lk.qf.pay.indiana.adapter;

import java.util.List;
import com.lk.bhb.pay.R;
import com.lk.qf.pay.adapter.MyBaseAdapter;
import com.lk.qf.pay.golbal.MyUrls;
import com.lk.qf.pay.indiana.bean.IndianaGoodsInfo;
import com.lk.qf.pay.utils.PercentUtils;
import com.lk.qf.pay.wedget.MyClickListener;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class IndianaGoodsListAdapter extends MyBaseAdapter<IndianaGoodsInfo> {

	private MyClickListener mListener;
	ImageLoader imgloader;
	DisplayImageOptions options;
	private int progress;

	public IndianaGoodsListAdapter(Context context,
			List<IndianaGoodsInfo> list, MyClickListener mListener,
			DisplayImageOptions options, ImageLoader imgloader) {
		super(context, list);
		// TODO Auto-generated constructor stub
		this.mListener = mListener;
		this.imgloader = imgloader;
		this.options = options;
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
			view = inflater.inflate(R.layout.indiana_list_goods_item_layout,
					null);

			holder.ibBuy = (ImageButton) view
					.findViewById(R.id.ib_add_buycar_item);
			holder.imgLogo = (ImageView) view
					.findViewById(R.id.img_indiana_goods_icon);

			holder.tvGoodsName = (TextView) view
					.findViewById(R.id.tv_indiana_goods_name);
			holder.tvTotalNum = (TextView) view
					.findViewById(R.id.tv_total_need_indiana_num);
			holder.tvRemainingNum = (TextView) view
					.findViewById(R.id.tv_total_remaining_indiana_num);
			holder.pb = (ProgressBar) view.findViewById(R.id.pb_indiana_goods);
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}
		IndianaGoodsInfo info = list.get(position);

		holder.tvGoodsName.setText(info.getGoodsName());
		holder.tvTotalNum.setText(""+info.getGoodsTotal());
		holder.tvRemainingNum.setText(""+info.getRemainingNum());
		int totalNum = info.getGoodsTotal();
		int boughtNum = info.getBoughtNum();
		progress = PercentUtils.getPercent(boughtNum, totalNum);
		holder.pb.setProgress(progress);

		if (info.getImgUrl() == null || info.getImgUrl().equals("")) {
			Log.i("result", "------------ser-1---------");
//			imgloader.displayImage(MyUrls.ROOT_URL2 + "/bank/yl.png",
//					holder.imgLogo, options);
		} else {
			imgloader.displayImage(MyUrls.ROOT_URL2 + info.getImgUrl(),
					holder.imgLogo, options);
		}
		holder.imgLogo.setTag(position);
		holder.imgLogo.setOnClickListener(mListener);
		holder.ibBuy.setTag(position);
		holder.ibBuy.setOnClickListener(mListener);
		return view;
	}

	class ViewHolder {
		ImageButton ibBuy;
		ImageView imgLogo;

		TextView tvGoodsName;
		TextView tvTotalNum;
		TextView tvRemainingNum;
		ProgressBar pb;

	}

}
