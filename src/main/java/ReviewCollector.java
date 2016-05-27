import cleansing.CleanReviews;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
/*
 * created by Natalie, 22.05.2016
 */
public class ReviewCollector {
	
	public static HashMap<String, String> readData(String reviewPath, int maxEntries) throws IOException {
		HashMap<String, String> reviews= new HashMap<String, String>();
		String businessID = "";
        String reviewText = "";
        
		// read data
        BufferedReader reader = new BufferedReader(new FileReader(new File(reviewPath)));
        
        String line = reader.readLine();
		int count = 0;
        while (line != null && count < maxEntries){
        	String[] lineItems = line.split(";;");
        	businessID = lineItems[0];
        	reviewText = lineItems[1];
            //text = text.concat(line + "\n");
        	
        	//run some cleansing operations - or later?
        	reviewText = CleanReviews.clean(reviewText);
        	
        	reviews.put(businessID, reviewText);
            line = reader.readLine();
			count++;
        }
        reader.close();
	
		return reviews;
	}
	
	public static void main(String args[]){
		String reviewPath = "data/Phoenix_reviews_per_business_BarsRestCafes_CHINESE.csv";
		
		try{
			HashMap<String, String> reviews = readData(reviewPath, 5);
			int reviewCtr = 0;
			for (HashMap.Entry<String, String> entry : reviews.entrySet()) {
				if (reviewCtr >= 3){
					break;
				}
			    String key = entry.getKey();
			    String value = entry.getValue();
			    System.out.println(reviewCtr + ". Reviews for: "+ key + " -> " + value);
			    reviewCtr++;
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
		}
		
	}
	
}
