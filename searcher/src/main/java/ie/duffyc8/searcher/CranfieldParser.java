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
    private Directory directory;

	public CranfieldParser(String indexDirectory)throws IOException{
        this.analyzer = new StandardStemAnalyzer();
        this.directory = FSDirectory.open(Paths.get(indexDirectory));
	}

	public void processFile(String inputFile, String outputDirectory) throws IOException {
		BufferedReader bufferedReader = null;
		String line;
		boolean firstFile = true;
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

			while ((line = bufferedReader.readLine()) != null) {
				
				if(line.startsWith(".I")){
					if(!firstFile){
						iwriter.addDocument(doc);
					}
					doc = new Document();
					firstFile =false;
					doc.add(new StringField("fileNumber", counter.toString(), Field.Store.YES));
					
					System.out.printf("Indexing \"%s\"\n", counter);
					counter++;
				}
				else if(getLineType(line)==""){
					doc.add(new Field(type, line, ft));
				}
				else{
					type = getLineType(line);
				}
			}
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
		else{
			return "";
		}
	}
	
	public void shutdown() throws IOException {
        directory.close();
    }
}