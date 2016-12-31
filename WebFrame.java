package WebLoader;

import java.awt.Event;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;

import com.sun.prism.paint.Color;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 *
 * @author narayan
 */
public class WebFrame extends Application {
	 boolean single = false;
     boolean multi = false;
     //static String file1;
    @SuppressWarnings("unchecked")
	public static void main(String[] args) {
        launch(args);
        
    }
	
	
    public void start(Stage stage) throws FileNotFoundException {
        TableView<URLStrings> tv = new TableView();
        ObservableList<URLStrings> data = FXCollections.observableArrayList();
        
       
        
        TableColumn URLCol = new TableColumn("URL");
        URLCol.setMinWidth(100);
        URLCol.setCellValueFactory(
                new PropertyValueFactory<URLStrings, String>("URL"));
        
        
        Scanner file = new Scanner(new File("list.txt"));
        while(file.hasNextLine())
        {
        	data.add(new URLStrings(file.nextLine(), ""));
        }
        
        int rowCount = data.size();
        
        TableColumn statusCol = new TableColumn("Status");
        statusCol.setMinWidth(100);
        statusCol.setCellValueFactory(new PropertyValueFactory<URLStrings, String>("status"));
        
        tv.getColumns().addAll(URLCol, statusCol);
        tv.setItems(data);
        tv.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        
        Scene scene = new Scene(new Group());
        stage.setTitle("Table View Sample");
        stage.setHeight(650);
        stage.setWidth(315);
        
        final VBox vbox = new VBox();
        vbox.setSpacing(5);
        vbox.setPadding(new Insets(10, 0, 10, 10));
        vbox.getChildren().addAll(tv);
        
        
        Button stop = new Button();
        Button singleThread = new Button("Start Single Thread");
        Button multiThread = new Button ("Multiple Threads (Enter in Text Box below)");
        TextArea text = new TextArea();
        Button reset = new Button("Reset");
        //text.setMaxSize(20, 1);
        text.setMinSize(0, 0);
        text.setPrefSize(100, 25);
        
        
        
        double urlCount = 1 / rowCount;
        
        ProgressBar progress = new ProgressBar(0);
       
        HBox h = new HBox();
        h.setSpacing(10);
        h.setPadding(new Insets(10, 0, 10, 10));
        
        h.getChildren().add(stop);
        h.getChildren().add(progress);
        h.getChildren().add(reset);
        
        vbox.getChildren().add(h);
        
        CountDownLatch shutdown = new CountDownLatch(1);
        vbox.getChildren().add(singleThread);
        vbox.getChildren().add(multiThread); 
        vbox.getChildren().add(text);
        
        ArrayList<Thread> threads = new ArrayList<Thread>();
        
       
        stop.setText("Stop");
        stop.setDisable(true);
        
        //add functionality
        stop.setOnAction(new EventHandler() {
           
			@Override
			public void handle(javafx.event.Event event) {
				if (single)
				{
					for(Thread w: threads)
					{
						//System.out.println("AMOUNT OF SIZE: " + threads.size());
						w.interrupt();
					}
					if (stop.getText().equals("Stop"))
					{
						stop.setText("Are you sure \nyou want to stop?\n (Click again\n to stop)");
					}
					else
					{
						progress.setProgress(1);
						stop.setDisable(true);
						reset.setDisable(false);
						singleThread.setDisable(false);
						multiThread.setDisable(false);
						single = false;
						stop.setText("Stop");
					}
				
				}
				if(multi)
				{
					if (Integer.parseInt(text.getText().trim()) >= data.size())
					{
						for(Thread w: threads)
						{
							//System.out.println("AMOUNT OF SIZE: " + threads.size());
							w.interrupt();
						}
						
							progress.setProgress(1);
							stop.setDisable(true);
							reset.setDisable(false);
							singleThread.setDisable(false);
							multiThread.setDisable(false);
							multi = false;
						}
					else
					{
						for(Thread w: threads)
						{
							//System.out.println("AMOUNT OF SIZE: " + threads.size());
							w.interrupt();
						}
						if (stop.getText().equals("Stop"))
						{
							stop.setText("Are you sure \nyou want to stop?\n (Click again\n to stop)");
						}
						else
						{
							stop.setText("Are you sure \nyou want to stop?\n (Click again\n to stop)");
							progress.setProgress(1);
							stop.setDisable(true);
							reset.setDisable(false);
							singleThread.setDisable(false);
							multiThread.setDisable(false);
							multi = false;
							stop.setText("Stop");
						}
					}
				}
				
			}
			
        });
        //System.out.println(threads.size());
        
        reset.setOnAction(new EventHandler(){

			@Override
			public void handle(javafx.event.Event arg0)
			{
				
				for(Thread w: threads)
				{
					//System.out.println("AMOUNT OF SIZE: " + threads.size());
					w.interrupt();
				}
				//System.out.println(data.size());
				for (URLStrings u : data)
				{
					u.setStatus(new SimpleStringProperty(""));
				}
				singleThread.setDisable(false);
				multiThread.setDisable(false);
				stop.setDisable(true);
				progress.setProgress(0);
				tv.refresh();
			}
        	
        });
        singleThread.setOnAction(new EventHandler() {
            
			@Override
			public void handle(javafx.event.Event event) {
				single = true;
				singleThread.setDisable(true);
				reset.setDisable(true);
				multiThread.setDisable(true);
				stop.setDisable(false);
        		Semaphore sem = new Semaphore(1);
        		long begin = System.currentTimeMillis();
        		CountDownLatch countdown = new CountDownLatch(1);
        		for(int i = 0; i < rowCount; i++)
        		{
        			URLStrings u = data.get(i);
        			WebWorker w = new WebWorker(u.getURL(), tv, i, progress, data, countdown, sem);
        			u.setStatus(new SimpleStringProperty("Fetching"));
        			Thread thread = new Thread(w);
	        		threads.add(thread);
	        		thread.start();
	        		System.out.println("Semaphore exception with single thread");	
        		}
        		int end = (int) (System.currentTimeMillis() - begin);
    			System.out.println("This took " + end + " millisecond(s)");
        		countdown.countDown();
        		reset.setDisable(false);
			}	
		
			
        });
       
        multiThread.setOnAction(new EventHandler(){
            
			@Override
			public void handle(javafx.event.Event event) 
			{
				multi = true;
				singleThread.setDisable(true);
				multiThread.setDisable(true);
				reset.setDisable(true);
				stop.setDisable(false);
				int amount = 0;
				try
				{
					amount = Integer.parseInt(text.getText());
				}
				catch(Exception e)
				{
					System.out.println("Please enter a valid number into the Text Box");
					
				}
    	        Semaphore sem = new Semaphore(amount);	
    	        CountDownLatch countdown = new CountDownLatch(1);
    	        long begin = System.currentTimeMillis();
        		for (int i = 0; i < rowCount; i++)
        		{
        			URLStrings u = data.get(i);
        			WebWorker w = new WebWorker(u.getURL(), tv, i, progress, data, countdown, sem);
        			u.setStatus(new SimpleStringProperty("Fetching"));
        			Thread t = new Thread(w);
					threads.add(t);
					t.start();
        		
        		}
        		int total = (int) (System.currentTimeMillis() - begin);
				System.out.println("This takes " + total + " millisecond(s)");
				countdown.countDown();
			}
			
        });
		

        ((Group) scene.getRoot()).getChildren().addAll(vbox);
        stage.setScene(scene);
        stage.show();
        
      
    }

    /**
     * @param args the command line arguments
     */
   
    


    public static class URLStrings {
        StringProperty URL;
        StringProperty status;
        URLStrings(String URL, String status) {
            this.URL = new SimpleStringProperty(URL);
            this.status = new SimpleStringProperty(status);
        }
        public String getURL() {
            return URL.get();
        }

        public String getStatus() {
            return status.get();
        }
        public void setStatus(StringProperty stat)
        {
        	status = stat;
        }
    }
}