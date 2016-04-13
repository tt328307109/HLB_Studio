package com.lk.qf.pay.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.AlertDialog;
import android.content.Context;
import android.view.View;
import android.widget.DatePicker;

public class DateDialogUtils {

	private Date date;
	private SimpleDateFormat sdf;
	private Calendar cal;
	private AlertDialog.Builder builder;
	private int mYear;
	private int mMonth;
	private int mDay;
	private int startYear;
	private int startMonth;
	private int startDay;
	private int endDay;
	private boolean timeIsOk = true;
	private static int TYPE = 0;// 起始时间
	private View view;
	private DatePicker datePicker;
	
	private Context context;
	public DateDialogUtils(Context context){
		this.context= context;
	}
//	private void showDateDialog() {
//
//		cal = Calendar.getInstance();
//		mYear = cal.get(Calendar.YEAR);
//		mMonth = cal.get(Calendar.MONTH);
//		mDay = cal.get(Calendar.DAY_OF_MONTH);
//		builder = new AlertDialog.Builder(context);
//		view = LayoutInflater.inflate(R.layout.custom_date_picker_layout,null);
//		datePicker = (DatePicker) view.findViewById(R.id.custom_datePicker);
//
//		// 设置当前时间
//		cal.setTimeInMillis(System.currentTimeMillis());
//		datePicker.init(mYear, mMonth, mDay, dateChangedListener);
//		builder.setView(view);
//		builder.setTitle(getResources().getString(R.string.choose_start_time));
//		builder.setPositiveButton(getResources().getString(R.string.confirm),
//				new DialogInterface.OnClickListener() {
//					// 确定
//					@Override
//					public void onClick(DialogInterface dialog, int arg1) {
//
//						DecimalFormat mFormat = new DecimalFormat("00");
//						String month = mFormat.format(Double
//								.valueOf(mMonth + 1));
//						String day = mFormat.format(Double.valueOf(mDay));
//						if (timeIsOk == true) {
//
//							if (TYPE == 0) {
//
//								tvStartTime.setText(mYear + month + day);
//								startYear = mYear;
//								startMonth = mMonth;
//								startDay = mDay;
//							} else if (TYPE == 1) {
//
//								tvEndTime.setText(mYear + month + day);
//							}
//						} else {
//							if (TYPE == 0) {
//								tvStartTime.setText("");
//							} else {
//								tvEndTime.setText("");
//							}
//						}
//
//						dialog.cancel();
//					}
//				});
//		// 取消
//		builder.setNegativeButton(getResources().getString(R.string.cancel),
//				new DialogInterface.OnClickListener() {
//
//					@Override
//					public void onClick(DialogInterface dialog, int arg1) {
//						// TODO Auto-generated method stub
//						dialog.cancel();
//					}
//				});
//
//		builder.create().show();
//
//	}
//
//	OnDateChangedListener dateChangedListener = new OnDateChangedListener() {
//
//		@Override
//		public void onDateChanged(DatePicker arg0, int year, int month, int day) {
//			// TODO Auto-generated method stub
//			mYear = year;
//			mMonth = month;
//			mDay = day;
//
//			if (isDateAfter(datePicker)) {
//				datePicker.init(2015, 8, 1, this);
//				T.ss("无效时间");
//				timeIsOk = false;
//			} else {
//
//				if (TYPE == 1 && isDateBefor(datePicker)) {
//
//					datePicker.init(cal.get(Calendar.YEAR),
//							cal.get(Calendar.MONTH),
//							cal.get(Calendar.DAY_OF_MONTH), this);
//					timeIsOk = false;
//					T.ss("无效时间");
//				} else {
//					timeIsOk = true;
//				}
//			}
//		}
//	};
//
//	private boolean isDateAfter(DatePicker tempView) {
//		Calendar mCalendar = Calendar.getInstance();
//		Calendar tempCalendar = Calendar.getInstance();
//		tempCalendar.set(tempView.getYear(), tempView.getMonth(),
//				tempView.getDayOfMonth(), 0, 0, 0);
//		if (tempCalendar.after(mCalendar)) {
//			return true;
//		} else {
//			return false;
//		}
//	}
//
//	private boolean isDateBefor(DatePicker eView) {
//		Calendar sCalendar = Calendar.getInstance();
//		sCalendar.set(startYear, startMonth, startDay);
//		Calendar eCalendar = Calendar.getInstance();
//		eCalendar.set(eView.getYear(), eView.getMonth(), eView.getDayOfMonth());
//		if (eCalendar.before(sCalendar)) {
//
//			return true;
//		} else {
//			return false;
//		}
//	}
}
