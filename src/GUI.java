package OnixCoverDownloader;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Button;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Scanner;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Label;

public class GUI {

	/**
	 * Launch the application.
	 * @param args
	 */
	static String fileChosen = "";
	static String filterChosen = "";
	static boolean firstFileChosen = false;
	static boolean firstFilterChosen = false;
	static Shell shlOnixCoverDownloader;
	static ProgressBar progressBar;
	static Label statusLbl;
	
	public static void main(String[] args) {
		Display display = Display.getDefault();
		shlOnixCoverDownloader = new Shell();
		shlOnixCoverDownloader.setSize(450, 300);
		shlOnixCoverDownloader.setText("Onix Cover Downloader");
		
		
		
		Button btnDoStuff = new Button(shlOnixCoverDownloader, SWT.NONE);
		
		Button useFilterCheckBox = new Button(shlOnixCoverDownloader, SWT.CHECK);
		useFilterCheckBox.setEnabled(false);
		useFilterCheckBox.setBounds(331, 41, 93, 16);
		useFilterCheckBox.setText("Filter by ISBN");
		
		
		btnDoStuff.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {

					OnixHandler Onix = new OnixHandler(fileChosen);
					
					if(firstFilterChosen && useFilterCheckBox.getSelection()){
						try {
							FileReader fr = new FileReader(filterChosen);
							BufferedReader br = new BufferedReader(fr);
							Scanner scan = new Scanner(br);
							
							ArrayList<String> filters = new ArrayList<String>();
							while(scan.hasNext()){
								filters.add(scan.nextLine());
							}
							scan.close();
							Onix.useFilter(filters);
						} catch (FileNotFoundException e1) {
							e1.printStackTrace();
						}
						
					}
					Onix.start();
					
				
			}
		});
		btnDoStuff.setBounds(10, 41, 135, 25);
		btnDoStuff.setText("Download Covers");
		
		
		
		Button btnChooseOnixFile = new Button(shlOnixCoverDownloader, SWT.NONE);
		btnChooseOnixFile.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				   FileDialog dialog = new FileDialog(shlOnixCoverDownloader, SWT.OPEN);
				   dialog.setFilterExtensions(new String [] {"*.xml"});
				   fileChosen = dialog.open();
				   firstFileChosen = true;
				   System.out.println(fileChosen);
			}
		});
		btnChooseOnixFile.setBounds(10, 10, 135, 25);
		btnChooseOnixFile.setText("Choose ONIX file");
		
		progressBar = new ProgressBar(shlOnixCoverDownloader, SWT.NONE);
		progressBar.setMaximum(1000);
		progressBar.setBounds(10, 234, 414, 17);
		
		statusLbl = new Label(shlOnixCoverDownloader, SWT.BORDER);
		statusLbl.setBounds(10, 175, 414, 40);
		
		
		
		Button isbnFilterBtn = new Button(shlOnixCoverDownloader, SWT.NONE);
		isbnFilterBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog dialog = new FileDialog(shlOnixCoverDownloader, SWT.OPEN);
				   dialog.setFilterExtensions(new String [] {"*.txt"});
				   filterChosen = dialog.open();
				   firstFilterChosen = true;
				   System.out.println(filterChosen);
			}
		});
		isbnFilterBtn.setBounds(309, 10, 115, 25);
		isbnFilterBtn.setText("Choose ISBN filter");
		
		Button btnHelp = new Button(shlOnixCoverDownloader, SWT.NONE);
		btnHelp.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Help helpWindow = new Help();
				helpWindow.open();
			}
		});
		btnHelp.setBounds(349, 144, 75, 25);
		btnHelp.setText("HELP");

		
		shlOnixCoverDownloader.open();
		shlOnixCoverDownloader.layout();
		while (!shlOnixCoverDownloader.isDisposed()) {
			if (!display.readAndDispatch()) {
				if(firstFileChosen){
					btnDoStuff.setEnabled(true);
				} else {
					btnDoStuff.setEnabled(false);
				}
				
				if(firstFilterChosen){
					useFilterCheckBox.setEnabled(true);
				} else {
					useFilterCheckBox.setEnabled(false);
				}
				display.sleep();
			}
		}
	}
	
	public static void updateProgressBar(int prog) //prog is from 0 - 100
	{
	    new Thread(new Runnable()
	    {
	    	boolean jobDone = false;
	        private int progress = prog;
	        @Override
	        public void run()
	        {
	            while (!progressBar.isDisposed() && !jobDone)
	            {
	                Display.getDefault().asyncExec(new Runnable()
	                {
	                    @Override
	                    public void run()
	                    {
	                        if (!progressBar.isDisposed() && !jobDone){
	                        	progressBar.setSelection(progress);
	                        	jobDone = true;
	                        }
	                            
	                    }
	                });
	                
	                try
	                {
	                    Thread.sleep(1000);
	                }
	                catch (InterruptedException e)
	                {
	                    e.printStackTrace();
	                }
	            }
	        }
	    }).start();
	}
	
	public static void updateStatusBox(String status){
		new Thread(new Runnable()
	    {
			boolean jobDone = false;
	        @Override
	        public void run()
	        {
	        	
	            while (!statusLbl.isDisposed() && jobDone == false)
	            {
	                Display.getDefault().asyncExec(new Runnable()
	                {
	                    @Override
	                    public void run()
	                    {
	                        if (!statusLbl.isDisposed() && jobDone == false){
	                        	statusLbl.setText(status);
	                        	jobDone = true;
	                        	
	                        }
	                        	
	                    }
	                });
	                
	                try
	                {
	                    Thread.sleep(1000);
	                }
	                catch (InterruptedException e)
	                {
	                    e.printStackTrace();
	                }
	               

	            }
	        }
	    }).start();
		
	}
}
