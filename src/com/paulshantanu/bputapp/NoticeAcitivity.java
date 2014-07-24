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

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar.OnNavigationListener;
import android.util.Log;
import android.widget.TableRow.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class NoticeAcitivity extends ActionBarActivity {

	String[] pages = {"Notices", "Results", "Calender", "Holidays", "Syllabus"};
	ProgressDialog dialog;
	SaxParserHandler notice_handler;
	StringBuffer str = new StringBuffer();
	String link;
	List<NameValuePair> nameValuePairs= new ArrayList<NameValuePair>();
	TextView main_tv;
	TableLayout tl;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_notice);
		showDropdown();
		main_tv = (TextView) findViewById(R.id.notice_text);
		tl = (TableLayout) findViewById(R.id.notice_table);
		String url = getIntent().getExtras().getString("url").trim();
		
		//check & convert relative urls to abosulte
		if (url.substring(0, 4).equals("http"))
		{
			link = url;
			
		}
		else
		{
			Log.i("link","substr: "+ url.substring(0, 4));
			str.append("http://www.bput.ac.in/");
		    str.append(url);
		    link = str.toString();
		}
	    Log.i("debug","url1: "+link); 		

		
		//add data to send to server by POST
		nameValuePairs.add(new BasicNameValuePair("url", link)); 
		new PostToServer().execute();
	}
	
	
	    // XML Parser Class
		public class PostToServer extends AsyncTask<String, Void, Void>
		{

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				dialog = new ProgressDialog(NoticeAcitivity.this);
				dialog.setMessage("Wait");
				dialog.setCancelable(false);
				dialog.show();
			}

			@Override
			protected Void doInBackground(String... params) {
				

				String url = "http://pauldmps.url.ph/notice.php";
				
				HttpClient client = new DefaultHttpClient();
				HttpPost post = new HttpPost(url);
				StringBuffer strbuf = null;	
				
				try {
					
					post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
					HttpResponse response = client.execute(post);
										
					HttpEntity entity = response.getEntity();
					
					
					BufferedReader br = new BufferedReader(new InputStreamReader(entity.getContent()));
					
					strbuf = new StringBuffer();
					String str = "";
					
					while ((str=br.readLine())!=null) {
		              				strbuf.append(str);
					}
		           
					Log.i("debug", "stream: "+ strbuf.toString());
					
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				SAXParserFactory spf = SAXParserFactory.newInstance();
				try {
					SAXParser sp = spf.newSAXParser();
					InputStream is = new ByteArrayInputStream(strbuf.toString().getBytes());
				//	Log.i("debug", "stream "+fromStream(is));
					notice_handler= new SaxParserHandler();
					sp.parse(is,notice_handler);
					
					
				} catch (Exception e) {
	                Log.i("debug","exception in parsing");
	                e.printStackTrace();
				}
				return null;	
			}
			
			
			
			@Override
			protected void onPostExecute(Void result) {
				super.onPostExecute(result);
				dialog.dismiss();
				main_tv.setText(notice_handler.getNotice().getNotice_body());
				Log.i("debug","msg body: "+notice_handler.getNotice().getNotice_body());
				if(notice_handler.getNotice().getHas_table()==true)
				{
					showTable();
				}
				
			}	
		}
	
		
		// Method to display table if one exists.
	public void showTable() {
		
		int columns = notice_handler.getNotice().getTable_head().size();
		Log.i("debug","collumns: "+columns);
		int rows = (notice_handler.getNotice().getTable_body().size())/columns;
		int counter =0;
            
		TableRow head_tr = new TableRow(this);
		head_tr.setId(0);
        head_tr.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
		
            for(int i=0;i<columns;i++)
            {
            	TextView tv = new TextView(this);
            	tv.setId(200+i);
                tv.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
                tv.setSingleLine(false);
            	tv.setText(notice_handler.getNotice().getTable_head().get(i));
            	head_tr.addView(tv);
            	
            }
            tl.addView(head_tr, new TableLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));


    		for(int i=0;i<rows-1;i++)
    		{
    			// Create table rows
    			TableRow tr = new TableRow(this);
    			tr.setId(100+i);
                //tr.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.MATCH_PARENT));
                
                for(int j=0;j<columns;j++)
                {
                	TextView tv = new TextView(this);
                	tv.setId(200+i);
                    tv.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
                    tv.setSingleLine(false);
                	tv.setText(notice_handler.getNotice().getTable_body().get(counter));
                	tr.addView(tv);
                	counter++;
                }
                tl.addView(tr, new TableLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));
                //tl.setVisibility(View.VISIBLE);
		    }
		
    		
	}
	
	
	// Method to show dropdown in action bar
		public void showDropdown() {
			
			ArrayAdapter<String> dropdown_adapter = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_list_item_1,pages);
			getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
			ActionBar.OnNavigationListener navigation_listener = new OnNavigationListener() {
				
				@Override
				public boolean onNavigationItemSelected(int itemPosition, long itemId) {
	                    //Todo later
	             //   Toast.makeText(getBaseContext(), "You selected" + pages[itemPosition], Toast.LENGTH_LONG).show();
					return false;
				}
				
			};
			getSupportActionBar().setListNavigationCallbacks(dropdown_adapter, navigation_listener);
			
		}
	
}
