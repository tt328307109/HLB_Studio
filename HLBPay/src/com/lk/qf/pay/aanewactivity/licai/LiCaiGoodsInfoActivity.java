package com.lk.qf.pay.aanewactivity.licai;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.lk.bhb.pay.R;
import com.lk.qf.pay.activity.BaseActivity;
import com.lk.qf.pay.beans.LicaiNewGoodsInfo;
import com.lk.qf.pay.utils.TimeUtils;

public class LiCaiGoodsInfoActivity extends LicaiBaseActivity implements
		OnClickListener {

	private TextView tvYesterdayEarningsAccount,
			tvTotalEarningsAccount, tvTotalBuyAccount, tvYH, tvLimitDay,
			tvQixiDate, tvDqDate,tvShow,tvTitle;
	private String goodsName = "",state="";
	private int day = 0;
	private LicaiNewGoodsInfo info;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.licai_goods_info_layout);
		init();
	}

	private void init() {
		findViewById(R.id.common_title_back_licai_record).setOnClickListener(this);
		tvTitle = (TextView) findViewById(R.id.common_title_name_licai_record);
//		tvGoodsName = (TextView) findViewById(R.id.tv_info_licai_new_goods_title);
		tvYesterdayEarningsAccount = (TextView) findViewById(R.id.tv_licai_new_info_yesterdayEarnings);
		tvTotalEarningsAccount = (TextView) findViewById(R.id.tv_licai_total_info_account);
		tvTotalBuyAccount = (TextView) findViewById(R.id.tv_info_licai_buy_info_account);
		tvYH = (TextView) findViewById(R.id.tv_add_licai_yuqi_yearNh);
		tvLimitDay = (TextView) findViewById(R.id.tv_info_licai_qixianDay);
		tvQixiDate = (TextView) findViewById(R.id.tv_info_licai_qixiDay);
		tvDqDate = (TextView) findViewById(R.id.tv_info_licai_daoqiDay);
		tvShow = (TextView) findViewById(R.id.tv_licai_info_state);

		Intent intent = getIntent();
		if (intent != null) {
			info = intent.getParcelableExtra("info");
			if (info != null) {
				tvTitle.setText(info.getNameTitle());
//			tvGoodsName.setText(info.getNameTitle());
				tvYesterdayEarningsAccount.setText(info.getYesterDayEarningsAccount());
				tvTotalEarningsAccount.setText(info.getTotalEarningsAccount());
				tvTotalBuyAccount.setText(info.getBuyAccount());
				tvYH.setText(info.getYearEarnings());
				tvLimitDay.setText(info.getTimeLimit());
				tvQixiDate.setText(TimeUtils.changeDateFormat("yyyy.MM.dd", info.getQixiDate()));
				tvDqDate.setText(TimeUtils.changeDateFormat("yyyy.MM.dd", info.getDaoqiDate()));
				day = Integer.parseInt(info.getTimeLimit());
				state = info.getLoanType();
				if (!state.equals("dai")) {
					tvShow.setText("");
				}
			}
		}

//		Date date = new Date();
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
//		tvQixiDate.setText(sdf.format(date));
//
//		try {
//			tvDqDate.setText(TimeUtils.getStatetime("yyyy.MM.dd", day));
//		} catch (ParseException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		finish();
	}

}
