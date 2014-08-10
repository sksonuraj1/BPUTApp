package com.paulshantanu.bputapp;

/* 
 * This class implements the basic SAX XML Parser to handle the XML parsing of the notices.
 */

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class SaxParserHandler extends DefaultHandler {
	int i = 0;
	String temp_str;
	Notice notice_obj;
	
	public Notice getNotice() {
		return notice_obj;
	}
	
	@Override
	public void startDocument() throws SAXException {
		super.startDocument();
		notice_obj = new Notice();
	}
	
	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		super.startElement(uri, localName, qName, attributes);
		if(qName.equals("notice")){
		notice_obj.setUrl(i, attributes.getValue(0));
	    }
		else if(qName.equals("thead")){
			notice_obj.setHas_table(true);
		}
	}

	@Override
	public void characters(char[] ch, int start, int length)throws SAXException {
		super.characters(ch, start, length);		
		temp_str= new String(ch,start,length).trim();
	}
	
	@Override
	public void endElement(String uri, String localName, String qName)throws SAXException {
		super.endElement(uri, localName, qName);
		if (qName.equals("notice")){
			notice_obj.setNotice_head(i,temp_str);
			i++;
		}
		else if (qName.equals("text")) {
        	notice_obj.setNotice_body(temp_str);
        }
        else if (qName.equals("thead")){
        	notice_obj.setTable_head(temp_str);
        }
        else if (qName.equals("td")){
        	notice_obj.setTable_body(temp_str);	
        }
	}
}
