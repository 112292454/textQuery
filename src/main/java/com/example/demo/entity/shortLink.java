package com.example.demo.entity;

import lombok.Data;

@Data
public class shortLink {
	int id;
	String sourceLongUrl;
	String shortUrl;
	long time;

	public shortLink(String sourceLongUrl, String shortUrl,long time) {
		this.sourceLongUrl = sourceLongUrl;
		this.shortUrl = shortUrl;
		this.time=time;
	}
}
