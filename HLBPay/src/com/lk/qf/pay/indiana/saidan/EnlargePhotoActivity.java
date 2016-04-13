package com.lk.qf.pay.indiana.saidan;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;

import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lk.bhb.pay.R;
import com.lk.qf.pay.activity.BaseActivity;

public class EnlargePhotoActivity extends Activity implements OnClickListener{
	@ViewInject(R.id.iv_enlarge_activity)
	private ImageView view1;
	private BitmapUtils bitmapUtils;
	String url;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE); 
		setContentView(R.layout.enlarge_photo);
		
		url = getIntent().getStringExtra("url");
		view1 = (ImageView) findViewById(R.id.iv_enlarge_activity);
		view1.setOnClickListener(this);
		ViewUtils.inject(this);
		bitmapUtils = new BitmapUtils(this);
		bitmapUtils.display(view1, url);
	}
	@Override
	public void onClick(View v) {
		finish();
	}
}
