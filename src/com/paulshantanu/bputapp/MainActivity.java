package com.paulshantanu.bputapp;


import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import com.paulshantanu.bputapp.ButteryProgressBar;
import com.paulshantanu.bputapp.R;
import com.paulshantanu.bputapp.SwipeRefreshHintLayout;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.OnNavigationListener;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;

public class MainActivity extends ActionBarActivity implements OnRefreshListener {

	String[] pages = {"Notices", "Results", "Calender", "Holidays", "Syllabus"};
	SaxParserHandler handler;
	ListView lv;
	SwipeRefreshLayout mSwipeRefreshLayout;
	SwipeRefreshHintLayout mSwipeRefreshHintLayout;
    ButteryProgressBar progressBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main); 
		
		progressBar = ButteryProgressBar.getInstance(MainActivity.this);
	
		mSwipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipe_container);
 	    mSwipeRefreshHintLayout = (SwipeRefreshHintLayout)findViewById(R.id.swipe_hint);
 	    mSwipeRefreshHintLayout.setSwipeLayoutTarget(mSwipeRefreshLayout);
 	    mSwipeRefreshLayout.setOnRefreshListener(this);
 	    mSwipeRefreshLayout.setColorScheme(R.color.holo_blue_light,android.R.color.transparent,android.R.color.transparent,android.R.color.transparent);

		
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	    // test for internet connection
	    if (cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isAvailable() && cm.getActiveNetworkInfo().isConnected()) 
	    
	    {
	    	
        onRefresh(); //call the refresh method to load the listview
       
	    }
    else
    {
    	AlertDialog.Builder b =new AlertDialog.Builder(this);
    	b.setTitle("No Connection");
    	b.setMessage("Cannot connect to the internet! Further updates will be unavailable.");
    	b.setPositiveButton("OK", new AlertDialog.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				progressBar.setVisibility(View.INVISIBLE);
			}
		});
    	
    	b.create().show();
    }
	    
	   
		/*
		final FrameLayout decorView = (FrameLayout) MainActivity.this.getWindow().getDecorView();
		decorView.addView(progressBar);
        final View contentView = decorView.findViewById(android.R.id.content);

		ViewTreeObserver observer = progressBar.getViewTreeObserver();
		observer.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
		    @Override
		    public void onGlobalLayout() {
		        progressBar.setY(contentView.getY());
		        ViewTreeObserver observer = progressBar.getViewTreeObserver();
		        observer.removeGlobalOnLayoutListener(this);
		    }
		}); */
		
	    
	    
    }

	@Override
	public void onRefresh() {
        progressBar.setVisibility(View.VISIBLE);
		mSwipeRefreshLayout.setRefreshing(true);
 	    mSwipeRefreshLayout.setColorScheme(R.color.transparent,android.R.color.transparent,android.R.color.transparent,android.R.color.transparent);
        getSupportActionBar().setIcon(android.R.color.transparent);
		getSupportActionBar().setTitle("Refreshing...");
    	new PostToServer().execute(); //execute XML parsing
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	
	// Method to show dropdown in action bar
	public void showDropdown() {
		
		ArrayAdapter<String> dropdown_adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1,pages);
		getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		ActionBar.OnNavigationListener navigation_listener = new OnNavigationListener() {
			
			@Override
			public boolean onNavigationItemSelected(int itemPosition, long itemId) {
				
             //   Toast.makeText(getBaseContext(), "You selected" + pages[itemPosition], Toast.LENGTH_LONG).show();
				return false;
			}
			
		};
		getSupportActionBar().setListNavigationCallbacks(dropdown_adapter, navigation_listener);
		getSupportActionBar().setIcon(getResources().getDrawable(R.drawable.ic_launcher));
		
	}
	
	// XML Parser Class
	public class PostToServer extends AsyncTask<String, Void, Void>
	{

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(String... params) {
			

			String url = "http://pauldmps.url.ph/default.php";
			
			HttpClient client = new DefaultHttpClient();
			HttpPost post = new HttpPost(url);
			StringBuffer strbuf = null;	
			
			try {
				HttpResponse response = client.execute(post);
				Log.i("debug",response.toString());
				
				HttpEntity entity = response.getEntity();
				Log.i("debug",entity.toString());
				
				BufferedReader br = new BufferedReader(new InputStreamReader(entity.getContent()));
				
				strbuf = new StringBuffer();
				String str = "";
				
				while ((str=br.readLine())!=null) {
	              				strbuf.append(str);
	         //   Log.i("debug","strbuf "+strbuf.toString());
				}
				
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			SAXParserFactory spf = SAXParserFactory.newInstance();
			try {
				SAXParser sp = spf.newSAXParser();
				InputStream is = new ByteArrayInputStream(strbuf.toString().getBytes());
		//		Log.i("debug", "stream "+fromStream(is));
				handler = new SaxParserHandler();
				sp.parse(is, handler);
				
				
			} catch (Exception e) {
                Log.i("debug","exception in parsing");
                e.printStackTrace();
			}
			return null;	
		}
		
		
		
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			// dialog.dismiss();
			 
			
			 handler.getNotice().getNotice_head().remove(handler.getNotice().getNotice_head().size()-1);
			 handler.getNotice().getNotice_head().remove(handler.getNotice().getNotice_head().size()-1);
			 
			 ArrayAdapter<String> adp =new ArrayAdapter<String>(MainActivity.this, 
					 android.R.layout.simple_list_item_1,
					 (String[]) handler.getNotice().getNotice_head().toArray(new String[handler.getNotice()
					                                                                    .getNotice_head().size()]));
			 lv= (ListView)findViewById(R.id.lv_notices_main);
			 lv.setAdapter(adp);
   			 lv.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1, int itemClicked,
							long arg3) {
						Log.i("debug","item clicked: "+itemClicked);
						 Intent i_notice = new Intent(MainActivity.this,NoticeAcitivity.class);
		                 i_notice.putExtra("url", handler.getNotice().getUrl().get(itemClicked));
		                 startActivity(i_notice);
						
					}
				});
   			    mSwipeRefreshLayout.setRefreshing(false);
                progressBar.setVisibility(View.INVISIBLE);
         	    mSwipeRefreshLayout.setColorScheme(R.color.holo_blue_light,android.R.color.transparent,android.R.color.transparent,android.R.color.transparent);
		        getSupportActionBar().setTitle("BPUT App");
		        showDropdown(); //show dropdown spinner method call
		}	 
	}
	}


