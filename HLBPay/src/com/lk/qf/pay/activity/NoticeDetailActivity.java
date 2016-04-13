package com.lk.qf.pay.activity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.lk.bhb.pay.R;
import com.lk.qf.pay.beans.NoticeBean;
import com.lk.qf.pay.tool.Logger;
import com.lk.qf.pay.tool.MyHttpClient;
import com.lk.qf.pay.wedget.CommonTitleBar;
import com.lk.qf.pay.wedget.CustomListView.OnRefreshListener;

public class NoticeDetailActivity extends BaseActivity {
	private CommonTitleBar title;
	private NoticeBean bean;
	private TextView tv_title, tv_content, tv_time;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.notice_detail_layout);
		initView();
		loadData(0);
		Logger.init().setMethodCount(0).hideThreadInfo();
	}

	ArrayList<NoticeBean> adaVal = new ArrayList<NoticeBean>();

	private void loadData(int id) {

	}

	private void initView() {
		bean = (NoticeBean) getIntent().getSerializableExtra("data");
		tv_title = findView(R.id.tv_msg_title);
		tv_content = findView(R.id.tv_msg_content);
		tv_time = findView(R.id.tv_msg_time);
		title = findView(R.id.titlebar_notice_detail);
		title.setActName("公告详情").setCanClickDestory(this, true);
		tv_title.setText(toS(bean.getTitle()));
		tv_content.setText(toS(bean.getContent()));
		tv_time.setText(datePaser(bean.getTime()));
		
	}
	
	private String datePaser(String str){
		SimpleDateFormat d	=new SimpleDateFormat("yyyyMMddHHmmss");
		 try {
			Date date=d.parse(str);
			SimpleDateFormat temp=new SimpleDateFormat("MM月dd日 HH:mm:ss");
			return temp.format(date);
		} catch (ParseException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
			return "";
		}
		
		
	}

	private String toS(String str) {
		if (null == str)
			return "";
		return str;
	}

	OnRefreshListener onRefrsh = new OnRefreshListener() {

		@Override
		public void onRefresh() {

		}
	};

	public <T extends View> T findView(int id) {
		return (T) findViewById(id);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		MyHttpClient.cancleRequest(this);
	}
}
