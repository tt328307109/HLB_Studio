package com.lk.qf.pay.wedget.customdialog;

import com.lk.bhb.pay.R;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class DeleteDialog extends Dialog implements android.view.View.OnClickListener{


	private TextView leftTextView, rightTextView;  
	private IDialogOnclickInterface dialogOnclickInterface;  
	private Context context;  

	public DeleteDialog(Context context, int theme) {  
		super(context, theme);  
		this.context = context;  
		// TODO Auto-generated constructor stub  
	}  

	@Override  
	protected void onCreate(Bundle savedInstanceState) {  
		// TODO Auto-generated method stub  
		super.onCreate(savedInstanceState);  
		setContentView(R.layout.layout_dialog_delete);  

		leftTextView = (TextView) findViewById(R.id.textview_one);  
		rightTextView = (TextView) findViewById(R.id.textview_two);  
		leftTextView.setOnClickListener(this);  
		rightTextView.setOnClickListener(this);  
	}  

	@Override  
	public void onClick(View v) {  
		// TODO Auto-generated method stub  
		dialogOnclickInterface = (IDialogOnclickInterface) context;  
		switch (v.getId()) {  
		case R.id.textview_one:  
			dialogOnclickInterface.leftOnclick();  
			break;  
		case R.id.textview_two:  
			dialogOnclickInterface.rightOnclick();  
			break;  
		default:  
			break;  
		}  
	}  

	public interface IDialogOnclickInterface {  
		void leftOnclick();  

		void rightOnclick();  
	}  

}
