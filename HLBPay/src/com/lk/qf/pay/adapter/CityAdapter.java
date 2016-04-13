package com.lk.qf.pay.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.List;

import com.lk.bhb.pay.R;
import com.lk.qf.pay.beans.CityInfo;


/**
 * 二级分类（即右侧菜单）的adapter
 * Created by hanj on 14-9-25.
 */
public class CityAdapter extends BaseAdapter{
    private Context context;
    private List<CityInfo> list;

    public CityAdapter(Context context, List<CityInfo> list){
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
            convertView = LayoutInflater.from(context).inflate(R.layout.right_listview_item, null);
            holder.nameTV = (TextView) convertView.findViewById(R.id.right_item_name);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

      //选中和没选中时，设置不同的颜色
//        if (position == selectedPosition){
//            convertView.setBackgroundResource(R.color.popup_right_bg);
//        }
          
        
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

    private class ViewHolder{
        TextView nameTV;
    }

}
