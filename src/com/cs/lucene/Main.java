package com.cs.lucene;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.Fields;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.Version;

public class Main {

	/**
	 * @param args
	 * @author SeleenaJ
	 */
	
	static Corpus CC = new Corpus();
	static Baseline_Sim  BS = new Baseline_Sim();
	static Improved_BM25 IBM = new Improved_BM25();
	
	
	static IndexReader reader_r;
	static IndexSearcher searcher_r;
	
	static IndexReader reader_t;
	static IndexSearcher searcher_t;
	
	// static IndexReader reader_c;
	// static IndexSearcher searcher_c;
	
	static Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_41);
	static QueryParser parser = new QueryParser(Version.LUCENE_41, "content", analyzer);
	
	//Initial lucene searcher
		public static void Initial(String r_index_path, String t_index_path) throws IOException{
			reader_r = DirectoryReader.open(FSDirectory.open(new File(r_index_path)));
			searcher_r = new IndexSearcher(reader_r);
			
			reader_t = DirectoryReader.open(FSDirectory.open(new File(t_index_path)));
			searcher_t = new IndexSearcher(reader_t);
			
		}
		
		// public static void E_Initial(String c_index_path) throws IOException{
		// 	reader_c = DirectoryReader.open(FSDirectory.open(new File(c_index_path)));
		// 	searcher_c = new IndexSearcher(reader_c);
		// }
		
	//Get query
		static Map<String, Double>q_weight = new HashMap<String, Double>();
		
		public static List<String> GetQuery(String film) throws IOException{
			List<String> query_words = new ArrayList<String>();
			
			Fields fields = MultiFields.getFields(reader_t);
		      Terms terms = fields.terms("content");
		      Map<String, long[]> collection = CC.TWeight_Col(reader_t, terms);
		      Map<String, Double> collection_idf = new HashMap<String, Double>();
		      
		      for (String t : collection.keySet()){
		    	  long d = collection.get(t)[1];
		    	  Double idf = Math.log10((reader_t.numDocs() + 1) / d );
		    	  collection_idf.put(t, idf);
		      }
		      
		      List<String> function_words = CC.Down_t_terms(150, collection_idf);
		      
		      Map<String, Integer> t_tf = new HashMap<String, Integer>();
		      
		      for (int i = 0 ; i < reader_t.maxDoc() ; i ++){
		    	  Document doc = reader_t.document(i);
		          String docId = doc.get("doc_id");
		          if (docId.matches(film)){
		        	  Terms rterms = reader_t.getTermVector(i, "content"); 
		        	  t_tf = CC.TF_Doc(rterms);
		          }
//	        	  Terms rterms = reader_t.getTermVector(i, "content"); 
//	        	  t_tf = CC.TF_Doc(rterms);
		      }
		      
		      Map<String, Double> T_weight = new HashMap<String, Double>();
		      Map<String, Double> T_tf = new HashMap<String, Double>();
		      for (int j = 0 ; j < function_words.size() ; j ++){
		    	  if (t_tf.containsKey(function_words.get(j)))
		    		  t_tf.remove(function_words.get(j));
		      }
		      
		      for (String tkey : t_tf.keySet()){
		    	  if (tkey.matches("Doc_Length"))
		    		  continue;
		    	  
		    	  if (t_tf.get(tkey) != null)
		    	  	  T_weight.put(tkey, 1.0 * t_tf.get(tkey)*collection_idf.get(tkey));
		    		  T_tf.put(tkey, 1.0 * t_tf.get(tkey));
		    	}
		      
		      query_words = CC.Top_t_terms(5, T_tf);
		      List<String> temp = CC.Top_t_terms(300, T_weight);
		      
		      int count = 0;
		      for (int k = 0 ; k < temp.size() ; k ++){
		    	  if (query_words.contains(temp.get(k)))
		    		  continue;
		    	  else{
					  query_words.add(temp.get(k));
					  count ++;
		    	  }
		    	  if (count > 295)
		    		  break;
		      }
		      
		      for (int m = 0 ; m < query_words.size() ; m ++){
		    	  q_weight.put(query_words.get(m), T_tf.get(query_words.get(m)));
		      }
			  
		      return query_words;
		}
		
	//WriteFile
		public static void writeFile(String lexeme, String filePath)  {
			try {
			File file = new File(filePath);
			if (!file.exists()) {
				file.createNewFile();
			}
			BufferedWriter output = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true)));
			output.write(lexeme + "\r\n");
			output.close();
			}catch(Exception e) {
				e.printStackTrace();
			}
	 }
	public static void GetCorpus (String film) throws IOException{
		String towrite = "";
		List<String> query_w = GetQuery(film);
		for (String Tkey : q_weight.keySet()){
			int Tkey_tf = q_weight.get(Tkey).intValue();
			for (int i = 0 ; i < Tkey_tf ; i ++){
				towrite += Tkey + " ";
			}
		}
		writeFile(towrite, "f:/cs410data" + film + "_trans.txt");
	}
	
	public static Map<String, Double> getCol_Weight (IndexReader reader) throws IOException{
		Map<String, Double> result = new HashMap<String, Double>();
		Fields fields = MultiFields.getFields(reader);
	    Terms terms = fields.terms("content");
	    Map<String, long[]> collection = CC.TWeight_Col(reader, terms);
	    for (String tkey : collection.keySet()){
	    	long tf = collection.get(tkey)[0];
	    	long df = collection.get(tkey)[1];
	    	Double weight = 1.0 * tf * Math.log10((reader.numDocs() + 1) / df );
	    	result.put(tkey, weight);
	    }
		return result;
	}
	
	public static void main(String[] args) throws IOException, ParseException {
		// TODO Auto-generated method stub
		
		String r_index_path = "f:/cs410project/Reviews_Index/THILI";
		String t_index_path = "f:/cs410project/Transcripts_Index/THILI";
		Initial(r_index_path, t_index_path);
		
		/*BaseLine*/
		List<String> query_w = GetQuery("THILI");
		Map<String, Float> BM_sim = BS.Sim_Search(reader_r, searcher_r, query_w, "3");
		Map<String, Float> JM_sim = BS.Sim_Search(reader_r, searcher_r, query_w, "2");
		Map<String, Float> DIR_sim = BS.Sim_Search(reader_r, searcher_r, query_w, "1");
		
		for (int i = 0 ; i < reader_r.maxDoc() ; i ++){
			Document doc = reader_r.document(i);
			String docId = doc.get("doc_id");
			String towrite_1 = "";
			//DIR
			if (DIR_sim.containsKey(docId))
				towrite_1 = docId + "," + DIR_sim.get(docId);
			else 
				towrite_1 = docId + ",0";
			
			writeFile(towrite_1, "f:/cs410data/Baseline_DIR.csv");
			
			//JM
			String towrite_2 = "";
			if (JM_sim.containsKey(docId))
				towrite_2 = docId + "," + JM_sim.get(docId);
			else 
				towrite_2 = docId + ",0";
			
			writeFile(towrite_1, "f:/cs410data/Baseline_JM.csv");
			
			String towrite_3 = "";
			if (BM_sim.containsKey(docId))
				towrite_3 = docId + "," + BM_sim.get(docId);
			else 
				towrite_3 = docId + ",0";
			
			writeFile(towrite_1, "f:/cs410data/Baseline_BM25.csv");
		}
		
		/*End BaseLine*/
		
		/*Improved_BM*/
		
		int avgl = CC.avg_Length(reader_r);
		
		for (int j = 0 ; j < reader_r.maxDoc(); j ++ ){
			Map<String, Double[]> r_tweight = new HashMap<String, Double[]>();
			Double[] weight = new Double[3];
			
			Document doc = reader_r.document(j);
			String docId = doc.get("doc_id");
			Terms rterms = reader_r.getTermVector(j, "content");
			if (rterms == null){
				//System.out.println(docId + " , 0.0");
				writeFile(docId + " , 0.0", "f:/cs410data/Improved_BM25.csv");
				continue;
			}
			else{
				Map<String, Integer> rtf_doc = CC.TF_Doc(rterms);
				Map<String, long[]> rweight_col = CC.TWeight_Col(reader_r, rterms);
				int docl = rtf_doc.get("Doc_Length");
				
				for (String rtkey : rtf_doc.keySet()){
					if (rtkey.matches("Doc_Length"))
						continue;
					int tf_d = rtf_doc.get(rtkey);
					long tf_c = rweight_col.get(rtkey)[0];
					long df_c = rweight_col.get(rtkey)[1];
					Double idf = Math.log10((reader_r.numDocs() + 1) / df_c );
					
					weight[0] = 1.0*tf_d;
					weight[1] = 1.0 * tf_c;
					weight[2] = idf;
					r_tweight.put(rtkey, weight);
				}
				
				Double r_BM = IBM.BM25(0.5, 1.6, 0.75, 1.2, avgl, docl, r_tweight, q_weight);
				//System.out.println(docId + " , " + r_BM );
				writeFile(docId + " , " + r_BM, "f:/cs410data/Improved_BM25.csv");
			}
			
		}
		
		/*End Improved BM*/

	}

}
