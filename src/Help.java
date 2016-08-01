package OnixCoverDownloader;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;

public class Help {

	/**
	 * Launch the application.
	 * @param args
	 */
	
	/**
	 * Open the window.
	 * @wbp.parser.entryPoint
	 */
	public void open() {
		Display display = Display.getDefault();
		Shell shlOnixCoverDownloader = new Shell();
		shlOnixCoverDownloader.setSize(652, 208);
		shlOnixCoverDownloader.setText("Onix Cover Downloader - HELP");
		FillLayout fl_shlOnixCoverDownloader = new FillLayout(SWT.HORIZONTAL);
		fl_shlOnixCoverDownloader.marginWidth = 10;
		fl_shlOnixCoverDownloader.marginHeight = 10;
		shlOnixCoverDownloader.setLayout(fl_shlOnixCoverDownloader);
		
		Label lblNewLabel = new Label(shlOnixCoverDownloader, SWT.WRAP);
		lblNewLabel.setText("'Choose Onix File' - use to select ONIX file\n 'Download Covers' - Downloads the covers in the selected ONIX file, progress in shown down the bottom\n"
				+ "'Choose ISBN filter' - choose a text with ISBN's (One per line) to be used as a whitelist\n"
				+ "'Use filter' - uses the ISBN filter selected by 'Choose ISBN filter'\n"
				+ "\n \n" 
				+ "The files will be saved in a folder with the same name as the ONIX file in the same folder as the ONIX file");

		shlOnixCoverDownloader.open();
		shlOnixCoverDownloader.layout();
		while (!shlOnixCoverDownloader.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

}
