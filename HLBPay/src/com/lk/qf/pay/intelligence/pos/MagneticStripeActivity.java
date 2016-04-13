package com.lk.qf.pay.intelligence.pos;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.lk.bhb.pay.R;
import com.lk.qf.pay.activity.BaseActivity;
import com.lk.qf.pay.beans.PosData;
import com.lk.qf.pay.tool.T;
import com.lk.qf.pay.wedget.CommonTitleBar;
import com.spos.sdk.imp.SposManager;
import com.spos.sdk.interfac.Magnetic;

public class MagneticStripeActivity extends BaseActivity implements OnClickListener{

	private TextView tvShowMsg,tvShowAccount;
	private ImageView img;
	private CommonTitleBar title;
	private Button btnSettle;
	static Magnetic mMagnetic;
	private String bankCardTrack2,bankCardTrack3,track55;//二磁道 三磁道 55域
	private String bankCardNum;//银行卡号
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.magnetic_layout);
		init();
	}
	
	private void init(){
		 mMagnetic = SposManager.getInstance().openMagnetic();
		 tvShowMsg = (TextView) findViewById(R.id.cashin_show_msg_intelligence_pos_text);
		 tvShowAccount = (TextView) findViewById(R.id.cashin_account__intelligence_postext);
		 btnSettle = (Button) findViewById(R.id.btn_settle);
		 btnSettle.setOnClickListener(this);
		 img = (ImageView) findViewById(R.id.cashin_intelligence_pos_img);
	}
	
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		btnSettle.setEnabled(false);
		Log.i("result", "-------------00---------------");

		byte[] readCard;
        String magneticReadText = "";
		
        readCard = mMagnetic.readCard();
        Log.i("result", "-------------1---------------");
        if (null == readCard) {
        	T.ss("获取银行卡信息失败");
        } else {
        	Log.i("result", "------------3---------------");
//        	String t1 = mMagnetic.getTrackData(readCard, 1);
//        	if (null != t1) {
//        		bankCardTrack1 = t1;
//        	}
        	String t2 = mMagnetic.getTrackData(readCard, 2);
        	if (null != t2) {
        		Magnetic.BankcardTrack2 bankcardTrack2 = mMagnetic.decodeBankcardTrack2(t2);
        		bankCardTrack2 = t2;
        		bankCardNum = bankcardTrack2.getPan();//银行卡号
        	}
        	String t3 = mMagnetic.getTrackData(readCard, 3);
        	if (null != t3) {
        		bankCardTrack3 = t3;
        	}
        	
        	if (null != t2) {
        		Log.i("result", "-------------4---------------");
        		Magnetic.BankcardTrack2 bankcardTrack2 = mMagnetic.decodeBankcardTrack2(t2);
        		magneticReadText += "－－－－银行卡磁道2解析－－－－\n"
        							+"主账号		- "+bankcardTrack2.getPan()+"\n"
        							+"失效日期	- "+bankcardTrack2.getEd()+"\n"
        							+"服务代码	- "+bankcardTrack2.getSc()+"\n"
        							+"附加数据	- "+bankcardTrack2.getAd()+"\n";
        	}
        	
        	PosData.getPosData().setTermType("01");// 蓝牙
    		PosData.getPosData().setRate("1");
    		PosData.getPosData().setTermNo("");//sn号
    		PosData.getPosData().setPayAmt("");
    		PosData.getPosData().setTrack("");//磁道信息
    		PosData.getPosData().setRandom("");//随机数
    		PosData.getPosData().setPeriod("");//有效期
    		PosData.getPosData().setCrdnum("");//序列号
    		PosData.getPosData().setIcdata("");//55
    		PosData.getPosData().setPinblok("");
        }
        
        btnSettle.setEnabled(true);    
	}

}
