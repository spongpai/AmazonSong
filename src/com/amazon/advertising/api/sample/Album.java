package com.amazon.advertising.api.sample;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class Album extends Product{
	
	//artist, album name, duration, label, genre, sale rank, and distribution of reviews (# of 5-1 stars)
	String artist, albumName; //, duration, label, genre, saleRank, detailPage;
	//public String ASIN = null;
	//public double avgRate;
	//public int[] disReviews;
	//public int totalReviews;
	
	public Album(){
		super();
		this.artist = "";
		this.albumName = "";
		this.duration = "";
		this.label = "";
		this.genre = "";
		this.saleRank = "";
		this.detailPage = "";
		this.disReviews = new int[5];
		this.totalReviews = 0;
	}
	
	public Album(String artist, String albumName, String detailPage){
		super();
		this.artist = artist;
		this.albumName = albumName;
		this.duration = "";
		this.label = "";
		this.genre = "";
		this.saleRank = "";
		this.detailPage = detailPage;
		this.disReviews = new int[5];
		this.totalReviews = 0;
	}
	
	public void setASIN(String asin){
		this.ASIN = asin;
	}
	
	public void setDisReview(int[] disReviews){
		this.disReviews = disReviews.clone();
	}
	
	public void setNumReview(int star, int numReviews){
		disReviews[star - 1] = numReviews;
	}
	
	public void setTotalReviews(int total){
		this.totalReviews = total;
	}
	
	public static String getAlbumHeader(){
		return "Album_ASIN" + Utils.split + "artist" + Utils.split + 
				"album_name" + Utils.split + "duration" + Utils.split + 
				"label" + Utils.split + "genre" + Utils.split + 
				"sale_rank" + Utils.split + "total_reviews" + Utils.split + 
				"5stars" + Utils.split + "4stars" + Utils.split + 
				"3stars" + Utils.split + "2stars" + Utils.split + 
				"1star" + Utils.split + "detail_page_url";
	}
	public String toString(){
		String output = this.ASIN + Utils.split + this.artist + Utils.split + this.albumName + Utils.split + 
				this.duration + Utils.split + this.label + Utils.split + this.genre + Utils.split + this.saleRank;
		
		if(this.disReviews != null){
			output += Utils.split + this.totalReviews 
					+ Utils.split + this.disReviews[4] + Utils.split + this.disReviews[3] 
					+ Utils.split + this.disReviews[2] + Utils.split + this.disReviews[1] 
					+ Utils.split + this.disReviews[0];
		} else{
			output += Utils.split + "0" + Utils.split + "0" + Utils.split + "0" + Utils.split + 
					"0" + Utils.split + "0" + Utils.split + "0";
		}
		output += Utils.split + this.detailPage;
		return output;
	}
	
	
}
