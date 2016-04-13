package com.lk.qf.pay.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lk.bhb.pay.R;
import com.lk.qf.pay.aanewactivity.AddDaiLiMerchantActivity;
import com.lk.qf.pay.aanewactivity.JieSuanActivity;
import com.lk.qf.pay.aanewactivity.PoPhotoActivity;
import com.lk.qf.pay.aanewactivity.RedemptionDepositActivity;
import com.lk.qf.pay.activity.AboutActivity;
import com.lk.qf.pay.activity.EquListActivity;
import com.lk.qf.pay.activity.HelpActivity;
import com.lk.qf.pay.activity.LoginActivity;
import com.lk.qf.pay.activity.PwdManageActivity;
import com.lk.qf.pay.activity.swing.SwingHXCardActivity;
import com.lk.qf.pay.beans.PosData;
import com.lk.qf.pay.golbal.Actions;
import com.lk.qf.pay.golbal.MApplication;
import com.lk.qf.pay.golbal.User;
import com.lk.qf.pay.sharedpref.SharedPrefConstant;
import com.lk.qf.pay.tool.T;
import com.lk.qf.pay.v50.PayByV50CardActivity;
import com.lk.qf.pay.wedget.customdialog.ActionSheetDialog;
import com.lk.qf.pay.wedget.customdialog.ActionSheetDialog.OnSheetItemClickListener;
import com.lk.qf.pay.wedget.customdialog.ActionSheetDialog.SheetItemColor;
import com.lk.qf.pay.wedget.customdialog.AlertDialog;
import com.lk.qf.pay.zxb.ZXBDeviceListActivity;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;
import com.umeng.update.UpdateStatus;

public class MoreFragment extends BaseFragment implements OnClickListener {

	private TextView more_handPos;// 绑定设备
	private TextView more_share_text;// 分享
	private TextView more_about_text;// 关于我们
	private TextView more_feedback_text;// 意见反馈
	private TextView more_help_text;// 使用帮助
	private TextView more_shiming_text;// 实名认证
	private TextView more_jiesuan_text;// 结算信息
	private TextView more_photo_text;// 上传证件照
	// private TextView more_upToGoldMedal;//升级金牌
	private TextView tvShiMing, tvPhoto, tvJiesuan, tvVersion, tvIsPos,
			tvIsYanjin;//
	private LinearLayout more_version_layout;// 版本更新
	private LinearLayout merchant_contact_layout;// 拨打客服电话
	private String userName;
	private MApplication mApplication;
	private Context mContext;
	private AudioManager localAudioManager;

	public static BaseFragment newInstance() {
		BaseFragment fragment = new MoreFragment();
		return fragment;
	}

