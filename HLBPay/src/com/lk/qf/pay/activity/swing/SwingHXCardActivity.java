package com.lk.qf.pay.activity.swing;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.lk.bhb.pay.R;
import com.lk.qf.pay.activity.BaseActivity;
import com.lk.qf.pay.activity.EquAddConfirmActivity;
import com.lk.qf.pay.beans.PosData;
import com.lk.qf.pay.golbal.Actions;
import com.lk.qf.pay.utils.AmountUtils;
import com.lk.qf.pay.wedget.CommonTitleBar;
import com.msafepos.sdk.HXPos;
import com.msafepos.sdk.PBOCUtil;
import com.msafepos.sdk.Util;
import com.msafepos.sdk.listener.OnStartRecordFail;
import com.msafepos.sdk.listener.OnWarnAndHint;
import com.msafepos.sdk.listener.PosEvent;

public class SwingHXCardActivity extends BaseActivity implements OnClickListener, PosEvent{
	
	private String amount = "",payType;// 充值金额
	private Context mContext;
	private TextView showAccountText;// 充值钱数
	private TextView showMsgText;// 刷卡状态
	private CommonTitleBar commonTitleBar;
	private ProgressBar progressBar;
	private String action="";
	private LinearLayout amountLayout;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.swing_hx_card);
		init();
	}
	private void init() {
		mContext = this;
		Intent intent = getIntent();
		if (intent!=null) {
			action = getIntent().getAction();
		}
		amountLayout = (LinearLayout) findViewById(R.id.cashin_amount_layout);
		commonTitleBar = (CommonTitleBar) findViewById(R.id.titlebar_swing_ldcard);
		showAccountText = (TextView) findViewById(R.id.cashin_account_text);
		commonTitleBar.setCanClickDestory(this, true);
		progressBar = (ProgressBar) findViewById(R.id.cashin_swing_pb);
		
		showMsgText = (TextView) findViewById(R.id.cashin_show_msg_text);	
		payType = PosData.getPosData().getPayType();
		if(action.equals(Actions.ACTION_CASHIN)){
			if (payType!=null) {
				if (payType.equals("03")) {
					commonTitleBar.setActName("即刷即到");
				}else{
					commonTitleBar.setActName("刷卡支付");
				}
			}else{
				commonTitleBar.setActName("刷卡支付");
			}
			amountLayout.setVisibility(View.VISIBLE);
			amount = PosData.getPosData().getPayAmt();
			showAccountText.setText(AmountUtils.changeFen2Yuan(amount) + "元");
		}else if(action.equals(Actions.ACTION_CHECK)){
			commonTitleBar.setActName("设备绑定");
		}else if(action.equals(Actions.ACTION_QUERY_BALANCE)){
			commonTitleBar.setActName("余额查询");
		}else {
			commonTitleBar.setActName("缴纳押金");
			amountLayout.setVisibility(View.VISIBLE);
			amount = PosData.getPosData().getPayAmt();
			showAccountText.setText(AmountUtils.changeFen2Yuan(amount) + "元");
		}
		HXPos.init(mContext, HXPos.COMM_VOC);
		HXPos.debug=false;
		HXPos.fskSendEvt=evtOnSend;
		HXPos.evt_startRecordFail=evtStartRecFail;
		/*if(action.equals(Actions.ACTION_CHECK)){
			showMsgText.setText("读取设备号");
			HXPos.getObj().SendReadNo();
		} else {
			showMsgText.setText("音频连接上");
			startSwing();
		}*/
	}
	
	private void startSwing(){
		int res = HXPos.getObj().SendSwipeCard2((byte) 35,
				HXPos.makeKeyIndex(0, 0, 0/* 加密密钥索引0-5 */), null, true, true,
				false, AmountUtils.changeY2F(amount).getBytes(), (byte) 5, "22");
		if(res < 0){
			showMsgText.setText("指令参数错误");
		}
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void OnLogInfo(String arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void OnPosConnect(boolean arg0) {
		if (arg0) {
			if (HXPos.getObj().getCommMode() == HXPos.COMM_BLUE) {
				
			} else {
				progressBar.setVisibility(View.VISIBLE);
				if(action.equals(Actions.ACTION_CHECK)){
					showMsgText.setText("读取设备号");
					HXPos.getObj().SendReadNo();
				} else {
					showMsgText.setText("音频连接上");
					startSwing();
				}
			}
		} else {
			progressBar.setVisibility(View.INVISIBLE);
			showMsgText.setText("未插入刷卡器");
			HXPos.getObj().close();
		}
	}
	@Override
	public void onRecvData(int arg0, byte[] arg1) {
		if(arg0 != 0){
			showMsgText.setText("刷卡器返回数据解码失败");
		} else {
			// 解析原始报文，结果放入HXPos.PosData结构
			HXPos.PosData res = HXPos.getObj().Parse(arg1);
			// 解析错误
			if (res == null){
				showMsgText.setText("解析错误");
				return;
			}
			if(res.batLeft!=null){
				//电池电量小于3.7V
				if(Integer.parseInt(Util.BytesToString(res.batLeft))<3700)
				{
					showMsgText.setText("电量不足");
				}
			}
			switch (res.cmdType) {
			case HXPos.CMD_SWIPECARD:
				if (res.result == 1) {
					showMsgText.setText("请刷卡或插卡(请匀速刷卡)");
				} else if (res.result == 100) {
					showMsgText.setText("IC卡插入");
				}  else if (res.result == 23) {
					showMsgText.setText("IC卡拔出");
				} else if (res.result == 35) {
					showMsgText.setText("IC卡读取失败");
					
				} else if (res.result == 22) {
					//磁条卡刷卡失败，无需再次发送刷卡指令，界面提示用户重新刷卡即可
					showMsgText.setText("磁条刷卡失败，请重新刷卡");
				} else if (res.result == 5) {
					showMsgText.setText("刷卡指令超时，用户无操作");
				} else if (res.result == 0) {
					
					if (res.cardType != null){
						//2-磁条,1-ic卡
						//mList.add("卡片类型:(1-ic卡;2-磁条;4-ic卡刷卡)" + Util.B2Hex(res.cardType));
						String mediaType = Util.B2Hex(res.cardType);
						if(mediaType.equals("02") || mediaType.equals("04")){
							PosData.getPosData().setMediaType("01");
						} else {
							PosData.getPosData().setMediaType("02");
						}
					}
					
					String track2 = "";
					String track3 = "";
					StringBuilder sb = new StringBuilder();
					// 刷卡成功，读取结果数据
					track2 = Util.BinToHex(res.cd2, 1, res.cd2.length - 1);
					
					if (res.cd3 != null) {
						track3 = Util.BinToHex(res.cd3, 1, res.cd3.length - 1); 
					}
					//转16进制
					//track2 = Util.BinToHex(track2.getBytes(),0,track2.length());
					//track3 = Util.BinToHex(track3.getBytes(),0,track3.length());
					PosData.getPosData().setTrack(track2 + "|" + track3);
					
					if (res.cardValidDate != null) {
						//mList.add("卡有效期：" + Util.BytesToString(res.cardValidDate));
						PosData.getPosData().setPeriod(Util.BytesToString(res.cardValidDate));
					}else{
						
						PosData.getPosData().setPeriod("");
					}
					if (res.cardMingWen != null) {
						//mList.add("卡号：" + Util.BytesToString(res.cardMingWen));
						PosData.getPosData().setCardNo(Util.BytesToString(res.cardMingWen));
					}
					if (res.field55 != null) {
						//mList.add("55域：" + Util.B2Hex(res.field55));
						PosData.getPosData().setIcdata(Util.B2Hex(res.field55));
					}else{
						PosData.getPosData().setIcdata("");
						
					}
					if (res.userPDNo != null){
						//mList.add("用户设备编号:" + Util.BytesToString(res.userPDNo));
						
					}
					
					/*if (res.batLeft != null)
						mList.add("剩余电量:" + Util.BytesToString(res.batLeft));*/

					if (res.userPDNo != null){
						String ksn = Util.BytesToString(res.userPDNo);
						
						if(ksn.length() > 12){
							ksn = ksn.substring(0, 12);
						}
						PosData.getPosData().setTermNo(ksn);
					}

					/*if (res.pan != null)
						mList.add("PAN:" + Util.B2Hex(res.pan));*/

					if (res.t5f34 != null){
						//mList.add("卡片序列号(23域):" + Util.B2Hex(res.t5f34));
						PosData.getPosData().setCrdnum(Util.B2Hex(res.t5f34));
					}else{
						PosData.getPosData().setCrdnum("00");
						
					}

					
					/*if (res.random != null)
						mList.add("加密随机数:" + Util.B2Hex(res.random));*/
					/********************************************************/
//					PosData.getPosData().setPayType("01");
					PosData.getPosData().setPayAmt(amount);
					PosData.getPosData().setTermType("02");
					PosData.getPosData().setRandom("");
					PosData.getPosData().setType("音频刷卡器");
					Intent intent = new Intent();
					if(action.equals(Actions.ACTION_CASHIN)){
						
						intent.setClass(mContext,SignaturePadActivity.class);
//						intent.setAction("PAY_BY_HX");
					} else {
						intent.setClass(mContext,CardBalanceConfirmActivity.class);
					}
					
					startActivity(intent);
					finish();
					
					/********************************************************/
				} else {
					// 刷卡失败，
					showMsgText.setText("刷卡失败,错误代码:" + res.result);
				}
				break;
			case HXPos.CMD_PBOC_END_DEAL: 
				// pboc ic卡交易结束
				if (res.result == 0) {
					showMsgText.setText("ic卡交易结束");
					if (res.gen_ac2_retData != null) {
						PBOCUtil.GenAC2Res res1 = PBOCUtil
								.ParseGenAC2(res.gen_ac2_retData);
						if (res1.cid == 0x40) {
							// 成功交易,卡片返回TC
							// 获取交易信息,卡号码,交易金额
							showMsgText.setText("交易卡号:" + HXPos.getObj().getCardNo());
						}
					}

				} else{
					showMsgText.setText("ic卡交易结束错误");
				}
				break;
			
			case HXPos.CMD_READ_NO: 
				String ksn = Util.BytesToString(res.userPDNo);
				
				if(ksn.length() > 12){
					ksn = ksn.substring(0, 12);
				}
				System.out.println("ok------------>");
				Intent it = new Intent(mContext, EquAddConfirmActivity.class);
				it.putExtra("ksn", ksn);
				startActivity(it);
				finish();
				
				break;
			case HXPos.CMD_IC_RESET: 
				// ic卡复位返回
				if (res.result == 0) {
					showMsgText.setText("IC卡复位成功");
				} else {
					showMsgText.setText("IC卡复位失败");
				}
				break;
			case HXPos.CMD_IC_CMD: {
				// ic卡指令返回
				if (res.result == 0) {
					showMsgText.setText("IC卡指令成功");
				} else {
					showMsgText.setText("IC卡指令失败");
				}
				break;
			}
			case HXPos.CMD_SELECT_AID: {
				// pboc ic卡有多个AID，等待用户确认（暂时不用）
				byte[] cmd = new byte[3];
				cmd[1] = HXPos.CMD_SELECT_AID;
				HXPos.getObj().SendRawCmd(cmd);
			}
			case HXPos.CMD_READ_GENERAL_DATA: {
				// 批量读取数据返回内容
				break;
			}
			default:
				break;
			}
		}
	}
	
	OnWarnAndHint evtOnSend=new OnWarnAndHint()
	{
		public void onSendFinish() {
			// TODO Auto-generated method stub
			//调试用输出信息，正式发布程序可以忽略
		}

		public void onLog(String arg0) {
			// TODO Auto-generated method stub
			//调试用输出信息，正式发布程序可以忽略
		}

		@Override
		public void onMusicPlay() {
			// TODO Auto-generated method stub
			//建议保留这个提示，部分手机可能在后台正在播放音乐
			//会影响刷卡器使用
			//showMsgText.setText("请关闭音乐播放器");
		}
		
	};
	
	OnStartRecordFail evtStartRecFail=new OnStartRecordFail()
	{
		@Override
		public void onStartRecordFail() {
			// TODO Auto-generated method stub
			//Toast.makeText(getApplicationContext(), "启动录音失败,请检查是否其他程序占用录音，请关闭程序重新启动",Toast.LENGTH_SHORT).show();
			showDialog("启动录音失败,请检查是否其他程序占用录音，请关闭程序重新启动");
		}
		
	};
	
	@Override
	protected void onStart() {
		super.onStart();
		HXPos.getObj().onStart(this);
		HXPos.getObj().hookMicPlugDetext(this);
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		HXPos.getObj().close();
		HXPos.getObj().unHookMicPlugDetect();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		HXPos.getObj().close();
		HXPos.getObj().unHookMicPlugDetect();
	}
	
}
