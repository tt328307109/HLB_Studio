package com.lk.qf.pay.activity;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.lk.bhb.pay.R;
import com.lk.pay.communication.AsyncHttpResponseHandler;
import com.lk.qf.pay.adapter.NoticeMsgAdapter;
import com.lk.qf.pay.beans.NoticeBean;
import com.lk.qf.pay.golbal.Urls;
import com.lk.qf.pay.tool.Logger;
import com.lk.qf.pay.tool.MyHttpClient;
import com.lk.qf.pay.wedget.CommonTitleBar;
import com.lk.qf.pay.wedget.CustomListView;
import com.lk.qf.pay.wedget.CustomListView.OnRefreshListener;

public class NoticeActivity extends BaseActivity {
	private CommonTitleBar title;
	CustomListView lv;
	private NoticeMsgAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_notice);
		initView();
		loadData(0);
		Logger.init().setMethodCount(0).hideThreadInfo();
	}

	ArrayList<NoticeBean> adaVal = new ArrayList<NoticeBean>();

	private void loadData(int id) {
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("pageSize", 10 + "");
		params.put("start", "0");
		MyHttpClient.post(this, Urls.SYSTEM_MESSAGE, params,
				new AsyncHttpResponseHandler() {

					@Override
					public void onSuccess(int statusCode, Header[] headers,
							byte[] responseBody) {
						String response = new String(responseBody);
						Logger.json(response);

						try {
							JSONObject json = new JSONObject(response)
									.getJSONObject("REP_BODY");
							if (json.getString("RSPCOD").equals("000000")) {
								JSONArray array = json
										.getJSONArray("noticeList");
								if(adaVal.size()>0){
									adaVal.clear();
								}
								for (int i = 0; i < array.length(); i++) {
									JSONObject temp = array.getJSONObject(i);
									adaVal.add(new NoticeBean(temp
											.optString("noticeTitle"), temp
											.optString("noticeBody"), temp
											.optString("noticeId"),temp.optString("createDate")));

								}
								if(adaVal.size()==0){
									findViewById(R.id.empty_notice).setVisibility(View.VISIBLE);
								}
								System.out
										.println("---------------公告数量-------------------"
												+ adaVal.size());
								if (null == adapter) {
									adapter = new NoticeMsgAdapter(
											NoticeActivity.this, adaVal);
									lv.setAdapter(adapter);
								} else {
									adapter.refreshValues(adaVal);
									adapter.notifyDataSetChanged();
									lv.onRefreshComplete();
									System.out.println("refresh--ok");
								}
							} else {
								com.lk.qf.pay.tool.T.ss(json
										.getString("RSPMSG"));

							}

						} catch (JSONException e) {
							// TODO 自动生成的 catch 块
							e.printStackTrace();
						}

					}

					@Override
					public void onFailure(int statusCode, Header[] headers,
							byte[] responseBody, Throwable error) {
					}

					@Override
					public void onStart() {
						super.onStart();
						showLoadingDialog();
					}

					@Override
					public void onFinish() {
						super.onFinish();
						dismissLoadingDialog();
					}
				});
	}

	private void initView() {
		title = findView(R.id.titlebar_notice);
		title.setActName("公告").setCanClickDestory(this, true);
		lv = findView(R.id.lv_notice);
		lv.setCanLoadMore(false);
		lv.setCanRefresh(true);
		lv.setOnItemClickListener(onitemClick);
		lv.setOnRefreshListener(onRefrsh);
	}

	OnRefreshListener onRefrsh = new OnRefreshListener() {

		@Override
		public void onRefresh() {

			loadData(0);
			System.out.println("onrefresh--------------------------");

		}
	};

	OnItemClickListener onitemClick = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			if (position < 0) {
				return;
			}
			startActivity(new Intent(NoticeActivity.this,
					NoticeDetailActivity.class).putExtra("data",
					adaVal.get(position - 1)));
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
