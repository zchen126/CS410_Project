package com.cs.lucene;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.lucene.index.Fields;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.util.BytesRef;

public class Corpus {
	
	//Get tf,df for each term in whole collection
		public static Map<String, long[]> TWeight_Col(IndexReader reader, Terms terms){
			Map<String, long[]> TWC = new HashMap<String, long[]>();
			
			try {
				TermsEnum termsEnum = terms.iterator(null);
				BytesRef text = null;
				while ((text = termsEnum.next()) != null) {
				    String term = text.utf8ToString();
				    long[] tfdf= new long[2];
				    Term termInstance = new Term("content", term);                              
				    long termFreq = reader.totalTermFreq(termInstance);
				    long termdf = reader.docFreq(termInstance);
				    tfdf[0] = termFreq;
				    tfdf[1] = termdf;
				    
				    TWC.put(term, tfdf);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return TWC;
		}
		
		//Get tf for each term within a document
		public static Map<String, Integer> TF_Doc (Terms terms) throws IOException{
			Map<String, Integer> tf_d = new HashMap<String, Integer>();
			int Doc_length = 0;
			
			TermsEnum termsEnum = terms.iterator(null);
			
			BytesRef text = null;
			while ((text = termsEnum.next()) != null) {
				String term = text.utf8ToString();
				int freq = (int) termsEnum.totalTermFreq();
				Doc_length += freq;
				tf_d.put(term, freq);
			}
			
			tf_d.put("Doc_Length", Doc_length);
			return tf_d;
		}
		
		//Get Avg_Doc_Length
		public static Integer avg_Length (IndexReader reader) throws IOException{
			int avgl = 0;
			long suml = 0;
			Fields fields = MultiFields.getFields(reader);
		    Terms terms = fields.terms("content");
		    try {
				TermsEnum termsEnum = terms.iterator(null);
				BytesRef text = null;
				while ((text = termsEnum.next()) != null) {
				    String term = text.utf8ToString();
				    Term termInstance = new Term("content", term);                              
				    long termFreq = reader.totalTermFreq(termInstance);
				    suml += termFreq;
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		    avgl = (int) suml / reader.numDocs();
			return avgl;
		}
		
		// Get Top K words based on term_weight
					public static  List<String> Top_t_terms (int topn , Map<String, Double> t_terms){
						 List<String> Top_t_terms = new ArrayList<String>();
						 int count = 0;
						 
						 Map<String, Double> sorted_t_terms = sortByValues(t_terms, "U");

						 for (String skey : sorted_t_terms.keySet()){
							 Top_t_terms.add(skey);
							 count ++;
							 
							 if (count == topn)
								 break;
						 }
						 
						 return Top_t_terms;
					 }
					
					
					public static  List<String> Down_t_terms (int downn , Map<String, Double> t_terms){
						 List<String> Top_t_terms = new ArrayList<String>();
						 int count = 0;
						 
						 Map<String, Double> sorted_t_terms = sortByValues(t_terms, "D");

						 for (String skey : sorted_t_terms.keySet()){
							 Top_t_terms.add(skey);
							 count ++;
							 
							 if (count == downn)
								 break;
						 }
						 
						 return Top_t_terms;
					 }
					
					 
					 // Sort HashMap by value
					 public static HashMap sortByValues(Map<String, Double> t_terms_weight, String U_D) { 
					       List list = new LinkedList(t_terms_weight.entrySet());
					       // Defined Custom Comparator here
					       Collections.sort(list, new Comparator() {
					            public int compare(Object o1, Object o2) {
					               return ((Comparable) ((Map.Entry) (o1)).getValue())
					                  .compareTo(((Map.Entry) (o2)).getValue());
					            }
					       });
					       
					       if (U_D.matches("U"))
					    	   Collections.reverse(list);

					       // Here I am copying the sorted list in HashMap
					       // using LinkedHashMap to preserve the insertion order
					       HashMap sortedHashMap = new LinkedHashMap();
					       for (Iterator it = list.iterator(); it.hasNext();) {
					              Map.Entry entry = (Map.Entry) it.next();
					              sortedHashMap.put(entry.getKey(), entry.getValue());
					       } 
					       return sortedHashMap;
					  }

}
