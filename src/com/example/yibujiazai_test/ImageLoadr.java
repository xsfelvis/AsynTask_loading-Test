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
 * ͼƬ����
 * ��һ��url��ͨ���첽�̵߳ķ�ʽ��bitmap��������
 * �������bitmap����Ϣ��ʽ���͸����̵߳�handler
 * Ȼ�������߳������ø�ui�̵߳�imageView
 */
public class ImageLoadr {
	private ImageView mImageView;
	private String mUrl;
	// ʹ��LRU�㷨
	private LruCache<String, Bitmap> mCaches;

	public ImageLoadr() {
		// ��ȡ����ʹ���ڴ�
		int MaxMemory = (int) Runtime.getRuntime().maxMemory();
		// �������軺���С
		int cacheSize = MaxMemory / 4;
		mCaches = new LruCache<String, Bitmap>(cacheSize) {
			@Override
			protected int sizeOf(String key, Bitmap value) {
				// ��ÿ�δ��뻺��ʱ����ã�����ϵͳ�������Ĵ�С
				return value.getByteCount();
			}
		};
	}

	// ���ӵ�����
	public void addBitmapTocache(String url, Bitmap bitmap) {
		if (getBitmapFromCache(url) == null) {
			// �жϵ�ǰ�Ƿ����url��ָ����ͼƬ
			mCaches.put(url, bitmap);
		}
	}

	// �ӻ����л�ȡ����
	public Bitmap getBitmapFromCache(String url) {
		return mCaches.get(url);

	}

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			/*
			 * ������newsAdapter�ж�ͼƬ������setTag������ڻ�ȡͼƬʱ��Ҫ�����ж�
			 */
			if (mImageView.getTag().equals(mUrl)) {
				// ���⻺��ͼƬ����ȷͼƬ������ͼƬ��λ��Ӱ��
				mImageView.setImageBitmap((Bitmap) msg.obj);// ��ʾ��Ӧurl��չ�ֵ�ͼƬ
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
			// �ͷ�������Դ
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

	/************* ����һ��ͨ�����̼߳���ͼƬ ***************/
	public void showImageByThread(ImageView imageView, final String url) {
		mImageView = imageView;
		mUrl = url;

		new Thread() {
			public void run() {
				super.run();
				Bitmap bitmap = getBitMapFromURL(url);
				Message message = Message.obtain();
				message.obj = bitmap;
				mHandler.sendMessage(message);// ��message���ͳ�ȥ
			}
		}.start();
	}

	/************************************************************************/
	/******************* ��������ͨ��Asyntask��ʽ����ͼƬ **********************************/
	public void showImageByAsyncTask(ImageView imageView, String url) {
		Bitmap bitmap = getBitmapFromCache(url);
		if (bitmap == null) {
			// ������û�и�ͼƬ��ֱ�Ӽ���
			new ImageLoaderAsynTask(imageView, url).execute(url);
		} else {
			// �������и�ͼƬֱ�Ӽ���
			imageView.setImageBitmap(bitmap);
		}

	}

	private class ImageLoaderAsynTask extends AsyncTask<String, Void, Bitmap> {
		private ImageView mImageView;
		// ��ֹlistviewͼƬ���ش�λ���Ĵ���
		private String mUrl;

		public ImageLoaderAsynTask(ImageView imageView, String url) {
			mImageView = imageView;
			mUrl = url;
		}

		@Override
		protected Bitmap doInBackground(String... params) {
			String url = params[0];
			// �������л�ȡͼƬ
			Bitmap bitmap = getBitMapFromURL(url);
			if (bitmap != null) {
				addBitmapTocache(url, bitmap);
			}
			return bitmap;
		}

		@Override
		protected void onPostExecute(Bitmap bitmap) {

			super.onPostExecute(bitmap);
			// ����ͼƬʱ�����жϷ�ֹlistview����ͼƬ��λ
			if (mImageView.getTag().equals(mUrl)) {
				mImageView.setImageBitmap(bitmap);
			}
		}

	}
}