	private View layoutView;
	private String state;// 商户状态
	private String strShiming;// 是否实名认证过0表示已认证
	private String strJiesuan;// 是否添加过结算信息过0表示已添加
	private String strPhoto;// 是否上传过认证照片 过0表示已认证
	private String strBandPos;// 绑定pos机数量
	private String usesort, userType, isFree, isShuhui;// 是否缴纳押金//商户类型//是否免押金//是否赎回过押金
	private RelativeLayout rlYajin;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mApplication = (MApplication) getActivity().getApplication();
		mContext = getActivity();
		userName = User.uName;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (layoutView == null) {

			layoutView = inflater.inflate(R.layout.fragment_more, container,
					false);
			userType = MApplication.mSharedPref
					.getSharePrefString(SharedPrefConstant.AGLEVEL);
			isFree = MApplication.mSharedPref
					.getSharePrefString(SharedPrefConstant.STITE);

			// more_upToGoldMedal = (TextView) layoutView
			// .findViewById(R.id.more_upToGoldMedal_text);
			// more_upToGoldMedal.setOnClickListener(this);
			rlYajin = (RelativeLayout) layoutView
					.findViewById(R.id.rl_more_yajin);

			more_handPos = (TextView) layoutView
					.findViewById(R.id.more_handPos_text);
			more_handPos.setOnClickListener(this);
			more_share_text = (TextView) layoutView
					.findViewById(R.id.more_share_text);
			more_share_text.setOnClickListener(this);
			more_version_layout = (LinearLayout) layoutView
					.findViewById(R.id.more_version_layout);
			more_version_layout.setOnClickListener(this);
			more_about_text = (TextView) layoutView
					.findViewById(R.id.more_about_text);
			more_about_text.setOnClickListener(this);
			merchant_contact_layout = (LinearLayout) layoutView
					.findViewById(R.id.merchant_contact_layout);
			merchant_contact_layout.setOnClickListener(this);
			more_feedback_text = (TextView) layoutView
					.findViewById(R.id.more_feedback_text);
			more_feedback_text.setOnClickListener(this);
			more_help_text = (TextView) layoutView
					.findViewById(R.id.more_help_text);
			more_help_text.setOnClickListener(this);

			more_shiming_text = (TextView) layoutView
					.findViewById(R.id.more_shiming_text);
			more_shiming_text.setOnClickListener(this);

			more_jiesuan_text = (TextView) layoutView
					.findViewById(R.id.more_jiesuan_text);
			more_jiesuan_text.setOnClickListener(this);
			more_photo_text = (TextView) layoutView
					.findViewById(R.id.more_photo_text);
			more_photo_text.setOnClickListener(this);

			layoutView.findViewById(R.id.btn_more_safe_quit)
					.setOnClickListener(this);
			tvShiMing = (TextView) layoutView
					.findViewById(R.id.tv_more_shiming_is);
			tvJiesuan = (TextView) layoutView.findViewById(R.id.tv_jiesuan_is);
			tvPhoto = (TextView) layoutView.findViewById(R.id.tv_more_photo_is);
			tvVersion = (TextView) layoutView
					.findViewById(R.id.more_version_text);// 版本号
			tvIsPos = (TextView) layoutView.findViewById(R.id.tv_more_pos_is);// 绑定pos机数量

			layoutView.findViewById(R.id.more_yajin_text).setOnClickListener(
					this);
			layoutView.findViewById(R.id.more_pwd_text)
					.setOnClickListener(this);
			tvIsYanjin = (TextView) layoutView
					.findViewById(R.id.tv_more_yajin_is);// 是否缴纳押金

			PackageManager pm = mContext.getPackageManager();
			PackageInfo pi;
			try {
				pi = pm.getPackageInfo(getActivity().getPackageName(), 0);
				tvVersion.setText("V" + pi.versionName);
			} catch (NameNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		// 缓存的rootView需要判断是否已经被加过parent，
		// 如果有parent需要从parent删除，要不然会发生这个rootview已经有parent的错误。
		ViewGroup parent = (ViewGroup) layoutView.getParent();
		if (parent != null) {
			parent.removeView(layoutView);
		}
		localAudioManager = (AudioManager) getActivity().getSystemService(
				Context.AUDIO_SERVICE);
		// layoutView.findViewById(R.id.rl_more).setPadding(0,
		// MyGetStatusUtils.getStatusBarHeight(mContext), 0, 0);

		return layoutView;
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		if (isFree.equals("1")) {
			rlYajin.setVisibility(View.GONE);
		}

		state = MApplication.mSharedPref
				.getSharePrefString(SharedPrefConstant.STATE);
		strShiming = MApplication.mSharedPref
				.getSharePrefString(SharedPrefConstant.MERENTERPRISERADD);
		strJiesuan = MApplication.mSharedPref
				.getSharePrefString(SharedPrefConstant.MERBANKADD);
		strPhoto = MApplication.mSharedPref
				.getSharePrefString(SharedPrefConstant.MERCERTIFICATEADD);
		strBandPos = MApplication.mSharedPref
				.getSharePrefString(SharedPrefConstant.POSCOUNT);

		usesort = MApplication.mSharedPref
				.getSharePrefString(SharedPrefConstant.USESORT);
		if (usesort.equals("0")) {
			tvIsYanjin.setText("未缴纳");
		} else {
			tvIsYanjin.setText("已缴纳");
		}

		if (strBandPos.equals("0")) {
			tvIsPos.setText("未绑定");
		} else {
			tvIsPos.setText("已绑定");
		}
		if (strShiming.equals("1")) {
			tvShiMing.setText("审核中");
		} else if (strShiming.equals("2")) {
			tvShiMing.setText("未通过");
		} else if (strShiming.equals("3")) {
			tvShiMing.setText("已认证");
		} else {
			tvShiMing.setText("未认证");
		}

		if (strJiesuan.equals("1")) {
			tvJiesuan.setText("审核中");
		} else if (strJiesuan.equals("2")) {
			tvJiesuan.setText("未通过");
		} else if (strJiesuan.equals("3")) {
			tvJiesuan.setText("已认证");
		}else{
			tvShiMing.setText("未认证");
		}

		if (strPhoto.equals("1")) {
			tvPhoto.setText("审核中");
		} else if (strPhoto.equals("2")) {
			tvPhoto.setText("未通过");
		} else if (strPhoto.equals("3")) {
			tvPhoto.setText("已认证");
		}else{
			tvPhoto.setText("未认证");
		}
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {

		case R.id.more_handPos_text:// 绑定mpos

			Intent equlist = new Intent(mContext, EquListActivity.class);
			startActivity(equlist);
			break;
		case R.id.more_yajin_text:// 押金管理
			if (strBandPos.equals("0")) {
				T.ss("暂未绑定刷卡器");
				return;
			}
			if (usesort.equals("0")) {
				payDeposit();
			} else if (!usesort.equals("0") && !userType.equals("4")) {

				Intent intentYajin = new Intent(mContext,
						RedemptionDepositActivity.class);
				startActivity(intentYajin);
			} else {
				T.ss("已缴纳过押金");
			}
			break;
		case R.id.more_shiming_text:
			if (strShiming.equals("3")) {
				T.ss("已认证");
				return;
			}
			if (strShiming.equals("3")) {

				T.ss("已认证");
				return;
			}
			Intent shiming = new Intent(mContext,
					AddDaiLiMerchantActivity.class);
			shiming.setAction("MER");
			shiming.putExtra("type", "1");
			shiming.putExtra("userName", MApplication.mSharedPref
					.getSharePrefString(SharedPrefConstant.USERNAME));
			startActivity(shiming);
			break;
		case R.id.more_jiesuan_text:
			if (strJiesuan.equals("3")) {
				T.ss("已认证");
				return;
			}
			Intent jiesuan = new Intent(mContext, JieSuanActivity.class);
			jiesuan.putExtra("type", "1");
			jiesuan.putExtra("userName", MApplication.mSharedPref
					.getSharePrefString(SharedPrefConstant.USERNAME));
			startActivity(jiesuan);
			break;
		case R.id.more_photo_text:
			if (strPhoto.equals("3")) {
				T.ss("已认证");
				return;
			}
			Intent photo = new Intent(mContext, PoPhotoActivity.class);
			photo.putExtra("type", "1");
			photo.putExtra("userName", MApplication.mSharedPref
					.getSharePrefString(SharedPrefConstant.USERNAME));
			startActivity(photo);
			break;
		case R.id.more_share_text:
			share();
			break;
		case R.id.more_pwd_text:
			Intent intent13 = new Intent(mContext, PwdManageActivity.class);
			startActivity(intent13);
			break;
		case R.id.more_version_layout:
			UmengUpdateAgent.setUpdateListener(updateListener);
			UmengUpdateAgent.forceUpdate(getActivity());
			break;
		case R.id.more_about_text:
			startActivity(new Intent(getActivity(), AboutActivity.class));
			break;
		case R.id.merchant_contact_layout:
			dialTelephone();
			break;
		case R.id.more_feedback_text:
			feedback();
			break;
		case R.id.more_help_text:
			startActivity(new Intent(getActivity(), HelpActivity.class));
			break;
		case R.id.btn_more_safe_quit:
			goQuit(); // 安全退出
			break;
		}

	}

	UmengUpdateListener updateListener = new UmengUpdateListener() {

		@Override
		public void onUpdateReturned(int updateStatus, UpdateResponse updateInfo) {
			// TODO Auto-generated method stub
			switch (updateStatus) {
			case UpdateStatus.Yes: // has update
				UmengUpdateAgent.showUpdateDialog(getActivity(), updateInfo);
				break;
			case UpdateStatus.No: // has no update
				T.ss("目前已是最新版本");
				break;
			case UpdateStatus.Timeout: // time out
				T.ss("请求超时");
				break;
			}
		}
	};

	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		if (hidden) {

		}
	}

