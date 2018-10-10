package ie.duffyc8.searcher;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.Query;

public class CranfieldQueryParser {
	
	private MultiFieldQueryParser parser;
	
	public CranfieldQueryParser(Analyzer analyzer){
		 parser = new MultiFieldQueryParser(
                new String[]{"title", "contents", "author"},
                analyzer);
	}
	
	public Query parseQuery(String querystring) throws ParseException{
		querystring.trim();
		return parser.parse(querystring);
	}
	
	public ArrayList<String> readFile(String inputFile){
		BufferedReader bufferedReader = null;
		String line;
		String querystring = "";
		ArrayList<String> strings = new ArrayList<>();
		
		try{
			bufferedReader = new BufferedReader(new FileReader(new File(inputFile)));
			bufferedReader.readLine();

			while ((line = bufferedReader.readLine()) != null) {
				if(line.startsWith(".I")){
					System.out.println("line: " + querystring);
					strings.add(querystring);
				}
				else if(line.startsWith(".W")){
					querystring ="";
				}
				else{
					line.replace('\n', ' ');
					querystring += line;
				}
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			if (bufferedReader != null)
				try {
					bufferedReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		return strings;
	}
}
