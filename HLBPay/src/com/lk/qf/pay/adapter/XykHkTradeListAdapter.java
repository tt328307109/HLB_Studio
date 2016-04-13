package com.lk.qf.pay.adapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import com.lk.bhb.pay.R;
import com.lk.qf.pay.adapter.XinyongkaListItemAdapter.ViewHolder;
import com.lk.qf.pay.beans.XYKTradeListInfo;
import com.lk.qf.pay.beans.Xinyongkainfo;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class XykHkTradeListAdapter extends MyBaseAdapter<XYKTradeListInfo>{

	public XykHkTradeListAdapter(Context context, List<XYKTradeListInfo> list) {
		super(context, list);
		// TODO Auto-generated constructor stub
	}
	public void sendSata(List<XYKTradeListInfo> list) {
		this.list = list;
		Log.i("result", "----------list---------" + list.size());
	}

	@Override
	public View getView(int position, View view, ViewGroup arg2) {
		// TODO Auto-generated method stub
		ViewHolder holder;
		if (view == null) {
			holder = new ViewHolder();
			view = inflater.inflate(R.layout.huankuan_list_item_layout, null);

			holder.tvHkAccount = (TextView) view
					.findViewById(R.id.tv_xyk_huankuan_item_account);
			holder.tvType = (TextView) view
					.findViewById(R.id.tv_xyk_huankuan_item_type);
			holder.tvTax = (TextView) view.findViewById(R.id.tv_xyk_huankuan_item_shouxu_account);
			holder.tvDate = (TextView) view.findViewById(R.id.tv_xyk_huankuan_item_time);

			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}
		XYKTradeListInfo info = list.get(position);
		
		String strDate = info.getDate();
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy/MM/dd");
	    SimpleDateFormat sdf2=new SimpleDateFormat("yyyyMMddHHmmss");  
	    try {
			strDate=sdf1.format(sdf2.parse(strDate));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    String type = info.getType();
		if (type.equals("ok")) {
			type = "成功";
		}else if (type.equals("fail")) {
			type = "失败";
			
		}else if (type.equals("deal")) {
			type = "处理中";
			
		}else if (type.equals("norm")) {
			type = "未处理";
			
		}else if (type.equals("frezz")) {
			
			type = "冻结";
		}
		holder.tvHkAccount.setText("￥"+info.getHkAccount());
		holder.tvType.setText(type);
		holder.tvTax.setText("手续费"+info.getSxAccount());
		holder.tvDate.setText(strDate);
		return view;
	}
	class ViewHolder {

		TextView tvHkAccount;// 还款金额
		TextView tvDate;// 还款时间
		TextView tvType;// 还款状态
		TextView tvTax;// 手续费
	}

}
