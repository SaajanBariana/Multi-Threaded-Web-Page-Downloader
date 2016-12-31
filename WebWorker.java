package WebLoader;

import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;

import javax.swing.*;

import WebLoader.WebFrame.URLStrings;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class WebWorker extends Thread {
/*
  This is the core web/download i/o code...*/
     String urlString;
     int row;
     CountDownLatch countDown;
     TableView table;
     ProgressBar bar;
     double addToProgressBar;
     ObservableList<URLStrings> list;
     Semaphore sem;
     
     public WebWorker(String url, TableView t, int c, ProgressBar b, ObservableList<URLStrings> d, CountDownLatch count, Semaphore sem) 
     {
     	urlString = url;
     	row = c;
     	table = t;
     	bar = b;
     	double size = d.size();
     	addToProgressBar = 1 / size;
     	list = d;
     	countDown = count;
     	this.sem = sem;
     }
     
     
     public synchronized void run() {
    	 try {
			countDown.await();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			System.out.println("Problem with countdown");
		}
    	 try {
			sem.acquire();
		} catch (InterruptedException e) {
			try {
				this.wait(100);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
    	 boolean inter = false;
       	System.out.println("Fetching...." + urlString);
 		InputStream input = null;
		StringBuilder contents = null;
		try {
			URL url = new URL(urlString);
			URLConnection connection = url.openConnection();
		
			// Set connect() to throw an IOException
			// if connection does not succeed in this many msecs.
			connection.setConnectTimeout(5000);
			
			connection.connect();
			input = connection.getInputStream();

			BufferedReader reader  = new BufferedReader(new InputStreamReader(input));
		
			char[] array = new char[1000];
			int len;
			contents = new StringBuilder(1000);
			while ((len = reader.read(array, 0, array.length)) > 0) {
				System.out.println("Fetching...." + urlString + len);
				contents.append(array, 0, len);
				Thread.sleep(100);
			}
			
			System.out.print(contents.toString());
			
		}
		// Otherwise control jumps to a catch...
		catch(MalformedURLException ignored) {
			System.out.println("Exception: " + ignored.toString());
			inter = true;
		}
		catch(InterruptedException exception) {
			// YOUR CODE HERE
			// deal with interruption
			inter();
			inter = true;
			System.out.println("Exception: " + exception.toString());
		}
		catch(IOException ignored) {
			System.out.println("Exception: " + ignored.toString());
			inter = true;
		}
		// "finally" clause, to close the input stream
		// in any case
		finally {
			try{
				if (input != null) input.close();
			}
			catch(IOException ignored) {}
		}
		if(!inter)
		{
			
			bar.setProgress(bar.getProgress() + addToProgressBar);
			System.out.println(addToProgressBar);
			if(bar.getProgress() > 0.9)
			{
				bar.setProgress(1);
			}
			
			list.get(row).setStatus(new SimpleStringProperty("Completed"));
			table.refresh();
		}
		sem.release();
		
	}
     
     public void inter()
     {
    	 bar.setProgress(bar.getProgress() + addToProgressBar);
		list.get(row).setStatus(new SimpleStringProperty("Interrupted"));
		table.refresh();
     }
     
   
  
	
}
