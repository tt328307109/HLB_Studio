package com.lk.qf.pay.aanewactivity;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar.LayoutParams;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Base64;
import android.util.FloatMath;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lidroid.xutils.view.annotation.event.OnTouch;
import com.lk.bhb.pay.R;
import com.lk.qf.pay.activity.BaseActivity;
import com.lk.qf.pay.golbal.MApplication;
import com.lk.qf.pay.golbal.MyUrls;
import com.lk.qf.pay.golbal.User;
import com.lk.qf.pay.sharedpref.SharedPrefConstant;
import com.lk.qf.pay.tool.T;
import com.lk.qf.pay.utils.MyMdFivePassword;
import com.lk.qf.pay.wedget.CommonTitleBar;

public class ShiMingChaXunActivity extends BaseActivity implements
		OnClickListener {

	private EditText edName, edCardNum;
	private TextView tvJieGuo, tvName, tvSex, tvJieGuo1, tvJieGuoName,
			tvJieGuoSex;
	private String name, cardNum, jieGuoName, jieGuoSex, photo, resultStr;
	private ImageView img;
	private ImageView imageShow;
	private LinearLayout ll;
	private CommonTitleBar title;
	/** 记录是拖拉照片模式还是放大缩小照片模式 */
	private int mode = 0;// 初始状态
	/** 拖拉照片模式 */
	private static final int MODE_DRAG = 1;
	/** 放大缩小照片模式 */
	private static final int MODE_ZOOM = 2;

	/** 用于记录开始时候的坐标位置 */
	private PointF startPoint = new PointF();
	/** 用于记录拖拉图片移动的坐标位置 */
	private Matrix matrix = new Matrix();
	/** 用于记录图片要进行拖拉时候的坐标位置 */
	private Matrix currentMatrix = new Matrix();

	/** 两个手指的开始距离 */
	private float startDis;
	/** 两个手指的中间点 */
	private PointF midPoint;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.shimingchaxun_layout);
		init();
	}

	private void init() {
		edName = (EditText) findViewById(R.id.ed_shimingchaxun_Name);
		edCardNum = (EditText) findViewById(R.id.ed_shimingchaxun_cardNum);
		tvJieGuo = (TextView) findViewById(R.id.tv_shimingchaxun_jieguo);
		tvName = (TextView) findViewById(R.id.tv_shimingchaxun_jieguoName);
		tvSex = (TextView) findViewById(R.id.tv_shimingchaxun_jieguoSex);
		tvJieGuo1 = (TextView) findViewById(R.id.tv_shimingchaxun_1);
		tvJieGuoName = (TextView) findViewById(R.id.tv_shimingchaxun_2);
		tvJieGuoSex = (TextView) findViewById(R.id.tv_shimingchaxun_3);
		img = (ImageView) findViewById(R.id.img_shimingrenzheng_photo);
		img.setOnTouchListener(touchListener);
		imageShow = (ImageView) findViewById(R.id.img_userPhoto);
		ll = (LinearLayout) findViewById(R.id.rl_shiming);
		findViewById(R.id.btn_shimingchaxun_query).setOnClickListener(this);
		title = (CommonTitleBar) findViewById(R.id.titlebar_shimingchaxun);
		title.setActName("实名查询");
		title.setCanClickDestory(this, true);
		// img.setOnTouchListener(touchListener);
		// Log.i("result", "--------User.sign -s--------"+User.sign);
	}

	private void getData() {
		name = edName.getText().toString();
		cardNum = edCardNum.getText().toString();

		if (cardNum.length() != 18) {
			T.ss("请输入有效的身份证号");
			return;
		}

		if (!name.equals("") && !cardNum.equals("")) {

			queryShenFen();
		} else if (name.equals("")) {
			T.ss("请输入姓名");
			return;
		} else if (cardNum.equals("")) {
			T.ss("请输入身份证号");
			return;
		}
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub

		switch (arg0.getId()) {
		case R.id.btn_shimingchaxun_query:// 查询
			getData();
			break;
		// case R.id.img_shimingrenzheng_photo://图片
		// LayoutParams params = (LayoutParams) imageShow.getLayoutParams();
		// params.height=LayoutParams.MATCH_PARENT;
		// params.width =LayoutParams.MATCH_PARENT;
		// // imageShow.setImageResource(R.drawable.newscar);
		// imageShow.setBackgroundDrawable(new BitmapDrawable(
		// stringtoBitmap(photo)));
		// imageShow.setLayoutParams(params);
		// break;
		// case R.id.img_userPhoto://查询
		// LayoutParams params2 = (LayoutParams) imageShow.getLayoutParams();
		// params2.height=LayoutParams.MATCH_PARENT;
		// params2.width =LayoutParams.MATCH_PARENT;
		// imageShow.setBackground(null);
		// // imageShow.setBackgroundDrawable(new BitmapDrawable(
		// // stringtoBitmap(photo)));
		// imageShow.setLayoutParams(params2);
		// break;

		default:
			break;
		}

	}

	private void queryShenFen() {

		RequestParams params = new RequestParams();
		String url = MyUrls.REALMANGE;
		showLoadingDialog();
		Map<String, String> map = new HashMap<String, String>();
		map.put("username", MApplication.mSharedPref
				.getSharePrefString(SharedPrefConstant.USERNAME));
		map.put("name", name);
		map.put("card", cardNum);
		String json = JSON.toJSONString(map);
		Log.i("result", "----ddd----s-------" + json);
		try {
			StringEntity bodyEntity = new StringEntity(json, "UTF-8");
			params.setBodyEntity(bodyEntity);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		HttpUtils utils = new HttpUtils();
		utils.send(HttpMethod.POST, url, params, new RequestCallBack<String>() {

			@Override
			public void onFailure(HttpException arg0, String arg1) {
				dismissLoadingDialog();
				T.ss("操作超时");
			}

			@Override
			public void onSuccess(ResponseInfo<String> response) {
				// TODO Auto-generated method stub
				String code = "";
				String message = "";

				String str = response.result;
				Log.i("result", "----查询成功----s-------" + str);
				try {
					JSONObject obj = new JSONObject(str);
					code = obj.optString("CODE");
					message = obj.optString("MESSAGE");
					if (code.equals("00")) {

						jieGuoName = obj.optString("name");
						jieGuoSex = obj.optString("sexCode");
						resultStr = obj.optString("citizenIdResult");
						photo = obj.optString("photos");
						ll.setBackground(getResources().getDrawable(
								R.drawable.bg_chaxunjieguo));
						tvJieGuo1.setText("查询结果");
						tvJieGuoName.setText("姓名");
						tvJieGuoSex.setText("性别");
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				if (code.equals("00")) {
					T.ss("查询成功");
					tvJieGuo.setText(resultStr);
					tvName.setText(jieGuoName);
					tvSex.setText(jieGuoSex);
					img.setBackgroundDrawable(new BitmapDrawable(
							stringtoBitmap(photo)));
				} else {
					T.ss(message);
				}
				dismissLoadingDialog();
			}
		});
	}

	OnTouchListener touchListener = new OnTouchListener() {

		@Override
		public boolean onTouch(View arg0, MotionEvent event) {
			// TODO Auto-generated method stub
			switch (event.getAction() & MotionEvent.ACTION_MASK) {
			// 手指压下屏幕
			case MotionEvent.ACTION_DOWN:
				Log.i("result", "------------1--------");
				mode = MODE_DRAG;
				// 记录ImageView当前的移动位置
				currentMatrix.set(img.getImageMatrix());
				startPoint.set(event.getX(), event.getY());
				break;
			// 手指在屏幕上移动，改事件会被不断触发
			case MotionEvent.ACTION_MOVE:
				Log.i("result", "------------2--------");
				// 拖拉图片
				if (mode == MODE_DRAG) {
					float dx = event.getX() - startPoint.x; // 得到x轴的移动距离
					float dy = event.getY() - startPoint.y; // 得到x轴的移动距离
					// 在没有移动之前的位置上进行移动
					matrix.set(currentMatrix);
					matrix.postTranslate(dx, dy);
				}
				// 放大缩小图片
				else if (mode == MODE_ZOOM) {
					float endDis = distance(event);// 结束距离
					if (endDis > 10f) { // 两个手指并拢在一起的时候像素大于10
						float scale = endDis / startDis;// 得到缩放倍数
						matrix.set(currentMatrix);
						matrix.postScale(scale, scale, midPoint.x, midPoint.y);
					}
				}
				break;
			// 手指离开屏幕
			case MotionEvent.ACTION_UP:
				Log.i("result", "------------3--------");
				// 当触点离开屏幕，但是屏幕上还有触点(手指)
			case MotionEvent.ACTION_POINTER_UP:
				Log.i("result", "------------4--------");
				mode = 0;
				break;
			// 当屏幕上已经有触点(手指)，再有一个触点压下屏幕
			case MotionEvent.ACTION_POINTER_DOWN:
				Log.i("result", "------------5--------");
				mode = MODE_ZOOM;
				/** 计算两个手指间的距离 */
				startDis = distance(event);
				/** 计算两个手指间的中间点 */
				if (startDis > 10f) { // 两个手指并拢在一起的时候像素大于10
					midPoint = mid(event);
					// 记录当前ImageView的缩放倍数
					currentMatrix.set(img.getImageMatrix());
				}
				break;
			}
			img.setImageMatrix(matrix);
			return true;
		}
	};

	/** 计算两个手指间的距离 */
	private float distance(MotionEvent event) {
		float dx = event.getX(1) - event.getX(0);
		float dy = event.getY(1) - event.getY(0);
		/** 使用勾股定理返回两点之间的距离 */
		return FloatMath.sqrt(dx * dx + dy * dy);
	}

	/** 计算两个手指间的中间点 */
	private PointF mid(MotionEvent event) {
		float midX = (event.getX(1) + event.getX(0)) / 2;
		float midY = (event.getY(1) + event.getY(0)) / 2;
		return new PointF(midX, midY);
	}

	private float spacing(MotionEvent event) {
		float x = event.getX(0) - event.getX(1);
		float y = event.getY(0) - event.getY(1);
		return FloatMath.sqrt(x * x + y * y);
	}

	private void midPoint(PointF point, MotionEvent event) {
		float x = event.getX(0) + event.getX(1);
		float y = event.getY(0) + event.getY(1);
		point.set(x / 2, y / 2);
	}

	private Bitmap stringtoBitmap(String string) {
		// 将字符串转换成Bitmap类型
		Bitmap bitmap = null;
		try {
			byte[] bitmapArray;
			bitmapArray = Base64.decode(string, Base64.DEFAULT);
			bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0,
					bitmapArray.length);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bitmap;
	}

}
