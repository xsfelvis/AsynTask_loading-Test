package com.example.yibujiazai_test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

/*
 * ʹ���첽���ط�ʽʵ�������б����
 * �漰��һ������
 * lrucache
 */

public class MainActivity extends Activity {
	private ListView mListview;
	private static String URL = "http://www.imooc.com/api/teacher?type=4&num=30";// Ľ���������ṩ�Ľӿڣ�����json��ʽ����

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		// ��ȡlistview�ؼ�
		mListview = (ListView) findViewById(R.id.tv_main);
		// �����߳�������AsynTask
		new NewsAsynTask().execute(URL);

	}

	// ʵ��������첽����
	/*
	 * AsynTask ������������ params��������Ҫ����url��ַ�����Ϊstring���� progress ���ﲻ��Ҫ���ؽ�����ʾ
	 * ���Ϊvoid result Ϊ json����֮�������bean�ļ���
	 */
	class NewsAsynTask extends AsyncTask<String, Void, List<NewsBean>> {
		// ͨ����ȡnewsbean���ϵõ����ݴ��ݵ�adapter�У�����������ʾ��ÿ��json����

		@Override
		protected List<NewsBean> doInBackground(String... params) {

			return getJsonData(params[0]);// ����Ĳ���ֻ��һ��url��ַ
		}

		// �����ɵ�nesBean���ø�listview
		@Override
		protected void onPostExecute(List<NewsBean> newsBeans) {

			super.onPostExecute(newsBeans);
			NewsAdapter adapter = new NewsAdapter(MainActivity.this, newsBeans,
					mListview);
			mListview.setAdapter(adapter);
		}

	}

	/*
	 * ��URL��Ӧ��json��ʽ����ת��Ϊ����װ��newsBean
	 */
	// ��ȡjson���ظ�ʽ����
	private List<NewsBean> getJsonData(String URL) {
		List<NewsBean> newsBeanList = new ArrayList<NewsBean>();
		try {
			String jsonString = readStream(new URL(URL).openStream());// ֱ�Ӹ���url��ȡ�������ݷ���inputstream����
			JSONObject jsonObject;
			NewsBean newsBean;
			// Log.d("xsf", jsonString); //��ӡ����
			// ��json���ݷ���jsonobject�У�Ȼ��ͨ��jsonarray��ȡ����Ҫ�����ݼ���
			try {
				jsonObject = new JSONObject(jsonString);
				JSONArray jsonArray = jsonObject.getJSONArray("data");
				// ͨ��forѭ��ȡ��jsonarrayÿ��ֵ���ŵ�newsBean�ļ�����ȥ
				for (int i = 0; i < jsonArray.length(); i++) {
					jsonObject = jsonArray.getJSONObject(i);
					newsBean = new NewsBean();
					newsBean.newsIconUrl = jsonObject.getString("picSmall");// ��ȡСͼƬ
					newsBean.newsTitle = jsonObject.getString("name");// ��ȡtitle
					newsBean.newsContent = jsonObject.getString("description");// ��ȡ����
					newsBeanList.add(newsBean);
				}

			} catch (JSONException e) {

				e.printStackTrace();
			}

		} catch (IOException e) {

			e.printStackTrace();
		}

		return newsBeanList;
	}

	// �ֽ���ת�ַ�������ȡ������������ҳ���ص�����
	private String readStream(InputStream is) {
		InputStreamReader isr;
		String result = "";
		try {
			String line = "";
			isr = new InputStreamReader(is, "utf-8");// �ֽ�����װ���ַ�������ָ��Ϊutf-8��ʽ
			BufferedReader br = new BufferedReader(isr);// ���ַ���ͨ��buffer��ʽ��ȡ����
			while ((line = br.readLine()) != null) {
				result += line;
			}

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}
}
