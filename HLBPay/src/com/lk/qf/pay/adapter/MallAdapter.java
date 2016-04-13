package com.lk.qf.pay.adapter;

import java.util.List;

import com.lk.bhb.pay.R;
import com.lk.qf.pay.beans.MallSpInfo;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class MallAdapter extends BaseAdapter{

	
	List<MallSpInfo> list;
	Context context;
	LayoutInflater inflater;
	
	public MallAdapter(Context context , List<MallSpInfo> list){
		this.context = context;
		this.list = list;
		inflater = LayoutInflater.from(context);
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list==null?0:list.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View view, ViewGroup arg2) {
		// TODO Auto-generated method stub
		ViewHolder holder;

		if (view == null) {
			holder = new ViewHolder();
//			view = inflater.inflate(R.layout.mall_sp_layout_item, null);
//			holder.img = (ImageView) view.findViewById(R.id.img_mall);
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}
		MallSpInfo info = list.get(position);
		holder.img.setBackgroundResource(info.getImgMall());
		return view;
	}

	class ViewHolder {
		ImageView img;
	}

}
