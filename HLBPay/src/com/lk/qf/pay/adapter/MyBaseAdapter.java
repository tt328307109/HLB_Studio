package com.lk.qf.pay.adapter;

import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public abstract class MyBaseAdapter<E> extends BaseAdapter{

	public List<E> list;
	public Context context;
	public LayoutInflater inflater;
	
	public MyBaseAdapter(Context context,List<E> list){
		this.list = list;
		this.context = context;
		inflater = LayoutInflater.from(context);
	}
	
	@Override
	public int getCount() {//这个是哪儿打印的
		// TODO Auto-generated method stub
//		Log.i("result", "----base---listlist-------"+(list==null?0:list.size()));
		return list==null?0:list.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return list.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	public abstract View getView(int arg0, View arg1, ViewGroup arg2);

}
