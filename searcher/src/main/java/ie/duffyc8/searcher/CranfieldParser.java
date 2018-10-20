package ie.duffyc8.searcher;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Paths;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class CranfieldParser {
    
    private Analyzer analyzer;

	public CranfieldParser(String indexDirectory)throws IOException{
        this.analyzer = new StandardStemAnalyzer();
	}

	public void processFile(String inputFile, String outputDirectory, Directory directory) throws IOException {
		BufferedReader bufferedReader = null;
		String line;
		boolean firstFile = true;
		String data = "";
		Integer counter = 1;
		Document doc = new Document();
		String type = "";
		
		// Create a new field type which will store term vector information
        FieldType ft = new FieldType(TextField.TYPE_STORED);
        ft.setTokenized(true); //done as default
        ft.setStoreTermVectors(true);
        ft.setStoreTermVectorPositions(true);
        ft.setStoreTermVectorOffsets(true);
        ft.setStoreTermVectorPayloads(true);

        // create and configure an index writer
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        IndexWriter iwriter = new IndexWriter(directory, config);

		try {
			bufferedReader = new BufferedReader(new FileReader(new File(inputFile)));
			bufferedReader.readLine();
			//ad final file at the end, after loop
			while ((line = bufferedReader.readLine()) != null) {
				
				type = getLineType(line);
				
				if(type == "index"){
					if(!firstFile){
						System.out.println(counter + ": contents : " + data);
						doc.add(new Field("contents", data, ft));
						iwriter.addDocument(doc);
					}
					firstFile =false;
					doc = new Document();
					doc.add(new StringField("fileNumber", counter.toString(), Field.Store.YES));
					counter++;
				}
				else if(type == "title"){
					data ="";
				}
				else if(type == "author"){
					System.out.println(counter + ": title : " + data);
					doc.add(new Field("title", data, ft));
					data="";
				}
				else if(type == "bibliography"){
					System.out.println(counter + ": author : " + data);
					doc.add(new Field("author", data, ft));
					data="";
				}
				else if(type == "contents"){
					System.out.println(counter + ": bibliography : " + data);
					doc.add(new Field("bibliography", data, ft));
					data="";
				}
				else{
					data+=line + " ";
				}
			}
			
			//add final file
			System.out.println(counter + ": contents : " + data);
			doc.add(new Field("contents", data, ft));
			iwriter.addDocument(doc);
			
			bufferedReader.close();
	        iwriter.close();
		}
		catch (IOException e) {
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
	}
	
	private String getLineType(String line){
		if(line.startsWith(".T")){
			return "title";
		}
		else if(line.startsWith(".A")){
			return "author";
		}
		else if(line.startsWith(".B")){
			return "bibliography";
		}
		else if (line.startsWith(".W") ){
			return "contents";
		}
		else if (line.startsWith(".I") ){
			return "index";
		}
		else{
			return "";
		}
	}
}