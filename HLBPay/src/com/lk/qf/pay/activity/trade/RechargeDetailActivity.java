package com.lk.qf.pay.activity.trade;

import java.util.HashMap;
import java.util.Map;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lk.bhb.pay.R;
import com.lk.qf.pay.activity.BaseActivity;
import com.lk.qf.pay.beans.Entity;
import com.lk.qf.pay.wedget.CommonTitleBar;
import com.lk.qf.pay.wedget.ShowDialog;

public class RechargeDetailActivity extends BaseActivity{
	
	private TextView tvOrderStatus, tvTransportName;
	private LinearLayout llShow;
	private String userName;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.deal_details);
		String prdOrdNo = getIntent().getStringExtra("prdOrdNo");
		llShow = (LinearLayout) findViewById(R.id.ll_withdraw_detail);
		tvOrderStatus = (TextView) findViewById(R.id.tv_withdraw_detail_orderStatus);
		tvTransportName = (TextView) findViewById(R.id.tv_withdraw_detail_transportName);
		((CommonTitleBar)findViewById(R.id.titlebar_dealdetail)).setActName(getResources().getString(R.string.deal_detail)).setCanClickDestory(this, true);
//		RechargeDetailTask task = new RechargeDetailTask();
//		task.execute(userName, prdOrdNo);
	}
	
//	class RechargeDetailTask extends AsyncTask<String, Integer, Map<String, Object>>{
//		private ShowDialog dialog;
//		@Override
//		protected void onPreExecute() {
//			dialog = new ShowDialog(RechargeDetailActivity.this);
//			dialog.setCancelable(false);
//			dialog.show();
//			super.onPreExecute();
//		}
//		@Override
//		protected Map<String, Object> doInBackground(String... params) {
//			HashMap<String, Object> map = new HashMap<String, Object>();
//			map.put("USRMP", params[0]);
//			map.put("prdOrdNo", params[1]);
//			return NetCommunicate.getData(URLs.QUERY_ORDER_DETAILS_RECHARGE, map);
//		}
//		@Override
//		protected void onPostExecute(Map<String, Object> result) {
//			dialog.cancel();
//			if (null != result) {
//				if (Entity.STATE_OK.equals(result.get(Entity.RSPCOD))) {
//					showDetail(result);
//				}else if (Entity.STATE_OUT_TIME.equals(result.get(Entity.RSPCOD))) {
//					checkLogin();
//				}else{
//					
//				}
//			}	
//			super.onPostExecute(result);
//		}
//	}
	
	private void showDetail(Map<String, Object> result) {
		tvOrderStatus.setText(result.get("ORDSTATUSNAME").toString());
		tvTransportName.setText(result.get("TRANSORTNAME").toString());
		LayoutInflater inflater = LayoutInflater.from(this);
		
		LinearLayout llNo = (LinearLayout) inflater.inflate(R.layout.deal_detail_item_explain, null);
		((TextView)llNo.findViewById(R.id.deal_detail_explain_key)).setText("交易号");
		((TextView)llNo.findViewById(R.id.deal_detail_explain_value)).setText(result.get("PRDORDNO").toString());
		llShow.addView(llNo);
		
		LinearLayout llAmt = (LinearLayout) inflater.inflate(R.layout.deal_detail_item_amt, null);
		((TextView)llAmt.findViewById(R.id.deal_detail_amt_key)).setText("交易金额");
		((TextView)llAmt.findViewById(R.id.deal_detail_amt_value)).setText(result.get("ORDAMT").toString());
		llShow.addView(llAmt);
		
		LinearLayout llTime = (LinearLayout) inflater.inflate(R.layout.deal_detail_item_explain, null);
		((TextView)llTime.findViewById(R.id.deal_detail_explain_key)).setText("交易时间");
		((TextView)llTime.findViewById(R.id.deal_detail_explain_value)).setText(result.get("ORDERDATE").toString());
		llShow.addView(llTime);
		
	}
}

