package com.thinkbeyond.lucenehelp;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

public class LuceneHelpDocFinderServlet extends HttpServlet {
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		System.out.println("Request received");
		
		String queryString=request.getParameter("query");
		
		System.out.println("Query for "+ queryString);
		
		String docBase = request.getRealPath("/")+"WEB-INF\\content";
		
		ServletOutputStream out = response.getOutputStream();
		
		try{
		
		System.out.println("Will search for index at :"+ docBase);
		
		 Directory directory =  FSDirectory.open(new File(docBase)); 
		  IndexReader reader = IndexReader.open(directory);
		  
		    IndexSearcher searcher = new IndexSearcher(reader);
		    Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_36);
			QueryParser queryParser = new QueryParser(Version.LUCENE_36,"contents", analyzer);
			Query query = queryParser.parse(queryString);
			
			long beginTime = System.currentTimeMillis();
			TopDocs hits = searcher.search(query,10);
			long finishedIn = System.currentTimeMillis() - beginTime;
		
			
			System.out.println(hits.totalHits);
			
			out.println("<h3>Found " + hits.totalHits+ " matching documents containing " +
					" searched item '"+ queryString +"'</h3>");
			
			out.println("<p>which took "+ finishedIn + " ms to execute</p>");
			
			ScoreDoc[] scoreDocs = hits.scoreDocs;
			
			StringBuilder searchResult = new StringBuilder();
			searchResult.append("<p>");
			
			if(scoreDocs.length > 0 ){
				searchResult.append("<ul>Match Found at : ");
				
				for(ScoreDoc doc : scoreDocs){
					Document matchedDoc = searcher.doc(doc.doc);
					searchResult.append("<li>" + matchedDoc.get("path")).append("</li>");
					
					
				}
				
				searchResult.append("</ul>");
				
			}
			searchResult.append("</p>");
			
			out.println(searchResult.toString());
			
			searcher.close();
			
		}  catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		
		
	}

}