	/**
	 * 缴纳押金
	 */
	private void payDeposit() {

		new ActionSheetDialog(getActivity())
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
									PosData.getPosData().setPayAmt("30000");
									intent1.setAction(Actions.ACTION_CASHIN);
									PosData.getPosData().setPayType("00");
									startActivity(intent1);
								} else {
									// MyCustomAlertDailogUtils alertDailogUtils
									// = new
									// MyCustomAlertDailogUtils(getActivity());
									// alertDailogUtils.showCustomAlertDailog1("请插入刷卡器");
									showDialog("请插入刷卡器！");
								}
							}
						})
				.addSheetItem("蓝牙刷卡器", SheetItemColor.Blue,
						new OnSheetItemClickListener() {
							@Override
							public void onClick(int which) {
								Intent it = new Intent(mContext,
										ZXBDeviceListActivity.class);
								PosData.getPosData().setPayAmt("30000");
								it.setAction(Actions.ACTION_CASHIN);
								PosData.getPosData().setPayType("00");
								startActivity(it);
							}
						})

				.addSheetItem("键盘蓝牙刷卡器", SheetItemColor.Blue,
						new OnSheetItemClickListener() {
							@Override
							public void onClick(int which) {
								Intent it3 = new Intent(mContext,
										PayByV50CardActivity.class);

								PosData.getPosData().setPayAmt("30000");
								it3.setAction(Actions.ACTION_CASHIN);
								PosData.getPosData().setPayType("00");
								startActivity(it3);
							}
						}).show();
	}

	/**
	 * 分享功能
	 */
	private void share() {

		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("image/*");
		intent.putExtra(Intent.EXTRA_SUBJECT, "分享");
		intent.putExtra(Intent.EXTRA_TEXT, "嗨! 我正在使用快易付客户端,你也来一起玩哈!"
				+ getResources().getString(R.string.download_add));
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(Intent.createChooser(intent, getActivity().getTitle()));
		getActivity().overridePendingTransition(R.anim.share_in_from_bottom, 0);
	}

	/**
	 * 弹出客服电话对话框
	 */
	private void dialTelephone() {

		new AlertDialog(getActivity())
				.builder()
				.setTitle(
						"服务热线:"
								+ getResources().getString(
										R.string.gs_phone_show))
				.setMsg("您确定要拨打客服电话？")
				.setPositiveButton("拨打", new OnClickListener() {
					@Override
					public void onClick(View v) {
						call();
					}
				}).setNegativeButton("取消", new OnClickListener() {
					@Override
					public void onClick(View v) {

					}
				}).show();
	}

	/**
	 * 进入拨打电话页面
	 */
	private void call() {
		Intent it = new Intent();
		it.setAction("android.intent.action.DIAL");
		it.setData(Uri.parse("tel:"
				+ getResources().getString(R.string.gs_phone)));
		startActivity(it);
	}

	/**
	 * 意见反馈
	 */
	private void feedback() {
		Intent data = new Intent(Intent.ACTION_SENDTO);
		data.setType("text/plain");
		data.setData(Uri.parse(getResources().getString(R.string.gs_email)));
		data.putExtra(Intent.EXTRA_SUBJECT, "用户:" + userName + " 意见反馈");
		data.putExtra(Intent.EXTRA_TEXT, "请输入反馈意见内容");
		data.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		try {
			startActivity(data);
		} catch (Exception e) {
			Toast.makeText(getActivity(), "客户端没有安装邮件", Toast.LENGTH_SHORT)
					.show();
		}
	}

	/**
	 * 安全退出
	 */
	private void goQuit() {

		new AlertDialog(getActivity()).builder().setTitle("提示")
				.setMsg("您确定要退出登录吗？")
				.setPositiveButton("确认退出", new OnClickListener() {
					@Override
					public void onClick(View v) {
						// getActivity().onBackPressed();
						// AppManager.getAppManager().finishAllActivity();
						// AppManager.getAppManager().AppExit(mContext);
						getActivity().finish();
						Intent intent = new Intent(getActivity(),
								LoginActivity.class);
						startActivity(intent);
					}
				}).setNegativeButton("取消", new OnClickListener() {
					@Override
					public void onClick(View v) {

					}
				}).show();
	}
}
