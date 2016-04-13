package com.lk.qf.pay.indiana.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.lk.bhb.pay.R;
import com.lk.qf.pay.activity.BaseActivity;
import com.lk.qf.pay.golbal.MyUrls;
import com.lk.qf.pay.indiana.bean.IndianaGoodsInfo;
import com.lk.qf.pay.utils.ImgOptions;
import com.lk.qf.pay.wedget.CommonTitleBarYellow;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class WinnerGoodsActivity extends IndianaBaseActivity implements
		OnClickListener {

	private CommonTitleBarYellow title;
	private TextView tvShow;
	private ImageView img;
	private DisplayImageOptions options;
	private ImageLoader imageLoader;
	private IndianaGoodsInfo info;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.winner_goods_layout);
		title = (CommonTitleBarYellow) findViewById(R.id.titlebar_my_winner_goodsInfo);
		title.setActName("中奖商品");
		title.setCanClickDestory(this, true);
		tvShow = (TextView) findViewById(R.id.tv_winnerGoodsInfo_showMessage);
		img = (ImageView) findViewById(R.id.img_winnerGoods_icon);
		findViewById(R.id.btn_winnerGoods_queren).setOnClickListener(this);
		imageLoader = ImageLoader.getInstance();
		imageLoader.init(ImageLoaderConfiguration.createDefault(this));
		options = ImgOptions.initImgOptions();

		Intent intent = getIntent();
		if (intent != null) {
			info = intent.getParcelableExtra("info");
			String str = getResources().getString(
					R.string.tvwinner_goodsName1,info.getGoodsName());
//			String goodsName=String.format(str,info.getGoodsName());   
//			tvShow.setText(Html.fromHtml(str));
			tvShow.setText(Html.fromHtml(getHtmlString( "#616161", false,false,getResources().getString(
					R.string.tvwinner_goodsName1),false)
					+ getHtmlString("#c22d2d", false,false, info.getGoodsName(),false)
					+ getHtmlString("#616161", false,false, getResources().getString(R.string.tvwinner_goodsName2),false)));	

			
			if (info.getImgUrl() == null || info.getImgUrl().equals("")) {
//				imageLoader.displayImage(MyUrls.ROOT_URL2 + "/bank/yl.png",
//						img, options);
			} else {
				imageLoader.displayImage(MyUrls.ROOT_URL2 + info.getImgUrl(),
						img, options);
			}
		}
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		Intent intent = new Intent(WinnerGoodsActivity.this,
				MyShippingAddressActivity.class);
		intent.putExtra("info", info);
		intent.setAction("win");
		startActivity(intent);
	}
	
	public String  getHtmlString(String color , boolean isBig, boolean b,String src,boolean isBr)	
	{
		String html="";
		if (isBig)
		{
			if (b)
			{
				html="<b>"+"<big>"+"<font color=\""+color+"\">"+src+"<font/>"+"</big>"+"</b>";
			}
			else
			{
				html="<big>"+"<font color=\""+color+"\">"+src+"<font/>"+"</big>";
			}

		}
		else
		{
			if (b)
			{
				html="<b>"+"<small>"+"<font color=\""+color+"\">"+src+"<font/>"+"</small>"+"</b>";
			}
			else
			{
				html="<small>"+"<font color=\""+color+"\">"+src+"<font/>"+"</small>";
			}
		}

		if (isBr)
		{
			html=html+"<br/>";
		}

		return html;
	}


}
