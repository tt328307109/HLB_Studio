package com.lk.qf.pay.nfc;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import org.json.JSONObject;
import com.lk.qf.pay.activity.BaseActivity;
import com.lk.qf.pay.beans.PosData;
import com.wepayplugin.nfcstd.WepayPlugin;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

public class NfcPayActivity extends BaseActivity {

	private Context mContext;
	private String md5MerchantCode22, md5Sign_KEY22;// 商户号,商户key
	private String amount = "";// 充值金额

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		amount = PosData.getPosData().getPayAmt();
		init();
	}

	private void init() {
		mContext = this;

		JSONObject jsonm = new JSONObject();
		try {
			 jsonm.put(WepayPlugin.merchantCode, "1000000848");//商户号
	            jsonm.put(WepayPlugin.outUserId, "1234567890");
	            jsonm.put(WepayPlugin.nonceStr, getRandomNum(32));
	            jsonm.put(WepayPlugin.outOrderId, getRandomNum(12));
	            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
	            jsonm.put(WepayPlugin.orderCreateTime, sdf.format(new Date()));
	            jsonm.put(WepayPlugin.totalAmount, Long.parseLong(amount));
	            jsonm.put(WepayPlugin.lastPayTime, "");//yyyyMMddHHmmss，该字段必须存在，无值传入空串
	            /********MD5签名*********/
	            String signmd5Src = MD5Encrypt.signJsonStringSort(jsonm.toString());
	            String signmd5 = MD5Encrypt.sign(signmd5Src, "b8e34a2f-f32e-4c03-9d1c-6d1ed8998c3b");//key
	            jsonm.put(WepayPlugin.sign, signmd5);
	            jsonm.put(WepayPlugin.payNotifyUrl, "http://192.168.6.34:10000/merchant/telcharge_notice.jsp");
	            jsonm.put(WepayPlugin.goodsName, "测试商品");
	            jsonm.put(WepayPlugin.goodsExplain, "测试商品描述");
		} catch (Exception e) {
			e.printStackTrace();
		}
		Log.i("result", "--------jsonm---------"+jsonm);
		WepayPlugin.getInstance().genWepayPayRequestJar(NfcPayActivity.this,
				jsonm.toString(), true);
	}

	/**
	 * 获取随机字符串
	 * 
	 * @param len
	 *            长度
	 * @return 随机字符串
	 */
	public static String getRandomNum(int len) {
		String[] arr = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9" };
		String s = "";
		if (len <= 0) {
			return s;
		}
		Random ra = new Random();
		int arrLen = arr.length;
		for (int i = 0; i < len; i++) {
			s += arr[ra.nextInt(arrLen)];
		}
		return s;
	}

	/**
	 * 接受支付控件的返回，处理支付控件返回的结果
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == WepayPlugin.reqCod) {
			if (data != null) {
				Bundle mbundle = data.getExtras();
				String parames = mbundle.getString("result");
				if (parames.equals("success")) {
					Toast.makeText(this, "支付成功", Toast.LENGTH_SHORT).show();
				} else if (parames.equals("cancel")) {
					Toast.makeText(this, "订单已取消", Toast.LENGTH_SHORT).show();
				} else if (parames.equals("fail")) {
					Toast.makeText(this, "支付失败", Toast.LENGTH_SHORT).show();
				} else if (parames.equals("error")) {
					Toast.makeText(this, "数据异常", Toast.LENGTH_SHORT).show();
				}
			} else {
				Toast.makeText(this, "出错啦", Toast.LENGTH_SHORT).show();
			}
		}
	}

}
