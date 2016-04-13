package com.lk.qf.pay.indiana.adapter;

import java.util.List;
import com.lk.bhb.pay.R;
import com.lk.qf.pay.adapter.MyBaseAdapter;
import com.lk.qf.pay.golbal.MyUrls;
import com.lk.qf.pay.indiana.bean.IndianaGoodsInfo;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class IndianaParticipantsAdapter extends MyBaseAdapter<IndianaGoodsInfo> {

	ImageLoader imgloader;
	DisplayImageOptions options;

	public IndianaParticipantsAdapter(Context context,
			List<IndianaGoodsInfo> list, DisplayImageOptions options,
			ImageLoader imgloader) {
		super(context, list);
		// TODO Auto-generated constructor stub
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
			view = inflater.inflate(R.layout.indiana_canyu_item_layout, null);

			holder.imgLogo = (ImageView) view
					.findViewById(R.id.img_indiana_canyu_icon);

			holder.tvUserName = (TextView) view
					.findViewById(R.id.tv_indiana_canyu_name);
			holder.tvTotalNum = (TextView) view
					.findViewById(R.id.tv_indiana_canyu_num);
			holder.tvUserAddr = (TextView) view
					.findViewById(R.id.tv_indiana_canyu_addr_city);
			holder.tvUserIP = (TextView) view
					.findViewById(R.id.tv_indiana_canyu_address);
			holder.tvTime = (TextView) view
					.findViewById(R.id.tv_indiana_canyu_time);
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}
		IndianaGoodsInfo info = list.get(position);
		String userName = info.getUserName();
		if (userName.length() == 11) {
			userName = userName.substring(0, 3)
					+ "*****"
					+ userName.substring(userName.length() - 4,
							userName.length());
		}
		holder.tvUserName.setText(userName);
		holder.tvTotalNum.setText("" + info.getUserCanyuNum());
		String addr = info.getUserAddress();
//		if (addr.length() >3) {
//			addr = addr.substring(0, 2);
//		}
		holder.tvUserAddr.setText(addr);
		holder.tvUserIP.setText(info.getUserIP());
		holder.tvTime.setText(info.getOpenTime());

//		if (info.getImgUrl() == null || info.getImgUrl().equals("")) {
//			Log.i("result", "------------ser-1---------");
//			imgloader.displayImage(MyUrls.ROOT_URL2 + "/bank/yl.png",
//					holder.imgLogo, options);
//		} else {
//			imgloader.displayImage(MyUrls.ROOT_URL2 + info.getImgUrl(),
//					holder.imgLogo, options);
//		}
		return view;
	}

	class ViewHolder {
		ImageView imgLogo;

		TextView tvUserName;
		TextView tvTotalNum;// 参与次数
		TextView tvUserAddr;
		TextView tvUserIP;
		TextView tvTime;// 揭晓时间

	}

}
