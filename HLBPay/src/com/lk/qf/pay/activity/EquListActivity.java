package com.lk.qf.pay.activity;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.apache.http.Header;
import org.apache.http.entity.StringEntity;

import com.alibaba.fastjson.JSON;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lk.bhb.pay.R;
import com.lk.pay.communication.AsyncHttpResponseHandler;
import com.lk.qf.pay.aanewactivity.RedemptionDepositActivity;
import com.lk.qf.pay.activity.swing.SwingHXCardActivity;
import com.lk.qf.pay.beans.BasicResponse;
import com.lk.qf.pay.beans.BindDeviceInfo;
import com.lk.qf.pay.beans.PosData;
import com.lk.qf.pay.golbal.Actions;
import com.lk.qf.pay.golbal.MApplication;
import com.lk.qf.pay.golbal.MyUrls;
import com.lk.qf.pay.golbal.Urls;
import com.lk.qf.pay.golbal.User;
import com.lk.qf.pay.jhl.CheckActivity;
import com.lk.qf.pay.jhl.PayByBuleCardActivity;
import com.lk.qf.pay.sharedpref.SharedPrefConstant;
import com.lk.qf.pay.tool.Logger;
import com.lk.qf.pay.tool.MyHttpClient;
import com.lk.qf.pay.tool.T;
import com.lk.qf.pay.v50.PayByV50CardActivity;
import com.lk.qf.pay.wedget.CommonTitleBar;
import com.lk.qf.pay.wedget.customdialog.ActionSheetDialog;
import com.lk.qf.pay.wedget.customdialog.ActionSheetDialog.OnSheetItemClickListener;
import com.lk.qf.pay.wedget.customdialog.ActionSheetDialog.SheetItemColor;
import com.lk.qf.pay.zxb.ZXBDeviceListActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class EquListActivity extends BaseActivity implements OnClickListener {

	private ArrayList<BindDeviceInfo> deviceList;
	private ListView listview;
	private Context mContext;
	AudioManager localAudioManager;
	private final String[] type = { "音频刷卡器", "蓝牙刷卡器", "键盘蓝牙刷卡器", };
	private int typeNum = 0;//
	// private final String[] type = {"新大陆A音频刷卡器", "锦鸿霖蓝牙刷卡器","V50蓝牙刷卡器",
	// "支付通蓝牙刷卡器" };
	private CommonTitleBar title;
	private String usesort = "";
	private String isbandpos = "";
	private Button btn;

	// private String bangditype = "0";

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.equ_list);
		mContext = this;
		init();
	}

	private void init() {
		// findViewById(R.id.btn_back).setOnClickListener(this);
		findViewById(R.id.equ_list_add_btn).setOnClickListener(this);
		btn = (Button) findViewById(R.id.btn_equ_list_yajin);
		btn.setOnClickListener(this);
		listview = (ListView) findViewById(R.id.equ_list_lv);
		deviceList = new ArrayList<BindDeviceInfo>();
		// getEqueList();
		postQueryjijuHttp();
		localAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		title = (CommonTitleBar) findViewById(R.id.titlebar_equlist);
		title.setActName("设备列表");
		title.setCanClickDestory(this, true);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.equ_list_add_btn:
			typeNum = 0;
			if (!isbandpos.equals("0")) {
				T.ss("已绑定刷卡器");
				return;
			}
			bindDevice();
			// if (MApplication.mSharedPref.getSharePrefString(
			// SharedPrefConstant.STATE).equals("en")) {
			// bindDevice();
			// } else {
			// T.ss("该商户尚未通过审核");
			// }
			break;
		case R.id.btn_equ_list_yajin:

			if (isbandpos.equals("0")) {
				T.ss("暂未绑定刷卡器");
				return;
			}
			// if (!usesort.equals("0")) {
			// Intent intent = new Intent(EquListActivity.this,
			// RedemptionDepositActivity.class);
			// startActivity(intent);
			// return;
			// }
			typeNum = 1;
			bindDevice();

			break;

		default:
			break;
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Log.i("result", "-------resume-----");
		isbandpos = MApplication.mSharedPref
				.getSharePrefString(SharedPrefConstant.POSCOUNT);
		usesort = MApplication.mSharedPref
				.getSharePrefString(SharedPrefConstant.USESORT);
		if (usesort.equals("0")) {
			if (!isbandpos.equals("0")) {
				goPayYaJin();
			}
		}
	}

	private void bindDevice() {
		new ActionSheetDialog(this)
				.builder()
				.setTitle("请选择刷卡器类型")
				.setCancelable(false)
				.setCanceledOnTouchOutside(true)
				.addSheetItem("音频刷卡器", SheetItemColor.Blue,
						new OnSheetItemClickListener() {
							@Override
							public void onClick(int which) {
								Intent intent1 = new Intent(mContext,
										SwingHXCardActivity.class);
								if (localAudioManager.isWiredHeadsetOn()) {
									if (typeNum == 0) {
										PosData.getPosData().setPayAmt("");
										intent1.setAction(Actions.ACTION_CHECK);
										startActivity(intent1);
									} else {
										PosData.getPosData().setPayAmt("30000");
										intent1.setAction(Actions.ACTION_CASHIN);
										PosData.getPosData().setPayType("00");
										startActivity(intent1);
									}
								} else {
									showDialog("请插入刷卡器!");
								}
								
							}
						})
				.addSheetItem("蓝牙刷卡器", SheetItemColor.Blue,
						new OnSheetItemClickListener() {
							@Override
							public void onClick(int which) {
								Intent it = new Intent(EquListActivity.this,
										ZXBDeviceListActivity.class);
								if (typeNum == 0) {
									PosData.getPosData().setPayAmt("");
									it.setAction(Actions.ACTION_CHECK);

								} else {
									PosData.getPosData().setPayAmt("30000");
									it.setAction(Actions.ACTION_CASHIN);
									PosData.getPosData().setPayType("00");
								}
								startActivity(it);
							}
						})

				.addSheetItem("键盘蓝牙刷卡器", SheetItemColor.Blue,
						new OnSheetItemClickListener() {
							@Override
							public void onClick(int which) {
								Intent it3 = new Intent(EquListActivity.this,
										PayByV50CardActivity.class);

								if (typeNum == 0) {
									PosData.getPosData().setPayAmt("");
									it3.setAction(Actions.ACTION_CHECK);

								} else {
									PosData.getPosData().setPayAmt("30000");
									it3.setAction(Actions.ACTION_CASHIN);
									PosData.getPosData().setPayType("00");
								}
								startActivity(it3);
							}
						}).show();
	}

	class EquListAdapter extends BaseAdapter {
		private ArrayList<BindDeviceInfo> list;
		private Context mContext;

		public EquListAdapter(Context mContext, ArrayList<BindDeviceInfo> list) {
			this.mContext = mContext;
			this.list = list;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return list.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return list.get(arg0);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			TextView tv = null;
			if (convertView == null) {
				convertView = LayoutInflater.from(mContext).inflate(
						R.layout.equ_list_item, null);
				tv = (TextView) convertView
						.findViewById(R.id.equ_list_item_ksn);
				convertView.setTag(tv);
			} else {
				tv = (TextView) convertView.getTag();
			}
			tv.setText(list.get(position).getTermNo());

			return convertView;
		}

	}

	/**
	 * 查询机具
	 * 
	 * @return
	 */
	private void postQueryjijuHttp() {
		showLoadingDialog();
		RequestParams params = new RequestParams();
		String url = MyUrls.HANGPOSLIST;

		Map<String, String> map = new HashMap<String, String>();

		map.put("username", MApplication.mSharedPref
				.getSharePrefString(SharedPrefConstant.USERNAME));

		String json = JSON.toJSONString(map);
		// Log.i("result", "-------请求-----" + json);
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

				T.ss("操作超时");
				dismissLoadingDialog();
			}

			@Override
			public void onSuccess(ResponseInfo<String> response) {
				// TODO Auto-generated method stub
				String str = response.result;
				JSONObject obj;
				String code = "";
				String message = "";
				int count;
				Log.i("result", "--------------s---------" + str);
				try {
					obj = new JSONObject(str);
					code = obj.optString("CODE");
					message = obj.optString("MESSAGE");
					count = obj.optInt("count");
					JSONArray array = obj.optJSONArray("data");
					if (code.equals("00")) {
						if (count > 0) {
							for (int i = 0; i < count; i++) {
								BindDeviceInfo d = new BindDeviceInfo();
								d.setAgentId(array.getJSONObject(i).optString(
										"ID"));// 机具id
								d.setTermNo(array.getJSONObject(i).optString(
										"POS"));// sn号

								deviceList.add(d);
							}
						} else {
							T.ss(getResources().getString(R.string.no_handPos));
						}
					} else {
						T.ss(message);
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// for (int j = 0; j < deviceList.size(); j++) {
				// bangditype = deviceList.get(j).getTerminalType();
				// }
				listview.setAdapter(new EquListAdapter(mContext, deviceList));
				dismissLoadingDialog();
			}
		});
	}

	/**
	 * 是否缴纳押金
	 */
	private void goPayYaJin() {
		AlertDialog.Builder builder = new AlertDialog.Builder(
				EquListActivity.this);
		builder.setMessage("您现在缴纳押金?");
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
				typeNum = 1;
				bindDevice();
			}
		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
		AlertDialog dialog = builder.create();
		dialog.show();
	}

}
