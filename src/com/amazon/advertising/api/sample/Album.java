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
		return "Album_ASIN|artist|album_name|duration|label|genre|sale_rank|total_reviews|5stars|4stars|3stars|2stars|1star|detail_page_url";
	}
	public String toString(){
		String output = this.ASIN + "|" + this.artist + "|" + this.albumName + "|" + this.duration + "|" + this.label + "|" + this.genre + "|" + this.saleRank;
		
		if(this.disReviews != null){
			output += "|" + this.totalReviews 
					+ "|" + this.disReviews[4] + "|" + this.disReviews[3] + "|" + this.disReviews[2] + "|" + this.disReviews[1] + "|" + this.disReviews[0];
		} else{
			output += "|0|0|0|0|0|0";
		}
		output += "|" + this.detailPage;
		return output;
	}
	
	
}
