package com.example.yibujiazai_test;

import java.net.URL;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class NewsAdapter extends BaseAdapter implements OnScrollListener {
	private List<NewsBean> mList;
	private LayoutInflater mInflater;// 需要f转化布局
	private ImageLoadr mImageloader;
	private int mStart, mEnd;
	public static String[] URLS;// 保存当前获取到所有图片的url
	// 增加flag判断当前是否是第一次启动
	private boolean mFirrstIn;

	// 构造方法
	public NewsAdapter(Context context, List<NewsBean> data, ListView listview) {
		mList = data;
		mInflater = LayoutInflater.from(context);
		mImageloader = new ImageLoadr(listview);// 保证只有一个imageloader
		URLS = new String[data.size()];
		for (int i = 0; i < data.size(); i++) {
			URLS[i] = data.get(i).newsIconUrl;
		}
		// 注册listview绑定监听事件,容易忘记
		listview.setOnScrollListener(this);
		mFirrstIn = true;
	}

	@Override
	public int getCount() {

		return mList.size();
	}

	@Override
	public Object getItem(int position) {

		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {

		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// viewholder方式
		ViewHolder viewholder = null;
		if (convertView == null) {
			viewholder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.item_layout, null);
			// 对viewholder元素进行初始化
			viewholder.ivIcon = (ImageView) convertView
					.findViewById(R.id.tv_icon);
			viewholder.tvTitle = (TextView) convertView
					.findViewById(R.id.tv_title);
			viewholder.tvContent = (TextView) convertView
					.findViewById(R.id.tv_content);
			convertView.setTag(viewholder);

		} else {
			viewholder = (ViewHolder) convertView.getTag();
		}
		viewholder.ivIcon.setImageResource(R.drawable.ic_launcher);
		/*
		 * 防止listview加载图片出现错位，非常重要 以下俩行代码很重要，因此在imageLoader获取图片需要进行判断
		 */
		String url = mList.get(position).newsIconUrl;
		viewholder.ivIcon.setTag(url);// 将图片和对应的url进行绑定

		// 使用多线程方式加载实际图片
		/*
		 * new ImageLoadr().showImageByThread(viewholder.ivIcon, url);
		 */

		// 使用AsyncTask加载实际图片
		// new ImageLoadr().showImageByAsyncTask(viewholder.ivIcon, url);
		mImageloader.showImageByAsyncTask(viewholder.ivIcon, url);

		viewholder.tvTitle.setText(mList.get(position).newsTitle);
		viewholder.tvContent.setText(mList.get(position).newsContent);
		return convertView;
	}

	class ViewHolder {
		public TextView tvTitle, tvContent;
		public ImageView ivIcon;

	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// listView滑动状态切换的时候才调用
		// 判断listview滚动状态
		if (scrollState == SCROLL_STATE_IDLE) {
			// 滚动停止状态，加载可见项
			mImageloader.loadImages(mStart, mEnd);

		} else {
			// 停止任务
			mImageloader.cancelAllTask();
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		// 整个滑动过程都会调用,不断获取当前可见项目和最后一个可见项目
		mStart = firstVisibleItem;
		mEnd = firstVisibleItem + visibleItemCount;
		if (mFirrstIn && visibleItemCount > 0) {
			// 手动加载第一屏
			mImageloader.loadImages(mStart, mEnd);
			mFirrstIn = false;
		}

	}

}
