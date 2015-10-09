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
	private LayoutInflater mInflater;// ��Ҫfת������
	private ImageLoadr mImageloader;
	private int mStart, mEnd;
	public static String[] URLS;// ���浱ǰ��ȡ������ͼƬ��url
	// ����flag�жϵ�ǰ�Ƿ��ǵ�һ������
	private boolean mFirrstIn;

	// ���췽��
	public NewsAdapter(Context context, List<NewsBean> data, ListView listview) {
		mList = data;
		mInflater = LayoutInflater.from(context);
		mImageloader = new ImageLoadr(listview);// ��ֻ֤��һ��imageloader
		URLS = new String[data.size()];
		for (int i = 0; i < data.size(); i++) {
			URLS[i] = data.get(i).newsIconUrl;
		}
		// ע��listview�󶨼����¼�,��������
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
		// viewholder��ʽ
		ViewHolder viewholder = null;
		if (convertView == null) {
			viewholder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.item_layout, null);
			// ��viewholderԪ�ؽ��г�ʼ��
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
		 * ��ֹlistview����ͼƬ���ִ�λ���ǳ���Ҫ �������д������Ҫ�������imageLoader��ȡͼƬ��Ҫ�����ж�
		 */
		String url = mList.get(position).newsIconUrl;
		viewholder.ivIcon.setTag(url);// ��ͼƬ�Ͷ�Ӧ��url���а�

		// ʹ�ö��̷߳�ʽ����ʵ��ͼƬ
		/*
		 * new ImageLoadr().showImageByThread(viewholder.ivIcon, url);
		 */

		// ʹ��AsyncTask����ʵ��ͼƬ
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
		// listView����״̬�л���ʱ��ŵ���
		// �ж�listview����״̬
		if (scrollState == SCROLL_STATE_IDLE) {
			// ����ֹͣ״̬�����ؿɼ���
			mImageloader.loadImages(mStart, mEnd);

		} else {
			// ֹͣ����
			mImageloader.cancelAllTask();
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		// �����������̶������,���ϻ�ȡ��ǰ�ɼ���Ŀ�����һ���ɼ���Ŀ
		mStart = firstVisibleItem;
		mEnd = firstVisibleItem + visibleItemCount;
		if (mFirrstIn && visibleItemCount > 0) {
			// �ֶ����ص�һ��
			mImageloader.loadImages(mStart, mEnd);
			mFirrstIn = false;
		}

	}

}
