package com.lk.qf.pay.indiana.activity;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import com.lk.bhb.pay.R;
import com.lk.qf.pay.utils.ExpandableTextViewUtils;
import com.lk.qf.pay.wedget.CommonTitleBarYellow;

public class CommonProblemActivity extends IndianaBaseActivity implements
		OnClickListener {

	private CommonTitleBarYellow title;
	private int[] tvQIds = new int[] { R.id.tv_indian_commonProblem_q1,
			R.id.tv_indian_commonProblem_q2, R.id.tv_indian_commonProblem_q3,
			R.id.tv_indian_commonProblem_q4, R.id.tv_indian_commonProblem_q5,
			R.id.tv_indian_commonProblem_q6, R.id.tv_indian_commonProblem_q7,
			R.id.tv_indian_commonProblem_q8, R.id.tv_indian_commonProblem_q9 };

	private TextView tvAnswer1, tvAnswer2, tvAnswer3, tvAnswer4, tvAnswer5,
			tvAnswer6, tvAnswer7, tvAnswer8, tvAnswer9;
	private View view;
	private ExpandableTextViewUtils eUtils;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.common_problem_layout);
		init();
	}

	private void init() {
		title = (CommonTitleBarYellow) findViewById(R.id.titlebar_indianCommonProblem);
		title.setActName("常见问题");
		title.setCanClickDestory(this, true);
		tvAnswer1 = (TextView) findViewById(R.id.tv_indian_commonProblem_a1);
		tvAnswer2 = (TextView) findViewById(R.id.tv_indian_commonProblem_a2);
		tvAnswer3 = (TextView) findViewById(R.id.tv_indian_commonProblem_a3);
		tvAnswer4 = (TextView) findViewById(R.id.tv_indian_commonProblem_a4);
		tvAnswer5 = (TextView) findViewById(R.id.tv_indian_commonProblem_a5);
		tvAnswer6 = (TextView) findViewById(R.id.tv_indian_commonProblem_a6);
		tvAnswer7 = (TextView) findViewById(R.id.tv_indian_commonProblem_a7);
		tvAnswer8 = (TextView) findViewById(R.id.tv_indian_commonProblem_a8);
		tvAnswer9 = (TextView) findViewById(R.id.tv_indian_commonProblem_a9);

		for (int i = 0; i < tvQIds.length; i++) {
			findViewById(tvQIds[i]).setOnClickListener(this);
		}
		eUtils = new ExpandableTextViewUtils();
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.tv_indian_commonProblem_q1:
			view = tvAnswer1;
			break;
		case R.id.tv_indian_commonProblem_q2:
			view = tvAnswer2;
			break;
		case R.id.tv_indian_commonProblem_q3:
			view = tvAnswer3;
			break;
		case R.id.tv_indian_commonProblem_q4:
			view = tvAnswer4;
			break;
		case R.id.tv_indian_commonProblem_q5:
			view = tvAnswer5;
			break;
		case R.id.tv_indian_commonProblem_q6:
			view = tvAnswer6;
			break;
		case R.id.tv_indian_commonProblem_q7:
			view = tvAnswer7;
			break;
		case R.id.tv_indian_commonProblem_q8:
			view = tvAnswer8;
			break;
		case R.id.tv_indian_commonProblem_q9:
			view = tvAnswer9;
			break;

		default:
			break;
		}
		expand();
	}

	private void expand() {
		if (view.getVisibility() == View.GONE) {
			eUtils.expand(view);
		} else {
			eUtils.collapse(view);
		}
	}

}
