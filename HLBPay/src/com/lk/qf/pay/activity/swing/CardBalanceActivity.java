package com.lk.qf.pay.activity.swing;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.List;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.bbpos.emvswipe.CAPK;
import com.bbpos.emvswipe.EmvSwipeController;
import com.bbpos.emvswipe.EmvSwipeController.AutoConfigError;
import com.bbpos.emvswipe.EmvSwipeController.BatteryStatus;
import com.bbpos.emvswipe.EmvSwipeController.CheckCardMode;
import com.bbpos.emvswipe.EmvSwipeController.CheckCardResult;
import com.bbpos.emvswipe.EmvSwipeController.DisplayText;
import com.bbpos.emvswipe.EmvSwipeController.EmvOption;
import com.bbpos.emvswipe.EmvSwipeController.EmvSwipeControllerListener;
import com.bbpos.emvswipe.EmvSwipeController.Error;
import com.bbpos.emvswipe.EmvSwipeController.NfcDataExchangeStatus;
import com.bbpos.emvswipe.EmvSwipeController.ReferralResult;
import com.bbpos.emvswipe.EmvSwipeController.StartEmvResult;
import com.bbpos.emvswipe.EmvSwipeController.TerminalSettingStatus;
import com.bbpos.emvswipe.EmvSwipeController.TransactionResult;
import com.bbpos.emvswipe.EmvSwipeController.TransactionType;
import com.lk.bhb.pay.R;
import com.lk.qf.pay.activity.BaseActivity;
import com.lk.qf.pay.beans.PosData;
import com.lk.qf.pay.utils.AmountUtils;
import com.lk.qf.pay.wedget.CommonTitleBar;

/**
 * 通过音频刷卡
 * 
 * @author fanbei
 * 
 */
public class CardBalanceActivity extends BaseActivity {

