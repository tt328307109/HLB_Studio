package com.mining.app.zxing;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Vector;
import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import com.alibaba.fastjson.JSON;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lk.bhb.pay.R;
import com.lk.qf.pay.activity.BaseActivity;
import com.lk.qf.pay.golbal.Actions;
import com.lk.qf.pay.golbal.MApplication;
import com.lk.qf.pay.golbal.MyUrls;
import com.lk.qf.pay.sharedpref.SharedPrefConstant;
import com.lk.qf.pay.tool.T;
import com.lk.qf.pay.utils.AmountUtils;
import com.lk.qf.pay.utils.CreatePayCodeUtils;
import com.lk.qf.pay.utils.JsonUtil;
import com.lk.qf.pay.utils.MyMdFivePassword;
import com.lk.qf.pay.wedget.view.PassWdDialog;
import com.lk.qf.pay.wedget.view.PayListener;
import com.mining.app.zxing.camera.CameraManager;
import com.mining.app.zxing.decoding.CaptureActivityHandler;
import com.mining.app.zxing.decoding.InactivityTimer;
import com.mining.app.zxing.view.ViewfinderView;

/**
 * Initial the camera
 * 
 * @author Ryan.Tang
 */
public class MipcaActivityCapture extends BaseActivity implements Callback , PayListener{

	private CaptureActivityHandler handler;
	private ViewfinderView viewfinderView;
	private boolean hasSurface;
	private Vector<BarcodeFormat> decodeFormats;
	private String characterSet;
	private InactivityTimer inactivityTimer;
	private MediaPlayer mediaPlayer;
	private boolean playBeep;
	private static final float BEEP_VOLUME = 0.10f;
	private boolean vibrate;

