package BookCoverDownloader;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;

public class Book extends Thread {
	private String ISBN;
	private String title;
	private ArrayList<String> contributors = new ArrayList<String>();
	private ArrayList<URL> links = new ArrayList<URL>();
	
	public Book(){

	}
	
	public void addContributor(String in){
		this.contributors.add(in);
	}
	
	
	public int getHQLinkIndex(String company){
		int index = 0;
		if(company.contains("Macmillan")){
			for(int i = 0; i < links.size(); i++){
				if(links.get(i).toString().contains("orig")){
					return i;
				}
			}
		}
		
		return index;
	}
	

	
	

	
	public String getISBN(){
		return this.ISBN;
	}
	
	public void setISBN(String ISBN){
		this.ISBN = ISBN;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	public void addLink(URL url){
		links.add(url);
	}
	
	public ArrayList<URL> getLinks(){
		return this.links;
	}
	
	
}

