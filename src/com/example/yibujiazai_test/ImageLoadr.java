package com.example.yibujiazai_test;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v4.util.LruCache;
import android.widget.ImageView;

/*
 * 图片加载
 * 从一个url中通过异步线程的方式将bitmap解析出来
 * 并将这个bitmap以消息方式发送给主线程的handler
 * 然后在主线程中设置给ui线程的imageView
 */
public class ImageLoadr {
	private ImageView mImageView;
	private String mUrl;
	// 使用LRU算法
	private LruCache<String, Bitmap> mCaches;

	public ImageLoadr() {
		// 获取最大可使用内存
		int MaxMemory = (int) Runtime.getRuntime().maxMemory();
		// 设置所需缓存大小
		int cacheSize = MaxMemory / 4;
		mCaches = new LruCache<String, Bitmap>(cacheSize) {
			@Override
			protected int sizeOf(String key, Bitmap value) {
				// 在每次存入缓存时候调用，告诉系统传入对象的大小
				return value.getByteCount();
			}
		};
	}

	// 增加到缓存
	public void addBitmapTocache(String url, Bitmap bitmap) {
		if (getBitmapFromCache(url) == null) {
			// 判断当前是否存在url所指定的图片
			mCaches.put(url, bitmap);
		}
	}

	// 从缓存中获取数据
	public Bitmap getBitmapFromCache(String url) {
		return mCaches.get(url);

	}

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			/*
			 * 由于在newsAdapter中对图片进行了setTag，因此在获取图片时需要进行判断
			 */
			if (mImageView.getTag().equals(mUrl)) {
				// 避免缓存图片对正确图片产生的图片错位的影响
				mImageView.setImageBitmap((Bitmap) msg.obj);// 显示对应url所展现的图片
			}

		}
	};

	public Bitmap getBitMapFromURL(String URLString) {
		Bitmap bitmap;
		InputStream is = null;
		try {
			URL url = new URL(URLString);
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();
			is = new BufferedInputStream(connection.getInputStream());
			bitmap = BitmapFactory.decodeStream(is);
			// 释放链接资源
			connection.disconnect();
			return bitmap;
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (java.io.IOException e) {

			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;

	}

	/************* 方法一：通过多线程加载图片 ***************/
	public void showImageByThread(ImageView imageView, final String url) {
		mImageView = imageView;
		mUrl = url;

		new Thread() {
			public void run() {
				super.run();
				Bitmap bitmap = getBitMapFromURL(url);
				Message message = Message.obtain();
				message.obj = bitmap;
				mHandler.sendMessage(message);// 将message发送出去
			}
		}.start();
	}

	/************************************************************************/
	/******************* 方法二：通过Asyntask方式加载图片 **********************************/
	public void showImageByAsyncTask(ImageView imageView, String url) {
		Bitmap bitmap = getBitmapFromCache(url);
		if (bitmap == null) {
			// 缓存中没有该图片则直接加载
			new ImageLoaderAsynTask(imageView, url).execute(url);
		} else {
			// 缓存中有该图片直接加载
			imageView.setImageBitmap(bitmap);
		}

	}

	private class ImageLoaderAsynTask extends AsyncTask<String, Void, Bitmap> {
		private ImageView mImageView;
		// 防止listview图片加载错位做的处理
		private String mUrl;

		public ImageLoaderAsynTask(ImageView imageView, String url) {
			mImageView = imageView;
			mUrl = url;
		}

		@Override
		protected Bitmap doInBackground(String... params) {
			String url = params[0];
			// 从网络中获取图片
			Bitmap bitmap = getBitMapFromURL(url);
			if (bitmap != null) {
				addBitmapTocache(url, bitmap);
			}
			return bitmap;
		}

		@Override
		protected void onPostExecute(Bitmap bitmap) {

			super.onPostExecute(bitmap);
			// 设置图片时增加判断防止listview加载图片错位
			if (mImageView.getTag().equals(mUrl)) {
				mImageView.setImageBitmap(bitmap);
			}
		}

	}
}
