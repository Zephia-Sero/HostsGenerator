package com.repsi0.hostsgenerator.main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class DDGEngine {
	public final String apiBase = "https://html.duckduckgo.com/html/?q=";
	public String apiLink;
	public ArrayList<String> results;
	public DDGEngine(String query) {
		apiLink = apiBase + query;
		results=getResults();
	}
	public void setQuery(String query) {
		apiLink = apiBase + query;
	}
	public String getSingleResult(Scanner sc) {
		if(results!=null&&results.size()>0) {
			System.out.println("Results from DuckDuckGo (" + apiLink + ")");
			if(results.size()>1) {
				System.out.println("Enter number for correct result (1-"+results.size()+") or enter 'c' (no quotes) to cancel:");
				for(int i = 0; i < results.size(); i++) {
					System.out.println("["+(i+1)+"] "+results.get(i));
				}
				System.out.println();
				String input="";
				while(!("123456789".contains(input) && input.length()==1)) {
					input=sc.nextLine();
					if(input.equals("c")) return "__NORESULTS__";
				}
				return results.get(Integer.parseInt(input)-1);
			} else {
				System.out.println("Found one result. Continue using it? (y/n)");
				System.out.println(results.get(0));
				System.out.println();
				String input="";
				while(!(input.equals("y")||input.equals("n"))) {
					input=sc.nextLine();
				}
				if(input.equals("y")) {
					return results.get(0);
				} else return "__NORESULTS__";
			}
		} else {
			System.out.println("Could not find any results. Press enter to try again, or type q and enter to save changes and quit.");
			String s = sc.nextLine();
			if(s.equals("q")) {
				sc.close();
				return "__EXIT__";
			}
			return "__NORESULTS__";
		}
	}
	public ArrayList<String> getResults() {
		try {
			Document doc = Jsoup.connect(apiLink).get();
			ArrayList<Element> es = doc.getElementsByClass("result__url");
			
			ArrayList<String> output = new ArrayList<String>();
			int offset = 0;
			if(es.size() == 0) return output;
			if(es.get(0).attr("href").length()==0) return output;
			for(int i = 0; i < (es.size() > 9 ? 9 : es.size()); i++) {
				if(i+offset>=es.size()) break;
				String txt = es.get(i+offset).attr("href");
				String txt2 = txt.substring(txt.indexOf("//")+2);
				String txt3 = txt2.substring(0,txt2.indexOf("/"));
				boolean isDuplicate=false;
				for(String s : output) {
					if(s.equals(txt3))isDuplicate=true;
				}
				if(!isDuplicate)output.add(txt3);
				else offset++;
			}
			return output;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
