package com.paulshantanu.bputapp;

/*
 * This activity downloads and displays those notices which are in PDF format. 
 * This activity uses PDF.js javascript library to display the PDF inside a WebView "webView".
 * 
 * PDF.js is licensed under the APACHE-V2 License. Copyright Mozilla Foundation, all rights reserved.
 */

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

@SuppressLint("NewApi")
public class PdfViewerAcitvity extends Activity {
	private WebView webView;
    ButteryProgressBar progressBar;
    StringBuffer str = new StringBuffer();
	String url;
	Uri path;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pdf_notice);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setSubtitle("View Notice");
		
		String link = getIntent().getExtras().getString("link");
		Log.i("debug", "pdfintent: "+link);
		
		url = URLDecoder.getDecodedUrl(link);
		
		
		//check & convert relative urls to abosulte
//				if (link.substring(0, 4).equals("http")){
//						url = link;
//				}
//				else{
//					str.append("http://www.bput.ac.in/");
//				    str.append(link); 
//					url = str.toString();
//				}
				
	    //url = url.replaceAll(" ", "%20"); //Replace spaces with "%20" in the URL
		
	    progressBar = ButteryProgressBar.getInstance(PdfViewerAcitvity.this);
        progressBar.setVisibility(View.VISIBLE);
		
		webView = (WebView) findViewById(R.id.notice_view);
		webView.setVisibility(View.INVISIBLE);
		WebSettings settings = webView.getSettings();
		settings.setJavaScriptEnabled(true);

		if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.JELLY_BEAN){ //required for running javascript on android 4.1 or later
		settings.setAllowFileAccessFromFileURLs(true);
		settings.setAllowUniversalAccessFromFileURLs(true);
		}
		settings.setBuiltInZoomControls(true);
		webView.setWebChromeClient(new WebChromeClient());
	
	    new DownloadTask(PdfViewerAcitvity.this).execute(url);
	}

		
	private class DownloadTask extends AsyncTask<String, Integer, String>{	
		private Context context;
	    private PowerManager.WakeLock mWakeLock;

	    public DownloadTask(Context context) {
	        this.context = context;
	    }
		

		@Override
		protected void onPreExecute() {
		   super.onPreExecute();
		   PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
	        mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
	             getClass().getName());
	        mWakeLock.acquire();
		   progressBar.setVisibility(View.VISIBLE);
		}

		@Override
		protected String doInBackground(String... sUrl) {       
			InputStream input = null;
	        OutputStream output = null;
	        HttpURLConnection connection = null;
	        try {
	            URL url = new URL(sUrl[0]);
	            connection = (HttpURLConnection) url.openConnection();
	            connection.connect();
	            
	            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) { //error, not HTTP 200- OK
	                return "Server returned HTTP " + connection.getResponseCode()
	                        + " " + connection.getResponseMessage();
	            }
	            
	            int fileLength = connection.getContentLength();
	            input = connection.getInputStream();
	            output = openFileOutput("notice.pdf", Context.MODE_PRIVATE);
	            byte data[] = new byte[4096];
	            long total = 0;
	            int count;
	            while ((count = input.read(data)) != -1) {
	                // allow canceling with back button
	                if (isCancelled()) {
	                    input.close();
	                    return null;
	                }
	                total += count;
	                // publishing the progress....
	                if (fileLength > 0) // only if total length is known
	                    publishProgress((int) (total * 100 / fileLength));
	                output.write(data, 0, count);
	                }
	        }
			catch(Exception e){
				e.printStackTrace();
			}
	        finally {
	        	try {
	                if (output != null)
	                    output.close();
	                if (input != null)
	                    input.close();
	            } catch (IOException ignored) {}
	            if (connection != null)
	                connection.disconnect();
	        }
			return null;
		}
		
		@Override
		protected void onProgressUpdate(Integer... progress) {
			super.onProgressUpdate(progress);
			getActionBar().setSubtitle("Loading " + progress[0] + "%");
		}
		
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			mWakeLock.release();
//			if (result != null)
//	            Toast.makeText(context,"Download error: "+result, Toast.LENGTH_LONG).show();
//	        else
//	            Toast.makeText(context,"File downloaded", Toast.LENGTH_SHORT).show();
			
			path = Uri.parse(context.getFilesDir().toString()+ "/notice.pdf");
	        webView.loadUrl("file:///android_asset/pdfviewer/index.html?file=" + path);
	        webView.setWebViewClient(new WebViewClient()
	        {
	        	@Override
	        	public void onPageFinished(WebView view, String url) {
	        		super.onPageFinished(view, url);
	        	
	        		 webView.setVisibility(View.VISIBLE);
	     			progressBar.setVisibility(View.INVISIBLE);
	     			getActionBar().setSubtitle("View Notice");		
	        		
	        	}
	        	
	        });
	       
		}
	}
	
	
	@Override
	protected void onResume() {
		super.onResume();
		webView.loadUrl( "javascript:window.location.reload( true )" );
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		webView.clearCache(true);
	}
	
	protected void  onDestroy() {
		super.onDestroy();
		webView.clearCache(true);
		File file = new File(getFilesDir(), "notice.pdf");
        file.delete();			
	}	

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	super.onCreateOptionsMenu(menu);
    	MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

    	switch (item.getItemId()) {
		case R.id.action_settings:		
				
			return true;
			
		case R.id.about:	
             AlertDialog.Builder b = new AlertDialog.Builder(this);
             b.setTitle("About");
             
             WebView about_view = new WebView(this);
             about_view.loadUrl("file:///android_asset/about.htm");
             
             b.setView(about_view);
             b.setPositiveButton("OK", null);
             b.create().show();
			return true;
			
		case android.R.id.home:
			 NavUtils.navigateUpFromSameTask(this);
		        return true;

		default:
	    	return super.onOptionsItemSelected(item);
		}
    }

}


	

