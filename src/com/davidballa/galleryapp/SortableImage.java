package com.davidballa.galleryapp;

import java.util.Date;

public class SortableImage {
	
	private String path;
	private Date lastModDate;
	/* Informations about the file
	private int size;
	private int width;
	private int height;
	*/
	
	public SortableImage(String absolutePath, Date date) {
		this.path = absolutePath;
		this.lastModDate = date;
	}
	
	public SortableImage(String string) {
		this.path = string;
	}

	public String getPath() {
		return path;
	}
	
	public Date getLastModDate() {
		return lastModDate;
	}
	
}
