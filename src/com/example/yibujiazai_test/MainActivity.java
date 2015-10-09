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
 * 使用异步加载方式实现新闻列表加载
 * 涉及到一级缓存
 * lrucache
 */

public class MainActivity extends Activity {
	private ListView mListview;
	private static String URL = "http://www.imooc.com/api/teacher?type=4&num=30";// 慕课网对外提供的接口，均已json格式返回

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		// 获取listview控件
		mListview = (ListView) findViewById(R.id.tv_main);
		// 在主线程中启动AsynTask
		new NewsAsynTask().execute(URL);

	}

	// 实现网络的异步访问
	/*
	 * AsynTask 输入三个参数 params，这里需要传入url网址，因此为string类型 progress 这里不需要返回进度显示
	 * 因此为void result 为 json解析之后的数据bean的集合
	 */
	class NewsAsynTask extends AsyncTask<String, Void, List<NewsBean>> {
		// 通过获取newsbean集合得到数据传递到adapter中，这样可以显示出每个json数据

		@Override
		protected List<NewsBean> doInBackground(String... params) {

			return getJsonData(params[0]);// 这里的参数只有一个url网址
		}

		// 将生成的nesBean设置给listview
		@Override
		protected void onPostExecute(List<NewsBean> newsBeans) {

			super.onPostExecute(newsBeans);
			NewsAdapter adapter = new NewsAdapter(MainActivity.this, newsBeans,
					mListview);
			mListview.setAdapter(adapter);
		}

	}

	/*
	 * 将URL对应的json格式数据转化为所封装的newsBean
	 */
	// 获取json返回格式数据
	private List<NewsBean> getJsonData(String URL) {
		List<NewsBean> newsBeanList = new ArrayList<NewsBean>();
		try {
			String jsonString = readStream(new URL(URL).openStream());// 直接根据url获取网络数据返回inputstream类型
			JSONObject jsonObject;
			NewsBean newsBean;
			// Log.d("xsf", jsonString); //打印测试
			// 将json数据放入jsonobject中，然后通过jsonarray获取所需要的数据集合
			try {
				jsonObject = new JSONObject(jsonString);
				JSONArray jsonArray = jsonObject.getJSONArray("data");
				// 通过for循环取出jsonarray每个值，放到newsBean的集合中去
				for (int i = 0; i < jsonArray.length(); i++) {
					jsonObject = jsonArray.getJSONObject(i);
					newsBean = new NewsBean();
					newsBean.newsIconUrl = jsonObject.getString("picSmall");// 获取小图片
					newsBean.newsTitle = jsonObject.getString("name");// 获取title
					newsBean.newsContent = jsonObject.getString("description");// 获取内容
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

	// 字节流转字符流，读取函数，解析网页返回的数组
	private String readStream(InputStream is) {
		InputStreamReader isr;
		String result = "";
		try {
			String line = "";
			isr = new InputStreamReader(is, "utf-8");// 字节流封装成字符流并且指定为utf-8格式
			BufferedReader br = new BufferedReader(isr);// 将字符流通过buffer形式读取出来
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
