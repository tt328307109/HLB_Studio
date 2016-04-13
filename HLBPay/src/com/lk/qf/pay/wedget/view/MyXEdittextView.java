package com.lk.qf.pay.wedget.view;

import com.lk.bhb.pay.R;
import com.lk.qf.pay.indiana.myinterface.MyEditTextListener;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.EditText;
import android.view.MotionEvent;
import android.view.View;

public class MyXEdittextView extends EditText implements View.OnTouchListener,
		View.OnFocusChangeListener, TextWatcher {

	private Drawable _right;
	private OnTouchListener _t;
	private OnFocusChangeListener _f;
	public static int PHONENUM = 0;
	public static int CREDIT_CARD = 1;
	public static int BANK_NUM = 2;
	public static int ID_CARD = 3;
	private int format_Style = 0;
	private int maxLength = 13;// 默认为手机号长度+两个空格符
	private String separator = " "; // 分割符，默认使用空格分割
	private int[] style;

	public MyXEdittextView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		init();
	}

	public MyXEdittextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		init();
	}

	public MyXEdittextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		init();
	}

	@Override
	public boolean onTouch(View arg0, MotionEvent arg1) {
		// TODO Auto-generated method stub
		if (getCompoundDrawables()[2] != null) {
			boolean tapped = arg1.getX() > (getWidth() - getPaddingRight() - _right
					.getIntrinsicWidth());
			if (tapped) {
				if (arg1.getAction() == MotionEvent.ACTION_UP) {
					setText("");
				}
				return true;
			}
		}
		if (_t != null) {
			return _t.onTouch(arg0, arg1);
		}
		return false;
	}

	@Override
	public void afterTextChanged(Editable arg0) {
		// TODO Auto-generated method stub
		setClearIconVisible(isFocused() && !TextUtils.isEmpty(arg0));
	}

	@Override
	public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
			int arg3) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onFocusChange(View arg0, boolean hasFocus) {
		// TODO Auto-generated method stub
		setClearIconVisible(hasFocus && !TextUtils.isEmpty(getText()));
		if (_f != null) {
			_f.onFocusChange(arg0, hasFocus);
		}
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		if (s == null || s.length() == 0)
			return;
		StringBuilder sb = new StringBuilder();
		if (format_Style == PHONENUM) {
			for (int i = 0; i < s.length(); i++) {
				if (i != 3 && i != 8 && s.charAt(i) == ' ') {
					continue;
				} else {
					sb.append(s.charAt(i));
					if ((sb.length() == 4 || sb.length() == 9)
							&& sb.charAt(sb.length() - 1) != ' ') {
						sb.insert(sb.length() - 1, ' ');
					}
				}
			}
		} else if (format_Style == CREDIT_CARD) {
			for (int i = 0; i < s.length(); i++) {
				if (i != 4 && i != 9 && i != 14 && s.charAt(i) == ' ') {
					continue;
				} else {
					sb.append(s.charAt(i));
					if ((sb.length() == 5 || sb.length() == 10 || sb.length() == 15)
							&& sb.charAt(sb.length() - 1) != ' ') {
						sb.insert(sb.length() - 1, ' ');
					}
				}
			}
		} else if (format_Style == BANK_NUM) {
			for (int i = 0; i < s.length(); i++) {
				if (i != 4 && i != 9 && i != 14 && i != 19
						&& s.charAt(i) == ' ') {
					continue;
				} else {
					sb.append(s.charAt(i));
					if ((sb.length() == 5 || sb.length() == 10
							|| sb.length() == 15 || sb.length() == 20)
							&& sb.charAt(sb.length() - 1) != ' ') {
						sb.insert(sb.length() - 1, ' ');
					}
				}
			}
		} else if (format_Style == ID_CARD) {
			for (int i = 0; i < s.length(); i++) {
				if (i != 3 && i != 7 && i != 16 && s.charAt(i) == ' ') {
					continue;
				} else {
					sb.append(s.charAt(i));
					if ((sb.length() == 4 || sb.length() == 8 || sb.length() == 17)
							&& sb.charAt(sb.length() - 1) != ' ') {
						sb.insert(sb.length() - 1, ' ');
					}
				}
			}
		}
		if (!sb.toString().equals(s.toString())) {
			int index = start + 1;
			if (sb.charAt(start) == ' ') {
				if (before == 0) {
					index++;
				} else {
					index--;
				}
			} else {
				if (before == 1) {
					index--;
				}
			}
//			 Log.i("result", "------sb.length()-----------" + sb.length());
//			 Log.i("result", "------maxLength-----------" + maxLength);
			if (sb.length() > maxLength) {
				setText(sb.substring(0, maxLength).toString());
			} else {
				setText(sb.toString());
			}
			setSelection(index);
		}
//		setFilters(new InputFilter[] { new InputFilter.LengthFilter(maxLength) });// 设置edittext
		// setMaxEms(maxLength);
	}

	@Override
	public void setOnFocusChangeListener(OnFocusChangeListener l) {
		this._f = l;
	}

	@Override
	public void setOnTouchListener(OnTouchListener l) {
		this._t = l;
	}

	private void init() {
		_right = getCompoundDrawables()[2];
		if (_right == null) {
			_right = getResources().getDrawable(R.drawable.icon_clear);
		}
		_right.setBounds(0, 0, _right.getIntrinsicWidth(),
				_right.getIntrinsicHeight());
		setCompoundDrawablePadding((int) getResources().getDimension(
				R.dimen.margin_micro));
		super.setOnFocusChangeListener(this);
		super.setOnTouchListener(this);
		addTextChangedListener(this);

	}

	/**
	 * 设置clearIcon可见性
	 * 
	 * @param visible
	 */
	private void setClearIconVisible(boolean visible) {
		Drawable temp = visible ? _right : null;
		Drawable[] drawables = getCompoundDrawables();
		setCompoundDrawables(drawables[0], drawables[1], temp, drawables[3]);
	}

	/**
	 * 获得除去分割符的输入框内容
	 */
	public String getNonSeparatorText() {
		return getText().toString().replaceAll(" ", "");
	}

	/**
	 * 
	 * @param formatStyle
	 *            PHONENUM =0手机号 CREDIT_CARD =1信用卡号 BANK_NUM= 2银行卡号 3身份证号
	 */
	public void setPatten(String separator, int formatStyle) {
		format_Style = formatStyle;
		this.separator = separator;
		if (format_Style == 0) {
			maxLength = 13;
		} else if (formatStyle == 1) {
			maxLength = 19;
		} else if (formatStyle == 2) {
			maxLength = 24;
		} else if (formatStyle == 3) {
			maxLength = 21;
		}
		// for (int i = 0; i < formatStyle.length; i++) {
		// maxLength = format_Style + formatStyle[i];
		// }
		// maxLength = maxLength + (formatStyle.length - 1);// edittext最大长度
	}
}
