package com.thinkbeyond.lucenehelp;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

public class LuceneIndexer implements ServletContextListener {
	 
	private IndexWriter writer;
	private ArrayList<File> queue = new ArrayList<File>();
	
	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void contextInitialized(ServletContextEvent context) {
		
		String currentDirectory = context.getServletContext().getRealPath("/");
		
		//create an index file in the current directory
		System.out.println("Current Directory : " + currentDirectory);
		
		//Index the content directory
		String indexPath = currentDirectory+"WEB-INF\\content";
		// <context-root>/WEB-INF/content
		try {
			FSDirectory directoryToIndex = FSDirectory.open(
					new File(indexPath));
		
			StandardAnalyzer analyzer = new StandardAnalyzer(Version.LUCENE_36);
			IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_36,analyzer);
			writer = new IndexWriter(directoryToIndex, config);
			
			
			indexFileOrDirectory(indexPath);
			
			closeIndex();
			
		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		 
		
		
	}
	
	
	public void indexFileOrDirectory(String fileName) throws IOException {
	    //===================================================
	    //gets the list of files in a folder (if user has submitted
	    //the name of a folder) or gets a single file name (is user
	    //has submitted only the file name)
	    //===================================================
	    addFiles(new File(fileName));
	   
	    int originalNumDocs = writer.numDocs();
	    for (File f : queue) {
	      FileReader fr = null;
	      try {
	        Document doc = new Document();

	        //===================================================
	        // add contents of file
	        //===================================================
	        fr = new FileReader(f);
	        doc.add(new Field("contents", fr));

	        //===================================================
	        //adding second field which contains the path of the file
	        //===================================================
	        doc.add(new Field("path", f.getName(),
	                Field.Store.YES,
	                Field.Index.NOT_ANALYZED));

	        writer.addDocument(doc);
	        System.out.println("Added: " + f);
	      } catch (Exception e) {
	        System.out.println("Could not add: " + f);
	      } finally {
	        fr.close();
	      }
	    }
	   
	    int newNumDocs = writer.numDocs();
	    System.out.println("");
	    System.out.println("************************");
	    System.out.println((newNumDocs - originalNumDocs) + " documents added.");
	    System.out.println("************************");

	    queue.clear();
	  }

	  private void addFiles(File file) {

	    if (!file.exists()) {
	      System.out.println(file + " does not exist.");
	    }
	    if (file.isDirectory()) {
	      for (File f : file.listFiles()) {
	        addFiles(f);
	      }
	    } else {
	      String filename = file.getName().toLowerCase();
	      //===================================================
	      // Only index text files
	      //===================================================
	      if (filename.endsWith(".htm") || filename.endsWith(".html") ||
	              filename.endsWith(".xml") || filename.endsWith(".txt")) {
	        queue.add(file);
	      } else {
	        System.out.println("Skipped " + filename);
	      }
	    }
	  }

	  /**
	   * Close the index.
	   * @throws java.io.IOException
	   */
	  public void closeIndex() throws IOException {
	    writer.close();
	  }
	}
	  
	  

