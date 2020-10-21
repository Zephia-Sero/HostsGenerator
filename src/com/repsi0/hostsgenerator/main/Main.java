package com.repsi0.hostsgenerator.main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import com.wolfram.alpha.WAEngine;
import com.wolfram.alpha.WAException;
import com.wolfram.alpha.WAPlainText;
import com.wolfram.alpha.WAPod;
import com.wolfram.alpha.WAQuery;
import com.wolfram.alpha.WAQueryResult;
import com.wolfram.alpha.WASubpod;

public class Main {
	// PUT YOUR APPID HERE:
    private static String appid = "";
    public static Scanner sc;
    public static HashMap<String,ArrayList<String>> subdomains = new HashMap<String,ArrayList<String>>();
    
    public static void main(String[] args) {
    	//grab app id from file
    	File appId = new File("appid");
    	if(appId.exists()) {
    		try {
				BufferedReader br = new BufferedReader(new FileReader(appId));
				appid=br.readLine();
				br.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
    	} else {
    		try {
				appId.createNewFile();
				throw new Exception("App ID not provided!");
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(0);
			}
    	}
    	sc = new Scanner(System.in);
    	String input = "";
    	while(!input.equals("__EXIT__")) {
    		input = "__NORESULTS__";
	    	while(input.equals("__NORESULTS__")) {
	    		System.out.println("Enter site name or type q and enter to quit:");
	    		String s = sc.nextLine();
	    		if(s.equals("q")) {
	    			input="__EXIT__";break;
	    		}
	    		DDGEngine ddgeng = new DDGEngine(s);
	    		input=ddgeng.getSingleResult(sc);
	    	}
	    	if(input.equals("__EXIT__")) break;
	    	ArrayList<String> start = new ArrayList<String>(); start.add(input);
	        subdomains.put(input,start);
	        if (args.length > 0) input = args[0];
	        
	        // The WAEngine is a factory for creating WAQuery objects,
	        // and it also used to perform those queries. You can set properties of
	        // the WAEngine (such as the desired API output format types) that will
	        // be inherited by all WAQuery objects created from it. Most applications
	        // will only need to crete one WAEngine object, which is used throughout
	        // the life of the application.
	        WAEngine engine = new WAEngine();
	        
	        // These properties will be set in all the WAQuery objects created from this WAEngine.
	        engine.setAppID(appid);
	        engine.addFormat("plaintext");
	
	        // Create the query.
	        WAQuery query = engine.createQuery();
	        
	        // Set properties of the query.
	        query.setInput(input);
	        query.addPodState("WebSiteStatisticsPod:InternetData__Subdomains");
	        query.addPodState("WebSiteStatisticsPod:InternetData__Subdomains_More");
	        
	        try {
	            // For educational purposes, print out the URL we are about to send:
	            //System.out.println("Query URL:");
	            //System.out.println(engine.toURL(query));
	            //System.out.println("");
	            
	            // This sends the URL to the Wolfram|Alpha server, gets the XML result
	            // and parses it into an object hierarchy held by the WAQueryResult object.
	            WAQueryResult queryResult = engine.performQuery(query);
	            
	            if (queryResult.isError()) {
	                System.out.println("Query error");
	                System.out.println("  error code: " + queryResult.getErrorCode());
	                System.out.println("  error message: " + queryResult.getErrorMessage());
	            } else if (!queryResult.isSuccess()) {
	                System.out.println("Query was not understood; no results available.");
	            } else {
	                // Got a result.
	                //System.out.println("Successful query. Pods follow:\n");
	                for (WAPod pod : queryResult.getPods()) {
	                    if (!pod.isError()) {
	                        if(pod.getTitle().contains("Web statistics for all of")) {
		                        for (WASubpod subpod : pod.getSubpods()) {
		                        	if(subpod.getTitle().equals("Subdomains")) {
			                            for (Object element : subpod.getContents()) {
			                                if (element instanceof WAPlainText) {
			                                	String txt = ((WAPlainText) element).getText();
			                                	for(String line : txt.split("\n")) {
			                                		String finaltext=line.substring(0,line.indexOf('|')).trim();
			                                		if(finaltext.equals("subdomain"))continue;
				                                	subdomains.get(input).add(finaltext);
			                                	}
			                                }
			                            }
		                        	}
		                        }
	                        }
	                    }
	                }
	                // We ignored many other types of Wolfram|Alpha output, such as warnings, assumptions, etc.
	                // These can be obtained by methods of WAQueryResult or objects deeper in the hierarchy.
	            }
	        } catch (WAException e) {
	            e.printStackTrace();
	        }
    	}
    	if(subdomains.size()>0) {
	    	File f = new File("hosts");
	    	try {
	    		if(!f.exists()) f.createNewFile();
				BufferedWriter bw = new BufferedWriter(new FileWriter(f));
				for(String s : subdomains.keySet()) {
					//comment
					bw.write("# " + s);
					bw.newLine();
					for(String s2 : subdomains.get(s)) {
						bw.write("127.0.0.1      " + s2 + "\n");
					}
				}
				bw.flush();
				bw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
    	}
        sc.close();
    }
}
