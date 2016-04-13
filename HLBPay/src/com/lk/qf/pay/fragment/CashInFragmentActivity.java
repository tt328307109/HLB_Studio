package com.lk.qf.pay.fragment;

import java.text.DecimalFormat;
import java.util.ArrayList;

import com.landicorp.android.mpos.reader.LandiMPos;
import com.lk.bhb.pay.R;
import com.lk.qf.pay.aanewactivity.CreatePayCodeActivity;
import com.lk.qf.pay.aanewactivity.shoukuan.CollectionCodeActivity;
import com.lk.qf.pay.activity.BaseActivity;
import com.lk.qf.pay.activity.BaseFragmentActivity;
import com.lk.qf.pay.activity.swing.SwingHXCardActivity;
import com.lk.qf.pay.beans.PosData;
import com.lk.qf.pay.golbal.Actions;
import com.lk.qf.pay.golbal.MApplication;
import com.lk.qf.pay.sharedpref.SharedPrefConstant;
import com.lk.qf.pay.tool.T;
import com.lk.qf.pay.utils.AmountUtils;
import com.lk.qf.pay.v50.PayByV50CardActivity;
import com.lk.qf.pay.wedget.CommonTitleBar;
import com.lk.qf.pay.wedget.CrashInGallery.GalleryFlow;
import com.lk.qf.pay.wedget.customdialog.ActionSheetDialog;
import com.lk.qf.pay.wedget.customdialog.ActionSheetDialog.OnSheetItemClickListener;
import com.lk.qf.pay.wedget.customdialog.ActionSheetDialog.SheetItemColor;
import com.lk.qf.pay.zxb.ZXBDeviceListActivity;

import android.app.Service;
import android.app.ApplicationErrorReport.CrashInfo;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.InputType;
import android.text.TextUtils;
import android.text.method.NumberKeyListener;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ImageView.ScaleType;

public class CashInFragmentActivity extends BaseActivity implements OnClickListener, OnItemClickListener {

	private ImageButton  btnWeixin, btnZhifubao, btnyifubao,
	btnBaidu;
	private Button  btn0, btn1, btn2, btn3, btn4, btn5, btn6, btn7, btn8,btnDel,btnPay,
	btn9, btnPoint;
	private String str00, str0, str1, str2, str3, str4, str5, str6, str7, str8,
	str9, strPoint;
	private EditText amountEdit;
	private StringBuilder sb;
	private boolean isDian = false;
	private Vibrator vibrator;
	private DecimalFormat nf;
	private LandiMPos reader;
	private Context ctx;
	private String buletooth;
	private String state, usesort, amount;
	private CommonTitleBar title;

	int screnWidth;
	GalleryFlow mGallery = null;
	ArrayList<BitmapDrawable> mBitmaps = new ArrayList<BitmapDrawable>();


	private void generateBitmaps()
	{
		int[] ids =
			{
				R.drawable.icon_zhifubaogq,
				R.drawable.icon_baiduqianbaogq,
				R.drawable.icon_weixingq,
				R.drawable.icon_yifubaogq,
				R.drawable.icon_shuaka
			};

		for (int id : ids)
		{
			Bitmap bitmap = createReflectedBitmapById(id);
			if (null != bitmap)
			{
				BitmapDrawable drawable = new BitmapDrawable(bitmap);
				drawable.setAntiAlias(true);
				mBitmaps.add(drawable);
			}
		}
	}

	private Bitmap createReflectedBitmapById(int resId)
	{
		Drawable drawable = getResources().getDrawable(resId);
		if (drawable instanceof BitmapDrawable)
		{
			Bitmap bitmap = ((BitmapDrawable)drawable).getBitmap();
			//            Bitmap reflectedBitmap = BitmapUtil.createReflectedBitmap(bitmap);

			return bitmap;
		}

		return null;
	}

	private class GalleryAdapter extends BaseAdapter
	{
		private int count = 5;
		@Override
		public int getCount()
		{
//			return mBitmaps.size();
		    return Integer.MAX_VALUE;  
		}