	private EmvSwipeController emvSwipeController;
	private Dialog dialog;
	private ProgressDialog progressDialog;
	private TextView cashin_show_msg_text;// 刷卡状态
	private ListView appListView;
	private String termNo = null;// 终端号
	private boolean isAskingForAmount = false;
	private String amount = "";// 充值数量
	private String cashbackAmount = "";
	private boolean isSwitchingActivity = false;
	private Context mContext;
	
	Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			startSwingCard();
		};
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	
		super.onCreate(savedInstanceState);
		setContentView(R.layout.card_balance);
		mContext = this;
		amount = "1";
		PosData.getPosData().setTermType("02");
		initUI();
		setSwingCardInit();
		bindBackButton();
	}

	@SuppressWarnings("static-access")
	private void initUI() {
		cashin_show_msg_text = (TextView) findViewById(R.id.cashin_show_msg_text);
	

	}

	/**
	 * 设置刷卡器初始化
	 */
	private void setSwingCardInit() {

		if (emvSwipeController == null) {
			emvSwipeController = EmvSwipeController.getInstance(this,
					new MyEmvSwipeControllerListener());
			emvSwipeController.startAudio();
		}
		try {
			String filename = "settings.txt";
			String inputDirectory = Environment.getExternalStorageDirectory()
					.getAbsolutePath() +

			"/Android/data/com.bbpos.emvswipe.ui/";

			FileInputStream fis = new FileInputStream(inputDirectory +

			filename);
			byte[] temp = new byte[fis.available()];
			fis.read(temp);
			fis.close();

			emvSwipeController.setAutoConfig(new String(temp));
			Toast.makeText(mContext, getString

			(R.string.setting_config), Toast.LENGTH_SHORT).show();
		} catch (Exception e) {
		}
		mHandler.sendEmptyMessageDelayed(01, 20);
	}

	/**
	 * 开始刷卡
	 */
	private void startSwingCard() {

		cashin_show_msg_text.setText("");
		cashin_show_msg_text.setText(R.string.starting);
		emvSwipeController.checkCard();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (isSwitchingActivity) {
			isSwitchingActivity = false;
		} else {
			emvSwipeController.stopAudio();
			emvSwipeController.resetEmvSwipeController();
			emvSwipeController = null;
		}
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		finish();
		super.onBackPressed();
	}

	/**
	 * 实现刷卡器的回调
	 * 
	 * @author Administrator
	 * 
	 */
	class MyEmvSwipeControllerListener implements EmvSwipeControllerListener {

		public void dismissDialog() {
			if (dialog != null) {
				dialog.dismiss();
				dialog = null;
			}
		}
		
		public void promptForAmount() {
	    	dismissDialog();			
			TransactionType transactionType = TransactionType.GOODS;
			if(emvSwipeController.setAmount(amount, cashbackAmount, "840", transactionType)) {

				dismissDialog();
				isAskingForAmount = false;
			} else {
				promptForAmount();
			}
	    }

		@Override
		public void onReturnCheckCardResult(CheckCardResult checkCardResult,
				Hashtable<String, String> decodeData) {
			dismissDialog();
			if (checkCardResult == CheckCardResult.NONE) {
				cashin_show_msg_text.setText(getString(R.string.no_card_detected));
				startSwingCard();
			} else if (checkCardResult == CheckCardResult.ICC) {
				PosData.getPosData().setMediaType("02");
				cashin_show_msg_text.setText(getString(R.string.icc_card_inserted));
				emvSwipeController.startEmv(EmvOption.START);
			} else if (checkCardResult == CheckCardResult.NOT_ICC) {
				cashin_show_msg_text.setText(getString(R.string.card_inserted));
			} else if (checkCardResult == CheckCardResult.BAD_SWIPE) {
				cashin_show_msg_text.setText(getString(R.string.bad_swipe));
				startSwingCard();
			} else if (checkCardResult == CheckCardResult.MCR) {
				String formatID = decodeData.get("formatID");
				String maskedPAN = decodeData.get("maskedPAN");
				String PAN = decodeData.get("PAN");
				String expiryDate = decodeData.get("expiryDate");
				String cardHolderName = decodeData.get("cardholderName");
				String ksn = decodeData.get("ksn");
				String serviceCode = decodeData.get("serviceCode");
				String track1Length = decodeData.get("track1Length");
				String track2Length = decodeData.get("track2Length");
				String track3Length = decodeData.get("track3Length");
				String encTracks = decodeData.get("encTracks");
				String encTrack1 = decodeData.get("encTrack1");
				String encTrack2 = decodeData.get("encTrack2");
				String encTrack3 = decodeData.get("encTrack3");
				String partialTrack = decodeData.get("partialTrack");
				String trackEncoding = decodeData.get("trackEncoding");
				String finalMessage = decodeData.get("finalMessage");
				String randomNumber = decodeData.get("randomNumber");
				String encWorkingKey = decodeData.get("encWorkingKey");
				for (String key : decodeData.keySet()) {
					System.out.println("[" + key + "]" + decodeData.get(key));
				}

				PosData.getPosData().setCardNo(PAN);
				PosData.getPosData().setTrack(encTrack2 + "|" + encTrack3);
				PosData.getPosData().setRandom(randomNumber);
				PosData.getPosData().setMediaType("01");
				emvSwipeController.getKsn();
			} else if (checkCardResult == CheckCardResult.NO_RESPONSE) {
				cashin_show_msg_text
						.setText(getString(R.string.card_no_response));
			} else if (checkCardResult == CheckCardResult.TRACK2_ONLY) {
				String formatID = decodeData.get("formatID");
				String maskedPAN = decodeData.get("maskedPAN");
				String PAN = decodeData.get("PAN");
				String expiryDate = decodeData.get("expiryDate");
				String cardHolderName = decodeData.get("cardholderName");
				String ksn = decodeData.get("ksn");
				String serviceCode = decodeData.get("serviceCode");
				String track1Length = decodeData.get("track1Length");
				String track2Length = decodeData.get("track2Length");
				String track3Length = decodeData.get("track3Length");
				String encTracks = decodeData.get("encTracks");
				String encTrack1 = decodeData.get("encTrack1");
				String encTrack2 = decodeData.get("encTrack2");
				String encTrack3 = decodeData.get("encTrack3");
				String partialTrack = decodeData.get("partialTrack");
				String trackEncoding = decodeData.get("trackEncoding");
				String finalMessage = decodeData.get("finalMessage");
				String randomNumber = decodeData.get("randomNumber");
				String encWorkingKey = decodeData.get("encWorkingKey");
			} else if (checkCardResult == CheckCardResult.NFC_TRACK2) {
				String formatID = decodeData.get("formatID");
				String maskedPAN = decodeData.get("maskedPAN");
				String PAN = decodeData.get("PAN");
				String expiryDate = decodeData.get("expiryDate");
				String cardHolderName = decodeData.get("cardholderName");
				String ksn = decodeData.get("ksn");
				String serviceCode = decodeData.get("serviceCode");
				String track1Length = decodeData.get("track1Length");
				String track2Length = decodeData.get("track2Length");
				String track3Length = decodeData.get("track3Length");
				String encTracks = decodeData.get("encTracks");
				String encTrack1 = decodeData.get("encTrack1");
				String encTrack2 = decodeData.get("encTrack2");
				String encTrack3 = decodeData.get("encTrack3");
				String partialTrack = decodeData.get("partialTrack");
				String trackEncoding = decodeData.get("trackEncoding");
				String finalMessage = decodeData.get("finalMessage");
				String randomNumber = decodeData.get("randomNumber");
				String encWorkingKey = decodeData.get("encWorkingKey");

				String content = getString(R.string.nfc_track2);
				content += getString(R.string.format_id) + " " + formatID
						+ "\n";
				content += getString(R.string.masked_pan) + " " + maskedPAN
						+ "\n";
				content += getString(R.string.pan) + " " + PAN + "\n";
				content += getString(R.string.expiry_date) + " " + expiryDate
						+ "\n";
				content += getString(R.string.cardholder_name) + " "
						+ cardHolderName + "\n";
				content += getString(R.string.ksn) + " " + ksn + "\n";
				content += getString(R.string.service_code) + " " + serviceCode
						+ "\n";
				content += getString(R.string.track_1_length) + " "
						+ track1Length + "\n";
				content += getString(R.string.track_2_length) + " "
						+ track2Length + "\n";
				content += getString(R.string.track_3_length) + " "
						+ track3Length + "\n";
				content += getString(R.string.encrypted_tracks) + " "
						+ encTracks + "\n";
				content += getString(R.string.encrypted_track_1) + " "
						+ encTrack1 + "\n";
				content += getString(R.string.encrypted_track_2) + " "
						+ encTrack2 + "\n";
				content += getString(R.string.encrypted_track_3) + " "
						+ encTrack3 + "\n";
				content += getString(R.string.partial_track) + " "
						+ partialTrack + "\n";
				content += getString(R.string.track_encoding) + " "
						+ trackEncoding + "\n";
				content += getString(R.string.final_message) + " "
						+ finalMessage + "\n";
				content += getString(R.string.random_number) + " "
						+ randomNumber + "\n";
				content += getString(R.string.encrypted_working_key) + " "
						+ encWorkingKey + "\n";
				// cashin_show_msg_text.setText(content);
				emvSwipeController.getKsn();

			} else if (checkCardResult == CheckCardResult.USE_ICC_CARD) {
				cashin_show_msg_text.setText(getString(R.string.use_icc_card));
			}
		}

		@Override
		public void onReturnCancelCheckCardResult(boolean isSuccess) {
			if (isSuccess) {
				cashin_show_msg_text.setText(R.string.cancel_check_card_success);
			} else {
				cashin_show_msg_text.setText(R.string.cancel_check_card_fail);
			}
		}

		@Override
		public void onReturnStartEmvResult(StartEmvResult startEmvResult,
				String ksn) {
			if (startEmvResult == StartEmvResult.SUCCESS) {
				cashin_show_msg_text
						.setText(getString(R.string.start_emv_success));
			} else {
				cashin_show_msg_text
						.setText(getString(R.string.start_emv_fail));
			}
		}

		@Override
		public void onReturnDeviceInfo(Hashtable<String, String> deviceInfoData) {
			String isSupportedTrack1 = deviceInfoData.get("isSupportedTrack1") == null ? ""
					: deviceInfoData.get("isSupportedTrack1");
			String isSupportedTrack2 = deviceInfoData.get("isSupportedTrack2") == null ? ""
					: deviceInfoData.get("isSupportedTrack2");
			String isSupportedTrack3 = deviceInfoData.get("isSupportedTrack3") == null ? ""
					: deviceInfoData.get("isSupportedTrack3");
			String bootloaderVersion = deviceInfoData.get("bootloaderVersion") == null ? ""
					: deviceInfoData.get("bootloaderVersion");
			String firmwareVersion = deviceInfoData.get("firmwareVersion") == null ? ""
					: deviceInfoData.get("firmwareVersion");
			String isUsbConnected = deviceInfoData.get("isUsbConnected") == null ? ""
					: deviceInfoData.get("isUsbConnected");
			String isCharging = deviceInfoData.get("isCharging") == null ? ""
					: deviceInfoData.get("isCharging");
			String batteryLevel = deviceInfoData.get("batteryLevel") == null ? ""
					: deviceInfoData.get("batteryLevel");
			String batteryPercentage = deviceInfoData.get("batteryPercentage") == null ? ""
					: deviceInfoData.get("batteryPercentage");
			String hardwareVersion = deviceInfoData.get("hardwareVersion") == null ? ""
					: deviceInfoData.get("hardwareVersion");
			String pinKsn = deviceInfoData.get("pinKsn") == null ? ""
					: deviceInfoData.get("pinKsn");
			String trackKsn = deviceInfoData.get("trackKsn") == null ? ""
					: deviceInfoData.get("trackKsn");
			String emvKsn = deviceInfoData.get("emvKsn") == null ? ""
					: deviceInfoData.get("emvKsn");
			String uid = deviceInfoData.get("uid") == null ? ""
					: deviceInfoData.get("uid");
			String csn = deviceInfoData.get("csn") == null ? ""
					: deviceInfoData.get("csn");
			String formatID = deviceInfoData.get("formatID") == null ? ""
					: deviceInfoData.get("formatID");
		}

		@Override
		public void onReturnTransactionResult(
				TransactionResult transactionResult) {
			dismissDialog();
			if (transactionResult == TransactionResult.APPROVED) {
				String message = getString(R.string.transaction_approved)
						+ "\n" + getString(R.string.amount) + ": $" + amount
						+ "\n";
				if (!cashbackAmount.equals("")) {
					message += getString(R.string.cashback_amount) + ": $"
							+ cashbackAmount;
				}
				cashin_show_msg_text.setText(message);
			} else if (transactionResult == TransactionResult.TERMINATED) {
				cashin_show_msg_text
						.setText(getString(R.string.transaction_terminated));
			} else if (transactionResult == TransactionResult.DECLINED) {
				cashin_show_msg_text
						.setText(getString(R.string.transaction_declined));
			} else if (transactionResult == TransactionResult.CANCEL) {
				cashin_show_msg_text.setText(getString(R.string.transaction_cancel));
			} else if (transactionResult == TransactionResult.CAPK_FAIL) {
				cashin_show_msg_text
						.setText(getString(R.string.transaction_capk_fail));
			} else if (transactionResult == TransactionResult.NOT_ICC) {
				cashin_show_msg_text
						.setText(getString(R.string.transaction_not_icc));
			} else if (transactionResult == TransactionResult.CARD_BLOCKED) {
				cashin_show_msg_text
						.setText(getString(R.string.transaction_card_blocked));
			} else if (transactionResult == TransactionResult.DEVICE_ERROR) {
				cashin_show_msg_text
						.setText(getString(R.string.transaction_device_error));
			} else if (transactionResult == TransactionResult.CARD_NOT_SUPPORTED) {
				cashin_show_msg_text.setText(getString(R.string.card_not_supported));
			} else if (transactionResult == TransactionResult.MISSING_MANDATORY_DATA) {
				cashin_show_msg_text
						.setText(getString(R.string.missing_mandatory_data));
			} else if (transactionResult == TransactionResult.NO_EMV_APPS) {
				cashin_show_msg_text.setText(getString(R.string.no_emv_apps));
			} else if (transactionResult == TransactionResult.INVALID_ICC_DATA) {
				cashin_show_msg_text.setText(getString(R.string.invalid_icc_data));
			} else if (transactionResult == TransactionResult.CONDITION_NOT_SATISFIED) {
				cashin_show_msg_text
						.setText(getString(R.string.condition_not_satisfied));
			} else if (transactionResult == TransactionResult.APPLICATION_BLOCKED) {
				cashin_show_msg_text
						.setText(getString(R.string.application_blocked));
			} else if (transactionResult == TransactionResult.ICC_CARD_REMOVED) {
				cashin_show_msg_text.setText(getString(R.string.icc_card_removed));
			}

		}

		@Override
		public void onReturnTransactionResult(
				TransactionResult transactionResult,
				Hashtable<String, String> data) {
			dismissDialog();

		}

		@Override
		public void onReturnBatchData(String tlv) {
			dismissDialog();
			String content = getString(R.string.batch_data);
			Hashtable<String, String> decodeData = EmvSwipeController.decodeTlv(tlv);
			Object[] keys = decodeData.keySet().toArray();
			Arrays.sort(keys);
			String randomNumber = decodeData.get("encTrack2EqRandomNumber");
			content += "\nrandomNumber:" + randomNumber;
			String encTrack2Eq = decodeData.get("encTrack2Eq");
			content += "\nencTrack2Eq:" + encTrack2Eq;
			
			String cardNo = decodeData.get("maskedPAN");
			content += "\ncardNo:" + cardNo;
			String encBatch = decodeData.get("encBatchMessage");
			content += "\nencBatch:" + encBatch;
			String period = decodeData.get("5f24");
			if(period.length()>4) 
				period = period.substring(0,4);
			content += "\nperiod:" + period;
			String icNumber = decodeData.get("5f34");
			if(icNumber.length()<3){
				icNumber = "0"+icNumber;
			}
			content += "\nicNumber:" + icNumber;
			String trmmodno = decodeData.get("batchKsn").toUpperCase();
			content += "\ntrmmodno:" + trmmodno;
			trmmodno = trmmodno.substring(0, 12);
			System.out.println(content);
			/*********************IC卡支付成功*********************/
			PosData.getPosData().setCardNo(cardNo);
			PosData.getPosData().setTermNo(trmmodno);
			PosData.getPosData().setTrack(encTrack2Eq + "|");
			PosData.getPosData().setRandom(randomNumber);
			PosData.getPosData().setMediaType("02");
			PosData.getPosData().setPeriod(period);
			PosData.getPosData().setCrdnum(icNumber);
			PosData.getPosData().setIcdata(encBatch);
			Intent intent = new Intent(mContext,CardBalanceConfirmActivity.class);
			startActivity(intent);
			finish();
			/**************************************************/
			
		}

		@Override
		public void onReturnTransactionLog(String tlv) {
			dismissDialog();
			String content = getString(R.string.transaction_log);
			content += tlv;
			// cashin_show_msg_text.setText(content);
		}

		@Override
		public void onReturnReversalData(String tlv) {
			dismissDialog();
			String content = getString(R.string.reversal_data);
			content += tlv;
			// cashin_show_msg_text.setText(content);
		}

		@Override
		public void onReturnPowerOnIccResult(boolean isSuccess, String ksn,
				String atr, int atrLength) {
		}

		@Override
		public void onReturnPowerOffIccResult(boolean isSuccess) {
		}

		@Override
		public void onReturnApduResult(boolean isSuccess, String apdu,
				int apduLength) {
		}

		@Override
		public void onReturnApduResultWithPkcs7Padding(boolean isSuccess,
				String apdu) {
		}

		@Override
		public void onReturnEmvCardDataResult(boolean isSuccess, String tlv) {
		}

		@Override
		public void onReturnEmvCardNumber(String cardNumber) {
			// cashin_show_msg_text.setText(getString(R.string.card_number) +
			// cardNumber);
		}

		@Override
		public void onReturnNfcDataResult(
				NfcDataExchangeStatus nfcDataExchangeStatus, String response,
				int responseLength) {
		}

		@Override
		public void onReturnPowerOffNfcResult(boolean isSuccess) {
		}

		@Override
		public void onReturnPowerOnNfcResult(boolean isSuccess, String data,
				int dataLength) {
		}

		@Override
		public void onReturnKsn(Hashtable<String, String> ksnTable) {
			String pinKsn = ksnTable.get("pinKsn") == null ? "" : ksnTable
					.get("pinKsn");
			String trackKsn = ksnTable.get("trackKsn") == null ? "" : ksnTable
					.get("trackKsn");
			String emvKsn = ksnTable.get("emvKsn") == null ? "" : ksnTable
					.get("emvKsn");
			String uid = ksnTable.get("uid") == null ? "" : ksnTable.get("uid");
			String csn = ksnTable.get("csn") == null ? "" : ksnTable.get("csn");

			String content = "";
			content += getString(R.string.pin_ksn) + pinKsn + "\n";
			content += getString(R.string.track_ksn) + trackKsn + "\n";
			content += getString(R.string.emv_ksn) + emvKsn + "\n";
			content += getString(R.string.uid) + uid + "\n";
			content += getString(R.string.csn) + csn + "\n";
			termNo = emvKsn.substring(0, 12);
			//StringUtils.getInterceptString(termNo, 4);
			Intent intent = new Intent(mContext,CardBalanceConfirmActivity.class);
			PosData.getPosData().setTermNo(termNo);
			startActivity(intent);
			finish();
		}

		@Override
		public void onReturnUpdateTerminalSettingResult(
				TerminalSettingStatus terminalSettingStatus) {
		}

		@Override
		public void onReturnReadTerminalSettingResult(
				TerminalSettingStatus terminalSettingStatus, String value) {
		}

		@Override
		public void onRequestSelectApplication(ArrayList<String> appList) {
			dismissDialog();

			dialog = new Dialog(mContext);
			dialog.setContentView(R.layout.application_dialog);
			dialog.setTitle(R.string.please_select_app);

			String[] appNameList = new String[appList.size()];
			for (int i = 0; i < appNameList.length; ++i) {
				appNameList[i] = appList.get(i);
			}

			appListView = (ListView) dialog.findViewById(R.id.appList);
			appListView.setAdapter(new ArrayAdapter<String>(
					mContext,
					android.R.layout.simple_list_item_1, appNameList));
			appListView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					emvSwipeController.selectApplication(position);
					dismissDialog();
				}

			});

			dialog.findViewById(R.id.cancelButton).setOnClickListener(
					new OnClickListener() {

						@Override
						public void onClick(View v) {
							emvSwipeController.cancelSelectApplication();
							dismissDialog();
						}
					});
			dialog.show();
		}

		@Override
		public void onRequestSetAmount() {
			isAskingForAmount = true;
			 promptForAmount();
		}

		@Override
		public void onRequestPinEntry() {
			dismissDialog();
			emvSwipeController.sendPinEntryResult("111111");
		}

		@Override
		public void onRequestVerifyID(String tlv) {
			dismissDialog();
			emvSwipeController.sendVerifyIDResult(true);
		}

		@Override
		public void onRequestCheckServerConnectivity() {
			dismissDialog();
			emvSwipeController.sendServerConnectivity(true);
		}

		@Override
		public void onRequestOnlineProcess(String tlv) {
			dismissDialog();
			emvSwipeController.sendOnlineProcessResult("8A023030");
			String DCdata = emvSwipeController.viposGetIccData(tlv);
			//PosData.getPosData().setIcdata(DCdata);
		}

		@Override
		public void onRequestTerminalTime() {
			dismissDialog();
			String terminalTime = new SimpleDateFormat("yyMMddHHmmss").format(Calendar.getInstance().getTime());
			emvSwipeController.sendTerminalTime(terminalTime);
			cashin_show_msg_text.setText(getString(R.string.request_terminal_time) + " "+ terminalTime);
		}

		@Override
		public void onRequestDisplayText(DisplayText displayText) {
			dismissDialog();

			String msg = "";
			if (displayText == DisplayText.AMOUNT) {
				msg = getString(R.string.amount);
			} else if (displayText == DisplayText.AMOUNT_OK_OR_NOT) {
				msg = getString(R.string.amount_ok);
			} else if (displayText == DisplayText.APPROVED) {
				msg = getString(R.string.approved);
			} else if (displayText == DisplayText.CALL_YOUR_BANK) {
				msg = getString(R.string.call_your_bank);
			} else if (displayText == DisplayText.CANCEL_OR_ENTER) {
				msg = getString(R.string.cancel_or_enter);
			} else if (displayText == DisplayText.CARD_ERROR) {
				msg = getString(R.string.card_error);
			} else if (displayText == DisplayText.DECLINED) {
				msg = getString(R.string.decline);
			} else if (displayText == DisplayText.ENTER_AMOUNT) {
				msg = getString(R.string.enter_amount);
			} else if (displayText == DisplayText.ENTER_PIN) {
				msg = getString(R.string.enter_pin);
			} else if (displayText == DisplayText.INCORRECT_PIN) {
				msg = getString(R.string.incorrect_pin);
			} else if (displayText == DisplayText.INSERT_CARD) {
				msg = getString(R.string.insert_card);
			} else if (displayText == DisplayText.NOT_ACCEPTED) {
				msg = getString(R.string.not_accepted);
			} else if (displayText == DisplayText.PIN_OK) {
				msg = getString(R.string.pin_ok);
			} else if (displayText == DisplayText.PLEASE_WAIT) {
				msg = getString(R.string.wait);
			} else if (displayText == DisplayText.PROCESSING_ERROR) {
				msg = getString(R.string.processing_error);
			} else if (displayText == DisplayText.REMOVE_CARD) {
				msg = getString(R.string.remove_card);
			} else if (displayText == DisplayText.USE_CHIP_READER) {
				msg = getString(R.string.use_chip_reader);
			} else if (displayText == DisplayText.USE_MAG_STRIPE) {
				msg = getString(R.string.use_mag_stripe);
			} else if (displayText == DisplayText.TRY_AGAIN) {
				msg = getString(R.string.try_again);
			} else if (displayText == DisplayText.REFER_TO_YOUR_PAYMENT_DEVICE) {
				msg = getString(R.string.refer_payment_device);
			} else if (displayText == DisplayText.TRANSACTION_TERMINATED) {
				msg = getString(R.string.transaction_terminated);
			} else if (displayText == DisplayText.TRY_ANOTHER_INTERFACE) {
				msg = getString(R.string.try_another_interface);
			} else if (displayText == DisplayText.ONLINE_REQUIRED) {
				msg = getString(R.string.online_required);
			} else if (displayText == DisplayText.PROCESSING) {
				msg = getString(R.string.processing);
			} else if (displayText == DisplayText.WELCOME) {
				msg = getString(R.string.welcome);
			} else if (displayText == DisplayText.PRESENT_ONLY_ONE_CARD) {
				msg = getString(R.string.present_one_card);
			} else if (displayText == DisplayText.CAPK_LOADING_FAILED) {
				msg = getString(R.string.capk_failed);
			} else if (displayText == DisplayText.LAST_PIN_TRY) {
				msg = getString(R.string.last_pin_try);
			}

			cashin_show_msg_text.setText(msg);
		}

		@Override
		public void onRequestClearDisplay() {
			dismissDialog();
			cashin_show_msg_text.setText("");
		}

		@Override
		public void onRequestReferProcess(String pan) {
			dismissDialog();
			dialog = new Dialog(mContext);
			dialog.setContentView(R.layout.refer_process_dialog);
			dialog.setTitle(getString(R.string.call_your_bank));

			dialog.findViewById(R.id.approvedButton).setOnClickListener(
					new OnClickListener() {

						@Override
						public void onClick(View v) {
							emvSwipeController
									.sendReferProcessResult(ReferralResult.APPROVED);
						}
					});

			dialog.findViewById(R.id.declinedButton).setOnClickListener(
					new OnClickListener() {

						@Override
						public void onClick(View v) {
							emvSwipeController
									.sendReferProcessResult(ReferralResult.DECLINED);
						}
					});

			dialog.findViewById(R.id.cancelButton).setOnClickListener(
					new OnClickListener() {

						@Override
						public void onClick(View v) {
							emvSwipeController.cancelReferProcess();
						}
					});
		}

		@Override
		public void onRequestAdviceProcess(String tlv) {
			dismissDialog();
			cashin_show_msg_text.setText(getString(R.string.advice_process));
		}

		@Override
		public void onRequestFinalConfirm() {
			dismissDialog();
			emvSwipeController.sendFinalConfirmResult(true);
		}

		@Override
		public void onAutoConfigProgressUpdate(double percentage) {
			if (progressDialog != null) {
				progressDialog.setProgress((int) percentage);
			}
		}

		@Override
		public void onAutoConfigCompleted(boolean isDefaultSettings,
				String autoConfigSettings) {
			if (progressDialog != null) {
				progressDialog.dismiss();
				progressDialog = null;
			}

			String outputDirectory = Environment.getExternalStorageDirectory()
					.getAbsolutePath() + "/Android/data/com.bbpos.emvswipe.ui/";
			String filename = "settings.txt";
			String content = getString(R.string.auto_config_completed);
			if (isDefaultSettings) {
				content += "\n" + getString(R.string.default_settings);
				new File(outputDirectory + filename).delete();
			} else {
				content += "\n" + getString(R.string.settings)
						+ autoConfigSettings;

				try {
					File directory = new File(outputDirectory);
					if (!directory.isDirectory()) {
						directory.mkdirs();
					}
					FileOutputStream fos = new FileOutputStream(outputDirectory
							+ filename, false);
					fos.write(autoConfigSettings.getBytes());
					fos.flush();
					fos.close();

					content += "\n"
							+ getString(R.string.settings_written_to_external_storage);
				} catch (Exception e) {
				}
			}
			// cashin_show_msg_text.setText(content);
		}

		@Override
		public void onAutoConfigError(AutoConfigError autoConfigError) {
			if (progressDialog != null) {
				progressDialog.dismiss();
				progressDialog = null;
			}

			if (autoConfigError == AutoConfigError.PHONE_NOT_SUPPORTED) {
				cashin_show_msg_text.setText(getString(R.string.auto_config_error_phone_not_supported));
			} else if (autoConfigError == AutoConfigError.INTERRUPTED) {
				cashin_show_msg_text.setText(getString(R.string.auto_config_error_interrupted));
			}
		}

		@Override
		public void onBatteryLow(BatteryStatus batteryStatus) {
			if (batteryStatus == BatteryStatus.LOW) {
				cashin_show_msg_text.setText(getString(R.string.battery_low));
			} else if (batteryStatus == BatteryStatus.CRITICALLY_LOW) {
				cashin_show_msg_text
						.setText(getString(R.string.battery_critically_low));
			}
		}

		@Override
		public void onNoDeviceDetected() {
			dismissDialog();
			cashin_show_msg_text
					.setText(getString(R.string.no_device_detected));
		}

		@Override
		public void onDevicePlugged() {
			dismissDialog();
			cashin_show_msg_text.setText(getString(R.string.device_plugged));
			startSwingCard();
		}

		@Override
		public void onDeviceUnplugged() {
			dismissDialog();
			cashin_show_msg_text.setText(getString(R.string.device_unplugged));
		}

		@Override
		public void onDeviceHere(boolean isHere) {
		}

		@Override
		public void onError(Error errorState) {
			// dismissDialog();
			// cashin_account_text.setText("");
			if (errorState == Error.CMD_NOT_AVAILABLE) {
				cashin_show_msg_text
						.setText(getString(R.string.command_not_available));
			} else if (errorState == Error.TIMEOUT) {
				cashin_show_msg_text
						.setText(getString(R.string.device_no_response));
			} else if (errorState == Error.DEVICE_RESET) {
				cashin_show_msg_text.setText(getString(R.string.device_reset));
			} else if (errorState == Error.UNKNOWN) {
				cashin_show_msg_text.setText(getString(R.string.unknown_error));
			} else if (errorState == Error.DEVICE_BUSY) {
				cashin_show_msg_text.setText(getString(R.string.device_busy));
			} else if (errorState == Error.INPUT_OUT_OF_RANGE) {
				cashin_show_msg_text.setText(getString(R.string.out_of_range));
			} else if (errorState == Error.INPUT_INVALID_FORMAT) {
				cashin_show_msg_text
						.setText(getString(R.string.invalid_format));
				Toast.makeText(mContext,
						getString(R.string.invalid_format), Toast.LENGTH_LONG)
						.show();
			} else if (errorState == Error.INPUT_ZERO_VALUES) {
				cashin_show_msg_text.setText(getString(R.string.zero_values));
			} else if (errorState == Error.INPUT_INVALID) {
				cashin_show_msg_text.setText(getString(R.string.input_invalid));
				Toast.makeText(mContext,
						getString(R.string.input_invalid), Toast.LENGTH_LONG)
						.show();
			} else if (errorState == Error.CASHBACK_NOT_SUPPORTED) {
				cashin_show_msg_text
						.setText(getString(R.string.cashback_not_supported));
				Toast.makeText(mContext,
						getString(R.string.cashback_not_supported),
						Toast.LENGTH_LONG).show();
			} else if (errorState == Error.CRC_ERROR) {
				cashin_show_msg_text.setText(getString(R.string.crc_error));
			} else if (errorState == Error.COMM_ERROR) {
				cashin_show_msg_text.setText(getString(R.string.comm_error));
			}
		}

		@Override
		public void onPowerDown() {
			cashin_show_msg_text.setText(getString(R.string.device_off));
		}

		@Override
		public void onReturnCAPKDetail(CAPK arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onReturnCAPKList(List<CAPK> arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onReturnCAPKLocation(String arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onReturnEmvCardBalance(boolean arg0, String arg1) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onReturnEmvLoadLog(String[] arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onReturnEmvReport(String arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onReturnEmvReportList(Hashtable<String, String> arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onReturnEmvTransactionLog(String[] arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onReturnEncryptPinResult(Hashtable<String, String> arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onReturnUpdateCAPKResult(boolean arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onReturnViposBatchExchangeApduResult(
				Hashtable<Integer, String> arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onReturnViposExchangeApduResult(String arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onWaitingForCard(CheckCardMode arg0) {
			dismissDialog();
			cashin_show_msg_text.setText(getString(R.string.waiting_for_card));		}

		@Override
		public void onBatchDataDetected() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onOnlineProcessDataDetected() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onReturnEncryptDataResult(boolean arg0,
				Hashtable<String, String> arg1) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onReversalDataDetected() {
			// TODO Auto-generated method stub
			
		}

	}

}

