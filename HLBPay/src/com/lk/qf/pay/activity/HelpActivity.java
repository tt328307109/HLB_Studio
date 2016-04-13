package com.lk.qf.pay.activity;

import java.util.ArrayList;
import java.util.HashMap;

import com.lk.bhb.pay.R;
import com.lk.qf.pay.wedget.CommonTitleBar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class HelpActivity extends BaseActivity{
	
	private ListView listView;
	private ArrayList<HashMap<String, String>> list;
	private CommonTitleBar title;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_help);
		initData();
		title = (CommonTitleBar) findViewById(R.id.titlebar_helpToUse);
		title.setActName("使用帮助");
		title.setCanClickDestory(this, true);
		listView = (ListView) findViewById(R.id.help_listview);
		SimpleAdapter adapter = new SimpleAdapter(this, list, R.layout.activity_help_item, new String[]{"NAME"}, new int[]{R.id.help_item_text});
		
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				goDetail(position);
				
			}
		});
		
	}
	
	private void initData() {
		String[] items = getResources().getStringArray(R.array.help_list);
		list = new ArrayList<HashMap<String,String>>();
		for (int i = 0; i < items.length; i++) {
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("NAME", items[i]);
			list.add(map);
		}
	}
	
	private void goDetail(int position) {
		Intent it = new Intent(this, HelpDetailActivity.class);
		it.putExtra("NAME", list.get(position).get("NAME"));
		it.putExtra("position", position);
		startActivity(it);
		
	}
	
}
