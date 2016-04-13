package com.lk.qf.pay.fragment;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.lk.bhb.pay.R;
import com.lk.pay.communication.AsyncHttpResponseHandler;
import com.lk.qf.pay.activity.NoticeActivity;
import com.lk.qf.pay.activity.NoticeDetailActivity;
import com.lk.qf.pay.adapter.NoticeMsgAdapter;
import com.lk.qf.pay.beans.NoticeBean;
import com.lk.qf.pay.golbal.MApplication;
import com.lk.qf.pay.golbal.Urls;
import com.lk.qf.pay.golbal.User;
import com.lk.qf.pay.tool.Logger;
import com.lk.qf.pay.tool.MyHttpClient;
import com.lk.qf.pay.wedget.CommonTitleBar;
import com.lk.qf.pay.wedget.CustomListView;
import com.lk.qf.pay.wedget.CustomListView.OnRefreshListener;

public class NoticeAFragment extends BaseFragment{ 
	private CommonTitleBar title;
	private CustomListView lv;
	private NoticeMsgAdapter adapter;
	private View layoutView;
	private Context mContext;
	
	public static BaseFragment newInstance() {
		BaseFragment fragment = new NoticeAFragment();
		return fragment;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (layoutView == null) {
			layoutView = inflater.inflate(R.layout.activity_notice, container,false);
			initView();
			loadData(0);
			Logger.init().setMethodCount(0).hideThreadInfo();

		}
		// 缓存的rootView需要判断是否已经被加过parent，
		// 如果有parent需要从parent删除，要不然会发生这个rootview已经有parent的错误。
		ViewGroup parent = (ViewGroup) layoutView.getParent();
		if (parent != null) {
			parent.removeView(layoutView);
		}
		return layoutView;
	}
	
	ArrayList<NoticeBean> adaVal = new ArrayList<NoticeBean>();

	private void loadData(int id) {
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("pageSize", 10 + "");
		params.put("start", "0");
		MyHttpClient.post(mContext, Urls.SYSTEM_MESSAGE, params,
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
									layoutView.findViewById(R.id.empty_notice).setVisibility(View.VISIBLE);
								}
								System.out
										.println("---------------公告数量-------------------"
												+ adaVal.size());
								if (null == adapter) {
									adapter = new NoticeMsgAdapter(
											mContext, adaVal);
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
		mContext = getActivity();
		title = (CommonTitleBar) layoutView.findViewById(R.id.titlebar_notice);
		title.getBtn_back().setVisibility(View.GONE);
		title.setActName("公告");
		lv = (CustomListView) layoutView.findViewById(R.id.lv_notice);
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
			startActivity(new Intent(getActivity(),
					NoticeDetailActivity.class).putExtra("data",
					adaVal.get(position - 1)));
		}
	};

	
}
