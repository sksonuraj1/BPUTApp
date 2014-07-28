package com.paulshantanu.bputapp;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.TableRow.LayoutParams;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class NoticeAcitivity extends ActionBarActivity implements AsyncTaskListener{

	ButteryProgressBar progressBar;
	SaxParserHandler notice_handler;
	StringBuffer str = new StringBuffer();
	String url;
	TextView main_tv;
	TableLayout tl;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_notice);
		main_tv = (TextView) findViewById(R.id.notice_text);
		tl = (TableLayout) findViewById(R.id.notice_table);
		progressBar = ButteryProgressBar.getInstance(NoticeAcitivity.this);
		notice_handler = new SaxParserHandler();

		String link = getIntent().getExtras().getString("link").trim();
		
		//check & convert relative urls to abosulte
		if (link.substring(0, 4).equals("http")){
			url = link;			
		}
		else{
			str.append("http://www.bput.ac.in/");
		    str.append(link);
		    url = str.toString();
		}

		new XMLParser(this, notice_handler,url).execute("http://pauldmps.url.ph/notice.php");
	}
		
	 public void onTaskComplete(String result) {
		 if(result.equals("OK")){
				progressBar.setVisibility(View.INVISIBLE);
				main_tv.setText(notice_handler.getNotice().getNotice_body());
				if(notice_handler.getNotice().getHas_table()==true)
					showTable();
			}
	 }
		
		
		// Method to display table if one exists.
	public void showTable() {	
		int columns = notice_handler.getNotice().getTable_head().size();
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


    		for(int i=0;i<rows-1;i++){
    			// Create table rows
    			TableRow tr = new TableRow(this);
    			tr.setId(100+i);
                
                for(int j=0;j<columns;j++){
                	TextView tv = new TextView(this);
                	tv.setId(200+i);
                    tv.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
                    tv.setSingleLine(false);
                	tv.setText(notice_handler.getNotice().getTable_body().get(counter));
                	tr.addView(tv);
                	counter++;
                }
                tl.addView(tr, new TableLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));
    		}		
	}
}
