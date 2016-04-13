package com.lk.qf.pay.adapter;

import java.util.List;

import com.lk.bhb.pay.R;
import com.lk.qf.pay.beans.ProvinceInfo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


public class ProvinceInfoAdapter extends BaseAdapter{

	 private Context context;
	    private List<ProvinceInfo> list;

	    public ProvinceInfoAdapter(Context context, List<ProvinceInfo> list) {
	        this.context = context;
	        this.list = list;
	    }

	    @Override
	    public int getCount() {
//	    	Log.i("result", "------------adap------------"+list.size());
	        return list == null ? 0 : list.size();
	    }

	    @Override
	    public Object getItem(int i) {
	        return null;
	    }

	    @Override
	    public long getItemId(int i) {
	        return 0;
	    }

	    @Override
	    public View getView(int position, View convertView, ViewGroup viewGroup) {
	        ViewHolder holder;
	        if (convertView == null) {
	            convertView = LayoutInflater.from(context).inflate(R.layout.left_listview_item, null);
	            holder = new ViewHolder();

	            holder.nameTV = (TextView) convertView.findViewById(R.id.left_item_name);
	            convertView.setTag(holder);
	        } else {
	            holder = (ViewHolder) convertView.getTag();
	        }

	        //选中和没选中时，设置不同的颜色
//	        if (position == selectedPosition){
//	            convertView.setBackgroundResource(R.color.popup_right_bg);
//	        }

	        holder.nameTV.setText(list.get(position).getName());

	        return convertView;
	    }

	    private int selectedPosition;

	    public void setSelectedPosition(int selectedPosition) {
	        this.selectedPosition = selectedPosition;
	    }

	    public int getSelectedPosition() {
	        return selectedPosition;
	    }

	    private class ViewHolder {
	        TextView nameTV;
	    }
}