		@Override
		public Object getItem(int position)
		{
			return null;
		}

		@Override
		public long getItemId(int position)
		{
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			if(position > count -1) {
				return getView(position % count,convertView,parent);  
			}
			if (null == convertView)
			{
				convertView = new MyImageView(CashInFragmentActivity.this);
				convertView.setLayoutParams(new Gallery.LayoutParams(120 , 120));
			}

			ImageView imageView = (ImageView)convertView;
			imageView.setImageDrawable(mBitmaps.get(position));
			imageView.setScaleType(ScaleType.FIT_XY);

			return imageView;
		}
	}

	private class MyImageView extends ImageView
	{
		public MyImageView(Context context)
		{
			this(context, null);
		}

		public MyImageView(Context context, AttributeSet attrs)
		{
			super(context, attrs, 0);
		}

		public MyImageView(Context context, AttributeSet attrs, int defStyle)
		{
			super(context, attrs, defStyle);
		}

		protected void onDraw(Canvas canvas)
		{
			super.onDraw(canvas);
		}
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_cashin);
		ctx = CashInFragmentActivity.this;
		reader = LandiMPos.getInstance(CashInFragmentActivity.this);
		vibrator = (Vibrator) ctx.getSystemService(Service.VIBRATOR_SERVICE);
		nf = new DecimalFormat("0.00"); // 保留几位小数
		buletooth = MApplication.mSharedPref.getSharePrefString(
				"blueTootchAddress", null);
		initView();

		title = (CommonTitleBar) findViewById(R.id.titlebar_chongzhi_new);
		title.setActName("充值");
		title.setCanClickDestory(this, true);
		amountEdit.setText("0.00");

		state = MApplication.mSharedPref
				.getSharePrefString(SharedPrefConstant.STATE);
		usesort = MApplication.mSharedPref
				.getSharePrefString(SharedPrefConstant.USESORT);

		// view.findViewById(R.id.ll_cash).setPadding(0,
		// MyGetStatusUtils.getStatusBarHeight(ctx), 0, 0);
		generateBitmaps();
		WindowManager wm = (WindowManager) CashInFragmentActivity.this
                .getSystemService(Context.WINDOW_SERVICE);
		screnWidth = wm.getDefaultDisplay().getWidth();
		
		mGallery = (GalleryFlow) findViewById(R.id.gallery_flow);
		mGallery.setBackgroundColor(Color.WHITE);
		//设置图片的间隔距离
		mGallery.setSpacing(200);
		mGallery.setFadingEdgeLength(0);
		mGallery.setGravity(Gravity.CENTER_VERTICAL);
		mGallery.setAdapter(new GalleryAdapter());
//		mGallery.setOnItemClickListener(this);
		mGallery.setSelection(Integer.MAX_VALUE / 2 + 1);

	}