	private String phone = "";
	private String account = "";
	private String beizhu = "";
	private String type = "";
	private String pwd = "",url;
	private String action = "", money, phoneNum;// 输入的金额 登录账号
	private SurfaceHolder surfaceHolder;
	private boolean isTrueCode;
	private PassWdDialog mPassWdDialog;
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_capture);
		// ViewUtil.addTopView(getApplicationContext(), this,
		// R.string.scan_card);
		CameraManager.init(getApplication());
		viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);

		Button mButtonBack = (Button) findViewById(R.id.button_back);
		mButtonBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				MipcaActivityCapture.this.finish();

			}
		});
		hasSurface = false;
		inactivityTimer = new InactivityTimer(this);
		Intent intent = getIntent();
		if (intent != null) {
			action = intent.getAction();
			money = intent.getStringExtra("money");
//			if (money!=null||!money.equals("")) {
//				money =AmountUtils.changeY2F(money);
//			}
//			Log.i("result", "------------money-s---------"+money);
//			Log.i("result", "------------action-s---------"+action);
		}
		phoneNum = MApplication.mSharedPref
				.getSharePrefString(SharedPrefConstant.USERNAME);
	}

	@Override
	protected void onResume() {
		super.onResume();
		SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
		SurfaceHolder surfaceHolder = surfaceView.getHolder();
		if (hasSurface) {
			initCamera(surfaceHolder);
		} else {
			surfaceHolder.addCallback(this);
			surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		}
		decodeFormats = null;
		characterSet = null;

		playBeep = true;
		AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
		if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
			playBeep = false;
		}
		initBeepSound();
		vibrate = true;

	}

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
		Log.i("result", "-----------onRestart--------");
		try {
			CameraManager.get().openDriver(surfaceHolder);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (handler != null) {
			handler.quitSynchronously();
			handler = null;
		}
		CameraManager.get().closeDriver();
	}

	@Override
	protected void onDestroy() {
		inactivityTimer.shutdown();
		super.onDestroy();
	}

	/**
	 * 
	 * @param result
	 * @param barcode
	 */
	public void handleDecode(Result result, Bitmap barcode) {
		isTrueCode = false;
		inactivityTimer.onActivity();
		playBeepSoundAndVibrate();
		String resultString = result.getText();
		Log.i("result", "--------action----" + action);
		if (resultString.equals("")) {
			Log.i("result", "--------扫描失败----" );
			T.ss("操作超时");
		} else {
			if (action != null) {
				if (action.equals(Actions.ACTION_ZHUANZHANG)) {// 转账
					Log.i("result", "--------转账----" + "转账");
					transferDealData(resultString);
				} else if (action.equals(Actions.ACTION_WEIXIN)) {// 微信
					url = MyUrls.WEIXIN_MICROPAYPAGE;
					transferMoneyYFB(resultString);
				} else if (action.equals(Actions.ACTION_ZHIFUBAO)) {// 支付宝
					url = MyUrls.ZHIFUBAO_MICROPAYPAGE;
					transferMoneyYFB(resultString);
				} else if (action.equals(Actions.ACTION_YIFUBAO)) {// 易付宝
					url = MyUrls.SCANCODE;
					transferMoneyYFB(resultString);
				} else if (action.equals(Actions.ACTION_BAIDU)) {// 百度钱包
					url = MyUrls.BAIDU_SCANCODEPAY;
					transferMoneyYFB(resultString);
				}
			}
		}
	}

	/**
	 * 内部钱包转账 处理数据
	 */
	private void transferDealData(String resultString) {
		Log.i("result", "--------resultString----" + resultString);
		String json = "";
		try {
			json = JsonUtil.toURLDecoded(resultString);
			Log.i("result", "--------json--s--" + json);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		JSONObject obj;
		try {
			obj = new JSONObject(json);
			phone = obj.optString("phone");
			account = obj.optString("total");
			beizhu = obj.optString("beizhu");
			type = obj.optString("type");
			isTrueCode = true;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (isTrueCode) {
			double money = Double.parseDouble(account);
			if (phone.equals(MApplication.mSharedPref
					.getSharePrefString(SharedPrefConstant.USERNAME))) {
				T.ss("双方账号相同,请核对后重试!");
				MipcaActivityCapture.this.finish();
			}
			if (money > 0) {
				mPassWdDialog = PassWdDialog.getInstance(MipcaActivityCapture.this,
						account,PassWdDialog.YUAN_MARK);
				mPassWdDialog.setPayListener(MipcaActivityCapture.this);
				mPassWdDialog.show();
				
			} else {
				T.ss("对方未设置转账金额,转账失败");
				MipcaActivityCapture.this.finish();
			}
		} else {
			T.ss("非快易付二维码");
			MipcaActivityCapture.this.finish();

		}
	}

	/**
	 * 易付宝转账
	 */
	private void transferMoneyYFB(String paymentCode) {
		showLoadingDialog();
		if (money != null && !money.equals("")) {
			double account = Double.parseDouble(money);
			if (account<=0) {
				T.ss("转账金额为0");
				dismissLoadingDialog();
				return;
			}
		}else{
			T.ss("金额为无效");
			dismissLoadingDialog();
			return;
		}
		money =AmountUtils.changeY2F(money);
		String[] str = {"phoneNum=" + phoneNum, "OrderAmount=" + money, "PaymentCode=" + paymentCode};
		String appSign = CreatePayCodeUtils.createSign(str);// 签名
		Log.i("result", "----------签名------------"+appSign);
		RequestParams params = new RequestParams();
		HashMap<String, String> map = new HashMap<String, String>();

		map.put("phoneNum", phoneNum);
		map.put("appSign", appSign);
		map.put("OrderAmount", money);// 金额String 
		map.put("PaymentCode", paymentCode);

		String json = JSON.toJSONString(map);
		Log.i("result", "----ddd------s-----" + json);
		try {
			StringEntity bodyEntity = new StringEntity(json, "UTF-8");
			params.setBodyEntity(bodyEntity);
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		HttpUtils httpUtils = new HttpUtils();

		httpUtils.send(HttpMethod.POST, url, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						// TODO Auto-generated method stub
						T.ss("操作超时");
						Log.i("result", "----ddd------s--失败---");
						dismissLoadingDialog();
						finish();
					}

					@Override
					public void onSuccess(ResponseInfo<String> response) {
						// TODO Auto-generated method stub
						dismissLoadingDialog();
						String str = response.result;
						String code = "";
						String message = "";
						Log.i("result", "----ddd-----a------" + str);

						JSONObject obj;
						try {
							obj = new JSONObject(str);
							code = obj.optString("Code");
							message = obj.optString("Message");
							T.ss(message);
							finish();
							// if (code.equals("0000")) {
							// T.ss("转账成功");
							// } else {
							// T.ss(message);
							// }
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}
				});
	}

	/**
	 * 转账
	 */
	private void transferMoney() {
		showLoadingDialog();
		RequestParams params = new RequestParams();
		HashMap<String, String> map = new HashMap<String, String>();

		map.put("username", phoneNum);
		map.put("pwd", MyMdFivePassword.MD5(MyMdFivePassword.MD5(pwd)));
		map.put("endusername", phone);
		map.put("total", account);// 金额
		map.put("remark", beizhu);
		map.put("token", MApplication.mSharedPref
				.getSharePrefString(SharedPrefConstant.TOKEN));

		String json = JSON.toJSONString(map);
		Log.i("result", "----ddd------s-----" + json);
		try {
			StringEntity bodyEntity = new StringEntity(json, "UTF-8");
			params.setBodyEntity(bodyEntity);
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		HttpUtils httpUtils = new HttpUtils();
		String url = MyUrls.TRANSFER_OP;

		httpUtils.send(HttpMethod.POST, url, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						// TODO Auto-generated method stub
						// T.ss("转账失败!" + arg0.getExceptionCode());
						dismissLoadingDialog();
					}

					@Override
					public void onSuccess(ResponseInfo<String> response) {
						// TODO Auto-generated method stub
						dismissLoadingDialog();
						String str = response.result;
						String code = "";
						String message = "";
						String name = "";
						Log.i("result", "----ddd-----a------" + str);

						JSONObject obj;
						try {
							obj = new JSONObject(str);
							code = obj.optString("CODE");
							message = obj.optString("MESSAGE");
							if (code.equals("00")) {
								T.ss("转账成功");
							} else {
								T.ss(message);
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

						MipcaActivityCapture.this.finish();
					}
				});
	}

	private void continuePreview() {
		SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
		SurfaceHolder surfaceHolder = surfaceView.getHolder();
		initCamera(surfaceHolder);
		if (handler != null) {
			handler.restartPreviewAndDecode();
		}
	}

	private void initCamera(SurfaceHolder surfaceHolder) {
		try {
			CameraManager.get().openDriver(surfaceHolder);
		} catch (IOException ioe) {
			return;
		} catch (RuntimeException e) {
			return;
		}
		if (handler == null) {
			handler = new CaptureActivityHandler(this, decodeFormats,
					characterSet);
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		if (!hasSurface) {
			hasSurface = true;
			initCamera(holder);
		}

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		hasSurface = false;

	}

	public ViewfinderView getViewfinderView() {
		return viewfinderView;
	}

	public Handler getHandler() {
		return handler;
	}

	public void drawViewfinder() {
		viewfinderView.drawViewfinder();

	}

	private void initBeepSound() {
		if (playBeep && mediaPlayer == null) {
			// The volume on STREAM_SYSTEM is not adjustable, and users found it
			// too loud,
			// so we now play on the music stream.
			setVolumeControlStream(AudioManager.STREAM_MUSIC);
			mediaPlayer = new MediaPlayer();
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mediaPlayer.setOnCompletionListener(beepListener);

			AssetFileDescriptor file = getResources().openRawResourceFd(
					R.raw.beep);
			try {
				mediaPlayer.setDataSource(file.getFileDescriptor(),
						file.getStartOffset(), file.getLength());
				file.close();
				mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
				mediaPlayer.prepare();
			} catch (IOException e) {
				mediaPlayer = null;
			}
		}
	}

	private static final long VIBRATE_DURATION = 200L;

	private void playBeepSoundAndVibrate() {
		if (playBeep && mediaPlayer != null) {
			mediaPlayer.start();
		}
		if (vibrate) {
			Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
			vibrator.vibrate(VIBRATE_DURATION);
		}
	}

	/**
	 * When the beep has finished playing, rewind to queue up another one.
	 */
	private final OnCompletionListener beepListener = new OnCompletionListener() {
		public void onCompletion(MediaPlayer mediaPlayer) {
			mediaPlayer.seekTo(0);
		}
	};

	@Override
	public void sure(String password) {
		// TODO Auto-generated method stub
		mPassWdDialog.dismiss();
		mPassWdDialog = null;
		pwd = password;
		transferMoney();
	}

	@Override
	public void cacel() {
		// TODO Auto-generated method stub
		mPassWdDialog.dismiss();
		mPassWdDialog = null;
		continuePreview();
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub
		continuePreview();
		mPassWdDialog.dismiss();
		mPassWdDialog = null;
	}

}