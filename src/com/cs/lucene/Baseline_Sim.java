package com.cs.lucene;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.LMDirichletSimilarity;
import org.apache.lucene.search.similarities.LMJelinekMercerSimilarity;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.util.Version;

public class Baseline_Sim {
	
	//Get similarity for each review with key plots
			public static Map<String, Float> Sim_Search(IndexReader reader, IndexSearcher searcher, List<String> q_words, String sim_option) throws IOException, ParseException{
				Map<String, Float> relevant_doc = new HashMap<String, Float>();
				Similarity sim = null;
				
				switch (sim_option){
					case "1":
						sim = new LMDirichletSimilarity(2000);
						break;
					case "2":
						sim = new LMJelinekMercerSimilarity((float) 0.7);
						break;
					case "3":
						sim = new BM25Similarity((float) 1.6, (float) 0.75);
						break;
				}
				
				searcher.setSimilarity(sim);
				
				Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_41);
				QueryParser parser = new QueryParser(Version.LUCENE_41, "content", analyzer);
				
				BooleanQuery query = new BooleanQuery();

				for (int i = 0 ; i < q_words.size() ; i ++){
					String qw = q_words.get(i);
					Term t = new Term("content", qw);
					query.add(new TermQuery(t), Occur.SHOULD);
				}
				
				TopDocs results = searcher.search(query, reader.numDocs());
				ScoreDoc[] hits = results.scoreDocs;
				for (int k = 0 ; k < hits.length ; k ++){
					org.apache.lucene.document.Document doc2 = searcher.doc(hits[k].doc);
					String docno = doc2.get("doc_id");
					relevant_doc.put(docno, hits[k].score);
					//System.out.println( docno + " , " + hits[k].score);
				}
				
				//System.out.println(reader.numDocs());
			
				
				return  relevant_doc;
			}

}
