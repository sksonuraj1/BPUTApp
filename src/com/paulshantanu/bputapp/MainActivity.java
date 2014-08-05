package com.paulshantanu.bputapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MainActivity extends Activity implements OnRefreshListener,AsyncTaskListener {

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
		getActionBar().setIcon(getResources().getDrawable(R.drawable.ic_launcher));    
		
		checkConnectivity();
	
		mSwipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipe_container);
 	    mSwipeRefreshHintLayout = (SwipeRefreshHintLayout)findViewById(R.id.swipe_hint);
 	    mSwipeRefreshHintLayout.setSwipeLayoutTarget(mSwipeRefreshLayout);
 	    mSwipeRefreshLayout.setOnRefreshListener(this);
 	    mSwipeRefreshLayout.setColorScheme(R.color.theme_red,android.R.color.transparent,android.R.color.transparent,android.R.color.transparent);
        onRefresh();
    }

	@Override
	public void onRefresh() {
        progressBar.setVisibility(View.VISIBLE);
		mSwipeRefreshLayout.setRefreshing(true);
 	    mSwipeRefreshLayout.setColorScheme(R.color.transparent,R.color.transparent,R.color.transparent,R.color.transparent);
		getActionBar().setTitle("Refreshing...");
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
         	    mSwipeRefreshLayout.setColorScheme(R.color.theme_red,R.color.transparent,R.color.transparent,R.color.transparent);
		        getActionBar().setTitle("BPUT App");
	    	}
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
                 
                // b.setMessage(Html.fromHtml(getResources().getString(R.string.about_string)));
                 b.setView(about_view);
                 b.setPositiveButton("OK", null);
                 b.create().show();
				return true;

			default:
		    	return super.onOptionsItemSelected(item);
			}
	    }
	}


