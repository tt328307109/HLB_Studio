package com.lk.qf.pay.tool;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.lk.qf.pay.golbal.Constant;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;

public class AsyncImageLoader {

	private static final String TAG = "AsyncImageLoader-------->";

	private HashMap<String, SoftReference<Drawable>> imageCache = new HashMap<String, SoftReference<Drawable>>();
	ExecutorService mExecutorSercvice = Executors.newFixedThreadPool(2);

	// private static final String ROOT_PATH = Utils.getSDCardAbsolutePath() +
	// "dragonwellp/img_temp/";

	public AsyncImageLoader() {
	}

	public Drawable loadDrawable(final String imageUrl,
			final ImageCallback imageCallback) {

		// if (imageCache.containsKey(imageUrl)) {
		// SoftReference<Drawable> softReference = imageCache.get(imageUrl);
		// Drawable drawable = softReference.get();
		// if (drawable != null) {
		// return drawable;
		// }
		// }

		final Handler handler = new Handler() {
			@Override
			public void handleMessage(Message message) {
				imageCallback.imageLoaded((Drawable) message.obj, imageUrl);
			}
		};

		Thread mThread = new Thread() {
			@Override
			public void run() {
				Bitmap bitmap = null;
				if (Constant.IS_HTTP) {
					bitmap = NetCommunicate.getBitmapFromUrl(imageUrl);
				} else {
					bitmap = NetCommunicate.https(imageUrl);
				}
				Drawable drawable = new BitmapDrawable(bitmap);
				// if (drawable != null) {
				// imageCache.put(imageUrl, new
				// SoftReference<Drawable>(drawable));
				// }
				Message message = handler.obtainMessage(0, drawable);
				handler.sendMessage(message);

			}
		};
		mExecutorSercvice.execute(mThread);
		return null;
	}

	public static Drawable loadImageFromUrl(String url) {
		URL m;
		InputStream in = null;
		try {
			m = new URL(url);
			in = (InputStream) m.getContent();
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Drawable d = Drawable.createFromStream(in, "src");
		return d;
	}

	public interface ImageCallback {
		public void imageLoaded(Drawable imageDrawable, String imageUrl);
	}

}
