package com.lk.qf.pay.activity;

import com.lk.bhb.pay.R;
import com.lk.qf.pay.adapter.HelpExpandableListAdapter;
import com.lk.qf.pay.wedget.CommonTitleBar;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ExpandableListView;
import android.widget.TextView;

public class HelpDetailActivity extends BaseActivity{
	
	private HelpExpandableListAdapter adapter;
	private ExpandableListView listView;
	private String[] groups;  
	private String[] childrens;
	private TextView contentText;
	private CommonTitleBar title;
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.help_detail);
		contentText = (TextView) findViewById(R.id.help_detail_content);
		listView = (ExpandableListView) findViewById(R.id.help_detail_expandablelistview);
		initData();
		title = (CommonTitleBar) findViewById(R.id.titlebar_helpInfo);
		title.setActName("帮助详情");
		title.setCanClickDestory(this, true);
		adapter = new HelpExpandableListAdapter(this, groups, childrens);
		listView.setGroupIndicator(null);
		listView.setAdapter(adapter);
	}

	private void initData() {
		int position = getIntent().getIntExtra("position", 0);
		contentText.setText(getIntent().getStringExtra("NAME"));
		switch (position) {
		case 0:
			groups = getResources().getStringArray(R.array.question_one_q);
			childrens = getResources().getStringArray(R.array.question_one_a);
			break;
		
		case 1:
			groups = getResources().getStringArray(R.array.question_seven_q);
			childrens = getResources().getStringArray(R.array.question_seven_a);
			break;	
		case 2:
			groups = getResources().getStringArray(R.array.question_eight_q);
			childrens = getResources().getStringArray(R.array.question_eight_a);
			break;	
		case 3:
			groups = getResources().getStringArray(R.array.question_nine_q);
			childrens = getResources().getStringArray(R.array.question_nine_a);
			break;	
		case 4:
			groups = getResources().getStringArray(R.array.question_ten_q);
			childrens = getResources().getStringArray(R.array.question_ten_a);
			break;	
		case 5:
			groups = getResources().getStringArray(R.array.question_twelve_q);
			childrens = getResources().getStringArray(R.array.question_twelve_a);
			break;	

		default:
			break;
		}
	}
}
