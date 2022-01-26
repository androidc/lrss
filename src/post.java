package com.example.lirurssreader_v4;

//change git test

public class post {
private String title;
private String descr;
private String pdaUrl;
private String pubDate;

public post(String title, String descr, String pdaUrl, String pubDate) {
	this.title = title;
	this.descr = descr;
	this.pdaUrl = pdaUrl;
	this.pubDate = pubDate;
	
}

public String getTitle(){
	return title;
}

public String getDescr() {
	return descr;
}

public String getPdaUrl() {
	return pdaUrl;
}
public String getpubDate() {
	return pubDate;
}
}
