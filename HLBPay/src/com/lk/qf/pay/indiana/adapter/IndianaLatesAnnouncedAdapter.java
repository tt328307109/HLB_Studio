package com.lk.qf.pay.indiana.adapter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import com.lk.bhb.pay.R;
import com.lk.qf.pay.adapter.MyBaseAdapter;
import com.lk.qf.pay.golbal.MyUrls;
import com.lk.qf.pay.indiana.bean.IndianaGoodsInfo;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class IndianaLatesAnnouncedAdapter extends
		MyBaseAdapter<IndianaGoodsInfo> {
//	private MyClickListener mListener;
	private ImageLoader imgloader;
	private DisplayImageOptions options;
	private long times = 125000;
	private ViewHolder holder;

	public IndianaLatesAnnouncedAdapter(Context context,
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
//		Log.i("result", "----------list---------" + list.size());
	}

	@Override
	public View getView(int position, View view, ViewGroup arg2) {
		// TODO Auto-generated method stub

		if (view == null) {
			holder = new ViewHolder();
			view = inflater.inflate(
					R.layout.indiana_latest_announced_item_layout, null);

			holder.imgLogo = (ImageView) view
					.findViewById(R.id.img_indiana_announced_goods_icon);
			holder.imgLogoType = (ImageView) view
					.findViewById(R.id.img_indiana_latest_announced_type);

			holder.tvGoodsName = (TextView) view
					.findViewById(R.id.tv_indiana_announced);
			holder.tvGoodsAccount = (TextView) view
					.findViewById(R.id.tv_indiana_announced_goods_account);
			holder.tvDaoTime = (TextView) view
					.findViewById(R.id.tv_indiana_announced_time);
			holder.tvOpenTime = (TextView) view
					.findViewById(R.id.tv_indiana_announced_openTime);
			holder.tvWinnerName = (TextView) view
					.findViewById(R.id.tv_indiana_announced_WinnerName);
			holder.llWinnerName = (LinearLayout) view
					.findViewById(R.id.ll_indiana_announced_winnerName);
			holder.llOpenTime = (LinearLayout) view
					.findViewById(R.id.ll_indiana_announced_time);
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}
		IndianaGoodsInfo info = list.get(position);

		holder.tvGoodsName.setText(info.getGoodsName());
		holder.tvGoodsAccount.setText("￥" + info.getGoodsTotal());

		long dvOpenTime = info.getShengyuTime();

		if (dvOpenTime > 0) {
			holder.tvDaoTime.setVisibility(View.VISIBLE);
			holder.imgLogoType.setVisibility(View.VISIBLE);
			holder.llWinnerName.setVisibility(View.GONE);
			holder.llOpenTime.setVisibility(View.GONE);
			// SSS为毫秒，如果精确到时分秒改成HH:mm:ss即可
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
					"mm:ss:SSS");
			Date date = new Date(dvOpenTime);
			String time = simpleDateFormat.format(date);
//			Log.i("result", "----------dvOpenTime---------==" + time);
			// 因为1秒是等于1000毫秒的，正常显示格式为05:40:99,所以此处截取掉最后一位数字
			holder.tvDaoTime.setText(time.substring(0, time.length() - 1));

		} else {
//			Log.i("result", "----------else-------==");
			// holder.tvDaoTime.setText(info.getOpenTime());
			String winnerName = info.getWinnerName();
			if (winnerName.length() == 11) {
				winnerName = winnerName.substring(0, 3)+"****"+winnerName.substring(winnerName.length()-4, winnerName.length());
			}
//			if (winnerName.length() >= 2) {
//				winnerName = "*" + winnerName.substring(1, winnerName.length());
//			}
			holder.tvWinnerName.setText(winnerName);
			holder.tvOpenTime.setText(info.getOpenTime());
			holder.llWinnerName.setVisibility(View.VISIBLE);
			holder.llOpenTime.setVisibility(View.VISIBLE);
			holder.tvDaoTime.setVisibility(View.GONE);
			holder.imgLogoType.setVisibility(View.GONE);
		}

		if (info.getImgUrl() == null || info.getImgUrl().equals("")) {
//			Log.i("result", "------------ser-1---------");
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
		ImageView imgLogoType;

		TextView tvGoodsName;
		TextView tvGoodsAccount;// 价值
		TextView tvDaoTime;
		TextView tvOpenTime;
		TextView tvWinnerName;
		LinearLayout llWinnerName;
		LinearLayout llOpenTime;

	}

//	int result = 0;
//	private Thread thread;
//	public void start() {
//		thread = new Thread() {
//			public void run() {
//				while (true) {
//					try {
////						Log.i("result","----------------list----s--------"+list.size());
//						if (list == null || result == list.size()) {
//							break;
//						}
//						sleep(10);
//						for (IndianaGoodsInfo idInfo : list) {
//							long dvOpenTime = idInfo.getShengyuTime();
//							if (dvOpenTime!=0) {
////								
//								if (dvOpenTime<0){
//									idInfo.setShengyuTime(0);
//									result++;
//								} else {
//									idInfo.setShengyuTime(dvOpenTime - 10);
//								}
//							}else{
////						Log.i("result","----------------falses--------");
//							}
//						}
//					} catch (InterruptedException e) {
//						e.printStackTrace();
//					}
//				}
//			}
//		};
//		thread.start();
//	}

}
