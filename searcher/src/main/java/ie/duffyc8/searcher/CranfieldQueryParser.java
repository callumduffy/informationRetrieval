package ie.duffyc8.searcher;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.Query;

public class CranfieldQueryParser {
	
	private MultiFieldQueryParser parser;
	
	public CranfieldQueryParser(Analyzer analyzer){
		 parser = new MultiFieldQueryParser(
                new String[]{"title", "contents","bibliography","author"},
                analyzer, boost());
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
		int count =1;
		
		try{
			bufferedReader = new BufferedReader(new FileReader(new File(inputFile)));
			bufferedReader.readLine();

			while ((line = bufferedReader.readLine()) != null) {
				if(line.startsWith(".I")){
					System.out.println();
					System.out.println("line: " + count + ": " + querystring);
					count++;
					strings.add(querystring);
				}
				else if(line.startsWith(".W")){
					querystring ="";
				}
				else{
					if(line.contains("?")){
						line = line.replace("?","");
					}
					querystring += line +  " ";
				}
			}
			//add 225th query
			strings.add(querystring);
			
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
	
	public static Map<String, Float> boost(){
		Map<String, Float> boostMap = new HashMap();
		boostMap.put("title", (float) 0.40);
		boostMap.put("author", (float) 0.1);
		boostMap.put("bibliography", (float) 0.04);
		boostMap.put("contents", (float) 0.46);
		return boostMap;
	}
}
