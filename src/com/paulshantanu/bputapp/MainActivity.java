package com.paulshantanu.bputapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MainActivity extends ActionBarActivity implements OnRefreshListener,AsyncTaskListener {

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
		getSupportActionBar().setIcon(getResources().getDrawable(R.drawable.ic_launcher));    
		
		checkConnectivity();
	
		mSwipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipe_container);
 	    mSwipeRefreshHintLayout = (SwipeRefreshHintLayout)findViewById(R.id.swipe_hint);
 	    mSwipeRefreshHintLayout.setSwipeLayoutTarget(mSwipeRefreshLayout);
 	    mSwipeRefreshLayout.setOnRefreshListener(this);
 	    mSwipeRefreshLayout.setColorScheme(R.color.holo_blue_light,android.R.color.transparent,android.R.color.transparent,android.R.color.transparent);
        onRefresh();
    }

	@Override
	public void onRefresh() {
        progressBar.setVisibility(View.VISIBLE);
		mSwipeRefreshLayout.setRefreshing(true);
 	    mSwipeRefreshLayout.setColorScheme(R.color.transparent,R.color.transparent,R.color.transparent,R.color.transparent);
		getSupportActionBar().setTitle("Refreshing...");
		handler = new SaxParserHandler();

		new XMLParser(this, handler,null).execute("http://pauldmps.url.ph/default.php");
	}
	
	public void checkConnectivity() {	
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	    // test for internet connection
	    if (!(cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isAvailable() && cm.getActiveNetworkInfo().isConnected())){
    	AlertDialog.Builder b =new AlertDialog.Builder(this);
    	b.setTitle("No Connection");
    	b.setMessage("Cannot connect to the internet!");
    	b.setPositiveButton("Retry", new AlertDialog.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				progressBar.setVisibility(View.INVISIBLE);
	               checkConnectivity();				
			}
		});
    	b.setNegativeButton("Exit", new AlertDialog.OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
                 finish();
			}
		});
    	b.create().show();
          }
	}
	
	    @Override
		public void onTaskComplete(String result) {
	    	if(result.equals("OK")){
			 ArrayAdapter<String> adp =new ArrayAdapter<String>(MainActivity.this, 
					 android.R.layout.simple_list_item_1,
					 handler.getNotice().getNotice_head().toArray(new String[handler.getNotice().getNotice_head().size()]));
			 lv= (ListView)findViewById(R.id.lv_notices_main);
			 lv.setAdapter(adp);
   			 lv.setOnItemClickListener(new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1, int itemClicked,
							long arg3) {
						String link = handler.getNotice().getUrl().get(itemClicked).trim();
						Log.i("debug",Boolean.toString(link.endsWith("pdf")));
						if(link.endsWith("pdf")){ //If the notice is PDF, start PDF opening activity.
							Log.i("debug", "PDF link found");
							Intent pdfintent = new Intent(MainActivity.this,PdfViewerAcitvity.class);
							pdfintent.putExtra("link", link);
							startActivity(pdfintent);
						}
						else //else parse the html notice
						{
						 Intent i_notice = new Intent(MainActivity.this,NoticeAcitivity.class);
		                 i_notice.putExtra("link", link);
		                 startActivity(i_notice);
						}
					}
				});
   			    mSwipeRefreshLayout.setRefreshing(false);
                progressBar.setVisibility(View.INVISIBLE);
         	    mSwipeRefreshLayout.setColorScheme(R.color.holo_blue_light,R.color.transparent,R.color.transparent,R.color.transparent);
		        getSupportActionBar().setTitle("BPUT App");
	    	}
	    }
	}


