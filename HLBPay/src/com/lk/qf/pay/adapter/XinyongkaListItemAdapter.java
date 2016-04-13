package com.lk.qf.pay.adapter;

import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lk.bhb.pay.R;
import com.lk.qf.pay.adapter.JiaoYiAdapter.ViewHolder;
import com.lk.qf.pay.beans.OrderInfo;
import com.lk.qf.pay.beans.Xinyongkainfo;
import com.lk.qf.pay.golbal.MyUrls;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class XinyongkaListItemAdapter extends MyBaseAdapter<Xinyongkainfo> {

	ViewHolder viewHolder;
	ImageLoader imgloader;
	DisplayImageOptions options;

	public XinyongkaListItemAdapter(Context context, List<Xinyongkainfo> list) {
		super(context, list);
		// TODO Auto-generated constructor stub
	}
	
	public XinyongkaListItemAdapter(Context context, List<Xinyongkainfo> list,
			DisplayImageOptions options, ImageLoader imgloader) {
		super(context, list);
		// TODO Auto-generated constructor stub

		this.imgloader = imgloader;
		this.options = options;
	}

	public void sendSata(List<Xinyongkainfo> list) {
		this.list = list;
	}

	@Override
	public View getView(int position, View view, ViewGroup arg2) {
		// TODO Auto-generated method stub
		ViewHolder holder;
		if (view == null) {
			holder = new ViewHolder();
			view = inflater.inflate(R.layout.xinyongka_item_layout, null);

			holder.imgLogo = (ImageView) view
					.findViewById(R.id.img_xyk_item_icon);
			holder.tvUserName = (TextView) view
					.findViewById(R.id.tv_xyk_item_userName);
			holder.tvCardNum = (TextView) view
					.findViewById(R.id.tv_xyk_item_bankNum);
			holder.tvDate = (TextView) view.findViewById(R.id.tv_xyk_day);
			holder.tvBankName = (TextView) view.findViewById(R.id.tv_xyk_item_bankName);
			holder.tvWeihuanAccount = (TextView) view.findViewById(R.id.tv_xyk_item_weihuan_account);
			holder.imgType = (ImageView) view.findViewById(R.id.img_xyk_type);

			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}
		Xinyongkainfo info = list.get(position);
		holder.tvUserName.setText(info.getUserName());
		holder.tvCardNum.setText(info.getCardNum().substring(info.getCardNum().length()-4,info.getCardNum().length()));
		holder.tvDate.setText(info.getDate()+"天");
		holder.tvBankName.setText(info.getBankName());
		String repay = info.getWeihuanAccount();
		double repayAccount = Double.parseDouble(repay);
		if (repayAccount>0) {
			holder.tvWeihuanAccount.setText("未还: "+repay);
		}else{
			holder.tvWeihuanAccount.setText("已还清");
		}
		String type = info.getType();
		if (type.equals("0")) {
			holder.imgType.setBackgroundResource(R.drawable.daoqi);
		}else if (type.equals("1")) {
			holder.imgType.setBackgroundResource(R.drawable.huankuan);
		}else if (type.equals("2")){
			holder.imgType.setBackgroundResource(R.drawable.weichuzhang);
		}else if(type.equals("3")){
			holder.imgType.setBackgroundResource(R.drawable.chuzhang);
		}else{
			holder.imgType.setBackground(null);
			holder.tvDate.setText("");
		}
		
		if (info.getImgUrl()==null || info.getImgUrl().equals("")) {
//			holder.imgLogo.setBackgroundResource(R.drawable.logo_yinhang);
			Log.i("result", "------------ser-1---------");
			imgloader.displayImage(MyUrls.ROOT_URL1+"/bank/yl.png", holder.imgLogo,
					options);////图片缓存有问题
			
		}else{
			Log.i("result", "------------ser-2---------"+(imgloader==null?0:1));
			
//			holder.imgLogo.setBackgroundResource(R.drawable.yinlian);
		imgloader.displayImage(MyUrls.ROOT_URL1+info.getImgUrl(), holder.imgLogo,
				options);////图片缓存有问题
		}
		return view;
	}

	class ViewHolder {
		ImageView imgLogo;// 银行logo
		ImageView imgType;// 还款状态

		TextView tvUserName;// 用户名
		TextView tvCardNum;// 卡号
		TextView tvDate;// 还款剩余日期
		TextView tvBankName;// 银行名
		TextView tvWeihuanAccount;// 未还金额
	}

}
