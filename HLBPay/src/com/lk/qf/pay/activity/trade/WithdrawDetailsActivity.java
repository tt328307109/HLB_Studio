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
import com.lk.qf.pay.beans.TradeBean;
import com.lk.qf.pay.wedget.CommonTitleBar;
import com.lk.qf.pay.wedget.ShowDialog;

public class WithdrawDetailsActivity extends BaseActivity{
	
	private TextView tvOrderStatus, tvTransportName;
	private LinearLayout llShow;
	private String userName;
	TradeBean bean;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.deal_details);
		String prdOrdNo = getIntent().getStringExtra("prdOrdNo");
		llShow = (LinearLayout) findViewById(R.id.ll_withdraw_detail);
		tvOrderStatus = (TextView) findViewById(R.id.tv_withdraw_detail_orderStatus);
		tvTransportName = (TextView) findViewById(R.id.tv_withdraw_detail_transportName);
	(	(CommonTitleBar)findViewById(R.id.titlebar_dealdetail)).setActName("提现详情").setCanClickDestory(this, true);
	bean=(TradeBean) getIntent().getSerializableExtra("data");
//		WithdrawDetailTask task = new WithdrawDetailTask();
//		task.execute(userName, prdOrdNo);
	}
	
//	class WithdrawDetailTask extends AsyncTask<String, Integer, Map<String, Object>>{
//		private ShowDialog dialog;
//		@Override
//		protected void onPreExecute() {
//			dialog = new ShowDialog(WithdrawDetailsActivity.this);
//			dialog.setCancelable(false);
//			dialog.show();
//			super.onPreExecute();
//		}
//		@Override
//		protected Map<String, Object> doInBackground(String... params) {
//			HashMap<String, Object> map = new HashMap<String, Object>();
//			map.put("USRMP", params[0]);
//			map.put("prdOrdNo", params[1]);
//			return NetCommunicate.getData(URLs.QUERY_ORDER_DETAILS_CASH_OUT, map);
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
		if(bean.getTranState().equals("1")){
			tvOrderStatus.setText("交易成功");
		}else if(bean.getTranState().equals("0")){
			tvOrderStatus.setText("交易失败");
		}else if(bean.getTranState().equals("2")){
			tvOrderStatus.setText("退货");
		}
		tvTransportName.setText("提现");
		LayoutInflater inflater = LayoutInflater.from(this);
		//交易号
		LinearLayout llNo = (LinearLayout) inflater.inflate(R.layout.deal_detail_item_explain, null);
		((TextView)llNo.findViewById(R.id.deal_detail_explain_key)).setText("交易号");
		((TextView)llNo.findViewById(R.id.deal_detail_explain_value)).setText("--");
		llShow.addView(llNo);
		//提现金额
		LinearLayout llAmt = (LinearLayout) inflater.inflate(R.layout.deal_detail_item_amt, null);
		((TextView)llAmt.findViewById(R.id.deal_detail_amt_key)).setText("提现金额");
		((TextView)llAmt.findViewById(R.id.deal_detail_amt_value)).setText(bean.getTranAmt());
		llShow.addView(llAmt);
		//提现手续费
//		LinearLayout llFeeAmt = (LinearLayout) inflater.inflate(R.layout.deal_detail_item_amt, null);
//		((TextView)llFeeAmt.findViewById(R.id.deal_detail_amt_key)).setText("手续费");
//		((TextView)llFeeAmt.findViewById(R.id.deal_detail_amt_value)).setText(result.get("FEEAMT").toString());
//		llShow.addView(llFeeAmt);
		//提现银行
		LinearLayout llBankName = (LinearLayout) inflater.inflate(R.layout.deal_detail_item_explain, null);
		((TextView)llBankName.findViewById(R.id.deal_detail_explain_key)).setText("提现银行");
		((TextView)llBankName.findViewById(R.id.deal_detail_explain_value)).setText("--");
		llShow.addView(llBankName);
		//银行卡号
		LinearLayout llBankNo = (LinearLayout) inflater.inflate(R.layout.deal_detail_item_explain, null);
		((TextView)llBankNo.findViewById(R.id.deal_detail_explain_key)).setText("银行卡号");
		((TextView)llBankNo.findViewById(R.id.deal_detail_explain_value)).setText(toS(bean.getBankCardNo()));
		llShow.addView(llBankNo);
		//交易时间
		LinearLayout llTime = (LinearLayout) inflater.inflate(R.layout.deal_detail_item_explain, null);
		((TextView)llTime.findViewById(R.id.deal_detail_explain_key)).setText("交易时间");
		((TextView)llTime.findViewById(R.id.deal_detail_explain_value)).setText(toS(bean.getTarnMonth()+""+bean.getTranDay()+""+bean.getTarnTime()));
		llShow.addView(llTime);
	}
	private String toS(Object str){
		if(null==str)
			return "";
		if(str.toString().contains("null")){
			return str.toString().replace("null", "");
		}
		return str.toString();
	}
}
