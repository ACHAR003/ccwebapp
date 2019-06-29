package com.neu.library.json;

import java.net.URL;

import com.neu.library.model.Image;

public class ImageJson {
	
	private String Id;
	private String url;
	
	
	public ImageJson(Image image) {
		this.Id = image.getId();
		this.url = image.getUrl();
	}
	
	public ImageJson(String id, String  url2) {
		this.Id = id;
		this.url = url2;
	}
	


	public String getId() {
		return Id;
	}


	public void setId(String id) {
		Id = id;
	}


	public String getUrl() {
		return url;
	}




	public void setUrl(String url) {
		this.url = url;
	}


	
	
	
	

}
