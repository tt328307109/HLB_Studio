package com.lk.qf.pay.wedget.view;

import java.util.ArrayList;

import com.lk.bhb.pay.R;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class PassWdDialog extends Dialog implements View.OnClickListener{

	Button iv1 , iv2 , iv3 ,iv4 , iv5 , iv6 ,iv7 ,iv8 , iv9 , iv0 , ivdel;
	TextView paybox1 ,paybox2 ,paybox3 ,paybox4 ,paybox5 ,paybox6 , pay_cancel , pay_sure , pay_content;
	String money , mark;
	ImageButton imgbtn_pwd_close;
	public static PassWdDialog mPassWdDialog;
	ArrayList<String> pwd;
	public static String YUAN_MARK="¥";
	public static String KUAIYIBI_MARK="快易币:";
	public static String INDIANA_MARK="夺宝币:";
	public PassWdDialog(Context context, int theme) {
		super(context, R.style.FullScreenDialogAct);
		init();
	}


	private PassWdDialog(Context context , String money , String mark) {
		super(context, R.style.FullScreenDialogActPay);
		this.money = money;
		this.mark = mark;
		init();
	}
	public static PassWdDialog getInstance(Context context , String money ,String mark) {
		
		return new PassWdDialog(context , money , mark) ;
	}

	private void init() {
//		requestWindowFeature(Window.FEATURE_NO_TITLE);15923254686
		pwd = new ArrayList<String>();
		setPadding();
		setContentView(R.layout.item_paypassword);
		initView();
		
	}


	private void setPadding() {
		Window win = PassWdDialog.this.getWindow();
		win.getDecorView().setPadding(0, 0, 0, 0);
		WindowManager.LayoutParams lp = win.getAttributes();
		        lp.width = WindowManager.LayoutParams.FILL_PARENT;
		        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
		        win.setAttributes(lp);
	}


	private void initView() {
		pay_content = (TextView) findViewById(R.id.pay_content);
		iv1 = (Button) findViewById(R.id.pay_keyboard_one);
		iv2 = (Button) findViewById(R.id.pay_keyboard_two);
		iv3 = (Button) findViewById(R.id.pay_keyboard_three);
		iv4 = (Button) findViewById(R.id.pay_keyboard_four);
		iv5 = (Button) findViewById(R.id.pay_keyboard_five);
		iv6 = (Button) findViewById(R.id.pay_keyboard_sex);
		iv7 = (Button) findViewById(R.id.pay_keyboard_seven);
		iv8 = (Button) findViewById(R.id.pay_keyboard_eight);
		iv9 = (Button) findViewById(R.id.pay_keyboard_nine);
		iv0 = (Button) findViewById(R.id.pay_keyboard_zero);
		ivdel = (Button) findViewById(R.id.pay_keyboard_del);
		imgbtn_pwd_close = (ImageButton) findViewById(R.id.imgbtn_pwd_close);
		
		paybox1 = (TextView) findViewById(R.id.pay_box1);
		paybox2 = (TextView) findViewById(R.id.pay_box2);
		paybox3 = (TextView) findViewById(R.id.pay_box3);
		paybox4 = (TextView) findViewById(R.id.pay_box4);
		paybox5 = (TextView) findViewById(R.id.pay_box5);
		paybox6 = (TextView) findViewById(R.id.pay_box6);
		pay_cancel = (TextView) findViewById(R.id.pay_cancel);
		pay_sure = (TextView) findViewById(R.id.pay_sure);

		iv1.setOnClickListener(this);
		iv2.setOnClickListener(this);
		iv3.setOnClickListener(this);
		iv4.setOnClickListener(this);
		iv5.setOnClickListener(this);
		iv6.setOnClickListener(this);
		iv7.setOnClickListener(this);
		iv8.setOnClickListener(this);
		iv9.setOnClickListener(this);
		iv0.setOnClickListener(this);
		ivdel.setOnClickListener(this);
		pay_cancel.setOnClickListener(this);
		pay_sure.setOnClickListener(this);

		pay_content.setText(mark + money);
		
		imgbtn_pwd_close.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				paybox1.setText(null);
				paybox2.setText(null);
				paybox3.setText(null);
				paybox4.setText(null);
				paybox5.setText(null);
				paybox6.setText(null);
				pwd.clear();
				PassWdDialog.this.dismiss();
				mPayListener.close();
			}
		});
		ivdel.setOnLongClickListener(new View.OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View arg0) {
				pwd.clear();
				paybox1.setText(null);
				paybox2.setText(null);
				paybox3.setText(null);
				paybox4.setText(null);
				paybox5.setText(null);
				paybox6.setText(null);
				return true;
			}
		});
		ivdel.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(pwd.size() == 0) {
					return;
				}
				if(pwd.size() == 1) {
					paybox1.setText(null);
				}
				if(pwd.size() == 2) {
					paybox2.setText(null);
				}
				if(pwd.size() == 3) {
					paybox3.setText(null);
				}
				if(pwd.size() == 4) {
					paybox4.setText(null);
				}
				if(pwd.size() == 5) {
					paybox5.setText(null);
				}
				if(pwd.size() == 6) {
					paybox6.setText(null);
				}
				
				pwd.remove(pwd.size() -1 );
				
			}
		});
		pay_cancel.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				paybox1.setText(null);
				paybox2.setText(null);
				paybox3.setText(null);
				paybox4.setText(null);
				paybox5.setText(null);
				paybox6.setText(null);
				mPayListener.cacel();
				pwd.clear();
				PassWdDialog.this.dismiss();
			}
		});
		
		pay_sure.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if(pwd.size() != 6) {
					return;
				}
				StringBuilder sb= new StringBuilder();
				for(int i = 0 ; i< pwd.size() ; i++){
				    sb.append(pwd.get(i));

				}
				String pwdS = new String(sb);
				mPayListener.sure(pwdS);
				pwd.clear();
				paybox1.setText(null);
				paybox2.setText(null);
				paybox3.setText(null);
				paybox4.setText(null);
				paybox5.setText(null);
				paybox6.setText(null);
			}
		});


	}


	@Override
	public void onClick(View v) {
		if(pwd.size() == 0) {
			paybox1.setText("*");
		}
		if(pwd.size() == 1) {
			paybox2.setText("*");
		}
		if(pwd.size() == 2) {
			paybox3.setText("*");
		}
		if(pwd.size() == 3) {
			paybox4.setText("*");
		}
		if(pwd.size() == 4) {
			paybox5.setText("*");
		}
		if(pwd.size() == 5) {
			paybox6.setText("*");
		}
		switch (v.getId()) {
		case R.id.pay_keyboard_one:
			if(pwd.size() < 6) {
				pwd.add("1");
			}
			break;
		case R.id.pay_keyboard_two:
			if(pwd.size() < 6) {
				pwd.add("2");
			}
			break;
		case R.id.pay_keyboard_three:
			if(pwd.size() < 6) {
				pwd.add("3");
			}
			break;
		case R.id.pay_keyboard_four:
			if(pwd.size() < 6) {
				pwd.add("4");
			}
			break;
		case R.id.pay_keyboard_five:
			if(pwd.size() < 6) {
				pwd.add("5");
			}
			break;
		case R.id.pay_keyboard_sex:
			if(pwd.size() < 6) {
				pwd.add("6");
			}
			break;
		case R.id.pay_keyboard_seven:
			if(pwd.size() < 6) {
				pwd.add("7");
			}
			break;
		case R.id.pay_keyboard_eight:
			if(pwd.size() < 6) {
				pwd.add("8");
			}
			break;
		case R.id.pay_keyboard_nine:
			if(pwd.size() < 6) {
				pwd.add("9");
			}
			break;
		case R.id.pay_keyboard_zero:
			if(pwd.size() < 6) {
				pwd.add("0");
			}
			break;
		default:

			break;
		}
	}


	public void setPayListener(PayListener mPayListener) {
		this.mPayListener = mPayListener;
	}

	PayListener mPayListener;
}
