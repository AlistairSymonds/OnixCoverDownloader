package BookCoverDownloader;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class OnixHandler implements Runnable {

	private Scanner scan = null;
	private BufferedReader br = null;
	private FileReader fr = null;
	private static boolean isRunning = false;
	private Thread t;
	private int numberOfBooks;
	private String file;
	private String company;
	private ArrayList<Book> books;
	private ArrayList<String> filters = new ArrayList<String>();
	
	public OnixHandler (String file){
		try{
			System.out.println(file);
			
			fr = new FileReader(file);
			br = new BufferedReader(fr);
			scan = new Scanner(br);
			this.file = file;
			getBooks();
		} catch (FileNotFoundException e){
			System.out.println("Couldn't find file");
			System.out.println("Press ENTER to close");
			
			try{
	            System.in.read();
	        }catch(Exception e2){
	        	System.exit(0);
	        }
			System.exit(0);

		}
	}
	public void useFilter(ArrayList<String> filtersIn){
		this.filters = filtersIn;
	}
	
	public static boolean getIsRunning(){
		return isRunning;
	}
	private void resetReaders() throws IOException{
		scan.close();
		br.close();
		fr.close();
		
		fr = new FileReader(file);
		br = new BufferedReader(fr);
		scan = new Scanner(br);
	}
	
	@Override
	public void run() {
		this.books = getBooks();
		long startTime = System.currentTimeMillis();
		long booksDLd = 0;
		String strPath = makeOutputDir();
		float progStep = 1000f/books.size();
		float currentProg = 0;
		for(int i = 0; i < books.size(); i++){
			if(!books.get(i).getLinks().isEmpty()){
				if(!filters.isEmpty()){
					progStep = 1000f/filters.size();
					for(int j = 0; j < filters.size(); j++){
						if(books.get(i).getISBN().equals(filters.get(j))){
							GUI.updateStatusBox("Downloading cover for ISBN: " + books.get(i).getISBN());
							downloadImg(books.get(i).getLinks().get(books.get(i).getHQLinkIndex(company)), strPath + "/" + books.get(i).getISBN()+ ".jpg");
							booksDLd++;
							currentProg = currentProg + progStep;
							GUI.updateProgressBar((int) currentProg);
						}
					}
				}else{
					System.out.println(i);
					GUI.updateStatusBox("Downloading cover for ISBN: " + books.get(i).getISBN());
					downloadImg(books.get(i).getLinks().get(books.get(i).getHQLinkIndex(company)), strPath + "/" + books.get(i).getISBN()+ ".jpg");
					booksDLd++;
					currentProg = currentProg + progStep;
					GUI.updateProgressBar((int) currentProg);
				}
			}
			
		}
		long endTime = System.currentTimeMillis();
		long timeTaken = endTime - startTime;
		GUI.updateProgressBar(1000);
		GUI.updateStatusBox("Done! Downloaded " + booksDLd + " books in " + timeTaken/1000f + " [s]");
		System.out.println("Thread done");
	}
	
	public void start(){
		System.out.println("Starting Onix Handler");
		if(t == null){
			t = new Thread(this, "OnixHandler");
			t.start();
		}
	}
	
	public String makeOutputDir(){
		String strPath = file;
		int onixNameEnd = file.lastIndexOf(".");
		strPath = strPath.substring(0, onixNameEnd);
		Path p = Paths.get(strPath.substring(0, onixNameEnd));
		try{
			Files.createDirectories(p);
		} catch (SecurityException e){
			System.out.println("Security Exception, try running elevated or in a different directory");
		} catch (Exception e){
			System.out.println(e.toString());
		}
		return strPath;
	}
		
	public ArrayList<Book> getBooks(){
		ArrayList<Book> books = new ArrayList<Book>();
		
		try {
			resetReaders();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		String input = "";
		while(scan.hasNext()){
			input = scan.nextLine();
			if(input.contains("<FromCompany>")){
				this.company = input;
			}
			
			boolean inBook = false;
			if(input.contains("<Product>")){
				inBook = true;
				Book newBook = new Book();
				while (inBook){
					input = scan.nextLine();
					
					if(input.contains("<RecordReference>")){
						int isbnStart = input.indexOf(">") + 1;
						int isbnEnd = input.indexOf("</");
						String ISBN = input.substring(isbnStart, isbnEnd);
						newBook.setISBN(ISBN);
					}
					
					if(input.contains("<TitleText>")){
						int titleStart = input.indexOf(">") + 1;
						int titleEnd = input.indexOf("</");
						String title = input.substring(titleStart, titleEnd);
						newBook.setTitle(title);
					}
					
					if(input.contains("<PersonName>")){
						int nameStart = input.indexOf(">") + 1;
						int nameEnd = input.indexOf("</");
						String name = input.substring(nameStart, nameEnd);
						newBook.addContributor(name);
					}
					
					if(input.contains("<MediaFileLink>")){
						int linkStart = input.indexOf(">") + 1;
						int linkEnd = input.indexOf("</");
						String link = input.substring(linkStart, linkEnd);
						
						if (this.company.contains("Macmillan")){
							link = link.replaceAll("amp;", "");
						}
						
						try {
							newBook.addLink(new URL(link));
						} catch (MalformedURLException e) {
							e.printStackTrace();
						}
					}
					
					
					
					if(input.contains("</Product>")){
						books.add(newBook);
						inBook = false;
					}
				}
				
				
			}
			
		
		
		}
		
		return books;
	}
	
	private int countBooks(){
		if (fr == null || br == null){
			return -1;
		}
		Scanner tempScan = new Scanner(br);
		int books = 0;
		while(tempScan.hasNext()){
			String line = tempScan.nextLine();
			if(line.contains("RecordReference")){
				books++;
				System.out.println(books);
			}
		}
		tempScan.close();
		return books;
		
	}
	
	public void downloadImg(URL imgLoc, String saveLoc){
		
		System.out.println("Downloading from " + imgLoc);
		ReadableByteChannel rbc;
		try {
			rbc = Channels.newChannel(imgLoc.openStream());
			
			try{
				FileOutputStream fos = new FileOutputStream(saveLoc);
				fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
				fos.close();
			} catch (Exception e){
				e.printStackTrace();
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		
	}
	
	
	
	
	
}
