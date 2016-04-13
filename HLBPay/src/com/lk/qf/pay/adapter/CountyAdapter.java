package com.lk.qf.pay.adapter;

import java.util.List;

import com.lk.bhb.pay.R;
import com.lk.qf.pay.beans.CountyInfo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class CountyAdapter extends BaseAdapter{

	 private Context context;
	    private List<CountyInfo> list;

	    public CountyAdapter(Context context, List<CountyInfo> list){
	        this.context = context;
	        this.list = list;
	    }

	    @Override
	    public int getCount() {
	        return list==null?0:list.size();
	    }

	    @Override
	    public Object getItem(int position) {
	        return null;
	    }

	    @Override
	    public long getItemId(int position) {
	        return 0;
	    }

	    @Override
	    public View getView(int position, View convertView, ViewGroup parent) {
	        ViewHolder holder;
	        if (convertView==null){
	            holder = new ViewHolder();
	            convertView = LayoutInflater.from(context).inflate(R.layout.right2_listview_item, null);
	            holder.nameTV = (TextView) convertView.findViewById(R.id.right2_item_name);
	            convertView.setTag(holder);
	        }else{
	            holder = (ViewHolder) convertView.getTag();
	        }

	        holder.nameTV.setText(list.get(position).getName());

	        return convertView;
	    }

	    private class ViewHolder{
	        TextView nameTV;
	    }
}
