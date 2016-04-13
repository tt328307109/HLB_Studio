package com.lk.qf.pay.aanewactivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lk.bhb.pay.R;
import com.lk.qf.pay.activity.BaseActivity;
import com.lk.qf.pay.wedget.CommonTitleBar;
import com.lk.qf.pay.wedget.webview.CommonLoadingComponent;

public class MyWebViewActivity extends BaseActivity implements OnClickListener {

	private WebView webView;
	// private String strTitle = "便民服务";
	private String webAddress,titleName;
	private LinearLayout llBack;
	private View layoutView;
	private CommonLoadingComponent loading;
	private Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_my_web_view);
		Intent intent = getIntent();
		if (intent != null) {
			// strTitle = intent.getStringExtra("title");
			webAddress = intent.getStringExtra("webAddress");
			titleName = intent.getStringExtra("titleName");
		} else {

			webAddress = "http://m.ctrip.com/html5";
		}
		webView = (WebView) findViewById(R.id.webview_service1);
		llBack = (LinearLayout) findViewById(R.id.common_title_back_webBack);
		llBack.setOnClickListener(this);
		loading = (CommonLoadingComponent)findViewById(R.id.loading_webview1);
		webView.getSettings().setJavaScriptEnabled(true);
		init();
	}

	private void init() {
		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setAllowFileAccess(true);
		webView.getSettings().setAllowContentAccess(true);
		String ua = webView.getSettings().getUserAgentString();
		// System.out.println("useagent=" + ua);
		webView.getSettings().setUserAgentString("");
		webView.setWebChromeClient(new WebChromeClient() {

			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				super.onProgressChanged(view, newProgress);

			}

		});
		webView.setWebViewClient(new WebViewClient() {
			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				super.onPageStarted(view, url, favicon);
				System.out.println("start....." + url);
				webView.setVisibility(View.GONE);
				loading.setIsLoading("加载中...");
				System.out.println("show...loading...");
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);

				// System.out.println("finish...." + url);
				loading.hideLayout();
				webView.setVisibility(View.VISIBLE);
			}

			@Override
			public void onReceivedError(WebView view, int errorCode,
					String description, String failingUrl) {
				super.onReceivedError(view, errorCode, description, failingUrl);
				// T.ss(description);
				// System.out.println("errorCode==" + errorCode);
				// System.out.println("description=" + description);
			}

		});
		webView.loadUrl(webAddress);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		// LayoutInflater.from(getActivity()).getMenuInflater().inflate(R.menu.my_web_view,
		// menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		String url1 = webView.getUrl();
		if (url1!=null) {
			if (url1.equals(webAddress)) {
				finish();
			} else {
				if (webView != null) {
					webView.goBack();
				}
			}
		}else{
			finish();
		}
	}

}
