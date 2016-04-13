package com.lk.qf.pay.adapter;

import java.util.List;
import com.lk.bhb.pay.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class MySpinnerAdapter extends MyBaseAdapter<String> {

	public MySpinnerAdapter(Context context, List<String> list) {
		super(context, list);
		// TODO Auto-generated constructor stub
	}

	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		// TODO Auto-generated method stub
		ViewHolder holder;
        if (convertView==null){
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.list_arr_center_layout, null);
            holder.str = (TextView) convertView.findViewById(R.id.tv_list);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

      //选中和没选中时，设置不同的颜色
//        if (position == selectedPosition){
//            convertView.setBackgroundResource(R.color.popup_right_bg);
//        }
          
        
        holder.str.setText(list.get(position));

        return convertView;
	}

	private class ViewHolder{
        TextView str;
    }
}
