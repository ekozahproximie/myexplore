package org.devtcg.rssreader.activity;

public class RssItem {
	String title;
	String link;
	String description;
	public RssItem(String title, String link, String description) {
		this.title=title;
		this.link=link;
		this.description=description;
	}

	public RssItem(CharSequence charSequence, CharSequence charSequence2,
			CharSequence charSequence3) {
		this.title=(String) charSequence;
		this.link=(String) charSequence2;
		this.description=(String) charSequence3;
	}

	public CharSequence getTitle() {
		
		return title;
	}

	public String getDescription() {
		
		return description;
	}

	public CharSequence getLink() {
		
		return link;
	}

}
