package com.lk.qf.pay.v50;

import java.util.List;

import com.lk.bhb.pay.R;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MyAdapter extends BaseAdapter {

	private List<MyBean> mBeans;
	private Activity activity;

	public MyAdapter(List<MyBean> mBeans, Activity activity) {
		this.mBeans = mBeans;
		this.activity = activity;
	}

	@Override
	public int getCount() {
		return mBeans.size();
	}

	@Override
	public Object getItem(int position) {
		return mBeans.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;

		if (convertView == null) {
			convertView = LayoutInflater.from(activity).inflate(R.layout.v50_list_item, null);
			holder = new ViewHolder();
			holder.name = (TextView) convertView.findViewById(R.id.name);			
			holder.iv_flag = (ImageView) convertView.findViewById(R.id.iv_flag);			
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.name.setText(mBeans.get(position).getName());
		if (mBeans.get(position).getResult() == 0) {
			// 尚未测试
			holder.iv_flag.setVisibility(View.GONE);
		} else if (mBeans.get(position).getResult() == 1) {
			// 测试成功
			holder.iv_flag.setImageResource(R.drawable.v50_success);
			holder.iv_flag.setVisibility(View.VISIBLE);
		} else if (mBeans.get(position).getResult() == 2) {
			// 测试失败
			holder.iv_flag.setImageResource(R.drawable.v50_fail);
			holder.iv_flag.setVisibility(View.VISIBLE);
		}

		return convertView;
	}

	static class ViewHolder {
		public TextView name;
		public ImageView iv_flag;
	}

}