//	@Override
//	public View onCreateView(LayoutInflater inflater, ViewGroup container,
//			Bundle savedInstanceState) {
//		// TODO Auto-generated method stub
//		View view = inflater.inflate(R.layout.fragment_cashin, null);
//		initView(view);
//		amountEdit.setText("0.00");
//
//		state = MApplication.mSharedPref
//				.getSharePrefString(SharedPrefConstant.STATE);
//		usesort = MApplication.mSharedPref
//				.getSharePrefString(SharedPrefConstant.USESORT);
//
//		// view.findViewById(R.id.ll_cash).setPadding(0,
//		// MyGetStatusUtils.getStatusBarHeight(ctx), 0, 0);
//		generateBitmaps();
//
//		mGallery = (GalleryFlow) view.findViewById(R.id.gallery_flow);
//		mGallery.setBackgroundColor(Color.WHITE);
//		mGallery.setSpacing(150);
//		mGallery.setFadingEdgeLength(0);
//		mGallery.setGravity(Gravity.CENTER_VERTICAL);
//		mGallery.setAdapter(new GalleryAdapter());
//		mGallery.setOnItemClickListener(this);
//		return view;
//	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		amountEdit.setText("0.00");
		sb = new StringBuilder();
		amountEdit.setKeyListener(new MNumberKeyListener());
		amountEdit.setFocusable(true);
		amount = "";
	}

	private void initView() {

		amountEdit = (EditText) findViewById(R.id.cashin_amount_edit);
//		btn00 = (Button) findViewById(R.id.btn00);
		btn0 = (Button) findViewById(R.id.btn0);
		btn1 = (Button) findViewById(R.id.btn1);
		btn2 = (Button) findViewById(R.id.btn2);
		btn3 = (Button) findViewById(R.id.btn3);
		btn4 = (Button) findViewById(R.id.btn4);
		btn5 = (Button) findViewById(R.id.btn5);
		btn6 = (Button) findViewById(R.id.btn6);
		btn7 = (Button) findViewById(R.id.btn7);
		btn8 = (Button) findViewById(R.id.btn8);
		btn9 = (Button) findViewById(R.id.btn9);
		btnPoint = (Button) findViewById(R.id.btn_point);
		btnDel = (Button) findViewById(R.id.btn_del);
		btnPay = (Button) findViewById(R.id.btn_pay);

		btnWeixin = (ImageButton) findViewById(R.id.btn_sk_weixin);
		btnZhifubao = (ImageButton) findViewById(R.id.btn_sk_zhifubao);
		btnyifubao = (ImageButton) findViewById(R.id.btn_sk_yifubao);
		btnBaidu = (ImageButton) findViewById(R.id.btn_sk_baidu);

		findViewById(R.id.btn_back);
//		btn00.setOnClickListener(this);
		btn0.setOnClickListener(this);
		btn1.setOnClickListener(this);
		btn2.setOnClickListener(this);
		btn3.setOnClickListener(this);
		btn4.setOnClickListener(this);
		btn5.setOnClickListener(this);
		btn6.setOnClickListener(this);
		btn7.setOnClickListener(this);
		btn8.setOnClickListener(this);
		btn9.setOnClickListener(this);
		btnPoint.setOnClickListener(this);
		btnDel.setOnClickListener(this);
		btnPay.setOnClickListener(this);
		btnWeixin.setOnClickListener(this);
		btnZhifubao.setOnClickListener(this);
		btnyifubao.setOnClickListener(this);
		btnBaidu.setOnClickListener(this);

	}

	public class MNumberKeyListener extends NumberKeyListener {
		@Override
		protected char[] getAcceptedChars() {
			char[] numberChars = { '.', '0', '1', '2', '3', '4', '5', '6', '7',
					'8', '9' };
			return numberChars;
		}

		@Override
		public int getInputType() {
			// return InputType.TYPE_NUMBER_FLAG_DECIMAL;
			return InputType.TYPE_DATETIME_VARIATION_NORMAL;
		}

	}

	private void insert(Button btn) {
		String str = btn.getText().toString();
		if (sb.length() == 0 && str.equals("00")) {
			return;
		}
		if (isDian) {
			if (sb.toString().contains(".")) {
				String sbStr = new StringBuilder(sb.toString()).reverse()
						.toString();

				if (sbStr.indexOf(".") == 1) {
					sb.append(str);
				}
			} else {
				if (sb.length() == 0) {
					sb.append("0." + str);
				} else {
					sb.append("." + str);
				}
			}
		} else {
			sb.append(str);
		}
		amount = nf.format(Double.parseDouble(sb.toString()));
		if (amount.length() < 14) {
			amountEdit.setText(amount);
		}

	}

	@Override
	public void onClick(View v) {
		vibrator.vibrate(new long[] { 0, 15 }, -1);
		switch (v.getId()) {
//		case R.id.btn00:
//			insert(btn00);
//			break;
		case R.id.btn0:
			insert(btn0);
			break;
		case R.id.btn1:
			insert(btn1);
			break;
		case R.id.btn2:
			insert(btn2);
			break;
		case R.id.btn3:
			insert(btn3);
			break;
		case R.id.btn4:
			insert(btn4);
			break;
		case R.id.btn5:
			insert(btn5);
			break;
		case R.id.btn6:
			insert(btn6);
			break;
		case R.id.btn7:
			insert(btn7);
			break;
		case R.id.btn8:
			insert(btn8);
			break;
		case R.id.btn9:
			insert(btn9);
			break;
		case R.id.btn_point:
			isDian = true;

			break;
		case R.id.btn_del:
			isDian = false;
			sb.delete(0, sb.length());
			amount = "";
			amountEdit.setText("");
			break;
		case R.id.btn_pay:
			int p = mGallery.getSelectedItemPosition();
			if(p % 5 == 0) {
				//支付宝
				if (judgeUserIsOk()) {
					Intent intentZhifubao = new Intent(CashInFragmentActivity.this,
							CollectionCodeActivity.class);
					intentZhifubao.setAction(Actions.ACTION_ZHIFUBAO);
					intentZhifubao.putExtra("account", amount);
					startActivity(intentZhifubao);
				}
			} else if(p % 5 == 1) {
				//百度
				if (judgeUserIsOk()) {
					Intent intentBaidu = new Intent(CashInFragmentActivity.this,
							CollectionCodeActivity.class);
					intentBaidu.setAction(Actions.ACTION_BAIDU);
					intentBaidu.putExtra("account", amount);
					startActivity(intentBaidu);
				}
			}
			else if(p % 5 == 2) {
				//微信
				if (judgeUserIsOk()) {
					Intent intentWeixin = new Intent(CashInFragmentActivity.this,
							CollectionCodeActivity.class);
					intentWeixin.setAction(Actions.ACTION_WEIXIN);
					intentWeixin.putExtra("account", amount);
					startActivity(intentWeixin);
				}
			}
			else if(p % 5 == 3) {
				//易福报
				if (judgeUserIsOk()) {
					Intent intentYifubao = new Intent(CashInFragmentActivity.this,
							CollectionCodeActivity.class);
					intentYifubao.setAction(Actions.ACTION_YIFUBAO);
					intentYifubao.putExtra("account", amount);
					startActivity(intentYifubao);
				}
			}
			else if(p % 5 == 4) {
				//刷卡
				if (judgeUserIsOk()) {

					goBrush();
				}
			}
		
			break;
		case R.id.btn_sk_baidu:
			if (judgeUserIsOk()) {
				Intent intentBaidu = new Intent(CashInFragmentActivity.this,
						CollectionCodeActivity.class);
				intentBaidu.setAction(Actions.ACTION_BAIDU);
				intentBaidu.putExtra("account", amount);
				startActivity(intentBaidu);
			}
			break;
		case R.id.btn_sk_weixin:
			if (judgeUserIsOk()) {
				Intent intentWeixin = new Intent(CashInFragmentActivity.this,
						CollectionCodeActivity.class);
				intentWeixin.setAction(Actions.ACTION_WEIXIN);
				intentWeixin.putExtra("account", amount);
				startActivity(intentWeixin);
			}
			break;
		case R.id.btn_sk_zhifubao:
			if (judgeUserIsOk()) {
				Intent intentZhifubao = new Intent(CashInFragmentActivity.this,
						CollectionCodeActivity.class);
				intentZhifubao.setAction(Actions.ACTION_ZHIFUBAO);
				intentZhifubao.putExtra("account", amount);
				startActivity(intentZhifubao);
			}
			break;
		case R.id.btn_sk_yifubao:
			if (judgeUserIsOk()) {
				Intent intentYifubao = new Intent(CashInFragmentActivity.this,
						CollectionCodeActivity.class);
				intentYifubao.setAction(Actions.ACTION_YIFUBAO);
				intentYifubao.putExtra("account", amount);
				startActivity(intentYifubao);
			}
			break;
		}
	}

	private boolean judgeUserIsOk() {
		boolean isOk = true;
		if (usesort.equals("0")) {
			T.ss("商户未缴纳押金");
			isOk = false;
		}
		if (!state.equals("en")) {
			T.ss("该商户尚未通过审核");
			isOk = false;
		}
		return isOk;
	}

	private void goBrush() {
		String amount = amountEdit.getText().toString().trim();
		amount = AmountUtils.changeY2F(amount);
		if (TextUtils.isEmpty(amount)) {
			Toast.makeText(ctx, "金额格式不正确", Toast.LENGTH_SHORT).show();
		} else {
			// int money = Integer.valueOf(amount) / 100;
			long money = Long.valueOf(amount);
			// double money = Double.valueOf(amount);
			if (money > 0) {
				goStepTwo(amount);
			} else {
				Toast.makeText(CashInFragmentActivity.this, "金额不能小于0元！", Toast.LENGTH_SHORT)
				.show();
			}
		}
	}


	private void goStepTwo(String amount) {
		// Intent it = new Intent(ctx, CashInStepTwoActivity.class);
		// it.putExtra("account", amount);
		// startActivity(it);
		// createOrder(amount);
		nextStep();
		PosData.getPosData().setPayAmt(amount);
		PosData.getPosData().setPayType("01");
	}

	private void nextStep() {
		new ActionSheetDialog(CashInFragmentActivity.this)
		.builder()
		.setTitle("请选择刷卡器类型")
		.setCancelable(false)
		.setCanceledOnTouchOutside(true)
		.addSheetItem("音频刷卡器", SheetItemColor.Blue,
				new OnSheetItemClickListener() {
			@Override
			public void onClick(int which) {
				startActivity(new Intent(ctx,
						SwingHXCardActivity.class)
				.setAction(Actions.ACTION_CASHIN));
			}
		})
		.addSheetItem("蓝牙刷卡器", SheetItemColor.Blue,
				new OnSheetItemClickListener() {
			@Override
			public void onClick(int which) {
				Intent it = new Intent(ctx,
						ZXBDeviceListActivity.class);
				it.setAction(Actions.ACTION_CASHIN);
				startActivity(it);
			}
		})
		.addSheetItem("键盘蓝牙刷卡器", SheetItemColor.Blue,
				new OnSheetItemClickListener() {
			@Override
			public void onClick(int which) {
				startActivity(new Intent(ctx,
						PayByV50CardActivity.class)
				.setAction(Actions.ACTION_CASHIN));
			}
		}).show();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		//切换标题的字
		int i = position;
		System.out.println("");
		switch (position % 5) {
		case 0:
			if (judgeUserIsOk()) {
				Intent intentZhifubao = new Intent(CashInFragmentActivity.this,
						CollectionCodeActivity.class);
				intentZhifubao.setAction(Actions.ACTION_ZHIFUBAO);
				intentZhifubao.putExtra("account", amount);
				startActivity(intentZhifubao);
			}
			break;
		case 1:
			if (judgeUserIsOk()) {
				Intent intentBaidu = new Intent(CashInFragmentActivity.this,
						CollectionCodeActivity.class);
				intentBaidu.setAction(Actions.ACTION_BAIDU);
				intentBaidu.putExtra("account", amount);
				startActivity(intentBaidu);
			}
			break;
		case 2:
			if (judgeUserIsOk()) {
				Intent intentWeixin = new Intent(CashInFragmentActivity.this,
						CollectionCodeActivity.class);
				intentWeixin.setAction(Actions.ACTION_WEIXIN);
				intentWeixin.putExtra("account", amount);
				startActivity(intentWeixin);
			}
			break;
		case 3:
			if (judgeUserIsOk()) {
				Intent intentYifubao = new Intent(CashInFragmentActivity.this,
						CollectionCodeActivity.class);
				intentYifubao.setAction(Actions.ACTION_YIFUBAO);
				intentYifubao.putExtra("account", amount);
				startActivity(intentYifubao);
			}
			break;
		case 4:
			

		default:
			break;
		}
	}
}
