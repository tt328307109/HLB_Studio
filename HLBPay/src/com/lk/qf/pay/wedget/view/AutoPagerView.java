package com.lk.qf.pay.wedget.view;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.lk.bhb.pay.R;
import com.lk.qf.pay.adapter.UstorePagerAdpater;

import android.content.Context;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class AutoPagerView extends FrameLayout implements OnPageChangeListener {

	private Context mContent;
	private ViewPager mViewPager;
	private List<View> picPagelist;
	private int curItemPic = 0;
	private LinearLayout lineary;
	private List<ImageView> pointImg;

	private final Timer timer = new Timer();

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			mViewPager.setCurrentItem(curItemPic);
		};
	};

	public boolean onTouchEvent(android.view.MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			return true;
			// break;
		case MotionEvent.ACTION_MOVE:

			break;
		case MotionEvent.ACTION_UP:
			// performClick();
			break;
		}

		return super.onTouchEvent(event);
	};

	@Override
	public boolean performClick() {
		// TODO �Զ���ɵķ������
		return super.performClick();
	}

	private TimerTask task = new TimerTask() {
		@Override
		public void run() {
			if (picPagelist.size() == 0) {
				return;
			} else {
				synchronized (mViewPager) {
					curItemPic = (curItemPic + 1) % picPagelist.size();
					handler.obtainMessage().sendToTarget();
				}
			}
		}
	};

	public AutoPagerView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		init(context, attrs);
	}

	/* ���ز����ļ�ʱִ�� */
	public AutoPagerView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		init(context, attrs);
	}

	public AutoPagerView(Context context) {
		super(context);
		init(context, null);
	}

	public void init(Context context, AttributeSet attrs) {
		Log.e("ustore", "intit");
		this.mContent = context;

		pointImg = new ArrayList<ImageView>();

		FrameLayout frameContainer = new FrameLayout(mContent);

		LayoutParams containerParams = new LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

		addView(frameContainer, containerParams);

		mViewPager = new ViewPager(mContent);
		frameContainer.addView(mViewPager);

		lineary = new LinearLayout(mContent);

		LayoutParams LinearyParams = new LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
				Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM);
		lineary.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM);
		lineary.setPadding(0, 0, 0, 10);
		frameContainer.addView(lineary, LinearyParams);
	}

	public void changeCurPontImg(int curPos) {
		for (int i = 0; i < picPagelist.size(); i++) {
			if (i == curPos) {
				pointImg.get(i).setBackgroundResource(
						R.drawable.page_indicator_focused);
			} else {
				pointImg.get(i).setBackgroundResource(
						R.drawable.page_indicator_unfocused);
			}
		}
	}

	public void setPageViewPics(List<View> picPageList) {
		this.picPagelist = picPageList;
		mViewPager.setAdapter(new UstorePagerAdpater(this.picPagelist));
		mViewPager.setOnPageChangeListener(this);

		for (int i = 0; i < picPageList.size(); i++) {
			ImageView imageview = new ImageView(mContent);
			imageview
					.setBackgroundResource(R.drawable.page_indicator_unfocused);
			pointImg.add(imageview);
			// imageview.setPadding(10, 0, 10, 0);
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			params.setMargins(0, 10, 10, 0);
			imageview.setLayoutParams(params);
			lineary.addView(imageview);
		}

		mViewPager.setCurrentItem(0);
		pointImg.get(0)
				.setBackgroundResource(R.drawable.page_indicator_focused);// ѡ��
		timer.schedule(task, 4000, 4000);
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {

	}

	@Override
	public void onPageSelected(int position) {

		this.curItemPic = position;
		changeCurPontImg(position);
	}

	public Timer getTimer() {
		return timer;
	}

	/**
	 * 解决与viewpager滑动事件冲突
	 */
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		getParent().requestDisallowInterceptTouchEvent(true);
		return super.dispatchTouchEvent(ev);
	}

}
