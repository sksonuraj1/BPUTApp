package com.paulshantanu.bputapp;


/*
 * This class is used to decode the url of the notice to correct format to fetch the data..
 * Notices of the BPUT website may have the following types of URLs:
 * 1. Absolute path urls : "http://results.bput.ac.in/"
 * 2. Relative path urls : "bput_notice/2023Scholarship_25072014.html"
 * 3. Javascript popup urls : "javascript:popUpWindow7('news_details.asp?slno=2029',620,300,100,100,'yes')"
 * 
 * This class handles these urls and uses basic String and StringBuilder functions to decode them to
 * proper absolute urls.
 * 
 * This class also identifies whether the file is a HTML file or a PDF file based on its extension so that 
 * the result can be used to display proper activity to handle the filetype.
 * 
*/
public class URLDecoder {

	private static StringBuilder url;
	public static final int INVALIDFILE = -1;
	public static final int HTMLFILE =0;
	public static final int PDFFILE =1;
	
	//public enum URLDecoder{HTMLFILE, INVALIDFILE, PDFFILE;}
	
	public static String getDecodedUrl(String inputUrl){
		
		url =  new StringBuilder();
		inputUrl = inputUrl.trim().replaceAll(" ","%20");
		
		if(inputUrl.startsWith("javascript"))
		{
			url.append(inputUrl.substring(inputUrl.indexOf('\'')+1, 
					inputUrl.indexOf('\'',inputUrl.indexOf('\'')+1)));
			inputUrl=url.toString();
		}
		else
		{
			url.append(inputUrl);
		}
		
		if (!(url.substring(0, 4).equals("http"))){
			
			url.delete(0, url.length());
			url.append("http://www.bput.ac.in/");
		    url.append(inputUrl);
		}
		return url.toString();
	}
	
	public static int getUrlType(String url) {
		
		if(url.endsWith("htm")||url.endsWith("html"))
		{
			return HTMLFILE;
		}
		else if(url.endsWith("pdf"))
		{
			return PDFFILE;
		}
		else
		{
			return INVALIDFILE;
		}
		
	}
	
	
	
}
