package ie.duffyc8.searcher;

import java.io.IOException;

import java.nio.file.Path;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;

import org.apache.lucene.util.BytesRef;

import org.apache.lucene.document.Document;

import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import org.apache.lucene.index.Term;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.Fields;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.index.PostingsEnum;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;

import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.TextField;
import org.apache.lucene.document.StringField;

import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.DocIdSetIterator;

public class QueryIndex {
    
    // Directory where the search index will be saved
    private static String INDEX_DIRECTORY = "index";
    public static String CORPUS_DIRECTORY = "cranfield";

    public static void main(String[] args) throws IOException {
        
    	CranfieldParser parser = new CranfieldParser(INDEX_DIRECTORY);
    	parser.processFile(CORPUS_DIRECTORY + "/cran.all.1400", INDEX_DIRECTORY);
    	
    	//at end
    	parser.shutdown();
    }
    
//    public void postingsDemo() throws IOException {
//        DirectoryReader ireader = DirectoryReader.open(directory);
//    
//        // Use IndexSearcher to retrieve some arbitrary document from the index        
//        IndexSearcher isearcher = new IndexSearcher(ireader);
//        Query queryTerm = new TermQuery(new Term("content","correlation"));
//        ScoreDoc[] hits = isearcher.search(queryTerm, 1).scoreDocs;
//        
//        // Make sure we actually found something
//        if (hits.length <= 0) {
//            System.out.println("Failed to retrieve a document");
//            return;
//        }
//
//        // get the document ID of the first search result
//        int docID = hits[0].doc;
//
//        // Get the fields associated with the document (filename and content)
//        Fields fields = ireader.getTermVectors(docID);
//
//        for (String field : fields) {
//            // For each field, get the terms it contains i.e. unique words
//            Terms terms = fields.terms(field);
//
//            // Iterate over each term in the field
//            BytesRef termByte = null;
//            TermsEnum termsEnum = terms.iterator();
//
//            while ((termByte = termsEnum.next()) != null) {                                
//                int id;
//
//                // for each term retrieve its postings list
//                PostingsEnum posting = null;
//                posting = termsEnum.postings(posting, PostingsEnum.FREQS);
//
//                // In spite of appearances, this only processes one document
//                // i.e the one we retrieved earlier
//                while ((id = posting.nextDoc()) != DocIdSetIterator.NO_MORE_DOCS) {
//                    // convert the term from a byte array to a string
//                    String termString = termByte.utf8ToString();
//                    
//                    // extract some stats from the index
//                    Term term = new Term(field, termString);
//                    long freq = posting.freq();
//                    long docFreq = ireader.docFreq(term);
//                    long totFreq = ireader.totalTermFreq(term);
//
//                    // print the results
//                    System.out.printf(
//                        "%-16s : freq = %4d : totfreq = %4d : docfreq = %4d\n",
//                        termString, freq, totFreq, docFreq
//                    );
//                }
//            }
//        }
//
//        // close everything when we're done
//        ireader.close();
//    }
}

