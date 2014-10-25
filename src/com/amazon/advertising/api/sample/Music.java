package com.amazon.advertising.api.sample;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class Music extends Product {
	int fileid;
	String date;
	String postTitle, artist, song, note;
	//public String ASIN = null;
	// "amz_title|amz_artist|amz_creator|amz_manufacturer|productgroup|"
	public Map<String, String> amzItemAttributes = new HashMap<String, String>();
	// "detail_page|duration|label|genre|sale_rank
	public Map<String, String> amzItemDetails = new HashMap<String, String>();
	//
	public Map<String, String> itemLinks = new HashMap<String, String>();
	
	public String amzArtist, amzSongTitle;
	public Album album;
	public Music(){
		
	}
	
	public Music(int fileid, String date, String postTitle, String artist, String song){
		this.fileid = fileid;
		this.date = date;
		this.postTitle = postTitle;
		this.artist = artist;
		this.song = song;
		this.note = "";
		this.disReviews = new int[5];
		this.totalReviews = 0;
	}
	
	public void setASIN(String asin){
		this.ASIN = asin;
	}
	
	public void setNote(String note){
		this.note = note;
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
	public static String getMusicHeader(){
		return "fileid|datesaved|posttitle|artist|song|ASIN|Note|" +
				"amz_artist|amz_song_title|duration|label|genre|sale_rank|" +
				"total_reviews|5stars|4stars|3stars|2stars|1star|avgRage|detail_page_url";
		
//		return "fileid|datesaved|posttitle|artist|song|ASIN|Note|" +
//				"amz_title|amz_artist|amz_creator|amz_manufacturer|productgroup|" +
//				"total_reviews|5stars|4stars|3stars|2stars|1star|" +
//				"request_url|technical_details|all_customer_reviews|all_offers";
	}
	public void setAlbum(Album al){
		this.album = al;
	}
	public Album getAlbum(){
		return this.album;
	}
	
	public String toString(){
		String output = this.fileid + "|" + this.date + "|" + this.postTitle + "|" + this.artist + "|" + this.song;
		if(this.ASIN != null)
			output += "|" + this.ASIN + "|" + this.note;
		else
			output += "| |" + this.note;
		
		output += "|" + this.amzArtist +
					"|" + this.amzSongTitle +
					"|" + this.duration + "|" + this.label + "|" + this.genre + "|" + this.saleRank;
		
		if(this.disReviews != null){
			output += "|" + this.totalReviews 
					+ "|" + this.disReviews[4] + "|" + this.disReviews[3] + "|" + this.disReviews[2] + "|" + this.disReviews[1] + "|" + this.disReviews[0]
					+ "|" + this.avgRate;
		} else{
			output += "|0|0|0|0|0|0|0";
		}
		
		output += "|" + this.detailPage;
		
		/*
		if(!this.amzItemAttributes.isEmpty()){
			output += "|" + this.amzItemAttributes.get("Title")
					+ "|" + this.amzItemAttributes.get("Artist")
					+ "|" + this.amzItemAttributes.get("Creator")
					+ "|" + this.amzItemAttributes.get("Manufacturer")
					+ "|" + this.amzItemAttributes.get("ProductGroup");
		} else{
			output += "| | | | |";
		}
		
		if(this.disReviews != null){
			output += "|" + this.totalReviews 
					+ "|" + this.disReviews[4] + "|" + this.disReviews[3] + "|" + this.disReviews[2] + "|" + this.disReviews[1] + "|" + this.disReviews[0];
		} else{
			output += "|0|0|0|0|0|0";
		}
		if(!this.itemLinks.isEmpty()){
			output += "|" + this.itemLinks.get("Technical Details") 
					+ "|" + this.itemLinks.get("All Customer Reviews")
					+ "|" + this.itemLinks.get("All Offers");
			//output += "|" + this.itemLinks.get("Add To Baby Registry");
			//output += "|" + this.itemLinks.get("Add To Wedding Registry");
			//output += "|" + this.itemLinks.get("Add To Wishlist");
			//output += "|" + this.itemLinks.get("Tell A Friend");
		} else{
			output += "| | |";
		}
		*/
		return output;
	}
	
	
}
