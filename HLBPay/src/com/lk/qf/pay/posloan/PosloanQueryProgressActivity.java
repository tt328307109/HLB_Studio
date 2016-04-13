package com.lk.qf.pay.posloan;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;

import com.lk.bhb.pay.R;
import com.lk.qf.pay.activity.BaseActivity;
import com.lk.qf.pay.wedget.CommonTitleBar;

public class PosloanQueryProgressActivity extends BaseActivity {

	private int state = 0;
	private ImageView img_posloan_chushen, img_posloan_chushen_xian,
			img_posloan_fushen, img_posloan_fushen_xian, img_posloan_zhongshen,
			img_posloan_zhongshen_xian, img_posloan_end;

	private Handler mHandler;
	private CommonTitleBar title;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.posloan_quer_progress_layout);

		init();
		changeState(state);
		mHandler = new Handler() {
			public void handleMessage(android.os.Message msg) {
				if (msg.what == 1) {
					changeState(msg.arg1);
				}
			};
		};
		new Thread() {
			@Override
			public void run() {
				while (state<5) {
					try {
						Thread.sleep(300);
						state++;
						Message msg = new Message();
						msg.what = 1;
						msg.arg1 = state;
						mHandler.sendMessage(msg);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			};
		}.start();
	}

	private void changeState(int state) {
		switch (state) {
		case 0:// 初审没过
			img_posloan_chushen.setBackgroundResource(R.drawable.yuan_gray);
			img_posloan_chushen_xian
					.setBackgroundResource(R.drawable.jindu_xian_2);
			img_posloan_fushen.setBackgroundResource(R.drawable.yuan_gray);
			img_posloan_fushen_xian
					.setBackgroundResource(R.drawable.jindu_xian_2);
			img_posloan_zhongshen.setBackgroundResource(R.drawable.yuan_gray);
			img_posloan_zhongshen_xian
					.setBackgroundResource(R.drawable.jindu_xian_2);
			img_posloan_end.setBackgroundResource(R.drawable.yuan_gray);
			break;

		case 1:// 初审刚过
			img_posloan_chushen.setBackgroundResource(R.drawable.yuan_orange);
			img_posloan_chushen_xian
					.setBackgroundResource(R.drawable.jindu_xian_1);
			img_posloan_fushen.setBackgroundResource(R.drawable.yuan_gray);
			img_posloan_fushen_xian
					.setBackgroundResource(R.drawable.jindu_xian_2);
			img_posloan_zhongshen.setBackgroundResource(R.drawable.yuan_gray);
			img_posloan_zhongshen_xian
					.setBackgroundResource(R.drawable.jindu_xian_2);
			img_posloan_end.setBackgroundResource(R.drawable.yuan_gray);
			break;

		case 2:// 复审刚过
			img_posloan_chushen.setBackgroundResource(R.drawable.yuan_orange);
			img_posloan_chushen_xian
					.setBackgroundResource(R.drawable.jindu_xian_1);
			img_posloan_fushen.setBackgroundResource(R.drawable.yuan_orange);
			img_posloan_fushen_xian
					.setBackgroundResource(R.drawable.jindu_xian_1);
			img_posloan_zhongshen.setBackgroundResource(R.drawable.yuan_gray);
			img_posloan_zhongshen_xian
					.setBackgroundResource(R.drawable.jindu_xian_2);
			img_posloan_end.setBackgroundResource(R.drawable.yuan_gray);
			break;

		case 3:// 终审刚过
			img_posloan_chushen.setBackgroundResource(R.drawable.yuan_orange);
			img_posloan_chushen_xian
					.setBackgroundResource(R.drawable.jindu_xian_1);
			img_posloan_fushen.setBackgroundResource(R.drawable.yuan_orange);
			img_posloan_fushen_xian
					.setBackgroundResource(R.drawable.jindu_xian_1);
			img_posloan_zhongshen.setBackgroundResource(R.drawable.yuan_orange);
			img_posloan_zhongshen_xian
					.setBackgroundResource(R.drawable.jindu_xian_1);
			img_posloan_end.setBackgroundResource(R.drawable.yuan_gray);
			break;

		case 4:// 通过
			img_posloan_chushen.setBackgroundResource(R.drawable.yuan_orange);
			img_posloan_chushen_xian
					.setBackgroundResource(R.drawable.jindu_xian_1);
			img_posloan_fushen.setBackgroundResource(R.drawable.yuan_orange);
			img_posloan_fushen_xian
					.setBackgroundResource(R.drawable.jindu_xian_1);
			img_posloan_zhongshen.setBackgroundResource(R.drawable.yuan_orange);
			img_posloan_zhongshen_xian
					.setBackgroundResource(R.drawable.jindu_xian_1);
			img_posloan_end.setBackgroundResource(R.drawable.yuan_big);
			break;

		}

	}

	private void init() {
		title = (CommonTitleBar) findViewById(R.id.titlebar_posLoan_progress);
		title.setActName("贷款进度");
		title.setCanClickDestory(this, true);
		
		img_posloan_chushen = (ImageView) findViewById(R.id.img_posloan_chushen);
		img_posloan_chushen_xian = (ImageView) findViewById(R.id.img_posloan_chushen_xian);
		img_posloan_fushen = (ImageView) findViewById(R.id.img_posloan_fushen);
		img_posloan_fushen_xian = (ImageView) findViewById(R.id.img_posloan_fushen_xian);
		img_posloan_zhongshen = (ImageView) findViewById(R.id.img_posloan_zhongshen);
		img_posloan_zhongshen_xian = (ImageView) findViewById(R.id.img_posloan_zhongshen_xian);
		img_posloan_end = (ImageView) findViewById(R.id.img_posloan_end);

	}

}
