package com.amazon.advertising.api.sample;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.lucene.search.spell.JaroWinklerDistance;
import org.apache.lucene.search.spell.LevensteinDistance;
import org.apache.lucene.search.spell.NGramDistance;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class MusicLookup {
	ArrayList<Music> musicList = new ArrayList<Music>();
	String fileID;
	int songID;
	String songURL, albumURL;
	 /*
     * Your AWS Access Key ID, as taken from the AWS Your Account page.
     */
    private static final String AWS_ACCESS_KEY_ID = "AKIAI73ZR3RBXDQTBXEA";

    /*
     * Your AWS Secret Key corresponding to the above ID, as taken from the AWS
     * Your Account page.
     */
    private static final String AWS_SECRET_KEY = "mhrHAi4rz2MlEzz/ARtrruC+JXFxXPifext5eRGp";

    /*
     * Use one of the following end-points, according to the region you are
     * interested in:
     * 
     *      US: ecs.amazonaws.com 
     *      CA: ecs.amazonaws.ca 
     *      UK: ecs.amazonaws.co.uk 
     *      DE: ecs.amazonaws.de 
     *      FR: ecs.amazonaws.fr 
     *      JP: ecs.amazonaws.jp
     * 
     */
    private static final String ENDPOINT = "ecs.amazonaws.com";

    private static final String NO_MATCH = "AWS.ECommerceService.NoExactMatches";
    
	public void MusicLookup(){
		
	}
	public void setSongURL(String url){
		songURL = url;
	}
	public void setAlbumURL(String url){
		albumURL = url;
	}
	public void sleep(int ms){
		try {
			Thread.sleep(ms);
		} catch (InterruptedException e) {
			System.out.println("Interrupt ----------------------------------------------");
			e.printStackTrace();
		}
	}
	public void getASINFromFile(String file){
		String line = null;
		BufferedReader br = null;
		try{
			br = new BufferedReader (new InputStreamReader(new FileInputStream(file)));
			line = br.readLine();	// skip the header
			//fileid|datesaved|posttitle|artist|song|ASIN|Note
			while((line = br.readLine())!= null){
				String[] token = line.split("\\|", -1);
				if(token.length != 7){
					writeToFile(file + "error", "["+token.length+"]" + line + "\n", true);
				} else{
					int id = Integer.parseInt(token[0]);
					Music temp = new Music(id, token[1], token[2], token[3], token[4]);
					temp.setASIN(token[5]);
					temp.setNote(token[6]);
					musicList.add(temp);
				}
			}
			br.close();
	    	br = null;
	    } catch (Exception e){
	    	System.out.println("Error in getASINFromFile");
	    	e.printStackTrace();
	    } 
	}
	public void getMusicFromFile(String file){
		String line = null;
	    BufferedReader br = null;
	    try{
	    	br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
	    	line = br.readLine(); 	// skip the header
	    	while ((line = br.readLine()) != null) {
	    		String[] token = line.split("\\|", -1);
	    		//System.out.println(line + "\n---- ["+token.length+"]");
	    		if(token.length != 5){
	    			writeToFile(file + "error", "["+token.length+"]" + line + "\n", true);
	    		} else {
	    			writeToFile(file + "correct", "["+token.length+"]" + line + "\n", true);
		    		int id = Integer.parseInt(token[0]);
		    		Music temp = new Music(id, token[1], token[2], token[3], token[4]);
		    		musicList.add(temp);
	    		}
      		}
	    	br.close();
	    	br = null;
	    } catch (Exception e){
	    	e.printStackTrace();
	    } 
	}
	public void getMusicFromFileWithSSN(String file){
		String line = null;
	    BufferedReader br = null;
	    try{
	    	br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
	    	line = br.readLine(); 	// skip the header
	    	//ssnumber|songid|post_title|song_artist|song_title

	    	while ((line = br.readLine()) != null) {
	    		String[] token = line.split("\\|", -1);
	    		//System.out.println(line + "\n---- ["+token.length+"]");
	    		if(token.length != 5){
	    			writeToFile(file + "error", "["+token.length+"]" + line + "\n", true);
	    		} else {
	    			writeToFile(file + "correct", "["+token.length+"]" + line + "\n", true);
		    		int id = Integer.parseInt(token[1]);
		    		Music temp = new Music(id, "", token[2], token[3], token[4]);
		    		temp.setSSNumber(token[0]);
		    		musicList.add(temp);
	    		}
      		}
	    	br.close();
	    	br = null;
	    } catch (Exception e){
	    	e.printStackTrace();
	    } 
	}
	
	public void writeToFile(String file, String text, boolean append){
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(file, append));
			bw.append(text);
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/*
     * Utility function to fetch the response from the service and extract the
     * title from the XML.
     */
    private boolean fetchISBN(String requestUrl, Music music) {
        Boolean found = false;
        music.itemLinks.put("requestUrl", requestUrl);
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(requestUrl);
            Node asinNode = doc.getElementsByTagName("ASIN").item(0);
            if(asinNode != null){
            	found = true;
            	music.setASIN(asinNode.getTextContent());
            }
            else{
            	Node errorNode = doc.getElementsByTagName("Errors").item(0);
            	System.out.println(errorNode.getTextContent());
            	this.writeToFile("error/exception" + fileID, "[" + songID + "] " + errorNode.getTextContent() + "\n", true);
                
            }
        } catch (Exception e) {
        	this.writeToFile("error/exception" + fileID, "[" + songID + "] " + e.getMessage() + "\n", true);
            
            throw new RuntimeException(e);
        }
        return found;
    }
    
    private void printDom(Document doc){
    	try {
	    	TransformerFactory tFactory = TransformerFactory.newInstance();
	    	Transformer transformer = tFactory.newTransformer();
	    	DOMSource source = new DOMSource(doc);
	    	StreamResult result = new StreamResult(System.out);
			transformer.transform(source, result);
		} catch (TransformerException e) {
			e.printStackTrace();
		}
    }
    
	/*
     * Utility function to fetch the response from the service and extract the
     * title from the XML.
     */
    private boolean fetchItem(String requestUrl, Music music) {
        Boolean found = false;
        music.itemLinks.put("requestUrl", requestUrl);
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(new URL(requestUrl).openStream());
            
            System.out.println("------\n");
            printDom(doc);
            System.out.println("\n------\n");
            Node asinNode = doc.getElementsByTagName("ASIN").item(0);
            if(asinNode != null){
            	found = true;
            	music.setASIN(asinNode.getTextContent());
            	Node item = doc.getElementsByTagName("ItemAttributes").item(0);
            	NodeList attr = item.getChildNodes();
            	for(int i = 0; i < attr.getLength(); i++){
            		music.amzItemAttributes.put(attr.item(i).getNodeName(), attr.item(i).getTextContent());
            	}
            	Node detailPage = doc.getElementsByTagName("DetailPageURL").item(0);
            	music.detailPage = detailPage.getTextContent();
            	this.setSongURL(music.detailPage);
            	this.setMusicDetails(music.detailPage, music);
            	
            	/*
            	NodeList itemLinks = doc.getElementsByTagName("ItemLink");
               	for(int i = 0; i < itemLinks.getLength(); i++){
               		Node link = itemLinks.item(i);
               		Node desc = link.getFirstChild();
               		Node url = link.getLastChild();
               		music.itemLinks.put(desc.getTextContent(), url.getTextContent());
               	}
               	if(!music.itemLinks.isEmpty() && music.itemLinks.containsKey("All Customer Reviews")){
               		//System.out.println(music.itemLinks.get("All Customer Reviews"));
               		//setMusicReview(music.itemLinks.get("All Customer Reviews"), music);
               	} 
               	*/
            }
            else{
            	Node errorNode = doc.getElementsByTagName("Errors").item(0);
            	System.out.println(errorNode.getTextContent());
            	this.writeToFile("error/exception" + fileID, "[" + songID + "] " + errorNode.getTextContent() + "\n", true);
                
            }
        } catch (Exception e) {
        	this.writeToFile("error/exception" + fileID, "[" + songID + "] " + e.getMessage() + "\n", true);
            
            throw new RuntimeException(e);
        }
        return found;
    }
    public void setMusicDetails(String productUrl, Music music){
    	org.jsoup.nodes.Document doc;
		try {
			this.sleep(3000);
			doc = Jsoup.connect(productUrl).timeout(30000).userAgent("Mozilla/17.0").get();
    		setItemDetail(doc, music);
    		
    		// get artist name
    		Element eArtist = doc.getElementById("artist_row");
    		if(eArtist != null){
    			music.amzArtist = eArtist.text();
    		} else{
    			music.amzArtist = "";
    		}
    		
    		
    		// get song title
    		Element eTitle = doc.getElementById("title_row");
    		if(eTitle != null){
    			music.amzSongTitle = eTitle.text();
    		} else{
    			music.amzSongTitle = "";
    		}
    		
    		
    		// get album
    		Element fromAlbum = doc.getElementById("fromAlbum");
    		if(fromAlbum != null){
	    		Element album = fromAlbum.select("a").first();
	    		String albumName = album.text();
	    		String albumUrl = album.attr("href");
	    		if(albumUrl != null){
	    			this.sleep(3000);
	    			this.setAlbumURL(albumUrl);
	    			setMusicAlbum(music.amzArtist, albumName, albumUrl, music);
	    		}
    		}
    		
    	} catch (Exception e){
    		this.writeToFile("error/exception_" + fileID, "[" + songID + "] setMusicDetails" + e.getMessage() + "\n", true);
            
    		System.out.println("Error in SetMusicDetails");
    		e.printStackTrace();
    	}
    	
    }
    
    public void setItemDetail(org.jsoup.nodes.Document doc, Product p){
    	Element product = doc.getElementById("productDetailsTable");
		//pageUrl|duration|label|genre|sale_rank
		Element list = product.select("ul").first();
		Iterator<Element> ite = list.select("li").iterator();
		//System.out.println("+++++\n" + doc.toString());
		// set label, duration, genre, and sale_rank
		while(ite.hasNext()){
			Element e = ite.next();
			String text = e.text();
			if(text.contains("ASIN: ")){
				p.ASIN = text.substring("ASIN: ".length());
			} else if(text.contains("Label: ")){
				p.label = text.substring("Label: ".length());
			} else if(text.contains("Duration: ")){
				p.duration = text.substring("Duration: ".length());
			} else if(text.contains("Amazon Best Sellers Rank: ")){
				p.saleRank =  text.substring("Amazon Best Sellers Rank: ".length(), text.indexOf("("));
				System.out.println("saleRank *************" + p.saleRank);
			} else if(text.contains("Genres: ")){
				String genres = "";
				Element gList = e.select("ul").first();
				Iterator<Element> gIte = gList.select("li").iterator();
				while(gIte.hasNext()){
					Element gE = gIte.next();
					genres += gE.text() +", ";
				}
				if(genres.length() > 2)
					genres = genres.substring(0, genres.lastIndexOf(","));
				p.genre = genres;
			}
		}
		
		// set avg_rating, stars
		Element avgRate = doc.getElementById("avgRating");
		int[] disRate = new int[5];
		int totalReviews = 0;
		if(avgRate != null){
			p.avgRate = Double.parseDouble(avgRate.text().substring(0, avgRate.text().indexOf(" ")));
			Element rateTable = doc.getElementById("histogramTable");
			Iterator<Element> rIte = rateTable.select("tr").iterator();
			int star = 5;
			while(rIte.hasNext() && star > 0){
				Element row = rIte.next();
				String txt = row.text();
				disRate[star - 1] = Integer.parseInt(txt.replace(star + " star ", ""));
				totalReviews += disRate[star - 1];
				//System.out.println();
				star--;
			}
			p.disReviews = disRate.clone();
			p.totalReviews = totalReviews;
			
			System.out.println("reviews ********* " + p.totalReviews + "|||" + p.disReviews[0] + ","
					+ p.disReviews[1] + ","+ p.disReviews[2] + ","+ p.disReviews[3] + ","+ p.disReviews[4] + ",");
		}
    }
    
    public void setMusicAlbum(String artist, String albumName, String albumUrl, Music m){
    	Album al = new Album(artist, albumName, albumUrl);
    	org.jsoup.nodes.Document doc;
		try {
			this.sleep(3000);
			doc = Jsoup.connect(albumUrl).timeout(30000).userAgent("Mozilla/17.0").get();
			this.setItemDetail(doc, al);
			m.album = al;
		} catch (IOException e) {
			this.writeToFile("error/exception_" + fileID, "[" + songID + "] setMusicAlbum: " + e.getMessage() + "\n", true);
            
			System.out.println("Error in setMusicAlbum");
			e.printStackTrace();
		}
    }
    /*
    public void setMusicReview(String reviewUrl, Music music){
    	org.jsoup.nodes.Document doc2;
		try {
			doc2 = Jsoup.connect(reviewUrl).get();
	       	Element summary = doc2.getElementById("productSummary");
	       	if(summary != null){
				Element tiny = summary.getElementsByClass("tiny").first();
				String[] sumReview = tiny.html().replace("<b>", "").split(" ");
				int numReview = Integer.parseInt(sumReview[0]);
				//System.out.println("number of reviews " + numReview);
				music.setTotalReviews(numReview);
				
				Element table = summary.select("table").first();
		
				Iterator<Element> ite = table.select("tr").iterator();
				ite.next();	// skip one "tr"
				int star = 5;
				while(ite.hasNext() && star > 0){
					Element row = ite.next();
					String review = row.text();
					review = review.substring(review.lastIndexOf("(") + 1, review.length()-1);
					//System.out.println(star + " " + review);
					music.setNumReview(star, Integer.parseInt(review));
					star--;
				}
	       	}
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    */
    
    public static void main(String[] args){
    	/*
         * Set up the signed requests helper 
         */
        SignedRequestsHelper helper;
        try {
            helper = SignedRequestsHelper.getInstance(ENDPOINT, AWS_ACCESS_KEY_ID, AWS_SECRET_KEY);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        
        String requestUrl = null;
        String title = null;
        Boolean found = false;
        /* The helper can sign requests in two forms - map form and string form */
        //String fileID = "ASINforDetails_0922";
        String fileID = "complete";
        Long timeStamp = System.currentTimeMillis();
        int format = 2; // 0 = Normal (5 fields), 1 = ASIN (7 fields), 2 = SSN (5 fields)
        
        //String searchIndex = "Music";
        String searchIndex = "MP3Downloads";
        //String searchIndex = "both";
        String fileName = "data/"+fileID+".csv";
        String asinFile = "result/"+fileID+"_set_"+searchIndex+"_" + timeStamp+".csv";
        String hitFile = "result/"+fileID+"_asin_"+searchIndex+"_" + timeStamp+".csv";
        String missFile = "result/"+fileID+"_miss_"+searchIndex+"_" + timeStamp+".csv";
        String albumFile = "result/"+fileID+"_album_"+searchIndex+"_" + timeStamp+".csv";
        //String simFile = "data/"+fileID+"_sim_"+searchIndex+".csv";
        MusicLookup ml = new MusicLookup();
        ml.fileID = fileID;
        
        if(format == 0){
        	ml.getMusicFromFile(fileName);		// NO ASIN in the file yet
        } else if(format == 1){
        	ml.getASINFromFile(fileName);		// ASIN is already included in the file
        } else if(format == 2){
        	ml.getMusicFromFileWithSSN(fileName);		// SSNumber is included in the file
        } else{
        	System.out.println("incorrect format....\nEXIT... BYE");
        	System.exit(-1);
        }
        
        int hit = 0;
        int miss = 0;
        int start = 1058;
        int stop = 1499;//ml.musicList.size();
        System.out.println(start + ",  " + stop);
        NGramDistance nGram  = new NGramDistance();
        LevensteinDistance levenstein = new LevensteinDistance();
        JaroWinklerDistance jaroWinkler = new JaroWinklerDistance();
        Map<String, String> params = new HashMap<String, String>();
        params.put("Service", "AWSECommerceService");
        params.put("Operation", "ItemSearch");
        params.put("AssociateTag", "socia0d4c-20");
        
        StringBuilder sbFound = new StringBuilder();
        StringBuilder sbMiss = new StringBuilder();
        StringBuilder sbAlbum = new StringBuilder();
        // Write header of the output files
        if(format == 2){
        	ml.writeToFile(hitFile, "SSNumber"+Utils.split+"key"+Utils.split+ Music.getMusicHeader() + Utils.split + "sim1"+Utils.split+"sim2"+Utils.split+"sim3"+Utils.split+"requestUrl\n" + sbFound.toString(), true);
            ml.writeToFile(albumFile, "SSNumber"+Utils.split+"key"+Utils.split+"fileid"+Utils.split + Album.getAlbumHeader() + "\n" + sbAlbum.toString(), true);
            ml.writeToFile(missFile, "SSNumber"+Utils.split+Music.getMusicHeader() + Utils.split + "requestUrl\n" + sbMiss.toString(), true);
            
        } else{
        	ml.writeToFile(hitFile, "key"+Utils.split+ Music.getMusicHeader() + Utils.split + "sim1"+Utils.split+"sim2"+Utils.split+"sim3"+Utils.split+"requestUrl\n" + sbFound.toString(), true);
            ml.writeToFile(albumFile, "key"+Utils.split+"fileid"+Utils.split + Album.getAlbumHeader() + "\n" + sbAlbum.toString(), true);
            ml.writeToFile(missFile, Music.getMusicHeader() + Utils.split + "requestUrl\n" + sbMiss.toString(), true);
            
        }
        /*
        // search for asin
        for(int i = start; i < stop; i++){
        	//System.out.println(ml.musicList.get(i).toString());
        	Music music = ml.musicList.get(i);
        	
        	// search by song name and artist
        	params.put("Keywords", music.song + " " + music.artist);
        	//params.put("Keywords", music.song);
            
        	// Try on "MP3Downloads" index
            params.put("SearchIndex", searchIndex);
            requestUrl = helper.sign(params);
            System.out.println("["+i+"] Signed Request is \"" + requestUrl + "\"");
            ml.songID = i;
            found = ml.fetchISBN(requestUrl, music);
            
            if(found){
            	hit++;
            	float sim1 = 0;//nGram.getDistance(music.song, music.amzItemAttributes.get("Title"));
            	float sim2 = 0;//levenstein.getDistance(music.song, music.amzItemAttributes.get("Title"));
            	float sim3 = 0;//jaroWinkler.getDistance(music.song, music.amzItemAttributes.get("Title"));
            	if(music.totalReviews > 0){
            		System.out.println("----- found reviews -----" + music.toString() + Utils.split + sim1 + Utils.split + sim2 + Utils.split + sim3);
            	}
            	
            	ml.writeToFile(asinFile, music.ASIN + Utils.split + music.toString() + Utils.split + sim1 + Utils.split + sim2 + Utils.split + sim3 + Utils.split + requestUrl + "\n", true);
            	
            } else{
            	ml.writeToFile(missFile, music.toString() + Utils.split + requestUrl + "\n", true);
            	miss++;
            }
            try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
				continue;
			}
        }
        
        System.exit(0);
        */
        if(format == 0){
        	for(int i = start; i < stop; i++){
            	//System.out.println(ml.musicList.get(i).toString());
            	Music music = ml.musicList.get(i);
            	
            	// search by song name and artist
            	params.put("Keywords", music.song + " " + music.artist);
            	//params.put("Keywords", music.song);
                
            	// Try on "MP3Downloads" index
                params.put("SearchIndex", searchIndex);
                requestUrl = helper.sign(params);
                System.out.println("["+i+"] Signed Request is \"" + requestUrl + "\"");
                ml.songID = i;
                found = ml.fetchItem(requestUrl, music);
                
                if(found){
                	hit++;
                	System.out.println(music.detailPage);
                	float sim1 = nGram.getDistance(music.song, music.amzItemAttributes.get("Title"));
                	float sim2 = levenstein.getDistance(music.song, music.amzItemAttributes.get("Title"));
                	float sim3 = jaroWinkler.getDistance(music.song, music.amzItemAttributes.get("Title"));
                	if(music.totalReviews > 0){
                		System.out.println("----- found reviews -----" + music.toString() + Utils.split + sim1 + Utils.split + sim2 + Utils.split + sim3);
                	}
                	
                	ml.writeToFile(hitFile, music.ASIN + Utils.split + music.toString() + Utils.split + sim1 + Utils.split + sim2 + Utils.split + sim3 + 
                			Utils.split + requestUrl + "\n", true);
                	if(music.album != null)
                		ml.writeToFile(albumFile, music.ASIN + Utils.split + music.fileid + Utils.split + music.album.toString() + "\n", true);
                	
                } else{
                	ml.writeToFile(missFile, music.toString() + Utils.split + requestUrl + "\n", true);
                	miss++;
                }
                try {
    				Thread.sleep(1000);
    			} catch (InterruptedException e) {
    				e.printStackTrace();
    				continue;
    			}
            }
        } else if (format == 1){
        	for(int i = start; i < stop; i++){
            	//System.out.println(ml.musicList.get(i).toString());
            	Music music = ml.musicList.get(i);
            	
            	// serach by ASIN
            	String key = music.ASIN;
            	params.put("Keywords", music.ASIN);
            	
            	// Try on "MP3Downloads" index
                params.put("SearchIndex", searchIndex);
                requestUrl = helper.sign(params);
                System.out.println("["+i+"] ASIN: "+ music.ASIN +" \nSigned Request is \"" + requestUrl + "\"");
                ml.songID = i;
                found = ml.fetchItem( requestUrl, music);
                
                if(found){
                	hit++;
                	System.out.println(music.detailPage);
                	float sim1 = nGram.getDistance(music.song, music.amzItemAttributes.get("Title"));
                	float sim2 = levenstein.getDistance(music.song, music.amzItemAttributes.get("Title"));
                	float sim3 = jaroWinkler.getDistance(music.song, music.amzItemAttributes.get("Title"));
                	if(music.totalReviews > 0){
                		System.out.println("----- found reviews -----" + music.toString() + Utils.split + sim1 + Utils.split + sim2 + Utils.split + sim3);
                	}
                	ml.writeToFile(hitFile, key + Utils.split + music.toString() + Utils.split + sim1 + Utils.split + sim2 + Utils.split + sim3 + 
                			Utils.split + requestUrl + "\n", true);
                	if(music.album != null)
                		ml.writeToFile(albumFile, key + Utils.split + music.fileid + Utils.split + music.album.toString() + "\n", true);
                	
                } else{
                	ml.writeToFile(missFile, music.toString() + Utils.split + requestUrl + "\n", true);
                	miss++;
                }try {
    				Thread.sleep(1000);
    			} catch (InterruptedException e) {
    				System.out.println("Interrupt ----------------------------------------------");
    				e.printStackTrace();
    				continue;
    			}
            }
        } else if(format == 2){
        	for(int i = start; i < stop; i++){
            	//System.out.println(ml.musicList.get(i).toString());
            	Music music = ml.musicList.get(i);
            	
            	// search by song name and artist
            	params.put("Keywords", music.song + " " + music.artist);
            	//params.put("Keywords", music.song);
                
            	// Try on "MP3Downloads" index
                params.put("SearchIndex", searchIndex);
                requestUrl = helper.sign(params);
                System.out.println("["+i+"] Signed Request is \"" + requestUrl + "\"");
                ml.songID = i;
                found = ml.fetchItem(requestUrl, music);
                
                if(found){
                	hit++;
                	System.out.println(music.detailPage);
                	float sim1 = nGram.getDistance(music.song, music.amzItemAttributes.get("Title"));
                	float sim2 = levenstein.getDistance(music.song, music.amzItemAttributes.get("Title"));
                	float sim3 = jaroWinkler.getDistance(music.song, music.amzItemAttributes.get("Title"));
                	//if(music.totalReviews > 0){
                	//	System.out.println("----- found reviews -----" + music.toString() + Utils.split + sim1 + Utils.split + sim2 + Utils.split + sim3);
                	//}
                	
                	ml.writeToFile(hitFile, music.ssnumber + Utils.split + music.ASIN + Utils.split + music.toString() + Utils.split + sim1 + Utils.split + sim2 + Utils.split + sim3 + 
                			Utils.split + requestUrl + "\n", true);
                	if(music.album != null)
                		ml.writeToFile(albumFile, music.ssnumber + Utils.split + music.ASIN +  Utils.split + music.fileid + Utils.split + music.album.toString() + "\n", true);
                	
                } else{
                	ml.writeToFile(missFile, music.ssnumber + Utils.split + music.toString() + Utils.split + requestUrl + "\n", true);
                	miss++;
                }
                try {
    				Thread.sleep(1000);
    			} catch (InterruptedException e) {
    				e.printStackTrace();
    				continue;
    			}
            }
        }
        
        
         
        System.out.println("hit: " + hit + ", miss: " + miss);
        
//        Music test = new Music(1, "2014-01-01", "title", "aaa", "High Top Fade");
//        
//        System.out.println("Map form example:");
//        Map<String, String> params = new HashMap<String, String>();
//        params.put("Service", "AWSECommerceService");
//        params.put("Operation", "ItemSearch");
//        params.put("AssociateTag", "socia0d4c-20");
//        params.put("Keywords", test.song);
//        params.put("SearchIndex", "Music");
//        //params.put("Availability", "Available");
//        requestUrl = helper.sign(params);
//        System.out.println("Signed Request is \"" + requestUrl + "\"");
//
//        found = ml.fetchItem(requestUrl, test);
//        if(found){
//        	System.out.println("ASIN is \"" + test.ASIN + "\"");
//        	System.out.println("title is: " +  test.amzItemAttributes.get("Title"));
//        }
//        System.out.println();
    }

}
