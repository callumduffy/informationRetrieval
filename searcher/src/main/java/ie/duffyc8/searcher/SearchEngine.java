package ie.duffyc8.searcher;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.apache.lucene.analysis.Analyzer;

import org.apache.lucene.document.Document;

import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.index.DirectoryReader;

import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.search.IndexSearcher;

public class SearchEngine {
    
    // Directory where the search index will be saved
    public static String INDEX_DIRECTORY = "index";
    public static String CORPUS_DIRECTORY = "cranfield";
    public static String QUERY_DIRECTORY = "query";
    
    //scoring type
    public static int VECTOR_SPACE_MODEL = 0;
    public static int BM_25 = 1;
    
 // Limit the number of search results we get
 	private static int MAX_RESULTS = 20;
    
    private static Directory indexDirectory;
    private static Directory queryDirectory;
    private static Analyzer analyzer;
    private static CranfieldQueryParser queryParser;

    public static void main(String[] args) throws IOException, ParseException {
    	
    	ArrayList<String> querystrings = new ArrayList<String>();
    	analyzer = new StandardStemAnalyzer();
    	
    	indexDirectory = FSDirectory.open(Paths.get(INDEX_DIRECTORY));
    	queryDirectory = FSDirectory.open(Paths.get(QUERY_DIRECTORY));
    	
        //create index for cranfield files
    	CranfieldParser fileParser = new CranfieldParser(INDEX_DIRECTORY);
    	fileParser.processFile(CORPUS_DIRECTORY + "/cran.all.1400", INDEX_DIRECTORY, indexDirectory);
    	
    	//create index for the cranfield queries
    	queryParser = new CranfieldQueryParser(analyzer);
    	querystrings = queryParser.readFile(CORPUS_DIRECTORY + "/cran.qry");
    	
    	//score the docs, leave one you want uncommented
    	//scoreDocs(BM_25, querystrings);
    	scoreDocs(VECTOR_SPACE_MODEL, querystrings);
    	
    	//at end
    	shutdown();
    }
    
    private static void scoreDocs(int type, ArrayList<String> querystrings) throws IOException, ParseException{
    	
    	//loop to actually search index dir and score
    	DirectoryReader ireader = DirectoryReader.open(indexDirectory);
		IndexSearcher isearcher = new IndexSearcher(ireader);
		Query query;
		int queryIndex = 1;
    	
    	if(type == VECTOR_SPACE_MODEL){ //classic similarity implements TFIDF with VSP
    		isearcher.setSimilarity(new ClassicSimilarity());
    	}
    	else if(type == BM_25){
    		isearcher.setSimilarity(new BM25Similarity());
    	}
    	else{
    		System.out.println("Invalid scoring integer param.");
    	}
    	
    	File fout = new File("search_results.txt");
	     
    	 BufferedWriter bw = new BufferedWriter(new FileWriter(fout));
    	
    	try{
        	for(String querystring : querystrings){
        		query = queryParser.parseQuery(querystring);
        		
        		ScoreDoc[] hits = isearcher.search(query, MAX_RESULTS).scoreDocs;

        		//write results to file for eval
        		writeResultsFile(hits, isearcher, queryIndex, type, bw);
        		
    			queryIndex++;
        	}
    	}catch(Exception e){
    		e.printStackTrace();
    	} finally {
    		try{
    			bw.close();
    		}catch(Exception e){
    			
    		}
    	}
    }
    
    public static void writeResultsFile(ScoreDoc[] hits, IndexSearcher isearcher, int queryIndex,
    		int type, BufferedWriter bw) throws IOException{
     
    	//format = Query 0 FileNum Rank(0-30) Score 'EXP'
    	for (int i = 0; i < hits.length; i++) {
    		Document hitDoc = isearcher.doc(hits[i].doc);
    		String line = (queryIndex) + " 0 " + hitDoc.get("fileNumber") + " " + (i+1) + " "
    				+ normaliseScore(hits[i].score, type) + " EXP" +" \n";
    		bw.write(line);
    		System.out.println(line);
    	}
     
    	if(queryIndex == 225){
    		bw.close();
    	}
    }
    
    public static int normaliseScore(float score, int type){
    	if(type == BM_25){
    		if(score >= 12.5){
        		return 5;
        	}
        	else if(score >= 10){
        		return 4;
        	}
        	else if(score >= 8){
        		return 3;
        	}
        	else if(score >= 6){
        		return 2;
        	}
        	else if(score >= 4.5){
        		return 1;
        	}
        	else{
        		return 0;
        	}
    	}
    	else{
    		if(score>=5){
    			return 5;
    		}
    		else{
    			return Math.round(score);
    		}
    	}
    }
    
    public static void shutdown() throws IOException {
    	indexDirectory.close();
    	queryDirectory.close();
    }
}

