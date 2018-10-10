package ie.duffyc8.searcher;

import java.io.IOException;
import java.nio.file.Paths;

import org.apache.lucene.analysis.Analyzer.TokenStreamComponents;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.StopwordAnalyzerBase;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.en.EnglishPossessiveFilter;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;

public final class StandardStemAnalyzer extends StopwordAnalyzerBase {
  private static String StopFileLocation = "cranfield/stop-word-list.txt"; 

  public StandardStemAnalyzer() throws IOException {
    super(StopwordAnalyzerBase.loadStopwordSet(Paths.get(StopFileLocation)));
  }

  protected TokenStreamComponents createComponents(String fieldName) {
    final Tokenizer source = new StandardTokenizer();
    TokenStream result = new StandardFilter(source);
    result = new EnglishPossessiveFilter(result);
    result = new LowerCaseFilter(result);
    result = new StopFilter(result, stopwords);
    result = new PorterStemFilter(result);
    return new TokenStreamComponents(source, result);
  }

  protected TokenStream normalize(String fieldName, TokenStream in) {
    TokenStream result = new StandardFilter(in);
    result = new LowerCaseFilter(result);
    return result;
  }
}