/**********************************************************************************************
 * Copyright 2009 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"). You may not use this file 
 * except in compliance with the License. A copy of the License is located at
 *
 *       http://aws.amazon.com/apache2.0/
 *
 * or in the "LICENSE.txt" file accompanying this file. This file is distributed on an "AS IS"
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under the License. 
 *
 * ********************************************************************************************
 *
 *  Amazon Product Advertising API
 *  Signed Requests Sample Code
 *
 *  API Version: 2009-03-31
 *
 */

package com.amazon.advertising.api.sample;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/*
 * This class shows how to make a simple authenticated ItemLookup call to the
 * Amazon Product Advertising API.
 * 
 * See the README.html that came with this sample for instructions on
 * configuring and running the sample.
 */
public class ItemLookupSample {
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

    /*
     * The Item ID to lookup. The value below was selected for the US locale.
     * You can choose a different value if this value does not work in the
     * locale of your choice.
     */
    private static final String ITEM_ID = "B00IOVH8AW"; //"0545010225";

    public static void main(String[] args) {
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

        /* The helper can sign requests in two forms - map form and string form */
        
        /*
         * Here is an example in map form, where the request parameters are stored in a map.
         */
        System.out.println("Map form example:");
        Map<String, String> params = new HashMap<String, String>();
        params.put("Service", "AWSECommerceService");
        //params.put("Version", "2009-03-31");
        params.put("Operation", "ItemLookup");
        params.put("ItemId", ITEM_ID);
        params.put("ResponseGroup", "Small");
        params.put("AssociateTag", "socia0d4c-20");

        requestUrl = helper.sign(params);
        System.out.println("Signed Request is \"" + requestUrl + "\"");

        title = fetchTitle(requestUrl);
        System.out.println("Signed Title is \"" + title + "\"");
        System.out.println();

        /* Here is an example with string form, where the requests parameters have already been concatenated
         * into a query string. */
//        System.out.println("String form example:");
//        String queryString = "Service=AWSECommerceService&Version=2009-03-31&Operation=ItemLookup&ResponseGroup=Small&ItemId="
//                + ITEM_ID;
//        requestUrl = helper.sign(queryString);
//        System.out.println("Request is \"" + requestUrl + "\"");
//
//        title = fetchTitle(requestUrl);
//        System.out.println("Title is \"" + title + "\"");
//        System.out.println();

    }

    /*
     * Utility function to fetch the response from the service and extract the
     * title from the XML.
     */
    private static String fetchTitle(String requestUrl) {
    	
        String title = null;
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(requestUrl);
            //System.out.print("-test-" + doc.toString());
            Node titleNode = doc.getElementsByTagName("Title").item(0);
            title = titleNode.getTextContent();
            String reviewUrl = "";
           	NodeList itemLinks = doc.getElementsByTagName("ItemLink");
           	for(int i = 0; i < itemLinks.getLength(); i++){
           		Node link = itemLinks.item(i);
           		Node desc = link.getFirstChild();
           		Node url = link.getLastChild();
           		
           		System.out.println(desc.getTextContent() + ": " + url.getTextContent());
           		if(desc.getTextContent().equalsIgnoreCase("All Customer Reviews")){
           			reviewUrl = url.getTextContent();
           		}
           	}
           	org.jsoup.nodes.Document doc2 = Jsoup.connect(reviewUrl).get();
           	Element summary = doc2.getElementById("productSummary");
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
        	
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return title;
    }

}
