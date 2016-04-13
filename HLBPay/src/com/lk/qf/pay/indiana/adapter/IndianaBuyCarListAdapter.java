package com.lk.qf.pay.indiana.adapter;

import java.util.List;
import com.lk.bhb.pay.R;
import com.lk.qf.pay.adapter.MyBaseAdapter;
import com.lk.qf.pay.golbal.MyUrls;
import com.lk.qf.pay.indiana.bean.IndianaGoodsInfo;
import com.lk.qf.pay.indiana.myinterface.MyEditTextListener;
import com.lk.qf.pay.utils.GetAppVersion;
import com.lk.qf.pay.wedget.MyClickListener;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class IndianaBuyCarListAdapter extends MyBaseAdapter<IndianaGoodsInfo> {

	private MyClickListener mListener;
	private ImageLoader imgloader;
	private DisplayImageOptions options;
	private int buyNumber = 1;
	private ViewHolder holder;
	private CharSequence temp;
	private IndianaGoodsInfo info;
	private Integer index = -1;
	 private MyEditTextListener listener;
	public IndianaBuyCarListAdapter(Context context,
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
	
	public void setlistener(MyEditTextListener listener){
		this.listener = listener;
	}

	@Override
	public View getView(int position, View view, ViewGroup arg2) {
		// TODO Auto-generated method stub

		if (view == null) {
			holder = new ViewHolder();
			view = inflater.inflate(R.layout.indiana_buycar_item_layout, null);

			holder.ibJian = (ImageButton) view
					.findViewById(R.id.ib_indiana_bc_Reduction);
			holder.ibJia = (ImageButton) view
					.findViewById(R.id.ib_indiana_bc_Add);
			holder.imgLogo = (ImageView) view
					.findViewById(R.id.img_indiana_bc_icon);

			holder.tvDelete = (TextView) view
					.findViewById(R.id.indiana_bc_delete);
			holder.tvGoodsName = (TextView) view
					.findViewById(R.id.tv_indiana_bc_goodsName);
			holder.tvTotalNum = (TextView) view
					.findViewById(R.id.tv_indiana_bc_totalNum);
			holder.tvRemainingNum = (TextView) view
					.findViewById(R.id.tv_indiana_bc_remaining_num);
			holder.tvDescribe = (TextView) view
					.findViewById(R.id.tv_indiana_bc_tvDescribe);
			holder.edNum = (EditText) view
					.findViewById(R.id.ed_indiana_bc_buyNum);
			holder.edNum.setTag(position);
			
			holder.edNum.setOnTouchListener(new OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					if (event.getAction() == MotionEvent.ACTION_UP) {
						index = (Integer) v.getTag();
					}
					return false;
				}
			});
			class MyTextWatcher implements TextWatcher {
				private ViewHolder mHolder;

				public MyTextWatcher(ViewHolder holder) {
					mHolder = holder;
				}

				@Override
				public void onTextChanged(CharSequence s, int start,
						int before, int count) {
				}

				@Override
				public void beforeTextChanged(CharSequence s, int start,
						int count, int after) {
				}

				@Override
				public void afterTextChanged(Editable s) {
					if (s != null && !"".equals(s.toString())) {
						int position = (Integer) mHolder.edNum.getTag();
						int before = list.get(position).getUserCanyuNum();
						list.get(position).setUserCanyuNum(Integer.parseInt(s.toString()));
//						list.get(position).setGoodsTotal(Integer.parseInt(s.toString()));
						int after =Integer.parseInt(s.toString());
						listener.getBuyNum(after-before);
					}
				}
			}
			holder.edNum.addTextChangedListener(new MyTextWatcher(holder));

			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}
		info = list.get(position);

		holder.tvGoodsName.setText(info.getGoodsName());
		holder.tvTotalNum.setText("总需" + info.getGoodsTotal());
		holder.tvRemainingNum.setText(""+info.getRemainingNum());
		holder.tvDescribe.setText(info.getDescribe());

		if (info.getImgUrl() == null || info.getImgUrl().equals("")) {
			Log.i("result", "------------ser-1---------");
//			imgloader.displayImage(MyUrls.ROOT_URL2 + "/bank/yl.png",
//					holder.imgLogo, options);
		} else {
			imgloader.displayImage(MyUrls.ROOT_URL2 + info.getImgUrl(),
					holder.imgLogo, options);
		}
		holder.edNum.setText("" + info.getUserCanyuNum());
		holder.imgLogo.setTag(position);
		holder.imgLogo.setOnClickListener(mListener);
		holder.ibJian.setTag(position);
		holder.ibJian.setOnClickListener(mListener);
		holder.ibJia.setTag(position);
		holder.ibJia.setOnClickListener(mListener);
		holder.tvDelete.setTag(position);
		holder.tvDelete.setOnClickListener(mListener);
		
		holder.edNum.clearFocus();
		if (index != -1 && index == position) {
			holder.edNum.requestFocus();
		}
		return view;
	}
	
	

	class ViewHolder {
		ImageButton ibJian;
		ImageButton ibJia;
		ImageView imgLogo;
		EditText edNum;
		TextView tvDelete;
		TextView tvGoodsName;
		TextView tvTotalNum;
		TextView tvRemainingNum;
		TextView tvDescribe;

	}

}
