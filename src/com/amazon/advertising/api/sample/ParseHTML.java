package com.amazon.advertising.api.sample;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.Iterator;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class ParseHTML {
	
	public ParseHTML(){
		
		
		
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
	
	public void OlympicMedal() throws IOException, SQLException, InterruptedException{
		String baseUrl = "http://www.databaseolympics.com/games/gamesyear.htm?g=26";
        //Document doc = Jsoup.connect(OlympicMedalMirrorProcessor.baseUrl + "?g=26").get();
		Document doc = Jsoup.connect(baseUrl).get();
    	String title = doc.title();
        System.out.println(title);
        Element table = doc.select("table.pt8").get(0);
        Elements trs = table.select("tr");
        Iterator trIter = trs.iterator();
        boolean firstRow = true;
        while (trIter.hasNext()) {
 
            Element tr = (Element)trIter.next();
            if (firstRow) {
                firstRow = false;
                continue;
            }
            Elements tds = tr.select("td");
            Iterator tdIter = tds.iterator();
            int tdCount = 1;
            String country = null;
            Integer gold = null;
            Integer silver = null;
            Integer bronze = null;
            Integer total = null;
            // process new line
            while (tdIter.hasNext()) {
 
                Element td = (Element)tdIter.next();
                switch (tdCount++) {
                case 1:
                    country = td.select("a").text();
                    break;
                case 2:
                    gold = Integer.parseInt(td.text());
                    break;
                case 3:
                    silver = Integer.parseInt(td.text());
                    break;
                case 4:
                    bronze = Integer.parseInt(td.text());
                    break;
                case 5:
                    total = Integer.parseInt(td.text());
                    break;
                }
 
            }
            System.out.println(country + ": gold " + gold + " silver " + silver + " bronze " + bronze + " total " +
                               total);
        } //table rows
	}
	public static void main(String[] args) throws IOException, SQLException, InterruptedException {
		//File input = new File("C:\\Projects\\ESApp\\AmazonMusic\\webpage\\review.html");
		//Document doc = Jsoup.parse(input, "UTF-8","");
		
		// get prduct details
		//String url = "http://www.amazon.com/Burn-Witch/dp/tech-data/B0060OFR9Y%3FSubscriptionId%3DAKIAI73ZR3RBXDQTBXEA%26tag%3Dsocia0d4c-20%26linkCode%3Dxm2%26camp%3D2025%26creative%3D386001%26creativeASIN%3DB0060OFR9Y";
		//String url = "http://www.amazon.com/gp/product/B00FAEPYT8/ref=dm_ws_tlw_trk1";
		String url = "http://www.amazon.com/She-Doesnt-Get-It/dp/B005N67G4E%3FSubscriptionId%3DAKIAI73ZR3RBXDQTBXEA%26tag%3Dsocia0d4c-20%26linkCode%3Dxm2%26camp%3D2025%26creative%3D165953%26creativeASIN%3DB005N67G4E";
		
		Document doc = Jsoup.connect(url).get();
		Element product = doc.getElementById("productDetailsTable");
		//pageUrl|duration|label|genre|sale_rank
		Element list = product.select("ul").first();
		Iterator<Element> ite = list.select("li").iterator();
		Music m = new Music();
		while(ite.hasNext()){
			Element e = ite.next();
			String text = e.text();
			if(text.contains("Label: ")){
				m.amzItemDetails.put("label", text.substring("Label: ".length()));
			} else if(text.contains("Duration: ")){
				m.amzItemDetails.put("duration", text.substring("Duration: ".length()));
			} else if(text.contains("Amazon Best Sellers Rank: ")){
				m.amzItemDetails.put("sale_rank", text.substring("Amazon Best Sellers Rank: ".length(), text.indexOf("(")));
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
				m.amzItemDetails.put("genre", genres);
			}
		}
		// get artist name
		Element eArtist = doc.getElementById("artist_row");
		String artistName = eArtist.text();
		
		// get album
		Element fromAlbum = doc.getElementById("fromAlbum");
		Element album = fromAlbum.select("a").first();
		String albumName = album.text();
		String albumUrl = album.attr("href");
		
		Album alb = new Album(artistName, albumName, albumUrl);
		doc = Jsoup.connect(albumUrl).get();
		Element product2 = doc.getElementById("productDetailsTable");
		//pageUrl|duration|label|genre|sale_rank
		Element list2 = product2.select("ul").first();
		Iterator<Element> ite2 = list2.select("li").iterator();
		
		while(ite2.hasNext()){
			Element e = ite2.next();
			String text = e.text();
			if(text.contains("Label: ")){
				alb.label = text.substring("Label: ".length());
			} else if(text.contains("Duration: ")){
				alb.duration = text.substring("Duration: ".length());
			} else if(text.contains("Amazon Best Sellers Rank: ")){
				alb.saleRank =  text.substring("Amazon Best Sellers Rank: ".length(), text.indexOf("("));
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
				alb.genre = genres;
			}
		}
		Element avgRate = doc.getElementById("avgRating");
		if(avgRate != null){
			alb.avgRate = Double.parseDouble(avgRate.text().substring(0, avgRate.text().indexOf(" ")));
			Element rateTable = doc.getElementById("histogramTable");
			Iterator<Element> rIte = rateTable.select("tr").iterator();
			int star = 5;
			while(rIte.hasNext() && star > 0){
				Element row = rIte.next();
				System.out.println(row.text());
				star--;
			}
		}
		
		//System.out.println("label " + m.amzItemDetails.get("label"));
		//System.out.println("duration " + m.amzItemDetails.get("duration"));
		//System.out.println("sale_rank " + m.amzItemDetails.get("sale_rank"));
		//System.out.println("genre " + m.amzItemDetails.get("genre"));
		System.out.println(m.getMusicHeader() + "\n" + m.toString());
		System.out.println(alb.getAlbumHeader() + "\n" + alb.toString());
		
		/*
		Elements pages = doc.getElementsByClass("paging");
		int lastPage = 1;
		if(pages.isEmpty()){	// there is only one review page
			
		} else{					// there are more than one review pages
			Element page = pages.first();
			String text = page.text();
			System.out.println(text);
			int fIndex = text.indexOf("|");
			int lIndex = text.lastIndexOf("|");
			text = text.substring(fIndex + 1, lIndex);
			String [] p = text.split(" ");
			lastPage = Integer.parseInt(p[p.length - 1]);
			
			System.out.println("last page " + lastPage);
			
			// get links
			Elements links = page.getElementsByAttribute("href");
			for(Element link : links){
				
				System.out.println(link.attr("href"));
			}
		}
		*/
		
		/*
		 * Get customer reviews
		 * 
		Element summary = doc.getElementById("productSummary");
		Element tiny = summary.getElementsByClass("tiny").first();
		String[] sumReview = tiny.html().replace("<b>", "").split(" ");
		int numReview = Integer.parseInt(sumReview[0]);
		System.out.println("number of reviews " + numReview);
			
		Element table = summary.select("table").first();

		Iterator<Element> ite = table.select("tr").iterator();
		ite.next();	// skip one "tr"
		int star = 5;
		while(ite.hasNext() && star > 0){
			Element row = ite.next();
			String review = row.text();
			review = review.substring(review.lastIndexOf("(") + 1, review.length()-1);
			System.out.println(star + " " + review);
			star--;
		}
		//swSprite s_star_5_0 
		//swSprite s_star_3_0 
		 */
	}
}
