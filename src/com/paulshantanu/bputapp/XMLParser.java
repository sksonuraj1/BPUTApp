package com.paulshantanu.bputapp;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;


interface AsyncTaskListener{
    public void onTaskComplete(String result);
} 

public class XMLParser extends AsyncTask<String, Void, String> {
	private Context context;
	private AsyncTaskListener callback;
	private SaxParserHandler handler;
	private List<NameValuePair> nameValuePairs;

	public XMLParser(Activity activity, SaxParserHandler handler, String url) {		
		this.context = (Context)activity;
		this.handler = handler;
	    this.callback = (AsyncTaskListener)activity;
	    nameValuePairs= new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("url", url)); 
	}

	@Override
	protected String doInBackground(String... sURL) {
		String url = sURL[0];
		HttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost(url);
		StringBuffer strbuf = null;	
		
		try {
			if(url!= null){
			    post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			}
			HttpResponse response = client.execute(post);
			HttpEntity entity = response.getEntity();

			BufferedReader br = new BufferedReader(new InputStreamReader(entity.getContent()));			
			strbuf = new StringBuffer();
			String str = "";
			
			while ((str=br.readLine())!=null) {
              	strbuf.append(str);
			}
			
			
	} catch (Exception e) {
			e.printStackTrace();
		}
		
	SAXParserFactory spf = SAXParserFactory.newInstance();
	  try {
			SAXParser sp = spf.newSAXParser();
			InputStream is = new ByteArrayInputStream(strbuf.toString().getBytes());
			sp.parse(is, handler);
			return "OK";
		  }
	  catch (Exception e) {
            Log.i("debug","exception in parsing");
            e.printStackTrace();
		}
	  return "Error";
	}

	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		
		if(context.getClass() == MainActivity.class){
		handler.getNotice().getNotice_head().remove(handler.getNotice().getNotice_head().size()-1);
		handler.getNotice().getNotice_head().remove(handler.getNotice().getNotice_head().size()-1);
		}
		callback.onTaskComplete(result);
	}
	
}
