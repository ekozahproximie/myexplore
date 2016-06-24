package com.spime.groupon;

public class Says {
public String stWebsiteContentHtml=null;
public String stEmailContentHtml=null;
public Says( String stWebsiteContentHtml,String stEmailContentHtml) {
	this.stWebsiteContentHtml=stWebsiteContentHtml;
	this.stEmailContentHtml=stEmailContentHtml;
}
@Override
	public String toString() {
		
		return stWebsiteContentHtml;
	}
}
