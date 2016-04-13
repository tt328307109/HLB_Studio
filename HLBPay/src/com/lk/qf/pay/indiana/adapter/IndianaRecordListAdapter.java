package com.lk.qf.pay.indiana.adapter;

import java.util.List;
import com.lk.bhb.pay.R;
import com.lk.qf.pay.adapter.MyBaseAdapter;
import com.lk.qf.pay.golbal.MyUrls;
import com.lk.qf.pay.indiana.bean.IndianaGoodsInfo;
import com.lk.qf.pay.utils.PercentUtils;
import com.lk.qf.pay.utils.TimeUtils;
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
import android.widget.RelativeLayout;
import android.widget.TextView;

public class IndianaRecordListAdapter extends MyBaseAdapter<IndianaGoodsInfo> {

	private MyClickListener mListener;
	ImageLoader imgloader;
	DisplayImageOptions options;

	public IndianaRecordListAdapter(Context context,
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
			view = inflater.inflate(R.layout.indiana_record_list_item_layout,
					null);

			holder.imgLogo = (ImageView) view
					.findViewById(R.id.img_indiana_record_goods_icon);
			holder.tvGoodsName = (TextView) view
					.findViewById(R.id.tv_indiana_record_Name);
			holder.tvTotalNum = (TextView) view
					.findViewById(R.id.tv_indiana_record_goods_account);
			holder.tvRemainingNum = (TextView) view
					.findViewById(R.id.tv_indiana_record_remain_Num);
			holder.tvCanyuNum = (TextView) view
					.findViewById(R.id.tv_indiana_record_canyu_Num);
			holder.tvQueryIndianaNum = (TextView) view
					.findViewById(R.id.tv_indiana_record_seeNum);
			holder.tvToIndiana = (TextView) view
					.findViewById(R.id.tv_indiana_record_GoBuy);
			holder.tvGoodsType = (TextView) view
					.findViewById(R.id.tv_record_list_goodsType);
			holder.tvWinnerName = (TextView) view
					.findViewById(R.id.tv_indiana_winner_record_name);
			holder.tvWinnerNum = (TextView) view
					.findViewById(R.id.tv_indiana_winner_record_Num);
			// holder.tvWinnerCanyuNum = (TextView) view
			// .findViewById(R.id.tv_indiana_info_record_Num);
			holder.tvOpenTime = (TextView) view
					.findViewById(R.id.tv_indiana_winner_record_time);
			holder.rl = (RelativeLayout) view
					.findViewById(R.id.rl_indiana_winner_record_info);
			holder.pb = (ProgressBar) view
					.findViewById(R.id.pb_indiana_record_remain);
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}
		IndianaGoodsInfo info = list.get(position);

		holder.tvGoodsName.setText(info.getGoodsName());
		holder.tvTotalNum.setText("总需:" + info.getGoodsTotal());
		holder.tvRemainingNum.setText("剩余:" + info.getRemainingNum());

		holder.tvCanyuNum.setText("" + info.getUserCanyuNum());
		int totalNum = info.getGoodsTotal();
		int boughtNum = info.getBoughtNum();
		int progress = PercentUtils.getPercent(boughtNum, totalNum);
		Log.i("result", "-----------progress------" + progress);
		holder.pb.setProgress(progress);

		if (info.getWinningNumber() == null
				|| info.getWinningNumber().equals("")) {
			holder.tvGoodsType.setText("进行中");
			holder.rl.setVisibility(View.GONE);
		} else {
			holder.tvGoodsType.setText("已揭晓");
			holder.rl.setVisibility(View.VISIBLE);
		}
		Log.i("result",
				"-----------------info.getUserName()------"
						+ info.getUserName());
		holder.tvWinnerName.setText("" + info.getUserName());
		
		String winnerName = info.getUserName();// 中奖者名字
		if (winnerName.length() == 11) {
			winnerName = winnerName.substring(0, 3)
					+ "****"
					+ winnerName.substring(winnerName.length() - 4,
							winnerName.length());
		}
		holder.tvWinnerName.setText(winnerName);
		holder.tvWinnerNum.setText("" + info.getWinningNumber());
		holder.tvOpenTime.setText( info.getOpenTime());
		
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
		holder.tvQueryIndianaNum.setTag(position);
		holder.tvQueryIndianaNum.setOnClickListener(mListener);
		holder.tvToIndiana.setTag(position);
		holder.tvToIndiana.setOnClickListener(mListener);
		holder.tvGoodsName.setTag(position);
		holder.tvGoodsName.setOnClickListener(mListener);
		if (info.getWinningNumber()!=null && !info.getWinningNumber().equals("")) {
			
		}else{
			holder.tvToIndiana.setVisibility(View.GONE);
		}
		return view;
	}

	class ViewHolder {
		ImageView imgLogo;
		RelativeLayout rl;
		TextView tvGoodsName;
		TextView tvTotalNum;// 总数
		TextView tvRemainingNum;// 剩余
		TextView tvCanyuNum;// 参与人数
		TextView tvQueryIndianaNum;// 查看夺宝号
		TextView tvToIndiana;// 我要夺宝
		TextView tvGoodsType;// 是否揭晓
		TextView tvWinnerName;// 中奖者名字
		TextView tvWinnerNum;// 中奖号码
		// TextView tvWinnerCanyuNum;//本期参与人数
		TextView tvOpenTime;// 揭晓时间
		ProgressBar pb;
	}
}
